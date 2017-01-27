package applabindia.com.tattoojocky;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import database.DBAdapter;
import database.TattocategoryDB;
import database.UserinfoDb;
import model.APIError;
import model.ResponseData;
import model.ResponseModel;
import model.TattoCatagory;
import model.TattoCatagoryResponse;
import model.UserModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import settings.Settingsmanager;
import utills.CommonUtill;
import utills.Constants;
import utills.ErrorUtils;
import webconnectionhandler.ApiClient;
import webconnectionhandler.ApiInterface;
import webconnectionhandler.NetworkStatus;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
    boolean toglge =false;
    ApiInterface apiService;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private List<String> emails = new ArrayList<String>();
    private ArrayAdapter<String> emails_adapter;
    private ViewFlipper mViewFlipper;

    private EditText usrName;
    private EditText passwordSignup;
    private EditText cPasswordSignup;
    private AutoCompleteTextView mEmailViewSignup;
    private RelativeLayout login_frame;
    private CommonUtill mUtill;
    private DBAdapter dbAdapter;
    private UserinfoDb userinfoDb;
    private TattocategoryDB tattocategoryDB;
    private Settingsmanager settingsmanager;
    private TextView skipTxt,forgotPwdTxtSignViw,forgotPwdTxtSigninViw,txt_forgot_pwd_cancel;
    private AutoCompleteTextView ed_forgot_pwd;
    private Button send;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signup);

        login_frame = (RelativeLayout)findViewById(R.id.login_frame);
        mViewFlipper = (ViewFlipper)findViewById(R.id.pager);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mEmailViewSignup = (AutoCompleteTextView) findViewById(R.id.emailSignUp);
        ed_forgot_pwd = (AutoCompleteTextView) findViewById(R.id.ed_forgot_pwd_email);

        skipTxt = (TextView)findViewById(R.id.skip);

        mEmailView.setThreshold(1);
        mEmailViewSignup.setThreshold(1);
        ed_forgot_pwd.setThreshold(1);
        populateAutoComplete();

        apiService =
                ApiClient.getClient().create(ApiInterface.class);

        mPasswordView = (EditText) findViewById(R.id.password);

        passwordSignup = (EditText) findViewById(R.id.passwordSignUp);
        cPasswordSignup = (EditText) findViewById(R.id.confirm_passwordSignUp);
        usrName = (EditText) findViewById(R.id.useNamerSignUp);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.loginKeyboard || id == EditorInfo.IME_NULL) {
                    // attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkStatus.getInstance().isConnected(LoginActivity.this)) {
                    attemptLogin();
                }else{
                    CommonUtill.showSnakbarError(LoginActivity.this,getResources().getString(R.string.network_error),login_frame);
                }
            }
        });

        Button mSignUp = (Button) findViewById(R.id.email_sign_up_button);
        mSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkStatus.getInstance().isConnected(LoginActivity.this)) {
                    attemptSignup();
                }else{
                    CommonUtill.showSnakbarError(LoginActivity.this,getResources().getString(R.string.network_error),login_frame);
                }
            }
        });

        skipTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {

            if(mViewFlipper.getDisplayedChild()==0) {
                Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onClick(View v) {
                                requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                            }
                        });
            }else  if(mViewFlipper.getDisplayedChild()==1){
                Snackbar.make(mEmailViewSignup, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onClick(View v) {
                                requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                            }
                        });
            }else  if(mViewFlipper.getDisplayedChild()==3){
                Snackbar.make(ed_forgot_pwd, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onClick(View v) {
                                requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                            }
                        });
            }
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            emails.add(email);
            emails_adapter.notifyDataSetChanged();
            showProgress(2);
            // mAuthTask = new UserLoginTask(email, password);
            // mAuthTask.execute((Void) null);
            //  doServerCallForCatg();
            doLogin(email,password);
        }
    }


    private void attemptSignup() {
        // Reset errors.
        mEmailViewSignup.setError(null);
        passwordSignup.setError(null);
        cPasswordSignup.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailViewSignup.getText().toString();
        String password = passwordSignup.getText().toString();
        String userNameTxt = usrName.getText().toString();


        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(userNameTxt)) {
            usrName.setError(getString(R.string.error_name_required));
            focusView = usrName;
            cancel = true;
        }else if (TextUtils.isEmpty(email)) {
            mEmailViewSignup.setError(getString(R.string.error_field_required));
            focusView = mEmailViewSignup;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailViewSignup.setError(getString(R.string.error_invalid_email));
            focusView = mEmailViewSignup;
            cancel = true;
        }else if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }else if(!password.equals(cPasswordSignup.getText().toString())) {
            cPasswordSignup.setError(getString(R.string.confirm_password_notequal));
            focusView = cPasswordSignup;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            emails.add(email);
            emails_adapter.notifyDataSetChanged();
            showProgress(2);
            // mAuthTask = new UserLoginTask(email, password);
            // mAuthTask.execute((Void) null);
            //  doServerCallForCatg();
            doSignUP(userNameTxt,email,password);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(int displayChild) {

        mViewFlipper.setInAnimation(this, R.anim.fadein);
        mViewFlipper.setOutAnimation(this, R.anim.fadeout);
        mViewFlipper.setDisplayedChild(displayChild);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Set<String> emailSet = new HashSet<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emailSet.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }


        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (EMAIL_PATTERN.matcher(account.name).matches()) {
                emailSet.add(account.name);
            }
        }

        addEmailsToAutoComplete(new ArrayList<String>(emailSet));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        emails_adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        mEmailView.setThreshold(1);
        mEmailView.setAdapter(emails_adapter);

        mEmailViewSignup.setThreshold(1);
        mEmailViewSignup.setAdapter(emails_adapter);

        ed_forgot_pwd.setThreshold(1);
        ed_forgot_pwd.setAdapter(emails_adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    public void btnClick(View v){

        switch (v.getId()){
            case R.id.signupTxViw:


                // set the required Animation type to ViewFlipper
                // The Next screen will come in form Left and current Screen will go OUT from Right
              /*  mViewFlipper.setInAnimation(this, R.anim.in_from_right);
                mViewFlipper.setOutAnimation(this, R.anim.out_to_left);*/
                mViewFlipper.setInAnimation(this, R.anim.fadein);
                mViewFlipper.setOutAnimation(this, R.anim.fadeout);
                // Show the next Screen
                mViewFlipper.showNext();

                break;
            case R.id.SignInViw:


                // set the required Animation type to ViewFlipper
                // The Next screen will come in form Right and current Screen will go OUT from Left
              /*  mViewFlipper.setInAnimation(this, R.anim.in_from_left);
                mViewFlipper.setOutAnimation(this, R.anim.out_to_right);*/
                mViewFlipper.setInAnimation(this, R.anim.fadein);
                mViewFlipper.setOutAnimation(this, R.anim.fadeout);
                // Show The Previous Screen
                mViewFlipper.showPrevious();


                break;

            case R.id.password:

                showPassword();
                break;

            case R.id.forgotPwdTxtSignViw:
            case R.id.forgotPwdTxtSigninViw:
                mViewFlipper.setInAnimation(this, R.anim.fadein);
                mViewFlipper.setOutAnimation(this, R.anim.fadeout);
                // Show The Previous Screen
                mViewFlipper.setDisplayedChild(3);
                break;


            case R.id.txt_forgot_pwd_cancel:
                mViewFlipper.setInAnimation(this, R.anim.fadein);
                mViewFlipper.setOutAnimation(this, R.anim.fadeout);
                // Show The Previous Screen
                mViewFlipper.setDisplayedChild(0);
                break;

            case R.id.frgt_pwd_button:

                if(NetworkStatus.getInstance().isConnected(LoginActivity.this)) {
                    callForgotPwd();
                }else{
                    CommonUtill.showSnakbarError(LoginActivity.this,getResources().getString(R.string.network_error),login_frame);
                }

                break;

        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showPassword(){
        if(!toglge){
            mPasswordView. setTransformationMethod(new PasswordTransformationMethod());
            //  mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mPasswordView.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().
                    getDrawable( R.drawable.ic_visibility_off_black_24dp),null);
            mPasswordView.setSelection(mPasswordView.length());
            toglge=true;
        }else{
            mPasswordView. setTransformationMethod(null);
            // mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mPasswordView.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().
                    getDrawable( R.drawable.ic_visibility_black_24dp),null);
            mPasswordView.setSelection(mPasswordView.length());
            toglge=false;
        }
    }

    private void doServerCallForCatg(){

        Call<TattoCatagory> call = apiService.getTattoCatgory("");
        call.enqueue(new Callback<TattoCatagory>() {


            @Override
            public void onResponse(Call<TattoCatagory> call, Response<TattoCatagory> response) {
                TattoCatagory list = response.body();
                if(list!=null) {
                    ArrayList<TattoCatagoryResponse> responseList = (ArrayList<TattoCatagoryResponse>) list.getResponseData();
                    if(responseList!=null){
                        createTattoCatg(responseList);
                    }
                }

            }

            @Override
            public void onFailure(Call<TattoCatagory> call, Throwable t) {
                Toast.makeText(LoginActivity.this,t.toString(),Toast.LENGTH_LONG ).show();
            }
        });
    }


    private void doSignUP(String name,String email_Id,String pwd){

        Call<UserModel> call = apiService.createUser(name, email_Id, pwd,"1");
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if(response.isSuccessful()){
                    UserModel userModel = response.body();
                    if(userModel.getStatus().equalsIgnoreCase(Constants.RESPONSE_STATUS_TRUE)){
                        showMessage(userModel.getMessage(),true);
                        //do next
                        initUserInfo(userModel.getResponseData(),2);

                    }else{
                        showProgress(0);
                        showMessage(userModel.getMessage(),false);
                    }
                }else{
                    Log.i("onFailure Error",response.message());
                    Toast.makeText(LoginActivity.this,response.message(),Toast.LENGTH_LONG ).show();

                    APIError error = ErrorUtils.parseError(response);
                    showMessage(error.getMessage(),true);
                    // … and use it to show error information

                    // … or just log the issue like we’re doing :)
                    Log.d("error message", error.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {

                showProgress(1);
                showMessage(getResources().getString(R.string.went_wrong),false);
            }
        });
    }



    private void doLogin (String email_Id,String pwd){

        Call<UserModel> call = apiService.logIn(email_Id,pwd,"Login");
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                if(response.isSuccessful()){
                    UserModel userModel = response.body();
                    if(userModel.getStatus().equalsIgnoreCase(Constants.RESPONSE_STATUS_TRUE)){
                        //do next
                        showMessage(userModel.getMessage(),true);
                        initUserInfo(userModel.getResponseData(),1);

                    }else{
                        showProgress(0);
                        showMessage(userModel.getMessage(),false);
                    }
                }else{
                    Log.i("onFailure Error",response.message());
                    Toast.makeText(LoginActivity.this,response.message(),Toast.LENGTH_LONG ).show();

                    APIError error = ErrorUtils.parseError(response);
                    showProgress(0);
                    showMessage(error.getMessage(),false);
                    // … and use it to show error information

                    // … or just log the issue like we’re doing :)
                    Log.d("error message", error.getMessage());
                }

            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                showProgress(0);
                showMessage(getResources().getString(R.string.went_wrong),false);
            }
        });

    }



    private void showMessage(String msg,boolean isSucees){
        if(isSucees) {
            CommonUtill.showSnakbarSucces(LoginActivity.this, msg, login_frame);
        }else{
            CommonUtill.showSnakbarError(LoginActivity.this, msg, login_frame);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }



    private void initUserInfo(final ResponseData data, int screenView){

        if(data!=null){
            try {
                dbAdapter = new DBAdapter(this);
                dbAdapter.open();
                userinfoDb = new UserinfoDb(this);
                userinfoDb.open();
                if(userinfoDb.createUserinfo(data)!=-1){
                    settingsmanager = new Settingsmanager(this);
                    settingsmanager.setLoginStatus(true);

                    new AsyncTask<Void,Void,Void>(){

                        @Override
                        protected Void doInBackground(Void... params) {

                            doServerCallForCatg();
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(UserinfoDb.USER_ID,data.getUserId() );
                                    intent.putExtra(UserinfoDb.USER_NAME, data.getName());
                                    intent.putExtra(UserinfoDb.USER_EMAIL,data.getEmail());
                                    intent.putExtra(UserinfoDb.USER_IMG_PATH,data.getImage());
                                    startActivity(intent);
                                }
                            },1000);
                        }
                    }.execute();

                }else{
                    showMessage("Log in error.",false);
                    showProgress(screenView);
                }

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                userinfoDb.close();
                dbAdapter.close();
            }


        }
    }



    private void createTattoCatg(ArrayList<TattoCatagoryResponse> responses){

        try {

            dbAdapter.open();
            tattocategoryDB = new TattocategoryDB(this);
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



    private void callForgotPwd(){
        ed_forgot_pwd.setError(null);
        // Store values at the time of the login attempt.
        String email = ed_forgot_pwd.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            ed_forgot_pwd.setError(getString(R.string.error_field_required));
            focusView = ed_forgot_pwd;
            cancel = true;
        } else if (!isEmailValid(email)) {
            ed_forgot_pwd.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            emails.add(email);
            emails_adapter.notifyDataSetChanged();
            showProgress(2);
            // mAuthTask = new UserLoginTask(email, password);
            // mAuthTask.execute((Void) null);
            //  doServerCallForCatg();
            doServercallForgotPwd(email);
        }
    }



    private void doServercallForgotPwd(String emailId){
        Call<ResponseModel> call = apiService.callForgotPwd(emailId);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if(response.isSuccessful()) {
                    ResponseModel model = response.body();
                    if (model.getStatus().equalsIgnoreCase(Constants.RESPONSE_STATUS_TRUE)) {
                        showMessage(model.getMessage(), true);
                        showProgress(0);

                    } else {
                        showProgress(3);
                        showMessage(model.getMessage(), false);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                showProgress(3);
                showMessage(getResources().getString(R.string.went_wrong),false);

            }
        });

    }
}

