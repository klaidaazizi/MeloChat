package com.example.melochat;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.melochat.models.PostItem;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class PostRVAdapter extends RecyclerView.Adapter<PostRVAdapter.PostRVHolder> {
    private final ArrayList<PostItem> postsList;
    private View context;

    //Constructor
    public PostRVAdapter(ArrayList<PostItem> postsList) {
        this.postsList = postsList;
    }

    @NonNull
    @Override
    public PostRVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_feed_item, parent, false);
        return new PostRVHolder(view);
    }

    @Override
    public void onBindViewHolder(PostRVHolder holder, int position) {

        PostItem currentItem = postsList.get(position);
        //holder.email.setText(currentItem.getUserId());
        holder.name.setText(currentItem.getUserId());
        holder.genre.setText(currentItem.getGenre());
        holder.content.setText(currentItem.getContent());
        holder.timestamp.setText(currentItem.getTimestamp());

        //Uri uri = Uri.parse(currentItem.getMedia());
        //TODO Generate thumbnail from uri
//        Glide.with(context)
//        .load(uri)
//                .thumbnail(0.1f)
//                .into(holder.media);

    }

    @Override
    public int getItemCount() {
        //Log.e("POSTS", String.valueOf(postsList));
        return postsList.size();
    }

    public class PostRVHolder extends RecyclerView.ViewHolder {
        public TextView email;
        public TextView name;
        public TextView genre;
        public TextView content;
        public TextView timestamp;
        public ImageView media;

        public PostRVHolder(View itemView) {
            super(itemView);
            //email = itemView.findViewById(R.id.textView_email);
            name = itemView.findViewById(R.id.textView_name);
            genre = itemView.findViewById(R.id.textView_genre);
            content = itemView.findViewById(R.id.textView_post);
            timestamp = itemView.findViewById(R.id.textView_timestamp);
            //media = itemView.findViewById(R.id.textView_media);
        }
    }

}
