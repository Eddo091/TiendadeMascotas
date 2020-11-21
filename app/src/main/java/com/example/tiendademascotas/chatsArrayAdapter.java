package com.example.tiendademascotas;

import android.content.Context;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**@AUTHORS: EDUARDO ISAÍAS VILLEGAS ORTIZ
 * @AUTHORS:Jennifer Lorena Aviles Aviles
 * **/
public class chatsArrayAdapter extends ArrayAdapter {
    private Context context;
    private List<chatMessage> chatMessageList = new ArrayList<>();
    private TextView chatText;

    public chatsArrayAdapter(@NonNull Context context, int resourceId) {
        super(context, resourceId);
        this.context = context;
    }
    public void add(chatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }
    public int getCount(){
        return chatMessageList.size();
    }
    public chatMessage getItem(int index){
        return chatMessageList.get(index);
    }
    public View getView(int posicion, View view, ViewGroup viewGroup){
        chatMessage objChatMessage = getItem(posicion);
        View fila = view;
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(objChatMessage.posicion){
            fila = layoutInflater.inflate(R.layout.msgizquierdo, viewGroup, false);
        } else{
            fila = layoutInflater.inflate(R.layout.msgderecho, viewGroup, false);
        }
        chatText = fila.findViewById(R.id.lblmsgd);
        chatText.setText(objChatMessage.message);
        return fila;
    }
}
