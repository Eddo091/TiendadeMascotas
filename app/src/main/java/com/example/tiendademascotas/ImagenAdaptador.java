package com.example.tiendademascotas;
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
public class ImagenAdaptador extends BaseAdapter {
    Context context;
    ArrayList<Tiendon> datos;
    LayoutInflater layoutInflater;
    Tiendon Tienda;

    public ImagenAdaptador(Context context, ArrayList<Tiendon> datos){
        this.context = context;
        try {
            this.datos=datos;
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
        View itemView = layoutInflater.inflate(R.layout.listview_imagenes, viewGroup, false);
        TextView textView = (TextView)itemView.findViewById(R.id.txtTitulo);
        ImageView imageView = (ImageView)itemView.findViewById(R.id.img);
        try {
            Tienda= datos.get(i);
            textView.setText(Tienda.getProducto());
            Bitmap imageBitmap = BitmapFactory.decodeFile(Tienda.getUrlImg());
            imageView.setImageBitmap(imageBitmap);
        }catch (Exception ex){ }
        return itemView;
    }


}