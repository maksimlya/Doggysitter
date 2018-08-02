package afeka.com.doggysitter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {
    private TextView nameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        nameView = findViewById(R.id.owner_name_view);
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        nameView.append(auth.getCurrentUser().getDisplayName());
    }
}
