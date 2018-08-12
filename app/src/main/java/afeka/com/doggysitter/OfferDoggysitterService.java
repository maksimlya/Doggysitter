package afeka.com.doggysitter;

import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TimeZone;

import afeka.com.doggysitter.ListViews.DoggysitterInstance;
import afeka.com.doggysitter.ListViews.DoggysitterInstanceAdapter;


public class OfferDoggysitterService extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private CalendarView calendarView;
    private Button addInstance;
    private ListView instancesList;
    private TimePickerDialog timePickerDialog;
    TimeZone a = TimeZone.getDefault();

    private Calendar calendar = Calendar.getInstance();
    private int myMonth;
    private int myDay;
    private ArrayList<DoggysitterInstance> myInstances;
    private int startHour = 0;
    private int endHour = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_doggysitter_service);
        myMonth = calendar.get(Calendar.MONTH) + 1;
        myDay = calendar.get(Calendar.DAY_OF_MONTH);


        calendarView = findViewById(R.id.calendarView);
        addInstance = findViewById(R.id.add_instance_btn);
        instancesList = findViewById(R.id.instances_list);
        calendarView.setMinDate(calendar.getTimeInMillis());

        myInstances = new ArrayList<>();

        final DoggysitterInstanceAdapter adapter = new DoggysitterInstanceAdapter(this,myInstances);

        instancesList.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference("/Doggysitters/" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String g = dataSnapshot.getKey();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Map<String,String> m = (Map<String, String>) child.getValue();
                    for(String key : m.keySet()){
                        DoggysitterInstance temp = new DoggysitterInstance();
                        temp.setMonth(Integer.parseInt(g));
                        temp.setDay(Integer.parseInt(child.getKey()));
                        temp.setStartHour(Integer.parseInt(key));
                        temp.setEndHour(Integer.parseInt(m.get(key)));
                        adapter.addItem(temp);
                        adapter.sort();
                        adapter.notifyDataSetChanged();
                    }


                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String g = dataSnapshot.getKey();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Map<String, String> m = (Map<String, String>) child.getValue();
                    for(String key : m.keySet()){
                        DoggysitterInstance temp = new DoggysitterInstance();
                        temp.setMonth(Integer.parseInt(g));
                        temp.setDay(Integer.parseInt(child.getKey()));
                        temp.setStartHour(Integer.parseInt(key));
                        temp.setEndHour(Integer.parseInt(m.get(key)));
                        adapter.addItem(temp);
                        adapter.sort();
                        adapter.notifyDataSetChanged();
                    }

                }
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

        addInstance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = TimePickerDialog.newInstance(OfferDoggysitterService.this, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.enableMinutes(false);
                timePickerDialog.show(getFragmentManager(),"First");
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                myMonth = month+1;
                myDay = dayOfMonth;



            }
        });
    }


    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        if(view.getTag().equals("First")){
            startHour = hourOfDay;
            timePickerDialog = TimePickerDialog.newInstance(OfferDoggysitterService.this, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.setMinTime(startHour+1,0,0);
            timePickerDialog.enableMinutes(false);
            timePickerDialog.show(getFragmentManager(),"Second");
        }

        else{ endHour = hourOfDay;

            FirebaseDatabase.getInstance().getReference("/Doggysitters/" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child(String.valueOf(myMonth)).child(String.valueOf(myDay)).child(String.valueOf(startHour)).setValue(String.valueOf(endHour));
        }
    }
}
