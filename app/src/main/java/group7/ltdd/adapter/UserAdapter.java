package group7.ltdd.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import group7.ltdd.model.Users;
import group7.ltdd.socialnetwork.ListUserActivity;
import group7.ltdd.socialnetwork.R;

public class UserAdapter extends ArrayAdapter<Users> {
    Activity context;
    int resource;
    List<Users> objects;

    public UserAdapter(Activity context, int resource, List<Users> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View row = inflater.inflate(resource, null);
        ImageView imgUser =row.findViewById(R.id.imageuser);
        TextView txtName = row.findViewById(R.id.txtName);
        TextView txtContent = row.findViewById(R.id.txtContent);

        final Users user = this.objects.get(position);

        if (user.getImageURL().equals("default"))
        {
            imgUser.setImageResource(R.drawable.default_user_art_g_2);
        }
        else
            Glide.with(context).load(user.getImageURL()).into(imgUser);

        txtName.setText(user.getUsername());
        txtContent.setText("Click to send message");
        return row;
    }
}
