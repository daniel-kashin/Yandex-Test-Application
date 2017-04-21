package com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangeReceiver;
import com.danielkashin.yandextestapplication.presentation_layer.view.history.HistoryFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.danielkashin.yandextestapplication.presentation_layer.view.history.HistoryFragment.State.FragmentType;


public class HistoryPagerAdapter extends FragmentPagerAdapter
    implements IHistoryPagerAdapter, IDatabaseChangeReceiver {

  private int mCurrentFragment = 0;
  private final int FRAGMENT_COUNT = 2;
  private ArrayList<WeakReference<IHistoryPage>> mPages;
  private final String[] mPageTitles;


  public HistoryPagerAdapter(FragmentManager fragmentManager, String[] pageTitles) {
    super(fragmentManager);

    if (pageTitles == null || pageTitles.length != FRAGMENT_COUNT) {
      throw new IllegalStateException("Count of pages should equal " + FRAGMENT_COUNT);
    }

    mPageTitles = pageTitles;
    mPages = new ArrayList<>(FRAGMENT_COUNT);
    for (int i = 0; i < FRAGMENT_COUNT; ++i){
      mPages.add(null);
    }
  }

  // -------------------------------- IDatabaseChangeReceiver -------------------------------------

  @Override
  public void receiveOnDataChanged(IDatabaseChangeReceiver source) {
    if (mPages == null || mPages.size() == 0){
      return;
    }

    for (int i = 0; i < mPages.size(); ++i){
      WeakReference<IHistoryPage> reference = mPages.get(i);
      if (reference != null && reference.get() != source){
        reference.get().receiveOnDataChanged(source);
      }
    }
  }

  // ------------------------------------- IHistoryPagerAdapter ----------------------------------------

  @Override
  public Fragment instantiateItem(ViewGroup container, int position) {
    Fragment createdFragment = (Fragment) super.instantiateItem(container, position);

    if (!(createdFragment instanceof IHistoryPage)) {
      throw new IllegalStateException("Fragment must be an instance of IHistoryPage type");
    } else {
      mPages.set(position, new WeakReference<>((IHistoryPage) createdFragment));
    }

    return createdFragment;
  }


  @Override
  public Fragment getItem(int position) {
    if (position == 0) {
      return HistoryFragment.getInstance(FragmentType.ALL_HISTORY);
    } else if (position == 1) {
      return HistoryFragment.getInstance(FragmentType.ONLY_FAVORITE_HISTORY);
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

  @Override
  public void onPageSelected(int position) {
    if (mPages != null) {
      for (int i = 0; i < mPages.size(); ++i) {
        WeakReference<IHistoryPage> reference = mPages.get(i);
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

  @Override
  public void onDeleteButtonClicked() {
    if (mPages != null) {
      IHistoryPage page = mPages.get(mCurrentFragment).get();
      if (page != null) {
        page.onDeleteButtonClicked();
      }
    }
  }

  @Override
  public boolean equalsCurrent(IHistoryPage page) {
    if (mPages == null || mCurrentFragment >= mPages.size()
        || mPages.get(mCurrentFragment) == null || page == null) {
      return false;
    }

    return mPages.get(mCurrentFragment).get() == page;
  }
}
