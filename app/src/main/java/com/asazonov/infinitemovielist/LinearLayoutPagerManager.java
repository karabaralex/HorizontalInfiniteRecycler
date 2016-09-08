package com.asazonov.infinitemovielist;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public class LinearLayoutPagerManager extends LinearLayoutManager {

    private final int space;
    private int mItemsPerPage;

    public LinearLayoutPagerManager(Context context, int orientation, boolean reverseLayout, int itemsPerPage, int space) {
        super(context, orientation, reverseLayout);

        this.space = space;
        mItemsPerPage = itemsPerPage;
    }

    @Override
    public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        return super.checkLayoutParams(lp) && lp.width == getItemSize();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return setProperItemSize(super.generateDefaultLayoutParams());
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return setProperItemSize(super.generateLayoutParams(lp));
    }

    private RecyclerView.LayoutParams setProperItemSize(RecyclerView.LayoutParams lp) {
        lp.width = getItemSize();
        return lp;
    }

    private int getItemSize() {
        int pageSize = getWidth() - this.space * (mItemsPerPage - 1);
        return Math.round((float) pageSize / mItemsPerPage);
    }
}