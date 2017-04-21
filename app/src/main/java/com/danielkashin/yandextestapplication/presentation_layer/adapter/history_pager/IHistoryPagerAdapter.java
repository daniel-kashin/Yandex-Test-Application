package com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager;

import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangePublisher;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangeReceiver;


public interface IHistoryPagerAdapter extends IDatabaseChangeReceiver {

  void onPageSelected(int position);

  void onDeleteButtonClicked();

  boolean equalsCurrent(IHistoryPage page);

}
