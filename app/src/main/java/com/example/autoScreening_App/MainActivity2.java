package com.example.autoScreening_App;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import static com.example.autoScreening_App.MainActivity.fileDate;

public class MainActivity2 extends AppCompatActivity {
    private Button mScreener;
    private TextView text;
    private EditText enterText;
    private TextToSpeech mTTS;
    private Button mVisitor;
    public Button mBack;
    private Button mEmployee;
    private EditText mAddress;
    public static int screener = 0;
    public static String getNumber;
    public static String category;
    public static String getAddress;
    public static boolean visitingState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity2);

        mScreener = findViewById(R.id.screener);
        text = findViewById(R.id.textTop);
        enterText = findViewById(R.id.enterText);
        mVisitor = findViewById(R.id.visitor);
        mEmployee = findViewById(R.id.employee);
        enterText.setVisibility(View.INVISIBLE);
        mAddress = findViewById(R.id.address);
        mAddress.setVisibility(View.INVISIBLE);

        //Text to Speech Module code
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        Log.e("TTS", "worked");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
        //Many of the nursing home facilities wanted a screener to verify all of the people's data 
        //Thus, I created a screener button that added their name and a key for all of the questions
        mScreener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScreener.setVisibility(View.INVISIBLE);
                mEmployee.setVisibility(View.INVISIBLE);
                mVisitor.setVisibility(View.INVISIBLE);
                enterText.setVisibility(View.VISIBLE);
                enterText.setHint("Enter Screener Name");
                String word = "Screener, please enter your name to verify this form.";
                text.setText(word);
                mTTS.speak("Screener, please enter your name to verify this form.", TextToSpeech.QUEUE_FLUSH, null);
                enterText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            enterText.setTextSize(40);
                            enterText.setHeight(120);
                            mEmployee.setEnabled(true);
                            mVisitor.setEnabled(true);
                            //Creates a pass and failed file and appends data to both files
                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileDate + "Pass.txt");
                            File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileDate +"Fail.txt");
                            try {
                                if (screener == 0) {
                                    Log.e("help", "helper");
                                    FileWriter fileWriter = new FileWriter(file, true);
                                    String breaker = "\r\n" + "----------------------------------------------------------------------------------------------------------------";
                                    String keyEnter = "\r\n" + "KEY FOR ENTERING QUESTIONS:";
                                    String keyExit = "\r\n" + "KEY FOR EXITING QUESTIONS:";
                                    String questionsEnter = "\r\n" + "1. Do you have a sore throat, cough, runny nose, or symptoms of a fever?"
                                            + "\r\n" + "2. Do you have shortness of breath, chest tightness, or body aches?" + "\r\n" + "3. Do you have diarrhea, nausea, vomiting, or loss of taste and smell?"
                                            + "\r\n" + "4. Have you had contact for more than 10 minutes with someone who is suspected or confirmed COVID-19 positive or is awaiting test results?" + "\r\n" + "IF YES - 5. Do you match the COVID-19 vaccination criteria?" +
                                            "\r\n" + "6. Have you worked in facilities or offices with recognized COVID-19 cases?" + "\r\n" + "IF YES - 7. Were you wearing recommended personal protective equipment?";
                                    String questionsExit = "\r\n" + "1. Have you developed any symptoms that were referenced upon entry?";
                                    String cont = "\r\n" + "SCREENER:  " + enterText.getText().toString();
                                    fileWriter.append(breaker);
                                    fileWriter.append(keyEnter);
                                    fileWriter.append(questionsEnter);
                                    fileWriter.append(breaker);
                                    fileWriter.append(keyExit);
                                    fileWriter.append(questionsExit);
                                    fileWriter.append(breaker);
                                    fileWriter.append(cont);
                                    fileWriter.close();

                                    FileWriter fileWriter2 = new FileWriter(file2, true);
                                    String breaker2 = "\r\n" + "----------------------------------------------------------------------------------------------------------------";
                                    String keyEnter2 = "\r\n" + "KEY FOR ENTERING QUESTIONS:";
                                    String keyExit2 = "\r\n" + "KEY FOR EXITING QUESTIONS:";
                                    String questionsEnter2 = "\r\n" + "1. Do you have a sore throat, cough, runny nose, or symptoms of a fever?"
                                            + "\r\n" + "2. Do you have shortness of breath, chest tightness, or body aches?" + "\r\n" + "3. Do you have diarrhea, nausea, vomiting, or loss of taste and smell?"
                                            + "\r\n" + "4. Have you had contact for more than 10 minutes with someone who is suspected or confirmed COVID-19 positive or is awaiting test results?" + "\r\n" + "IF YES - 5. Do you match the COVID-19 vaccination criteria?" +
                                            "\r\n" +  "6. Have you worked in facilities or offices with recognized COVID-19 cases?" + "\r\n" + "IF YES - 7. Were you wearing recommended personal protective equipment?";
                                    String questionsExit2 = "\r\n" + "1. Have you developed any symptoms that were referenced upon entry?";
                                    String cont2 = "\r\n" + "SCREENER:  " + enterText.getText().toString();
                                    fileWriter2.append(breaker2);
                                    fileWriter2.append(keyEnter2);
                                    fileWriter2.append(questionsEnter2);
                                    fileWriter2.append(breaker2);
                                    fileWriter2.append(keyExit2);
                                    fileWriter2.append(questionsExit2);
                                    fileWriter2.append(breaker2);
                                    fileWriter2.append(cont2);
                                    fileWriter2.close();
                                } else if (screener != 0) {
                                    FileWriter fileWriter2 = new FileWriter(file2, true);
                                    String cont2 = "\r\n" + "SCREENER:  " + enterText.getText().toString();
                                    fileWriter2.append(cont2);
                                    fileWriter2.close();

                                    FileWriter fileWriter3 = new FileWriter(file,true);
                                    String cont3 = "\r\n" + "SCREENER:  " + enterText.getText().toString();
                                    fileWriter3.append(cont3);
                                    fileWriter3.close();

                                }
                            }catch(FileNotFoundException e){
                                e.printStackTrace();
                            } catch(IOException e){
                                e.printStackTrace();
                            }

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    screener +=1;
                                    enterText.getText().clear();
                                    enterText.setVisibility(View.INVISIBLE);
                                    mScreener.setVisibility(View.VISIBLE);
                                    mVisitor.setVisibility(View.VISIBLE);
                                    mEmployee.setVisibility(View.VISIBLE);
                                    text.setText("Select if you are a visitor, employee, or screening supervisor.");


                                }
                            }, 500);



                        }
                        return false;
                    }
                });

            }
        });

        //When the visitor button is clicked, it opens the next activity with the screening questions
        mVisitor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                visitingState = true;
                openActivity1();
                category = "VISITOR ||";

            }
        });

        //When the employee button is clicked, it opens the next activity with the screening questions 
        mEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               visitingState = false;
                screener +=1;
                getNumber = "";
                getAddress = "";
                category = "EMPLOYEE ||";
                openActivity1();
            }
        });

    }


    //Simple functions to open the next activity, and transition to the next Android screen
    public void openActivity1() {
        Intent intent = new Intent(this, com.example.autoScreening_App.MainActivity.class);
        startActivity(intent);
    }

    public void openActivity2() {
        Intent intent = new Intent(this, com.example.autoScreening_App.MainActivity2.class);
        startActivity(intent);

    }



}
