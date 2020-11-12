package com.example.tiendademascotas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DatabaseReference mibd;
MyFirebaseInstanceIdServices myFirebaseInstanceIdServices= new MyFirebaseInstanceIdServices();
    JSONArray datosJSON;
    JSONObject jsonObject;
    Integer posicion;
    ArrayList<String> arrayList = new ArrayList<String>();
    ArrayList<String> copyStringArrayList = new ArrayList<String>();
    ArrayAdapter<String> stringArrayAdapter;
    utilidadescomunes uc;
    DetectarmiInternet di;
//Actualizacion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.iniciosesion );
        //Llamar al token
        final String Mitoken = myFirebaseInstanceIdServices.miToken;
        //Guardar los datos en Firebase
        mibd= FirebaseDatabase.getInstance().getReference("Usuarios");
        //Botón  Para Guardar Registros
        Button BtnGuardarUsRegistro= findViewById( R.id.BtnGuardaRegistro );
        BtnGuardarUsRegistro.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            try {
            TextView tempval=findViewById( R.id.txtSesionnombre );

            String nombre= tempval.getText().toString(),
                    id=mibd.push().getKey();
                Usuarios user = new Usuarios( nombre,"pruebacorreo@gmail.com","",Mitoken );

            if ( id!=null){
            mibd.child( id ).setValue(user).addOnSuccessListener( new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText( getApplicationContext(), "Registro guardado con exito", Toast.LENGTH_LONG ).show();
                }
            } );

            } else {
                Toast.makeText( getApplicationContext(), "Error al crear el registro", Toast.LENGTH_LONG ).show();
            }
            } catch (Exception ex) {
                //En caso de error
            Toast.makeText( getApplicationContext(), "Error al intentar guardar Registro"+ex.getMessage(), Toast.LENGTH_LONG ).show();
            }
            }
        } );
    }

    }



