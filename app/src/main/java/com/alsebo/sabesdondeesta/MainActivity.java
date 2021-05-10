package com.alsebo.sabesdondeesta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button jugar = (Button) findViewById(R.id.button);
        jugar.setOnClickListener(this);
        Button acerca = (Button) findViewById(R.id.button2);
        acerca.setOnClickListener(this);

        pref = getPreferences(Context.MODE_PRIVATE);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:// BOTON JUGAR
                Intent play = new Intent(this, MainActivityMenu.class);
                EditText editar = (EditText) findViewById(R.id.editAstronauta);

                //MODO DE ALMACENAMIENTO PARA EL APODO
                SharedPreferences.Editor editor = pref.edit();
                String nombreAstrounata = editar.getText().toString();
                editor.putString("apodo",nombreAstrounata);
                editor.commit();

                startActivity(play);
                break;
            case R.id.button2:// BOTON ACERCA
                Intent acerca = new Intent(this, MainActivityAcerca.class);
                startActivity(acerca);
                break;
            default:
                break;
        }
    }
    public void facebook(View view) {  //ICONO ACCEDER PAGINA WEB FACEBOOK
        Intent facebook = new Intent(Intent.ACTION_VIEW);
        facebook.setData(Uri.parse("https://www.facebook.com"));
        startActivity(facebook);
    }
}