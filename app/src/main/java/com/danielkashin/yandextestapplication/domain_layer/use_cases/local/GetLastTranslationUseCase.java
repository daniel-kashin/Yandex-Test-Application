package com.danielkashin.yandextestapplication.domain_layer.use_cases.local;

import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.services.local.ITranslateLocalService;
import com.danielkashin.yandextestapplication.domain_layer.repository.ITranslateRepository;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.base.UseCase;


public class GetLastTranslationUseCase implements UseCase {

  private ITranslateRepository repository;


  public GetLastTranslationUseCase(ITranslateRepository repository){
    this.repository = repository;
  }


  @Override
  public void cancel() {
    // do nothing
  }


  public void run(final Callbacks callbacks) {

  }


  public interface Callbacks {

    void onGetLastTranslationResult(DatabaseTranslation translation);

    void onGetLastTranslationError(ExceptionBundle exception);

  }
}
