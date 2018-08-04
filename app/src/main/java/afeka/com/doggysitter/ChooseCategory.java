package afeka.com.doggysitter;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class ChooseCategory extends AppCompatActivity {
    final FirebaseAuth auth = FirebaseAuth.getInstance();
    public final String fname = auth.getCurrentUser().getDisplayName() + "/profilePhoto.png";
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference(fname);
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    Button logOut;
    Button profile;
    TextView welcomeMessage;
    Button park;
    String dName;
    String dSpecie;
    Integer dAge;
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

        Intent intent = getIntent();
        if(intent.getStringExtra("filePath") != null){
            Bitmap bitmap = BitmapFactory.decodeFile(intent.getStringExtra("filePath"));
            profileImage.setImageBitmap(bitmap);
        }

        welcomeMessage.append(auth.getCurrentUser().getDisplayName());

        try {
            final File localFile = File.createTempFile("images", "png");
            storageReference.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    profileImage.setImageBitmap(bitmap);
                }
            });
        } catch (IOException e){

        }
        mDatabase.child(auth.getCurrentUser().getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dName = dataSnapshot.child("Dog Name").getValue(String.class);
                dSpecie = dataSnapshot.child("Dog Specie").getValue(String.class);
                dAge = dataSnapshot.child("Dog Age").getValue(Integer.class);
                Toast.makeText(ChooseCategory.this,"Welcome owner of dog name: " + dName + ", specie: " + dSpecie + ", age: " + dAge,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



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
