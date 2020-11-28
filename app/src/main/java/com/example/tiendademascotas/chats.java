package com.example.tiendademascotas;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;
import java.util.List;

/**@AUTHORS: EDUARDO ISAÍAS VILLEGAS ORTIZ
 * @AUTHORS:Jennifer Lorena Aviles Aviles
 * **/
public class chats extends AppCompatActivity {
    String to = "", user = "", from = "";
    DatabaseReference databaseReference;
    private chatsArrayAdapter chatArrayAdapter;
    TextView txtMsg;
    ListView ltsChats;
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
                tempVal.setText(parametros.getString("user"));
            }
            txtMsg = findViewById(R.id.txtMsg);
            txtMsg.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if( event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER ){
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
                        sendChatMessage(false, txtMsg.getText().toString());
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "Error al intentar enviar el msg: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error al iniciar el chat: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void sendChatMessage(Boolean posicion, String msg){
        try {
            guardarMsgFirebase(msg);

            chatArrayAdapter.add(new chatMessage(posicion, msg));
            txtMsg.setText("");
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error al enviar el msg: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void guardarMsgFirebase(String msg){
        try {
            JSONObject notificacion = new JSONObject();
            notificacion.put("msg", msg);
            notificacion.put("to", to);
            notificacion.put("from", from);
            notificacion.put("to_from", to + "_" + from);

            chats_mensajes chatsMgs = new chats_mensajes(to,from,to + "_" + from,msg);
            String id = databaseReference.push().getKey();
            databaseReference.child(id).setValue(chatsMgs);
        }catch (Exception ex){

        }

    }
}
