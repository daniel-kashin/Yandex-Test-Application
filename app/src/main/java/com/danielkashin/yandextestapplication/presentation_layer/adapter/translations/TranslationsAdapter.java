package com.danielkashin.yandextestapplication.presentation_layer.adapter.translations;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;

import java.util.ArrayList;
import java.util.List;

public class TranslationsAdapter extends RecyclerView.Adapter<TranslationsAdapter.TranslationViewHolder>
  implements ITranslationsModel {

  private List<Translation> mTranslations;


  public TranslationsAdapter(){
    mTranslations = new ArrayList<>();
  }

  // ------------------------------ ITranslationsModel methods ------------------------------------

  @Override
  public void addTranslations(List<Translation> translations) {
    for (Translation translation : translations){
      mTranslations.add(translation);
      notifyItemChanged(mTranslations.size() - 1);
    }
  }

  @Override
  public int getSize() {
    return getItemCount();
  }

  @Override
  public void clear() {
    mTranslations.clear();
    notifyDataSetChanged();
  }

  // ----------------------------- RecyclerView.Adapter methods -----------------------------------

  @Override
  public TranslationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_translation, parent, false);

    return new TranslationViewHolder(view);
  }

  @Override
  public void onBindViewHolder(TranslationViewHolder holder, int position) {
    Translation translation = mTranslations.get(holder.getAdapterPosition());

    holder.setOriginalText(translation.getOriginalText());
    holder.setTranslatedText(translation.getTranslatedText());
    holder.setIsFavourite(translation.ifFavorite());
    holder.setLanguage(translation.getLanguage());
  }

  @Override
  public int getItemCount() {
    return mTranslations.size();
  }

  // ----------------------------------- inner classes --------------------------------------------

  static class TranslationViewHolder extends RecyclerView.ViewHolder {

    private final ToggleButton favoriteToggle;
    private final TextView originalText;
    private final TextView translatedText;
    private final TextView language;


    private TranslationViewHolder(View view){
      super(view);
      favoriteToggle = (ToggleButton)view.findViewById(R.id.toggle_favorite);
      originalText = (TextView)view.findViewById(R.id.text_original);
      translatedText = (TextView)view.findViewById(R.id.text_translated);
      language = (TextView)view.findViewById(R.id.text_language);
    }

    private void setIsFavourite(boolean isFavourite){
      favoriteToggle.setChecked(isFavourite);
    }

    private void setOriginalText(String text){
      originalText.setText(text);
    }

    private void setTranslatedText(String text){
      translatedText.setText(text);
    }

    private void setLanguage(String text){
      language.setText(text);
    }
  }

}
