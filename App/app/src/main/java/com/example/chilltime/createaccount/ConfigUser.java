package com.example.chilltime.createaccount;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.chilltime.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ConfigUser extends AppCompatActivity  {
    public static final int IMAGE_GALLERY_REQUEST = 20;
    static final int REQUEST_IMAGE_CAPTURE = 1034;

    //Variaves
    Map<String, Object> userData;
    private String sexo;
    Uri imageUri;
    Bitmap bmp;
    boolean done=false;

    // autenticação no firebase
    FirebaseAuth mAuth;
    // guardar os dados
    FirebaseFirestore mStore;
    //referencia storage
    StorageReference storageReference;
    StorageReference ref = null;
    String imgStorage;
    String imagePath;
    String userID;
    DocumentReference documentReference;

    // Dados para serem guardados
    EditText name;
    static TextView date, txtErroSex;
    ImageButton female, male;
    ImageView image;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_user);
        name = findViewById(R.id.Name);
        date = findViewById(R.id.txt_date);
        female = findViewById(R.id.btn_female);
        male = findViewById(R.id.btn_male);
        image = findViewById(R.id.image);
        txtErroSex = findViewById(R.id.txt_sexo_erro);

        // autenticação no firebase
        mAuth = FirebaseAuth.getInstance();

        // guardar os dados no firestore
        mStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = mAuth.getCurrentUser().getUid();
        documentReference = mStore.collection("Users").document(userID);

    }


    public void addImage(View view) {
        //invoke image gallery
        Intent intentPhoto = new Intent();
        intentPhoto.setType("image/*");
        intentPhoto.setAction(Intent.ACTION_PICK);

        startActivityForResult(Intent.createChooser(intentPhoto, "Select Picture"), IMAGE_GALLERY_REQUEST);
    }

    public void addPhoto(View view) {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            File tmp = getImageFile();
            //System.out.println("pathhhhhhhhhhhhhhhhhhhhhhhhh "+tmp.toString());
            imageUri = Uri.fromFile(tmp);
            //System.out.println("URI-------------------------------> "+imageUri.toString());
            //takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File getImageFile(){
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        String imageName = "photo"+System.currentTimeMillis();
        File imageFile = null;
        try{
            imageFile = File.createTempFile(imageName, ".jpg", dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_GALLERY_REQUEST && data!=null && data.getData()!=null) {
                imageUri = data.getData();
                InputStream inputStream;
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap img = BitmapFactory.decodeStream(inputStream);
                    //show photo
                    image.setImageBitmap(img);
                    try {
                        System.out.println("URI-------------------------------> "+imageUri.toString());
                        bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        System.out.println("BITMAP-------------------------------> "+bmp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }
            }
        }
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            Bundle extras = data.getExtras();
            bmp = (Bitmap) extras.get("data");
            //System.out.println("BITMAP-------------------------------> "+bmp);
            //show photo
            image.setImageBitmap(bmp);
        }
        long nameFile = System.currentTimeMillis();
        ref = storageReference.child(nameFile + ".jpeg");
        //System.out.println("BITMAP-------------------------------> "+bmp);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        byte[] dataByte = bytes.toByteArray();
        //System.out.println("DATA BYTES-------------------------------> "+dataByte.toString());
        ref.putBytes(dataByte)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ConfigUser.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ConfigUser.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imgStorage = String.valueOf(uri);
                                done = true;
                            }
                        });
                    }
                });

    }

    public void setMale(View view) {
        male.setAlpha((float)1.0);
        female.setAlpha((float) 0.7);
        sexo = "M";
    }

    public void setFemale(View view) {
        female.setAlpha((float) 1.0);
        male.setAlpha((float)0.7);
        sexo = "F";
    }

    public void addDate(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**********************************************************/
    //Class interna do calendario
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = 1990;
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT,this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            month = month+1;
            date.setText(dayOfMonth+"/"+month+"/"+year);
        }
    }
    /****************************************/
    public void onNext(View view) throws IOException {
        final String saveName = name.getText().toString();
        final String saveDate = date.getText().toString();
        final Uri url;
        InputStream inputStream;

        if(TextUtils.isEmpty(saveName)){
            name.setError("Name is required!");
            return;
        }
        if(TextUtils.isEmpty(saveDate)){
            date.setError("Date is required!");
            return;
        }
        if(sexo.isEmpty()){
            txtErroSex.setError("Select your genre!");
        }

        if(imageUri != null) {
            if(done) {
                userData = new HashMap<>();
                userData.put("Name", saveName);
                userData.put("Sex", sexo);
                userData.put("Date", saveDate);
                userData.put("Image", imgStorage);
                documentReference.update(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("ADICIONADOOOO");
                    }
                });
                // Ir para o genre
                Intent intent = new Intent(ConfigUser.this, ChooseGenreMovie.class);
                startActivity(intent);
                ConfigUser.this.finish();
            }
        }else{
            //System.out.println("################################################################# "+imgStorage);
            if(sexo.equals("F")){
                imgStorage = "https://firebasestorage.googleapis.com/v0/b/chilltime-10d1d.appspot.com/o/woman.png?alt=media&token=e7918ae3-1435-48f7-8e58-134b32fbb11c";

            }
            else{
                imgStorage = "https://firebasestorage.googleapis.com/v0/b/chilltime-10d1d.appspot.com/o/male.png?alt=media&token=272afc4a-256e-4d24-ab54-55f4f13c7203";

            }
            userData = new HashMap<>();
            userData.put("Name", saveName);
            userData.put("Sex", sexo);
            userData.put("Date", saveDate);
            userData.put("Image", imgStorage);
            documentReference.update(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("ADICIONADOOOO");
                }
            });
            // Ir para o genre
            Intent intent = new Intent(ConfigUser.this, ChooseGenreMovie.class);
            startActivity(intent);
            ConfigUser.this.finish();
        }

    }

}

