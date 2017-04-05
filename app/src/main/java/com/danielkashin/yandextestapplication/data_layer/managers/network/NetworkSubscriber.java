package com.danielkashin.yandextestapplication.data_layer.managers.network;


public abstract class NetworkSubscriber {

  private boolean disposed;
  private IDisposeListener disposeListener;

  // ----------------------------------------------------------------------------------------------

  public final boolean isDisposed() {
    return disposed;
  }

  public void setOnDisposeListener(IDisposeListener listener) {
    disposeListener = listener;
  }

  public final void dispose() {
    if (!disposed){
      disposed = true;

      if (disposeListener != null) {
        disposeListener.onDispose();
      }
    }
  }

  // ----------------------------------------------------------------------------------------------

  public abstract void onResult(NetworkStatus networkStatus);

  // ----------------------------------------------------------------------------------------------

  public interface IDisposeListener {

    void onDispose();

  }

}
