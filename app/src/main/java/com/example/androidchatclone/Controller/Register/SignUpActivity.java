package com.example.androidchatclone.Controller.Register;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.androidchatclone.Controller.Home.HomeActivity;
import com.example.androidchatclone.Controller.Login.SignInActivity;
import com.example.androidchatclone.Controller.Util.Child;
import com.example.androidchatclone.Model.UserInfo;
import com.example.androidchatclone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private CircleImageView imageView;
    private EditText etMail, etPassword, etName, etPasswordRepetation;

    private Uri uriPhoto;
    private Context context = this;

    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Register");

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        imageView = findViewById(R.id.imgProfile);
        etName = findViewById(R.id.etNameSurname);
        etMail = findViewById(R.id.etMail);
        etPassword = findViewById(R.id.etPassword);
        etPasswordRepetation = findViewById(R.id.etPasswordRepetiton);
        progressBar = findViewById(R.id.progressBar);

        if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},101);
        }
        

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,100);
            }
        });

        findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uriPhoto == null || etMail.getText().toString().trim().isEmpty() || etName.getText().toString().trim().isEmpty()
                || etPassword.getText().toString().trim().isEmpty() || etPasswordRepetation.getText().toString().trim().isEmpty()) {
                    Toast.makeText(context, "Fields can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!etPassword.getText().toString().trim().equals(etPasswordRepetation.getText().toString().trim())) {
                    Toast.makeText(context, "Passwords are not the same.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String mail = etMail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(mail,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        setUserInfo();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private  void setUserInfo() {

        UUID uuid = UUID.randomUUID();
        final String path = "image/"+uuid + ".jpg";

        storageReference.child(path).putFile(uriPhoto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String uid = firebaseAuth.getUid();
                        String name = etName.getText().toString().trim();
                        String url = uri.toString();
                        String mail = etMail.getText().toString().trim();

                       databaseReference.child(Child.users).push().setValue(
                               new UserInfo(mail,name,url,uid)
                       );
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(context, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK) {
            return; 
        }
        if (requestCode == 100 && data != null) {
            uriPhoto = data.getData();
        }
        
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uriPhoto);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }
}
