package com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager;

import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangeReceiver;


public interface IHistoryPage extends IDatabaseChangeReceiver {

  void onAnotherPageSelected();

  void onSelected();

  void onDeleteButtonClicked();

}
