package afeka.com.doggysitter;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChooseCategory extends AppCompatActivity {
    private DatabaseReference mDatabase;
    Button logOut;
    Button profile;
    TextView welcomeMessage;
    Button park;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("/profiles");
        logOut = findViewById(R.id.log_out_btn);
        park = findViewById(R.id.park_btn);
        profile = findViewById(R.id.profile_btn);
        welcomeMessage = findViewById(R.id.welcome_view);
        welcomeMessage.append(auth.getCurrentUser().getDisplayName());

        Toast.makeText(this,"Welcome owner of dog name: " + mDatabase.child("Maks").child("Name"),Toast.LENGTH_LONG).show();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseCategory.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

        park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent intent = new Intent(ChooseCategory.this,MapActivity.class);
              //  startActivity(intent);
            }
        });


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                signOut();
            }
        });
    }

    void signOut() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
        startActivity(intent);
        finish();
    }


}
