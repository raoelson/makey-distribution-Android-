package com.example.makeyservice.makeydistribution.APISqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.makeyservice.makeydistribution.Model.GeoAdresse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makeyservice on 20/06/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "makeyDistribution.db";

    // Contacts table name
    private static final String TABLE_ADRESSE = "geoAdresse";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_ADRESSE = "adresse";
    private static final String KEY_CP = "cp";
    private static final String KEY_HOUSENUMBER = "housenumber";
    private static final String KEY_CHEMIN = "chemin";
    private static final String KEY_SCORE = "score";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_ADRESSE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ADRESSE + " TEXT,"
                + KEY_CP + " INTEGER," + KEY_HOUSENUMBER + " TEXT," + KEY_CHEMIN + " TEXT,"
                + KEY_SCORE + " TEXT )";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADRESSE);

        // Create tables again
        //onCreate(db);
    }

    public void addGeoAdresse(GeoAdresse geoAdresse) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ADRESSE, geoAdresse.getStreet());
        values.put(KEY_CP, geoAdresse.getPostcode());
        values.put(KEY_SCORE, geoAdresse.getScore()*100);
        values.put(KEY_HOUSENUMBER, geoAdresse.getHousenumber());
        values.put(KEY_CHEMIN, geoAdresse.getChemin());
        // Inserting Row
        db.insert(TABLE_ADRESSE, null, values);
        db.close(); // Closing database connection
    }

    public List<GeoAdresse> getAllGeoAdresse() {
        List<GeoAdresse> adressesList = new ArrayList<GeoAdresse>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ADRESSE + " ORDER BY "+KEY_ID+" DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GeoAdresse geoAdresse = new GeoAdresse();
                geoAdresse.setId(Integer.parseInt(cursor.getString(0)));
                geoAdresse.setStreet(cursor.getString(1));
                geoAdresse.setPostcode(Integer.parseInt(cursor.getString(2)));
                geoAdresse.setScore(Double.valueOf(cursor.getString(5)));
                if(cursor.getString(3) != null){
                    geoAdresse.setHousenumber(Integer.parseInt(cursor.getString(3)));
                }
                geoAdresse.setChemin(cursor.getString(4));
                adressesList.add(geoAdresse);
            } while (cursor.moveToNext());
        }

        // return adressesList list
        return adressesList;
    }


    public int deleteGeoAdresse(GeoAdresse adresse) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ADRESSE, KEY_ID + " = ?",
                new String[] { String.valueOf(adresse.getId()) });
        db.close();
        return 1;
    }

    public GeoAdresse getGeoAdresse(Integer id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ADRESSE, new String[] {KEY_CHEMIN,KEY_SCORE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null,null,null);
        if (cursor != null)
            cursor.moveToFirst();
        GeoAdresse geoAdresse = new GeoAdresse();
        geoAdresse.setChemin(cursor.getString(0));
        // return contact
        return geoAdresse;
    }
}