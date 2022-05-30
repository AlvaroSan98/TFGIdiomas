package com.example.tfgidiomas;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfgidiomas.db.DbHelper;
import com.example.tfgidiomas.db.Utilidades;

import java.io.UTFDataFormatException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class Wordle extends AppCompatActivity {

    private ArrayList<String> intentos;
    private String fecha;

    private LinearLayout ext_layout;
    private EditText et_intento;

    private String solucion;

    private Bundle bundle;
    private long idPartida;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordle);

        bundle = getIntent().getBundleExtra("bundle");

        ext_layout = findViewById(R.id.wordle_ext_layout);
        et_intento = findViewById(R.id.et_wordle_intento);


        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        fecha = df.format(date);
        intentos = new ArrayList<String>();
        solucion = getSolucion();
        int intentos = rellenarIntentos(fecha);
        if (intentos != -1) {
            rellenarLayouts();
        }
    }

    //TODO: registrar el intento en el arraylist y en la bd
    public void comprobarIntento(View view) {
        String intento = et_intento.getText().toString();
        LinearLayout layout_intento = (LinearLayout) ext_layout.getChildAt(intentos.size());
        if (intento.length() != layout_intento.getChildCount()) {
            Toast.makeText(this, "El numero de letras no coincide", Toast.LENGTH_SHORT).show();
        }
        else {
            for (int i = 0; i < solucion.length(); i++) {
                Button b = (Button) layout_intento.getChildAt(i);
                comprobarLetra(b, intento, i);
            }
            registrarIntentoBBDD(intento);
            intentos.add(intento);
            if (intento.equalsIgnoreCase(solucion)) {
                terminarJuego(true);
            }
            else {
                if (intentos.size() < 5) {
                    LinearLayout layout = new LinearLayout(this);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    layout.setGravity(Gravity.CENTER);
                    for (int j = 0; j < solucion.length(); j++) {
                        Button b = new Button(this);
                        b.setBackground(getDrawable(R.drawable.btn_desordenado_answer_vacio));
                        b.setClickable(false);
                        b.setMinWidth(0);
                        b.setMinHeight(0);
                        b.setMinimumWidth(0);
                        b.setMinimumHeight(0);
                        layout.addView(b);
                    }
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
                    layout.setLayoutParams(params);
                    ext_layout.addView(layout);
                } else {
                    terminarJuego(false);
                }
            }
        }
    }

    private void terminarJuego(boolean correcto) {
        TextView tv = findViewById(R.id.tv_intentos_wordle_completados);
        tv.setVisibility(View.VISIBLE);
        if (correcto) {
            tv.setText(getString(R.string.wordle_conseguido));
        }
        else {
            tv.setText(getString(R.string.intentos_wordle_completados));
        }
        EditText et = findViewById(R.id.et_wordle_intento);
        Button b = findViewById(R.id.btn_comprobar_intento);
        et.setVisibility(View.INVISIBLE);
        b.setVisibility(View.INVISIBLE);
    }

    private void comprobarLetra(Button b, String intento, int i) {
        int count = 0;
        for (int j = 0; j < solucion.length(); j++) {
            if (Character.toLowerCase(intento.charAt(i)) == Character.toLowerCase(solucion.charAt(j))) {
                 count++;
            }
        }
        if (Character.toLowerCase(intento.charAt(i)) == Character.toLowerCase(solucion.charAt(i))) {
            if (count == 1) {
                b.setBackground(getDrawable(R.drawable.btn_desordenado_answer_completed));
            }
            else {
                b.setBackground(getDrawable(R.drawable.btn_desordenado_answer_duplicated));
            }
        } else {
            if (count == 1) {
                b.setBackground(getDrawable(R.drawable.btn_desordenado_answer_incomplete));
            }
            else b.setBackground(getDrawable(R.drawable.btn_desordenado_question_error));
        }
        b.setText("" + intento.charAt(i));
    }

    private void registrarIntentoBBDD(String intento) {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db == null) {
            Toast.makeText(this, "Error al conectar con la BBDD", Toast.LENGTH_SHORT).show();
        }
        else {
            ContentValues cv = new ContentValues();
            cv.put(Utilidades.WORDLE_ID, idPartida);
            cv.put(Utilidades.WORDLE_INTENTO, intento);
            long idResultante = db.insert(Utilidades.TABLA_WORDLE_INTENTOS, Utilidades.WORDLE_INTENTO, cv);
            if (idResultante == -1) {
                Toast.makeText(this, "Error al registrar intento en la BBDD", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Intento registrado en la BBDD", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getSolucion() {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            Toast.makeText(this, "Error al conectar a una BD", Toast.LENGTH_SHORT);
            return null;
        }
        else {
            if (comprobarWordleUsuario()) return null;
            else {
                String query = "SELECT " + Utilidades.WORDLE_SOLUCION + " FROM " + Utilidades.TABLA_WORDLE_SOLUCION
                        + " WHERE " + Utilidades.WORDLE_ID + " =?";
                String[] selectionArgs = new String[]{"" + idPartida};
                Cursor c = db.rawQuery(query, selectionArgs);
                if (!c.moveToFirst()) {
                    Toast.makeText(this, "No se han encontrado soluciones para el dia de hoy", Toast.LENGTH_SHORT).show();
                    return null;
                } else {
                    return c.getString(0);
                }
            }
        }
    }

    private boolean comprobarWordleUsuario() {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db!=null) {
            String query = "SELECT " + Utilidades.PARTIDAS_ID + " FROM " + Utilidades.TABLA_PARTIDAS + " p INNER JOIN "
                    + Utilidades.TABLA_PARTIDA_USUARIO + " x ON p." + Utilidades.PARTIDAS_ID + "=x."
                    + Utilidades.PARTIDA_USUARIO_PARTIDA + " WHERE p." + Utilidades.PARTIDAS_FECHA + "=? AND p."
                    + Utilidades.PARTIDAS_JUEGO + "=? AND x." + Utilidades.PARTIDA_USUARIO_USUARIO + "=?";
            String[] selectionArgs = new String[]{fecha, "wordle", bundle.getString("usuario")};
            Cursor c = db.rawQuery(query, selectionArgs);
            if (c.getCount() != 0) {
                c.moveToFirst();
                idPartida = c.getLong(c.getColumnIndex(Utilidades.PARTIDAS_ID));
                return false;
            }
            else {
                return true;
            }
        }
        return false;
    }

    private int rellenarIntentos(String fecha) {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            Toast.makeText(this, "Error al conectarse a la BD", Toast.LENGTH_SHORT).show();
            return -1;
        }
        else {
            if (solucion != null) {
                String query = "SELECT " + Utilidades.WORDLE_INTENTO + " FROM " + Utilidades.TABLA_WORDLE_INTENTOS + " WHERE " + Utilidades.WORDLE_ID + " =?";
                String[] selectionArgs = new String[]{"" + idPartida};
                Cursor c = db.rawQuery(query, selectionArgs);
                if (!c.moveToFirst()) {
                    Toast.makeText(this, "No ha habido intentos para la solución propuesta", Toast.LENGTH_SHORT).show();
                    return 0;
                } else {
                    int intentos = 0;
                    do {
                        this.intentos.add(c.getString(0));
                        intentos++;
                    } while (c.moveToNext());
                    return intentos;
                }

            }
            else {
                return 0;
            }
        }
    }

    private void rellenarLayouts() {
        boolean terminado = false;
        if (intentos.size() != 0) {
            int length = intentos.get(0).length();
            for (int i = 0; i < intentos.size(); i++) {
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layout.setGravity(Gravity.CENTER);
                for (int j = 0; j < length; j++) {
                    Button b = new Button(this);
                    comprobarLetra(b, intentos.get(i), j);
                    if (intentos.get(i).equalsIgnoreCase(solucion)) terminado = true;
                    b.setClickable(false);
                    b.setMinWidth(0);
                    b.setMinHeight(0);
                    b.setMinimumWidth(0);
                    b.setMinimumHeight(0);
                    layout.addView(b);
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
                layout.setLayoutParams(params);
                ext_layout.addView(layout);
            }
            if (terminado) {
                terminarJuego(true);
            }
            else {
                if (intentos.size() < 5) {
                    LinearLayout layout = new LinearLayout(this);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    layout.setGravity(Gravity.CENTER);
                    for (int j = 0; j < length; j++) {
                        Button b = new Button(this);
                        b.setBackground(getDrawable(R.drawable.btn_desordenado_answer_vacio));
                        b.setClickable(false);
                        b.setMinWidth(0);
                        b.setMinHeight(0);
                        b.setMinimumWidth(0);
                        b.setMinimumHeight(0);
                        layout.addView(b);
                    }
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
                    layout.setLayoutParams(params);
                    ext_layout.addView(layout);
                } else {
                    terminarJuego(false);
                }
            }
        }
        else {
            rellenarPrimerIntento();
        }
    }

    private void rellenarPrimerIntento() {
        if (solucion == null) {
            ArrayList<String> palabras = new ArrayList<String>();
            boolean rellenado = false;
            DbHelper dbHelper = new DbHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            if (db == null) {
                Toast.makeText(this, "Error al conectar a la BD", Toast.LENGTH_SHORT).show();
            } else {
                String query = "SELECT p." + Utilidades.PALABRAS_NOMBRE + " FROM " + Utilidades.TABLA_PALABRAS + " p INNER JOIN "
                        + Utilidades.TABLA_COLECCIONES + " c ON p." + Utilidades.PALABRAS_COLECCION + "=c."
                        + Utilidades.COLECCIONES_ID + " WHERE c." + Utilidades.COLECCIONES_IDIOMA + "=?";
                Long idioma = bundle.getLong("idioma");
                String[] selectionArgs = new String[]{idioma.toString()};
                Cursor c = db.rawQuery(query, selectionArgs);
                if (!c.moveToFirst()) {
                    Toast.makeText(this, "No hay palabras en la BBDD", Toast.LENGTH_SHORT).show();
                } else {
                    do {
                        palabras.add(c.getString(0));
                    } while (c.moveToNext());
                    rellenado = true;
                }
            }
            String traduccion;
            if (!rellenado)
                Toast.makeText(this, "No se han encontrado palabras", Toast.LENGTH_SHORT).show();
            else {
                Random rand = new Random();
                do {
                    traduccion = palabras.get(rand.nextInt(palabras.size()));
                } while (traduccion.length() > 7);
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layout.setGravity(Gravity.CENTER);
                for (int i = 0; i < traduccion.length(); i++) {
                    Button b = new Button(this);
                    b.setBackground(getDrawable(R.drawable.btn_desordenado_answer_vacio));
                    b.setClickable(false);
                    b.setMinWidth(0);
                    b.setMinHeight(0);
                    b.setMinimumWidth(0);
                    b.setMinimumHeight(0);
                    layout.addView(b);
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
                layout.setLayoutParams(params);
                ext_layout.addView(layout);
                solucion = traduccion;
                registrarSolucionBBDD(traduccion);
            }
        }
        else {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setGravity(Gravity.CENTER);
            for (int i = 0; i < solucion.length(); i++) {
                Button b = new Button(this);
                b.setBackground(getDrawable(R.drawable.btn_desordenado_answer_vacio));
                b.setClickable(false);
                b.setMinWidth(0);
                b.setMinHeight(0);
                b.setMinimumWidth(0);
                b.setMinimumHeight(0);
                layout.addView(b);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
            layout.setLayoutParams(params);
            ext_layout.addView(layout);
        }
    }

    private void registrarSolucionBBDD(String traduccion) {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db == null) {
            Toast.makeText(this, "Error al conectar con la BBDD", Toast.LENGTH_SHORT).show();
        }
        else {
            ContentValues cv_partida = new ContentValues();
            cv_partida.put(Utilidades.PARTIDAS_FECHA, fecha);
            cv_partida.put(Utilidades.PARTIDAS_JUEGO, "wordle");
            cv_partida.put(Utilidades.PARTIDAS_PUNTOS, "0");
            Long idResultante = db.insert(Utilidades.TABLA_PARTIDAS, Utilidades.PARTIDAS_ID, cv_partida);
            if (idResultante == -1) {
                Toast.makeText(this, "Error al insertar partida", Toast.LENGTH_SHORT).show();
            }
            else {
                ContentValues cv_partida_usuario = new ContentValues();
                cv_partida_usuario.put(Utilidades.PARTIDA_USUARIO_PARTIDA, idResultante);
                cv_partida_usuario.put(Utilidades.PARTIDA_USUARIO_USUARIO, bundle.getString("usuario"));
                Long idResultante2 = db.insert(Utilidades.TABLA_PARTIDA_USUARIO, Utilidades.PARTIDA_USUARIO_PARTIDA, cv_partida_usuario);
                if (idResultante2 == -1) {
                    Toast.makeText(this, "Error al insertar partida-usuario", Toast.LENGTH_SHORT).show();
                }
                else {
                    this.idPartida = idResultante;
                    ContentValues cv_wordle = new ContentValues();
                    cv_wordle.put(Utilidades.WORDLE_SOLUCION, traduccion);
                    cv_wordle.put(Utilidades.WORDLE_ID, idResultante);
                    Long idWordle = db.insert(Utilidades.TABLA_WORDLE_SOLUCION, Utilidades.WORDLE_ID, cv_wordle);
                    if (idWordle == -1) {
                        Toast.makeText(this, "Error al insertar wordle-solucion", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(this, "Solucion insertada con éxito", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}