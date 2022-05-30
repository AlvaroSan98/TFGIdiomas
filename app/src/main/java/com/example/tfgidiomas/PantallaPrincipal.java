package com.example.tfgidiomas;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfgidiomas.db.DbHelper;
import com.example.tfgidiomas.db.Utilidades;

public class PantallaPrincipal extends AppCompatActivity {

    public TextView presentacion;

    private String usuario;
    private Long idioma;
    private Bundle bundle;
    private int n_palabras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_principal);

        presentacion = findViewById(R.id.principal_presentacion);
        bundle = getIntent().getBundleExtra("bundle");
        usuario = bundle.getString("usuario");
        idioma = bundle.getLong("idioma");
        n_palabras = contarPalabras();
        presentacion.setText("En este idioma tienes actualmente " + n_palabras + " palabras");
    }

    public void aAddPalabra(View view) {
        Intent i = new Intent(this, AddPalabra.class);
        i.putExtra("bundle", bundle);
        i.putExtra("tipo", "add");
        startActivity(i);
    }

    public void aColecciones(View view) {
        Intent i = new Intent(this, ColeccionesActivity.class);
        i.putExtra("bundle", bundle);
        startActivity(i);
    }

    public void aPractica(View view) {
        Intent i = new Intent(this, PracticaConf2.class);
        i.putExtra("bundle", bundle);
        startActivity(i);
    }

    public void aPerfil(View view) {
        Intent i = new Intent(this, PerfilActivity.class);
        i.putExtra("bundle", bundle);
        startActivity(i);
    }

    public void aIdioma(View view) {
        Intent i = new Intent(this, ElegirIdioma.class);
        i.putExtra("bundle", bundle);
        startActivity(i);
        finish();
    }

    public void aUsuario(View view) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("bundle", bundle);
        startActivity(i);
        finish();
    }

    private int contarPalabras() {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db==null) {
            Toast.makeText(this, "Error al conectar a la BBDD", Toast.LENGTH_SHORT).show();
        }
        else {
            String query = "SELECT * FROM " + Utilidades.TABLA_PALABRAS + " p INNER JOIN " + Utilidades.TABLA_COLECCIONES +
                    " c ON p." + Utilidades.PALABRAS_COLECCION + "=c." + Utilidades.COLECCIONES_ID + " WHERE c." +
                    Utilidades.COLECCIONES_IDIOMA + "=?";
            String[] selectionArgs = new String[]{idioma.toString()};
            Cursor c = db.rawQuery(query, selectionArgs);
            return c.getCount();
        }
        return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        n_palabras = contarPalabras();
        presentacion.setText("En este idioma tienes actualmente " + n_palabras + " palabras");
    }
}
