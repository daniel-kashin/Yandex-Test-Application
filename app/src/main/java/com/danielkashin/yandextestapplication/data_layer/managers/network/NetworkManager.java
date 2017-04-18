package com.danielkashin.yandextestapplication.data_layer.managers.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkManager implements INetworkManager {

  private final Context context;
  private final IntentFilter filter;


  private NetworkManager(Context context) {
    this.context = context.getApplicationContext();
    this.filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
  }

  // ------------------------------ INetworkManager methods ---------------------------------------

  @Override
  public void registerSubscriber(final NetworkSubscriber networkSubscriber) {
    final BroadcastReceiver receiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
          if (!networkSubscriber.isDisposed()) {
            networkSubscriber.onResult(getStatus(context));
          }
      }
    };

    networkSubscriber.setOnDisposeListener(new NetworkSubscriber.IDisposeListener() {
      @Override
      public void onDispose() {
        try {
          context.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
          // in case receiver was already unregistered
        }
      }
    });

    context.registerReceiver(receiver, filter);
  }

  @Override
  public NetworkStatus getCurrentNetworkStatus() {
    return getStatus(context);
  }

  // ---------------------------------- private methods --------------------------------------------

  private NetworkStatus getStatus(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager)context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    if(connectivityManager != null) {
      NetworkInfo info = connectivityManager.getActiveNetworkInfo();
      if(info != null) {
        if(info.getType() == ConnectivityManager.TYPE_WIFI) {
          return NetworkStatus.CONNECTED;
        } else if(info.getType() == ConnectivityManager.TYPE_MOBILE) {
          return NetworkStatus.CONNECTED;
        } else {
          return NetworkStatus.DISCONNECTED;
        }
      } else {
        return NetworkStatus.DISCONNECTED;
      }
    } else {
      return NetworkStatus.UNKNOWN;
    }
  }

  // ------------------------------------- factory ------------------------------------------------

  public static class Factory {

    private Factory() {}

    public static INetworkManager create(Context context){
      return new NetworkManager(context);
    }

  }
}
