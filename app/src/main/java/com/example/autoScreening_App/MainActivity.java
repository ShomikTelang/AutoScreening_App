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
import java.util.Timer;

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
    private Button backButton;
    private TextView text;
    private TextView mResult;
    private Button mTemperature;
    private EditText mName;
    private EditText mTemp;
    private Timer timer = new Timer();
    private final long DELAY = 1000;
    int counterEnter = 0;
    int counterExit = 0;
    int enterSign = 0;
    int exitSign = 0;
    int visitorEnterSign = 0;
    int visitorEnterSign2 = 0;
    int visitorEnterSign3  = 0;
    int visitorExitSign = 0;
    int counterChronic = 0;
    int person = 0;
    int personleave = 0;
    int backClick = 0;
    int name = 0;
    int visiting = 0;
    int otherFamilyStart = 0;
    int personCondition = 0;
    boolean familyVisitor = false;
    boolean otherVisitor = false;
    @SuppressLint("SimpleDateFormat")
    public static DateFormat dateFormat = new SimpleDateFormat("MM-dd");
    public static Date shomik = new Date();
    public static String fileDate = dateFormat.format(shomik);

    Toolbar toolbar;
    Button btn_get_sign, mClear, mGetSign, mCancel;

    File file;
    Dialog dialog;
    LinearLayout mContent;
    View view;
    signature mSignature;
    Bitmap bitmap,scaledBitmap;
    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/Signatures - Month " + signatureMonth + "/";

    public static DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    public static DateFormat df2 = new SimpleDateFormat("MM");
    public static Date travis = new Date();
    public static String signatureMonth = df2.format(travis);
    public static Date drake = new Date();
    public static String shortDate = df.format(drake);

    int x = 0;
    int y = 0;
    int key = 0;
    int z = 0;
    int b = 0;
    int backTemp = 0;
    int testing = 0;
    String date = java.text.DateFormat.getTimeInstance().format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Button to open signature panel
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
        backButton = findViewById(R.id.back2);
        backButton.setEnabled(true);
        backButton.setVisibility(View.INVISIBLE);

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.US);
                    Log.e("TTS", "work man");
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
                    otherFamilyStart = 1;
                }
                else {
                    mName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    x = 1;
                    z = 1;
                    mName.setVisibility(View.VISIBLE);
                    mName.setHint("Enter Name");
                    mEnter.setVisibility(View.INVISIBLE);
                    mExit.setVisibility(View.INVISIBLE);
                    speak();
                    counterEnter = 0;
                    name = 0;
                    String date = java.text.DateFormat.getDateTimeInstance().format(new Date());
                    btn_get_sign.setVisibility(View.VISIBLE);
                    mYes.setEnabled(true);
                    mNo.setEnabled(true);
                    enterSign = 1;
                    mTemperature.setVisibility(View.INVISIBLE);
                    mResult.setVisibility(View.INVISIBLE);
                }


            }
        });
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTemp.getText().clear();
                y = 1;
                b = 1;
                name = 2;
                getNumber = "";
                getAddress = "";
                if(MainActivity2.visitingState == true){
                    category = "VISITOR ||";
                }else{
                    category = "EMPLOYEE ||";
                }


                mName.setVisibility(View.VISIBLE);
                key += 1;
                mName.setHint("Enter Name");
                mEnter.setVisibility(View.INVISIBLE);
                mExit.setVisibility(View.INVISIBLE);
                personleave = 1;
                person = 0;
                speak();
                counterExit = 0;
                name = 0;
                btn_get_sign.setVisibility(View.VISIBLE);
                exitSign = 1;
                Log.e("value", "hello");

                mTemperature.setVisibility(View.INVISIBLE);
                mResult.setVisibility(View.INVISIBLE);


            }
        });


        mYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kyrie = 0;
                if(otherFamilyStart ==1) {
                    visiting = 0;
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
                                        x = 1;
                                        z = 1;
                                        mName.setVisibility(View.VISIBLE);
                                        mName.setHint("Enter Name");
                                        mEnter.setVisibility(View.INVISIBLE);
                                        mExit.setVisibility(View.INVISIBLE);
                                        speak();
                                        name = 0;
                                        String date = java.text.DateFormat.getDateTimeInstance().format(new Date());
                                        btn_get_sign.setVisibility(View.VISIBLE);
                                        visitorEnterSign = 1;
                                        Log.e("value", "hello");

                                        mTemperature.setVisibility(View.INVISIBLE);
                                        mResult.setVisibility(View.INVISIBLE);
                                    }
                                },1000);


                            }
                            return false;
                        }
                    });

                }
                if(visiting ==1) {
                    otherFamilyStart = 0;
                    mYes.setVisibility(View.VISIBLE);
                    mNo.setVisibility(View.VISIBLE);
                    String word5= "Have you provided a negative COVID-19 test twice in the last week?";
                    text.setText(word5);
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Have you provided a negative COVID-19 test twice in the last week?", TextToSpeech.QUEUE_FLUSH, null);
                            testing =2;
                            visiting = 0;
                            Log.e("help","zion willilamson");

                        }
                    }, 0);
                }
                else if (testing ==2) {
                    Log.e("help","enterdawg");
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
                                        x = 1;
                                        z = 1;
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
                                        Log.e("value", "hello");

                                        mTemperature.setVisibility(View.INVISIBLE);
                                        mResult.setVisibility(View.INVISIBLE);
                                    }
                                },1000);


                            }
                            return false;
                        }
                    });
                }
                if (x == 1) {
                    visiting = 0;
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    mYes.setVisibility(View.VISIBLE);
                    mNo.setVisibility(View.VISIBLE);
                    Log.e("helper", "helper");
                    String word3 = "Do you have any chronic conditions that may lead to these symptoms?";
                    text.setText(word3);
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Do you have any chronic conditions that may lead to these symptoms?", TextToSpeech.QUEUE_FLUSH, null);
                            x += 1;
                            z = 4;
                        }
                    }, 0);
                }
                else if (x == 2) {
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    mYes.setVisibility(View.VISIBLE);
                    mNo.setVisibility(View.VISIBLE);
                    Log.i("help", "help");
                    String word = "Do you have shortness of breath, chest tightness, or body aches?";
                    text.setText(word);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Do you have shortness of breath, chest tightness, or body aches?", TextToSpeech.QUEUE_FLUSH, null);
                            x += 1;
                            z = 2;

                        }
                    }, 0);

                } else if (x == 3) {
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    mYes.setVisibility(View.VISIBLE);
                    mNo.setVisibility(View.VISIBLE);
                    Log.e("help", "help");
                    String word6 = "Do you have any chronic conditions that may lead to these symptoms?";
                    text.setText(word6);
                    Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Do you have any chronic conditions that may lead to these symptoms?", TextToSpeech.QUEUE_FLUSH, null);
                            x += 1;
                            z = 5;
                        }
                    }, 0);
                } else if (x == 4) {
                    backTemp = 0;
                    mYes.setVisibility(View.VISIBLE);
                    mNo.setVisibility(View.VISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    Log.i("value", "" + counterEnter);
                    String word4 = "Do you have diarrhea, nausea, vomiting, or loss of taste and smell?";
                    text.setText(word4);
                    Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Do you have diarrhea, nausea, vomiting, or loss of taste and smell?", TextToSpeech.QUEUE_FLUSH, null);
                            x += 1;
                            z = 3;
                        }
                    }, 0);

                } else if (x == 5) {
                    mYes.setVisibility(View.VISIBLE);
                    mNo.setVisibility(View.VISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);
                    Log.e("hello", "why");
                    String word6 = "Do you have any chronic conditions that may lead to these symptoms?";
                    text.setText(word6);
                    Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Do you have any chronic conditions that may lead to these symptoms?", TextToSpeech.QUEUE_FLUSH, null);
                            x += 1;
                            z = 6;
                        }
                    }, 0);
                } else if (x == 6) {
                    backTemp = 1;
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

                                                    Log.i("value", "how are you");


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
                                                                                    x = 0;
                                                                                    openActivity2();
                                                                                }
                                                                            }, 5000);

                                                                        }
                                                                    }, 1000);


//

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
                                                                                    x += 1;
                                                                                    z = 7;
                                                                                    mYes.setVisibility(View.VISIBLE);
                                                                                    mNo.setVisibility(View.VISIBLE);
                                                                                }
                                                                            }, 0);


                                                                        }
                                                                    }, 1000);

                                                                }
                                                                  else {
//                                                                        mYes.setVisibility(View.VISIBLE);
//                                                                        mNo.setVisibility(View.VISIBLE);
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
                                                            x += 1;
                                                            z = 7;
                                                            mYes.setVisibility(View.VISIBLE);
                                                            mNo.setVisibility(View.VISIBLE);
                                                        }
                                                    }, 0);

                                                    Log.i("value", "how are you");


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

                } else if (x == 7) {
                    david =2;
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
                         //   mName.getText().clear();
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
                                    x += 1;
                                    z = 8;
                                    mYes.setVisibility(View.VISIBLE);
                                    mNo.setVisibility(View.VISIBLE);
                                }
                            }, 0);



                        }
                    }, 0);
                }


                else if (x == 8) {
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
                } else if (x == 9) {
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
                            x += 1;
                            z = 10;
                        }
                    }, 0);
                }else if(x == 10) {
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
                if (y == 1) {
                    mYes.setVisibility(View.INVISIBLE);
                    mNo.setVisibility(View.INVISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);

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
                        Log.i("name", cont);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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


        mNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kyrie = 0;
                mYes.setVisibility(View.INVISIBLE);
                mNo.setVisibility(View.INVISIBLE);
                if (b == 1) {
                    String word3 = "Screener, please take the user's temperature. Click the temperature button after completing the temperature scanning.";
                    text.setText(word3);
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Screener, please take the user's temperature. Click the temperature button after completing the temperature scanning.", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }, 0);

//                            mTTS.speak(word3, TextToSpeech.QUEUE_FLUSH, null);
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

                                                    Log.i("value", "how are you");


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
                                                                if (number > 99.6 && number <110) {
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
                                                                                    x = 0;
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
//
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
                if(otherFamilyStart == 1) {
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
                            otherFamilyStart = 0;
                        }
                    }, 50);

                }

                if (visiting == 1) {
                    Log.e("help", "visitingShomik");
                    testing = 0;
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
                                        x = 1;
                                        z = 1;
                                        mName.setVisibility(View.VISIBLE);
                                        mName.setHint("Enter Name");
                                        mEnter.setVisibility(View.INVISIBLE);
                                        mExit.setVisibility(View.INVISIBLE);
                                        speak();
                                        counterEnter = 0;
                                        name = 0;
                                        String date = java.text.DateFormat.getDateTimeInstance().format(new Date());
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

                if(testing == 2) {
                    Log.e("help", String.valueOf(testing));
                    Log.e("help","gethelp");
                    mYes.setVisibility(View.VISIBLE);
                    mNo.setVisibility(View.VISIBLE);
                    String word3 = "Unfortunately, you may not enter. Please contact the nursing home supervisor.";
                    text.setText(word3);
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTTS.speak("Unfortunately, you may not enter. Please contact the nursing home supervisor.", TextToSpeech.QUEUE_FLUSH, null);
                            testing = 0;
                        }
                    }, 0);
                    Handler handler10 = new Handler();
                    handler10.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            openActivity2();
                        }
                    },5000);
                }
                if (z == 1) {
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
                            z += 1;
                            x = 3;
                            testing = 0;
                            visiting = 0;
                        }
                    }, 0);

                }
                else if (z == 2) {
                    backTemp = 0;
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
                            z += 1;
                            x = 5;
                        }
                    }, 0);
                } else if (z == 3) {
                    backTemp = 1;
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
                            backTemp = 0;
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

                                                    Log.i("value", "how are you");


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
                                                                                    x = 0;
                                                                                    openActivity2();
                                                                                }
                                                                            }, 5000);

                                                                        }
                                                                    }, 1000);


//

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
                                                                                    z = 7;
                                                                                    x = 7;
                                                                                    mYes.setVisibility(View.VISIBLE);
                                                                                    mNo.setVisibility(View.VISIBLE);
                                                                                }
                                                                            }, 0);


                                                                        }
                                                                    }, 1000);
//


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
                                                            z = 7;
                                                            x = 7;
                                                            mYes.setVisibility(View.VISIBLE);
                                                            mNo.setVisibility(View.VISIBLE);
                                                        }
                                                    }, 0);

                                                    Log.i("value", "how are you");


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

                } else if (z == 4) {
                    mYes.setVisibility(View.INVISIBLE);
                    mNo.setVisibility(View.INVISIBLE);
                    mName.setVisibility(View.INVISIBLE);
                    mTemp.setVisibility(View.INVISIBLE);

                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileDate +"Fail.txt");
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

                } else if (z == 5) {
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

                } else if (z == 6) {
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


                } else if (z == 7) {
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
                            z = 9;
                            x = 9;
                        }
                    }, 0);

                } else if (z == 8) {
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
                } else if (z == 9) {
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

                } else if (z == 10) {
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

    public void dialog_action() {

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

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });

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

                            Log.i("value", "how are you");
                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btn_get_sign.setVisibility(View.INVISIBLE);
                                    kyrie = 1;
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

                            Log.i("value", "how are you");
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
//                                            mTTS.speak("Have you used the hand rub or worn the latex gloves?", TextToSpeech.QUEUE_FLUSH, null);
                                    mTTS.speak("Do you have a fever, sore throat, cough, or a runny nose?", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }, 0);

                            Log.i("value", "how are you");
                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btn_get_sign.setVisibility(View.INVISIBLE);
                                    zion = 0;
                                    kyrie = 1;
                                    testing = 0;
                                    mName.setVisibility(View.INVISIBLE);
                                    mYes.setVisibility(View.VISIBLE);
                                    mNo.setVisibility(View.VISIBLE);
                                    mYes.setText("YES");
                                    mNo.setText("NO");
                                    mYes.setEnabled(true);
                                    mNo.setEnabled(true);
                                    otherFamilyStart = 0;
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
                            // String weird = "Have you used the hand rub or worn the latex gloves?";
                            text.setText(weird);
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
//                                            mTTS.speak("Have you used the hand rub or worn the latex gloves?", TextToSpeech.QUEUE_FLUSH, null);
                                    mTTS.speak("Do you have a fever, sore throat, cough, or a runny nose?", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }, 0);

                            Log.i("value", "how are you");
                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btn_get_sign.setVisibility(View.INVISIBLE);
                                    zion = 0;
                                    kyrie = 1;
                                    testing = 0;
                                    otherFamilyStart = 0;
                                    visiting = 0;
                                    mName.setVisibility(View.INVISIBLE);
                                    mYes.setVisibility(View.VISIBLE);
                                    mNo.setVisibility(View.VISIBLE);
                                    mYes.setEnabled(true);
                                    mNo.setEnabled(true);
                                    backButton.setVisibility(View.VISIBLE);
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

                            Log.i("value", "how are you");
                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btn_get_sign.setVisibility(View.INVISIBLE);
                                    zion = 0;
                                    kyrie = 1;
                                    testing = 0;
                                    mName.setVisibility(View.INVISIBLE);
                                    mYes.setVisibility(View.VISIBLE);
                                    mNo.setVisibility(View.VISIBLE);
                                    mYes.setEnabled(true);
                                    mNo.setEnabled(true);
                                    backButton.setVisibility(View.VISIBLE);
                                }
                            }, 500);


                        }
                    }, 1500);
                }
                // Calling the same class
               // recreate();

            }
        });

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


    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

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

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
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
            if (historicalX < dirtyRect.left) {
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
        //reset signature pad
        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }


    public void openActivity2() {
        screener+=1;
        Intent intent = new Intent(this, com.example.autoScreening_App.MainActivity2.class);
        startActivity(intent);
    }
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
   private void speak() {
        String word = "Please type your name. Next, click sign this form, sign in the white box, and click save. \r\nACKNOWLEDGEMENT: By signing below I confirm that I have answered the above questions honestly and that I will self-monitor and immediately disclose to the Nursing Home Administrator or Manager on Duty any signs or symptoms of respiratory infection, including fever, cough, shortness of breath, or sore throat AND that I received education related to the infection prevention and donning/doffing PPE, AND THAT I WASHED OR SANITIZED MY HANDS PRIOR TO BEGINNING WORK AND/OR PROVIDING DIRECT CARE.";
        text.setText(word);
        mTTS.speak("Please type your name. Next, click sign this form, sign in the white box, and click save. ", TextToSpeech.QUEUE_FLUSH, null);
    }
    protected void onDestroy() {

        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }
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
