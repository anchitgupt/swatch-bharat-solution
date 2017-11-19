package ateam.com.clean;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ateam.com.clean.Data.IssueData;

public class Report extends AppCompatActivity {

    private static final String TAG = "Report";
    private static String CLASSNAME = "Report";
    private static int CAMERA_REQUEST =121;
    String mBundle;
    private StorageReference mStorageref;
    private DatabaseReference mDatabase;
    FirebaseUser user;
    String[] user_id;
    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*
        getting @intent value for the type of the request
         */
        mBundle =getIntent().getStringExtra("type");
        Log.e("TYPE",mBundle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.media.action.IMAGE_CAPTURE");
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent,CAMERA_REQUEST);
            }
        });


        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("issue");
        //getting the user id created using the email of the user
        user_id = user.getEmail().split("@");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(/*requestCode == CAMERA_REQUEST && resultCode == RESULT_OK*/true){
            Log.e(CLASSNAME, "Activity received");
            //filenaming in storage
            /*
            getting the token as @key for each complaint
             */
            key = mDatabase.push().getKey();
            Log.e(TAG, "key :"+key);
            /*
            setting the time for each photo that is taken
             */
            String filname = new SimpleDateFormat("hh_mm_ss_dd_MM_yyyy", Locale.getDefault()).format(new Date());
            mStorageref = FirebaseStorage.getInstance().getReference(user_id[0]).child(key).child(filname);

            /*
            creating bitmap for the image
             */
            Bitmap mBitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream mByteOutputStream = new ByteArrayOutputStream();
            Bitmap.createBitmap(mBitmap).compress(Bitmap.CompressFormat.JPEG, 100,mByteOutputStream );



            byte[] bytes = mByteOutputStream.toByteArray();
            UploadTask uploadTask = mStorageref.putBytes(bytes);

            /*
            uploading the photo in the storage

             */
            uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadurl = taskSnapshot.getDownloadUrl();

                    /*
                    IssueData class is the class which handles
                     */
                    IssueData issueData = new IssueData(String.valueOf(downloadurl), "Moradabad", key,mBundle);

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

                                    if (task.isSuccessful())
                                        Log.e(TAG , "KEY VALUE ADDED");

                                }
                            })
                            .addOnFailureListener(Report.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "onFailure: "+e.getMessage());
                                }
                            });

                    Log.e(CLASSNAME, downloadurl.toString());

                    Toast.makeText(Report.this, "FILE UPLODED SUCESSFULLY", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Report.this, "ERROR: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
