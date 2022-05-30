package com.example.tfgidiomas;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfgidiomas.adaptadores.AdaptadorPartidasPerfil;
import com.example.tfgidiomas.db.DbHelper;
import com.example.tfgidiomas.db.Utilidades;

import java.util.ArrayList;

public class PerfilActivity extends AppCompatActivity {

    private TextView tv_colecciones;
    private TextView tv_palabras;
    private TextView tv_idioma;

    private Bundle bundle;
    private ArrayList<Partida> partidas;

    private int count_colecciones;
    private int count_palabras;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        bundle = getIntent().getBundleExtra("bundle");
        partidas = new ArrayList<Partida>();

        count_colecciones = 0;
        count_palabras = 0;

        contarColecciones();
        tv_colecciones = findViewById(R.id.tv_colecciones_perfil);
        tv_palabras = findViewById(R.id.tv_palabras_perfil);
        tv_idioma = findViewById(R.id.tv_idioma_perfil);
        tv_colecciones.setText("Colecciones: " + count_colecciones);
        tv_palabras.setText("Palabras: " + count_palabras);
        tv_idioma.setText("Idioma: " + nombreIdioma());

        rellenarPartidas();

        RecyclerView rv = findViewById(R.id.rv_partidas_perfil);
        AdaptadorPartidasPerfil adapter = new AdaptadorPartidasPerfil(partidas, this, bundle);
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv.setAdapter(adapter);

    }

    private String nombreIdioma() {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            Toast.makeText(this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
        }
        else {
            String[] camposDevueltos = new String[]{Utilidades.IDIOMAS_NOMBRE};
            String select = Utilidades.IDIOMAS_ID + " = ?";
            String[] selectionArgs = new String[]{"" + bundle.getLong("idioma")};
            Cursor c = db.query(
                    Utilidades.TABLA_IDIOMAS,
                    camposDevueltos,
                    select,
                    selectionArgs,
                    null,
                    null,
                    null
            );
            if (!c.moveToFirst()) {
                Toast.makeText(this, "No se ha encontrado ninguna partida de este usuario", Toast.LENGTH_LONG).show();
                db.close();
            } else {
                return c.getString(c.getColumnIndex(Utilidades.IDIOMAS_NOMBRE));
            }
        }
        return "ERROR AL BUSCAR IDIOMA";
    }

    private void contarColecciones() {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            Toast.makeText(this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
        }
        else {
            String[] camposDevueltos = new String[]{Utilidades.COLECCIONES_ID};
            String select = Utilidades.COLECCIONES_IDIOMA + " = ?";
            String[] selectionArgs = new String[]{"" + bundle.getLong("idioma")};
            Cursor c = db.query(
                    Utilidades.TABLA_COLECCIONES,
                    camposDevueltos,
                    select,
                    selectionArgs,
                    null,
                    null,
                    null
            );
            if (!c.moveToFirst()) {
                Toast.makeText(this, "No se ha encontrado ninguna partida de este usuario", Toast.LENGTH_LONG).show();
                db.close();
            }
            else {
                Long idColeccion;
                do {
                    idColeccion = c.getLong(c.getColumnIndex(Utilidades.COLECCIONES_ID));
                    count_colecciones++;
                    camposDevueltos = new String[]{Utilidades.PALABRAS_ID};
                    select = Utilidades.PALABRAS_COLECCION + " = ?";
                    selectionArgs = new String[]{"" + idColeccion};
                    Cursor c_aux = db.query(
                            Utilidades.TABLA_PALABRAS,
                            camposDevueltos,
                            select,
                            selectionArgs,
                            null,
                            null,
                            null
                    );
                    count_palabras += c_aux.getCount();
                } while (c.moveToNext());
            }
        }
    }

    private void rellenarPartidas() {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            Toast.makeText(this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
        }
        else {
            String query = "SELECT * FROM " + Utilidades.TABLA_PARTIDAS + " p INNER JOIN " + Utilidades.TABLA_PARTIDA_USUARIO
                    + " pu ON p." + Utilidades.PARTIDAS_ID + "=pu." + Utilidades.PARTIDA_USUARIO_PARTIDA + " WHERE "
                    + Utilidades.PARTIDA_USUARIO_USUARIO + "=? ORDER BY " + Utilidades.PARTIDAS_ID + " DESC";
            String[] selectionArgs = new String[]{bundle.getString("usuario")};
            Cursor c = db.rawQuery(query, selectionArgs);
            if (!c.moveToFirst()) {
                Toast.makeText(this, "No se han encontrado partidas para este usuario", Toast.LENGTH_SHORT).show();
            }
            else {
                Long id;
                String fecha;
                int puntos;
                String juego;
                do {
                    juego = c.getString(c.getColumnIndex(Utilidades.PARTIDAS_JUEGO));
                    if (!juego.equalsIgnoreCase("wordle")) {
                        id = c.getLong(c.getColumnIndex(Utilidades.PARTIDAS_ID));
                        fecha = c.getString(c.getColumnIndex(Utilidades.PARTIDAS_FECHA));
                        puntos = c.getInt(c.getColumnIndex(Utilidades.PARTIDAS_PUNTOS));
                        Partida p = new Partida(id, fecha, puntos, juego, null);
                        partidas.add(p);
                    }
                } while (c.moveToNext());
            }
        }
    }
}
