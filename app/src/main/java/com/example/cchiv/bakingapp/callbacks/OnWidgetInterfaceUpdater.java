package com.example.cchiv.bakingapp.callbacks;

import android.content.Context;

import com.example.cchiv.bakingapp.obj.Recipe;

import java.util.ArrayList;

public interface OnWidgetInterfaceUpdater {

    void OnUpdateWidgetInterface(Context context, ArrayList<Recipe> recipes);
}
