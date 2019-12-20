package com.example.booktobook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class MessageActivity extends AppCompatActivity {

    Intent intent;
    ImageView back,plus;
    TextView name;
    Chat chat;
    EditText edit_chat;
    Button send;
    RecyclerView recyclerView;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    List<Chat> chats= new ArrayList<>();
    Context mcontext=this;
    SharedPreferences sharedPreferences;
    String sender;
    MessageListAdapter messageListAdapter;
    private SimpleDateFormat dateFormatHour= new SimpleDateFormat("aa hh:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        plus=findViewById(R.id.plusBtn);
        back=findViewById(R.id.backBtn);
        name=findViewById(R.id.chatName);
        recyclerView=findViewById(R.id.messages_view);
        edit_chat=findViewById(R.id.edittext_chatbox);
        send=findViewById(R.id.button_chatbox_send);
        intent=getIntent();
        chat= (Chat) intent.getExtras().getSerializable("room_data");
        name.setText(chat.roomName);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dateFormatHour.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        sharedPreferences=getSharedPreferences("pref",MODE_PRIVATE);
        sender=sharedPreferences.getString("ID","");
        
        openChat();
        
        messageListAdapter= new MessageListAdapter(this,chats,sender);
        recyclerView.setAdapter(messageListAdapter);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(edit_chat.getText().toString().equals("")))
                    sendMessage(edit_chat.getText().toString());


            }
        });

        //documentReference=db.collection("")

    }

    private void sendMessage(final String toString) {
        final HashMap<String,Object> map= new HashMap<>();
        map.put("room_id",chat.id);
        map.put("message",toString);
        map.put("sender",sender);
        map.put("receiver",chat.roomName);
        map.put("sent",System.currentTimeMillis());
        chat.timestamp=dateFormatHour.format(new Date());
        map.put("timestamp",chat.timestamp);

        db.collection("chat")
                .add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                sendFcm(toString);
            }
        });
        edit_chat.setText("");
        openChat();

    }

    private void sendFcm(final String toString) {
        final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
        final String SERVER_KEY = "AAAAvwFFAB0:APA91bH27fELBmCwMY1ND4vQVUeaKmujW-k0N72NDzhJDaoV4IQ9z-KHcfS1UePQ_bGUKK2vWbJRmFLD_6txrpS8BJj5tpU1NKashowU-6jat4RW5aaPeQVHn9m6y7ZHlPqCJi4y1kB9";
        documentReference=db.collection("Users")
                .document(chat.roomName);

        final DocumentReference finalDocumentReference = documentReference;
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                final String token = (String) documentSnapshot.get("token");
                Log.d("yourToken",token);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject root = new JSONObject();
                            JSONObject notification = new JSONObject();
                            String data =sender+" : "+toString;
                            notification.put("body", data);
                            notification.put("title","BookToBook");
                            root.put("notification",notification);
                            root.put("to",token);
                            root.put("click_action","MESSAGE_ACTIVITY");

                            URL url= new URL(FCM_MESSAGE_URL);
                            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setDoOutput(true);
                            connection.setDoInput(true);
                            connection.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                            connection.setRequestProperty("Accept", "application/json");
                            connection.setRequestProperty("Content-type", "application/json");
                            OutputStream os=connection.getOutputStream();
                            os.write(root.toString().getBytes("utf-8"));
                            os.flush();
                            connection.getResponseCode();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("yourToken", "fail");
            }
        });
    }

    private void openChat() {
        //처음에 db 메세지 가져오기
        db.collection("chat")
                .whereEqualTo("room_id",chat.id)
                .orderBy("sent", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e !=null){
                            return;
                        }
                        chats.clear();
                            for (QueryDocumentSnapshot snapshot:queryDocumentSnapshots)
                            {
                                Chat chat = new Chat();
                                chat.id=snapshot.getString("room_id");
                                chat.message=snapshot.getString("message");
                                chat.sender=snapshot.getString("sender");
                                chat.timestamp=snapshot.getString("timestamp");

                                chats.add(chat);
                            }
                            messageListAdapter.notifyDataSetChanged();
                        }

                });
        }


//                if (task.isSuccessful()){
//                    DocumentSnapshot document=task.getResult();
//                    if (document.exists()){
//                        Chat chat = new Chat();
//                        chat.id=document.getString("room_id");
//                        chat.message=document.getString("message");
//                        chat.sender=document.getString("sender");
//
//                        chats.add(chat);
//                    }else{
//
//                    }
//                }
//                recyclerView.setAdapter(new MessageListAdapter(mcontext,chats,sender));
//                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }




