package edu.hust.truongvu.pdfviewer.entity;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by truon on 10/24/2017.
 */

public class MyFile implements Parcelable{
    private String name;
    private String path;
    private String parent;
    private Bitmap imageBitmap;

    public MyFile(String name, String path, String parent, Bitmap imageBitmap) {
        this.name = name;
        this.path = path;
        this.parent = parent;
        this.imageBitmap = imageBitmap;
    }

    protected MyFile(Parcel in) {
        name = in.readString();
        path = in.readString();
        parent = in.readString();
        imageBitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }


    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public static final Creator<MyFile> CREATOR = new Creator<MyFile>() {
        @Override
        public MyFile createFromParcel(Parcel in) {
            return new MyFile(in);
        }

        @Override
        public MyFile[] newArray(int size) {
            return new MyFile[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(path);
        parcel.writeString(parent);
        parcel.writeParcelable(imageBitmap, i);
    }
}
