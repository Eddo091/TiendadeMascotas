package com.example.tiendademascotas;

public class Mascotas {
    String codigo, nombre, precio, marca, urlFoto,Token;
    public Mascotas (){}
    public Mascotas(String Codigo, String Nombre, String Precio,String Marca, String urlFoto, String token){
        this.codigo=Codigo;
        this.nombre= Nombre;
        this.precio= Precio;
        this.urlFoto = urlFoto;
        this.Token = token;

    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
