package com.example.tiendademascotas;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class chats extends AppCompatActivity {
    String to = "", miToken="";
    DatabaseReference databaseReference;
    private chatsArrayAdapter chatArrayAdapter;
    TextView txtMsg;
    ListView ltsChats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats);

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
        if( parametros.getString("to")!=null ){
            to = parametros.getString("to");
            tempVal.setText( parametros.getString("Mascota") );
        }
        txtMsg = findViewById(R.id.txtMsg);
        ltsChats.findViewById(R.id.ltsChats);

        chatArrayAdapter = new chatsArrayAdapter(getApplicationContext(), R.layout.msgizquierdo);
        ltsChats.setAdapter(chatArrayAdapter);

        Button btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    sendChatMessage(false, txtMsg.getText().toString());
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Error al intentar enviar el msg: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
                }
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
}
