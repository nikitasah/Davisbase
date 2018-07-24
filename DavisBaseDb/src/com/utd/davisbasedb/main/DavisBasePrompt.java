package com.utd.davisbasedb.main;

import java.io.File;
import java.util.Scanner;
import com.utd.davisbasedb.utils.DatabaseConstants;
import com.utd.davisbasedb.main.Query;


/*
 *  @author Nikita 
 *  @version 1.0
 *  <b>
 *  <p> This class is used to create a prompt and collect user input</p>
 *  </b>
 */

public class DavisBasePrompt {
		
	static Scanner scanner = new Scanner(System.in).useDelimiter(";");
	static String defaultDatabase = DatabaseConstants.DEFAULTDB;
	
    public static void main(String[] args) {
    	
		/* Display the welcome screen */
		splashScreen();
		/* Variable to collect user input from the prompt */
		String userCommand = ""; 
		/* Initialize the meta data */
		initializeMetaData();
		while(!userCommand.equals("exit")) {
			System.out.print(DatabaseConstants.PROMPT);
			userCommand = scanner.next().replace("\n", " ").replace("\r", "").trim().toLowerCase();
			parseUserCommand(userCommand);
		}
		System.out.println("Exiting from DavisBase");
	}

    /*
     * This function is used to initialize meta data of the database
     */
    
    public static void initializeMetaData() 
	{
		try {
			File data = new File(DatabaseConstants.DEFAULT_DATA_DIRNAME);
			if (!data.exists()) 
			{
				data.mkdir();
			}
			File metaData = new File("data\\meta_data");
			if (metaData.mkdir()) 
			{
				System.out.println("System directory 'data\\meta_data' doesn't exit, Initializing metadata!");
				FileOperations.initializeDataStore();
			} 
			else 
			{
				boolean catalog = false;
				String meta_columns = DatabaseConstants.SYSTEM_TABLES_TABLENAME;
				String meta_tables = DatabaseConstants.SYSTEM_COLUMNS_TABLENAME;
				String[] tableList = metaData.list();

				for (int i = 0; i < tableList.length; i++) 
				{
					if (tableList[i].equals(meta_columns))
						catalog = true;
				}
				if (!catalog) 
				{
					System.out.println(
							"System table 'davisbase_columns.tbl' does not exit, initializing davisbase_columns");
					System.out.println();
					FileOperations.initializeDataStore();
				}
				catalog = false;
				for (int i = 0; i < tableList.length; i++) 
				{
					if (tableList[i].equals(meta_tables))
						catalog = true;
				}
				if (!catalog) 
				{
					System.out.println(
							"System table 'davisbase_tables.tbl' does not exit, initializing davisbase_tables");
					System.out.println();
					FileOperations.initializeDataStore();
				}
			}
		} 
		catch (SecurityException se) 
		{
			System.out.println("Meta Data files not created " + se);
		}
	}

    /*
     * @param userCommand : String entered by the user
     * This function is used to initialize parse the query
     */
    
    public static void parseUserCommand(String userCommand) {
		
		String[] tokens = userCommand.split(" ");
		switch(tokens[0]) {
			case "use":
				if(tokens[1].equals("")) {
					System.out.println("Incorrect input. Refer to the help section");	
				}
			    else if(!Query.isDataBaseExists(tokens[1])) {
					System.out.println("Database doesn't exist");
				}
				else {
					System.out.println("using "+ defaultDatabase);
				}
				break;
			case "create":
				if(tokens[1].equals("table")) {
					Query.parseCreateTable(userCommand);
				}
				else if(tokens[1].equals("database")) {
					Query.createDatabase(tokens[2]);
				}
				else {
					System.out.println("Incorrect input. Refer to the help section");				
				}
				break;
			case "show":
				if(tokens[1].equals("tables")) {
					Query.showTables(userCommand);
				}
				else if(tokens[1].equals("databases")) {
					Query.showDatabases();
				}
				else {
					System.out.println("Incorrect input. Refer to the help section");				
				}
				break;
			case "select":
				Query.parseSelect(userCommand);
				break;
			case "drop":
				if(tokens[1].equals("table")) {
					String tableName = tokens[2].trim();
					if(!Query.isTableExists(tableName)) {
						System.out.println("Table " + tableName + " doesn't exist");
						break;
					}
					else {
						Query.dropTable(tableName);
					}
				}		
				break;
			case "insert":
				Query.parseInsert(userCommand);
				break;
			case "delete":
				Query.parseDelete(userCommand);
				break;
			case "update":
				Query.parseUpdate(userCommand);
				break;
			case "version":
				System.out.println();
				System.out.println("DavisBaseLite Version " + DatabaseConstants.VERSION);
				break;
			case "help":
				help();
				break;
			case "exit":
				System.out.println();
				break;
			default:
				System.out.println("Wrong user command " + userCommand);
				break;		
		}		
	}
    
    
    /*
	 *  Display the help menu
	 */
    
    public static void help() {
    	System.out.println(line("*",80));
		System.out.println("SUPPORTED COMMANDS\n");
		System.out.println("All commands below are case insensitive\n");
		System.out.println("SHOW TABLES;");
		System.out.println("\tDisplay the names of all tables.\n");
		System.out.println("SELECT <column_list> FROM <table_name> [WHERE <condition>];");
		System.out.println("\tDisplay table records whose optional <condition>");
		System.out.println("\tis <column_name> = <value>.\n");
		System.out.println("DROP TABLE <table_name>;");
		System.out.println("\tRemove table data (i.e. all records) and its schema.\n");
		System.out.println("UPDATE TABLE <table_name> SET <column_name> = <value> [WHERE <condition>];");
		System.out.println("\tModify records data whose optional <condition> is\n");
		System.out.println("VERSION;");
		System.out.println("\tDisplay the program version.\n");
		System.out.println("HELP;");
		System.out.println("\tDisplay this help information.\n");
		System.out.println("EXIT;");
		System.out.println("\tExit the program.\n");
		System.out.println(line("*",80));
    }

	 /*
	 *  Display the splash screen
	 */
    
	public static void splashScreen() {
		System.out.println(line("-",80));
        System.out.println("Welcome to DavisBaseLite"); // Display the string.
		System.out.println("DavisBaseLite Version " + DatabaseConstants.VERSION);
		System.out.println(DatabaseConstants.COPYRIGHT);
		System.out.println("\nType \"help;\" to display supported commands.");
		System.out.println(line("-",80));
	}
	
	
	 /* @param s The String to be repeated
	 *  @param num The number of time to repeat String s.
	 *  @return String A String object, which is the String s appended to itself num times.
	 */
	
	public static String line(String s, int num) {
		String a = "";
		for(int i = 0; i < num; i++) {
			a += s;
		}
		return a;
	}
}
