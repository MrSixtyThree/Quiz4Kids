package com.example.assignment2;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    public static List<User> userList;

    public static User currentUser;

    public static void loadUsers(Context context){

        userList = new ArrayList<>();

        try{
            File file = new File(context.getFilesDir(), "login_details.json");

            // Check if file is already created or not, if
            if(file.createNewFile()){
                System.out.println("Creating new file");
                initialiseJsonFile(file);
            }
            else{
                // Read JSON file
                InputStream inputStream = new FileInputStream(file);
                int size = inputStream.available();
                byte[] bufferArray = new byte[size];
                inputStream.read(bufferArray);
                inputStream.close();

                // Get JSON Object
                String json = new String(bufferArray);
                JSONObject jsonObj = new JSONObject(json);
                JSONArray userJSON = jsonObj.getJSONArray("Users");

                // Get Variables to make User Objects
                for(int i = 0; i < userJSON.length(); i++){

                    JSONObject userObject = userJSON.getJSONObject(i);

                    String username = userObject.getString("username");
                    String email = userObject.getString("email");
                    String password = userObject.getString("password");
                    List<Attempt> previousAttempts = new ArrayList<>();
                    JSONArray attemptsJSON =  userObject.getJSONArray("previous_attempts");

                    // Add attempt objects
                    for(int j = 0; j < attemptsJSON.length(); j++){

                        JSONObject previousAttemptObject = attemptsJSON.getJSONObject(j);
                        String quizArea = previousAttemptObject.getString("quiz_area");
                        String startTime = previousAttemptObject.getString("start_time");
                        int points = previousAttemptObject.getInt("points_earned");
                        previousAttempts.add(new Attempt(quizArea,startTime,points));
                    }

                    // Add user to userList
                    userList.add(new User(username, email, password, previousAttempts));

                }
            }

        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch(JSONException e){
            e.printStackTrace();
        }

    }

    public static boolean addUser(Context context, String username, String email, String password){

        try {

            File file = new File(context.getFilesDir(), "login_details.json");

            // Check if file is already created or not, if
            if(file.createNewFile()){
                initialiseJsonFile(file);
            }

            // Read the existing JSON data from the file
            InputStream inputStream = new FileInputStream(file);
            int size = inputStream.available();
            byte[] bufferArray = new byte[size];
            inputStream.read(bufferArray);
            inputStream.close();

            // Convert the byte array to a String
            String json = new String(bufferArray);

            // Parse the JSON data into a JSONObject
            JSONObject jsonObject = new JSONObject(json);

            // Get the "Users" array from the JSONObject
            JSONArray usersArray = jsonObject.getJSONArray("Users");

            // Check if user exists
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userObject = usersArray.getJSONObject(i);
                if (userObject.getString("username").equals(username) || userObject.getString("email").equals(email)) {
                    // User already exists
                    return false;
                }
            }

            // Create a new JSONObject for the new user
            JSONObject newUser = new JSONObject();
            newUser.put("username", username);
            newUser.put("email", email);
            newUser.put("password", password);
            newUser.put("previous_attempts", new JSONArray()); // Initialize with an empty array

            // Add the new user to the JSONArray
            usersArray.put(newUser);

            jsonObject.put("Users", usersArray);

            System.out.println(jsonObject.toString());

            // Write the updated JSONArray back to the JSON file
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
            writer.write(jsonObject.toString()); // check
            writer.close();

            // Update the userList
            loadUsers(context);

            return true;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            System.out.print("Error2\n");
            return false;
        }
    }

    // This was to fix the key of my JSON file after changing a user password
    public static void fixJSON(Context context){
        try {

            File file = new File(context.getFilesDir(), "login_details.json");

            // Read the existing JSON data from the file
            InputStream inputStream = new FileInputStream(file);
            int size = inputStream.available();
            byte[] bufferArray = new byte[size];
            inputStream.read(bufferArray);
            inputStream.close();

            // Convert the byte array to a String
            String json = new String(bufferArray);

            // Parse the JSON data into a JSONObject
            JSONArray jsonObject = new JSONArray(json);

            JSONObject fixedJSON = new JSONObject();
            fixedJSON.put("Users", jsonObject);

            FileWriter writer = new FileWriter(file);
            writer.write(fixedJSON.toString());
            writer.close();

        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
            System.out.print("Error2\n");
        }
    }

    public static boolean changePassword(Context context, String newPassword){

        try {

            File file = new File(context.getFilesDir(), "login_details.json");

            // Check if file is already created or not, if
            if (file.createNewFile()) {
                initialiseJsonFile(file);
            }

            // Read the existing JSON data from the file
            InputStream inputStream = new FileInputStream(file);
            int size = inputStream.available();
            byte[] bufferArray = new byte[size];
            inputStream.read(bufferArray);
            inputStream.close();

            // Convert the byte array to a String
            String json = new String(bufferArray);

            // Parse the JSON data into a JSONObject
            JSONObject jsonObject = new JSONObject(json);

            // Get the "Users" array from the JSONObject
            JSONArray usersArray = jsonObject.getJSONArray("Users");

            // Change password for CurrentUser
            JSONArray updatedUsersArray = new JSONArray();
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userObject = usersArray.getJSONObject(i);
                if (userObject.getString("username").equals(UserManager.currentUser.getUsername())){
                    userObject.put("password", newPassword);
                }
                updatedUsersArray.put(userObject);
            }
            FileWriter writer = new FileWriter(file);
            JSONObject update = new JSONObject();
            update.put("Users", updatedUsersArray);

            writer.write(update.toString());
            writer.close();

            return true;
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
            System.out.print("Error2\n");
            return false;
        }
    }

    public static User login(String username, String password){
        for(User user: userList){
            if(user.getUsername().equals(username) && user.getPassword().equals(password)){
                return user;
            }
        }
        return null;
    }

    private static void initialiseJsonFile(File file) throws IOException, JSONException {

        // Create Users JSON Object
        JSONObject initialData = new JSONObject();
        initialData.put("Users", new JSONArray());

        // Write Object
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write(initialData.toString());
        writer.close();
    }

    // Testing Loading from JSON in LogCat Terminal
    public static void testUserLoading(Context context){

        loadUsers(context);
        // Display user details and previous attempts
        for (User user : userList) {
            String output = "";
            output += "Username: " + user.getUsername() + "\n";
            output += "Email: " + user.getEmail() + "\n";
            output += "Password: " + user.getPassword() + "\n";
            output += "Previous Attempts for " + user.getUsername() + ":\n";
            for (Attempt attempt : user.getPreviousAttempts()) {
                output += attempt.toString() + "\n";
            }

            System.out.println(output);
        }

    }

    public static boolean updateCurrentUserToJSON(Context context){

        try {

            File file = new File(context.getFilesDir(), "login_details.json");

            // Check if file is already created or not, if
            if (file.createNewFile()) {
                initialiseJsonFile(file);
            }

            // Read the existing JSON data from the file
            InputStream inputStream = new FileInputStream(file);
            int size = inputStream.available();
            byte[] bufferArray = new byte[size];
            inputStream.read(bufferArray);
            inputStream.close();

            // Convert the byte array to a String
            String json = new String(bufferArray);

            // Parse the JSON data into a JSONObject
            JSONObject jsonObject = new JSONObject(json);

            // Get the "Users" array from the JSONObject
            JSONArray usersArray = jsonObject.getJSONArray("Users");

            // Convert AttemptsList to JSON array
            JSONArray updatedCurrentUserAttempts = new JSONArray();

            for(Attempt attempt : UserManager.currentUser.getPreviousAttempts()){
                JSONObject attemptObject = new JSONObject();
                attemptObject.put("quiz_area", attempt.getQuizArea());
                attemptObject.put("start_time", attempt.getStartTime());
                attemptObject.put("points_earned", attempt.getPointsEarned());
                updatedCurrentUserAttempts.put(attemptObject);
            }

            // Update attempts for CurrentUser
            JSONArray updatedUsersArray = new JSONArray();

            // Find current user and add new attempts array
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userObject = usersArray.getJSONObject(i);
                if (userObject.getString("username").equals(UserManager.currentUser.getUsername())){
                    userObject.put("previous_attempts", updatedCurrentUserAttempts);
                }
                updatedUsersArray.put(userObject);
            }

            // Overwrite existing JSON file with new file and updates
            FileWriter writer = new FileWriter(file);
            JSONObject update = new JSONObject();
            update.put("Users", updatedUsersArray);

            writer.write(update.toString());
            writer.close();

            return true;
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
            System.out.print("Error2\n");
            return false;
        }
    }

}

class User{

    // Attributes
    private String username;
    private String email;
    private String password;
    private List<Attempt> previousAttempts;

    // Constructor
    public User(String username, String email, String password, List<Attempt> previousAttempts){
        this.username = username;
        this.email = email;
        this.password = password;
        this.previousAttempts = previousAttempts;
    }

    // Getters
    public String getUsername() {return username;}
    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public List<Attempt> getPreviousAttempts() {return previousAttempts;}

    public int getTotalPoints(){
        int total = 0;
        for (Attempt attempt: this.getPreviousAttempts()){
            total += attempt.getPointsEarned();
        }
        return total;
    }

    // Setter
    public boolean changePassword(Context context, String currentPassword, String newPassword){
        if (this.getPassword().equals(currentPassword)){
            if(UserManager.changePassword(context, newPassword)){
                this.password = newPassword;
                return true;
            }
            else{
                return false;
            }
        }
        return false;
    }

    // Method to add a new attempt to the list of previous attempts
    public void addAttempt(String quizArea, String startTime, int pointsEarned) {
        previousAttempts.add(new Attempt(quizArea, startTime, pointsEarned));
    }

}

class Attempt{

    // Attributes
    private String quizArea;
    private String startTime;
    private int pointsEarned;

    // Constructor
    public Attempt(String quizArea, String startTime, int pointsEarned) {
        this.quizArea = quizArea;
        this.startTime = startTime;
        this.pointsEarned = pointsEarned;
    }

    // Getters
    public String getQuizArea() {return quizArea;}
    public String getStartTime() {return startTime;}
    public int getPointsEarned() {return pointsEarned;}

    // toString override
    @Override
    public String toString(){
        return String.format("\"%s\" category: attempt started on \n%s \n- points earned = %d ", this.getQuizArea(), this.getStartTime(), this.getPointsEarned());
    }

}
