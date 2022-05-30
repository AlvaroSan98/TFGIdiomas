package com.example.tfgidiomas.desordenado;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfgidiomas.Palabra;
import com.example.tfgidiomas.PantallaPrincipal;
import com.example.tfgidiomas.R;
import com.example.tfgidiomas.adaptadores.AdaptadorPalabrasFinal;
import com.example.tfgidiomas.db.DbHelper;
import com.example.tfgidiomas.db.Utilidades;
import com.example.tfgidiomas.Partida;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DesordenadoFinal extends AppCompatActivity {

    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;

    private Bundle bundle;

    private ArrayList<Palabra> palabrasJugadas;
    private int puntos;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desordenado_final);

        tv1 = findViewById(R.id.tv_palabras_acertadas_test);
        tv2 = findViewById(R.id.tv_puntos_test_base);
        tv3 = findViewById(R.id.tv_puntos_test_acertados);
        tv4 = findViewById(R.id.tv_tiempo_sobrante);
        tv5 = findViewById(R.id.tv_puntos_test_total);

        bundle = getIntent().getBundleExtra("bundle");

        palabrasJugadas = (ArrayList<Palabra>) getIntent().getSerializableExtra("palabras_jugadas");

        RecyclerView rv = findViewById(R.id.rv_palabras_des_final);
        AdaptadorPalabrasFinal adapter = new AdaptadorPalabrasFinal(palabrasJugadas, this);
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv.setAdapter(adapter);

        puntosConseguidos();

        registrarPartida();

    }

    private void registrarPartida() {
        String juego = "desordenado";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);
        Partida partida = new Partida(null, fecha, puntos, juego, palabrasJugadas);
        insertarPartidaBBDD(partida);
    }

    private void insertarPartidaBBDD(Partida partida) {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            Long idPartida = db.insert(Utilidades.TABLA_PARTIDAS, Utilidades.PARTIDAS_ID, partida.toContentValues());
            if (idPartida == -1) {
                Toast.makeText(this, "Error al introducir en la tabla palabras", Toast.LENGTH_LONG).show();
                db.close();
            }
            else {
                ContentValues cv = new ContentValues();
                cv.put(Utilidades.PARTIDA_USUARIO_USUARIO, bundle.getString("usuario"));
                cv.put(Utilidades.PARTIDA_USUARIO_PARTIDA, idPartida);
                Long idPartidaUsuario = db.insert(Utilidades.TABLA_PARTIDA_USUARIO, Utilidades.PARTIDA_USUARIO_USUARIO, cv);
                if (idPartidaUsuario == -1) {
                    Toast.makeText(this, "Error al introducir en la tabla partida-usuario", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void puntosConseguidos() {
        int total = getIntent().getIntExtra("palabras_total", 0);
        int acertadas = getIntent().getIntExtra("palabras_acertadas", 0);
        int tiempoSobrante = getIntent().getIntExtra("tiempo_sobrante", 0);
        tv1.setText(acertadas + " palabras acertadas de " + total + " escogidas");
        puntos = calculaPuntos(total, acertadas, tiempoSobrante);
        tv5.setText("¡Has conseguido " + puntos + " puntos!");
    }

    public void aCasa(View view) {
        Intent i = new Intent(this, PantallaPrincipal.class);
        i.putExtra("bundle", bundle);
        startActivity(i);
        finish();
    }

    private int calculaPuntos(int total, int acertadas, int tiempo) {
        int puntos = 0;
        if (total <= 25) {
            tv2.setText(total + " palabras escogidas = " + 0 + " puntos");
        }
        else if (total > 25 && total <= 50) {
            tv2.setText(total + " palabras escogidas = " + 10 + " puntos");
            puntos += 10;
        }
        else if (total > 50 && total <= 100) {
            tv2.setText(total + " palabras escogidas = " + 25 + " puntos");
            puntos += 25;
        }
        else if (total > 100 && total <= 200) {
            tv2.setText(total + " palabras escogidas = " + 50 + " puntos");
            puntos += 50;
        }
        else {
            tv2.setText(total + " palabras escogidas = " + 100 + " puntos");
            puntos += 100;
        }
        tv3.setText(acertadas + " palabras acertadas -> " + acertadas + " x 2 = " + acertadas*2 + " puntos");
        puntos += acertadas*2;
        tv4.setText(tiempo + " segundos sobrantes -> " + tiempo + " x 5 = " + tiempo*5 + " puntos");
        puntos += tiempo*5;
        return puntos;
    }

}