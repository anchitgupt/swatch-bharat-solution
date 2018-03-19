package ateam.com.clean.Profile;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ateam.com.clean.Data.Count;
import ateam.com.clean.Data.User;
import ateam.com.clean.R;

public class UserProfile extends AppCompatActivity implements ValueEventListener {

    private static String TAG = "UserProfile";
    private FirebaseUser user;
    private Count count;
    IconRoundCornerProgressBar pbGarbage, pbPit, pbLog, pbChild;
    TextView textGarbage, textPit, textLog, textChild;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = FirebaseAuth.getInstance().getCurrentUser();
        TextView profile = findViewById(R.id.profile_name);
        textGarbage = findViewById(R.id.text_garbage);
        textChild = findViewById(R.id.text_labour);
        textLog = findViewById(R.id.text_log);
        textPit = findViewById(R.id.text_pit);
        profile.setText(user.getDisplayName());

        pbGarbage = findViewById(R.id.progress_garbage);
        pbPit = findViewById(R.id.progress_pit);
        pbLog = findViewById(R.id.progress_log);
        pbChild = findViewById(R.id.progress_child);


        User userN = new User();
        String id = userN.getUserID(user.getEmail());
        FirebaseDatabase.getInstance().getReference().child("users").child(id).child("count").addValueEventListener(this);

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        count = dataSnapshot.getValue(Count.class);
    //  Log.e(TAG, "onDataChange: "+String.valueOf(count.getGarbage()));
        pbGarbage.setProgress(count.getGarbage()%50);
        pbPit.setProgress(count.getPit()%50);
        pbLog.setProgress(count.getLog()%50);
        pbChild.setProgress(count.getChild()%50);

        pbGarbage.setProgressColor(Color.GREEN);
        pbPit.setProgressColor(Color.GREEN);
        pbLog.setProgressColor(Color.GREEN);
        pbChild.setProgressColor(Color.GREEN);
        pbGarbage.setBackgroundColor(Color.WHITE);
        pbPit.setBackgroundColor(Color.WHITE);
        pbLog.setBackgroundColor(Color.WHITE);
        pbChild.setBackgroundColor(Color.WHITE);



        textGarbage.setText("Garbage Reported: " + String.valueOf(count.getGarbage())+" Level:"+String.valueOf(count.getGarbage()/50));
        textPit.setText("Pit Holes Reported: " + String.valueOf(count.getPit())+" Level:"+String.valueOf(count.getPit()/50));
        textChild.setText("Child Labour Reported: " + String.valueOf(count.getChild())+" Level:"+String.valueOf(count.getChild()/50));
        textLog.setText("Water Log Reported: " + String.valueOf(count.getLog())+" Level:"+String.valueOf(count.getLog()/50));

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
