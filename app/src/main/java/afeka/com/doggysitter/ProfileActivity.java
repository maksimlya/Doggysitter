package afeka.com.doggysitter;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class ProfileActivity extends AppCompatActivity {
    final FirebaseAuth auth = FirebaseAuth.getInstance();
    public final String fname = Objects.requireNonNull(auth.getCurrentUser()).getDisplayName() + "/profilePhoto.png";                           // Path to profile photo
    final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();                                                                      // Firebase storage reference
    private StorageReference storageReference = firebaseStorage.getReference(fname);
    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + Objects.requireNonNull(auth.getCurrentUser().getDisplayName()));
    private EditText dogName;
    private EditText dogSpecie;
    private EditText dogAge;
    private ImageView choosePhoto;
    private Button addressBtn;
    private EditText phoneNumber;
    int PLACE_PICKER_REQUEST = 1;

    private FileOutputStream outputStream;                  // Output stream to download profile photo to phone's local memory

    private String dName;
    private  String address;
    private String phone;
    private String dSpecie;
    private int dAge;
    private Bitmap dPhoto;
    ByteArrayOutputStream baos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView nameView = findViewById(R.id.owner_name_view);
        nameView.append(Objects.requireNonNull(auth.getCurrentUser()).getDisplayName());

        dogName = findViewById(R.id.dog_name_text);
        dogSpecie = findViewById(R.id.specie_text);
        dogAge = findViewById(R.id.age_txt);
        choosePhoto = findViewById(R.id.photo_chooser);
        addressBtn = findViewById(R.id.address_btn);
        phoneNumber = findViewById(R.id.phone_input);
        Button saveToDB = findViewById(R.id.save_btn);

       dPhoto = BitmapFactory.decodeFile(MainActivity.PHOTO_FILE_LOCATION);             // Decode image from local storage

        mDatabase.addValueEventListener(new ValueEventListener() {                          // Get current profile values from firebase
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dogName.setText(dataSnapshot.child("Dog Name").getValue(String.class));                         // Name
                dogSpecie.setText(dataSnapshot.child("Dog Specie").getValue(String.class));                     // Specie
                dogAge.setText(String.valueOf(dataSnapshot.child("Dog Age").getValue(Integer.class)));          // Age
                phoneNumber.setText(dataSnapshot.child("Phone Number").getValue(String.class));                 // Phone Number
                addressBtn.setText(dataSnapshot.child("Address").getValue(String.class));                       // Address
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Bitmap bitmap = BitmapFactory.decodeFile(MainActivity.PHOTO_FILE_LOCATION);
        choosePhoto.setImageBitmap(dPhoto);                                                                     // Set profile photo from the decoded bitmap


        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();                        // Use Google Places API to find address
        try {
            startActivityForResult(builder.build(ProfileActivity.this), PLACE_PICKER_REQUEST);      // The result is taken care in 'Activity result' function
        }   catch (GooglePlayServicesRepairableException e){
            e.getConnectionStatusCode();
        } catch (GooglePlayServicesNotAvailableException e){
            e.getMessage();
        }

            }
        });

        saveToDB.setOnClickListener(new View.OnClickListener() {                                            // Save updated profile values to Firebase database
            @Override
            public void onClick(View view) {
                if (isEmpty(dogName) || isEmpty(dogSpecie) || isEmpty(dogAge) || isEmpty(phoneNumber) || addressBtn.getText().equals("Choose Address")) {       // Checks for empty fields in profile form
                    String str = "You can't have empty fields!!!";
                    Toast.makeText(ProfileActivity.this, str, Toast.LENGTH_SHORT).show();
                } else {
                    phone = ((EditText) findViewById(R.id.phone_input)).getText().toString();
                    address = addressBtn.getText().toString();
                    dName = ((EditText) findViewById(R.id.dog_name_text)).getText().toString();
                    dAge = Integer.parseInt(((EditText) findViewById(R.id.age_txt)).getText().toString());
                    dSpecie = ((EditText) findViewById(R.id.specie_text)).getText().toString();
                    baos = new ByteArrayOutputStream();
                    dPhoto.compress(Bitmap.CompressFormat.PNG,100,baos);
                    byte[] data = baos.toByteArray();

                    mDatabase.child("Dog Name").setValue(dName);                                                    // Parse profile's values and store them in firebase database, and the photo is stored in firebase storage.
                    mDatabase.child("Dog Age").setValue(dAge);
                    mDatabase.child("Dog Specie").setValue(dSpecie);
                    mDatabase.child("Address").setValue(address);
                    mDatabase.child("Phone Number").setValue(phone);

                    storageReference.putBytes(data);                        // Uploads the photo to firebase storage.

                    try {
                        File a = new File(MainActivity.PHOTO_FILE_LOCATION);

                        a.getParentFile().mkdirs();
                        a.createNewFile();

                            outputStream = new FileOutputStream(a, false);
                            outputStream.write(data);                                           // Store the new profile photo in phone memory.
                            outputStream.close();

                    } catch (FileNotFoundException e){
                        e.fillInStackTrace();
                        Log.d("Debug",e.getMessage());
                    }
                    catch (IOException e){
                        Log.d("Debug",e.getMessage());
                    }


                    Toast.makeText(ProfileActivity.this,"Dog name: " + dName + ", specie: " + dSpecie + ", age: " + dAge + " saved!!!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this,ChooseCategory.class);                                                                              // Show success message and go back to Categories activity.
                    startActivity(intent);
                    finish();



                }
            }
        });

        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                                   // Checks for storage permissions and go to photo chooser in permissionStorageCallback
                Nammu.init(ProfileActivity.this);
                Nammu.askForPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE,permissionStorageCallback);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {                   // 'Nammu' = permissions helper
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    final PermissionCallback permissionStorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            Toast.makeText(ProfileActivity.this,"Permission Granted",Toast.LENGTH_SHORT).show();
            EasyImage.openGallery(ProfileActivity.this, 1);                                                 // EasyImage = image picker helper
        }

        @Override
        public void permissionRefused(){
            Toast.makeText(ProfileActivity.this,"Permission Refused",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagesPicked(@NonNull List<File> imagesFiles, EasyImage.ImageSource source, int type) {           // Set the picked image.
                Bitmap myBitmap = BitmapFactory.decodeFile(imagesFiles.get(0).getAbsolutePath());
                choosePhoto.setImageBitmap(myBitmap);
                dPhoto = myBitmap;                                                                                          // Store it in bitmap
            }
        });

        if (requestCode == PLACE_PICKER_REQUEST) {                                                      // This part takes care for address picker from 'Google Places API'
            if (resultCode == RESULT_OK) {
                address = PlacePicker.getPlace(this,data).getName().toString();
                addressBtn.setText(address);
                LatLng coordinates = PlacePicker.getPlace(this,data).getLatLng();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Geofire/Doggysitters");
                GeoFire geoFire = new GeoFire(ref);
                String user = Objects.requireNonNull(auth.getCurrentUser()).getDisplayName();
                if(user != null)
                geoFire.setLocation(user, new GeoLocation(coordinates.latitude, coordinates.longitude), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {                       // Store in Geofire place for easy distance checks
                        Toast.makeText(ProfileActivity.this,"Success!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }



    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

}
