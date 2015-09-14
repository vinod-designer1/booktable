package com.experiences.projects.booktable;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;


public class TimePrefActivity extends Activity {

    Context context;

    Button datePickerBtn;
    Button timePickerBtnbefore;
    Button timePickerBtnafter;
    Boolean afterTime = true;

    int year_x, month_x, day_x, hour_x, min_x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_pref);

        context = this;

        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        hour_x = cal.get(Calendar.HOUR);
        min_x = cal.get(Calendar.MINUTE);

        final AbstractWheel people = (AbstractWheel) findViewById(R.id.tp_sw_hv_people);
        people.setViewAdapter(new NumericWheelAdapter(this, 1, 20));
        people.setCyclic(true);

        showDateDialogButton();
        showTimeDialogButton();

        Button next_hotels = (Button) findViewById(R.id.btn_next);

        next_hotels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker date = (DatePicker) v.findViewById(R.id.dp_date);
                TimePicker time = (TimePicker) v.findViewById(R.id.tp_time);


                SharedPreferences mypref = getSharedPreferences("timer", Activity.MODE_PRIVATE);
                SharedPreferences.Editor myprefEditor = mypref.edit();
                if (date != null)
                    myprefEditor.putString("Date", date.toString());
                if (time != null)
                    myprefEditor.putString("Time", time.toString());
                myprefEditor.putString("People", "");
                myprefEditor.apply();

                Intent hotelIntent = new Intent(context, HotelList.class);
                context.startActivity(hotelIntent);
            }
        });
    }

    private void showDateDialogButton() {
        datePickerBtn = (Button) findViewById(R.id.tp_btn_select_date);

        datePickerBtn.setText(year_x + "/" + month_x + "/" + day_x);

        datePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });
    }

    private void showTimeDialogButton() {
        timePickerBtnafter = (Button) findViewById(R.id.tp_btn_time_after);
        timePickerBtnafter.setText(hour_x + ":" + min_x);
        timePickerBtnafter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afterTime = true;
                showDialog(1);
            }
        });



        timePickerBtnbefore = (Button) findViewById(R.id.tp_btn_time_before);
        timePickerBtnbefore.setText(hour_x + ":" + min_x);
        timePickerBtnbefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afterTime = false;
                showDialog(1);
            }
        });
    }


    protected Dialog onCreateDialog(int id) {
        if (id == 0)
            return  new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    datePickerBtn.setText(year + "/" + monthOfYear + "/" + dayOfMonth);
                    //Toast.makeText(context, year + "/" + monthOfYear + "/" + dayOfMonth, Toast.LENGTH_LONG).show();
                }
            }, year_x, month_x, day_x);
        else if (id == 1) {
            return new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    if (afterTime) {
                        timePickerBtnafter.setText(hourOfDay+":"+minute);
                    } else {
                        timePickerBtnbefore.setText(hourOfDay+":"+minute);
                    }
                }
            }, hour_x, min_x, true);
        }

        return  null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_picker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
