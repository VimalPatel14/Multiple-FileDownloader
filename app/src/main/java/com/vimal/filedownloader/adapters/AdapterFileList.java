package com.vimal.filedownloader.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vimal.filedownloader.R;
import com.vimal.filedownloader.models.FileModel;

import java.util.ArrayList;

public class AdapterFileList extends RecyclerView.Adapter {

    Context context;
    ArrayList<FileModel> data;
    onClickMenuItem click;

    public interface onClickMenuItem {
        void onClick(int position);
    }

    public AdapterFileList(Context context, ArrayList<FileModel> data, onClickMenuItem click) {
        this.context = context;
        this.data = data;
        this.click = click;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.adapter_file_list, parent, false);
        return new GalleryItemHolder(row);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final GalleryItemHolder subjectHolder = (GalleryItemHolder) holder;
        subjectHolder.navigationTitle.setText(data.get(position).getFileName());
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class GalleryItemHolder extends RecyclerView.ViewHolder {
        TextView navigationTitle;
        ImageView navigationThumb;
        LinearLayout mainCard;

        public GalleryItemHolder(View itemView) {
            super(itemView);
            mainCard = itemView.findViewById(R.id.mainCard);
            navigationTitle = itemView.findViewById(R.id.tvName);
            navigationThumb = itemView.findViewById(R.id.navigationThumb);
        }
    }


}