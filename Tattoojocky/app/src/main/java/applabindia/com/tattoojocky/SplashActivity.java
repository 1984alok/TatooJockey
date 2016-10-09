package applabindia.com.tattoojocky;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import database.DBAdapter;
import database.UserinfoDb;
import model.UserModel;
import settings.Settingsmanager;

public class SplashActivity extends AppCompatActivity {


    ImageView logo;
    Animation anim ;
    com.victor.loading.newton.NewtonCradleLoading b;
    private Settingsmanager mSettingsmanager;
    private UserinfoDb userinfoDb;
    private DBAdapter dbAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logo = (ImageView) findViewById(R.id.logo);
        b  = (com.victor.loading.newton.NewtonCradleLoading)findViewById(R.id.newton_cradle_loading);
        mSettingsmanager = new Settingsmanager(this);
        anim = AnimationUtils.loadAnimation(this,R.anim.bounce);
        logo.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                b.setVisibility(View.VISIBLE);
                b.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent;
                        if(!mSettingsmanager.isLoginStatus()) {
                            intent = new Intent(SplashActivity.this,LoginActivity.class);
                        }else {

                            UserModel.ResponseData data = getUserInfo();
                            if (data != null) {
                                intent = new Intent(SplashActivity.this, HomeActivity.class);
                                intent.putExtra(UserinfoDb.USER_ID,data.getUserId() );
                                intent.putExtra(UserinfoDb.USER_NAME, data.getName());
                                intent.putExtra(UserinfoDb.USER_EMAIL,data.getEmail());
                                intent.putExtra(UserinfoDb.USER_IMG_PATH,"http://cdn.bestappleprice.com/wp-content/uploads/2015/04/apple-customer-care-india.png");
                            } else {
                                intent = new Intent(SplashActivity.this, HomeActivity.class);
                                intent.putExtra(UserinfoDb.USER_ID, "-1");
                                intent.putExtra(UserinfoDb.USER_ID, "Guest");
                                intent.putExtra(UserinfoDb.USER_ID, "");
                                intent.putExtra(UserinfoDb.USER_IMG_PATH,"");
                            }
                        }

                        startActivity(intent);
                    }
                },5000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });




    }


    @Override
    protected void onStop() {
        super.onStop();
       finish();
    }



    private UserModel.ResponseData  getUserInfo(){
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


}
