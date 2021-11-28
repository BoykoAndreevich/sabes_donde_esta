package com.alsebo.sabesdondeesta;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivityMenu extends AppCompatActivity implements View.OnClickListener{

    Boolean pregunta = false;
    Boolean dificil;
    private static final String TAG = "MainActivityMenu";
    FirebaseFirestore db;
    public static TextView punt;
    String emaile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        punt = (TextView) findViewById(R.id.textViewPuntos);
        ImageView imagen = (ImageView) findViewById(R.id.radio);

        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.animacion);
        imagen.startAnimation(hyperspaceJumpAnimation);

        ImageButton play = (ImageButton) findViewById(R.id.monumentosButton);
        play.setOnClickListener(this);
        ImageButton user = (ImageButton) findViewById(R.id.usersButton);
        user.setOnClickListener(this);
        ImageButton misPreguntas = (ImageButton) findViewById(R.id.preguntaButton);
        misPreguntas.setOnClickListener(this);
        ImageButton reinicio = (ImageButton) findViewById(R.id.reinicioButton);
        reinicio.setOnClickListener(this);

        FirebaseUser use = FirebaseAuth.getInstance().getCurrentUser();
        if (use != null) {
             emaile = use.getEmail();
        }

        Switch dificultad = (Switch) findViewById(R.id.switchDificultad);
        dificultad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean pulsado){
                if (pulsado) {
                    confirmar();
                } else {
                    dificil = false;
                }
            }
        });

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("jugador").document(emaile);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        punt.setText(document.getLong("puntuacion").toString());
                        pregunta = document.getBoolean("profesor");
                        //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        Intent maps = new Intent(this, MapsActivity.class);
        switch (view.getId()) {
            case R.id.monumentosButton:
                maps.putExtra("tipo","monumentos");
                maps.putExtra("modo",dificil);
                startActivity(maps);
                finish();
                break;

            case R.id.usersButton:
                if (pregunta == false) {
                    premiun();
                } else {

                }
                break;

            case R.id.preguntaButton:
                if (pregunta == false) {
                   premiun();
                } else {
                    Intent map = new Intent(this, MapsActivityPregunta.class);
                    startActivity(map);
                }
                break;

            case R.id.reinicioButton:
                 reinicio();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    public void dialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Eres un astronauta que esta en el espacio exterior en la Estacion Espacial Internacional." +
                " Mientras se descarga la nueva actualización del software y se instale," +
                " Houston desde la base en la tierra te propone un juego por la radio, para pasar el rato." +
                " Serias capaz de ubicar " +
                "todas las localizaciones de Houston? Dependiendo de lo que te acerques a la respuesta," +
                " Houston te dará una puntuación. ¡Suerte astronauta!");
        builder.setTitle("Estación Espacial Internacional");
        builder.setIcon(R.drawable.logo);
        builder.setNegativeButton("¡Aceptar reto!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void premiun() {
        AlertDialog.Builder builderPregunta = new AlertDialog.Builder(this);
        builderPregunta.setMessage("Este modo esta desactivado, debido a que su uso es exclusivo es para el modo PREMIUN. Podras añadir tus propias preguntas " +
                "y respuesta ademas de hacer la de otros usuarios. Para a ver quien se ubica mejor");
        builderPregunta.setTitle("¡ATENCION!");
        builderPregunta.setPositiveButton("¡ACTIVAR PREMIUN!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builderPregunta.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = builderPregunta.create();
        dialog.show();
    }

    public void reinicio() {
        AlertDialog.Builder builderPregunta = new AlertDialog.Builder(this);
        builderPregunta.setMessage("¿Deseas reiniciar tu puntuacion a 0 y volver a comenzar? ");
        builderPregunta.setTitle("BORRAR PUNTUACION");
        builderPregunta.setPositiveButton("aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builderPregunta.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = builderPregunta.create();
        dialog.show();
    }

    public void confirmar() {
        AlertDialog.Builder builderPregunta = new AlertDialog.Builder(this);
        builderPregunta.setMessage("Aqui aumentaras la dificultad, si tu respuesta esta a mas de 25km de la respuesta correcta" +
                "se te restaran 100 puntos. Ademas tendras un tiempo limitado de 10s seg para contestar.");
        builderPregunta.setTitle("¡MODO SUPER DIFICIL!");
        builderPregunta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dificil= true;
            }
        });
        AlertDialog dialog = builderPregunta.create();
        dialog.show();
    }

}