package afeka.com.doggysitter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class ParksListActivity extends AppCompatActivity {
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Geofire");
    private GeoFire geoFire;
    private ArrayList<Park> parks;
    private ListView parksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parks_list);

        parksList = findViewById(R.id.parks_list);

        geoFire  = new GeoFire(ref);
        parks = new ArrayList<>();
        final ParksAdapter adapter = new ParksAdapter(this,parks);
        parksList.setAdapter(adapter);


        GeoLocation myLocation = new GeoLocation(MainActivity.MY_LOCATION.latitude,MainActivity.MY_LOCATION.longitude);

        GeoQuery geoQuery = geoFire.queryAtLocation(myLocation,10);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Park tmp = new Park();
                tmp.setName(key);
                tmp.setLocation(location);
                tmp.setDogsAmount(0);
                parks.add(tmp);
                adapter.sort();
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });






    }
}
