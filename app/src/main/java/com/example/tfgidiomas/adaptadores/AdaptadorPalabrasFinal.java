package com.example.tfgidiomas.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tfgidiomas.Palabra;
import com.example.tfgidiomas.R;


import java.util.ArrayList;

public class AdaptadorPalabrasFinal extends RecyclerView.Adapter<AdaptadorPalabrasFinal.ViewHolderPalabrasFinal>{

    private ArrayList<Palabra> palabras;
    private int[] puntos;

    private Context context;


    public AdaptadorPalabrasFinal(ArrayList<Palabra> palabras, Context context){
            this.palabras=palabras;
            this.context=context;
    }

    public AdaptadorPalabrasFinal(ArrayList<Palabra> palabras, int[] puntos, Context context) {
        this.palabras = palabras;
        this.puntos = puntos;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderPalabrasFinal onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.carta_palabra_final,null,false);
            return new ViewHolderPalabrasFinal(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPalabrasFinal holder,int position){
        if (puntos == null) holder.asignarDatos(palabras.get(position));
        else holder.asignarDatos(palabras.get(position), puntos[position]);
    }

    @Override
    public int getItemCount(){
            return palabras.size();
    }

    public class ViewHolderPalabrasFinal extends RecyclerView.ViewHolder {

        TextView tv_palabra;
        TextView tv_traduccion;
        TextView tv_puntos;

        public ViewHolderPalabrasFinal(@NonNull View itemView) {
            super(itemView);
            tv_palabra = itemView.findViewById(R.id.tv_inside_palabra);
            tv_traduccion = itemView.findViewById(R.id.tv_inside_traduccion);
            tv_puntos = itemView.findViewById(R.id.tv_puntos_palabra_final);

        }

        public void asignarDatos(Palabra palabra) {
            tv_palabra.setText(palabra.getPalabra());
            tv_traduccion.setText(palabra.getTraduccion());
            tv_puntos.setText("2 puntos");
       }

        public void asignarDatos(Palabra palabra, int puntos) {
            tv_palabra.setText(palabra.getPalabra());
            tv_traduccion.setText(palabra.getTraduccion());
            tv_puntos.setText(puntos + " puntos");
        }
    }
}
