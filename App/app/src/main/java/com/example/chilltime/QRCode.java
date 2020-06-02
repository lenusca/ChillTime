package com.example.chilltime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.squareup.picasso.Picasso;

import java.util.List;

public class QRCode extends AppCompatActivity {
    CameraView cameraView;
    boolean isDetected = false;
    Button btn_scan;
    Intent intent;
    String urlQRCode;
    //firebase ml kit
    FirebaseVisionBarcodeDetectorOptions options;
    FirebaseVisionBarcodeDetector detector;
    // firebase user
    TextView name;
    ImageView image;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String userID;
    //SIDEBAR
    DrawerLayout sidebar;
    ActionBarDrawerToggle choice;

    // video
    public static final String EXTRA_MESSAGE = "com.example.chilltime.extra.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_code);
       // btn_scan = findViewById(R.id.btnscan);

        // SideBar
        sidebar = (DrawerLayout)findViewById(R.id.sidebar);
        final NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        View headerView = nav_view.getHeaderView(0);
        name = headerView.findViewById(R.id.user_name);
        image = headerView.findViewById(R.id.user_photo);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        // ir buscar o identificador do utilizador
        userID = mAuth.getCurrentUser().getUid();

        // ir buscar o documento relacionado com o utilizador, usando o uid
        final DocumentReference documentReference = mStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                // dizer o que quer ir buscar
                String printname = "";
                String printimage = "";
                printname = documentSnapshot.getString("Name");
                printimage = documentSnapshot.getString("Image");
                name.setText(printname);
                Picasso.get().load(printimage).into(image);
            }
        });
        choice = new ActionBarDrawerToggle(this, sidebar, R.string.Open, R.string.Close);
        sidebar.addDrawerListener(choice);
        choice.setDrawerIndicatorEnabled(true);
        choice.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        nav_view.setItemIconTintList(null);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.list_movies:
                        intent = new Intent(QRCode.this, Movies.class);
                        startActivity(intent);
                        QRCode.this.finish();
                        return true;
                    case R.id.list_series:
                        intent = new Intent(QRCode.this, Series.class);
                        startActivity(intent);
                        QRCode.this.finish();
                        return true;
                    case R.id.gps:
                        intent = new Intent(QRCode.this, GPS.class);
                        startActivity(intent);
                        return true;
                    case R.id.qrcode:
                        intent = new Intent(QRCode.this, QRCode.class);
                        startActivity(intent);
                        return true;
                    case R.id.userinfo:
                        intent = new Intent(QRCode.this, User.class);
                        startActivity(intent);
                        QRCode.this.finish();
                        return true;
                    case R.id.settings:
                        Intent intent = new Intent(QRCode.this, Settings.class);
                        startActivity(intent);
                        QRCode.this.finish();
                        return true;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(QRCode.this, Login.class);
                        startActivity(intent);
                        finish();
                        return true;
                    default:
                        return true;
                }
            }
        });



        //request camera permitions
        Dexter.withActivity(this)
                .withPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        startCamera();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
               .check();

    }



    private void startCamera(){
        //btn_scan.setEnabled(isDetected);
        /*btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDetected = !isDetected;
                System.out.println("is detected "+isDetected);
                btn_scan.setEnabled(isDetected);
            }
        });*/

        cameraView = findViewById(R.id.camera_view);
        cameraView.setLifecycleOwner(this);
        cameraView.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull Frame frame) {
                processImage(getVisionImageFrame(frame));
            }
        });

        //configurar o detetor de código de barras
        options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE, FirebaseVisionBarcode.FORMAT_AZTEC)
                .build();
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
    }

    //sidebar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return choice.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void processImage(FirebaseVisionImage image) {
        if(!isDetected){
            detector.detectInImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                            if(firebaseVisionBarcodes.size()>0){
                                isDetected = false;
                                //btn_scan.setEnabled(isDetected);
                                FirebaseVisionBarcode item = firebaseVisionBarcodes.get(0);
                                switch(item.getValueType()){
                                    case FirebaseVisionBarcode.TYPE_URL:
                                        urlQRCode = item.getRawValue();
                                        break;
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(QRCode.this, " "+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            String[] idYoutube = urlQRCode.split("=");
            //System.out.println("IDDDDDDDDDDD "+idYoutube[1]);
            intent = new Intent(QRCode.this, PlayVideo.class);
            intent.putExtra(EXTRA_MESSAGE, idYoutube[1]);
            QRCode.this.startActivity(intent);
            QRCode.this.finish();
        }

    }

    private FirebaseVisionImage getVisionImageFrame(Frame frame) {
        byte[] data = frame.getData();
        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setHeight(frame.getSize().getHeight())
                .setWidth(frame.getSize().getWidth())
                .build();

        return FirebaseVisionImage.fromByteArray(data, metadata);
    }
}