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
import android.widget.ListView;
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

import de.hdodenhof.circleimageview.CircleImageView;
import group7.ltdd.adapter.UserAdapter;
import group7.ltdd.model.Users;

public class ListUserActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    CircleImageView profile_image;
    TextView txtUsername;

    FirebaseUser user;

    DatabaseReference myRef;

    ListView lvUsers;
    ArrayList<Users> dsUsers;
    UserAdapter adapterUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        AddControls();
        AddEvents();

    }


    private void AddControls() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        profile_image = this.<CircleImageView>findViewById(R.id.profile_image);
        txtUsername= this.<TextView>findViewById(R.id.txtUsername);
        lvUsers = this.<ListView>findViewById(R.id.listUsers);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("account").child(user.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                txtUsername.setText(user.getUsername());
                if (user.getImageURL().equals("default"))
                {
                    profile_image.setImageResource(R.drawable.default_user_art_g_2);
                }
                else
                    Glide.with(ListUserActivity.this).load(user.getImageURL()).into(profile_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dsUsers = new ArrayList<>();
        adapterUsers = new UserAdapter(ListUserActivity.this,R.layout.item_user,dsUsers);
        lvUsers.setAdapter(adapterUsers);
        LayDanhSachUser();
        adapterUsers.notifyDataSetChanged();
    }

    private void AddEvents() {
        lvUsers.setOnItemClickListener(this);
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
                startActivity(new Intent(ListUserActivity.this,MainActivity.class));
                finish();
                return true;
        }
        return false;
    }

    private void LayDanhSachUser()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("account");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapterUsers.clear();
                for (DataSnapshot data: dataSnapshot.getChildren())
                {
                    Users user = new Users();
                    user.setId(data.getKey());
                    user.setImageURL(data.child("imageURL").getValue().toString());
                    user.setName(data.child("name").getValue().toString());
                    user.setUsername(data.child("username").getValue().toString());
                    adapterUsers.add(user);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Users user = dsUsers.get(position);
        Intent intent = new Intent(ListUserActivity.this,ChatActivity.class);
        intent.putExtra("userid",user.getId());
        startActivity(intent);
    }
}
