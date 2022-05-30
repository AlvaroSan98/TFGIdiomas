package com.example.tfgidiomas;

import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfgidiomas.db.DbHelper;
import com.example.tfgidiomas.db.Utilidades;

public class PracticaConf2 extends AppCompatActivity {

    Bundle bundle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practica_conf2);
        bundle = getIntent().getBundleExtra("bundle");
    }

    public void aTest (View view) {
        if (getNumPalabras() >= 10) {
            Intent i = new Intent(this, PracticaConf3.class);
            i.putExtra("bundle", bundle);
            i.putExtra("tipo", "test");
            startActivity(i);
            finish();
        }
        else {
            Toast.makeText(this, "Necesitas tener al menos 20 palabras añadidas para jugar", Toast.LENGTH_SHORT).show();
        }
    }

    public void aIntruso(View view) {
        if (getNumPalabras() >= 10) {
            Intent i = new Intent(this, PracticaConf3.class);
            i.putExtra("bundle", bundle);
            i.putExtra("tipo", "intruso");
            startActivity(i);
            finish();
        }
        else {
            Toast.makeText(this, "Necesitas tener al menos 20 palabras añadidas para jugar", Toast.LENGTH_SHORT).show();
        }
    }

    public void aDesordenado(View view) {
        if (getNumPalabras() >= 10) {
            Intent i = new Intent(this, PracticaConf3.class);
            i.putExtra("bundle", bundle);
            i.putExtra("tipo", "desordenado");
            startActivity(i);
            finish();
        }
        else {
            Toast.makeText(this, "Necesitas tener al menos 20 palabras añadidas para jugar", Toast.LENGTH_SHORT).show();
        }
    }

    public void aWordle(View view) {
        if (getNumPalabras() >= 1) {
            Intent i = new Intent(this, Wordle.class);
            i.putExtra("bundle", bundle);
            i.putExtra("tipo", "wordle");
            startActivity(i);
            finish();
        }
        else {
            Toast.makeText(this, "Necesitas tener al menos 20 palabras añadidas para jugar", Toast.LENGTH_SHORT).show();
        }
    }

    private int getNumPalabras() {
        DbHelper dbHelper = new DbHelper(PracticaConf2.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            Toast.makeText(PracticaConf2.this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
            return 0;
        }
        else {
            String query = "SELECT * FROM " + Utilidades.TABLA_PALABRAS + " p INNER JOIN " + Utilidades.TABLA_COLECCIONES + " c ON "
                    + "p." + Utilidades.PALABRAS_COLECCION + "=c." + Utilidades.COLECCIONES_ID + " WHERE " + Utilidades.COLECCIONES_IDIOMA
                    + "=?";
            String idioma = "" +  bundle.getLong("idioma");
            String[] selectionArgs = new String[]{idioma};
            SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, selectionArgs);
            return c.getCount();
        }
    }
}