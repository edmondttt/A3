package com.example.quizapp;

import static android.content.ContentValues.TAG;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    int numQuestion = 10;
    int correctAns = 0;
    int x = 0;
    ArrayList<Question> questions = new ArrayList<>();
    ProgressBar progressBar;
    String filenameCache = "myfile_cache.txt";

    private AlertDialog.Builder builder;

    // Get the device's default Locale
    Locale defaultLocale = Locale.getDefault();

    // Get the language code (e.g., "en" for English, "es" for Spanish)
    String languageCode = defaultLocale.getLanguage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button TrueBtn = findViewById(R.id.buttonTrue);
        Button FalseBtn = findViewById(R.id.buttonFalse);
        Button StartBtn = findViewById(R.id.buttonStart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final int progressStatus = 0;
        progressBar = findViewById(R.id.progressBar);


        if (languageCode.equals("zh")){
            Configuration configuration = getResources().getConfiguration();
            configuration.setLocale(new Locale("zh"));
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        }


        StartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
                QuestionFragment myFragment = (QuestionFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
                // Check if the fragment is not null before calling the method
                if (myFragment != null) {
                    // Call the public method in the fragment to update the text
                    myFragment.setText(questions.get(0).getQuestion());
                    myFragment.changeBackgroundColor();
                }
            }
        });
        TrueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (questions.size() != 0) {
                    String Ans = questions.get(0).getAnswer();
                    if (Ans.equals("True")){
                        Toast.makeText(MainActivity.this, "Correct", Toast.LENGTH_SHORT).show();
                        correctAns++;

                    }
                    else{
                        Toast.makeText(MainActivity.this, "Wrong", Toast.LENGTH_SHORT).show();

                    }

                }
                nextQuestion(questions);
                updateProgressBar();
            }

        });

        FalseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (questions.size() != 0) {
                    String Ans = questions.get(0).getAnswer();
                    if (Ans.equals("False")){
                        Toast.makeText(MainActivity.this, "Correct", Toast.LENGTH_SHORT).show();
                        correctAns++;
                        Log.e("Q", Integer.toString(correctAns));
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Wrong", Toast.LENGTH_SHORT).show();
                    }


                }
                nextQuestion(questions);
                updateProgressBar();
            }

        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.average){
            String data = readFromCacheStorage(getApplicationContext(), filenameCache);
            int index1 = data.indexOf(",");
            int index2 = data.indexOf(";");
            String c = data.substring(0,index1);
            String q = data.substring(index1 + 1,index2);

            builder = new AlertDialog.Builder(this);
            builder.setMessage("Your correct answer / total number of questions = " + c + " out of " + q)
                    .setCancelable(false).setTitle("Result")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //storeCacheStorage(getApplicationContext(),filenameCache,Integer.toString(correctAns), Integer.toString(numQuestion));
                            //Toast.makeText(MainActivity.this, "Thanks for Purchasing!", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            generateQuestion(questions, numQuestion);
                            reset();
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.show();
        }
        if (item.getItemId() == R.id.numQuestion){
            showDialog();
//            reset();
//            QuestionFragment myFragment = (QuestionFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
//            // Check if the fragment is not null before calling the method
//            if (myFragment != null) {
//                // Call the public method in the fragment to update the text
//                myFragment.setText(questions.get(0).getQuestion());
//                myFragment.changeBackgroundColor();
//            }
        }
        if (item.getItemId() == R.id.reset){
            storeCacheStorage(getApplicationContext(),filenameCache,"0","0");
            Toast.makeText(MainActivity.this, "Resat!", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    private void generateQuestion(ArrayList<Question> questions, int numQuestion) {
        questions.clear();
        for (int i = 0; i < numQuestion; i++) {

                String [] q = getResources().getStringArray(R.array.question);
                String [] a = getResources().getStringArray(R.array.questionAns);
                Question question = new Question(q[i],a[i]);
                questions.add(question);


            Collections.shuffle(questions);
        }
    }

    private void nextQuestion(ArrayList<Question> questions) {
        if (questions.size() != 0){

            questions.remove(0);
            QuestionFragment myFragment = (QuestionFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
            // Check if the fragment is not null before calling the method
            if (myFragment != null) {
                // Call the public method in the fragment to update the text
                if (questions.size() != 0) {
                    myFragment.setText(questions.get(0).getQuestion());
                    myFragment.changeBackgroundColor();
                }
            }
        }
        else{

            builder = new AlertDialog.Builder(this);
            builder.setMessage("Your score is :" + correctAns + " out of " + numQuestion)
                    .setCancelable(false).setTitle("Result")
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            storeCacheStorage(getApplicationContext(),filenameCache,Integer.toString(correctAns), Integer.toString(numQuestion));
                            //Toast.makeText(MainActivity.this, "Thanks for Purchasing!", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button

                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.show();
        }

    }
    private void updateProgressBar() {
        int prograss = 100 / numQuestion;
        if (progressBar.getProgress() != progressBar.getMax()){
            progressBar.incrementProgressBy(prograss);
        }

    }
    private void reset() {
        generateQuestion(questions, numQuestion);
        progressBar.setProgress(0);
        correctAns = 0;

    }


    public void storeCacheStorage(Context context, String filename, String correctAns, String numbQuestions)
    {
        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, filename);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(correctAns.getBytes());
            fileOutputStream.write(",".getBytes());
            fileOutputStream.write(numbQuestions.getBytes());
            fileOutputStream.write(";".getBytes());
            Log.i("Data:", "Cache Saved");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readFromCacheStorage(Context context, String filename)
    {


        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, filename);


        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            fileInputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            String line;

            while((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stringBuilder.toString();
    }
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        final EditText editTextInput = dialogView.findViewById(R.id.editTextInput);
        builder.setView(dialogView)
                .setTitle("Enter the number of question less than 10")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the user input when the "OK" button is clicked
                        String userInput = editTextInput.getText().toString();
                        // Process the user input here
                        numQuestion = Integer.parseInt(userInput);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle "Cancel" button click (optional)
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}