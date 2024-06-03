package com.isaanilkoca.javamaps.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.isaanilkoca.javamaps.model.Place;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao // date access objet verilere erişim arayüzüdür
public interface PlaceDao {

    @Query("SELECT * FROM Place")
    Flowable<List<Place>> getAll(); // ne döndürecegi sonra ne parametresi isteyecegini yaziyoruz


    @Insert
    Completable insert(Place place);

    @Delete
    Completable delete(Place place);






}
