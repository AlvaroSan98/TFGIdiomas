package com.example.tfgidiomas.adaptadores;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tfgidiomas.Partida;
import com.example.tfgidiomas.R;


import java.util.ArrayList;

public class AdaptadorPartidasPerfil extends RecyclerView.Adapter<AdaptadorPartidasPerfil.ViewHolderPartidasPerfil>{

    private ArrayList<Partida> partidas;

    private Context context;
    private Bundle bundle;


    public AdaptadorPartidasPerfil(ArrayList<Partida> partidas, Context context, Bundle bundle){
        this.partidas=partidas;
        this.context=context;
        this.bundle=bundle;
    }

    @NonNull
    @Override
    public ViewHolderPartidasPerfil onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.carta_partida_perfil,null,false);
        return new ViewHolderPartidasPerfil(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPartidasPerfil holder,int position){
        holder.asignarDatos(partidas.get(position));
    }

    @Override
    public int getItemCount(){
        return partidas.size();
    }

    public class ViewHolderPartidasPerfil extends RecyclerView.ViewHolder {

        TextView tv_fecha;
        TextView tv_nombres;
        TextView tv_puntos;

        public ViewHolderPartidasPerfil(@NonNull View itemView) {
            super(itemView);
            tv_fecha = itemView.findViewById(R.id.tv_fecha_partida);
            tv_nombres = itemView.findViewById(R.id.tv_solo_multijugador);
            tv_puntos = itemView.findViewById(R.id.tv_puntos_partida_perfil);
        }

        public void asignarDatos(Partida partida) {
            tv_fecha.setText(partida.getFecha());
            tv_nombres.setText("Juego: " + partida.getJuego());
            tv_puntos.setText(partida.getPuntos() + " puntos");
        }
    }
}
