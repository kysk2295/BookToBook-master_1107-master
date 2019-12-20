package com.example.booktobook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class EnrollActivity extends AppCompatActivity {

    private ImageView bookImageView;
    private TextView titleTextView;
    private TextView authorTextView;
    private TextView publisherTextView;
    private Button enrollButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_from_cam);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        bookImageView = findViewById(R.id.activity_enroll_ImageView_bookImg);
        titleTextView = findViewById(R.id.activity_enroll_textView_title);
        authorTextView = findViewById(R.id.activity_enroll_textView_author);
        publisherTextView = findViewById(R.id.activity_enroll_textView_publisher);
        enrollButton = findViewById(R.id.activity_enroll_button_enroll);
        backButton = findViewById(R.id.activity_enroll_imageButton_back);

        Intent intent = getIntent();

        Bitmap bookImg = intent.getParcelableExtra("img");
        final String bookImg_url = intent.getExtras().getString("img_url");
        final String title = intent.getExtras().getString("title");
        final String publisher = intent.getExtras().getString("publisher");
        final String author = intent.getExtras().getString("auth");

        SharedPreferences preferences = getSharedPreferences("pref",MODE_PRIVATE);
        final String ID = preferences.getString("ID","unknown user");
        final String place = preferences.getString("place","none");
        final String time= preferences.getString("time","none");



        bookImageView.setImageBitmap(bookImg);
        titleTextView.setText("제목 : "+title);
        authorTextView.setText("작가 : "+author);
        publisherTextView.setText("출판사 : "+publisher);





        //등록을 눌렀을때
        enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
                BookData bookData = new BookData(bookImg_url,title,author,publisher,pref.getString("ID",""),place,time);

                //책 등록
                db.collection("Books").add(bookData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("EnrollActivity-enroll","enroll success");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("EnrollActivity-enroll","enroll failure");
                            }
                        });



                //나의 책장에 추가
//                db.collection("Users").document(ID)
//                        .update("myBook", FieldValue.arrayUnion(new MyBookData(bookImg_url,title,author,publisher)));


                //뒤로 가기
                onBackPressed();
            }
        });

        //뒤로가기 버튼을 누르면 뒤로간다
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



    }
}
