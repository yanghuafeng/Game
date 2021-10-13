package com.example.game;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by YHF at 16:18 on 2019-09-25.
 */

public class LoginDialog extends Dialog implements View.OnClickListener {
    private EditText text;
    private final String jumpPassword = "jump";
    private final String cheatPassword = "cheat";
    private LoginType type ;

    public LoginDialog(Context context,LoginType t) {
        super(context);
        type = t;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login);
        Button button = (Button)findViewById(R.id.confirm);
        button.setOnClickListener(this);
        button = (Button)findViewById(R.id.close);
        button.setOnClickListener(this);
        text = (EditText)findViewById(R.id.passsword);

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.close:
                dismiss();
                break;
            case R.id.confirm:
                if(type == LoginType.jump) {
                    if (text.getText().toString().equals(jumpPassword)) {
                        BaseApplication.setCanJump(true);
                        dismiss();
                        Toast.makeText(getContext(),R.string.password_right,Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(),R.string.password_false,Toast.LENGTH_SHORT).show();
                        text.setText("");
                    }
                }else if(type == LoginType.cheat){
                    if (text.getText().toString().equals(cheatPassword)) {
                        BaseApplication.setCanCheat(true);
                        dismiss();
                        Toast.makeText(getContext(),R.string.password_right,Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(),R.string.password_false,Toast.LENGTH_SHORT).show();
                        text.setText("");
                    }
                }
                break;
        }
    }

    public enum LoginType{
        jump,
        cheat
    }
}
