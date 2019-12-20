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
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Chat> chats = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    String id;
    Context mcontext=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        recyclerView = findViewById(R.id.recycler_view_chat);

        SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
        id = sharedPreferences.getString("ID", "id");
        final ChatListAdapter chatListAdapter= new ChatListAdapter(chats);
        recyclerView.setAdapter(chatListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //for test
      //  id = "1234";

        final Chat chat = new Chat();

        db.collection("room")
                .whereEqualTo("haver",id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        if (snapshot.exists()) {

                            //방 Id
                            chat.id = snapshot.getId();
                            //방 이름
                            chat.haver = snapshot.getString("haver");
                            chat.borrower = snapshot.getString("borrower");

                            chat.roomName = chat.borrower;
                            //chat.name 에다 자기 id 넣어줌.
                            chat.name = id;
                            chats.add(chat);
                            Log.d("kwk",chat.haver+" / "+chat.roomName);
                        }

                    }
                    chatListAdapter.notifyDataSetChanged();
                } else {
                    Log.d("asdd","test fail");
                }
            }
        });


        db.collection("room")
                .whereEqualTo("borrower",id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                if (snapshot.exists()) {

                                        //방 Id
                                        chat.id = snapshot.getId();
                                        //방 이름
                                        chat.haver = snapshot.getString("haver");
                                        chat.borrower = snapshot.getString("borrower");
                                        chat.roomName = chat.haver;
                                        //chat.name 에다 자기 id 넣어줌.
                                        chat.name = id;
                                        chats.add(chat);
                                        Log.d("kwk",chat.haver+" / "+chat.roomName+"test2");


                                }
                            }
                            chatListAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("asdd","test fail");
                        }
                    }
                });

        Log.d("qwer", String.valueOf(chats.size()));

//
//
//
//        final ChatListAdapter adapter = new ChatListAdapter(chats);
//        adapter.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Intent i = new Intent(ChatActivity.this, MessageActivity.class);
//                i.putExtra("room_data", adapter.getItem(position));
//                startActivity(i);
//            }
//        });
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//





//        documentReference= (DocumentReference) db.collection("room")
//                .orderBy("name").startAt(id)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//
//                        List<Chat> rooms= new ArrayList<>();
//                        for (QueryDocumentSnapshot doc:queryDocumentSnapshots){
//                            Chat chat= new Chat();
//                            //방 Id
//                            chat.id=doc.getId();
//                            //방 이름
//                            chat.haver=doc.getString("haver");
//                            chat.borrower=doc.getString("borrower");
//                            //책을 빌린 사람의 경우
//                            if (chat.borrower.equals(id))
//                            {
//                                chat.roomName=chat.haver;
//                            }
//                            //책을 빌려준 사람의 경우
//                            else
//                            {
//                                chat.roomName=chat.borrower;
//                            }
//                            //chat.name 에다 자기 id 넣어줌.
//                            chat.name=id;
//                            rooms.add(chat);
//                        }
////                        chats= (ArrayList<Chat>) rooms;
//                        final ChatListAdapter adapter = new ChatListAdapter(chats);
                        chatListAdapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent i = new Intent(ChatActivity.this,MessageActivity.class);
                                i.putExtra("room_data",chatListAdapter.getItem(position));
                                startActivity(i);
                            }
                        });
//                        recyclerView.setAdapter(adapter);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//
//                    }
//                });
//

        }
}
