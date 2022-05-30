package com.example.tfgidiomas.db;

public class Utilidades {


    //TABLA USUARIOS
    public static final String CREAR_TABLA_USUARIOS = "CREATE TABLE " + Utilidades.TABLA_USUARIOS + "(" +
            Utilidades.USUARIOS_NOMBRE + " VARCHAR(30) NOT NULL, " +
            Utilidades.USUARIOS_PASSWORD + " VARCHAR(50) NOT NULL)";

    public static final String DROP_TABLA_USUARIOS = "DROP TABLE IF EXISTS " + Utilidades.TABLA_USUARIOS;

    public static final String TABLA_USUARIOS = "usuarios";
    public static final String USUARIOS_NOMBRE = "nombre";
    public static final String USUARIOS_PASSWORD = "password"; //TODO: HACER UN HASH DE LA CONTRASEÑA Y GUARDARLO
    
    //TABLA IDIOMAS
    public static final String CREAR_TABLA_IDIOMAS = "CREATE TABLE " + Utilidades.TABLA_IDIOMAS + "(" +
            Utilidades.IDIOMAS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Utilidades.IDIOMAS_NOMBRE + " VARCHAR(30) NOT NULL, " +
            Utilidades.IDIOMAS_USUARIO + " VARCHAR(30) NOT NULL)";

    public static final String DROP_TABLA_IDIOMAS = "DROP TABLE IF EXISTS " + Utilidades.TABLA_IDIOMAS;

    public static final String TABLA_IDIOMAS = "idiomas";
    public static final String IDIOMAS_ID = "id";
    public static final String IDIOMAS_NOMBRE = "nombre";
    public static final String IDIOMAS_USUARIO = "usuario";


    //TABLA COLECCIONES
    public static final String CREAR_TABLA_COLECCIONES = "CREATE TABLE " + Utilidades.TABLA_COLECCIONES + "(" +
            Utilidades.COLECCIONES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Utilidades.COLECCIONES_NOMBRE + " VARCHAR(30) NOT NULL, " +
            Utilidades.COLECCIONES_IDIOMA + " INTEGER NOT NULL)";

    public static final String DROP_TABLA_COLECCIONES = "DROP TABLE IF EXISTS " + Utilidades.TABLA_COLECCIONES;

    public static final String TABLA_COLECCIONES = "colecciones";
    public static final String COLECCIONES_ID = "id";
    public static final String COLECCIONES_NOMBRE = "nombre";
    public static final String COLECCIONES_IDIOMA = "idioma";


    //TABLA PALABRAS
    public static final String CREAR_TABLA_PALABRAS = "CREATE TABLE " + Utilidades.TABLA_PALABRAS + "(" +
            Utilidades.PALABRAS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Utilidades.PALABRAS_NOMBRE + " VARCHAR(30) NOT NULL, " +
            Utilidades.PALABRAS_TRADUCCION + " VARCHAR(30) NOT NULL, " +
            Utilidades.PALABRAS_COLECCION + " INTEGER NOT NULL, " +
            Utilidades.PALABRAS_TIPO + " TEXT NOT NULL)";

    public static final String DROP_TABLA_PALABRAS = "DROP TABLE IF EXISTS " + Utilidades.TABLA_PALABRAS;

    public static final String TABLA_PALABRAS = "palabras";
    public static final String PALABRAS_ID = "id";
    public static final String PALABRAS_NOMBRE = "nombre";
    public static final String PALABRAS_TRADUCCION = "traduccion";
    public static final String PALABRAS_TIPO = "tipo";
    public static final String PALABRAS_COLECCION = "coleccion";


    //TABLA PARTIDAS
    public static final String CREAR_TABLA_PARTIDAS = "CREATE TABLE " + Utilidades.TABLA_PARTIDAS + "(" +
            Utilidades.PARTIDAS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Utilidades.PARTIDAS_FECHA + " VARCHAR(30) NOT NULL, " +
            Utilidades.PARTIDAS_PUNTOS + " VARCHAR(30) NOT NULL, " +
            Utilidades.PARTIDAS_JUEGO + " VARCHAR(30) NOT NULL)";

    public static final String DROP_TABLA_PARTIDAS = "DROP TABLE IF EXISTS " + Utilidades.TABLA_PARTIDAS;

    public static final String TABLA_PARTIDAS = "partidas";
    public static final String PARTIDAS_ID = "id";
    public static final String PARTIDAS_FECHA = "fecha";
    public static final String PARTIDAS_PUNTOS = "puntos";
    public static final String PARTIDAS_JUEGO = "juego";


    //TABLA PARTIDAS-PALABRAS
    public static final String CREAR_TABLA_PARTIDA_PALABRA = "CREATE TABLE " + Utilidades.TABLA_PARTIDA_PALABRA + "(" +
            Utilidades.PARTIDA_PALABRA_PALABRA + " INTEGER, " +
            Utilidades.PARTIDA_PALABRA_PARTIDA + " INTEGER, " +
            "PRIMARY KEY (" + Utilidades.PARTIDA_PALABRA_PALABRA + ", " + Utilidades.PARTIDA_PALABRA_PARTIDA + "))";

    public static final String DROP_TABLA_PARTIDA_PALABRA = "DROP TABLE IF EXISTS " + Utilidades.TABLA_PARTIDA_PALABRA;

    public static final String TABLA_PARTIDA_PALABRA = "partida_palabra";
    public static final String PARTIDA_PALABRA_PALABRA = "palabra";
    public static final String PARTIDA_PALABRA_PARTIDA = "partida";

    //TABLA PARTIDAS-USUARIO
    public static final String CREAR_TABLA_PARTIDA_USUARIO = "CREATE TABLE " + Utilidades.TABLA_PARTIDA_USUARIO + "(" +
            Utilidades.PARTIDA_USUARIO_USUARIO + " VARCHAR(30) NOT NULL, " +
            Utilidades.PARTIDA_USUARIO_PARTIDA + " INTEGER, " +
            "PRIMARY KEY (" + Utilidades.PARTIDA_USUARIO_USUARIO + ", " + Utilidades.PARTIDA_USUARIO_PARTIDA + "))";

    public static final String DROP_TABLA_PARTIDA_USUARIO = "DROP TABLE IF EXISTS " + Utilidades.TABLA_PARTIDA_USUARIO;

    public static final String TABLA_PARTIDA_USUARIO = "partida_usuario";
    public static final String PARTIDA_USUARIO_USUARIO = "usuario";
    public static final String PARTIDA_USUARIO_PARTIDA = "partida";


    //TABLA WORDLE INTENTOS
    public static final String CREAR_TABLA_WORDLE_INTENTOS = "CREATE TABLE " + Utilidades.TABLA_WORDLE_INTENTOS + "(" +
            Utilidades.WORDLE_ID + " INTEGER, " +
            Utilidades.WORDLE_INTENTO + " VARCHAR(30))";

    public static final String DROP_TABLA_WORDLE_INTENTOS = "DROP TABLE IF EXISTS " + Utilidades.TABLA_WORDLE_INTENTOS;

    public static final String TABLA_WORDLE_INTENTOS = "wordle_intentos";
    public static final String WORDLE_INTENTO= "intento";
    public static final String WORDLE_ID= "id_wordle";

    //TABLA WORDLE SOLUCION
    public static final String CREAR_TABLA_WORDLE_SOLUCION = "CREATE TABLE " + Utilidades.TABLA_WORDLE_SOLUCION + "(" +
            Utilidades.WORDLE_ID + " INTEGER, " +
            Utilidades.WORDLE_SOLUCION + " VARCHAR(30))";

    public static final String DROP_TABLA_WORDLE_SOLUCION = "DROP TABLE IF EXISTS " + Utilidades.TABLA_WORDLE_SOLUCION;

    public static final String TABLA_WORDLE_SOLUCION = "wordle_solucion";
    public static final String WORDLE_SOLUCION= "solucion";
    public static final String WORDLE_SOL_ID= "id_wodle";

}
