package group7.ltdd.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import group7.ltdd.model.Post;
import group7.ltdd.model.Users;
import group7.ltdd.socialnetwork.R;

public class PostAdapter extends ArrayAdapter<Post> {
    Activity context;
    int resource;
    List<Post> objects;

    CircleImageView profile_image;
    TextView txtName, txtTime, txtContent, txtSlLike, txtLike;
    ImageView imgPhoto, imgLike, imgShare, imgCmt;
    public PostAdapter(Activity context, int resource, List<Post> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View row = inflater.inflate(resource, null);
        profile_image = row.findViewById(R.id.imgAvatar);
        txtName = row.findViewById(R.id.txtName);
        txtTime = row.findViewById(R.id.txtTime);
        txtContent = row.findViewById(R.id.txtStatus);
        txtSlLike = row.findViewById(R.id.txtSlLike);
        imgPhoto = row.findViewById(R.id.imgPicture);
        imgLike = row.findViewById(R.id.imgLike);
        imgCmt = row.findViewById(R.id.imgCmt);
        imgShare = row.findViewById(R.id.imgShare);
        txtLike = row.findViewById(R.id.txtLike);

        final Post post = this.objects.get(position);

        if (post.getProfileURL().equals("default")){
            profile_image.setImageResource(R.drawable.default_user_art_g_2);
        }
        else
        {
            Glide.with(context).load(post.getProfileURL()).into(profile_image);
        }

        txtName.setText(post.getName());

        txtTime.setText(post.getTime());

        txtContent.setText(post.getContent());

        if (post.getCountLike()>0)
            txtSlLike.setText(post.getCountLike()+" Lượt thích");
        else
            txtSlLike.setText("");

        if (post.getImageURL().equals("default")){
            imgPhoto.setVisibility(View.GONE);
        }
        else
        {
            Glide.with(context).load(post.getImageURL()).into(imgPhoto);
        }

        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XuLyThich(post);
            }
        });
        return row;
    }

    private void XuLyThich(Post post) {
        if (post.getLiked()==0){
            txtLike.setTextColor(Color.parseColor("#F2081DE4"));
            post.setLiked(1);
        }
        else {
            txtLike.setTextColor(Color.parseColor("#A31A1313"));
            post.setLiked(0);
        }
    }
}
