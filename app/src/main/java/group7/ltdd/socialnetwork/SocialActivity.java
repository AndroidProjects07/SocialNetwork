package group7.ltdd.socialnetwork;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import group7.ltdd.adapter.UserAdapter;
import group7.ltdd.model.Users;

public class SocialActivity extends AppCompatActivity {

    TabHost tabHost;

    TextView txtUsername;
    ImageView imgProfile;

    FirebaseUser myuser;


    FirebaseUser myUser;

    DatabaseReference myRef;

    ListView lvUsers;
    ArrayList<Users> dsUsers;
    UserAdapter adapterUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        AddControls();
        AddEvents();
    }

    private void AddControls() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        tabHost = findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("t1");
        tab1.setContent(R.id.tab1);
        tab1.setIndicator("",getResources().getDrawable(R.drawable.ic_profile));
        tabHost.addTab(tab1);

        TabHost.TabSpec tab2 = tabHost.newTabSpec("t2");
        tab2.setContent(R.id.tab2);
        tab2.setIndicator("",getResources().getDrawable(R.drawable.ic_newsfeed));
        tabHost.addTab(tab2);
        lvUsers = this.<ListView>findViewById(R.id.listUsers);

        TabHost.TabSpec tab3 = tabHost.newTabSpec("t3");
        tab3.setContent(R.id.tab3);
        tab3.setIndicator("",getResources().getDrawable(R.drawable.ic_chat));
        tabHost.addTab(tab3);


        txtUsername= this.<TextView>findViewById(R.id.txtUsername);
        imgProfile= this.<ImageView>findViewById(R.id.profile_image);
    }


    private void AddEvents() {
        myuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("account").child(myuser.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                txtUsername.setText(user.getName());
                if (user.getImageURL().equals("default"))
                {
                    imgProfile.setImageResource(R.drawable.default_user_art_g_2);
                }
                else
                    Glide.with(SocialActivity.this).load(user.getImageURL()).into(imgProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Users user = dsUsers.get(position);
                Intent intent = new Intent(SocialActivity.this,ChatActivity.class);
                intent.putExtra("userid",user.getId());
                startActivity(intent);
            }
        });
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                if (tabId.equalsIgnoreCase("t1")) {

                } else if (tabId.equalsIgnoreCase("t2")) {

                } else {
                    XuLyTagChat();
                }
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SocialActivity.this,MainActivity.class));
                finish();
                return true;
        }
        return false;
    }


    private void XuLyTagChat()
    {
        dsUsers = new ArrayList<>();
        adapterUsers = new UserAdapter(SocialActivity.this,R.layout.item_user,dsUsers);
        lvUsers.setAdapter(adapterUsers);
        LayDanhSachUser();
        adapterUsers.notifyDataSetChanged();
    }

    private void LayDanhSachUser() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("account");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapterUsers.clear();
                for (DataSnapshot data: dataSnapshot.getChildren())
                {
                    if (!data.getKey().equals(myuser.getUid())) {
                        Users user = new Users();
                        user.setId(data.getKey());
                        user.setImageURL(data.child("imageURL").getValue().toString());
                        user.setName(data.child("name").getValue().toString());
                        user.setUsername(data.child("username").getValue().toString());
                        adapterUsers.add(user);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
