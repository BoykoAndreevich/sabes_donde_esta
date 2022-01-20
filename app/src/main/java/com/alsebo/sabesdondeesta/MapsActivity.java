package com.alsebo.sabesdondeesta;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.DecimalFormat;

public class MapsActivity extends FragmentActivity implements  View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    LatLng pregunta , respuesta;
    Marker markerPregunta, markerespuesta ;
    long newpuntos, oldpuntos;
    TextView textoPregunta, textoContador ;
    boolean cambiarMapa = false;
    Boolean pararTimpo = false;
    boolean success;
    private static final String TAG = "MapsActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();;
    int cont, num;
    int contador =0;
    String prefEmail, prefModo;
    Boolean prefDificulta;
    SupportMapFragment mapFragment;
    Button jugar, niIdea;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragments_map);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        prefs = getSharedPreferences("donde.esta",Context.MODE_PRIVATE);
        editor = prefs.edit();

        prefEmail = prefs.getString("email", "email");
        prefModo = prefs.getString("modo", "modo");
        prefDificulta = prefs.getBoolean("dificultad", false);

        jugar = (Button) findViewById(R.id.button3);
        jugar.setOnClickListener(this);
        niIdea = (Button) findViewById(R.id.buttonNoSe);
        niIdea.setOnClickListener(this);
        Button next = (Button) findViewById(R.id.buttonSiguiente);
        next.setOnClickListener(this);
        textoPregunta = (TextView) findViewById(R.id.preguntaEdit);
        Button atras= (Button) findViewById(R.id.atras);
        atras.setOnClickListener(this);
        textoContador = (TextView) findViewById(R.id.textViewContador);

        DocumentReference docRef = db.collection("jugador").document(prefEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        oldpuntos = document.getLong("puntuacion");
                    }
                }
            }
        });

        db.collection(prefModo).whereNotEqualTo("pregunta",null )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            cont=task.getResult().size();
                            num= (int) (Math.random()*cont+1);

                            db.collection(prefModo)
                                    .document(String.valueOf(num)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            textoPregunta.append(String.valueOf(document.get("pregunta")));
                                            pregunta = new LatLng((Double) document.get("latitude"), (Double) document.get("longitude"));
                                        }
                                    }
                                }
                            });
                        }
                    }
                });

    }
    @Override
    public void onStart() {
        super.onStart();
        cronometro();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        try {
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

        } catch (Resources.NotFoundException e) {
            Log.e("TAG", "Can't find style. Error: ", e);
        }

    }

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button3:
                desactivar();
                pararTimpo = true;
                cambiarMapa = true;
                mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
                markerPregunta = mMap.addMarker(new MarkerOptions()
                        .position(pregunta)
                        .title("¡ESTA AQUI!")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                );

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


                float distance = locationA.distanceTo(locationB) / 1000;

                distancia(distance);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pregunta));
                break;

            case R.id.buttonNoSe:
                desactivar();
                pararTimpo = true;
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
                finish();
                startActivity(mapaa);
                break;

            case R.id.atras:
                Intent atras = new Intent(this, MainActivityMenu.class);
                startActivity(atras);
                finish();
                break;

            default:
        }
    }

    public void desactivar(){
        jugar.setEnabled(false);
        niIdea.setEnabled(false);
    }

    public void cronometro(){
        new CountDownTimer(30000,1000){

            @Override
            public void onTick(long l) {
                textoContador.setText(""+l/1000);
                if(pararTimpo==true){
                    textoContador.setText(""+0);
                }
            }

            @Override
            public void onFinish() {
                if(pararTimpo==false) {
                    cambiMapa();
                    distancia((float) 0);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(pregunta));
                    desactivar();
                }
            }
        }.start();
    }

    public void cambiMapa(){
        cambiarMapa = true;
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        markerPregunta = mMap.addMarker(new MarkerOptions()
                .position(pregunta)
                .title("¡ESTA AQUI!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        );
    }

    public void distancia(Float distance2) {
        newpuntos = 0;

        DecimalFormat format = new DecimalFormat("#0.00");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("RESPUESTA");
        if (prefDificulta == true){
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

        DocumentReference userRef = db.collection("jugador").document(prefEmail);
        userRef
                .update("puntuacion", newpuntos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

}