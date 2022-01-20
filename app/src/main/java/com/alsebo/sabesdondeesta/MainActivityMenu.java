package com.alsebo.sabesdondeesta;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivityMenu extends AppCompatActivity implements View.OnClickListener{

    boolean dificil;
    private static final String TAG = "MainActivityMenu";
    FirebaseFirestore db;
    TextView punt;
    String prefEmail;
    Boolean prefModo;
    public static SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Switch dificultad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        prefs = getSharedPreferences("donde.esta",Context.MODE_PRIVATE);
        editor = prefs.edit();

        prefEmail = prefs.getString("email", "email");
        prefModo = prefs.getBoolean("dificultad", false);

        int inicio = prefEmail.indexOf("@");
        String apodo = prefEmail.substring(0,inicio);

        punt = (TextView) findViewById(R.id.textViewPuntos);
        ImageView dedo = (ImageView) findViewById(R.id.dedo);

        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.dedo);
        dedo.startAnimation(hyperspaceJumpAnimation);

        ImageButton play = (ImageButton) findViewById(R.id.monumentosButton);
        play.setOnClickListener(this);
        ImageButton aleatorio = (ImageButton) findViewById(R.id.imageButtonAleatorio);
        aleatorio.setOnClickListener(this);
        ImageButton eeuu = (ImageButton) findViewById(R.id.imageButtonEeuu);
        eeuu.setOnClickListener(this);
        ImageButton espana = (ImageButton) findViewById(R.id.imageButtonEspaña);
        espana.setOnClickListener(this);
        ImageButton europ = (ImageButton) findViewById(R.id.imageButtonEuropa);
        europ.setOnClickListener(this);
        ImageButton reinicio = (ImageButton) findViewById(R.id.reinicioButton);
        reinicio.setOnClickListener(this);
        ImageButton user = (ImageButton) findViewById(R.id.imageButtonUser);
        user.setOnClickListener(this);
        ImageButton nota = (ImageButton) findViewById(R.id.imageButtonNotas);
        nota.setOnClickListener(this);
        TextView apodoUser = (TextView) findViewById(R.id.textUser);
        apodoUser.setText(" Nombre: "+apodo);

        dificultad = (Switch) findViewById(R.id.switchDificultad);

        if (prefModo == true){
            dificultad.setChecked(true);
        }


        dificultad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean pulsado){
                if (pulsado) {
                    confirmar();
                } else {
                    dificil = false;
                    editor.putBoolean("dificultad", dificil);
                    editor.commit();
                }
            }
        });

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("jugador").document(prefEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        punt.setText(document.getLong("puntuacion").toString());
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
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
    public void onClick(View view) {
        Intent maps = new Intent(this, MapsActivity.class);
        switch (view.getId()) {
            case R.id.monumentosButton:
                editor.putString("modo", "questions.monumentos");
                editor.commit();
                startActivity(maps);
                finish();
                break;

            case R.id.imageButtonAleatorio:
                editor.putString("modo", "questions.aleatorio");
                editor.commit();
                startActivity(maps);
                finish();
                break;

            case R.id.imageButtonEeuu:
                editor.putString("modo", "questions.eeuu");
                editor.commit();
                startActivity(maps);
                finish();
                break;

            case R.id.imageButtonEspaña:
                editor.putString("modo", "questions.españa");
                editor.commit();
                startActivity(maps);
                finish();
                break;

            case R.id.imageButtonEuropa:
                editor.putString("modo", "questions.europa");
                editor.commit();
                startActivity(maps);
                finish();
                break;

            case R.id.reinicioButton:
                reinicio();
                break;

            case R.id.imageButtonUser:
                DialogoUsersFragment dialogoUsersFragment = new DialogoUsersFragment();
                dialogoUsersFragment.show(getSupportFragmentManager(),"Buscar Astronauta");
                break;

            case R.id.imageButtonNotas:
                Intent notas = new Intent(this, MainActivityPregunta.class);
                startActivity(notas);
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
        builder.setPositiveButton("¡Aceptar reto!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void reinicio() {
        AlertDialog.Builder builderPregunta = new AlertDialog.Builder(this);
        builderPregunta.setMessage("¿Deseas reiniciar tu puntuacion a 0 y volver a comenzar? ");
        builderPregunta.setTitle("BORRAR PUNTUACION");
        builderPregunta.setPositiveButton("aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                DocumentReference userRef = db.collection("jugador").document(prefEmail);
                userRef
                        .update("puntuacion", 0)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
                ventana();
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

    public void ventana() {
        Intent mnu = new Intent(this, MainActivityMenu.class);
        finish();
        startActivity(mnu);
    }

    public void confirmar() {
        AlertDialog.Builder builderPregunta = new AlertDialog.Builder(this);
        builderPregunta.setMessage("Aqui aumentaras la dificultad, si tu respuesta esta a mas de 25km de la respuesta correcta " +
                "se te restaran 100 puntos.");
        builderPregunta.setTitle("¡MODO SUPER DIFICIL!");
        builderPregunta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dificil= true;
                editor.putBoolean("dificultad", dificil);
                editor.commit();
            }
        });
        AlertDialog dialog = builderPregunta.create();
        dialog.show();
    }
}