package com.example.tiendademascotas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
/**@AUTHORS: EDUARDO ISAÍAS VILLEGAS ORTIZ
 * @AUTHORS:Jennifer Lorena Aviles Aviles
 * **/
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
        ltsMascotas = findViewById( R.id.ltsTiendaMascotaFireBase );
        mostrarlistadoMascotas();
        ltsMascotas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putString("User", datosJSONArray.getJSONObject(position).getString("User"));
                    bundle.putString("to", datosJSONArray.getJSONObject(position).getString("to"));
                    bundle.putString("from", datosJSONArray.getJSONObject(position).getString("from"));

                    Intent intent = new Intent(getApplicationContext(), chats.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Error al seleccionar el usuario a chatear: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void mostrarlistadoMascotas() {

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
                        datosJSONObject = new JSONObject();
                        datosJSONObject.put("user", user.getUserName());
                        datosJSONObject.put("to", user.getToken());
                        datosJSONObject.put("from", myFirebaseInstanceIdService.miToken);
                        datosJSONArray.put(datosJSONObject);
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

