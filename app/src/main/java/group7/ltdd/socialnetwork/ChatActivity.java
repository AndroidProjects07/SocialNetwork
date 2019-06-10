package group7.ltdd.socialnetwork;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

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

    DatabaseReference reference;

    ValueEventListener seenListener;

    String imageURL="default", audioURL="default";

    ImageView chooseImage,chooseAudio;

    StorageReference storageReference;

    public static MediaPlayer mediaPlayer;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private int isImage=0;
    public static TextView txtStt;
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
        chooseImage = this.<ImageView>findViewById(R.id.chooseImage);
        chooseAudio=findViewById(R.id.chooseAudio);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        txtStt = findViewById(R.id.txtStatus);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SocialActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
        reference = database.getReference("account").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                txtUser.setText(user.getUsername());
                if (user.getImageURL().equals("default"))
                {
                    profile_image.setImageResource(R.drawable.default_user_art_g_2);
                }
                else
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);

                ReadMessage(myUser.getUid(),userid,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //
        btnSend= this.<ImageButton>findViewById(R.id.btnSend);
        edtMessage= this.<EditText>findViewById(R.id.txtSend);

        //set storage
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        SeenMessage(userid);
    }

    private void AddEvents() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageURL="default";
                audioURL="default";
                if (edtMessage.getText().toString().equals(""))
                    Toast.makeText(ChatActivity.this, "Bạn chưa nhập nội dung tin nhắn", Toast.LENGTH_SHORT).show();
                else
                SendMessage(myUser.getUid(),userid,edtMessage.getText().toString(),imageURL,audioURL);
                edtMessage.setText("");
            }
        });
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isImage=1;
                openImage();
            }
        });
        chooseAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isImage=0;
                openImage();
            }
        });

    }

    private void SeenMessage(final String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    Chat chat = data.getValue(Chat.class);
                    if (chat.getReciever().equals(myUser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isseen","true");
                        data.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendMessage(String sender, String reciever, String message, String imageURL,String audioURL)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("reciever",reciever);
        hashMap.put("message",message);
        hashMap.put("isseen","false");
        hashMap.put("imageURL",imageURL);
        hashMap.put("audioURL",audioURL);
        reference.child("Chats").push().setValue(hashMap);
    }

    private void ReadMessage(final String myid, final String userid, final String imageurl)
    {
        mchat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
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

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        if (mediaPlayer!=null){
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    public static void TimeOut(){

        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    private void openImage() {
        Intent intent = new Intent();
        if (isImage==1)
            intent.setType("image/*");
        else
            intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(ChatActivity.this);
        pd.setMessage("Uploading...");
        pd.show();
        if (imageUri!=null){
            final StorageReference fileReference =  storageReference.child(System.currentTimeMillis()+","
                    + getFileExtension(imageUri));
            uploadTask =fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){

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
                        if (isImage==1)
                            SendMessage(myUser.getUid(),userid,edtMessage.getText().toString(),mUri,"default");
                        else
                            SendMessage(myUser.getUid(),userid,edtMessage.getText().toString(),"default",mUri);
                        pd.dismiss();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Thất bại!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else
        {
            Toast.makeText(this, "Không có image nào được chọn!", Toast.LENGTH_SHORT).show();
        }
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
}
