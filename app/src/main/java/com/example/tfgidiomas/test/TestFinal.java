package com.example.tfgidiomas.test;

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
import com.example.tfgidiomas.Partida;
import com.example.tfgidiomas.R;
import com.example.tfgidiomas.adaptadores.AdaptadorPalabrasFinal;
import com.example.tfgidiomas.db.DbHelper;
import com.example.tfgidiomas.db.Utilidades;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TestFinal extends AppCompatActivity {

    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView tv_buen_intento;

    private Bundle bundle;
    private int[] puntosPalbras;
    private ArrayList<Palabra> palabrasJugadas;
    private int puntos;
    private int vidasSobrantes;

    private String juego;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_final);

        tv1 = findViewById(R.id.tv_palabras_acertadas_test);
        tv2 = findViewById(R.id.tv_puntos_test_base);
        tv3 = findViewById(R.id.tv_puntos_test_acertados);
        tv4 = findViewById(R.id.tv_vidas_sobrantes);
        tv5 = findViewById(R.id.tv_puntos_test_total);

        tv_buen_intento = findViewById(R.id.tv_fin_intruso);

        juego = getIntent().getStringExtra("juego");
        if (juego.equalsIgnoreCase("test")) tv_buen_intento.setVisibility(View.INVISIBLE);
        else tv_buen_intento.setVisibility(View.VISIBLE);
        bundle = getIntent().getBundleExtra("bundle");
        palabrasJugadas = (ArrayList<Palabra>) getIntent().getSerializableExtra("palabrasJugadas");
        ArrayList<Palabra> palabrasAcertadas = (ArrayList<Palabra>) getIntent().getSerializableExtra("palabrasAcertadas");
        vidasSobrantes = getIntent().getIntExtra("vidasSobrantes", 0);

        puntosPalbras = new int[palabrasJugadas.size()];
        puntosConseguidos();


        RecyclerView rv = findViewById(R.id.rv_palabras_test_final);
        rellenaPalabrasPuntos(palabrasJugadas, palabrasAcertadas);
        AdaptadorPalabrasFinal adapter = new AdaptadorPalabrasFinal(palabrasJugadas, puntosPalbras, this);
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv.setAdapter(adapter);

        registrarPartida();
    }

    public void aCasa(View view) {
        Intent i = new Intent(this, PantallaPrincipal.class);
        i.putExtra("bundle", bundle);
        startActivity(i);
        finish();
    }

    private void registrarPartida() {
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
        tv1.setText(acertadas + " palabras acertadas de " + total + " escogidas");
        calculaPuntos(total, acertadas);
        tv5.setText("¡Has conseguido " + puntos + " puntos!");
        float estadistica = (float) acertadas / (total - 4);
        if (estadistica > 0.25 && estadistica < 0.75) tv_buen_intento.setText(getString(R.string.se_puede_mejorar));
        else if (estadistica >= 0.75) tv_buen_intento.setText(R.string.buen_intento);
        else tv_buen_intento.setText(getString(R.string.ponte_las_pilas));
    }

    private void calculaPuntos(int total, int acertadas) {
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
        tv4.setText(vidasSobrantes + " vidas sobrantes -> " + vidasSobrantes + " x 20 = " + vidasSobrantes*20 + " puntos");
        puntos += vidasSobrantes*20;
        this.puntos = puntos;
    }

    private void rellenaPalabrasPuntos(ArrayList<Palabra> jugadas, ArrayList<Palabra> acertadas) {
        int i = 0;
        int j = 0;
        if (juego.equalsIgnoreCase("test")) {
            while (i < jugadas.size()) {
                if (jugadas.get(i).getPalabra().equals(acertadas.get(j).getPalabra())) {
                    puntosPalbras[i] = 2;
                    i++;
                    j++;
                }
                else {
                    puntosPalbras[i] = 0;
                    i++;
                }
            }
        }
        else {
            while (i < jugadas.size()) {
                if (jugadas.get(i).equals(acertadas.get(j))) {
                    puntosPalbras[i] = 2;
                    i++;
                    j++;
                }
                else {
                    puntosPalbras[i] = 0;
                    i++;
                }
            }
        }
    }
}