package com.experiences.projects.booktable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by thinkplaces on 09/09/15.
 */
public class HotelMenuListAdapter extends ArrayAdapter<ParseObject> {

    public HotelMenuListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public HotelMenuListAdapter(Context context, int resource, List<ParseObject> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.menuitem, null);
        }

        final ParseObject p = (ParseObject) getItem(position);

        if (p != null) {
            final TextView tv_item_name = (TextView) v.findViewById(R.id.tv_item_name);
            tv_item_name.setText(p.getString("Item"));

            final TextView tv_item_cost = (TextView) v.findViewById(R.id.tv_item_cost);
            tv_item_cost.setText(p.get("Price").toString());

            final TextView tv_total_cost = (TextView) v.findViewById(R.id.tv_item_total_cost);
            tv_total_cost.setText("0");

            final TextView tv_item_qty = (TextView) v.findViewById(R.id.tv_item_qty);

            ImageButton btn_add_item = (ImageButton) v.findViewById(R.id.ibtn_add_item);
            btn_add_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cqty = tv_item_qty.getText().toString();
                    int qty = Integer.parseInt(cqty);
                    tv_item_qty.setText(String.valueOf(qty + 1));
                    int cost = Integer.parseInt(p.get("Price").toString());
                    tv_total_cost.setText(String.valueOf((qty + 1) * cost));

                }
            });

            ImageButton btn_remove_item = (ImageButton) v.findViewById(R.id.ibtn_remove_item);
            btn_remove_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cqty = tv_item_qty.getText().toString();
                    int qty = Integer.parseInt(cqty);
                    if (qty > 0) {
                        tv_item_qty.setText(String.valueOf(qty - 1));
                        int cost = Integer.parseInt(p.get("Price").toString());
                        tv_total_cost.setText(String.valueOf((qty-1)*cost));
                    }
                }
            });
        }

        return v;
    }

}