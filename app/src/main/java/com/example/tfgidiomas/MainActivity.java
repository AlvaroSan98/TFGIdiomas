package com.example.tfgidiomas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tfgidiomas.db.DbHelper;
import com.example.tfgidiomas.db.Utilidades;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    TextInputEditText nombre_usuario;
    TextInputEditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db == null) dbHelper.onCreate(db);

        nombre_usuario = findViewById(R.id.et_login_usuario);
        password = findViewById(R.id.et_login_pass);
    }

    public void aEleccionIdioma (View view) {
        String usuario = nombre_usuario.getText().toString();
        String pass = password.getText().toString();
        String pass_hashed = get_SHA_512_SecurePassword(pass, "x7x89s");
        if (usuarioEncontrado(usuario, pass_hashed)) {
            //Toast.makeText(this, "Usuario " + usuario + " logeado correctamente", Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, ElegirIdioma.class);
            i.putExtra("usuario", usuario);
            startActivity(i);
            finish();
        }
    }

    public void aRegistro (View view) {
        Intent i = new Intent(this, Registro.class);
        startActivity(i);
        finish();
    }

    private boolean usuarioEncontrado(String usuario, String pass) {
        DbHelper dbHelper = new DbHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            Toast.makeText(MainActivity.this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            String[] camposDevueltos = new String[]{Utilidades.USUARIOS_NOMBRE, Utilidades.USUARIOS_PASSWORD};
            String select = Utilidades.USUARIOS_NOMBRE + " = ?";
            String[] selectionArgs = new String[]{usuario};
            Cursor c = db.query(
                    Utilidades.TABLA_USUARIOS,
                    camposDevueltos,
                    select,
                    selectionArgs,
                    null,
                    null,
                    null
                    );
            if (!c.moveToFirst()) {
                Toast.makeText(this, "No se ha encontrado ningún usuario con ese nombre", Toast.LENGTH_LONG).show();
                db.close();
                return false;
            }
            else {
                String usuarioDevuelto = c.getString(c.getColumnIndex(Utilidades.USUARIOS_NOMBRE));
                String passDevuelta = c.getString(c.getColumnIndex(Utilidades.USUARIOS_PASSWORD));
                if (usuarioDevuelto.equals(usuario) && passDevuelta.equals(pass)) {
                    db.close();
                    return true;
                }
                else {
                    Toast.makeText(this, "La contraseña no coincide con la del usuario", Toast.LENGTH_LONG).show();
                    db.close();
                    return false;
                }
            }
        }
    }

    /*public void borrarDb (View view) {
        DbHelper dbHelper = new DbHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db == null) {
            Toast.makeText(this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
        }
        else {
            int rowsDevueltas = db.delete(
                    Utilidades.TABLA_USUARIOS,
                    "1",
                    null
            );
            Toast.makeText(this, "Se han eliminado " + rowsDevueltas + " usuarios", Toast.LENGTH_SHORT).show();
            rowsDevueltas = db.delete(
                    Utilidades.TABLA_IDIOMAS,
                    "1",
                    null
            );
            Toast.makeText(this, "Se han eliminado " + rowsDevueltas + " idiomas", Toast.LENGTH_SHORT).show();
            rowsDevueltas = db.delete(
                    Utilidades.TABLA_COLECCIONES,
                    "1",
                    null
            );
            Toast.makeText(this, "Se han eliminado " + rowsDevueltas + " palabras_colecciones", Toast.LENGTH_SHORT).show();
            rowsDevueltas = db.delete(
                    Utilidades.TABLA_PALABRAS,
                    "1",
                    null
            );
            Toast.makeText(this, "Se han eliminado " + rowsDevueltas + " palabras", Toast.LENGTH_SHORT).show();
            rowsDevueltas = db.delete(
                    Utilidades.TABLA_PARTIDAS,
                    "1",
                    null
            );
            Toast.makeText(this, "Se han eliminado " + rowsDevueltas + " pàrtidas", Toast.LENGTH_SHORT).show();
            rowsDevueltas = db.delete(
                    Utilidades.TABLA_PARTIDA_PALABRA,
                    "1",
                    null
            );
            Toast.makeText(this, "Se han eliminado " + rowsDevueltas + " partida-palabra", Toast.LENGTH_SHORT).show();
            rowsDevueltas = db.delete(
                    Utilidades.TABLA_PARTIDA_USUARIO,
                    "1",
                    null
            );
            Toast.makeText(this, "Se han eliminado " + rowsDevueltas + " partida-usuario", Toast.LENGTH_SHORT).show();
            db.close();
        }
    }*/

    public String get_SHA_512_SecurePassword(String passwordToHash, String salt){

        String generatedPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes("UTF-8"));
            byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
}