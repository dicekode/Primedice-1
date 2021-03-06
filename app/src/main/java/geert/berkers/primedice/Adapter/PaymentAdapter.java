package geert.berkers.primedice.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import geert.berkers.primedice.Data.Payment;
import geert.berkers.primedice.Data.URLS;
import geert.berkers.primedice.R;

/**
 * Primedice Application Created by Geert on 20-2-2016.
 */
public class PaymentAdapter extends BaseAdapter {
    private final Context context;

    private final ArrayList<Payment> paymentsList;

    // Create PaymentAdapter
    public PaymentAdapter(Context context, ArrayList<Payment> paymentsList) {
        this.context = context;
        this.paymentsList = paymentsList;
    }

    @Override
    public int getCount() {
        return paymentsList.size();
    }

    @Override
    public Payment getItem(int position) {
        return paymentsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.payment_paymentslist_layout, parent, false);
        } else {
            row = convertView;
        }

        final TextView txid = (TextView) row.findViewById(R.id.txtTXID);
        final TextView amount = (TextView) row.findViewById(R.id.txtAmount);
        final TextView date = (TextView) row.findViewById(R.id.txtDate);


        final String txidString = paymentsList.get(position).getTxid().replace("-0","");

        txid.setText(txidString);
        amount.setText(paymentsList.get(position).getAmount());
        date.setText(paymentsList.get(position).getTimestamp());

        //row.setBackgroundResource(R.color.white);

        // Add onclick function. This opens the player information
        txid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLS.BLOCKCHAIN_TX + txidString));
                v.getContext().startActivity(browserIntent);
            }
        });

        return row;
    }
}