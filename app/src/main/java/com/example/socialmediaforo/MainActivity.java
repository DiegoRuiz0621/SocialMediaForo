package com.example.socialmediaforo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediaforo.provider.AuthProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    TextView mTextViewRegistrate;
    TextInputEditText mInputEmail;
    TextInputEditText mInputpassword;
    Button mbuttonlogin;
    FirebaseAuth mAuth;
    AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewRegistrate = findViewById(R.id.textViewRegistrate);
        mInputEmail = findViewById(R.id.correologin);
        mInputpassword = findViewById(R.id.contraseñalogin);
        mbuttonlogin = findViewById(R.id.btnlogin);

        mAuth = FirebaseAuth.getInstance();
        mAuthProvider = new AuthProvider();

        mbuttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        mTextViewRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegistrarseActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuthProvider.getUserSession() != null){
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void login() {
        String email = mInputEmail.getText().toString();
        String password = mInputpassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                   // Toast.makeText(MainActivity.this, "El usuario se almaceno correctamente en la base de datos", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this, "El Email o la contraseña no son correctos", Toast.LENGTH_LONG).show();
                }
            }
        });
        Log.d("CAMPO", "email: " + email);
        Log.d("CAMPO", "password: " + password);
    }
}