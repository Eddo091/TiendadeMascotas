package com.example.tiendademascotas;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIdServices extends FirebaseInstanceIdService {
    public static String mitoken;
    //Creando acceso al token para traer id
    public final String miToken= FirebaseInstanceId.getInstance().getToken();
}
