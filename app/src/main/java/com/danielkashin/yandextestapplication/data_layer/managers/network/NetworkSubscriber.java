package com.danielkashin.yandextestapplication.data_layer.managers.network;


public abstract class NetworkSubscriber {

  private boolean disposed;
  private IDisposeListener disposeListener;

  // ---------------------------------------- public ----------------------------------------------

  public final boolean isDisposed() {
    return disposed;
  }

  public void setOnDisposeListener(IDisposeListener listener) {
    disposeListener = listener;
  }

  public final void dispose() {
    if (!disposed) {
      disposed = true;

      if (disposeListener != null) {
        disposeListener.onDispose();
      }
    }
  }

  // --------------------------------------- abstract ---------------------------------------------

  public abstract void onResult(NetworkStatus networkStatus);

  // -------------------------------------- inner types -------------------------------------------

  public interface IDisposeListener {

    void onDispose();

  }
}
