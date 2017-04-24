package com.danielkashin.yandextestapplication.presentation_layer.adapter.base;

/*
* fragment/activity that want to be notified about its selection/deselection
* from the holder fragment/activity
*/
public interface ISelectedListener {

  void onAnotherPageSelected();

  void onSelected();

}
