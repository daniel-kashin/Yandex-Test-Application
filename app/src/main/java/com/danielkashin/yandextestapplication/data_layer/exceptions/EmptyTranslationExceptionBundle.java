package com.danielkashin.yandextestapplication.data_layer.exceptions;


public class EmptyTranslationExceptionBundle extends ExceptionBundle {

  public EmptyTranslationExceptionBundle(){
    super(Reason.EMPTY_TRANSLATION);
  }

}
