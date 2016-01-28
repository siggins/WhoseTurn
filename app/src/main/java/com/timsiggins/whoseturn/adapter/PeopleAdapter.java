package com.timsiggins.whoseturn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.timsiggins.whoseturn.R;
import com.timsiggins.whoseturn.data.Person;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by tim on 11/3/15.
 */
public class PeopleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private final LayoutInflater mInflater;
    private final Context context;
    private List<Person> people;

    public PeopleAdapter(Context context, List<Person> people) {
        this.context = context;
        mInflater = LayoutInflater.from(this.context);
        this.people = people;
    }


    @Override
    public int getItemViewType(int position) {
        // last item is always the add item
        return position < people.size() ? 1 : 2;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        RecyclerView.ViewHolder vh = null;
        if (viewType == 1) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.person_list_item, parent, false);
            // set the view's size, margins, paddings and layout parameters
            vh = new ViewHolderPerson(v);

        } else if (viewType == 2) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.add_new_item, parent, false);
            Log.d("tag", "item view is " + v);

            vh = new ViewHolderAdd(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < people.size()) {
            Person person = people.get(position);
            if (person != null && holder instanceof ViewHolderPerson) {
                ViewHolderPerson pHolder = (ViewHolderPerson) holder;
                pHolder.title.setText(person.getName());
                pHolder.subtitle.setText(MessageFormat.format(context.getString(R.string.last_paid), person.getLastPaid()));
                pHolder.btnPay.setOnClickListener(this);
            }
        } else if (holder instanceof ViewHolderAdd) {
            ViewHolderAdd aHolder = (ViewHolderAdd) holder;
            aHolder.title.setOnClickListener(this);
            //todo - add stuffs
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
        return people.size() + 1;
    }

    @Override
    public void onClick(View view) {

    }


    public static class ViewHolderPerson extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        Button btnPay;
        public View itemView;

        public ViewHolderPerson(View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            btnPay = (Button) itemView.findViewById(R.id.btnPay);
        }
    }

    public static class ViewHolderAdd extends RecyclerView.ViewHolder {
        TextView title;

        public ViewHolderAdd(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

    public interface PeopleAdapterCallback {
        void onPayClicked();
        void onAddClicked();
    }
}
