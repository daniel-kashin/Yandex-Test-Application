package com.danielkashin.yandextestapplication.presentation_layer.view.main_tab;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangePublisher;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangeReceiver;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.main_pager.IMainPagerAdapter;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.main_pager.MainPagerAdapter;

import java.util.ArrayList;


public class MainTabActivity extends AppCompatActivity
    implements IDatabaseChangePublisher {

  private ViewPager mViewPager;
  private TabLayout mTabLayout;


  // --------------------------------------- lifecycle --------------------------------------------

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tab);
    initializeView();
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

    int[] tabIcons = {R.drawable.ic_compare_arrows, R.drawable.ic_compare_arrows};
    if (mTabLayout.getTabCount() != tabIcons.length) {
      throw new IllegalStateException("Tab icons size and tab layout size must be equal");
    }

    for (int i = 0; i < mTabLayout.getTabCount(); ++i) {
      TabLayout.Tab tab = mTabLayout.getTabAt(i);
      mTabLayout.getTabAt(i).setIcon(tabIcons[i]);
    }
  }
}
