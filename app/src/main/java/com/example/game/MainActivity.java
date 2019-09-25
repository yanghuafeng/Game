package com.example.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private GameView gameView;
    private TextView textView;
    private String score;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        init();

    }

    private void init(){
        gameView = (GameView)findViewById(R.id.game_view);
        textView = (TextView)findViewById(R.id.score);
        Button button = (Button)findViewById(R.id.start);
        button.setOnClickListener(this);
        button = (Button)findViewById(R.id.img_btn_close);
        button.setOnClickListener(this);
        button = (Button)findViewById(R.id.jump);
        button.setOnClickListener(this);
        button = (Button)findViewById(R.id.cheat);
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN: {
                        if(!BaseApplication.getCanCheat()){
                            LoginDialog dialog = new LoginDialog(context, LoginDialog.LoginType.cheat);
                            dialog.show();
                        }else {
                            gameView.cheat();
                        }
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        gameView.normal();
                        break;
                    }
                }
                return true;
            }
        });
        GameView.OnChangeListener listener = new GameView.OnChangeListener() {
            @Override
            public void refresh() {
                score = "关卡："+gameView.getLevel()+" - "+gameView.getMission()+"   已对："+gameView.getDoneNum()+"/"+gameView.getNeedNum();
                textView.setText(score);
            }
        };
        gameView.setOnChangeListener(listener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_btn_close:
                System.exit(0);
                break;
            case R.id.start:
                gameView.startGame(false);
                break;
            case R.id.jump:
                if (!BaseApplication.getCanJump()) {
                    LoginDialog dialog = new LoginDialog(this, LoginDialog.LoginType.jump);
                    dialog.show();
                } else {
                    gameView.startGame(true);
                }
                break;
        }
    }
}
