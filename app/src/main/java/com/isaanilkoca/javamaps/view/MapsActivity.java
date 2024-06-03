package com.isaanilkoca.javamaps.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.shape.MarkerEdgeTreatment;
import com.google.android.material.snackbar.Snackbar;
import com.isaanilkoca.javamaps.R;
import com.isaanilkoca.javamaps.databinding.ActivityMapsBinding;
import com.isaanilkoca.javamaps.model.Place;
import com.isaanilkoca.javamaps.roomdb.PlaceDao;
import com.isaanilkoca.javamaps.roomdb.PlaceDatabase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener { // arayüz uygular burada

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    ActivityResultLauncher<String> permissionLauncher; // izin istemek icin olusturduk
    LocationManager locationManager;
    LocationListener locationListener;
    SharedPreferences sharedPreferences;
    boolean info;
    PlaceDatabase db;
    PlaceDao placeDao;
    Double selectedLatitude;
    Double selectedLongitude;
    Place selectedPlace;


    private CompositeDisposable compositeDisposable= new CompositeDisposable();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerLauncher(); // cagırdık

        sharedPreferences= this.getSharedPreferences("com.isaanilkoca.javamaps",MODE_PRIVATE);
        info=false;

        db= Room.databaseBuilder(getApplicationContext(),PlaceDatabase.class,"Places")
                .allowMainThreadQueries()//hatayı çözer ama önerilmez kullanılması kaydeder yani izin veriir
                .build();
        placeDao=db.placeDao();

        selectedLatitude=0.0;
        selectedLongitude=0.0;

        binding.saveButton.setEnabled(false);// bir yer secmeden etkin olmaz

    }



    @Override
    public void onMapReady(GoogleMap googleMap) { // harita hazır oldugunda yapılacak işlemler
         mMap=googleMap;
         mMap.setOnMapLongClickListener(this);// uzun tıklandıgında override ettigimiz metodu güncel haritamızda kullanacagımızı söyleeriz burada
     //casting islemi yapıldı (LocationManager)olarak kaydet demek

        Intent intent= getIntent();
        String intentInfo=intent.getStringExtra("info");

        if(intentInfo.equals("new")){

        binding.saveButton.setVisibility(View.VISIBLE); // yeni eklerken save butonu görünsün demek
        binding.deleteButton.setVisibility(View.GONE); // tamamen gider ve diger buton kalır demek
            locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);//konum yöneticisidir bütün islem bunun üzerinden döner
            locationListener= new LocationListener() { // bu degisikleri dinliyor ve degisim konumla ilgili yapılacak metodlar yapiliir

                @Override
                public void onLocationChanged(@NonNull Location location) {
//             System.out.println("location:"+location.toString()); // konum degisirse eger alıp yazdırırız

                    info=sharedPreferences.getBoolean("info",false);// info diye deger var yoksa degeri false demek

                    if(!info){ // infp yoksa demektir ünlem koymak basına kısasıdır
                        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());// locationda hem enlem hem boylam var
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
                        sharedPreferences.edit().putBoolean("info",true).apply();
                    }
                }

            };

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                //request permission esit degilse
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    Snackbar.make(binding.getRoot(),"Permission needed for maps",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //request permisson
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION); // izin isteme

                        }
                    }).show();
                }else{
                    //request permisson
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION); // izin isteme tekrar

                }
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastLocation!=null){
                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                }
                mMap.setMyLocationEnabled(true); // konumunun dogrulugundan emin ol demek

            }

        }else{
            mMap.clear();
            selectedPlace=(Place) intent.getSerializableExtra("place");

            LatLng latLng=new LatLng(selectedPlace.latitude,selectedLongitude);
            mMap.addMarker(new MarkerOptions().position(latLng).title(selectedPlace.name));

           mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

           binding.placeNameText.setText(selectedPlace.name);
           binding.saveButton.setVisibility(View.GONE);
           binding.deleteButton.setVisibility(View.VISIBLE);

        }
       // casting
//
//     LatLng eiffel=new LatLng(48.85851967240428, 2.2944744955006087);//enlem boylamı bir arada tutmak icin olusturulmus basit sınıf
//     mMap.addMarker(new MarkerOptions().position(eiffel).title("Eiffel Tower"));// kırmızı isaret buradadır
//     mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eiffel,15)); // konuma dogru odaklanır zoomla secenekleri vardır

    }
 private void registerLauncher(){
        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                //permission granted izin verildi
                    if(ContextCompat.checkSelfPermission(MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);// izin verildiyse naoiyim kısmıdır
                        // izin verildi mi yüzde yüz kontrol ettik

                        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(lastLocation!=null){
                            LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                        }

                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            }else{
                    //permission denied izin reddedildi
                    Toast.makeText(MapsActivity.this,"Permission neeeded!",Toast.LENGTH_LONG).show();
                }
            }
        });
 }
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) { // arayüzün metodu import ettik  haritaya basılı tuttugumuzda ne yapacagı yazar

        mMap.clear();// önceden konulmus markerları kaldırır
        mMap.addMarker(new MarkerOptions().position(latLng));
        selectedLatitude=latLng.latitude;
        selectedLongitude=latLng.longitude;
        binding.saveButton.setEnabled(true); // kullanıcı bir yer secmeden kayıt etmesini sitemiyoruz ilk tıklayamıyory yer secmeden

    }
    // save ve delete methodları buraya yazdim

    public void save(View view){
    Place place=new Place(binding.placeNameText.getText().toString(),selectedLatitude,selectedLongitude);

    //threading ->main(uı), default (cpu intensive),IO thread(network ,database)

//    /placeDao.insert(place).subscribeOn(Schedulers.io()).subscribe();

        // disopable kullan at demek

        compositeDisposable.add(placeDao.insert(place) // place dao insert yap
                .subscribeOn(Schedulers.io()) // ama bunu io threadde yap
                .observeOn(AndroidSchedulers.mainThread())//main threadde gözlemliycem
                .subscribe(MapsActivity.this::handleResponse)//referans veriyorum demek v mapsactivity subscripe olucam demek
        );
    }
    private void handleResponse(){
        Intent intent=new Intent(MapsActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // flag ekleyip her şeyi temizler
        startActivity(intent);
    }
    public void delete(View view){
        if(selectedPlace!=null){
            compositeDisposable.add(placeDao.delete(selectedPlace)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())  
                    .subscribe(MapsActivity.this::handleResponse)
            );
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear(); // daha önce yapılanlar çöpe atilir hafızada yer tutmaz
    }
}