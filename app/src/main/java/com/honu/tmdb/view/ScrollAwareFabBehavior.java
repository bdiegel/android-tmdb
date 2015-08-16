package com.honu.tmdb.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Provides behavior for FAB to show/hide on scroll of dependent view.
 *
 * To use, place following attribute in layout on FAB:
 *
 *     app:layout_behavior="com.honu.tmdb.view.ScrollAwareFabBehavior"
 *
 *  https://github.com/ianhanniballake
 */
public class ScrollAwareFabBehavior extends FloatingActionButton.Behavior {

    public ScrollAwareFabBehavior(Context context, AttributeSet attrs) {
        super();
    }

    /**
     *
     * Ensures we react to vertical scrolling
     */
    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout,
                                       final FloatingActionButton child,
                                       final View directTargetChild,
                                       final View target,
                                       final int nestedScrollAxes) {

        return (nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL) || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    /**
     * Overrides the default scroll reaction to show/hide the FAB:
     *
     * 1. hide the FAB if user scrolled down and FAB is currently visible
     * 2. show the FAB if user scrolled up and the FAB is currently not visible
     */
    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout,
                               final FloatingActionButton child,
                               final View target,
                               final int dxConsumed,
                               final int dyConsumed,
                               final int dxUnconsumed,
                               final int dyUnconsumed) {

        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.hide();
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.show();
        }
    }
}
