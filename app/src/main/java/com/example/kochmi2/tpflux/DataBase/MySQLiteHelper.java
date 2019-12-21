package com.example.kochmi2.tpflux.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**

 * Derive de la classe SQliteOpenHelper qui gere la creation
 * et mise a jour de la bdd
 */


public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_FLUXS = "fluxs";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FLUX = "flux";
    private static  final String DATABASE_CREATE = "create table "+ TABLE_FLUXS+ "( "
            +COLUMN_ID +
            " integer primary key autoincrement, " + COLUMN_FLUX +
            " text not null);";

    public MySQLiteHelper(Context context){
        super(context, "flux.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase maBase) {
        maBase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase maBase, int oldVersion, int newVersion) {
        maBase.execSQL("DROP TABLE IF EXISTS " + "flux");
        onCreate(maBase);
    }

}
