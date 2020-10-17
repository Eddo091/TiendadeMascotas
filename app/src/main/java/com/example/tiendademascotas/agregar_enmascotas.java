package com.example.tiendademascotas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class agregar_enmascotas extends AppCompatActivity {
    String resp, accion, id, rev;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_agregar_enmascotas );
        try {
            FloatingActionButton btnMostrarAmigos = findViewById(R.id.btnMostrarMasc);
            btnMostrarAmigos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarMascota();
                }
            });
            Button btnGuardarMasc = findViewById( R.id.btnGuardarMasc );
            btnGuardarMasc.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guardarMascota();
                }
            } );
            mostrarDatosMascota();
        } catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error al agregar en Tienda Mascota: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    void mostrarDatosMascota(){
        try {
            Bundle recibirParametros = getIntent().getExtras();
            accion = recibirParametros.getString("accion");
            if (accion.equals("modificar")){
                JSONObject dataMascota = new JSONObject(recibirParametros.getString("dataMascota")).getJSONObject("value");

                TextView tempVal = (TextView)findViewById(R.id.txtCodigoMasc);
                tempVal.setText(dataMascota.getString("codigo"));

                tempVal = (TextView)findViewById(R.id.txtNombreMasc);
                tempVal.setText(dataMascota.getString("nombre"));

                tempVal = (TextView)findViewById(R.id.txtMarcaMasc);
                tempVal.setText(dataMascota.getString("direccion"));

                tempVal = (TextView)findViewById(R.id.txtPrecioMasc);
                tempVal.setText(dataMascota.getString("telefono"));

                id = dataMascota.getString("_id");
                rev = dataMascota.getString("_rev");
            }
        }catch (Exception ex){
            ///
        }
    }
    private void mostrarMascota(){
        Intent mostrarMascota = new Intent(agregar_enmascotas.this, MainActivity.class);
        startActivity(mostrarMascota);
    }

    private void guardarMascota(){
        TextView tempVal = findViewById(R.id.txtCodigoMasc);
        String codigoprod = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtNombreMasc);
        String nombre = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtMarcaMasc);
        String marca = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtPrecioMasc);
        String precio = tempVal.getText().toString();

        try {
            JSONObject datosMasc = new JSONObject();
            datosMasc.put("Codigo Producto", codigoprod);
            datosMasc.put("Nombre", nombre);
            datosMasc.put("Marca", marca);
            datosMasc.put("Precio", precio);
            enviarDatosMasc objGuardarMasc = new enviarDatosMasc();
            objGuardarMasc.execute(datosMasc.toString());

        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class enviarDatosMasc extends AsyncTask<String,String, String> {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... parametros) {
            StringBuilder stringBuilder = new StringBuilder();
            String jsonResponse = null;
            String jsonDatos = parametros[0];
            BufferedReader reader;
            try {
                URL url = new URL("http://192.168.0.15:5984/tiendamascotas/");
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
                    Toast.makeText(getApplicationContext(), "Datos de Mascota guardado con exito", Toast.LENGTH_SHORT).show();
                    mostrarMascota();
                } else {
                    Toast.makeText(getApplicationContext(), "Error al intentar guardar datos de Mascota", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Error al guardar Mascota: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}