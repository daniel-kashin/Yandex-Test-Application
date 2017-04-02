package com.danielkashin.yandextestapplication.domain_layer.use_cases.local;

import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.services.local.ITranslateDatabaseService;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.base.UseCase;


public class GetLastTranslationUseCase implements UseCase {

  private final ITranslateDatabaseService databaseService;


  public GetLastTranslationUseCase(ITranslateDatabaseService databaseService){
    this.databaseService = databaseService;
  }


  public void run(final Callbacks callbacks) {
    DatabaseTranslation translation = databaseService.getLastTranslation();
    if (translation == null){
      callbacks.onGetLastTranslationError(new ExceptionBundle(ExceptionBundle.Reason.EMPTY_TRANSLATION));
    } else {
      callbacks.onGetLastTranslationResult(translation);
    }
  }


  @Override
  public void cancel() {
    // do nothing
  }


  public interface Callbacks {

    void onGetLastTranslationResult(DatabaseTranslation translation);

    void onGetLastTranslationError(ExceptionBundle exception);

  }
}
