package com.example.tfgidiomas.test;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfgidiomas.Palabra;
import com.example.tfgidiomas.R;
import com.example.tfgidiomas.db.DbHelper;
import com.example.tfgidiomas.db.Utilidades;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Random;

public class JuegoTest extends AppCompatActivity {

    private Bundle bundle;
    private ArrayList<Palabra> sustantivos;
    private ArrayList<Palabra> adjetivos;
    private ArrayList<Palabra> adverbios;
    private ArrayList<Palabra> verbos;
    private ArrayList<Palabra> pronombres;
    private ArrayList<Palabra> determinante;
    private ArrayList<Palabra> preposicion;
    private ArrayList<Palabra> conjuncion;

    private Pregunta pre;
    private Boolean correcta;
    private int contador;
    private int c_vidas;
    private Palabra palabraPreguntada;
    private ArrayList<Palabra> palabrasJugadas;
    private ArrayList<String> tiposJugados;
    private ArrayList<Palabra> palabrasAcertadas;
    private ArrayList<String> tiposAcertados;

    private Boolean otraPregunta;

    private ArrayList<Palabra> palabrasTotal;

    private TextView tv_que_significa;
    private TextView tv_contador;
    private TextView vidas_count;
    private TextView pregunta;
    private MaterialButton mb1;
    private MaterialButton mb2;
    private MaterialButton mb3;
    private MaterialButton mb4;
    private ImageView check;
    private ImageView cross;
    private MediaPlayer respuestaCorrecta;
    private MediaPlayer respuestaIncorrecta;

    private String juego;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego_test);

        juego = getIntent().getStringExtra("juego");
        bundle = getIntent().getBundleExtra("bundle");

        sustantivos = new ArrayList<Palabra>();
        adjetivos = new ArrayList<Palabra>();
        adverbios = new ArrayList<Palabra>();
        verbos = new ArrayList<Palabra>();
        pronombres = new ArrayList<Palabra>();
        preposicion = new ArrayList<Palabra>();
        determinante = new ArrayList<Palabra>();
        conjuncion = new ArrayList<Palabra>();
        palabrasTotal = new ArrayList<Palabra>();
        contador = 0;
        otraPregunta = false;
        c_vidas = 5;

        palabrasJugadas = new ArrayList<Palabra>();
        palabrasAcertadas = new ArrayList<Palabra>();
        tiposJugados = new ArrayList<String>();
        tiposAcertados = new ArrayList<String>();

        tv_contador = findViewById(R.id.contador);
        vidas_count = findViewById(R.id.vidas_count);
        tv_que_significa = findViewById(R.id.tv_que_significa);
        if (juego.equalsIgnoreCase("test")) tv_que_significa.setText("¿Qué significa...?");
        else tv_que_significa.setText("¿Cuál no es un...?");
        vidas_count.setText("" + c_vidas);
        instanciarVista();

        getPalabras();
        if (juego.equalsIgnoreCase("test")) escogePregunta();
        else escogePreguntaIntruso();
     }

     public void marcarRespuesta(String respuesta) {
        if (respuesta.equals(pre.getOpcion1())) correcta = true;
        else correcta = false;
         AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

         if (correcta) {
             if (juego.equalsIgnoreCase("test")) {
                 palabrasJugadas.add(palabraPreguntada);
                 palabrasAcertadas.add(palabraPreguntada);
             }
             else {
                 tiposJugados.add(pregunta.getText().toString());
                 tiposAcertados.add(pregunta.getText().toString());
             }
            check.setVisibility(View.VISIBLE);
            respuestaCorrecta.start();
            contador++;
        }
        else {
            if (juego.equalsIgnoreCase("test")) palabrasJugadas.add(palabraPreguntada);
            else tiposJugados.add(pregunta.getText().toString());
            cross.setVisibility(View.VISIBLE);
            respuestaIncorrecta.start();
            c_vidas--;
        }
         mb1.setEnabled(false);
         mb2.setEnabled(false);
         mb3.setEnabled(false);
         mb4.setEnabled(false);
         esperarNuevaPregunta(2000);
     }

     private int sumaPalabras() {
        return sustantivos.size() + adjetivos.size() + adverbios.size() + verbos.size() + pronombres.size() + preposicion.size()
                + determinante.size() + conjuncion.size();
     }

    private void esperarNuevaPregunta(int milisegundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cross.setVisibility(View.INVISIBLE);
                check.setVisibility(View.INVISIBLE);
                tv_contador.setText(contador + " correctas");
                vidas_count.setText("" + c_vidas);
                mb1.setEnabled(true);
                mb2.setEnabled(true);
                mb3.setEnabled(true);
                mb4.setEnabled(true);
                if (sumaPalabras() < 4) aTestFinal();
                else if (c_vidas == 0) aTestFinal();
                else {
                    if (juego.equalsIgnoreCase("test")) escogePregunta();
                    else {
                        if (validarSiguientePreguntaIntruso()) escogePreguntaIntruso();
                        else aTestFinal();
                    }
                }
            }
        }, milisegundos);
    }


    private void instanciarVista() {
        pregunta = findViewById(R.id.palabraPregunta);
        mb1 = findViewById(R.id.opcion1);
        mb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marcarRespuesta(mb1.getText().toString());
            }
        });
        mb2 = findViewById(R.id.opcion2);
        mb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marcarRespuesta(mb2.getText().toString());
            }
        });mb3 = findViewById(R.id.opcion3);
        mb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marcarRespuesta(mb3.getText().toString());
            }
        });
        mb4 = findViewById(R.id.opcion4);
        mb4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marcarRespuesta(mb4.getText().toString());
            }
        });
        check = findViewById(R.id.check);
        cross = findViewById(R.id.cross);
        respuestaCorrecta = MediaPlayer.create(this, R.raw.respuesta_correcta);
        respuestaIncorrecta = MediaPlayer.create(this, R.raw.respuesta_incorrecta);
        tv_contador = findViewById(R.id.contador);
    }

    private void escogePreguntaIntruso() {
        Random rand = new Random();
        int tipo;
        int palabra;
        pre = new Pregunta();
        boolean isEmptyIntruso;
        boolean isEmpty = false;
        do {
            tipo = rand.nextInt(7);
            switch (tipo) {
                case 0:
                    if (sustantivos.size() < 3) {
                        aTestFinal();
                    } else {
                        isEmpty = false;
                        tv_que_significa.setText("¿Cuál no es un...?");
                        pregunta.setText("Sustantivo");
                        int tipoIntruso;
                        isEmptyIntruso = false;
                        do {
                            tipoIntruso = rand.nextInt(6);
                            switch (tipoIntruso) {
                                case 0:
                                    if (adjetivos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(adjetivos.get(rand.nextInt(adjetivos.size())).getPalabra());
                                    }
                                    break;
                                case 1:
                                    if (adverbios.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(adverbios.get(rand.nextInt(adverbios.size())).getPalabra());
                                    }
                                    break;
                                case 2:
                                    if (verbos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(verbos.get(rand.nextInt(verbos.size())).getPalabra());
                                    }
                                    break;
                                case 3:
                                    if (determinante.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(determinante.get(rand.nextInt(determinante.size())).getPalabra());
                                    }
                                    break;
                                case 4:
                                    if (pronombres.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(pronombres.get(rand.nextInt(pronombres.size())).getPalabra());
                                    }
                                    break;
                                case 5:
                                    if (preposicion.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(preposicion.get(rand.nextInt(preposicion.size())).getPalabra());
                                    }
                                    break;
                                default:
                                    if (conjuncion.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(conjuncion.get(rand.nextInt(conjuncion.size())).getPalabra());
                                    }
                            }
                        } while (isEmptyIntruso);
                        pre.setOpcion2(sustantivos.get(rand.nextInt(sustantivos.size())).getPalabra());
                        while (pre.getOpcion2().equals(pre.getOpcion1()))
                            pre.setOpcion2(sustantivos.get(rand.nextInt(sustantivos.size())).getPalabra());
                        pre.setOpcion3(sustantivos.get(rand.nextInt(sustantivos.size())).getPalabra());
                        while (pre.getOpcion3().equals(pre.getOpcion1()) || pre.getOpcion3().equals(pre.getOpcion2()))
                            pre.setOpcion3(sustantivos.get(rand.nextInt(sustantivos.size())).getTraduccion());
                        pre.setOpcion4(sustantivos.get(rand.nextInt(sustantivos.size())).getPalabra());
                        while (pre.getOpcion4().equals(pre.getOpcion1()) || pre.getOpcion4().equals(pre.getOpcion2()) || pre.getOpcion4().equals(pre.getOpcion3()))
                            pre.setOpcion4(sustantivos.get(rand.nextInt(sustantivos.size())).getPalabra());

                        sustantivos.remove(sustantivos.size() - 1);
                    }
                    break;
                case 1:
                    if (adjetivos.size() < 3) {
                        isEmpty = true;
                    } else {
                        isEmpty = false;
                        tv_que_significa.setText("¿Cuál no es un...?");
                        pregunta.setText("Adjetivo");
                        int tipoIntruso;
                        isEmptyIntruso = false;
                        do {
                            tipoIntruso = rand.nextInt(6);
                            switch (tipoIntruso) {
                                case 0:
                                    if (sustantivos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(sustantivos.get(rand.nextInt(sustantivos.size())).getPalabra());
                                    }
                                    break;
                                case 1:
                                    if (adverbios.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(adverbios.get(rand.nextInt(adverbios.size())).getPalabra());
                                    }
                                    break;
                                case 2:
                                    if (verbos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(verbos.get(rand.nextInt(verbos.size())).getPalabra());
                                    }
                                    break;
                                case 3:
                                    if (determinante.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(determinante.get(rand.nextInt(determinante.size())).getPalabra());
                                    }
                                    break;
                                case 4:
                                    if (pronombres.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(pronombres.get(rand.nextInt(pronombres.size())).getPalabra());
                                    }
                                    break;
                                case 5:
                                    if (preposicion.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(preposicion.get(rand.nextInt(preposicion.size())).getPalabra());
                                    }
                                    break;
                                default:
                                    if (conjuncion.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(conjuncion.get(rand.nextInt(conjuncion.size())).getPalabra());
                                    }
                            }
                        } while (isEmptyIntruso);
                        pre.setOpcion2(adjetivos.get(rand.nextInt(adjetivos.size())).getPalabra());
                        while (pre.getOpcion2().equals(pre.getOpcion1()))
                            pre.setOpcion2(adjetivos.get(rand.nextInt(adjetivos.size())).getPalabra());
                        pre.setOpcion3(adjetivos.get(rand.nextInt(adjetivos.size())).getPalabra());
                        while (pre.getOpcion3().equals(pre.getOpcion1()) || pre.getOpcion3().equals(pre.getOpcion2()))
                            pre.setOpcion3(adjetivos.get(rand.nextInt(adjetivos.size())).getPalabra());
                        pre.setOpcion4(adjetivos.get(rand.nextInt(adjetivos.size())).getPalabra());
                        while (pre.getOpcion4().equals(pre.getOpcion1()) || pre.getOpcion4().equals(pre.getOpcion2()) || pre.getOpcion4().equals(pre.getOpcion3()))
                            pre.setOpcion4(adjetivos.get(rand.nextInt(adjetivos.size())).getPalabra());
                        adjetivos.remove(adjetivos.size() - 1);
                    }
                    break;
                case 2:
                    if (adverbios.size() < 3) {
                        isEmpty = true;
                    } else {
                        isEmpty = false;
                        tv_que_significa.setText("¿Cuál no es un...?");
                        pregunta.setText("Adverbio");
                        int tipoIntruso;
                        isEmptyIntruso = false;
                        do {
                            tipoIntruso = rand.nextInt(6);
                            switch (tipoIntruso) {
                                case 0:
                                    if (sustantivos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(sustantivos.get(rand.nextInt(sustantivos.size())).getPalabra());
                                    }
                                    break;
                                case 1:
                                    if (adjetivos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(adjetivos.get(rand.nextInt(adjetivos.size())).getPalabra());
                                    }
                                    break;
                                case 2:
                                    if (verbos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(verbos.get(rand.nextInt(verbos.size())).getPalabra());
                                    }
                                    break;
                                case 3:
                                    if (determinante.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(determinante.get(rand.nextInt(determinante.size())).getPalabra());
                                    }
                                    break;
                                case 4:
                                    if (pronombres.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(pronombres.get(rand.nextInt(pronombres.size())).getPalabra());
                                    }
                                    break;
                                case 5:
                                    if (preposicion.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(preposicion.get(rand.nextInt(preposicion.size())).getPalabra());
                                    }
                                    break;
                                default:
                                    if (conjuncion.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(conjuncion.get(rand.nextInt(conjuncion.size())).getPalabra());
                                    }
                            }
                        } while (isEmptyIntruso);
                        pre.setOpcion2(adverbios.get(rand.nextInt(adverbios.size())).getPalabra());
                        while (pre.getOpcion2().equals(pre.getOpcion1()))
                            pre.setOpcion2(adverbios.get(rand.nextInt(adverbios.size())).getPalabra());
                        pre.setOpcion3(adverbios.get(rand.nextInt(adverbios.size())).getPalabra());
                        while (pre.getOpcion3().equals(pre.getOpcion1()) || pre.getOpcion3().equals(pre.getOpcion2()))
                            pre.setOpcion3(adverbios.get(rand.nextInt(adverbios.size())).getPalabra());
                        pre.setOpcion4(adverbios.get(rand.nextInt(adverbios.size())).getPalabra());
                        while (pre.getOpcion4().equals(pre.getOpcion1()) || pre.getOpcion4().equals(pre.getOpcion2()) || pre.getOpcion4().equals(pre.getOpcion3()))
                            pre.setOpcion4(adverbios.get(rand.nextInt(adverbios.size())).getPalabra());
                        adverbios.remove(adverbios.size() - 1);
                    }
                    break;
                case 3:
                    if (verbos.size() < 3) {
                        isEmpty = true;
                    } else {
                        isEmpty = false;
                        tv_que_significa.setText("¿Cuál no es un...?");
                        pregunta.setText("Verbo");
                        int tipoIntruso;
                        isEmptyIntruso = false;
                        do {
                            tipoIntruso = rand.nextInt(6);
                            switch (tipoIntruso) {
                                case 0:
                                    if (sustantivos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(sustantivos.get(rand.nextInt(sustantivos.size())).getPalabra());
                                    }
                                    break;
                                case 1:
                                    if (adjetivos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(adjetivos.get(rand.nextInt(adjetivos.size())).getPalabra());
                                    }
                                    break;
                                case 2:
                                    if (adverbios.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(adverbios.get(rand.nextInt(adverbios.size())).getPalabra());
                                    }
                                    break;
                                case 3:
                                    if (determinante.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(determinante.get(rand.nextInt(determinante.size())).getPalabra());
                                    }
                                    break;
                                case 4:
                                    if (pronombres.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(pronombres.get(rand.nextInt(pronombres.size())).getPalabra());
                                    }
                                    break;
                                case 5:
                                    if (preposicion.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(preposicion.get(rand.nextInt(preposicion.size())).getPalabra());
                                    }
                                    break;
                                default:
                                    if (conjuncion.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(conjuncion.get(rand.nextInt(conjuncion.size())).getPalabra());
                                    }
                            }
                        } while (isEmptyIntruso);
                        pre.setOpcion2(verbos.get(rand.nextInt(verbos.size())).getPalabra());
                        while (pre.getOpcion2().equals(pre.getOpcion1()))
                            pre.setOpcion2(verbos.get(rand.nextInt(verbos.size())).getPalabra());
                        pre.setOpcion3(verbos.get(rand.nextInt(verbos.size())).getPalabra());
                        while (pre.getOpcion3().equals(pre.getOpcion1()) || pre.getOpcion3().equals(pre.getOpcion2()))
                            pre.setOpcion3(verbos.get(rand.nextInt(verbos.size())).getPalabra());
                        pre.setOpcion4(verbos.get(rand.nextInt(verbos.size())).getPalabra());
                        while (pre.getOpcion4().equals(pre.getOpcion1()) || pre.getOpcion4().equals(pre.getOpcion2()) || pre.getOpcion4().equals(pre.getOpcion3()))
                            pre.setOpcion4(verbos.get(rand.nextInt(verbos.size())).getPalabra());
                        verbos.remove(verbos.size() - 1);
                    }
                    break;
                case 4:
                    if (pronombres.size() < 3) {
                        isEmpty = true;
                    } else {
                        isEmpty = false;
                        tv_que_significa.setText("¿Cuál no es un...?");
                        pregunta.setText("Pronombre");
                        int tipoIntruso;
                        isEmptyIntruso = false;
                        do {
                            tipoIntruso = rand.nextInt(6);
                            switch (tipoIntruso) {
                                case 0:
                                    if (sustantivos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(sustantivos.get(rand.nextInt(sustantivos.size())).getPalabra());
                                    }
                                    break;
                                case 1:
                                    if (adjetivos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(adjetivos.get(rand.nextInt(adjetivos.size())).getPalabra());
                                    }
                                    break;
                                case 2:
                                    if (adverbios.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(adverbios.get(rand.nextInt(adverbios.size())).getPalabra());
                                    }
                                    break;
                                case 3:
                                    if (verbos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(verbos.get(rand.nextInt(verbos.size())).getPalabra());
                                    }
                                    break;
                                case 4:
                                    if (determinante.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(determinante.get(rand.nextInt(determinante.size())).getPalabra());
                                    }
                                    break;
                                case 5:
                                    if (preposicion.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(preposicion.get(rand.nextInt(preposicion.size())).getPalabra());
                                    }
                                    break;
                                default:
                                    if (conjuncion.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(conjuncion.get(rand.nextInt(conjuncion.size())).getPalabra());
                                    }
                            }
                        } while (isEmptyIntruso);
                        pre.setOpcion2(pronombres.get(rand.nextInt(pronombres.size())).getPalabra());
                        while (pre.getOpcion2().equals(pre.getOpcion1()))
                            pre.setOpcion2(pronombres.get(rand.nextInt(pronombres.size())).getPalabra());
                        pre.setOpcion3(pronombres.get(rand.nextInt(pronombres.size())).getPalabra());
                        while (pre.getOpcion3().equals(pre.getOpcion1()) || pre.getOpcion3().equals(pre.getOpcion2()))
                            pre.setOpcion3(pronombres.get(rand.nextInt(pronombres.size())).getPalabra());
                        pre.setOpcion4(pronombres.get(rand.nextInt(pronombres.size())).getPalabra());
                        while (pre.getOpcion4().equals(pre.getOpcion1()) || pre.getOpcion4().equals(pre.getOpcion2()) || pre.getOpcion4().equals(pre.getOpcion3()))
                            pre.setOpcion4(pronombres.get(rand.nextInt(pronombres.size())).getPalabra());
                        pronombres.remove(pronombres.size() - 1);
                    }
                    break;
                case 5:
                    if (determinante.size() < 3) {
                        isEmpty = true;
                    } else {
                        isEmpty = false;
                        tv_que_significa.setText("¿Cuál no es un...?");
                        pregunta.setText("Determinante");
                        int tipoIntruso;
                        isEmptyIntruso = false;
                        do {
                            tipoIntruso = rand.nextInt(6);
                            switch (tipoIntruso) {
                                case 0:
                                    if (sustantivos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(sustantivos.get(rand.nextInt(sustantivos.size())).getPalabra());
                                    }
                                    break;
                                case 1:
                                    if (adjetivos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(adjetivos.get(rand.nextInt(adjetivos.size())).getPalabra());
                                    }
                                    break;
                                case 2:
                                    if (adverbios.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(adverbios.get(rand.nextInt(adverbios.size())).getPalabra());
                                    }
                                    break;
                                case 3:
                                    if (verbos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(verbos.get(rand.nextInt(verbos.size())).getPalabra());
                                    }
                                    break;
                                case 4:
                                    if (pronombres.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(pronombres.get(rand.nextInt(pronombres.size())).getPalabra());
                                    }
                                    break;
                                case 5:
                                    if (preposicion.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(preposicion.get(rand.nextInt(preposicion.size())).getPalabra());
                                    }
                                    break;
                                default:
                                    if (conjuncion.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(conjuncion.get(rand.nextInt(conjuncion.size())).getPalabra());
                                    }
                            }
                        } while (isEmptyIntruso);
                        pre.setOpcion2(determinante.get(rand.nextInt(determinante.size())).getPalabra());
                        while (pre.getOpcion2().equals(pre.getOpcion1()))
                            pre.setOpcion2(determinante.get(rand.nextInt(determinante.size())).getPalabra());
                        pre.setOpcion3(determinante.get(rand.nextInt(determinante.size())).getPalabra());
                        while (pre.getOpcion3().equals(pre.getOpcion1()) || pre.getOpcion3().equals(pre.getOpcion2()))
                            pre.setOpcion3(determinante.get(rand.nextInt(determinante.size())).getPalabra());
                        pre.setOpcion4(determinante.get(rand.nextInt(determinante.size())).getPalabra());
                        while (pre.getOpcion4().equals(pre.getOpcion1()) || pre.getOpcion4().equals(pre.getOpcion2()) || pre.getOpcion4().equals(pre.getOpcion3()))
                            pre.setOpcion4(determinante.get(rand.nextInt(determinante.size())).getPalabra());
                        determinante.remove(determinante.size() - 1);
                    }
                    break;
                case 6:
                    if (preposicion.size() < 3) {
                        isEmpty = true;
                    } else {
                        isEmpty = false;
                        tv_que_significa.setText("¿Cuál no es una...?");
                        pregunta.setText("Preposición");
                        int tipoIntruso;
                        isEmptyIntruso = false;
                        do {
                            tipoIntruso = rand.nextInt(6);
                            switch (tipoIntruso) {
                                case 0:
                                    if (sustantivos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(sustantivos.get(rand.nextInt(sustantivos.size())).getPalabra());
                                    }
                                    break;
                                case 1:
                                    if (adjetivos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(adjetivos.get(rand.nextInt(adjetivos.size())).getPalabra());
                                    }
                                    break;
                                case 2:
                                    if (adverbios.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(adverbios.get(rand.nextInt(adverbios.size())).getPalabra());
                                    }
                                    break;
                                case 3:
                                    if (verbos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(verbos.get(rand.nextInt(verbos.size())).getPalabra());
                                    }
                                    break;
                                case 4:
                                    if (pronombres.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(pronombres.get(rand.nextInt(pronombres.size())).getPalabra());
                                    }
                                    break;
                                case 5:
                                    if (determinante.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(determinante.get(rand.nextInt(determinante.size())).getPalabra());
                                    }
                                    break;
                                default:
                                    if (conjuncion.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(conjuncion.get(rand.nextInt(conjuncion.size())).getPalabra());
                                    }
                            }
                        } while (isEmptyIntruso);
                        pre.setOpcion2(preposicion.get(rand.nextInt(preposicion.size())).getPalabra());
                        while (pre.getOpcion2().equals(pre.getOpcion1()))
                            pre.setOpcion2(preposicion.get(rand.nextInt(preposicion.size())).getPalabra());
                        pre.setOpcion3(preposicion.get(rand.nextInt(preposicion.size())).getPalabra());
                        while (pre.getOpcion3().equals(pre.getOpcion1()) || pre.getOpcion3().equals(pre.getOpcion2()))
                            pre.setOpcion3(preposicion.get(rand.nextInt(preposicion.size())).getPalabra());
                        pre.setOpcion4(preposicion.get(rand.nextInt(preposicion.size())).getPalabra());
                        while (pre.getOpcion4().equals(pre.getOpcion1()) || pre.getOpcion4().equals(pre.getOpcion2()) || pre.getOpcion4().equals(pre.getOpcion3()))
                            pre.setOpcion4(preposicion.get(rand.nextInt(preposicion.size())).getPalabra());
                        preposicion.remove(preposicion.size() - 1);
                    }
                    break;
                default:
                    if (conjuncion.size() < 3) {
                        isEmpty = true;
                    } else {
                        isEmpty = false;
                        tv_que_significa.setText("¿Cuál no es una...?");
                        pregunta.setText("Conjunción");
                        int tipoIntruso;
                        isEmptyIntruso = false;
                        do {
                            tipoIntruso = rand.nextInt(6);
                            switch (tipoIntruso) {
                                case 0:
                                    if (sustantivos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(sustantivos.get(rand.nextInt(sustantivos.size())).getPalabra());
                                    }
                                    break;
                                case 1:
                                    if (adjetivos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(adjetivos.get(rand.nextInt(adjetivos.size())).getPalabra());
                                    }
                                    break;
                                case 2:
                                    if (adverbios.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(adverbios.get(rand.nextInt(adverbios.size())).getPalabra());
                                    }
                                    break;
                                case 3:
                                    if (verbos.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(verbos.get(rand.nextInt(verbos.size())).getPalabra());
                                    }
                                    break;
                                case 4:
                                    if (pronombres.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(pronombres.get(rand.nextInt(pronombres.size())).getPalabra());
                                    }
                                    break;
                                case 5:
                                    if (determinante.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(determinante.get(rand.nextInt(determinante.size())).getPalabra());
                                    }
                                    break;
                                default:
                                    if (preposicion.size() < 1) isEmptyIntruso = true;
                                    else {
                                        isEmptyIntruso = false;
                                        pre.setOpcion1(preposicion.get(rand.nextInt(preposicion.size())).getPalabra());
                                    }
                            }
                        } while (isEmptyIntruso);
                        pre.setOpcion2(conjuncion.get(rand.nextInt(conjuncion.size())).getPalabra());
                        while (pre.getOpcion2().equals(pre.getOpcion1()))
                            pre.setOpcion2(conjuncion.get(rand.nextInt(conjuncion.size())).getPalabra());
                        pre.setOpcion3(conjuncion.get(rand.nextInt(conjuncion.size())).getPalabra());
                        while (pre.getOpcion3().equals(pre.getOpcion1()) || pre.getOpcion3().equals(pre.getOpcion2()))
                            pre.setOpcion3(conjuncion.get(rand.nextInt(conjuncion.size())).getPalabra());
                        pre.setOpcion4(conjuncion.get(rand.nextInt(conjuncion.size())).getPalabra());
                        while (pre.getOpcion4().equals(pre.getOpcion1()) || pre.getOpcion4().equals(pre.getOpcion2()) || pre.getOpcion4().equals(pre.getOpcion3()))
                            pre.setOpcion4(conjuncion.get(rand.nextInt(conjuncion.size())).getPalabra());
                        conjuncion.remove(conjuncion.size() - 1);
                    }
            }
        } while (isEmpty);
        instanciarPreguntaBotones(pre);
    }

     private void escogePregunta() {
        Random rand = new Random();
        int tipo = rand.nextInt(7);
        int palabra;
        pre = new Pregunta();
        switch (tipo) {
            case 0:
                if (sustantivos.size() < 1) otraPregunta = true;
                else {
                    palabra = rand.nextInt(sustantivos.size());
                    pregunta.setText(sustantivos.get(palabra).getPalabra());
                    pre.setOpcion1(sustantivos.get(palabra).getTraduccion());
                    if (sustantivos.size() < 4) {
                        getPalabrasAleatorias(pre);
                    } else {
                        pre.setOpcion2(sustantivos.get(rand.nextInt(sustantivos.size())).getTraduccion());
                        while (pre.getOpcion2().equals(pre.getOpcion1()))
                            pre.setOpcion2(sustantivos.get(rand.nextInt(sustantivos.size())).getTraduccion());
                        pre.setOpcion3(sustantivos.get(rand.nextInt(sustantivos.size())).getTraduccion());
                        while (pre.getOpcion3().equals(pre.getOpcion1()) || pre.getOpcion3().equals(pre.getOpcion2()))
                            pre.setOpcion3(sustantivos.get(rand.nextInt(sustantivos.size())).getTraduccion());
                        pre.setOpcion4(sustantivos.get(rand.nextInt(sustantivos.size())).getTraduccion());
                        while (pre.getOpcion4().equals(pre.getOpcion1()) || pre.getOpcion4().equals(pre.getOpcion2())
                                || pre.getOpcion4().equals(pre.getOpcion3()))
                            pre.setOpcion4(sustantivos.get(rand.nextInt(sustantivos.size())).getTraduccion());
                    }
                    instanciarPreguntaBotones(pre);
                    palabraPreguntada = sustantivos.get(palabra);
                    sustantivos.remove(palabra);
                    otraPregunta = false;
                }
                break;
            case 1:
                if (adjetivos.size() < 1) otraPregunta = true;
                else {
                    palabra = rand.nextInt(adjetivos.size());
                    pregunta.setText(adjetivos.get(palabra).getPalabra());
                    pre.setOpcion1(adjetivos.get(palabra).getTraduccion());
                    if (adjetivos.size() < 4) {
                        getPalabrasAleatorias(pre);
                    } else {
                        pre.setOpcion2(adjetivos.get(rand.nextInt(adjetivos.size())).getTraduccion());
                        while (pre.getOpcion2().equals(pre.getOpcion1()))
                            pre.setOpcion2(adjetivos.get(rand.nextInt(adjetivos.size())).getTraduccion());
                        pre.setOpcion3(adjetivos.get(rand.nextInt(adjetivos.size())).getTraduccion());
                        while (pre.getOpcion3().equals(pre.getOpcion1()) || pre.getOpcion3().equals(pre.getOpcion2()))
                            pre.setOpcion3(adjetivos.get(rand.nextInt(adjetivos.size())).getTraduccion());
                        pre.setOpcion4(adjetivos.get(rand.nextInt(adjetivos.size())).getTraduccion());
                        while (pre.getOpcion4().equals(pre.getOpcion1()) || pre.getOpcion4().equals(pre.getOpcion2()) || pre.getOpcion4().equals(pre.getOpcion3()))
                            pre.setOpcion4(adjetivos.get(rand.nextInt(adjetivos.size())).getTraduccion());
                    }
                    instanciarPreguntaBotones(pre);
                    palabraPreguntada = adjetivos.get(palabra);
                    adjetivos.remove(palabra);
                    otraPregunta = false;
                }
                break;
            case 2:
                if (adverbios.size() < 1) otraPregunta = true;
                else {
                    palabra = rand.nextInt(adverbios.size());
                    pregunta.setText(adverbios.get(palabra).getPalabra());
                    pre.setOpcion1(adverbios.get(palabra).getTraduccion());
                    if (adverbios.size() < 4) {
                        getPalabrasAleatorias(pre);
                    } else {
                        pre.setOpcion2(adverbios.get(rand.nextInt(adverbios.size())).getTraduccion());
                        while (pre.getOpcion2().equals(pre.getOpcion1()))
                            pre.setOpcion2(adverbios.get(rand.nextInt(adverbios.size())).getTraduccion());
                        pre.setOpcion3(adverbios.get(rand.nextInt(adverbios.size())).getTraduccion());
                        while (pre.getOpcion3().equals(pre.getOpcion1()) || pre.getOpcion3().equals(pre.getOpcion2()))
                            pre.setOpcion3(adverbios.get(rand.nextInt(adverbios.size())).getTraduccion());
                        pre.setOpcion4(adverbios.get(rand.nextInt(adverbios.size())).getTraduccion());
                        while (pre.getOpcion4().equals(pre.getOpcion1()) || pre.getOpcion4().equals(pre.getOpcion2()) || pre.getOpcion4().equals(pre.getOpcion3()))
                            pre.setOpcion4(adverbios.get(rand.nextInt(adverbios.size())).getTraduccion());
                    }
                    instanciarPreguntaBotones(pre);
                    palabraPreguntada = adverbios.get(palabra);
                    adverbios.remove(palabra);
                    otraPregunta = false;
                }
                break;
            case 3:
                if (verbos.size() < 1) otraPregunta = true;
                else {
                    palabra = rand.nextInt(verbos.size());
                    pregunta.setText(verbos.get(palabra).getPalabra());
                    pre.setOpcion1(verbos.get(palabra).getTraduccion());
                    if (verbos.size() < 4) {
                        getPalabrasAleatorias(pre);
                    } else {
                        pre.setOpcion2(verbos.get(rand.nextInt(verbos.size())).getTraduccion());
                        while (pre.getOpcion2().equals(pre.getOpcion1()))
                            pre.setOpcion2(verbos.get(rand.nextInt(verbos.size())).getTraduccion());
                        pre.setOpcion3(verbos.get(rand.nextInt(verbos.size())).getTraduccion());
                        while (pre.getOpcion3().equals(pre.getOpcion1()) || pre.getOpcion3().equals(pre.getOpcion2()))
                            pre.setOpcion3(verbos.get(rand.nextInt(verbos.size())).getTraduccion());
                        pre.setOpcion4(verbos.get(rand.nextInt(verbos.size())).getTraduccion());
                        while (pre.getOpcion4().equals(pre.getOpcion1()) || pre.getOpcion4().equals(pre.getOpcion2()) || pre.getOpcion4().equals(pre.getOpcion3()))
                            pre.setOpcion4(verbos.get(rand.nextInt(verbos.size())).getTraduccion());
                    }
                    instanciarPreguntaBotones(pre);
                    palabraPreguntada = verbos.get(palabra);
                    verbos.remove(palabra);
                    otraPregunta = false;
                }
                break;
            case 4:
                if (pronombres.size() < 1) otraPregunta = true;
                else {
                    palabra = rand.nextInt(pronombres.size());
                    pregunta.setText(pronombres.get(palabra).getPalabra());
                    pre.setOpcion1(pronombres.get(palabra).getTraduccion());
                    if (pronombres.size() < 4) {
                        getPalabrasAleatorias(pre);
                    } else {
                        pre.setOpcion2(pronombres.get(rand.nextInt(pronombres.size())).getTraduccion());
                        while (pre.getOpcion2().equals(pre.getOpcion1()))
                            pre.setOpcion2(pronombres.get(rand.nextInt(pronombres.size())).getTraduccion());
                        pre.setOpcion3(pronombres.get(rand.nextInt(pronombres.size())).getTraduccion());
                        while (pre.getOpcion3().equals(pre.getOpcion1()) || pre.getOpcion3().equals(pre.getOpcion2()))
                            pre.setOpcion3(pronombres.get(rand.nextInt(pronombres.size())).getTraduccion());
                        pre.setOpcion4(pronombres.get(rand.nextInt(pronombres.size())).getTraduccion());
                        while (pre.getOpcion4().equals(pre.getOpcion1()) || pre.getOpcion4().equals(pre.getOpcion2()) || pre.getOpcion4().equals(pre.getOpcion3()))
                            pre.setOpcion4(pronombres.get(rand.nextInt(pronombres.size())).getTraduccion());
                    }
                    instanciarPreguntaBotones(pre);
                    palabraPreguntada = pronombres.get(palabra);
                    pronombres.remove(palabra);
                    otraPregunta = false;
                }
                break;
            case 5:
                if (determinante.size() < 1) otraPregunta = true;
                else {
                    palabra = rand.nextInt(determinante.size());

                    pregunta.setText(determinante.get(palabra).getPalabra());
                    pre.setOpcion1(determinante.get(palabra).getTraduccion());
                    if (determinante.size() < 4) {
                        getPalabrasAleatorias(pre);
                    } else {
                        pre.setOpcion2(determinante.get(rand.nextInt(determinante.size())).getTraduccion());
                        while (pre.getOpcion2().equals(pre.getOpcion1()))
                            pre.setOpcion2(determinante.get(rand.nextInt(determinante.size())).getTraduccion());
                        pre.setOpcion3(determinante.get(rand.nextInt(determinante.size())).getTraduccion());
                        while (pre.getOpcion3().equals(pre.getOpcion1()) || pre.getOpcion3().equals(pre.getOpcion2()))
                            pre.setOpcion3(determinante.get(rand.nextInt(determinante.size())).getTraduccion());
                        pre.setOpcion4(determinante.get(rand.nextInt(determinante.size())).getTraduccion());
                        while (pre.getOpcion4().equals(pre.getOpcion1()) || pre.getOpcion4().equals(pre.getOpcion2()) || pre.getOpcion4().equals(pre.getOpcion3()))
                            pre.setOpcion4(determinante.get(rand.nextInt(determinante.size())).getTraduccion());
                    }
                    instanciarPreguntaBotones(pre);
                    palabraPreguntada = determinante.get(palabra);
                    determinante.remove(palabra);
                    otraPregunta = false;
                }
                break;
            case 6:
                if (preposicion.size() < 1) otraPregunta = true;
                else {
                    palabra = rand.nextInt(preposicion.size());
                    pregunta.setText(preposicion.get(palabra).getPalabra());
                    pre.setOpcion1(preposicion.get(palabra).getTraduccion());
                    if (preposicion.size() < 4) {
                        getPalabrasAleatorias(pre);
                    } else {
                        pre.setOpcion2(preposicion.get(rand.nextInt(preposicion.size())).getTraduccion());
                        while (pre.getOpcion2().equals(pre.getOpcion1()))
                            pre.setOpcion2(preposicion.get(rand.nextInt(preposicion.size())).getTraduccion());
                        pre.setOpcion3(preposicion.get(rand.nextInt(preposicion.size())).getTraduccion());
                        while (pre.getOpcion3().equals(pre.getOpcion1()) || pre.getOpcion3().equals(pre.getOpcion2()))
                            pre.setOpcion3(preposicion.get(rand.nextInt(preposicion.size())).getTraduccion());
                        pre.setOpcion4(preposicion.get(rand.nextInt(preposicion.size())).getTraduccion());
                        while (pre.getOpcion4().equals(pre.getOpcion1()) || pre.getOpcion4().equals(pre.getOpcion2()) || pre.getOpcion4().equals(pre.getOpcion3()))
                            pre.setOpcion4(preposicion.get(rand.nextInt(preposicion.size())).getTraduccion());
                    }
                    instanciarPreguntaBotones(pre);
                    palabraPreguntada = preposicion.get(palabra);
                    preposicion.remove(palabra);
                    otraPregunta = false;
                }
                break;
            default:
                if (conjuncion.size() < 1) otraPregunta = true;
                else {
                    palabra = rand.nextInt(conjuncion.size());
                    pregunta.setText(conjuncion.get(palabra).getPalabra());
                    pre.setOpcion1(conjuncion.get(palabra).getTraduccion());
                    if (conjuncion.size() < 4) {
                        getPalabrasAleatorias(pre);
                    } else {
                        pre.setOpcion2(conjuncion.get(rand.nextInt(conjuncion.size())).getTraduccion());
                        while (pre.getOpcion2().equals(pre.getOpcion1()))
                            pre.setOpcion2(conjuncion.get(rand.nextInt(conjuncion.size())).getTraduccion());
                        pre.setOpcion3(conjuncion.get(rand.nextInt(conjuncion.size())).getTraduccion());
                        while (pre.getOpcion3().equals(pre.getOpcion1()) || pre.getOpcion3().equals(pre.getOpcion2()))
                            pre.setOpcion3(conjuncion.get(rand.nextInt(conjuncion.size())).getTraduccion());
                        pre.setOpcion4(conjuncion.get(rand.nextInt(conjuncion.size())).getTraduccion());
                        while (pre.getOpcion4().equals(pre.getOpcion1()) || pre.getOpcion4().equals(pre.getOpcion2()) || pre.getOpcion4().equals(pre.getOpcion3()))
                            pre.setOpcion4(conjuncion.get(rand.nextInt(conjuncion.size())).getTraduccion());
                    }
                    instanciarPreguntaBotones(pre);
                    palabraPreguntada = conjuncion.get(palabra);
                    conjuncion.remove(palabra);
                    otraPregunta = false;
                }
                break;
        }
        if (otraPregunta) escogePregunta();
    }

    private void aTestFinal() {
        Intent i = new Intent(this, TestFinal.class);
        //if (juego.equalsIgnoreCase("test")) {
            i.putExtra("palabras_total", palabrasTotal.size());
            i.putExtra("palabras_acertadas", contador);
            i.putExtra("bundle", bundle);
            i.putExtra("palabrasJugadas", palabrasJugadas);
            i.putExtra("palabrasAcertadas", palabrasAcertadas);
            i.putExtra("vidasSobrantes", c_vidas);
            i.putExtra("juego", juego);
        //}
        startActivity(i);
        finish();

    }

    private boolean validarSiguientePreguntaIntruso() {
        boolean valido = false;
        if (sustantivos.size() >= 3) valido = true;
        else if (adjetivos.size() >= 3) valido = true;
        else if (adverbios.size() >= 3) valido = true;
        else if (verbos.size() >= 3) valido = true;
        else if (preposicion.size() >= 3) valido = true;
        else if (pronombres.size() >= 3) valido = true;
        else if (conjuncion.size() >= 3) valido = true;
        else if (determinante.size() >= 3) valido = true;
        return valido;
    }

    private void getPalabrasAleatorias(Pregunta pre) {
        Random rand = new Random();
        pre.setOpcion2(palabrasTotal.get(rand.nextInt(palabrasTotal.size())).getTraduccion());
        while (pre.getOpcion2().equals(pre.getOpcion1()))
            pre.setOpcion2(palabrasTotal.get(rand.nextInt(palabrasTotal.size())).getTraduccion());
        pre.setOpcion3(palabrasTotal.get(rand.nextInt(palabrasTotal.size())).getTraduccion());
        while (pre.getOpcion3().equals(pre.getOpcion1()) || pre.getOpcion3().equals(pre.getOpcion2()))
            pre.setOpcion3(palabrasTotal.get(rand.nextInt(palabrasTotal.size())).getTraduccion());
        pre.setOpcion4(palabrasTotal.get(rand.nextInt(palabrasTotal.size())).getTraduccion());
        while (pre.getOpcion4().equals(pre.getOpcion1()) || pre.getOpcion4().equals(pre.getOpcion2()) || pre.getOpcion4().equals(pre.getOpcion3()))
            pre.setOpcion4(palabrasTotal.get(rand.nextInt(palabrasTotal.size())).getTraduccion());
    }

    private void instanciarPreguntaBotones(Pregunta pre) {
        Random rand = new Random();
        int sol = rand.nextInt(4);
        if (sol == 0) {
            mb1.setText(pre.getOpcion1());
            mb2.setText(pre.getOpcion2());
            mb3.setText(pre.getOpcion3());
            mb4.setText(pre.getOpcion4());
        }
        else if (sol == 1) {
            mb1.setText(pre.getOpcion2());
            mb2.setText(pre.getOpcion1());
            mb3.setText(pre.getOpcion3());
            mb4.setText(pre.getOpcion4());
        }
        else if (sol == 2) {
            mb1.setText(pre.getOpcion2());
            mb2.setText(pre.getOpcion3());
            mb3.setText(pre.getOpcion1());
            mb4.setText(pre.getOpcion4());
        }
        else if (sol == 3) {
            mb1.setText(pre.getOpcion2());
            mb2.setText(pre.getOpcion3());
            mb3.setText(pre.getOpcion4());
            mb4.setText(pre.getOpcion1());
        }
    }

     private void getPalabras() {
         DbHelper dbHelper = new DbHelper(JuegoTest.this);
         SQLiteDatabase db = dbHelper.getReadableDatabase();
         if (db == null) {
             Toast.makeText(JuegoTest.this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
         } else {
             ArrayList<Long> cols = (ArrayList<Long>) bundle.getSerializable("colecciones");
             String coleccionesToAdd = "(";
             for (int i = 0; i < cols.size() - 1; i++) {
                 coleccionesToAdd += cols.get(i) + ",";
             }
             coleccionesToAdd += cols.get(cols.size()-1) + ")";
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
                 if (p.getTipo().equals(getResources().getString(R.string.sustantivo))) sustantivos.add(p);
                 else if (p.getTipo().equals(getResources().getString(R.string.sustantivo))) sustantivos.add(p);
                 else if (p.getTipo().equals(getResources().getString(R.string.adjetivo))) adjetivos.add(p);
                 else if (p.getTipo().equals(getResources().getString(R.string.adverbio))) adverbios.add(p);
                 else if (p.getTipo().equals(getResources().getString(R.string.verbo))) verbos.add(p);
                 else if (p.getTipo().equals(getResources().getString(R.string.pronombre))) pronombres.add(p);
                 else if (p.getTipo().equals(getResources().getString(R.string.determinante))) determinante.add(p);
                 else if (p.getTipo().equals(getResources().getString(R.string.preposicion))) preposicion.add(p);
                 else if (p.getTipo().equals(getResources().getString(R.string.conjuncion))) conjuncion.add(p);
                 palabrasTotal.add(p);
                 } while (c.moveToNext());
                 db.close();
             }
         }
     }
}
