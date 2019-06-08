package group7.ltdd.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import group7.ltdd.model.Chat;
import group7.ltdd.socialnetwork.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>
{

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    public static final int MSG_TYPE_LEFT_IMAGE=2;
    public static final int MSG_TYPE_RIGHT_IMAGE=3;
    private Context mContext;
    private List<Chat> mChats;
    private String imageURL;

    private FirebaseUser muser;

    public  MessageAdapter(Context mContext, List<Chat> mChats, String imageURL)
    {
        this.mContext=mContext;
        this.mChats=mChats;
        this.imageURL=imageURL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else if (viewType==MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else if (viewType==MSG_TYPE_RIGHT_IMAGE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_image_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_image_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Chat chat = mChats.get(i);
        if (!chat.getImageURL().equals("default"))
            Glide.with(mContext).load(chat.getImageURL()).into(viewHolder.imgChat);
        else viewHolder.txtMessage.setText(chat.getMessage());
        if (imageURL.equals("default"))
        {
            viewHolder.profile_image.setImageResource(R.drawable.default_user_art_g_2);
        }
        else
            Glide.with(mContext).load(imageURL).into(viewHolder.profile_image);

        if (i==mChats.size()-1){
            if (chat.getIsseen().equals("true")){
                viewHolder.txtSeen.setText("Đã xem");
            }
            else
            {
                viewHolder.txtSeen.setText("Đã gửi");
            }
        }
        else
        {
            viewHolder.txtSeen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtMessage;
        public ImageView profile_image;
        public TextView txtSeen;
        public ImageView imgChat;
        public ViewHolder(View itemview){
            super(itemview);
            txtMessage= itemview.<TextView>findViewById(R.id.txtShowMessage);
            profile_image= itemview.<ImageView>findViewById(R.id.profile_image);
            txtSeen= itemview.<TextView>findViewById(R.id.txtSeen);
            imgChat= itemview.<ImageView>findViewById(R.id.imgChat);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat = mChats.get(position);
        muser = FirebaseAuth.getInstance().getCurrentUser();
        if (chat.getSender().equals(muser.getUid()))
        {
            if (!chat.getImageURL().equals("default"))
                return MSG_TYPE_RIGHT_IMAGE;
            else return MSG_TYPE_RIGHT;
        }
        else {
            if (!chat.getImageURL().equals("default"))
                return MSG_TYPE_LEFT_IMAGE;
            else return MSG_TYPE_LEFT;
        }
    }
}
