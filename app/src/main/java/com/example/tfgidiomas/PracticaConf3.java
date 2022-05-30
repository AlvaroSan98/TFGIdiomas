package com.example.tfgidiomas;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfgidiomas.adaptadores.GridJuegoAdapter;
import com.example.tfgidiomas.db.DbHelper;
import com.example.tfgidiomas.db.Utilidades;
import com.example.tfgidiomas.desordenado.JuegoDesordenado;
import com.example.tfgidiomas.test.JuegoTest;

import java.util.ArrayList;
import java.util.Arrays;

public class PracticaConf3 extends AppCompatActivity {

    GridView gv;
    Bundle bundle;
    Long idioma;
    Coleccion[] colecciones;
    Boolean[] escogidos;
    ArrayList<String>[] tiposXcoleccion;
    TextView tv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practica_conf3);
        gv = findViewById(R.id.gv_colecciones_juego);
        bundle = getIntent().getBundleExtra("bundle");
        idioma = bundle.getLong("idioma");
        int numColecciones = getNumColecciones();
        colecciones = new Coleccion[numColecciones];
        escogidos = new Boolean[numColecciones];
        tiposXcoleccion = new ArrayList[numColecciones];
        for (int i = 0; i < numColecciones; i++) {
            escogidos[i] = false;
            tiposXcoleccion[i] = new ArrayList<String>();
        }
        getColecciones();
        tv = findViewById(R.id.tv_palabras_added);
        GridJuegoAdapter adapter = new GridJuegoAdapter(PracticaConf3.this, colecciones, escogidos, tv);
        gv.setAdapter(adapter);
    }

    public void aJugar (View view) {
        if (isReady()) {
            Intent intent;
            String juego = "";
            if (getIntent().getStringExtra("tipo").equals("test")) {
                intent = new Intent(this, JuegoTest.class);
                juego = "test";
            }
            else if (getIntent().getStringExtra("tipo").equals("intruso")) {
                intent = new Intent(this, JuegoTest.class);
                juego = "intruso";
            }
            else {
                intent = new Intent(this, JuegoDesordenado.class);
            }
            ArrayList<Long> coleccionesEscogidas = new ArrayList<Long>();
            int tiposTotal = 0;
            ArrayList<String> tiposFinal = new ArrayList<String>();
            for (int i = 0; i < getNumColecciones(); i++) {
                if (escogidos[i] == true) {
                    coleccionesEscogidas.add(colecciones[i].getId());
                    for (int j = 0; j < tiposXcoleccion[i].size(); j++) {
                        if (!tiposFinal.contains(tiposXcoleccion[i].get(j))) tiposFinal.add(tiposXcoleccion[i].get(j));
                    }
                }
            }
            if (juego.equalsIgnoreCase("intruso") && tiposFinal.size() <= 1) Toast.makeText(this, "Debes tener más de 2 tipos de palabras", Toast.LENGTH_SHORT).show();
            else {
                intent.putExtra("juego", juego);
                bundle.putSerializable("colecciones", coleccionesEscogidas);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
                finish();
            }
        }
        else {
            Toast.makeText(this, "Selecciona al menos 20 palabras para poder jugar", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isReady() {
        String[] strings = new String[3];
        strings = tv.getText().toString().split(" ");
        if (Integer.parseInt(strings[0]) < 5) return false;
        else return true;
    }

    private void getColecciones() {
        DbHelper dbHelper = new DbHelper(PracticaConf3.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            Toast.makeText(PracticaConf3.this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
        }
        else {
            String[] camposDevueltos = new String[]{Utilidades.COLECCIONES_ID, Utilidades.COLECCIONES_NOMBRE};
            String select = Utilidades.COLECCIONES_IDIOMA + " = ?";
            String[] selectionArgs = new String[]{idioma.toString()};
            Cursor c = db.query(
                    Utilidades.TABLA_COLECCIONES,
                    camposDevueltos,
                    select,
                    selectionArgs,
                    null,
                    null,
                    null
            );
            if (c.getCount() == 0) {
                Toast.makeText(this, "No se ha encontrado ninguna colección", Toast.LENGTH_LONG).show();
                db.close();
            }
            else {
                c.moveToFirst();
                int i = 0;
                do {
                    Coleccion col = new Coleccion();
                    col.setId(c.getLong(c.getColumnIndex(Utilidades.COLECCIONES_ID)));
                    col.setIdioma(bundle.getLong("idioma"));
                    col.setNombre(c.getString(c.getColumnIndex(Utilidades.COLECCIONES_NOMBRE)));
                    col.setnPalabras(getNumPalabrasInCol(col.getId()));
                    colecciones[i] = col;
                    getTiposPalabras(col.getId(), tiposXcoleccion[i]);
                    i++;
                } while (c.moveToNext());
                db.close();
            }
        }
    }

    private int getNumPalabrasInCol(Long idColeccion) {
        DbHelper dbHelper = new DbHelper(PracticaConf3.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            Toast.makeText(PracticaConf3.this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
            return 0;
        }
        else {
            String query = "SELECT * FROM " + Utilidades.TABLA_PALABRAS + " WHERE " + Utilidades.PALABRAS_COLECCION + " = ?";
            String[] selectionArgs = new String[]{idColeccion.toString()};
            SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, selectionArgs);
            return c.getCount();
        }
    }

    private void getTiposPalabras(Long idColeccion, ArrayList<String> arrayTipos) {
        DbHelper dbHelper = new DbHelper(PracticaConf3.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            Toast.makeText(PracticaConf3.this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
        }
        else {
            String query = "SELECT " + Utilidades.PALABRAS_TIPO + " FROM " + Utilidades.TABLA_PALABRAS + " WHERE "
                    + Utilidades.PALABRAS_COLECCION + " = ?" ;
            String[] selectionArgs = new String[]{idColeccion.toString()};
            SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, selectionArgs);
            String tipo;
            if (c.moveToFirst()) {
                do {
                    tipo = c.getString(c.getColumnIndex(Utilidades.PALABRAS_TIPO));
                    if (!arrayTipos.contains(tipo)) arrayTipos.add(tipo);
                } while (c.moveToNext());
            }
        }
    }

    private int getNumColecciones() {
        DbHelper dbHelper = new DbHelper(PracticaConf3.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            Toast.makeText(PracticaConf3.this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
            return 0;
        }
        else {
            String query = "SELECT * FROM " + Utilidades.TABLA_COLECCIONES + " WHERE " + Utilidades.COLECCIONES_IDIOMA + " = ?";
            String[] selectionArgs = new String[]{idioma.toString()};
            SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, selectionArgs);
            return c.getCount();
        }
    }
}
