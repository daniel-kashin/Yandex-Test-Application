package com.danielkashin.yandextestapplication.presentation_layer.adapter.translations;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;

import java.util.ArrayList;
import java.util.List;


public class TranslationsAdapter extends RecyclerView.Adapter<TranslationsAdapter.TranslationViewHolder>
    implements ITranslationsAdapter {

  private static final int ITEM_DIFFERENCE_START_UPLOADING_TO_END = 10;
  private static final String KEY_TRANSLATIONS = "KEY_TRANSLATIONS";
  private ITranslationsAdapter.Callbacks mCallbacks;
  private ArrayList<Translation> mTranslations;
  private boolean mEndReached;
  private boolean mDataUploadingToEnd;


  public TranslationsAdapter() {
    mTranslations = new ArrayList<>();
  }

  public TranslationsAdapter(Bundle savedInstanceState) {
    mTranslations = restoreTranslations(savedInstanceState);
  }

  // ------------------------------ ITranslationsAdapter methods ------------------------------------

  @Override
  public void setDataUploadingToEndTrue() {
    mDataUploadingToEnd = true;
  }

  @Override
  public void addCallbacks(ITranslationsAdapter.Callbacks callbacks) {
    mCallbacks = callbacks;
  }

  @Override
  public void removeCallbacks() {
    mCallbacks = null;
  }

  @Override
  public void clear() {
    mTranslations.clear();
    mEndReached = false;
    mDataUploadingToEnd = false;
    notifyDataSetChanged();
  }

  @Override
  public void addTranslations(List<Translation> translations, boolean clear) {
    if (clear) {
      clear();
    }

    for (Translation translation : translations) {
      mTranslations.add(translation);
      notifyItemInserted(mTranslations.size() - 1);
    }

    if (!clear) {
      mDataUploadingToEnd = false;
    }
  }

  @Override
  public void setEndReached() {
    mEndReached = true;
    mDataUploadingToEnd = false;
  }

  @Override
  public int getSize() {
    return getItemCount();
  }

  @Override
  public boolean isInitialized() {
    return mTranslations != null;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelableArrayList(KEY_TRANSLATIONS, mTranslations);
  }

  @Override
  public boolean isDataUploadingToEndNeeded(int lastVisibleItem) {
    boolean result = !mDataUploadingToEnd && !mEndReached && !(mTranslations == null)
        && (lastVisibleItem > mTranslations.size() - ITEM_DIFFERENCE_START_UPLOADING_TO_END);

    return result;
  }

  @Override
  public boolean isOnlyFavorite() {
    if (mTranslations == null) {
      return false;
    }

    for (int i = 0; i < mTranslations.size(); ++i) {
      if (!mTranslations.get(i).ifFavorite()) {
        return false;
      }
    }

    return true;
  }

  @Override
  public void deleteTranslation(Translation translation) {
    if (mTranslations != null) {
      for (int i = 0; i < mTranslations.size(); ++i) {
        if (mTranslations.get(i) == translation) {
          mTranslations.remove(i);
          notifyItemRemoved(i);
          return;
        }
      }
    }
  }

  // ---------------------------------- RecyclerView.Adapter --------------------------------------

  @Override
  public TranslationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_translation, parent, false);

    return new TranslationViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final TranslationViewHolder holder, final int position) {
    final Translation translation = mTranslations.get(holder.getAdapterPosition());

    holder.setOriginalText(translation.getOriginalText());
    holder.setTranslatedText(translation.getTranslatedText());
    holder.setIsFavourite(translation.ifFavorite());
    holder.setLanguageText(translation.getLanguageCodePair());

    holder.setOnFavoriteToggleListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mCallbacks != null) {
          translation.setFavorite(isChecked);
          mCallbacks.onToggleFavoriteClicked(translation);
        }
      }
    });

    holder.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mCallbacks != null) {
          mCallbacks.onItemClicked(translation);
        }
      }
    });

    holder.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View view) {
        if (mCallbacks != null) {
          mCallbacks.onLongItemClicked(translation);
          return true;
        } else {
          return false;
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return mTranslations.size();
  }

  // -------------------------------------- private -----------------------------------------------

  private ArrayList<Translation> restoreTranslations(Bundle savedInstanceState) {
    if (savedInstanceState == null || !savedInstanceState.containsKey(KEY_TRANSLATIONS)) {
      return null;
    }

    ArrayList<Parcelable> parcelableArrayList = savedInstanceState
        .getParcelableArrayList(KEY_TRANSLATIONS);

    if (parcelableArrayList == null) {
      return null;
    }

    ArrayList<Translation> result = new ArrayList<>();
    for (Parcelable parcelable : parcelableArrayList) {
      result.add((Translation) parcelable);
    }
    return result;
  }

  // ----------------------------------- inner classes --------------------------------------------

  static class TranslationViewHolder extends RecyclerView.ViewHolder {

    private final View rootView;
    private final ToggleButton favoriteToggle;
    private final TextView originalText;
    private final TextView translatedText;
    private final TextView languageText;


    private TranslationViewHolder(View view) {
      super(view);
      rootView = view;
      favoriteToggle = (ToggleButton) view.findViewById(R.id.toggle_favorite);
      originalText = (TextView) view.findViewById(R.id.text_original);
      translatedText = (TextView) view.findViewById(R.id.text_translated);
      languageText = (TextView) view.findViewById(R.id.text_language);
    }

    private void setIsFavourite(boolean isFavourite) {
      favoriteToggle.setOnCheckedChangeListener(null);
      favoriteToggle.setChecked(isFavourite);
    }

    private void setOriginalText(String text) {
      originalText.setText(text);
    }

    private void setTranslatedText(String text) {
      translatedText.setText(text);
    }

    private void setLanguageText(String text) {
      languageText.setText(text);
    }

    private void setOnFavoriteToggleListener(CompoundButton.OnCheckedChangeListener listener) {
      favoriteToggle.setOnCheckedChangeListener(listener);
    }

    public void setOnClickListener(View.OnClickListener listener) {
      rootView.setOnClickListener(listener);
    }

    public void setOnLongClickListener(View.OnLongClickListener listener) {
      rootView.setOnLongClickListener(listener);
    }
  }

}
