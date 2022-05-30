package com.example.tfgidiomas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tfgidiomas.adaptadores.AdaptadorIdiomas;
import com.example.tfgidiomas.db.DbHelper;
import com.example.tfgidiomas.db.Utilidades;

import java.util.ArrayList;

public class ElegirIdioma extends AppCompatActivity {

    public ImageButton btn_add;
    public EditText et_añadir_idioma;
    public ImageButton btn_aceptar;
    public RecyclerView recycler;

    private ArrayList<Idioma> idiomas;
    private String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eleccion_idioma);

        btn_add = findViewById(R.id.btn_add_idioma);
        btn_aceptar = findViewById(R.id.btn_aceptar_idioma);
        et_añadir_idioma = findViewById(R.id.et_nuevo_idioma);

        recycler = (RecyclerView) findViewById(R.id.recycler_idiomas);
        recycler.setLayoutManager(new GridLayoutManager(this, 1));
        usuario = getIntent().getStringExtra("usuario");
        if (usuario == null) usuario = getIntent().getBundleExtra("bundle").getString("usuario");

        idiomas = new ArrayList<Idioma>();
        rellenarIdiomas(usuario);

        AdaptadorIdiomas adaptador = new AdaptadorIdiomas(idiomas);
        recycler.setAdapter(adaptador);
    }

    public void aPantallaPrincipal(View view) {
        Intent i = new Intent(this, PantallaPrincipal.class);
        Bundle bundle = new Bundle();
        bundle.putString("usuario", usuario);
        Button btn = (Button) view;
        Long id_idioma = new Long(0);
        for (int j = 0; j < idiomas.size(); j++) {
            if (btn.getText().toString().equals(idiomas.get(j).get_idioma())) {
                id_idioma = idiomas.get(j).get_id();
            }
        }
        bundle.putLong("idioma", id_idioma);
        i.putExtra("bundle", bundle);
        startActivity(i);
        finish();
    }

    public void aPantallaPrincipalNuevo (Long id_idioma) {
        Intent i = new Intent(this, PantallaPrincipal.class);
        Bundle bundle = new Bundle();
        bundle.putString("usuario", usuario);
        bundle.putLong("idioma", id_idioma);
        i.putExtra("bundle", bundle);
        startActivity(i);
        finish();
    }

    public void addIdioma(View view) {
        btn_add.setVisibility(View.INVISIBLE);
        et_añadir_idioma.setVisibility(View.VISIBLE);
        btn_aceptar.setVisibility(View.VISIBLE);
    }

    public void aceptarIdioma(View view) {
        String idioma = et_añadir_idioma.getText().toString();
        if (idioma.trim().isEmpty()) {
            Toast.makeText(this, "Escribe el idioma que quieres añadir", Toast.LENGTH_LONG).show();
        }
        else {
            DbHelper dbHelper = new DbHelper(ElegirIdioma.this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db == null) {
                Toast.makeText(this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
            }
            else {
                Idioma lan = new Idioma(idioma, 0, usuario);
                Long idResultante = db.insert(Utilidades.TABLA_IDIOMAS, Utilidades.IDIOMAS_NOMBRE, lan.toContentValues());
                if (idResultante == -1) {
                    Toast.makeText(this, "Error al insertar idioma en bbdd", Toast.LENGTH_LONG).show();
                }
                else {
                    //Toast.makeText(this, "Idioma añadido correctamente", Toast.LENGTH_LONG).show();
                    aPantallaPrincipalNuevo(idResultante);
                }
            }
        }
    }

    private void rellenarIdiomas(String usuario) {
        DbHelper dbHelper = new DbHelper(ElegirIdioma.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            Toast.makeText(this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
        }
        else {
            String[] camposDevueltos = new String[]{Utilidades.IDIOMAS_NOMBRE, Utilidades.IDIOMAS_USUARIO, Utilidades.IDIOMAS_ID};
            String select = Utilidades.IDIOMAS_USUARIO + " = ?";
            String[] selectionArgs = new String[]{usuario};
            SQLiteCursor c = (SQLiteCursor) db.query(
                    Utilidades.TABLA_IDIOMAS,
                    camposDevueltos,
                    select,
                    selectionArgs,
                    null,
                    null,
                    null
            );
            c.moveToFirst();
            do {
                idiomas.add(new Idioma(c.getString(c.getColumnIndex(Utilidades.IDIOMAS_NOMBRE)), 0,  c.getString(c.getColumnIndex(Utilidades.IDIOMAS_USUARIO)),
                        c.getLong(c.getColumnIndex(Utilidades.IDIOMAS_ID))));
            } while (c.moveToNext());
            db.close();
        }
    }
}