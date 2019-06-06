package group7.ltdd.socialnetwork;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import group7.ltdd.adapter.MessageAdapter;
import group7.ltdd.model.Chat;
import group7.ltdd.model.Users;

public class ChatActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView txtUser;

    ImageButton btnSend;
    EditText edtMessage;

    FirebaseUser myUser;
    String userid;

    MessageAdapter messageAdapter;
    List<Chat> mchat;
    RecyclerView recyclerView;
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
        myUser = FirebaseAuth.getInstance().getCurrentUser();

        //
        recyclerView = findViewById(R.id.listChat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
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

                ReadMessage(myUser.getUid(),userid,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //
        btnSend= this.<ImageButton>findViewById(R.id.btnSend);
        edtMessage= this.<EditText>findViewById(R.id.txtSend);
    }

    private void AddEvents() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtMessage.getText().toString().equals(""))
                    Toast.makeText(ChatActivity.this, "Bạn chưa nhập nội dung tin nhắn", Toast.LENGTH_SHORT).show();
                else
                SendMessage(myUser.getUid(),userid,edtMessage.getText().toString());
                edtMessage.setText("");
            }
        });
    }

    private void SendMessage(String sender, String reciever, String message)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("reciever",reciever);
        hashMap.put("message",message);

        reference.child("Chats").push().setValue(hashMap);
    }

    private void ReadMessage(final String myid, final String userid, final String imageurl)
    {
        mchat = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot data: dataSnapshot.getChildren())
                {
                    Chat chat = data.getValue(Chat.class);
                    if (chat.getReciever().equals(myid)&&chat.getSender().equals(userid)||
                    chat.getReciever().equals(userid)&&chat.getSender().equals(myid))
                    {
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(ChatActivity.this,mchat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
