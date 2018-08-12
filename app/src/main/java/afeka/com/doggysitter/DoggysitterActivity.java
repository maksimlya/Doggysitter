package afeka.com.doggysitter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


public class DoggysitterActivity extends AppCompatActivity {
    private Button askService;
    private Button offerService;
    private ImageView dogPhoto;
    private TextView unameText;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doggysitter);

        unameText = findViewById(R.id.uname_text);
        dogPhoto = findViewById(R.id.photo_view);
        offerService = findViewById(R.id.offer_service);
        askService = findViewById(R.id.ask_service);

        offerService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoggysitterActivity.this,OfferDoggysitterService.class);
                startActivity(intent);
            }
        });

        unameText.append(auth.getCurrentUser().getDisplayName());
        Bitmap bm = BitmapFactory.decodeFile(MainActivity.PHOTO_FILE_LOCATION);
        dogPhoto.setImageBitmap(bm);



    }
}