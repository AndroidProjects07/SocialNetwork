package group7.ltdd.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import group7.ltdd.model.Chat;
import group7.ltdd.model.Users;
import group7.ltdd.socialnetwork.R;

public class UserAdapter extends ArrayAdapter<Users> {
    Activity context;
    int resource;
    List<Users> objects;
    boolean isChat;
    ImageView imgon, imgoff;

    String theLastMessage;

    public UserAdapter(Activity context, int resource, List<Users> objects, boolean isChat) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
        this.isChat=isChat;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View row = inflater.inflate(resource, null);
        ImageView imgUser =row.findViewById(R.id.imageuser);
        TextView txtName = row.findViewById(R.id.txtName);
        TextView txtContent = row.findViewById(R.id.txtContent);

        imgon = row.<ImageView>findViewById(R.id.img_on);
        imgoff = row.<ImageView>findViewById(R.id.img_off);
        final Users user = this.objects.get(position);

        if (user.getImageURL().equals("default"))
        {
            imgUser.setImageResource(R.drawable.default_user_art_g_2);
        }
        else
            Glide.with(context).load(user.getImageURL()).into(imgUser);

        if (isChat){
            lastMessage(user.getId(),txtContent);
        }


        if (isChat) {
            if (user.getStatus().equals("online")) {
                imgon.setVisibility(View.VISIBLE);
                imgoff.setVisibility(View.GONE);
            } else {
                imgon.setVisibility(View.GONE);
                imgoff.setVisibility(View.VISIBLE);
            }
            txtName.setText(user.getUsername());
            txtContent.setText("Click to send message");
        }
        else
        {
            imgon.setVisibility(View.GONE);
            imgoff.setVisibility(View.GONE);
        }
        return row;
    }

    private void lastMessage(final String userid, final TextView txtLastMessage){
        theLastMessage="default";
        final FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    Chat chat = data.getValue(Chat.class);
                    if ((chat.getReciever().equals(firebaseUser.getUid())&&chat.getSender().equals(userid))||
                    (chat.getReciever().equals(userid)&&chat.getSender().equals(firebaseUser.getUid()))){
                        theLastMessage=chat.getMessage();
                    }
                }

                switch (theLastMessage){
                    case "default":
                        txtLastMessage.setText("Chọn để gửi tin nhắn");
                        break;
                    default:
                        txtLastMessage.setText(theLastMessage);
                        break;
                }
                theLastMessage="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
