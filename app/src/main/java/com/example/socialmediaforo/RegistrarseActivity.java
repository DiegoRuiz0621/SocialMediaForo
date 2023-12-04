package com.example.socialmediaforo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrarseActivity extends AppCompatActivity {

    CircleImageView mCirculoatras;
    TextInputEditText mTextInputUsername;
    TextInputEditText mTextInputemail;
    TextInputEditText mTextInputpassword;
    TextInputEditText mTextInputconfirmarPassword;
    Button mButtonRegistrer;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        mCirculoatras = findViewById(R.id.circuloatras);
        mTextInputUsername = findViewById(R.id.UsuarioNombreRegistro);
        mTextInputemail = findViewById(R.id.CorreoRegistro);
        mTextInputpassword = findViewById(R.id.ContraseñaRegistro);
        mTextInputconfirmarPassword = findViewById(R.id.ConfirmarContraseñaRegistro);
        mButtonRegistrer = findViewById(R.id.btnRegistrarse);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mButtonRegistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        mCirculoatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void register(){
        String username = mTextInputUsername.getText().toString();
        String email = mTextInputemail.getText().toString();
        String password = mTextInputpassword.getText().toString();
        String ConfirmarPassword = mTextInputconfirmarPassword.getText().toString();

        if(!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !ConfirmarPassword.isEmpty()){
            if(isEmailValid(email)){
                if (password.equals(ConfirmarPassword)) {
                    if (password.length() >= 6){
                        createUser(username, email, password);
                    }else{
                        Toast.makeText(this, "la contraseña debe de tener al menos 6 caracteres", Toast.LENGTH_LONG).show();
                    }
                }
                Toast.makeText(this, "El Email es valido y llenaste toda la informacion", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "llenaste toda la informacion pero El Email no es valido", Toast.LENGTH_LONG).show();
            }
            Toast.makeText(this, "Has insertados todos los campos", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Te faltan campos por llenar", Toast.LENGTH_LONG).show();
        }
    }


   // ^: Coincide con el inicio de la cadena.
   //[\\w\\.-]+: Coincide con uno o más caracteres de palabra (\w), punto (.) o guion medio (-).
   //@: Coincide con el símbolo "@".
   //([\\w\\-]+\\.)+: Coincide con uno o más grupos que contienen caracteres de palabra o guion medio seguidos por un punto.
   //[A-Z]{2,4}: Coincide con entre 2 y 4 letras mayúsculas al final de la cadena (representando la extensión del dominio, como "com" o "net").
   //$: Coincide con el final de la cadena.
    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void createUser(final String username, final String email, final String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String id = mAuth.getCurrentUser().getUid();
                    Map<String, Object> map = new HashMap<>();
                    map.put("email", email);
                    map.put("username", username);
                    mFirestore.collection("Users").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(RegistrarseActivity.this, "El usuario se almaceno correctamente en la base de datos", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(RegistrarseActivity.this, "El usuario NO se almaceno en la base de datos", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    Toast.makeText(RegistrarseActivity.this, "El Usuario se registro correctamente", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(RegistrarseActivity.this, "El Usuario NO se registro correctamente", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}