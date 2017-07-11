package com.cloudrail.unifiedpayment;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cloudrail.si.types.Charge;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by cloudrail on 10/07/17.
 */

public class ChargeAdapter extends ArrayAdapter<Charge> {
    private List<Charge> data;

    public ChargeAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Charge> objects) {
        super(context, resource, objects);
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item, null);
        }

        final Charge charge = this.data.get(position);

        if(charge != null) {
            TextView tvId = (TextView) v.findViewById(R.id.list_item_id);
            TextView tvPrice = (TextView) v.findViewById(R.id.list_item_price);
            TextView tvDate = (TextView) v.findViewById(R.id.list_item_date);
            TextView tvStatus = (TextView) v.findViewById(R.id.list_item_status);
            TextView tvRefunded = (TextView) v.findViewById(R.id.list_item_refunded);

            tvId.setText(charge.getId());
            tvPrice.setText(formatAmount(charge.getAmount()) + " " + charge.getCurrency());
            tvDate.setText(formatTime(charge.getCreated()));
            tvStatus.setText(charge.getStatus());

            switch(charge.getStatus()) {
                case "succeeded": {
                    tvStatus.setTextColor(Color.GREEN);
                    break;
                }
                case "pending": {
                    tvStatus.setTextColor(Color.YELLOW);
                    break;
                }
                case "failed": {
                    tvStatus.setTextColor(Color.RED);
                    break;
                }
            }


            if(charge.isRefunded()) {
                tvRefunded.setText("refunded");
                tvRefunded.setVisibility(View.VISIBLE);
            } else {
                tvRefunded.setVisibility(View.GONE);
            }
        }
        return v;
    }

    private String formatAmount(Long amountL) { // change to currency format
        Double amount = amountL / 100.0;
        float epsilon = 0.004f;
        if (Math.abs(Math.round(amount) - amount) < epsilon) {
            return String.format("%10.0f", amount);
        } else {
            return String.format("%10.2f", amount);
        }
    }

    private String formatTime(Long seconds) {   // change to human-readable format
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY HH:MM");
        Date resultdate = new Date(seconds * 1000);

        return sdf.format(resultdate);
    }
}
