package afeka.com.doggysitter.ListViews;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

import afeka.com.doggysitter.R;

public class DoggysitterOfferAdapter extends BaseAdapter {
    private ArrayList<DoggysitterOffer> mDataSource;
    private LayoutInflater mInflater;
    private HashSet<DoggysitterOffer> offersSet;


    public DoggysitterOfferAdapter(Context context, ArrayList<DoggysitterOffer> items) {
        offersSet = new HashSet<>(items);
        mDataSource = items;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return mDataSource.size();
    }


    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        @SuppressLint("ViewHolder") View rowView = mInflater.inflate(R.layout.doggysitter_offer_item, parent, false);

        TextView dateView =
                rowView.findViewById(R.id.date_view);
        TextView hoursView =
                rowView.findViewById(R.id.hours_view);
        TextView nameView =
                rowView.findViewById(R.id.name_view);
        TextView addressView =
                rowView.findViewById(R.id.address_view);
        Button callButton = rowView.findViewById(R.id.call_button_view);
        TextView distanceView =
                rowView.findViewById(R.id.distance_view);


        final DoggysitterOffer offer = (DoggysitterOffer) getItem(position);
        dateView.setText(offer.getDate());
        hoursView.setText(offer.getHours());
        nameView.setText(offer.getName());
        addressView.setText(offer.getAddress());
        callButton.setText(String.format("Call %s", offer.getPhoneNumber()));
        distanceView.setText(String.valueOf(offer.getDistanceToDoggysitter() + " KM"));

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + offer.getPhoneNumber()));

                if (ActivityCompat.checkSelfPermission(parent.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                parent.getContext().startActivity(intent);
            }
        });

        return rowView;
    }

    public void addItem(DoggysitterOffer offer){
        if(offersSet.add(offer)){
            mDataSource.add(offer);
        }
    }

    public void clear(){
        offersSet = new HashSet<>();
        mDataSource = new ArrayList<>();
        this.notifyDataSetChanged();
    }


}









