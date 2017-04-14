package com.danielkashin.yandextestapplication.presentation_layer.view.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.PresenterLoader;


public abstract class PresenterFragment<P extends Presenter<V>, V extends IView>
    extends Fragment implements IView, LoaderManager.LoaderCallbacks<P> {

  private P mPresenter;

  // ------------------------ provide presenter to extended classes -------------------------------

  protected final P getPresenter() {
    return this.mPresenter;
  }

  // --------------------------------- Lifecycle methods ------------------------------------------

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Loader loader = getLoaderManager().getLoader(getFragmentId());
    if (loader != null) {
      int i = loader.hashCode();
      mPresenter = ((PresenterLoader<P, V>) loader).getPresenter();
    }

    if (mPresenter == null) {
      getLoaderManager().initLoader(getFragmentId(), null, this);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    super.onCreateView(inflater, parent, savedInstanceState);
    return inflater.inflate(getLayoutRes(), parent, false);
  }

  @Override
  public final void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initializeView(view);
  }

  @Override
  public void onStart() {
    super.onStart();
    mPresenter.attachView(getViewInterface());
  }

  @Override
  public void onStop() {
    mPresenter.detachView();
    super.onStop();
  }

  // -------------------------- LoaderManager.LoaderCallbacks methods ------------------------------

  @Override
  public Loader<P> onCreateLoader(int i, Bundle bundle) {
    return new PresenterLoader<>(getContext(), getPresenterFactory());
  }

  @Override
  public void onLoadFinished(Loader<P> loader, P presenter) {
    this.mPresenter = presenter;
  }

  @Override
  public void onLoaderReset(Loader<P> loader) {
    this.mPresenter = null;
  }

  // ----------------------------------- Abstract methods -----------------------------------------

  protected abstract V getViewInterface();

  protected abstract IPresenterFactory<P, V> getPresenterFactory();

  protected abstract int getFragmentId();

  protected abstract int getLayoutRes();

  protected abstract void initializeView(View view);
}
