package com.mhmtozcann.balon.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by razor on 23.04.2016.
 */
public class Main extends Activity implements View.OnClickListener{
    private static final String TAG = "MAINMENU";
    Context context;
    CheckBox sesKontrol;
    AlertDialog.Builder builder;
    Button yeni,skor,hakkinda,cikis;
    @Override
    public void onClick(View v) {
        builder = new AlertDialog.Builder(context);
        switch (v.getId()){
            case R.id.yeni:
                startActivity(new Intent(Main.this,AndroidLauncher.class));
                break;
            case R.id.skor:
                builder.setTitle("En Yüksek Skor");
                builder.setMessage("En Yüksek Skorunuz: "+ prefs.getInt("enyuksek",0));
                builder.setPositiveButton("Kapat", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.hakkinda:
                builder.setTitle("Hakkında");
                builder.setMessage("Mehmet Özcan - 130202016\nYusuf Pamukçu - 130202024\nOnur Polat - 130202054\nBerk Öztürk - 130202068");
                builder.setPositiveButton("Kapat", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog2= builder.create();
                dialog2.show();
                break;
            case R.id.cikis:
                this.finish();
                break;
        }
    }

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor = prefs.edit();

        context = Main.this;

        sesKontrol = (CheckBox)findViewById(R.id.ses);
        yeni = (Button)findViewById(R.id.yeni);
        skor = (Button)findViewById(R.id.skor);
        hakkinda = (Button)findViewById(R.id.hakkinda);
        cikis = (Button)findViewById(R.id.cikis);

        yeni.setOnClickListener(this);
        skor.setOnClickListener(this);
        hakkinda.setOnClickListener(this);
        cikis.setOnClickListener(this);

        if(prefs.getBoolean("ses",true)){
            sesKontrol.setChecked(true);
        }else sesKontrol.setChecked(false);

        sesKontrol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.d(TAG,"Oyun Sesleri Açık");
                    editor.putBoolean("ses",true);
                }else {
                    Log.d(TAG,"Oyun Sesleri Kapalı");
                    editor.putBoolean("ses",false);
                }
                editor.commit();
            }
        });
    }
}
