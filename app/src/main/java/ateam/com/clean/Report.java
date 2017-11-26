package ateam.com.clean;

/**
 * Created by apple on 26/11/17.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ateam.com.clean.Adapter.ReportAdapter;
import ateam.com.clean.Data.IssueData;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ateam.com.clean.Adapter.ReportAdapter;
import ateam.com.clean.Data.IssueData;

public class Report extends AppCompatActivity implements ValueEventListener {


    private static final String TAG = "Report";
    private static final int REQUEST_CODE = 21;
    private static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static int CAMERA_REQUEST = 1211;
    String mBundle;
    private StorageReference mStorageref;
    private DatabaseReference mDatabase;
    FirebaseUser user;
    String[] user_id;
    String key;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    IssueData issueData;
    List<IssueData> issueDataList;
    LocationManager locationManager;
    String filename = null;
    String url, location, time;
    private ProgressDialog progressDialog;
    Bitmap mBitmap;
    ByteArrayOutputStream mByteOutputStream;
    byte[] bytes;

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
        mDatabase = FirebaseDatabase.getInstance().getReference("issue");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                getReference("issue");
        databaseReference.keepSynced(true);
        //getting the user id created using the email of the user
        user_id = user.getEmail().split("@");

        FirebaseDatabase.getInstance().getReference("issue").child(user_id[0]).child(mBundle).addValueEventListener(this);
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


