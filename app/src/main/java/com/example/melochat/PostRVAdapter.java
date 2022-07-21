package com.example.melochat;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.melochat.models.PostItem;

import java.util.ArrayList;

public class PostRVAdapter extends RecyclerView.Adapter<PostRVAdapter.PostRVHolder> {
    private final ArrayList<PostItem> postsList;

    //Constructor
    public PostRVAdapter(ArrayList<PostItem> postsList) {
        this.postsList = postsList;
    }

    @Override
    public PostRVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_post, parent, false);
        return new PostRVHolder(view);
    }

    @Override
    public void onBindViewHolder(PostRVHolder holder, int position) {
        PostItem currentItem = postsList.get(position);

        //TODO Get username and name from user Id database
        holder.username.setText(currentItem.getUserId());
        holder.genre.setText(currentItem.getGenre());
        holder.content.setText(currentItem.getContent());
        holder.timestamp.setText(currentItem.getTimestamp());
        Uri uri = Uri.parse(currentItem.getContent());
        //TODO Add Picasso
        //Picasso.get().load(uri).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public class PostRVHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView fullName;
        public TextView genre;
        public TextView content;
        public TextView timestamp;
        public ImageView media;

        public PostRVHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.textView_username);
            fullName = itemView.findViewById(R.id.textView_name);
            genre = itemView.findViewById(R.id.textView_genre);
            content = itemView.findViewById(R.id.textView_post);
            timestamp = itemView.findViewById(R.id.textView_timestamp);
            media = itemView.findViewById(R.id.textView_media);
        }
    }

}
