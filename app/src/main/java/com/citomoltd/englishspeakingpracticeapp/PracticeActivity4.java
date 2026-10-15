package com.citomoltd.englishspeakingpracticeapp;

import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PracticeActivity4 extends AppCompatActivity {

    RecyclerView recyclerView;
    Button btnSpeak, btnNext, btnReset;
    EditText edtUserResponse;
    RadioGroup radioGroupSpeakers;
    List<DialogLine> dialogLines;
    List<String> uniqueSpeakers = new ArrayList<>();
    int currentLine = 0;
    String currentSpeaker = "";
    TextToSpeech textToSpeech;
    ChatAdapter chatAdapter;
    private FrameLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practice4);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        btnReset = findViewById(R.id.btnReset);
        btnSpeak = findViewById(R.id.btnSpeak);
        btnNext = findViewById(R.id.btnNext);
        edtUserResponse = findViewById(R.id.edtUserResponse);
        radioGroupSpeakers = findViewById(R.id.radioGroupSpeakers);
        adContainer = findViewById(R.id.adContainer);
        edtUserResponse.setVisibility(View.GONE);


        String dialogSummary = getIntent().getStringExtra("dialog");
        dialogLines = parseDialog(dialogSummary);
        AdHelper.loadBannerAd(this, adContainer);

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(1.0f);
            }
        });

        // Set default speaker
        if (!uniqueSpeakers.isEmpty()) {
            currentSpeaker = uniqueSpeakers.get(0);
        }

       /* // Create RadioButtons dynamically for each speaker
        for (int i = 0; i < uniqueSpeakers.size(); i++) {
            String speaker = uniqueSpeakers.get(i);
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(speaker);
            radioButton.setId(View.generateViewId());
            radioGroupSpeakers.addView(radioButton);

            if (i == 0) {
                radioButton.setChecked(true);
            }
        }*/


        for (int i = 0; i < uniqueSpeakers.size(); i++) {
            String speaker = uniqueSpeakers.get(i);
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(speaker);
            radioButton.setId(View.generateViewId());

            // Add some margin to RadioButton
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 30, 0); // Add margin of 16dp (bottom margin)
            radioButton.setLayoutParams(params);


            radioGroupSpeakers.addView(radioButton);

            // Use ContextCompat.getDrawable instead of getResources().getDrawable (which is deprecated)
            Drawable drawable;
            if (i == 0) {
                drawable = ContextCompat.getDrawable(this, R.drawable.beginner); // Image for Speaker 1
            } else if (i == 1) {
                drawable = ContextCompat.getDrawable(this, R.drawable.regular); // Image for Speaker 2
            } else {
                drawable = ContextCompat.getDrawable(this, R.drawable.regular); // Default Image
            }

            // Convert 50dp to pixels
            int sizeInPixels = (int) (50 * getResources().getDisplayMetrics().density);

            // Set bounds to scale the image to 50dp
            drawable.setBounds(0, 0, sizeInPixels, sizeInPixels);

            // Set the image to the left of the text
            radioButton.setCompoundDrawables(drawable, null, null, null);

            // Set custom tick image when RadioButton is checked (instead of circle)
            radioButton.setButtonDrawable(R.drawable.radio_button_tick_image);  // Custom tick image

            // Set the first RadioButton to be checked by default
            if (i == 0) {
                radioButton.setChecked(true);
            }
        }

        // Handle speaker change
        radioGroupSpeakers.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadio = findViewById(checkedId);
            if (selectedRadio != null) {
                currentSpeaker = selectedRadio.getText().toString();
                chatAdapter.setCurrentSpeaker(currentSpeaker);
                chatAdapter.notifyDataSetChanged();
            }
        });

        // Set up RecyclerView
        chatAdapter = new ChatAdapter(dialogLines, currentSpeaker, currentLine);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Speak current line
        btnSpeak.setOnClickListener(v -> {
            if (currentLine < dialogLines.size()) {
                DialogLine line = dialogLines.get(currentLine);
                if (!line.speaker.equals(currentSpeaker)) {
                    textToSpeech.speak(line.message, TextToSpeech.QUEUE_FLUSH, null, null);
                }
                chatAdapter.setSelectedLine(currentLine);
                chatAdapter.notifyDataSetChanged();
            }
        });

        // Move to next line
        btnNext.setOnClickListener(v -> {
            if (currentLine < dialogLines.size()) {
                DialogLine line = dialogLines.get(currentLine);

                // Check if it's the user's turn
                if (line.speaker.equals(currentSpeaker)) {
                    edtUserResponse.setVisibility(View.VISIBLE);
                    edtUserResponse.requestFocus();

                    String userText = edtUserResponse.getText().toString().trim();
                    if (!userText.isEmpty()) {
                        // Remove quotes before comparing
                        String expectedClean = line.message.replace("\"", "").trim();
                        String userClean = userText.replace("\"", "").trim();

                        if (userClean.equalsIgnoreCase(expectedClean)) {
                            Toast.makeText(this, "✅ Correct!", Toast.LENGTH_SHORT).show();
                            showAnswerDialog("Correct", line.message, userText);
                        } else {
                            Toast.makeText(this, "❌ Incorrect! Expected: " + line.message, Toast.LENGTH_LONG).show();
                            showAnswerDialog("Incorrect", line.message, userText);
                        }

                        edtUserResponse.setText(""); // Clear input after check
                    } else {
                        Toast.makeText(this, "Please enter your response!", Toast.LENGTH_SHORT).show();
                        return;
                    }


                } else {
                    edtUserResponse.setVisibility(View.GONE);
                    textToSpeech.speak(line.message, TextToSpeech.QUEUE_FLUSH, null, null);

                    // Animate the speaker's expected text
                    animateText(line.message);
                }

                currentLine++;

                if (currentLine >= dialogLines.size()) {
                    Toast.makeText(this, "🔁 Restarting dialog!", Toast.LENGTH_SHORT).show();
                    currentLine = 0;
                    // Show final result when the dialog ends
                    showFinalResult();
                }

                chatAdapter.setSelectedLine(currentLine);
                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(currentLine);

            }
        });

        btnReset.setOnClickListener(v -> {
            currentLine = 0; // Reset the line index
            chatAdapter.setSelectedLine(currentLine); // Reset the selected line in adapter
            chatAdapter.notifyDataSetChanged(); // Refresh the RecyclerView
            edtUserResponse.setText(""); // Clear the user's response field
        });


    }

    // Parse dialog and extract unique speakers
    private List<DialogLine> parseDialog(String summary) {
        List<DialogLine> lines = new ArrayList<>();
        Set<String> speakerSet = new HashSet<>();

        String[] splitLines = summary.split("\n");
        String currentSpeaker = "";
        StringBuilder currentMessage = new StringBuilder();

        for (String line : splitLines) {
            int colonIndex = line.indexOf(":");
            if (colonIndex != -1) {
                if (currentMessage.length() > 0) {
                    lines.add(new DialogLine(currentSpeaker, currentMessage.toString()));
                }
                currentSpeaker = line.substring(0, colonIndex).trim();
                currentMessage = new StringBuilder(line.substring(colonIndex + 1).trim());
                speakerSet.add(currentSpeaker);
            } else {
                currentMessage.append(" ").append(line.trim());
            }
        }

        if (currentMessage.length() > 0) {
            lines.add(new DialogLine(currentSpeaker, currentMessage.toString()));
        }

        uniqueSpeakers.addAll(speakerSet);
        return lines;
    }

    private void animateText(String message) {
        TextView textView = findViewById(R.id.txtExpectedMessage);  // Assuming this TextView exists in the layout
        textView.setText(message);

        // Apply animation (fade in effect)
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f);
        fadeIn.setDuration(1000);  // Duration for the fade-in effect
        fadeIn.start();
    }


    private void showFinalResult() {
        int correctAnswers = 0;
        int totalAnswers = dialogLines.size();

        // Calculate correct answers (for simplicity, we can track answers in a variable)
        String resultMessage = "You answered " + correctAnswers + " out of " + totalAnswers + " correctly!";

        new AlertDialog.Builder(this)
                .setTitle("Final Results")
                .setMessage(resultMessage)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    finish();  // Close the activity after showing results
                })
                .show();
    }

    private void showAnswerDialog(String result, String expected, String userResponse) {
        String message;

        // Normalize inputs by trimming spaces (but case-sensitive)
        String normalizedExpected = expected.trim();
        String normalizedUserResponse = userResponse.trim();

        // Prepare the message with <br> for line breaks instead of \n
        if ("Correct".equals(result)) {
            message = "<b>Your response:</b> \"" + userResponse + "\"<br><br>" +
                    "<b>The correct answer is:</b> \"" + expected + "\"<br><br>" +
                    "<b>Great job!</b>";
        } else {
            // Case-sensitive comparison of the expected and user response
            if (normalizedExpected.equals(normalizedUserResponse)) {
                message = "<b>Your response:</b> \"" + userResponse + "\"<br><br>" +
                        "<b>The correct answer is:</b> \"" + expected + "\"<br><br>" +
                        "<b>Great job!</b>";
            } else {
                message = "<b>Your response:</b> \"" + userResponse + "\"<br><br>" +
                        "<b>The correct answer is:</b> \"" + expected + "\"<br><br>" +
                        "<b>Mistake:</b> The response you gave does not match the expected answer.<br>" +
                        "<b>Tip:</b> Pay attention to details like punctuation, word choice, and capitalization.<br>" +
                        "Remember: Small letter and capital letter differences matter!<br><br>" +
                        "<b>Better luck next time!</b>";
            }
        }

        // Hide EditText before showing the dialog
        edtUserResponse.setVisibility(View.GONE); // Or View.INVISIBLE if you prefer

        // Show the dialog with feedback
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            new AlertDialog.Builder(this)
                    .setTitle(result)
                    .setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY))  // Updated for newer APIs
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                        // Restore UI actions
                    })
                    .show();
        }else {
            new AlertDialog.Builder(this)
                    .setTitle(result)
                    .setMessage(HtmlCompat.fromHtml(
                            message,
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                    )) // Apply HTML formatting to the message
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                        // Make EditText visible again after dialog is dismissed
                        // Make it visible again
                    })
                    .show();
        }
    }


    // Function to highlight mistakes in user response


    @Override
    protected void onResume() {
        super.onResume();
        // Optionally, you can reload the banner ad when the activity resumes
        AdHelper.loadBannerAd(this, adContainer);
    }


}
