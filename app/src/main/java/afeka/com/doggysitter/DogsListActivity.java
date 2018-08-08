package afeka.com.doggysitter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Objects;

public class DogsListActivity extends AppCompatActivity {
    private String fname;
    private StorageReference firebaseStorage;
    private DatabaseReference mDatabase;
    private Button navigateToPark;
    private ImageView parkImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dogs_list);
        parkImage = findViewById(R.id.park_img);
        navigateToPark = findViewById(R.id.navigate_btn);

        Intent intent = getIntent();

        final String pName = intent.getStringExtra("Name");
        mDatabase =  FirebaseDatabase.getInstance().getReference("/Parks/" + pName);
        String path = getFilesDir() + "/parkPhotos/" + pName;
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        parkImage.setImageBitmap(bitmap);
        final Park tmp = new Park();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tmp.setName(dataSnapshot.getKey());
                GeoLocation parkLocation = new GeoLocation(Objects.requireNonNull(dataSnapshot.child("0").getValue(Double.class)),Objects.requireNonNull(dataSnapshot.child("1").getValue(Double.class)));
                tmp.setLocation(parkLocation);
                if(dataSnapshot.child("dogAmount").getValue(Integer.class) == null){
                    mDatabase.child("dogAmount").setValue(0);
                    tmp.setDogsAmount(0);

                }
                else
                    tmp.setDogsAmount(Objects.requireNonNull(dataSnapshot.child("dogAmount").getValue(Integer.class)));
                Toast.makeText(DogsListActivity.this,"Park name: " + tmp.getName() + ", Coords: Lat: " + tmp.getLocation().latitude + ", Lon: " + tmp.getLocation().longitude + ", dogsAmount: " +tmp.getDogsAmount(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        navigateToPark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DogsListActivity.this,Navigation.class);
                intent.putExtra("Name",tmp.getName());
                startActivity(intent);
            }
        });




    }
}
