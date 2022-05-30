package com.example.tfgidiomas.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfgidiomas.Idioma;
import com.example.tfgidiomas.R;

import java.util.ArrayList;

public class AdaptadorIdiomas extends RecyclerView.Adapter<AdaptadorIdiomas.ViewHolderIdiomas> {

    ArrayList<Idioma> listaIdiomas;

    public AdaptadorIdiomas(ArrayList<Idioma> listaIdiomas) {
        this.listaIdiomas = listaIdiomas;
    }

    @NonNull
    @Override
    public ViewHolderIdiomas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.boton_idioma, null, false);
        return new ViewHolderIdiomas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderIdiomas holder, int position) {
        holder.asignarDatos(listaIdiomas.get(position).get_idioma());
    }

    @Override
    public int getItemCount() {
        return listaIdiomas.size();
    }

    public class ViewHolderIdiomas extends RecyclerView.ViewHolder{

        Button idioma;

        public ViewHolderIdiomas(@NonNull View itemView) {
            super(itemView);
            idioma = itemView.findViewById(R.id.btn_idioma);
        }

        public void asignarDatos(String nombre) {
            idioma.setText(nombre);
        }
    }

}
