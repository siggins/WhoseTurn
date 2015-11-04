package com.timsiggins.whoseturn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

import com.timsiggins.whoseturn.adapter.PeopleAdapter;
import com.timsiggins.whoseturn.data.Group;
import com.timsiggins.whoseturn.database.GroupsDatabase;
import com.timsiggins.whoseturn.database.PeopleDatabase;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * A fragment representing a single Group detail screen.
 * This fragment is either contained in a {@link GroupListActivity}
 * in two-pane mode (on tablets) or a {@link GroupDetailActivity}
 * on handsets.
 */
public class GroupDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_GROUP = "item_id";
    private static final int PICK_IMAGE = 1;

    /**
     * The dummy content this fragment is presenting.
     */
    private Group mGroup;
    private PeopleDatabase peopleDatabase;

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
                if (mGroup.size() == 0) {
                    //double check to see if anyone is in the group
                    peopleDatabase = new PeopleDatabase(getActivity());
                    peopleDatabase.open();
                    mGroup.addPeople(peopleDatabase.getPeopleForGroup(mGroup.getId()));
                    peopleDatabase.close();
                }

                Activity activity = this.getActivity();

                final String picPath = mGroup.getPicture();
                Log.d("GroupDetailFragment","picPath is "+picPath);

                if (picPath != null && !picPath.isEmpty()) {
                    ImageView img = (ImageView) activity.findViewById(R.id.toolbar_image);
                    try {
                        Drawable pic = Drawable.createFromStream(activity.openFileInput(picPath), null);
                        img.setImageDrawable(pic);
                        Log.d("GroupDetailFragment","set background");
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
                            //show pic chooser and set pic
                            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            getIntent.setType("image/*");

                            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            pickIntent.setType("image/*");

                            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                            startActivityForResult(pickIntent, PICK_IMAGE);
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
            ListView personList = (ListView) rootView.findViewById(R.id.people_list);
            personList.setAdapter(new PeopleAdapter(getActivity(), mGroup.getPeople()));
//            ((TextView) rootView.findViewById(R.id.group_detail)).setText(mGroup.getLastUsed().toString());
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());

                    //get phone display width to resize pic to store a smaller image
                    Display display = getActivity().getWindowManager().getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int maxWidth = Math.max(point.x, point.y);//biggest in either direction
                    int maxHeight = maxWidth * 3 / 5;
                    final BitmapDrawable drawable = decodeFile(inputStream, maxWidth, maxHeight);

                    if (drawable != null) {
                        final String filename = "pic-" + mGroup.getId() + ".jpg";
                        final FileOutputStream fos = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                        drawable.getBitmap().compress(Bitmap.CompressFormat.JPEG,90,fos);
                        fos.close();

                        GroupsDatabase groupsDatabase = new GroupsDatabase(getActivity());
                        groupsDatabase.open();
                        groupsDatabase.addPicToGroup(mGroup.getId(), filename);
                        groupsDatabase.close();

                    }

                } catch (FileNotFoundException e) {
                    Log.e("GroupDetailFragment", "Could not open input stream for saving picture", e);
                } catch (IOException e) {
                    Log.e("GroupDetailFragment", "IO Exception");
                }


            }
        }
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
            return new BitmapDrawable(getResources(),BitmapFactory.decodeStream(is2, null, o2));
        } catch (Exception e) {
            return null;
        }
    }
}
