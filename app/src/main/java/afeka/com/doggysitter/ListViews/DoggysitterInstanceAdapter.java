package afeka.com.doggysitter.ListViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import afeka.com.doggysitter.R;

public class DoggysitterInstanceAdapter extends BaseAdapter {
    private HashSet<DoggysitterInstance> sources;
    private Context mContext;
    private ArrayList<DoggysitterInstance> mDataSource;
    private LayoutInflater mInflater;
    public DoggysitterInstanceAdapter(Context context, ArrayList<DoggysitterInstance> items){
        mContext = context;
        mDataSource = items;
        sources = new HashSet<>(items);
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public void addItem(DoggysitterInstance temp){
        if(sources.add(temp)){
            mDataSource.add(temp);
        }
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.instance_list_item, parent, false);

        TextView dateView =
                (TextView) rowView.findViewById(R.id.date_view);
        TextView hoursView =
                (TextView) rowView.findViewById(R.id.hours_view);

        DoggysitterInstance instance = (DoggysitterInstance) getItem(position);
        dateView.setText(instance.getDate());
        hoursView.setText(instance.getHours());

        return rowView;
    }

    public void sort(){
        Collections.sort(mDataSource);
    }
}








