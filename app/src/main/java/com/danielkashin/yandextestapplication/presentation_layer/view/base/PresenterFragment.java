package com.danielkashin.yandextestapplication.presentation_layer.view.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.PresenterLoader;


/*
 * basic fragment that holds reference to presenter
 */
public abstract class PresenterFragment<P extends Presenter<V>, V extends IView>
    extends Fragment implements IView, LoaderManager.LoaderCallbacks<P> {

  private P mPresenter;


  protected final Presenter<V> getPresenter() {
    return this.mPresenter;
  }

  // -------------------------------------- lifecycle ---------------------------------------------

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    super.onCreate(savedInstanceState);

    // loader is created -> get presenter
    Loader loader = getLoaderManager().getLoader(getFragmentId());
    if (loader != null) {
      mPresenter = ((PresenterLoader<P, V>) loader).getPresenter();
    }

    // init loader
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

  // ------------------------------ LoaderManager.LoaderCallbacks ---------------------------------

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

  // --------------------------------------- abstract ---------------------------------------------

  protected abstract V getViewInterface();

  protected abstract IPresenterFactory<P, V> getPresenterFactory();

  protected abstract int getFragmentId();

  protected abstract int getLayoutRes();

  protected abstract void initializeView(View view);

}
