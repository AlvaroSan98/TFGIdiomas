package com.example.tfgidiomas;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfgidiomas.db.DbHelper;
import com.example.tfgidiomas.db.Utilidades;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Registro extends AppCompatActivity {

    EditText et_nombre_usuario;
    EditText et_contraseña;
    EditText et_contraseña2;
    EditText et_idioma;

    private Long idioma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_registro);

        et_nombre_usuario = findViewById(R.id.et_registro_usuario);
        et_contraseña = findViewById(R.id.et_registro_pass);
        et_contraseña2 = findViewById(R.id.et_registro_pass2);
        et_idioma = findViewById(R.id.et_registro_idioma);

    }

    public void aPantallaPrincipal(View view) {
        String usuario = et_nombre_usuario.getText().toString();
        String pass1 = et_contraseña.getText().toString();
        String pass2 = et_contraseña2.getText().toString();
        String idioma = et_idioma.getText().toString();

        if (comprobarCampos(usuario, pass1, pass2, idioma)) {
            if (comprobarNombreUsuario(usuario)){
                String pass_hashed = get_SHA_512_SecurePassword(pass1, "x7x89s");
                Usuario user = new Usuario(usuario, pass_hashed);
                Idioma lan = new Idioma(idioma, 0, usuario);
                if (registrarUsuario(user, lan)) {
                    Intent i = new Intent(this, PantallaPrincipal.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("usuario", usuario);
                    bundle.putLong("idioma", this.idioma);
                    i.putExtra("bundle", bundle);
                    startActivity(i);
                    finish();
                }
            }
        }
    }

    private boolean comprobarNombreUsuario(String usuario) {
        DbHelper dbHelper = new DbHelper(Registro.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db == null) {
            Toast.makeText(this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            String[] camposDevueltos = new String[]{Utilidades.USUARIOS_NOMBRE};
            String select = Utilidades.USUARIOS_NOMBRE + " = ?";
            String[] selectionArgs = new String[]{usuario};
            SQLiteCursor c = (SQLiteCursor) db.query(
                    Utilidades.TABLA_USUARIOS,
                    camposDevueltos,
                    select,
                    selectionArgs,
                    null,
                    null,
                    null
            );
            if (c.getCount() != 0) {
                Toast.makeText(this, "Ya existe un usuario con ese nombre", Toast.LENGTH_LONG).show();
                return false;
            }
            else return true;
        }
    }

    private boolean comprobarCampos(String usuario, String pass1, String pass2, String idioma) {
        if (usuario.trim().isEmpty()) {
            Toast.makeText(this, "Necesitas dar un nombre al usuario", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (pass1.trim().isEmpty()) {
            Toast.makeText(this, "Necesitar dar una contraseña al usuario", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (!pass1.equals(pass2)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (idioma.trim().isEmpty()) {
            Toast.makeText(this, "Necesitas estudiar al menos un idioma", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private boolean registrarUsuario(Usuario user, Idioma lan) {
        DbHelper dbHelper = new DbHelper(Registro.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db == null) {
            Toast.makeText(this, "Error al conectar con la bbdd", Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            long idResultante = db.insert(Utilidades.TABLA_USUARIOS, Utilidades.USUARIOS_NOMBRE,
                    user.toContentValues());
            if (idResultante == -1) {
                Toast.makeText(this, "Error al insertar el usuario en la bbdd", Toast.LENGTH_LONG).show();
                db.close();
                return false;
            }
            idResultante = db.insert(Utilidades.TABLA_IDIOMAS, Utilidades.IDIOMAS_NOMBRE, lan.toContentValues());
            if (idResultante == -1) {
                Toast.makeText(this, "Error al insertar el idioma en la bbdd", Toast.LENGTH_LONG).show();
                db.close();
                return false;
            }
            else {
                this.idioma = idResultante;
                db.close();
                return true;
            }
        }
    }

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
