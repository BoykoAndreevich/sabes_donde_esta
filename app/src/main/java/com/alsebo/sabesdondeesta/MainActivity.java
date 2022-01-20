package com.alsebo.sabesdondeesta;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button login, sign, acerca;
    EditText email, password;
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    FirebaseFirestore db;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    AlertDialog mDialog;
    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("donde.esta",Context.MODE_PRIVATE);
        editor = prefs.edit();
        
        acerca = (Button) findViewById(R.id.button2);
        acerca.setOnClickListener(this);
        sign = (Button) findViewById(R.id.signUpBotton);
        sign.setOnClickListener(this);
        login = (Button) findViewById(R.id.loginButton);
        login.setOnClickListener(this);
        email = (EditText) findViewById(R.id.emailEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        mDialog = new SpotsDialog.Builder().setContext(MainActivity.this).setMessage("Conectando...").build();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signUpBotton:
                registro();
                break;

            case R.id.loginButton:
                login();
               break;

            case R.id.button2:
                Intent acerca = new Intent(this, MainActivityAcerca.class);
                startActivity(acerca);
                break;

            default:
                break;
        }
    }

    private void jugar() {
        Intent play = new Intent(this, MainActivityMenu.class);
        startActivity(play);
    }

    private void showAlert(String mensaje) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mensaje);
        builder.setTitle("ERROR");
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void login(){
        String emai = email.getText().toString();
        String clave = password.getText().toString();

        if(!emai.isEmpty() && !clave.isEmpty()){
            if(clave.length() >=6){
                mDialog.show();
                mAuth.signInWithEmailAndPassword(emai, clave)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    editor.putString("email", emai );
                                    editor.commit();
                                    jugar();
                                } else {
                                    showAlert("Se ha producido un error autenticando al usuario");
                                }
                                mDialog.dismiss();
                            }
                        });
            }else{
                showAlert("La contraseña debe tener 6 caracteres");
            }
        }else{
            showAlert("La contraseña y el email son obligatorios");
        }
    }

    private void registro(){
        String emai = email.getText().toString();
        String clave = password.getText().toString();

        if(!emai.isEmpty() && !clave.isEmpty()){
            if(clave.length() >=6){
        mAuth.createUserWithEmailAndPassword(emai, clave)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> jugador = new HashMap<>();
                            jugador.put("ema", emai);
                            jugador.put("puntuacion", 0);

                            editor.putString("email", emai );
                            editor.commit();

                            db.collection("jugador").document(emai)
                                    .set(jugador)
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
                            jugar();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            showAlert("ERROR al autenticarte");
                        }
                    }
                });
            }else{
                showAlert("La contraseña debe tener 6 caracteres");
            }
        }else{
            showAlert("La contraseña y el email son obligatorios");
        }
    }
}