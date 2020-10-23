package com.example.tiendademascotas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.security.Guard;
import java.text.SimpleDateFormat;
import java.util.Date;

public class agregar_entienda extends AppCompatActivity {
    String resp, rev;
    utilidadescomunes uc;
    String accion="nuevo";
    String id ="0";
    ImageView imgFoto;
    String urlCompletaImg;
    Intent takePictureIntent;
    Button btnTienda;
    DB Tiendita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_agregar_entienda );
        imgFoto = findViewById(R.id.imgFoto);
        btnTienda = findViewById(R.id.btnAgregarProductoTie);
        btnTienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarTienda();
            }
        });
        GuardaDatosT();
        muestraDatosT();
        FotoTienda();

    }
    private void mostrarTienda() {
        Intent mostrarTienda = new Intent(agregar_entienda.this, MainActivity.class);
        startActivity(mostrarTienda);
    }

    private void muestraDatosT() {
        try {
            Bundle recibirParametros = getIntent().getExtras();
            accion = recibirParametros.getString("accion");
            if (accion.equals("modificar")){
                String[] data = recibirParametros.getStringArray("dataAmigo");

                id = data[0];

                TextView tempVal = (TextView)findViewById(R.id.txtCodigoTie);
                tempVal.setText(data[1]);

                tempVal = (TextView)findViewById(R.id.txtProductoTie);
                tempVal.setText(data[2]);

                tempVal = (TextView)findViewById(R.id.txtPrecioTie);
                tempVal.setText(data[3]);

                urlCompletaImg = data[4];
                Bitmap imageBitmap = BitmapFactory.decodeFile(urlCompletaImg);
                imgFoto.setImageBitmap(imageBitmap);
            }
        }catch (Exception ex){
            ///
        }
    }
    private void GuardaDatosT() {
        btnTienda = findViewById(R.id.btnAgregarProductoTie);
        btnTienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tempVal = findViewById(R.id.txtCodigoTie);
                String codigo= tempVal.getText().toString();

                tempVal = findViewById(R.id.txtProductoTie);
                String producto = tempVal.getText().toString();

                tempVal = findViewById(R.id.txtPrecioTie);
                String precio = tempVal.getText().toString();

                String[] data = {id,codigo, producto,precio,urlCompletaImg};

                Tiendita = new DB(getApplicationContext(),"", null, 1);
                Tiendita.mantenimientoTiendaon(accion, data);

                Toast.makeText(getApplicationContext(),"REGISTRO HECHO", Toast.LENGTH_LONG).show();
                muestraDatosT();
            }
        });
    }
    private void FotoTienda() {
        imgFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    //guardando la imagen
                    File photoFile = null;
                    try {
                        photoFile =CreaImagen();
                    }catch (Exception ex){}
                    if (photoFile != null) {
                        try {
                            Uri photoURI = FileProvider.getUriForFile(agregar_entienda.this, "steph.rs.controlesbasicos.fileprovider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, 1);
                        }catch (Exception ex){
                            Toast.makeText(getApplicationContext(), "Error Toma Foto: "+ ex.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }
    private File CreaImagen() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "imagen_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == RESULT_OK) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(urlCompletaImg);
                imgFoto.setImageBitmap(imageBitmap);
            }
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    }
