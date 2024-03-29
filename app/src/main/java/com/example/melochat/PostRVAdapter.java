package com.example.melochat;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melochat.models.PostItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class PostRVAdapter extends RecyclerView.Adapter<PostRVAdapter.PostRVHolder> {
    private ArrayList<PostItem> postsList;
    private DatabaseReference database;
    private StorageReference mStorage;
    private StorageReference profileImagesRef;

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
        holder.name.setText(currentItem.getUserName());
        holder.genre.setText(currentItem.getGenre());
        holder.content.setText(currentItem.getContent());
        holder.timestamp.setText(currentItem.getTimestamp());


        // Set photo of posted by user
        mStorage = FirebaseStorage.getInstance().getReference();
        profileImagesRef = mStorage.child("profileImages");

        String posterUID = currentItem.getUserId();

        // Get URL of profile image named by their userID and update UI
        profileImagesRef.child(posterUID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.posterPhoto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });



        if (currentItem.getMedia() != null) {
            Uri uri = Uri.parse(currentItem.getMedia());

            holder.mediaView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentItem.getMedia() != null) {
                        Intent viewIntent =
                                new Intent("android.intent.action.VIEW", Uri.parse(uri.toString()));
                        viewIntent.setPackage("com.android.chrome");
                        Bundle extras = viewIntent.getExtras();
                        startActivity(holder.mediaView.getContext(), viewIntent, extras);
                    }
                }
            });
        }


        holder.likeCount.setText(currentItem.getLikes().toString());
        holder.commentCount.setText(currentItem.getCommentsNumber().toString());
        holder.repostCount.setText(currentItem.getReposts().toString());

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
                database.child("postsWithComments").child(timestamp).child("likes").setValue(currentItem.getLikes())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Add a Comment");
                final EditText comment = new EditText(view.getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                comment.setLayoutParams(lp);
                builder.setView(comment);

                builder.setPositiveButton("Enter",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String text = comment.getText().toString();
                                // Update current item
                                currentItem.addComment(text);
                                // Update postsList
                                postsList.set(holder.getAdapterPosition(), currentItem);
                                // Update UI
                                holder.commentCount.setText(currentItem.getCommentsNumber().toString());
                                // Update database
                                String timestamp = currentItem.getTimestamp();
                                DatabaseReference commentRef = database.child("postsWithComments").child(timestamp).child("comments").getRef();
                                commentRef.push().setValue(text);

                                dialog.cancel();
                            }
                        });

               builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.show();
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
                database.child("postsWithComments").child(timestamp).child("reposts").setValue(currentItem.getReposts())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
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

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> commentsList= currentItem.getComments();
                String comments[] = commentsList.toArray(new String[commentsList.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("All Comments");
                builder.setItems(comments, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setNegativeButton("Return",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                builder.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public class PostRVHolder extends RecyclerView.ViewHolder {
        public TextView email;
        public TextView name;
        public TextView genre;
        public TextView content;
        public TextView timestamp;
        public ImageView mediaView;
        public ImageView posterPhoto;
        public Button like;
        public Button comment;
        public Button repost;
        public Button more;
        public TextView likeCount;
        public TextView commentCount;
        public TextView repostCount;

        public PostRVHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView_name);
            genre = itemView.findViewById(R.id.textView_genre);
            content = itemView.findViewById(R.id.textView_post);
            timestamp = itemView.findViewById(R.id.textView_timestamp);
            mediaView = (ImageView) itemView.findViewById(R.id.imageView_thumbnail);
            posterPhoto = (ImageView) itemView.findViewById(R.id.imageView_userPhoto);
            like = itemView.findViewById(R.id.button_like);
            comment = itemView.findViewById(R.id.button_comment);
            repost = itemView.findViewById(R.id.button_repost);
            more = itemView.findViewById(R.id.button_more);
            likeCount = itemView.findViewById(R.id.textView_like);
            commentCount = itemView.findViewById(R.id.textView_comment);
            repostCount = itemView.findViewById(R.id.textView_repost);
        }
    }
}
