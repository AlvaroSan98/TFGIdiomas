package com.example.tfgidiomas;

import android.content.ContentValues;

import com.example.tfgidiomas.db.Utilidades;

public class Usuario {

    private String _usuario;
    private String _password;

    Usuario(String usuario, String password) {
        _usuario = usuario;
        _password = password;
    }

    public String get_usuario() {
        return _usuario;
    }

    public void set_usuario(String _usuario) {
        this._usuario = _usuario;
    }

    public String get_password() {
        return _password;
    }

    public void set_password(String _password) {
        this._password = _password;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(Utilidades.USUARIOS_NOMBRE, _usuario);
        cv.put(Utilidades.USUARIOS_PASSWORD, _password);
        return cv;
    }

}
