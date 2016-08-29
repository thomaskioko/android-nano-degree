package com.example.xyzreader.ui.adapter;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.ui.ArticleListActivity;
import com.example.xyzreader.ui.util.DynamicHeightNetworkImageView;


public class ArticleListAdapter extends CursorRecyclerViewAdapter<ArticleListAdapter.ViewHolder> {
    private ArticleListActivity mArticleListActivity;

    /**
     * Constructor
     *
     * @param cursor Cursor
     */
    public ArticleListAdapter(ArticleListActivity activity, Cursor cursor) {
        super(cursor);
        mArticleListActivity = activity;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public DynamicHeightNetworkImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (DynamicHeightNetworkImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_article, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW, ItemsContract.Items.buildItemUri(getItemId(viewHolder.getAdapterPosition())));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    parent.getContext().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(mArticleListActivity, viewHolder.thumbnailView,
                            viewHolder.thumbnailView.getTransitionName()).toBundle()
                    );
                } else {
                    parent.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                            ItemsContract.Items.buildItemUri(getItemId(viewHolder.getAdapterPosition()))));
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        holder.titleView.setText(cursor.getString(ArticleLoader.Query.TITLE));
        holder.subtitleView.setText(
                DateUtils.getRelativeTimeSpanString(
                        cursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                        System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL).toString()
        );

        holder.thumbnailView.setAspectRatio(cursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
        Glide.clear(holder.thumbnailView);

        Glide.with(holder.thumbnailView.getContext())
                .load(cursor.getString(ArticleLoader.Query.THUMB_URL))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                                   Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        Bitmap bitmap = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();
                        Palette palette = Palette.generate(bitmap);
                        int defaultColor = 0xFF333333;
                        int color = palette.getDarkMutedColor(defaultColor);
//                        holder.itemView.setBackgroundColor(color);
                        return false;
                    }
                })
                .into(holder.thumbnailView);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }


}