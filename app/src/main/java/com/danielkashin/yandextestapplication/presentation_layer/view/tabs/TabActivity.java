package com.danielkashin.yandextestapplication.presentation_layer.view.tabs;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.presentation_layer.view.history_pager.HistoryPagerFragment;
import com.danielkashin.yandextestapplication.presentation_layer.view.translate.TranslateFragment;


public class TabActivity extends AppCompatActivity {

  private final static String SELECTED_MENU_ITEM_ID = "selected_tab_id";

  private BottomNavigationView mBottomNavigationView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tab);
    initializeView();

    if (savedInstanceState != null) {
      setSelectedMenuItem(savedInstanceState.getInt(SELECTED_MENU_ITEM_ID));
    }

    if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
      tryToAttachFragment(TranslateFragment.class.getSimpleName());
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putInt(SELECTED_MENU_ITEM_ID, getSelectedMenuItem());
  }

  private void initializeView() {
    mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

    mBottomNavigationView.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
              case R.id.menu_search:
                return tryToAttachFragment(TranslateFragment.class.getSimpleName());
              case R.id.menu_history:
                return tryToAttachFragment(HistoryPagerFragment.class.getSimpleName());
              case R.id.menu_settings:
                return false;
              default:
                return false;
            }
          }
        });
  }

  private boolean tryToAttachFragment(String newFragmentName) {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    if (topFragment != null) {
      if (topFragment.getClass().getSimpleName().equals(newFragmentName)) {
        // top fragment is the same class with the fragment we want to add
        transaction.commit();
        return false;
      } else {
        // top fragment is found but has the different class from the fragment we want to add

        // detach top fragment so we can restore its state later
        transaction.detach(topFragment);

        // try to attach existing fragment or create new in case of failure
        Fragment savedState = getSupportFragmentManager().findFragmentByTag(newFragmentName);
        if (savedState != null) {
          transaction.attach(savedState);
        } else {
          transaction.add(
              R.id.fragment_container,
              getFragmentByFragmentName(newFragmentName),
              newFragmentName
          );
        }

        // commit changes
        transaction.commit();
        getSupportFragmentManager().executePendingTransactions();
        return true;
      }
    } else {
      // fragmentManager is empty -- just add fragment to the top
      Fragment newFragment = getFragmentByFragmentName(newFragmentName);
      transaction.add(R.id.fragment_container, newFragment, newFragmentName)
          .commit();

      getSupportFragmentManager().executePendingTransactions();
      return true;
    }
  }

  private Fragment getFragmentByFragmentName(String fragmentName) {
    String translate = TranslateFragment.class.getSimpleName();
    String historyPager = HistoryPagerFragment.class.getSimpleName();

    if (fragmentName.equals(translate)) {
      return TranslateFragment.getInstance();
    } else if (fragmentName.equals(historyPager)) {
      return HistoryPagerFragment.getInstance();
    } else {
      return null;
    }
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
