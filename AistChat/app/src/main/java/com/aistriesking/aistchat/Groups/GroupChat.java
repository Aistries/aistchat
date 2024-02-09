package com.aistriesking.aistchat.Groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.aistriesking.aistchat.databinding.ActivityMainBinding;
import com.aistriesking.aistchat.ChatUpdates.Notifications;
import com.aistriesking.aistchat.MainActivity;
import com.aistriesking.aistchat.Messages;
import com.aistriesking.aistchat.People;
import com.aistriesking.aistchat.Profiling.ProfileActivity;
import com.aistriesking.aistchat.R;
import com.aistriesking.aistchat.Stream.LiveStreamActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class GroupChat extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    private SearchView searchView;

    NavigationView navigationView;

    ListView lvTopics;
    ArrayList<String> listOfTopics = new ArrayList<String>();
    ArrayAdapter arrayAdpt;
    String UserName;

    String correct_username = "McRaj";
    String correct_username1 = "mcRaj";
    String correct_username2 = "mcraj";
    String correct_username3 = "MCRAJ";
    String correct_username4 = "Mcraj";

    private DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().getRoot();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group_chat);

        searchView = findViewById(R.id.searchview);
        searchView.setOnQueryTextListener(this); // Set the OnQueryTextListener


        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.mlayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView= findViewById(R.id.navigationView);

        // to make the Navigation drawer icon always appear on the action bar
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        lvTopics = (ListView)findViewById(R.id.lvTopics);
        arrayAdpt = new ArrayAdapter(this, android.R.layout.simple_list_item_1,listOfTopics);
        lvTopics.setAdapter(arrayAdpt);

        getUserName();

        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> set = new HashSet<String>();
                Iterator i = snapshot.getChildren().iterator();

                while(i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }
                arrayAdpt.clear();
                arrayAdpt.addAll(set);
                arrayAdpt.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // Handle navigation item clicks
                int itemId = item.getItemId();
                if (itemId == R.id.activities) {
                    startActivity(new Intent(GroupChat.this, LiveStreamActivity.class));
                    return true;
                }
                return false;
            }
        });

        /* // Handle navigation item clicks
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    //startActivity(new Intent(CustomerDashboard.this, CustomerDashboard.class));
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    //startActivity(new Intent(CustomerDashboard.this, about_customer.class));
                    return true;
                } else if (itemId == R.id.nav_people) {
                    startActivity(new Intent(MainActivity.this, People.class));
                    return true;
                } else if (itemId == R.id.nav_account) {
                    startActivity(new Intent(MainActivity.this, Messages.class));
                    return true;
                }
                return false;
            }
        });*/

        // Set up the Bottom Navigation View
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation item clicks
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(GroupChat.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    startActivity(new Intent(GroupChat.this, Notifications.class));
                    return true;
                } else if (itemId == R.id.nav_people) {
                    //startActivity(new Intent(GroupChat.this, GroupChat.class));
                    return true;
                } else if (itemId == R.id.nav_account) {
                    startActivity(new Intent(GroupChat.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });

        lvTopics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), Discussion.class);
                i.putExtra("Selected_Topic", ((TextView)view).getText().toString());
                i.putExtra("username", UserName);
                startActivity(i);
            }
        });


        Load_setting();


    }

    private void getUserName(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        EditText userName = new EditText(this);
        userName.setHint("Username");



        builder.setView(userName);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserName = userName.getText().toString();


                if(TextUtils.isEmpty(userName.getText().toString())){
                    Toast.makeText(GroupChat.this, "Empty data provided", Toast.LENGTH_LONG).show();
                    getUserName();
                }else if(userName.getText().toString().equals(correct_username)){
                    Toast.makeText(GroupChat.this, "OOPS! Unfortunately that's a Branding name...", Toast.LENGTH_LONG).show();
                    getUserName();
                }else if(userName.getText().toString().equals(correct_username1)){
                    Toast.makeText(GroupChat.this, "OOPS! Unfortunately that's a Branding name...", Toast.LENGTH_LONG).show();
                    getUserName();
                }else if(userName.getText().toString().equals(correct_username2)){
                    Toast.makeText(GroupChat.this, "OOPS! Unfortunately that's a Branding name...", Toast.LENGTH_LONG).show();
                    getUserName();
                }else if(userName.getText().toString().equals(correct_username3)){
                    Toast.makeText(GroupChat.this, "OOPS! Unfortunately that's a Branding name...", Toast.LENGTH_LONG).show();
                    getUserName();
                }else if(userName.getText().toString().equals(correct_username4)){
                    Toast.makeText(GroupChat.this, "OOPS! Unfortunately that's a Branding name...", Toast.LENGTH_LONG).show();
                    getUserName();
                }else{
                    Toast.makeText(GroupChat.this, "Successful login", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getUserName();
                Toast.makeText(GroupChat.this, "Empty data provided", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }


    private void Load_setting(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        /*boolean chk_night = sp.getBoolean("NIGHT", false);
        if(chk_night){
            Cl.setBackgroundColor(Color.parseColor("#FF000000"));
            Tv.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
        }else{
            Cl.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            Tv.setBackgroundColor(Color.parseColor("#FF000000"));

        }*/
        String orien = sp.getString("ORIENTATION","false");
        if("1".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
        }else if("2".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else if("3".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Handle the query submission if needed
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Handle the query text change
        filterTopics(newText);
        return true;
    }

    private void filterTopics(String query) {
        ArrayList<String> filteredTopics = new ArrayList<>();
        for (String topic : listOfTopics) {
            if (topic.toLowerCase().contains(query.toLowerCase())) {
                filteredTopics.add(topic);
            }
        }
        arrayAdpt.clear();
        arrayAdpt.addAll(filteredTopics);
        arrayAdpt.notifyDataSetChanged();
    }





    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle navigation item clicks
        int itemId = item.getItemId();
        if (itemId == R.id.about) {
            //startActivity(new Intent(CustomerDashboard.this, CustomerDashboard.class));
            return true;
        } else if (itemId == R.id.settings) {
            //startActivity(new Intent(CustomerDashboard.this, about_customer.class));
            return true;
        }
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return  true;
        }
        return true;


    }


    @Override
    protected void onResume() {
        Load_setting();
        super.onResume();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}