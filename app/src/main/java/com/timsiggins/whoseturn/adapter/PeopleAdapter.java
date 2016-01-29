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
public class PeopleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater mInflater;
    private final Context context;
    private static final String tag = "PeopleAdapter";
    private PeopleAdapterListener listener;
    private final int VIEW_NORMAL = 1;
    private final int VIEW_ADD = 2;
    private List<Person> people;
    private View.OnClickListener addListener;

    public PeopleAdapter(Context context, List<Person> people, PeopleAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        mInflater = LayoutInflater.from(this.context);
        this.people = people;
    }


    @Override
    public int getItemViewType(int position) {
        // last item is always the add item
        return position < people.size() ? VIEW_NORMAL : VIEW_ADD;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        RecyclerView.ViewHolder vh = null;
        if (viewType == VIEW_NORMAL) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.person_list_item, parent, false);
            // set the view's size, margins, paddings and layout parameters
            vh = new ViewHolderPerson(v);

        } else if (viewType == VIEW_ADD) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.add_new_item, parent, false);
            Log.d("tag", "item view is " + v);

            vh = new ViewHolderAdd(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (position < people.size()) {
            Person person = people.get(position);
            if (person != null && holder instanceof ViewHolderPerson) {
                Log.d(tag,"setting up holder");
                ViewHolderPerson pHolder = (ViewHolderPerson) holder;
                pHolder.title.setText(person.getName());
                pHolder.subtitle.setText(MessageFormat.format(context.getString(R.string.last_paid), person.getLastPaid()));
                pHolder.btnPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(tag,"pay clicked");
                        listener.onPayClicked(holder.getAdapterPosition());
                    }
                });
            }
        } else if (holder instanceof ViewHolderAdd) {
            ViewHolderAdd aHolder = (ViewHolderAdd) holder;
            addListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(tag, "add clicked");
                    listener.onAddClicked();
                }
            };
            aHolder.title.setOnClickListener(addListener);
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

            title = (TextView) itemView.findViewById(R.id.add);
        }
    }

    public interface PeopleAdapterListener {
        void onPayClicked(int position);
        void onAddClicked();
    }
}
