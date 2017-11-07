package com.pdf.reader.pdfviewer.holder;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdf.reader.pdfviewer.R;
import com.pdf.reader.pdfviewer.entity.MyFile;
import com.pdf.reader.pdfviewer.helper.MyHelper;

/**
 * Created by truon on 10/26/2017.
 */

public class FileViewHolder extends RecyclerView.ViewHolder {
    public interface FileListener{
         void onFileResult(MyFile file);
         void onMoreInfo(MyFile file);
         void shareFile(MyFile file);
         void deleteFile(MyFile file);
    }
    private FileListener listener;
    private Context context;
    private TextView textView;
    private View more;
    private ImageView img;

    public FileViewHolder(View itemView, Context context, FileListener listener) {
        super(itemView);
        this.context = context;
        this.listener = listener;
        textView = itemView.findViewById(R.id.title_file);
        img = itemView.findViewById(R.id.image_pdf);
        more = itemView.findViewById(R.id.more);
    }

    public void setContent(final MyFile file){
        textView.setText(MyHelper.formatTitle(file.getName(), 35));
        img.setImageBitmap(file.getImageBitmap());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFileResult(file);
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, more);
                popup.getMenuInflater().inflate(R.menu.more_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.more_info){
                            listener.onMoreInfo(file);
                        }else if(id == R.id.share_file){
                            listener.shareFile(file);
                        }else if (id == R.id.delete_file){
                            listener.deleteFile(file);
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }
}
