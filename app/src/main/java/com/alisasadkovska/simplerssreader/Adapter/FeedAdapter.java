package com.alisasadkovska.simplerssreader.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alisasadkovska.simplerssreader.Interface.ItemClickListener;
import com.alisasadkovska.simplerssreader.R;
import com.alisasadkovska.simplerssreader.model.RSSObject;
import com.alisasadkovska.simplerssreader.ui.NewsDetailActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    public TextView txtTitle, txtPubDate, txtContent, txtAuthor;
    public ImageView coverImage;
    public ProgressBar progressBar;
    private ItemClickListener itemClickListener;

    public FeedViewHolder(@NonNull View itemView) {
        super(itemView);
        txtTitle = itemView.findViewById(R.id.textTitle);
        txtPubDate = itemView.findViewById(R.id.publishedAt);
        txtContent = itemView.findViewById(R.id.textDescription);
        txtAuthor = itemView.findViewById(R.id.textAuthor);
        coverImage = itemView.findViewById(R.id.newsCoverImage);

        progressBar = itemView.findViewById(R.id.progressBar);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    @Override
    public boolean onLongClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), true);
        return true;
    }
}

public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder>{

    private RSSObject rssObject;
    private Context context;
    private LayoutInflater inflater;

    public FeedAdapter(RSSObject rssObject, Context mContext) {
        this.rssObject = rssObject;
        this.context = mContext;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.news_item, parent, false);
        return new FeedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedViewHolder holder, int position) {
        holder.txtTitle.setText(rssObject.getItems().get(position).getTitle());
        holder.txtPubDate.setText(rssObject.getItems().get(position).getPubDate());
        holder.txtContent.setText(rssObject.getItems().get(position).getContent());
        holder.txtAuthor.setText(rssObject.getItems().get(position).getAuthor());

       Picasso.get().load(rssObject.getItems().get(position).getEnclosure().getLink()).into(holder.coverImage, new Callback() {
           @Override
           public void onSuccess() {
               holder.progressBar.setVisibility(View.GONE);
           }

           @Override
           public void onError(Exception e) {
               holder.progressBar.setVisibility(View.GONE);
           }
       });

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (!isLongClick){
                    Intent intent = new Intent(context, NewsDetailActivity.class);

                    intent.putExtra("url", rssObject.getItems().get(position).getLink());
                    intent.putExtra("title", rssObject.getItems().get(position).getTitle());
                    intent.putExtra("img",  rssObject.getItems().get(position).getEnclosure().getLink());
                    intent.putExtra("date", rssObject.getItems().get(position).getPubDate());
                    intent.putExtra("source",  rssObject.getItems().get(position).getGuid());
                    intent.putExtra("author",  rssObject.getItems().get(position).getAuthor());

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return rssObject.items.size();
    }
}
