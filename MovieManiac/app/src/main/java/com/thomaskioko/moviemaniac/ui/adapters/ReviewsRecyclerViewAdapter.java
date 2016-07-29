package com.thomaskioko.moviemaniac.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thomaskioko.moviemaniac.R;
import com.thomaskioko.moviemaniac.model.Result;
import com.thomaskioko.moviemaniac.model.ReviewResults;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Thomas Kioko
 */
public class ReviewsRecyclerViewAdapter extends RecyclerView.Adapter<ReviewsRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private final List<ReviewResults> mReviewResultsList;

    /**
     * @param context           Application context
     * @param reviewResultsList {@link Result} A list of Movie Results
     */
    public ReviewsRecyclerViewAdapter(Context context, List<ReviewResults> reviewResultsList) {
        mContext = context;
        mReviewResultsList = reviewResultsList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ReviewResults reviewResults = mReviewResultsList.get(position);

        holder.mReviewAuthor.setText(reviewResults.getAuthor());
        holder.mReviewContent.setText(reviewResults.getContent());

        holder.mReviewReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display Full review
                showCompleteReviewDialog(reviewResults.getAuthor(), reviewResults.getContent());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReviewResultsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @Bind(R.id.review_author)
        TextView mReviewAuthor;
        @Bind(R.id.review_content)
        TextView mReviewContent;
        @Bind(R.id.review_read_more)
        TextView mReviewReadMore;

        /**
         * @param view {@link View}
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, mView);
        }
    }

    /**
     * Method to display method dialog with complete review details
     *
     * @param author  Author name
     * @param content Author comments.
     */
    protected void showCompleteReviewDialog(String author, String content) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        alertDialogBuilder.setIcon(mContext.getResources().getDrawable(R.mipmap.ic_launcher));
        alertDialogBuilder.setTitle(author);
        alertDialogBuilder
                .setMessage(content)
                .setCancelable(true)
                .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }
}
