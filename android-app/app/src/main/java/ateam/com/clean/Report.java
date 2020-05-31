package ateam.com.clean;

/**
 * Created by apple on 26/11/17.
 */

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ateam.com.clean.Adapter.ReportAdapter;
import ateam.com.clean.Data.IssueData;
import ateam.com.clean.Data.User;

public class Report extends AppCompatActivity implements ValueEventListener {


    private static final String TAG = "Report";
    String mBundle;

    FirebaseUser user;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    IssueData issueData;
    List<IssueData> issueDataList;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        /*FirebaseDatabase.getInstance().setPersistenceEnabled(true);*/


        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.report_recycler);
        setSupportActionBar(toolbar);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        /*
        getting @intent value for the type of the request
         */
        mBundle =this.getIntent().getStringExtra("type");
        Log.e("TYPE", mBundle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Report.this, MapReport.class);
                intent.putExtra("type",mBundle);
                startActivity(intent);
            }
        });


        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                getReference("issue");
        databaseReference.keepSynced(true);
        //getting the user id created using the email of the user
        User userN = new User();
        FirebaseDatabase.getInstance().getReference("issue").child(userN.getUserID(user.getEmail())).child(mBundle).addValueEventListener(this);
        //cacahing the data

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        issueDataList = new ArrayList<>();
        for (DataSnapshot mydata : dataSnapshot.getChildren()) {

            issueData = mydata.getValue(IssueData.class);
            issueDataList.add(issueData);


        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportAdapter(issueDataList, this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(this, "ERROR :" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }

}


