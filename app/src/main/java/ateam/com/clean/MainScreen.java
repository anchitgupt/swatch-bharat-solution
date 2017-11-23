package ateam.com.clean;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

public class MainScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ValueEventListener, View.OnClickListener {

    private static String TAG = "MainScreen";
    TextView textViewUserName, textViewUserNameEmail;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    String userName;
    LinearLayout garbageLayout, pitLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isLocatinmPermissionGranted();
        garbageLayout = (LinearLayout) findViewById(R.id.linearLayoutGarbage);
        pitLayout = (LinearLayout) findViewById(R.id.linearLayoutPit);

        garbageLayout.setOnClickListener(this);
        pitLayout.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");



        if(user == null){
            startActivity(new Intent(this, LoginActivity.class));
        }

        //TODO Alert Dialog for sending mail
        /*if(user.getEmail() != null){
        if(!user.isEmailVerified())

            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Email Verification Sent," +
                                        "Check your E-Mail to Verify your Account", Toast.LENGTH_LONG).show();
                                //startActivity(new Intent(getApplicationContext(), MainScreen.class));
                                //finish();
                            }
                            if(task.isComplete()){
                                //Toast.makeText(MainScreen.this, "Complete", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }*/
        Log.e(TAG, "ID: "+user.getDisplayName());

        //DatabaseReference d = reference.child(id).getDatabase().getReference();
        //String id  = user.getUid();
        //DatabaseReference d = reference.child(id).getDatabase().getReference();


        View header = navigationView.getHeaderView(0);

        textViewUserName = (TextView) header.findViewById(R.id.textViewUserName);
        textViewUserNameEmail =(TextView) header.findViewById(R.id.textViewUserNameEmail);


        if(user.getEmail() != null || user.getDisplayName() != null) {
            textViewUserNameEmail.setText(user.getEmail());
        }


        reference.addValueEventListener(this);
        garbageLayout.setOnClickListener(this);
        pitLayout.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id  == R.id.logout){
            auth.signOut();
            user =null;
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            try{
                ArrayList<Uri> uris = new ArrayList<Uri>();
                Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                sendIntent.setType("application/*");
                uris.add(Uri.fromFile(new File(getApplicationInfo().publicSourceDir)));
                sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                startActivity(Intent.createChooser(sendIntent, "Share APK via"));

            }catch(Exception e){
                System.out.println(e.getMessage());
            }

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        String[] user_id = new String[0];
        try {
            user_id = user.getEmail().split("@");
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataSnapshot = dataSnapshot.child(user_id[0]);
        userName = (String) dataSnapshot.child("name").getValue();
        textViewUserName.setText(userName);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName).build();
        user.updateProfile(profileUpdates);
/*
        String userName  = String.valueOf(dataSnapshot.child(user_id[0]).child("name"));
        Log.e("USer",userName);*/
        Log.e("User",userName);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(this, "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if(view == garbageLayout){
            Toast.makeText(this, "Garbage Layout", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Report.class);
            intent.putExtra("type","garbage");
            this.startActivity(intent);
        }

        if(view == pitLayout){
            Toast.makeText(this, "Pit Layout", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Report.class);
            intent.putExtra("type","pit");
            this.startActivity(intent);
        }
    }
    public void isLocatinmPermissionGranted() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");

            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");


        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");

            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");


        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");

            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");

        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");

            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 103);
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            
        }
    }
}
