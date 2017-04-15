package com.danielkashin.yandextestapplication.presentation_layer.application;


import com.danielkashin.yandextestapplication.data_layer.services.translate.local.ITranslateLocalService;

public interface ITranslateLocalServiceProvider {

  ITranslateLocalService getTranslateLocalService();

}
