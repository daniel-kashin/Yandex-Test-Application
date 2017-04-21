package com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager;

import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangeReceiver;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.ISelectedListener;


public interface IHistoryPage extends IDatabaseChangeReceiver, ISelectedListener {

  void onDeleteButtonClicked();

}
