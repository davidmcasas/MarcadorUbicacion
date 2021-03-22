package com.davidmcasas.marcadorubicacion.database;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.davidmcasas.marcadorubicacion.R;

class MarcadorViewHolder extends RecyclerView.ViewHolder {
    TextView vNombre, vDescripcion;
    ImageView editContact, deleteMarcador;
    MarcadorViewHolder(View itemView) {
        super(itemView);
        vNombre = itemView.findViewById(R.id.marcadorNombre);
        vDescripcion = itemView.findViewById(R.id.marcadorDescripcion);
        editContact = itemView.findViewById(R.id.editMarcador);
        deleteMarcador = itemView.findViewById(R.id.deleteMarcador);
    }
}