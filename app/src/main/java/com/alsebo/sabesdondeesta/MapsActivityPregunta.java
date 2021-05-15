package com.alsebo.sabesdondeesta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MapsActivityPregunta extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, View.OnClickListener {

    private GoogleMap mMap;
    LatLng pregunta;
    Marker markerPregunta;
    EditText textoPregunta;
    TextView textoRespuesta;
    int cont;
    private static final String TAG = "MapsActivityPregunta";


    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> pregunt = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_pregunta);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        textoPregunta = (EditText) findViewById(R.id.preguntaEdit);
        textoRespuesta = (TextView) findViewById(R.id.respuestaEdit);


        db.collection("questions.users").whereNotEqualTo("pregunta",null )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                                cont=task.getResult().size();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    //ACCION PARA MARCAR ZONA AL PULSAR EN EL MAPA
    @Override
    public void onMapClick(LatLng latLng) {
        pregunta = latLng;
        markerPregunta = mMap.addMarker(new MarkerOptions()
                .position(latLng)
        );
    }

    @Override
    public void onClick(View view) {
        pregunt.put("pregunta", textoPregunta.getText().toString());
        pregunt.put("latitude", pregunta.latitude);
        pregunt.put("longitude", pregunta.longitude);

        cont++;
        db.collection("questions.users")
                .document(String.valueOf(cont)).set(pregunt)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("error", "Error adding document", e);
                    }
                });
        textoRespuesta.setText("latitude ="+pregunta.latitude+"\nlongitude ="+pregunta.longitude);
        dialog();
    }

    public void dialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("La pregunta se ha guardado correctamente\n"+"Numero preguntas = " + cont );
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void salir(View view) {
        Intent map = new Intent(this, MainActivityMenu.class);
        startActivity(map);
    }
}