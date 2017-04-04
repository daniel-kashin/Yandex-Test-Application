package com.danielkashin.yandextestapplication.presentation_layer.adapter.history;


import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseTranslation;

import io.realm.RealmResults;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  RealmResults<DatabaseTranslation> results;


  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return null;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

  }

  @Override
  public int getItemCount() {
    return 0;
  }



//  private class TranslationHolder extends RecyclerView.ViewHolder {




//  }

}
