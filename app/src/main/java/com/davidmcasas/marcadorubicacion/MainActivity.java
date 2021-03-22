package com.davidmcasas.marcadorubicacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.davidmcasas.marcadorubicacion.database.DatabaseHelper;
import com.davidmcasas.marcadorubicacion.database.Marcador;
import com.davidmcasas.marcadorubicacion.database.MarcadorAdapter;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static DatabaseHelper db; // Aquí se almacenará una referencia a la instancia de la base de datos
    public static Context appContext; // Aquí se almacenará una referencia al Context (this) del MainActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getResources().getString(R.string.title));

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Almacenar en variables estáticas el Context y la instancia de la base de datos,
        // para poder acceder a dichos objetos fácilmente desde otras clases.
        appContext = this;
        db = DatabaseHelper.getInstance(this);

        // Pedir permismos de Ubicación si no se tienen
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarListaMarcadores();
        LocationHelper.activarUbicacion();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults[0] == 0) { // si el usuario activa los permisos
            LocationHelper.activarUbicacion();
        } else { // si los rechaza
            Toast.makeText(this, R.string.permissions_denied, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Carga el Activity Marcador, activando el marcador que se pase por parámetro.
     * @param marcador
     */
    public static void cargarActivityMarcador(Marcador marcador) {
        MarcadorActivity.marcador = marcador;
        //VisorUbicacion.marcador = marcador;
        Intent myIntent = new Intent(MainActivity.appContext, MarcadorActivity.class);
        appContext.startActivity(myIntent);
    }

    /**
     * Inicia el diálogo de nuevo marcador
     * @param view
     */
    public void botonNuevoMarcador(View view) {

        // Creamos el dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.appContext);
        builder.setCancelable(true);
        builder.setTitle(R.string.new_marker);
        builder.setMessage(R.string.type_name);
        final EditText input = new EditText(MainActivity.appContext);
        LinearLayout parentLayout = new LinearLayout(view.getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(80,20,80,20);
        input.setLayoutParams(lp);
        parentLayout.addView(input);
        builder.setView(parentLayout);

        // Añadimos el botón OK y definimos su funcionalidad
        builder.setPositiveButton("OK"/*android.R.string.ok*/,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Marcador marcador = new Marcador(null, null, null, null, null, null);
                        marcador.setNombre(input.getText().toString());
                        try {
                            LocationHelper.actualizarUbicacion(marcador);
                            //DatabaseHelper.getInstance(MainActivity.appContext).addMarcador(marcador);
                            Toast.makeText(MainActivity.appContext, R.string.marker_created, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            try {
                                LocationHelper.actualizarUbicacion(marcador, new LatLng(40.4167, -3.70325)); // km 0 de madrid
                                Toast.makeText(MainActivity.appContext, R.string.default_location, Toast.LENGTH_LONG).show();
                            } catch (Exception e2) {
                                Toast.makeText(MainActivity.appContext, R.string.marker_failure, Toast.LENGTH_LONG).show();
                            }
                        }
                        ((MainActivity)MainActivity.appContext).cargarListaMarcadores();
                    }
                });

        // Mostramos el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /**
     * Carga la lista de marcadores
     */
    public void cargarListaMarcadores() {
        RecyclerView marcadoresView = findViewById(R.id.listaMarcadores);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        marcadoresView.setLayoutManager(linearLayoutManager);
        marcadoresView.setHasFixedSize(true);

        ArrayList<Marcador> marcadores = db.getMarcadores();
        if (marcadores.size() > 0) {
            marcadoresView.setVisibility(View.VISIBLE);
            MarcadorAdapter mAdapter = new MarcadorAdapter(this, marcadores);
            marcadoresView.setAdapter(mAdapter);
        }
        else {
            marcadoresView.setVisibility(View.GONE);
            //Toast.makeText(this, "No hay ningún marcador, crea uno.", Toast.LENGTH_LONG).show();
        }
    }

    public void botonVerTodos(View view) {
        Intent myIntent = new Intent(MainActivity.appContext, TodosActivity.class);
        appContext.startActivity(myIntent);
    }

}