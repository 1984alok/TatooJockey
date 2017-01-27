package applabindia.com.tattoojocky;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import database.DBAdapter;
import database.UserinfoDb;
import model.ResponseData;
import model.TattoCatagory;
import model.TattoCatagoryResponse;
import model.UserModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import settings.Settingsmanager;
import utills.CommonUtill;
import webconnectionhandler.ApiClient;
import webconnectionhandler.ApiInterface;
import webconnectionhandler.DBCalls;
import webconnectionhandler.NetworkStatus;

import static permission_manager.PermissionHandler.checkIfAlreadyhavePermission;
import static permission_manager.PermissionHandler.requestForSpecificPermission;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener{


    private ImageView logo;
    private Animation anim,fadeAnim;
    private com.victor.loading.newton.NewtonCradleLoading ploader;
    private Settingsmanager mSettingsmanager;
    private UserinfoDb userinfoDb;
    private DBAdapter dbAdapter;
    private LinearLayout skipView;
    private TextView skipTxt,loginTxt;
    private ApiInterface apiService;
    private RelativeLayout frameContainer;
    boolean isSkipClicked;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logo = (ImageView) findViewById(R.id.logo);
        skipView = (LinearLayout) findViewById(R.id.bootom_skip_view);
        loginTxt = (TextView) findViewById(R.id.loginTxt);
        skipTxt = (TextView) findViewById(R.id.skipTxt);
        ploader = (com.victor.loading.newton.NewtonCradleLoading)findViewById(R.id.newton_cradle_loading);
        frameContainer = (RelativeLayout)findViewById(R.id.frameContainer);
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        mSettingsmanager = new Settingsmanager(this);
        anim = AnimationUtils.loadAnimation(this,R.anim.bounce);
        fadeAnim = AnimationUtils.loadAnimation(this,R.anim.fadein);
        skipTxt.setOnClickListener(this);
        loginTxt.setOnClickListener(this);

        anim.setAnimationListener(animationListener);
        fadeAnim.setAnimationListener(animationListener);
       // printHashKey();
        requestAllPermission();
    }

    private void requestAllPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkIfAlreadyhavePermission(this)) {
                requestForSpecificPermission(this);
            } else{
                //do your work
                logo.startAnimation(anim);
            }
        }else{
            logo.startAnimation(anim);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == 101){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                Log.i("Home","onRequestPermissionsResult granted");
                logo.startAnimation(anim);

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Log.i("Home","onRequestPermissionsResult denied");
            }
            return;
        }
    }


    public void printHashKey(){
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "applabindia.com.tattoojocky",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

            if (animation==anim){
                ploader.setVisibility(View.VISIBLE);
                ploader.start();
                if(NetworkStatus.getInstance().isConnected(SplashActivity.this)) {
                    doServerCallForCatg();
                }else{
                    CommonUtill.showSnakbarError(SplashActivity.this,getResources().getString(R.string.network_error),frameContainer);
                }
            }else if(animation==fadeAnim){

                skipView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };



    private void startNextScreen() {
        if (!mSettingsmanager.isLoginStatus()) {
            skipView.startAnimation(fadeAnim);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent;
                    ResponseData data = getUserInfo();

                    if (data != null) {
                        startNextwithValidUserData(data);
                    } else {
                        startNextwithBlankUserData();
                    }

                }
            }, 5000);
        }
    }


    private void startNextwithBlankUserData(){

        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(UserinfoDb.USER_ID, "0");
        intent.putExtra(UserinfoDb.USER_NAME, "Guest");
        intent.putExtra(UserinfoDb.USER_EMAIL, "");
        intent.putExtra(UserinfoDb.USER_IMG_PATH,"");
        startActivity(intent);
    }


    private void startNextwithValidUserData(ResponseData data){
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(UserinfoDb.USER_ID,data.getUserId() );
        intent.putExtra(UserinfoDb.USER_NAME, data.getName());
        intent.putExtra(UserinfoDb.USER_EMAIL,data.getEmail());
        intent.putExtra(UserinfoDb.USER_IMG_PATH,data.getImage());
        startActivity(intent);
    }


    @Override
    protected void onStop() {
        super.onStop();
        //if(isSkipClicked) {
            finish();
       // }
    }



    private ResponseData  getUserInfo(){
        try {
            dbAdapter = new DBAdapter(this);
            dbAdapter.open();
            userinfoDb = new UserinfoDb(this);
            userinfoDb.open();
            return  userinfoDb.getUserinfo();

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            dbAdapter.close();
            userinfoDb.close();
        }

        return null;
    }

    private void doServerCallForCatg(){

        Call<TattoCatagory> call = apiService.getTattoCatgory("");
        call.enqueue(new Callback<TattoCatagory>() {


            @Override
            public void onResponse(Call<TattoCatagory> call, Response<TattoCatagory> response) {
                TattoCatagory mTattoCatagory = response.body();
                if(mTattoCatagory!=null) {
                    ArrayList<TattoCatagoryResponse> responseList = (ArrayList<TattoCatagoryResponse>) mTattoCatagory.getResponseData();
                    if(responseList!=null){
                        DBCalls.getInstance(SplashActivity.this).createTattooCatg(responseList);
                        startNextScreen();
                    }else{
                        CommonUtill.showSnakbarError(SplashActivity.this,getResources().getString(R.string.went_wrong),frameContainer);
                    }
                }

            }

            @Override
            public void onFailure(Call<TattoCatagory> call, Throwable t) {
                Toast.makeText(SplashActivity.this,t.toString(),Toast.LENGTH_LONG ).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.skipTxt:
                isSkipClicked = true;
                startNextwithBlankUserData();
                break;
            case R.id.loginTxt:
                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                break;
        }
    }
}
