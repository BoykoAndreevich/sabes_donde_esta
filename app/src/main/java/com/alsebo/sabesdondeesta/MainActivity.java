package com.alsebo.sabesdondeesta;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAnalytics mFirebaseAnalytics;
    public static EditText email;
    EditText password;
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static Map<String, Object> user = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("message", "Integración de Firebase completa");
        mFirebaseAnalytics.logEvent("InitScreen", bundle);

        Button acerca = (Button) findViewById(R.id.button2);
        acerca.setOnClickListener(this);
        Button sign = (Button) findViewById(R.id.signUpBotton);
        sign.setOnClickListener(this);
        Button login = (Button) findViewById(R.id.loginButton);
        login.setOnClickListener(this);

        email = (EditText) findViewById(R.id.emailEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signUpBotton:
                if(email.getText()!=null && password.getText()!=null  ){
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        jugar();
                                    }else {
                                        showAlert();
                                    }
                                }
                            });
                }
                user.put("AnadirPregunta", false);
                user.put("puntuacion", 0);
                db.collection("users")
                        .document(email.getText().toString()).set(user)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("error", "Error adding document", e);
                            }
                        });
                break;

            case R.id.loginButton:
                if(email.getText()!=null && password.getText()!=null  ){
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        jugar();
                                    }else {
                                        showAlert();
                                    }
                                }
                            });
                }
                break;

            case R.id.button2:// BOTON ACERCA
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

    private void showAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Se ha producido un error autenticando al usuario");
        builder.setTitle("ERROR");
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**public void instagram(View view) {  //ICONO ACCEDER PAGINA WEB FACEBOOK
        Intent facebook = new Intent(Intent.ACTION_VIEW);
        facebook.setData(Uri.parse("https://www.facebook.com"));
        startActivity(facebook);
    }*/
}