package com.timsiggins.whoseturn;

/**
 * Created by tim on 11/3/15.
 */
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;
// ...

public class EditTextDialog extends DialogFragment implements TextView.OnEditorActionListener {

    private EditText mEditText;

    public interface EditTextDialogListener {
        void onFinishEditDialog(String inputText);
    }

    public EditTextDialog() {
    }

    public static EditTextDialog newInstance(String title, String message) {
        EditTextDialog frag = new EditTextDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message",message);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                done();
            }
        });
        String title = getArguments().getString("title", "Enter Text");
        builder.setTitle(title);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_edit_text, null);
        mEditText = (EditText) view.findViewById(R.id.txt_text);

        TextView message = (TextView) view.findViewById(R.id.lbl_message);
        message.setText(getArguments().getString("message","Enter some text"));

        builder.setView(view);

        return builder.create();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            done();
            return true;
        }
        return false;
    }

    private void done() {
        // Return input text to activity
        EditTextDialogListener listener = (EditTextDialogListener) getActivity();
        listener.onFinishEditDialog(mEditText.getText().toString());
        dismiss();
    }
}
