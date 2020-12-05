package com.example.tiendademascotas;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
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
        final TextView textView = (TextView)itemView.findViewById(R.id.txtTitulo);
        final ImageView imageView = (ImageView)itemView.findViewById(R.id.img);
        try {
            user = datos.get(i);
            textView.setText(user.getNombre());
            Glide.with(context).load(user.getUrlFoto()).into(imageView);
        }catch (Exception ex){
            textView.setText(ex.getMessage());
        }
        return itemView;
    }
}
