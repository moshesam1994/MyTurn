package com.example.myapplication;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import java.io.IOException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddProductActivity extends Activity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;

    private ImageView imageViewProduct;
    private EditText editTextName;
    private EditText editTextPrice;
    private Button buttonAddProduct;

    private Uri selectedImageUri;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        imageViewProduct = findViewById(R.id.image_view_product);
        editTextName = findViewById(R.id.edit_text_name);
        editTextPrice = findViewById(R.id.edit_text_price);
        buttonAddProduct = findViewById(R.id.button_add_product);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        imageViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGallery();

            }
        });

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                imageViewProduct.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addProduct() {
        String name = editTextName.getText().toString().trim();
        String priceText = editTextPrice.getText().toString().trim();
        if (priceText.isEmpty()) {
            Toast.makeText(this, "Please enter valid product price", Toast.LENGTH_SHORT).show();
            return;
        }
        double price = Double.parseDouble(priceText);

        if (!name.isEmpty() && price > 0 && selectedImageUri != null) {
            StorageReference fileRef = storageRef.child("products/" + System.currentTimeMillis() + ".jpg");
            fileRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            Product newProduct = new Product(name, price, imageUrl);
                            db.collection("products").add(newProduct).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(AddProductActivity.this, "Product added", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddProductActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please enter valid product details", Toast.LENGTH_SHORT).show();
        }
    }
}