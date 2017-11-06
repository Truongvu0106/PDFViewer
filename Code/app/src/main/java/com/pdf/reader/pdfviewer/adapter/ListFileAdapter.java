package com.pdf.reader.pdfviewer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import com.pdf.reader.pdfviewer.entity.MyFile;
import com.pdf.reader.pdfviewer.R;
import com.pdf.reader.pdfviewer.holder.FileViewHolder;

/**
 * Created by truon on 10/19/2017.
 */

public class ListFileAdapter extends RecyclerView.Adapter<FileViewHolder> {
    private static final int LIST_ITEM = 0;
    private static final int GRID_ITEM = 1;
    boolean isSwitch = true;

    private ArrayList<MyFile> myFiles;
    private Context context;
    private FileViewHolder.FileListener listener;

    public ListFileAdapter(ArrayList<MyFile> myFiles, Context context, FileViewHolder.FileListener listener){
        this.myFiles = myFiles;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == LIST_ITEM){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_list, parent, false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_grid, parent, false);
        }
        FileViewHolder holder = new FileViewHolder(view, context, new FileViewHolder.FileListener() {
            @Override
            public void onFileResult(MyFile file) {
                listener.onFileResult(file);
            }

            @Override
            public void onMoreInfo(MyFile file) {
                listener.onMoreInfo(file);
            }

            @Override
            public void shareFile(MyFile file) {
                listener.shareFile(file);
            }

            @Override
            public void deleteFile(MyFile file) {
                listener.deleteFile(file);
            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        MyFile file = myFiles.get(position);
        holder.setContent(file);
    }

    @Override
    public int getItemCount() {
        return myFiles.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isSwitch){
            return LIST_ITEM;
        }else{
            return GRID_ITEM;
        }
    }

    public boolean toggleItemViewType () {
        isSwitch = !isSwitch;
        return isSwitch;
    }


}
