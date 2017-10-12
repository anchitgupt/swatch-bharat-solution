package ateam.com.clean;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ateam.com.clean.Data.UserData;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextUsername,editTextEmail,editTextPassword, editTextDate;
    Button buttonRegister;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    TextView textViewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsername = (EditText)findViewById(R.id.editTextUsername);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextDate = (EditText) findViewById(R.id.editTextDate);
        buttonRegister = (Button)findViewById(R.id.buttonRegister);
        textViewRegister = (TextView) findViewById(R.id.textViewRegister);
        textViewRegister.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");

    }

    @Override
    public void onClick(View view) {
        if(view == buttonRegister) {
            String username = editTextUsername.getText().toString().trim();
            String pass = editTextPassword.getText().toString();
            String email = editTextEmail.getText().toString().trim();
            String date = editTextDate.getText().toString().trim();
            final String id = reference.push().getKey();

            final UserData userData = new UserData(username, date, "", "", email);

            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this,
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                reference.child(id).setValue(userData).addOnCompleteListener(MainActivity.this,
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(MainActivity.this,
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(MainActivity.this, "Data Not " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                            }
                        }
                    })
                    .addOnFailureListener(this,
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
        }
        if( view ==textViewRegister){
            startActivity(new Intent(this, LoginActivity.class));
        }

    }
}
