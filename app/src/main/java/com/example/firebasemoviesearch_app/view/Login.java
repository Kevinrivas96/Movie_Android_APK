package com.example.firebasemoviesearch_app.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebasemoviesearch_app.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class  Login extends AppCompatActivity {

    ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentObj = new Intent(getApplicationContext(), Register.class);
                startActivity(intentObj);
                finish();
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.email.getText().toString();
                String password = binding.password.getText().toString();
                singIn(email, password);
            }
        });

    }
    private void singIn(String email, String password){
        // [Start sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // Sign in success, update UI with the signed-in User's information
                            Log.d("tag", "signInWithEmail:success");
                            Toast.makeText(Login.this, "Authentication passed.", Toast.LENGTH_SHORT).show();
                            Intent intentObj = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intentObj);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user
                            Log.i("tag", "SignInWithEmail:failure");
                            Toast.makeText(Login.this, "User doesn't exist.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //[End sign_in_with_email]
    }
}
