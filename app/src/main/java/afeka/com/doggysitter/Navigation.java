package afeka.com.doggysitter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Navigation extends AppCompatActivity {
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Intent intent = getIntent();
        String pName = intent.getStringExtra("Name");
        mDatabase = FirebaseDatabase.getInstance().getReference("/Parks/" + pName);
        final Park destPark = new Park();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                destPark.setName(dataSnapshot.getKey());
                destPark.setLocation(new GeoLocation(dataSnapshot.child("0").getValue(Double.class),dataSnapshot.child("1").getValue(Double.class)));
                destPark.setDogsAmount(dataSnapshot.child("dogAmount").getValue(Integer.class));
                Toast.makeText(Navigation.this,"Navigating to " + destPark.getName() + " park at Lat: " + destPark.getLocation().latitude + ", Lon: " + destPark.getLocation().longitude,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
