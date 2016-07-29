package com.thomaskioko.moviemaniac.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.thomaskioko.moviemaniac.MovieManiacApplication;
import com.thomaskioko.moviemaniac.R;
import com.thomaskioko.moviemaniac.model.Result;
import com.thomaskioko.moviemaniac.ui.MovieDetailActivity;
import com.thomaskioko.moviemaniac.ui.fragments.MovieDetailFragment;
import com.thomaskioko.moviemaniac.util.ApplicationConstants;

import java.util.List;

/**
 * @author Thomas Kioko
 */
public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MoviesRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private FragmentManager mFragmentManager;
    private boolean mTwoPane;
    private final List<Result> mResultList;

    /**
     * @param context         Application context
     * @param fragmentManager {@link FragmentManager}
     * @param isTwoPane       {@link Boolean} Where the view is a two pane(Tablet view)
     * @param resultList      {@link Result} A list of Movie Results
     */
    public MoviesRecyclerViewAdapter(Context context, FragmentManager fragmentManager, boolean isTwoPane,
                                     List<Result> resultList) {
        mContext = context;
        mFragmentManager = fragmentManager;
        mTwoPane = isTwoPane;
        mResultList = resultList;
        MovieManiacApplication.isTwoPane = isTwoPane;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Result movieResult = mResultList.get(position);

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
                MovieManiacApplication.result = movieResult;
                if (mTwoPane) {
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    mFragmentManager.beginTransaction()
                            .replace(R.id.movie_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MovieDetailActivity.class);
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
        public final ImageView mImageView;

        /**
         * @param view {@link View}
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.imageView);
        }
    }
}
