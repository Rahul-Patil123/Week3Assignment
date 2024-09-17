/*
 * Name : Rahul Ganeshwar Patil
 * Date : 17-09-2024
 * Description : [This Program can be used by umpire to keep score card running]**/

import java.util.Scanner;
public class CricketScoreMain {
	final static Constant constant = new Constant();
	final static Scanner input = new Scanner(System.in);
	public static void main(String args[]) {
		constant.printOptions();
		System.out.println(constant.ENTER_CHOICE);
		String option = input.nextLine();
		boolean isQuit = false;
		do {
			switch(option) {
			case "1" : 
				System.out.println("Selected Choice 1");
//				constant.TeamName.toString();
//				constant.viewTeams();
				break;
			case "2" : 
				System.out.println("Selected Choice 2");
				String EnteredTeamName = input.nextLine();
				break;
			case "3" : 
				System.out.println("Selected Choice 3");
				break;
			case "4" : 
				System.out.println("Exiting !! BYE BYE BYE");
				isQuit = true;
				break;
			default : 
				System.out.println("Enter valid options only!!!");
				isQuit = true;
				break;
			}
			if(!isQuit) {
				constant.printOptions();
				option = input.nextLine();
				
			}
		}while(!isQuit);
	}
}
