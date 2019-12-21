package com.example.kochmi2.tpflux.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import static com.example.kochmi2.tpflux.DataBase.MySQLiteHelper.COLUMN_FLUX;
import static com.example.kochmi2.tpflux.DataBase.MySQLiteHelper.TABLE_FLUXS;


/*Classe controleur. Contient les differentes methodes qui vont interargir avec la base de donnees.
  C'est le Data Access Object.
*/

public class FluxsDataSource {

    //Database fields
    private Context context;
    private SQLiteDatabase maBase;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { COLUMN_FLUX };

    public FluxsDataSource(Context context){

        this.context = context;
        dbHelper = new MySQLiteHelper(context);
    }

    //Ouverture de la base:
    public void open() throws SQLException {
        maBase = dbHelper.getWritableDatabase();
    }

    //Fermeture de la base:
    public void close(){
        dbHelper.close();
    }


    //Inserer un element
    public void createFluxs(String flux){
        ContentValues values = new ContentValues();
        values.put(COLUMN_FLUX, flux);
        long insertId = maBase.insert(MySQLiteHelper.TABLE_FLUXS,
                null, values);

        //return insertId;
    }

    //Effacer un element
    public void deleteFlux(){

        maBase.delete(MySQLiteHelper.TABLE_FLUXS,
                null,
                null);
    }

    //Obtain Strings from the database table
    public ArrayList<String> getData(){
        allColumns = new String[] {COLUMN_FLUX};
        Cursor c = maBase.query(TABLE_FLUXS, allColumns, null, null, null, null, null);
        ArrayList<String> data = new ArrayList<>();

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            data.add(c.getString(c.getColumnIndex(COLUMN_FLUX)));
        }

        return data;
    }

}//Fin de FluxsDataSource
