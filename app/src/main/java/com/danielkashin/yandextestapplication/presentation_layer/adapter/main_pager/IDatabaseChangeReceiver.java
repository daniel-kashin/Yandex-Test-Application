package com.danielkashin.yandextestapplication.presentation_layer.adapter.main_pager;

import com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager.IHistoryPage;


public interface IDatabaseChangeReceiver {

  void onDataChanged(IHistoryPage source);

  void onDataChanged();

}
