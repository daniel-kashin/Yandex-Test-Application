package com.danielkashin.yandextestapplication.presentation_layer.adapter.languages;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Language;

import java.util.ArrayList;

public class LanguagesAdapter extends RecyclerView.Adapter<LanguagesAdapter.TranslationViewHolder>
    implements ILanguagesAdapter {

  private static final String KEY_LANGUAGES = "KEY_LANGUAGES";
  private ILanguagesAdapter.Callbacks mCallbacks;
  private ArrayList<Language> mLanguages;


  public LanguagesAdapter() {
    mLanguages = new ArrayList<>();
  }

  public LanguagesAdapter(Bundle bundle) {
    mLanguages = restoreLanguages(bundle);
  }

  // ----------------------------------- ILanguagesAdapter -------------------------------------------

  @Override
  public void addCallbacks(Callbacks callbacks) {
    mCallbacks = callbacks;
  }

  @Override
  public void removeCallbacks() {
    mCallbacks = null;
  }

  @Override
  public boolean isInitialized() {
    return mLanguages != null;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
      outState.putParcelableArrayList(KEY_LANGUAGES, mLanguages);
  }

  @Override
  public void addLanguages(ArrayList<Language> languages) {
    if (languages != null) {
      for (Language language : languages) {
        mLanguages.add(language);
        notifyItemInserted(mLanguages.size() - 1);
      }
    }
  }

  // ---------------------------------- RecyclerView.Adapter --------------------------------------

  @Override
  public TranslationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_language, parent, false);

    return new TranslationViewHolder(view);
  }

  @Override
  public void onBindViewHolder(TranslationViewHolder holder, int position) {
    final Language language = mLanguages.get(holder.getAdapterPosition());

    holder.setLanguageText(language.getText());
    holder.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mCallbacks != null) {
          mCallbacks.onItemClicked(language);
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return mLanguages.size();
  }

  // -------------------------------------- private -----------------------------------------------

  private ArrayList<Language> restoreLanguages(Bundle savedInstanceState) {
    if (savedInstanceState == null || !savedInstanceState.containsKey(KEY_LANGUAGES)) {
      return null;
    }

    ArrayList<Parcelable> parcelableArrayList = savedInstanceState
        .getParcelableArrayList(KEY_LANGUAGES);

    if (parcelableArrayList == null) {
      return null;
    }

    ArrayList<Language> result = new ArrayList<>();
    for (Parcelable parcelable : parcelableArrayList) {
      result.add((Language) parcelable);
    }
    return result;
  }

  // ------------------------------------- inner classes ------------------------------------------

  static class TranslationViewHolder extends RecyclerView.ViewHolder {

    private final TextView languageText;
    private final View rootView;

    private TranslationViewHolder(View view) {
      super(view);
      rootView = view;
      languageText = (TextView) view.findViewById(R.id.text_language);
    }

    private void setLanguageText(String text) {
      languageText.setText(text);
    }

    private void setOnClickListener(View.OnClickListener listener) {
      rootView.setOnClickListener(listener);
    }

  }
}
