package applabindia.com.tattoojocky.tattoodetail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import applabindia.com.tattoojocky.HomeActivity;
import applabindia.com.tattoojocky.R;
import model.TattooInfo;
import utills.CommonUtill;


public class TattooListAdapter extends RecyclerView.Adapter<TattooListAdapter.ViewHolder> {

  Context mContext;
  OnItemClickListener mItemClickListener;
  ArrayList<TattooInfo.ResponseDato> dataList;
  public  boolean isShowingListView;
  public static final String ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button";

  public TattooListAdapter(Context context,ArrayList<TattooInfo.ResponseDato> dataList,boolean isShowingListView) {
    this.mContext = context;
    this.dataList = dataList;
    this.isShowingListView = isShowingListView;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


    View view = LayoutInflater.from(parent.getContext()).inflate(isShowingListView ? R.layout.item_feed : R.layout.tattoo_row_layout_list, parent,false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    final TattooInfo.ResponseDato data = dataList.get(position);

    holder.placeName.setText(data.getTattooName());
    Picasso.with(mContext).load(data.getTattooImage())
            .error(R.drawable.logo)
            .into(holder.placeImage);
    holder.likeCount.setCurrentText(data.getTattooLikes()
    );
    holder.shareCount.setCurrentText(data.getShareCount());
    holder.viewCount.setCurrentText(data.getTattooViews());
    holder.btnLike.setImageResource(data.getIsLiked()==1 ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_grey);

   /* Bitmap photo = CommonUtill.getBitmapFromUrl(data.getTattooImage(),mContext);

    Palette.generateAsync(photo, new Palette.PaletteAsyncListener() {
      public void onGenerated(Palette palette) {
        int mutedLight = palette.getMutedColor(mContext.getResources().getColor(android.R.color.black));
        holder.placeNameHolder.setBackgroundColor(mutedLight);
      }
    })*/;

    holder.btnLike.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int likeCount = Integer.parseInt(data.getTattooLikes());
        likeCount = ++likeCount;
        data.setTattooLikes(String.valueOf(likeCount));
        data.setIsLiked(1);
        notifyItemChanged(position, ACTION_LIKE_BUTTON_CLICKED);
        if (mContext instanceof HomeActivity) {
          ((HomeActivity) mContext).showLikedSnackbar();
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return dataList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public LinearLayout placeHolder;
    public LinearLayout placeNameHolder;
    public TextView placeName;
    public ImageView placeImage;
    public TextSwitcher likeCount,viewCount,shareCount;
    public  FrameLayout vImageRoot;
    public ImageButton btnLike;

    public ViewHolder(View itemView) {
      super(itemView);
      placeHolder = (LinearLayout) itemView.findViewById(R.id.mainHolder);
      placeName = (TextView) itemView.findViewById(R.id.placeName);
      placeNameHolder = (LinearLayout) itemView.findViewById(R.id.placeNameHolder);
      placeImage = (ImageView) itemView.findViewById(R.id.placeImage);
      likeCount = (TextSwitcher) itemView.findViewById(R.id.tsLikesCounter);
      viewCount = (TextSwitcher) itemView.findViewById(R.id.tsViewCounter);
      shareCount = (TextSwitcher) itemView.findViewById(R.id.tsShareCounter);
      vImageRoot = (FrameLayout) itemView.findViewById(R.id.vImageRoot);
      btnLike = (ImageButton) itemView.findViewById(R.id.btnLike);
      placeHolder.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      if (mItemClickListener != null) {
        mItemClickListener.onItemClick(itemView, getPosition());
      }
    }
  }

  public interface OnItemClickListener {
    void onItemClick(View view, int position);
  }

  public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
    this.mItemClickListener = mItemClickListener;
  }

  public void updateListGridView(boolean isShowingListView){
    this.isShowingListView = isShowingListView;
  }

}
