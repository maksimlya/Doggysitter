package afeka.com.doggysitter;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class DoggysitterActivity extends AppCompatActivity {
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Geofire");
    private GeoFire geoFire;

    private EditText x;
    private EditText y;
    private Button send;
    private EditText result;
    ArrayList<String> parksInRadius = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doggysitter);
        x = findViewById(R.id.x_querry);
        y = findViewById(R.id.y_querry);
        send = findViewById(R.id.send_querry_btn);
        result = findViewById(R.id.result_text);

        geoFire  = new GeoFire(ref);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double a = Double.parseDouble(x.getText().toString());
                Double b = Double.parseDouble(y.getText().toString());


               result.append("");

                GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(a,b),0.3);

               geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                   @Override
                   public void onKeyEntered(String key, GeoLocation location) {
                       parksInRadius.add(key);
                       result.append(key+"\n");
                   }

                   @Override
                   public void onKeyExited(String key) {
                       Toast.makeText(DoggysitterActivity.this,"Someone Exited our area!!!",Toast.LENGTH_LONG).show();
                   }

                   @Override
                   public void onKeyMoved(String key, GeoLocation location) {
                       Toast.makeText(DoggysitterActivity.this,"Someone moved!!!!",Toast.LENGTH_LONG).show();
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
