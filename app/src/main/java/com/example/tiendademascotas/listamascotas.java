package com.example.tiendademascotas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class listamascotas extends AppCompatActivity {
    DatabaseReference mDatabaseReference;
    ListView ltsMascotas;
    JSONArray datosJSONArray = new JSONArray();
    JSONObject datosJSONObject;
    MyFirebaseInstanceIdServices myFirebaseInstanceIdService = new MyFirebaseInstanceIdServices();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        mostrarlistadoMascotas();
    }

    private void mostrarlistadoMascotas() {
        ltsMascotas = findViewById( R.id.ltsTiendaMascotaFireBase );
        mDatabaseReference = FirebaseDatabase.getInstance().getReference( "usuarios" );
        mDatabaseReference.orderByChild( "token" ).equalTo( myFirebaseInstanceIdService.miToken ).addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    if (snapshot.getChildrenCount() <= 0) {
                        registrarUsuario();
                        finish();
                    }
                } catch (Exception ex) {
                    Toast.makeText( getApplicationContext(), "Error al saber si estoy registrado: " + ex.getMessage(), Toast.LENGTH_LONG ).show();
                    registrarUsuario();
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    ArrayList<Usuarios> stringArrayList = new ArrayList<Usuarios>();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Usuarios user = dataSnapshot.getValue(Usuarios.class);
                        stringArrayList.add(user);
                    }
                    adaptadorimagenes adaptadorImg = new adaptadorimagenes(getApplicationContext(), stringArrayList);
                    ltsMascotas.setAdapter(adaptadorImg);
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Error al recuperar: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
        private void registrarUsuario () {
            Intent intent = new Intent( getApplicationContext(), MainActivity.class );
            startActivity( intent );
        }

    }

