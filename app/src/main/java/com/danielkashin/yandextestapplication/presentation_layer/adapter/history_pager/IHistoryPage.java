package com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager;


public interface IHistoryPage {

  void onDataChanged();

  void onUnselected();

  void onSelected();

  void onDeleteButtonClicked();

}
