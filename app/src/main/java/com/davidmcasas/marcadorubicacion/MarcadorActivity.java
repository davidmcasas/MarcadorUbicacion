package com.davidmcasas.marcadorubicacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.davidmcasas.marcadorubicacion.database.Marcador;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarcadorActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static Context appContext;
    public static Marcador marcador = null;
    private static final float DEFAULT_ZOOM = 16f;
    private static GoogleMap gMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marcador);

        appContext = this;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarValores();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        gMap = googleMap;
        colocarMarcador();

        final GoogleMap g = gMap;

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(final LatLng latLng) {

                // Creamos el dialogo
                AlertDialog.Builder builder = new AlertDialog.Builder(MarcadorActivity.this);
                builder.setCancelable(true);
                builder.setTitle(R.string.move_marker);
                builder.setMessage(R.string.move_marker_selected_desc);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                // Añadimos el botón OK y definimos su funcionalidad
                builder.setPositiveButton(R.string.yes/*android.R.string.ok*/,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    LocationHelper.actualizarUbicacion(marcador, latLng);
                                    //DatabaseHelper.getInstance(MainActivity.appContext).addMarcador(marcador);
                                    Toast.makeText(MainActivity.appContext, R.string.marker_moved, Toast.LENGTH_LONG).show();
                                    colocarMarcador();
                                    cargarValores();
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.appContext, R.string.move_failure, Toast.LENGTH_LONG).show();
                                }
                                ((MainActivity)MainActivity.appContext).cargarListaMarcadores();
                            }
                        });
                builder.setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }

    public void colocarMarcador() {
        if (gMap == null) {
            return;
        }
        gMap.clear();
        String[] valores = marcador.getCoordenadas().split(" ");
        LatLng latlng = new LatLng(Double.parseDouble(valores[0]), Double.parseDouble(valores[1]));
        gMap.addMarker(new MarkerOptions().position(latlng).title(marcador.getNombre()));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, DEFAULT_ZOOM));
    }

    public void botonActualizarUbicacion(View view) {

        // Creamos el dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(MarcadorActivity.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.move_marker);
        builder.setMessage(R.string.move_marker_desc);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        // Añadimos el botón OK y definimos su funcionalidad
        builder.setPositiveButton(R.string.yes/*android.R.string.ok*/,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            LocationHelper.actualizarUbicacion(marcador);
                            Toast.makeText(MainActivity.appContext, R.string.marker_moved, Toast.LENGTH_LONG).show();
                            colocarMarcador();
                            cargarValores();
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.appContext, R.string.move_failure, Toast.LENGTH_LONG).show();
                        }
                        ((MainActivity)MainActivity.appContext).cargarListaMarcadores();
                    }
                });
        builder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cargarValores() {

        // Si el marcador no tiene descripcion, es posible que sea porque no se pudo obtener al moverlo,
        // por ejemplo si no había conexión a internet. En este bloque se intenta actualizar el marcador.
        if (marcador.getDescripcion() == null || marcador.getDescripcion().length() == 0) {
            double lat = Double.parseDouble(marcador.getCoordenadas().split(" ")[0]);
            double lng = Double.parseDouble(marcador.getCoordenadas().split(" ")[1]);
            LocationHelper.actualizarUbicacion(marcador, new LatLng(lat,lng));
        }

        TextView tv_nombre = (TextView) findViewById(R.id.marcadorActivity_nombre);
        TextView tv_descripcion = (TextView) findViewById(R.id.marcadorActivity_descripcion);
        TextView tv_coordenadas = (TextView) findViewById(R.id.marcadorActivity_coordenadas);
        tv_nombre.setText(marcador.getNombre());
        tv_descripcion.setText(marcador.getDescripcion());
        tv_coordenadas.setText(marcador.getCoordenadas());
    }

    public void copiarDescripcion(View view) {
        TextView tv = findViewById(R.id.marcadorActivity_descripcion);
        String text = tv.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text",  text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, R.string.clipboard_address, Toast.LENGTH_SHORT).show();
    }

    public void copiarCoordenadas(View view) {
        TextView tv = findViewById(R.id.marcadorActivity_coordenadas);
        String text = tv.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text",  text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, R.string.clipboard_coordinates, Toast.LENGTH_SHORT).show();
    }
}