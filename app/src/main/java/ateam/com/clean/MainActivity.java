package ateam.com.clean;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import ateam.com.clean.Data.User;
import ateam.com.clean.Data.UserData;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    EditText editTextUsername, editTextEmail, editTextPassword, editTextPhone;
    Button buttonRegister;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    TextView textViewLogin;
    ProgressDialog alert;
    Spinner spinnerCity, spinnerState;
    ArrayAdapter<String> arrayAdapter;
    List<String> list;
    String statere;
    User userN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhone = findViewById(R.id.editTextDate);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);
        spinnerCity = findViewById(R.id.spinnerCity);
        spinnerState = findViewById(R.id.spinnerState);
        spinnerState.setOnItemSelectedListener(this);


        alert = new ProgressDialog(this, R.style.Custom);
        Log.e("MainActivity", "Entered ");

        textViewLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");

        if (user != null) {
            startActivity(new Intent(this, MainScreen.class));
        }

    }

    @Override
    public void onClick(View view) {

        if (view == buttonRegister) {
            final String username = editTextUsername.getText().toString().trim();
            String pass = editTextPassword.getText().toString().trim();
            final String email = editTextEmail.getText().toString().trim();
            String date = editTextPhone.getText().toString().trim();
            String state = spinnerState.getSelectedItem().toString().trim();
            String city = spinnerCity.getSelectedItem().toString().trim();
            userN = new User();
            final String userID = userN.getUserID(email);
            final String[] user_id = email.split("@");
            if (checkNull()) {
                if (date.length() != 10) {
                    editTextPhone.setError("Enter Valid 10 digit Phone number");
                    return;
                }

                //final String id = reference.push().getKey();

                final UserData userData = new UserData(userID, username, date, city, state, email);

                alert.setMessage("Registering...");
                alert.show();

                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //DBMS Entry is created for the user
                                    Log.e("MainActivity", auth.getCurrentUser().getUid());
                                    reference.child(userID).setValue(userData).addOnCompleteListener(MainActivity.this,
                                            new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    String token = FirebaseInstanceId.getInstance().getToken();
                                                    userN = new User();
                                                    reference.child(userN.getUserID(email)).child("token").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                                alert.dismiss();
                                                                Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                                                                user = FirebaseAuth.getInstance().getCurrentUser();
                                                                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                                                        }
                                                    });

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
        //Register Ends
        if (view == textViewLogin) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private boolean checkNull() {
        if (TextUtils.isEmpty(editTextUsername.getText())) {
            editTextUsername.setError("Enter Name");
            return false;
        }
        if (TextUtils.isEmpty(editTextEmail.getText())) {
            editTextEmail.setError("Enter E-Mail");
            return false;
        }
        if (TextUtils.isEmpty(editTextPassword.getText())) {
            editTextPassword.setError("Enter Password");
            return false;
        }
        if (TextUtils.isEmpty(editTextPhone.getText())) {
            editTextPhone.setError("Enter Phone Number");
            return false;
        }
        return true;
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        statere = spinnerState.getSelectedItem().toString();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        /*
        *Code for to get the city from the URL: https://api.myjson.com/bins/urt55
        *
        * to the state selected
         */

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..");
        dialog.show();

        JsonArrayRequest jsonArrayRequest =
                new JsonArrayRequest("https://api.myjson.com/bins/urt55", new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            String[] str = new String[response.length()];
                            list = new ArrayList<>();
                            int k = 0;
                            for (int i = 0; i < response.length(); i++) {
                                if (response.getJSONObject(i).getString("state").contentEquals(statere)) {
                                    str[i] = response.getJSONObject(i).getString("city_name");
                                    //Log.e("City",str[i]);
                                    list.add(str[i]);
                                    k++;
                                }
                            }
                            spinnerCity.setAdapter(
                                    new ArrayAdapter<String>
                                            (MainActivity.this, R.layout.support_simple_spinner_dropdown_item, list));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "ERROR: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonArrayRequest);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
