package com.danielkashin.yandextestapplication.presentation_layer.view.tabs;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.presentation_layer.view.translate.TranslateFragment;


public class TabActivity extends AppCompatActivity {

  BottomNavigationView mBottomNavigationView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tab);

    initializeView();

    if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
      tryToAttachFragment(TranslateFragment.class.getSimpleName());
    }
  }

  private void initializeView(){
    mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

    mBottomNavigationView.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
          case R.id.menu_search:
            return tryToAttachFragment(TranslateFragment.class.getSimpleName());
          case R.id.menu_history:
            return false;
          case R.id.menu_settings:
            return false;
          default:
            return false;
        }
      }
    });
  }

  private boolean tryToAttachFragment(String newFragmentName){
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    if (topFragment != null){
      if (topFragment.getClass().getSimpleName().equals(newFragmentName)){
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

  private Fragment getFragmentByFragmentName(String fragmentName){
    String translate = TranslateFragment.class.getSimpleName();

    if (fragmentName.equals(translate)){
      return TranslateFragment.getInstance();
    } else {
      return null;
    }
  }



}
