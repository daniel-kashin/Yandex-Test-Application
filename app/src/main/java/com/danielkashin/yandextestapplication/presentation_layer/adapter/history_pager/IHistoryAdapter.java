package com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager;


public interface IHistoryAdapter {

  void onPageSelected(int position);

  void onDeleteButtonClicked();

  void onDataChanged(IHistoryPage source);

  void onDataChanged();

  boolean equalsCurrent(IHistoryPage page);

}
