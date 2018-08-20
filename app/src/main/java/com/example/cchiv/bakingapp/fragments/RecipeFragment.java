package com.example.cchiv.bakingapp.fragments;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cchiv.bakingapp.BakingWidgetProvider;
import com.example.cchiv.bakingapp.R;
import com.example.cchiv.bakingapp.activities.StepActivity;
import com.example.cchiv.bakingapp.adapters.ContentAdapter;
import com.example.cchiv.bakingapp.obj.Recipe;
import com.example.cchiv.bakingapp.util.BakingLoader;
import com.squareup.picasso.Picasso;

public class RecipeFragment extends Fragment implements BakingLoader.OnInterfaceRecipeCallback {

    public static final int LOADER_RECIPES = 50;

    public static final String RECIPE_ID_KEY = "recipe_id";

    private Context context;
    private Recipe recipe;

    private Boolean toggleMenu = false;

    private LinearLayout linearMenuLayout;

    private ListView contentListView;
    private View recipeView;
    private ContentAdapter contentAdapter = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    public void onToggleMenu() {
        if(!toggleMenu)
            linearMenuLayout.setVisibility(View.VISIBLE);
        else linearMenuLayout.setVisibility(View.GONE);

        toggleMenu = !toggleMenu;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_detailed_layout, container, false);

        contentListView = view.findViewById(R.id.content_list);
        recipeView = inflater.inflate(R.layout.recipe_layout, contentListView, false);

        ((LinearLayout) recipeView.findViewById(R.id.recipe_menu)).setVisibility(View.VISIBLE);
        ((ImageView) recipeView.findViewById(R.id.menu_drawable)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onToggleMenu();
            }
        });

        linearMenuLayout = recipeView.findViewById(R.id.menu_container);
        for(int it = 0; it < linearMenuLayout.getChildCount(); it++) {
            TextView textView = (TextView) linearMenuLayout.getChildAt(it);

            switch(textView.getId()) {
                case R.id.recipe_follow : {
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                            intent.putExtra("type", BakingWidgetProvider.UPDATE_RECIPE);
                            intent.putExtra("id", recipe.getId());

                            int[] appWidgetId = AppWidgetManager.getInstance(context).getAppWidgetIds(
                                    new ComponentName(context, BakingWidgetProvider.class));

                            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);

                            context.sendBroadcast(intent);

                            linearMenuLayout.setVisibility(View.GONE);
                        }
                    });
                    break;
                }
                case R.id.recipe_see_more : {
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), StepActivity.class);
                            intent.putExtra("id", recipe.getId());

                            startActivity(intent);

                            linearMenuLayout.setVisibility(View.GONE);
                        }
                    });
                    break;
                }
                default : {
                    linearMenuLayout.setVisibility(View.GONE);

                    break;
                }
            }
        }

        contentListView.addHeaderView(recipeView);

        if(savedInstanceState != null) {
            BakingLoader bakingLoader = new BakingLoader(context, this, savedInstanceState.getInt(RECIPE_ID_KEY));
            getLoaderManager().initLoader(LOADER_RECIPES, null, bakingLoader).forceLoad();
        } else {
            onFragmentDataChanged();
        }

        return view;
    }

    @Override
    public void OnInterfaceRecipeUpdateCallback(Recipe recipe) {
        this.recipe = recipe;

        if(recipe != null)
            onFragmentDataChanged();
    }

    public void onFragmentDataChanged() {
        ImageView imageView = (ImageView) recipeView.findViewById(R.id.mImageViewImage);
        Picasso.get()
                .load(recipe.getImage())
                .placeholder(R.drawable.ic_tray)
                .error(R.drawable.ic_tray)
                .resize(100, 100)
                .into(imageView);

        ((TextView) recipeView.findViewById(R.id.mTextViewName)).setText(recipe.getName());
        ((TextView) recipeView.findViewById(R.id.mTextServings)).setText(
                getString(R.string.app_recipe_servings, recipe.getServings())
        );

        if(contentAdapter == null) {
            contentAdapter = new ContentAdapter(context, recipe.getIngredients());
            contentListView.setAdapter(contentAdapter);
        } else {
            contentAdapter.onReplace(recipe.getIngredients());
            contentAdapter.notifyDataSetChanged();
        }
    }

    public void onChangeRecipe(Recipe recipe) {
        this.recipe = recipe;
        onFragmentDataChanged();
    };

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if(recipe != null)
            outState.putInt(RECIPE_ID_KEY, recipe.getId());
    }

    public void onRecipeAttach(Recipe recipe) {
        this.recipe = recipe;
    }
}
