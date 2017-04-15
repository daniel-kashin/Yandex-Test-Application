package com.danielkashin.yandextestapplication.presentation_layer.adapter.main_pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import com.danielkashin.yandextestapplication.presentation_layer.view.history_pager.HistoryPagerFragment;
import com.danielkashin.yandextestapplication.presentation_layer.view.translate.TranslateFragment;


public class MainPagerAdapter extends FragmentPagerAdapter implements IMainPagerAdapter {

  private int mCurrentFragment = 0;
  private final int FRAGMENT_COUNT = 2;
  private ITranslationKeeper mTranslationKeeper;
  private IDatabaseChangeReceiver mDatabaseChangeReceiver;


  public MainPagerAdapter(FragmentManager fragmentManager) {
    super(fragmentManager);
  }


  @Override
  public Fragment instantiateItem(ViewGroup container, int position) {
    Fragment createdFragment = (Fragment) super.instantiateItem(container, position);

    if (position == 0) {
      if (!(createdFragment instanceof ITranslationKeeper)) {
        throw new IllegalStateException("First fragment must be an instance of ITranslationKeeper");
      } else {
        mTranslationKeeper = (ITranslationKeeper) createdFragment;
      }
    } else if (position == 1) {
      if (!(createdFragment instanceof IDatabaseChangeReceiver)) {
        throw new IllegalStateException("Second fragment must be an instance of IDatabaseChangeReceiver");
      } else {
        mDatabaseChangeReceiver = (IDatabaseChangeReceiver) createdFragment;
      }
    }

    return createdFragment;
  }


  @Override
  public Fragment getItem(int position) {
    if (position == 0) {
      return TranslateFragment.getInstance();
    } else if (position == 1) {
      return HistoryPagerFragment.getInstance();
    } else {
      return null;
    }
  }

  @Override
  public int getCount() {
    return FRAGMENT_COUNT;
  }

}
