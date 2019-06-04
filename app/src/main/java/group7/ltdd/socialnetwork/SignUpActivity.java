package group7.ltdd.socialnetwork;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    EditText edtUsername,edtPassword,edtRepassword, edtFullname, edtEmail;
    Button btnSignup;
    TextView txtLinkLogin;

    FirebaseAuth auth;

    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        AddControls();
        AddEvents();
    }

    private void AddControls() {
        edtUsername = this.<EditText>findViewById(R.id.txtUsername);
        edtPassword= this.<EditText>findViewById(R.id.txtPassword);
        edtRepassword= this.<EditText>findViewById(R.id.txtRePassword);
        edtEmail= this.<EditText>findViewById(R.id.txtEmail);
        edtFullname= this.<EditText>findViewById(R.id.txtFullname);
        btnSignup = this.<Button>findViewById(R.id.btnSignUp);
        txtLinkLogin= this.<TextView>findViewById(R.id.txtLinkLogin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Đăng ký");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

    }

    private void AddEvents() {
        txtLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtPassword.getText().toString().equals(edtRepassword.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "Không khớp mật khẩu!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                String repassword = edtRepassword.getText().toString();
                String email = edtEmail.getText().toString();
                String name = edtFullname.getText().toString();
                if (TextUtils.isEmpty(username)|| TextUtils.isEmpty(password)||TextUtils.isEmpty(repassword)
                    ||TextUtils.isEmpty(email)||TextUtils.isEmpty(name))
                {
                    Toast.makeText(SignUpActivity.this, "Bạn phải nhập đầy đủ dữ liệu", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    register(username,email,password,name);
                }



            }
        });
    }

    private void register(final String username, String email, String password, final String name)
    {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            try {
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                String userid = firebaseUser.getUid();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                reference = database.getReference("account");
                                reference.child(userid).child("imageURL").setValue("default");
                                reference.child(userid).child("name").setValue(name);
                                reference.child(userid).child("username").setValue(username);
                                Intent intent = new Intent(SignUpActivity.this,ListUserActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            catch (Exception ex)
                            {

                            }

                        }
                        else
                        {
                            Toast.makeText(SignUpActivity.this, "Lỗi đăng ký! Có thể đã tồn tại tài khoản!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}
