package com.example.melochat;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melochat.models.PostItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ProfileRVAdapter extends RecyclerView.Adapter<ProfileRVAdapter.ProfileRVHolder> {
    private Uri uri;
    private ArrayList<PostItem> postsList;
    private DatabaseReference database;

    // Constructor
    public ProfileRVAdapter(ArrayList<PostItem> postsList){
        this.postsList = postsList;
    }

    @NonNull
    @Override
    public ProfileRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_profile_item, parent, false);
        return new ProfileRVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileRVHolder holder, int position) {
        database = FirebaseDatabase.getInstance().getReference();
        PostItem currentItem = postsList.get(position);

        holder.name.setText(currentItem.getUserName());
        holder.genre.setText(currentItem.getGenre());
        holder.content.setText(currentItem.getContent());
        holder.timestamp.setText(currentItem.getTimestamp());

        if (currentItem.getMedia() != null){
            uri = Uri.parse(currentItem.getMedia());
        }

        holder.mediaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentItem.getMedia() != null){
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW", Uri.parse(uri.toString()));
                    Bundle extras = viewIntent.getExtras();
                    startActivity(holder.mediaView.getContext(), viewIntent, extras);
                }
            }
        });
        holder.likeCount.setText(currentItem.getLikes().toString());
        holder.commentCount.setText(currentItem.getComments().toString());
        holder.repostCount.setText(currentItem.getReposts().toString());

        // generate thumbnail from URI
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentItem.addLike();
                // update postList
                postsList.set(holder.getAdapterPosition(), currentItem);
                // Update likeCount
                holder.likeCount.setText(currentItem.getLikes().toString());
                // update database
                String timestamp = currentItem.getTimestamp();
                database.child("posts").child(timestamp).child("likes").setValue(currentItem.getLikes())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Utils.postToastMessage("Successfully updated likes!", v.getContext());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Utils.postToastMessage("Failed to update likes!", v.getContext());
                            }
                        });

            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentItem.addComment();
                // update postlist
                postsList.set(holder.getAdapterPosition(), currentItem);
                // update UI
                holder.commentCount.setText(currentItem.getComments().toString());
                // update database
                String timestamp = currentItem.getTimestamp();
                database.child("posts").child("comments").setValue(currentItem.getComments())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Utils.postToastMessage("Successfully updated comments!", v.getContext());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Utils.postToastMessage("Failed to update comments!", v.getContext());
                            }
                        });
            }
        });

        // add reposts event
        holder.repost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentItem.addRepost();
                // update postList
                postsList.set(holder.getAdapterPosition(), currentItem);
                // Update UI
                holder.repostCount.setText(currentItem.getReposts().toString());
                // update the database
                String timestamp = currentItem.getTimestamp();
                database.child("posts").child(timestamp).child("reposts").setValue(currentItem.getReposts())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Utils.postToastMessage("Successfully updated reposts!", v.getContext());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Utils.postToastMessage("Failed to update reposts.",v.getContext());
                            }
                        });
            }
        });
    }

    public ArrayList<PostItem> getPostsList() {
        return postsList;
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }


    public class ProfileRVHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView genre;
        public TextView content;
        public TextView timestamp;
        public ImageView mediaView;
        public Button like;
        public Button comment;
        public Button repost;
        public TextView likeCount;
        public TextView commentCount;
        public TextView repostCount;

        public ProfileRVHolder(@NonNull View itemView) {
            super(itemView);
            // add all cardview attribute tags! 
            name = itemView.findViewById(R.id.textView_name);
            genre = itemView.findViewById(R.id.textView_genre);
            content = itemView.findViewById(R.id.textView_post);
            timestamp = itemView.findViewById(R.id.textView_timestamp);
            mediaView = itemView.findViewById(R.id.imageView_thumbnail);
            like = itemView.findViewById(R.id.button_like);
            comment = itemView.findViewById(R.id.button_comment);
            repost = itemView.findViewById(R.id.button_repost);
            likeCount = itemView.findViewById(R.id.textView_like);
            commentCount = itemView.findViewById(R.id.textView_comment);
            repostCount = itemView.findViewById(R.id.textView_repost);
        }
    }
}
