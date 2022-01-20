package com.alsebo.sabesdondeesta;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivityAcerca extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_acerca);

        Button atras = (Button) findViewById(R.id.back);
        atras.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}