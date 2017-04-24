package com.danielkashin.yandextestapplication.presentation_layer.adapter.base;

/*
* receiveOnDataChanged is triggered when parent activity/fragment want to notify its
* child activity/fragment that data in database has changer
*
* alone argument "source" is receiver we will ignore when notifying
*
* chain starts in some fragment/activity and finishes with some handling of the database change,
* for example, refreshing recycler view
*
*/
public interface IDatabaseChangeReceiver {

  void receiveOnDataChanged(IDatabaseChangeReceiver source);

}
