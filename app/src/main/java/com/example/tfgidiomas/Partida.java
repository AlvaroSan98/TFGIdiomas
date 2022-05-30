package com.example.tfgidiomas;

import android.content.ContentValues;

import com.example.tfgidiomas.db.Utilidades;

import java.util.ArrayList;

public class Partida {

    private Long id;
    private String fecha;
    private int puntos;
    private String juego;
    private ArrayList<Palabra> palabras;

    public String getJuego() {
        return juego;
    }

    public void setJuego(String juego) {
        this.juego = juego;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public ArrayList<Palabra> getPalabras() {
        return palabras;
    }

    public void setPalabras(ArrayList<Palabra> palabras) {
        this.palabras = palabras;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(Utilidades.PARTIDAS_FECHA, fecha);
        cv.put(Utilidades.PARTIDAS_PUNTOS, puntos);
        cv.put(Utilidades.PARTIDAS_JUEGO, juego);
        return cv;
    }

    public Partida(Long id, String fecha, int puntos, String juego, ArrayList<Palabra> palabras) {
        this.id = id;
        this.fecha = fecha;
        this.puntos = puntos;
        this.palabras = palabras;
        this.juego = juego;
    }
}
