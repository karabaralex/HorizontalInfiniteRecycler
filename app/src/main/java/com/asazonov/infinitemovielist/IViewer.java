package com.asazonov.infinitemovielist;

import java.util.ArrayList;

/**
 * Test app
 * Created by karabaralex on 06/09/16.
 */
public interface IViewer {
    void publish(ArrayList<MovieViewModel> items);

    void publishPreviews(ArrayList<PreviewViewModel> items);
}
