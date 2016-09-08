package com.asazonov.infinitemovielist;

import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public class AwesomeLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "AwesomeLayoutManager";

    private static final float SCALE_THRESHOLD_PERCENT = 0.66f;
    private SparseArray<View> viewCache = new SparseArray<>();
    private int mAnchorPos;

    public AwesomeLayoutManager() {

    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        fill(recycler);
        mAnchorPos = 0;
    }

    private void fill(RecyclerView.Recycler recycler) {

        View anchorView = getAnchorView();
        viewCache.clear();
        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            View view = getChildAt(i);
            int pos = getPosition(view);
            viewCache.put(pos, view);
        }

        for (int i = 0; i < viewCache.size(); i++) {
            detachView(viewCache.valueAt(i));
        }

        fillLeft(anchorView, recycler);
        fillRight(anchorView, recycler);

        for (int i = 0; i < viewCache.size(); i++) {
            recycler.recycleView(viewCache.valueAt(i));
        }
    }

    int mCenterPos = -1;

    private void fillLeft(@Nullable View anchorView, RecyclerView.Recycler recycler) {
        int width = getWidth();
        int viewWidth = width / 7;
        int bigWidth = viewWidth * 2;
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY);
        final int bigWidthSpec = View.MeasureSpec.makeMeasureSpec(bigWidth, View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        int anchorPos;
        int anchorLeft = 0;
        if (anchorView != null) {
            anchorPos = getPosition(anchorView);
            anchorLeft = getDecoratedLeft(anchorView);
        } else {
            anchorPos = mAnchorPos;
            anchorLeft = -width / 2;
        }
        int center = getCenter();
        boolean fillLeft = true;
        int pos = anchorPos - 1;
        int viewRight = anchorLeft;
        int height = getHeight() - getPaddingBottom() - getPaddingTop();

        while (fillLeft) {
            int aPos = pos >= 0 ? pos : getItemCount() + pos;
            boolean isCenter = center < viewRight && center > (viewRight - bigWidth);
            if (isCenter) {
                mCenterPos = aPos;
            }
            View view;
            view = recycler.getViewForPosition(aPos);
            recycler.bindViewToPosition(view, aPos);
            addView(view, 0);
            if (isCenter) {
                measureChildWithDecorationsAndMargin(view, bigWidthSpec, heightSpec);
            } else {
                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
            }
            int decoratedMeasuredHeight = getDecoratedMeasuredHeight(view);
            int decoratedMeasuredWidth = getDecoratedMeasuredWidth(view);
            layoutDecorated(view, viewRight - decoratedMeasuredWidth, getPaddingTop() + height - decoratedMeasuredHeight, viewRight, getPaddingTop() + height);
            viewRight = getDecoratedLeft(view);
            fillLeft = (viewRight > 0);
            pos--;
        }
    }

    private void fillRight(View anchorView, RecyclerView.Recycler recycler) {
        int width = getWidth();
        int viewWidth = getWidth() / 7;
        int bigWidth = viewWidth * 2;
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY);
        final int bigWidthSpec = View.MeasureSpec.makeMeasureSpec(bigWidth, View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        int anchorPos;
        int anchorLeft;
        if (anchorView != null) {
            anchorPos = getPosition(anchorView);
            anchorLeft = getDecoratedLeft(anchorView);
        } else {
            anchorPos = mAnchorPos;
            anchorLeft = -viewWidth / 2;
        }

        int center = getCenter();
        int pos = anchorPos;
        boolean fillRight = true;
        int viewLeft = anchorLeft;
        int itemCount = getItemCount();
        int height = getHeight() - getPaddingBottom() - getPaddingTop();

        while (fillRight) {
            int aPos = pos < itemCount ? pos : pos % itemCount;
            boolean isCenter = center > viewLeft && center < (viewLeft + bigWidth);
            if (isCenter) {
                mCenterPos = aPos;
            }
            View view;
            view = recycler.getViewForPosition(aPos);
            recycler.bindViewToPosition(view, aPos);
            addView(view);
            if (isCenter) {
                mCenterPos = aPos;
                measureChildWithDecorationsAndMargin(view, bigWidthSpec, heightSpec);
            } else {
                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
            }
            int decoratedMeasuredHeight = getDecoratedMeasuredHeight(view);
            int decoratedMeasuredWidth = getDecoratedMeasuredWidth(view);
            layoutDecorated(view, viewLeft, getPaddingTop() + height - decoratedMeasuredHeight, viewLeft + decoratedMeasuredWidth, getPaddingTop() + height);
            viewLeft = getDecoratedRight(view);
            fillRight = viewLeft <= width;
            pos++;
        }
    }

    public int centerPosition() {
        return mCenterPos;
    }

    private void updateViewScale() {
        int childCount = getChildCount();
        int height = getHeight();
        int thresholdPx = (int) (height * SCALE_THRESHOLD_PERCENT);
        for (int i = 0; i < childCount; i++) {
            float scale = 1f;
            View view = getChildAt(i);
            int viewTop = getDecoratedTop(view);
            if (viewTop >= thresholdPx) {
                int delta = viewTop - thresholdPx;
                scale = (height - delta) / (float) height;
                scale = Math.max(scale, 0);
            }
            view.setPivotX(view.getHeight() / 2);
            view.setPivotY(view.getHeight() / -2);
            view.setScaleX(scale);
            view.setScaleY(scale);
        }
    }

    private View getAnchorView() {
        int childCount = getChildCount();
        Rect mainRect = new Rect(0, 0, getWidth(), getHeight());
        int maxSquare = 0;
        View anchorView = null;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            int top = getDecoratedTop(view);
            int bottom = getDecoratedBottom(view);
            int left = getDecoratedLeft(view);
            int right = getDecoratedRight(view);
            Rect viewRect = new Rect(left, top, right, bottom);
            boolean intersect = viewRect.intersect(mainRect);
            if (intersect) {
                int square = viewRect.width() * viewRect.height();
                if (square > maxSquare) {
                    anchorView = view;
                }
            }
        }
        return anchorView;
    }

    public int findFirstVisibleItemPosition() {
        int childCount = getChildCount();
        Rect mainRect = new Rect(0, 0, getWidth(), getHeight());
        int maxSquare = 0;
        View anchorView = null;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            int top = getDecoratedTop(view);
            int bottom = getDecoratedBottom(view);
            int left = getDecoratedLeft(view);
            int right = getDecoratedRight(view);
            Rect viewRect = new Rect(left, top, right, bottom);
            boolean intersect = viewRect.intersect(mainRect);
            if (intersect) {
                int square = viewRect.width() * viewRect.height();
                if (square > 0) {
                    return getPosition(view);
                }
            }
        }

        return -1;
    }

    public void scrollToCenter(RecyclerView recyclerView) {
        int pos = -1;
        final int center = getCenter();
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int left = getDecoratedLeft(view);
            int right = getDecoratedRight(view);

            if (left < center && right > center) {
                pos = getPosition(view);
                break;
            }
        }

        if (pos > 0)
            smoothScrollToPosition(recyclerView, null, pos);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, final int position) {
        if (position >= getItemCount()) {
            Log.e(TAG, "Cannot scroll to " + position + ", item count is " + getItemCount());
            return;
        }

        final int center = getCenter();

        LinearSmoothScroller scroller = new LinearSmoothScroller(recyclerView.getContext()) {
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {

                return AwesomeLayoutManager.this.computeScrollVectorForPosition(targetPosition);
            }

            @Override
            public int calculateDxToMakeVisible(View view, int snapPreference) {
                int i = center - (getDecoratedLeft(view) + getDecoratedMeasuredWidth(view) / 2);
                if (getDecoratedLeft(view) > center) i = center - getDecoratedLeft(view);
                if (getDecoratedRight(view) < center) i = center - getDecoratedRight(view);
                return i;
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 150f / displayMetrics.densityDpi;
            }
        };
        scroller.setTargetPosition(position);
        startSmoothScroll(scroller);
    }

    private int getCenter() {
        return getWidth() / 2;
    }

    private PointF computeScrollVectorForPosition(int targetPosition) {
        if (getChildCount() == 0) {
            return null;
        }
        final int firstChildPos = getPosition(getChildAt(0));
        final int direction = targetPosition < firstChildPos ? -1 : 1;

        return new PointF(direction, 0);
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int delta = scrollHorizontallyInternal(dx);

        offsetChildrenHorizontal(-delta);
        fill(recycler);
        return delta;
    }

    private int scrollHorizontallyInternal(int dx) {
        int childCount = getChildCount();
        int itemCount = getItemCount();
        if (childCount == 0) {
            return 0;
        }

        int delta = 0;
        if (dx < 0) {
            delta = dx;
        } else if (dx > 0) {
            delta = dx;
        }
        return delta;
    }

    private void measureChildWithDecorationsAndMargin(View child, int widthSpec, int heightSpec) {
        Rect decorRect = new Rect();
        calculateItemDecorationsForChild(child, decorRect);
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        widthSpec = updateSpecWithExtra(widthSpec, lp.leftMargin + decorRect.left,
                lp.rightMargin + decorRect.right);
        heightSpec = updateSpecWithExtra(heightSpec, lp.topMargin + decorRect.top,
                lp.bottomMargin + decorRect.bottom);
        child.measure(widthSpec, heightSpec);
    }

    private int updateSpecWithExtra(int spec, int startInset, int endInset) {
        if (startInset == 0 && endInset == 0) {
            return spec;
        }
        final int mode = View.MeasureSpec.getMode(spec);
        if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY) {
            return View.MeasureSpec.makeMeasureSpec(
                    View.MeasureSpec.getSize(spec) - startInset - endInset, mode);
        }
        return spec;
    }
}