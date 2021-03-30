package com.example.autoScreening_App;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.autoScreening_App.MainActivity2.category;
import static com.example.autoScreening_App.MainActivity2.getAddress;
import static com.example.autoScreening_App.MainActivity2.getNumber;
import static com.example.autoScreening_App.MainActivity2.screener;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech mTTS;
    private Button mEnter;
    private Button mExit;
    private Button mYes;
    private Button mNo;
    private TextView text;
    private TextView mResult;
    private Button mTemperature;
    private EditText mName;
    private EditText mTemp;
    int counterEnter = 0;
    int enterSign = 0;
    int exitSign = 0;
    int visitorEnterSign = 0;
    int visitorEnterSign2 = 0;
    int visitorEnterSign3  = 0;
    int personleave = 0;
    int name = 0;
    int visiting = 0;
    int otherFamilySelection = 0;
   
    @SuppressLint("SimpleDateFormat")
    public static DateFormat dateFormat = new SimpleDateFormat("MM-dd");
    public static Date newDate = new Date();
    public static String fileDate = dateFormat.format(newDate);

    Toolbar toolbar;
    Button btn_get_sign, mClear, mGetSign, mCancel;

    File file;
    Dialog dialog;
    LinearLayout mContent;
    View view;
    signature mSignature;
    Bitmap bitmap;
    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/Signatures - Month " + signatureMonth + "/";


    public static DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    public static DateFormat df2 = new SimpleDateFormat("MM");
    public static Date date1 = new Date();
    public static String signatureMonth = df2.format(date1);
    public static Date date2 = new Date();
    public static String shortDate = df.format(date2);

    //These are lot of various counter variables, I use to track the flow of the questionnaire
    //The names explain their purpose fairly well
    int answerYes= 0;
    int exitingState = 0;
    int answerNo = 0;
    int exitTemp = 0;
    int visitorInformation = 0;
    String date = java.text.DateFormat.getTimeInstance().format(new Date());
    
    /* 
    Note: The hardest code in this project was the functions I created at the bottom. The questionnaire flow was long and time taking, but it follows the same logic throughout the code
            As you look at the code, you will see similar code being repeated for different situations
            I created functions to decrease redundency, but I continue to make efforts to make my code more efficient and clean
     */



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //Permissions to create file in device's storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

        //Declaring the various elements in my activity that my app is composed of
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btn_get_sign = (Button) findViewById(R.id.signature);
        btn_get_sign.setVisibility(View.INVISIBLE);

        file = new File(DIRECTORY);
        if (!file.exists()) {
            file.mkdir();
        }
        mEnter = findViewById(R.id.entering);
        mExit = findViewById(R.id.exiting);
        mResult = findViewById(R.id.result);
        mResult.setVisibility(View.INVISIBLE);
        mTemperature = findViewById(R.id.temperature);
        mTemperature.setVisibility(View.INVISIBLE);
        mName = findViewById(R.id.name);
        mName.setVisibility(View.INVISIBLE);
        mTemp = findViewById(R.id.temp);
        mYes = findViewById(R.id.yes);
        mNo = findViewById(R.id.no);
        mYes.setVisibility(View.INVISIBLE);
        mNo.setVisibility(View.INVISIBLE);
        mTemp.setVisibility(View.INVISIBLE);

        //Text-To-Speech Module code
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.US);
                    mTTS.speak("Please select if you are entering or exiting. You must answer all questions with yes or no.", TextToSpeech.QUEUE_FLUSH, null);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        mEnter.setEnabled(true);
                        mExit.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        mTTS.speak("Please select if you are entering or exiting. You must answer all questions with yes or no.", TextToSpeech.QUEUE_FLUSH, null);

        dialog = new Dialog(MainActivity.this);
        // Removing the features of Normal Dialogs
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signature);
        dialog.setCancelable(true);

        btn_get_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Function call for Digital Signature
                dialog_action();

            }
        });

        text = findViewById(R.id.shomtext);

        //After the user has started the screening, it asks if the user is entering or exiting the facility
        //If the user is an employee who clicks enter, it begins by asking the user their name and to sign
        //If the user is a visitor who clicks enter, it begins by asking the user if they are a family visitor or other visitor
        mEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTemp.getText().clear();

                if(MainActivity2.visitingState == true) {
                    mEnter.setVisibility(View.INVISIBLE);
                    mExit.setVisibility(View.INVISIBLE);
                    String word3 = "Please select if you are a family visitor or other visitor.";
                    text.setText(word3);
                    mTTS.speak("Please select if you are a family visitor or other visitor.", TextToSpeech.QUEUE_FLUSH, null);
                    mYes.setEnabled(true);
                    mNo.setEnabled(true);
                    mYes.setVisibility(View.VISIBLE);
                    mYes.setText("FAMILY VISITOR");
                    mNo.setVisibility(View.VISIBLE);
                    mNo.setText("OTHER VISITOR");
                    otherFamilySelection = 1;
                }
                else {
                    mName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    answerYes = 1;
                    answerNo = 1;
                    mName.setVisibility(View.VISIBLE);
                    mName.setHint("Enter Name");
                    mEnter.setVisibility(View.INVISIBLE);
                    mExit.setVisibility(View.INVISIBLE);
                    speak();
                    btn_get_sign.setVisibility(View.VISIBLE);
                    mYes.setEnabled(true);
                    mNo.setEnabled(true);
                    enterSign ++;
                    mTemperature.setVisibility(View.INVISIBLE);
                    mResult.setVisibility(View.INVISIBLE);
                }
            }
        });

        //After the user has started the screening, it asks if the user is entering or exiting the facility
        //If the user clicks exit, it begins by asking the user their name and to sign
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTemp.getText().clear();
                exitingState = 1;
                getNumber = "";
                getAddress = "";
                if(MainActivity2.visitingState){
                    category = "VISITOR ||";
                }else{
                    category = "EMPLOYEE ||";
                }
                //Below I use a property called setVisibility to determine if I want to show or not show a certain element on the app screen
                mName.setVisibility(View.VISIBLE);
                mName.setHint("Enter Name");
                mEnter.setVisibility(View.INVISIBLE);
                mExit.setVisibility(View.INVISIBLE);
                personleave = 1;
                speak();
                btn_get_sign.setVisibility(View.VISIBLE);
                mTemperature.setVisibility(View.INVISIBLE);
                mResult.setVisibility(View.INVISIBLE);
            }
        });

        //Basically, I organized this app by having a YES button clicker and NO button clicker
        //Under each button, I have a bunch of commands and counter variables to direct the flow of the app
        //I use a lot of IF-ElSE statements to do this
        //Below is all of the questions and flow of the app if the user clicked YES to answer a question
        mYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Below is all of the beginning code for the visitor
                //Most facilities wanted visitors to enter their phone number and address, so I had to add this additional info
                if(otherFamilySelection == 1) {
                    visiting = 0;
                    screener ++;
                    mTemp.setVisibility(View.VISIBLE);
                    mYes.setVisibility(View.INVISIBLE);
                    mNo.setVisibility(View.INVISIBLE);
                    mEnter.setVisibility(View.INVISIBLE);
                    mName.setVisibility(View.VISIBLE);
                    mExit.setVisibility(View.INVISIBLE);
                    mName.setHint("Enter Address: ");
                    mTemp.setHint("Enter Phone Number: ");
                    String word = "Please enter your address and phone number. It will ask for your name later.";
                    //prints the text on the app screen
                    text.setText(word);
                    //Uses Text to Speech to speak the question or statement to the user
                    mTTS.speak("Please enter your address and phone number. It will ask for your name later.", TextToSpeech.QUEUE_FLUSH, null);
                    mName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        //Used a property called onEditorAction, so when the user clickes done on the keyboard it will move forward with the app
                        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                getAddress = " ADDRESS:" + mName.getText().toString();
                                category = "VISITOR ||";
                            }
                            return false;
                        }
                    });
                    mTemp.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                getAddress = " ADDRESS:" + mName.getText().toString();
                                getNumber = " NUMBER:" + mTemp.getText().toString() + " ";
                                category = "VISITOR ||";

                                mName.getText().clear();
                                mTemp.getText().clear();
                                mName.setVisibility(View.INVISIBLE);
                                mTemp.setVisibility(View.INVISIBLE);

                                Handler handler = new Handler();
                                handler. postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                                        answerYes= 1;
                                        answerNo= 1;
                                        mName.setVisibility(View.VISIBLE);
                                        mName.setHint("Enter Name");
                                        mEnter.setVisibility(View.INVISIBLE);
                                        mExit.setVisibility(View.INVISIBLE);
                                        speak();
                                        String date = java.text.DateFormat.getDateTimeInstance().format(new Date());
                                        btn_get_sign.setVisibility(View.VISIBLE);
                                        visitorEnterSign = 1;
                                        mTemperature.setVisibility(View.INVISIBLE);
                                        mResult.setVisibility(View.INVISIBLE);
                                    }
                                },1000);


                            }
                            return false;
                        }
                    });

                }

                //If user answered NO to one of the preliminary visiting questions
                if(visiting ==1) {
                    otherFamilySelection = 0;
                    mYes.setVisibility(View.VISIBLE);
                    mNo.setVisibility(View.VISIBLE);
                    String word5= "Have you provided a negative COVID-19 test twice in the last week?";
                    text.setText(word5);
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Have you provided a negative COVID-19 test twice in the last week?", TextToSpeech.QUEUE_FLUSH, null);
                            visitorInformation = 2;
                            visiting = 0;


                        }
                    }, 0);
                }
                //Asks the visitor for their phone number and address
                else if (visitorInformation ==2) {
                    mTemp.setVisibility(View.VISIBLE);
                    mYes.setVisibility(View.INVISIBLE);
                    mNo.setVisibility(View.INVISIBLE);
                    mEnter.setVisibility(View.INVISIBLE);
                    mName.setVisibility(View.VISIBLE);
                    mExit.setVisibility(View.INVISIBLE);
                    screener +=1;
                    mName.setHint("Enter Address: ");
                    mTemp.setHint("Enter Phone Number: ");
                    String word = "Please enter your address and phone number. It will ask for your name later.";
                    text.setText(word);
                    mTTS.speak("Please enter your address and phone number. It will ask for your name later.", TextToSpeech.QUEUE_FLUSH, null);
                    mName.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                getAddress = " ADDRESS:" + mName.getText().toString();
                                category = "VISITOR ||";
                            }
                            return false;
                        }
                    });

                    mTemp.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                getAddress = " ADDRESS:" + mName.getText().toString();
                                getNumber = " NUMBER:" + mTemp.getText().toString() + " ";
                                category = "VISITOR ||";
                                mName.getText().clear();
                                mTemp.getText().clear();
                                mName.setVisibility(View.INVISIBLE);
                                mTemp.setVisibility(View.INVISIBLE);

                                //A huge part of how I timed the app fuctions is by using two handlers (nested handler)
                                //Handler allows you to run the code after a given amount of time (given at the bottom)
                                //Helped with reducing crashes and helping the app move smoothly
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                                        answerYes= 1;
                                        answerNo= 1;
                                        mName.setVisibility(View.VISIBLE);
                                        mName.setHint("Enter Name");
                                        mEnter.setVisibility(View.INVISIBLE);
                                        mExit.setVisibility(View.INVISIBLE);
                                        speak();
                                        counterEnter = 0;
                                        name = 0;
                                        String date = java.text.DateFormat.getDateTimeInstance().format(new Date());
                                        btn_get_sign.setVisibility(View.VISIBLE);
                                        visitorEnterSign3 = 1;

                                        mTemperature.setVisibility(View.INVISIBLE);
                                        mResult.setVisibility(View.INVISIBLE);
                                    }
                                },1000);


                            }
                            return false;
                        }
                    });
                }
                //This is where the screening begins
                //If the user answered YES to the first screening question, it will enter this if statement
                if (answerYes== 1) {
                    visiting = 0;
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    mYes.setVisibility(View.VISIBLE);
                    mNo.setVisibility(View.VISIBLE);
                    String word3 = "Do you have any chronic conditions that may lead to these symptoms?";
                    text.setText(word3);
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Do you have any chronic conditions that may lead to these symptoms?", TextToSpeech.QUEUE_FLUSH, null);
                            answerYes+= 1;
                            answerNo= 4;

                        }
                    }, 0);
                }
                //If the user answered YES to the previous question, it will proceed through the screening and enter this if statement
                else if (answerYes== 2) {
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    mYes.setVisibility(View.VISIBLE);
                    mNo.setVisibility(View.VISIBLE);
                    String word = "Do you have shortness of breath, chest tightness, or body aches?";
                    text.setText(word);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Do you have shortness of breath, chest tightness, or body aches?", TextToSpeech.QUEUE_FLUSH, null);
                            answerYes+= 1;
                            answerNo= 2;

                        }
                    }, 0);
                }
                //If the user answered YES to the previous question, it will proceed through the screening and enter this if statement
                else if (answerYes== 3) {
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    mYes.setVisibility(View.VISIBLE);
                    mNo.setVisibility(View.VISIBLE);
                    String word6 = "Do you have any chronic conditions that may lead to these symptoms?";
                    text.setText(word6);
                    Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Do you have any chronic conditions that may lead to these symptoms?", TextToSpeech.QUEUE_FLUSH, null);
                            answerYes+= 1;
                            answerNo= 5;
                        }
                    }, 0);
                }
                //If the user answered YES to the previous question, it will proceed through the screening and enter this if statement
                else if (answerYes== 4) {
                    mYes.setVisibility(View.VISIBLE);
                    mNo.setVisibility(View.VISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    String word4 = "Do you have diarrhea, nausea, vomiting, or loss of taste and smell?";
                    text.setText(word4);
                    Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Do you have diarrhea, nausea, vomiting, or loss of taste and smell?", TextToSpeech.QUEUE_FLUSH, null);
                            answerYes+= 1;
                            answerNo= 3;
                        }
                    }, 0);
                }
                //If the user answered YES to the previous question, it will proceed through the screening and enter this if statement
                else if (answerYes== 5) {
                    mYes.setVisibility(View.VISIBLE);
                    mNo.setVisibility(View.VISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    String word6 = "Do you have any chronic conditions that may lead to these symptoms?";
                    text.setText(word6);
                    Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Do you have any chronic conditions that may lead to these symptoms?", TextToSpeech.QUEUE_FLUSH, null);
                            answerYes+= 1;
                            answerNo= 6;
                        }
                    }, 0);
                }
                //If the user answered YES to the previous question, it will proceed through the screening and enter this if statement
                else if (answerYes== 6) {
                    mYes.setVisibility(View.INVISIBLE);
                    mNo.setVisibility(View.INVISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    String word0 = "Screener, please take the user's temperature. Click the temperature button after completing the temperature scanning.";
                    text.setText(word0);
                    Handler handler8 = new Handler();
                    handler8.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Screener, please take the user's temperature. Click the temperature button after completing the temperature scanning.", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }, 0);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    mTemperature.postDelayed(new Runnable() {
                        public void run() {
                            mTemperature.setVisibility(View.VISIBLE);
                        }
                    }, 0);
                    mTemperature.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mName.setVisibility(View.INVISIBLE);
                            mTemp.setVisibility(View.VISIBLE);
                            mTemp.setHint("Enter Temperature");
                            String word = "Screener, notify the user what their temperature was. \r\nUser, please type your temperature and click done on the keyboard when finished.";
                            text.setText(word);
                            mTTS.speak(word, TextToSpeech.QUEUE_FLUSH, null);
                            mTemperature.setVisibility(View.INVISIBLE);
                            mTemp.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                                        float number = Float.parseFloat(mTemp.getText().toString());
                                        //User enters his temperature and clicks done
                                        //If his/her temperature is too high, it will enter the below if statement
                                        if (number > 99.6 && number < 110) {
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mTemp.setVisibility(View.INVISIBLE);
                                                    mTemp.getText().clear();
                                                    mName.setVisibility(View.INVISIBLE);
                                                    String weird = "Your temperature is too high. Please ask the supervisor or a nurse to get your oral temperature.";
                                                    text.setText(weird);
                                                    Handler handler2 = new Handler();
                                                    handler2.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            mTTS.speak("Your temperature is too high. Please ask the supervisor or a nurse to get your oral temperature.", TextToSpeech.QUEUE_FLUSH, null);
                                                        }
                                                    }, 0);

                                                }
                                            }, 1000);
                                            Handler handler2 = new Handler();
                                            handler2.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mTemp.setVisibility(View.VISIBLE);
                                                    mTemp.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                                                        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                                                            if (actionId == EditorInfo.IME_ACTION_DONE) {

                                                                float number = Float.parseFloat(mTemp.getText().toString());
                                                                //User previously entered a temperature that was too high
                                                                //User now  enters his oral temperature  and clicks done
                                                                //If his/her oral temperature is too high, it will enter the below if statement
                                                                //User will fail screening, as they may have a fever
                                                                if (number > 99.6 && number < 110) {
                                                                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileDate +"Fail.txt");
                                                                    try {
                                                                        String temp = "[Temp:" + mTemp.getText().toString() + "] ";
                                                                        String answer1 = " 1-NO ";
                                                                        String answer2 = " 2-NO";
                                                                        String answer3 = " 3-NO";
                                                                        String contExit =  "\r\n" + "["+shortDate + " "+ date+"] " + category + " NAME:" + mName.getText().toString() + getNumber + getAddress + " ENTERING:";
                                                                        FileWriter fileWriter = new FileWriter(file, true);
                                                                        String cont = "FAILED ";
                                                                        fileWriter.append(fileDate);
                                                                        fileWriter.append(contExit);
                                                                        fileWriter.append(answer1);
                                                                        fileWriter.append(answer2);
                                                                        fileWriter.append(answer3);
                                                                        fileWriter.append(temp);
                                                                        fileWriter.append(cont);
                                                                        fileWriter.close();
                                                                        Log.i("name", cont);
                                                                    } catch (FileNotFoundException e) {
                                                                        e.printStackTrace();
                                                                    } catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    sendSMS();
                                                                    mTemp.getText().clear();
                                                                    mTemp.setVisibility(View.INVISIBLE);
                                                                    mName.setVisibility(View.INVISIBLE);
                                                                    mName.getText().clear();
                                                                    Handler handler = new Handler();
                                                                    handler.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            mName.setVisibility(View.INVISIBLE);
                                                                            mTemp.setVisibility(View.INVISIBLE);
                                                                            mTemp.getText().clear();
                                                                            mName.getText().clear();
                                                                            String weird = "You may have COVID-19. Please leave.";
                                                                            text.setText(weird);
                                                                            Handler handler2 = new Handler();
                                                                            handler2.postDelayed(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    mTTS.speak("You may have COVID-19. Please leave.", TextToSpeech.QUEUE_FLUSH, null);
                                                                                }
                                                                            }, 0);
                                                                            mResult.setVisibility(View.VISIBLE);
                                                                            mResult.setText("FAILED");
                                                                            mResult.setTextColor(Color.parseColor("#1800f0"));
                                                                            Handler handler = new Handler();
                                                                            handler.postDelayed(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    mTemp.setVisibility(View.INVISIBLE);
                                                                                    mTemp.getText().clear();
                                                                                    mResult.setVisibility(View.INVISIBLE);
                                                                                    text.setText("Please select if you are entering or exiting. \r\nYou must answer all questions with yes or no.");
                                                                                    mEnter.setVisibility(View.VISIBLE);
                                                                                    mExit.setVisibility(View.VISIBLE);
                                                                                    answerYes= 0;
                                                                                    openActivity2();
                                                                                }
                                                                            }, 5000);

                                                                        }
                                                                    }, 1000);

                                                                    //If the user entered a oral temperature that was normal, it progresses through the screening and enters the below else if
                                                                } else if (number <= 99.6 && number > 85) {
                                                                    Handler handler = new Handler();
                                                                    handler.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            mTemp.setVisibility(View.INVISIBLE);
                                                                            mName.setVisibility(View.INVISIBLE);
                                                                            String weird = "Have you had contact for more than 10 minutes with someone who is suspected or confirmed COVID-19 positive or is awaiting test results?";
                                                                            text.setText(weird);
                                                                            Handler handler2 = new Handler();
                                                                            handler2.postDelayed(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    mTTS.speak("Have you had contact for more than 10 minutes with someone who is suspected or confirmed COVID-19 positive or is awaiting test results?", TextToSpeech.QUEUE_FLUSH, null);
                                                                                    answerYes+= 1;
                                                                                    answerNo= 7;
                                                                                    mYes.setVisibility(View.VISIBLE);
                                                                                    mNo.setVisibility(View.VISIBLE);
                                                                                }
                                                                            }, 0);


                                                                        }
                                                                    }, 1000);

                                                                }
                                                                //Makes sure that the user entered a normal temperature (If user entered 10 degrees for temperature it will ask the user to enter again)
                                                                  else {
                                                                        mTemp.setVisibility(View.INVISIBLE);
                                                                        String weird = "You may have entered your temperature incorrectly.";
                                                                        text.setText(weird);
                                                                        Handler handler2 = new Handler();
                                                                        handler2.postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                mTTS.speak("You may have entered the temperature incorrectly.", TextToSpeech.QUEUE_FLUSH, null);

                                                                            }
                                                                        }, 0);
                                                                        Handler handler3 = new Handler();
                                                                        handler3.postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                openActivity2();

                                                                            }
                                                                        }, 2000);
                                                                    }

                                                            }
                                                            return false;
                                                        }
                                                    });

                                                }
                                            }, 1000);
                                        }
                                        //If user initially entered a normal temperature it progresses through screening and enters the below else if statement
                                        else if (number <= 99.6 && number > 85) {
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mTemp.setVisibility(View.INVISIBLE);
                                                    mName.setVisibility(View.INVISIBLE);
                                                    String weird = "Have you had contact for more than 10 minutes with someone who is suspected or confirmed COVID-19 positive or is awaiting test results?";
                                                    text.setText(weird);
                                                    Handler handler2 = new Handler();
                                                    handler2.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            mTTS.speak("Have you had contact for more than 10 minutes with someone who is suspected or confirmed COVID-19 positive or is awaiting test results?", TextToSpeech.QUEUE_FLUSH, null);
                                                            answerYes+= 1;
                                                            answerNo= 7;
                                                            mYes.setVisibility(View.VISIBLE);
                                                            mNo.setVisibility(View.VISIBLE);
                                                        }
                                                    }, 0);


                                                }
                                            }, 1000);
                                        }
                                        //Makes sure that the user entered a normal temperature (If user entered 10 degrees for temperature it will ask the user to enter again)
                                        else {
                                                mYes.setVisibility(View.VISIBLE);
                                                mNo.setVisibility(View.VISIBLE);
                                                mTemp.setVisibility(View.INVISIBLE);
                                                String weird = "You may have entered your temperature incorrectly.";
                                                text.setText(weird);
                                                Handler handler2 = new Handler();
                                                handler2.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mTTS.speak("You may have entered the temperature incorrectly.", TextToSpeech.QUEUE_FLUSH, null);
                                                    }
                                                }, 0);
                                                Handler handler3 = new Handler();
                                                handler3.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        openActivity2();

                                                    }
                                                }, 4000);
                                            }

                                    }
                                    return false;
                                }
                            });


                        }

                    });
                }
                //If user answers YES to previously asked question (Have you had contact for more than 10 min...?)
                //Enters the below else if statement
                else if (answerYes== 7) {
                    mYes.setVisibility(View.INVISIBLE);
                    mNo.setVisibility(View.INVISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mName.setVisibility(View.INVISIBLE);
                            mTemp.setVisibility(View.INVISIBLE);
                            String weird = "Are you: \r\n" +
                                    "1. fully vaccinated (i.e., ≥2 weeks following receipt of the second dose in a 2-dose series, or ≥2 weeks following receipt of one dose of a single-dose vaccine);\n" +
                                    "2. within 3 months following receipt of the last dose in the series; AND,\n" +
                                    "3. asymptomatic since the current COVID-19 exposure.;";
                            text.setText(weird);
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTTS.speak("Do you match all of the criteria that is listed in the question?", TextToSpeech.QUEUE_FLUSH, null);
                                    answerYes+= 1;
                                    answerNo= 8;
                                    mYes.setVisibility(View.VISIBLE);
                                    mNo.setVisibility(View.VISIBLE);
                                }
                            }, 0);
                        }
                    }, 0);
                }

                //If user enters YES to the previous questions it enter the below else if
                else if (answerYes== 8) {
                    mYes.setVisibility(View.INVISIBLE);
                    mNo.setVisibility(View.INVISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    //Create pass file with the date
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileDate + "Pass.txt");
                    //Append user's data to this file
                    try {
                        String temp = " [Temp:" + mTemp.getText().toString() + "] ";
                        String answer1 = " 1-NO ";
                        String answer2 = " 2-NO ";
                        String answer3 = " 3-NO ";
                        String answer4 = " 4-YES ";
                        String answer5 = " 5-YES (All Criteria Met) ";
                        String contEnter = "\r\n" + "["+shortDate + " "+ date+"] " + category + " NAME:"  + mName.getText().toString() + com.example.autoScreening_App.MainActivity2.getNumber + getAddress + " ENTERING:";
                        FileWriter fileWriter = new FileWriter(file, true);
                        String cont = "PASSED ";
                        fileWriter.append(contEnter);
                        fileWriter.append(answer1);
                        fileWriter.append(answer2);
                        fileWriter.append(answer3);
                        fileWriter.append(temp);
                        fileWriter.append(answer4);
                        fileWriter.append(answer5);
                        fileWriter.append(cont);
                        fileWriter.close();
                        Log.i("name", cont);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //User has passed the screening and it lets him/her now
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mName.setVisibility(View.INVISIBLE);
                            mName.getText().clear();
                            String weird = "You may enter! Have a nice day!";
                            text.setText(weird);
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTTS.speak("You may enter! Have a nice day!", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }, 0);

                            mResult.setVisibility(View.VISIBLE);
                            mResult.setText("PASSED");
                            mResult.setTextColor(Color.parseColor("#1800f0"));
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mResult.setVisibility(View.INVISIBLE);
                                    text.setText("Click the start button. \r\nOnly answer after the prompt pops up. \r\nYou must answer yes or no for all questions. \r\nSpeak yes to begin.");
                                    mEnter.setVisibility(View.VISIBLE);
                                    mExit.setVisibility(View.VISIBLE);
                                    openActivity2();
                                }
                            }, 5000);

                        }
                    }, 1000);
                }
                //If user responds YES to another screening question before this, it will enter this else if
                else if (answerYes== 9) {
                    mYes.setVisibility(View.VISIBLE);
                    mNo.setVisibility(View.VISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    String word3 = "Were you wearing recommended personal protective equipment?";
                    text.setText(word3);
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Were you wearing recommended personal protective equipment?", TextToSpeech.QUEUE_FLUSH, null);
                            answerYes+= 1;
                            answerNo= 10;
                        }
                    }, 0);
                }
                //Based on user's responses, it may enter this else if statement
                else if(answerYes== 10) {
                    mYes.setVisibility(View.INVISIBLE);
                    mNo.setVisibility(View.INVISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    //Again, creates pass file
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileDate + "Pass.txt");
                    //Appends data to this file
                    try {
                        String temp = " [Temp:" + mTemp.getText().toString() + "] ";
                        String answer1 = " 1-NO ";
                        String answer2 = " 2-NO ";
                        String answer3 = " 3-NO ";
                        String answer4 = " 4-NO ";
                        String answer5 = " 5-N/A ";
                        String answer6 = " 6-YES ";
                        String answer7 = " 7-YES ";
                        String contEnter = "\r\n" + "["+shortDate + " "+ date+"] " + category + " NAME:"  + mName.getText().toString() + com.example.autoScreening_App.MainActivity2.getNumber + getAddress + " ENTERING:";
                        FileWriter fileWriter = new FileWriter(file, true);
                        String cont = "PASSED ";
                        fileWriter.append(contEnter);
                        fileWriter.append(answer1);
                        fileWriter.append(answer2);
                        fileWriter.append(answer3);
                        fileWriter.append(temp);
                        fileWriter.append(answer4);
                        fileWriter.append(answer5);
                        fileWriter.append(answer6);
                        fileWriter.append(answer7);
                        fileWriter.append(cont);
                        fileWriter.close();
                        Log.i("name", cont);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mName.setVisibility(View.INVISIBLE);
                            mName.getText().clear();
                            String weird = "You may enter! Have a nice day!";
                            text.setText(weird);
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTTS.speak("You may enter! Have a nice day!", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }, 0);

                            mResult.setVisibility(View.VISIBLE);
                            mResult.setText("PASSED");
                            mResult.setTextColor(Color.parseColor("#1800f0"));
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mResult.setVisibility(View.INVISIBLE);
                                    text.setText("Click the start button. \r\nOnly answer after the prompt pops up. \r\nYou must answer yes or no for all questions. \r\nSpeak yes to begin.");
                                    mEnter.setVisibility(View.VISIBLE);
                                    mExit.setVisibility(View.VISIBLE);
                                    openActivity2();
                                }
                            }, 5000);

                        }
                    }, 1000);
                }

                //If user is exiting, and answered YES to the exiting question, it enters this if statement
                if (exitingState == 1) {
                    mYes.setVisibility(View.INVISIBLE);
                    mNo.setVisibility(View.INVISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);

                    //Creates fail file
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileDate + "Fail.txt");
                    //Appends data to this file
                    try {
                        String answer1 = " 1-YES ";
                        String contExit = "\r\n" + "["+shortDate + " "+ date+"] " + category + " NAME: " + mName.getText().toString() + getNumber + getAddress + " EXITING:";
                        FileWriter fileWriter = new FileWriter(file, true);
                        String cont = "FAILED ";
                        fileWriter.append(contExit);
                        fileWriter.append(answer1);
                        fileWriter.append(cont);
                        Log.v("log_tag", "Panel Saved");
                        fileWriter.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //User failed so it will call SMS alarm system function
                    sendSMS();
                    mName.setVisibility(View.INVISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mName.setVisibility(View.INVISIBLE);
                            mName.getText().clear();
                            String weird = "You may have COVID-19. Please leave.";
                            text.setText(weird);
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTTS.speak("You may have COVID-19. Please leave.", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }, 0);
                            mResult.setVisibility(View.VISIBLE);
                            mResult.setText("FAILED");
                            mResult.setTextColor(Color.parseColor("#e62400"));
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mYes.setVisibility(View.INVISIBLE);
                                    mNo.setVisibility(View.INVISIBLE);
                                    mResult.setVisibility(View.INVISIBLE);
                                    text.setText("Please select if you are entering or exiting. \r\nYou must answer all questions with yes or no.");
                                    mEnter.setVisibility(View.VISIBLE);
                                    mExit.setVisibility(View.VISIBLE);
                                    openActivity2();
                                }
                            }, 5000);

                        }
                    }, 1000);
                }
            }
        });

        //If the user ever clicked NO to enter a question, it will enter this part of the code
        mNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYes.setVisibility(View.INVISIBLE);
                mNo.setVisibility(View.INVISIBLE);
                //If the user is exiting, and says NO to the first question, it will enter the below if statement
                if (exitTemp == 1) {
                    String word3 = "Screener, please take the user's temperature. Click the temperature button after completing the temperature scanning.";
                    text.setText(word3);
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Screener, please take the user's temperature. Click the temperature button after completing the temperature scanning.", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }, 0);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    mTemperature.postDelayed(new Runnable() {
                        public void run() {
                            mTemperature.setVisibility(View.VISIBLE);
                        }
                    }, 0);
                    /*
                        It will enter the same temperature code that I have been using this whole time
                        It asks the user for their temperature
                        If the user's temperature is normal, it progresses through the screening, and if it is not, it will ask for oral temperature
                        If the oral temperature is normal, it progresses through the screening, and if it is not, it will fail the user
                     */
                    mTemperature.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mName.setVisibility(View.INVISIBLE);
                            mTemp.setVisibility(View.VISIBLE);
                            mTemp.setHint("Enter Temperature");
                            String word = "Screener, notify the user what their temperature was. \r\nUser, please type your temperature and click done on the keyboard when finished.";
                            text.setText(word);
                            mTTS.speak(word, TextToSpeech.QUEUE_FLUSH, null);
                            mTemperature.setVisibility(View.INVISIBLE);
                            mTemp.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                                    if (actionId == EditorInfo.IME_ACTION_DONE) {

                                        float number = Float.parseFloat(mTemp.getText().toString());
                                        if (number > 99.6 && number < 110) {
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mTemp.setVisibility(View.INVISIBLE);
                                                    mTemp.getText().clear();
                                                    mName.setVisibility(View.INVISIBLE);
                                                    String weird = "Your temperature is too high. Please ask the supervisor or a nurse to get your oral temperature.";
                                                    text.setText(weird);
                                                    Handler handler2 = new Handler();
                                                    handler2.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            mTTS.speak("Your temperature is too high. Please ask the supervisor or a nurse to get your oral temperature.", TextToSpeech.QUEUE_FLUSH, null);
                                                        }
                                                    }, 0);


                                                }
                                            }, 1000);
                                            Handler handler2 = new Handler();
                                            handler2.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mTemp.setVisibility(View.VISIBLE);
                                                    mTemp.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                                                        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                                                            if (actionId == EditorInfo.IME_ACTION_DONE) {

                                                                float number = Float.parseFloat(mTemp.getText().toString());
                                                                if (number > 99.6 && number < 110) {
                                                                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileDate +"Fail.txt");
                                                                    try {
                                                                        String temp = "[Temp:" + mTemp.getText().toString() + "] ";
                                                                        String answer1 = " 1-NO ";
                                                                        getNumber = "";
                                                                        getAddress = "";
                                                                        String contExit = "\r\n" + "["+shortDate + " "+ date+"] " + category + " NAME:" + mName.getText().toString() + com.example.autoScreening_App.MainActivity2.getNumber + getAddress + " EXITING:";
                                                                        FileWriter fileWriter = new FileWriter(file, true);
                                                                        String cont = "FAILED ";
                                                                        fileWriter.append(contExit);
                                                                        fileWriter.append(answer1);
                                                                        fileWriter.append(temp);
                                                                        fileWriter.append(cont);
                                                                        fileWriter.close();
                                                                        Log.i("name", cont);
                                                                    } catch (FileNotFoundException e) {
                                                                        e.printStackTrace();
                                                                    } catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    sendSMS();
                                                                    mTemp.getText().clear();
                                                                    mTemp.setVisibility(View.INVISIBLE);
                                                                    mName.setVisibility(View.INVISIBLE);
                                                                    mName.getText().clear();
                                                                    Handler handler = new Handler();
                                                                    handler.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            mName.setVisibility(View.INVISIBLE);
                                                                            mName.getText().clear();
                                                                            String weird = "You may have COVID-19. Please leave.";
                                                                            text.setText(weird);
                                                                            Handler handler2 = new Handler();
                                                                            handler2.postDelayed(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    mTTS.speak("You may have COVID-19. Please leave.", TextToSpeech.QUEUE_FLUSH, null);
                                                                                }
                                                                            }, 0);
                                                                            mResult.setVisibility(View.VISIBLE);
                                                                            mResult.setText("FAILED");
                                                                            mResult.setTextColor(Color.parseColor("#1800f0"));
                                                                            Handler handler = new Handler();
                                                                            handler.postDelayed(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    mTemp.getText().clear();
                                                                                    mTemp.setVisibility(View.INVISIBLE);
                                                                                    mResult.setVisibility(View.INVISIBLE);
                                                                                    text.setText("Please select if you are entering or exiting. \r\nYou must answer all questions with yes or no.");
                                                                                    mEnter.setVisibility(View.VISIBLE);
                                                                                    mExit.setVisibility(View.VISIBLE);
                                                                                    answerYes= 0;
                                                                                    openActivity2();
                                                                                }
                                                                            }, 5000);

                                                                        }
                                                                    }, 1000);

                                                                } else if (number <= 99.6 && number > 85) {
//
                                                                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileDate + "Pass.txt");
                                                                    try {
                                                                        String temp = "[Temp:" + mTemp.getText().toString() + "] ";
                                                                        String answer1 = " 1-NO ";
                                                                        getNumber = "";
                                                                        getAddress = "";
                                                                        String contExit = "\r\n" + "["+shortDate + " "+ date+"] " + category + " NAME:" + mName.getText().toString() + com.example.autoScreening_App.MainActivity2.getNumber + getAddress+ " EXITING:";
                                                                        FileWriter fileWriter = new FileWriter(file, true);
                                                                        String cont = "PASSED ";
                                                                        fileWriter.append(contExit);
                                                                        fileWriter.append(answer1);
                                                                        fileWriter.append(temp);
                                                                        fileWriter.append(cont);
                                                                        fileWriter.close();
                                                                        Log.i("name", cont);
                                                                    } catch (FileNotFoundException e) {
                                                                        e.printStackTrace();
                                                                    } catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                    Handler handler = new Handler();
                                                                    handler.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            mTemp.getText().clear();
                                                                            mTemp.setVisibility(View.INVISIBLE);
                                                                            mName.setVisibility(View.INVISIBLE);
                                                                            mName.getText().clear();
                                                                            String weird = "You may exit! Have a nice day!";
                                                                            text.setText(weird);
                                                                            Handler handler2 = new Handler();
                                                                            handler2.postDelayed(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    mTTS.speak("You may exit! Have a nice day!", TextToSpeech.QUEUE_FLUSH, null);
                                                                                }
                                                                            }, 0);
                                                                            mResult.setVisibility(View.VISIBLE);
                                                                            mResult.setText("PASSED");
                                                                            mResult.setTextColor(Color.parseColor("#1800f0"));
                                                                            Handler handler = new Handler();
                                                                            handler.postDelayed(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    mResult.setVisibility(View.INVISIBLE);
                                                                                    text.setText("Please select if you are entering or exiting. \r\nYou must answer all questions with yes or no.");
                                                                                    mEnter.setVisibility(View.VISIBLE);
                                                                                    mExit.setVisibility(View.VISIBLE);
                                                                                    openActivity2();
                                                                                }
                                                                            }, 5000);

                                                                        }
                                                                    }, 1000);


                                                                }

                                                            }
                                                            return false;
                                                        }
                                                    });

                                                }
                                            }, 1000);
                                      } else if (number <= 99.6 && number > 85) {
                                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileDate + "Pass.txt");
                                            try {
                                                String temp = "[Temp:" + mTemp.getText().toString() + "] ";
                                                String answer1 = " 1-NO ";
                                                getNumber = "";
                                                getAddress = "";
                                                String contExit = "\r\n" + "["+shortDate + " "+ date+"] " + category + " NAME:" + mName.getText().toString() + com.example.autoScreening_App.MainActivity2.getNumber + getAddress +" EXITING:";
                                                FileWriter fileWriter = new FileWriter(file, true);
                                                String cont = "PASSED ";
                                                fileWriter.append(contExit);
                                                fileWriter.append(answer1);
                                                fileWriter.append(temp);
                                                fileWriter.append(cont);
                                                fileWriter.close();
                                                Log.i("name", cont);
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mTemp.getText().clear();
                                                    mTemp.setVisibility(View.INVISIBLE);
                                                    mName.setVisibility(View.INVISIBLE);
                                                    mName.getText().clear();
                                                    String weird = "You may exit! Have a nice day!";
                                                    text.setText(weird);
                                                    Handler handler2 = new Handler();
                                                    handler2.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            mTTS.speak("You may exit! Have a nice day!", TextToSpeech.QUEUE_FLUSH, null);
                                                        }
                                                    }, 0);
                                                    mResult.setVisibility(View.VISIBLE);
                                                    mResult.setText("PASSED");
                                                    mResult.setTextColor(Color.parseColor("#1800f0"));
                                                    Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            mResult.setVisibility(View.INVISIBLE);
                                                            text.setText("Please select if you are entering or exiting. \r\nYou must answer all questions with yes or no.");
                                                            mEnter.setVisibility(View.VISIBLE);
                                                            mExit.setVisibility(View.VISIBLE);
                                                            openActivity2();
                                                        }
                                                    }, 5000);

                                                }
                                            }, 1000);
                                        }

                                    }
                                    return false;
                                }
                            });

                        }

                    });
                }

                //If the user was a visitor, and answered the first question NO, it will enter this if statement
                if(otherFamilySelection == 1) {
                    mEnter.setVisibility(View.INVISIBLE);
                    mExit.setVisibility(View.INVISIBLE);

                    String word5 = "Do you visit the nursing home on a weekly basis?";
                    text.setText(word5);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Do you visit the nursing home on a weekly basis?", TextToSpeech.QUEUE_FLUSH, null);
                            mYes.setEnabled(true);
                            mNo.setEnabled(true);
                            mYes.setVisibility(View.VISIBLE);
                            mNo.setVisibility(View.VISIBLE);
                            mYes.setText("YES");
                            mNo.setText("NO");
                            visiting = 1;
                            otherFamilySelection = 0;
                        }
                    }, 50);

                }
                //Same as before, app asks the user for their phone number and address
                if (visiting == 1) {
                    visitorInformation = 0;
                    mTemp.setVisibility(View.VISIBLE);
                    mYes.setVisibility(View.INVISIBLE);
                    mNo.setVisibility(View.INVISIBLE);
                    mEnter.setVisibility(View.INVISIBLE);
                    mName.setVisibility(View.VISIBLE);
                    mExit.setVisibility(View.INVISIBLE);
                    screener +=1;
                    mName.setHint("Enter Address: ");
                    mTemp.setHint("Enter Phone Number: ");
                    String word = "Please enter your address and phone number. It will ask for your name later.";
                    text.setText(word);
                    mTTS.speak("Please enter your address and phone number. It will ask for your name later.", TextToSpeech.QUEUE_FLUSH, null);
                    mName.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                getAddress = " ADDRESS:" + mName.getText().toString();
                                category = "VISITOR ||";
                            }
                            return false;
                        }
                    });

                    mTemp.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                getAddress = " ADDRESS:" + mName.getText().toString();
                                getNumber = " NUMBER:" + mTemp.getText().toString() + " ";
                                category = "VISITOR ||";

                                mName.getText().clear();
                                mTemp.getText().clear();
                                mName.setVisibility(View.INVISIBLE);
                                mTemp.setVisibility(View.INVISIBLE);

                                Handler handler = new Handler();
                                handler. postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                                        answerYes = 1;
                                        answerNo = 1;
                                        mName.setVisibility(View.VISIBLE);
                                        mName.setHint("Enter Name");
                                        mEnter.setVisibility(View.INVISIBLE);
                                        mExit.setVisibility(View.INVISIBLE);
                                        speak();
                                        btn_get_sign.setVisibility(View.VISIBLE);
                                        visitorEnterSign2 = 1;
                                        mTemperature.setVisibility(View.INVISIBLE);
                                        mResult.setVisibility(View.INVISIBLE);
                                    }
                                },1000);

                            }
                            return false;
                        }
                    });
                }
                //Same as the pattern for the YES button, I use a combination of IF-ELSE statements to direct the questionnaire flow
                //The questionnaire flow for when the user answers NO to a screening question is below
                if (answerNo == 1) {
                    visiting = 0;
                    mYes.setVisibility(View.VISIBLE);
                    mNo.setVisibility(View.VISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    String word3 = "Do you have shortness of breath, chest tightness, or body aches?";
                    text.setText(word3);
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Do you have shortness of breath, chest tightness, or body aches?", TextToSpeech.QUEUE_FLUSH, null);
                            answerNo += 1;
                            answerYes = 3;
                            visitorInformation = 0;
                            visiting = 0;
                        }
                    }, 0);

                }
                //If user answers NO to previous screening question, it will enter the below Else If statement
                else if (answerNo == 2) {
                    mYes.setVisibility(View.VISIBLE);
                    mYes.setEnabled(true);
                    mNo.setEnabled(true);
                    mNo.setVisibility(View.VISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    String word3 = "Do you have diarrhea, nausea, vomiting, or loss of taste and smell?";
                    text.setText(word3);
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Do you have diarrhea, nausea, vomiting, or loss of taste and smell?", TextToSpeech.QUEUE_FLUSH, null);
                            answerNo += 1;
                            answerYes = 5;
                        }
                    }, 0);
                }
                //If user answers NO to previous screening question, it will enter the below Else If statement
                //As previously before, it asks for the temperature and follows the same logic
                else if (answerNo == 3) {
                    mYes.setVisibility(View.INVISIBLE);
                    mNo.setVisibility(View.INVISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    String word0 = "Screener, please take the user's temperature. Click the temperature button after completing the temperature scanning.";
                    text.setText(word0);
                    Handler handler8 = new Handler();
                    handler8.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Screener, please take the user's temperature. Click the temperature button after completing the temperature scanning.", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }, 0);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    mTemperature.postDelayed(new Runnable() {
                        public void run() {
                            mTemperature.setVisibility(View.VISIBLE);
                        }
                    }, 0);
                    mTemperature.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mName.setVisibility(View.INVISIBLE);
                            mTemp.setVisibility(View.VISIBLE);
                            mTemp.setHint("Enter Temperature");
                            String word = "Screener, notify the user what their temperature was. \r\nUser, please type your temperature and click done on the keyboard when finished.";
                            text.setText(word);
                            mTTS.speak(word, TextToSpeech.QUEUE_FLUSH, null);
                            mTemperature.setVisibility(View.INVISIBLE);
                            mTemp.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                                        float number = Float.parseFloat(mTemp.getText().toString());
                                        if (number > 99.6 && number < 110) {
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mTemp.setVisibility(View.INVISIBLE);
                                                    mTemp.getText().clear();
                                                    mName.setVisibility(View.INVISIBLE);
                                                    String weird = "Your temperature is too high. Please ask the supervisor or a nurse to get your oral temperature.";
                                                    text.setText(weird);
                                                    Handler handler2 = new Handler();
                                                    handler2.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            mTTS.speak("Your temperature is too high. Please ask the supervisor or a nurse to get your oral temperature.", TextToSpeech.QUEUE_FLUSH, null);
                                                        }
                                                    }, 0);
                                                }
                                            }, 1000);
                                            Handler handler2 = new Handler();
                                            handler2.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mTemp.setVisibility(View.VISIBLE);
                                                    mTemp.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                                                        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                                                            if (actionId == EditorInfo.IME_ACTION_DONE) {

                                                                float number = Float.parseFloat(mTemp.getText().toString());
                                                                if (number > 99.6 && number < 110) {
                                                                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileDate +"Fail.txt");
                                                                    try {
                                                                        String temp = "[Temp:" + mTemp.getText().toString() + "] ";
                                                                        String answer1 = " 1-NO ";
                                                                        String answer2 = " 2-NO";
                                                                        String answer3 = " 3-NO";
                                                                        String contExit = "\r\n" + "["+shortDate + " "+ date+"] " + category + " NAME:"  +mName.getText().toString() + com.example.autoScreening_App.MainActivity2.getNumber + getAddress + " EXITING:";
                                                                        FileWriter fileWriter = new FileWriter(file, true);
                                                                        String cont = "FAILED ";
                                                                        fileWriter.append(contExit);
                                                                        fileWriter.append(answer1);
                                                                        fileWriter.append(answer2);
                                                                        fileWriter.append(answer3);
                                                                        fileWriter.append(temp);
                                                                        fileWriter.append(cont);
                                                                        fileWriter.close();
                                                                        Log.i("name", cont);
                                                                    } catch (FileNotFoundException e) {
                                                                        e.printStackTrace();
                                                                    } catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    sendSMS();
                                                                    mName.setVisibility(View.INVISIBLE);
                                                                    mName.getText().clear();
                                                                    mTemp.getText().clear();
                                                                    mTemp.setVisibility(View.INVISIBLE);
                                                                    Handler handler = new Handler();
                                                                    handler.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            mName.setVisibility(View.INVISIBLE);
                                                                            mName.getText().clear();
                                                                            String weird = "You may have COVID-19. Please leave.";
                                                                            text.setText(weird);
                                                                            Handler handler2 = new Handler();
                                                                            handler2.postDelayed(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    mTTS.speak("You may have COVID-19. Please leave.", TextToSpeech.QUEUE_FLUSH, null);
                                                                                }
                                                                            }, 0);
                                                                            mResult.setVisibility(View.VISIBLE);
                                                                            mResult.setText("FAILED");
                                                                            mResult.setTextColor(Color.parseColor("#1800f0"));
                                                                            Handler handler = new Handler();
                                                                            handler.postDelayed(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    mResult.setVisibility(View.INVISIBLE);
                                                                                    text.setText("Please select if you are entering or exiting. \r\nYou must answer all questions with yes or no.");
                                                                                    mEnter.setVisibility(View.VISIBLE);
                                                                                    mExit.setVisibility(View.VISIBLE);
                                                                                    answerYes= 0;
                                                                                    openActivity2();
                                                                                }
                                                                            }, 5000);

                                                                        }
                                                                    }, 1000);
                                                                } else if (number <= 99.6 && number > 85) {
                                                                    Handler handler = new Handler();
                                                                    handler.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            mTemp.setVisibility(View.INVISIBLE);
                                                                            mName.setVisibility(View.INVISIBLE);
                                                                            String weird = "Have you had contact for more than 10 minutes with someone who is suspected or confirmed COVID-19 positive or is awaiting test results?";
                                                                            text.setText(weird);
                                                                            Handler handler2 = new Handler();
                                                                            handler2.postDelayed(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    mTTS.speak("Have you had contact for more than 10 minutes with someone who is suspected or confirmed COVID-19 positive or is awaiting test results?", TextToSpeech.QUEUE_FLUSH, null);
                                                                                    answerNo= 7;
                                                                                    answerYes= 7;
                                                                                    mYes.setVisibility(View.VISIBLE);
                                                                                    mNo.setVisibility(View.VISIBLE);
                                                                                }
                                                                            }, 0);
                                                                        }
                                                                    }, 1000);
                                                                }else {
                                                                    mYes.setVisibility(View.VISIBLE);
                                                                    mNo.setVisibility(View.VISIBLE);
                                                                    mTemp.setVisibility(View.INVISIBLE);
                                                                    String weird = "You may have entered your temperature incorrectly. ";
                                                                    text.setText(weird);
                                                                    Handler handler2 = new Handler();
                                                                    handler2.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            mTTS.speak("You may have entered the temperature incorrectly.", TextToSpeech.QUEUE_FLUSH, null);
                                                                        }
                                                                    }, 0);
                                                                    Handler handler3 = new Handler();
                                                                    handler3.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            openActivity2();

                                                                        }
                                                                    }, 4000);
                                                                }
                                                            }
                                                            return false;
                                                        }
                                                    });

                                                }
                                            }, 1000);


                                        } else if (number <= 99.6 && number > 85) {
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mTemp.setVisibility(View.INVISIBLE);
                                                    mName.setVisibility(View.INVISIBLE);
                                                    String weird = "Have you had contact for more than 10 minutes with someone who is suspected or confirmed COVID-19 positive or is awaiting test results?";
                                                    text.setText(weird);
                                                    Handler handler2 = new Handler();
                                                    handler2.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            mTTS.speak("Have you had contact for more than 10 minutes with someone who is suspected or confirmed COVID-19 positive or is awaiting test results?", TextToSpeech.QUEUE_FLUSH, null);
                                                            answerNo = 7;
                                                            answerYes = 7;
                                                            mYes.setVisibility(View.VISIBLE);
                                                            mNo.setVisibility(View.VISIBLE);
                                                        }
                                                    }, 0);

                                                }
                                            }, 1000);
                                        } else {
                                            mYes.setVisibility(View.VISIBLE);
                                            mNo.setVisibility(View.VISIBLE);
                                            mTemp.setVisibility(View.INVISIBLE);
                                            String weird = "You may have entered your temperature incorrectly.";
                                            text.setText(weird);
                                            Handler handler2 = new Handler();
                                            handler2.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mTTS.speak("You may have entered the temperature incorrectly.", TextToSpeech.QUEUE_FLUSH, null);
                                                }
                                            }, 0);
                                            Handler handler3 = new Handler();
                                            handler3.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    openActivity2();
                                                }
                                            }, 4000);
                                        }

                                    }
                                    return false;
                                }
                            });


                        }

                    });

                }
                //If user answers NO to previous screening question, it will enter the below Else If statement
                else if (answerNo == 4) {
                    mYes.setVisibility(View.INVISIBLE);
                    mNo.setVisibility(View.INVISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    //Creates the fail file in the local device storage
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileDate +"Fail.txt");
                    //Append the appropriate data to the file
                    try {
                        String answer1 = " 1-YES ";
                        String contEnter = "\r\n" + "["+shortDate + " "+ date+"] " + category + " NAME:"  + mName.getText().toString() + com.example.autoScreening_App.MainActivity2.getNumber + getAddress + " ENTERING:";
                        FileWriter fileWriter = new FileWriter(file, true);
                        String cont = "FAILED ";
                        fileWriter.append(contEnter);
                        fileWriter.append(answer1);
                        fileWriter.append(cont);
                        fileWriter.close();
                        Log.i("name", cont);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //User failed so SMS alarm is sent
                    sendSMS();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mName.setVisibility(View.INVISIBLE);
                            mName.getText().clear();
                            String weird = "You may have COVID-19. Please leave.";
                            text.setText(weird);
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTTS.speak("You may have COVID-19. Please leave.", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }, 0);

                            mResult.setVisibility(View.VISIBLE);
                            mResult.setText("FAILED");
                            mResult.setTextColor(Color.parseColor("#e62400"));
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mYes.setVisibility(View.INVISIBLE);
                                    mNo.setVisibility(View.INVISIBLE);
                                    mResult.setVisibility(View.INVISIBLE);
                                    text.setText("Please select if you are entering or exiting. \r\nYou must answer all questions with yes or no.");
                                    mEnter.setVisibility(View.VISIBLE);
                                    mExit.setVisibility(View.VISIBLE);
                                    openActivity2();

                                }
                            }, 5000);

                        }
                    }, 1000);

                }
                //If user answers NO to previous screening question, it will enter the below Else If statement
                else if (answerNo == 5) {
                    mYes.setVisibility(View.INVISIBLE);
                    mNo.setVisibility(View.INVISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);

                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileDate +"Fail.txt");
                    try {
                        String answer1 = " 1-NO ";
                        String answer2 = " 2-YES ";
                        String contEnter = "\r\n" + "["+shortDate + " "+ date+"] " + category + " NAME:"  + mName.getText().toString() + com.example.autoScreening_App.MainActivity2.getNumber + getAddress + " ENTERING:";
                        FileWriter fileWriter = new FileWriter(file, true);
                        String cont = "FAILED ";
                        fileWriter.append(contEnter);
                        fileWriter.append(answer1);
                        fileWriter.append(answer2);
                        fileWriter.append(cont);
                        fileWriter.close();
                        Log.i("name", cont);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //User failed so SMS alarm is sent
                    sendSMS();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mName.setVisibility(View.INVISIBLE);
                            mName.getText().clear();
                            String weird = "You may have COVID-19. Please leave.";
                            text.setText(weird);
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTTS.speak("You may have COVID-19. Please leave.", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }, 0);

                            mResult.setVisibility(View.VISIBLE);
                            mResult.setText("FAILED");
                            mResult.setTextColor(Color.parseColor("#e62400"));
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mResult.setVisibility(View.INVISIBLE);
                                    text.setText("Please select if you are entering or exiting. \r\nYou must answer all questions with yes or no.");
                                    mEnter.setVisibility(View.VISIBLE);
                                    mExit.setVisibility(View.VISIBLE);
                                    openActivity2();
                                }
                            }, 5000);

                        }
                    }, 1000);

                }
                //If user answers NO to previous screening question, it will enter the below Else If statement
                else if (answerNo == 6) {
                    mYes.setVisibility(View.INVISIBLE);
                    mNo.setVisibility(View.INVISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);

                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileDate +"Fail.txt");
                    try {
                        String answer1 = " 1-NO ";
                        String answer2 = " 2-NO ";
                        String answer3 = " 3-YES ";
                        String contExit = "\r\n" + "["+shortDate + " "+ date+"] " + category + " NAME:" + mName.getText().toString() + com.example.autoScreening_App.MainActivity2.getNumber + getAddress + " ENTERING:";
                        FileWriter fileWriter = new FileWriter(file, true);
                        String cont = "FAILED ";
                        fileWriter.append(contExit);
                        fileWriter.append(answer1);
                        fileWriter.append(answer2);
                        fileWriter.append(answer3);
                        fileWriter.append(cont);
                        fileWriter.close();
                        Log.i("name", cont);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sendSMS();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mName.setVisibility(View.INVISIBLE);
                            mName.getText().clear();
                            String weird = "You may have COVID-19. Please leave.";
                            text.setText(weird);
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTTS.speak("You may have COVID-19. Please leave.", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }, 0);

                            mResult.setVisibility(View.VISIBLE);
                            mResult.setText("FAILED");
                            mResult.setTextColor(Color.parseColor("#e62400"));
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mResult.setVisibility(View.INVISIBLE);
                                    text.setText("Please select if you are entering or exiting. \r\nYou must answer all questions with yes or no.");
                                    mEnter.setVisibility(View.VISIBLE);
                                    mExit.setVisibility(View.VISIBLE);
                                    openActivity2();
                                }
                            }, 5000);

                        }
                    }, 1000);


                }
                //If user answers NO to previous screening question, it will enter the below Else If statement
                else if (answerNo == 7) {
                    mYes.setVisibility(View.VISIBLE);
                    mNo.setVisibility(View.VISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    String word3 = "Have you worked in facilities or offices with recognized COVID-19 cases?";
                    text.setText(word3);
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Have you worked in facilities or offices with recognized COVID-19 cases?", TextToSpeech.QUEUE_FLUSH, null);
                            answerNo = 9;
                            answerYes = 9;
                        }
                    }, 0);

                }
                //If user answers NO to previous screening question, it will enter the below Else If statement
                else if (answerNo == 8) {
                    mYes.setVisibility(View.INVISIBLE);
                    mNo.setVisibility(View.INVISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);

                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileDate +"Fail.txt");
                    try {
                        String temp = " [Temp:" + mTemp.getText().toString() + "] ";
                        String answer1 = " 1-NO ";
                        String answer2 = " 2-NO ";
                        String answer3 = " 3-NO ";
                        String answer4 = " 4-YES ";
                        String answer5 = " 5-NO (Not all Criteria Met) ";
                        String contEnter = "\r\n" + "["+shortDate + " "+ date+"] " + category + " NAME:"  + mName.getText().toString() + com.example.autoScreening_App.MainActivity2.getNumber + getAddress + " ENTERING:";
                        FileWriter fileWriter = new FileWriter(file, true);
                        String cont = "FAILED ";
                        fileWriter.append(contEnter);
                        fileWriter.append(answer1);
                        fileWriter.append(answer2);
                        fileWriter.append(answer3);
                        fileWriter.append(temp);
                        fileWriter.append(answer4);
                        fileWriter.append(answer5);
                        fileWriter.append(cont);
                        fileWriter.close();
                        Log.i("name", cont);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sendSMS();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mName.setVisibility(View.INVISIBLE);
                            mName.getText().clear();
                            String weird = "You may have COVID-19. Please leave and quarantine.";
                            text.setText(weird);
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTTS.speak("You may have COVID-19. Please leave and quarantine.", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }, 0);

                            mResult.setVisibility(View.VISIBLE);
                            mResult.setText("FAILED");
                            mResult.setTextColor(Color.parseColor("#e62400"));
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mResult.setVisibility(View.INVISIBLE);
                                    text.setText("Please select if you are entering or exiting. \r\nYou must answer all questions with yes or no.");
                                    mEnter.setVisibility(View.VISIBLE);
                                    mExit.setVisibility(View.VISIBLE);
                                    openActivity2();
                                }
                            }, 5000);

                        }
                    }, 1000);
//
                }
                //If user answers NO to previous screening question, it will enter the below Else If statement
                else if (answerNo == 9) {
                    mYes.setVisibility(View.INVISIBLE);
                    mNo.setVisibility(View.INVISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);

                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileDate + "Pass.txt");
                    try {
                        String temp = " [Temp:" + mTemp.getText().toString() + "] ";
                        String answer1 = " 1-NO ";
                        String answer2 = " 2-NO ";
                        String answer3 = " 3-NO ";
                        String answer4 = " 4-NO ";
                        String answer5 = " 5-N/A ";
                        String answer6 = " 6-NO ";
                        String contEnter = "\r\n" + "["+shortDate + " "+ date+"] " + category + " NAME:"  + mName.getText().toString() + com.example.autoScreening_App.MainActivity2.getNumber + getAddress + " ENTERING:";
                        FileWriter fileWriter = new FileWriter(file, true);
                        String cont = "PASSED ";
                        fileWriter.append(contEnter);
                        fileWriter.append(answer1);
                        fileWriter.append(answer2);
                        fileWriter.append(answer3);
                        fileWriter.append(temp);
                        fileWriter.append(answer4);
                        fileWriter.append(answer5);
                        fileWriter.append(answer6);
                        fileWriter.append(cont);
                        fileWriter.close();
                        Log.i("name", cont);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mName.setVisibility(View.INVISIBLE);
                            mName.getText().clear();
                            String weird = "You may enter! Have a nice day!";
                            text.setText(weird);
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTTS.speak("You may enter! Have a nice day!", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }, 0);

                            mResult.setVisibility(View.VISIBLE);
                            mResult.setText("PASSED");
                            mResult.setTextColor(Color.parseColor("#1800f0"));
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mResult.setVisibility(View.INVISIBLE);
                                    text.setText("Please select if you are entering or exiting. \r\nYou must answer all questions with yes or no.");
                                    mEnter.setVisibility(View.VISIBLE);
                                    mExit.setVisibility(View.VISIBLE);
                                    openActivity2();
                                }
                            }, 5000);

                        }
                    }, 1000);

                }
                //If user answers NO to previous screening question, it will enter the below Else If statement
                else if (answerNo == 10) {
                    mYes.setVisibility(View.INVISIBLE);
                    mNo.setVisibility(View.INVISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);

                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileDate +"Fail.txt");
                    try {
                        String temp = " [Temp:" + mTemp.getText().toString() + "] ";
                        String answer1 = " 1-NO ";
                        String answer2 = " 2-NO ";
                        String answer3 = " 3-NO ";
                        String answer4 = " 4-NO ";
                        String answer5 = " N/A ";
                        String answer6 = " 6-YES ";
                        String answer7 = " 7-NO ";
                        String contEnter = "\r\n" + "["+shortDate + " "+ date+"] " + category + " NAME:"  + mName.getText().toString() + com.example.autoScreening_App.MainActivity2.getNumber + getAddress + " ENTERING:";
                        FileWriter fileWriter = new FileWriter(file, true);
                        String cont = "FAILED ";
                        fileWriter.append(contEnter);
                        fileWriter.append(answer1);
                        fileWriter.append(answer2);
                        fileWriter.append(answer3);
                        fileWriter.append(temp);
                        fileWriter.append(answer4);
                        fileWriter.append(answer5);
                        fileWriter.append(answer6);
                        fileWriter.append(answer7);
                        fileWriter.append(cont);
                        fileWriter.close();
                        Log.i("name", cont);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sendSMS();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mName.setVisibility(View.INVISIBLE);
                            mName.getText().clear();
                            String weird = "You may have COVID-19. Please leave.";
                            text.setText(weird);
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTTS.speak("You may have COVID-19. Please leave.", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }, 0);

                            mResult.setVisibility(View.VISIBLE);
                            mResult.setText("FAILED");
                            mResult.setTextColor(Color.parseColor("#e62400"));
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mResult.setVisibility(View.INVISIBLE);
                                    text.setText("Please select if you are entering or exiting. \r\nYou must answer all questions with yes or no.");
                                    mEnter.setVisibility(View.VISIBLE);
                                    mExit.setVisibility(View.VISIBLE);
                                    openActivity2();
                                }
                            }, 5000);

                        }
                    }, 1000);

                }
            }
        });

    }
    //This is the major class for my Digital Signature module
    public void dialog_action() {
        //Initialize and Declare the various physical elements that are needed 
        mContent = (LinearLayout) dialog.findViewById(R.id.linearLayout);
        mSignature = new signature(getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) dialog.findViewById(R.id.clear);
        mGetSign = (Button) dialog.findViewById(R.id.getsign);
        mGetSign.setEnabled(false);
        mCancel = (Button) dialog.findViewById(R.id.cancel);
        view = mContent;
        //Clear Button - clears whatever signature that has already been drawn on the signature pad
        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });
        //When the user clicks save, this code is run
        //Inside this part, it calls the save function that is written below
        //The signature panel is dismissed and removed from the screen
        //Finally, based on the situation (visitor or employee?, entering or exiting?), it asks the first screening question when this button is clicked as well
        mGetSign.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String pic_name = new SimpleDateFormat("MM-dd", Locale.getDefault()).format(new Date());
                String StoredPath = DIRECTORY + pic_name + " " + mName.getText().toString() + ".png";
                Log.v("log_tag", "Panel Saved");
                view.setDrawingCacheEnabled(true);
                mSignature.save(view,StoredPath);
                dialog.dismiss();

                if(enterSign == 1){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mName.setVisibility(View.INVISIBLE);
                            String weird = "Do you have a fever, sore throat, cough, or a runny nose?";
                            text.setText(weird);
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTTS.speak("Do you have a fever, sore throat, cough, or a runny nose?", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }, 0);

                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btn_get_sign.setVisibility(View.INVISIBLE);
                                    mYes.setVisibility(View.VISIBLE);
                                    mNo.setVisibility(View.VISIBLE);
                                    mYes.setEnabled(true);
                                    mNo.setEnabled(true);
                                }
                            }, 500);
                        }
                        },1000);
                }else if(exitSign == 1){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mName.setVisibility(View.INVISIBLE);
                            String weird = "Have you developed any symptoms that were referenced upon entry?";
                            text.setText(weird);
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTTS.speak("Have you developed any symptoms that were referenced upon entry?", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }, 0);

                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btn_get_sign.setVisibility(View.INVISIBLE);
                                    mYes.setVisibility(View.VISIBLE);
                                    mNo.setVisibility(View.VISIBLE);
                                    mYes.setEnabled(true);
                                    mNo.setEnabled(true);
                                }
                            }, 500);


                        }
                    }, 1000);
                }else if(visitorEnterSign == 1){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mName.setVisibility(View.INVISIBLE);
                            String weird = "Do you have a fever, sore throat, cough, or a runny nose?";
                            text.setText(weird);
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTTS.speak("Do you have a fever, sore throat, cough, or a runny nose?", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }, 0);

                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btn_get_sign.setVisibility(View.INVISIBLE);
                                    visitorInformation = 0;
                                    mName.setVisibility(View.INVISIBLE);
                                    mYes.setVisibility(View.VISIBLE);
                                    mNo.setVisibility(View.VISIBLE);
                                    mYes.setText("YES");
                                    mNo.setText("NO");
                                    mYes.setEnabled(true);
                                    mNo.setEnabled(true);
                                    otherFamilySelection = 0;
                                }
                            }, 500);
                        }
                    }, 1500);;
                }else if(visitorEnterSign2 == 1){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mName.setVisibility(View.INVISIBLE);
                            String weird = "Do you have a fever, sore throat, cough, or a runny nose?";
                            text.setText(weird);
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTTS.speak("Do you have a fever, sore throat, cough, or a runny nose?", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }, 0);

                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btn_get_sign.setVisibility(View.INVISIBLE);
                                    visitorInformation = 0;
                                    otherFamilySelection = 0;
                                    visiting = 0;
                                    mName.setVisibility(View.INVISIBLE);
                                    mYes.setVisibility(View.VISIBLE);
                                    mNo.setVisibility(View.VISIBLE);
                                    mYes.setEnabled(true);
                                    mNo.setEnabled(true);
                                }
                            }, 500);
                        }
                    }, 1500);

                }else if(visitorEnterSign3 == 1){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mName.setVisibility(View.INVISIBLE);
                            String weird = "Do you have a fever, sore throat, cough, or a runny nose?";
                            text.setText(weird);
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTTS.speak("Do you have a fever, sore throat, cough, or a runny nose?", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }, 0);

                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btn_get_sign.setVisibility(View.INVISIBLE);
                                    visitorInformation = 0;
                                    mName.setVisibility(View.INVISIBLE);
                                    mYes.setVisibility(View.VISIBLE);
                                    mNo.setVisibility(View.VISIBLE);
                                    mYes.setEnabled(true);
                                    mNo.setEnabled(true);
                                }
                            }, 500);


                        }
                    }, 1500);
                }
            }
        });

        //When this button is clicked, the signature pad is dismissed and is not expanded
        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Canceled");
                dialog.dismiss();
                // Calling the same class
                recreate();
            }
        });
        dialog.show();
    }

    //This is the class where the algorithm and base code for Digital Signature module is actually written
    public class signature extends View {
        //Declare and Initialize the various private instance data that will be needed
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        //Signature constructor where I call the View superclass
        //Modify the various properties of the paint object
        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }
        
        //saves signature to a png image
        public void save(View v, String StoredPath) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Toast.makeText(getBaseContext(), "SMS sent",
                    Toast.LENGTH_SHORT).show();


            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }

        }
        //clear's pad when clear button is clicked
        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        //uses MotionEvent and stroke movement to get digital signature
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);
            //Switch and case statements to account for all of the drawing motions
            //This is the algorithm that allows the user to actually draw the pixels on the Signature pad
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX= eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX= event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignore touch event: " + event.toString());
                    return false;
            }
            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));
            lastTouchX = eventX;
            lastTouchY = eventY;
            return true;
        }


        private void debug(String string) {
            Log.v("log_tag", string);
        }
        //make signature pad bigger on screen
        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX< dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }
            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }
        //resets signature pad back to its normal size
        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    //Allows me to transition between app screens
    public void openActivity2() {
        screener+=1;
        Intent intent = new Intent(this, com.example.autoScreening_App.MainActivity2.class);
        startActivity(intent);
    }
    //Function for sending the SMS alarm message
    //This function is called everytime the user fails
    //For this competition purposes, I do not have a phone number entered as the recipient 
        //However, usually the nursing home administrator or supervisor's phone number is entered in the code
    private void sendSMS() {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);
        // when the SMS has been sent
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio is off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));


        // when the SMS has been delivered
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage("Phone Number", null, mName.getText().toString() + " FAILED", sentPI, deliveredPI);
    }
    //a function that I call a few times
    //uses Android TextToSpeech to speak the words
   private void speak() {
        String word = "Please type your name. Next, click sign this form, sign in the white box, and click save.";
        text.setText(word);
        mTTS.speak("Please type your name. Next, click sign this form, sign in the white box, and click save. ", TextToSpeech.QUEUE_FLUSH, null);
    }
    
    //This is essential for turning off the TextToSpeech when it is not called
    //Prevents the app from crashing
    protected void onDestroy() {

        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }
    //App asks for permission to create a file on the device's local storage 
    //Also asks for SMS permission 
    //This function is used to do this
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                    finish();

                }
        }
    }
}
