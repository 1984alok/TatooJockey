package applabindia.com.tattoojocky.tattoodetail;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import applabindia.com.tattoojocky.HomeActivity;
import applabindia.com.tattoojocky.R;
import applabindia.com.tattoojocky.RecyclerViewPositionHelper;
import listener.OnLoadMoreListener;
import model.TattooInfo;
import utills.CommonUtill;
import utills.Constants;


public class TattooListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public static final int VIEW_TYPE_ITEM = 0;
  public static final int VIEW_TYPE_LOADING = 1;
  public static final int NATIVE_EXPRESS_AD_VIEW_TYPE=2;
  private OnLoadMoreListener mOnLoadMoreListener;
  private RecyclerView mRecyclerView;
  private int lastVisibleItem, totalItemCount;
  private boolean isLoading;
  private int visibleThreshold = 2;
  public RecyclerViewPositionHelper mRecyclerViewHelper;

  Context mContext;
  HomeActivity activity;
  OnItemClickListener mItemClickListener;
  ArrayList<TattooInfo.ResponseDato> dataList;
  public  boolean isShowingListView;

  public static final String ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button";
  public static final String ACTION_LIKE_IMAGE_CLICKED = "action_like_image_button";
  public static final int VIEW_TYPE_DEFAULT = 1;
  public static final int VIEW_TYPE_LOADER = 2;
  TattooInfo.ResponseDato data;
  public boolean allLoaded;


  public TattooListAdapter(Context context,ArrayList<TattooInfo.ResponseDato> dataList,
                           boolean isShowingListView,RecyclerView mRecyclerView) {
    this.mContext = context;
    activity = (HomeActivity)mContext;
    this.dataList = dataList;
    this.isShowingListView = isShowingListView;
    this.mRecyclerView = mRecyclerView;
    allLoaded = false;
    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
        totalItemCount = mRecyclerViewHelper.getItemCount();
        lastVisibleItem = mRecyclerViewHelper.findLastCompletelyVisibleItemPosition();
        Log.d("lastVisibleItem :: ",mRecyclerViewHelper.findLastCompletelyVisibleItemPosition()+"findLastVisibleItemPosition :: "+mRecyclerViewHelper.findLastVisibleItemPosition());

        if (!isLoading && totalItemCount < (lastVisibleItem + visibleThreshold)) {
          Log.d("Calling..","prev allLoaded ::"+allLoaded);
          if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore();
            Log.d("Calling..","onLoadMore");
          }
          isLoading = true;
        }
      }
    });
  }

  @Override
  public int getItemViewType(int position) {
    // return dataList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    if(dataList.get(position)==null){
      return VIEW_TYPE_LOADING;
    }else if(dataList.get(position).getTattooCateId().equals(Constants.ADD_CATG)){
      return  NATIVE_EXPRESS_AD_VIEW_TYPE;
    }else{
      return VIEW_TYPE_ITEM;
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Log.i("adapter","onCreateViewHolder::"+viewType);
    if (viewType == VIEW_TYPE_ITEM) {

      View view = LayoutInflater.from(parent.getContext()).inflate(isShowingListView ? R.layout.item_feed : R.layout.tattoo_row_layout_list, parent,false);
      return new ViewHolder(view);

    } else if (viewType == VIEW_TYPE_LOADING) {

      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
      return new LoadingViewHolder(view);
    }else if(viewType==NATIVE_EXPRESS_AD_VIEW_TYPE){
      View nativeExpressLayoutView = LayoutInflater.from(
              parent.getContext()).inflate(R.layout.native_express_ad_container,
              parent, false);
      return new NativeExpressAdViewHolder(nativeExpressLayoutView);
    }
    return null;

  }

  @Override
  public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

    data = dataList.get(position);
    if(data!=null) {
      if (position > 4 && position % 4 == 0 && !data.getTattooCateId().equals(Constants.ADD_CATG) && isShowingListView) {
        activity.addAddvertiseTattoo(position);
      } else if (data.getTattooCateId().equals(Constants.ADD_CATG) && !isShowingListView) {
        activity.removeAddvertiseTattoo(position);
      }
    }


    if (holder instanceof ViewHolder) {
      Log.i("adapter","onBindViewHolder ViewHolder::"+position);
      if(data!=null) {
        ((ViewHolder) holder).placeName.setText(data.getTattooName());
        Picasso.with(mContext).load(data.getTattooImage())
               // .placeholder(R.drawable.progress_animation)
                .error(R.drawable.applogo)
                .into(((ViewHolder) holder).placeImage);
        ((ViewHolder) holder).likeCount.setCurrentText(data.getTattooLikes());
        ((ViewHolder) holder).dislikeCount.setCurrentText(data.getTattooDislikes());
        ((ViewHolder) holder).shareCount.setCurrentText(data.getShareCount());
        ((ViewHolder) holder).btnLike.setImageResource(data.getIsLiked() == 1 ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_grey);
      }
    }else if(holder instanceof LoadingViewHolder) {
      Log.i("adapter","onBindViewHolder LoadingViewHolder::"+position);
      LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
      loadingViewHolder.progressBar.start();
    }else if(holder instanceof NativeExpressAdViewHolder){

      NativeExpressAdViewHolder addHolder = (NativeExpressAdViewHolder) holder;
      // Set its video options.
      addHolder. mAdView.setVideoOptions(new VideoOptions.Builder()
              .setStartMuted(true)
              .build());

      // The VideoController can be used to get lifecycle events and info about an ad's video
      // asset. One will always be returned by getVideoController, even if the ad has no video
      // asset.
      final VideoController mVideoController = addHolder.mAdView.getVideoController();
      mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
        @Override
        public void onVideoEnd() {
          Log.d("Add", "Video playback is finished.");
          super.onVideoEnd();
        }
      });

      // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
      // loading.
      addHolder.mAdView.setAdListener(new AdListener() {
        @Override
        public void onAdLoaded() {
          if (mVideoController.hasVideoContent()) {
            Log.d("Add", "Received an ad that contains a video asset.");
          } else {
            Log.d("Add", "Received an ad that does not contain a video asset.");
          }
        }
      });

      addHolder.mAdView.loadAd(new AdRequest.Builder()
             // .addTestDevice("736B8BBE5F5D34A682BB1FB44A04DABD")
              .build());
    }
  }

  @Override
  public int getItemCount() {
    return dataList == null ? 0 : dataList.size();
  }




  /**
   * The {@link NativeExpressAdViewHolder} class.
   */
  public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

    NativeExpressAdView mAdView;
    VideoController mVideoController;

    NativeExpressAdViewHolder(View view) {
      super(view);
      mAdView = (NativeExpressAdView)view.findViewById(R.id.adView);
    }
  }

  public class LoadingViewHolder extends RecyclerView.ViewHolder {
    public com.victor.loading.rotate.RotateLoading progressBar;

    public LoadingViewHolder(View itemView) {
      super(itemView);
      progressBar = (com.victor.loading.rotate.RotateLoading) itemView.findViewById(R.id.progressBar1);
    }
  }


  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    //  public LinearLayout placeHolder;
    public LinearLayout placeNameHolder;
    public TextView placeName;
    public ImageView placeImage;
    public TextSwitcher likeCount,shareCount,dislikeCount;
    public  FrameLayout vImageRoot;
    public ImageButton btnLike,btnShare,btnDislike;
    public View vBgLike;
    public ImageView ivLike;

    public LinearLayout ll_like;
    public LinearLayout ll_dislike;
    public LinearLayout ll_share;

    public ViewHolder(View itemView) {
      super(itemView);
      // placeHolder = (LinearLayout) itemView.findViewById(R.id.mainHolder);
      placeName = (TextView) itemView.findViewById(R.id.placeName);
      placeNameHolder = (LinearLayout) itemView.findViewById(R.id.placeNameHolder);
      placeImage = (ImageView) itemView.findViewById(R.id.placeImage);
      likeCount = (TextSwitcher) itemView.findViewById(R.id.tsLikesCounter);
      shareCount = (TextSwitcher) itemView.findViewById(R.id.tsShareCounter);
      dislikeCount = (TextSwitcher) itemView.findViewById(R.id.tsDsLikesCounter);

      vImageRoot = (FrameLayout) itemView.findViewById(R.id.vImageRoot);
      btnLike = (ImageButton) itemView.findViewById(R.id.btnLike);
      btnDislike = (ImageButton) itemView.findViewById(R.id.btnDisLike);

      ll_like = (LinearLayout) itemView.findViewById(R.id.ll_like);
      ll_dislike = (LinearLayout) itemView.findViewById(R.id.ll_dislike);
      ll_share = (LinearLayout) itemView.findViewById(R.id.ll_share);

      btnShare = (ImageButton) itemView.findViewById(R.id.btnShare);
      vBgLike = (View) itemView.findViewById(R.id.vBgLike);
      ivLike = (ImageView) itemView.findViewById(R.id.ivLike);
      placeImage.setOnClickListener(this);

      ll_like.setOnClickListener(this);
      ll_share.setOnClickListener(this);
      ll_dislike.setOnClickListener(this);

      btnShare.setOnClickListener(this);
      btnLike.setOnClickListener(this);
      btnDislike.setOnClickListener(this);


    }

    public TattooInfo.ResponseDato getItem(){
      return data;
    }

    @Override
    public void onClick(View v) {
      if (mItemClickListener != null) {
        mItemClickListener.onItemClick(v,itemView, getPosition());
      }
    }
  }

  public interface OnItemClickListener {
    void onItemClick(View clickView,View view, int position);
  }

  public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
    this.mItemClickListener = mItemClickListener;
  }

  public void updateListGridView(boolean isShowingListView){
    this.isShowingListView = isShowingListView;
  }


  public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
    this.mOnLoadMoreListener = mOnLoadMoreListener;
  }

  public void setLoaded() {
    isLoading = false;
  }


}
