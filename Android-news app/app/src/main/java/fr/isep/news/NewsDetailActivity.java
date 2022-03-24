package fr.isep.news;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import fr.isep.news.Model.Newsdetail;
import fr.isep.news.databinding.ActivityNewsdetailBinding;


public class NewsDetailActivity extends AppCompatActivity {

    private ActivityNewsdetailBinding binding;

    private String NewsTitle, NewsAuthor, NewsPublishAt, NewsContent, NewsImageURL, NewsURL, NewsDescription;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String userId;
    private InterstitialAd interstitialAd;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsdetail);

        interstitialAd = new InterstitialAd(this);
        // "testb4znbuh3n2" is a dedicated test ad unit ID. Before releasing your app, replace the test ad unit ID with the formal one.
        interstitialAd.setAdId("j1y43tbymy");
        interstitialAd.setAdListener(adListener);
        loadInterstitialAd();

        NewsTitle = getIntent().getStringExtra("NewsTitle");
        NewsAuthor = getIntent().getStringExtra("NewsAuthor");
        NewsPublishAt = getIntent().getStringExtra("NewsPulishedAt");
        NewsDescription = getIntent().getStringExtra("NewsDescription");
        NewsContent = getIntent().getStringExtra("NewsContent");
        NewsImageURL = getIntent().getStringExtra("NewsImageURL");
        NewsURL = getIntent().getStringExtra("NewsURL");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        if (NewsContent == null) {
            NewsContent = "There is no content for this news...Click the button below to get more...";
        }
        String[] temp = NewsContent.split("\\[");

        binding = ActivityNewsdetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.home.setOnClickListener(this::ClicktoHomePage);
        binding.CollectionNews.setOnClickListener(this::ClickToCollection);
        binding.collect.setOnClickListener(this::CollectNews);
        binding.NotCollect.setOnClickListener(this::NotCollectNews);

        binding.NewsTitle.setText(NewsTitle);
        binding.NewsAuthor.setText("Author: " + NewsAuthor);
        binding.NewsPublishAt.setText("Publish Date: " + NewsPublishAt);
        binding.NewsDescription.setText(NewsDescription);
        binding.NewsContent.setText(temp[0]);


        Picasso.get().load(NewsImageURL).into(binding.NewsImage);

        binding.ReadMore.setOnClickListener(this::ReadMore);

        setImageState();

    }


    private void setImageState() {

        DocumentReference documentReference = db.collection("user").document(userId).collection("News").document(NewsTitle);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null && documentSnapshot.exists()) {
                    binding.NotCollect.setVisibility(View.VISIBLE);
                    binding.collect.setVisibility(View.GONE);
                } else {
                    binding.NotCollect.setVisibility(View.GONE);
                    binding.collect.setVisibility(View.VISIBLE);
                }
            }

        });

    }


    private void CollectNews(View view) {

        Newsdetail News = new Newsdetail(NewsTitle, NewsAuthor, NewsPublishAt, NewsDescription,
                NewsContent, NewsImageURL, NewsImageURL);

        DocumentReference documentReference = db.collection("user").document(userId).collection("News").document(NewsTitle);

        documentReference.set(News).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showInterstitialAd();
                Log.d("tag", "News is added with ID: " + NewsTitle);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("tag", "Error adding document" + e.toString());
            }
        });

        setImageState();

    }


    private void NotCollectNews(View view) {
        DocumentReference documentReference = db.collection("user").document(userId).collection("News").document(NewsTitle);
        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("tag", "Remove this news from the collection "+NewsTitle);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("d", "Error deleting", e);
            }
        });

        setImageState();
    }


    //Jump to show the source and all of the news
    private void ReadMore(View view) {
        showInterstitialAd();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(NewsURL));
        startActivity(intent);
    }

    private void ClickToCollection(View view) {
        showInterstitialAd();
        startActivity(new Intent(getApplicationContext(), CollectionActivity.class));
    }

    private void ClicktoHomePage(View view) {
        showInterstitialAd();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
