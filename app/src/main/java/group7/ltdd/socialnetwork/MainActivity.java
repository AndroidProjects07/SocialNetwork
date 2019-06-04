package group7.ltdd.socialnetwork;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button btnDangNhap, btnDangKy;

    @Override
    protected void onStart() {

        super.onStart();
        AutoLogin();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AddControls();
        AddEvents();
    }


    private void AddControls() {
        btnDangNhap= this.<Button>findViewById(R.id.btnDangNhap);
        btnDangKy= this.<Button>findViewById(R.id.btnDangKy);
    }

    private void AddEvents() {
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
    private void AutoLogin()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null)
        {
            Intent intent = new Intent(MainActivity.this,ListUserActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
