package com.example.tfgidiomas;

import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfgidiomas.adaptadores.AdaptadorColecciones;
import com.example.tfgidiomas.adaptadores.AdaptadorPalabras;
import com.example.tfgidiomas.db.DbHelper;
import com.example.tfgidiomas.db.Utilidades;

import java.util.ArrayList;

public class ColeccionesActivity extends AppCompatActivity {

    public RecyclerView rv;
    private ArrayList<Coleccion> colecciones;

    private Long idioma;
    private Bundle bundle;
    private AdaptadorColecciones adaptador;

    private static Dialog dialog;
    private DbHelper dbHelper;
    private static Coleccion coleccion;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colecciones);

        idioma = getIntent().getBundleExtra("bundle").getLong("idioma");
        bundle = getIntent().getBundleExtra("bundle");

        colecciones = new ArrayList<Coleccion>();
        rv = (RecyclerView) findViewById(R.id.rv_colecciones);
        rellenarColecciones();
        if (colecciones.size() == 0) {
            findViewById(R.id.tv_no_colecciones).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.tv_no_colecciones).setVisibility(View.INVISIBLE);
        }
        rv.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        RecyclerView.ItemDecoration id = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view) % 2;
                if (position == 0) {
                    outRect.right = 16;
                }
                else {
                    outRect.left = 16;
                }
            }
        };
        rv.addItemDecoration(id);
        adaptador = new AdaptadorColecciones(colecciones, this);
        rv.setAdapter(adaptador);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.eliminar_coleccion_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dbHelper = new DbHelper(this);
    }

    public static void crearDialogo(Coleccion c) {
        dialog.show();
        TextView tv = dialog.findViewById(R.id.tv_confirmar_eliminar_coleccion);
        String textToChange = tv.getText().toString();
        coleccion = c;
        textToChange = textToChange.replace("*x*", c.getNombre());
        tv.setText(textToChange);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void aInsideColeccion(View view) {
        Button b = (Button) view;
        String coleccion = b.getText().toString();
        Intent i = new Intent(this, InsideColeccion.class);
        for (int j = 0; j < colecciones.size(); j++) {
            if (colecciones.get(j).getNombre().equals(coleccion)) {
                i.putExtra("coleccion", colecciones.get(j).getId());
                i.putExtra("coleccion_str", colecciones.get(j).getNombre());
            }
        }
        i.putExtra("bundle", bundle);
        startActivity(i);
    }

    public void eliminarColeccion(View view) {
        Button boton = (Button) view;
        if (boton.getText().equals(getResources().getString(R.string.confirmar))) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db != null) {
                String where = Utilidades.COLECCIONES_ID + " = ?";
                String[] campos = new String[]{coleccion.getId().toString()};
                int rows = db.delete(Utilidades.TABLA_COLECCIONES, where, campos);
                if (rows > 0) {
                    Toast.makeText(this, "La colección se ha eliminado correctamente", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    colecciones.clear();
                    rellenarColecciones();
                    adaptador = new AdaptadorColecciones(colecciones, this);
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

    private void rellenarColecciones() {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db != null) {
            String query = "SELECT * FROM " + Utilidades.TABLA_COLECCIONES + " WHERE " + Utilidades.COLECCIONES_IDIOMA + " = ?";
            String[] selectionArgs = new String[]{idioma.toString()};
            SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, selectionArgs);
            Coleccion col;
            int nPalabras;
            String nombre_coleccion;
            Long id_coleccion;
            if (c.getCount() != 0) {
                c.moveToFirst();
                do {
                    nombre_coleccion = c.getString(c.getColumnIndex(Utilidades.COLECCIONES_NOMBRE));
                    id_coleccion = c.getLong(c.getColumnIndex(Utilidades.COLECCIONES_ID));
                    col = new Coleccion(nombre_coleccion, idioma, id_coleccion);
                    colecciones.add(col);
                } while (c.moveToNext());
            }
            db.close();
        }
    }

}
