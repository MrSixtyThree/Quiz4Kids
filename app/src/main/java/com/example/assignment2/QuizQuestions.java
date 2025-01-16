package com.example.assignment2;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizQuestions {

    // Attributes
    private List<Question> questions;

    // Constructor
    public QuizQuestions(Context context, String category){
        questions = new ArrayList<>();
        loadQuestions(context, category);
    }

    public List<Question> getQuestions(){
        return questions;
    }

    // Loads questions from JSON file
    private void loadQuestions(Context context, String category){
        try {
            // Read the JSON File of Quiz Questions
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("quiz_questions.json");
            int size = inputStream.available();
            byte[] bufferArray = new byte[size];
            inputStream.read(bufferArray);
            inputStream.close();

            // Get JSON objects from Category
            String json = new String(bufferArray);
            JSONObject jsonObj = new JSONObject(json);
            JSONArray categoryInfo = jsonObj.getJSONArray(category);

            // Create list of available indices to later remove chosen ones to avoid double up questions
            List<Integer> availableIndices = new ArrayList<>();
            for (int i = 0; i < categoryInfo.length(); i++) {
                availableIndices.add(i);
            }

            // Pick four random indices and get info to make question object from JSONArray
            Random random = new Random();
            for(int i = 0; i < 4; i++){

                // Find Index
                int randomIndex = random.nextInt(availableIndices.size());
                int questionIndex = availableIndices.get(randomIndex);

                // Create variables to be parsed to Question Object
                JSONObject questionObject = categoryInfo.getJSONObject(questionIndex);
                String question = questionObject.getString("question");
                String answer = questionObject.getString("answer");
                String[] options = null;
                String image = null;

                // Set options or image variables depending on the category selected
                switch(category){
                    case "Cartoons":
                        JSONArray optionsJSON = questionObject.getJSONArray("options");
                        options = new String[optionsJSON.length()];
                        for (int j = 0; j < optionsJSON.length(); j++){
                            options[j] = optionsJSON.getString(j);
                        }
                        // Create question object
                        questions.add(new Question(question, answer, options));
                        break;
                    case "Animals":
                        image = questionObject.getString("image");
                        questions.add(new Question(question, answer, image));
                        break;
                }
                availableIndices.remove(randomIndex); // Remove randomly chosen index from list of available indices
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    // Testing Loading from JSON in LogCat Terminal
    public static void testQuestionLoading(Context context){
        QuizQuestions qq = new QuizQuestions(context, "Animals");
        List<Question> questions = qq.getQuestions();
        QuizQuestions qq2 = new QuizQuestions(context, "Cartoons");
        List<Question> questions2 = qq2.getQuestions();

        for (Question question : questions) {
            String questionText = question.getQuestion();
            String answer = question.getAnswer();
            String image = question.getImage();

            // Display question information
            String questionInfo = "Question: " + questionText + "\n"
                    + "Answer: " + answer + "\n"
                    + "Image:" + image + "\n";
            System.out.println(questionInfo);
        }

        for (Question question : questions2) {
            String questionText = question.getQuestion();
            String answer = question.getAnswer();
            String[] options = question.getOptions();

            // Display question information
            String questionInfo = "Question: " + questionText + "\n"
                    + "Answer: " + answer + "\n"
                    + "Options:" + options[0] + "\n"
                    + options[1] + "\n"
                    + options[2] + "\n";
            System.out.println(questionInfo);
        }
    }

}

class Question {

    // Attributes
    private String question;
    private String answer;
    private String[] options; // For Cartoons
    private String image; // For Animals

    // Constructor for Animals
    public Question(String question, String answer, String image){
        this.question = question;
        this.answer = answer;
        this.image = image;
        this.options = null;
    }

    // Constructor for Cartoons
    public Question(String question, String answer, String[] options){
        this.question = question;
        this.answer = answer;
        this.image = null;
        this.options = options;
    }

    // Getters
    public String getQuestion(){
        return this.question;
    }

    public String getAnswer(){
        return this.answer;
    }

    public String getImage(){
        return this.image;
    }

    public String[] getOptions(){
        return this.options;
    }
}
