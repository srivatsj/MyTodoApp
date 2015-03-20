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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sjayaram.mytodoapp.Models.Item;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * Created by sjayaram on 3/18/2015.
 */
public class AddEditItemDialog extends DialogFragment {

    private EditText mItemText;
    private EditText mItemPriorityText;
    private DatePicker date_picker;
    private int year;
    private int month;
    private int day;

    public interface AddEditNameDialogListener {
        void onFinishAddEditDialog(String itemName, String priority, Date date);
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
        date_picker = (DatePicker) view.findViewById(R.id.date_picker);

        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically
        mItemText.requestFocus();


        MainActivity activity = (MainActivity) getActivity();
        Item item = activity.getSelectedItem();

        if(item != null)
        {
            mItemText.setText(item.name);
            mItemText.setSelection(item.name.length());
            mItemPriorityText.setText(item.priority);

            Calendar cal = new GregorianCalendar();
            cal.setTime(item.dueDate);
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);

            // set current date into Date Picker
            date_picker.init(year, month, day, null);
        }

        Button button = (Button) view.findViewById(R.id.btSave);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = new GregorianCalendar(date_picker.getYear(), date_picker.getMonth(), date_picker.getDayOfMonth());
                addEditNameDialogListener.onFinishAddEditDialog(mItemText.getText().toString(), mItemPriorityText.getText().toString(), cal.getTime());
                dismiss();
            }
        });

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return view;
    }
}
