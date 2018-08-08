package afeka.com.doggysitter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ParksListActivity extends AppCompatActivity {
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Geofire");
    private StorageReference storage;
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

        parksList.setClickable(true);
        parksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Park tmp = (Park) parent.getAdapter().getItem(position);
                final Intent intent = new Intent(ParksListActivity.this, DogsListActivity.class);
                intent.putExtra("Name", "momo.jpg");
                final File localFile = new File(getFilesDir() + "/parkPhotos/" + "momo.jpg");
                if (!localFile.exists()) {                                              // If profile photo for current account does not exist on the phone memory
                    localFile.getParentFile().mkdirs();
                    try {
                        localFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                storage = FirebaseStorage.getInstance().getReference("/Snapshots/" + tmp.getName());
                storage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        startActivity(intent);
                    }
                });

            }
        });



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
