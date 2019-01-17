package com.projects.andreafranco.snapchatclone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class CreateSnapActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGES = 1;
    ImageView mLoadedImageview;
    EditText mMessageEdittext;
    FirebaseStorage mFirebaseStorage;
    String mImageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_snap);
        mLoadedImageview = findViewById(R.id.loaded_imageview);
        mMessageEdittext = findViewById(R.id.message_edittext);
        mFirebaseStorage = FirebaseStorage.getInstance();
        mImageName = UUID.randomUUID().toString() + ".jpg";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getPhoto();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void chooseImageClick(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getPhoto();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGES && resultCode == RESULT_OK) {
            Uri uriImages = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImages);
                mLoadedImageview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void nextButtonClick(View view) {
        if (mLoadedImageview.getDrawable() instanceof BitmapDrawable &&
                !TextUtils.isEmpty(mMessageEdittext.getText())) {

            mLoadedImageview.setDrawingCacheEnabled(true);
            mLoadedImageview.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) mLoadedImageview.getDrawable()).getBitmap();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            //Folder on firebase called images
            StorageReference imagesReference = mFirebaseStorage.getReference().child("images").child(mImageName);

            UploadTask uploadTask = imagesReference.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateSnapActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String path = taskSnapshot.getUploadSessionUri().getPath();
                    Toast.makeText(CreateSnapActivity.this, "Upload file path: " +path, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(CreateSnapActivity.this, ChooseUserActivity.class);
                    intent.putExtra("imageUrl", path);
                    intent.putExtra("imageName", mImageName);
                    intent.putExtra("message", mMessageEdittext.getText().toString());
                    startActivity(intent);
                }
            });
        } else {
            //Image not loaded
            Toast.makeText(this, "Complete all fields", Toast.LENGTH_SHORT).show();
        }
    }
}
