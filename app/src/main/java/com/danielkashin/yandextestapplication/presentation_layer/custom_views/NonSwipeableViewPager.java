package com.danielkashin.yandextestapplication.presentation_layer.custom_views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;
import java.lang.reflect.Field;


public class NonSwipeableViewPager extends ViewPager {

  public NonSwipeableViewPager(Context context) {
    super(context);
    setMyScroller();
  }

  public NonSwipeableViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
    setMyScroller();
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    // disable swipes
    return false;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    // disable swipes
    return false;
  }

  private void setMyScroller() {
    try {
      Class<?> viewPagerClass = ViewPager.class;
      Field scroller = viewPagerClass.getDeclaredField("mScroller");
      scroller.setAccessible(true);
      scroller.set(this, new MyScroller(getContext()));
    } catch (Exception e) {
      // do nothing
    }
  }

  private class MyScroller extends Scroller {
    private MyScroller(Context context) {
      super(context, new DecelerateInterpolator());
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
      super.startScroll(startX, startY, dx, dy, 250);
    }
  }
}
