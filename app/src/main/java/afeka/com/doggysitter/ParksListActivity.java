package afeka.com.doggysitter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import afeka.com.doggysitter.ListViews.Park;
import afeka.com.doggysitter.ListViews.ParksAdapter;

public class ParksListActivity extends AppCompatActivity {
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Geofire/Parks");
    private StorageReference storage;
    private ArrayList<Park> parks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parks_list);

        ListView parksList = findViewById(R.id.parks_list);

        GeoFire geoFire = new GeoFire(ref);
        parks = new ArrayList<>();
        final ParksAdapter adapter = new ParksAdapter(this,parks);
        parksList.setAdapter(adapter);

        parksList.setClickable(true);
        parksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Park tmp = (Park) parent.getAdapter().getItem(position);
                final Intent intent = new Intent(ParksListActivity.this, DogsListActivity.class);
                intent.putExtra("Name", tmp.getName());
                final File localFile = new File(getFilesDir() + "/parkPhotos/" + tmp.getName());
                if (!localFile.exists()) {                                              // If profile photo for current account does not exist on the phone memory
                    localFile.getParentFile().mkdirs();

                    try {
                       localFile.createNewFile();
                        storage = FirebaseStorage.getInstance().getReference("/Snapshots/" + tmp.getName());
                            storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    storage.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                            startActivity(intent);
                                        }
                                    });
                                }
                            });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    startActivity(intent);
                }


            }
        });



        GeoLocation myLocation = new GeoLocation(MainActivity.MY_LOCATION.latitude,MainActivity.MY_LOCATION.longitude);

        GeoQuery geoQuery = geoFire.queryAtLocation(myLocation,10);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                final Park tmp = new Park();
                tmp.setName(key);
                tmp.setLocation(location);
                FirebaseDatabase.getInstance().getReference("/Parks/" + tmp.getName()).child("dogsAmount").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(Integer.class) == null)
                            tmp.setDogsAmount(0);
                        else {
                            Integer dogsAmount = dataSnapshot.getValue(Integer.class);
                            if(dogsAmount != null) {
                                tmp.setDogsAmount(dogsAmount);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
               // tmp.setDogsAmount(0);
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
