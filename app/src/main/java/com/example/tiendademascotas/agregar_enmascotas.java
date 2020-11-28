package com.example.tiendademascotas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class agregar_enmascotas extends AppCompatActivity {
    ListView ltsMascotas;
    DatabaseReference mibd;
    DatabaseReference mDatabaseReference;
    ImageView imgfoto;
    Intent takePictureIntent;
    String urlCompletaImg;
    MyFirebaseInstanceIdServices myFirebaseInstanceIdServices = new MyFirebaseInstanceIdServices();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_agregar_enmascotas );
        imgfoto=findViewById(R.id.imgFoto);

        /**Pedir permiso si es android 6.0 en adelante**/
        imgfoto.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view== imgfoto){

                    if (ContextCompat.checkSelfPermission(agregar_enmascotas.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(agregar_enmascotas.this,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(agregar_enmascotas.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        } else {
                            ActivityCompat.requestPermissions(agregar_enmascotas.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    225);
                        }


                        if (ActivityCompat.shouldShowRequestPermissionRationale(agregar_enmascotas.this,
                                Manifest.permission.CAMERA)) {

                        } else {
                            ActivityCompat.requestPermissions(agregar_enmascotas.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    226);
                        }
                    } else {
                        tomarFoto();
                    }
                }
            }

        } );

        mibd = FirebaseDatabase.getInstance().getReference( "Mascotas" );

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
            mDatabaseReference = FirebaseDatabase.getInstance().getReference( "Mascotas" );

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
                        ArrayList<Mascotas> stringArrayList = new ArrayList<Mascotas>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Mascotas user = dataSnapshot.getValue( Mascotas.class );
                            stringArrayList.add( user );
                        }
                        adaptadorimagenesmasc adaptadorImg = new adaptadorimagenesmasc( getApplicationContext(), stringArrayList );
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
        Button BtnGuardarUsRegistro = findViewById( R.id.btnGuardarMasc );
        BtnGuardarUsRegistro.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    Mascotas user = new Mascotas( Codigo,nombre,precio,marca,urlCompletaImg,Mitoken);
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
        );}



    /**Tomar la foto**/
    void tomarFoto () {
        imgfoto.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
                if (takePictureIntent.resolveActivity( getPackageManager() ) != null) {
                    //guardando la imagen
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (Exception ex) { }
                    if (photoFile != null) {
                        try {
                            Uri photoURI = FileProvider.getUriForFile( agregar_enmascotas.this, "com.example.prueba.fileprovider", photoFile );
                            takePictureIntent.putExtra( MediaStore.EXTRA_OUTPUT, photoURI );
                            startActivityForResult( takePictureIntent, 1 );
                        } catch (Exception ex) {
                            Toast.makeText( getApplicationContext(), "Error Toma Foto: " + ex.getMessage(), Toast.LENGTH_LONG ).show();
                        }
                    }
                }
            }
        } );
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == 1 && resultCode == RESULT_OK) {
                Bitmap imageBitmap= BitmapFactory.decodeFile(urlCompletaImg);
                imgfoto.setImageBitmap(imageBitmap);

            }
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile () throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "imagen" + timeStamp + "_";
        File storageDir = getExternalFilesDir( Environment.DIRECTORY_PICTURES);
        if( storageDir.exists()==false ){
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        urlCompletaImg = image.getAbsolutePath();
        return image;
    }
}
