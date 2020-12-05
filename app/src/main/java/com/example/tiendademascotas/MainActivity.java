package com.example.tiendademascotas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import android.os.Environment;
import android.provider.MediaStore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/**@AUTHORS: EDUARDO ISAÍAS VILLEGAS ORTIZ
 * @AUTHORS:Jennifer Lorena Aviles Aviles
 * **/
public class MainActivity extends AppCompatActivity {


    DatabaseReference mibd;
    MyFirebaseInstanceIdServices myFirebaseInstanceIdServices = new MyFirebaseInstanceIdServices();
    ImageView imgfoto;
    Intent takePictureIntent;
    String urlCompletaImg;
    String urlCompletaImgFirestore;
    String miToken;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        miToken = myFirebaseInstanceIdServices.miToken;

        try {
            setContentView( R.layout.iniciosesion);
        } catch (Exception ex) {Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();}

        imgfoto=findViewById(R.id.imgFoto);

        /**Pedir permiso si es android 6.0 en adelante**/
        imgfoto.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view== imgfoto){

                        if (ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {


                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            } else {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        225);
                            }


                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                    Manifest.permission.CAMERA)) {

                            } else {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        226);
                            }
                        } else {
                            tomarFoto();
                        }
                    }
                }

        } );


/**BtnGuardar**/

        try {

            /**Llamar al token**/
            final String Mitoken = myFirebaseInstanceIdServices.miToken;
            /**Guardar datos en Firebase**/
            mibd = FirebaseDatabase.getInstance().getReference( "Usuarios" );
            /**Boton Para Guardar Registros**/
            Button BtnGuardarUsRegistro = findViewById( R.id.BtnGuardaRegistro );
            BtnGuardarUsRegistro.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                    subirFilestore();
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
    /**Subir datos en Firebase**/
        private void subirFilestore(){
            Toast.makeText(getApplicationContext(), "Te informaremos cuando la foto se suba a firestoire",Toast.LENGTH_SHORT).show();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            Uri file = Uri.fromFile(new File(urlCompletaImg));
            final StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());

            final UploadTask uploadTask = riversRef.putFile(file);
            StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = uploadTask.addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText( getApplicationContext(), "Fallo el intento de subir la foto a firestore: " + e.getMessage(), Toast.LENGTH_LONG ).show();

                }
            } );
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Listo se subio la foto a firestore",Toast.LENGTH_SHORT).show();
                    Task<Uri> downloadUri = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            return riversRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()) {
                                urlCompletaImgFirestore = task.getResult().toString();
                                guardarDatosFirebase();
                            }
                        }
                    });
                }
            });
        }
        /**Guardar datos en Firebase**/
        private void guardarDatosFirebase(){
            TextView tempval = findViewById( R.id.txtSesionnombre );
            TextView tempval2 = findViewById( R.id.txtcorreo );
            TextView tempval3 = findViewById( R.id.txtcontra );
            String email = tempval2.getText().toString();
            String contra = tempval3.getText().toString();
            String nombre = tempval.getText().toString(),

                    id = mibd.push().getKey();
            Usuarios user = new Usuarios( nombre, email,contra, urlCompletaImg, urlCompletaImgFirestore, miToken );

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
                        } catch (Exception ex) { }
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




