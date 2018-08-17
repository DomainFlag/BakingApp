package com.example.cchiv.bakingapp.fragments;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.squareup.picasso.Picasso;

public class RecipeFragment extends Fragment {

    private Context context;
    private Recipe recipe;

    private Boolean toggleMenu = false;

    private LinearLayout linearMenuLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    public void onToggleMenu(View view) {
        if(!toggleMenu)
            linearMenuLayout.setVisibility(View.VISIBLE);
        else linearMenuLayout.setVisibility(View.GONE);

        toggleMenu = !toggleMenu;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_detailed_layout, container, false);

        ListView contentListView = view.findViewById(R.id.content_list);

        View recipeView = inflater.inflate(R.layout.recipe_layout, contentListView, false);

        ImageView imageMenuView = recipeView.findViewById(R.id.menu_drawable);
        LinearLayout linearLayout = recipeView.findViewById(R.id.recipe_menu);
        linearLayout.setVisibility(View.VISIBLE);

        imageMenuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onToggleMenu(view);
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

        Log.v("----------------", String.valueOf(recipe == null));

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

        contentListView.addHeaderView(recipeView);

        ContentAdapter contentAdapter = new ContentAdapter(context, recipe.getIngredients());
        contentListView.setAdapter(contentAdapter);

        return view;
    }

    public void onRecipeAttach(Recipe recipe) {
        this.recipe = recipe;
    }
}
