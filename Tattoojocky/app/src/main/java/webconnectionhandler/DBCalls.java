package webconnectionhandler;

import android.content.Context;

import java.util.ArrayList;

import database.DBAdapter;
import database.TattocategoryDB;
import model.TattoCatagory;
import model.TattoCatagoryResponse;
import model.TattooInfo;
import model.UserModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.Query;

/**
 * Created by Alok on 26-09-2016.
 */
public class DBCalls{
    private DBAdapter dbAdapter;
    private TattocategoryDB tattocategoryDB;
    private Context mContext;

    private static  DBCalls instance;

    public static DBCalls getInstance(Context mContext){
        if(instance==null){
            instance = new DBCalls(mContext);
        }
        return  instance;
    }

    private  DBCalls(Context mContext){
        this.mContext =mContext;
        dbAdapter = new DBAdapter(mContext);
    }

    public void createTattooCatg(ArrayList<TattoCatagoryResponse> responses){
        try {
            dbAdapter.open();
            tattocategoryDB = new TattocategoryDB(mContext);
            tattocategoryDB.open();
            if(tattocategoryDB.getCount()>0){
                tattocategoryDB.deleteAll_UsrDetails();
            }
            for (int i = 0; i < responses.size(); i++) {
                tattocategoryDB.createTattooinfo(responses.get(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            tattocategoryDB.close();
            dbAdapter.close();
        }
    }


}
