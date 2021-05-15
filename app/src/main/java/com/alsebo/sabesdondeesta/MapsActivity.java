package com.alsebo.sabesdondeesta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.alsebo.sabesdondeesta.MainActivity.db;
import static com.alsebo.sabesdondeesta.MainActivity.email;
import static com.alsebo.sabesdondeesta.MainActivity.user;
import static com.alsebo.sabesdondeesta.MainActivityMenu.puntos;


public class MapsActivity extends FragmentActivity implements  View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {


    private GoogleMap mMap;
    LatLng pregunta , respuesta;
    Marker markerPregunta , markerespuesta ;
    long newpuntos;
    TextView textoPregunta;
    int cont, num;
    private static final String TAG = "MapsActivity";

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
        ImageButton borrar = (ImageButton) findViewById(R.id.borrarButton);
        borrar.setOnClickListener(this);

        textoPregunta = (TextView) findViewById(R.id.preguntaEdit);

        Bundle extras = getIntent().getExtras();
        String tip = extras.getString("tipo");

        /**db.collection("questions."+tip).whereNotEqualTo("pregunta",null )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            cont=task.getResult().size();
                            // textoPregunta.setText(String.valueOf(cont));
                            num= (int) (Math.random()*cont+1);
                            db.collection("questions."+tip)
                                    .document(String.valueOf(num)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            textoPregunta.append(String.valueOf(document.get("pregunta")));
                                            pregunta = new LatLng((Double) document.get("latitude"), (Double) document.get("longitude"));
                                        }
                                    } else {
                                        Log.w("error", "Error getting documents.", task.getException());
                                    }
                                }
                            });

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });*/

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
        newpuntos = 0;
        DecimalFormat format = new DecimalFormat("#0.00");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("RESPUESTA");
                if(distance2 > 1000) {
                    newpuntos -= 10;
                    builder.setIcon(R.drawable.cara);
                }else{
                    newpuntos +=100;
                    builder.setIcon(R.drawable.feliz);
                }
        builder.setMessage(String.valueOf(newpuntos)+ " puntos\n"+"Distancia: "+format.format(distance2) + " KM");
        AlertDialog dialog = builder.create();
        dialog.show();
        puntos += newpuntos;
        user.put("puntuacion", puntos);
        db.collection("users")
                .document(email.getText().toString()).set(user)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("error", "Error adding document", e);
                    }
                });

    }

    //BOTON QUE AL PULSAR REINICIA LA VISTA DEL MAPA PARA GENERAR OTRA PREGUNTA CON SU RESPUESTA
    public void volver(View view) {

    }
    //METODO PARA REINICIAR LA ACTIVIDAD
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
        finish();
    }

    public void borrarTodo(View view) {
        markerespuesta.remove();
    }

    /**private void removeAllMarkers() {
        List<Marker> AllMarkers = new ArrayList<Marker>();
        for (Marker markerespuesta: AllMarkers) {
            markerespuesta.remove();
        }
        AllMarkers.clear();
*/

}