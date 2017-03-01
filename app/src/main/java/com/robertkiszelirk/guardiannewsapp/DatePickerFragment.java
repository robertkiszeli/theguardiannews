package com.robertkiszelirk.guardiannewsapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
            case 1:setFromDate(year,month,day);
                break;
            case 2:setToDate(year,month,day);
                break;
        }

    }

    private void setFromDate(int year, int month, int day){

        // Set year
        String sYear = String.valueOf(year);
        // Set month
        String sMonth;
        month += 1;
        if (month < 10){
            sMonth = "0" + String.valueOf(month);
        }else{
            sMonth = String.valueOf(month);
        }
        // Set day
        String sDay;
        if (day < 10){
            sDay = "0" + String.valueOf(day);
        }else{
            sDay = String.valueOf(day);
        }
        // Construct string
        String selectedDate = sYear + "-" + sMonth + "-" + sDay;
        // Convert dates to milliseconds
        String stringTo = MainActivity.TO_DATE;
        Date dateFrom = null;
        try {
            dateFrom = new SimpleDateFormat("yyyy-MM-dd").parse(selectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date dateTo = null;
        try {
            dateTo = new SimpleDateFormat("yyyy-MM-dd").parse(stringTo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millisFrom = dateFrom != null ? dateFrom.getTime() : 0;
        long millisTo = dateTo != null ? dateTo.getTime() : 0;
        // Check date valid from is before to date and neither after today
        if((System.currentTimeMillis() > millisTo)&&(millisFrom <= millisTo)){
            // Set date to button
            AppCompatButton fromDate = (AppCompatButton) getActivity().findViewById(R.id.from_date_button);
            fromDate.setText(selectedDate);
            MainActivity.FROM_DATE = selectedDate;
        }
    }

    private void setToDate(int year, int month, int day){

        // Set year
        String sYear = String.valueOf(year);
        // Set month
        String sMonth;
        month += 1;
        if (month < 10){
            sMonth = "0" + String.valueOf(month);
        }else{
            sMonth = String.valueOf(month);
        }
        // Set day
        String sDay;
        if (day < 10){
            sDay = "0" + String.valueOf(day);
        }else{
            sDay = String.valueOf(day);
        }
        // Construct string
        String selectedDate = sYear + "-" + sMonth + "-" + sDay;
        String stringFrom = MainActivity.FROM_DATE;
        Date dateFrom = null;
        try {
            dateFrom = new SimpleDateFormat("yyyy-MM-dd").parse(stringFrom);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date dateTo = null;
        try {
            dateTo = new SimpleDateFormat("yyyy-MM-dd").parse(selectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millisFrom = dateFrom != null ? dateFrom.getTime() : 0;
        long millisTo = dateTo != null ? dateTo.getTime() : 0;
        // Check date valid from is before to date and neither after today
        if((System.currentTimeMillis() > millisTo)&&(millisFrom <= millisTo)){
            // Set date to button
            AppCompatButton toDate = (AppCompatButton) getActivity().findViewById(R.id.to_date_button);
            toDate.setText(selectedDate);
            MainActivity.TO_DATE = selectedDate;
        }
    }
}
