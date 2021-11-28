package com.alsebo.sabesdondeesta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements  View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    int contador =0;
    private GoogleMap mMap;
    LatLng pregunta , respuesta;
    Marker markerPregunta, markerespuesta ;
    long newpuntos, otrojuego;
    TextView textoPregunta;
    Boolean cambiarMapa = false;
    boolean success;
    private static final String TAG = "MapsActivity";
    FirebaseFirestore db;
    int cont, num;
    long oldpuntos;
    Boolean modoDificil;
    Bundle extras;
    String emaile;
    String tip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragments_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button jugar = (Button) findViewById(R.id.button3);
        jugar.setOnClickListener(this);
        Button niIdea = (Button) findViewById(R.id.buttonNoSe);
        niIdea.setOnClickListener(this);
        Button next = (Button) findViewById(R.id.buttonSiguiente);
        next.setOnClickListener(this);
        textoPregunta = (TextView) findViewById(R.id.preguntaEdit);
        ImageButton atras= (ImageButton) findViewById(R.id.imageButtonAtras);
        atras.setOnClickListener(this);

        extras = getIntent().getExtras();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            emaile = user.getEmail();
        }

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("jugador").document(emaile);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        oldpuntos = document.getLong("puntuacion");
                        // pregunta = document.getBoolean("profesor");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        tip = extras.getString("tipo");
        modoDificil = extras.getBoolean("modo");

        db = FirebaseFirestore.getInstance();
        db.collection("questions."+tip).whereNotEqualTo("pregunta",null )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            cont=task.getResult().size();
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
                });

    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    //APLICANDO ESTILO AL MAPA
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            if(cambiarMapa == false) {
                 success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style_json));
            }else{
                SupportMapFragment mapFragment = new SupportMapFragment();
                mapFragment.getMapAsync(this);
                success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style));
            }
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
        if (cambiarMapa == false) {
            if (contador != 0) {
                markerespuesta.remove();
                markerespuesta = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                );
            } else {
                markerespuesta = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                );
                contador = +1;
            }
        }
    }

    //BOTON PARA RESPONDER A LA PREGUNTA DESPUES DE MARCARLA EN EL MAPA
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button3:
                cambiarMapa = true;
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
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
                float distance = locationA.distanceTo(locationB) / 1000;

                distancia(distance);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pregunta));
                break;

            case R.id.buttonNoSe:
                cambiarMapa = true;
                mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
                markerPregunta = mMap.addMarker(new MarkerOptions()
                        .position(pregunta)
                        .title("¡ESTA AQUI!")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                );
                distancia((float) 0);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pregunta));
                break;

            case R.id.buttonSiguiente:
                Intent mapaa = new Intent(this, MapsActivity.class);
                mapaa.putExtra("tipo",tip);
                finish();
                startActivity(mapaa);
                break;

            case R.id.imageButtonAtras:
                Intent atras = new Intent(this, MainActivityMenu.class);
                startActivity(atras);
                finish();
                break;

            default:
        }
    }


    //METODO PARA GENERAR LA VISTA PEQUEÑA DE LA RESPUESTA Y GUARDAR LA PUNTUACION EN EL ALMACENAMIENTO
    public void distancia(Float distance2) {
        newpuntos = 0;

        DecimalFormat format = new DecimalFormat("#0.00");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("RESPUESTA");
        if (modoDificil == true){
            if (distance2 > 25 || distance2 == 0) {
                newpuntos -= 100;
                builder.setIcon(R.drawable.cancelar);
            } else {
                newpuntos += 100;
                builder.setIcon(R.drawable.cheque);
            }
        }else{
            if (distance2 > 100 || distance2 == 0) {
                newpuntos -= 10;
                builder.setIcon(R.drawable.cancelar);
            } else {
                newpuntos += 10;
                builder.setIcon(R.drawable.cheque);
            }
        }
        builder.setMessage(String.valueOf(newpuntos) + " puntos\n" + "Distancia: " + format.format(distance2) + " KM");
        AlertDialog dialog = builder.create();
        dialog.show();
        newpuntos += oldpuntos;

        DocumentReference userRef = db.collection("jugador").document(emaile);
        userRef
                .update("puntuacion", newpuntos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });


    }

}