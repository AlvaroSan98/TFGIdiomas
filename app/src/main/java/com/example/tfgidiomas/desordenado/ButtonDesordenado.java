package com.example.tfgidiomas.desordenado;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tfgidiomas.R;

import java.util.ArrayList;

public class ButtonDesordenado extends androidx.appcompat.widget.AppCompatButton implements DesordenadoObservable{

    private Context context;
    private boolean pulsado;
    private String tipo;
    private char letra;
    private ArrayList<ButtonDesordenado> answer;
    private ButtonDesordenado adjunto;

    private ArrayList<DesordenadoObservador> observadores;

    public void enalzarObservador(DesordenadoObservador o) {observadores.add(o); }

    public void setAdjunto(ButtonDesordenado adjunto) {
        this.adjunto = adjunto;
    }

    public char getLetra() {
        return letra;
    }

    public void setLetra(char letra) {
        this.letra = letra;
        this.setText("" + letra);
    }

    public boolean isPulsado() {
        return pulsado;
    }

    public void setPulsado(boolean pulsado) {
        this.pulsado = pulsado;
    }

    public ButtonDesordenado(@NonNull Context context, String tipo, String sol, ArrayList<ButtonDesordenado> lista) {
        super(context);
        this.context = context;
        pulsado = false;
        observadores = new ArrayList<DesordenadoObservador>();
        this.tipo = tipo;
        this.answer = lista;
        setTextSize(24);

        if (this.tipo.equals("question")) {
            this.setBackground(context.getDrawable(R.drawable.btn_desordenado_question));
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int gap = 100;
                    for (int i = 0; i < answer.size(); i++) {
                        if (!answer.get(i).isPulsado()) {
                            gap = i;
                            break;
                        }
                    }
                    if (gap == 100) {
                        Toast.makeText(context, "Error: los botones están llenos", Toast.LENGTH_LONG).show();
                    }
                    else {
                        answer.get(gap).setText("" + letra);
                        answer.get(gap).setAdjunto(ButtonDesordenado.this);
                        adjunto = answer.get(gap);
                        answer.get(gap).setPulsado(true);
                        answer.get(gap).setLetra(letra);
                        setVisibility(INVISIBLE);
                        notificar();
                    }
                }
            });
        } else {
            this.setBackground(context.getDrawable(R.drawable.btn_desordenado_answer_vacio));
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPulsado(false);
                    adjunto.setVisibility(VISIBLE);
                    setLetra(' ');
                    setText(" ");
                }
            });
        }
    }

    public void marcarError() {
        if (letra != ' ') {
            setBackground(context.getDrawable(R.drawable.btn_desordenado_question_error));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setBackground(context.getDrawable(R.drawable.btn_desordenado_answer_vacio));
                    setLetra(' ');
                    setText(" ");
                    adjunto.setVisibility(VISIBLE);
                }
            }, 500);
            setPulsado(false);
        }
    }

    public void marcarCorrecta() {
        if (letra != ' ') {
            setBackground(context.getDrawable(R.drawable.btn_desordenado_answer_completed));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setBackground(context.getDrawable(R.drawable.btn_desordenado_answer_vacio));
                    setLetra(' ');
                    setText(" ");
                    adjunto.setVisibility(VISIBLE);
                }
            }, 500);
            setPulsado(false);
        }
    }

    @Override
    public void notificar() {
        for (int i = 0; i < observadores.size(); i++) {
            observadores.get(i).update();
        }
    }
}
