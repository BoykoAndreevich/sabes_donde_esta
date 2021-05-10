package com.alsebo.sabesdondeesta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivityAcerca extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_acerca);

        ImageButton jugar = (ImageButton) findViewById(R.id.imageButton7);
        jugar.setOnClickListener(this);
    }

    //BOTON PARA VOLVER A LA ACTIVIDAD ANTERIOR
    @Override
    public void onClick(View view) {
        Intent home = new Intent(this, MainActivity.class);
        startActivity(home);
    }
}