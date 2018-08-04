package afeka.com.doggysitter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    public final String fname = Objects.requireNonNull(auth.getCurrentUser()).getDisplayName() + "/profilePhoto.png";
    final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference = firebaseStorage.getReference(fname);
    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Objects.requireNonNull(auth.getCurrentUser().getDisplayName()));
    private EditText dogName;
    private EditText dogSpecie;
    private EditText dogAge;
    private ImageView choosePhoto;
   // private Button resetValues;

    private FileOutputStream outputStream;


    private String dName;
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
        Button saveToDB = findViewById(R.id.save_btn);
       // resetValues = findViewById(R.id.reset_btn);

       dPhoto = BitmapFactory.decodeFile(MainActivity.PHOTO_FILE_LOCATION);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dogName.setText(dataSnapshot.child("Dog Name").getValue(String.class));
                dogSpecie.setText(dataSnapshot.child("Dog Specie").getValue(String.class));
                dogAge.setText(String.valueOf(dataSnapshot.child("Dog Age").getValue(Integer.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Bitmap bitmap = BitmapFactory.decodeFile(MainActivity.PHOTO_FILE_LOCATION);
        choosePhoto.setImageBitmap(bitmap);


        saveToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty(dogName) || isEmpty(dogSpecie) || isEmpty(dogAge)) {
                    String str = "You can't have empty fields!!!";
                    Toast.makeText(ProfileActivity.this, str, Toast.LENGTH_SHORT).show();
                } else {
                    dName = ((EditText) findViewById(R.id.dog_name_text)).getText().toString();
                    dAge = Integer.parseInt(((EditText) findViewById(R.id.age_txt)).getText().toString());
                    dSpecie = ((EditText) findViewById(R.id.specie_text)).getText().toString();
                    baos = new ByteArrayOutputStream();
                    dPhoto.compress(Bitmap.CompressFormat.PNG,100,baos);
                    byte[] data = baos.toByteArray();

                    mDatabase.child("Dog Name").setValue(dName);
                    mDatabase.child("Dog Age").setValue(dAge);
                    mDatabase.child("Dog Specie").setValue(dSpecie);

                    UploadTask uploadTask = storageReference.putBytes(data);

                    try {
                        File a = new File(MainActivity.PHOTO_FILE_LOCATION);
                        boolean mkdirs = a.getParentFile().mkdirs();
                        boolean newFile = a.createNewFile();
                        outputStream = new FileOutputStream(a,false);
                        outputStream.write(data);
                        outputStream.close();
                    } catch (FileNotFoundException e){
                        e.fillInStackTrace();
                        Log.d("Debug",e.getMessage());
                    }
                    catch (IOException e){
                        Log.d("Debug",e.getMessage());
                    }


                    Toast.makeText(ProfileActivity.this,"Dog name: " + dName + ", specie: " + dSpecie + ", age: " + dAge + " saved!!!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this,ChooseCategory.class);
                    startActivity(intent);
                    finish();



                }
            }
        });

        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nammu.init(ProfileActivity.this);
                Nammu.askForPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE,permissionStorageCallback);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    final PermissionCallback permissionStorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            Toast.makeText(ProfileActivity.this,"Permission Granted",Toast.LENGTH_SHORT).show();
            EasyImage.openGallery(ProfileActivity.this, 1);
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
            public void onImagesPicked(@NonNull List<File> imagesFiles, EasyImage.ImageSource source, int type) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imagesFiles.get(0).getAbsolutePath());
                choosePhoto.setImageBitmap(myBitmap);
                dPhoto = myBitmap;
            }
        });
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

}
