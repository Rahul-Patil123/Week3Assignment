import java.io.*;
import java.util.*;

public class Trial {
    private static final String FILE_NAME = "teams.txt";
    static Scanner input = new Scanner(System.in);
    // Add a team and its players to the file
    public static void addOrUpdateTeam(String teamName, String[] players) {
        File originalFile = new File(FILE_NAME);
        File tempFile = new File("temp_" + FILE_NAME);

        // Check if the original file exists and is not empty
        boolean fileExists = originalFile.exists();
        boolean fileIsEmpty = true;

        if (fileExists) {
            try (BufferedReader reader = new BufferedReader(new FileReader(originalFile))) {
                if (reader.readLine() != null) {
                    fileIsEmpty = false;
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the file.");
                e.printStackTrace();
            }
        }

        // If the file doesn't exist or is empty, just add the new team
        if (!fileExists || fileIsEmpty) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                writer.write("Team " + teamName + ":");
                writer.newLine();
                for (String player : players) {
                    writer.write("  Player: " + player);
                    writer.newLine();
                }
                System.out.println("Team added successfully.");
                return;
            } catch (IOException e) {
                System.out.println("An error occurred while writing to the file.");
                e.printStackTrace();
                return;
            }
        }

        // If the file exists and is not empty, we proceed to update or add the team
        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean teamFound = false;
            while ((line = reader.readLine()) != null) {
                if (line.equals("Team " + teamName + ":")) {
                    teamFound = true;
                    writer.write("Team " + teamName + ":");
                    writer.newLine();
                    for (String player : players) {
                        writer.write("  Player: " + player);
                        writer.newLine();
                    }
                    // Skip the current team players and move on to the next team in the file
                    while ((line = reader.readLine()) != null && line.startsWith("  Player: ")) {
                    }
                }
                if (line != null) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            if (!teamFound) {
                writer.write("Team " + teamName + ":");
                writer.newLine();
                for (String player : players) {
                    writer.write("  Player: " + player);
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            System.out.println("An error occurred while modifying the file.");
            e.printStackTrace();
        }
        if (originalFile.delete()) {
            if (!tempFile.renameTo(originalFile)) {
                System.out.println("An error occurred while renaming the file.");
            }
        }
    }
    public static void addPlayerToTeam(String teamName, String newPlayer) {
        File tempFile = new File("temp_" + FILE_NAME);
        File originalFile = new File(FILE_NAME);

        boolean teamFound = false;

        // Check if the file exists and is not empty
        if (!originalFile.exists() || originalFile.length() == 0) {
            System.out.println("No teams exist. Adding a new team.");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(originalFile))) {
                writer.write("Team: " + teamName);
                writer.newLine();
                writer.write("  Player: " + newPlayer);
                writer.newLine();
                return;
            } catch (IOException e) {
                System.out.println("An error occurred while writing to the file.");
                e.printStackTrace();
                return;
            }
        }

        // If the file exists, read it and modify it
        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
                if (line.equals("Team: " + teamName)) {
                    teamFound = true;
                } else if (teamFound && line.startsWith("  Player: ")) {
                } else if (teamFound && line.isEmpty()) {
                    // Add the new player before closing the team section
                    writer.write("  Player: " + newPlayer);
                    writer.newLine();
                    teamFound = false; 
                }
            }

            // If the team was found and hasn't been updated yet, add the new player at the end of the team section
            if (teamFound) {
                writer.write("  Player: " + newPlayer);
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("An error occurred while modifying the file.");
            e.printStackTrace();
            return;
        }

        // Replace the original file with the updated file
        if (originalFile.delete()) {
            if (!tempFile.renameTo(originalFile)) {
                System.out.println("An error occurred while renaming the file.");
                try (InputStream in = new FileInputStream(tempFile);
                     OutputStream out = new FileOutputStream(originalFile)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                } catch (IOException ioException) {
                    System.out.println("An error occurred while copying the file.");
                    ioException.printStackTrace();
                }
            }
        } else {
            System.out.println("Failed to delete the original file.");
        }

        // Optionally, delete the temp file if it exists
        if (tempFile.exists()) {
            tempFile.delete();
        }

        // If the team was not found, add the team at the end of the file
        if (!teamFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(originalFile, true))) {
                writer.write("Team: " + teamName);
                writer.newLine();
                writer.write("  Player: " + newPlayer);
                writer.newLine();
                System.out.println("Team " + teamName + " was not found, so it was added.");
            } catch (IOException e) {
                System.out.println("An error occurred while adding the new team to the file.");
                e.printStackTrace();
            }
        }
    }
    public static void displayTeams() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading from the file.");
            e.printStackTrace();
        }	
    }

    public static void main(String[] args) {
    	System.out.println("Enter number of players you want :");
    	int squadMembersCount = input.nextInt();
    	input.nextLine();
        String[] Players = new String[squadMembersCount];
        System.out.println("Enter names of players");
        for(int i = 0; i < squadMembersCount; i++) {
        	Players[i] = input.nextLine();
        }

        addOrUpdateTeam("A", Players);
        addPlayerToTeam("C", "Chomu");

        System.out.println("Teams and Players:");
        displayTeams();
    }
}
