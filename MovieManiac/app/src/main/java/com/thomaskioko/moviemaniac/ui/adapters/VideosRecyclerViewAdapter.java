package com.thomaskioko.moviemaniac.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thomaskioko.moviemaniac.R;
import com.thomaskioko.moviemaniac.model.Result;
import com.thomaskioko.moviemaniac.model.VideoResults;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Thomas Kioko
 */
public class VideosRecyclerViewAdapter extends RecyclerView.Adapter<VideosRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private final List<VideoResults> mVideoResultsList;

    /**
     * @param context         Application context
     * @param videoResultses      {@link Result} A list of Video Results
     */
    public VideosRecyclerViewAdapter(Context context, List<VideoResults> videoResultses) {
        mContext = context;
        mVideoResultsList = videoResultses;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_trailer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final VideoResults videoResults = mVideoResultsList.get(position);

        holder.mTrailerName.setText(videoResults.getName());

        //TODO:: Get the background image
        String imagePath = "http://www.youtube.com/watch?v=" + videoResults.getKey();
        Glide.with(mContext)
                .load(imagePath)
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                .into(holder.mImageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + videoResults.getKey()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideoResultsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @Bind(R.id.imageview_thumbnail)
        ImageView mImageView;
        @Bind(R.id.textview_title)
        TextView mTrailerName;

        /**
         * @param view {@link View}
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, mView);
        }
    }
}
