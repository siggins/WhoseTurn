package com.timsiggins.whoseturn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.timsiggins.whoseturn.data.Group;
import com.timsiggins.whoseturn.database.GroupsDatabase;


/**
 * An activity representing a list of Groups. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link GroupDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link GroupListFragment} and the item details
 * (if present) is a {@link GroupDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link GroupListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class GroupListActivity extends AppCompatActivity
        implements GroupListFragment.Callbacks, EditTextDialog.EditTextDialogListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_app_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        if (findViewById(R.id.group_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((GroupListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.group_list))
                    .setActivateOnItemClick(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                EditTextDialog alertDialog = EditTextDialog.newInstance("Create a New Group", "Please enter the group name", GroupListActivity.this);
                alertDialog.show(fm, "fragment_alert");
            }
        });

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link GroupListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     *
     * @param group
     */
    @Override
    public void onItemSelected(Group group) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(GroupDetailFragment.ARG_GROUP, group);
            GroupDetailFragment fragment = new GroupDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.group_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, GroupDetailActivity.class);
            detailIntent.putExtra(GroupDetailFragment.ARG_GROUP, group);
            startActivity(detailIntent);
        }
    }

    @Override
    public void onFinishEditDialog(String inputText) {
//        Snackbar.make(null, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
        final GroupsDatabase groupsDatabase = new GroupsDatabase(this);
        groupsDatabase.open();
        Group g = groupsDatabase.addGroup(inputText);
        groupsDatabase.close();
        onItemSelected(g);
    }
}
