package com.pdf.reader.pdfviewer.customview;

import android.app.Activity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.pdf.reader.pdfviewer.adapter.SearchAdapter;
import com.pdf.reader.pdfviewer.entity.MyFile;
import com.pdf.reader.pdfviewer.R;

/**
 * Created by truon on 10/24/2017.
 */

public class MyActionBar {

    public static int FLAG_START = 1;
    public static int FLAG_MAIN = 2;
    public static int FLAG_VIEW_FILE = 3;
//    public static int FLAG_ALL_FILE = 4;
    public interface ActionBarListener{
        public void onSwitchMain();
        public void onSwitchViewFile();
        public void onJumpViewFile();
        public void onSelectItemSearch(MyFile file);
//        public void onShowAll();
    }
    private ActionBarListener listener;
    private View view;
    private View layoutSearch;
    private View back, option;
    private TextView textView;
    private ImageView clear;
    private AutoCompleteTextView autoCompleteTextView;
    public MyActionBar(final Activity context, int flag, final ArrayList<MyFile> dataSearch, final ActionBarListener listener){
        view = View.inflate(context, R.layout.actionbar_layout, null);
        back = view.findViewById(R.id.back);
        option = view.findViewById(R.id.myoption);
        textView = view.findViewById(R.id.mytitle);
        layoutSearch = view.findViewById(R.id.layout_search);
        autoCompleteTextView = view.findViewById(R.id.search);
        clear = view.findViewById(R.id.clear);
        this.listener = listener;

        if (flag == FLAG_MAIN){
            back.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            layoutSearch.setVisibility(View.GONE);
            SearchAdapter searchAdapter = new SearchAdapter(context, dataSearch, new SearchAdapter.SearchListener() {
                @Override
                public void onSearchItemSelect(MyFile file) {
                    listener.onSelectItemSearch(file);
                }
            });
            autoCompleteTextView.setAdapter(searchAdapter);
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    autoCompleteTextView.setText("");
                }
            });
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.onBackPressed();
                }
            });
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(context, option);
                    popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == R.id.switch_view){
                                listener.onSwitchMain();
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });
        }else if (flag == FLAG_VIEW_FILE){
            back.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            layoutSearch.setVisibility(View.GONE);

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.onBackPressed();
                }
            });

            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(context, option);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.view_file_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == R.id.switch_view_type) {
                                listener.onSwitchViewFile();
                            } else if (id == R.id.jump) {
                                listener.onJumpViewFile();
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });
        }else if (flag == FLAG_START){
            back.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            layoutSearch.setVisibility(View.VISIBLE);

            SearchAdapter searchAdapter = new SearchAdapter(context, dataSearch, new SearchAdapter.SearchListener() {
                @Override
                public void onSearchItemSelect(MyFile file) {
                    listener.onSelectItemSearch(file);
                }
            });
            autoCompleteTextView.setAdapter(searchAdapter);
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    autoCompleteTextView.setText("");
                }
            });

            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(context, option);
                    popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == R.id.switch_view){
                                listener.onSwitchMain();
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });
        }
    }

    public View getView(){
        return view;
    }

    public void setTitle(CharSequence sequence){
        textView.setText(sequence);
    }
}
