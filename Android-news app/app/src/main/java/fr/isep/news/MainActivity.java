
 //code by YOUSSEF FAOUZI




package fr.isep.news;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.InterstitialAd;

import java.util.ArrayList;

import fr.isep.news.NewsAPI.RetrofitBuilder;
import fr.isep.news.NewsAPI.RetrofitInterface;
import fr.isep.news.Adapter.CategoryRecyclerVAdapter;
import fr.isep.news.Adapter.NewsRecyclerVAdapter;
import fr.isep.news.Model.Category;
import fr.isep.news.Model.News;
import fr.isep.news.Model.Newsdetail;
import fr.isep.news.databinding.ActivityMainBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements CategoryRecyclerVAdapter.CategoryClickInterface{

    private ActivityMainBinding binding;

    private ArrayList<Newsdetail> NewsdetailArrayList = new ArrayList<>();
    private ArrayList<Category> CategoryArrayList = new ArrayList<>();

    private CategoryRecyclerVAdapter categoryRecyclerVAdapter;
    private NewsRecyclerVAdapter newsRecyclerVAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    String userId,CategoryName;
    private InterstitialAd interstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        interstitialAd = new InterstitialAd(this);
        // "testb4znbuh3n2" is a dedicated test ad unit ID. Before releasing your app, replace the test ad unit ID with the formal one.
        interstitialAd.setAdId("j1y43tbymy");
        interstitialAd.setAdListener(adListener);
        loadInterstitialAd();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.manageAccount.setOnClickListener(this::ClickToProfile);
        binding.CollectionNews.setOnClickListener(this::ClickToCollection);

        categoryRecyclerVAdapter = new CategoryRecyclerVAdapter(CategoryArrayList,this,this::onClickCategory);
        newsRecyclerVAdapter = new NewsRecyclerVAdapter(NewsdetailArrayList,this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.News.setLayoutManager(layoutManager);
        binding.News.setAdapter(newsRecyclerVAdapter);

        binding.Category.setAdapter(categoryRecyclerVAdapter);
        getCategory();

    }

    private void ClickToProfile(View view) {
        showInterstitialAd();
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }

    private void ClickToCollection(View view) {
        showInterstitialAd();
        startActivity(new Intent(getApplicationContext(), CollectionActivity.class));
    }

    @Override
    public void onClickCategory(int position) {
        showInterstitialAd();
        String categoryName = CategoryArrayList.get(position).getCategoryName();
        binding.Hint.setVisibility(View.INVISIBLE);
        getNews(categoryName);

    }

    private void getCategory(){

        DocumentReference documentReference = db.collection("category").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null && documentSnapshot.exists()) {
                CategoryName = documentSnapshot.getString("categoryName");
                //根据,分割category字符串
                String[] as = CategoryName.split(",");
                for (int i = 0; i < as.length; i++) {
                    if(as[i].equals("Business")){
                        CategoryArrayList.add(new Category("business"));}
                    if(as[i].equals("Entertainment")){
                        CategoryArrayList.add(new Category("entertainment"));}
                    if(as[i].equals("General")){ CategoryArrayList.add(new Category("general"));}
                    if(as[i].equals("Health")){
                        CategoryArrayList.add(new Category("health"));}
                    if(as[i].equals("Science")){
                        CategoryArrayList.add(new Category("science"));}
                    if(as[i].equals("Sports")){ CategoryArrayList.add(new Category("sports"));}
                    if(as[i].equals("Technology")){ CategoryArrayList.add(new Category("technology"));}
                }
                }else{
                    Log.d("tag", "onEvent: Document do not exists");
                }
                categoryRecyclerVAdapter.notifyDataSetChanged();
            }

        });

    }

    private void getNews(String CategoryName){
        NewsdetailArrayList.clear();

        String apiKey ="2a75f3dbcae446c4868c3e50e889dab7";

        RetrofitInterface retrofitInterface = RetrofitBuilder.getRetrofitInstance().create(RetrofitInterface.class);
        Call<News> call = retrofitInterface.getNewsByCategory(CategoryName,apiKey);

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                News news = response.body();
                ArrayList<Newsdetail> newsdetails = news.getArticles();
                for (int i = 0; i < newsdetails.size(); i++){
                    showInterstitialAd();
                    NewsdetailArrayList.add(new Newsdetail(newsdetails.get(i).getTitle(),newsdetails.get(i).getAuthor(),newsdetails.get(i).getPublishedAt(),
                            newsdetails.get(i).getDescription(),newsdetails.get(i).getUrl(), newsdetails.get(i).getUrlToImage(),newsdetails.get(i).getContent()));
                }
                newsRecyclerVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadInterstitialAd() {

        // Load an interstitial ad.
        AdParam adParam = new AdParam.Builder().build();
        interstitialAd.loadAd(adParam);

    }

    private void showInterstitialAd() {
        // Display the ad.
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show(this);
        }
    }

    private AdListener adListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            // Called when an ad is loaded successfully.

            showInterstitialAd();
        }
        @Override
        public void onAdFailed(int errorCode) {
            // Called when an ad fails to be loaded.

        }
        @Override
        public void onAdClosed() {
            // Called when an ad is closed.

        }
        @Override
        public void onAdClicked() {
            // Called when an ad is clicked.

        }
        @Override
        public void onAdLeave() {
            // Called when an ad leaves an app.

        }
        @Override
        public void onAdOpened() {
            // Called when an ad is opened.

        }
        @Override
        public void onAdImpression() {
            // Called when an ad impression occurs.

        }
    };
}