package com.timsiggins.whoseturn;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.timsiggins.whoseturn.adapter.PeopleAdapter;
import com.timsiggins.whoseturn.data.Group;
import com.timsiggins.whoseturn.database.PeopleDatabase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

/**
 * A fragment representing a single Group detail screen.
 * This fragment is either contained in a {@link GroupListActivity}
 * in two-pane mode (on tablets) or a {@link GroupDetailActivity}
 * on handsets.
 */
public class GroupDetailFragment extends Fragment implements
        PeopleAdapter.PeopleAdapterListener, EditTextDialog.EditTextDialogListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_GROUP = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Group mGroup;
    private PeopleAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GroupDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_GROUP)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mGroup = getArguments().getParcelable(ARG_GROUP);
            if (mGroup != null) {
                Log.d("GroupDetailFragment", "mGroup old size is " + mGroup.size());
                if (mGroup.size() == 0) {
                    //double check to see if anyone is in the group
                    PeopleDatabase db = new PeopleDatabase(getActivity());
                    db.open();
                    mGroup.addPeople(db.getPeopleForGroup(mGroup.getId()));
                    db.close();
                    Log.d("GroupDetailFragment", "mGroup new size is " + mGroup.size());
                }
                Activity activity = this.getActivity();

                final String picPath = mGroup.getPicture();
                Log.d("GroupDetailFragment", "picPath is " + picPath);

                if (picPath != null && !picPath.isEmpty()) {
                    ImageView img = (ImageView) activity.findViewById(R.id.toolbar_image);
                    try {
                        Drawable pic = Drawable.createFromStream(activity.openFileInput(picPath), null);
                        img.setImageDrawable(pic);
                        Log.d("GroupDetailFragment", "set background");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(mGroup.getName());
                    appBarLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_detail, container, false);

        if (mGroup != null) {
            RecyclerView personList = (RecyclerView) rootView.findViewById(R.id.people_list);
            personList.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new PeopleAdapter(getActivity(), mGroup.getPeople(), this);
            Log.d("GroupDetailFragment", "There are " + adapter.getItemCount() + " items in the adapter");
            adapter.notifyDataSetChanged();
            personList.setAdapter(adapter);


//            ((TextView) rootView.findViewById(R.id.group_detail)).setText(mGroup.getLastUsed().toString());
        }

        return rootView;
    }


    private BitmapDrawable decodeFile(InputStream in, int maxWidth, int maxHeight) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
            InputStream is2 = new ByteArrayInputStream(baos.toByteArray());

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is1, null, o);


            System.out.println("h:" + o.outHeight + " w:" + o.outWidth);
            int scale = 1;
            if (o.outHeight > maxHeight || o.outWidth > maxWidth) {

                scale = (int) Math.pow(2,
                        (int) Math.round(Math.log((double) Math.max(o.outHeight / maxHeight, o.outWidth / maxWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return new BitmapDrawable(getResources(), BitmapFactory.decodeStream(is2, null, o2));
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public void onPayClicked(int position) {
        PeopleDatabase database = new PeopleDatabase(getContext());
        database.open();
        int id = (int) adapter.getItemId(position);
        database.makePersonPay(id, mGroup.getId());
        mGroup.setPeople(database.getPeopleForGroup(mGroup.getId()));
        database.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAddClicked() {
        FragmentManager fm = getFragmentManager();
        EditTextDialog alertDialog = EditTextDialog.newInstance("Add a new person", MessageFormat.format("Please enter a name to add to {0}", mGroup.getName()), this);
        alertDialog.show(fm, "fragment_alert");
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        PeopleDatabase db = new PeopleDatabase(getContext());
        db.open();
        db.addPersonToGroup(mGroup.getId(), inputText);
        mGroup.setPeople(db.getPeopleForGroup(mGroup.getId()));
        db.close();
        adapter.notifyDataSetChanged();
    }

}
