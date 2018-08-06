package afeka.com.doggysitter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class DoggysitterActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLoadedCallback {

    private GoogleMap mMap;
    private Button snapShot;
    private static final int DELAY_TIME_IN_MILLI = 3000;
    GoogleMap.SnapshotReadyCallback callback;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doggysitter);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final ArrayList<Park> parks = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("/Parks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot data : dataSnapshot.getChildren()){
                    Park tmp = new Park();
                    tmp.setName(data.getKey());
                    tmp.setLocation(new GeoLocation(data.child("0").getValue(Double.class),data.child("1").getValue(Double.class)));
                    parks.add(tmp);
                }

                    int idx = 0;

                    takeSnapshot(parks,idx);





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void takeSnapshot(final ArrayList<Park> parks, final int idx) {
        if (idx == parks.size())
            return;
        else {
            final Park park = parks.get(idx);
             callback = new GoogleMap.SnapshotReadyCallback() {
                Bitmap bitmap;
                @Override
                public void onSnapshotReady(Bitmap snapshot) {
                    bitmap = Bitmap.createBitmap(snapshot, 0, snapshot.getHeight() / 3, snapshot.getWidth(), snapshot.getHeight() / 3);
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        FirebaseStorage.getInstance().getReference("Snapshots/" + park.getName()).putBytes(baos.toByteArray());
                        takeSnapshot(parks,idx+1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };

            mMap.addMarker(new MarkerOptions().position(new LatLng(park.getLocation().latitude, park.getLocation().longitude)).title(park.getName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(park.getLocation().latitude, park.getLocation().longitude), 17.0f));
            mMap.setOnMapLoadedCallback(this);


        }


    }

    @Override
    public void onMapLoaded() {
        mMap.snapshot(callback);
    }
}