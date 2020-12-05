package com.example.tiendademascotas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**@AUTHORS: EDUARDO ISAÍAS VILLEGAS ORTIZ
 * @AUTHORS:Jennifer Lorena Aviles Aviles
 * **/
public class chats extends Activity {
    String to = "", user = "", from = "",msg="",urlFoto="",urlFotoFirestore="";
    DatabaseReference databaseReference;
    private chatsArrayAdapter chatArrayAdapter;
    TextView txtMsg;
    ListView ltsChats;

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(MyFirebaseMessagingService.DISPLAY_MESSAGE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(notificacionPush, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificacionPush);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats);

        try {
            databaseReference = FirebaseDatabase.getInstance().getReference("chats");

            TextView tempVal = findViewById(R.id.lblToChats);
            ImageView imgAtras = findViewById(R.id.imgAtras);

            imgAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), listamascotas.class);
                    startActivity(intent);
                }
            });
            Bundle parametros = getIntent().getExtras();
            if (parametros.getString("to") != null) {
                to = parametros.getString("to");
                from = parametros.getString("from");
                user = parametros.getString("user");
                urlFoto = parametros.getString("urlFoto");
                urlFotoFirestore = parametros.getString("urlFotoFirestore");
                tempVal.setText(parametros.getString("user"));
            }
            txtMsg = findViewById(R.id.txtMsg);
            txtMsg.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if( event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER ){
                        guardarMsgFirebase(txtMsg.getText().toString());
                        sendChatMessage(true, txtMsg.getText().toString());
                    }
                    return false;
                }
            });
            ltsChats = findViewById(R.id.ltsChats);

            chatArrayAdapter = new chatsArrayAdapter(getApplicationContext(), R.layout.msgizquierdo);
            ltsChats.setAdapter(chatArrayAdapter);

            Button btnEnviar = findViewById(R.id.btnEnviar);
            btnEnviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        guardarMsgFirebase(txtMsg.getText().toString());
                        sendChatMessage(false, txtMsg.getText().toString());
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "Error al intentar enviar el msg: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            mostrarFoto();
            historialMsg();
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error al iniciar el chat: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarFoto(){
        ImageView imgFoto = findViewById(R.id.imgFoto);
        Glide.with(getApplicationContext()).load(urlFotoFirestore).into(imgFoto);
    }
    private void historialMsg(){
        databaseReference = FirebaseDatabase.getInstance().getReference("chats");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    if( snapshot.getChildrenCount()>0 ){
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if( (dataSnapshot.child("de").getValue().equals(from) && dataSnapshot.child("para").getValue().equals(to)) || (dataSnapshot.child("de").getValue().equals(to) && dataSnapshot.child("para").getValue().equals(from))){
                                sendChatMessage(dataSnapshot.child("para").getValue().equals(from), dataSnapshot.child("msg").getValue().toString());
                            }
                        }
                    }
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Error al recuperar historial de msgs: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void sendChatMessage(Boolean posicion, String msg){
        try {


            chatArrayAdapter.add(new chatMessage(posicion, msg));
            txtMsg.setText("");
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error al enviar el msg: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void guardarMsgFirebase(String msg){
        try {

            JSONObject data = new JSONObject();
            data.put("para", to);
            data.put("de", from);
            data.put("msg", msg);
            data.put("user", user);

            JSONObject notificacion = new JSONObject();
            notificacion.put("title","Mensaje de "+ user);
            notificacion.put("body",msg);


            JSONObject miData = new JSONObject();
            miData.put("notification", notificacion);
            miData.put("data", data);
            miData.put("to", to);

            enviarDatos objEnviar = new enviarDatos();
            objEnviar.execute(miData.toString());

            chats_mensajes chatsMgs = new chats_mensajes(to,from,to + "_" + from,msg);
            String id = databaseReference.push().getKey();
            databaseReference.child(id).setValue(chatsMgs);
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error al enviar msg a firebase: "+ ex.getMessage(), Toast.LENGTH_LONG).show();

        }

    }
    private class enviarDatos extends AsyncTask<String,String,String> {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... params) {
            StringBuilder result = new StringBuilder();

            String JsonResponse = null;
            String JsonDATA = params[0];
            BufferedReader reader = null;

            try {
                //conexion al servidor...
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Authorization", "key=AAAAdtr2Wt0:APA91bElu6u80uT3nPDYTt5Azox2a0ivcVtMgsLZsCLgiSB6jA3c1GWXChY9VzULNCFEgnW6-8pYKwJCGqHPFlttF6xNF2dY1wHepqnDGavzgCcNcskq1XWWgqibYOh4xphXGB1xwckn");

                //set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                writer.close();

                // json data
                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                StringBuffer buffer = new StringBuffer();
                while ((inputLine = reader.readLine()) != null) {
                    buffer.append(inputLine + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                JsonResponse = buffer.toString();
                return JsonResponse;

            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                if( s!=null ) {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("success") <= 0) {
                        Toast.makeText(getApplicationContext(), "Error al enviar mensaje", Toast.LENGTH_LONG).show();
                    }
                }
            }catch(Exception ex){
                Toast.makeText(getApplicationContext(), "Error al enviar a la red: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
    private BroadcastReceiver notificacionPush = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WakeLocker.acquire(getApplicationContext());

            msg = intent.getStringExtra("msg");
            to = intent.getStringExtra("from");
            from = intent.getStringExtra("to");
            sendChatMessage(true, msg);
            WakeLocker.release();
        }
    };
}
