package com.pdf.reader.pdfviewer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import com.pdf.reader.pdfviewer.R;
import com.pdf.reader.pdfviewer.entity.MyFile;
import com.pdf.reader.pdfviewer.entity.MyFolder;
import com.pdf.reader.pdfviewer.holder.FileViewHolder;
import com.pdf.reader.pdfviewer.holder.FileViewHolder.*;
import com.pdf.reader.pdfviewer.holder.FolderViewHolder;
import com.pdf.reader.pdfviewer.holder.FolderViewHolder.*;

/**
 * Created by truon on 10/26/2017.
 */

public class MultitypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int FILE_TYPE_LIST = 0;
    public static final int FOLDER_TYPE_LIST = 1;
    public static final int FILE_TYPE_GRID = 2;
    public static final int FOLDER_TYPE_GRID = 3;
    private ArrayList<Object> data;
    boolean isSwitch = true;
    private Context context;
    private FileListener fileListener;
    private FolderListener folderListener;

    public MultitypeAdapter(ArrayList<Object> data, Context context, FileListener fileListener, FolderListener folderListener){
        this.data = data;
        this.context = context;
        this.fileListener = fileListener;
        this.folderListener = folderListener;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType){
            case FILE_TYPE_LIST:
                View fileView = inflater.inflate(R.layout.item_file_list, parent, false);
                return new FileViewHolder(fileView, context, fileListener);
            case FOLDER_TYPE_LIST:
                View folderView = inflater.inflate(R.layout.item_folder_list, parent, false);
                return new FolderViewHolder(folderView, folderListener);
            case FILE_TYPE_GRID:
                View fileViewGrid = inflater.inflate(R.layout.item_file_grid, parent, false);
                return new FileViewHolder(fileViewGrid, context, fileListener);
            case FOLDER_TYPE_GRID:
                View folderViewGrid = inflater.inflate(R.layout.item_folder_grid, parent, false);
                return new FolderViewHolder(folderViewGrid, folderListener);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case FILE_TYPE_LIST:
                MyFile file = (MyFile) data.get(position);
                FileViewHolder fileHolder = (FileViewHolder) holder;
                fileHolder.setContent(file);
                break;
            case FOLDER_TYPE_LIST:
                MyFolder folder = (MyFolder) data.get(position);
                FolderViewHolder folderholder = (FolderViewHolder) holder;
                folderholder.setContent(folder);
                break;
            case FILE_TYPE_GRID:
                MyFile fileGrid = (MyFile) data.get(position);
                FileViewHolder fileHolderGrid = (FileViewHolder) holder;
                fileHolderGrid.setContent(fileGrid);
                break;
            case FOLDER_TYPE_GRID:
                MyFolder folderGrid = (MyFolder) data.get(position);
                FolderViewHolder folderholderGrid = (FolderViewHolder) holder;
                folderholderGrid.setContent(folderGrid);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isSwitch){
            if (data.get(position) instanceof MyFile){
                return FILE_TYPE_LIST;
            }else if (data.get(position) instanceof MyFolder){
                return FOLDER_TYPE_LIST;
            }
        }else {
            if (data.get(position) instanceof MyFile){
                return FILE_TYPE_GRID;
            }else if (data.get(position) instanceof MyFolder){
                return FOLDER_TYPE_GRID;
            }
        }

        return -1;
    }

    public boolean toggleItemViewType () {
        isSwitch = !isSwitch;
        return isSwitch;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
