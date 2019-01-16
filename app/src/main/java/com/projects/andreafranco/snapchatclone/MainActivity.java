package com.projects.andreafranco.snapchatclone;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText mUsernameTextView, mPasswordTextView;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsernameTextView = findViewById(R.id.username_edittext);
        mPasswordTextView = findViewById(R.id.password_edittext);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {

        }
    }

    public void goClick(View view) {
        if (TextUtils.isEmpty(mUsernameTextView.getText()) || TextUtils.isEmpty(mPasswordTextView.getText())) {
            Toast.makeText(this, "Username and password are mandatory", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(mUsernameTextView.getText().toString(), mPasswordTextView.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Login OK
                                logInDone();
                            } else {
                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Log In")
                                        .setMessage("Would you like to create a new user?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                signUp();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setCancelable(false);
                                AlertDialog alertDialog = alertBuilder.create();
                                alertDialog.show();
                            }
                        }
                    });
        }
    }

    private void signUp() {
        mAuth.createUserWithEmailAndPassword(mUsernameTextView.getText().toString(), mPasswordTextView.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (authResult.getUser() != null) {
                            //User created
                            logInDone();
                        } else {
                            Toast.makeText(MainActivity.this, "Error creating new user", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void logInDone() {
        Intent intent = new Intent(this, SnapsActivity.class);
        startActivity(intent);
    }
}
