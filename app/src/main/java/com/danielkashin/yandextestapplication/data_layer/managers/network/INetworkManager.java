package com.danielkashin.yandextestapplication.data_layer.managers.network;


public interface INetworkManager {

  NetworkStatus getCurrentNetworkStatus();

  void registerSubscriber(NetworkSubscriber subscriber);

}
