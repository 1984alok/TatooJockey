package database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {

	public static final String DATABASE_NAME = "Bsds"; //$NON-NLS-1$

	public static final int DATABASE_VERSION = 1;


	// DATABASE ENTRY

	private static final String CREATE_TABLE_USERINFO =
			"create table Userinfo (_id integer primary key autoincrement, " //$NON-NLS-1$
					+ UserinfoDb.USER_ID+ " TEXT," //$NON-NLS-1$
					+ UserinfoDb.USER_NAME+ " TEXT," //$NON-NLS-1$
					+ UserinfoDb.USER_EMAIL+ " TEXT," //$NON-NLS-1$
					+ UserinfoDb.USER_PWD+ " TEXT," //$NON-NLS-1$
					+ UserinfoDb.USER_CONTACT+ " TEXT," //$NON-NLS-1$
					+ UserinfoDb.USER_GENDER+ " TEXT," //$NON-NLS-1$
					+ UserinfoDb.USER_DOB+ " TEXT," //$NON-NLS-1$
					+ UserinfoDb.USER_CITY+ " TEXT," //$NON-NLS-1$
					+ UserinfoDb.USER_STATE+ " TEXT," //$NON-NLS-1$
					+ UserinfoDb.USER_COUNTRY+ " TEXT," //$NON-NLS-1$
					+ UserinfoDb.USER_ABT_ME+ " TEXT," //$NON-NLS-1$
					+ UserinfoDb.USER_IS_NOTI+ " TEXT," //$NON-NLS-1$
					+ UserinfoDb.USER_JOINDATE+ " TEXT," //$NON-NLS-1$
					+ UserinfoDb.USER_LAST_LOGIN+ " TEXT," //$NON-NLS-1$
					+ UserinfoDb.USER_IMG_PATH+ " TEXT" + ");"; //$NON-NLS-1$ //$NON-NLS-2$


	private static final String CREATE_TABLE_TATTO_CATAGORY =
			"create table TattoCatag (_id integer primary key autoincrement, " //$NON-NLS-1$
					+ TattocategoryDB.TATTO_CATG_ID+ " TEXT," //$NON-NLS-1$
					+ TattocategoryDB.TATTO_CATG_NAME+ " TEXT," //$NON-NLS-1$
					+ TattocategoryDB.TATTO_CATG_PARENT_ID+ " TEXT," //$NON-NLS-1$
					+ TattocategoryDB.TATTO_CATG_URL+ " TEXT,"
					+ TattocategoryDB.TATTO_CATG_STATUS+ " TEXT" + ");"; //$NON-NLS-1$ //$NON-NLS-2$




	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	/**
	 * Constructor
	 * @param ctx
	 */
	public DBAdapter(Context ctx)
	{
		this.context = ctx;
		this.DBHelper = new DatabaseHelper(this.context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{

			db.execSQL(CREATE_TABLE_USERINFO);
			db.execSQL(CREATE_TABLE_TATTO_CATAGORY);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,
							  int newVersion)
		{
			// Adding any table mods to this guy here
		}
	}

	/**
	 * open the db
	 * @return this
	 * @throws SQLException
	 * return type: DBAdapter
	 */
	public DBAdapter open() throws SQLException
	{
		this.db = this.DBHelper.getWritableDatabase();
		return this;
	}

	/**
	 * close the db 
	 * return type: void
	 */
	public void close()
	{
		this.DBHelper.close();
	}

}
