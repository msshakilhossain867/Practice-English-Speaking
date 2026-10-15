package com.citomoltd.englishspeakingpracticeapp;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    TextView txtQuestion, txtScore, txtTotalQuestions, textTittle;
    RadioGroup radioGroup;
    RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    Button btnNext;
    DbHelper dbHelper;
    ArrayList<QuizModel> quizList;
    int currentIndex = 0;
    int score = 0;
    int itemId;
    ImageView imageView;
    boolean answerSelected = false;

    private FrameLayout adContainer;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtQuestion = findViewById(R.id.txtQuestion);
        txtScore = findViewById(R.id.txtScore);
        txtTotalQuestions = findViewById(R.id.txtTotalQuestions);
        radioGroup = findViewById(R.id.radioGroup);
        rbOption1 = findViewById(R.id.rbOption1);
        rbOption2 = findViewById(R.id.rbOption2);
        rbOption3 = findViewById(R.id.rbOption3);
        rbOption4 = findViewById(R.id.rbOption4);
        btnNext = findViewById(R.id.btnNext);
        textTittle = findViewById(R.id.textTittle);
        imageView = findViewById(R.id.imageQuiz);
        adContainer = findViewById(R.id.adContainer);

        dbHelper = new DbHelper(this);
        itemId = getIntent().getIntExtra("item_id", -1);
        int images = getIntent().getIntExtra("image", 0);

        int value = getIntent().getIntExtra("topic", 0);
        String tittle = getIntent().getStringExtra("tittle");

        if (value == 1) {
            quizList = loadQuizQuestions(itemId);
        } else {
            quizList = loadQuizQuestionsRegular(itemId);
        }
        quizList = loadQuizQuestions(itemId);
        showQuestion();
        AdHelper.loadBannerAd(this, adContainer);

        txtTotalQuestions.setText("Total Quiz: " + quizList.size());

        textTittle.setText(tittle);

        if (images != 0) {
            imageView.setImageResource(images);
        } else {
            imageView.setImageResource(R.drawable.beginner); // fallback image
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (!answerSelected) {
                checkAnswer(checkedId);
                answerSelected = true;
                btnNext.setVisibility(View.VISIBLE);  // Show "Next" button only after selection
            }
        });

        btnNext.setOnClickListener(v -> {
            currentIndex++;
            if (currentIndex < quizList.size()) {
                resetOptions();
                showQuestion();
                answerSelected = false;  // Reset answer selection for next question
            } else {
                finish();
            }
        });
    }

    private ArrayList<QuizModel> loadQuizQuestions(int itemId) {
        ArrayList<QuizModel> list = new ArrayList<>();
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM quizzes WHERE item_id=" + itemId, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String question = cursor.getString(2);
            String option1 = cursor.getString(3);
            String option2 = cursor.getString(4);
            String option3 = cursor.getString(5);
            String option4 = cursor.getString(6);
            int correctOption = cursor.getInt(7); // Correct option index (1,2,3,4)
            list.add(new QuizModel(id, question, option1, option2, option3, option4, correctOption));
        }
        cursor.close();
        return list;
    }

    private ArrayList<QuizModel> loadQuizQuestionsRegular(int itemId) {
        ArrayList<QuizModel> list = new ArrayList<>();
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM regular_quizzes WHERE item_id=" + itemId, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String question = cursor.getString(2);
            String option1 = cursor.getString(3);
            String option2 = cursor.getString(4);
            String option3 = cursor.getString(5);
            String option4 = cursor.getString(6);
            int correctOption = cursor.getInt(7); // Correct option index (1,2,3,4)
            list.add(new QuizModel(id, question, option1, option2, option3, option4, correctOption));
        }
        cursor.close();
        return list;
    }

    private void showQuestion() {
        QuizModel quiz = quizList.get(currentIndex);
        txtQuestion.setText(quiz.getQuestion());
        rbOption1.setText(quiz.getOption1());
        rbOption2.setText(quiz.getOption2());
        rbOption3.setText(quiz.getOption3());
        rbOption4.setText(quiz.getOption4());
    }

    private void checkAnswer(int checkedId) {
        QuizModel quiz = quizList.get(currentIndex);
        int correctOption = quiz.getCorrectOption(); // 1-based index

        // Get user selected answer
        RadioButton selectedRadioButton = findViewById(checkedId);
        int userSelected = radioGroup.indexOfChild(selectedRadioButton) + 1; // Convert to 1-based index

        // Highlight correct and incorrect answers
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            rbOption1.setTextColor(correctOption == 1 ? Color.GREEN : (userSelected == 1 ? Color.RED : Color.WHITE));
            rbOption2.setTextColor(correctOption == 2 ? Color.GREEN : (userSelected == 2 ? Color.RED : Color.WHITE));
            rbOption3.setTextColor(correctOption == 3 ? Color.GREEN : (userSelected == 3 ? Color.RED : Color.WHITE));
            rbOption4.setTextColor(correctOption == 4 ? Color.GREEN : (userSelected == 4 ? Color.RED : Color.WHITE));
        } else {
            rbOption1.setTextColor(correctOption == 1 ? Color.GREEN : (userSelected == 1 ? Color.RED : Color.BLACK));
            rbOption2.setTextColor(correctOption == 2 ? Color.GREEN : (userSelected == 2 ? Color.RED : Color.BLACK));
            rbOption3.setTextColor(correctOption == 3 ? Color.GREEN : (userSelected == 3 ? Color.RED : Color.BLACK));
            rbOption4.setTextColor(correctOption == 4 ? Color.GREEN : (userSelected == 4 ? Color.RED : Color.BLACK));

        }

        // Update score only if the answer is correct
        if (userSelected == correctOption) {
            ++score;
        }
        txtScore.setText("Score: " + score + "/" + quizList.size());
    }

    private void resetOptions() {

        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            radioGroup.clearCheck();
            rbOption1.setTextColor(Color.WHITE);
            rbOption2.setTextColor(Color.WHITE);
            rbOption3.setTextColor(Color.WHITE);
            rbOption4.setTextColor(Color.WHITE);
            btnNext.setVisibility(View.GONE);
        } else {

            radioGroup.clearCheck();
            rbOption1.setTextColor(Color.BLACK);
            rbOption2.setTextColor(Color.BLACK);
            rbOption3.setTextColor(Color.BLACK);
            rbOption4.setTextColor(Color.BLACK);
            btnNext.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        // Optionally, you can reload the banner ad when the activity resumes
        AdHelper.loadBannerAd(this, adContainer);
    }
}
