package ateam.com.clean;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextEmail,editTextPassword;
    Button buttonLogin;
    TextView textViewLogin;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        buttonLogin = (Button)findViewById(R.id.buttonLogin);
        textViewLogin = (TextView)findViewById(R.id.textViewLogin);

        buttonLogin.setOnClickListener(this);
        textViewLogin.setOnClickListener(this);


        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");

    }

    @Override
    public void onClick(View view) {
        if(view == buttonLogin){
            String pass =editTextPassword.getText().toString();
            String email=editTextEmail.getText().toString().trim();

            auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(LoginActivity.this, "Signed In", Toast.LENGTH_SHORT).show();

                            if(!user.isEmailVerified()){
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(LoginActivity.this, "Email Verified", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(LoginActivity.this, "ERROR: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "!!Email is Verified!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "ERROR:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        if(view == textViewLogin){
            String email=editTextEmail.getText().toString().trim();
            if(email.isEmpty())
                editTextEmail.setError("Enter Email here First");
            else
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           Toast.makeText(LoginActivity.this, "Mail Sent to your email"+user.getEmail(), Toast.LENGTH_SHORT).show();
                           auth.signOut();
                       }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "ERROR: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            //startActivity(new Intent(this, MainActivity.class));
        }
    }
}
