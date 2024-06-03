package com.isaanilkoca.javamaps.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Place implements Serializable { // room bunu kullanacagını anlamaz entity koymazsak

    @PrimaryKey(autoGenerate = true) // otomatiktan bütün idler olusur
    public int id;

    @ColumnInfo(name="name")
    public String name;

    @ColumnInfo(name="latitude")
    public double latitude;

    @ColumnInfo(name="longitude")
    public double longitude;

    public Place(String name,double latitude,double longitude){
     this.name=name;
     this.latitude=latitude;
     this.longitude=longitude;

    }



}
