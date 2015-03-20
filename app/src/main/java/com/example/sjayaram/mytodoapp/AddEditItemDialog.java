package com.example.sjayaram.mytodoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sjayaram.mytodoapp.Models.Item;

import java.util.Map;

/**
 * Created by sjayaram on 3/18/2015.
 */
public class AddEditItemDialog extends DialogFragment {

    private EditText mItemText;
    private EditText mItemPriorityText;

    public interface AddEditNameDialogListener {
        void onFinishAddEditDialog(String itemName, String priority);
    }

    AddEditNameDialogListener addEditNameDialogListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            addEditNameDialogListener = (AddEditNameDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement addEditNameDialogListener");
        }
    }

    public AddEditItemDialog() {
        // Empty constructor required for DialogFragment
    }

    public static AddEditItemDialog newInstance(String title) {
        AddEditItemDialog frag = new AddEditItemDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_item, container);

        mItemText = (EditText) view.findViewById(R.id.tvItemName);
        mItemPriorityText = (EditText) view.findViewById(R.id.tvItemPriority);

        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically
        mItemText.requestFocus();

        MainActivity activity = (MainActivity) getActivity();
        Item item = activity.getSelectedItem();

        if(item != null)
        {
            mItemText.setText(item.name);
            mItemPriorityText.setText(item.priority);
        }

        Button button = (Button) view.findViewById(R.id.btSave);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEditNameDialogListener.onFinishAddEditDialog(mItemText.getText().toString(), mItemPriorityText.getText().toString());
                dismiss();
            }
        });

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return view;
    }
}
