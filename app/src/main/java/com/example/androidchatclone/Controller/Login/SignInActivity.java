package com.example.androidchatclone.Controller.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.androidchatclone.Controller.Home.HomeActivity;
import com.example.androidchatclone.Controller.Register.SignUpActivity;
import com.example.androidchatclone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private EditText etMail, etPassword;
    private Context context = this;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        setTitle("Login");
        firebaseAuth = FirebaseAuth.getInstance();
        etMail = findViewById(R.id.etMail);
        etPassword = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etMail.getText().toString().trim().isEmpty() || etPassword.getText().toString().trim().isEmpty() ) {
                    Toast.makeText(context, "Fields can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                String mail = etMail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(mail,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            }
        });

        findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SignUpActivity.class));
            }
        });
    }
}
