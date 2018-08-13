package afeka.com.doggysitter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    public static String F_NAME;
    public static String PHOTO_FILE_LOCATION;
    public static LatLng MY_LOCATION;
    private StorageReference storageReference;
    private static final int RC_SIGN_IN = 0;


    List<AuthUI.IdpConfig> providers = Arrays.asList(                       // Google + email authorization
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLocation();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE}, 2);
                }
        }

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.doggybackground)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Intent loggedIn = new Intent(this,ChooseCategory.class);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                    F_NAME = Objects.requireNonNull(auth.getCurrentUser()).getDisplayName() + "/profilePhoto.png";
                    FirebaseDatabase.getInstance().getReference("/Users/" + auth.getCurrentUser().getDisplayName()).child("isOnline").setValue(true);
                    FirebaseDatabase.getInstance().getReference("/Users/" + auth.getCurrentUser().getDisplayName()).child("isOnline").onDisconnect().setValue(false);
                    FirebaseDatabase.getInstance().getReference("/Users/" + auth.getCurrentUser().getDisplayName()).child("Park").onDisconnect().removeValue();
                    FirebaseDatabase.getInstance().goOnline();



                    final File localFile =  new File(getFilesDir() + "/" + F_NAME);
                    PHOTO_FILE_LOCATION = localFile.getAbsolutePath();
                    if(!localFile.exists()) {                                              // If profile photo for current account does not exist on the phone memory
                        localFile.getParentFile().mkdirs();
                        try {
                            localFile.createNewFile();
                        } catch (IOException e) {
                        e.printStackTrace();
                    }
                    storageReference = FirebaseStorage.getInstance().getReference(F_NAME);
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {          // Check if profile photo exists on server.
                            @Override
                            public void onSuccess(Uri uri) {
                                storageReference.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {          // Loads the profile photo from server
                                    @Override
                                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                        startActivity(loggedIn);
                                        finish();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {                                                                       // Loads placeholder photo
                                FirebaseStorage.getInstance().getReference("placeholder.jpg").getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                        startActivity(loggedIn);
                                        finish();
                                    }
                                });
                            }
                        });



                    } else {
                    startActivity(loggedIn);
                    finish();
                }

            }
        }
    }

    public void getLocation(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                  if(location != null){
                    MY_LOCATION = new LatLng(location.getLatitude(),location.getLongitude());
                    Toast.makeText(MainActivity.this,"Got Location!" + MY_LOCATION.latitude + " " + MY_LOCATION.longitude,Toast.LENGTH_LONG).show();

                  }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Failed to get location!!!",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        MY_LOCATION = new LatLng(location.getLatitude(),location.getLongitude());
    }
}
