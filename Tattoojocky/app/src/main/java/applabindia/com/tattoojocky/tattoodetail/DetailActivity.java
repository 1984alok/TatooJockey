package applabindia.com.tattoojocky.tattoodetail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import applabindia.com.tattoojocky.HomeActivity;
import applabindia.com.tattoojocky.R;
import model.TattooInfo;
import uk.co.senab.photoview.PhotoViewAttacher;
import utills.CommonUtill;
import utills.Constants;


public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

  public static final String EXTRA_PARAM_ID = "place_id";
  public static final String EXTRA_PARAM_HANDLER = "handler";

  public static final String NAV_BAR_VIEW_NAME = Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME;
  private ListView mList;
  private ImageView mImageView;
  private TextView mTitle;
  private LinearLayout mTitleHolder;
  private Palette mPalette;
  private ImageButton mAddButton;
  private Animatable mAnimatable;
  private LinearLayout mRevealView;
  private EditText mEditTextTodo;
  private boolean isEditTextVisible;
  private InputMethodManager mInputManager;
  private TattooInfo.ResponseDato mTattoo;
  private ArrayList<String> mTodoList;
  private ArrayAdapter mToDoAdapter;
  int defaultColorForRipple;
  private TextSwitcher likeCount,shareCount,dislikeCount;
  private ImageButton btnLike,btnShare,btnDislike;
  private LinearLayout ll_Like,ll_Share,ll_Dislike;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);

    mTattoo = (TattooInfo.ResponseDato) getIntent().getSerializableExtra(EXTRA_PARAM_ID);

    mList = (ListView) findViewById(R.id.list);
    mImageView = (ImageView) findViewById(R.id.placeImage);
    mTitle = (TextView) findViewById(R.id.textView);
    mTitleHolder = (LinearLayout) findViewById(R.id.placeNameHolder);
    mAddButton = (ImageButton) findViewById(R.id.btn_add);
    mRevealView = (LinearLayout) findViewById(R.id.llEditTextHolder);
    mEditTextTodo = (EditText) findViewById(R.id.etTodo);

    likeCount = (TextSwitcher)findViewById(R.id.tsLikesCounter);
    shareCount = (TextSwitcher)findViewById(R.id.tsShareCounter);
    dislikeCount = (TextSwitcher)findViewById(R.id.tsDsLikesCounter);

    btnLike = (ImageButton)findViewById(R.id.btnLike);
    btnShare = (ImageButton)findViewById(R.id.btnShare);
    btnDislike = (ImageButton)findViewById(R.id.btnDisLike);


    ll_Like = (LinearLayout)findViewById(R.id.ll_like);
    ll_Share = (LinearLayout)findViewById(R.id.ll_share);
    ll_Dislike = (LinearLayout) findViewById(R.id.ll_dislike);

    /*ll_Like.setOnClickListener(this);
    ll_Share.setOnClickListener(this);
    ll_Dislike.setOnClickListener(this);

    btnLike.setOnClickListener(this);
    btnShare.setOnClickListener(this);
    btnDislike.setOnClickListener(this);*/


    mAddButton.setImageResource(R.drawable.icn_morph_reverse);
    mAddButton.setOnClickListener(this);
    defaultColorForRipple = getResources().getColor(R.color.colorPrimaryDark);
    mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    mRevealView.setVisibility(View.INVISIBLE);
    isEditTextVisible = false;

    mTodoList = new ArrayList<>();
    mToDoAdapter = new ArrayAdapter(this, R.layout.row_todo, mTodoList);
    mList.setAdapter(mToDoAdapter);

    loadPlace();
    windowTransition();
    getPhoto();

    mTitle.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DetailActivity.super.onBackPressed();
      }
    });

  }

  private void loadPlace() {

    btnLike.setImageResource(mTattoo.getIsLiked() == 1 ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_grey);

    likeCount.setCurrentText(mTattoo.getTattooLikes());
    shareCount.setCurrentText(mTattoo.getShareCount());
    dislikeCount.setCurrentText(mTattoo.getTattooDislikes());


    mTitle.setText(mTattoo.getTattooName());
    Picasso.with(this).load(mTattoo.getTattooImage())
            .error(R.drawable.logo)
            .into(mImageView);
    PhotoViewAttacher mAttacher = new PhotoViewAttacher(mImageView);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private void windowTransition() {
    getWindow().setEnterTransition(makeEnterTransition());
    getWindow().getEnterTransition().addListener(new TransitionAdapter() {
      @Override
      public void onTransitionEnd(Transition transition) {
        mAddButton.animate().alpha(1.0f);
        getWindow().getEnterTransition().removeListener(this);
      }
    });
  }

  @TargetApi(Build.VERSION_CODES.KITKAT)
  public static Transition makeEnterTransition() {
    Transition fade = new Fade();
    fade.excludeTarget(android.R.id.navigationBarBackground, true);
    fade.excludeTarget(android.R.id.statusBarBackground, true);
    return fade;
  }

  private void addToDo(String todo) {
    mTodoList.add(todo);
  }

  private void getPhoto() {
    new AsyncTask<Void,Void,Bitmap>(){
      Bitmap image = null;
      String urlString = mTattoo.getTattooImage();
      @Override
      protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        colorize(bitmap);
      }

      @Override
      protected Bitmap doInBackground(Void... params) {

        try {

          if(!urlString.equalsIgnoreCase("")||urlString!=null||!urlString.equalsIgnoreCase("null")) {
            URL url = new URL(urlString);
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
          }else {
            image = BitmapFactory.decodeResource(DetailActivity.this.getResources(),R.drawable.logo);
          }
        } catch (Exception e) {
          e.printStackTrace();
          image = BitmapFactory.decodeResource(DetailActivity.this.getResources(),R.drawable.logo);
        }
        return image;
      }
    }.execute();



  }

  private void colorize(Bitmap photo) {
    if(photo!=null) {
      mPalette = Palette.generate(photo);
      // applyPalette();
    }else{
      Log.i("colorize ","invalid phto");
    }
  }

  private void applyPalette() {
    getWindow().setBackgroundDrawable(new ColorDrawable(mPalette.getDarkMutedColor(defaultColorForRipple)));
    mTitleHolder.setBackgroundColor(mPalette.getMutedColor(defaultColorForRipple));
    applyRippleColor(mPalette.getVibrantColor(defaultColorForRipple),
            mPalette.getDarkVibrantColor(defaultColorForRipple));
    mRevealView.setBackgroundColor(mPalette.getLightVibrantColor(defaultColorForRipple));
  }

  private void applyRippleColor(int bgColor, int tintColor) {
    colorRipple(mAddButton, bgColor, tintColor);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private void colorRipple(ImageButton id, int bgColor, int tintColor) {
    View buttonView = id;
    RippleDrawable ripple = (RippleDrawable) buttonView.getBackground();
    GradientDrawable rippleBackground = (GradientDrawable) ripple.getDrawable(0);
    rippleBackground.setColor(bgColor);
    ripple.setColor(ColorStateList.valueOf(tintColor));
  }

  @Override
  public void onClick(View v) {

    Message msg = new Message();
    Bundle data = new Bundle();

    switch (v.getId()) {
      case R.id.btn_add:
        if (!isEditTextVisible) {
          revealEditText(mRevealView);
          mEditTextTodo.requestFocus();
          mInputManager.showSoftInput(mEditTextTodo, InputMethodManager.SHOW_IMPLICIT);
          mAddButton.setImageResource(R.drawable.icn_morp);
          mAnimatable = (Animatable) (mAddButton).getDrawable();
          mAnimatable.start();
          applyRippleColor(getResources().getColor(R.color.highlight_blue), getResources().getColor(R.color.cardview_dark_background));
        } else {
          addToDo(mEditTextTodo.getText().toString());
          mToDoAdapter.notifyDataSetChanged();
          mInputManager.hideSoftInputFromWindow(mEditTextTodo.getWindowToken(), 0);
          hideEditText(mRevealView);
          mAddButton.setImageResource(R.drawable.icn_morph_reverse);
          mAnimatable = (Animatable) (mAddButton).getDrawable();
          mAnimatable.start();
          applyRippleColor(mPalette.getVibrantColor(defaultColorForRipple),
                  mPalette.getDarkVibrantColor(defaultColorForRipple));
        }
        break;
      case R.id.btnLike:
      case R.id.ll_like:


        data.putString("LIKE", Constants.LIKE);
        msg.setData(data);
        HomeActivity.recvMsgHandler.sendMessage(msg);

        break;

      case R.id.btnDisLike:
      case R.id.ll_dislike:

        data.putString("DISLIKE", Constants.DISLIKE);
        msg.setData(data);
        HomeActivity.recvMsgHandler.sendMessage(msg);
        break;

      case R.id.btnShare:
      case R.id.ll_share:

        data.putString("SHARE", Constants.SHARE);
        msg.setData(data);
        HomeActivity.recvMsgHandler.sendMessage(msg);

        break;
    }
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private void revealEditText(LinearLayout view) {
    int cx = view.getRight() - 30;
    int cy = view.getBottom() - 60;
    int finalRadius = Math.max(view.getWidth(), view.getHeight());
    Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
    view.setVisibility(View.VISIBLE);
    isEditTextVisible = true;
    anim.start();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private void hideEditText(final LinearLayout view) {
    int cx = view.getRight() - 30;
    int cy = view.getBottom() - 60;
    int initialRadius = view.getWidth();
    Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);
    anim.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        view.setVisibility(View.INVISIBLE);
      }
    });
    isEditTextVisible = false;
    anim.start();
  }

  @Override
  public void onBackPressed() {

    super.onBackPressed();
    /*AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
    alphaAnimation.setDuration(100);
    mAddButton.startAnimation(alphaAnimation);
    alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @TargetApi(Build.VERSION_CODES.LOLLIPOP)
      @Override
      public void onAnimationEnd(Animation animation) {
        mAddButton.setVisibility(View.GONE);
        finishAfterTransition();
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });*/
  }
}
