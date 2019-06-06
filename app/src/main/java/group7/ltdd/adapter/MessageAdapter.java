package group7.ltdd.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.List;

import group7.ltdd.model.Chat;
import group7.ltdd.model.Users;
import group7.ltdd.socialnetwork.ListUserActivity;
import group7.ltdd.socialnetwork.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>
{

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;

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
        else
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Chat chat = mChats.get(i);
        viewHolder.txtMessage.setText(chat.getMessage());
        if (imageURL.equals("default"))
        {
            viewHolder.profile_image.setImageResource(R.drawable.default_user_art_g_2);
        }
        else
            Glide.with(mContext).load(imageURL).into(viewHolder.profile_image);
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtMessage;
        public ImageView profile_image;
        public ViewHolder(View itemview){
            super(itemview);
            txtMessage= itemview.<TextView>findViewById(R.id.txtShowMessage);
            profile_image= itemview.<ImageView>findViewById(R.id.profile_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        muser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChats.get(position).getSender().equals(muser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
            return MSG_TYPE_LEFT;
    }
}
