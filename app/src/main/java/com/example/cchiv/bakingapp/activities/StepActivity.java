package com.example.cchiv.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.cchiv.bakingapp.R;
import com.example.cchiv.bakingapp.fragments.MasterListFragment;
import com.example.cchiv.bakingapp.obj.Recipe;
import com.example.cchiv.bakingapp.obj.Step;
import com.example.cchiv.bakingapp.util.BakingLoader;
import com.example.cchiv.bakingapp.util.BakingPlayer;

import java.util.ArrayList;

public class StepActivity extends AppCompatActivity implements View.OnClickListener, BakingLoader.OnInterfaceRecipeCallback {

    private final static String RECIPE_ID_KEY = "RECIPE_KEY_ID";
    private final static String RECIPE_STEP_INDEX_KEY = "RECIPE_STEP_INDEX_KEY";

    private ArrayList<Step> steps;

    private View mPreviousStep;
    private View mNextStep;

    private int mStepIndex = 0;

    private BakingPlayer bakingPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        int id;
        if(savedInstanceState == null) {
            Intent intent = getIntent();
            id = intent.getIntExtra("id", -1);
        } else {
            id = savedInstanceState.getInt(RECIPE_ID_KEY, -1);
            mStepIndex = savedInstanceState.getInt(RECIPE_STEP_INDEX_KEY, 0);
        }

        if(id == -1) finish();

        BakingLoader bakingLoader = new BakingLoader(this, this, id);
        getSupportLoaderManager().initLoader(MasterListFragment.LOADER_RECIPES, null, bakingLoader).forceLoad();

        bakingPlayer = new BakingPlayer();
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

        renderStepContent(mStepIndex, true);
    }

    public void renderStepContent(int position, boolean configChange) {
        Step step = steps.get(position);

        if(position == 0) {
            mPreviousStep.setVisibility(View.GONE);
        } else if(position == steps.size()-1) {
            mNextStep.setVisibility(View.GONE);
        } else {
            mPreviousStep.setVisibility(View.VISIBLE);
            mNextStep.setVisibility(View.VISIBLE);
        }

        View playerView = findViewById(R.id.player_layout);
        View utilitiesView = findViewById(R.id.step_utilities);
        bakingPlayer.onViewPlayer(this, this, playerView, utilitiesView, steps.get(mStepIndex), configChange);

        TextView stepDescription = findViewById(R.id.step_description);
        stepDescription.setText(step.getDescription());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        if(id != -1) {
            outState.putInt(RECIPE_ID_KEY, id);
        }

        outState.putInt(RECIPE_STEP_INDEX_KEY, mStepIndex);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();

        return true;
    }

    @Override
    public void onClick(View view) {
        String type = (String) view.getTag();

        if(type.equals(getString(R.string.app_step_previous))) {
            if(mStepIndex > 0) {
                mStepIndex--;

                renderStepContent(mStepIndex, false);
            }
        } else {
            if(mStepIndex < steps.size()-1) {
                mStepIndex++;

                renderStepContent(mStepIndex, false);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(bakingPlayer != null)
            bakingPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(bakingPlayer != null)
            bakingPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onStop() {
        super.onStop();

        if(bakingPlayer != null)
            bakingPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(bakingPlayer != null) {
            bakingPlayer.onDestroy(isChangingConfigurations());
        }
    }
}
