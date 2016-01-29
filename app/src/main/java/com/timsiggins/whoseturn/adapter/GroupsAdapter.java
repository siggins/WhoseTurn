package com.timsiggins.whoseturn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.timsiggins.whoseturn.R;
import com.timsiggins.whoseturn.data.Group;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by tim on 11/1/15.
 */
public class GroupsAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private final Context context;
    private List<Group> groups;

    public GroupsAdapter(Context context, List<Group> groups) {
        this.context = context;
        mInflater = LayoutInflater.from(this.context);
        this.groups = groups;
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public Object getItem(int i) {
        return groups.get(i);
    }

    @Override
    public long getItemId(int i) {
        final Group group = groups.get(i);
        if (group != null) {
            return group.getId();
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // A ViewHolderPerson keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        ViewHolder holder;
        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.group_list_item, null);
            // Creates a ViewHolderPerson and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.subtitle = (TextView) convertView.findViewById(R.id.subtitle);
            convertView.setTag(holder);
        } else {
            // Get the ViewHolderPerson back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }
        // Bind the data efficiently with the holder.
        final Group group = groups.get(position);
        holder.title.setText(group.getName());
        if (holder.subtitle != null) {
            if (group.size() == 0) {
                holder.subtitle.setText(R.string.group_size0);
            } else if (group.size() == 1) {
                holder.subtitle.setText(group.getPerson(0).getName());
            } else if (group.size() == 2) {
                final String name = group.getPerson(0).getName();
                final String name1 = group.getPerson(1).getName();
                holder.subtitle.setText(MessageFormat.format(context.getString(R.string.group_size2), name, name1));
            } else if (group.size() == 3) {
                holder.subtitle.setText(MessageFormat.format(context.getString(R.string.groups_size3), group.getPerson(0).getName(), group.getPerson(1).getName()));
            } else if (group.size() > 3) {
                holder.subtitle.setText(MessageFormat.format(context.getString(R.string.groups_sizelarge), group.getPerson(0).getName(), group.getPerson(1).getName(), group.size() - 2));
            }
        }
        return convertView;
    }

    static class ViewHolder {
        TextView title;
        TextView subtitle;
    }

}
