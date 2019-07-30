package com.tsnanh.myspecialnote.model;


import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

@Entity (nameInDb = "Notes")
public class NoteModel implements Parcelable {

    @Id(autoincrement = true)
    private long id;

    @Property(nameInDb = "NoteTitle")
    private String noteTitle;

    @Property(nameInDb = "NoteContent")
    private String noteContent;

    @Property(nameInDb = "DateTime")
    private String datetime;

    @Property(nameInDb = "Image")
    private String image;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Property(nameInDb = "Color") @Unique
    private int color;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    protected NoteModel(Parcel parcel) {
        this.id = parcel.readLong();
        this.noteTitle = parcel.readString();
        this.noteContent = parcel.readString();
        this.datetime = parcel.readString();
        this.image = parcel.readString();
        this.color = parcel.readInt();
    }

    @Generated(hash = 1550272437)
    public NoteModel(long id, String noteTitle, String noteContent, String datetime,
            String image, int color) {
        this.id = id;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.datetime = datetime;
        this.image = image;
        this.color = color;
    }

    @Generated(hash = 1532285157)
    public NoteModel() {
    }

    public static final Creator<NoteModel> CREATOR = new Creator<NoteModel>() {
        @Override
        public NoteModel createFromParcel(Parcel parcel) {
            return new NoteModel(parcel);
        }

        @Override
        public NoteModel[] newArray(int i) {
            return new NoteModel[i];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeString(this.noteTitle);
        parcel.writeString(this.noteContent);
        parcel.writeString(this.datetime);
        parcel.writeString(this.image);
        parcel.writeInt(this.color);
    }
}
