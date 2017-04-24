package com.danielkashin.yandextestapplication.data_layer.managers.network;

/*
 * manager for easy subscribing to network state changes
 */
public interface INetworkManager {

  NetworkStatus getCurrentNetworkStatus();

  void registerSubscriber(NetworkSubscriber subscriber);

}
