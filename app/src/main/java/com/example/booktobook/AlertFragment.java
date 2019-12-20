package com.example.booktobook;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AlertFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Alert> dataArrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_alert,container,false);

        recyclerView = view.findViewById(R.id.recycler_view_alert);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        SharedPreferences pref = this.getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        String id = pref.getString("ID", "");

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        //set adapter
        dataArrayList = new ArrayList<>();
        adapter = new AdapterAlertFragment(dataArrayList,id);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();



        DocumentReference documentReference = db.collection("Users").document(id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()) {
                        if (documentSnapshot.getData().get("alert") != null) {
                            List list = (List) documentSnapshot.getData().get("alert");
                            for (int i = 0; i < list.size(); i++) {
                                HashMap map = (HashMap) list.get(i);


                                dataArrayList.add(new Alert(
                                        map.get("place").toString(),
                                        map.get("time").toString(),
                                        map.get("status").toString(),
                                        map.get("book_title").toString(),
                                        map.get("who").toString()
                                ));


                                adapter.notifyDataSetChanged();

                            }
                        }
                    }
                    else{
                        Log.d("TAG", "No such document");
                    }
                }
                else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });







        return view;
    }
}
