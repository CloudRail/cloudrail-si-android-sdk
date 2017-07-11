package com.cloudrail.unifiedpayment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cloudrail.si.interfaces.Payment;
import com.cloudrail.si.services.PayPal;
import com.cloudrail.si.services.Stripe;
import com.cloudrail.si.types.Address;
import com.cloudrail.si.types.Charge;
import com.cloudrail.si.types.CreditCard;
import com.cloudrail.si.types.Refund;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


//* {@link ChargeViewer.OnFragmentInteractionListener} interface
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ChargeViewer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChargeViewer extends Fragment {
    // the fragment initialization parameters
    private static final String ARG_SERVICE = "service";

    private String mServiceString;
    private Payment service;

    private Context context;
    private ListView listView;
    private ProgressBar spinner;
    private View selectedItem;
    private int selectedItemPosition;

    private ChargeAdapter listAdapter;

    public ChargeViewer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param serviceStr the name of the bucket cloud storage service
     * @return A new instance of fragment ChargeViewer.
     */
    public static ChargeViewer newInstance(String serviceStr) {
        ChargeViewer fragment = new ChargeViewer();
        Bundle args = new Bundle();
        args.putString(ARG_SERVICE, serviceStr);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mServiceString = getArguments().getString(ARG_SERVICE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_charge_viewer, container, false);
        this.listView = (ListView) v.findViewById(R.id.chargeListView);
        this.spinner = (ProgressBar) v.findViewById(R.id.spinner);

        switch (mServiceString) {
            case "paypal": {
                service = new PayPal(context, true, MainActivity.PAYPAL_CLIENT_IDENTIFIER, MainActivity.PAYPAL_CLIENT_SECRET);
                break;
            }
            case "stripe": {
                service = new Stripe(context, MainActivity.STRIPE_SECRET_KEY);
                break;
            }
        }
        refreshList();

        this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                selectedItem = view;
                final PopupMenu popupMenu = new PopupMenu(context, view);
                MenuInflater menuInflater = ((Activity)context).getMenuInflater();
                menuInflater.inflate(R.menu.selected_item_bar, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_refund_fully: {
                                refundCharge(listAdapter.getItem(position));
                                refreshList();
                                return true;
                            }
                            case R.id.action_refund_partially: {
                                refundPartially(listAdapter.getItem(position));
                                refreshList();
                                return true;
                            }
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.show();

                return true;
            }
        });

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View v, int position, long id) {
                final Charge charge = listAdapter.getItem(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final List<Refund> refunds = service.getRefundsForCharge(charge.getId());

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String[] refundStrings = new String[refunds.size()];
                                int i = 0;
                                for (Refund r : refunds) {
                                    refundStrings[i] = formatAmount(r.getAmount()) + r.getCurrency() + "   (" + formatTime(r.getCreated()) + ")";
                                    i++;
                                }
                                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                                View layout = inflater.inflate(R.layout.display_charge, (ViewGroup) ((Activity)context).findViewById(R.id.display_charge_root));
                                enterContents(layout, charge, refunds);

                                PopupWindow pw = new PopupWindow(layout, 900, 1000, true);
                                pw.showAtLocation(v, Gravity.CENTER, 0, 0);

                                ListView lv = layout.findViewById(R.id.refunds_list_view);
                                lv.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, refundStrings));
                            }
                        });
                    }
                }).start();
            }
        });

        ((TextView)v.findViewById(R.id.text2)).setText(mServiceString);
        return v;
    }

    private void enterContents(View layout, Charge charge, List<Refund> refunds) {
        ((TextView)layout.findViewById(R.id.list_item_id_2)).setText(charge.getId());
        ((TextView)layout.findViewById(R.id.list_item_price_2)).setText(formatAmount(charge.getAmount()) + charge.getCurrency());
        ((TextView)layout.findViewById(R.id.list_item_date_2)).setText(formatTime(charge.getCreated()));
        ((TextView)layout.findViewById(R.id.payer_name)).setText("Payer: " + charge.getSource().getFirstName() + "  " + charge.getSource().getLastName());
        ((TextView)layout.findViewById(R.id.card_number)).setText("Credit Card: " + charge.getSource().getNumber());
        TextView tvStatus = layout.findViewById(R.id.list_item_status_2);
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
        if (refunds.size() > 0) {
            ((TextView)layout.findViewById(R.id.refundsTextView)).setText("Refunds:");
        } else {
            ((TextView)layout.findViewById(R.id.refundsTextView)).setText("Refunds: none");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh: {
                refreshList();
                break;
            }
            case R.id.action_create_charge: {
                createCharge();
                break;
            }
        }
        return true;
    }

    private void refundCharge(final Charge charge) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                service.refundCharge(charge.getId());
            }
        }).start();
    }

    private void refundPartially(final Charge charge) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Refund Amount:");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final int refundAmount = Integer.parseInt(input.getText().toString());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        service.partiallyRefundCharge(charge.getId(), (long)refundAmount);
                    }
                }).start();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void createCharge() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        final View v = inflater.inflate(R.layout.layout_create_new_charge, null);
        dialogBuilder.setView(v);

        dialogBuilder.setTitle("Add new Charge")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }})
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final Long amount = Long.parseLong(((EditText) v.findViewById(R.id.amount)).getText().toString());
                        final String currency = ((EditText) v.findViewById(R.id.currency)).getText().toString();

                        String cvc = ((EditText) v.findViewById(R.id.cvc)).getText().toString();
                        Long expire_month = Long.parseLong(((EditText) v.findViewById(R.id.expire_month)).getText().toString());
                        Long expire_year = Long.parseLong(((EditText) v.findViewById(R.id.expire_year)).getText().toString());
                        String number = ((EditText) v.findViewById(R.id.number)).getText().toString();
                        String type = ((EditText) v.findViewById(R.id.type)).getText().toString();
                        String firstName = ((EditText) v.findViewById(R.id.firstName)).getText().toString();
                        String lastName = ((EditText) v.findViewById(R.id.lastName)).getText().toString();

                        Address address = new Address();
                        address.setLine1(((EditText) v.findViewById(R.id.line1)).getText().toString());
                        address.setLine2(((EditText) v.findViewById(R.id.line2)).getText().toString());
                        address.setPostalCode(((EditText) v.findViewById(R.id.postalCode)).getText().toString());
                        address.setCountry(((EditText) v.findViewById(R.id.country)).getText().toString());
                        address.setCity(((EditText) v.findViewById(R.id.city)).getText().toString());
                        address.setState(((EditText) v.findViewById(R.id.state)).getText().toString());

                        final CreditCard creditCard = new CreditCard(cvc, expire_month, expire_year, number, type, firstName, lastName, address);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                service.createCharge(amount, currency, creditCard);
                                refreshList();
                            }
                        }).start();
                    }
                });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private String formatTime(Long seconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY HH:MM");
        Date resultdate = new Date(seconds * 1000);

        return sdf.format(resultdate);
    }

    private String formatAmount(Long amountL) {
        Double amount = amountL / 100.0;
        float epsilon = 0.004f; // 4 tenths of a cent
        if (Math.abs(Math.round(amount) - amount) < epsilon) {
            return String.format("%10.0f", amount); // sdb
        } else {
            return String.format("%10.2f", amount); // dj_segfault
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_charges, menu);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
            this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    private void refreshList() {
        startSpinner();
        new Thread(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                long oneYearAgo = now - 1000*3600*24*365;
                final List<Charge> items = service.listCharges(oneYearAgo, now, null);

                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listAdapter = new ChargeAdapter(context, R.layout.list_item, items);
                        listView.setAdapter(listAdapter);
                        stopSpinner();
                    }
                });
            }
        }).start();
    }

    private void startSpinner() {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.setVisibility(View.VISIBLE);
            }
        });
    }

    private void stopSpinner() {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.setVisibility(View.GONE);
            }
        });
    }
}
