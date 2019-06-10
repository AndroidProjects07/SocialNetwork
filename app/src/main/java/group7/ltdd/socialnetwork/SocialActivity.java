package group7.ltdd.socialnetwork;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import group7.ltdd.adapter.PostAdapter;
import group7.ltdd.adapter.UserAdapter;
import group7.ltdd.model.Post;
import group7.ltdd.model.Users;

public class SocialActivity extends AppCompatActivity {

    TabHost tabHost;

    TextView txtUsername;
    ImageView imgProfile;

    FirebaseUser myuser;


    DatabaseReference myRef;

    ListView lvUsers;
    ArrayList<Users> dsUsers;
    UserAdapter adapterUsers;



    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    EditText edtSearchUsers,edtStatus;

    ListView lvPost;
    ArrayList<Post> dsPost;
    PostAdapter postAdapter;

    ListView lvPostPersonal;
    ArrayList<Post> dsPostPersonal;
    PostAdapter postAdapterPersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        AddControls();
        AddEvents();
    }

    private void AddControls() {

        //set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        //set up tabhost
        tabHost = findViewById(R.id.tabHost);
        tabHost.setup();
        //tab1
        TabHost.TabSpec tab1 = tabHost.newTabSpec("t1");
        tab1.setContent(R.id.tab1);
        tab1.setIndicator("",getResources().getDrawable(R.drawable.ic_profile));
        tabHost.addTab(tab1);
        lvPostPersonal=findViewById(R.id.listPostPersonal);
        //tab 2
        TabHost.TabSpec tab2 = tabHost.newTabSpec("t2");
        tab2.setContent(R.id.tab2);
        tab2.setIndicator("",getResources().getDrawable(R.drawable.ic_newsfeed));
        tabHost.addTab(tab2);
        lvPost = this.<ListView>findViewById(R.id.listPost);
        //tab3
        TabHost.TabSpec tab3 = tabHost.newTabSpec("t3");
        tab3.setContent(R.id.tab3);
        tab3.setIndicator("",getResources().getDrawable(R.drawable.ic_chat));
        tabHost.addTab(tab3);
        lvUsers = this.<ListView>findViewById(R.id.listUsers);


        txtUsername= this.<TextView>findViewById(R.id.txtUsername);
        imgProfile= this.<ImageView>findViewById(R.id.profile_image);

        edtSearchUsers= this.<EditText>findViewById(R.id.searchUser);
        edtStatus = findViewById(R.id.edtStatus);


        //set storage
        storageReference = FirebaseStorage.getInstance().getReference("uploads");


        dsPost = new ArrayList<>();
        postAdapter = new PostAdapter(SocialActivity.this,R.layout.item_news,dsPost);
        lvPost.setAdapter(postAdapter);
        LayDanhSachBaiDang();


        dsPostPersonal = new ArrayList<>();
        postAdapterPersonal=new PostAdapter(SocialActivity.this,R.layout.item_news,dsPostPersonal);
        lvPostPersonal.setAdapter(postAdapterPersonal);
        LayDanhSachBaiDangCaNhan();
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
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(imgProfile);
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
                    LayDanhSachBaiDangCaNhan();
                } else if (tabId.equalsIgnoreCase("t2")) {
                    LayDanhSachBaiDang();
                } else {
                    XuLyTagChat();
                }
            }
        });
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        edtSearchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SocialActivity.this,StatusActivity.class);
                startActivity(intent);
            }
        });
    }

    private void searchUsers(String s) {
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("account").orderByChild("username")
                .startAt(s).endAt(s+"\uf0ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dsUsers.clear();
                for (DataSnapshot data: dataSnapshot.getChildren())
                {
                    if (!data.getKey().equals(myuser.getUid())) {
                        Users user = new Users();
                        user.setId(data.getKey());
                        user.setImageURL(data.child("imageURL").getValue().toString());
                        user.setName(data.child("name").getValue().toString());
                        user.setUsername(data.child("username").getValue().toString());
                        user.setStatus(data.child("status").getValue().toString());
                        dsUsers.add(user);
                    }
                }
                adapterUsers = new UserAdapter(SocialActivity.this,R.layout.item_user,dsUsers,true);
                lvUsers.setAdapter(adapterUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                startActivity(new Intent(getApplicationContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
        }
        return false;
    }


    private void XuLyTagChat()
    {
        dsUsers = new ArrayList<>();
        adapterUsers = new UserAdapter(SocialActivity.this,R.layout.item_user,dsUsers,true);
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
                if (edtSearchUsers.getText().toString().equals("")){
                    adapterUsers.clear();
                    for (DataSnapshot data: dataSnapshot.getChildren())
                    {
                        if (!data.getKey().equals(myuser.getUid())) {
                            Users user = new Users();
                            user.setId(data.getKey());
                            user.setImageURL(data.child("imageURL").getValue().toString());
                            user.setName(data.child("name").getValue().toString());
                            user.setUsername(data.child("username").getValue().toString());
                            user.setStatus(data.child("status").getValue().toString());
                            adapterUsers.add(user);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(SocialActivity.this);
        pd.setMessage("Uploading...");
        pd.show();
        if (imageUri!=null){
            final StorageReference fileReference =  storageReference.child(System.currentTimeMillis()+","
                    + getFileExtension(imageUri));
            uploadTask =fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>(){

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("account").child(myuser.getUid());
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("imageURL",mUri);
                        reference.updateChildren(map);
                        pd.dismiss();
                    }
                    else
                    {
                        Toast.makeText(SocialActivity.this, "Thất bại!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SocialActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else
        {
            Toast.makeText(this, "Không có image nào được chọn!", Toast.LENGTH_SHORT).show();
        }
    }

    private void LayDanhSachBaiDang(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               ArrayList<Post> arrPost = new ArrayList<>();
                dsPost.clear();
                for (DataSnapshot data: dataSnapshot.getChildren())
                {
                    Post post = new Post();
                    post.setIdPost(data.getKey());
                    post.setIdUser(data.child("iduser").getValue().toString());
                    post.setTime(data.child("time").getValue().toString());
                    post.setContent(data.child("content").getValue().toString());
                    post.setImageURL(data.child("imageURL").getValue().toString());
                    post.setCountLike(Integer.parseInt(data.child("countlike").getValue().toString()));
                    post.setLiked(Integer.parseInt(data.child("liked").getValue().toString()));
                    arrPost.add(post);
                }
                for (int i=arrPost.size()-1; i>=0; i--){
                    dsPost.add(arrPost.get(i));
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void LayDanhSachBaiDangCaNhan(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dsPostPersonal.clear();
                ArrayList<Post> arrPost = new ArrayList<>();
                for (DataSnapshot data: dataSnapshot.getChildren())
                {
                    if (data.child("iduser").getValue().toString().equals(user.getUid())){
                        Post post = new Post();
                        post.setIdPost(data.getKey());
                        post.setIdUser(data.child("iduser").getValue().toString());
                        post.setTime(data.child("time").getValue().toString());
                        post.setContent(data.child("content").getValue().toString());
                        post.setImageURL(data.child("imageURL").getValue().toString());
                        post.setCountLike(Integer.parseInt(data.child("countlike").getValue().toString()));
                        post.setLiked(Integer.parseInt(data.child("liked").getValue().toString()));
                        arrPost.add(post);
                    }
                }
                for (int i=arrPost.size()-1; i>=0; i--) {
                    dsPostPersonal.add(arrPost.get(i));
                }
                postAdapterPersonal.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();
            if (uploadTask!=null && uploadTask.isInProgress()){
                Toast.makeText(this, "Đang tải lên!", Toast.LENGTH_SHORT).show();
            }
            else {
                uploadImage();
            }
        }
    }

    private void status(String status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("account").child(myuser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
