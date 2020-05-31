package ateam.com.clean;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.Log;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
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


        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        textViewLogin = findViewById(R.id.textViewLogin);
        textViewRegister = findViewById(R.id.textViewRegister);

        buttonLogin.setOnClickListener(this);
        textViewLogin.setOnClickListener(this);
        textViewRegister.setOnClickListener(this);

        alert = new ProgressDialog(this,R.style.Custom);


        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


        if(user!=null){
            startActivity(new Intent(this, MainScreen.class));
            finish();
        }
        reference = FirebaseDatabase.getInstance().getReference("users");

    }

    @Override
    public void onClick(View view) {

        if(view == buttonLogin){
            String pass =editTextPassword.getText().toString();
            String email=editTextEmail.getText().toString().trim();

            if(checkNull() && isValidMail(email)) {
                alert.setMessage("Signing In");
                alert.show();

                auth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                                    alert.dismiss();
                                    startActivity(new Intent(LoginActivity.this, MainScreen.class));
                                    Toast.makeText(LoginActivity.this, "!!Email is Verified!!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else
                                    Toast.makeText(LoginActivity.this, "ERROR: "+"USER LOGIN FAILURE", Toast.LENGTH_SHORT).show();


                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Toast.makeText(LoginActivity.this, "ERROR:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onFailure: "+e.getMessage() );
                                alert.dismiss();
                            }
                        });
            }
        }

        if(view == textViewLogin){
            final String email=editTextEmail.getText().toString().trim();
            if(email.isEmpty()) {
                editTextEmail.setError("Enter Email here First");
                editTextEmail.setFocusable(true);
            }
            else {
                alert.setMessage("Sending Password reset Mail");
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    alert.dismiss();
                                    Toast.makeText(LoginActivity.this, "Mail Sent to your email: " + email, Toast.LENGTH_SHORT).show();
                                    auth.signOut();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                alert.dismiss();
                                Log.e(TAG, "onFailure: "+e.getMessage() );
                                //Toast.makeText(LoginActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }


        }
        if(view == textViewRegister) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private boolean checkNull() {
        if(TextUtils.isEmpty(editTextEmail.getText())){
            editTextEmail.setError("Enter E-Mail");
            return false;}
        if(TextUtils.isEmpty(editTextPassword.getText())){
            editTextPassword.setError("Enter Password");
            return false;}
        return true;
    }
    private boolean isValidMail(String email) {
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        p = Pattern.compile(EMAIL_STRING);
        m = p.matcher(email);
        check = m.matches();

        if (!check) {
            editTextEmail.setError("Not Valid Email");
        }
        return check;
    }

}
