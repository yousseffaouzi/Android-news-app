package fr.isep.news;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.isep.news.Model.User;
import fr.isep.news.databinding.ActivitySignupBinding;


public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;


    private static Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    //"\\w+@(\\w+.)+[a-z]{2,3}"

    private FirebaseAuth mAuth;

    //the db
    private FirebaseFirestore db;

    String userId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ClickToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });


        binding.LbtnSignup.setOnClickListener(v -> signup());
    }

    private void signup() {
        String email = binding.EnterEmail.getText().toString();
        String userName = binding.EnterUsername.getText().toString();
        String password = binding.EnterPassword.getText().toString();
        String passwordAgain = binding.EnterPasswordAgain.getText().toString();

        if(!isEmail(email) || email.length() > 31){
            Toast.makeText(this, "Email format error", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(email) || TextUtils.isEmpty(userName) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordAgain)){
            Toast.makeText(this, "Please fill in all the boxes",Toast.LENGTH_LONG).show();
        }else if(password.length()<6){
            Toast.makeText(this, "Password Must be >= 6 Characters",Toast.LENGTH_LONG).show();
        }else if(!password.equals(passwordAgain)){
            Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show();
        }else{
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(SignupActivity.this,"Sign up Success!",Toast.LENGTH_SHORT).show();

                                userId = mAuth.getCurrentUser().getUid();
                                //automatically create the collection
                                DocumentReference documentReference = db.collection("user").document(userId);

                                User newuser = new User(userName,email);
//                                Map<String, Object> user = new HashMap<>();
//                                user.put("userName", userName);
//                                user.put("email", email);
//                                user.put("password", password);

                                // insert user
                                documentReference.set(newuser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("tag", "user is added with ID: " + userId);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("tag", "Error adding document" + e.toString());
                                    }
                                });

                                Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignupActivity.this, "Error..."+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

        }
    }

    public static boolean isEmail(String email){
        if (null==email || "".equals(email))
            return false;
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
