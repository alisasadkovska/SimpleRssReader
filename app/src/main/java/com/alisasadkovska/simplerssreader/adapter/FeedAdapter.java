package com.alisasadkovska.simplerssreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.alisasadkovska.simplerssreader.Interface.ItemClickListener;
import com.alisasadkovska.simplerssreader.R;
import com.alisasadkovska.simplerssreader.common.Common;
import com.alisasadkovska.simplerssreader.model.database.Item;
import com.alisasadkovska.simplerssreader.model.database.RSSObject;
import com.alisasadkovska.simplerssreader.ui.NewsDetailActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;

import static com.alisasadkovska.simplerssreader.common.Common.FAVORITE;
import static com.alisasadkovska.simplerssreader.common.Common.FEED_SIZE;
import static com.alisasadkovska.simplerssreader.common.Common.THEME_ID;
import static com.alisasadkovska.simplerssreader.common.Common.TITLE_SIZE;
import static com.alisasadkovska.simplerssreader.common.Common.coverPath;

class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    TextView txtTitle, txtPubDate, txtCategory;
    ImageView coverImage;
    ImageButton favoriteButton;
    ProgressBar progressBar;
    private ItemClickListener itemClickListener;

    FeedViewHolder(@NonNull View itemView) {
        super(itemView);
        txtTitle = itemView.findViewById(R.id.textTitle);
        txtCategory = itemView.findViewById(R.id.textCategory);
        txtPubDate = itemView.findViewById(R.id.publishedAt);
        coverImage = itemView.findViewById(R.id.newsCoverImage);
        progressBar = itemView.findViewById(R.id.progressBar);
        favoriteButton = itemView.findViewById(R.id.favoriteButton);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

     void setItemClickListener(ItemClickListener itemClickListener) {
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
    private ArrayList<Item>favoriteList;


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
    public void onBindViewHolder(@NonNull final FeedViewHolder holder, final int position) {
        final String title = rssObject.getItems().get(position).getTitle();
        holder.txtTitle.setText(title);
        holder.txtTitle.setTextSize((Float) Paper.book().read(TITLE_SIZE));

        if (rssObject.getItems().get(position).getCategories().size()!=0){
            String category =  rssObject.getItems().get(position).getCategories().get(0);
            if (category!=null)
                holder.txtCategory.setText(category);
        }

        holder.txtPubDate.setText(rssObject.getItems().get(position).getPubDate());
        String coverImageUrl = rssObject.getItems().get(position).getEnclosure().getLink();

        if (coverImageUrl!=null){
            Picasso.get().load(rssObject.getItems().get(position).getEnclosure().getLink()).into(holder.coverImage, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    holder.progressBar.setVisibility(View.GONE);
                    Picasso.get().load(R.drawable.ic_terrain_red_24dp).into(holder.coverImage);
                }
            });
       }else {
            Picasso.get().load(coverPath).into(holder.coverImage, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(R.drawable.ic_terrain_red_24dp).into(holder.coverImage);
                    holder.progressBar.setVisibility(View.GONE);
                }
            });
        }


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (!isLongClick){
                    Intent intent = new Intent(context, NewsDetailActivity.class);

                    intent.putExtra("url", rssObject.getItems().get(position).getLink());
                    String title = rssObject.getItems().get(position).getTitle();
                    title = title.replaceAll("(<.*?>)|(&.*?;)|([ ]{2,})", "");
                    intent.putExtra("title", title);
                    intent.putExtra("img",  rssObject.getItems().get(position).getEnclosure().getLink());
                    intent.putExtra("date", rssObject.getItems().get(position).getPubDate());
                    intent.putExtra("source",  rssObject.getItems().get(position).getGuid());
                    intent.putExtra("author",  rssObject.getItems().get(position).getAuthor());
                    String content = rssObject.getItems().get(position).getContent();
                    content = content.replace("<p>", "");
                    content = content.replace("</p>", " \n");
                    content = content.replaceAll("(<.*?>)|(&.*?;)|([ ]{2,})", "");

                    intent.putExtra("content", content);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

        if (Paper.book().contains(FAVORITE))
            favoriteList = Paper.book().read(FAVORITE);
        else favoriteList = new ArrayList<>();



        int themeId = Paper.book().read(THEME_ID);

        if (themeId==0)
            holder.favoriteButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_save_white_24dp));
        else
            holder.favoriteButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_save_black_24dp));

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i =0; i<favoriteList.size();i++){
                    if (favoriteList.get(i).getTitle().contains(title)){
                        Toasty.warning(context, title + context.getString(R.string.present), Toasty.LENGTH_SHORT).show();
                        return;
                    }
                }
                        favoriteList.add(rssObject.getItems().get(position));

                            for (int i =0; i < favoriteList.size(); i++){
                                if (favoriteList.get(i).getEnclosure().getLink()==null)
                                favoriteList.get(i).getEnclosure().setLink(coverPath);
                            }




                        Paper.book().write(FAVORITE, favoriteList);
                        Toasty.info(context, title + context.getString(R.string.saved), Toasty.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return rssObject.getItems().size();
    }
}
