package com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.danielkashin.yandextestapplication.presentation_layer.view.history_all.HistoryAllFragment;
import com.danielkashin.yandextestapplication.presentation_layer.view.history_favourite.HistoryFavouriteFragment;
import com.danielkashin.yandextestapplication.presentation_layer.view.translate.TranslateFragment;


public class HistoryPagerAdapter extends FragmentPagerAdapter {

  private final int FRAGMENT_COUNT = 2;
  private final String[] mPageTitles;


  public HistoryPagerAdapter(FragmentManager fragmentManager, String[] pageTitles){
    super(fragmentManager);

    if (pageTitles == null || pageTitles.length != FRAGMENT_COUNT) {
      throw new NullPointerException("Count of pages should equal " + FRAGMENT_COUNT);
    }

    mPageTitles = pageTitles;
  }


  @Override
  public Fragment getItem(int position) {
    if (position == 0) {
      return HistoryAllFragment.getInstance();
    } else if (position == 1) {
      return HistoryFavouriteFragment.getInstance();
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
