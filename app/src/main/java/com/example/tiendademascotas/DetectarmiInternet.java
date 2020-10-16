package com.example.tiendademascotas;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class DetectarmiInternet {
    private Context _context;

    public DetectarmiInternet(Context _context) {
        this._context = _context;
    }

    public boolean HayConexcionaInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) _context.getSystemService( Context.CONNECTIVITY_SERVICE );
        if( connectivityManager!=null ){
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if(networkInfos!=null){
                for(int i=0; i<networkInfos.length; i++){
                    if(networkInfos[i].getState()==NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

