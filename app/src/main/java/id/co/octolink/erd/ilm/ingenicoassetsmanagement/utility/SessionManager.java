package id.co.octolink.erd.ilm.ingenicoassetsmanagement.utility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.login.LoginActivity;

/**
 * Created by E.R.D on 4/2/2016.
 */
public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Ingenico AMS Session";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    public static final String KEY_STAFF_ID = "staff_id";
    public static final String KEY_STAFF_CODE = "staff_code";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_LOCATION_CODE = "location_code";

    public static final String KEY_BARCODE = "barcode";
    public static final String KEY_BRAND = "brand";

    public static final String KEY_FS_STAFF = "fs_staff";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String staffID, String staffCode, String firstName,
                                   String lastName, String password, String locCode){
        editor.putBoolean(KEY_IS_LOGGEDIN, true);

        editor.putString(KEY_STAFF_ID, staffID);
        editor.putString(KEY_STAFF_CODE, staffCode);
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_LOCATION_CODE, locCode);

        editor.commit();
    }

    public void createBarcodeSession(String barcode){
        editor.putBoolean(KEY_IS_LOGGEDIN, true);

        editor.putString(KEY_BARCODE, barcode);

        editor.commit();
    }

    public void createBrandSession(String brand){
        editor.putBoolean(KEY_IS_LOGGEDIN, true);

        editor.putString(KEY_BRAND, brand);

        editor.commit();
    }

    public void createStaffFSSession(String fsStaff){
        editor.putBoolean(KEY_IS_LOGGEDIN, true);

        editor.putString(KEY_FS_STAFF, fsStaff);

        editor.commit();
    }

    public void checkLogin(){
        if(!this.isLoggedIn()){
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_STAFF_ID, pref.getString(KEY_STAFF_ID, null));
        user.put(KEY_STAFF_CODE, pref.getString(KEY_STAFF_CODE, null));
        user.put(KEY_FIRST_NAME, pref.getString(KEY_FIRST_NAME, null));
        user.put(KEY_LAST_NAME, pref.getString(KEY_LAST_NAME, null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        user.put(KEY_LOCATION_CODE, pref.getString(KEY_LOCATION_CODE, null));
        user.put(KEY_BARCODE, pref.getString(KEY_BARCODE, null));
        user.put(KEY_BRAND, pref.getString(KEY_BRAND, null));
        user.put(KEY_FS_STAFF, pref.getString(KEY_FS_STAFF, null));

        return user;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

}
