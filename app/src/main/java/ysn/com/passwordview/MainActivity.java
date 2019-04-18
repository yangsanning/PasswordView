package ysn.com.passwordview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private PasswordView passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.main_activity_password_btn);
        button.setOnClickListener(this);

        passwordView = findViewById(R.id.main_activity_password_view);
        passwordView.setOnFinishListener(new PasswordView.OnFinishListener() {

            @Override
            public void onFinish(String password) {
                Toast.makeText(MainActivity.this, password, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_activity_password_btn:
                if (passwordView.isCirclePassword()) {
                    button.setText("显示密码");
                    passwordView.setCirclePassword(false);
                } else {
                    button.setText("隐藏密码");
                    passwordView.setCirclePassword(true);
                }
                break;
            default:
                break;
        }
    }
}