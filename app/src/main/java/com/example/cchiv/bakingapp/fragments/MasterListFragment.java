package com.example.cchiv.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cchiv.bakingapp.R;
import com.example.cchiv.bakingapp.adapters.RecipeAdapter;
import com.example.cchiv.bakingapp.obj.Recipe;
import com.example.cchiv.bakingapp.util.BakingLoader;

public class MasterListFragment extends Fragment {

    public static final int LOADER_RECIPES = 50;

    private Context context;
    private onListItemListener mCallback;

    private RecipeAdapter recipeAdapter;

    public interface onListItemListener {
        void onListItemClicked(Recipe recipe);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
        this.mCallback = (onListItemListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_master_list, container, false);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        RecyclerView recyclerView = view.findViewById(R.id.recipes_list);
        recipeAdapter = new RecipeAdapter(this.context, null, new RecipeAdapter.OnClickListener() {
            @Override
            public void onListClickItem(Recipe recipe) {
                mCallback.onListItemClicked(recipe);
            }
        });

        recyclerView.setAdapter(recipeAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        BakingLoader bakingLoader = new BakingLoader(context, recipeAdapter);
        getLoaderManager().initLoader(LOADER_RECIPES, null, bakingLoader).forceLoad();

        return view;
    }
}
