package com.example.socialmediaforo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediaforo.models.Post;
import com.example.socialmediaforo.provider.AuthProvider;
import com.example.socialmediaforo.provider.ImageProvider;
import com.example.socialmediaforo.provider.PostProvider;
import com.example.socialmediaforo.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class PostActivity extends AppCompatActivity {

    ImageView mImageViewPost1;
   // ImageView mImageViewPost2;
    private final int GALLERY_REQUEST_CODE = 1;
   // private final int GALLERY_REQUEST_CODE2 = 2;
    File mImageFile;
    //File mImageFile2;
    Button mButtonPost;
    ImageProvider mImageProvider;
    TextInputEditText mTextInputTitulo;
    TextInputEditText mTextInputDescripcion;
    ImageView mImageViewsistemas;
    ImageView mImageViewindustrial;
    ImageView mImageViewcivil;
    ImageView mImageViewambiental;
    TextView mTextViewCategory;

    PostProvider mPostProvider;
    AuthProvider mAuthprovider;

    String mcarrera = "";
    String mtitulo = "";
    String mdescripcion = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mImageProvider = new ImageProvider();
        mPostProvider = new PostProvider();
        mAuthprovider = new AuthProvider();

        mImageViewPost1 = findViewById(R.id.imageViewPost1);
       // mImageViewPost2 = findViewById(R.id.imageViewPost2);
        mButtonPost = findViewById(R.id.btnpost);
        mTextInputTitulo = findViewById(R.id.Titulo);
        mTextInputDescripcion = findViewById(R.id.Descripcion);
        mImageViewsistemas = findViewById(R.id.imageViewsistemas);
        mImageViewindustrial = findViewById(R.id.imageViewindustrial);
        mImageViewcivil = findViewById(R.id.imageViewcivil);
        mImageViewambiental = findViewById(R.id.imageViewambiental);
        mTextViewCategory = findViewById(R.id.carrerasdeingenieria);


        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPost();

            }
        });

        mImageViewPost1.setOnClickListener((view -> {openGallery(GALLERY_REQUEST_CODE); }));
       // mImageViewPost2.setOnClickListener((view -> {openGallery(GALLERY_REQUEST_CODE2); }));

        mImageViewsistemas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mcarrera = "Sistemas";
                mTextViewCategory.setText(mcarrera);
            }
        });

        mImageViewindustrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mcarrera = "Industrial";
                mTextViewCategory.setText(mcarrera);
            }
        });

        mImageViewcivil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mcarrera = "Civil";
                mTextViewCategory.setText(mcarrera);
            }
        });

        mImageViewambiental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mcarrera = "Ambiental";
                mTextViewCategory.setText(mcarrera);
            }
        });
    }

    private void clickPost() {
        mtitulo = mTextInputTitulo.getText().toString();
        mdescripcion = mTextInputDescripcion.getText().toString();
        if (!mtitulo.isEmpty() && !mdescripcion.isEmpty() && !mcarrera.isEmpty()){
            if (mImageFile != null) {
                saveImage();
            }else{
                Toast.makeText(PostActivity.this, "Debes seleccionar una imagen", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(PostActivity.this, "Porfavor, Complete todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage() {
        mImageProvider.save(PostActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Post post = new Post();

                            post.setImage1(url);
                           // post.setImage2(url);
                            post.setTitle(mtitulo);
                            post.setDescription(mdescripcion);
                            post.setCategory(mcarrera);
                            post.setIdUser(mAuthprovider.getUid());
                            mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> taskSave) {
                                    if(taskSave.isSuccessful()){
                                        Toast.makeText(PostActivity.this, "Se almaceno correctamente la informacion", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(PostActivity.this, "NO se almaceno correctamente la informacion", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    });
                    Toast.makeText(PostActivity.this, "se almaceno correctamente la imagen", Toast.LENGTH_SHORT).show();
            }else{
                    Toast.makeText(PostActivity.this, "NO se almaceno correctamente la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            mImageFile = FileUtil.from(PostActivity.this, result.getData().getData());
                            mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
                        }catch (Exception e){
                            Log.d("ERROR", "Se produjo un error " +e.getMessage());
                            Toast.makeText(PostActivity.this, "Se prodijo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }
    );





    private void openGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryLauncher.launch(galleryIntent);
       // startActivityForResult(galleryIntent, requestCode);
    }

    //IR a la galeria y seleccionar la foto
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
//            try {
//                mImageFile = FileUtil.from(this, data.getData());
//                mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
//            }catch (Exception e){
//                Log.d("ERROR", "Se produjo un error " +e.getMessage());
//                Toast.makeText(this, "Se prodijo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }
//    }
}