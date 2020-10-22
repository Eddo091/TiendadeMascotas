package com.example.tiendademascotas;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DB  extends  SQLiteOpenHelper {
    static String nameDB = "db_tiendaon"; /**Declaracion de la BD**/
    static String tblTienda = "CREATE TABLE Tienda(idTienda integer primary key autoincrement, codigo text, producto text, precio text, url text)";

    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context,nameDB,factory,version); /**nameDB= Creacion de la BD en SQLite**/
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tblTienda);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    /**CRUD SQLITE**/
    public Cursor mantenimientoTiendaon(String accion, String[] data){
        SQLiteDatabase sqLiteDatabaseReadable=getReadableDatabase();
        SQLiteDatabase sqLiteDatabaseWritable=getWritableDatabase();
        Cursor cursor = null;
        switch (accion){
            case "consultar":
                cursor=sqLiteDatabaseReadable.rawQuery("SELECT * FROM Tienda ORDER BY producto ASC", null);
                break;
            case "nuevo":
                sqLiteDatabaseWritable.execSQL("INSERT INTO Tienda (codigo,producto,precio,url) VALUES('"+ data[1] +"','"+data[2]+"','"+data[3]+"','"+data[4]+"'");
                break;
            case "modificar":
                sqLiteDatabaseWritable.execSQL("UPDATE Tienda SET codigo='"+ data[1] +"',producto='"+data[2]+"',precio='"+data[3]+"',url='"+data[4]+"' WHERE idTienda='"+data[0]+"'");
                break;
            case "eliminar":
                sqLiteDatabaseWritable.execSQL("DELETE FROM Tienda WHERE idTienda='"+ data[0] +"'");
                break;
        }
        return cursor;
    }
}
