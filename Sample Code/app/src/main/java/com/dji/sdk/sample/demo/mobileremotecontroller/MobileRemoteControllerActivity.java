package com.dji.sdk.sample.demo.mobileremotecontroller;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dji.sdk.sample.R;
import com.dji.sdk.sample.internal.utils.ModuleVerificationUtil;

import java.util.ArrayList;
import java.util.Locale;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;

public class MobileRemoteControllerActivity extends AppCompatActivity {

    FlightController flightController;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    Button voiceCommand;
    boolean isFlightOnAir = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_remote_controller);
        voiceCommand = (Button)findViewById(R.id.button_voice_command);
        voiceCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceCommanding();
            }
        });


    }


    public void startVoiceCommanding() {

               Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
               intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                       RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
               intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
               intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                       "Give me a command...");
               try {
                   startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
               } catch (ActivityNotFoundException a) {
                   Toast.makeText(getApplicationContext(),
                           "Sorry, Your device doesn't support Speech input",
                           Toast.LENGTH_SHORT).show();
               }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        flightController = ModuleVerificationUtil.getFlightController();
        if (flightController == null) {
            return;
        }

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //Toast.makeText(this, result.get(0), Toast.LENGTH_SHORT).show();

                    if (result.get(0).equalsIgnoreCase("start") && isFlightOnAir==false) {
                        isFlightOnAir=true;
                        Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
                        flightController.startTakeoff(new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {

                            }
                        });
                    }

                    if (result.get(0).equalsIgnoreCase("stop")) {
                        isFlightOnAir=false;
                        Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
                        flightController.startLanding(new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {

                            }
                        });

                    }

                    if (result.get(0).equalsIgnoreCase("done")) {
                        isFlightOnAir=false;
                        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                        flightController.confirmLanding(new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {

                            }
                        });

                    }
                    startVoiceCommanding();
                }

                break;

            }

        }

    }
}
