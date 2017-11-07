package com.pdf.reader.pdfviewer.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pdf.reader.pdfviewer.R;
import com.pdf.reader.pdfviewer.entity.MyFolder;

/**
 * Created by truon on 10/26/2017.
 */

public class FolderViewHolder extends RecyclerView.ViewHolder {
    public interface FolderListener{
        public void onFolderResult(MyFolder folder);
    }
    private FolderListener listener;
    private TextView textView;
    public FolderViewHolder(View itemView, FolderListener listener) {
        super(itemView);
        this.listener = listener;
        textView = itemView.findViewById(R.id.title_folder);
    }

    public void setContent(final MyFolder folder){
        textView.setText(folder.getName());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFolderResult(folder);
            }
        });
    }
}
