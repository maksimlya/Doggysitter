package afeka.com.doggysitter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class ChooseCategory extends AppCompatActivity {
    final FirebaseAuth auth = FirebaseAuth.getInstance();
    Button logOut;
    Button profile;
    Button doggysitter;
    TextView welcomeMessage;
    Button park;
    Button utilities;
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        utilities = findViewById(R.id.utilities_btn);
        profileImage = findViewById(R.id.profile_image);
        logOut = findViewById(R.id.log_out_btn);
        park = findViewById(R.id.park_btn);
        profile = findViewById(R.id.profile_btn);
        doggysitter = findViewById(R.id.doggysitter_btn);
        welcomeMessage = findViewById(R.id.welcome_view);
        Bitmap bitmap = BitmapFactory.decodeFile(MainActivity.PHOTO_FILE_LOCATION);
        profileImage.setImageBitmap(bitmap);


        utilities.setEnabled(false);


        welcomeMessage.append(Objects.requireNonNull(auth.getCurrentUser()).getDisplayName());


        doggysitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseCategory.this,DoggysitterActivity.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseCategory.this,ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseCategory.this,ParksListActivity.class);
                startActivity(intent);
            }
        });


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().goOffline();
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
