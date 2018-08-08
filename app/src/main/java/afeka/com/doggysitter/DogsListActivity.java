package afeka.com.doggysitter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.firebase.geofire.GeoFire;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Objects;

public class DogsListActivity extends AppCompatActivity {
    private String fname;
    private StorageReference firebaseStorage;

    private ImageView parkImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dogs_list);
        parkImage = findViewById(R.id.park_img);

        Intent intent = getIntent();

        String pName = intent.getStringExtra("Name");
        String path = getFilesDir() + "/parkPhotos/" + pName;
        File a = new File(path);
        Bitmap bitmap;
        if(a.exists()) {
            bitmap = BitmapFactory.decodeFile(path);
            parkImage.setImageBitmap(bitmap);
        }



    }
}
