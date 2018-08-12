package com.example.cchiv.bakingapp;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.cchiv.bakingapp.obj.Recipe;
import com.example.cchiv.bakingapp.obj.Step;
import com.example.cchiv.bakingapp.util.BakingLoader;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class StepActivity extends AppCompatActivity implements View.OnClickListener, BakingLoader.OnInterfaceCallback {

    private PlayerView playerView;
    private ArrayList<Step> steps;
    private SimpleExoPlayer player = null;

    private View mPreviousStep;
    private View mNextStep;

    private int mStepIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        Intent intent = getIntent();
        intent.getIntExtra("id", -1);

        BakingLoader bakingLoader = new BakingLoader(this);
        getSupportLoaderManager().initLoader(20, null, bakingLoader).forceLoad();

        ActionBar actionBar = getActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void OnInterfaceUpdateCallback(ArrayList<Recipe> recipes) {
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        Recipe recipe = getRecipe(recipes, id);
        if(recipe == null)
            finish();

        mPreviousStep = findViewById(R.id.previous_step);
        mNextStep = findViewById(R.id.next_step);

        mPreviousStep.setOnClickListener(this);
        mNextStep.setOnClickListener(this);

        steps = recipe.getSteps();

        playerView = findViewById(R.id.step_video);

        player = createPlayer();
        playerView.setPlayer(player);

        renderStepContent(playerView, mStepIndex);
    }

    public Recipe getRecipe(ArrayList<Recipe> recipes, int id) {
        if(id == -1)
            return null;

        for(int it = 0; it < recipes.size(); it++) {
            if(recipes.get(it).getId() == id)
                return recipes.get(it);
        }

        return null;
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

        if(!step.getVideoURL().isEmpty()) {
            playerView.setVisibility(View.VISIBLE);

            setMediaPlayer(step);
        } else {
            playerView.setVisibility(View.GONE);

            player.setPlayWhenReady(false);
        }
    }

    public SimpleExoPlayer createPlayer() {
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(this);
        DefaultLoadControl defaultLoadControl = new DefaultLoadControl();

        return ExoPlayerFactory.newSimpleInstance(defaultRenderersFactory, trackSelector, defaultLoadControl);
    }

    public void setMediaPlayer(Step step) {
        DataSource.Factory factory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this,
                        this.getApplicationContext().getPackageName())
        );

        ExtractorMediaSource.Factory extractorMediaSource = new ExtractorMediaSource.Factory(factory);

        MediaSource mediaSource = extractorMediaSource.createMediaSource(Uri.parse(step.getVideoURL()));

        player.prepare(mediaSource);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(player != null)
            player.release();
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
