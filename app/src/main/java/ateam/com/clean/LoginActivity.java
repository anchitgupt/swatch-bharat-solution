package ateam.com.clean;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextEmail,editTextPassword;
    Button buttonLogin;
    TextView textViewLogin, textViewRegister;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    ProgressDialog alert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        buttonLogin = (Button)findViewById(R.id.buttonLogin);

        textViewLogin = (TextView)findViewById(R.id.textViewLogin);
        textViewRegister = (TextView) findViewById(R.id.textViewRegister);

        buttonLogin.setOnClickListener(this);
        textViewLogin.setOnClickListener(this);
        textViewRegister.setOnClickListener(this);

        alert = new ProgressDialog(this,R.style.Custom);


        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");

    }

    @Override
    public void onClick(View view) {
        if(view == buttonLogin){
            String pass =editTextPassword.getText().toString();
            String email=editTextEmail.getText().toString().trim();
            alert.setMessage("Signing In");
            alert.show();
            auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(LoginActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainScreen.class));
                                Toast.makeText(LoginActivity.this, "!!Email is Verified!!", Toast.LENGTH_SHORT).show();
                                alert.dismiss();
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "ERROR:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            alert.dismiss();
                        }
                    });
        }

        if(view == textViewLogin){
            String email=editTextEmail.getText().toString().trim();
            if(email.isEmpty()) {
                editTextEmail.setError("Enter Email here First");
            }
            else {
                alert.setMessage("Sending Password reset Mail");
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    alert.dismiss();
                                    Toast.makeText(LoginActivity.this, "Mail Sent to your email" + user.getEmail(), Toast.LENGTH_SHORT).show();
                                    auth.signOut();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                alert.dismiss();
                                Toast.makeText(LoginActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            if(view == textViewRegister) {
                startActivity(new Intent(this, MainActivity.class));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(user!=null) {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout:
                auth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
