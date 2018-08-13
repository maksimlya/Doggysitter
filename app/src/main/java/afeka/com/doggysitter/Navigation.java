package afeka.com.doggysitter;

import android.content.Intent;
import android.net.Uri;
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

import afeka.com.doggysitter.ListViews.Park;

public class Navigation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Intent intent = getIntent();
        String pName = intent.getStringExtra("Name");
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("/Parks/" + pName);
        final Park destPark = new Park();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                destPark.setName(dataSnapshot.getKey());
                Double lattitude = dataSnapshot.child("0").getValue(Double.class);
                Double longitude = dataSnapshot.child("1").getValue(Double.class);
                if(lattitude != null && longitude != null)
                    destPark.setLocation(new GeoLocation(lattitude,longitude));
                Integer dogsAmount = dataSnapshot.child("dogsAmount").getValue(Integer.class);
                if(dogsAmount != null)
                    destPark.setDogsAmount(dogsAmount);
                Toast.makeText(Navigation.this,"Navigating to " + destPark.getName() + " park at Lat: " + destPark.getLocation().latitude + ", Lon: " + destPark.getLocation().longitude,Toast.LENGTH_SHORT).show();


                double lat = destPark.getLocation().latitude;

                double lng = destPark.getLocation().longitude;

                String format = "geo:0,0?q=" + lat + "," + lng + "( Location title)";

                Uri uri = Uri.parse(format);


                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
