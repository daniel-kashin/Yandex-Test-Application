package com.danielkashin.yandextestapplication.data_layer.exceptions;


public class EmptyTranslationsExceptionBundle extends ExceptionBundle {

  public EmptyTranslationsExceptionBundle(){
    super(Reason.EMPTY_TRANSLATIONS);
  }

}
