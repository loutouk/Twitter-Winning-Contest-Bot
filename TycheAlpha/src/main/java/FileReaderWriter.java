import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Louis Boursier
 */


public class FileReaderWriter {

    private static final String ENTERED_CONTESTS_FILE_NAME = "entered_contests.txt";
    private static final String FOLLOWED_USERS_FILE_NAME = "followed_users.txt";

    private static FileReaderWriter ourInstance = new FileReaderWriter();

    public static FileReaderWriter getInstance() {
        return ourInstance;
    }

    private FileReaderWriter() {
    }

    public static List<Long> getEnteredContests() {
        List<Long> enteredContest = new ArrayList<>();
        try {
            File file = new File(ENTERED_CONTESTS_FILE_NAME);
            try {
                file.createNewFile(); // if file already exists will do nothing
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                enteredContest.add(Long.valueOf(scanner.nextLine()));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return enteredContest;
    }

    public static void writeEnteredContests(List<Long> tweetIds) {
        File file = new File(ENTERED_CONTESTS_FILE_NAME);
        try {
            file.createNewFile(); // if file already exists will do nothing
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter fr = null;
        try {
            fr = new FileWriter(file, true);
            try {
                for(Long tweetId : tweetIds){
                    fr.write(tweetId + "\n");
                }
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFollowedUsers(String userName) {
        File file = new File(FOLLOWED_USERS_FILE_NAME);
        try {
            file.createNewFile(); // if file already exists will do nothing
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter fr = null;
        try {
            fr = new FileWriter(file, true);
            try {
                fr.write(userName + "\n");
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getFollowedUsers() {
        List<String> followedUsers = new ArrayList<>();
        try {
            File file = new File(FOLLOWED_USERS_FILE_NAME);
            try {
                file.createNewFile(); // if file already exists will do nothing
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                followedUsers.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return followedUsers;
    }
}
