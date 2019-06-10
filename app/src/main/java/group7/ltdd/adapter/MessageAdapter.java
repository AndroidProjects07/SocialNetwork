package group7.ltdd.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;

import group7.ltdd.model.Chat;
import group7.ltdd.socialnetwork.ChatActivity;
import group7.ltdd.socialnetwork.R;
import group7.ltdd.socialnetwork.SocialActivity;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>
{

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    public static final int MSG_TYPE_LEFT_IMAGE=2;
    public static final int MSG_TYPE_RIGHT_IMAGE=3;
    public static final int MSG_TYPE_LEFT_AUDIO=4;
    public static final int MSG_TYPE_RIGHT_AUDIO=5;
    private Context mContext;
    private List<Chat> mChats;
    private String imageURL;
    private FirebaseUser muser;
    private boolean isPlay=false;
    ProgressDialog progressDialog;

    ViewHolder vieww;

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
        } else if (viewType==MSG_TYPE_LEFT_IMAGE){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_image_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else if (viewType==MSG_TYPE_RIGHT_AUDIO){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_audio_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_audio_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        vieww = viewHolder;
        isPlay=false;
        final Chat chat = mChats.get(i);
        if (!chat.getImageURL().equals("default"))
            Glide.with(mContext).load(chat.getImageURL()).into(viewHolder.imgChat);
        else if (chat.getAudioURL().equals("default")){
            viewHolder.txtMessage.setText(chat.getMessage());
        }
        else //URL Audio = default
        {
        }
        progressDialog = new ProgressDialog(mContext);
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

        if (!chat.getAudioURL().equals("default")) {
            viewHolder.imgPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ChatActivity.mediaPlayer==null){
                        ChatActivity.mediaPlayer = new MediaPlayer();
                        ChatActivity.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    }
                    else
                    {
                        if (ChatActivity.mediaPlayer.isPlaying()){
                            ChatActivity.mediaPlayer.stop();
                        }
                    }
                    viewHolder.imgPlay.setVisibility(View.INVISIBLE);
                    viewHolder.imgPause.setVisibility(View.VISIBLE);
                    viewHolder.txtStatus.setText("Pause");
                    if (!isPlay){
                        new Player().execute(chat.getAudioURL());
                    }
                    else {
                        if (!ChatActivity.mediaPlayer.isPlaying()){
                            ChatActivity.mediaPlayer.start();
                        }
                    }

                }
            });
            viewHolder.imgPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.txtStatus.setText("Play");
                    viewHolder.imgPlay.setVisibility(View.VISIBLE);
                    viewHolder.imgPause.setVisibility(View.INVISIBLE);
                    if (ChatActivity.mediaPlayer.isPlaying()) {
                        ChatActivity.mediaPlayer.pause();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtMessage;
        public ImageView profile_image;
        public TextView txtSeen, txtStatus;
        public ImageView imgChat, imgPlay, imgPause;
        public ViewHolder(View itemview){
            super(itemview);
            txtMessage= itemview.<TextView>findViewById(R.id.txtShowMessage);
            profile_image= itemview.<ImageView>findViewById(R.id.profile_image);
            txtSeen= itemview.<TextView>findViewById(R.id.txtSeen);
            imgChat= itemview.<ImageView>findViewById(R.id.imgChat);
            imgPlay =  itemview.findViewById(R.id.imgPlay);
            imgPause = itemview.findViewById(R.id.imgPause);
            txtStatus=itemview.findViewById(R.id.txtStatus);
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
            else if (!chat.getAudioURL().equals("default"))
                return MSG_TYPE_RIGHT_AUDIO;
            else
                return MSG_TYPE_RIGHT;
        }
        else {
            if (!chat.getImageURL().equals("default"))
                return MSG_TYPE_LEFT_IMAGE;
            else if (!chat.getAudioURL().equals("default"))
                return MSG_TYPE_LEFT_AUDIO;
            else return MSG_TYPE_LEFT;
        }
    }

    class Player extends AsyncTask<String,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {

            Boolean prepared =false;
            try {

                ChatActivity.mediaPlayer.setDataSource(strings[0]);
                ChatActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        isPlay=false;
                        //vieww.txtStatus = vieww.itemView.findViewById(R.id.txtStatus);
                        //vieww.txtStatus.setText("Start Play");
                        ChatActivity.TimeOut();
                    }
                });
                ChatActivity.mediaPlayer.prepare();
                prepared=true;
            }
            catch (IOException ex){
                Log.e("My audio app ", ex.getMessage());
                prepared=false;
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (progressDialog.isShowing()){
                progressDialog.cancel();
            }
            ChatActivity.mediaPlayer.start();
            isPlay=true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
    }
}

