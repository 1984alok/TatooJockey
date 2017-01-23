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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import applabindia.com.tattoojocky.HomeActivity;
import applabindia.com.tattoojocky.R;
import applabindia.com.tattoojocky.RecyclerViewPositionHelper;
import listener.OnLoadMoreListener;
import model.TattooInfo;
import utills.CommonUtill;


public class TattooListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public static final int VIEW_TYPE_ITEM = 0;
  public static final int VIEW_TYPE_LOADING = 1;
  private OnLoadMoreListener mOnLoadMoreListener;
  private RecyclerView mRecyclerView;
  private int lastVisibleItem, totalItemCount;
  private boolean isLoading;
  private int visibleThreshold = 2;
  RecyclerViewPositionHelper mRecyclerViewHelper;

  Context mContext;
  OnItemClickListener mItemClickListener;
  ArrayList<TattooInfo.ResponseDato> dataList;
  public  boolean isShowingListView;

  public static final String ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button";
  public static final String ACTION_LIKE_IMAGE_CLICKED = "action_like_image_button";
  public static final int VIEW_TYPE_DEFAULT = 1;
  public static final int VIEW_TYPE_LOADER = 2;
  TattooInfo.ResponseDato data;

  public TattooListAdapter(Context context,ArrayList<TattooInfo.ResponseDato> dataList,
                           boolean isShowingListView,RecyclerView mRecyclerView) {
    this.mContext = context;
    this.dataList = dataList;
    this.isShowingListView = isShowingListView;
    this.mRecyclerView = mRecyclerView;
    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
        totalItemCount = mRecyclerViewHelper.getItemCount();
        lastVisibleItem = mRecyclerViewHelper.findLastCompletelyVisibleItemPosition();
        Log.d("lastVisibleItem :: ",mRecyclerViewHelper.findLastCompletelyVisibleItemPosition()+"findLastVisibleItemPosition :: "+mRecyclerViewHelper.findLastVisibleItemPosition());

        if (!isLoading && totalItemCount < (lastVisibleItem + visibleThreshold)) {
          if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore();
          }
          isLoading = true;
        }
      }
    });
  }

  @Override
  public int getItemViewType(int position) {
    return dataList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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
    }
    return null;

  }

  @Override
  public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

    if (holder instanceof ViewHolder) {
      Log.i("adapter","onBindViewHolder ViewHolder::"+position);
      data = dataList.get(position);
      ((ViewHolder) holder).placeName.setText(data.getTattooName());
      Picasso.with(mContext).load(data.getTattooImage())
              .placeholder(R.drawable.progress_animation)
              .error(R.drawable.applogo)
              .into(((ViewHolder) holder).placeImage);
      ((ViewHolder) holder).likeCount.setCurrentText(data.getTattooLikes());
      ((ViewHolder) holder).dislikeCount.setCurrentText(data.getTattooDislikes());
      ((ViewHolder) holder).shareCount.setCurrentText(data.getShareCount());
      ((ViewHolder) holder).btnLike.setImageResource(data.getIsLiked() == 1 ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_grey);

    }else if(holder instanceof LoadingViewHolder) {
      Log.i("adapter","onBindViewHolder LoadingViewHolder::"+position);
      LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
      loadingViewHolder.progressBar.start();
    }
  }

  @Override
  public int getItemCount() {
    return dataList == null ? 0 : dataList.size();
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
