package applabindia.com.tattoojocky;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;

public class ProfileScrollingActivity extends AppCompatActivity {

    AppBarLayout appBarLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    CircularImageView imgProfile;
    Animation pfImageEntryAnimation;
    Animation pfImageExitAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        imgProfile  = (CircularImageView) findViewById(R.id.usrImgProfile);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

      //  mTitle.setText("Custom...");
        getSupportActionBar().setTitle("Alok");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pfImageEntryAnimation = AnimationUtils.loadAnimation(this,R.anim.bounce);
        pfImageExitAnimation = AnimationUtils.loadAnimation(this,R.anim.fadeout);
        pfImageEntryAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imgProfile.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        pfImageExitAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
               imgProfile.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                /**
                 * verticalOffset changes in diapason
                 * from 0 - appBar is fully unwrapped
                 * to -appBarLayout's height - appBar is totally collapsed
                 * so in example we hide FAB when user folds half of the appBarLayout
                 */
                if (appBarLayout.getHeight() / 4 < -verticalOffset) {
                   imgProfile.setVisibility(View.GONE);
                   // imgProfile.startAnimation(pfImageExitAnimation);

                } else {
                   //imgProfile.setVisibility(View.VISIBLE);
                    imgProfile.startAnimation(pfImageEntryAnimation);
                }
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
