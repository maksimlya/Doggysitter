package afeka.com.doggysitter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import afeka.com.doggysitter.ListViews.Dog;
import afeka.com.doggysitter.ListViews.DogAdapter;
import afeka.com.doggysitter.ListViews.Park;

public class DogsListActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ArrayList<Dog> dogsInPark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dogs_list);
        ImageView parkImage = findViewById(R.id.park_img);
        Button navigateToPark = findViewById(R.id.navigate_btn);
        ListView dogsList = findViewById(R.id.dogs_list);
        dogsInPark = new ArrayList<>();

        final DogAdapter adapter = new DogAdapter(this,dogsInPark);
        dogsList.setAdapter(adapter);

        Intent intent = getIntent();

        final String pName = intent.getStringExtra("Name");
        mDatabase =  FirebaseDatabase.getInstance().getReference("/Parks/" + pName);
        String path = getFilesDir() + "/parkPhotos/" + pName;
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        parkImage.setImageBitmap(bitmap);
        final Park newPark = new Park();
        final Park oldPark = new Park();


        FirebaseDatabase.getInstance().getReference("Users/" + Objects.requireNonNull(auth.getCurrentUser()).getDisplayName()).child("Park").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String old = dataSnapshot.child("name").getValue(String.class);
                if(old != null)
                    oldPark.setName(old);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("Visitors").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final Dog temp = new Dog();
                FirebaseDatabase.getInstance().getReference("/Users/" + dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        temp.setName(dataSnapshot.child("Dog Name").getValue(String.class));
                        String path = getFilesDir() + "/dogsPhotos/" + dataSnapshot.getKey() + "/photo";
                        final File dogPhoto = new File(path);

                        if (!dogPhoto.exists()) {                                              // If profile photo for current account does not exist on the phone memory
                            dogPhoto.getParentFile().mkdirs();

                            try {
                                dogPhoto.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            FirebaseStorage.getInstance().getReference("/" + dataSnapshot.getKey() + "/profilePhoto.png").getFile(dogPhoto).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                    temp.setPhoto(dogPhoto.getAbsolutePath());
                                    dogsInPark.add(temp);
                                    adapter.notifyDataSetChanged();
                                }
                            });

                        } else {
                            temp.setPhoto(dogPhoto.getAbsolutePath());
                            dogsInPark.add(temp);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newPark.setName(dataSnapshot.getKey());
                GeoLocation parkLocation = new GeoLocation(Objects.requireNonNull(dataSnapshot.child("0").getValue(Double.class)),Objects.requireNonNull(dataSnapshot.child("1").getValue(Double.class)));
                newPark.setLocation(parkLocation);
                if(dataSnapshot.child("dogsAmount").getValue(Integer.class) == null){
                    mDatabase.child("dogsAmount").setValue(0);
                    newPark.setDogsAmount(0);

                }
                else
                    newPark.setDogsAmount(Objects.requireNonNull(dataSnapshot.child("dogsAmount").getValue(Integer.class)));


                FirebaseDatabase.getInstance().getReference("/Parks/" + newPark.getName()).child("dogsAmount").onDisconnect().setValue(dataSnapshot.child("Visitors").getChildrenCount());

                 }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        navigateToPark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(oldPark.getName() != null) {
                    FirebaseDatabase.getInstance().getReference("/Parks/" + oldPark.getName()).child("Visitors").child(Objects.requireNonNull(auth.getCurrentUser().getDisplayName())).removeValue();
                }
                FirebaseDatabase.getInstance().getReference("/Parks/" + newPark.getName()).child("Visitors").child(auth.getCurrentUser().getDisplayName()).onDisconnect().removeValue();
                FirebaseDatabase.getInstance().getReference("/Users/" + auth.getCurrentUser().getDisplayName()).child("Park").setValue(newPark);
                HashMap<String,Object> newVisitor = new HashMap<>();
                newVisitor.put(auth.getCurrentUser().getDisplayName(),newPark.getDistanceToPark());
                FirebaseDatabase.getInstance().getReference("/Parks/" + newPark.getName()).child("Visitors").updateChildren(newVisitor);



                FirebaseDatabase.getInstance().getReference("/Parks/" + newPark.getName()).child("Visitors").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            FirebaseDatabase.getInstance().getReference("/Parks/" + newPark.getName()).child("dogsAmount").setValue(dataSnapshot.getChildrenCount());

                            Intent intent = new Intent(DogsListActivity.this,Navigation.class);
                            intent.putExtra("Name",newPark.getName());
                            startActivity(intent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


            }
        });





    }

}
