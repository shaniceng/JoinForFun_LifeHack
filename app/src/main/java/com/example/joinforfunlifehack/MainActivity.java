package com.example.joinforfunlifehack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joinforfunlifehack.ui.gallery.GalleryFragment;
import com.example.joinforfunlifehack.ui.gallery.Item;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.joinforfunlifehack.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private String currentuser;

    private View header;
    private TextView headerName;
    private TextView headerEmail;
    private ArrayList<Item> list;
    private String expirationDate;
    private String name;

    private Button scanbtn;

    private int noficationID = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get firebase auth
        auth= FirebaseAuth.getInstance();
        currentuser = auth.getCurrentUser().getUid();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
//        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        header = navigationView.getHeaderView(0);
        headerName = (TextView) header.findViewById(R.id.userName);
        headerEmail = (TextView) header.findViewById(R.id.userEmail);
        getUser();

        //reminder
        list = new ArrayList<>();
        getCalender();
        GetAlarm();

    }

    private void getUser() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.hasChildren() && dataSnapshot.getKey().equals(currentuser)) {
                        User user = dataSnapshot.getValue(User.class);
                        headerName.setText(user.getFullname());
                        headerEmail.setText(user.getEmail());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getCalender() {
        //get firebase data
        databaseReference = FirebaseDatabase.getInstance().getReference("Items/" + currentuser);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.hasChildren()) {
                        Item item = dataSnapshot.getValue(Item.class);
                        list.add(item);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_LONG).show();
            }
        });

        scanbtn = (Button) findViewById(R.id.scanbtn);
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                startActivity(intent);
            }
        });


    }

    private void GetAlarm() {
        Date date = null;

        //get current date
        Calendar calender = Calendar.getInstance();

        //get reminder
        for (Item item: list) {
            expirationDate = item.getExpirationDate();
            name = item.getItemName();;
            try {
                date = new SimpleDateFormat("dd/MM/yyyy").parse(expirationDate);
            } catch (ParseException e) {
                date = null;
                e.printStackTrace();
            }
        }


        //initialise alarm
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        intent.putExtra("notificationID", noficationID);
        intent.putExtra("todo", name + "is expiring today");
        PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (date != null) {
            calender.setTime(date);
            calender.set(Calendar.HOUR, 0);
            calender.set(Calendar.MINUTE, 0);
            calender.set(Calendar.SECOND, 0);
            calender.set(Calendar.AM_PM, Calendar.PM);
            long alarmStartTime = calender.getTimeInMillis();

            //setAlarm
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmStartTime,alarmIntent);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logOut) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginPage.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}