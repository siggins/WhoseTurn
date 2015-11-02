package com.timsiggins.whoseturn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.timsiggins.whoseturn.R;
import com.timsiggins.whoseturn.data.Group;

import java.util.List;

/**
 * Created by tim on 11/1/15.
 */
public class GroupsAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private List<Group> groups;

    public GroupsAdapter(Context context, List<Group> groups) {
        mInflater = LayoutInflater.from(context);
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
        // A ViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        ViewHolder holder;
        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.group_list_item, null);
            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.group_title);
            holder.subtitle = (TextView) convertView.findViewById(R.id.group_subtitle);
            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }
        // Bind the data efficiently with the holder.
        final Group group = groups.get(position);
        holder.title.setText(group.getName());
        holder.subtitle.setText(group.size());
        return convertView;
    }

    static class ViewHolder {
        TextView title;
        TextView subtitle;
    }

}
