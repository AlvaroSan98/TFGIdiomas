package com.example.tfgidiomas;

import android.content.ContentValues;

import com.example.tfgidiomas.db.Utilidades;

public class Idioma {

    private String _idioma;
    private int _nPalabras;
    private String _usuario;
    private Long _id;

    Idioma(String idioma, int nPalabras, String usuario, Long id) {
        _idioma = idioma;
        _nPalabras = nPalabras;
        _usuario = usuario;
        _id = id;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    Idioma(String idioma, int nPalabras, String usuario) {
        _idioma = idioma;
        _nPalabras = nPalabras;
        _usuario = usuario;
    }

    public String get_idioma() {
        return _idioma;
    }

    public void set_idioma(String _idioma) {
        this._idioma = _idioma;
    }

    public String get_usuario() {
        return _usuario;
    }

    public void set_usuario(String _usuario) {
        this._usuario = _usuario;
    }

    public int get_nPalabras() {
        return _nPalabras;
    }

    public void set_nPalabras(int _nPalabras) {
        this._nPalabras = _nPalabras;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(Utilidades.IDIOMAS_NOMBRE, _idioma);
        cv.put(Utilidades.IDIOMAS_USUARIO, _usuario);
        return cv;
    }

}
