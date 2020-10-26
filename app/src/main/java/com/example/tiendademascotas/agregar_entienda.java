package com.example.tiendademascotas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class agregar_entienda extends AppCompatActivity {
    String resp, accion, id, rev;
    utilidadescomunes uc;
    ImageView imgFoto;
    String urlCompletaImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_agregar_entienda );
        imgFoto = findViewById(R.id.imgFoto);
        try {
            FloatingActionButton btnMostrarTienda = findViewById(R.id.btnMostrarTie);
            btnMostrarTienda.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarTienda();
                }
            });
            Button btnGuardarTie = findViewById( R.id.btnMostrarTie );
            btnGuardarTie.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guardarTie();
                }
            } );
            mostrarDatosTienda();
        } catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error al agregar en Tienda Online: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }

/**
 * @Author: USIS017717 VILLEGAS ORTIZ, EDUARDO ISAÍAS
 * @Author: USIS057519 AVILES AVILES, JENNIFER LORENA
 * **/

    }

    void mostrarDatosTienda(){
        try {
            Bundle recibirParametros = getIntent().getExtras();
            accion = recibirParametros.getString("accion");
            
            if (accion.equals("modificar")){
                JSONObject dataTienda = new JSONObject(recibirParametros.getString("dataTienda")).getJSONObject("value");

                TextView tempVal = (TextView)findViewById(R.id.txtCodigoTie);
                tempVal.setText(dataTienda.getString("Codigo"));

                tempVal = (TextView)findViewById(R.id.txtProductoTie);
                tempVal.setText(dataTienda.getString("Producto"));

                tempVal = (TextView)findViewById(R.id.txtPrecioTie);
                tempVal.setText(dataTienda.getString("Precio"));


                id = dataTienda.getString("_id");
                rev = dataTienda.getString("_rev");
                Bitmap imageBitmap = BitmapFactory.decodeFile(urlCompletaImg);
                imgFoto.setImageBitmap(imageBitmap);
            }


        }catch (Exception ex){
            ///
        }


    }
    private void mostrarTienda(){
        Intent mostrarTienda = new Intent( agregar_entienda.this, MainActivity.class);
        startActivity(mostrarTienda);
    }

    private void guardarTie(){
        TextView tempVal = findViewById(R.id.txtCodigoTie);
        String codigoprod = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtProductoTie);
        String nombre = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtPrecioTie);
        String precio = tempVal.getText().toString();
        String[] data= {urlCompletaImg};


        try {
            JSONObject datosTie = new JSONObject();
            datosTie.put("Codigo", codigoprod);
            datosTie.put("Producto", nombre);
            datosTie.put("Precio", precio);
            enviarDatosTie objGuardarTie = new enviarDatosTie();
            objGuardarTie.execute(datosTie.toString());

        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class enviarDatosTie extends AsyncTask<String,String, String> {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... parametros) {
            StringBuilder stringBuilder = new StringBuilder();
            String jsonResponse = null;
            String jsonDatos = parametros[0];
            BufferedReader reader;
            try {
                URL url = new URL(uc.url_mto);
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestProperty("Accept","application/json");

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(jsonDatos);
                writer.close();

                InputStream inputStream = urlConnection.getInputStream();
                if(inputStream==null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                resp = reader.toString();

                String inputLine;
                StringBuffer stringBuffer = new StringBuffer();
                while ((inputLine=reader.readLine())!= null){
                    stringBuffer.append(inputLine+"\n");
                }
                if(stringBuffer.length()==0){
                    return null;
                }
                jsonResponse = stringBuffer.toString();
                return jsonResponse;
            }catch (Exception ex){
                //
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getBoolean("ok")){
                    Toast.makeText(getApplicationContext(), "Datos de Tienda guardado con exito", Toast.LENGTH_SHORT).show();
                    mostrarTienda();
                } else {
                    Toast.makeText(getApplicationContext(), "Error al intentar guardar datos de Tienda", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Error al guardar Tienda: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}