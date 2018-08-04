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


public class ChooseCategory extends AppCompatActivity {
    final FirebaseAuth auth = FirebaseAuth.getInstance();
    public final String fname = auth.getCurrentUser().getDisplayName() + "/profilePhoto.png";
    Button logOut;
    Button profile;
    TextView welcomeMessage;
    Button park;
    ImageView profileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        profileImage = findViewById(R.id.profile_image);
        logOut = findViewById(R.id.log_out_btn);
        park = findViewById(R.id.park_btn);
        profile = findViewById(R.id.profile_btn);
        welcomeMessage = findViewById(R.id.welcome_view);
        Bitmap bitmap = BitmapFactory.decodeFile(MainActivity.PHOTO_FILE_LOCATION);
        profileImage.setImageBitmap(bitmap);

        welcomeMessage.append(auth.getCurrentUser().getDisplayName());


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
