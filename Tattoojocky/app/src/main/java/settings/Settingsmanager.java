package settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Settingsmanager {

	// Shared Preferences
	SharedPreferences pref;

	// Editor for Shared preferences
	Editor editor;

	// Context
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREF_NAME = "TatooJockyPreference";

	// All Shared Preferences Keys
	public static final String KEY_LOGIN_STATUS = "login_status";
	public static final String KEY_NOTIFICATION_STATUS = "notify_status";

	//tag
	private String TAG = "Settingsmanager";


	// Constructor
	public Settingsmanager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME,
				Activity.MODE_PRIVATE);
		editor = pref.edit();
	}

	public boolean isLoginStatus() {
		return pref.getBoolean(KEY_LOGIN_STATUS, false);
	}


	public void setLoginStatus(boolean loginStatus) {

		editor.putBoolean(KEY_LOGIN_STATUS, loginStatus);
		editor.commit();
	}


	public boolean isNotifyStatus() {
		return pref.getBoolean(KEY_NOTIFICATION_STATUS, true);
	}


	public void setNotifyStatus(boolean notifyStatus) {
		editor.putBoolean(KEY_NOTIFICATION_STATUS, notifyStatus);
		editor.commit();
	}


}
