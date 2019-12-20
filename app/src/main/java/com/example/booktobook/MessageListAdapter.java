package com.example.booktobook;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.opencensus.internal.Utils;

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int SENT=0;
    private static final int RECEIVED=1;
    private Context context;
    private String userId;
    private List<Chat> chats;
    private SimpleDateFormat dateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateFormatHour= new SimpleDateFormat("aa hh:mm");

    public MessageListAdapter(Context context, List<Chat> chats, String userId){
        this.context=context;
        this.chats=chats;
        this.userId=userId;
        dateFormatDay.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        dateFormatHour.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
    }

    @Override
    public int getItemViewType(int position) {
        if (chats.get(position).sender.contentEquals(userId)){
            return SENT;
        }else {
            return RECEIVED;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType==SENT){
            view=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_sent,parent,false);
            return new SentMessageHolder(view);
        }else if (viewType==RECEIVED){
            view=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_received,parent,false);
            return new ReceivedHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Chat chat=chats.get(position);
        switch (holder.getItemViewType()){
            case SENT:
                ((SentMessageHolder)holder).bind(chat);
                break;
            case RECEIVED:
                ((ReceivedHolder)holder).bind(chat);
        }
    }


    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class SentMessageHolder extends RecyclerView.ViewHolder{
        TextView messageText, timeText;

        public SentMessageHolder(@NonNull View itemView) {

            super(itemView);
            messageText=itemView.findViewById(R.id.text_message_body);
            timeText=itemView.findViewById(R.id.text_message_time);
        }
        void bind(Chat chat){
            messageText.setText(chat.message);
            timeText.setText(chat.timestamp);
            //이미지

        }
    }
    public class ReceivedHolder extends RecyclerView.ViewHolder{

        TextView messageText, timeText,nameText;
        ImageView profileImage;
        public ReceivedHolder(@NonNull View itemView)
        {
            super(itemView);
            messageText=itemView.findViewById(R.id.text_message_body);
            timeText=itemView.findViewById(R.id.text_message_time);
            nameText=itemView.findViewById(R.id.text_message_name);
            profileImage=itemView.findViewById(R.id.image_message_profile);
        }

        void bind(Chat chat){
            messageText.setText(chat.message);
            timeText.setText(chat.timestamp);
            nameText.setText(chat.name);
        }
    }

}
