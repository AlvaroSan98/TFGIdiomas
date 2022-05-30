package com.example.tfgidiomas.desordenado;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfgidiomas.Palabra;
import com.example.tfgidiomas.R;
import com.example.tfgidiomas.db.DbHelper;
import com.example.tfgidiomas.db.Utilidades;

import java.util.ArrayList;
import java.util.Random;

public class JuegoDesordenado extends AppCompatActivity implements DesordenadoObservador {

    private LinearLayout layout_sol;
    private GridLayout layout_ques;

    private ArrayList<Palabra> palabras;
    private Palabra palabra;
    private int correctasCount;
    private int escogidas;
    private int tiempoSobrante;

    private ArrayList<ButtonDesordenado> answer;
    private ArrayList<ButtonDesordenado> question;
    private int[] letrasSolucion;

    private Bundle bundle;
    private ArrayList<Palabra> palabrasJugadas;

    private ProgressBar bar;
    private CountDownTimer timer;
    private TextView texto_bar;
    private TextView tv_sumarTiempo;
    private int tiempo;
    private MediaPlayer respuestaCorrecta;
    private MediaPlayer respuestaIncorrecta;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego_desordenado);
        layout_sol = findViewById(R.id.layout_botones_sol);
        layout_ques = findViewById(R.id.layout_botones_question);
        bar = findViewById(R.id.temporizador);
        texto_bar = findViewById(R.id.texto_bar);
        tv_sumarTiempo = findViewById(R.id.sumar_tiempo);

        respuestaCorrecta = MediaPlayer.create(this, R.raw.respuesta_correcta);
        respuestaIncorrecta = MediaPlayer.create(this, R.raw.respuesta_incorrecta);

        bundle = getIntent().getBundleExtra("bundle");

        question = new ArrayList<ButtonDesordenado>();
        answer = new ArrayList<ButtonDesordenado>();

        palabras = new ArrayList<Palabra>();
        palabrasJugadas = new ArrayList<Palabra>();
        tiempo = 30;
        correctasCount = 0;
        getPalabras();
        escogidas = palabras.size();
        escogerPalabra();
        fillLetrasSolucion();
        asignarBotones();
        activarTiempo(tiempo);
    }

    private void aFinal() {
        tiempoSobrante = Integer.parseInt(texto_bar.getText().toString());
        Intent i = new Intent(this, DesordenadoFinal.class);
        i.putExtra("bundle", bundle);
        i.putExtra("palabras_total", escogidas);
        i.putExtra("palabras_acertadas", correctasCount);
        i.putExtra("tiempo_sobrante", tiempoSobrante);
        i.putExtra("palabras_jugadas", palabrasJugadas);
        startActivity(i);
    }

    private void escogerPalabra() {
        Random rand = new Random();
        if (palabras.size() == 0) {
            aFinal();
        } else {
            int escogida = rand.nextInt(palabras.size());
            palabra = palabras.get(escogida);
            palabrasJugadas.add(palabras.get(escogida));
            palabras.remove(escogida);
            TextView tv_palabra = findViewById(R.id.palabraPregunta);
            tv_palabra.setText(palabra.getPalabra());
        }
    }

    private void asignarBotones() {
        String _palabra = palabra.getTraduccion();
        layout_sol.setOrientation(LinearLayout.VERTICAL);
        int lineasLayout = _palabra.length() / 6;
        if (_palabra.length() % 6 > 0) lineasLayout++;
        for (int i = 0; i < lineasLayout; i++) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            for (int count = 0; count < 6 && i * 6 + count < _palabra.length(); count++) {
                ButtonDesordenado but = new ButtonDesordenado(this, "answer", _palabra, null);
                but.setMinWidth(0);
                but.setMinHeight(0);
                but.setMinimumWidth(0);
                but.setMinimumHeight(0);
                answer.add(but);
                if (_palabra.charAt((i*6) + count) == ' ') {
                    but.setPulsado(true);
                    but.setLetra(' ');
                    but.setVisibility(View.INVISIBLE);
                }
                layout.addView(but);
            }
            layout_sol.addView(layout);
        }

        boolean contains = false;
        int posicion = 100;

        if (_palabra.length() > 10) {
            layout_ques.setRowCount(3);
            layout_ques.setColumnCount(6);
            for (int i = 0; i < 18; i++) {
                ButtonDesordenado b = new ButtonDesordenado(this, "question", _palabra, answer);
                b.enalzarObservador(this);
                for (int j = 0; j < _palabra.length(); j++) {
                    if (letrasSolucion[j] == i) {
                        contains = true;
                        posicion = j;
                        break;
                    }
                }
                if (!contains) {
                    Random rand = new Random();
                    char letra = (char) (rand.nextInt(26) + 'a');
                    b.setLetra(letra);
                } else {
                    b.setLetra(_palabra.charAt(posicion));
                }
                b.setMinWidth(0);
                b.setMinHeight(0);
                b.setMinimumWidth(0);
                b.setMinimumHeight(0);
                question.add(b);
                layout_ques.addView(b, i);
                contains = false;
            }
        }
        else {
            layout_ques.setRowCount(2);
            layout_ques.setColumnCount(6);
            for (int i = 0; i < 12; i++) {
                ButtonDesordenado b = new ButtonDesordenado(this, "question", _palabra, answer);
                b.enalzarObservador(this);
                for (int j = 0; j < _palabra.length(); j++) {
                    if (letrasSolucion[j] == i) {
                        contains = true;
                        posicion = j;
                        break;
                    }
                }
                if (!contains){
                    Random rand = new Random();
                    char letra = (char) (rand.nextInt(26) + 'a');
                    b.setLetra(letra);
                }
                else {
                    b.setLetra(_palabra.charAt(posicion));
                }
                contains = false;
                b.setMinWidth(0);
                b.setMinHeight(0);
                b.setMinimumWidth(0);
                b.setMinimumHeight(0);
                question.add(b);
                layout_ques.addView(b, i);
            }
        }

    }

    private void fillLetrasSolucion() {
        letrasSolucion = new int[palabra.getTraduccion().length()];
        boolean contains = false;
        Random rand = new Random();
        int aleatorio = 0;
        if (palabra.getTraduccion().length() > 10) {
            for (int i = 0; i < palabra.getTraduccion().length(); i++) {
                do {
                    if (palabra.getTraduccion().charAt(i) != ' ') {
                        contains = false;
                        aleatorio = rand.nextInt(18);
                        for (int j = 0; j < i; j++) {
                            if (letrasSolucion[j] == aleatorio) contains = true;
                        }
                    }
                } while (contains);
                letrasSolucion[i] = aleatorio;
            }
        }
        else {
            for (int i = 0; i < palabra.getTraduccion().length(); i++) {
                do {
                    if (palabra.getTraduccion().charAt(i) != ' ') {
                        contains = false;
                        aleatorio = rand.nextInt(12);
                        for (int j = 0; j < i; j++) {
                            if (letrasSolucion[j] == aleatorio) contains = true;
                        }
                    }
                } while (contains);
                letrasSolucion[i] = aleatorio;
            }
        }
    }

    private void getPalabras() {
        DbHelper dbHelper = new DbHelper(JuegoDesordenado.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            Toast.makeText(JuegoDesordenado.this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
        } else {
            ArrayList<Long> cols = (ArrayList<Long>) bundle.getSerializable("colecciones");
            String coleccionesToAdd = "(";
            for (int i = 0; i < cols.size() - 1; i++) {
                coleccionesToAdd += cols.get(i) + ",";
            }
            coleccionesToAdd += cols.get(cols.size() - 1) + ")";
            String query = "SELECT * FROM " + Utilidades.TABLA_PALABRAS + " WHERE " + Utilidades.PALABRAS_COLECCION
                    + " IN " + coleccionesToAdd;
            SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null);
            if (c.getCount() == 0) {
                Toast.makeText(this, "No se ha encontrado ninguna colección", Toast.LENGTH_LONG).show();
                db.close();
            } else {
                c.moveToFirst();
                do {
                    Palabra p = new Palabra();
                    p.setId(c.getLong(c.getColumnIndex(Utilidades.PALABRAS_ID)));
                    p.setPalabra(c.getString(c.getColumnIndex(Utilidades.PALABRAS_NOMBRE)));
                    p.setTraduccion(c.getString(c.getColumnIndex(Utilidades.PALABRAS_TRADUCCION)));
                    p.setTipo(c.getString(c.getColumnIndex(Utilidades.PALABRAS_TIPO)));
                    palabras.add(p);
                } while (c.moveToNext());
                db.close();
            }
        }
    }

    @Override
    public void update() {
        boolean correcta = true;
        boolean completada = true;
        for (int i = 0; i < answer.size(); i++) {
            if (!answer.get(i).isPulsado()) {
                completada = false;
            }
            if (Character.toLowerCase(answer.get(i).getLetra()) != Character.toLowerCase(palabra.getTraduccion().charAt(i))) {
                correcta = false;
            }
        }
        if (completada) {
            if (!correcta) {
                AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                respuestaIncorrecta.start();
                for (int i = 0; i < answer.size(); i++) {
                    answer.get(i).marcarError();
                }
            }
            else {
                AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                respuestaCorrecta.start();
                correctasCount++;
                for (int i = 0; i < answer.size(); i++) {
                    answer.get(i).marcarCorrecta();
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout_ques.removeAllViews();
                        layout_sol.removeAllViews();
                        answer.clear();
                        escogerPalabra();
                        fillLetrasSolucion();
                        asignarBotones();
                    }
                }, 500);
                Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_sumarTiempo.setVisibility(View.INVISIBLE);
                    }
                }, 1500);
                tv_sumarTiempo.setVisibility(View.VISIBLE);
                tv_sumarTiempo.setText("+10");
                tiempo = Integer.parseInt(texto_bar.getText().toString()) + 10;
                timer.cancel();
                activarTiempo(tiempo);
            }
        }
    }

    private void activarTiempo(int segundos) {
        final int tiempo = segundos;
        bar.setMax(tiempo);
        int intervalo = 1000;
        timer = new CountDownTimer(tiempo * intervalo, intervalo) {

            private int segundos = tiempo;

            @Override
            public void onTick(long l) {
                bar.setProgress(tiempo - segundos);
                texto_bar.setText("" + segundos);
                segundos--;
            }

            @Override
            public void onFinish() {
                bar.setProgress(tiempo - segundos);
                texto_bar.setText("" + segundos);
                aFinal();
            }
        };
        timer.start();
    }

    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        finish();
    }

}
