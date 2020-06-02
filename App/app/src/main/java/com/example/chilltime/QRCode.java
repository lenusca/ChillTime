package com.example.chilltime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    // video
    public static final String EXTRA_MESSAGE = "com.example.chilltime.extra.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_code);
        btn_scan = findViewById(R.id.btnscan);

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
        btn_scan.setEnabled(isDetected);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDetected = !isDetected;
                System.out.println("is detected "+isDetected);
                btn_scan.setEnabled(isDetected);

            }
        });

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

    private void processImage(FirebaseVisionImage image) {
        if(!isDetected){
            detector.detectInImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                            if(firebaseVisionBarcodes.size()>0){
                                isDetected = false;
                                btn_scan.setEnabled(isDetected);
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
            System.out.println("IDDDDDDDDDDD "+idYoutube[1]);
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