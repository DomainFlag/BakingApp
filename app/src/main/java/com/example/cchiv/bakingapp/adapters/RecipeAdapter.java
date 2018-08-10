package com.example.cchiv.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.bakingapp.R;
import com.example.cchiv.bakingapp.obj.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnClickListener {
        void onListClickItem(Recipe rec);
    }

    public Context context;
    private OnClickListener onListClickItem;
    public ArrayList<Recipe> recipeArrayList;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipesArrayList, OnClickListener
            onListClickItem) {
        super();

        this.context = context;
        this.onListClickItem = onListClickItem;

        if(recipesArrayList == null)
            this.recipeArrayList = new ArrayList<>();
        else this.recipeArrayList = recipesArrayList;
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextViewName;
        private TextView mTextServings;
        private ImageView mImageViewImage;

        private RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextViewName = itemView.findViewById(R.id.mTextViewName);
            mTextServings = itemView.findViewById(R.id.mTextServings);
            mImageViewImage = itemView.findViewById(R.id.mImageViewImage);
        }
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecipeViewHolder(
                LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.recipe_layout, viewGroup, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final RecipeViewHolder recipeViewHolder, final int i) {
        Recipe recipe = this.recipeArrayList.get(i);

        Picasso.get()
                .load(recipe.getImage())
                .placeholder(R.drawable.ic_tray)
                .error(R.drawable.ic_tray)
                .resize(100, 100)
                .into(recipeViewHolder.mImageViewImage);

        recipeViewHolder.mTextServings.setText(context.getString(R.string.app_recipe_servings, recipe.getServings()));
        recipeViewHolder.mTextViewName.setText(recipe.getName());


        recipeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListClickItem.onListClickItem(recipeArrayList.get(recipeViewHolder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.recipeArrayList.size();
    }
}
