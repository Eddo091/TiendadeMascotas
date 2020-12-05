package com.example.tiendademascotas;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
public class adaptadorimagenesmasc  extends  BaseAdapter{
    Context context;
    ArrayList<Mascotas> datos;
    LayoutInflater layoutInflater;
    Mascotas user;

    public adaptadorimagenesmasc(Context context, ArrayList<Mascotas> datos){
        this.context = context;
        try {
            this.datos = datos;
        }catch (Exception ex){}
    }
    @Override
    public int getCount() {
        try {
            return datos.size();
        }catch (Exception ex) {
            return 0;
        }
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        layoutInflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        @SuppressLint("ViewHolder") View itemView = layoutInflater.inflate(R.layout.listview_imagenes, viewGroup, false);
        TextView textView = (TextView)itemView.findViewById(R.id.txtTitulo);
        ImageView imageView = (ImageView)itemView.findViewById(R.id.img);
        try {
            user = datos.get(i);
            textView.setText(user.getNombre());
            Bitmap imageBitmap = BitmapFactory.decodeFile(user.getUrlFoto());
            imageView.setImageBitmap(imageBitmap);
        }catch (Exception ex){ }
        return itemView;
    }
}