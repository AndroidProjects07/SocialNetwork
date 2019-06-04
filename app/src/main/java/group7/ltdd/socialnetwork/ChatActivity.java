package group7.ltdd.socialnetwork;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import group7.ltdd.model.Users;

public class ChatActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView txtUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        AddControls();
        AddEvents();
    }

    private void AddControls() {
        profile_image= this.<CircleImageView>findViewById(R.id.profile_image);
        txtUser= this.<TextView>findViewById(R.id.txtUsername);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        FirebaseUser myUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        String userid = intent.getStringExtra("userid");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("account").child(userid);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                txtUser.setText(user.getUsername());
                if (user.getImageURL().equals("default"))
                {
                    profile_image.setImageResource(R.drawable.default_user_art_g_2);
                }
                else
                    Glide.with(ChatActivity.this).load(user.getImageURL()).into(profile_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void AddEvents() {
    }
}
