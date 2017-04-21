package com.danielkashin.yandextestapplication.presentation_layer.adapter.main_pager;


import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangeReceiver;

public interface IMainPagerAdapter extends IDatabaseChangeReceiver {

  void onPageSelected(int position);

  void setTranslationData(Translation translation);

  int getTranslateHolderPosition();

  boolean translationHolderIsCurrent();

}
