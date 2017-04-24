package com.danielkashin.yandextestapplication.presentation_layer.presenter.history;

import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;


public interface IHistoryPresenter {

  void onAdapterItemLongClicked(Translation translation);

  void onAdapterToggleClicked(Translation translation);

  void deleteTranslations();

  void uploadTranslations(int offset, String searchRequest);

}
