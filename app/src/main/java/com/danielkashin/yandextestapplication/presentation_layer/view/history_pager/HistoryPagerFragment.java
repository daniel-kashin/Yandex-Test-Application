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
import com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager.HistoryPagerAdapter;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager.IHistoryAdapter;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager.IHistoryPage;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.main_pager.IDatabaseChangeReceiver;


public class HistoryPagerFragment extends Fragment implements IHistoryPagerView, IDatabaseChangeReceiver {

  private static final String KEY_CLEAR_HISTORY_IMAGE_VISIBLE = "KEY_CLEAR_HISTORY_IMAGE_VISIBLE";

  private ViewPager mViewPager;
  private TabLayout mTabLayout;
  private ImageView mClearHistoryImage;
  private View.OnClickListener mClearHistoryImageClickListener;


  public static HistoryPagerFragment getInstance() {
    HistoryPagerFragment fragment = new HistoryPagerFragment();

    return fragment;
  }

  // ---------------------------------- lifecycle -------------------------------------------------

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
  public void onSaveInstanceState(Bundle outState){
    outState.putBoolean(KEY_CLEAR_HISTORY_IMAGE_VISIBLE, mClearHistoryImage.getVisibility() == View.VISIBLE);
  }

  // -------------------------------- IHistoryPagerView -------------------------------------------

  @Override
  public void hideDeleteHistoryButton(IHistoryPage source) {
    if (((IHistoryAdapter) mViewPager.getAdapter()).equalsCurrent(source)) {
      mClearHistoryImage.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  public void showDeleteHistoryButton(IHistoryPage source) {
    if (((IHistoryAdapter) mViewPager.getAdapter()).equalsCurrent(source)) {
      mClearHistoryImage.setOnClickListener(mClearHistoryImageClickListener);
      mClearHistoryImage.setVisibility(View.VISIBLE);
    }
  }

  // ------------------------------ IDatabaseChangeReceiver ---------------------------------------

  @Override
  public void onDataChanged(IHistoryPage source) {
    ((IHistoryAdapter)mViewPager.getAdapter()).onDataChanged(source);
  }

  @Override
  public void onDataChanged() {
    ((IHistoryAdapter)mViewPager.getAdapter()).onDataChanged();
  }

  // ---------------------------------- private methods -------------------------------------------

  private void initializeView(View view, Bundle savedInstanceState) {
    mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
    mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
    mClearHistoryImage = (ImageView) view.findViewById(R.id.button_clear_history);
    if (savedInstanceState != null && savedInstanceState.containsKey(KEY_CLEAR_HISTORY_IMAGE_VISIBLE)){
      mClearHistoryImage.setVisibility(savedInstanceState.getBoolean(KEY_CLEAR_HISTORY_IMAGE_VISIBLE)
          ? View.VISIBLE
          : View.INVISIBLE);
    }

    mViewPager.setAdapter(new HistoryPagerAdapter(getChildFragmentManager(),
        getResources().getStringArray(R.array.view_pager_labels)));

    mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        ((IHistoryAdapter) mViewPager.getAdapter()).onPageSelected(position);
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
        ((IHistoryAdapter) mViewPager.getAdapter()).onDeleteButtonClicked();
      }
    };

    mClearHistoryImage.setOnClickListener(mClearHistoryImageClickListener);
  }
}
