package com.thomaskioko.moviemaniac.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.thomaskioko.moviemaniac.R;
import com.thomaskioko.moviemaniac.util.ApplicationConstants;

/**
 * Favorite Adapter class that implements {@link CursorAdapter}
 *
 * @author Thomas Kioko
 */
public class FavoritesAdapter extends CursorAdapter {

    private FragmentManager mFragmentManager;


    /**
     * Constructor.
     *
     * @param context         Application context
     * @param cursor          Cursor containing data
     * @param flags           int
     * @param fragmentManager {@link FragmentManager}
     */
    public FavoritesAdapter(Context context, Cursor cursor, int flags, FragmentManager fragmentManager) {
        super(context, cursor, flags);
        mFragmentManager = fragmentManager;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_content, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        //Format image URL
        String imagePath = ApplicationConstants.TMDB_IMAGE_URL
                + ApplicationConstants.IMAGE_SIZE_500
                + cursor.getString(ApplicationConstants.COL_MOVIE_POSTER_PATH);

        //Load the image using glide
        Glide.with(context)
                .load(imagePath)
                .centerCrop()
                .error(R.mipmap.ic_launcher) //In case there's an error, display the launcher icon
                .into(viewHolder.mImageView);

    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final View mView;
        public final ImageView mImageView;

        /**
         * @param view {@link View}
         */
        public ViewHolder(View view) {
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.imageView);
        }
    }

}
