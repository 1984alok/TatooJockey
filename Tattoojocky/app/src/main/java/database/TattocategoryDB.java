package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

import model.TattoCatagoryResponse;
import model.UserModel;

/**
 * Created by Alok on 08-10-2016.
 */
public class TattocategoryDB {


    public static final String ROW_ID = "_id";
    public static final String TATTO_CATG_ID = "cateId";
    public static final String TATTO_CATG_NAME = "cateName";
    public static final String TATTO_CATG_PARENT_ID = "parentId";
    public static final String TATTO_CATG_URL = "cateUrl";
    public static final String TATTO_CATG_STATUS = "cateStatus";






    private static final String TATTOO_CATG_TABLE = "TattoCatag";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DBAdapter.DATABASE_NAME, null, DBAdapter.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx
     *            the Context within which to work
     */
    public TattocategoryDB(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the cars database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException
     *             if the database could be neither opened or created
     */
    public TattocategoryDB open() throws SQLException {
        this.mDbHelper = new DatabaseHelper(this.mCtx);
        this.mDb = this.mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * close return type: void
     */
    public void close() {
        this.mDbHelper.close();
    }



    public long createTattooinfo(TattoCatagoryResponse info){

        System.out.println("inserting data into  data base...");
        ContentValues initialValues = new ContentValues();
        initialValues.put(TATTO_CATG_ID, info.getCateId());
        initialValues.put(TATTO_CATG_NAME, info.getCateName());
        initialValues.put(TATTO_CATG_PARENT_ID, info.getParentId());
        initialValues.put(TATTO_CATG_URL, info.getCateUrl());
        initialValues.put(TATTO_CATG_STATUS, info.getCateStatus());
        return this.mDb.insert(TATTOO_CATG_TABLE, null, initialValues);
    }




    public ArrayList<TattoCatagoryResponse> getTattooinfo() {

        String selectQuery = "SELECT * FROM " + TATTOO_CATG_TABLE;
        ArrayList<TattoCatagoryResponse> catagList = new ArrayList<TattoCatagoryResponse>();
        TattoCatagoryResponse info;
        Cursor cur = this.mDb.rawQuery(selectQuery, null);
        if(cur.moveToFirst()){
            do{

                info = new TattoCatagoryResponse();
                info.setCateId(cur.getString(1));
                info.setCateName(cur.getString(2));
                info.setParentId(cur.getString(3));
                info.setCateUrl(cur.getString(4));
                info.setCateStatus(cur.getString(5));
                catagList.add(info);
            }while(cur.moveToNext());
        }
        return catagList;


    }





    //Delete all info

    public void deleteAll_UsrDetails(){

        String deletequery ="DELETE from "+TATTOO_CATG_TABLE;
        this.mDb.execSQL(deletequery);
    }





    // Getting  Count
    public int getCount() {
        String countQuery  = "SELECT * FROM " + TATTOO_CATG_TABLE;
        System.out.println("countQuery......"+countQuery);
        Cursor cursor = this.mDb.rawQuery(countQuery, null);
        int c = cursor.getCount();
        cursor.close();
        // return count
        return c;

    }

}
