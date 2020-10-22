package com.example.tiendademascotas;

public class  Tienda {
    String id;
    String codigo;
    String producto;
    String precio;
    String urlImg;

    public void tiendaon (String id, String codigo, String producto, String precio, String urlImg) {
        this.id = id;
        this.codigo = codigo;
        this.producto = producto;
        this.precio = precio;
        this.urlImg = urlImg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String Codigo) {
        this.codigo = Codigo;
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

    public void setPrecio(String Precio) {
        this.precio = Precio;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }
}

