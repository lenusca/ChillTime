package com.example.chilltime;

import android.content.Intent;

import android.os.Bundle;


import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;


public class PlayVideo extends YouTubeBaseActivity {
    // Views
    YouTubePlayerView video;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        video = findViewById(R.id.video);

        // buscar o id do video no yotube
        final Intent intent = getIntent();
        final String videoId = intent.getStringExtra(DetailsMovie.EXTRA_MESSAGE);

        //

        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                System.out.println("DEU");
                youTubePlayer.loadVideo(videoId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                System.out.println("FAIL");
            }
        };

        // Inicializar Youtube Player
        video.initialize(YoutubeConfig.getApiKey(), onInitializedListener);


    }
}
