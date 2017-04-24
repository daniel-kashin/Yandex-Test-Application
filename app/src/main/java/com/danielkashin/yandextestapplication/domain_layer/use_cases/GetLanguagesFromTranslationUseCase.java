package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.support.v4.util.Pair;

import com.danielkashin.yandextestapplication.domain_layer.repository.languages.ISupportedLanguagesRepository;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;


public class GetLanguagesFromTranslationUseCase {

  private final ISupportedLanguagesRepository supportedLanguagesRepository;


  public GetLanguagesFromTranslationUseCase(ISupportedLanguagesRepository supportedLanguagesRepository) {
    if (supportedLanguagesRepository == null) {
      throw new IllegalArgumentException("All arguments of use case must be non null");
    }

    this.supportedLanguagesRepository = supportedLanguagesRepository;
  }

  // ------------------------------------- public methods -----------------------------------------

  public void run(Callbacks callbacks, Translation translation) {
    if (callbacks == null) {
      throw new IllegalStateException("Callbacks in UseCase must be non null");
    }

    // just execute it synchronously: if service will change, we will be able to make this task
    // async in few lines of code without recompiling anything except this class
    LanguagePair languagePair = supportedLanguagesRepository.getLanguages(
        translation.getLanguageCodes()[0],
        translation.getLanguageCodes()[1]);
    callbacks.onGetLanguagesFromTranslationSuccess(new Pair<>(translation, languagePair));
  }

  // ------------------------------------ inner types --------------------------------------------

  public interface Callbacks {

    void onGetLanguagesFromTranslationSuccess(Pair<Translation, LanguagePair> result);

  }
}
