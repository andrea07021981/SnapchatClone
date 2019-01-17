package com.projects.andreafranco.snapchatclone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseUserActivity extends AppCompatActivity {

    private List<String> mArraList;
    private List<String> mArraListKeys;
    private ArrayAdapter<String> mAdapter;
    private String mImageUrl;
    private String mImageName;
    private String mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        //Get data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mImageUrl = (String) extras.get("imageUrl");
            mImageName = (String) extras.get("imageName");
            mMessage = (String) extras.get("message");
        }
        mArraList = new ArrayList<>();
        mArraListKeys = new ArrayList<>();
        ListView chooseUserListView = findViewById(R.id.choose_user_listview);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mArraList);
        chooseUserListView.setAdapter(mAdapter);

        //Get the users from firebase db
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String email = dataSnapshot.child("email").getValue().toString();
                        mArraList.add(email);
                        mArraListKeys.add(dataSnapshot.getKey());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        chooseUserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> snapMap = new HashMap<>();
                snapMap.put("from", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                snapMap.put("imagename", mImageName);
                snapMap.put("imageurl", mImageUrl);
                snapMap.put("message", mMessage);
                FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(mArraListKeys.get(position))
                        .child("snaps")
                        .push()
                        .setValue(snapMap);

                Intent intent = new Intent(ChooseUserActivity.this, SnapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
