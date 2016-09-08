package com.asazonov.infinitemovielist;

import android.support.annotation.DrawableRes;

import java.util.ArrayList;

/**
 * Test app
 * Created by karabaralex on 06/09/16.
 */
public class Presenter {

    private static final int PAGE_SIZE = 20;
    private IViewer mViewer;

    void bind(IViewer viewer) {
        mViewer = viewer;
    }

    public void loadPage(int page) {
        ArrayList<MovieViewModel> items = new ArrayList<>(PAGE_SIZE);
        for (int i = 0; i < PAGE_SIZE; i++) {
            if ((i % 3) == 0) {
                items.add(createMovie("Драмы" + i, R.drawable.photo_1));
            }
            else if ((i % 3) == 1) {
                items.add(createMovie("Боевики" + i, R.drawable.photo_2));
            }
            else if ((i % 3) == 2) {
                items.add(createMovie("Ужасы" + i, R.drawable.photo_3));
            }
        }

        mViewer.publish(items);
    }

    public void onMovieSelected(int movie) {
        loadPreviews(movie);
    }

    private void loadPreviews(int movie) {
        ArrayList<PreviewViewModel> items = new ArrayList<>();
        String text = "some text" + movie;
        items.add(createPreview(text, R.drawable.big_photo_1));
        items.add(createPreview(text, R.drawable.big_photo_2));
        items.add(createPreview(text, R.drawable.big_photo_3));
        items.add(createPreview(text, R.drawable.big_photo_3));
        items.add(createPreview(text, R.drawable.big_photo_3));
        items.add(createPreview(text, R.drawable.big_photo_3));
        items.add(createPreview(text, R.drawable.big_photo_3));

        mViewer.publishPreviews(items);
    }

    private MovieViewModel createMovie(String text, @DrawableRes int image) {
        MovieViewModel movieViewModel = new MovieViewModel();
        movieViewModel.setTitle(text);
        movieViewModel.setImage(image);
        return movieViewModel;
    }

    private PreviewViewModel createPreview(String text, @DrawableRes int image) {
        PreviewViewModel movieViewModel = new PreviewViewModel();
        movieViewModel.setTitle(text);
        movieViewModel.setImage(image);
        return movieViewModel;
    }
}
