package com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.danielkashin.yandextestapplication.presentation_layer.view.history.HistoryFragment;


public class HistoryPagerAdapter extends FragmentPagerAdapter {

  private final int FRAGMENT_COUNT = 2;
  private final String[] mPageTitles;


  public HistoryPagerAdapter(FragmentManager fragmentManager, String[] pageTitles){
    super(fragmentManager);

    if (pageTitles == null || pageTitles.length != FRAGMENT_COUNT) {
      throw new IllegalStateException("Count of pages should equal " + FRAGMENT_COUNT);
    }

    mPageTitles = pageTitles;
  }


  @Override
  public Fragment getItem(int position) {
    if (position == 0) {
      return HistoryFragment.getInstance(false); // onlyFavorite set to false
    } else if (position == 1) {
      return HistoryFragment.getInstance(true); // onlyFavorite set to true
    } else {
      return null;
    }
  }

  @Override
  public int getCount() {
    return FRAGMENT_COUNT;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return mPageTitles[position];
  }
}
