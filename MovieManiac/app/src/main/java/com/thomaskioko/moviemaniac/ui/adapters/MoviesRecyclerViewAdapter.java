package com.thomaskioko.moviemaniac.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.thomaskioko.moviemaniac.R;
import com.thomaskioko.moviemaniac.interfaces.MovieDetailCallback;
import com.thomaskioko.moviemaniac.model.Result;
import com.thomaskioko.moviemaniac.ui.MovieDetailActivity;
import com.thomaskioko.moviemaniac.ui.MovieListActivity;
import com.thomaskioko.moviemaniac.util.ApplicationConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Thomas Kioko
 */
public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MoviesRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private final List<Result> mResultList;
    private int mListPosition = 0;
    private MovieDetailCallback movieDetailCallback;

    /**
     * @param context    Application context
     * @param resultList {@link Result} A list of Movie Results
     */
    public MoviesRecyclerViewAdapter(Context context, List<Result> resultList) {
        mContext = context;
        mResultList = resultList;
    }

    public void setMovieDetailCallback(MovieDetailCallback callback) {
        this.movieDetailCallback = callback;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Result movieResult = mResultList.get(position);
        mListPosition = position;

        String imagePath = ApplicationConstants.TMDB_IMAGE_URL
                + ApplicationConstants.IMAGE_SIZE_500
                + movieResult.getPosterPath();

        Glide.with(mContext)
                .load(imagePath)
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                .into(holder.mImageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MovieListActivity.isTwoPane) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("data", mResultList.get(position));
                    movieDetailCallback.CallbackRequest(ApplicationConstants.CALLBACK_MOVIE_BUNDLE, bundle);
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MovieDetailActivity.class);
                    intent.putExtra("data", mResultList.get(position));
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @Bind(R.id.imageView)
        ImageView mImageView;

        /**
         * @param view {@link View}
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }

    /**
     * Method to get the selected list position
     *
     * @return List Position
     */
    public int getPosition() {
        return mListPosition;
    }

    /**
     * @param add             {@link Boolean} True/False
     * @param resultArrayList {@link Result} a list of movie objects
     */
    public void reloadRecyclerView(boolean add, ArrayList<Result> resultArrayList) {
        if (!add) mResultList.clear();
        if (resultArrayList.size() > 0) mResultList.addAll(resultArrayList);
        notifyDataSetChanged();
    }
}
