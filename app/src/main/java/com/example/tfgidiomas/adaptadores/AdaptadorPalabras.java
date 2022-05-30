package com.example.tfgidiomas.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfgidiomas.AddPalabra;
import com.example.tfgidiomas.InsideColeccion;
import com.example.tfgidiomas.Palabra;
import com.example.tfgidiomas.R;

import java.util.ArrayList;

public class AdaptadorPalabras extends RecyclerView.Adapter<AdaptadorPalabras.ViewHolderPalabras> {

    private ArrayList<Palabra> palabras;
    private Context context;

    private Bundle bundle;

    public AdaptadorPalabras(ArrayList<Palabra> palabras, Context context, Bundle bundle) {
        this.palabras = palabras;
        this.context = context;
        this.bundle = bundle;
    }

    @NonNull
    @Override
    public ViewHolderPalabras onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carta_palabra, null, false);
        return new ViewHolderPalabras(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPalabras holder, int position) {
        holder.asignarDatos(palabras.get(position));
    }

    @Override
    public int getItemCount() {
        return palabras.size();
    }

    public class ViewHolderPalabras extends RecyclerView.ViewHolder {

        TextView tv_palabra;
        TextView tv_traduccion;
        TextView tv_tipo;

        ImageButton editar;
        ImageButton eliminar;

        public ViewHolderPalabras(@NonNull View itemView) {
            super(itemView);
            tv_palabra = itemView.findViewById(R.id.tv_inside_palabra);
            tv_traduccion = itemView.findViewById(R.id.tv_inside_traduccion);
            tv_tipo = itemView.findViewById(R.id.tv_inside_tipo);

            editar = itemView.findViewById(R.id.ib_editar_palabra);
            eliminar = itemView.findViewById(R.id.ib_eliminar_palabra);
        }

        public void asignarDatos(Palabra palabra) {
            tv_palabra.setText(palabra.getPalabra());
            tv_traduccion.setText(palabra.getTraduccion());
            tv_tipo.setText(palabra.getTipo());

            editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, AddPalabra.class);
                    i.putExtra("bundle", bundle);
                    i.putExtra("palabra", palabra);
                    i.putExtra("tipo", "editar");
                    context.startActivity(i);
                }
            });

            eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InsideColeccion.crearDialogo(palabra);
                }
            });
        }
    }

}
