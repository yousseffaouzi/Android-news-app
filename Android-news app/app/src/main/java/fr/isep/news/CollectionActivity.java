package fr.isep.news;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.InterstitialAd;

import java.util.ArrayList;

import fr.isep.news.Adapter.NewsRecyclerVAdapter;
import fr.isep.news.Model.Newsdetail;
import fr.isep.news.databinding.ActivityCollectionBinding;


public class CollectionActivity extends AppCompatActivity {

    private ActivityCollectionBinding binding;

    private ArrayList<Newsdetail> NewsArrayList = new ArrayList<>();
    private NewsRecyclerVAdapter newsRecyclerVAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private InterstitialAd interstitialAd;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        interstitialAd = new InterstitialAd(this);
        // "testb4znbuh3n2" is a dedicated test ad unit ID. Before releasing your app, replace the test ad unit ID with the formal one.
        interstitialAd.setAdId("j1y43tbymy");
        interstitialAd.setAdListener(adListener);
        loadInterstitialAd();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();


        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.home.setOnClickListener(this::ClicktoHomePage);
        binding.manageAccount.setOnClickListener(this::ClickToProfile);

        newsRecyclerVAdapter = new NewsRecyclerVAdapter(NewsArrayList,this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.News.setLayoutManager(layoutManager);
        binding.News.setAdapter(newsRecyclerVAdapter);

        getNews();

    }

    private void getNews() {

        DocumentReference documentReference = db.collection("user").document(userId);

        documentReference.collection("News").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("tag", document.getId() + " => " + document.getData());
                        NewsArrayList.add(new Newsdetail(document.getString("title"),document.getString("author"),document.getString("publishedAt"),
                                document.getString("description"),document.getString("url"),document.getString("urlToImage"),document.getString("content")));
                    }
                    newsRecyclerVAdapter.notifyDataSetChanged();
                }else {
                    Log.d("tag", "Error getting documents.", task.getException());
                }
            }
        });

    }

    private void ClicktoHomePage(View view) {
        showInterstitialAd();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private void ClickToProfile(View view) {
        showInterstitialAd();
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
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
