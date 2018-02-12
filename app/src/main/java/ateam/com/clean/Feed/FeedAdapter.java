package ateam.com.clean.Feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Objects;

import ateam.com.clean.Adapter.ReportAdapter;
import ateam.com.clean.Data.FeedData;
import ateam.com.clean.PhotoViewer;
import ateam.com.clean.R;

/**
 * Project Clean2
 * Created by Anchit Gupta on 12/02/18.
 * Under the MIT License
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private static final String TAG = "FEEDADAPTER";
    private List<FeedData> feedDataList;
    private FeedData feedData,  localFeedData;
    private Context context;

    public FeedAdapter(List<FeedData> feedDataList, Context context) {
        this.feedDataList = feedDataList;
        this.context = context;
    }

    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed, parent, false);
        return new FeedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FeedAdapter.ViewHolder holder, final int position) {
        feedData = feedDataList.get(position);

        Log.e(TAG, "POSITION: " + position);
        Log.e(TAG, "onBindViewHolder: " + feedData.getUrl());

        Glide.with(context).load(feedData.getUrl()).into(holder.feedImageView);
        holder.feedKeyValue.setText(feedData.getKey());
        holder.feedLocationValue.setText(feedData.getLatlng() + "\n" + feedData.getLocation());
        holder.feedTypeValue.setText(feedData.getType());
        holder.feedTimeValue.setText(feedData.getTime());
        if (Objects.equals(feedData.getStatus(), "false")) {
            holder.feedStatusValue.setText("Pending");
        } else {
            holder.feedStatusValue.setText("Resolved");
            holder.feedStatusValue.setTextColor(Color.GREEN);
        }
        holder.feedDescriptionValue.setText(feedData.getDesc());
        holder.feedLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "YOU have clicked "+position, Toast.LENGTH_SHORT).show();

            }
        });
        holder.feedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context.getApplicationContext(), PhotoViewer.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localFeedData = feedDataList.get(position);

                intent.putExtra("image", localFeedData.getUrl());

                Log.e(TAG, "POSITION: " + position);
                Log.e(TAG, "onClick: " + localFeedData.getUrl());
                //intent.putExtra(context.getApplicationContext().EXTRA, contact);
                Activity activity = (Activity) context;
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(activity, holder.feedImageView,"profile");
                intent.putExtra("loc", localFeedData.getLocation());
                context.startActivity(intent, options.toBundle());
            }
        });

    }

    @Override
    public int getItemCount() {

        return feedDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView feedKeyValue, feedLocationValue, feedTypeValue, feedDescriptionValue, feedTimeValue, feedStatusValue, feedWorkerValue;
        public ImageView feedImageView;
        public LinearLayout feedLinearLayout;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            feedImageView = itemView.findViewById(R.id.feed_image);
            feedLocationValue = itemView.findViewById(R.id.feed_location_value);
            feedTypeValue = itemView.findViewById(R.id.feed_key_type_value);
            feedKeyValue = itemView.findViewById(R.id.feed_key_value);
            feedDescriptionValue = itemView.findViewById(R.id.feed_description_value);
            feedTimeValue = itemView.findViewById(R.id.feed_time_value);
            feedLinearLayout = itemView.findViewById(R.id.feed_linear);
            cardView = itemView.findViewById(R.id.feed_card);
            feedStatusValue = itemView.findViewById(R.id.feed_status_value);
            feedLocationValue.setMovementMethod(new ScrollingMovementMethod());
            feedWorkerValue = itemView.findViewById(R.id.feed_worker_value);
        }
    }
}
