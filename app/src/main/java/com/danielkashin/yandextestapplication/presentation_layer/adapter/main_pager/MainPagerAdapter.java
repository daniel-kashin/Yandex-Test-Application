package com.danielkashin.yandextestapplication.presentation_layer.adapter.main_pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangeReceiver;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager.IHistoryPage;
import com.danielkashin.yandextestapplication.presentation_layer.view.history_pager.HistoryPagerFragment;
import com.danielkashin.yandextestapplication.presentation_layer.view.translate.TranslateFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class MainPagerAdapter extends FragmentPagerAdapter implements IMainPagerAdapter {

  private final int FRAGMENT_COUNT = 2;
  private int mCurrentFragment;
  private ArrayList<WeakReference<IMainPage>> mPages;


  public MainPagerAdapter(FragmentManager fragmentManager) {
    super(fragmentManager);

    mPages = new ArrayList<>(FRAGMENT_COUNT);
    for (int i = 0; i < FRAGMENT_COUNT; ++i) {
      mPages.add(null);
    }
  }

  // ---------------------------------- FragmentPagerAdapter --------------------------------------

  @Override
  public Fragment instantiateItem(ViewGroup container, int position) {
    Fragment createdFragment = (Fragment) super.instantiateItem(container, position);

    if (!(createdFragment instanceof IMainPage)) {
      throw new IllegalStateException("Fragment must be an instance of IMainPage type");
    } else {
      mPages.set(position, new WeakReference<>((IMainPage) createdFragment));
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

  // ------------------------------------ IMainPagerAdapter ---------------------------------------

  @Override
  public void onPageSelected(int position) {
    if (mPages != null) {
      for (int i = 0; i < mPages.size(); ++i) {
        WeakReference<IMainPage> reference = mPages.get(i);
        if (reference != null && reference.get() != null) {
          if (i == position) {
            mCurrentFragment = position;
            reference.get().onSelected();
          } else {
            reference.get().onAnotherPageSelected();
          }
        }
      }
    }
  }

  // --------------------------------- IDatabaseChangeReceiver ------------------------------------

  @Override
  public void receiveOnDataChanged(IDatabaseChangeReceiver source) {
    if (mPages == null || mPages.size() == 0) {
      return;
    }

    for (int i = 0; i < mPages.size(); ++i) {
      WeakReference<IMainPage> reference = mPages.get(i);
      if (reference != null && reference.get() != source) {
        if (mCurrentFragment != i) {
            reference.get().receiveOnDataChanged(null);
        } else {
          reference.get().receiveOnDataChanged(source);
        }
      }
    }
  }

}
