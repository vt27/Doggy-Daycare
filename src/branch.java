import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;
// for reading from the command line
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

// for the login window
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * This class implements a graphical login window and a simple text
 * interface for interacting with the branch table 
 */
public class branch implements ActionListener {

    public static String doggy_day_care_sql = "";
    // command line reader
    private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private Connection con;
    //
    // user is allowed 3 login attempts
    private int loginAttempts = 0;

    // components of the login window
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    private JFrame loginFrame;
    private JFrame mainFrame;
    
    /*
     * constructs login window and loads JDBC driver
     */
    public branch() {
        loginFrame = new JFrame("User Login");

        JLabel usernameLabel = new JLabel("Enter username: ");
        JLabel passwordLabel = new JLabel("Enter password: ");

        usernameLabel.setFont(new Font("Serif", Font.PLAIN, 80));
        passwordLabel.setFont(new Font("Serif", Font.PLAIN, 80));

        usernameField = new JTextField(10);
        passwordField = new JPasswordField(10);
        passwordField.setEchoChar('*');

        JButton loginButton = new JButton("Log In");

        JPanel contentPane = new JPanel();
        loginFrame.setContentPane(contentPane);
        // mainFrame.setContentPane(mainContentPanel);

        // layout components using the GridBag layout manager
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        contentPane.setLayout(gb);
        contentPane.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));

        // place the username label
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.insets = new Insets(10, 10, 5, 0);
        gb.setConstraints(usernameLabel, c);
        contentPane.add(usernameLabel);

        // place the text field for the username
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(10, 0, 5, 10);
        gb.setConstraints(usernameField, c);
        contentPane.add(usernameField);

        // place password label
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.insets = new Insets(0, 10, 10, 0);
        gb.setConstraints(passwordLabel, c);
        contentPane.add(passwordLabel);

        // place the password field
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(0, 0, 10, 10);
        gb.setConstraints(passwordField, c);
        contentPane.add(passwordField);

        // place the login button
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(5, 10, 10, 10);
        c.anchor = GridBagConstraints.CENTER;
        gb.setConstraints(loginButton, c);
        contentPane.add(loginButton);

        // register password field and OK button with action event handler
        passwordField.addActionListener(this);
        loginButton.addActionListener(this);

        // anonymous inner class for closing the window
        loginFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // size the window to obtain a best fit for the components
        loginFrame.pack();

        // center the frame
        Dimension d = loginFrame.getToolkit().getScreenSize();
        Rectangle r = loginFrame.getBounds();
        loginFrame.setLocation((d.width - r.width) / 2, (d.height - r.height) / 2);
        // make the window visible
        loginFrame.setVisible(true);

        // place the cursor in the text field for the username
        usernameField.requestFocus();

        try {
            // Load the Oracle JDBC driver
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
            System.exit(-1);
        }
    }

    public void runSqlScript() {
        oraDebugging();
        dropTables();
        String workingDir = System.getProperty("user.dir");
        String path = workingDir + "/src/Doggy Day Care.sql";
        System.out.println(path);
        Scanner scan;
        try {
            scan = new Scanner(new File(path));
            scan.useDelimiter(Pattern.compile(";"));
            while (scan.hasNext()) {
                String sqlLine = scan.next();
                System.out.println(sqlLine);
                executeSql(sqlLine);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void oraDebugging() {
        Statement statem;
        try {
            statem = con.createStatement();
            statem.executeUpdate("alter session set \"_optimizer_join_elimination_enabled\" = false;");
            statem.executeUpdate("alter session set \"_optimizer_native_full_outer_join\"=off;");
        } catch (SQLException e) {
        }
    }

    private void dropTables() {
        String workingDir = System.getProperty("user.dir");
        String path = workingDir + "/src/Drop Table.sql";
        System.out.println(path);
        Scanner scan;
        try {
            scan = new Scanner(new File(path));
            scan.useDelimiter(Pattern.compile(";"));
            while (scan.hasNext()) {
                String sqlLine = scan.next();

                System.out.println(sqlLine);
                try {
                    executeSql(sqlLine);
                } catch (SQLException e) {
                }
            }
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private void executeSql(String sqlLine) throws SQLException {
        Statement statem;
        statem = con.createStatement();
        statem.executeUpdate(sqlLine);
    }
    
    /*
     * connects to Oracle database named ug using user supplied username and
     * password
     */
    public boolean connect(String username, String password) {
        String connectURL = "jdbc:oracle:thin:@localhost:1522:ug";

        try {
			 con = DriverManager.getConnection(connectURL,username,password);

            System.out.println("\nConnected to Oracle!");
            return true;
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
            return false;
        }
    }

    // daniel global variables being used
    boolean a = false;
    String cid = "1";
    String eid = "a";
    
    private void mainMenu() throws Exception 
    {

        System.out.print("If you are a customer, press 1\n");
        System.out.print("If you are an employee, press 2\n");
        System.out.print("Press 3 to Run Script\n");

        int choice= 0;
        try {
            choice = Integer.parseInt(in.readLine());
        } catch (NumberFormatException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        switch(choice)
        {
        case 0: break;
        case 1: customerMainMenu("1"); break;
        case 2: employeeMainMenu("1");break;
        case 3: runSqlScript();
                mainMenu();
                break;

        }
    }


    // employeeMainMenu checks validity of employeeID 
        public boolean employeeMainMenu(String id) 
    {
        PreparedStatement ps;
        ResultSet rs;

        if (a == false) {
            try {
                ps = con.prepareStatement("SELECT E_ID FROM employee_manages WHERE E_ID = ?");
                ps.setString(1, id);
                rs = ps.executeQuery();
                manager();
                if (!rs.next()) {
                    return false; 
                } 
            } catch (Exception e1) {
                return false; 
            }

        }
        return true;

    }

    public Employee getEmployeeProfile(int eid){
        return queryEmployeeProfileFromDB(eid); 
    }
    
    public Employee queryEmployeeProfileFromDB(int eid){
        
        String e_name = null;
        int managedBy = 0;
        int m_id = 0;
        boolean isManager = false;
        
        Statement stmt;
        ResultSet rs;
        
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM employee_manages WHERE e_id=" + eid);
            
            rs.next();
            e_name = rs.getString("e_name");
            if(rs.getString("m_id") != null) {
                managedBy = rs.getInt("m_id");
            }
            
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM full_time WHERE e_id=" + eid);
            
            rs.next();
            if(rs.getString("m_id") != null) {
                m_id = rs.getInt("m_id");
                isManager = true;
            }
            
        } catch (SQLException e) {
            System.out.println("Wrong ID");
            e.printStackTrace();
        }
        
        Employee emp = new Employee(eid,e_name);
        
        emp.setMID(m_id);
//		emp.setManagedBy(managedBy);
        
//		if(isManager) emp.setAsManager();
        
        try {
            ArrayList<EmployeeSchedule> schedule = getSchedule(eid);
            System.out.println(schedule.size());
            emp.newSchedule(schedule);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            
        }
        
        
        return emp;
        
    }


    //GUI VERSION!!
    public boolean customerMainMenu(String id) 
    {
        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = con.prepareStatement("SELECT C_ID FROM customer WHERE C_ID = ?"); 
            ps.setString(1, id);
            rs = ps.executeQuery();
            update();
            if (!rs.next()) {
                return false; 
            } 
        } catch (Exception e1) {
            return false; 
        }
        return true;

    }
    
    

    // Update daniel
    private void update() {

        int choice;
        boolean quit;

        quit = false;

        try {
            // disable auto commit mode
            con.setAutoCommit(false);

            while (!quit) {
                System.out.print("\n1.  see Profile\n");
                System.out.print("2.  Update Customer\n");
                System.out.print("3.  add dogs\n");
                System.out.print("4.  delete dogs\n");
                // System.out.print("5. show dogs\n");
                System.out.print("9.  Back\n");
                System.out.print("0.  Quit\n>>");

                choice = Integer.parseInt(in.readLine());

                System.out.println(" ");

                switch (choice) {
                case 1:
                    seeProfile();
                    break;
                case 2:
                    updateCustomer();
                    break;
                case 3:
                    addDogs();
                    break;
                case 4:
                    deleteDogs();
                    break;
                case 9:
//					customerMainMenu();
                    break;
                case 0:
                    a = false;
                    quit = true;

                }
            }

            con.close();
            in.close();
            System.out.println("\nGood Bye!\n\n");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("IOException!");

            try {
                con.close();
                System.exit(-1);
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
        }
    }
    

    // daniel manager section

    private void manager() {

        int choice;
        boolean quit;

        quit = false;

        try {
            // disable auto commit mode
            con.setAutoCommit(false);

            while (!quit) {

                System.out.print("1.  Manage Customer\n");
                System.out.print("2.  Manage Products\n");
                System.out.print("3.  MANEGER SECTION\n");
                System.out.print("4.  Back\n");
                System.out.print("5.  Quit\n");

                choice = Integer.parseInt(in.readLine());

                System.out.println(" ");

                switch (choice) {
                case 1:
                    manageCustomer();
                    break;
                case 2:
                    manageProducts();
                    break;
                case 3:
                    manageManeger();
                    break;
                case 4:
                    a = false;
//					mainMenu();
                    break;
                case 5:
                    a = false;
                    quit = true;
                }
            }

            con.close();
            in.close();
            System.out.println("\nGood Bye!\n\n");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("IOException!");

            try {
                con.close();
                System.exit(-1);
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
        }
    }

    // daniel manageCustomer

    private void manageCustomer() {

        int choice;
        boolean quit;

        quit = false;

        try {
            // disable auto commit mode
            con.setAutoCommit(false);

            while (!quit) {
                System.out.print("\n1. Delete Reservation\n");
                System.out.print("2.  Add New Customer\n");
                System.out.print("3.  Add dogs\n");
                System.out.print("4.  Delete dogs\n");
                System.out.println("5. Get Customer of Month");
                System.out.print("6.  Back\n");
                System.out.print("7.  Quit\n>>");

                choice = Integer.parseInt(in.readLine());

                System.out.println(" ");

                switch (choice) {
                // case 1: deleteReservation(); break;
                case 2:
                    insertCustomer();
                    break;
                case 3:
//					addDogs2();
                    break;
                case 4:
//					deleteDogs2();
                    break;
                case 5:
                    customerOfMonth();
                case 6:
                    break;
                case 7:
                    a = false;
                    quit = true;

                }
            }

            con.close();
            in.close();
            System.out.println("\nGood Bye!\n\n");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("IOException!");

            try {
                con.close();
                System.exit(-1);
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
        }
    }

    // daniel manageProducts

    private void manageProducts() {

        int choice;
        boolean quit;

        quit = false;

        try {
            // disable auto commit mode
            con.setAutoCommit(false);

            while (!quit) {
                System.out.print("\n1. Bought By All\n");
                System.out.print("2.  View Stock\n");
                System.out.print("3.  Order products\n");
                System.out.print("4.  Delete Buys\n");
                System.out.print("5.  Back\n");
                System.out.print("6.  Quit\n>>");

                choice = Integer.parseInt(in.readLine());

                System.out.println(" ");

                switch (choice) {
                case 1: customersThatBoughtAllProducts(); break;
                case 2:
                    viewStocks();
                    break;
                case 3:
                    insertStocks();
                    break;
                case 4:
                	deleteBuys();
                	break;                   
                case 5:
                    break;
                case 6:
                    a = false;
                    quit = true;

                }
            }

            con.close();
            in.close();
            System.out.println("\nGood Bye!\n\n");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("IOException!");

            try {
                con.close();
                System.exit(-1);
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
        }
    }

    // daniel manageManager
    private void manageManeger() {

        int choice;
        boolean quit;

        quit = false;

        try {
            // disable auto commit mode
            con.setAutoCommit(false);

            while (!quit) {
                System.out.print("\n1. Add Product\n");
                System.out.print("2.  Add Service\n");
                System.out.print("3.  Show Employee\n");
                System.out.print("4.  Add Employee\n");
                System.out.print("5.  Delete Employee\n");
                System.out.print("6.  Back\n");
                System.out.print("7.  Quit\n>>");

                choice = Integer.parseInt(in.readLine());

                System.out.println(" ");

                switch (choice) {
                // case 1: addProduct(); break;
                case 2:
                    insertService();
                    break;
                case 3:
                    showEmployee();
                    break;
                case 4:
                    insertEmployee();
                    break;
                case 5:
                    deleteEmployee();
                    break;
                case 6:
                    break;
                case 7:
                    a = false;
                    quit = true;

                }
            }

            con.close();
            in.close();
            System.out.println("\nGood Bye!\n\n");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("IOException!");

            try {
                con.close();
                System.exit(-1);
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
        }
    }
    
    
    
    
    
    ///////////////All the customer functions///////////  
    
    // daniel showDogs

        private void showDogs() {
            String d_id;
            String d_name;
            String breed;
            String age;
            String sex;
            Statement stmt;
            String c_id;
            ResultSet rs;

            String ps;

            try {
                stmt = con.createStatement();
                String c_cid = String.valueOf(cid);
                ps = "SELECT * FROM dog WHERE C_ID =" + c_cid;
                rs = stmt.executeQuery(ps);

                // get info on ResultSet
                ResultSetMetaData rsmd = rs.getMetaData();

                // get number of columns
                int numCols = rsmd.getColumnCount();

                System.out.println(" ");

                // display column names;
                for (int i = 0; i < numCols; i++) {
                    // get column name and print it

                    System.out.printf("%-15s", rsmd.getColumnName(i + 1));
                }

                System.out.println(" ");

                while (rs.next()) {
                    // for display purposes get everything from Oracle
                    // as a string

                    // simplified output formatting; truncation may occur

                    d_id = rs.getString("d_id");
                    System.out.printf("%-10.10s", d_id);

                    d_name = rs.getString("d_name");
                    System.out.printf("%-20.20s", d_name);

                    breed = rs.getString("breed");
                    System.out.printf("%-20.20s", breed);

                    age = rs.getString("age");
                    System.out.printf("%-10.10s", age);

                    sex = rs.getString("sex");
                    System.out.printf("%-20.20s", sex);

                    c_id = rs.getString("c_id");
                    System.out.printf("%-10.10s\n", c_id);

                }

                // close the statement;
                // the ResultSet will also be closed
                stmt.close();
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
            }
        }
        
        public ArrayList<Reservation> getAllReservations() 
        { 
            Statement stmt;
            ArrayList<Reservation> reservations = new ArrayList<Reservation>();
            try {
                stmt = con.createStatement();
                String c_cid = String.valueOf(cid);
                String ps = "SELECT * FROM res";
                ResultSet rs = stmt.executeQuery(ps);

                // get info on ResultSet
                ResultSetMetaData rsmd = rs.getMetaData();
                while (rs.next()) {
                    int confirmationNumber = Integer.parseInt(rs.getString("confirmation_no"));
                    String from_date = rs.getString("from_date");
                    String to_date = rs.getString("to_date");
                    int customerId = Integer.parseInt(rs.getString("c_id"));
                    System.out.println(confirmationNumber + " " + from_date + " " + customerId);
                    Reservation reservation = new Reservation(confirmationNumber, from_date, to_date, customerId);
                    reservations.add(reservation);
                } 
                return reservations;
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                return reservations;
            }
        }
        
        public Customer getCustomerProfile(int customerId) throws Exception
        {
            return queryCustomerProfileFromDB(customerId);
        }

        private Customer queryCustomerProfileFromDB(int customerId) throws Exception
        {
            Customer customer;
            
            String c_id = Integer.toString(customerId);
            String c_name;
            String email_address;
            String phone_no;
            String address;
            Statement stmt;
            ResultSet rs;

            String ps;

            try {
                stmt = con.createStatement();
                ps = "SELECT * FROM customer WHERE C_ID =" + c_id;
                rs = stmt.executeQuery(ps);

                // get info on ResultSet
                ResultSetMetaData rsmd = rs.getMetaData();
                while (rs.next()) {
                    // for display purposes get everything from Oracle
                    // as a string

                    // simplified output formatting; truncation may occur
                    c_id = rs.getString("c_id");
                    c_name = rs.getString("c_name");
                    email_address = rs.getString("email_address");
                    phone_no = rs.getString("phone_no");
                    address = rs.getString("address");
                    customer = new Customer(Integer.parseInt(c_id), c_name, email_address, phone_no, address);
                    ArrayList<Dog> dogs = queryCustomerDogs(Integer.parseInt(c_id));
                    customer.addDogs(dogs);
                    return customer;
                }
            } catch (SQLException ex) {
                throw new Exception("Something went wrong while retrieving your information");
            }
            return null;
        }
        
        private ArrayList<Dog> queryCustomerDogs(int customerId) throws Exception
        {
            int d_id;
            String d_name;
            String breed;
            int age;
            String sex;
            Statement stmt;
            ResultSet rs;

            String ps;
            ArrayList<Dog> dogs = new ArrayList<Dog>();

            try {
                stmt = con.createStatement();
                ps = "SELECT * FROM dog WHERE C_ID =" + Integer.toString(customerId);
                rs = stmt.executeQuery(ps);

                // get info on ResultSet
                ResultSetMetaData rsmd = rs.getMetaData();

                while (rs.next()) {
                    // for display purposes get everything from Oracle
                    // as a string

                    d_id = Integer.parseInt(rs.getString("d_id"));
                    d_name = rs.getString("d_name");
                    breed = rs.getString("breed");
                    age = Integer.parseInt(rs.getString("age"));
                    sex = rs.getString("sex");
                    // simplified output formatting; truncation may occur
                    Dog dog = new Dog(d_id, d_name, breed, age, sex);
                    dogs.add(dog);
                }

                // close the statement;
                // the ResultSet will also be closed
                stmt.close();
            } catch (SQLException ex) {
                throw new Exception("Error in retrieving dogs");
            }
            return dogs;
        }
    
    
    // daniel see profile

        private void seeProfile() {
            String c_id;
            String c_name;
            String email_address;
            String phone_no;
            String address;
            Statement stmt;
            ResultSet rs;

            String ps;

            try {
                stmt = con.createStatement();
                String c_cid = String.valueOf(cid);
                ps = "SELECT * FROM customer WHERE C_ID =" + c_cid;
                rs = stmt.executeQuery(ps);

                // get info on ResultSet
                ResultSetMetaData rsmd = rs.getMetaData();

                // get number of columns
                int numCols = rsmd.getColumnCount();

                System.out.println(" ");

                // display column names;
                for (int i = 0; i < numCols; i++) {
                    // get column name and print it

                    System.out.printf("%-15s", rsmd.getColumnName(i + 1));
                }

                System.out.println(" ");

                while (rs.next()) {
                    // for display purposes get everything from Oracle
                    // as a string

                    // simplified output formatting; truncation may occur

                    c_id = rs.getString("c_id");
                    System.out.printf("%-10.10s", c_id);

                    c_name = rs.getString("c_name");
                    System.out.printf("%-20.20s", c_name);

                    email_address = rs.getString("email_address");
                    if (rs.wasNull()) {
                        System.out.printf("%-20.20s", " ");
                    } else {
                        System.out.printf("%-20.20s", email_address);
                    }

                    phone_no = rs.getString("phone_no");
                    System.out.printf("%-15.15s", phone_no);

                    address = rs.getString("address");
                    if (rs.wasNull()) {
                        System.out.printf("%-15.15s\n", " ");
                    } else {
                        System.out.printf("%-15.15s\n", address);
                    }
                }

                // close the statement;
                // the ResultSet will also be closed
                stmt.close();
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
            }
            showDogs();
        }
        

        
        
        /*
         * updates the name of a customer daniel if you do not want to change, just
         * press enter.
         * 
         */
        // daniel to check if the string is empty or not
        public static boolean empty(final String s) {
            // Null-safe, short-circuit evaluation.
            return s == null || s.trim().isEmpty();
        }

        private void updateCustomer() {
            String c_name;
            String email_address;
            String phone_no;
            String address;

            PreparedStatement ps;

            try {
                ps = con.prepareStatement("UPDATE customer SET c_name = ? WHERE c_id = ?");
                int cidd = Integer.parseInt(cid);
                ps.setInt(2, cidd);

                System.out.print("\nCustomer Name: ");

                String choice = in.readLine();
                if (!empty(choice)) {

                    c_name = choice;
                    ps.setString(1, c_name);

                    int rowCount = ps.executeUpdate();
                    if (rowCount == 0) {
                        System.out.println("\nCustomer " + cid + " does not exist!");
                    }

                    con.commit();

                    ps.close();

                }

            } catch (IOException e) {
                System.out.println("IOException!");
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());

                try {
                    con.rollback();
                } catch (SQLException ex2) {
                    System.out.println("Message: " + ex2.getMessage());
                    System.exit(-1);
                }
            }

            // daniel email_address
            try {
                ps = con.prepareStatement("UPDATE customer SET email_address = ? WHERE c_id = ?");
                int cidd = Integer.parseInt(cid);
                ps.setInt(2, cidd);

                System.out.print("\nCustomer email_address: ");

                String choice = in.readLine();
                if (!empty(choice)) {

                    c_name = choice;
                    ps.setString(1, c_name);
                    email_address = choice;
                    ps.setString(1, email_address);

                    int rowCount = ps.executeUpdate();
                    if (rowCount == 0) {
                        System.out.println("\nCustomer " + cid + " does not exist!");
                    }

                    con.commit();

                    ps.close();

                }

            } catch (IOException e) {
                System.out.println("IOException!");
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());

                try {
                    con.rollback();
                } catch (SQLException ex2) {
                    System.out.println("Message: " + ex2.getMessage());
                    System.exit(-1);
                }
            }

            // daniel phone_no
            try {
                ps = con.prepareStatement("UPDATE customer SET phone_no = ? WHERE c_id = ?");
                int cidd = Integer.parseInt(cid);
                ps.setInt(2, cidd);

                System.out.print("\nCustomer phone_no: ");

                String choice = in.readLine();
                if (!empty(choice)) {

                    c_name = choice;
                    ps.setString(1, c_name);
                    phone_no = choice;
                    ps.setString(1, phone_no);

                    int rowCount = ps.executeUpdate();
                    if (rowCount == 0) {
                        System.out.println("\nCustomer " + cid + " does not exist!");
                    }

                    con.commit();

                    ps.close();

                }

            } catch (IOException e) {
                System.out.println("IOException!");
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());

                try {
                    con.rollback();
                } catch (SQLException ex2) {
                    System.out.println("Message: " + ex2.getMessage());
                    System.exit(-1);
                }
            }

            // daniel address
            try {
                ps = con.prepareStatement("UPDATE customer SET address = ? WHERE c_id = ?");
                int cidd = Integer.parseInt(cid);
                ps.setInt(2, cidd);

                System.out.print("\nCustomer address: ");

                String choice = in.readLine();
                if (!empty(choice)) {

                    c_name = choice;
                    ps.setString(1, c_name);
                    address = choice;
                    ps.setString(1, address);

                    int rowCount = ps.executeUpdate();
                    if (rowCount == 0) {
                        System.out.println("\nCustomer " + cid + " does not exist!");
                    }

                    con.commit();

                    ps.close();

                }

            } catch (IOException e) {
                System.out.println("IOException!");
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());

                try {
                    con.rollback();
                } catch (SQLException ex2) {
                    System.out.println("Message: " + ex2.getMessage());
                    System.exit(-1);
                }
            }

        }
        
        public void customerAddDog(int customerId, Dog dog) throws Exception {
            addDogs(customerId, dog.name, dog.breed, dog.age, dog.gender);
        }

        private void addDogs(int customerId, String dogName, String dogBreed, int dogAge, String dogGender) throws Exception {
            int d_id = 0;

            PreparedStatement ps;
            PreparedStatement ps2;

            try {
                ps = con.prepareStatement("INSERT INTO dog VALUES (?,?,?,?,?,?)");

                // daniel getting a random d_id from 1 to 1000 and with no
                // duplicates
                ResultSet rs;

                try {
                    ps2 = con.prepareStatement("SELECT D_ID FROM dog WHERE D_ID = ?");

                    d_id = branch.getRandomNumberFrom(1, 10000);
                    ps2.setInt(1, d_id);

                    rs = ps2.executeQuery();
                    if (rs.next()) {
                        addDogs();
                    }

                } catch (Exception e1) {
                    System.out.print("\nWrong Id, please type again");
                }

                ps.setInt(1, d_id);
                ps.setString(2, dogName);
                ps.setString(3, dogBreed);
                ps.setInt(4, dogAge);
                ps.setString(5, dogGender);
                ps.setInt(6, customerId);
                ps.executeUpdate();

                // commit work
                con.commit();

                ps.close();
            } catch (SQLException ex) {
                try {
                    // undo the insert
                    con.rollback();
                    throw new Exception("One or more of the fields you inputted is incorrect");
                } catch (SQLException ex2) {
                    throw new Exception("One or more of the fields you inputted is incorrect and rollback was not possible");
                }
            }
        }
        
        public void customerUpdateProfile(int customerId, Customer customer) throws Exception {
            updateCustomer(customerId, customer.getName(), customer.getEmailAddress(), customer.getPhoneNumber(), customer.getAddress());
        }
        
        //Called by customer
        //Update customer with parameters 
        private void updateCustomer(int customerId, String customerName, String emailAddress,
                                    String phoneNumber, String address) throws Exception {

            PreparedStatement ps;

            try {
                ps = con.prepareStatement("UPDATE customer SET c_name=?, email_address = ?, phone_no = ?, address = ? WHERE c_id = ?");
                ps.setInt(5, customerId);
                
                if (!empty(customerName) && !empty(emailAddress) && !empty(phoneNumber)) {
                    ps.setString(1, customerName);
                    ps.setString(2, emailAddress);
                    ps.setString(3, phoneNumber);
                    ps.setString(4, address);
                    int rowCount = ps.executeUpdate();
                    if (rowCount == 0) {
                        throw new Exception("Customer with id:" + customerId + " does not exist in database");
                    }
                con.commit();
                ps.close();
                }

            } catch (SQLException ex) {
                try {
                    con.rollback();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    throw e;
                }
            }
        }
        
        // daniel addDogs
        private void addDogs() {
            int d_id;
            
            String d_name;
            String breed;
            int age = 0;
            String sex;
            
            Statement stmt;

            PreparedStatement ps;
            PreparedStatement ps2;
            PreparedStatement ps3;
            
            ResultSet rs1;


            try {
                ps = con.prepareStatement("INSERT INTO dog VALUES (?,?,?,?,?,?)");

                //Query highest d_id and increment by 1
                stmt = con.createStatement();
                rs1 = stmt.executeQuery("select d_id from (select d_id from dog order by d_id desc) where rownum=1");
                rs1.next();
                d_id = rs1.getInt("d_id");

                ps.setInt(1, ++d_id);
                

                System.out.print("\nDog Name: ");
                d_name = in.readLine();
                ps.setString(2, d_name);

                System.out.print("\nDog Breed: ");
                breed = in.readLine();
                ps.setString(3, breed);

                System.out.print("\nDog Age: ");
                try {
                    age = Integer.parseInt(in.readLine());
                } catch (NumberFormatException e) {
                    System.out.print("\n It has to be in number. Ex)5 years old = 5 ");
                    update();
                    e.printStackTrace();
                }
                ps.setInt(4, age);

                System.out.print("\nDog Sex: ");
                sex = in.readLine();
                ps.setString(5, sex);

                // daniel cid from the global variable and needs to be changed from
                // string to int
                int cidd = Integer.parseInt(cid);
                ps.setInt(6, cidd);

                ps.executeUpdate();

                // commit work
                con.commit();

                ps.close();
            } catch (IOException e) {
                System.out.println("IOException!");
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
                try {
                    // undo the insert
                    con.rollback();
                } catch (SQLException ex2) {
                    System.out.println("Message: " + ex2.getMessage());
                    System.exit(-1);
                }
            }
        }
        
        //Insert dogs with parameters
        private void addDogs(String d_name,String breed,int age,String sex) {
            int d_id;
            
            Statement stmt;
            PreparedStatement ps = null;
            ResultSet rs1;

            try {

                //Query highest d_id and increment by 1
                stmt = con.createStatement();
                rs1 = stmt.executeQuery("select d_id from (select d_id from dog order by d_id desc) where rownum=1");
                rs1.next();
                d_id = rs1.getInt("d_id");
                
                int cidd = Integer.parseInt(cid);
                ps.setInt(1, ++d_id);
                ps.setString(2, d_name);
                ps.setString(3, breed);
                ps.setInt(4, age);
                ps.setString(5, sex);
                ps.setInt(6, cidd);

                ps.executeUpdate();

                // commit work
                con.commit();

                ps.close();
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
                try {
                    // undo the insert
                    con.rollback();
                } catch (SQLException ex2) {
                    System.out.println("Message: " + ex2.getMessage());
                    System.exit(-1);
                }
            }
        }
        
        public void deleteDog(int customerId, String dogName) throws Exception
        {
            deleteDogs(customerId, dogName);
        }

        private void deleteDogs(int customerId, String dogName) throws Exception{
            PreparedStatement ps;

            try {
                ps = con.prepareStatement("DELETE FROM dog WHERE c_id = ? AND d_name = ?");
                ps.setInt(1, customerId);
                ps.setString(2, dogName);

                int rowCount = ps.executeUpdate();

                if (rowCount == 0) {
                    throw new Exception("Dog with that name does not exist");
                }

                con.commit();

                ps.close();
            } catch (SQLException ex) {
                try {
                    con.rollback();
                } catch (SQLException rollbackException) {
                    throw rollbackException;
                }
                throw ex;
            }
        }
        
        // daniel delete dogs

        private void deleteDogs() {
            String d_name;
            PreparedStatement ps;

            try {
                ps = con.prepareStatement("DELETE FROM dog WHERE c_id = ? AND d_name = ?");

                int cidd = Integer.parseInt(cid);
                ps.setInt(1, cidd);

                System.out.print("\nDog Name: ");
                d_name = in.readLine();
                ps.setString(2, d_name);

                int rowCount = ps.executeUpdate();

                if (rowCount == 0) {
                    System.out.println("\nDog " + d_name + " does not exist!");
                }

                con.commit();

                ps.close();
            } catch (IOException e) {
                System.out.println("IOException!");
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());

                try {
                    con.rollback();
                } catch (SQLException ex2) {
                    System.out.println("Message: " + ex2.getMessage());
                    System.exit(-1);
                }
            }
        }

        //View all this user's reservations
        public ArrayList<Reservation> viewReservations(int customerId) throws Exception{
            Statement stmt;
            ResultSet rs;
            
            Statement stmt1;
            ResultSet rs1;
            
            Statement stmt2;
            ResultSet rs2;
            
            String conf_no;
            String from_date;
            String to_date;
            String s_id;
            String c_id;
            String d_id;
            
            ArrayList<Reservation> reservations = new ArrayList<Reservation>();
            
            try {
                stmt = con.createStatement();
                rs = stmt.executeQuery("SELECT * FROM res r WHERE c_id='" + Integer.toString(customerId) + "'");
                System.out.println("query from res");			
                
                while(rs.next()) {
                    
                    conf_no = rs.getString("confirmation_no");
                    from_date = rs.getString("from_date");
                    to_date = rs.getString("to_date");
                    s_id = rs.getString("s_id");
                    
                    stmt1 = con.createStatement();
                    rs1 = stmt1.executeQuery("SELECT * FROM has_a_schedule s,(SELECT d_id FROM dog WHERE c_id='"+customerId+"') d WHERE sc_from='" +from_date+ "' AND d.d_id=s.d_id");
                    
                    String serviceType = "";
                    switch(s_id) {
                        case "1":
                            serviceType = "Dog Walking";
                            break;
                        case "2":
                            serviceType = "Grooming";
                            break;
                        case "3":
                            serviceType = "Boarding";
                            break;
                        case "4":
                            serviceType = "Dog Training";
                            break;
                        default:
                            serviceType = "All";
                            break;
                    }
                    while(rs1.next()) {
                        
                        d_id = rs1.getString("d_id");
                        
                        System.out.println("\nConfirmation Number: " + conf_no);
                        System.out.println("Drop-Off Date Time: " + from_date);
                        System.out.println("Pick-Up Date Time: " + to_date);
                        System.out.println("Service ID: " + s_id);
                        System.out.println("Dog ID: " + d_id);

                        
                        Reservation reservation = new Reservation(Integer.parseInt(d_id), from_date, to_date, serviceType, Integer.parseInt(conf_no));
                        reservations.add(reservation);
                    }
                    
                    
                    
                    System.out.println("done");
                    
                }
                return reservations;
            } catch (SQLException e) {
                throw e;
            }
        }
    
        public int seePoints(int customerId) throws Exception{

            String points;
            Statement stmt;
            ResultSet rs;

            String ps;

            try {
                stmt = con.createStatement();
                String c_cid = String.valueOf(cid);
                ps = "SELECT number_of_points FROM customer c, loyalty_member l WHERE l.C_ID =" + Integer.toString(customerId);
                rs = stmt.executeQuery(ps);

                if (!rs.next()) {
                    throw new Exception("Customer is not a member!");
                }

                // get info on ResultSet
                ResultSetMetaData rsmd = rs.getMetaData();

                if (rs.next()) {
                    // for display purposes get everything from Oracle
                    // as a string

                    // simplified output formatting; truncation may occur

                    points = rs.getString("number_of_points");
                    return Integer.parseInt(points);
                } 

                // close the statement;
                // the ResultSet will also be closed
                stmt.close();
                throw new Exception("No data was extracted");
            } catch (SQLException ex) {
                throw ex;
            }
        }
    
    // daniel see Points

        private void seePoints() {

            String points;
            Statement stmt;
            ResultSet rs;

            String ps;

            try {
                stmt = con.createStatement();
                String c_cid = String.valueOf(cid);
                ps = "SELECT number_of_points FROM customer c, loyalty_member l WHERE l.C_ID =" + c_cid;
                rs = stmt.executeQuery(ps);

                if (!rs.next()) {
                    System.out.println("\nYou're not a member\n");
                    System.out.println("Would like to sing up? Y/N");
                    try {
                        String foo = in.readLine();
                        String foo2 = "y";
                        if (foo.equals(foo2)) {
                            signUp();

                        } else {
                            // showMenu();
                        }
                    } catch (IOException e) {
                        // showMenu();
                        e.printStackTrace();
                    }

                }

                // get info on ResultSet
                ResultSetMetaData rsmd = rs.getMetaData();

                // get number of columns
                int numCols = rsmd.getColumnCount();

                System.out.println(" ");

                // display column names;
                for (int i = 0; i < numCols; i++) {
                    // get column name and print it

                    System.out.printf("%-15s", rsmd.getColumnName(i + 1));
                }

                System.out.println(" ");

                if (rs.next()) {
                    // for display purposes get everything from Oracle
                    // as a string

                    // simplified output formatting; truncation may occur

                    points = rs.getString("number_of_points");
                    System.out.printf("%-10.10s", points);

                } else {
                    System.out.printf("\n You are not a loyalty member");
                }

                // close the statement;
                // the ResultSet will also be closed
                stmt.close();
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
            }
        }
        
        
        
        // daniel sign up 
        private void signUp() {

            int number_of_points = 0;

            PreparedStatement ps;

            try {
                ps = con.prepareStatement("INSERT INTO loyalty_member VALUES (?,?)");

                int cidd = Integer.parseInt(cid);
                ps.setInt(1, cidd);

                ps.setInt(2, number_of_points);

                ps.executeUpdate();

                // commit work
                con.commit();

                ps.close();
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
                try {
                    // undo the insert
                    con.rollback();
                } catch (SQLException ex2) {
                    System.out.println("Message: " + ex2.getMessage());
                    System.exit(-1);
                }
            }
        }

        private ArrayList<EmployeeSchedule> getSchedule(int employeeId) throws Exception 
        {
            Statement stmt;
            ResultSet rs;
            ArrayList<EmployeeSchedule> schedules = new ArrayList<EmployeeSchedule>();
            String ps;

            try {
                stmt = con.createStatement();
                String c_cid = 
                ps = "SELECT * FROM has_a_schedule WHERE e_id =" + String.valueOf(employeeId);
                rs = stmt.executeQuery(ps);
                
                // get info on ResultSet
                ResultSetMetaData rsmd = rs.getMetaData();

                if (rs.next()) {
                    // for display purposes get everything from Oracle
                    // as a string
                    // simplified output formatting; truncation may occur
                    EmployeeSchedule schedule = new EmployeeSchedule(rs.getString("sc_from"), rs.getString("sc_to"));
                    schedules.add(schedule);
                }
                stmt.close();
                return schedules;
            } catch (SQLException ex) {
                throw ex;
            }
        }

        // daniel seeRank

        public ArrayList<TopRankedLoyaltyMember> seeRank() throws Exception{
            Statement stmt;
            ResultSet rs;
            ArrayList<TopRankedLoyaltyMember> topRankedLoyaltyMembers = new ArrayList<TopRankedLoyaltyMember>();

            try {
                stmt = con.createStatement();

                rs = stmt.executeQuery(
                        "SELECT * FROM (SELECT c.c_name, c.phone_no, l.number_of_points FROM customer c, loyalty_member l WHERE c.c_id = l.c_id ORDER BY l.number_of_points DESC) WHERE ROWNUM <= 3");

                // get info on ResultSet
                ResultSetMetaData rsmd = rs.getMetaData();

                // get number of columns
                int numCols = rsmd.getColumnCount();

                while (rs.next()) {
                    // for display purposes get everything from Oracle
                    // as a string

                    // simplified output formatting; truncation may occur
                    TopRankedLoyaltyMember member = new TopRankedLoyaltyMember(rs.getString("c_name"), Integer.parseInt(rs.getString("number_of_points")));
                    topRankedLoyaltyMembers.add(member);
                }

                // close the statement;
                // the ResultSet will also be closed
                stmt.close();
                return topRankedLoyaltyMembers;
            } catch (SQLException ex) {
                throw ex;
            }
        }
        
        // new reserve and

//		private void reserve() {
//			int confirmation_no = 0;
//			
//			String year1;
//			String month1;
//			String day1;
//			String hour1;
//			String minute1;
//
//			String year3;
//			String month3;
//			String day3;
//			String hour3;
//			String minute3;
//			
//			String date1;
//			String date2 = "";
//			String date3;
//			String date4;
//			
//			String sc_from = null;
//			String sc_to = null;
//
//			Timestamp sc_from2 = null;
//			Timestamp sc_to2 = null;
//
//			String type;
//
//			int s_id = 0;
//			int e_id = 0;
//			int sc_id = 0;
//			int d_id = 0;
//			
//			String d_name = "";
//
//			int duration = 0;
//
//			ResultSet rs1;
//			ResultSet rs2;
//			ResultSet rs3;
//			ResultSet rs4;
//			ResultSet rs5;
//			ResultSet rs6;
//			ResultSet rs7;
//			ResultSet rs8;
//
//			PreparedStatement ps1;
//			PreparedStatement ps2;
//			PreparedStatement ps3;
//			PreparedStatement ps4;
//			PreparedStatement ps5;
//			PreparedStatement ps6;
//			PreparedStatement ps7;
//			PreparedStatement ps8;
//
//			Timestamp t_date1 = null;
//			Timestamp t_date2 = null;
//
//			Statement stmt;
//
//			try {
//				ps1 = con.prepareStatement("INSERT INTO res VALUES (?,?,?,?,?)");
//
//				stmt = con.createStatement();
//				rs1 = stmt.executeQuery("select confirmation_no from (select confirmation_no from res order by confirmation_no desc) where rownum=1");
//				rs1.next();
//				confirmation_no = rs1.getInt("confirmation_no");
//
//
//				ps1.setInt(1, ++confirmation_no);
//
//				// daniel getting the starting date
//				System.out.print("\nType your dog name: ");
//				d_name = in.readLine();
//
//
//				//changing the dog name to d_id
//
//				ps2 = con.prepareStatement("SELECT d_id FROM dog WHERE d_name = ? AND c_id = ?");
//
//				ps2.setString(1, d_name);
//				ps2.setString(2, cid);
//
//				rs2 = ps2.executeQuery();
//
//				while(rs2.next()){
//
//					d_name = rs2.getString("d_id");
//
//				}
//
//				System.out.print("\nType your come-in date: ");
//				System.out.print("\nyear ex. 2016: ");
//				year1 = in.readLine();
//
//
//				System.out.print("\nmonth ex. 03: ");
//				month1 = in.readLine();
//
//
//				System.out.print("\nday ex. 02: ");
//				day1 = in.readLine();
//
//
//				System.out.print("\nhour ex. 13: ");
//				hour1 = in.readLine();
//
//
//				System.out.print("\nminute ex. 02: ");
//				minute1 = in.readLine();
//
//				date1 = year1 + "-" + month1 + "-" + day1 + " " + hour1 + ":" + minute1 + ":" + "00.000000";
//				
//				try {
//					t_date1 = Timestamp.valueOf(date1);
//				} catch (Exception e) {
//					System.out.print("\nWrong format: Type the date again, as it says.");
//					////customerMainMenu();
//					e.printStackTrace();
//				}
//
//				// daniel getting the duration from the service
//				try {
//
//					ps3 = con.prepareStatement("SELECT duration, s_id FROM service WHERE type=?");
//
//					System.out.print("\nService type: ");
//					type = in.readLine();
//					ps3.setString(1, type);
//
//
//					rs3 = ps3.executeQuery();
//					while (rs3.next()) {
//						duration = rs3.getInt("duration");
//						s_id = rs3.getInt("s_id");
//					}
//
//					ps3.close();
//
//				}
//
//
//				catch (SQLException ex) {
//					System.out.println("Message: " + ex.getMessage());
//
//				}
//
//				// daniel check if the date is conflicted with the existing schedule abc
//				try {
//					ps4 = con.prepareStatement("SELECT e_id FROM has_a_schedule ORDER BY e_id");
//					boolean b = false;
//					rs4 =  ps4.executeQuery();
//
//					while (rs4.next()) {
//						e_id = rs4.getInt("e_id");
//						b = false;
//
//						try {
//							stmt = con.createStatement();
//							ps5 = con.prepareStatement("SELECT sc_from, sc_to FROM has_a_schedule where e_id = ?");
//
//							ps5.setInt(1, e_id);
//
//							rs5 = ps5.executeQuery();
//
//							while (rs5.next()) {
//								// start time, end time from schedule
//								sc_from2 = rs5.getTimestamp("sc_from");
//								sc_to2 = rs5.getTimestamp("sc_to");
//
//								// start time, end time of the service(end time is not a pick up time)
//								Calendar cal = Calendar.getInstance();
//								cal.setTimeInMillis(t_date1.getTime());
//
//								cal.add(Calendar.HOUR,duration);
//								t_date2 = new Timestamp(cal.getTime().getTime());
//
//								if ((sc_from2.after(t_date1) && sc_from2.before(t_date2)) || (sc_to2.after(t_date1) && sc_to2.before(t_date2)) ) {
//
//									b = true;
//									System.out.print("this is true-------- \n");
//								} 
//							} 
//
//
//							if (b == false) {
//
//
//								String foo_t_date = t_date1.toString();
//								String foo_year = foo_t_date.substring(2, 4);
//								String foo_month = foo_t_date.substring(5, 7);
//								String foo_day = foo_t_date.substring(8, 10);
//								String foo_time = foo_t_date.substring(11, 16);
//
//								date2 = foo_year  + "-" + foo_month + "-" + foo_day  + " " + foo_time + ":" + "00.000000";
//								System.out.println("\ndate2: " + date2);
//
//								ps1.setString(2, date2);
//
//
//							}
//							ps5.close();
//						}
//						catch (SQLException ex) {
//							System.out.println("Message: " + ex.getMessage());
//						}
//						if (b == false) {
//							break;
//						}
//					}
//
//
//					if (b == true) {
//						System.out.printf("\nTime Conflict!");
//						//customerMainMenu();	
//					}
//					ps4.close();
//				}
//
//				catch (SQLException ex) {
//					System.out.println("Message: " + ex.getMessage());
//					try {
//						// undo the insert
//						con.rollback();
//					} catch (SQLException ex2) {
//						System.out.println("Message: " + ex2.getMessage());
//						System.exit(-1);
//					}
//				}
//
//
//
//				// daniel getting the pick up date
//				System.out.print("\nType your pick-up date: ");
//				System.out.print("\nyear ex. 2016: ");
//				year3 = in.readLine();
//
//
//				System.out.print("\nmonth ex. 03: ");
//				month3 = in.readLine();
//
//
//				System.out.print("\nday ex. 02: ");
//				day3 = in.readLine();
//
//
//				System.out.print("\nhour ex. 13: ");
//				hour3 = in.readLine();
//
//
//				System.out.print("\nminute ex.02: ");
//				minute3 = in.readLine();
//
//				date3 = year3 + "-" + month3 + "-" + day3 + " " + hour3 + ":" + minute3 + ":" + "00.000000";
//
//				Timestamp foo_t_date = null;
//				try {
//					foo_t_date = Timestamp.valueOf(date3);
//				} catch (Exception e) {
//					System.out.print("\nWrong date foramt, type it again, as it says.");
//					//customerMainMenu();
//					e.printStackTrace();
//				}
//				
//				String foo_year3 = year3.substring(2, 4);
//
//				date4 = foo_year3 + "-" + month3 + "-" + day3 + " " + hour3 + ":" + minute3 + ":" + "00.0000000";
//
//				if (foo_t_date.before(t_date2)) {
//					System.out.println("Invalid Pickup date");
//					//customerMainMenu();
//				}
//
//				int cidd = Integer.parseInt(cid);
//
//				ps1.setString(3, date4);
//				ps1.setInt(4, s_id);
//				ps1.setInt(5, cidd);
//
//				ps1.executeUpdate();
//				
//				con.commit();
//
//				ps1.close();
//			} catch (IOException e) {
//				System.out.println("IOException!");
//			} catch (SQLException ex) {
//				System.out.println("Message: " + ex.getMessage());
//				try {
//					con.rollback();
//				} catch (SQLException ex2) {
//					System.out.println("Message: " + ex2.getMessage());
//					System.exit(-1);
//				}
//			}
//
//			try {
//				ps7 = con.prepareStatement("INSERT INTO has_a_schedule VALUES (?,?,?,?,?)");
//
//				//Query highest sc_id and add 1
//				stmt = con.createStatement();
//				rs8 = stmt.executeQuery("select sc_id from (select sc_id from has_a_schedule order by sc_id desc) where rownum=1");
//				rs8.next();
//				sc_id = rs8.getInt("sc_id");
//
//				ps7.setInt(1, ++sc_id);
//				ps7.setString(2, date2);
//				
//				String foo_t_date = t_date2.toString();
//				String foo_year = foo_t_date.substring(2, 4);
//				String foo_month = foo_t_date.substring(5, 7);
//				String foo_day = foo_t_date.substring(8, 10);
//				String foo_time = foo_t_date.substring(11, 16);
//
//				foo_t_date =  foo_day + "-" + foo_month + "-" + foo_year + ":" + foo_time + ":" + "00.000000";
//
//				ps7.setString(3, foo_t_date);
//				ps7.setInt(4, e_id);
//				ps7.setString(5, d_name);
//
//				ps7.executeUpdate();
//
//				con.commit();
//				ps7.close();
//
//			} catch (SQLException ex) {
//				System.out.println("Message: " + ex.getMessage());
//				try {
//					// undo the insert
//					con.rollback();
//				} catch (SQLException ex2) {
//					System.out.println("Message: " + ex2.getMessage());
//					System.exit(-1);
//				}
//			}
//		}
        
        private void reserve() {
            
            int confirmation_no = 0;
            String d_name = "Gnar";
            String dropOffDate = "2016-12-10 13:00";
            String service_type = "All";
            String pickUpDate = "2016-12-15 13:00";
            
            String date1;
            String date2 = "";
            String date3;
            String date4;

            Timestamp sc_from2 = null;
            Timestamp sc_to2 = null;

            int s_id = 0;
            int e_id = 0;
            int sc_id = 0;
            int d_id = 0;

            int duration = 0;

            ResultSet rs1;
            ResultSet rs2;
            ResultSet rs3;
            ResultSet rs4;
            ResultSet rs5;
            ResultSet rs8;

            PreparedStatement ps1;
            PreparedStatement ps2;
            PreparedStatement ps3;
            PreparedStatement ps4;
            PreparedStatement ps5;
            PreparedStatement ps7;

            Timestamp t_date1 = null;
            Timestamp t_date2 = null;

            Statement stmt;

            try {
                ps1 = con.prepareStatement("INSERT INTO res VALUES (?,?,?,?,?)");

                stmt = con.createStatement();
                rs1 = stmt.executeQuery("select confirmation_no from (select confirmation_no from res order by confirmation_no desc) where rownum=1");
                rs1.next();
                confirmation_no = rs1.getInt("confirmation_no");

                ps1.setInt(1, ++confirmation_no);
                
                System.out.println("after ps1");

                //changing the dog name to d_id
                ps2 = con.prepareStatement("SELECT d_id FROM dog WHERE d_name = ? AND c_id = ?");

                ps2.setString(1, d_name);
                ps2.setString(2, cid);

                rs2 = ps2.executeQuery();

                while(rs2.next()){
                    d_id = rs2.getInt("d_id");
                }

                date1 = dropOffDate + ":" + "00.000000";
                
                System.out.println("after date1");
                
                try {
                    t_date1 = Timestamp.valueOf(date1);
                } catch (Exception e) {
                    System.out.print("\nWrong format: Type the date again, as it says.");
//					//customerMainMenu();
                    e.printStackTrace();
                }

                // daniel getting the duration from the service
                try {

                    ps3 = con.prepareStatement("SELECT duration, s_id FROM service WHERE type=?");

                    ps3.setString(1, service_type);


                    rs3 = ps3.executeQuery();
                    while (rs3.next()) {
                        duration = rs3.getInt("duration");
                        s_id = rs3.getInt("s_id");
                    }

                    ps3.close();

                }


                catch (SQLException ex) {
                    System.out.println("Message: " + ex.getMessage());

                }

                // daniel check if the date is conflicted with the existing schedule abc
                try {
                    ps4 = con.prepareStatement("SELECT e_id FROM has_a_schedule ORDER BY e_id");
                    boolean b = false;
                    rs4 =  ps4.executeQuery();

                    while (rs4.next()) {
                        e_id = rs4.getInt("e_id");
                        b = false;

                        try {
                            stmt = con.createStatement();
                            ps5 = con.prepareStatement("SELECT sc_from, sc_to FROM has_a_schedule where e_id = ?");

                            ps5.setInt(1, e_id);

                            rs5 = ps5.executeQuery();

                            while (rs5.next()) {
                                // start time, end time from schedule
                                sc_from2 = rs5.getTimestamp("sc_from");
                                sc_to2 = rs5.getTimestamp("sc_to");

                                // start time, end time of the service(end time is not a pick up time)
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(t_date1.getTime());

                                cal.add(Calendar.HOUR,duration);
                                t_date2 = new Timestamp(cal.getTime().getTime());

                                if ((sc_from2.after(t_date1) && sc_from2.before(t_date2)) || (sc_to2.after(t_date1) && sc_to2.before(t_date2)) ) {

                                    b = true;
                                    System.out.print("this is true-------- \n");
                                } 
                            } 


                            if (b == false) {


                                String foo_t_date = t_date1.toString();
                                String foo_year = foo_t_date.substring(2, 4);
                                String foo_month = foo_t_date.substring(5, 7);
                                String foo_day = foo_t_date.substring(8, 10);
                                String foo_time = foo_t_date.substring(11, 16);

                                date2 = foo_year  + "-" + foo_month + "-" + foo_day  + " " + foo_time + ":" + "00.000000";

                                ps1.setString(2, date2);


                            }
                            ps5.close();
                        }
                        catch (SQLException ex) {
                            System.out.println("Message: " + ex.getMessage());
                        }
                        if (b == false) {
                            break;
                        }
                    }


                    if (b == true) {
                        System.out.printf("\nTime Conflict!");
                        //customerMainMenu();	
                    }
                    ps4.close();
                }

                catch (SQLException ex) {
                    System.out.println("Message: " + ex.getMessage());
                    try {
                        // undo the insert
                        con.rollback();
                    } catch (SQLException ex2) {
                        System.out.println("Message: " + ex2.getMessage());
                        System.exit(-1);
                    }
                }

                date3 = pickUpDate + ":" + "00.000000";
                System.out.println("\ndate3: " + date3);

                Timestamp foo_t_date = null;
                try {
                    foo_t_date = Timestamp.valueOf(date3);
                } catch (Exception e) {
                    System.out.print("\nWrong date foramt, type it again, as it says.");
                    //customerMainMenu();
                    e.printStackTrace();
                }
                
                date4 = date3.substring(2);
                System.out.println("\ndate4: " + date4);

                
                if (foo_t_date.before(t_date2)) {
                    System.out.println("Invalid Pickup date");
                    //customerMainMenu();
                }

                int cidd = Integer.parseInt(cid);

                ps1.setString(3, date4);
                ps1.setInt(4, s_id);
                ps1.setInt(5, cidd);

                ps1.executeUpdate();
                System.out.println("\nafter ps1 execute");
                
                con.commit();

                ps1.close();
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
                try {
                    con.rollback();
                } catch (SQLException ex2) {
                    System.out.println("Message: " + ex2.getMessage());
                    System.exit(-1);
                }
            }

            try {
                
                System.out.println("\nstart insert into schedule");
                ps7 = con.prepareStatement("INSERT INTO has_a_schedule VALUES (?,?,?,?,?)");

                //Query highest sc_id and add 1
                stmt = con.createStatement();
                rs8 = stmt.executeQuery("select sc_id from (select sc_id from has_a_schedule order by sc_id desc) where rownum=1");
                rs8.next();
                sc_id = rs8.getInt("sc_id");

                ps7.setInt(1, ++sc_id);
                ps7.setString(2, date2);
                
                String foo_t_date = t_date2.toString();
                String foo_year = foo_t_date.substring(2, 4);
                String foo_month = foo_t_date.substring(5, 7);
                String foo_day = foo_t_date.substring(8, 10);
                String foo_time = foo_t_date.substring(11, 16);

                foo_t_date =  foo_day + "-" + foo_month + "-" + foo_year + ":" + foo_time + ":" + "00.000000";

                ps7.setString(3, foo_t_date);
                ps7.setInt(4, e_id);
                ps7.setInt(5, d_id);

                ps7.executeUpdate();
                System.out.println("\nafter ps7 execute");

                con.commit();
                ps7.close();

            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
                try {
                    // undo the insert
                    con.rollback();
                } catch (SQLException ex2) {
                    System.out.println("Message: " + ex2.getMessage());
                    System.exit(-1);
                }
            }
        }
        
        public void makeReservation(Reservation reservation)
        {
            reserve(reservation.getCustomerId(), reservation.dogName, reservation.reservationStart, reservation.reservationEnd, reservation.serviceType);
        }
        
        
        //Insert reservation with parameters
        private void reserve(int customerId, String d_name, String dropOffDate, String pickUpDate, String service_type) {
            
            int confirmation_no = 0;
            
            String date1;
            String date2 = "";
            String date3;
            String date4;

            Timestamp sc_from2 = null;
            Timestamp sc_to2 = null;

            int s_id = 0;
            int e_id = 0;
            int sc_id = 0;
            int d_id = 0;

            int duration = 0;

            ResultSet rs1;
            ResultSet rs2;
            ResultSet rs3;
            ResultSet rs4;
            ResultSet rs5;
            ResultSet rs8;

            PreparedStatement ps1;
            PreparedStatement ps2;
            PreparedStatement ps3;
            PreparedStatement ps4;
            PreparedStatement ps5;
            PreparedStatement ps7;

            Timestamp t_date1 = null;
            Timestamp t_date2 = null;

            Statement stmt;

            try {
                ps1 = con.prepareStatement("INSERT INTO res VALUES (?,?,?,?,?)");

                stmt = con.createStatement();
                rs1 = stmt.executeQuery("select confirmation_no from (select confirmation_no from res order by confirmation_no desc) where rownum=1");
                rs1.next();
                confirmation_no = rs1.getInt("confirmation_no");

                ps1.setInt(1, ++confirmation_no);

                //changing the dog name to d_id
                ps2 = con.prepareStatement("SELECT d_id FROM dog WHERE d_name = ? AND c_id = ?");

                ps2.setString(1, d_name);
                ps2.setString(2, Integer.toString(customerId));

                rs2 = ps2.executeQuery();

                while(rs2.next()){
                    d_id = rs2.getInt("d_id");
                }

                date1 = dropOffDate + ":" + "00.000000";
                
                try {
                    t_date1 = Timestamp.valueOf(date1);
                } catch (Exception e) {
                    System.out.print("\nWrong format: Type the date again, as it says.");
                    //customerMainMenu();
                    e.printStackTrace();
                }

                // daniel getting the duration from the service
                try {

                    ps3 = con.prepareStatement("SELECT duration, s_id FROM service WHERE type=?");

                    ps3.setString(1, service_type);


                    rs3 = ps3.executeQuery();
                    while (rs3.next()) {
                        duration = rs3.getInt("duration");
                        s_id = rs3.getInt("s_id");
                    }

                    ps3.close();

                }


                catch (SQLException ex) {
                    System.out.println("Message: " + ex.getMessage());

                }

                // daniel check if the date is conflicted with the existing schedule abc
                try {
                    ps4 = con.prepareStatement("SELECT e_id FROM has_a_schedule ORDER BY e_id");
                    boolean b = false;
                    rs4 =  ps4.executeQuery();

                    while (rs4.next()) {
                        e_id = rs4.getInt("e_id");
                        b = false;

                        try {
                            stmt = con.createStatement();
                            ps5 = con.prepareStatement("SELECT sc_from, sc_to FROM has_a_schedule where e_id = ?");

                            ps5.setInt(1, e_id);

                            rs5 = ps5.executeQuery();

                            while (rs5.next()) {
                                // start time, end time from schedule
                                sc_from2 = rs5.getTimestamp("sc_from");
                                sc_to2 = rs5.getTimestamp("sc_to");

                                // start time, end time of the service(end time is not a pick up time)
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(t_date1.getTime());

                                cal.add(Calendar.HOUR,duration);
                                t_date2 = new Timestamp(cal.getTime().getTime());

                                if ((sc_from2.after(t_date1) && sc_from2.before(t_date2)) || (sc_to2.after(t_date1) && sc_to2.before(t_date2)) ) {

                                    b = true;
                                    System.out.print("this is true-------- \n");
                                } 
                            } 


                            if (b == false) {


                                String foo_t_date = t_date1.toString();
                                String foo_year = foo_t_date.substring(2, 4);
                                String foo_month = foo_t_date.substring(5, 7);
                                String foo_day = foo_t_date.substring(8, 10);
                                String foo_time = foo_t_date.substring(11, 16);

                                date2 = foo_year  + "-" + foo_month + "-" + foo_day  + " " + foo_time + ":" + "00.000000";
                                System.out.println("\ndate2: " + date2);

                                ps1.setString(2, date2);


                            }
                            ps5.close();
                        }
                        catch (SQLException ex) {
                            System.out.println("Message: " + ex.getMessage());
                        }
                        if (b == false) {
                            break;
                        }
                    }


                    if (b == true) {
                        System.out.printf("\nTime Conflict!");
                        //customerMainMenu();	
                    }
                    ps4.close();
                }

                catch (SQLException ex) {
                    System.out.println("Message: " + ex.getMessage());
                    try {
                        // undo the insert
                        con.rollback();
                    } catch (SQLException ex2) {
                        System.out.println("Message: " + ex2.getMessage());
                        System.exit(-1);
                    }
                }

                date3 = pickUpDate + ":" + "00.000000";

                Timestamp foo_t_date = null;
                try {
                    foo_t_date = Timestamp.valueOf(date3);
                } catch (Exception e) {
                    System.out.print("\nWrong date foramt, type it again, as it says.");
                    //customerMainMenu();
                    e.printStackTrace();
                }
                
                date4 = date3.substring(2);

                
                if (foo_t_date.before(t_date2)) {
                    System.out.println("Invalid Pickup date");
                    //customerMainMenu();
                }

                int cidd = Integer.parseInt(cid);

                ps1.setString(3, date4);
                ps1.setInt(4, s_id);
                ps1.setInt(5, cidd);

                ps1.executeUpdate();
                
                con.commit();

                ps1.close();
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
                try {
                    con.rollback();
                } catch (SQLException ex2) {
                    System.out.println("Message: " + ex2.getMessage());
                    System.exit(-1);
                }
            }

            try {
                ps7 = con.prepareStatement("INSERT INTO has_a_schedule VALUES (?,?,?,?,?)");

                //Query highest sc_id and add 1
                stmt = con.createStatement();
                rs8 = stmt.executeQuery("select sc_id from (select sc_id from has_a_schedule order by sc_id desc) where rownum=1");
                rs8.next();
                sc_id = rs8.getInt("sc_id");

                ps7.setInt(1, ++sc_id);
                ps7.setString(2, date2);
                
                String foo_t_date = t_date2.toString();
                String foo_year = foo_t_date.substring(2, 4);
                String foo_month = foo_t_date.substring(5, 7);
                String foo_day = foo_t_date.substring(8, 10);
                String foo_time = foo_t_date.substring(11, 16);

                foo_t_date =  foo_day + "-" + foo_month + "-" + foo_year + ":" + foo_time + ":" + "00.000000";

                ps7.setString(3, foo_t_date);
                ps7.setInt(4, e_id);
                ps7.setInt(5, d_id);

                ps7.executeUpdate();

                con.commit();
                ps7.close();

            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
                try {
                    // undo the insert
                    con.rollback();
                } catch (SQLException ex2) {
                    System.out.println("Message: " + ex2.getMessage());
                    System.exit(-1);
                }
            }
        }
        
        
        //Change month from "10" to "OCT"	
        public String changeDateFormat(String month) {

            String foo1 =  "01";
            String foo2 =  "02";
            String foo3 =  "03";
            String foo4 =  "04";
            String foo5 =  "05";
            String foo6 =  "06";
            String foo7 =  "07";
            String foo8 =  "08";
            String foo9 =  "09";
            String foo10 = "10";
            String foo11 = "11";
            String foo12 = "12";

            if (month.equals(foo1)){
                month = "JAN";
            }
            else if (month.equals(foo2)){
                month = "FEB";
            }else if (month.equals(foo3)){
                month = "MAR";
            }else if (month.equals(foo4)){
                month = "APR";
            }else if (month.equals(foo5)){
                month = "MAY";
            }else if (month.equals(foo6)){
                month = "JUN";
            }else if (month.equals(foo7)){
                month = "JUL";
            }else if (month.equals(foo8)){
                month = "AUG";
            }else if (month.equals(foo9)){
                month = "SEP";
            }else if (month.equals(foo10)){
                month = "OCT";
            }else if (month.equals(foo11)){
                month = "NOV";
            }else if (month.equals(foo12)){
                month = "DEC";
            }

            return month;

        }


/////All employee functions///////////



    /*
     * insert new row in customer
     */
    private void insertCustomer() {
        int c_id = 0;
        String name;
        String email_address;
        String phone_no;
        String address;
        PreparedStatement ps;
        PreparedStatement ps2;
        PreparedStatement ps3;
        Statement stmt;
        ResultSet rs1;

        try {
            ps3 = con.prepareStatement("SELECT * FROM customer");

            int rowCount = ps3.executeUpdate();
            System.out.print(String.valueOf(rowCount));

            if (rowCount >= 100) {
                System.out.print("Your Database is full\n");
                return;

            }

        } catch (Exception e1) {
            System.out.print("\nWrong Id, please type again");
            // undo the insert
            manageCustomer();
        }

        try {
            ps = con.prepareStatement("INSERT INTO customer VALUES (?,?,?,?,?)");

            ResultSet rs;

            try {
                ps2 = con.prepareStatement("SELECT C_ID FROM customer WHERE C_ID = ?");
                stmt = con.createStatement();
                rs1 = stmt.executeQuery("select c_id from (select c_id from customer order by c_id desc) where rownum=1");
                rs1.next();
                c_id = rs1.getInt("c_id");

                c_id = branch.getRandomNumberFrom(1, 10000);
                ps2.setInt(1, c_id);
                ps.setInt(1, ++c_id);

                rs = ps2.executeQuery();

                if (rs.next()) {

                    insertCustomer();
                }

            } catch (Exception e1) {
                System.out.print("\nWrong Id, please type again");
                // undo the insert
                manageCustomer();
            }

            ps.setInt(1, c_id);

            System.out.print("\nCustomer Name: ");
            name = in.readLine();
            ps.setString(2, name);

            System.out.print("\nCustomer Email Address: ");
            email_address = in.readLine();

            if (email_address.length() == 0) {
                ps.setString(3, null);
            } else {
                ps.setString(3, email_address);
            }

            System.out.print("\nCustomer Phone NO: ");
            phone_no = in.readLine();
            ps.setString(4, phone_no);

            System.out.print("\nCustomer Address: ");
            address = in.readLine();
            if (address.length() == 0) {
                ps.setString(5, null);
            } else {
                ps.setString(5, address);
            }

            ps.executeUpdate();

            // commit work
            con.commit();

            ps.close();
        } catch (IOException e) {
            System.out.println("IOException!");
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
            try {
                // undo the insert
                con.rollback();
            } catch (SQLException ex2) {
                System.out.println("Message: " + ex2.getMessage());
                System.exit(-1);
            }
        }
    }
    
    
    //Insert customer with parameters
    private void insertCustomer(String c_name,String email_address,String phone_no,String address) {
        
        int c_id = 0;
        
        Statement stmt;
        PreparedStatement ps;
        
        ResultSet rs1;

        try {
            ps = con.prepareStatement("INSERT INTO customer VALUES (?,?,?,?,?)");

            stmt = con.createStatement();
            rs1 = stmt.executeQuery("select c_id from (select c_id from customer order by c_id desc) where rownum=1");
            rs1.next();
            c_id = rs1.getInt("c_id");
            
            ps.setInt(1, ++c_id);
            ps.setString(2, c_name);
            if (email_address.length() == 0) {
                ps.setString(3, null);
            } else {
                ps.setString(3, email_address);
            }
            ps.setString(4, phone_no);
            if (address.length() == 0) {
                ps.setString(5, null);
            } else {
                ps.setString(5, address);
            }

            ps.executeUpdate();
            // commit work
            con.commit();

            ps.close();
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
            try {
                // undo the insert
                con.rollback();
            } catch (SQLException ex2) {
                System.out.println("Message: " + ex2.getMessage());
                System.exit(-1);
            }
        }
    }


    

    // daniel randomly generating numbers(d_id should be generated from 1 to
    // 1000)
    public static int getRandomNumberFrom(int min, int max) {
        Random foo = new Random();
        int randomNumber = foo.nextInt((max + 1) - min) + min;

        return randomNumber;

    }

    
    public void cancelReservation(int customerId, String from_date, int d_id)
    {
        cancelReserve(customerId, from_date, d_id);
    }
    

    //Cancel reservation using c_name, d_name, and from_date
    private void cancelReserve(int customerId, String from_date, int dogId) {

        PreparedStatement ps;
        PreparedStatement ps2;

        try {
            //Delete from reservation
            ps = con.prepareStatement("DELETE FROM res WHERE c_id = ? AND from_date LIKE ?");
            ps.setInt(1, customerId);
            ps.setString(2, "%"+from_date+"%");
            
            System.out.println("before ps");
            
            ps.executeUpdate();
            
            System.out.println("after ps");
            
            ps.close();
            
            
            //Delete from has_a_schedule
            ps2 = con.prepareStatement("DELETE FROM has_a_schedule WHERE d_id=? AND sc_from LIKE ?");
            
            ps2.setInt(1, dogId);
            ps2.setString(2, "%"+from_date+"%");
            
            System.out.println("before ps2");
            
            ps2.executeUpdate();

            System.out.println("after ps2");
            
            con.commit();

            ps2.close();
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());

            try {
                con.rollback();
            } catch (SQLException ex2) {
                System.out.println("Message: " + ex2.getMessage());
                System.exit(-1);
            }
        }
    }
    
    //Retrieve c_id from c_name and d_name
    public String customerID(String c_name, String d_name) {
        PreparedStatement ps;
        Statement stmt;
        ResultSet rs;
        
        String c_id = "0";
        
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("select c.c_id from customer c,dog d where c.c_name='" + c_name + "' and d.d_name='" + d_name + "' and d.c_id=c.c_id");
                    
            rs.next();
            c_id = rs.getString("c_id");
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return c_id;
    }


    //Vincent
    private String[] pMonths = {"01", "02", "10", "11"};
    
    
    //check if employee is a manager with input e_id
    //returns m_id if manager, else 0 not a manager
    public int checkIfManager(int e_id) {
        Statement stmt;
        ResultSet rs;
        
        int m_id = 0;
        
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT m_id FROM full_time WHERE e_id='" + e_id + "'");
            
            if(rs.next()) {
                m_id = rs.getInt("m_id");
            }
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return m_id;
    }
    
//////daniel’s viewService 
    private void viewService() {
        String s_id;
        String rate;
        String type;
        String duration;
        String price;

        Statement stmt;
        ResultSet rs;

        try {
            stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT * FROM service");

            // get info on ResultSet
            ResultSetMetaData rsmd = rs.getMetaData();

            // get number of columns
            int numCols = rsmd.getColumnCount();

            System.out.println(" ");

            // display column names;
            for (int i = 0; i < numCols; i++) {
                // get column name and print it
                System.out.printf("%-15s", rsmd.getColumnName(i + 1));
            }

            System.out.println(" ");

            while (rs.next()) {
                // for display purposes get everything from Oracle
                // as a string

                // simplified output formatting; truncation may occur

                s_id = rs.getString("s_id");
                System.out.printf("%-10.10s", s_id);

                rate = rs.getString("rate");
                System.out.printf("%-20.20s", rate);

                type = rs.getString("type");
                System.out.printf("%-20.20s", type);

                duration = rs.getString("duration");
                System.out.printf("%-20.20s", duration);

                price = rs.getString("price");
                System.out.printf("%-15.15s\n", price);

            }

            // close the statement;
            // the ResultSet will also be closed
            stmt.close();
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
        }
    }
    
    
    private void deleteBuys() {
    	PreparedStatement ps;
    	ResultSet rs;
    	
    	String p_id;
    	String c_id;
    	
    	try {
			ps = con.prepareStatement("DELETE FROM buys WHERE p_id=?");
			
			System.out.println("Product ID: ");
			p_id = in.readLine();
			
			ps.setString(1, p_id);
			
			ps.executeUpdate();
			
			ps.close();
			
			
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	
    	
    }
    

    //Customers who have bought all the different products
    private void customersThatBoughtAllProducts() throws SQLException{
        Statement stmt;
        ResultSet rs;


        String c_id = "";
        String c_name = "";


        try{
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT c.c_id,c.c_name FROM customer c WHERE NOT EXISTS ((SELECT p.p_id FROM products p) MINUS (SELECT b.p_id FROM buys b WHERE c.c_id=b.c_id))");

            ResultSetMetaData rsmd = rs.getMetaData();

            // get number of columns
            int numCols = rsmd.getColumnCount();

            System.out.println(" ");

            // display column names;
            for (int i = 0; i < numCols; i++)
            {
                // get column name and print it
                System.out.printf("%-15s", rsmd.getColumnName(i+1));    
            }

            System.out.println(" ");

            while(rs.next())
            {
                c_id = rs.getString("c_id");
                System.out.printf("%-10.10s", c_id);


                c_name = rs.getString("c_name");
                System.out.printf("%-20.20s", c_name +"\n");

            }

            stmt.close();

        }
        catch(SQLException ex){
            System.out.println("Message: " + ex.getMessage());
        }
    }


    //Check if month is a promotional month
    public boolean checkPMonth(String month){
        if (Arrays.asList(pMonths).contains(month)) return true;
        return false;
    }

    
    //Change "january" to format able to be used in sql LIKE query
    public String formatMonth(String month){
        if(month.length() == 1) return "%-0"+month+"-%";
        if(month.length() == 2) return "%-"+month+"-%";
        switch(month) 
        {

        case("january"): 	return "%-01-%";
        case("february"): 	return "%-02-%"; 
        case("march"): 		return "%-03-%"; 
        case("april"):		return "%-04-%"; 
        case("may"): 		return "%-05-%"; 
        case("june"): 		return "%-06-%"; 
        case("july"): 		return "%-07-%"; 
        case("august"): 	return "%-08-%"; 
        case("september"): 	return "%-09-%"; 
        case("october"): 	return "%-10-%"; 
        case("november"): 	return "%-11-%"; 
        case("december"): 	return "%-12-%";

        default:       		System.out.println("Wrong Month Format");
        return "Invalid Month Format";
        }
    }


    public void customerOfMonth(){


        PreparedStatement ps;
        ResultSet rs;
        String m;
        String c_name;
        String c_id;
        String total;

        try {

            ps = con.prepareStatement("select c_id,c_name,total from customer c,(select c_id,count(c_id) as total from res where from_date like ? group by c_id having count(c_id) = (select num from (select c_id,count(c_id) as num from res where from_date like ? group by c_id order by count(c_id) desc) where rownum=1)) s where c.c_id=s.c_id");

            System.out.print("\nMonth ie. 01,02,10 : ");
            m = in.readLine();
            System.out.print(formatMonth(m));
            ps.setString(1, formatMonth(m));
            ps.setString(2, formatMonth(m));

            rs = ps.executeQuery();
            while(rs.next()) {
                c_id = rs.getString("c_id");
                c_name = rs.getString("c_name");
                total = rs.getString("total");
                System.out.println("Customer of the month: " + c_id + ": " + c_name + " with " + total + " service transactions");

            }



        } catch (SQLException | IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
        }

    }
    
    
    //format month as 01,02,10,12
    public void addPoints(int c_id,String month){
        Statement stmt;
        boolean promotionalMonth;
        
        try {
            stmt = con.createStatement();
            
            promotionalMonth = checkPMonth(month);
            
            if(promotionalMonth){
            stmt.executeQuery("update set loyalty_points=(loyalty_points+200) where c_id='"+c_id+"'");
            }
            else{
                stmt.executeQuery("update set loyalty_points=(loyalty_points+100) where c_id='"+c_id+"'");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    

    


    


    // daniel add service
    private void insertService() {
        int s_id = 0;
        int rate;
        String type;
        int duration;
        float price;

        PreparedStatement ps;
        PreparedStatement ps2;
        
        Statement stmt;
        ResultSet rs1;

        PreparedStatement ps3;

        // daniel check if dogs over created
        try {
            ps3 = con.prepareStatement("SELECT * FROM service");

            int rowCount = ps3.executeUpdate();
            System.out.print(String.valueOf(rowCount));

            if (rowCount >= 1000) {
                System.out.print("Your Database is full\n");
                return;

            }

        } catch (Exception e1) {
            System.out.print("\nWrong Id, please type again");
            // undo the insert
            // showMenu();
        }

        try {
            ps = con.prepareStatement("INSERT INTO service VALUES (?,?,?,?)");

            // daniel getting a random d_id from 1 to 1000 and with no
            // duplicates
            ResultSet rs;
            stmt = con.createStatement();
            rs1 = stmt.executeQuery("select s_id from (select s_id from service order by s_id desc) where rownum=1");
            rs1.next();
            s_id = rs1.getInt("s_id");

            ps.setInt(1, ++s_id);

            System.out.print("\nType: ");
            type = in.readLine();
            ps.setString(3, type);

            System.out.print("\nRate: ");
            rate = Integer.parseInt(in.readLine());
            ps.setInt(2, rate);

            System.out.print("\nDuration: ");
            System.out.print("\nhours: ");
            duration = Integer.parseInt(in.readLine());

            ps.setInt(4, duration);
            
            System.out.print("\nPrice: ");
            price = Float.parseFloat(in.readLine());
            
            ps.setFloat(5, price);

            ps.executeUpdate();

            // commit work
            con.commit();

            ps.close();
        } catch (IOException e) {
            System.out.println("IOException!");
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
            try {
                // undo the insert
                con.rollback();
            } catch (SQLException ex2) {
                System.out.println("Message: " + ex2.getMessage());
                System.exit(-1);
            }
        }
    }
    
    
    // daniel’s seeCustomerRank
    // daniel seeCustomerRank 123456
    private void seeCustomerRank() {
        String rank;
        ResultSet rs;
        
        PreparedStatement ps;

        try {
            ps = con.prepareStatement("SELECT rank FROM (SELECT c_id ,row_number() over(order by number_of_points DESC) as rank from loyalty_member) WHERE c_id = ?");
             
            ps.setString(1, cid);
            
            rs = ps.executeQuery();
            
            // get info on ResultSet
            ResultSetMetaData rsmd = rs.getMetaData();


            // get number of columns
            int numCols = rsmd.getColumnCount();


            System.out.println(" ");


            // display column names;
            for (int i = 0; i < numCols; i++) {
                // get column name and print it


                System.out.printf("%-15s", rsmd.getColumnName(i + 1));
            }

            System.out.println(" ");


            while (rs.next()) {
                // for display purposes get everything from Oracle
                // as a string
                // simplified output formatting; truncation may occur

                rank = rs.getString("rank");
                System.out.printf("%-20.20s", rank);

            }

            // close the statement;
            // the ResultSet will also be closed
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
        }
    }

    
    //Insert Service by employee/manager with parameters
    private void insertService(String type,int rate,int duration,float price) {
        int s_id = 0;

        PreparedStatement ps;
        
        Statement stmt;
        ResultSet rs1;

        
        try {
            ps = con.prepareStatement("INSERT INTO service VALUES (?,?,?,?)");

            stmt = con.createStatement();
            rs1 = stmt.executeQuery("select s_id from (select s_id from service order by s_id desc) where rownum=1");
            rs1.next();
            s_id = rs1.getInt("s_id");

            ps.setInt(1, ++s_id);
            ps.setInt(2, rate);
            ps.setString(3, type);
            ps.setInt(4, duration);
            ps.setFloat(5, price);

            ps.executeUpdate();

            // commit work
            con.commit();

            ps.close();
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
            try {
                // undo the insert
                con.rollback();
            } catch (SQLException ex2) {
                System.out.println("Message: " + ex2.getMessage());
                System.exit(-1);
            }
        }
    }

    // daniel edit # of quantity in stocks

    // daniel addDogs

    private void insertStocks() {
        int stock_id = 0;
        int quantity_ordered;
        int p_id;

        PreparedStatement ps;
        PreparedStatement ps2;
        
        Statement stmt;
        ResultSet rs1;
        ResultSet rs;

        PreparedStatement ps3;

        // daniel check if dogs over created
        try {
            ps3 = con.prepareStatement("SELECT * FROM stocks");
            ps = con.prepareStatement("INSERT INTO stocks VALUES (?,?,?,?)");

            int rowCount = ps3.executeUpdate();
            System.out.print(String.valueOf(rowCount));
            stmt = con.createStatement();
            rs1 = stmt.executeQuery("select stock_id from (select stock_id from stocks order by stock_id desc) where rownum=1");
            
            rs1.next();
            stock_id = rs1.getInt("stock_id");
            ps.setInt(3, ++stock_id);

            System.out.print("\n Product Id: ");
            p_id = Integer.parseInt(in.readLine());
            ps.setInt(2, p_id);

            int eidd = Integer.parseInt(eid);
            ps.setInt(1, eidd);
            
            System.out.print("\n quantity_ordered: ");
            quantity_ordered = Integer.parseInt(in.readLine());
            ps.setInt(4, quantity_ordered);

            ps = con.prepareStatement("INSERT INTO stocks VALUES (?,?,?,?)");
            ps.executeUpdate();

            // daniel getting a random d_id from 1 to 1000 and with no
            // duplicates
            // commit work
            con.commit();

            ps.close();
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
            try {
                ps2 = con.prepareStatement("SELECT stock_id FROM stocks WHERE stock_id = ?");

                stock_id = branch.getRandomNumberFrom(1, 10000);
                ps2.setInt(1, stock_id);

                rs = ps2.executeQuery();

                if (rs.next()) {

                    insertStocks();
                }

            } catch (Exception e1) {
                System.out.print("\nWrong Id, please type again");
                // undo the insert
                manageManeger();
                try {
                    con.rollback();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    //Insert stocks with parameters
    private void insertStocks(int p_id,int quantity_ordered) {
        int stock_id = 0;

        PreparedStatement ps;
        
        Statement stmt;
        ResultSet rs1;

        try {
            ps = con.prepareStatement("INSERT INTO stocks VALUES (?,?,?,?)");
            
            System.out.print("\n Product Id: ");
            p_id = Integer.parseInt(in.readLine());
            ps.setInt(2, p_id);
        
            stmt = con.createStatement();
            rs1 = stmt.executeQuery("select stock_id from (select stock_id from stocks order by stock_id desc) where rownum=1");
            rs1.next();
            stock_id = rs1.getInt("stock_id");
            
            int eidd = Integer.parseInt(eid);
            
            ps.setInt(1, eidd);
            ps.setInt(2, p_id);
            ps.setInt(3, ++stock_id);
            ps.setInt(4, quantity_ordered);

            ps.executeUpdate();

            // commit work
            con.commit();

            ps.close();
        } catch (IOException e) {
            System.out.println("IOException!");
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
            try {
                // undo the insert
                con.rollback();
            } catch (SQLException ex2) {
                System.out.println("Message: " + ex2.getMessage());
                System.exit(-1);
            }
        }
    }

    /*
     * create table employee
     *
     */



    // daniel view stock
    private void viewStocks() {
        String e_id;
        String p_id;
        String stock_id;
        String quantity_ordered;

        String p1_name;
        String p1_brand;
        Statement stmt;
        ResultSet rs;

        try {
            stmt = con.createStatement();

            rs = stmt.executeQuery(
                    "SELECT s.stock_id, s.p_id, p.p1_name, p.p1_brand, s.quantity_ordered FROM stocks s, products_t1 p WHERE s.p_id = p.p_id");

            // get info on ResultSet
            ResultSetMetaData rsmd = rs.getMetaData();

            // get number of columns
            int numCols = rsmd.getColumnCount();

            System.out.println(" ");

            // display column names;
            for (int i = 0; i < numCols; i++) {
                // get column name and print it

                System.out.printf("%-15s", rsmd.getColumnName(i + 1));
            }

            System.out.println(" ");

            while (rs.next()) {
                // for display purposes get everything from Oracle
                // as a string

                // simplified output formatting; truncation may occur

                stock_id = rs.getString("stock_id");
                System.out.printf("%-10.10s", stock_id);

                p_id = rs.getString("p_id");
                System.out.printf("%-20.20s", p_id);

                p1_name = rs.getString("p1_name");
                System.out.printf("%-20.20s", p1_name);

                p1_brand = rs.getString("p1_brand");
                System.out.printf("%-20.20s", p1_brand);

                quantity_ordered = rs.getString("quantity_ordered");
                System.out.printf("%-15.15s\n", quantity_ordered);

            }

            // close the statement;
            // the ResultSet will also be closed
            stmt.close();
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
        }
    }
    
    
    //Insert row into buys table
    public void insertBuys(){
        PreparedStatement ps;

        int receipt_no;
        int p_id;
        int c_id;
        int quantity_bought;

        Statement stmt;
        ResultSet rs2;

        try {

            ps = con.prepareStatement("instert into buys values(?,?,?,?)");

            stmt = con.createStatement();
            rs2 = stmt.executeQuery("select receipt_no from (select receipt_no from buys order by receipt_no desc) where rownum=1");
            rs2.next();
            receipt_no = rs2.getInt("receipt_no");

            ps.setInt(1,++receipt_no);

            System.out.print("\nProduct ID: ");
            p_id = Integer.parseInt(in.readLine());
            ps.setInt(2,p_id);

            System.out.print("\nCustomer ID: ");
            c_id = Integer.parseInt(in.readLine());
            ps.setInt(3,c_id);

            System.out.print("\nQuantity Bought: ");
            quantity_bought = Integer.parseInt(in.readLine());
            ps.setInt(4, quantity_bought);

            ps.close();

        } catch (SQLException | NumberFormatException | IOException e) {
            e.printStackTrace();
        }

    }
    
    //Insert buys with parameters
    public void insertBuys(int p_id,int c_id,int quantity_bought){
        PreparedStatement ps;

        int receipt_no;

        Statement stmt;
        ResultSet rs2;

        try {

            ps = con.prepareStatement("insert into buys values(?,?,?,?)");

            stmt = con.createStatement();
            rs2 = stmt.executeQuery("select receipt_no from (select receipt_no from buys order by receipt_no desc) where rownum=1");
            rs2.next();
            receipt_no = rs2.getInt("receipt_no");
            
            ps.setInt(1,++receipt_no);
            ps.setInt(2,p_id);
            ps.setInt(3,c_id);
            ps.setInt(4,quantity_bought);
            
            ps.executeUpdate();

            ps.close();

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

    }
    
    public void insertProduct(){
        PreparedStatement ps;

        int p_id;
        String p_name;
        String p_type;
        String p_brand;
        int quantity;
        float price;

        Statement stmt;
        ResultSet rs2;

        try {

            ps = con.prepareStatement("insert into products values(?,?,?,?,?,?)");

            stmt = con.createStatement();
            rs2 = stmt.executeQuery("select p_id from (select p_id from products order by p_id desc) where rownum=1");
            rs2.next();
            p_id = rs2.getInt("p_id");

            ps.setInt(1,++p_id);

            System.out.print("\nProduct Name: ");
            p_name = in.readLine();
            ps.setString(2,p_name);

            System.out.print("\nProduct Type: ");
            p_type = in.readLine();
            ps.setString(3,p_type);

            System.out.print("\nProduct Brand: ");
            p_brand = in.readLine();
            ps.setString(4, p_brand);
            
            System.out.print("\nQuantity: ");
            quantity = Integer.parseInt(in.readLine());
            ps.setInt(5, quantity);
            
            System.out.print("\nPrice: ");
            price = Float.parseFloat(in.readLine());
            ps.setFloat(6, price);
            
            ps.executeUpdate();

            ps.close();

        } catch (SQLException | NumberFormatException | IOException e) {
            e.printStackTrace();
        }

    }
    
    //Insert product with parameters
    public void insertProduct(String p_name,String p_type,String p_brand,int quantity,float price){
        PreparedStatement ps;

        int p_id;

        Statement stmt;
        ResultSet rs2;

        try {

            ps = con.prepareStatement("insert into products values(?,?,?,?,?,?)");

            stmt = con.createStatement();
            rs2 = stmt.executeQuery("select p_id from (select p_id from products order by p_id desc) where rownum=1");
            rs2.next();
            p_id = rs2.getInt("p_id");
            
            ps.setInt(1,++p_id);
            ps.setString(2,p_name);
            ps.setString(3,p_type);
            ps.setString(4, p_brand);
            ps.setInt(5, quantity);
            ps.setFloat(6, price);
            
            ps.executeUpdate();

            ps.close();

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

    }
    

    // daniel serachStocks

    /*
     * insert new row in employee
     */
    private void insertEmployee() {
        int e_id = 0;
        String e_name;
        int m_id = 0;
        int salary;
        int hourly_rate;
        String foo = null;
        String foo2 = null;

        PreparedStatement ps;
        PreparedStatement ps2;
        PreparedStatement ps3;
        PreparedStatement ps4;
        PreparedStatement ps5;
        PreparedStatement ps6;
        PreparedStatement ps7;
        
        Statement stmt;
        ResultSet rs1;
        
        Statement stmt1;
        ResultSet rs2;

        try {
            ps = con.prepareStatement("INSERT INTO employee_manages VALUES (?,?,?)");

            ResultSet rs;
            stmt = con.createStatement();
            rs1 = stmt.executeQuery("select e_id from (select e_id from employee_manages order by e_id desc) where rownum=1");
            rs1.next();
            e_id = rs1.getInt("e_id");

            ps.setInt(1, ++e_id);

            System.out.print("\nEmployee Name: ");
            e_name = in.readLine();
            ps.setString(2, e_name);

            System.out.print("\nType Manager Id: \n Type 'm' if you want this person as a manager ");
            foo = in.readLine();
            foo2 = "m";

            if (foo.equals(foo2)) {
                // add this person to full_time
                System.out.print("\ntest");
                // String foo10;

                ps.setNull(3, java.sql.Types.INTEGER);
            }

            else {

                int foo3 = Integer.parseInt(foo);

                ps.setInt(3, foo3);

            }

            ps.executeUpdate();

            // commit work
            con.commit();

            ps.close();
        } catch (IOException e) {
            System.out.println("IOException!");
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
            try {
                // undo the insert
                con.rollback();
            } catch (SQLException ex2) {
                System.out.println("Message: " + ex2.getMessage());
                System.exit(-1);
            }
        }
        //// adding full time and part_time

        if (foo.equals(foo2)) {
            // add this person to full_time

            try {
                ps4 = con.prepareStatement("INSERT INTO full_time VALUES (?,?,?)");

                ps4.setInt(1, e_id);

                System.out.print("\nSalary: ");
                salary = Integer.parseInt(in.readLine());
                ps4.setInt(2, salary);

                stmt1 = con.createStatement();
                rs2 = stmt1.executeQuery("select m_id from (select m_id from full_time order by m_id desc) where rownum=1");
                rs2.next();
                m_id = rs2.getInt("m_id");

                ps4.setInt(3, ++m_id);

                ps4.executeUpdate();

                // commit work
                con.commit();

                ps4.close();
            } catch (IOException e) {
                System.out.println("IOException!");
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
                try {
                    // undo the insert
                    con.rollback();
                } catch (SQLException ex2) {
                    System.out.println("Message: " + ex2.getMessage());
                    System.exit(-1);
                }
            }

        }

        else {

            try {
                ps5 = con.prepareStatement("INSERT INTO part_time VALUES (?,?)");

                ps5.setInt(1, e_id);

                System.out.print("\nhourly_rate: ");
                hourly_rate = Integer.parseInt(in.readLine());
                ps5.setInt(2, hourly_rate);

                ps5.executeUpdate();

                // commit work
                con.commit();

                ps5.close();
            } catch (IOException e) {
                System.out.println("IOException!");
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
                try {
                    // undo the insert
                    con.rollback();
                } catch (SQLException ex2) {
                    System.out.println("Message: " + ex2.getMessage());
                    System.exit(-1);
                }
            }

        }

    }
    
    
    //Insert employee with parameters
    private void insertEmployee(String e_name,int m_id,int wage) {
        int e_id = 0;
        String foo = null;

        PreparedStatement ps;
        PreparedStatement ps4;
        PreparedStatement ps5;
        
        Statement stmt;
        ResultSet rs1;
        
        Statement stmt1;
        ResultSet rs2;

    
        try {
            ps = con.prepareStatement("INSERT INTO employee_manages VALUES (?,?,?)");

            stmt = con.createStatement();
            rs1 = stmt.executeQuery("select e_id from (select e_id from employee_manages order by e_id desc) where rownum=1");
            rs1.next();
            e_id = rs1.getInt("e_id");

            ps.setInt(1, ++e_id);
            ps.setString(2, e_name);
            foo = String.valueOf(m_id);

            if (foo.equals("m")) {
                // add this person to full_time
                System.out.print("\ntest");

                ps.setNull(3, java.sql.Types.INTEGER);
            }

            else {

                ps.setInt(3, m_id);

            }

            ps.executeUpdate();

            // commit work
            con.commit();

            ps.close();
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
            try {
                // undo the insert
                con.rollback();
            } catch (SQLException ex2) {
                System.out.println("Message: " + ex2.getMessage());
                System.exit(-1);
            }
        }
        //// adding full time and part_time

        if (foo.equals("m")) {
            // add this person to full_time

            try {
                ps4 = con.prepareStatement("INSERT INTO full_time VALUES (?,?,?)");

                ps4.setInt(1, e_id);
                ps4.setInt(2, wage);

                stmt1 = con.createStatement();
                rs2 = stmt1.executeQuery("select m_id from (select m_id from full_time order by m_id desc) where rownum=1");
                rs2.next();
                int m_id1 = rs2.getInt("m_id");

                ps4.setInt(3, ++m_id1);

                ps4.executeUpdate();

                // commit work
                con.commit();

                ps4.close();
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
                try {
                    // undo the insert
                    con.rollback();
                } catch (SQLException ex2) {
                    System.out.println("Message: " + ex2.getMessage());
                    System.exit(-1);
                }
            }

        }

        else {

            try {
                ps5 = con.prepareStatement("INSERT INTO part_time VALUES (?,?)");

                ps5.setInt(1, e_id);
                ps5.setInt(2, wage);

                ps5.executeUpdate();

                // commit work
                con.commit();

                ps5.close();
            } catch (SQLException ex) {
                System.out.println("Message: " + ex.getMessage());
                try {
                    // undo the insert
                    con.rollback();
                } catch (SQLException ex2) {
                    System.out.println("Message: " + ex2.getMessage());
                    System.exit(-1);
                }
            }

        }

    }

    /*
     * deletes a employee
     */
    private void deleteEmployee() {
        int e_id;
        PreparedStatement ps;

        try {
            ps = con.prepareStatement("DELETE FROM employee_manages WHERE e_id = ?");

            System.out.print("\nEmployee ID: ");
            e_id = Integer.parseInt(in.readLine());
            ps.setInt(1, e_id);

            int rowCount = ps.executeUpdate();

            if (rowCount == 0) {
                System.out.println("\nEmployee " + e_id + " does not exist!");
            }

            con.commit();

            ps.close();
        } catch (IOException e) {
            System.out.println("IOException!");
        } catch (SQLException ex) {
            System.out.println("Message: " + "You cannot delete this employee b/c this employee has schedules");

            try {
                con.rollback();
            } catch (SQLException ex2) {
                System.out.println("Message: " + ex2.getMessage());
                System.exit(-1);
            }
        }
    }


    /*
     * display information about employee
     */
    private void showEmployee() {
        String e_id;
        String e_name;
        String m_id;

        Statement stmt;
        ResultSet rs;

        try {
            stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT * FROM employee_manages");

            // get info on ResultSet
            ResultSetMetaData rsmd = rs.getMetaData();

            // get number of columns
            int numCols = rsmd.getColumnCount();

            System.out.println(" ");

            // display column names;
            for (int i = 0; i < numCols; i++) {
                // get column name and print it

                System.out.printf("%-15s", rsmd.getColumnName(i + 1));
            }

            System.out.println(" ");

            while (rs.next()) {
                // for display purposes get everything from Oracle
                // as a string

                // simplified output formatting; truncation may occur

                e_id = rs.getString("e_id");
                System.out.printf("%-10.10s", e_id);

                e_name = rs.getString("e_name");
                System.out.printf("%-20.20s", e_name);

                m_id = rs.getString("m_id");
                System.out.printf("%-15.15s\n", m_id);

            }

            // close the statement;
            // the ResultSet will also be closed
            stmt.close();
        } catch (SQLException ex) {
            System.out.println("Message: " + ex.getMessage());
        }
    }

    public static void main(String args[]) {
		branch b = new branch();
//        DogGUI d = new DogGUI();
    }

    public void actionPerformed(ActionEvent e) {
        if (connect(usernameField.getText(), String.valueOf(passwordField.getPassword()))) {
            // if the username and password are valid,
            // remove the login window and display a text menu
            loginFrame.dispose();
            try {
                mainMenu();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        } else {
            loginAttempts++;

            if (loginAttempts >= 3) {
                loginFrame.dispose();
                System.exit(-1);
            } else {
                // clear the password
                passwordField.setText("");
            }
        }
    }
}

