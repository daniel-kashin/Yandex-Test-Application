package com.danielkashin.yandextestapplication.presentation_layer.adapter.translations;


import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
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

  private static final String KEY_TRANSLATIONS = "KEY_TRANSLATIONS";
  private ArrayList<Translation> mTranslations;


  public TranslationsAdapter() {
    mTranslations = new ArrayList<>();
  }

  public TranslationsAdapter(Bundle savedInstanceState) {
    mTranslations = restoreTranslations(savedInstanceState);
  }

  // ------------------------------ ITranslationsModel methods ------------------------------------

  @Override
  public void clear() {
    mTranslations.clear();
    notifyDataSetChanged();
  }

  @Override
  public void addTranslations(List<Translation> translations, boolean clear) {
    if (clear) mTranslations.clear();

    for (Translation translation : translations) {
      mTranslations.add(translation);
      notifyItemChanged(mTranslations.size() - 1);
    }
  }

  @Override
  public int getSize() {
    return getItemCount();
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

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelableArrayList(KEY_TRANSLATIONS, mTranslations);
  }

  private ArrayList<Translation> restoreTranslations(Bundle savedInstanceState) {
    if (savedInstanceState == null || !savedInstanceState.containsKey(KEY_TRANSLATIONS)){
      throw new IllegalStateException("Bundle must contain the needed field");
    }

    ArrayList<Parcelable> parcelableArrayList = savedInstanceState.getParcelableArrayList(KEY_TRANSLATIONS);

    if (parcelableArrayList == null){
      throw new IllegalStateException("Field in bundle cannot be null");
    }

    ArrayList<Translation> result = new ArrayList<>();
    for (Parcelable parcelable : parcelableArrayList){
      result.add((Translation)parcelable);
    }
    return result;
  }

  // ----------------------------------- inner classes --------------------------------------------

  static class TranslationViewHolder extends RecyclerView.ViewHolder {

    private final ToggleButton favoriteToggle;
    private final TextView originalText;
    private final TextView translatedText;
    private final TextView language;


    private TranslationViewHolder(View view) {
      super(view);
      favoriteToggle = (ToggleButton) view.findViewById(R.id.toggle_favorite);
      originalText = (TextView) view.findViewById(R.id.text_original);
      translatedText = (TextView) view.findViewById(R.id.text_translated);
      language = (TextView) view.findViewById(R.id.text_language);
    }

    private void setIsFavourite(boolean isFavourite) {
      favoriteToggle.setChecked(isFavourite);
    }

    private void setOriginalText(String text) {
      originalText.setText(text);
    }

    private void setTranslatedText(String text) {
      translatedText.setText(text);
    }

    private void setLanguage(String text) {
      language.setText(text);
    }


  }

}
