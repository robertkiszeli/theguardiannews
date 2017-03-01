package com.robertkiszelirk.guardiannewsapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by kiszeli on 2017.02.25..
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    // Date type to check if it is fromDate or toDate
    private int dateType = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get curent date type
        dateType = getArguments().getInt("dateType");
        // Get current date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Show date picker dialog
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        // Send back date to main activity
        switch (dateType){
            case 1:MainActivity.setFromDate(year,month,day);
                break;
            case 2:MainActivity.setToDate(year,month,day);
                break;
        }

    }
}
