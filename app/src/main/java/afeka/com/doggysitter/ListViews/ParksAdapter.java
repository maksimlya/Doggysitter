package afeka.com.doggysitter.ListViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import afeka.com.doggysitter.R;

public class ParksAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Park> mDataSource;

    public ParksAdapter(Context context, ArrayList<Park> items) {
        mDataSource = items;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public int getCount() {
        return mDataSource.size();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View rowView = mInflater.inflate(R.layout.parks_list_item, parent, false);

        TextView parkNameView =
                rowView.findViewById(R.id.park_name_view);
        TextView dogsAmountView =
                rowView.findViewById(R.id.dogs_amount_view);
        TextView distanceView =
                rowView.findViewById(R.id.distance_view);
        Park park = (Park) getItem(position);
        parkNameView.setText(park.getName());
        dogsAmountView.append(String.valueOf(park.getDogsAmount()));
        distanceView.append(String.valueOf(park.getDistanceToPark() + "  KM"));

        return rowView;
    }

    public void sort(){
        Collections.sort(mDataSource);
    }

}
