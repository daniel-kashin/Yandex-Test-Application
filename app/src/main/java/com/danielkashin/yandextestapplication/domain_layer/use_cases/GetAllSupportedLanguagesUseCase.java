package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import com.danielkashin.yandextestapplication.domain_layer.repository.languages.ISupportedLanguagesRepository;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Language;

import java.util.ArrayList;


public class GetAllSupportedLanguagesUseCase {

  private final ISupportedLanguagesRepository supportedLanguagesRepository;


  public GetAllSupportedLanguagesUseCase(ISupportedLanguagesRepository supportedLanguagesRepository) {
    if (supportedLanguagesRepository == null) {
      throw new IllegalArgumentException("All arguments of use case must be non null");
    }

    this.supportedLanguagesRepository = supportedLanguagesRepository;
  }

  // ------------------------------------- public methods -----------------------------------------

  public void run(Callbacks callbacks) {
    if (callbacks == null) {
      throw new IllegalStateException("Callbacks must be non null");
    }

    // just execute it synchronously: if service will change, we will be able to make this task
    // async in few lines of code without recompiling anything except this class
    ArrayList<Language> languages = supportedLanguagesRepository.getAllLanguages();
    callbacks.onGetLanguagesFromTranslationSuccess(languages);
  }

  // ------------------------------------ inner types --------------------------------------------

  public interface Callbacks {

    void onGetLanguagesFromTranslationSuccess(ArrayList<Language> result);

  }
}
