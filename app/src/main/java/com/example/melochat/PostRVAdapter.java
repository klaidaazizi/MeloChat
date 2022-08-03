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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class PostRVAdapter extends RecyclerView.Adapter<PostRVAdapter.PostRVHolder> {
    private ArrayList<PostItem> postsList;
    private DatabaseReference database;


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

    public ArrayList<PostItem> getPostsList() {
        return postsList;
    }

    @Override
    public void onBindViewHolder(PostRVHolder holder, int position) {
        database = FirebaseDatabase.getInstance().getReference();

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
                // Update postsList
                postsList.set(holder.getAdapterPosition(), currentItem);
                // Update UI
                holder.likeCount.setText(currentItem.getLikes().toString());
                // Update database
                String timestamp = currentItem.getTimestamp();
                database.child("posts").child(timestamp).child("likes").setValue(currentItem.getLikes())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Utils.postToastMessage("Successfully updated likes!", view.getContext());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Utils.postToastMessage("Failed to update likes.",view.getContext());
                            }
                        });
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentItem.addComment();
                // Update postsList
                postsList.set(holder.getAdapterPosition(), currentItem);
                // Update UI
                holder.commentCount.setText(currentItem.getComments().toString());
                // Update database
                String timestamp = currentItem.getTimestamp();
                database.child("posts").child(timestamp).child("comments").setValue(currentItem.getLikes())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Utils.postToastMessage("Successfully updated comments!", view.getContext());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Utils.postToastMessage("Failed to update comments.", view.getContext());
                            }
                        });
            }
        });

        holder.repost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentItem.addRepost();
                // Update postsList
                postsList.set(holder.getAdapterPosition(), currentItem);
                // Update UI
                holder.repostCount.setText(currentItem.getReposts().toString());
                // Update database
                String timestamp = currentItem.getTimestamp();
                database.child("posts").child(timestamp).child("reposts").setValue(currentItem.getReposts())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Utils.postToastMessage("Successfully updated reposts!", view.getContext());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Utils.postToastMessage("Failed to update reposts.",view.getContext());
                            }
                        });
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
