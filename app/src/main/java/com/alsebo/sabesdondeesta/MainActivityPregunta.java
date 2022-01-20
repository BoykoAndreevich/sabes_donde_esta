package com.alsebo.sabesdondeesta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class MainActivityPregunta extends AppCompatActivity implements View.OnClickListener {

    private ListView listapral;
    private TextView textopral;
    String prefEmail,num;
    private static final String TAG = "MainActivityPrgunta";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences prefs;
    ArrayList<MainActivityPregunta.Encapsulador> datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_prgunta);
        prefs = getSharedPreferences("donde.esta",Context.MODE_PRIVATE);
        prefEmail = prefs.getString("email", "email");

        listapral = findViewById(R.id.listapral);
        textopral = findViewById(R.id.textpral);

        Button misPreguntas = (Button) findViewById(R.id.buttonAñadir);
        misPreguntas.setOnClickListener(this);
        Button borrar = (Button) findViewById(R.id.buttonBorrar);
        borrar.setOnClickListener(this);
        Button sal = (Button) findViewById(R.id.salir);
        sal.setOnClickListener(this);

        datos = new ArrayList<>();
        db.collection(prefEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String num = (String) document.getId();
                                String  pregunta = (String) document.get("pregunta");
                                String  textoPregunta = document.get("latitude")+" "+ document.get("longitude");
                                datos.add(new MainActivityPregunta.Encapsulador(R.drawable.mapa,num,pregunta,textoPregunta));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        cargarLista();
                    }
                });

        listapral.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> pariente, View view, int posicion, long id){
                Encapsulador elegido = (Encapsulador) pariente.getItemAtPosition(posicion);
                String textoelegido = "seleccionado: " + elegido.get_textotitulo();
                textopral.setText(textoelegido);
                num =  elegido.get_Num();
            }
        });
    }

    public void cargarLista(){
        listapral.setAdapter(new Adaptador(this, R.layout.entrada,datos){
            @Override
            public void onEntrada(Object entrada, View view){
                TextView texto_superior_entrada = (TextView) view.findViewById(R.id.text_titulo);
                TextView texto_inferior_entrada = (TextView) view.findViewById(R.id.text_datos);
                ImageView imagen_entrada = (ImageView) view.findViewById(R.id.imagen);
                texto_superior_entrada.setText(((Encapsulador) entrada).get_textotitulo());
                texto_inferior_entrada.setText(((Encapsulador) entrada).get_textocontenido());
                imagen_entrada.setImageResource(((Encapsulador) entrada).get_idimagen());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAñadir:
                Intent preguntas = new Intent(this, MapsActivityPregunta.class);
                startActivity(preguntas);
                finish();
                break;

            case R.id.buttonBorrar:
                dialog();
                break;

            case R.id.salir:
                finish();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    public void dialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Seguro que deseas borrar la pregunta?");
        builder.setTitle("BORRAR PREGUNTA");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(num != null) {
                    db.collection(prefEmail).document(num)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    ventana();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                }else{
                    showAlert("Debes seleccionar una pregunta");
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void ventana(){
        Intent play = new Intent(this, MainActivityPregunta.class);
        startActivity(play);
        finish();
    }

    private void showAlert(String mensaje) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(mensaje);
        builder.setTitle("ERROR");
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static class Encapsulador{

        private int imagen;
        private String num;
        private String titulo;
        private String texto;

        public Encapsulador(int idimagen,String num, String textotitulo, String textocontenido){
            this.imagen = idimagen;
            this.num = num;
            this.titulo = textotitulo;
            this.texto = textocontenido;
        }

        public String get_Num() {
            return num;
        }

        public String get_textotitulo(){
            return titulo;
        }

        public String get_textocontenido(){
            return texto;}

        public int get_idimagen(){
            return imagen;
        }
    }
}