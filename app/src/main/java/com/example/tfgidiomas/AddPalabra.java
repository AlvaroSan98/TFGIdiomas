package com.example.tfgidiomas;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfgidiomas.adaptadores.AdaptadorPalabras;
import com.example.tfgidiomas.db.DbHelper;
import com.example.tfgidiomas.db.Utilidades;

import java.util.ArrayList;

public class AddPalabra extends AppCompatActivity {

    private Spinner spinner;

    private EditText palabra;
    private EditText traduccion;
    private RadioGroup rg1;
    private RadioGroup rg2;
    private boolean isChecking;

    private String usuario;
    private Long idioma;
    private String[] str_colecciones;
    private ArrayList<Coleccion> colecciones;
    private Bundle bundle;

    private String tipo;

    private Dialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_palabra);

        colecciones = new ArrayList<Coleccion>();

        spinner = findViewById(R.id.add_spinner);

        palabra = findViewById(R.id.et_add_palabra);
        traduccion = findViewById(R.id.et_add_traduccion);
        rg1 = findViewById(R.id.rg_tipo1);
        rg2 = findViewById(R.id.rg_tipo2);
        isChecking = true;

        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    if (rg2.getCheckedRadioButtonId() != -1) rg2.clearCheck();
                    RadioButton rb = findViewById(checkedId);
                    rb.setChecked(true);
                }
                isChecking = true;
            }
        });

        rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    if (rg1.getCheckedRadioButtonId() != -1) rg1.clearCheck();
                    RadioButton rb = findViewById(checkedId);
                    rb.setChecked(true);
                }
                isChecking = true;
            }
        });

        bundle = getIntent().getBundleExtra("bundle");
        idioma = bundle.getLong("idioma");
        usuario = bundle.getString("usuario");
        tipo = getIntent().getStringExtra("tipo");

        rellenarColecciones(null);

        dialog = new Dialog(AddPalabra.this);
        dialog.setContentView(R.layout.activity_add_coleccion);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        ImageButton ib = (ImageButton) findViewById(R.id.ib_editar_add_palabra);
        if (tipo.equals("editar")) {
            ib.setBackgroundResource(R.drawable.ic_edit_icon);
            rellenarPalabra((Palabra) getIntent().getSerializableExtra("palabra"));
        }
        else {
            ib.setBackgroundResource(R.drawable.ic_add_btn);
        }
    }

    private void rellenarColecciones(String coleccion) {
        DbHelper dbHelper = new DbHelper(AddPalabra.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            Toast.makeText(this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
        }
        else {
            String sqlInst = "SELECT * FROM " + Utilidades.TABLA_COLECCIONES + " WHERE " +
                    Utilidades.COLECCIONES_IDIOMA + " = ?";
            String[] selectionArgs = new String[]{idioma.toString()};
            SQLiteCursor c = (SQLiteCursor) db.rawQuery(sqlInst, selectionArgs);
            int i = 0;
            int count = c.getCount();
            if (c.getCount() != 0) {
                c.moveToFirst();
                str_colecciones = new String[c.getCount()];
                do {
                    Coleccion col = new Coleccion();
                    col.setId(c.getLong(c.getColumnIndex(Utilidades.COLECCIONES_ID)));
                    col.setNombre(c.getString(c.getColumnIndex(Utilidades.COLECCIONES_NOMBRE)));
                    col.setIdioma(c.getLong(c.getColumnIndex(Utilidades.COLECCIONES_IDIOMA)));
                    str_colecciones[i] = col.getNombre();
                    colecciones.add(col);
                    i++;
                } while (c.moveToNext());
                ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, str_colecciones);
                spinner.setAdapter(adapter);
                if (coleccion != null) {
                    spinner.setSelection(str_colecciones.length - 1);
                }
            }
        }
        if (tipo.equals("editar")) {
            String str_coleccion = bundle.getString("coleccion");
            for (int i = 0; i < str_colecciones.length; i++) {
                if (str_colecciones[i].equals(str_coleccion)) {
                    spinner.setSelection(i);
                    spinner.setEnabled(false);
                    Button añadirColeccion = findViewById(R.id.add_palabra_add_coleccion);
                    añadirColeccion.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public void aAddColeccion(View view) {
        dialog.show();
    }

    public void aceptarColeccion(View view) {
        EditText nueva_coleccion = dialog.findViewById(R.id.et_nueva_coleccion);
        String str_coleccion = nueva_coleccion.getText().toString();
        if (str_coleccion.trim().isEmpty() || !comprobarNombreColeccion(str_coleccion)) {
            Toast.makeText(this, "Por favor, escribe un nombre válido", Toast.LENGTH_LONG).show();
        }
        else {
            DbHelper dbHelper = new DbHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db == null) {
                Toast.makeText(this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
            }
            else {
                Coleccion col = new Coleccion(str_coleccion, idioma);
                Long idResultante = db.insert(Utilidades.TABLA_COLECCIONES, Utilidades.COLECCIONES_NOMBRE, col.toContentValues());
                if (idResultante == -1) {
                    Toast.makeText(this, "Error al insertar nueva coleccion", Toast.LENGTH_LONG).show();
                }
                else {
                    colecciones.clear();
                    rellenarColecciones(str_coleccion);
                    dialog.cancel();
                }
            }
        }
    }

    private boolean comprobarNombreColeccion(String nombre) {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            Toast.makeText(this, "Error al conectar con la BBDD", Toast.LENGTH_SHORT).show();
        }
        else {
            String query = "SELECT * FROM " + Utilidades.TABLA_COLECCIONES + " WHERE " + Utilidades.COLECCIONES_IDIOMA + "=?";
            String[] selectArgs = new String[]{idioma.toString()};
            Cursor c = db.rawQuery(query, selectArgs);
            if (!c.moveToFirst()) {
                return true;
            }
            else {
                do {
                    String n_coleccion = c.getString(c.getColumnIndex(Utilidades.COLECCIONES_NOMBRE));
                    if (n_coleccion.equalsIgnoreCase(nombre)) {
                        Toast.makeText(this, "Ya existe una colección con ese nombre", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } while (c.moveToNext());
                return true;
            }
        }
        return false;
    }

    public void executePalabra(View view) {
        String palabra = this.palabra.getText().toString();
        String traduccion = this.traduccion.getText().toString();
        if (comprobarPalabra(palabra, traduccion)) {
            int id_tipo;
            if (rg1.getCheckedRadioButtonId() == -1) id_tipo = rg2.getCheckedRadioButtonId();
            else id_tipo = rg1.getCheckedRadioButtonId();
            RadioButton rb = findViewById(id_tipo);
            String tipo = rb.getText().toString();
            if (this.tipo.equals("editar")) {
                Palabra p = (Palabra) getIntent().getSerializableExtra("palabra");
                p.setPalabra(palabra);
                p.setTipo(tipo);
                p.setTraduccion(traduccion);
                p.setColeccion(colecciones.get(spinner.getSelectedItemPosition()).getId());
                modificarPalabra(p);
            }
            else {
                Coleccion c = colecciones.get(spinner.getSelectedItemPosition());
                Palabra p = new Palabra(palabra, traduccion, tipo, c.getId());
                introducirPalabra(p);
            }
        }
    }

    private void rellenarPalabra(Palabra p) {
        palabra.setText(p.getPalabra());
        traduccion.setText(p.getTraduccion());
        if (p.getTipo().equals(getResources().getString(R.string.sustantivo))) {
            RadioButton rb = findViewById(R.id.rb_sustantivo);
            rb.setChecked(true);
        }
        else if (p.getTipo().equals(getResources().getString(R.string.adjetivo))) {
            RadioButton rb = findViewById(R.id.rb_adjetivo);
            rb.setChecked(true);
        }
        else if (p.getTipo().equals(getResources().getString(R.string.verbo))) {
            RadioButton rb = findViewById(R.id.rb_verbo);
            rb.setChecked(true);
        }
        else if (p.getTipo().equals(getResources().getString(R.string.adverbio))) {
            RadioButton rb = findViewById(R.id.rb_adverbio);
            rb.setChecked(true);
        }
        else if (p.getTipo().equals(getResources().getString(R.string.determinante))) {
            RadioButton rb = findViewById(R.id.rb_determinante);
            rb.setChecked(true);
        }
        else if (p.getTipo().equals(getResources().getString(R.string.pronombre))) {
            RadioButton rb = findViewById(R.id.rb_pronombre);
            rb.setChecked(true);
        }
        else if (p.getTipo().equals(getResources().getString(R.string.preposicion))) {
            RadioButton rb = findViewById(R.id.rb_preposicion);
            rb.setChecked(true);
        }
        else if (p.getTipo().equals(getResources().getString(R.string.conjuncion))) {
            RadioButton rb = findViewById(R.id.rb_conjuncion);
            rb.setChecked(true);
        }
    }

    private boolean comprobarPalabra(String palabra, String traduccion) {
        if (palabra.trim().isEmpty()) {
            Toast.makeText(this, "Introduce algo en el campo palabra", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (traduccion.trim().isEmpty()) {
            Toast.makeText(this, "Introduce algo en el campo traduccion", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (rg1.getCheckedRadioButtonId() == -1 && rg2.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Introduce el tipo de palabra", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (spinner.getSelectedItem() == null) {
            Toast.makeText(this, "Introduce la palabra en una colección", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!this.tipo.equals("editar") && !comprobarPalabraColeccion()) {
            return false;
        }
        return true;
    }

    private void modificarPalabra(Palabra palabra) {
        eliminarPalabra(palabra);
        introducirPalabra(palabra);
        finish();
    }

    private boolean comprobarPalabraColeccion() {
        String palabra = this.palabra.getText().toString();
        String traduccion = this.traduccion.getText().toString();
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            Toast.makeText(this, "Error al conectar con la BBDD", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            String query = "SELECT * FROM " + Utilidades.TABLA_PALABRAS + " WHERE " +
                    Utilidades.PALABRAS_COLECCION + "=?";
            Long coleccionId = colecciones.get(spinner.getSelectedItemPosition()).getId();
            String[] selectionArgs = new String[]{coleccionId.toString()};
            Cursor c = db.rawQuery(query, selectionArgs);
            if (!c.moveToFirst()) {
                //Toast.makeText(this, "No hay ninguna palabra en esta coleccion", Toast.LENGTH_SHORT).show();
                return true;
            }
            else {
                do {
                    String nombrePalabra = c.getString(c.getColumnIndex(Utilidades.PALABRAS_NOMBRE));
                    String traduccionPalabra = c.getString(c.getColumnIndex(Utilidades.PALABRAS_TRADUCCION));
                    if (nombrePalabra.equalsIgnoreCase(palabra) && traduccionPalabra.equalsIgnoreCase(traduccion)) {
                        Toast.makeText(this, "La palabra ya está añadida en la colección", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } while (c.moveToNext());
                return true;
            }
        }
    }

    private void eliminarPalabra(Palabra palabra) {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            String where = Utilidades.PALABRAS_ID + " = ?";
            String[] campos = new String[]{palabra.getId().toString()};
            int rows = db.delete(Utilidades.TABLA_PALABRAS, where, campos);
            if (rows > 0) {
                //Log.d("BBDD", "la palabra se ha eliminado correctamente");
            }
            else {
                Toast.makeText(this, "Ha habido un problema al eliminar la palabra", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void introducirPalabra(Palabra palabra) {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            Long idPalabra = db.insert(Utilidades.TABLA_PALABRAS, Utilidades.PALABRAS_ID, palabra.toContentValues());
            if (idPalabra == -1) {
                Toast.makeText(this, "Error al introducir en la tabla palabras", Toast.LENGTH_LONG).show();
                db.close();
            }
            else {
                if (!this.tipo.equals("editar")) Toast.makeText(this, "La palabra " + palabra.getPalabra() + " se ha añadido a la bbdd", Toast.LENGTH_SHORT).show();
                this.palabra.setText("");
                this.traduccion.setText("");
                db.close();
            }
        }
    }
}
