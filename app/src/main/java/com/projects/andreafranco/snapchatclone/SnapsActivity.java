package com.projects.andreafranco.snapchatclone;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.projects.andreafranco.snapchatclone.adapters.SnapListAdapter;
import com.projects.andreafranco.snapchatclone.helpers.RecyclerItemTouchHelper;
import com.projects.andreafranco.snapchatclone.models.Snap;

import java.util.ArrayList;

public class SnapsActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    FirebaseAuth mAuth;
    RecyclerView mSnapsListView;
    private ArrayList<Snap> mSnaps;
    private SnapListAdapter mAdapter;
    private ConstraintLayout mSnapConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaps);
        setTitle("Snaps list");
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        mAuth = FirebaseAuth.getInstance();
        mSnaps = new ArrayList<>();
        mAdapter = new SnapListAdapter(this, mSnaps);
        mSnapsListView = findViewById(R.id.snaps_recycleview);
        mSnapConstraintLayout = findViewById(R.id.snap_constraintlayout);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mSnapsListView.setAdapter(mAdapter);
        mSnapsListView.setLayoutManager(layoutManager);
        mSnapsListView.setHasFixedSize(true);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mSnapsListView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.snaps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Retrieve the snaps of the current user
        FirebaseDatabase.getInstance().getReference("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("snaps")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable final String s) {
                        if (dataSnapshot.exists()) {
                            final String userName = dataSnapshot.child("from").getValue().toString();
                            final String imagename = dataSnapshot.child("imagename").getValue().toString();
                            final String message = dataSnapshot.child("message").getValue().toString();
                            final String key = dataSnapshot.getKey();
                            StorageReference child = FirebaseStorage.getInstance().getReference().child("images/" + imagename);

                            final long ONE_MEGABYTE = 1024 * 1024;
                            child.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    // Data for "images/island.jpg" is returns, use this as needed
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                    Snap snap = new Snap(bitmap, userName, message, key, imagename);
                                    mSnaps.add(snap);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    Toast.makeText(SnapsActivity.this, "Error:" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        for (Snap snap : mSnaps) {
                            if (snap.getKey().equals(dataSnapshot.getKey())) {
                                mSnaps.remove(snap);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.createSnap:
                Intent intent = new Intent(this, CreateSnapActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(SnapsActivity.this)
                .setTitle("Log In")
                .setMessage("Would you like to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof SnapListAdapter.SnapViewHolder) {
            // get the removed item name to display it in snack bar
            String name = mSnaps.get(viewHolder.getAdapterPosition()).getEmail();
            String fileName = mSnaps.get(viewHolder.getAdapterPosition()).getImageName();
            String key = mSnaps.get(viewHolder.getAdapterPosition()).getKey();

            // backup of removed item for undo purpose
            final Snap deletedItem = mSnaps.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            //Delete from firebase
            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(mAuth.getCurrentUser().getUid())
                    .child("snaps")
                    .child(key).removeValue();

            //Delete storage image
            StorageReference child = FirebaseStorage.getInstance().getReference().child("images/" + fileName);
            child.delete();

            Snackbar snackbar = Snackbar
                    .make(mSnapConstraintLayout, name + " removed from Firebase!", Snackbar.LENGTH_LONG);
            // TODO showing snack bar with Undo option
            /*snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });*/
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
