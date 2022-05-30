package com.example.tfgidiomas.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String NOMBRE_DB = "aplicacion.db";

    public DbHelper(@Nullable Context context) {
        super(context, NOMBRE_DB, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(Utilidades.CREAR_TABLA_USUARIOS);
        db.execSQL(Utilidades.CREAR_TABLA_IDIOMAS);
        db.execSQL(Utilidades.CREAR_TABLA_COLECCIONES);
        db.execSQL(Utilidades.CREAR_TABLA_PALABRAS);
        db.execSQL(Utilidades.CREAR_TABLA_PARTIDAS);
        db.execSQL(Utilidades.CREAR_TABLA_PARTIDA_USUARIO);
        db.execSQL(Utilidades.CREAR_TABLA_WORDLE_INTENTOS);
        db.execSQL(Utilidades.CREAR_TABLA_WORDLE_SOLUCION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(Utilidades.DROP_TABLA_USUARIOS);
        db.execSQL(Utilidades.DROP_TABLA_IDIOMAS);
        db.execSQL(Utilidades.DROP_TABLA_COLECCIONES);
        db.execSQL(Utilidades.DROP_TABLA_PALABRAS);
        db.execSQL(Utilidades.DROP_TABLA_PARTIDAS);
        db.execSQL(Utilidades.DROP_TABLA_PARTIDA_USUARIO);
        db.execSQL(Utilidades.DROP_TABLA_WORDLE_INTENTOS);
        db.execSQL(Utilidades.DROP_TABLA_WORDLE_SOLUCION);
        onCreate(db);
    }
}
