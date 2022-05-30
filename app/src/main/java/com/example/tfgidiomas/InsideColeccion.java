package com.example.tfgidiomas;

import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfgidiomas.adaptadores.AdaptadorPalabras;
import com.example.tfgidiomas.db.DbHelper;
import com.example.tfgidiomas.db.Utilidades;

import java.util.ArrayList;

public class InsideColeccion extends AppCompatActivity {

    public RecyclerView rv;
    private ArrayList<Palabra> palabras;

    private Long idColeccion;

    private static Dialog dialog;
    private DbHelper dbHelper;
    private static Palabra palabra;
    private AdaptadorPalabras adaptador;

    private Bundle bundle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_coleccion);

        idColeccion = getIntent().getExtras().getLong("coleccion");

        bundle = getIntent().getBundleExtra("bundle");
        bundle.putString("coleccion", getIntent().getStringExtra("coleccion_str"));

        rv = findViewById(R.id.rv_inside_coleccion);
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        palabras = new ArrayList<Palabra>();
        rellenarPalabras();
        if (palabras.size() == 0) {
            findViewById(R.id.tv_no_palabras).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.tv_no_palabras).setVisibility(View.INVISIBLE);
        }
        adaptador = new AdaptadorPalabras(palabras, this, bundle);
        rv.setAdapter(adaptador);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.eliminar_palabra_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dbHelper = new DbHelper(this);
    }

    private void rellenarPalabras() {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db != null) {
            String query = "SELECT * FROM " + Utilidades.TABLA_PALABRAS + " WHERE " + Utilidades.PALABRAS_COLECCION + " = ?";
            String[] selectionArgs = new String[]{idColeccion.toString()};
            SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, selectionArgs);
            if (c.getCount() != 0) {
                c.moveToFirst();
                String palabra;
                String traduccion;
                String tipo;
                Long id;
                Palabra p;
                do {
                    palabra = c.getString(c.getColumnIndex(Utilidades.PALABRAS_NOMBRE));
                    traduccion = c.getString(c.getColumnIndex(Utilidades.PALABRAS_TRADUCCION));
                    tipo = c.getString(c.getColumnIndex(Utilidades.PALABRAS_TIPO));
                    id = c.getLong(c.getColumnIndex(Utilidades.PALABRAS_ID));
                    p = new Palabra(id,palabra, traduccion, tipo);
                    palabras.add(p);
                } while(c.moveToNext());
            }
        }
    }

    public static void crearDialogo(Palabra p) {
        dialog.show();
        TextView tv = dialog.findViewById(R.id.tv_confirmar_eliminar_palabra);
        String textToChange = tv.getText().toString();
        palabra = p;
        textToChange = textToChange.replace("*x*", p.getPalabra());
        tv.setText(textToChange);
    }

    public void eliminarPalabra(View view) {
        Button boton = (Button) view;
        if (boton.getText().equals(getResources().getString(R.string.confirmar))) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db != null) {
                String where = Utilidades.PALABRAS_ID + " = ?";
                String[] campos = new String[]{palabra.getId().toString()};
                int rows = db.delete(Utilidades.TABLA_PALABRAS, where, campos);
                if (rows > 0) {
                        Toast.makeText(this, "La palabra se ha eliminado correctamente", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                        palabras.clear();
                        rellenarPalabras();
                        AdaptadorPalabras adaptador = new AdaptadorPalabras(palabras, this, bundle);
                        rv.setAdapter(adaptador);
                }
                else {
                    Toast.makeText(this, "Ha habido un problema al eliminar la palabra", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else {
            dialog.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        palabras.clear();
        rellenarPalabras();
        adaptador = new AdaptadorPalabras(palabras, this, bundle);
        rv.setAdapter(adaptador);
    }
}
