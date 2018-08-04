package afeka.com.doggysitter;

import android.Manifest;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class ProfileActivity extends AppCompatActivity {
    final FirebaseAuth auth = FirebaseAuth.getInstance();
    final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private TextView nameView;
    private EditText dogName;
    private EditText dogSpecie;
    private EditText dogAge;
    private ImageView choosePhoto;
    private Button saveToDB;
    private Button resetValues;
    public final String fname = auth.getCurrentUser().getDisplayName() + "/profilePhoto.png";
    private FileOutputStream outputStream;
    private StorageReference storageReference = firebaseStorage.getReference(fname);

    private String dName;
    private String dSpecie;
    private int dAge;
    private Bitmap dPhoto;
    ByteArrayOutputStream baos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameView = findViewById(R.id.owner_name_view);

        nameView.append(auth.getCurrentUser().getDisplayName());

        dogName = findViewById(R.id.dog_name_text);
        dogSpecie = findViewById(R.id.specie_text);
        dogAge = findViewById(R.id.age_txt);
        choosePhoto = findViewById(R.id.photo_chooser);
        saveToDB = findViewById(R.id.save_btn);
        resetValues = findViewById(R.id.reset_btn);

try {
    final File localFile = File.createTempFile("images", "png");
    storageReference.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
            choosePhoto.setImageBitmap(bitmap);
        }
    });
} catch (IOException e){

}



        saveToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty(dogName) || isEmpty(dogSpecie) || isEmpty(dogAge) || dPhoto == null) {
                    String str = "You can't have empty fields!!!";
                    Toast.makeText(ProfileActivity.this, str, Toast.LENGTH_SHORT).show();
                } else {
                    dName = ((EditText) findViewById(R.id.dog_name_text)).getText().toString();
                    dAge = Integer.parseInt(((EditText) findViewById(R.id.age_txt)).getText().toString());
                    dSpecie = ((EditText) findViewById(R.id.specie_text)).getText().toString();
                    baos = new ByteArrayOutputStream();
                    dPhoto.compress(Bitmap.CompressFormat.PNG,100,baos);
                    byte[] data = baos.toByteArray();

                    Toast.makeText(ProfileActivity.this,"Dog name: " + dName + ", specie: " + dSpecie + ", age: " + dAge + " saved!!!",Toast.LENGTH_SHORT).show();
                    mDatabase.child(auth.getCurrentUser().getDisplayName()).child("Dog Name").setValue(dName);
                    mDatabase.child(auth.getCurrentUser().getDisplayName()).child("Dog Age").setValue(dAge);
                    mDatabase.child(auth.getCurrentUser().getDisplayName()).child("Dog Specie").setValue(dSpecie);

                    UploadTask uploadTask = storageReference.putBytes(data);

                    try {
                        outputStream = new FileOutputStream(new File(MainActivity.F_NAME));
                        outputStream.write(data);
                        outputStream.close();
                    } catch (FileNotFoundException e){
                        e.fillInStackTrace();
                        Log.d("Debug",e.getMessage());
                    }
                    catch (IOException e){
                        Log.d("Debug",e.getMessage());
                    }






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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
            public void onImagesPicked(List<File> imagesFiles, EasyImage.ImageSource source, int type) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imagesFiles.get(0).getAbsolutePath());
                choosePhoto.setImageBitmap(myBitmap);
                dPhoto = myBitmap;
            }
        });
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

}
