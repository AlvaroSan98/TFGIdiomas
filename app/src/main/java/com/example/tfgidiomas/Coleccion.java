package com.example.tfgidiomas;

import android.content.ContentValues;

import com.example.tfgidiomas.db.Utilidades;

public class Coleccion {

    private String nombre;
    private Long idioma;
    private int nPalabras;
    private Long id;

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    private boolean pressed;

    public Coleccion() { }

    public int getnPalabras() {
        return nPalabras;
    }

    public void setnPalabras(int nPalabras) {
        this.nPalabras = nPalabras;
    }

    public Coleccion(String nombre, Long idioma, Long id) {
        this.nombre = nombre;
        this.idioma = idioma;
        this.id = id;
        pressed = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Coleccion(String nombre, Long idioma) {
        this.nombre = nombre;
        this.idioma = idioma;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getIdioma() {
        return idioma;
    }

    public void setIdioma(Long idioma) {
        this.idioma = idioma;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(Utilidades.COLECCIONES_NOMBRE, nombre);
        cv.put(Utilidades.COLECCIONES_IDIOMA, idioma);
        return cv;
    }
}
