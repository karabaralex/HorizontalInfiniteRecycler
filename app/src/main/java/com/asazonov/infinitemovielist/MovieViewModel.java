package com.asazonov.infinitemovielist;

import android.support.annotation.DrawableRes;

/**
 * Test app
 * Created by karabaralex on 06/09/16.
 */
public class MovieViewModel {
    private String mTitle;
    @DrawableRes
    private int mImage;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getImage() {
        return mImage;
    }

    public void setImage(int mImage) {
        this.mImage = mImage;
    }
}
