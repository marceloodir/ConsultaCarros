package com.example.marcelo.pesquisacarro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by marcelo on 25/08/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "banco.db";
    public static final String VEICULOS_TABLE_NAME = "veiculos";
    public static final String VEICULOS_COLUMN_ID = "id";
    public static final String VEICULOS_COLUMN_PLACA = "placa";
    public static final String VEICULOS_COLUMN_RENAVAM = "renavam";
    public static final String VEICULOS_COLUMN_STATUS = "status";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("create table %s (%s integer primary key, %s text, %s text, %s text)",
                VEICULOS_TABLE_NAME,
                VEICULOS_COLUMN_ID,
                VEICULOS_COLUMN_PLACA,
                VEICULOS_COLUMN_RENAVAM,
                VEICULOS_COLUMN_STATUS);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s",VEICULOS_TABLE_NAME));
        onCreate(db);
    }

    public void removeAllVeiculos() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("DELETE FROM  %s",VEICULOS_TABLE_NAME));
    }

    public void removeDB(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    public boolean insertVeiculo  (Veiculo veiculo)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("placa", veiculo.getPlaca());
        contentValues.put("renavam", veiculo.getRenavam());
        contentValues.put("status", veiculo.getStatus());
        db.insert("veiculos", null, contentValues);
        return true;
    }

    public Integer deleteVeiculo (Veiculo veiculo)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("veiculos",
                "placa = ? ",
                new String[] { veiculo.getPlaca() });
    }

    public ArrayList<Veiculo> getAllVeiculos()
    {
        ArrayList<Veiculo> array_list = new ArrayList<Veiculo>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from veiculos", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            array_list.add(new Veiculo(
                    res.getString(res.getColumnIndex(VEICULOS_COLUMN_PLACA)),
                    res.getString(res.getColumnIndex(VEICULOS_COLUMN_RENAVAM)),
                    res.getString(res.getColumnIndex(VEICULOS_COLUMN_STATUS))
            ));

            res.moveToNext();
        }
        res.close();
        return array_list;
    }
}
