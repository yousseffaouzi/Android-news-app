package fr.isep.news;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

import fr.isep.news.databinding.ActivityProfilemanagementBinding;


public class ProfileActivity extends AppCompatActivity {

    private ActivityProfilemanagementBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private FirebaseAuth.AuthStateListener authStateListener;

    String userId;
    private InterstitialAd interstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilemanagement);

        interstitialAd = new InterstitialAd(this);
        // "testb4znbuh3n2" is a dedicated test ad unit ID. Before releasing your app, replace the test ad unit ID with the formal one.
        interstitialAd.setAdId("j1y43tbymy");
        interstitialAd.setAdListener(adListener);
        loadInterstitialAd();

//      Listener called when there is a change in the authentication state
        authStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
                else {
                    showUserData();
                    showCategory();
                }
            }
        };

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(authStateListener);

        db = FirebaseFirestore.getInstance();

        userId = mAuth.getCurrentUser().getUid();


        binding = ActivityProfilemanagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.home.setOnClickListener(this::ClicktoHomePage);
        binding.CollectionNews.setOnClickListener(this::ClicktoCollection);

        binding.LogOutButton.setOnClickListener(this::LogOut);

        binding.UpdateButton.setOnClickListener(this::UpdateProfile);

        binding.manageChannelButton.setOnClickListener(this::updateCategory);

    }



    private void showUserData() {
        DocumentReference documentReference = db.collection("user").document(userId);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    if (e == null && documentSnapshot.exists()) {
                        binding.ProfileUserName.getEditText().setText(documentSnapshot.getString("userName"));
                        binding.ProfileEmail.getEditText().setText(documentSnapshot.getString("email"));
                    }else{
                        Log.d("tag", "onEvent: Document do not exists");
                    }

            }
        });
    }

    private void UpdateProfile(View view) {
        final String email = binding.ProfileEmail.getEditText().getText().toString().trim();
        DocumentReference documentReference = db.collection("user").document(userId);
        mAuth.getCurrentUser().updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Map<String,Object> user = new HashMap<>();
                user.put("email",email);
                user.put("userName",binding.ProfileUserName.getEditText().getText().toString());
                documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ProfileActivity.this, "Update Success!" , Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("tag", "Error updating" + e.toString());
                Toast.makeText(ProfileActivity.this, "Error..."+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void showCategory() {

        DocumentReference documentReference = db.collection("category").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null && documentSnapshot.exists()) {
                    showInterstitialAd();
                binding.ChosenCategory.setText(documentSnapshot.getString("categoryName"));
                }else{
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });
    }

    private void updateCategory(View view) {
        showInterstitialAd();
        startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
    }

    private void LogOut(View view) {
        showInterstitialAd();
        mAuth.signOut();
    }


    private void ClicktoHomePage(View view) {
        showInterstitialAd();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }


    private void ClicktoCollection(View view) {
        showInterstitialAd();
        startActivity(new Intent(getApplicationContext(), CollectionActivity.class));
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
