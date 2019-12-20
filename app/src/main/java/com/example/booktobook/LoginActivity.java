package com.example.booktobook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;


public class LoginActivity extends AppCompatActivity {

    private EditText editText_id;
    private EditText editText_password;
    private EditText editText_place;
    private EditText editText_time;
    private Button button_signUp;
    private FirebaseFirestore db;
    private String id;
    private String password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        SharedPreferences preferences = getSharedPreferences("pref",MODE_PRIVATE);
        Boolean isLoginOnce = preferences.getBoolean("isLogin",false);

//        //만약 로그인한 적이 있다면 그냥 로그인
        if(isLoginOnce){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            Toast.makeText(LoginActivity.this, preferences.getString("ID","unknown")+"님 반갑습니다!", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }

         db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .build();

        db.setFirestoreSettings(settings);

        editText_id = findViewById(R.id.editText_id);
        editText_password = findViewById(R.id.editText_password);
        editText_place = findViewById(R.id.editText_place);
        editText_time = findViewById(R.id.editText_time);

        button_signUp = findViewById(R.id.button_signUp);


//        //회원가입 버튼 클릭시
//        //
//        //만약 동일한 아이디가 있다면?
//        //      회원가입 X 메세지
//        //아니면
//        //      아이디-패스워드로 해서 파베에 저장
        button_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String id = editText_id.getText().toString();
                final String password = editText_password.getText().toString();
                final String place = editText_place.getText().toString();
                final String time = editText_time.getText().toString();

                DocumentReference documentReference = db.collection("Users").document(id);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){//해당 도큐먼트가 이미 존재한다 => 아이디가 있다 => 새로 추가할 수가 없음
                                Log.d("LoginActivity-signUp", "already exist : "+id);
                                Toast.makeText(LoginActivity.this, "이미 존재하는 아이디입니다!", Toast.LENGTH_SHORT).show();
                            }else{//해당 도큐먼트가 존재하지 않는다 => 아이디가 없다 => 새로 추가
                                User user = new User(id,password,place,time);
                                db.collection("Users").document(id).set(user);
                                Log.d("LoginActivity-signUp", "new User made : "+user.getId());
                                Toast.makeText(LoginActivity.this, user.getId()+"님 반갑습니다!", Toast.LENGTH_SHORT).show();
                                SharedPreferences sharedPreferences = getSharedPreferences("pref",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLogin",true);
                                editor.putString("ID",id.toString());
                                editor.putString("place",place.toString());
                                editor.putString("time",time.toString());
                                editor.apply();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);

                            }
                        }
                        else{
                            Log.d("LoginActivity-signUp", "get failed with ", task.getException());
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Log.d("login", "login Sucessful");
                        FirebaseApp.initializeApp(getApplicationContext());


                    }
                });
            }
        });


    }
}

