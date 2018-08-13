package afeka.com.doggysitter;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import afeka.com.doggysitter.ListViews.DoggysitterOffer;
import afeka.com.doggysitter.ListViews.DoggysitterOfferAdapter;

public class AskDoggysitterService extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Doggysitters");
    private GeoFire geoFire;
    private DoggysitterOfferAdapter adapter;
    private GeoLocation myLocation;
    private GeoQuery geoQuery;
    private EditText filterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_doggysitter_service);
        Button filterBtn = findViewById(R.id.filter_btn);
        filterView = findViewById(R.id.filter_text_view);
        ListView offersList = findViewById(R.id.offers_list);
        ArrayList<DoggysitterOffer> offers = new ArrayList<>();
        adapter = new DoggysitterOfferAdapter(this, offers);
        offersList.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference("Geofire/Doggysitters/" + auth.getCurrentUser().getDisplayName() + "/l").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double lat = dataSnapshot.child("0").getValue(Double.class);
                Double lon = dataSnapshot.child("1").getValue(Double.class);
                if(lat != null && lon != null)
                    myLocation = new GeoLocation(lat,lon);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference("/Geofire/Doggysitters"));
            filterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.clear();
                    geoQuery = geoFire.queryAtLocation(myLocation,Double.parseDouble(filterView.getText().toString()));
                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, final GeoLocation location) {
                            if(!key.equals(auth.getCurrentUser().getDisplayName()))
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot doggySitter : dataSnapshot.getChildren()){
                                            if(Objects.equals(doggySitter.getKey(), auth.getCurrentUser().getDisplayName()))
                                                continue;
                                            for(DataSnapshot month : doggySitter.getChildren()){
                                                for(DataSnapshot day : month.getChildren()){
                                                    for (DataSnapshot instance : day.getChildren()){
                                                        final DoggysitterOffer temp = new DoggysitterOffer();
                                                        temp.setName(doggySitter.getKey());
                                                        temp.setMonth(Integer.parseInt(month.getKey()));
                                                        temp.setDay(Integer.parseInt(day.getKey()));
                                                        temp.setStartHour(Integer.parseInt(instance.getKey()));
                                                        temp.setEndHour(Integer.parseInt(instance.getValue(String.class)));
                                                        FirebaseDatabase.getInstance().getReference("/Users/" + temp.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                temp.setAddress(dataSnapshot.child("Address").getValue(String.class));
                                                                temp.setPhoneNumber(dataSnapshot.child("Phone Number").getValue(String.class));
                                                                temp.setTargetLocation(location);
                                                                temp.setHomeLocation(myLocation);
                                                                adapter.addItem(temp);
                                                                adapter.notifyDataSetChanged();

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
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
            });










    }
}
