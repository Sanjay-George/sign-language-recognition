package com.example.vishwashrisairam.opencvcamera2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Vishwashrisairam on 4/1/2017.
 */

public class Result extends Activity implements TextToSpeech.OnInitListener{

    private TextToSpeech tts;
    private TextView view;
    private Button b;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.result);

        tts=new TextToSpeech(this,this);

        Bundle extras = getIntent().getExtras();
        String inputString = extras.getString("message");
        view = (TextView) findViewById(R.id.textView);
        view.setText(inputString);

        b=(Button)findViewById(R.id.speakButton);
        b.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                speakOut();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if(status==TextToSpeech.SUCCESS){
            int result=tts.setLanguage(Locale.US);

            if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","Language is not supported");
            }else{
                b.setEnabled(true);
                speakOut();
            }
        }else{
            Log.e("TTS","Initialised Failed");
        }
    }

    private void speakOut() {
        String text=view.getText().toString();
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    public void finish() {

        super.finish();
    }



}
