package com.danielkashin.yandextestapplication.presentation_layer.view.main_tab;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangeReceiver;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.main_pager.MainPagerAdapter;


public class MainTabActivity extends AppCompatActivity implements IDatabaseChangeReceiver {

  private final static String SELECTED_MENU_ITEM_ID = "selected_tab_id";

  private ViewPager mViewPager;
  private BottomNavigationView mBottomNavigationView;


  // --------------------------------------- lifecycle --------------------------------------------

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tab);
    initializeView();

    if (savedInstanceState != null) {
      setSelectedMenuItem(savedInstanceState.getInt(SELECTED_MENU_ITEM_ID));
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putInt(SELECTED_MENU_ITEM_ID, getSelectedMenuItem());
  }

  // -------------------------------- IDatabaseChangeReceiver -------------------------------------

  @Override
  public void receiveOnDataChanged(IDatabaseChangeReceiver source) {
    ((IDatabaseChangeReceiver)mViewPager.getAdapter()).receiveOnDataChanged(source);
  }

  // --------------------------------------- lifecycle --------------------------------------------


  private void initializeView() {
    mViewPager = (ViewPager) findViewById(R.id.view_pager);
    mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

    mViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));

    mBottomNavigationView.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
              case R.id.menu_search:
                mViewPager.setCurrentItem(0, true);
                return true;
              case R.id.menu_history:
                mViewPager.setCurrentItem(1, true);
                return true;
              case R.id.menu_settings:
                return false;
              default:
                return false;
            }
          }
        });
  }

  private int getSelectedMenuItem() {
    Menu menu = mBottomNavigationView.getMenu();
    for (int i = 0; i < menu.size(); ++i) {
      if (menu.getItem(i).isChecked()) {
        return i;
      }
    }

    return 1; // return default value
  }

  private void setSelectedMenuItem(int position) {
    Menu menu = mBottomNavigationView.getMenu();

    if (position > menu.size()) position = 1; // set default value

    menu.getItem(position).setChecked(true);
  }

}
