package com.example.cchiv.bakingapp.obj;

import android.content.ContentValues;

import com.example.cchiv.bakingapp.data.ContentContract.StepEntry;

public class Step {

    private int id;
    private String shortDescription;
    private String description;
    private String videoURL;
    private String thumbnailURL;

    public Step(int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }

    public ContentValues generateContentValues(long recipeID) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(StepEntry.COL_STEP_RECIPE, recipeID);
        contentValues.put(StepEntry.COL_STEP_DESCRIPTION, this.description);
        contentValues.put(StepEntry.COL_STEP_SHORT_DESCRIPTION, this.shortDescription);
        contentValues.put(StepEntry.COL_STEP_THUMBNAIL_URL, this.thumbnailURL);
        contentValues.put(StepEntry.COL_STEP_VIDEO_URL, this.videoURL);

        return contentValues;
    };

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }
}
