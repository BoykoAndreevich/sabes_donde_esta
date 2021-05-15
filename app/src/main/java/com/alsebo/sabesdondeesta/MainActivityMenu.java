package com.alsebo.sabesdondeesta;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.google.firebase.firestore.DocumentSnapshot;

import static com.alsebo.sabesdondeesta.MainActivity.db;
import static com.alsebo.sabesdondeesta.MainActivity.email;

public class MainActivityMenu extends AppCompatActivity implements View.OnClickListener {

    public static long puntos;
    Boolean pregunta = false;
    Boolean dificil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        TextView punt = (TextView) findViewById(R.id.textViewPuntos);
        db.collection("users")
                .document(email.getText().toString()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                puntos= (long) document.get("puntuacion");
                                punt.setText(String.valueOf(document.get("puntuacion")));
                            }
                        } else {
                            Log.w("error", "Error getting documents.", task.getException());
                        }
                    }
                });

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

        Switch dificultad = (Switch) findViewById(R.id.switchDificultad);
        dificultad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean pulsado) {
                if (pulsado){
                    confirmar();
                }else{
                    dificil = false;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent maps = new Intent(this, MapsActivity.class);
        switch (view.getId()) {
            case R.id.monumentosButton:
                //finish();
                maps.putExtra("tipo","monumentos");
                startActivity(maps);
                break;

            case R.id.usersButton:
                if (pregunta == false) {
                    premiun();
                } else {
                    finish();
                    Intent map = new Intent(this, MapsActivityPregunta.class);
                    startActivity(map);
                }
                break;

            case R.id.preguntaButton:
                if (pregunta == false) {
                   premiun();
                } else {

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

    /**public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        onBackPressed();
    }*/

    public void premiun() {
        AlertDialog.Builder builderPregunta = new AlertDialog.Builder(this);
        builderPregunta.setMessage("Este modo esta desactivado, debido a que su uso es exclusivo es para el modo PREMIUN");
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
        builderPregunta.setMessage("Este modo esta desactivado, debido a que su uso es exclusivo es para el modo PREMIUN");
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

    public void confirmar() {
        AlertDialog.Builder builderPregunta = new AlertDialog.Builder(this);
        builderPregunta.setMessage("Este modo");
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