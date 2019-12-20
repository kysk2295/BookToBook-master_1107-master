package com.example.booktobook;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ShelfFragment extends Fragment {

    private RecyclerView recyclerView;

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView textView_shelf;
    private ArrayList<MyBookData> dataArrayList;
    private String id;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shelf,container,false);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        recyclerView  = view.findViewById(R.id.recycler_view_shelf);
        textView_shelf = view.findViewById(R.id.fragment_shelf_myShelf_textView);



        //get id from sharedPreference
        SharedPreferences pref = this.getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        id = pref.getString("ID", "");

        textView_shelf.setText(id+"님의 책장");


        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        dataArrayList = new ArrayList<>();
        adapter = new AdapterShelfFragment(dataArrayList);
        recyclerView.setAdapter(adapter);


        //  수정: User의 myBooks를 삭제하고 그냥 Books에서 haver가 나 인걸 가져오자

        db.collection("Books")
                .whereEqualTo("haver", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());

                                dataArrayList.add(new MyBookData(
                                        document.get("book_image").toString(),
                                        document.get("title").toString(),
                                        "저자:"+document.get("author").toString(),
                                        "출판사:"+document.get("publisher").toString()
                                ));

//
//                                adapter.notifyDataSetChanged();

                            }
                        } else {
                            Log.d("Error", "Error getting documents: ", task.getException());
                        }
                    }
                });

        return view;
    }
}
