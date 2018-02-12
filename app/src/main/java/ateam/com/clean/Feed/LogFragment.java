package ateam.com.clean.Feed;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ateam.com.clean.Data.FeedData;
import ateam.com.clean.R;

/**
 * Project Clean2
 * Created by Anchit Gupta on 12/02/18.
 * Under the MIT License
 */

public class LogFragment extends Fragment implements ValueEventListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "PlaceHolderFragment";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<FeedData> feedDataList;
    private FeedData feedData;
    private TextView textView;


    public LogFragment() {
    }

    public static LogFragment newInstance(int sectionNumber) {
        //FeedActivity.PlaceholderFragment fragment = new FeedActivity.PlaceholderFragment();
        LogFragment fragment = new LogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        //String type = getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER));
        FirebaseDatabase.getInstance().getReference().child("log").child("new").addValueEventListener(this);
        recyclerView = rootView.findViewById(R.id.feed_recycler);
        textView = rootView.findViewById(R.id.text_not);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {


        feedDataList = new ArrayList<>();
        for (DataSnapshot mydata : dataSnapshot.getChildren()) {
            feedData = mydata.getValue(FeedData.class);
            if(feedData.getUrl() != null) {
                feedDataList.add(feedData);
            }
        }

        if(feedDataList.size() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new FeedAdapter(feedDataList, getContext());
            recyclerView.setAdapter(adapter);
        }
        else{
            textView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: " );
    }


}
