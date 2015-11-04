package com.timsiggins.whoseturn.adapter;

import android.content.Context;
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
public class PeopleAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private final Context context;
    private List<Person> people;

    public PeopleAdapter(Context context, List<Person> people) {
        this.context = context;
        mInflater = LayoutInflater.from(this.context);
        this.people = people;
    }

    @Override
    public int getCount() {
        return people.size();
    }

    @Override
    public Object getItem(int i) {
        return people.get(i);
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // A ViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        ViewHolder holder;
        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.subtitle = (TextView) convertView.findViewById(R.id.subtitle);
            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }
        // Bind the data efficiently with the holder.
        final Person person = people.get(position);
        if (person != null) {
            holder.title.setText(person.getName());
            holder.subtitle.setText(MessageFormat.format(context.getString(R.string.last_paid), person.getLastPaid()));
        } else {
            Log.d("PeopleAdapter", "no person in list at position "+position);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView title;
        TextView subtitle;
    }
}
