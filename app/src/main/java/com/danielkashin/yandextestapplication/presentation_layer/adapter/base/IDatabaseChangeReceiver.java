package com.danielkashin.yandextestapplication.presentation_layer.adapter.base;


public interface IDatabaseChangeReceiver {

  void receiveOnDataChanged(IDatabaseChangeReceiver source);

}
