package com.example.tiendademascotas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    DatabaseReference mibd;
    MyFirebaseInstanceIdServices myFirebaseInstanceIdServices = new MyFirebaseInstanceIdServices();
    Integer posicion;
    ImageView imgfoto;
    Intent takePictureIntent;
    String urlCompletaImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.iniciosesion );
        imgfoto=findViewById(R.id.imgFoto);
        tomarFoto();
        try {
            //Llamar al token
            final String Mitoken = myFirebaseInstanceIdServices.miToken;
            //Guardar los datos en Firebase
            mibd = FirebaseDatabase.getInstance().getReference( "Usuarios" );
            //Botón  Para Guardar Registros
            Button BtnGuardarUsRegistro = findViewById( R.id.BtnGuardaRegistro );
            BtnGuardarUsRegistro.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        TextView tempval = findViewById( R.id.txtSesionnombre );

                        String nombre = tempval.getText().toString(),
                                id = mibd.push().getKey();
                        Usuarios user = new Usuarios( nombre, "pruebacorreo@gmail.com", urlCompletaImg, Mitoken );

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
            });


            } catch(Exception ex){
                Toast.makeText( getApplicationContext(), "Error: " + ex.getMessage(), Toast.LENGTH_LONG ).show();


            }
        }

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
                    } catch (Exception ex) {
                    }
                    if (photoFile != null) {
                        try {
                            Uri photoURI = FileProvider.getUriForFile( MainActivity.this, "com.example.prueba.fileprovider", photoFile );
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
                Bitmap imageBitmap = BitmapFactory.decodeFile(urlCompletaImg);
                imgfoto.setImageBitmap(imageBitmap);
            }
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile () throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "imagen_" + timeStamp + "_";
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




