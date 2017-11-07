package com.pdf.reader.pdfviewer.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by truon on 10/26/2017.
 */

public class MyFolder implements Parcelable{
    private String name;
    private ArrayList<MyFile> listFile;

    public MyFolder(String name, ArrayList<MyFile> listFile) {
        this.name = name;
        this.listFile = listFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<MyFile> getListFile() {
        return listFile;
    }

    public void setListFile(ArrayList<MyFile> listFile) {
        this.listFile = listFile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
//        int length = this.getListFile().size();
//        Parcelable[] parcelables = new Parcelable[length];
//        for (int j = 0; j < length; j++){
//            parcelables[i] = (Parcelable) this.getListFile().get(j);
//        }
//        parcel.writeParcelableArray(parcelables, i);
//        parcel.writeList(this.listFile);
        parcel.writeTypedList(this.listFile);
    }

    protected MyFolder(Parcel in) {
        this.name = in.readString();
        listFile = new ArrayList<>();
        in.readTypedList(listFile, MyFile.CREATOR);
    }

    public static final Creator<MyFolder> CREATOR = new Creator<MyFolder>() {
        @Override
        public MyFolder createFromParcel(Parcel in) {
            return new MyFolder(in);
        }

        @Override
        public MyFolder[] newArray(int size) {
            return new MyFolder[size];
        }
    };
}
