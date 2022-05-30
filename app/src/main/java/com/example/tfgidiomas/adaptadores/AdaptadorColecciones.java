package com.example.tfgidiomas.adaptadores;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfgidiomas.Coleccion;
import com.example.tfgidiomas.ColeccionesActivity;
import com.example.tfgidiomas.R;

import java.util.ArrayList;

public class AdaptadorColecciones extends RecyclerView.Adapter<AdaptadorColecciones.ViewHolderColecciones> {

    ArrayList<Coleccion> listaColecciones;
    Context context;
    ViewHolderColecciones holder;

    public AdaptadorColecciones(ArrayList<Coleccion> listaColecciones, Context context) {
        this.listaColecciones = listaColecciones;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderColecciones onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.boton_colecciones, null, false);
        return new ViewHolderColecciones(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderColecciones holder, int position) {
        holder.asignarDatos(listaColecciones.get(position).getNombre(), position);
    }

    @Override
    public int getItemCount() {
        return listaColecciones.size();
    }

    public class ViewHolderColecciones extends RecyclerView.ViewHolder {

        Button b;

        public ViewHolderColecciones(@NonNull View itemView) {
            super(itemView);
            b = itemView.findViewById(R.id.btn_colecciones);
        }

        public void asignarDatos(String coleccion, int position) {
            b.setText(coleccion);
            b.setTag(position);
            b.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ColeccionesActivity.crearDialogo(listaColecciones.get(position));
                    return true;
                }
            });
        }
    }

}
