package com.ajithvgiri.shoppinglist.adapter;

import android.content.Context;

import com.ajithvgiri.shoppinglist.model.Shopping;

import io.realm.RealmResults;

/**
 * Created by Ajith v Giri on 25-11-2016.
 */

public class RealmShoppingAdapter extends RealmModelAdapter<Shopping> {

    public RealmShoppingAdapter(Context context, RealmResults<Shopping> realmResults, boolean automaticUpdate) {

        super(context, realmResults, automaticUpdate);
    }
}