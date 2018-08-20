package com.example.cchiv.bakingapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cchiv.bakingapp.R;
import com.example.cchiv.bakingapp.fragments.MasterListFragment;
import com.example.cchiv.bakingapp.obj.Recipe;
import com.example.cchiv.bakingapp.obj.Step;
import com.example.cchiv.bakingapp.util.BakingLoader;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.Allocation;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class StepActivity extends AppCompatActivity implements View.OnClickListener, BakingLoader.OnInterfaceRecipeCallback {

    private final static String RECIPE_ID_KEY = "RECIPE_KEY_ID";
    private final static String RECIPE_STEP_INDEX_KEY = "RECIPE_STEP_INDEX_KEY";
    private final static String RECIPE_STEP_VIDEO_POSITION_KEY = "RECIPE_STEP_VIDEO_POSITION_KEY";
    private final static String RECIPE_STEP_VIDEO_WINDOW_KEY = "RECIPE_STEP_VIDEO_WINDOW_KEY";

    private final static String RECIPE_VIDEO_FILE_ALLOCATOR = "video_buffer";

    private PlayerView playerView;
    private ArrayList<Step> steps;
    private ExoPlayer player = null;

    private View mPreviousStep;
    private View mNextStep;

    private int mStepIndex = 0;
    private long mVideoPosition = -1;
    private int mVideoWindow = -1;

    private boolean mFullscreenMode = false;

    private DefaultLoadControl defaultLoadControl;

    private String fileLocator = null;

    LinearLayout utilitiesLayout;
    LinearLayout playerViewLayout;
    ImageView stepThumbnail;

    private final static String TAG = "StepActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        int id;
        if(savedInstanceState == null) {
            Intent intent = getIntent();
            id = intent.getIntExtra("id", -1);

            if(id == -1) finish();
        } else {
            id = savedInstanceState.getInt(RECIPE_ID_KEY, -1);
            if(id == -1) finish();

            fileLocator = savedInstanceState.getString(RECIPE_VIDEO_FILE_ALLOCATOR);
            mStepIndex = savedInstanceState.getInt(RECIPE_STEP_INDEX_KEY, 0);
            mVideoPosition = savedInstanceState.getLong(RECIPE_STEP_VIDEO_POSITION_KEY, -1);
            mVideoWindow = savedInstanceState.getInt(RECIPE_STEP_VIDEO_WINDOW_KEY, -1);
        }

        BakingLoader bakingLoader = new BakingLoader(this, this, id);
        getSupportLoaderManager().initLoader(MasterListFragment.LOADER_RECIPES, null, bakingLoader).forceLoad();

        utilitiesLayout = findViewById(R.id.step_utilities);
        playerViewLayout = findViewById(R.id.player_view_layout);
        stepThumbnail = findViewById(R.id.step_thumbnail);
        ImageButton imageButton = findViewById(R.id.exo_custom_full_screen);

        immersive_mode_check(imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                immersive_mode_check(view);
            }
        });
    }

    @Override
    public void OnInterfaceRecipeUpdateCallback(Recipe recipe) {
        if(recipe == null)
            finish();

        mPreviousStep = findViewById(R.id.previous_step);
        mNextStep = findViewById(R.id.next_step);

        mPreviousStep.setOnClickListener(this);
        mNextStep.setOnClickListener(this);

        steps = recipe.getSteps();

        playerView = findViewById(R.id.player_view);

        player = createPlayer();

        playerView.setPlayer(player);
        player.setPlayWhenReady(true);

        renderStepContent(playerView, mStepIndex);
    }

    public void immersive_mode_check(View view) {
        ImageButton imageButton = (ImageButton) view;
        if(mFullscreenMode)
            immersive_mode_enter(imageButton);
        else immersive_mode_exit(imageButton);

        mFullscreenMode = !mFullscreenMode;
    }

    public void immersive_mode_enter(ImageButton imageButton) {
        if(Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            if(Build.VERSION.SDK_INT >= 15) {
                findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            }
        }

        if(getActionBar() != null)
            getActionBar().hide();

        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        utilitiesLayout.setVisibility(View.GONE);

        imageButton.setImageResource(R.drawable.exo_controls_fullscreen_exit);
    }

    public void immersive_mode_exit(ImageButton imageButton) {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_VISIBLE);

        if(getActionBar() != null)
            getActionBar().show();

        if(getSupportActionBar() != null)
            getSupportActionBar().show();

        utilitiesLayout.setVisibility(View.VISIBLE);

        imageButton.setImageResource(R.drawable.exo_controls_fullscreen_enter);
    }

    public void renderStepContent(PlayerView playerView, int position) {
        Step step = steps.get(position);

        if(position == 0) {
            mPreviousStep.setVisibility(View.GONE);
        } else if(position == steps.size()-1) {
            mNextStep.setVisibility(View.GONE);
        } else {
            mPreviousStep.setVisibility(View.VISIBLE);
            mNextStep.setVisibility(View.VISIBLE);
        }

        TextView stepDescription = findViewById(R.id.step_description);
        stepDescription.setText(step.getDescription());

        if(playerView != null) {
            if(!step.getVideoURL().isEmpty()) {
                playerViewLayout.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.VISIBLE);
                stepThumbnail.setVisibility(View.GONE);

                setMediaPlayer(step);
            } else {
                playerView.setVisibility(View.GONE);
                player.setPlayWhenReady(false);

                if(!step.getThumbnailURL().isEmpty()) {
                    playerViewLayout.setVisibility(View.VISIBLE);
                    stepThumbnail.setVisibility(View.VISIBLE);

                    Picasso.get().load(step.getThumbnailURL())
                            .placeholder(R.drawable.ic_tray)
                            .error(R.drawable.ic_tray)
                            .into(stepThumbnail);
                } else {
                    playerViewLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    public SimpleExoPlayer createPlayer() {
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(this);

        if(fileLocator == null) {
            defaultLoadControl = new DefaultLoadControl();
        } else {
            defaultLoadControl = new DefaultLoadControl();

            DefaultAllocator allocator = new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE);

            File file = new File(getCacheDir(), fileLocator);

            int size = (int) file.length();
            Toast.makeText(this, String.valueOf(size), Toast.LENGTH_LONG).show();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Allocation allocation = new Allocation(bytes, 0);

            allocator.release(allocation);
            defaultLoadControl = new DefaultLoadControl(allocator, 360000, 600000, 2500, 5000, -1, true);
        }

        return ExoPlayerFactory.newSimpleInstance(defaultRenderersFactory, trackSelector, defaultLoadControl);
    }

    public void setMediaPlayer(Step step) {
        DataSource.Factory factory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this,
                        this.getApplicationContext().getPackageName())
        );

        ExtractorMediaSource.Factory extractorMediaSource = new ExtractorMediaSource.Factory(factory);
        MediaSource mediaSource = extractorMediaSource.createMediaSource(Uri.parse(step.getVideoURL()));

        if(mVideoPosition != -1 && mVideoWindow != -1) {
            player.seekTo(mVideoWindow, mVideoPosition);
        }

        player.prepare(mediaSource, false, false);
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public String saveBufferAllocation(DefaultLoadControl defaultLoadControl, String url) {
        Allocator allocator = defaultLoadControl.getAllocator();
        Allocation allocation = allocator.allocate();

        File file;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, getApplicationContext().getCacheDir());

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(allocation.data);
            fileOutputStream.close();

            return file.getName();
        } catch (IOException e) {
            Log.v("StepActivity", e.toString());
        }

        return null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(RECIPE_VIDEO_FILE_ALLOCATOR, saveBufferAllocation(defaultLoadControl, RECIPE_VIDEO_FILE_ALLOCATOR));

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        if(id != -1) {
            outState.putInt(RECIPE_ID_KEY, id);
        }

        outState.putLong(RECIPE_STEP_VIDEO_POSITION_KEY, player.getCurrentPosition());
        outState.putInt(RECIPE_STEP_INDEX_KEY, mStepIndex);
        outState.putInt(RECIPE_STEP_VIDEO_WINDOW_KEY, player.getCurrentWindowIndex());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();

        return true;
    }

    @Override
    public void onPause() {
        super.onPause();

        if(player != null)
            player.setPlayWhenReady(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(player != null)
            player.setPlayWhenReady(true);
    }

    @Override
    public void onStop() {
        super.onStop();

        if(player != null)
            player.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(player != null)
            player.release();

        if(fileLocator != null) {
            File file = new File(getCacheDir(), fileLocator);
            file.delete();
        }
    }

    @Override
    public void onClick(View view) {
        String type = (String) view.getTag();

        if(type.equals(getString(R.string.app_step_previous))) {
            if(mStepIndex > 0) {
                mStepIndex--;

                renderStepContent(playerView, mStepIndex);
            }
        } else {
            if(mStepIndex < steps.size()-1) {
                mStepIndex++;

                renderStepContent(playerView, mStepIndex);
            }
        }
    }
}
