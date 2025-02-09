package com.example.firebasemoviesearch_app.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebasemoviesearch_app.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    ActivityRegisterBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        binding.regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.regEmail.getText().toString();
                String password = binding.regPassword.getText().toString();
                registerUser(email, password);
            }
        });

    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                          if(task.isSuccessful()){
                              // Sign in success, update UI with the signed-in User's information
                              Log.d("tag", "createUserwithEmail:success");
                              FirebaseUser user = mAuth.getCurrentUser();
                              Toast.makeText(Register.this, "RegisterUser Pass." +user.getUid(), Toast.LENGTH_SHORT).show();

                              // Redirect to Login Activity
                              Intent intentObj = new Intent(getApplicationContext(), Login.class);
                              startActivity(intentObj);
                              finish();

                          } else {
                              // If sign in fails, display a message to the User.
                              Log.d("tag", "createUserwithEmail:failure", task.getException());
                              Toast.makeText(Register.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                          }
                    }
                });
    }
}