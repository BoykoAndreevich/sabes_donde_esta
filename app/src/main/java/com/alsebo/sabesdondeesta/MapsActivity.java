package com.alsebo.sabesdondeesta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.text.DecimalFormat;


public class MapsActivity extends FragmentActivity implements  View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    LatLng pregunta , respuesta;
    Marker markerPregunta , markerespuesta ;
    int puntos, num;
    TextView textoPregunta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragments_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button jugar = (Button) findViewById(R.id.button3);
        jugar.setOnClickListener(this);

        SharedPreferences.Editor editor= MainActivity.pref.edit();
        editor.putString("1","¿Dónde está el Taj Mahal?");
        editor.putString("2", "¿Dónde está las Pirámides Egipcias?");
        editor.putString("3", "¿Dónde está la Torre de Pisa?");
        editor.putString("4", "¿Dónde está la Estatua de la Libertad?");
        editor.putString("5", "¿Dónde están las ruinas Mayas 'Chichén Itzá'?");
        editor.commit();

        textoPregunta = (TextView) findViewById(R.id.textView4);
        num = (int) (Math.random()*5+1);
        String valor = MainActivity.pref.getString(String.valueOf(num), "Sin respuesta de Houston!");
        textoPregunta.setText(valor);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    //APLICANDO ESTILO AL MAPA
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("TAG", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("TAG", "Can't find style. Error: ", e);
        }

    }
    //ACCION PARA MARCAR ZONA AL PULSAR EN EL MAPA
    @Override
    public void onMapClick(LatLng latLng) {
            respuesta = latLng;
            markerespuesta = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
            );
    }

    //BOTON PARA RESPONDER A LA PREGUNTA DESPUES DE MARCARLA EN EL MAPA
    @Override
    public void onClick(View view) {
        if(num == 2) {
            pregunta = new LatLng(29.977667, 31.133077);
        }else if(num == 3) {
            pregunta = new LatLng(43.722812, 10.396554);
        }else if(num == 4) {
            pregunta = new LatLng(40.689310, -74.044011);
        }else if(num == 5){
            pregunta = new LatLng(20.684243, -88.567777);
        }else{
            pregunta = new LatLng(27.175525, 78.041526);
        }
        markerPregunta = mMap.addMarker(new MarkerOptions()
                .position(pregunta)
                .title("¡ESTA AQUI!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        );

        //CREAR LINEAS ENTRE LAS DOS RESPUESTAS
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(0x66FF0000);
        polylineOptions.add(respuesta);
        polylineOptions.add(pregunta);
        mMap.addPolyline(polylineOptions);

        Location locationA = new Location("LocationA");
        locationA.setLatitude(respuesta.latitude);
        locationA.setLongitude(respuesta.longitude);

        Location locationB = new Location("LocationB");
        locationB.setLatitude(pregunta.latitude);
        locationB.setLongitude(pregunta.longitude);

        //METODO PARA CALCULAR LA DISTANCIA ENTRE LAS DOS POSICIONES
        float distance = locationA.distanceTo(locationB)/1000;

        distancia(distance);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pregunta));
    }

    //METODO PARA GENERAR LA VISTA PEQUEÑA DE LA RESPUESTA Y GUARDAR LA PUNTUACION EN EL ALMACENAMIENTO
    public void distancia(Float distance2){
        puntos = 0;
        DecimalFormat format = new DecimalFormat("#0.00");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("RESPUESTA");
                if(distance2 > 1000) {
                    puntos += 10;
                    builder.setIcon(R.drawable.cara);
                }else{
                    puntos +=1000;
                    builder.setIcon(R.drawable.feliz);
                }
        builder.setMessage(String.valueOf(puntos)+ " puntos\n"+"Distancia: "+format.format(distance2) + " KM");
        AlertDialog dialog = builder.create();
        dialog.show();

        int puntosAntes = MainActivity.pref.getInt("nuevosPuntos", 0);
        puntosAntes += puntos;

        SharedPreferences.Editor editor = MainActivity.pref.edit();
        editor.putInt("nuevosPuntos", puntosAntes);
        editor.commit();

    }

    //BOTON QUE AL PULSAR REINICIA LA VISTA DEL MAPA PARA GENERAR OTRA PREGUNTA CON SU RESPUESTA
    public void volver(View view) {
        reiniciarActivity(this);
    }

    //METODO PARA REINICIAR LA ACTIVIDAD DEL MAPA
    public static void reiniciarActivity(Activity actividad){
        Intent intent=new Intent();
        intent.setClass(actividad, actividad.getClass());
        //llamamos a la actividad
        actividad.startActivity(intent);
        //finalizamos la actividad actual
        actividad.finish();
    }
    //BOTON PARA VOLVER AL ACTIVIDAD DEL MENU
    public void menu(View view) {
        Intent menu = new Intent(this, MainActivityMenu.class);
        startActivity(menu);
    }
}