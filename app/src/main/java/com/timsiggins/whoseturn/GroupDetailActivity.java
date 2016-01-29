package com.timsiggins.whoseturn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

import com.timsiggins.whoseturn.data.Group;
import com.timsiggins.whoseturn.database.GroupsDatabase;
import com.timsiggins.whoseturn.database.PeopleDatabase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An activity representing a single Group detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link GroupListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link GroupDetailFragment}.
 */
public class GroupDetailActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;


    private PeopleDatabase peopleDatabase;
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);


        // Show the Up button in the action bar.
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }


        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            group = getIntent().getParcelableExtra(GroupDetailFragment.ARG_GROUP);
            arguments.putParcelable(GroupDetailFragment.ARG_GROUP, group);
            GroupDetailFragment fragment = new GroupDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.group_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        //return true;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                navigateUpTo(new Intent(this, GroupListActivity.class));
                return true;
            case R.id.delete:
                //todo - delete group
                GroupsDatabase groupsDatabase = new GroupsDatabase(this);
                groupsDatabase.open();
                groupsDatabase.deleteGroup(group.getId());
                groupsDatabase.close();
                finish();
                return true;
            case R.id.change_pic:
                //show pic chooser and set pic
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(pickIntent, PICK_IMAGE);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());

                    //get phone display width to resize pic to store a smaller image
                    Display display = getWindowManager().getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int maxWidth = Math.max(point.x, point.y);//biggest in either direction
                    int maxHeight = maxWidth * 3 / 5;
                    final BitmapDrawable drawable = decodeFile(inputStream, maxWidth, maxHeight);

                    if (drawable != null) {
                        final String filename = "pic-" + group.getId() + ".jpg";
                        final FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
                        drawable.getBitmap().compress(Bitmap.CompressFormat.JPEG, 90, fos);
                        fos.close();

                        GroupsDatabase groupsDatabase = new GroupsDatabase(this);
                        groupsDatabase.open();
                        groupsDatabase.addPicToGroup(group.getId(), filename);
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
            return new BitmapDrawable(getResources(), BitmapFactory.decodeStream(is2, null, o2));
        } catch (Exception e) {
            return null;
        }
    }

}
