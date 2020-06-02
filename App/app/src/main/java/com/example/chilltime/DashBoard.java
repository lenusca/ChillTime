package com.example.chilltime;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.chilltime.service.Notifications;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

public class DashBoard extends AppCompatActivity {

    Button btMovies, btSeries, btGPS, btQrCode, btUserActivity, btSettings;
    ImageView logo;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String userID;
    DocumentReference documentReference;
    List<String> dateList;
    List<String> serieList;
    // Para mandar o id para outra activity(details)
    public static final String EXTRA_MESSAGE = "com.example.chilltime.extra.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Buttons
        btMovies = findViewById(R.id.btMovies);
        btSeries = findViewById(R.id.btSeries);
        btGPS = findViewById(R.id.btGPS);
        btQrCode = findViewById(R.id.btQrCode);
        btUserActivity = findViewById(R.id.btUserActivity);
        btSettings = findViewById(R.id.btSettings);
        //Image View
        logo = findViewById(R.id.imageView2);

        dateList = new ArrayList<>();
        serieList = new ArrayList<>();

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode){
            case Configuration.UI_MODE_NIGHT_NO:
                logo.setImageResource(R.drawable.logo);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                logo.setImageResource(R.drawable.logo_dark);
                break;
        }

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        // ir buscar o identificador do utilizador
        userID = mAuth.getCurrentUser().getUid();
        // ir buscar o documento relacionado com o utilizador, usando o uid
        documentReference = mStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.get("WatchesSeriesDate") != null){ ;
                    dateList = (List<String>) documentSnapshot.get("WatchesSeriesDate");
                    serieList = (List<String>) documentSnapshot.get("WatchesSeries");
                    if(!isMyServiceRunning(Notifications.class)){

                        Intent intent = new Intent(DashBoard.this, Notifications.class);
                        intent.putExtra(EXTRA_MESSAGE, dateList.toString() + ":"+ serieList.toString());
                        startService(intent);
                    }
                }
                if(documentSnapshot.getString("Dark")!=null){
                    if(documentSnapshot.getString("Dark").equals("1")){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }else{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                }
            }
        });

        notification();
    }


    private void notification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("n", "n", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n").setContentText("New Series and Movies").setSmallIcon(R.drawable.logo).setAutoCancel(true);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999, builder.build());

    }


    public void GPS(View view) {
        //isServiceOK();
        Intent intent = new Intent(this, GPS.class);
        startActivity(intent);
        DashBoard.this.finish();
    }

    public void Movies(View view) {
        Intent intent = new Intent(this, Movies.class);
        startActivity(intent);
        DashBoard.this.finish();
    }


    public void Series(View view) {
        Intent intent = new Intent(this, Series.class);
        startActivity(intent);
        DashBoard.this.finish();
    }

    public void QRCode(View view) {
        Intent intent = new Intent(this, QRCode.class);
        startActivity(intent);
        //DashBoard.this.finish();
    }

    public void Settings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        DashBoard.this.finish();
    }

    public void UserActivity(View view) {
        Intent intent = new Intent(this, User.class);
        startActivity(intent);
        DashBoard.this.finish();
    }

    // GOOGLE MAPS
    public boolean isServiceOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(DashBoard.this);
        if(available == ConnectionResult.SUCCESS){
            System.out.println("Google play service is working");
            return true;
        }
        return false;
    }

    // Notifcações
    // verifica se o serviço esta a correr
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
