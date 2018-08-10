package com.example.cchiv.bakingapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cchiv.bakingapp.adapters.RecipeAdapter;
import com.example.cchiv.bakingapp.obj.Recipe;
import com.example.cchiv.bakingapp.util.BakingParser;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MasterListFragment extends Fragment {

    private Context context;
    private onListItemListener mCallback;

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
        RecipeAdapter recipeAdapter = new RecipeAdapter(this.context, null, new RecipeAdapter.OnClickListener() {
            @Override
            public void onListClickItem(Recipe recipe) {
                mCallback.onListItemClicked(recipe);
            }
        });

        recyclerView.setAdapter(recipeAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        BakingLoader bakingLoader = new BakingLoader(this.context, recipeAdapter);
        bakingLoader.execute();

        return view;
    }

    public static class BakingLoader extends AsyncTask<Void, Void, ArrayList<Recipe>> {

        private WeakReference<Context> weakReference;
        private RecipeAdapter recipeAdapter;

        private BakingLoader(Context context, RecipeAdapter recipeAdapter) {
            this.weakReference = new WeakReference<>(context);
            this.recipeAdapter = recipeAdapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Recipe> doInBackground(Void... voids) {
            Context context = this.weakReference.get();

            if(context == null)
                return null;

            BakingParser bakingParser = new BakingParser(context);
            StringBuilder stringBuilder = bakingParser.readBackingSource();

            return bakingParser.parseBackingSource(stringBuilder);
        }

        @Override
        protected void onPostExecute(ArrayList<Recipe> recipes) {
            super.onPostExecute(recipes);

            this.recipeAdapter.recipeArrayList.clear();
            this.recipeAdapter.recipeArrayList.addAll(recipes);
            this.recipeAdapter.notifyDataSetChanged();
        }
    }
}
