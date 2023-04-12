package com.example.app.chary;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class CustomizeActivity extends AppCompatActivity {
    SharedPreferences sh;
    EditText user_msg;
    Button default_btn;
    Switch send_location_switch;
    Switch send_time_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);

        setTitle("Customize Message");

        // initialization
        user_msg=findViewById(R.id.user_msg);
        default_btn=findViewById(R.id.default_button);
        send_location_switch=findViewById(R.id.send_location_switch);
        send_time_switch=findViewById(R.id.send_time_switch);


        // getting & setting stored values into widgets
        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        user_msg.setText(sh.getString("user_msg", "Help I'm in Danger!")); // set stored text
        send_location_switch.setChecked(sh.getBoolean("send_location_switch",true));
        send_time_switch.setChecked(sh.getBoolean("send_time_switch",true));


        // saving messages as they are typed in the text editor
        user_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("logging","Pressed!!!!!"+charSequence.toString());

                SharedPreferences.Editor edit = sh.edit();
                edit.putString("user_msg", charSequence.toString());
                edit.apply();

            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // saving send_location_switch on/off state
        send_location_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor edit = sh.edit();
                edit.putBoolean("send_location_switch", isChecked);
                edit.apply();
            }
        });

        // saving send_time_switch on/off state
        send_time_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor edit = sh.edit();
                edit.putBoolean("send_time_switch", isChecked);
                edit.apply();
            }
        });


        // when clicking on default btn
        default_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_msg.setText("HELP I'M IN DANGER!");
            }
        });
    }
}