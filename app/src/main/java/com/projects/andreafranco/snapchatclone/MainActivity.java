package com.projects.andreafranco.snapchatclone;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
                            AuthResult result = task.getResult();
                            result.getUser();
                        }
                    });
        }
    }
}
