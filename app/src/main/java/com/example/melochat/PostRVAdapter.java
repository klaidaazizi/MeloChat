package com.example.melochat;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melochat.models.PostItem;

import java.util.ArrayList;

public class PostRVAdapter extends RecyclerView.Adapter<PostRVAdapter.PostRVHolder> {
    private final ArrayList<PostItem> postsList;

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
        holder.name.setText(currentItem.getUserName());
        holder.genre.setText(currentItem.getGenre());
        holder.content.setText(currentItem.getContent());
        holder.timestamp.setText(currentItem.getTimestamp());
        holder.likeCount.setText(currentItem.getLikes().toString());
        holder.commentCount.setText(currentItem.getComments().toString());
        holder.repostCount.setText(currentItem.getReposts().toString());
        //Uri uri = Uri.parse(currentItem.getMedia());
        //TODO Generate thumbnail from uri

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentItem.addLike();
                holder.likeCount.setText(currentItem.getLikes().toString());
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentItem.addComment();
                holder.commentCount.setText(currentItem.getComments().toString());
            }
        });

        holder.repost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentItem.addRepost();
                holder.repostCount.setText(currentItem.getReposts().toString());
            }
        });
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
        public Button like;
        public Button comment;
        public Button repost;
        public TextView likeCount;
        public TextView commentCount;
        public TextView repostCount;



        public PostRVHolder(View itemView) {
            super(itemView);
            //email = itemView.findViewById(R.id.textView_email);
            name = itemView.findViewById(R.id.textView_name);
            genre = itemView.findViewById(R.id.textView_genre);
            content = itemView.findViewById(R.id.textView_post);
            timestamp = itemView.findViewById(R.id.textView_timestamp);
            like = itemView.findViewById(R.id.button_like);
            comment = itemView.findViewById(R.id.button_comment);
            repost = itemView.findViewById(R.id.button_repost);
            likeCount = itemView.findViewById(R.id.textView_like);
            commentCount = itemView.findViewById(R.id.textView_comment);
            repostCount = itemView.findViewById(R.id.textView_repost);
            //media = itemView.findViewById(R.id.textView_media);
        }
    }

}
