package com.experiences.projects.booktable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SharedMenu extends Activity {

    Context context;
    private HotelMenuListAdapter hotelItemAdapter;
    private ArrayAdapter<String> friendListAdapter;

    String bookingId;

    ListView menuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_menu);

        Intent intent = getIntent();
        String hotelid = intent.getStringExtra("HotelId");
        bookingId = intent.getStringExtra("BookingId");


        context = this;

        ArrayList<ParseObject> hotelMenuInfo = new ArrayList<ParseObject>();
        hotelItemAdapter = new HotelMenuListAdapter(context, 0, hotelMenuInfo);
        menuList = (ListView) findViewById(R.id.lv_shared_hotel_menu);
        menuList.setAdapter(hotelItemAdapter);

        ArrayList<String> s = new ArrayList<String>();
        friendListAdapter = new ArrayAdapter<String>(context, 0, s);
        ListView friendList = (ListView) findViewById(R.id.sm_lv_friendList);
        friendList.setAdapter(friendListAdapter);

        getHotelMenuItem(hotelid);

        assignActionstoView();
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void addBookingMenu(View v) {
        for (int i=0; i < menuList.getCount(); ++i) {
            ParseObject userbooking = new ParseObject("UserBooking");
            userbooking.setObjectId(bookingId);

            ParseObject userOrder = new ParseObject("UserOrder");
            userOrder.put("OrderedUser", ParseUser.getCurrentUser());
            userOrder.put("Booking", userbooking); //@TODO: check key
            userOrder.put("Item", hotelItemAdapter.getItem(i));
            userOrder.put("Qty", Integer.parseInt(
                    ((TextView) menuList.getChildAt(i).findViewById(R.id.tv_item_qty)).getText().toString()
            ));
            userOrder.saveInBackground();
        }

        Intent homeIntent = new Intent(context, TimePrefActivity.class);
        context.startActivity(homeIntent);
    }

    private void assignActionstoView() {
        Button btnAddPerson = (Button) findViewById(R.id.sm_btn_add_person);
        Button btnConfirmMenu = (Button) findViewById(R.id.sm_btn_confirm_menu);

        btnConfirmMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookingMenu(v);
            }
        });



        btnAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText personMail = (EditText) v.findViewById(R.id.sm_et_frnd_search);

                if (isValidEmail(personMail.getText())) {
                    ParseQuery<ParseUser> userquery = ParseUser.getQuery();
                    userquery.whereEqualTo("username", personMail.getText());

                    userquery.getFirstInBackground(new GetCallback<ParseUser>() {
                        public void done(final ParseUser user, ParseException e) {
                            if (user == null) {
                                ParseUser newuser = new ParseUser();
                                newuser.setUsername(personMail.getText().toString());
                                newuser.setPassword("AwesomeUser@salt");
                                newuser.signUpInBackground(new SignUpCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            addUserToShareList(user);
                                            friendListAdapter.insert(personMail.getText().toString(), friendListAdapter.getCount());
                                            friendListAdapter.notifyDataSetChanged();
                                        } else {
                                            Log.d("SHAREDMENU", e.getMessage());
                                        }
                                    }
                                });
                            } else {
                                addUserToShareList(user);
                            }
                        }
                    });
                }
            }
        });
    }

    private void addUserToShareList(final ParseUser user) {
        ParseQuery<ParseObject> bookingQuery = ParseQuery.getQuery("UserBooking");
        bookingQuery.getInBackground(bookingId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject booking, ParseException e) {
                if (e == null) {
                    ParseRelation peopleComing = booking.getRelation("PeopleComing");
                    peopleComing.add(user);
                    booking.saveInBackground();
                } else {
                    Log.d("SHAREDMENU", e.getMessage());
                }
            }
        });
    }

    private void getHotelMenuItem(String hotelId) {
        ParseQuery<ParseObject> hotelItemQuery = ParseQuery.getQuery("HotelMenu");
        ParseObject hotel = new ParseObject("Hotel");
        hotel.setObjectId(hotelId);
        hotelItemQuery.whereEqualTo("Hotel", hotel);


        hotelItemQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> hotelItemList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < hotelItemList.size(); ++i) {
                        hotelItemAdapter.insert(hotelItemList.get(i), hotelItemAdapter.getCount());
                    }

                    hotelItemAdapter.notifyDataSetChanged();
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shared_menu, menu);
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
