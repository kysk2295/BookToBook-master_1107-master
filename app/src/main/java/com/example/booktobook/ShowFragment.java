package com.example.booktobook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ShowFragment extends Fragment{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<BookData> dataArrayList;
    ImageButton chatBtn;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show,container,false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        chatBtn=view.findViewById(R.id.button_chat);
        recyclerView = view.findViewById(R.id.recycler_view_show);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),ChatActivity.class);
                startActivity(i);
            }
        });

        SharedPreferences pref = this.getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        String id = pref.getString("ID", "");


        dataArrayList = new ArrayList<>();
        adapter = new AdapterBook(dataArrayList,getContext());
        recyclerView.setAdapter(adapter);


        db.collection("Books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d("ShowFragment", document.getId() + " => " + document.getData());
                                if(document.getBoolean("abled")){
                                    dataArrayList.add(new BookData(
                                            document.get("book_image").toString(),
                                            document.get("title").toString(),
                                            "저자:"+document.get("author").toString(),
                                            "출판사:"+document.get("publisher").toString(),
                                            "주인:"+document.get("haver").toString(),
                                            "장소:"+document.get("place").toString(),
                                            "시간:"+document.get("time").toString()
                                    ));
                                }
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("ShowFragment", "Error getting documents: ", task.getException());
                        }
                    }
                });





        return view;
    }
}

