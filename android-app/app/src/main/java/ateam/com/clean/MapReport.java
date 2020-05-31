package ateam.com.clean;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ateam.com.clean.Data.AdminData;
import ateam.com.clean.Data.IssueData;
import ateam.com.clean.Data.User;

public class MapReport extends AppCompatActivity implements View.OnClickListener {


    private static int CAMERA_REQUEST = 1211;
    private static final int REQUEST_PLACE_PICKER = 1;
    private static final String TAG = "MapReport";
    private ImageView imageView;
    private TextView textLocation, textTime;
    private EditText editDes;
    String latlng;
    IssueData issueData;
    Button submitButton;
    Bitmap imageURL;
    boolean b, isphotoTaken;
    LocationManager locationManager;
    Bitmap mBitmap;
    ByteArrayOutputStream mByteOutputStream;
    byte[] bytes;
    private StorageReference mStorageref;
    private DatabaseReference mDatabase;
    FirebaseUser user;
    String[] user_id;
    String key;
    String location;
    String mBundle;
    String time;
    String filename;
    ProgressDialog progressDialog;
    User userN;
    String statusIssue = "false";
    AdminData adminData;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_report);

        imageView = findViewById(R.id.dialog_image_view);
        textLocation = findViewById(R.id.dialog_approx_location);
        textTime = findViewById(R.id.dialog_time);
        editDes = findViewById(R.id.dialog_des);
        submitButton = findViewById(R.id.dialog_button);
        b = false;
        isphotoTaken = false;

        Date date = Calendar.getInstance().getTime();
        DateTime dt = new DateTime(date);
        Log.e(TAG, String.valueOf(dt.getDayOfMonth()+"/"+dt.getMonthOfYear()+"/"+dt.getYear()));
        Log.e(TAG, String.valueOf(dt.getHourOfDay()+":"+dt.getMinuteOfHour()));
        time = String.valueOf(dt.getDayOfMonth()+"/"+dt.getMonthOfYear()+"/"+dt.getYear());
        textTime.setText(String.valueOf(dt.getDayOfMonth()+"/"+dt.getMonthOfYear()+"/"+dt.getYear()) + ";" +
                String.valueOf(dt.getHourOfDay()+":"+dt.getMinuteOfHour()));
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        imageView.setOnClickListener(this);
        textLocation.setOnClickListener(this);
        submitButton.setOnClickListener(this);


        mBundle = getIntent().getStringExtra("type");

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("issue");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                getReference("issue");
        databaseReference.keepSynced(true);
    }

    @Override
    public void onClick(View view) {
        if (view == textLocation) {
            b = true;
            try {
                PlacePicker.IntentBuilder intentBuilder =
                        new PlacePicker.IntentBuilder();
                Intent intent = intentBuilder.build(this);
                // Start the intent by requesting a result,
                // identified by a request code.
                startActivityForResult(intent, REQUEST_PLACE_PICKER);

            } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            }

        }
        if (view == submitButton) {
            if (!b) {
                textLocation.setError("Click here");
                Toast.makeText(this, "Choose the location", Toast.LENGTH_SHORT).show();
            } else if (!isphotoTaken) {
                Toast.makeText(this, "Upload Image", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "onClick: Clicked");
                storeImageInCloud();

            }
        }

        if (view == imageView) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                showGPSDisabledAlertToUser();
            else {
                Intent intent = new Intent();
                intent.setAction("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                filename = getFilename();
                startActivityForResult(intent, CAMERA_REQUEST);

            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();

            LatLng lln = place.getLatLng();
            latlng = String.valueOf(lln.latitude+";"+lln.longitude);
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }

            location = name + "," + address + "," + Html.fromHtml(attributions);
            textLocation.setText(name + "," + address + "," + Html.fromHtml(attributions));

        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            isphotoTaken = true;
            mBitmap = (Bitmap) data.getExtras().get("data");
            mByteOutputStream = new ByteArrayOutputStream();
            Bitmap.createBitmap(mBitmap).compress(Bitmap.CompressFormat.JPEG, 100, mByteOutputStream);
            bytes = mByteOutputStream.toByteArray();
            imageView.setImageBitmap(mBitmap);
        }
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

    public void storeImageInCloud() {

        //filenaming in storage
            /*
            getting the token as @key for each complaint
             */
        key = mDatabase.push().getKey();
        // url = data.getStringExtra("url");
        /*location = data.getStringExtra("location");
        time = data.getStringExtra("time");*/
        //bytes = data.getByteArrayExtra("image");


        Log.e(TAG, "onSuccess: " + location);
        Log.e(TAG, "onSuccess: " + time);
        Log.e(TAG, "key :" + key);
            /*
            setting the time for each photo that is taken
             */
        userN = new User();
        mStorageref = FirebaseStorage.getInstance().getReference(userN.getUserID(user.getEmail())).child(key).child(filename);
        /**
         *
         * getting @intent data to store image in local directory also
         * and getting the data stored in the storage
         *creating bitmap for the image
         */

        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setCancelable(false);

        UploadTask uploadTask = mStorageref.putBytes(bytes);

        uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//TODO: changes done
                final Uri[] downloadurl = {null};
                 taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        downloadurl[0] = task.getResult();
                        Log.e(TAG, "onSuccess: " + location);
                        Log.e(TAG, "onSuccess: " + time);

                        /*
                         *
                         * User data updated
                         */
                        issueData = new IssueData(String.valueOf(downloadurl[0]), latlng,location, key, mBundle, time,editDes.getText().toString(), statusIssue);

                        mDatabase.child(userN.getUserID(user.getEmail())).child(mBundle).child(key).setValue(issueData)
                                .addOnCompleteListener(MapReport.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isComplete()) {
                                            Log.e(TAG, "KEY VALUE ADDED");
                                            Toast.makeText(MapReport.this, "User Value Updated", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(MapReport.this, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "onFailure: " + e.getMessage());
                                    }
                                });

                        /**
                         *
                         * Admin data updated
                         */
                        adminData = new AdminData(
                                String.valueOf(downloadurl[0]),
                                latlng,
                                location,
                                key,
                                mBundle,
                                time,
                                editDes.getText().toString(),
                                "",
                                user.getEmail(),
                                "false"
                        );

                        FirebaseDatabase.getInstance().getReference(mBundle).child("new").child(key).setValue(adminData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.e(TAG, "onComplete: admin data");
                                            Toast.makeText(MapReport.this, "Admin received Data", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: no admin data" + e.getMessage());

                            }
                        });


                        //Log.e(TAG, downloadurl.toString(), new Throwable("ERROR GETTING THE URL"));

                        Toast.makeText(MapReport.this, "FILE UPLODED SUCESSFULLY", Toast.LENGTH_SHORT).show();
                        finish();
                        Log.e(TAG, "onSuccess: FILE UPLOADED SUCESSFULLY");
                        progressDialog.dismiss();
                    }
                });//getUploadSessionUri();



            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MapReport.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnProgressListener(this, new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double per = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setMessage((int) per + " % completed...");
            }
        });
            /*
            uploading the photo in the storage
             */


    }

    private String getFilename() {
        return new SimpleDateFormat("hh_mm_ss_dd_MM_yyyy", Locale.getDefault()).format(new Date()) + ".jpeg";
    }

}
