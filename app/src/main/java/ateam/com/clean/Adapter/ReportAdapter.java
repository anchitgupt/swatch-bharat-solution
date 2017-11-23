package ateam.com.clean.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.zip.Inflater;

import ateam.com.clean.Data.IssueData;
import ateam.com.clean.R;

/**
 * Created by apple on 19/11/17.
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder>{

    private List<IssueData> issueDataList;
    private Context context;
    private IssueData issueData;
    private static String TAG ="ReportAdpater";

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
        Log.e(TAG, "onBindViewHolder: "+issueData.getUrl() );
        Glide.with(context).load(issueData.getUrl()).into(holder.reportImageView);
        holder.reportKeyValue.setText(issueData.getKey());
        holder.reportLocationValue.setText(issueData.getLocation());
        holder.reportTypeValue.setText(issueData.getType());
        holder.reportLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "YOU have clicked "+position, Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public int getItemCount() {
//        Log.e(TAG, "getItemCount: "+issueDataList.size() );
        return issueDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView reportKeyValue, reportLocationValue, reportTypeValue;
        public ImageView reportImageView;
        public LinearLayout reportLinearLayout;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            reportImageView =  itemView.findViewById(R.id.report_image);
            reportLocationValue = itemView.findViewById(R.id.report_location_value);
            reportTypeValue = itemView.findViewById(R.id.report_key_type_value);
            reportKeyValue = itemView.findViewById(R.id.report_key_value);
            reportLinearLayout = itemView.findViewById(R.id.report_linear);
            cardView = itemView.findViewById(R.id.report_card);
        }
    }
}
