package com.alsebo.sabesdondeesta;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivityMenu extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);


        TextView nom = (TextView) findViewById(R.id.textUser);
        String valor2 = MainActivity.pref.getString("apodo", "Astronauta");
        nom.setText(valor2);

        EditText punt = (EditText) findViewById(R.id.editTextNumber);
        ImageView imagen = (ImageView) findViewById(R.id.radio);
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.animacion);
        imagen.startAnimation(hyperspaceJumpAnimation);

        ImageButton play = (ImageButton) findViewById(R.id.imageButton2);
        play.setOnClickListener(this);

        int valor= MainActivity.pref.getInt("nuevosPuntos", 0);

        punt.setText(String.valueOf(valor));
    }

    @Override
    public void onClick(View view) {
        finish();
        Intent maps = new Intent(this, MapsActivity.class);
        startActivity(maps);

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
}