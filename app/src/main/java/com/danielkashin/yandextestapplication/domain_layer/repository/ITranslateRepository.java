package com.danielkashin.yandextestapplication.domain_layer.repository;

import android.os.AsyncTask;

import com.danielkashin.yandextestapplication.domain_layer.async_task.remote.VoidAsyncTask;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;


public interface ITranslateRepository {

  VoidAsyncTask getTranslation(String originalText, String language, GetTranslationCallback callback);

  VoidAsyncTask getLastTranslation(GetTranslationCallback callback);

  VoidAsyncTask getAllTranslations(int count, int offset, GetTranslationsCallback callback);

  VoidAsyncTask getFavouriteTranslations(int count, int offset, GetTranslationsCallback callback);

}