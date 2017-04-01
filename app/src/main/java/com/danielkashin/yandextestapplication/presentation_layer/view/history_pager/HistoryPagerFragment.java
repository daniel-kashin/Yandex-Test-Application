package com.danielkashin.yandextestapplication.presentation_layer.view.history_pager;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.presentation_layer.view.history_pager.adapter.HistoryPagerAdapter;


public class HistoryPagerFragment extends Fragment {

  private ViewPager mViewPager;
  private TabLayout mTabLayout;


  public static HistoryPagerFragment getInstance() {
    return new HistoryPagerFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_history_pager, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initializeView(view);
  }


  private void initializeView(View view) {
    mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
    mViewPager.setAdapter(
        new HistoryPagerAdapter(
            getChildFragmentManager(),
            getResources().getStringArray(R.array.view_pager_labels)
        )
    );

    mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
    mTabLayout.setupWithViewPager(mViewPager, true);
  }
}
