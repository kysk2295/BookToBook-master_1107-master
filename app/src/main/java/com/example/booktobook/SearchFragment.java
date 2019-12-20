package com.example.booktobook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SearchFragment extends Fragment {

    AutoCompleteTextView autoCompleteTextView;
    private ArrayList<String> data;
    Button search;

    private SearchActivity activity;
    private OnDataPassListener onDataPassListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        data = new ArrayList<>();
        add();
        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, data));
        search = view.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchActivity.class);
                String abc = autoCompleteTextView.getText().toString();
                i.putExtra("abc",abc);

                Log.d("tag", abc);

                onDataPassListener.onDataPass(abc);
                // activity.onDataPass(abc);
                //onDataPassListener.onDataPass(abc);
                startActivity(i);

            }
        });
        return view;
    }

    private void add() {
        //예시 데이터
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference BOOKS = db.collection("Books");

        BOOKS.orderBy("title").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                            BookData bookData = documentSnapshot.toObject(BookData.class);
                            data.add(bookData.getTitle());
                        }
                    }
                });
    }

    public interface OnDataPassListener {
        String onDataPass(String data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            SearchActivity activity= new SearchActivity();
            onDataPassListener=(OnDataPassListener) activity;
        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString()
//                    + " must implement Listener");
        }

    }

    @Override
    public void onDetach () {
        onDataPassListener = null;
        super.onDetach();

    }


}


