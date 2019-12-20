package com.example.booktobook;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdapterShelfFragment extends RecyclerView.Adapter<AdapterShelfFragment.ViewHolder> {

    private ArrayList<MyBookData> bookDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView cover;
        public TextView title;
        public TextView author;
        public TextView publisher;
        public Button re_enroll_button;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.item_mybook_imageView_cover);
            title = itemView.findViewById(R.id.item_mybook_textView_title);
            author = itemView.findViewById(R.id.item_mybook_textView_author);
            publisher = itemView.findViewById(R.id.item_mybook_textView_publisher);
            re_enroll_button = itemView.findViewById(R.id.item_mybook_button_release);

            //재등록버튼:그냥 해당책을 abled를 true로 만들어주면 된다.
            re_enroll_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    final FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("Books")
                            .whereEqualTo("title",title.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("AdapterBook", document.getId() + " => " + document.getData());
                                            db.collection("Books").document(document.getId())
                                                    .update(
                                                            "abled",true
                                                    );
                                        }
                                    } else {
                                        Log.d("AdapterBook", "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                }
            });

        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterShelfFragment(ArrayList<MyBookData> dataset) {
        bookDataSet = dataset;
    }

    // Create new views
    @NonNull
    @Override
    public AdapterShelfFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mybook,parent,false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull AdapterShelfFragment.ViewHolder holder, int position) {


        Glide.with(holder.itemView.getContext())
                .load(bookDataSet.get(position).getBook_image())
                .into(holder.cover);

       holder.title.setText(bookDataSet.get(position).title);
       holder.author.setText(bookDataSet.get(position).author);
       holder.publisher.setText(bookDataSet.get(position).publisher);

        Log.d("AdapterShelf-test-title",bookDataSet.get(position).title);
    }

    @Override
    public int getItemCount() {
        return bookDataSet.size();
    }
}


