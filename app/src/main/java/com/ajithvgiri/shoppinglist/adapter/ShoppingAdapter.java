package com.ajithvgiri.shoppinglist.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ajithvgiri.shoppinglist.R;
import com.ajithvgiri.shoppinglist.context.Prefs;
import com.ajithvgiri.shoppinglist.model.Shopping;
import com.ajithvgiri.shoppinglist.realm.RealmController;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Ajith v Giri on 25-11-2016.
 */

public class ShoppingAdapter extends RealmRecyclerViewAdapter<Shopping> {

    final Context context;
    private Realm realm;
    private LayoutInflater inflater;

    public ShoppingAdapter(Context context) {

        this.context = context;
    }

    // create new views (invoked by the layout manager)
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate a new card view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        return new CardViewHolder(view);
    }

    // replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        realm = RealmController.getInstance().getRealm();

// get the article
        final Shopping shopping = getItem(position);
// cast the generic view holder to our specific one
        final CardViewHolder holder = (CardViewHolder) viewHolder;

        // set the title and the snippet
        holder.textTitle.setText(shopping.getTitle());
        holder.textAuthor.setText(shopping.getDescription());
        String status = shopping.getStatus();
        if (status.equals("checked")){
            holder.checkBox.setChecked(true);
            holder.textTitle.setPaintFlags(holder.textTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textAuthor.setPaintFlags(holder.textAuthor.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            holder.checkBox.setChecked(false);
            holder.textTitle.setPaintFlags(holder.textTitle.getPaintFlags()  & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textAuthor.setPaintFlags(holder.textAuthor.getPaintFlags()  & (~Paint.STRIKE_THRU_TEXT_FLAG));

        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.checkBox.isChecked()){
                    holder.textTitle.setPaintFlags(holder.textTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.textAuthor.setPaintFlags(holder.textAuthor.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    RealmResults<Shopping> results = realm.where(Shopping.class).findAll();
                    realm.beginTransaction();
                    results.get(position).setStatus("checked");
                    realm.commitTransaction();
                    notifyDataSetChanged();
                }else {
                    holder.textTitle.setPaintFlags(holder.textTitle.getPaintFlags()  & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.textAuthor.setPaintFlags(holder.textAuthor.getPaintFlags()  & (~Paint.STRIKE_THRU_TEXT_FLAG));

                    RealmResults<Shopping> results = realm.where(Shopping.class).findAll();
                    realm.beginTransaction();
                    results.get(position).setStatus("unchecked");
                    realm.commitTransaction();

                    notifyDataSetChanged();

                }
            }
        });
                //holder.textDescription.setText(shopping.getDescription());


                //remove single match from realm
                        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                RealmResults<Shopping> results = realm.where(Shopping.class).findAll();

                // Get the shopping title to show it in toast message
                Shopping b = results.get(position);
                String title = b.getTitle();

                // All changes to data must happen in a transaction
                realm.beginTransaction();

                // remove single match
                results.remove(position);
                realm.commitTransaction();

                if (results.size() == 0) {
                    Prefs.with(context).setPreLoad(false);
                }

                notifyDataSetChanged();

                Toast.makeText(context, title + " is removed from Shopping", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

//        //update single match from realm
        holder.card.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View content = inflater.inflate(R.layout.edit_item, null);
                final EditText editTitle = (EditText) content.findViewById(R.id.title);
                final EditText editAuthor = (EditText) content.findViewById(R.id.author);

                editTitle.setText(shopping.getTitle());
                editAuthor.setText(shopping.getDescription());


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(content)
                        .setTitle("Edit Item")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                RealmResults<Shopping> results = realm.where(Shopping.class).findAll();

                                realm.beginTransaction();
                                results.get(position).setDescription(editAuthor.getText().toString());
                                results.get(position).setTitle(editTitle.getText().toString());
                                //results.get(position).setImageUrl(editThumbnail.getText().toString());
                                realm.commitTransaction();
                                if (results.size() == 0) {
                                    Prefs.with(context).setPreLoad(false);
                                }
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }


    // return the size of your data set (invoked by the layout manager)
    public int getItemCount() {

        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public CardView card;
        public TextView textTitle;
        public TextView textAuthor;
        public CheckBox checkBox;

        public CardViewHolder(View itemView) {
            // standard view holder pattern with Butterknife view injection
            super(itemView);

            card = (CardView) itemView.findViewById(R.id.card);
            textTitle = (TextView) itemView.findViewById(R.id.text_country);
            textAuthor = (TextView) itemView.findViewById(R.id.text_currency);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkbox);
            //textDescription = (TextView) itemView.findViewById(R.id.text_currencys_description);
            //imageBackground = (ImageView) itemView.findViewById(R.id.image_background);
        }
    }
}