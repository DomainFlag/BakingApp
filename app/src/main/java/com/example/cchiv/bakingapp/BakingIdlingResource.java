package com.example.cchiv.bakingapp;

import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

public class BakingIdlingResource implements IdlingResource {

    private ResourceCallback callback;

    private AtomicBoolean mIsIdleNow = new AtomicBoolean(true);

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return false;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
    }

    public void changeIdleState(boolean isIdleNow) {
        mIsIdleNow.set(isIdleNow);
        if(isIdleNow && callback != null) {
            callback.onTransitionToIdle();
        }
    }
}
