package com.example.melochat;

import static android.provider.MediaStore.Video.Thumbnails.MINI_KIND;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melochat.models.PostItem;

import java.util.ArrayList;

public class PostRVAdapter extends RecyclerView.Adapter<PostRVAdapter.PostRVHolder> {
    private final ArrayList<PostItem> postsList;
    private View context;
    Bitmap bmThumbnail;

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

        Uri uri = Uri.parse(currentItem.getMediaURL());

        //Glide.with(holder.media.getContext()).load(uri).into(holder.media);

        //Picasso.get().load(uri.toString()).into(holder.media);

//        bmThumbnail = getThumblineImage(uri.toString());
//        holder.media.setImageBitmap(bmThumbnail);

        holder.media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse(uri.toString()));
                Bundle extras = viewIntent.getExtras();
                startActivity(holder.media.getContext(),viewIntent,extras);
            }
        });
    }

    //Reference: https://stackoverflow.com/questions/23522124/android-display-a-video-thumbnail-from-a-url
    public static Bitmap getThumblineImage(String videoPath) {
        return ThumbnailUtils.createVideoThumbnail(videoPath, MINI_KIND);
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
        public ImageButton media;

        public PostRVHolder(View itemView) {
            super(itemView);
            //email = itemView.findViewById(R.id.textView_email);
            name = itemView.findViewById(R.id.textView_name);
            genre = itemView.findViewById(R.id.textView_genre);
            content = itemView.findViewById(R.id.textView_post);
            timestamp = itemView.findViewById(R.id.textView_timestamp);
            media = (ImageButton) itemView.findViewById(R.id.imageView_thumbnail);

        }
    }

}
