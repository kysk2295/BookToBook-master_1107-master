package com.example.booktobook;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class AdapterBook extends RecyclerView.Adapter<AdapterBook.ViewHolder> {

    private static ArrayList<BookData> bookDataSet;
    private static String id;
    private static String TIME;
    private static String PLACE;
    Context mcontext;


    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView cover;
        public TextView title;
        public TextView author;
        public TextView publisher;
        public TextView haver;
        public TextView place;
        public TextView time;
        public Button borrow;
        String token;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.item_book_imageView_cover);
            title = itemView.findViewById(R.id.item_book_textView_title);
            author = itemView.findViewById(R.id.item_book_textView_author);
            publisher = itemView.findViewById(R.id.item_book_textView_publisher);
            haver = itemView.findViewById(R.id.item_book_textView_haver);
            place = itemView.findViewById(R.id.item_book_textView_place);
            time = itemView.findViewById(R.id.item_book_textView_time);
            borrow = itemView.findViewById(R.id.item_book_button_borrow);


            borrow.setOnClickListener(new View.OnClickListener() {
                                          @SuppressLint("ObsoleteSdkInt")
                                          @Override
                                          public void onClick(View view) {

                                              final FirebaseFirestore db = FirebaseFirestore.getInstance();
                                              DocumentReference documentReference;
                                              //양 계정에 알림을 추가
//                                              DocumentReference documentReference = db.collection("Users")
//                                                      .document(haver.getText().toString().substring(3));
//                                              documentReference.update("alert", FieldValue.arrayUnion(
//                                                      new Alert(
//                                                              place.getText().toString().substring(3),
//                                                              time.getText().toString().substring(3),
//                                                              "빌려줌",
//                                                              title.getText().toString(),
//                                                              id.toString()
//                                                      )
//                                              ));
//
//                                              documentReference = db.collection("Users")
//                                                      .document(id.toString());
//                                              documentReference.update("alert", FieldValue.arrayUnion(
//                                                      new Alert(
//                                                              place.getText().toString().substring(3),
//                                                              time.getText().toString().substring(3),
//                                                              "빌림",
//                                                              title.getText().toString(),
//                                                              haver.getText().toString().substring(3)
//                                                      )
//                                              ));

                                              // 자기 자신에게 알림
                                              final int notiId = 101;
                                              final String strChannelId = "channel_id";
                                              String content = haver.getText().toString().substring(3) + "님의 " + title.getText().toString() + " 책 을 빌렸습니다.";
                                              // Pending intent 생성
                                              Intent notificationIntent = new Intent(mcontext, MainActivity.class);
                                              notificationIntent.putExtra("noti_id", notiId);
                                              notificationIntent.putExtra("noti_text", "PendingIntent test");
                                              PendingIntent contentIntent = PendingIntent.getActivity(mcontext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                              // Notification 생성
                                              NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontext, strChannelId);
                                              builder.setContentTitle("BookToBook")
                                                      .setContentText(content)
                                                      .setSmallIcon(R.mipmap.ic_launcher)
                                                      .setContentIntent(contentIntent)
                                                      .setAutoCancel(true)
                                                      .setWhen(System.currentTimeMillis())
                                                      .setDefaults(Notification.DEFAULT_ALL);

                                              // Android O (API 26) 이상 부터는 channel id 등록 필요
                                              NotificationManager notificationManager = (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);
                                              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                  final String strTitle = "BootToBook";
                                                  NotificationChannel channel = notificationManager.getNotificationChannel(strChannelId);
                                                  if (channel == null) {
                                                      channel = new NotificationChannel(strChannelId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                                                      notificationManager.createNotificationChannel(channel);
                                                  }

                                                  builder.setChannelId(strChannelId);
                                              }

                                              notificationManager.notify(notiId, builder.build());



                    // 빌려준 사람에게 알람
                    final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
                    final String SERVER_KEY = "AAAAvwFFAB0:APA91bH27fELBmCwMY1ND4vQVUeaKmujW-k0N72NDzhJDaoV4IQ9z-KHcfS1UePQ_bGUKK2vWbJRmFLD_6txrpS8BJj5tpU1NKashowU-6jat4RW5aaPeQVHn9m6y7ZHlPqCJi4y1kB9";
                    documentReference=db.collection("Users")
                            .document(haver.getText().toString().substring(3));
                                              final DocumentReference finalDocumentReference = documentReference;
                                              documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            token= (String) documentSnapshot.get("token");
                            Log.d("yourToken",token);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject root = new JSONObject();
                                        JSONObject notification = new JSONObject();
                                        String data = id.toString() + "님이 " + title.getText().toString()
                                                + "책을 빌렸습니다." + "\n" + place.getText().toString().substring(3) + "장소에서" + time.getText().toString().substring(3) + "에 만납니다.";

                                        notification.put("body", data);
                                        notification.put("title","BookToBook");
                                        root.put("notification",notification);
                                        root.put("to",token);
                                        root.put("click_action","CHAT_ACTIVITY");

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
                            Log.d("yourToken","fail");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String,Object> room=new HashMap<>();
                            room.put("name",haver.getText().toString().substring(3)+"/"+title.getText().toString());
                            room.put("haver",haver.getText().toString().substring(3));
                            room.put("borrower",id.toString());

                            db.collection("room")
                                    .add(room)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Intent i = new Intent(mcontext,ChatActivity.class);
                                            mcontext.startActivity(i);
                                        }
                                    });
                        }
                    });

//                    documentReference=db.collection("Users")
//                            .document(id.toString());
//                    final ArrayList<String> finalFlag = new ArrayList<>();
//                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if (task.isSuccessful()){
//                                DocumentSnapshot snapshot=task.getResult();
//                                if (snapshot.exists()){
//                                    if (snapshot.getData().get("alert")!=null){
//                                        List list = (List) snapshot.getData().get("alert");
//                                        for (int i=0;i<list.size();i++){
//                                            HashMap map=(HashMap) list.get(i);
//                                            finalFlag.add(map.get("status").toString());
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    });
//
//                    int cnt=0;
//                    for (int n=0;n<finalFlag.size();n++){
//                        if (finalFlag.get(n).equals("빌려줌")){
//                                cnt=1;
//
//                        }
//                    }



                    //빌리기를 누르자마자 바로 비활성화
                    //  책의 haver를 주인에서 나로 바꿔주자
//                    db.collection("Books")
//                            .whereEqualTo("title",title.getText().toString())
//                            .get()
//                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                    if (task.isSuccessful()) {
//                                        for (QueryDocumentSnapshot document : task.getResult()) {
//                                            Log.d("AdapterBook", document.getId() + " => " + document.getData());
//                                            db.collection("Books").document(document.getId())
//                                                    .update(
//                                                            "abled", false,
//                                                            "haver",id,
//                                                            "time",TIME,
//                                                            "place",PLACE
//                                                    );
//
//                                        }
//                                    } else {
//                                        Log.d("AdapterBook", "Error getting documents: ", task.getException());
//                                    }
//                                }
//                            });
//
//                    //해당 아이템 리사이클러에서 삭제
//                    final int adapterPosition = getAdapterPosition();
//                    bookDataSet.remove(adapterPosition);
//                    notifyItemRemoved(adapterPosition);
//                    notifyItemRangeChanged(adapterPosition,bookDataSet.size());
//
                }
           });


        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterBook(ArrayList<BookData> dataset, Context context) {
        bookDataSet = dataset;
        SharedPreferences preferences = context.getSharedPreferences("pref",0);
        id = preferences.getString("ID","");
        PLACE = preferences.getString("place","");
        TIME = preferences.getString("time","");
        mcontext=context;

    }

    // Create new views
    @NonNull
    @Override
    public AdapterBook.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book,parent,false);
        mcontext=parent.getContext();

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }




    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull AdapterBook.ViewHolder holder, int position) {

//        holder.cover.setImageResource(bookDataSet.get(position).book_image);

        Glide.with(holder.itemView.getContext())
                .load(bookDataSet.get(position).getBook_image())
                .into(holder.cover);
        holder.title.setText(bookDataSet.get(position).title);
        holder.author.setText(bookDataSet.get(position).author);
        holder.publisher.setText(bookDataSet.get(position).publisher);
        holder.haver.setText(bookDataSet.get(position).haver);
        holder.place.setText(bookDataSet.get(position).place);
        holder.time.setText(bookDataSet.get(position).time);


        Log.d("AdapterBook-test-title",bookDataSet.get(position).title);
    }

    @Override
    public int getItemCount() {
        return bookDataSet.size();
    }

//    private void sendNotificationToUser(String token) {
//        RootModel rootModel = new RootModel(token, new NotificationModel("Title", "Body"), new DataModel("Name", "30"));
//
//        RetrofitService apiService =  ApiClient.getClient().create(RetrofitService.class);
//        Call<okhttp3.ResponseBody> responseBodyCall=apiService.sendNotification(rootModel);
//
//        responseBodyCall.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Log.d(TAG,"Successfully notification send by using retrofit.");
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });

    }







