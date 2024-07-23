package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddImageActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private Uri imageUri;
    private int imageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);

        imageView = findViewById(R.id.imageView);
        Button buttonSelectImage = findViewById(R.id.buttonSelectImage);
        Button buttonUploadImage = findViewById(R.id.buttonUploadImage);

        imageIndex = getIntent().getIntExtra("imageIndex", -1);

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    uploadImage();
                } else {
                    Toast.makeText(AddImageActivity.this, "Please select an image first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void uploadImage() {
        // Simulate image upload and get the URL
        String uploadedImageUrl = imageUri.toString();

        // Pass the URL and image index back to the calling activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("imageUri", uploadedImageUrl);
        resultIntent.putExtra("imageIndex", imageIndex);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public static void startForResult(Activity activity, int imageIndex, int requestCode) {
        Intent intent = new Intent(activity, AddImageActivity.class);
        intent.putExtra("imageIndex", imageIndex);
        activity.startActivityForResult(intent, requestCode);
    }
}
