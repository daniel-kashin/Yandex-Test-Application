package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.support.v4.util.Pair;

import com.danielkashin.yandextestapplication.data_layer.repository.languages.ISupportedLanguagesRepository;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;


public class GetLanguagesFromTranslationUseCase {

  private final ISupportedLanguagesRepository supportedLanguagesRepository;

  private RepositoryAsyncTaskResponse<LanguagePair> getLanguages;


  public GetLanguagesFromTranslationUseCase(ISupportedLanguagesRepository supportedLanguagesRepository) {
    if (supportedLanguagesRepository == null) {
      throw new IllegalArgumentException("All arguments of use case must be non null");
    }

    this.supportedLanguagesRepository = supportedLanguagesRepository;
  }

  // ------------------------------------- public methods -----------------------------------------

  public void run(final Callbacks callbacks, final Translation translation) {
    LanguagePair languagePair = supportedLanguagesRepository.getLanguages(
        translation.getLanguageCodes()[0],
        translation.getLanguageCodes()[1]);

    callbacks.onGetLanguagesFromTranslationSuccess(new Pair<>(translation, languagePair));
  }

  // ------------------------------------ inner classes--------------------------------------------

  public interface Callbacks {

    void onGetLanguagesFromTranslationSuccess(Pair<Translation, LanguagePair> result);

  }
}
