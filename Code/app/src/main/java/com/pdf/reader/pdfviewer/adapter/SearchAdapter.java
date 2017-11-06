package com.pdf.reader.pdfviewer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.pdf.reader.pdfviewer.R;
import com.pdf.reader.pdfviewer.entity.MyFile;
import com.pdf.reader.pdfviewer.helper.MyHelper;

/**
 * Created by truon on 10/25/2017.
 */

public class SearchAdapter extends ArrayAdapter<MyFile> {
    public interface SearchListener{
        public void onSearchItemSelect(MyFile file);
    }
    private SearchListener listener;
    private ArrayList<MyFile> datas, temps, suggestions;
    TextView textView;
    ImageView imageView;
    public SearchAdapter(@NonNull Context context, ArrayList<MyFile> data, SearchListener listener) {
        super(context, android.R.layout.simple_list_item_1, data);
        this.datas = data;
        this.listener = listener;
        temps = new ArrayList<MyFile>(data);
        suggestions = new ArrayList<MyFile>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final MyFile myFile = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_search, parent, false);
        }
        textView = convertView.findViewById(R.id.title_search);
        imageView = convertView.findViewById(R.id.image_search);
        textView.setText(MyHelper.formatTitle(myFile.getName(), 35));
        imageView.setImageBitmap(myFile.getImageBitmap());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSearchItemSelect(myFile);
            }
        });
        return convertView;

    }

    @NonNull
    @Override
    public Filter getFilter() {
        return myFilter;
    }

    Filter myFilter = new Filter() {

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            MyFile myFile = (MyFile) resultValue;
            String result = MyHelper.formatTitle(myFile.getName(), 20);
            return result;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence != null) {
                suggestions.clear();
                for (MyFile myFile : temps) {
                    if (myFile.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        suggestions.add(myFile);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            ArrayList<MyFile> f = (ArrayList<MyFile>) filterResults.values;
            if (filterResults != null && filterResults.count > 0) {
                clear();
                for (MyFile file : f) {
                    add(file);
                    notifyDataSetChanged();
                }
            }
            else{
                clear();
                notifyDataSetChanged();
            }
        }


    };
}
