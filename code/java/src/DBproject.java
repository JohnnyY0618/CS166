/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;


/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		DBproject esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new DBproject (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Doctor");
				System.out.println("2. Add Patient");
				System.out.println("3. Add Appointment");
				System.out.println("4. Make an Appointment");
				System.out.println("5. List appointments of a given doctor");
				System.out.println("6. List all available appointments of a given department");
				System.out.println("7. List total number of different types of appointments per doctor in descending order");
				System.out.println("8. Find total number of patients per doctor with a given status");
				System.out.println("9. < EXIT");
				
				switch (readChoice()){
					case 1: AddDoctor(esql); break;
					case 2: AddPatient(esql); break;
					case 3: AddAppointment(esql); break;
					case 4: MakeAppointment(esql); break;
					case 5: ListAppointmentsOfDoctor(esql); break;
					case 6: ListAvailableAppointmentsOfDepartment(esql); break;
					case 7: ListStatusNumberOfAppointmentsPerDoctor(esql); break;
					case 8: FindPatientsCountWithStatus(esql); break;
					case 9: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

	public static void AddDoctor(DBproject esql) {//1
		int doctorID = -1; //  for doctor ID, -1 indicate input null
		String doctorName; // for reading user input of doctor name
		String doctorSpecialty; // for reading user input for doctor specialty
		int doctorDID = -1; // for reading use input for depart ID
		String last;
		String first;

		try {
			String query1 = "SELECT MAX(doctor_ID) FROM Doctor";
			doctorID = 1 + Integer.parseInt(esql.executeQueryAndReturnResult(query1).get(0).get(0));
			System.out.println("Doctor ID is automatically assigned: " + doctorID);;
		}catch (Exception e) {
			System.out.println(e);
		}


		do { // for reading user input for Doctor name and checking validation (2)
			System.out.print("Enter the Doctor first name: ");
			try {
				first = in.readLine();
				System.out.print("Enter the Doctor last name: ");
				last = in.readLine();

				doctorName = first + " " + last;
				if ( ( (first.length() <= 0) && (last.length() <= 0 ) ) || doctorName.length() > 128) {
					throw new RuntimeException("Doctor full Name cannot be null or exceed 128 characters");
				}
				else if (first.length() <= 0 || first.length() > 128) {
					throw new RuntimeException("Doctor first Name cannot be null or exceed 128 characters");
				}
				else if (last.length() <= 0 || last.length() > 128) {
					throw new RuntimeException("Doctor last Name cannot be null or exceed 128 characters");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}while (true);

		do { // for reading user input for Doctor specialty and checking validation (3)
			System.out.print("Enter the Specialty for the Doctor: ");
			try {
				doctorSpecialty = in.readLine();
				if(doctorSpecialty.length() <= 0 || doctorSpecialty.length() > 24) {
					throw new RuntimeException("Doctor Specialty cannot be null or exceed 24 characters");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}while (true);

		do { // for reading user input for Doctor department ID and checking validation (4)
			System.out.print("Enter the Department  ID number: ");
			try {
				doctorDID = Integer.parseInt(in.readLine());
				if ( doctorDID <= -1 ) {
					throw new RuntimeException("Doctor Department ID cannot be null or negative!");
				}
				break;
			}catch (NumberFormatException e) {
				System.out.println("Your input is invalid! Note: Department ID is integer number.");
				continue;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}

		}while (true);

		try{ // after done for ask user input insert the value to the table
			String query = "INSERT INTO Doctor (doctor_ID, name, specialty, did) VALUES (" + doctorID + ",  \'" + doctorName + "\', \'" + doctorSpecialty + "\', " + doctorDID + ");";
			esql.executeUpdate(query);
		}catch(Exception e){
			System.err.println (e.getMessage());
		}

	}

	public static void AddPatient(DBproject esql) {//2
		int pID = -1; // for patient ID
		String pName; // for patinet name
		String gender; // for patient gender
		int age = -1; // for patient age
		String address; // for patient address
		int num_appts = 0; // new patient have make any appointment yet
		String last;
		String first;

		try {
			String query2 = "SELECT MAX(patient_ID) FROM Patient";
			pID = 1 + Integer.parseInt(esql.executeQueryAndReturnResult(query2).get(0).get(0));
			System.out.println("Patient ID is automatically assigned: " + pID);
		}catch (Exception e) {
			System.out.println(e);
		}

		do { // for reading user input for Patient name and checking validation
			System.out.print("Enter the Patient first name: ");
			try {
				first = in.readLine();
				System.out.print("Enter the Patient last name: ");
				last = in.readLine();
				pName = first + " " + last;
				if( ( (first.length() <= 0) && (last.length() <= 0) ) || pName.length() > 128) {
					throw new RuntimeException("Patient full Name cannot be null or exceed 128 characters");
				}
				else if (first.length() <= 0 || first.length() > 128) {
					throw new RuntimeException("Patient first Name cannot be null or exceed 128 characters");
				}
				else if (last.length() <= 0 || last.length() > 128) {
					throw new RuntimeException("Patient last Name cannot be null or exceed 128 characters");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}while (true);


		do { // for reading user input for Patient gender and checking validation
			System.out.print("Enter the Patient's gender: ");
			try {
				gender = in.readLine().toUpperCase();
				if (gender.length() <= 0) {
					throw new RuntimeException("Patient gender cannot be null!");
				}
				else if( !(gender.equals("M")) && !(gender.equals("F")) ) {
					throw new RuntimeException("Please enter M for male and F for female!");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}while (true);

		do { // for reading use input for Patient age and checking validation
			System.out.print("Enter the Patient age: ");
			try {
				age = Integer.parseInt(in.readLine());
				if ( age <= -1 ) {
					throw new RuntimeException("Patient age cannot be null or negative!");
				}
				break;
			}catch (NumberFormatException e) {
				System.out.println("Your input is invalid! Note: Patient age is integer number.");
				continue;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}while (true);

		do { // for reading user input for Patient address and checking validation
			System.out.print("Enter the Patient address: ");
			try {
				address = in.readLine();
				if(address.length() <= 0 || address.length() > 256) {
					throw new RuntimeException("Patient address cannot be null or exceed 256 characters");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}while (true);

		try{ // after done for ask user input insert the value to the table
			String query = "INSERT INTO Patient (patient_ID, name, gtype, age, address, number_of_appts) VALUES (" + pID + ",  \'" + pName + "\', \'" + gender + "\', " + age + ", \'" + address + "\', " + num_appts + " );";
			esql.executeUpdate(query);
		}catch(Exception e){
			System.err.println (e.getMessage());
		}
	}

	public static void AddAppointment(DBproject esql) {//3
		int appntID = -1;
		String date;
		String timeSlot;
		String status;

		try {
			String query3 = "SELECT MAX(appnt_ID) FROM Appointment";
			appntID = 1 + Integer.parseInt(esql.executeQueryAndReturnResult(query3).get(0).get(0));
			System.out.println("Patient ID is automatically assigned: " + appntID);
		}catch (Exception e) {
			System.out.println(e);
		}

		do {
			System.out.print("Enter Appointment Date (mm/dd/yyyy): ");
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
				date = in.readLine();
				Date test = dateFormat.parse(date);
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid! (Hint: mm/dd/yyyy)");
				continue;
			}
		}while (true);

		do {
			System.out.print("Enter Appointment time slot (HH:mm-HH:mm): ");
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm-HH:mm");
				timeSlot = in.readLine();
				Date test = dateFormat.parse(timeSlot);
				if(timeSlot.length() <= 0 || timeSlot.length() > 11) {
					throw new RuntimeException("Time slot cannot be null or exceed 11 characters");
				}
				break;
			}catch (ParseException e) {
				System.out.println("Your input is invalid! (Hint: HH:mm-HH:mm)");
				continue;
			}
			catch (Exception e1) {
				System.out.println(e1);
				continue;
			}
		}while (true);

		do {
			System.out.print("Enter Appointment Status: ");
			try {
				status = in.readLine().toUpperCase();
				if (status.length() <= 0) {
					throw new RuntimeException("Patient gender cannot be null!");
				}
				else if( !(status.equals("AV")) && !(status.equals("AC")) && !(status.equals("WL")) && !(status.equals("PA")) ) {
					throw new RuntimeException("You input is invalid! (Hint: Appintment Type: AV(available), AC(Active), WL(Waitlisted), PA(Past))");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}while (true);

		try{ // after done for ask user input insert the value to the table
			String query = "INSERT INTO Appointment (appnt_ID, adate, time_slot, status) VALUES (" + appntID + ",  \'" + date + "\', \'" + timeSlot + "\', \'" + status + "\');";
			esql.executeUpdate(query);
		}catch(Exception e){
			System.err.println (e.getMessage());
		}

	}

	public static void MakeAppointment(DBproject esql) {//4
		// Given a patient, a doctor and an appointment of the doctor that s/he wants to take, add an appointment to the DB
		int pID = -1;
		int doctorID = -1;
		int aID = 1;
		String status = "";
		String inputOption; // for use input option
		int hid = -1;

		do {
			System.out.print("Enter the Patient ID number: ");
			try {
				pID = Integer.parseInt(in.readLine());
				if ( pID <= -1 ) {
					throw new RuntimeException("Patient ID cannot be null or negative!");
				}
				else {
					String query1 = "SELECT MAX(patient_ID) FROM Patient";
					int max =  Integer.parseInt(esql.executeQueryAndReturnResult(query1).get(0).get(0));
					if (pID > max) {
						throw new RuntimeException("Patient ID does not exist!");
					}
				}
				break;
			}catch (NumberFormatException e) {
				System.out.println("Your input is invalid! Note: ID is integer number.");
				continue;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}while (true);
		do {
			System.out.print("Enter the Docotr ID number: ");
			try {
				doctorID = Integer.parseInt(in.readLine());
				if ( doctorID <= -1 ) {
					throw new RuntimeException("Doctor ID cannot be null or negative!");
				}
				else {
					String query1 = "SELECT MAX(doctor_ID) FROM Doctor";
					int max = Integer.parseInt(esql.executeQueryAndReturnResult(query1).get(0).get(0));
					if (doctorID > max) {
						throw new RuntimeException("Doctor ID does not exist!");
					}
				}
				break;
			}catch (NumberFormatException e) {
				System.out.println("Your input is invalid! Note: ID is integer number.");
				continue;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}while (true);
		do {
			System.out.print("Enter the Appointment ID number: ");
			try {
				aID = Integer.parseInt(in.readLine());
				if ( aID <= -1 ) {
					throw new RuntimeException("Appointment ID cannot be null or negative!");
				}
				else {
					String query1 = "SELECT MAX(appnt_ID) FROM Appointment";
					int max = Integer.parseInt(esql.executeQueryAndReturnResult(query1).get(0).get(0));
					if (aID > max) {
						throw new RuntimeException("Appointment ID does not exist!");
					}
				}
				break;
			}catch (NumberFormatException e) {
				System.out.println("Your input is invalid! Note: ID is integer number.");
				continue;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}while (true);

		try {
			String query = "SELECT A.status FROM Appointment A WHERE A.appnt_ID = " + aID +";";
			String curr = esql.executeQueryAndReturnResult(query).get(0).get(0);
			if (!curr.equals("PA")) {
				if (curr.equals("AV")) {
					status = "AC";
					System.out.println("Appointment current status: Available ");
				}
				else if (curr.equals("AC")) {
					status = "WL";
					System.out.println("Appointment current status: Active");
				}
				else if (curr.equals("WL")) {
					status = "WL";
					System.out.println("Appointment current status: Waitlisted");
				}
				String qhid = "SELECT DP.hid FROM Doctor D, Department DP WHERE D.did = DP.dept_ID AND D.doctor_ID = " + doctorID + ";";
				hid = Integer.parseInt(esql.executeQueryAndReturnResult(qhid).get(0).get(0));
				String query1 = "INSERT INTO has_appointment (appt_id, doctor_id) VALUES (" + aID + ", " + doctorID + ");";
				String query2 = "INSERT INTO searches (hid, pid,aid) VALUES (" + hid + ", " + pID + ", " + aID + ");";
				esql.executeUpdate(query1);
				esql.executeUpdate(query2);
				String query3 = "UPDATE Appointment SET status = \'" + status + "\' WHERE appnt_ID = " + aID + ";";
				esql.executeUpdate(query3);
				System.out.println("You appoinment is being make!");
				String nums = "SELECT number_of_appts FROM Patient WHERE patient_ID = " + pID  + ";";
				int num_appt = 1 + Integer.parseInt(esql.executeQueryAndReturnResult(nums).get(0).get(0));
				System.out.println(num_appt);
				String query4 = "UPDATE Patient SET number_of_appts = " + num_appt  + ";";
				esql.executeUpdate(query4);
			}
			else {
				System.out.println("Appointment status is past or does not exist!");
			}
		}catch (Exception e) {
			System.err.println (e.getMessage());
		}

	}

	public static void ListAppointmentsOfDoctor(DBproject esql) {//5
		// For a doctor ID and a date range, find the list of active and available appointments of the doctor
		int doctorID = -1;
		String date1;
		String date2;

		do {
			System.out.print("Enter the Docotr ID number: ");
			try {
				doctorID = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid! Note: ID is integer number.");
				continue;
			}
		}while (true);
		do {
			System.out.print("Enter Date Range, Begin (mm/dd/yyyy): ");
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
				date1 = in.readLine();
				Date test = dateFormat.parse(date1);
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid! (Hint: mm/dd/yyyy)");
				continue;
			}
		}while (true);
		do {
			System.out.print("Enter Date Range, End (mm/dd/yyyy): ");
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
				date2 = in.readLine();
				Date test = dateFormat.parse(date2);
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid! (Hint: mm/dd/yyyy)");
				continue;
			}
		}while (true);

		try {
			String query = "SELECT  A.appnt_ID, A.adate, A.time_slot, A.status FROM Appointment A, has_appointment H WHERE H.appt_id = A.appnt_ID AND (A.status = 'AC' OR A.status = 'AV') AND H.doctor_id = " + doctorID + " AND (A.adate >=  \'" + date1 + "\' AND A.adate <= \'" + date2 + "\' );";

			if(esql.executeQueryAndPrintResult(query) == 0) {
				System.out.println("Doctor's active and available Appointment does not exist in the given date range:" + date1 + " -- " + date2);
			}
		}catch (Exception e) {
			System.err.println (e.getMessage());
		}

	}

	public static void ListAvailableAppointmentsOfDepartment(DBproject esql) {//6
		// For a department name and a specific date, find the list of available appointments of the department
		String deptName;
		String date;
		do { // for reading user input for Department name and checking validation
			System.out.print("Enter the Department name: ");
			try {
				deptName = in.readLine();
				if(deptName.length() <= 0 || deptName.length() > 32) {
					throw new RuntimeException("Patient Name cannot be null or exceed 32 characters");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}while (true);

		do {
			System.out.print("Enter the Date (mm/dd/yyyy): ");
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
				date = in.readLine();
				Date test = dateFormat.parse(date);//int month = Integer.parseInt(date.substring(0,1));//int day = Integer.parseInt(date.)
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid! (Hint: mm/dd/yyyy)");
				continue;
			}
		}while (true);
		try {
			String query = "SELECT DISTINCT A.appnt_ID, A.adate, A.time_slot, A.status FROM Appointment A, searches S, Department DP WHERE A.appnt_ID = S.aid AND S.hid = DP.hid AND A.status ='AV' AND DP.name = \'" + deptName +"\' AND A.adate = \'" + date + "\';";

			if(esql.executeQueryAndPrintResult(query) == 0) {
				System.out.println("The Department of available Appointment for the given date do not exist!");
			}
		}catch (Exception e) {
			System.err.println (e.getMessage());
		}

	}

	public static void ListStatusNumberOfAppointmentsPerDoctor(DBproject esql) {//7
		// Count number of different types of appointments per doctors and list them in descending order
		try {
			String query = "SELECT D.doctor_ID, D.name, A.status, COUNT(A.status) AS Total FROM Doctor D, Appointment A, has_appointment H WHERE D.doctor_ID = H.doctor_id AND H.appt_id = A.appnt_ID GROUP BY D.doctor_ID, A.status ORDER BY D.doctor_ID ASC,  Total DESC;";
			if(esql.executeQueryAndPrintResult(query) == 0) {
				System.out.println("The query do not exist!");
			}
		}catch (Exception e) {
			System.err.println (e.getMessage());
		}
	}

	
	public static void FindPatientsCountWithStatus(DBproject esql) {//8
		// Find how many patients per doctor there are with a given status (i.e. PA, AC, AV, WL) and list that number per doctor.
		String status;
		do {
			System.out.print("Enter Appointment Status: ");
			try {
				status = in.readLine().toUpperCase();
				if (status.length() <= 0) {
					throw new RuntimeException("Patient gender cannot be null!");
				} else if (!(status.equals("AV")) && !(status.equals("AC")) && !(status.equals("WL")) && !(status.equals("PA"))) {
					throw new RuntimeException("You input is invalid! (Hint: Appintment Type: AV(available), AC(Active), WL(Waitlisted), PA(Past))");
				}
				break;
			} catch (Exception e) {
				System.out.println(e);
				continue;
			}
		} while (true);
		try {
			String query = "SELECT D.doctor_id, D.name, COUNT(S.pid) AS Number_Of_Patients FROM Doctor D, Appointment A, has_appointment H, searches S WHERE D.doctor_ID = H.doctor_id AND A.appnt_ID = H.appt_id AND A.appnt_ID = S.aid AND A.status = \'" + status + "\' GROUP BY D.doctor_id ORDER BY Number_Of_Patients DESC;";
			if(esql.executeQueryAndPrintResult(query) == 0) {
				System.out.println("The query do not exist!");
			}
		}catch (Exception e) {
			System.err.println (e.getMessage());
		}
	}
}
