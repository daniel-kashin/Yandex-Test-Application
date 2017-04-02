package com.danielkashin.yandextestapplication.domain_layer.use_cases.local;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.services.local.ITranslateDatabaseService;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.base.UseCase;
import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmAsyncTask;


public class SaveTranslationUseCase implements UseCase {

  private final ITranslateDatabaseService databaseService;
  private final ArrayList<RealmAsyncTask> asyncTasks;


  public SaveTranslationUseCase(ITranslateDatabaseService databaseService){
    this.databaseService = databaseService;
    this.asyncTasks = new ArrayList<>();
  }


  public void run(final Callbacks callbacks, String originalText, String translatedText, String language) {
    Realm.Transaction.OnError onError = new Realm.Transaction.OnError() {
      @Override
      public void onError(Throwable error) {
          callbacks.onSaveTranslationError(new ExceptionBundle(ExceptionBundle.Reason.SAVE_DENIED));
      }
    };

    RealmAsyncTask realmAsyncTask = databaseService
        .saveTranslationAsync(originalText, translatedText, language, onError);

    asyncTasks.add(realmAsyncTask);
  }


  @Override
  public void cancel() {
    for (int i = 0; i < asyncTasks.size(); ++i) {
      if (asyncTasks.get(i) != null) {
        asyncTasks.get(i).cancel();
      }
    }
  }


  public interface Callbacks {

    void onSaveTranslationError(ExceptionBundle exception);

  }
}
