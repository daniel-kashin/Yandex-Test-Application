package com.danielkashin.yandextestapplication.presentation_layer.adapter.main_pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangeReceiver;
import com.danielkashin.yandextestapplication.presentation_layer.view.history_pager.HistoryPagerFragment;
import com.danielkashin.yandextestapplication.presentation_layer.view.translate.TranslateFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class MainPagerAdapter extends FragmentPagerAdapter implements IMainPagerAdapter {

  private final int FRAGMENT_COUNT = 2;
  private ArrayList<WeakReference<IDatabaseChangeReceiver>> mReceivers;


  public MainPagerAdapter(FragmentManager fragmentManager) {
    super(fragmentManager);

    mReceivers = new ArrayList<>(FRAGMENT_COUNT);
    for (int i = 0; i < FRAGMENT_COUNT; ++i){
      mReceivers.add(null);
    }
  }

  // ---------------------------------- FragmentPagerAdapter --------------------------------------

  @Override
  public Fragment instantiateItem(ViewGroup container, int position) {
    Fragment createdFragment = (Fragment) super.instantiateItem(container, position);

    if (!(createdFragment instanceof IDatabaseChangeReceiver)) {
      throw new IllegalStateException("Fragment must be an instance of IDatabaseChangeReceiver type");
    } else {
      mReceivers.set(position, new WeakReference<>((IDatabaseChangeReceiver) createdFragment));
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

  // --------------------------------- IDatabaseChangeReceiver ------------------------------------

  @Override
  public void receiveOnDataChanged(IDatabaseChangeReceiver source) {
    if (mReceivers == null || mReceivers.size() == 0){
      return;
    }

    for (int i = 0; i < mReceivers.size(); ++i){
      WeakReference<IDatabaseChangeReceiver> reference = mReceivers.get(i);
      if (reference != null && reference.get() != source){
        reference.get().receiveOnDataChanged(null);
      }
    }
  }

}
