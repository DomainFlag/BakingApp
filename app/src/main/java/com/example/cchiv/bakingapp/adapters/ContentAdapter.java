package com.example.cchiv.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cchiv.bakingapp.R;
import com.example.cchiv.bakingapp.obj.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class ContentAdapter extends BaseAdapter {

    private List<String> labels = new ArrayList<>();
    private List<Ingredient> ingredients;

    private Context context;

    public ContentAdapter(Context context, ArrayList<Ingredient> ingredients) {
        super();

        this.context = context;

        this.labels.clear();
        this.labels.add("Ingredients");

        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Object content = getItem(position);

        if(content instanceof String) {
            return inflateLabelView(content, convertView, parent);
        } else {
            return inflateIngredientView(content, convertView, parent);
        }
    }

    private View inflateLabelView(Object content, View convertView, ViewGroup parent) {
        String label = (String) content;

        if(convertView == null || convertView.findViewById(R.id.content_label) == null)
            convertView = LayoutInflater.from(this.context)
                    .inflate(R.layout.content_label, parent, false);

        ((TextView) convertView.findViewById(R.id.content_label)).setText(label);

        return convertView;
    }

    private View inflateIngredientView(Object content, View convertView, ViewGroup parent) {
        Ingredient ingredient = (Ingredient) content;

        if(convertView == null || convertView.findViewById(R.id.ingredient_name) == null)
            convertView = LayoutInflater.from(this.context)
                    .inflate(R.layout.ingredient_layout, parent, false);

        TextView ingredientName = (TextView) convertView.findViewById(R.id.ingredient_name);
        ingredientName.setText(ingredient.getIngredient());

        TextView ingredientQuantity = (TextView) convertView.findViewById(R.id.ingredient_quantity);
        ingredientQuantity.setText(context.getString(R.string.app_recipe_quantity, ingredient.getQuantity(), ingredient.getMeasure()));

        return convertView;
    }

    public void onReplace(ArrayList<Ingredient> ingredients) {
        if(this.ingredients != ingredients)
            this.ingredients = ingredients;
    }

    @Override
    public int getCount() {
        /* We need to inflate also the Label layout if there is any content */
        return (!this.ingredients.isEmpty() ? this.ingredients.size() + 1 : 0);
    }

    @Override
    public Object getItem(int position) {
        if(!this.ingredients.isEmpty() && position == 0)
            return this.labels.get(0);
        else if(!this.ingredients.isEmpty()) {
            return this.ingredients.get(position - 1);
        } else return 0;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
