package ateam.com.clean;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
    private static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static int CAMERA_REQUEST = 121;
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
    File folder;
    File file;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.report_recycler);
        setSupportActionBar(toolbar);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        /*
        getting @intent value for the type of the request
         */
        mBundle = getIntent().getStringExtra("type");
        Log.e("TYPE", mBundle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    showGPSDisabledAlertToUser();
                else {
                    Intent intent = new Intent();
                    intent.setAction("android.media.action.IMAGE_CAPTURE");
                    folder = new File(PATH + "/Clean");
                    if (!folder.exists())
                        folder.mkdir();
                    filename = getFilename();
                    file = new File(PATH + "/Clean", filename);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(intent, CAMERA_REQUEST);
                }
            }
        });


        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("issue");
        //getting the user id created using the email of the user
        user_id = user.getEmail().split("@");
        FirebaseDatabase.getInstance().getReference("issue").child(user_id[0]).child(mBundle).addValueEventListener(this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Log.e(TAG, "Activity received");
            storeImageInCloud();
        }
    }

    public void storeImageInCloud() {

        //filenaming in storage
            /*
            getting the token as @key for each complaint
             */
        key = mDatabase.push().getKey();

        Log.e(TAG, "key :" + key);
            /*
            setting the time for each photo that is taken
             */

        mStorageref = FirebaseStorage.getInstance().getReference(user_id[0]).child(key).child(filename);
        /**
         * getting @intent data to store image in local directory also
         * and getting the data stored in the storage
         *creating bitmap for the image
         */
        Bitmap mBitmap;
        try {
            mBitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream mByteOutputStream = new ByteArrayOutputStream();
        //Bitmap.createBitmap(mBitmap).compress(Bitmap.CompressFormat.JPEG, 100, mByteOutputStream);
        byte[] bytes = mByteOutputStream.toByteArray();

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        // progressDialog.show();
        UploadTask uploadTask = mStorageref.putBytes(bytes);

            /*
            uploading the photo in the storage
             */
        uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Uri downloadurl = taskSnapshot.getDownloadUrl();

                //progressDialog.setProgress(taskSnapshot.getBytesTransferred()*100/taskSnapshot.getTotalByteCount());
                    /*
                    IssueData class is the class which handles
                     */
                issueData = new IssueData(String.valueOf(downloadurl),
                            /*address+", "+city*/"Moradabad", key, mBundle);

                    /*
                    Creating the databaserreference in the firebase database for the
                    the user complaint as hirarchy
                    issue-|
                          -user_id
                            - garbage
                                -  @key
                                    -user_id
                                    -location
                                    -url
                                    -@key
                           - pit
                                -  @key
                                    -user_id
                                    -location
                                    -url
                                    -@key
                     */

                mDatabase.child(user_id[0]).child(mBundle).child(key).setValue(issueData)
                        .addOnCompleteListener(Report.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Log.e(TAG, "KEY VALUE ADDED");

                                }
                            }
                        })
                        .addOnFailureListener(Report.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: " + e.getMessage());
                            }
                        });

                Log.e(TAG, downloadurl.toString(), new Throwable("ERROR GETTING THE URL"));

                Toast.makeText(Report.this, "FILE UPLODED SUCESSFULLY", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onSuccess: FILE UPLOADED SUCESSFULLY");

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.cancel();
                Toast.makeText(Report.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }).addOnProgressListener(this, new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double per = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setMessage((int) per + " % completed...");
            }
        });

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        issueDataList = new ArrayList<>();
        for (DataSnapshot mydata : dataSnapshot.getChildren()
                ) {
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

    /**
     * using the AlertDialog to
     * enable the GPS services in the
     * given device
     */
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);

                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private String getFilename() {
        return new SimpleDateFormat("hh_mm_ss_dd_MM_yyyy", Locale.getDefault()).format(new Date()) + ".jpeg";
    }

}
 /*
                    *
                    * Experimental feature to get the location of the user
                    *
                     */
//->
                    /*Geocoder geocoder;
                    List<Address> addresses = null;
                    geocoder = new Geocoder(Report.this, Locale.getDefault());
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(Report.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(Report.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location location
                            = null;
                    ProgressDialog dialog;
                    dialog = new ProgressDialog(Report.this);
                    dialog.create();
                   while (lm != null) {
                        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                    dialog.cancel();
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();*/

//-->