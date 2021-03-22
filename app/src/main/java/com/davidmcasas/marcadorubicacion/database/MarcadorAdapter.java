package com.davidmcasas.marcadorubicacion.database;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import com.davidmcasas.marcadorubicacion.MainActivity;
import com.davidmcasas.marcadorubicacion.R;

public class MarcadorAdapter extends RecyclerView.Adapter<MarcadorViewHolder> {

    private Context context;
    private ArrayList<Marcador> marcadores;

    public MarcadorAdapter(Context context, ArrayList<Marcador> marcadores) {
        this.context = context;
        this.marcadores = marcadores;
    }
    @Override
    public MarcadorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.marcador_list_layout, parent, false);
        return new MarcadorViewHolder(view);
    }
    @Override
    public void onBindViewHolder(MarcadorViewHolder holder, int position) {
        final Marcador marcador = marcadores.get(position);
        holder.vNombre.setText(marcador.getNombre());
        holder.vDescripcion.setText(marcador.getDescripcion());
        holder.editContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.appContext);
                builder.setCancelable(true);
                builder.setTitle(MainActivity.appContext.getResources().getString(R.string.rename_marker));
                builder.setMessage(MainActivity.appContext.getResources().getString(R.string.type_name));
                final EditText input = new EditText(MainActivity.appContext);
                input.setText(marcador.getNombre().toString());
                LinearLayout parentLayout = new LinearLayout(MainActivity.appContext);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(80,20,80,20);
                input.setLayoutParams(lp);
                parentLayout.addView(input);
                builder.setView(parentLayout);
                builder.setPositiveButton(MainActivity.appContext.getResources().getString(R.string.ok)/*android.R.string.ok*/,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (! marcador.getNombre().equals(input.getText().toString())) {
                                    marcador.setNombre(input.getText().toString());
                                    DatabaseHelper.getInstance(MainActivity.appContext).updateMarcador(marcador);
                                    Toast.makeText(MainActivity.appContext, MainActivity.appContext.getResources().getString(R.string.marker_renamed), Toast.LENGTH_LONG).show();
                                    ((MainActivity)MainActivity.appContext).cargarListaMarcadores();
                                }

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        holder.deleteMarcador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.appContext);
                builder.setCancelable(true);
                builder.setTitle(marcador.getNombre());
                builder.setMessage(MainActivity.appContext.getResources().getString(R.string.delete_marker));
                builder.setPositiveButton(MainActivity.appContext.getResources().getString(R.string.yes)/*android.R.string.ok*/,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseHelper.getInstance(MainActivity.appContext).deleteMarcador(marcador.getId());
                                Toast.makeText(MainActivity.appContext, MainActivity.appContext.getResources().getString(R.string.marker_deleted), Toast.LENGTH_LONG).show();
                                ((MainActivity)MainActivity.appContext).cargarListaMarcadores();
                            }
                        });
                builder.setNegativeButton(MainActivity.appContext.getResources().getString(R.string.no)/*android.R.string.cancel*/,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // no hacer nada
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (marcador.getCoordenadas() != null) {
                    MainActivity.cargarActivityMarcador(marcador);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return marcadores.size();
    }

}