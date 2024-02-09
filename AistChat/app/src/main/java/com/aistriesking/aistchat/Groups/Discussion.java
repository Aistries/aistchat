package com.aistriesking.aistchat.Groups;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.aistriesking.aistchat.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Discussion extends AppCompatActivity {

    private ProgressBar progressBar;
    int i = 0;

    Button btnSend;
    EditText etMessage;
    ListView lvTopics;
    ArrayList<String> ListConversation = new ArrayList<String>();
    ArrayAdapter arrayAdpt;

    private DatabaseReference dbr;

    String UserName,SelectedTopic,user_msg_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        //progressBar = findViewById(R.id.progress_bar);

        btnSend = (Button) findViewById(R.id.btnSend);
        etMessage = (EditText) findViewById(R.id.txt_message);

        etMessage.setText(null);

        lvTopics = (ListView)findViewById(R.id.lvTopics);
        arrayAdpt = new ArrayAdapter(this, android.R.layout.simple_list_item_1, ListConversation);
        lvTopics.setAdapter(arrayAdpt);

        UserName = getIntent().getExtras().get("username").toString();
        SelectedTopic = getIntent().getExtras().get("Selected_Topic").toString();
        setTitle(SelectedTopic + " " + "Page");


        dbr = FirebaseDatabase.getInstance().getReference().child(SelectedTopic);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // set the limitations for the numeric
                // text under the progress bar
                if (i <= 100) {

                    //progressBar.setProgress(i);
                    i++;
                    handler.postDelayed(this, 10);
                } else {
                    handler.removeCallbacks(this);
                }
            }
        }, 10);


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<String, Object>();

                user_msg_key = dbr.push().getKey();
                dbr.updateChildren(map);
                DatabaseReference dbr2 = dbr.child(user_msg_key);
                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("msg", etMessage.getText().toString());
                map2.put("user", UserName);
                dbr2.updateChildren(map2);
                etMessage.setText(null);

                addNotification();

            }
        });
        dbr.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateConversation(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateConversation(snapshot);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });




    }



    public void updateConversation(DataSnapshot snapshot){
        String msg,user;
        Iterator i = snapshot.getChildren().iterator();
        while(i.hasNext()){
            msg=(String) ((DataSnapshot)i.next()).getValue();
            user = (String) ((DataSnapshot)i.next()).getValue();

            arrayAdpt.insert(user + ": " + msg,0);
            arrayAdpt.notifyDataSetChanged();
        }

    }

    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_account)
                        .setContentTitle("Notifications")
                        .setContentText("You have a new Message");

        Intent notificationIntent = new Intent(this, GroupChat.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}