package com.danielkashin.yandextestapplication.presentation_layer.adapter.base;

import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;

/*
* fragment/activity that can hold translation and change its representation
*/
public interface ITranslateHolder {

  void setTranslationData(Translation translation);

}
