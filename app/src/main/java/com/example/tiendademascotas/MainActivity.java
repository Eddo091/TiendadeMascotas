package com.example.tiendademascotas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
//https://developer.android.com/training/camera/photobasics?hl=es-419#java
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;




public class MainActivity extends AppCompatActivity {
    utilidadescomunes uc;
    DetectarmiInternet di;
    DB miBD;
    Cursor miTiendaon;
    // JSONArray datosJSON;
   // JSONObject jsonObject;
    Integer posicion;
    Tiendon Tien;
    ArrayList<Tiendon> StringArrayList = new ArrayList<Tiendon>();
    ArrayList<Tiendon> copyStringArrayList = new ArrayList<Tiendon>();
     ListView ltsTienda;




/**
 * @Author: USIS017717 VILLEGAS ORTIZ, EDUARDO ISAÍAS
 * @Author: USIS057519 AVILES AVILES, JENNIFER LORENA
 * **/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        FloatingActionButton btnAgregar =(FloatingActionButton)findViewById(R.id.btnAgregarProductoTie);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregar("nuevo", new String[]{});
            }
        });
        obbtenerDatos();
        buscar();
    }

    void buscar() {
        final TextView tempVal = (TextView)findViewById(R.id.txtBuscarProductoTienda);
        tempVal.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    StringArrayList.clear();
                    if (tempVal.getText().toString().trim().length() < 1) {//no hay texto para buscar
                        StringArrayList.addAll(copyStringArrayList);
                    } else {//hacemos la busqueda
                        for (Tiendon Tie : copyStringArrayList) {
                            String producto = Tie.getProducto();
                            if (producto.toLowerCase().contains(tempVal.getText().toString().trim().toLowerCase())) {
                                StringArrayList.add(Tie);
                            }
                        }
                    }
                    ImagenAdaptador adaptadorImagen = new ImagenAdaptador(getApplicationContext(), StringArrayList);
                    ltsTienda.setAdapter(adaptadorImagen);
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.menuprincipal, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        miTiendaon.moveToPosition(info.position);
        menu.setHeaderTitle(miTiendaon.getString(1));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnxAgregarTien:
                agregar("nuevo", new String[]{});
                return true;

            case R.id.mnxModificarTien:
                String[] dataTien = {
                        miTiendaon.getString(0),//id
                        miTiendaon.getString(1),//codigo
                        miTiendaon.getString(2),//producto
                        miTiendaon.getString(3),//precio
                        miTiendaon.getString(4)//urlImg
                         //
                };
                agregar("modificar",dataTien);
                return true;

            case R.id.mnxEliminarTien:
                android.app.AlertDialog eliminar =  eliminarProducto();
                eliminar.show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    android.app.AlertDialog eliminarProducto() {
        android.app.AlertDialog.Builder confirmacion = new android.app.AlertDialog.Builder(MainActivity.this);
        confirmacion.setTitle(miTiendaon.getString(1));
        confirmacion.setMessage("ESTA SEGURO DE ELIMINAR ESTE PRODUCTO?");
        confirmacion.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                miBD.mantenimientoTiendaon("eliminar",new String[]{miTiendaon.getString(0)});
                obbtenerDatos();
                Toast.makeText(getApplicationContext(), "PRODUCTO ELIMINADO CON EXITO",Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        confirmacion.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "ELIMINACION CANCELADA SATISFACTORIAMENTE",Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        return confirmacion.create();
    }

    void obbtenerDatos() {
        miBD = new DB (getApplicationContext(), "", null, 1);
        miTiendaon = miBD.mantenimientoTiendaon("consultar", null);
        if( miTiendaon.moveToFirst() ){ //hay registro en la BD que mostrar
            mostrarDatosTien();
        } else{ //No tengo registro que mostrar.
            Toast.makeText(getApplicationContext(), "NO HAY REGISTROS QUE MOSTRAR",Toast.LENGTH_LONG).show();
            agregar("nuevo",    new String[]{});
        }
    }

    void mostrarDatosTien() {
       StringArrayList.clear();
        ltsTienda = (ListView)findViewById(R.id.ltsTiendaCouchDB);
        do {
            Tien = new Tiendon(miTiendaon.getString(0),miTiendaon.getString(1), miTiendaon.getString(2), miTiendaon.getString(3), miTiendaon.getString(4));
            StringArrayList.add(Tien);
        }while(miTiendaon.moveToNext());
        ImagenAdaptador adaptadorImg = new ImagenAdaptador ( getApplicationContext(), StringArrayList);
        ltsTienda.setAdapter(adaptadorImg);

        copyStringArrayList.clear();//limpiamos
        copyStringArrayList.addAll(StringArrayList);//creamos la copia
        registerForContextMenu(ltsTienda);
    }
    void agregar(String accion, String[] dataProducto) {
        Bundle enviarParametros = new Bundle();
        enviarParametros.putString("accion",accion);
        enviarParametros.putStringArray("data",dataProducto);
        Intent agregarProducto = new Intent(MainActivity.this, agregar_entienda.class);
        agregarProducto.putExtras(enviarParametros);
        startActivity(agregarProducto);
    }

}

class Tiendon {
    String id;
    String codigo;
    String producto;
    String precio;
    String urlImg;

    public Tiendon(String string, String id, String codigo, String producto, String precio) {
        this.id = id;
        this.producto = producto;
        this.codigo = codigo;
        this.precio = precio;
        this.urlImg = urlImg;
    }

    public String getId() {
        return id;
    }
    public void setId(String id)    {
        this.id = id;
    }


    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo= codigo;
    }


    public String getProducto() {
        return producto;
    }
    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getPrecio() {
        return precio;
    }
    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getUrlImg() {
        return urlImg;
    }
    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }
}



