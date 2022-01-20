package com.alsebo.sabesdondeesta;

import static com.alsebo.sabesdondeesta.MainActivityMenu.prefs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class DialogoUsersFragment extends DialogFragment {

    Activity activity;
    Button busca;
    EditText emai;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "DialogoUsersFragment";
    String nombre;


    public DialogoUsersFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_dialogo_users, null);
        builder.setView(v);

        emai = v.findViewById(R.id.username);

        busca = v.findViewById(R.id.buttonBuscar);
        busca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombre = emai.getText().toString();
                if(!nombre.isEmpty()) {
                    db.collection(nombre)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        ventana();
                                        dismiss();
                                    }
                                } else {
                                    dialog("Usuario no encontrado");
                                }
                            }
                        });
                }else {
                    dialog("Esribe un email");
                }
            }
        });
        return builder.create();
    }

    public void ventana(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("modo",nombre);
        editor.commit();
        Intent map = new Intent(activity, MapsActivity.class);
        startActivity(map);
    }

    public void  dialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("UPPPPPSSS!");
        builder.setMessage(msg);
        builder.create();
        builder.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof  Activity){
            this.activity = (Activity) context;
        }else{
            throw new RuntimeException(context.toString() + " must implement OnfragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialogo_users, container, false);
    }
}