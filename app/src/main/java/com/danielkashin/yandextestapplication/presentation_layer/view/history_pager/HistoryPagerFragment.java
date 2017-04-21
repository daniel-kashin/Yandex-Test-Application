package com.danielkashin.yandextestapplication.presentation_layer.view.history_pager;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangePublisher;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager.HistoryPagerAdapter;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager.IHistoryPagerAdapter;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager.IHistoryPage;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangeReceiver;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.main_pager.IMainPage;


public class HistoryPagerFragment extends Fragment
    implements IHistoryPagerView, IMainPage, IDatabaseChangePublisher {

  private static final String KEY_CLEAR_HISTORY_IMAGE_VISIBLE = "KEY_CLEAR_HISTORY_IMAGE_VISIBLE";

  private ViewPager mViewPager;
  private TabLayout mTabLayout;
  private ImageView mClearHistoryImage;
  private View.OnClickListener mClearHistoryImageClickListener;


  public static HistoryPagerFragment getInstance() {
    return new HistoryPagerFragment();
  }

  // ---------------------------------- lifecycle -------------------------------------------------

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (!(getActivity() instanceof IDatabaseChangePublisher)) {
      throw new IllegalStateException("Parent activity must implement IHistoryPagerView IDatabaseChangePublisher");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_history_pager, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initializeView(view, savedInstanceState);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putBoolean(KEY_CLEAR_HISTORY_IMAGE_VISIBLE, mClearHistoryImage.getVisibility() == View.VISIBLE);
  }

  // ------------------------------ IDatabaseChangeReceiver ---------------------------------------

  @Override
  public void receiveOnDataChanged(IDatabaseChangeReceiver source) {
    ((IDatabaseChangeReceiver) mViewPager.getAdapter()).receiveOnDataChanged(source);
  }

  // ------------------------------ IDatabaseChangeReceiver ---------------------------------------

  @Override
  public void publishOnDataChanged(IDatabaseChangePublisher source) {
    ((IDatabaseChangePublisher) getActivity()).publishOnDataChanged(source);
  }

  // ------------------------------------- IMainPage ----------------------------------------------

  @Override
  public void onAnotherPageSelected() {
    ((IHistoryPagerAdapter)mViewPager.getAdapter()).onPageSelected(-1);
  }

  @Override
  public void onSelected() {
    // TODO
  }

  // -------------------------------- IHistoryPagerView -------------------------------------------

  @Override
  public void hideDeleteHistoryButton(IHistoryPage source) {
    if (((IHistoryPagerAdapter) mViewPager.getAdapter()).equalsCurrent(source)) {
      mClearHistoryImage.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  public void showDeleteHistoryButton(IHistoryPage source) {
    if (((IHistoryPagerAdapter) mViewPager.getAdapter()).equalsCurrent(source)) {
      mClearHistoryImage.setOnClickListener(mClearHistoryImageClickListener);
      mClearHistoryImage.setVisibility(View.VISIBLE);
    }
  }

  // ---------------------------------- private methods -------------------------------------------

  private void initializeView(View view, Bundle savedInstanceState) {
    mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
    mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
    mClearHistoryImage = (ImageView) view.findViewById(R.id.button_clear_history);
    if (savedInstanceState != null && savedInstanceState.containsKey(KEY_CLEAR_HISTORY_IMAGE_VISIBLE)) {
      mClearHistoryImage.setVisibility(savedInstanceState.getBoolean(KEY_CLEAR_HISTORY_IMAGE_VISIBLE)
          ? View.VISIBLE
          : View.INVISIBLE);
    }

    mViewPager.setAdapter(new HistoryPagerAdapter(getChildFragmentManager(),
        getResources().getStringArray(R.array.view_pager_labels)));

    mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        ((IHistoryPagerAdapter) mViewPager.getAdapter()).onPageSelected(position);
      }

      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      }

      @Override
      public void onPageScrollStateChanged(int state) {
      }
    });

    mTabLayout.setupWithViewPager(mViewPager, true);

    mClearHistoryImageClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ((IHistoryPagerAdapter) mViewPager.getAdapter()).onDeleteButtonClicked();
      }
    };

    mClearHistoryImage.setOnClickListener(mClearHistoryImageClickListener);
  }
}
