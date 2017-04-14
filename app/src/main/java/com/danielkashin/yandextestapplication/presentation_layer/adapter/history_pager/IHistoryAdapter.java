package com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager;


public interface IHistoryAdapter {

  void onPageSelected(int position);

  void onDeleteButtonClicked();

  boolean equalsCurrent(IHistoryPage page);

}
