package com.example.cchiv.bakingapp.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.cchiv.bakingapp.R;
import com.example.cchiv.bakingapp.obj.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

public class BakingPlayer {

    private Context context = null;
    private AppCompatActivity activity = null;

    public static ExoPlayer exoPlayer = null;

    private boolean mIdleMode = false;

    public void onViewPlayer(Context context, AppCompatActivity activity, View rootView, final View secondaryView, Step step, boolean configChange) {
        this.context = context;
        this.activity = activity;

        ImageView stepThumbnail = rootView.findViewById(R.id.step_thumbnail);
        PlayerView playerView = rootView.findViewById(R.id.player_view);

        if(!step.getVideoURL().isEmpty()) {
            mIdleMode = false;
            if(exoPlayer == null) {
                setPlayer(rootView, secondaryView, step);
            } else {
                if(configChange) {
                    exoPlayer.setPlayWhenReady(true);
                    setPlayer(rootView, secondaryView, step);
                } else {
                    restartPlayer(step);
                    exoPlayer.setPlayWhenReady(true);
                }

                playerView.setPlayer(exoPlayer);
            }

            playerView.setVisibility(View.VISIBLE);
            stepThumbnail.setVisibility(View.GONE);
        } else {
            mIdleMode = true;
            playerView.setVisibility(View.GONE);

            if(!step.getThumbnailURL().isEmpty()) {
                stepThumbnail.setVisibility(View.VISIBLE);

                Picasso.get().load(step.getThumbnailURL())
                        .placeholder(R.drawable.ic_tray)
                        .error(R.drawable.ic_tray)
                        .into(stepThumbnail);
            };

            if(exoPlayer != null)
                exoPlayer.setPlayWhenReady(false);
        }
    }

    private void setPlayer(View rootView, final View secondaryView, Step step) {
        final ImageButton imageScreenExitButton = rootView.findViewById(R.id.exo_screen_exit);
        final ImageButton imageScreenEnterButton = rootView.findViewById(R.id.exo_screen_enter);

        imageScreenExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                immersive_mode_exit(imageScreenExitButton, imageScreenEnterButton, secondaryView);
            }
        });

        imageScreenEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                immersive_mode_enter(imageScreenEnterButton, imageScreenExitButton, secondaryView);
            }
        });


        if(exoPlayer == null) {
            exoPlayer = createPlayer();
            setMediaPlayer(exoPlayer, step);
        }

        PlayerView playerView = rootView.findViewById(R.id.player_view);
        playerView.setPlayer(exoPlayer);
        exoPlayer.setPlayWhenReady(true);
    }

    private void restartPlayer(Step step) {
        if(exoPlayer != null) {
            setMediaPlayer(exoPlayer, step);
        }
    }

    private ExoPlayer createPlayer() {
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(context);
        DefaultLoadControl defaultLoadControl = new DefaultLoadControl();

        return ExoPlayerFactory.newSimpleInstance(defaultRenderersFactory, trackSelector, defaultLoadControl);
    }

    private void setMediaPlayer(ExoPlayer player, Step step) {
        DataSource.Factory factory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context,
                        context.getPackageName())
        );

        ExtractorMediaSource.Factory extractorMediaSource = new ExtractorMediaSource.Factory(factory);
        MediaSource mediaSource = extractorMediaSource.createMediaSource(Uri.parse(step.getVideoURL()));

        player.prepare(mediaSource);
    }

    private void immersive_mode_enter(ImageButton imageButton, View imageSecondaryView, View secondaryView) {
        secondaryView.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
        imageSecondaryView.setVisibility(View.VISIBLE);

        if(Build.VERSION.SDK_INT >= 19) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            if(Build.VERSION.SDK_INT >= 15) {
                activity.findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            }
        }

        if(activity.getSupportActionBar() != null)
            activity.getSupportActionBar().hide();
    }

    private void immersive_mode_exit(ImageButton imageButton, View imageSecondaryView, View secondaryView) {
        secondaryView.setVisibility(View.VISIBLE);
        imageButton.setVisibility(View.GONE);
        imageSecondaryView.setVisibility(View.VISIBLE);

        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_VISIBLE);


        if(activity.getSupportActionBar() != null)
            activity.getSupportActionBar().show();
    }

    /**
     * In case the player will gonna get in resume state in a step section without any video
     */
    public void setPlayWhenReady(boolean playWhenReady) {
        if(exoPlayer != null) {
            if(playWhenReady) {
                if(!mIdleMode)
                    exoPlayer.setPlayWhenReady(true);
            } else {
                exoPlayer.setPlayWhenReady(false);
            }
        }
    }

    public void onDestroy(boolean isChangingConfigurations) {
        if(exoPlayer != null) {
            if(isChangingConfigurations) {
                exoPlayer.setPlayWhenReady(false);
            } else {
                exoPlayer.release();
                exoPlayer = null;
            }
        }
    }
}
