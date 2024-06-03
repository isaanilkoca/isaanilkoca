package com.isaanilkoca.javamaps.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.isaanilkoca.javamaps.R;
import com.isaanilkoca.javamaps.adapter.PlaceAdapter;
import com.isaanilkoca.javamaps.databinding.ActivityMainBinding;
import com.isaanilkoca.javamaps.model.Place;
import com.isaanilkoca.javamaps.roomdb.PlaceDao;
import com.isaanilkoca.javamaps.roomdb.PlaceDatabase;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    PlaceDatabase db;
    PlaceDao placeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        db= Room.databaseBuilder(getApplicationContext(),PlaceDatabase.class,"Places").build();
        placeDao=db.placeDao();


        compositeDisposable.add(placeDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MainActivity.this::handleResponse)
        );

    }

    private void handleResponse(List<Place> placeList){

    binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        PlaceAdapter placeAdapter= new PlaceAdapter(placeList);
        binding.recyclerView.setAdapter(placeAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();//menüyü baglamak icin menu ınflater kullanılır
        menuInflater.inflate(R.menu.travel_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // menuya tıklanırsa nolacak
        if (item.getItemId() == R.id.add_place) {
            if (item.getItemId() == R.id.add_place)// add place mi tıklandı gercekten diye kontrol ederiz
            {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class); // mainden mapse gidicez demek
                intent.putExtra("info","new");
                startActivity(intent);
            }
            return super.onOptionsItemSelected(item);
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}

