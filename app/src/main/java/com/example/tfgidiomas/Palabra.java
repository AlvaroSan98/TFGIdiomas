package com.example.tfgidiomas;

import android.content.ContentValues;

import com.example.tfgidiomas.db.Utilidades;

import java.io.Serializable;

public class Palabra implements Serializable {

    private Long id;
    private String palabra;
    private String traduccion;
    private String tipo;
    private Long coleccion;

    public Palabra(Long id, String palabra, String traduccion, String tipo) {
        this.id = id;
        this.palabra = palabra;
        this.traduccion = traduccion;
        this.tipo = tipo;
    }

    public Palabra(String palabra, String traduccion, String tipo, Long coleccion) {
        this.palabra = palabra;
        this.traduccion = traduccion;
        this.tipo = tipo;
        this.coleccion = coleccion;
    }

    public Palabra() { }

    public Long getColeccion() {
        return coleccion;
    }

    public void setColeccion(Long coleccion) {
        this.coleccion = coleccion;
    }

    public Palabra(String palabra, String traduccion, String tipo, Long id, Long coleccion) {
        this.id = id;
        this.palabra = palabra;
        this.traduccion = traduccion;
        this.tipo = tipo;
        this.coleccion = coleccion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    public String getTraduccion() {
        return traduccion;
    }

    public void setTraduccion(String traduccion) {
        this.traduccion = traduccion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(Utilidades.PALABRAS_NOMBRE, palabra);
        cv.put(Utilidades.PALABRAS_TRADUCCION, traduccion);
        cv.put(Utilidades.PALABRAS_TIPO, tipo);
        cv.put(Utilidades.PALABRAS_COLECCION, coleccion);
        return cv;
    }
}
