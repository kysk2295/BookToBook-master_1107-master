package com.example.booktobook;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.util.Log;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.multidex.MultiDex;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.firestore.CollectionReference;
        import com.google.firebase.firestore.DocumentReference;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.QueryDocumentSnapshot;
        import com.google.firebase.firestore.QuerySnapshot;

        import java.util.ArrayList;

public class SearchActivity extends Activity implements SearchFragment.OnDataPassListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<BookData> dataArrayList = new ArrayList<>();
    private String data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_from_search);

        recyclerView = findViewById(R.id.recycler_view_search);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        Intent i =getIntent();
        data=i.getExtras().getString("abc");
        Log.d("tag",data);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference BOOKS = db.collection("Books");

        //firestore.collection("Books").whereEqualTo(data,true)
        BOOKS.orderBy("title")
                .startAt(data)
                .endAt(data+"\uf8ff")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            BookData bookData = documentSnapshot.toObject(BookData.class);
                            dataArrayList.add(new BookData(
                                    bookData.getBook_image().toString(),
                                    bookData.getTitle().toString(),
                                    bookData.getAuthor().toString(),
                                    bookData.getPublisher().toString(),
                                    bookData.getHaver().toString(),
                                    bookData.getPlace().toString(),
                                    bookData.getTime().toString()
                            ));

                            if (bookData.author.equals("null"))
                            {

                            }


                        }
                        adapter.notifyDataSetChanged();

                    }
                });
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //set adapter

        //SharedPreferences pref = this.getSharedPreferences("pref", MODE_PRIVATE);
        //String id = pref.getString("ID", "");



        adapter = new AdapterBook(dataArrayList,this);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public String onDataPass(String data) {
        this.data=data;
        return this.data;
    }
}