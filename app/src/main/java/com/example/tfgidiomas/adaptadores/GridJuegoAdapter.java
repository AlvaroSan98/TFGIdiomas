package com.example.tfgidiomas.adaptadores;

import android.content.Context;
import android.os.Build;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.tfgidiomas.Coleccion;
import com.example.tfgidiomas.R;
import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

public class GridJuegoAdapter extends BaseAdapter {

    Context context;
    Coleccion[] colecciones;
    Boolean[] escogidos;

    TextView tv;

    LayoutInflater inflater;

    public GridJuegoAdapter(Context c, Coleccion[] colecciones, Boolean[] escogidos, TextView tv) {
        this.context = c;
        this.colecciones = colecciones;
        this.escogidos = escogidos;
        this.tv = tv;
    }

    @Override
    public int getCount() {
        return colecciones.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.boton_colecciones_juego, null);
        }

        MaterialButton mb = convertView.findViewById(R.id.btn_colecciones_juego);

        mb.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (escogidos[position] == false) {
                    escogidos[position] = true;
                    String[] strings = new String[3];
                    strings = tv.getText().toString().split(" ");
                    int palabrasTotal = Integer.parseInt(strings[0]) + colecciones[position].getnPalabras();
                    tv.setText("" + palabrasTotal + " palabras añadidas");
                    mb.setBackgroundColor(context.getColor(R.color.color_secondary));
                }
                else {
                    escogidos[position] = false;
                    String[] strings = new String[3];
                    strings = tv.getText().toString().split(" ");
                    int palabrasTotal = Integer.parseInt(strings[0]) - colecciones[position].getnPalabras();
                    tv.setText("" + palabrasTotal + " palabras añadidas");
                    mb.setBackgroundColor(context.getColor(R.color.color_primary));
                }
            }
        });
        mb.setText(colecciones[position].getNombre());

        return convertView;
    }
}
