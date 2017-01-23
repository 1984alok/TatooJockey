package applabindia.com.tattoojocky.profile;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import applabindia.com.tattoojocky.R;
import database.DBAdapter;
import database.UserinfoDb;
import de.hdodenhof.circleimageview.CircleImageView;
import model.UserModel;

public class ProfileScreen extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener,View.OnClickListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private ImageView backImg,userBigImg;
    private de.hdodenhof.circleimageview.CircleImageView userImg;

    private TextView dobTxt,joiningDtTxt,emailTxt,phNoTxt,countrytTxt,
            stateTxt,cityTxt,aboutMeTxt,userName;
    private DBAdapter dbAdapter;
    private UserinfoDb mUserinfoDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);

        bindActivity();

        mAppBarLayout.addOnOffsetChangedListener(this);

       // mToolbar.inflateMenu(R.menu.menu_main);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
    }

    private void bindActivity() {
        mToolbar        = (Toolbar) findViewById(R.id.main_toolbar);
        mTitle          = (TextView) findViewById(R.id.main_textview_title);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        mAppBarLayout   = (AppBarLayout) findViewById(R.id.main_appbar);
        backImg         = (ImageView) findViewById(R.id.main_imageview_backbutton);
        userBigImg         = (ImageView) findViewById(R.id.main_imageview_placeholder);
        userImg         = (CircleImageView) findViewById(R.id.profile_userImg);
        dobTxt = (TextView) findViewById(R.id.profileDob);
        joiningDtTxt = (TextView) findViewById(R.id.profileJoiningDt);
        emailTxt = (TextView) findViewById(R.id.profileEmail);
        phNoTxt = (TextView) findViewById(R.id.profilePhone);
        countrytTxt = (TextView) findViewById(R.id.profileCountry);
        stateTxt = (TextView) findViewById(R.id.profileState);
        cityTxt = (TextView) findViewById(R.id.profileCity);
        aboutMeTxt = (TextView) findViewById(R.id.prompt_about_me);
        userName = (TextView) findViewById(R.id.profile_usrName);

        dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        mUserinfoDb = new UserinfoDb(this);
        mUserinfoDb.open();
        setData(mUserinfoDb.getUserinfo());

        backImg.setOnClickListener(this);

    }


    private void setData(UserModel.ResponseData model){

        userName.setText(model.getName());
        mTitle.setText(model.getName());
        emailTxt.setText(model.getEmail());
        joiningDtTxt.setText(model.getJoinDate());
        dobTxt.setText(model.getDob());
        phNoTxt.setText(model.getContact());
        countrytTxt.setText(TextUtils.isEmpty(model.getCountry())? "Country" : model.getCountry());
        stateTxt.setText(TextUtils.isEmpty(model.getState())? "State" : model.getState());
        cityTxt.setText(TextUtils.isEmpty(model.getCity())? "City" : model.getCity());
        aboutMeTxt.setText(model.getAboutMe());
        String imgPath = model.getImage();
        if(imgPath!=null){
            Picasso.with(this).load(imgPath)
                    .error(R.drawable.ic_user)
                    .into(userBigImg);

            Picasso.with(this).load(imgPath)
                    .error(R.drawable.ic_user)
                    .into(userImg);
        }else{

        }


    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;

            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.main_imageview_backbutton:
                finish();
                break;

        }
    }
}
