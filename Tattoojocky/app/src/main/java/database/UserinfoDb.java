package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import model.ResponseData;
import model.UserModel;


public class UserinfoDb {




	public static final String ROW_ID = "_id";
	public static final String USER_ID = "userId";
	public static final String USER_NAME = "name";
	public static final String USER_EMAIL = "email";
	public static final String USER_PWD = "password";
	public static final String USER_CONTACT = "contact";
	public static final String USER_GENDER = "gender";
	public static final String USER_DOB = "dob";
	public static final String USER_CITY = "city";

	public static final String USER_STATE = "state";
	public static final String USER_COUNTRY = "country";
	public static final String USER_ABT_ME = "aboutMe";
	public static final String USER_IS_NOTI = "isNotification";
	public static final String USER_JOINDATE = "joinDate";
	public static final String USER_LAST_LOGIN = "last_login";
	public static final String USER_IMG_PATH = "UserImgPath";





	private static final String USERINFO_TABLE = "Userinfo";

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
	public UserinfoDb(Context ctx) {
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
	public UserinfoDb open() throws SQLException {
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



	public long createUserinfo(ResponseData info){

		System.out.println("inserting data into  data base...");
		ContentValues initialValues = new ContentValues();
		initialValues.put(USER_ID, info.getUserId());
		initialValues.put(USER_NAME, info.getName());
		initialValues.put(USER_EMAIL, info.getEmail());
		initialValues.put(USER_PWD, info.getPassword());
		initialValues.put(USER_CONTACT, info.getContact());

		initialValues.put(USER_GENDER, info.getGender());
		initialValues.put(USER_DOB, info.getDob());
		initialValues.put(USER_CITY, info.getCity());
		initialValues.put(USER_STATE, info.getState());
		initialValues.put(USER_COUNTRY, info.getCountry());

		initialValues.put(USER_ABT_ME, info.getAboutMe());
		initialValues.put(USER_IS_NOTI, info.getIsNotification());
		initialValues.put(USER_JOINDATE, info.getJoinDate());
		initialValues.put(USER_LAST_LOGIN, info.getLastLogin());
		if(info.getImage().contains("_thumb")) {
			String img = info.getImage().replace("_thumb","");
			initialValues.put(USER_IMG_PATH, img);
		}else{
			initialValues.put(USER_IMG_PATH, info.getImage());
		}

		return this.mDb.insert(USERINFO_TABLE, null, initialValues);
	}


	public boolean updateUserinfo(ResponseData info){

		System.out.println("inserting data into  data base...");
		ContentValues initialValues = new ContentValues();
		initialValues.put(USER_ID, info.getUserId());
		initialValues.put(USER_NAME, info.getName());
		initialValues.put(USER_EMAIL, info.getEmail());
		initialValues.put(USER_PWD, info.getPassword());
		initialValues.put(USER_CONTACT, info.getContact());

		initialValues.put(USER_GENDER, info.getGender());
		initialValues.put(USER_DOB, info.getDob());
		initialValues.put(USER_CITY, info.getCity());
		initialValues.put(USER_STATE, info.getState());
		initialValues.put(USER_COUNTRY, info.getCountry());

		initialValues.put(USER_ABT_ME, info.getAboutMe());
		initialValues.put(USER_IS_NOTI, info.getIsNotification());
		initialValues.put(USER_JOINDATE, info.getJoinDate());
		initialValues.put(USER_LAST_LOGIN, info.getLastLogin());
		if(info.getImage().contains("_thumb")) {
			String img = info.getImage().replace("_thumb","");
			initialValues.put(USER_IMG_PATH, img);
		}else{
			initialValues.put(USER_IMG_PATH, info.getImage());
		}
		return this.mDb.update(USERINFO_TABLE, initialValues, USER_ID + " = " + info.getUserId(), null) >0;
	}


	public ResponseData getUserinfo() {

		String selectQuery = "SELECT * FROM " + USERINFO_TABLE;
		//ArrayList<UserInfo> nameList = new ArrayList<UserInfo>();
		ResponseData info = null;
		Cursor cur = this.mDb.rawQuery(selectQuery, null);
		if(cur.moveToFirst()){
			do{
				info = new ResponseData();
				info.setUserId(cur.getString(1));
				info.setName(cur.getString(2));
				info.setEmail(cur.getString(3));
				info.setPassword(cur.getString(4));
				info.setContact(cur.getString(5));
				info.setGender(cur.getString(6));
				info.setDob(cur.getString(7));


				info.setCity(cur.getString(8));
				info.setState(cur.getString(9));
				info.setCountry(cur.getString(10));
				info.setAboutMe(cur.getString(11));
				info.setIsNotification(cur.getString(12));
				info.setJoinDate(cur.getString(13));
				info.setLastLogin(cur.getString(14));
				info.setImage(cur.getString(15));


				//nameList.add(info);
			}while(cur.moveToNext());
		}
		return info;


	}



	//Delete all info

	public void deleteAll_UsrDetails(){

		String deletequery ="DELETE from "+USERINFO_TABLE;
		this.mDb.execSQL(deletequery);
	}





	// Getting  Count
	public int getCount() {
		String countQuery  = "SELECT * FROM " + USERINFO_TABLE;
		System.out.println("countQuery......"+countQuery);
		Cursor cursor = this.mDb.rawQuery(countQuery, null);
		int c = cursor.getCount();
		cursor.close();
		// return count
		return c;

	}


}
