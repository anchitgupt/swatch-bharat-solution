package ateam.com.clean.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;

import ateam.com.clean.Data.IssueData;
import ateam.com.clean.PhotoViewer;
import ateam.com.clean.R;
import ateam.com.clean.Report;

/**
 * Created by apple on 19/11/17.
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private List<IssueData> issueDataList;
    private Context context;
    private IssueData issueData, localIssueData;
    private static String TAG = "ReportAdpater";

    public ReportAdapter(List<IssueData> issueDataList, Context context) {
        this.issueDataList = issueDataList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        issueData = issueDataList.get(position);

        Log.e(TAG, "POSITION: " + position);
        Log.e(TAG, "onBindViewHolder: " + issueData.getUrl());

            Glide.with(context).load(issueData.getUrl()).into(holder.reportImageView);
            holder.reportKeyValue.setText(issueData.getKey());
            holder.reportLocationValue.setText(issueData.getLatlng() + "\n" + issueData.getLocation());
            holder.reportTypeValue.setText(issueData.getType());
            holder.reportTimeValue.setText(issueData.getTime());
            if (Objects.equals(issueData.getStatus(), "false")) {
                holder.reportStatusValue.setText("Pending");
            } else {
                holder.reportStatusValue.setText("Resolved");
                holder.reportStatusValue.setTextColor(Color.GREEN);
            }
            holder.reportDescriptionValue.setText(issueData.getDesc());
            holder.reportLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context, "YOU have clicked "+position, Toast.LENGTH_SHORT).show();

                }
            });
            holder.reportImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context.getApplicationContext(), PhotoViewer.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    localIssueData = issueDataList.get(position);

                    intent.putExtra("image", localIssueData.getUrl());

                    Log.e(TAG, "POSITION: " + position);
                    Log.e(TAG, "onClick: " + localIssueData.getUrl());

                    intent.putExtra("loc", localIssueData.getLocation());
                    context.startActivity(intent);
                }
            });

    }

    @Override
    public int getItemCount() {
//        Log.e(TAG, "getItemCount: "+issueDataList.size() );
        return issueDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView reportKeyValue, reportLocationValue, reportTypeValue, reportDescriptionValue, reportTimeValue, reportStatusValue;
        public ImageView reportImageView;
        public LinearLayout reportLinearLayout;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            reportImageView = itemView.findViewById(R.id.report_image);
            reportLocationValue = itemView.findViewById(R.id.report_location_value);
            reportTypeValue = itemView.findViewById(R.id.report_key_type_value);
            reportKeyValue = itemView.findViewById(R.id.report_key_value);
            reportDescriptionValue = itemView.findViewById(R.id.report_description_value);
            reportTimeValue = itemView.findViewById(R.id.report_time_value);
            reportLinearLayout = itemView.findViewById(R.id.report_linear);
            cardView = itemView.findViewById(R.id.report_card);
            reportStatusValue = itemView.findViewById(R.id.report_status_value);
            reportLocationValue.setMovementMethod(new ScrollingMovementMethod());
        }
    }
}
