package com.example.tiendademascotas;

public class Usuarios {
    String UserName, email, contra,urlFoto,Token;
    //polimorfismo
    public  Usuarios(){}

    //parametros
    public Usuarios(String userName, String email, String contra, String urlFoto, String token) {
        this.UserName=userName;
        this.email = email;
        this.contra= contra;
        this.urlFoto = urlFoto;
        this.Token = token;
    }


    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContra() {
        return contra;
    }

    public void setContra(String contra) {
        this.contra = contra;
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
