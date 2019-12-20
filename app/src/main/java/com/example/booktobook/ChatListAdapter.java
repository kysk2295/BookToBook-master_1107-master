package com.example.booktobook;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {


    ArrayList<Chat> chats;
    Context context;
    public static final String CHAT_ROOM_ID="CHAT_ROOM_ID";
    public static final String CHAT_ROOM_NAME="CHAT_ROOM_NAME";
    private OnItemClickListener onItemClickListener;

    ChatListAdapter(ArrayList<Chat> chats)
    {
        this.chats=chats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View v= LayoutInflater.from(context).inflate(R.layout.item_chatlist,parent,false);
        return new ViewHolder(v, onItemClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat=chats.get(position);
        holder.name.setText(chat.roomName);
        holder.time.setText((CharSequence) chat.timestamp);
        holder.chat.setText(chat.message);

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public Chat getItem(int position) {
        return chats.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView name,chat,time;
        public ViewHolder(@NonNull final View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            imageView=itemView.findViewById(R.id.profile_image);
            name=itemView.findViewById(R.id.profile_name);
            chat=itemView.findViewById(R.id.profile_chat);
            time=itemView.findViewById(R.id.profile_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(v, getAdapterPosition());

                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
