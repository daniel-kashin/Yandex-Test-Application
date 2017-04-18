package com.danielkashin.yandextestapplication.presentation_layer.view.history_pager;

import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangePublisher;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager.IHistoryPage;


public interface IHistoryPagerView extends IDatabaseChangePublisher {

  void hideDeleteHistoryButton(IHistoryPage source);

  void showDeleteHistoryButton(IHistoryPage source);

}
