package afeka.com.doggysitter.ListViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import afeka.com.doggysitter.ListViews.Dog;
import afeka.com.doggysitter.R;

public class DogAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Dog> mDataSource;

    public DogAdapter(Context context, ArrayList<Dog> items){
        mContext = context;
        mDataSource = items;
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


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.dogs_list_item, parent, false);

        TextView dogNameView =
                (TextView) rowView.findViewById(R.id.dog_name_view);
        ImageView dogPhotoView =
                (ImageView) rowView.findViewById(R.id.dog_photo_view);
        Dog dog = (Dog) getItem(position);
        dogNameView.setText(dog.getName());
        dogPhotoView.setImageBitmap(dog.getPhoto());

        return rowView;
    }
}

