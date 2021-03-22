package com.davidmcasas.marcadorubicacion;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.davidmcasas.marcadorubicacion.database.DatabaseHelper;
import com.davidmcasas.marcadorubicacion.database.Marcador;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

public class LocationHelper {

    /**
     * Activa el LocationManager para que reciba actualizaciones de la ubicación del dispositivo
     */
    public static void activarUbicacion() {

        try {
            LocationManager locManager = (LocationManager) MainActivity.appContext.getSystemService(Context.LOCATION_SERVICE);
            String bestProvider = locManager.getBestProvider(new Criteria(), true);

            if (ActivityCompat.checkSelfPermission(MainActivity.appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            locManager.requestLocationUpdates(bestProvider, 5000l, 1f, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    // no hacer nada
                }
            });
        } catch (Exception e) {
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    /**
     * Apaño
     * @return
     */
    private static Location getLastKnownLocation() {
        LocationManager locManager = (LocationManager) MainActivity.appContext.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(MainActivity.appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.appContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            Location l = locManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {

                bestLocation = l;
            }
        }
        return bestLocation;
    }

    /**
     * Actualiza las coordenadas y descripcion de un objeto Marcador a la ubicacion actual del dispositivo.
     * Lo actualiza en la base de datos, y si es nuevo (id == null) lo inserta.
     * @param marcador Objeto Marcador a actualizar
     */
    public static void actualizarUbicacion(Marcador marcador) {

        LocationManager locManager = (LocationManager) MainActivity.appContext.getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = locManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(MainActivity.appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        locManager.requestLocationUpdates(bestProvider, 5000l, 1f, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

            }
        });
        Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (loc == null) locManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (loc == null) locManager.getLastKnownLocation(bestProvider);
        if (loc == null) loc = getLastKnownLocation();
        String coordenadas = loc.getLatitude()+" "+loc.getLongitude();
        // -- actualizar descripcion
        Geocoder gcd = new Geocoder(MainActivity.appContext, Locale.getDefault());
        List<Address> addresses;
        String descripcion = "";
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
            if (addresses.size() > 0) {
                //descripcion = addresses.get(0).getLocality();
                descripcion = addresses.get(0).getAddressLine(0);
            }
        } catch (Exception e) {

        }

        // --

        marcador.setDescripcion(descripcion);
        marcador.setCoordenadas(coordenadas);

        if (marcador.getId() == null) {
            MainActivity.db.addMarcador(marcador);
        } else {
            MainActivity.db.updateMarcador(marcador);
        }
        //
        //Toast.makeText(this, "Marcador actualizado", Toast.LENGTH_LONG).show();

    }

    /**
     * Actualiza las coordenadas y descripcion de un objeto Marcador a la ubicacion indicada.
     * Lo actualiza en la base de datos, y si es nuevo (id == null) lo inserta.
     * @param marcador Objeto Marcador a actualizar
     * @param latlng array de dos valores double, latitud y longitud
     */
    public static void actualizarUbicacion(Marcador marcador, LatLng latlng) {

        Location loc = new Location("gps");
        loc.setLatitude(latlng.latitude);
        loc.setLongitude(latlng.longitude);
        String coordenadas = latlng.latitude+" "+latlng.longitude;

        // -- actualizar descripcion
        Geocoder gcd = new Geocoder(MainActivity.appContext, Locale.getDefault());
        List<Address> addresses;
        String descripcion = "";
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
            if (addresses.size() > 0) {
                //descripcion = addresses.get(0).getLocality();
                descripcion = addresses.get(0).getAddressLine(0);
            }
        } catch (Exception e) {
            Toast.makeText(MarcadorActivity.appContext, R.string.address_failure, Toast.LENGTH_LONG).show();
        }

        // --

        marcador.setDescripcion(descripcion);
        marcador.setCoordenadas(coordenadas);

        if (marcador.getId() == null) {
            MainActivity.db.addMarcador(marcador);
        } else {
            MainActivity.db.updateMarcador(marcador);
        }
        //
        //Toast.makeText(this, "Marcador actualizado", Toast.LENGTH_LONG).show();

    }

}
