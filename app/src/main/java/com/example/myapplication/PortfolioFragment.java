package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class PortfolioFragment extends Fragment implements View.OnClickListener {

    private static final int ADD_IMAGE_REQUEST_CODE = 100;

    private ArrayList<ImageView> imageViews = new ArrayList<>();
    private LinearLayout imageContainer;
    private int index = 0;

    private StorageReference storageRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_portfolio, container, false);
        imageContainer = view.findViewById(R.id.image_container);
        FirebaseApp.initializeApp(getContext());
        storageRef = FirebaseStorage.getInstance().getReference().child("images");
        Log.d("PortfolioFragment", "Firebase Storage Reference: " + storageRef.toString());
        loadImagesFromStorage();
        return view;
    }

    @Override
    public void onClick(View v) {
        AddImageActivity.startForResult(getActivity(), index, ADD_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            String imageUriString = data.getStringExtra("imageUri");
            if (imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);
                Log.d("PortfolioFragment", "Received image URI: " + imageUri.toString());
                uploadImageToFirebase(imageUri);
            } else {
                Log.e("PortfolioFragment", "Image URI is null");
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference imageRef = storageRef.child(System.currentTimeMillis() + ".jpg");

        Log.d("PortfolioFragment", "Uploading image URI: " + imageUri);
        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                Log.d("PortfolioFragment", "Image download URL: " + imageUrl);
                                addImageView(imageUrl);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                        Log.e("PortfolioFragment", "Failed to upload image", e);
                    }
                });
    }

    private void loadImagesFromStorage() {
        Log.d("PortfolioFragment", "Loading images from storage");
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            addImageView(imageUrl);
                            Log.d("PortfolioFragment", "Image loaded from storage: " + imageUrl);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("PortfolioFragment", "Failed to get download URL", e);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("PortfolioFragment", "Failed to list items in storage", e);
            }
        });
    }

    private void addImageView(String imageUrl) {
        ImageView newImageView = new ImageView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 0);
        newImageView.setLayoutParams(layoutParams);
        Glide.with(requireContext()).load(imageUrl).into(newImageView);
        newImageView.setOnClickListener(this);
        imageContainer.addView(newImageView);
        imageViews.add(newImageView);
        index++;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
