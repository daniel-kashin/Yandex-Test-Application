package com.danielkashin.yandextestapplication.presentation_layer.adapter.base;

/*
* publishOnDataChanged is triggered when child fragment/activity has changed database and
* want to notify its parent fragment/activity about that
*
* for much clearness, both child and parent implement this interface and the chain starts
* executing when some method decides that database was changed. the chain starts in some
* fragment/activity, for example, when translation favorite toggle is clicked
* and finishes in the activity/fragment, that then triggers IDatabaseChangeReceiver to
* notify all its child activities/fragments
*
* alone argument "source" is the very first publisher in the chain, if we want to ignore
* modifying it when moving in the reverse direction. otherwise, just leave it as null
*/
public interface IDatabaseChangePublisher {

  void publishOnDataChanged(IDatabaseChangePublisher source);

}
