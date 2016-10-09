package applabindia.com.tattoojocky;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.BlurEffect;
import com.mingle.sweetpick.RecyclerViewDelegate;
import com.mingle.sweetpick.SweetSheet;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import applabindia.com.tattoojocky.tattoodetail.DetailActivity;
import applabindia.com.tattoojocky.tattoodetail.TattooListAdapter;
import database.DBAdapter;
import database.TattocategoryDB;
import database.UserinfoDb;
import model.APIError;
import model.TattoCatagoryResponse;
import model.TattooInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import settings.Settingsmanager;
import utills.CommonUtill;
import utills.Constants;
import utills.ErrorUtils;
import utills.Utils;
import webconnectionhandler.ApiClient;
import webconnectionhandler.ApiInterface;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //Add Implementation
    private InterstitialAd mInterstitialAd;
    private Timer timerTask = null;
    Toolbar toolbar;
    ImageView ivLogo;
    private MenuItem inboxMenuItem,inboxMenuItemGrid;

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;
    private boolean pendingIntroAnimation;

    private CircularImageView circularImageView;
    private TextView userNameTxt;
    private TextView userEmailTxt;

    private Animation animImg,animUsrName,animUsrEmail;
    private  Animation.AnimationListener listner;

    private UserinfoDb userinfoDb;
    private DBAdapter dbAdapter;
    private TattocategoryDB tattocategoryDB;
    private  Settingsmanager settingsmanager;
    boolean isLogOut = false;
    private SweetSheet mSweetSheet;
    private DrawerLayout drawer;
    private FrameLayout container,inFrameLayout;
    private HashMap<String,TattoCatagoryResponse> tattocatagMap;

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private TattooListAdapter mAdapter;
    private boolean isListView;
    private ArrayList<TattooInfo.ResponseDato> tatoInfoList;
    private ApiInterface apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        isListView = true;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ivLogo = (ImageView) findViewById(R.id.ivLogo);
        container = (FrameLayout) findViewById(R.id.container);
        inFrameLayout = (FrameLayout) findViewById(R.id.inerContainer);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);

        apiService =
                ApiClient.getClient().create(ApiInterface.class);

        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
        mRecyclerView.setHasFixedSize(true); //Data size is fixed - improves performance

        tatoInfoList = new ArrayList<TattooInfo.ResponseDato>();
        //  mAdapter = new TattooListAdapter(this,tatoInfoList);
        //  mRecyclerView.setAdapter(mAdapter);

        //  mAdapter.setOnItemClickListener(onItemClickListener);



        setUpActionBar();
        dbAdapter = new DBAdapter(this);
        settingsmanager = new Settingsmanager(this);

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }

        animImg = AnimationUtils.loadAnimation(this,R.anim.bounce);
        animUsrName = AnimationUtils.loadAnimation(this,R.anim.in_from_left);
        animUsrEmail = AnimationUtils.loadAnimation(this,R.anim.in_from_left);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);

        View headerLayout = navigationView.getHeaderView(0);
        circularImageView = (CircularImageView)headerLayout. findViewById(R.id.usrImg);
        userEmailTxt = (TextView)headerLayout. findViewById(R.id.textViewEmail);
        userNameTxt = (TextView)headerLayout. findViewById(R.id.textViewUSerName);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                circularImageView.setVisibility(View.INVISIBLE);
                userEmailTxt.setVisibility(View.INVISIBLE);
                userNameTxt.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                startNavdrawerAnim();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //init

        Intent intent = getIntent();
        if (intent!=null){
            Picasso.with(this).load(intent.getStringExtra(UserinfoDb.USER_IMG_PATH))
                    .error(R.drawable.ic_user)
                    .into(circularImageView);
            userNameTxt.setText(intent.getStringExtra(UserinfoDb.USER_NAME));
            userEmailTxt.setText(intent.getStringExtra(UserinfoDb.USER_EMAIL));
        }

        // Create the InterstitialAd and set the adUnitId (defined in values/strings.xml).
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();
        // startTimer();

        setupTattoCatagView(getTattoCatg());
        setupFeed();
    }

    private void setupFeed() {
      /*  LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        mRecyclerView.setLayoutManager(linearLayoutManager);*/

        mAdapter = new TattooListAdapter(HomeActivity.this,tatoInfoList,isListView);
        // mAdapter.setOnFeedItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
       /* mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
            }
        })*/;
        //  mRecyclerView.setItemAnimator(new FeedItemAnimator());
        getTattoos("","1");
    }


    private void setupTattoCatagView(ArrayList<TattoCatagoryResponse> responseList) {
        final ArrayList<MenuEntity> list = new ArrayList<>();
        MenuEntity menuEntity1 = new MenuEntity();
        menuEntity1.iconId = android.R.drawable.ic_menu_agenda;
        menuEntity1.titleColor = 0xff000000;
        menuEntity1.title = "Choose Catagory";
        list.add(menuEntity1);
        MenuEntity menuEntity ;
        if(responseList!=null&responseList.size()>0){
            tattocatagMap = new HashMap<String, TattoCatagoryResponse>();
            for (int i = 0; i < responseList.size(); i++) {
                menuEntity = new MenuEntity();
                menuEntity.iconurl = responseList.get(i).getCateUrl();
                menuEntity.titleColor = 0xffb3b3b3;
                menuEntity.title = responseList.get(i).getCateName();
                list.add(menuEntity);
                tattocatagMap.put(responseList.get(i).getCateName(),responseList.get(i));
            }
        }

        mSweetSheet = new SweetSheet(container);

        mSweetSheet.setMenuList(list);
        mSweetSheet.setDelegate(new RecyclerViewDelegate(true));
        mSweetSheet.setBackgroundEffect(new BlurEffect(8));

        mSweetSheet.setOnMenuItemClickListener(new SweetSheet.OnMenuItemClickListener() {
            @Override
            public boolean onItemClick(int position, MenuEntity menuEntity1) {
                list.get(position).setSelected(true);
                ((RecyclerViewDelegate) mSweetSheet.getDelegate()).notifyDataSetChanged();
                mSweetSheet.toggle();
                Toast.makeText(HomeActivity.this, menuEntity1.title + "  " + position, Toast.LENGTH_SHORT).show();
                return false;
            }
        });


    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setUpActionBar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }


    private void startNavdrawerAnim(){


        circularImageView.startAnimation(animImg);
        listner = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if(animation.equals(animImg)){
                    circularImageView.setVisibility(View.VISIBLE);
                    userNameTxt.startAnimation(animUsrName);
                }else if(animation.equals(animUsrName)){
                    userNameTxt.setVisibility(View.VISIBLE);
                    userEmailTxt.startAnimation(animUsrEmail);
                }else if(animation.equals(animUsrEmail)){
                    userEmailTxt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        animImg.setAnimationListener(listner);
        animUsrName.setAnimationListener(listner);
        animUsrEmail.setAnimationListener(listner);



    }


    private void startIntroAnimation() {

        int actionbarSize = Utils.dpToPx(56);
        toolbar.setTranslationY(-actionbarSize);
        ivLogo.setTranslationY(-actionbarSize);
        //  inboxMenuItem.getActionView().setTranslationY(-actionbarSize);

        toolbar.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        ivLogo.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);
        /*inboxMenuItem.getActionView().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //startContentAnimation();
                    }
                })
                .start();*/
    }

    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
                goToNextLevel();
            }
        });
        return interstitialAd;
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            goToNextLevel();
        }
    }

    private void loadInterstitial() {
        // Disable the next level button and load the ad.
        // mNextLevelButton.setEnabled(false);
        AdRequest adRequest = new AdRequest.Builder()
                // .setRequestAgent("android_studio:ad_template")
                .addTestDevice("736B8BBE5F5D34A682BB1FB44A04DABD")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void goToNextLevel() {
        // Show the next level and reload the ad to prepare for the level after.
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        inboxMenuItem = menu.findItem(R.id.action_toggle);
        inboxMenuItem.setActionView(R.layout.menu_item_view);

        inboxMenuItemGrid = menu.findItem(R.id.action_toggle_grid);
        inboxMenuItemGrid.setActionView(R.layout.menu_item_grid);

        inboxMenuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSweetSheet.toggle();
                // inFrameLayout.setForeground(getResources().getDrawable(R.drawable.login_edit_border));
            }
        });

        inboxMenuItemGrid.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggle(inboxMenuItemGrid);

            }
        });


        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    private void toggle(MenuItem item) {
        if (isListView) {
            mStaggeredLayoutManager.setSpanCount(2);
            item.setIcon(R.drawable.ic_grid);
            item.setTitle("Show as grid");
            isListView = false;

        } else {
            mStaggeredLayoutManager.setSpanCount(1);
            item.setIcon(R.drawable.ic_list);
            item.setTitle("Show as list");
            isListView = true;
        }

        supportInvalidateOptionsMenu();
        mAdapter.updateListGridView(isListView);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       /* int id = item.getItemId();

        if (id == R.id.action_toggle) {
            mSweetSheet.toggle();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            try {

                dbAdapter.open();
                userinfoDb = new UserinfoDb(this);
                userinfoDb.open();
                userinfoDb.deleteAll_UsrDetails();
                settingsmanager.setLoginStatus(false);
                isLogOut = true;
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));

            }catch (Exception e) {
                e.printStackTrace();
            }finally {
                dbAdapter.close();
                userinfoDb.close();
            }



        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void startTimer() {
        if (timerTask == null) {
            timerTask = new Timer();
            timerTask.schedule(new TimerTask() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInterstitial();
                        }
                    });


                }
            }, 0,2000);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        //stopTimer();

        if(isLogOut){
            finish();
        }
    }



    public void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }


    @Nullable
    private ArrayList<TattoCatagoryResponse>  getTattoCatg(){
        try {
            dbAdapter.open();
            tattocategoryDB = new TattocategoryDB(this);
            tattocategoryDB.open();
            return  tattocategoryDB.getTattooinfo();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            tattocategoryDB.close();
            dbAdapter.close();
        }

        return null;
    }


    TattooListAdapter.OnItemClickListener onItemClickListener = new TattooListAdapter.OnItemClickListener() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onItemClick(View v, int position) {
            Intent transitionIntent = new Intent(HomeActivity.this, DetailActivity.class);
            transitionIntent.putExtra(DetailActivity.EXTRA_PARAM_ID,tatoInfoList.get(position));
            ImageView placeImage = (ImageView) v.findViewById(R.id.placeImage);
            LinearLayout placeNameHolder = (LinearLayout) v.findViewById(R.id.placeNameHolder);

            View navigationBar = HomeActivity.this.findViewById(android.R.id.navigationBarBackground);
            View statusBar = findViewById(android.R.id.statusBarBackground);

            Pair<View, String> imagePair = Pair.create((View) placeImage, "tImage");
            Pair<View, String> holderPair = Pair.create((View) placeNameHolder, "tNameHolder");
            Pair<View, String> navPair = Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
            Pair<View, String> statusPair = Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
            Pair<View, String> toolbarPair = Pair.create((View)toolbar, "tActionBar");

            ArrayList<Pair<View, String>> list = new ArrayList<>();

            list.add(imagePair);
            list.add(holderPair);
            list.add(toolbarPair);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                list.add(navPair);
                list.add(statusPair);
            }

            //remove any views that are null
            for (ListIterator<Pair<View, String>> iter = list.listIterator(); iter.hasNext();) {
                Pair pair = iter.next();
                if (pair.first == null) iter.remove();
            }

            Pair<View, String>[] sharedElements = list.toArray(new Pair[list.size()]);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this,sharedElements);
            ActivityCompat.startActivity(HomeActivity.this, transitionIntent, options.toBundle());
        }
    };


    private void getTattoos(String tattooCatId,String pageNo){

        Call<TattooInfo> info = apiService.getTattoInfo(tattooCatId,pageNo);
        info.enqueue(new Callback<TattooInfo>() {
            @Override
            public void onResponse(Call<TattooInfo> call, Response<TattooInfo> response) {
                if(response.isSuccessful()){
                    TattooInfo tattooInfo = response.body();
                    if(tattooInfo.getStatus().equalsIgnoreCase(Constants.RESPONSE_STATUS_TRUE)){
                        //do next
                        tatoInfoList.addAll(tattooInfo.getResponseData());
                        mAdapter.notifyDataSetChanged();
                       // mAdapter = new TattooListAdapter(HomeActivity.this,tatoInfoList);
                       // mRecyclerView.setAdapter(mAdapter);
                        // mAdapter.setOnItemClickListener(onItemClickListener);

                    }else{
                        CommonUtill.showSnakbarError(HomeActivity.this,tattooInfo.getMessage(),container);
                    }
                }else{
                    Log.i("onFailure Error",response.message());

                    APIError error = ErrorUtils.parseError(response);
                    CommonUtill.showSnakbarError(HomeActivity.this,error.getMessage(),container);
                    Log.d("error message", error.getMessage());
                }
            }

            @Override
            public void onFailure(Call<TattooInfo> call, Throwable t) {
                CommonUtill.showSnakbarError(HomeActivity.this,getResources().getString(R.string.went_wrong),container);
            }
        });



    }


    public void showLikedSnackbar() {
        Snackbar.make(container, "Liked!", Snackbar.LENGTH_SHORT).show();
    }

}
