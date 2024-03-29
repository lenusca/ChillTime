package com.example.chilltime.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chilltime.menu.DashBoard;
import com.example.chilltime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class Notifications extends Service {
    // Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String userID;
    List<String> dateList = new ArrayList<>();
    List<String> seriesList = new ArrayList<>();
    // Must create a default constructor
    Handler mhandler = new Handler();
    RequestQueue mQueue;

    // Define the code block to be executed
    public static final long NOTIFY_INTERVAL = 20 * 1000; // 20 seconds
    private Timer mTimer = null;
    // Run the above code block on the main thread after 2 seconds


    @Override
    public void onCreate() {
        createNotificationChannel();
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        String message[] = intent.getStringExtra(DashBoard.EXTRA_MESSAGE).split(":")[0].replace("[", "").replace("]", "").split(", ");
        dateList = Arrays.asList(message);
        String series[] = intent.getStringExtra(DashBoard.EXTRA_MESSAGE).split(":")[1].replace("[", "").replace("]", "").split(", ");
        for(int i=0; i<series.length; i++){
            // show details
            mQueue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.themoviedb.org/3/tv/"+series[i]+"?api_key=6458cccff38c4ec22f31df407f03048e&language=en-US", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                if(response.getString("original_name").contains(":")){
                                    seriesList.add(response.getString("original_name").split(":")[0]);
                                }
                                else if(response.getString("original_name").contains("(")){
                                    seriesList.add(response.getString("original_name").split(Pattern.quote("("))[0]);
                                }
                                else{
                                    System.out.println(response.getString("original_name"));
                                    seriesList.add(response.getString("original_name").toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("ERROR");
                    error.printStackTrace();
                }
            });
            mQueue.add(request);
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "asd";
            String description = "asd2";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("asd3", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mhandler.post(new Runnable() {

                @Override
                public void run() {
                    // se hoje o dia for igual imprime a notifcação
                    if(dateList.contains(getDateTime()) && seriesList.size() == dateList.size()){

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "asd3")
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle(seriesList.get(dateList.indexOf(getDateTime())))
                                .setContentText("New episode")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                        // notificationId is a unique int for each notification that you must define
                        notificationManager.notify(1, builder.build());
                    }


                }

            });
        }

        private String getDateTime() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(new Date());

        }
    }
}

