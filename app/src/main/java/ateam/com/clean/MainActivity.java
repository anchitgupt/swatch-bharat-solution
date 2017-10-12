package ateam.com.clean;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

    EditText editTextUsername,editTextEmail,editTextPassword, editTextPhone;
    Button buttonRegister;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    TextView textViewLogin;
    ProgressDialog alert;
    Spinner spinnerCity, spinnerState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsername = (EditText)findViewById(R.id.editTextUsername);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextPhone = (EditText) findViewById(R.id.editTextDate);
        buttonRegister = (Button)findViewById(R.id.buttonRegister);
        textViewLogin = (TextView) findViewById(R.id.textViewLogin);
        spinnerCity = (Spinner) findViewById(R.id.spinnerCity);
        spinnerState = (Spinner) findViewById(R.id.spinnerState);

        alert = new ProgressDialog(this,R.style.Custom);

        textViewLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");
        if(user!=null){
            startActivity(new Intent(this, MainScreen.class));
        }

    }

    @Override
    public void onClick(View view) {

        if(view == buttonRegister) {
            String username = editTextUsername.getText().toString().trim();
            String pass = editTextPassword.getText().toString();
            String email = editTextEmail.getText().toString().trim();
            String date = editTextPhone.getText().toString().trim();
            String state = spinnerState.getSelectedItem().toString().trim();
            String city = spinnerCity.getSelectedItem().toString().trim();

            if (checkNull()) {

                final String id = reference.push().getKey();

                final UserData userData = new UserData(username, date, city, state, email);

                alert.setMessage("Registering...");
                alert.show();
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
                                                        user.sendEmailVerification()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            alert.dismiss();
                                                                            Toast.makeText(MainActivity.this, "Email Verification Sent," +
                                                                                    "Check your E-Mail to Verify your Account", Toast.LENGTH_LONG).show();
                                                                            startActivity(new Intent(MainActivity.this, MainScreen.class));
                                                                        }
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(MainActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        alert.dismiss();
                                                                    }
                                                                });
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(MainActivity.this,
                                                    new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            alert.dismiss();
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
                                        alert.dismiss();
                                    }
                                });

            }

        }
        if( view == textViewLogin){
            startActivity(new Intent(this, LoginActivity.class));
        }

    }

    private boolean checkNull() {
        if(TextUtils.isEmpty(editTextUsername.getText())){
            editTextUsername.setError("Enter Name");
            return false;}
        if(TextUtils.isEmpty(editTextEmail.getText())){
            editTextEmail.setError("Enter E-Mail");
            return false;}
        if(TextUtils.isEmpty(editTextPassword.getText())){
            editTextPassword.setError("Enter Password");
            return false;}
        if(TextUtils.isEmpty(editTextPhone.getText())){
            editTextPhone.setError("Enter Age");
            return false;}
        return true;
    }
}
