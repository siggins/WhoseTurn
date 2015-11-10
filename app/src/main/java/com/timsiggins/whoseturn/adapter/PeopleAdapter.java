package com.timsiggins.whoseturn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.timsiggins.whoseturn.R;
import com.timsiggins.whoseturn.data.Person;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by tim on 11/3/15.
 */
public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    private final Context context;
    private List<Person> people;

    public PeopleAdapter(Context context, List<Person> people) {
        this.context = context;
        mInflater = LayoutInflater.from(this.context);
        this.people = people;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position < people.size()) {
            Person person = people.get(position);
            if (person != null) {
                holder.title.setText(person.getName());
                holder.subtitle.setText(MessageFormat.format(context.getString(R.string.last_paid), person.getLastPaid()));

            }
        }
    }


    @Override
    public long getItemId(int i) {
        final Person person = people.get(i);
        if (person != null) {
            return person.getId();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return people.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        public View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
        }
    }


}
