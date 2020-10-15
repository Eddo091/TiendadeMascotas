package com.example.tiendademascotas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class agregar_enmascotas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_agregar_enmascotas );
        FloatingActionButton btnMostrarAmigos = findViewById(R.id.btnMostrarMasc);
        btnMostrarAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMascota();
            }
        });
    }
    private void mostrarMascota(){
        Intent mostrarAmigos = new Intent(agregar_enmascotas.this, MainActivity.class);
        startActivity(mostrarAmigos);
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
            }catch (Exception ex){
                //
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}