package com.danielkashin.yandextestapplication.presentation_layer.application;


import com.danielkashin.yandextestapplication.data_layer.services.translation.local.ITranslationLocalService;

public interface ITranslateLocalServiceProvider {

  ITranslationLocalService getTranslateLocalService();

}
