package com.alsebo.sabesdondeesta;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
    int contador = 0;
    String prefEmail;
    private static final String TAG = "MapsActivityPregunta";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences prefs;
    int siPregunta = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_pregunta);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        textoPregunta = (EditText) findViewById(R.id.preguntaEdit);
        textoRespuesta = (TextView) findViewById(R.id.respuestaEdit);

        prefs = getSharedPreferences("donde.esta", Context.MODE_PRIVATE);
        prefEmail = prefs.getString("email", "email");

        db.collection(prefEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            cont = task.getResult().size();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    if(Integer.parseInt(document.getId()) == siPregunta ) {
                                        siPregunta++;
                                    }
                                }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style));

            if (!success) {
                Log.e("TAG", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("TAG", "Can't find style. Error: ", e);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        pregunta = latLng;
        if (contador != 0) {
            markerPregunta.remove();
            markerPregunta = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
            );
        } else {
            markerPregunta = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
            );
            contador = +1;
        }
    }

    @Override
    public void onClick(View view) {

        String questions = textoPregunta.getText().toString();

        if(!questions.isEmpty() && pregunta != null) {
            cont += 1;

            Map<String, Object> jugadorPregunta = new HashMap<>();
            jugadorPregunta.put("pregunta", questions);
            jugadorPregunta.put("latitude", pregunta.latitude);
            jugadorPregunta.put("longitude", pregunta.longitude);

            db.collection(prefEmail).document(String.valueOf(siPregunta))
                    .set(jugadorPregunta)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
            textoRespuesta.setText(pregunta.latitude+" "+pregunta.longitude);
            dialog("La pregunta se ha guardado correctamente\n" + "Numero preguntas = " + cont);
        }else{
            dialog();
        }

    }
    public void dialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Debes hacer una pregunta y marcar tu respuesta");
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

        public void dialog(String mensaje) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mensaje);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cargarLista();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void salir(View view) {
        Intent play = new Intent(this, MainActivityPregunta.class);
        startActivity(play);
        finish();
    }

    public void cargarLista() {
        Intent play = new Intent(this, MainActivityPregunta.class);
        startActivity(play);
        finish();
    }
}