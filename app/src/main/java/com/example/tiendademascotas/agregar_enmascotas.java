package com.example.tiendademascotas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class agregar_enmascotas extends AppCompatActivity {
    ListView ltsMascotas;
    DatabaseReference mibd;
    DatabaseReference mDatabaseReference;
    MyFirebaseInstanceIdServices myFirebaseInstanceIdServices = new MyFirebaseInstanceIdServices();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_agregar_enmascotas );
        mibd = FirebaseDatabase.getInstance().getReference( "MascotasBD" );

        try {

            FloatingActionButton btnMostrarAmigos = findViewById(R.id.btnMostrarMasc);
            btnMostrarAmigos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarDatosMascota();
                }
            });
            Button btnGuardarMasc = findViewById( R.id.btnGuardarMasc );
            btnGuardarMasc.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GuardarDatosMascota();
                }
            } );
            mostrarDatosMascota();
        } catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error al agregar en Tienda Mascota: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    void mostrarDatosMascota() {
        try {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference( "MascotasBD" );

            mDatabaseReference.orderByChild( "token" ).equalTo( myFirebaseInstanceIdServices.miToken ).addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    try {
                        if (snapshot.getChildrenCount() <= 0) {
                            registrarMascota();
                            finish();
                        }
                    } catch (Exception ex) {
                        Toast.makeText( getApplicationContext(), "Error al saber si estoy registrado: " + ex.getMessage(), Toast.LENGTH_LONG ).show();
                        registrarMascota();
                        finish();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            } );
            mDatabaseReference.addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        ArrayList<Usuarios> stringArrayList = new ArrayList<Usuarios>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Usuarios user = dataSnapshot.getValue( Usuarios.class );
                            stringArrayList.add( user );
                        }
                        adaptadorimagenes adaptadorImg = new adaptadorimagenes( getApplicationContext(), stringArrayList );
                        ltsMascotas.setAdapter( adaptadorImg );
                    } catch (Exception ex) {
                        Toast.makeText( getApplicationContext(), "Error al recuperar: " + ex.getMessage(), Toast.LENGTH_LONG ).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            } );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        private void registrarMascota () {
            Intent intent = new Intent( getApplicationContext(), MainActivity.class );
            startActivity( intent );
        }

    public  void GuardarDatosMascota(){
        final String Mitoken = myFirebaseInstanceIdServices.miToken;

        try {
            TextView tempval0 = findViewById( R.id.txtCodigoMasc );
            String Codigo = tempval0.getText().toString();
            TextView tempval1 = findViewById( R.id.txtNombreMasc );
            String nombre = tempval1.getText().toString();
            TextView tempval2 = findViewById( R.id.txtPrecioMasc );
            String precio = tempval2.getText().toString();
            TextView tempval3 = findViewById( R.id.txtMarcaMasc );
            String marca = tempval3.getText().toString(),
                    id = mibd.push().getKey();
            Usuarios user = new Usuarios( Codigo, nombre,precio, marca, Mitoken );

            if (id != null) {
                mibd.child( id ).setValue( user ).addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText( getApplicationContext(), "Registro guardado con exito", Toast.LENGTH_LONG ).show();
                        Intent intent = new Intent( getApplicationContext(), listamascotas.class );
                        startActivity( intent );
                    }
                } ).addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( getApplicationContext(), "Error al crear el registro en la Base de Datos" + e.getMessage(), Toast.LENGTH_LONG ).show();

                    }
                } );

            } else {
                Toast.makeText( getApplicationContext(), "Error al crear el registro", Toast.LENGTH_LONG ).show();
            }
        } catch (Exception ex) {
            //En caso de error
            Toast.makeText( getApplicationContext(), "Error al intentar guardar Registro" + ex.getMessage(), Toast.LENGTH_LONG ).show();
        }
    }
}
