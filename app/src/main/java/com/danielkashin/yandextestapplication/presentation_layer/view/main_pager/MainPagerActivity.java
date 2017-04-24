package com.danielkashin.yandextestapplication.presentation_layer.view.main_pager;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangePublisher;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangeReceiver;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.main_pager.IMainPagerAdapter;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.main_pager.MainPagerAdapter;


public class MainPagerActivity extends AppCompatActivity
    implements IMainPagerView, IDatabaseChangePublisher {

  private ViewPager mViewPager;
  private TabLayout mTabLayout;


  // --------------------------------------- lifecycle --------------------------------------------

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_tab);
    initializeView();
  }

  @Override
  public void onBackPressed() {
    IMainPagerAdapter adapter = (IMainPagerAdapter)mViewPager.getAdapter();

    if (adapter == null || adapter.translationHolderIsCurrent()) {
      super.onBackPressed();
    } else {
      mViewPager.setCurrentItem(adapter.getTranslateHolderPosition(), true);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  // -------------------------------- IDatabaseChangePublisher ------------------------------------

  @Override
  public void publishOnDataChanged(IDatabaseChangePublisher source) {
    if (source instanceof IDatabaseChangeReceiver) {
      ((IDatabaseChangeReceiver) mViewPager.getAdapter()).receiveOnDataChanged((IDatabaseChangeReceiver) source);
    } else {
      ((IDatabaseChangeReceiver) mViewPager.getAdapter()).receiveOnDataChanged(null);
    }
  }

  // --------------------------------------- lifecycle --------------------------------------------


  private void initializeView() {
    mViewPager = (ViewPager) findViewById(R.id.view_pager);
    mTabLayout = (TabLayout) findViewById(R.id.tab_layout);

    mViewPager.setOffscreenPageLimit(3);
    mViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
    mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        ((IMainPagerAdapter) mViewPager.getAdapter()).onPageSelected(position);
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    mTabLayout.setupWithViewPager(mViewPager);

    int[] tabIcons = {R.drawable.selector_languages, R.drawable.selector_history, R.drawable.selector_info};
    if (mTabLayout.getTabCount() != tabIcons.length) {
      throw new IllegalStateException("Tab icons size and tab layout size must be equal");
    }

    for (int i = 0; i < mTabLayout.getTabCount(); ++i) {
      TabLayout.Tab tab = mTabLayout.getTabAt(i);
      if (tab != null) {
        tab.setIcon(tabIcons[i]);
        tab.setCustomView(R.layout.item_tab_main);
      }
    }
  }

  // ------------------------------------- IMainPagerView -------------------------------------------

  @Override
  public void openTranslatePage(Translation translation) {
    IMainPagerAdapter adapter = (IMainPagerAdapter)mViewPager.getAdapter();

    mViewPager.setCurrentItem(adapter.getTranslateHolderPosition(), true);
    adapter.setTranslationData(translation);
  }
}
