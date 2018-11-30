package com.davisbase.main;
import java.util.Scanner;
import com.davisbase.util.DavisBaseConstants;
import com.davisbase.util.HelperUtil;
import java.util.ArrayList;
import java.util.Arrays;
import static java.lang.System.out;


public class MyDbPrompt {

	/* This can be changed to whatever you like */
	static boolean isExit = false;
	static HelperUtil helperUtil = new HelperUtil();
	/*
	 * Page size for alll files is 512 bytes by default.
	 * You may choose to make it user modifiable
	 */

	/* 
	 *  The Scanner class is used to collect user commands from the prompt
	 *  There are many ways to do this. This is just one.
	 *
	 *  Each time the semicolon (;) delimiter is entered, the userCommand 
	 *  String is re-populated.
	 */
	static Scanner scanner = new Scanner(System.in).useDelimiter(";");
	
	/** ***********************************************************************
	 *  Main method
	 */
    public static void main(String[] args) {

		splashScreen(); // Display the welcome screen
		BuildTree.initialzeCatalog();// Initializes the catalog directories
		BuildTree.createUserData(); // Create user_data directory
		
		/* Variable to collect user input from the prompt */
		String userCommand = ""; 

		while(!isExit) {
			System.out.print(DavisBaseConstants.PROMPT);
			userCommand = scanner.next().replace("\n", " ").replace("\r", "").trim().toLowerCase();
			parseUserCommand(userCommand);
		}
		System.out.println("Exiting...");
	}

	/** ***********************************************************************
	 *  Static method definitions
	 */

	/**
	 *  Display the splash screen
	 */
	public static void splashScreen() {
		System.out.println(line("-",80));
        System.out.println("Welcome to DavisBaseLite"); // Display the string.
		System.out.println("DavisBaseLite Version " + getVersion());
		System.out.println(getCopyright());
		System.out.println("\nType \"help;\" to display supported commands.");
		System.out.println(line("-",80));
	}
	
	/**
	 * @param s The String to be repeated
	 * @param num The number of time to repeat String s.
	 * @return String A String object, which is the String s appended to itself num times.
	 */
	public static String line(String s,int num) {
		String a = "";
		for(int i=0;i<num;i++) {
			a += s;
		}
		return a;
	}
	
	public static void printCmd(String s) {
		System.out.println("\n\t" + s + "\n");
	}
	public static void printDef(String s) {
		System.out.println("\t\t" + s);
	}
	
		/**
		 *  Help: Display supported commands
		 */
		public static void help() {
			out.println(line("*",80));
			out.println("SUPPORTED COMMANDS\n");
			out.println("All commands below are case insensitive\n");
			out.println("SHOW TABLES;");
			out.println("\tDisplay the names of all tables.\n");
			//printCmd("SELECT * FROM <table_name>;");
			//printDef("Display all records in the table <table_name>.");
			out.println("SELECT <column_list> FROM <table_name> [WHERE <condition>];");
			out.println("\tDisplay table records whose optional <condition>");
			out.println("\tis <column_name> = <value>.\n");
			out.println("DROP TABLE <table_name>;");
			out.println("\tRemove table data (i.e. all records) and its schema.\n");
			out.println("UPDATE TABLE <table_name> SET <column_name> = <value> [WHERE <condition>];");
			out.println("\tModify records data whose optional <condition> is\n");
			out.println("VERSION;");
			out.println("\tDisplay the program version.\n");
			out.println("HELP;");
			out.println("\tDisplay this help information.\n");
			out.println("EXIT;");
			out.println("\tExit the program.\n");
			out.println(line("*",80));
		}

	/** return the DavisBase version */
	public static String getVersion() {
		return DavisBaseConstants.VERSION;
	}
	
	public static String getCopyright() {
		return DavisBaseConstants.COPYRIGHT;
	}
	
	public static void displayVersion() {
		System.out.println("DavisBaseLite Version " + getVersion());
		System.out.println(getCopyright());
	}
		
	public static void parseUserCommand (String userCommand) {
		
		/* commandTokens is an array of Strings that contains one token per array element 
		 * The first token can be used to determine the type of command 
		 * The other tokens can be used to pass relevant parameters to each command-specific
		 * method inside each case statement */
		// String[] commandTokens = userCommand.split(" ");
		ArrayList<String> commandTokens = new ArrayList<String>(Arrays.asList(userCommand.split(" ")));
		

		/*
		*  This switch handles a very small list of hardcoded commands of known syntax.
		*  You will want to rewrite this method to interpret more complex commands. 
		*/
		switch (commandTokens.get(0)) {
			case "select":
				System.out.println("CASE: SELECT");
				parseQuery(userCommand);
				break;
			case "drop":
				System.out.println("CASE: DROP");
				dropTable(userCommand);
				break;
			case "create":
				System.out.println("CASE: CREATE");
				if(commandTokens.get(1).equals(DavisBaseConstants.TABLE)) {
					parseCreateTable(userCommand);
				}
				break;
			case "update":
				System.out.println("CASE: UPDATE");
				parseUpdate(userCommand);
				break;
			case "help":
				help();
				break;
			case "version":
				displayVersion();
				break;
			case "exit":
				isExit = true;
				break;
			case "quit":
				isExit = true;
			default:
				System.out.println("I didn't understand the command: \"" + userCommand + "\"");
				break;
		}
	}
	

	/**
	 *  Stub method for dropping tables
	 *  @param dropTableString is a String of the user input
	 */
	public static void dropTable(String dropTableString) {
		System.out.println("STUB: This is the dropTable method.");
		System.out.println("\tParsing the string:\"" + dropTableString + "\"");
		String[] temp = dropTableString.split(" ");
		String tableName = temp[2].trim();
		if(!helperUtil.isTableExist(tableName)) {
			System.out.println("Table " + tableName + " is not present");
		}
		else
			BuildTree.DropTable(tableName);
	}
	
	/**
	 *  Stub method for executing queries
	 *  @param queryString is a String of the user input
	 */
	public static void parseQuery(String queryString) {
		System.out.println("STUB: This is the parseQuery method");
		System.out.println("\tParsing the string:\"" + queryString + "\"");
		String[] splitByWhere = queryString.toLowerCase().split("where");
		String tableName = splitByWhere[0].split("from")[1].trim();
		String[] colNames = splitByWhere[0].split("from")[0].replaceAll("select", " ").split(" ");
		String[] whereCond = new String[0];
		if(!helperUtil.isTableExist(tableName)) {
			System.out.println("Table " + tableName + " not present");
		}
		else {
			for(int i=0; i<colNames.length; i++) {
				colNames[i] = colNames[i].trim();
			}
			if(splitByWhere.length > 1) {
				whereCond = helperUtil.CheckCondition(splitByWhere[1]);
			}
			BuildTree.SelectQuery(tableName, colNames, whereCond);
		}
	}

	/**
	 *  Stub method for updating records
	 *  @param updateString is a String of the user input
	 */
	public static void parseUpdate(String updateString) {
		System.out.println("STUB: This is the dropTable method");
		System.out.println("Parsing the string:\"" + updateString + "\"");
		String[] updateTemp = updateString.toLowerCase().split("set");
		//String[] temp = updateTemp[0].trim().split(" ");
		String tableName = updateTemp[0].trim().split(" ")[1].trim();
		String updateValue;
		String whereCond = null;
		if(!helperUtil.isTableExist(tableName)) {
			System.out.println("Table not present");
		}
		else if(updateTemp[1].toLowerCase().contains("where")){
			String[] updates = updateTemp[1].toLowerCase().split("where");
			updateValue = updates[0].trim();
			whereCond = updates[1].trim();
			BuildTree.UpdateQuery(tableName,helperUtil.CheckCondition(updateValue), helperUtil.CheckCondition(whereCond));
		}
		else {
			updateValue = updateTemp[1].trim();
			BuildTree.UpdateQuery(tableName,helperUtil.CheckCondition(updateValue), new String[0]);
		}
	}

	
	/**
	 *  Stub method for creating new tables
	 *  @param queryString is a String of the user input
	 */
	public static void parseCreateTable(String createTableString) {
		
		System.out.println("STUB: Calling your method to create a table");
		System.out.println("Parsing the string:\"" + createTableString + "\"");
		ArrayList<String> createTableTokens = new ArrayList<String>(Arrays.asList(createTableString.split(" ")));

		/* Define table file name */
		String tableName = createTableTokens.get(2);
		String[] tempTable = createTableString.replaceAll("\\(", "").replaceAll("\\)", "").split(tableName);
		String[] columnNames = tempTable[1].trim().split(",");
		
		if(!helperUtil.isTableExist(tableName)) {
			System.out.println("Table " + tableName + "already exists");
		}
		
		else {
			BuildTree.createTable(tableName, columnNames);
		}
		
		/*  Code to insert a row in the davisbase_tables table 
		 *  i.e. database catalog meta-data 
		 */
		
		/*  Code to insert rows in the davisbase_columns table  
		 *  for each column in the new table 
		 *  i.e. database catalog meta-data 
		 */
	}
	
	
}
