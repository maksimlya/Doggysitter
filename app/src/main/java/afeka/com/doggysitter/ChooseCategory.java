package afeka.com.doggysitter;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ChooseCategory extends AppCompatActivity {
    Button logOut;
    TextView welcomeMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        logOut = findViewById(R.id.log_out_btn);
        welcomeMessage = findViewById(R.id.welcome_view);
        welcomeMessage.append(auth.getCurrentUser().getDisplayName());


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
