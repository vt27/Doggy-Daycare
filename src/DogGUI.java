import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

public class DogGUI implements ActionListener{
	branch branch = new branch();
	
	// user is allowed 3 login attempts
	private int loginAttempts = 0;

	// components of the login window
	private JTextField usernameField;
	private JPasswordField passwordField;
	
	private JFrame loginFrame;
	private JFrame mainFrame;
	private JFrame removeDog = new JFrame("Remove Dog");
	private JFrame removeReservation = new JFrame("Remove Reservation");
	private JPanel mainContentPanel = new JPanel(new CardLayout());
	private JPanel contentDBPane = new JPanel();
	private JPanel mainMenu = new JPanel(); 
	private JPanel customerReservationPanel = new JPanel();
	private JPanel addDogPanel = new JPanel();
	private JPanel customerProfilePanel = new JPanel();
	private JPanel seeRanksPanel = new JPanel(); 

	// All Employee Panels 
	private JPanel employeeProfilePanel = new JPanel(); 
	
	// All Manager Panels. Use employeeIsManager to check if these panels will be displayed 
	private boolean employeeIsManager = false; 
	
	private DefaultListModel<String> customerDogListModel = new DefaultListModel();
	private DefaultListModel customerReservationListModel;
	private JList<String> customerDogList;
	private JList<String> customerReservationList;
	private JComboBox profilePanelOptions;
	private JComboBox employeeProfilePanelOptions;
	private JList<String> copyCustomerDogList;
	private JList<String> copyCustomerReservationList;
	private DefaultListModel<String> employeeListModel;
	private DefaultListModel<String> employeeReservationListModel;

	// A valid UserID. Will be initialized in mainMenu() 
	private int userID; 
	// This global variable is specifically for method mainMenu()
	String user_type = ""; 
	
	public DogGUI() {
		
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

	@Override
	/*
	 * event handler for login window
	 */
	public void actionPerformed(ActionEvent e) {
		if (branch.connect(usernameField.getText(), String.valueOf(passwordField.getPassword()))) {
			// if the username and password are valid,
			// remove the login window and display a text menu
			loginFrame.dispose();
			initiateAllPanels();

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
	
	
	private void initiateAllPanels()
	{
		promptDatabaseCreation();
		mainMenu(); 
		
		mainContentPanel.add(contentDBPane, "Recreate database?");
		mainContentPanel.add(mainMenu, "Main Menu");
		mainFrame.getContentPane().add(mainContentPanel);
		mainFrame.setVisible(true);
	}
	
	private void initiateCustomerProfilePanel()
	{
		customerProfilePanel();
		makeReservationPanel();
		addDogPanel();
		mainContentPanel.add(customerProfilePanel, "Profile");
		mainContentPanel.add(customerReservationPanel, "Make a Reservation");
		mainContentPanel.add(addDogPanel, "Add Dog");
		mainContentPanel.add(seeRanksPanel, "See Rank");
	}

	private void initiateEmployeeProfilePanel(){
		employeeProfilePanel(); 
		mainContentPanel.add(employeeProfilePanel,  "Employee Profile");
	}

	private void promptDatabaseCreation()
	{
		mainFrame = new JFrame("Doggy Daycare");
		mainFrame.setBounds(0,0,800,350);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentDBPane.setLayout(null);
		JLabel promptLabel = new JLabel("Do you wish to recreate the database? If this is your first time logging in, select Yes",
				JLabel.CENTER);
		promptLabel.setBounds(0, 55, 800, 60);
		
		JButton yesButton = new JButton("Yes");
		yesButton.setBounds(250, 120, 70, 30);
		yesButton.addActionListener(new DataCreateAction());
		
		JButton noButton = new JButton("No");
		noButton.setBounds(450, 120, 70, 30);
		noButton.addActionListener(new DataCreateActionHitNo());
		
		contentDBPane.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
		contentDBPane.add(promptLabel, BorderLayout.CENTER);
		contentDBPane.add(yesButton);
		contentDBPane.add(noButton);
		JFrame.setDefaultLookAndFeelDecorated(true);
	}
	
	private void mainMenu() {
		// once we successfully log in, mainFrame closes, sql script prints out,
		// then this frame should open

		JLabel mainMenuLabel = new JLabel("Choose User Type");

		mainMenu.add(mainMenuLabel);
		mainMenu.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		// Radio buttons
		JRadioButton employeeRadio = new JRadioButton("Employee");
		mainMenu.add(employeeRadio);
		JRadioButton customerRadio = new JRadioButton("Customer");
		mainMenu.add(customerRadio);

		// Only select one not both
		ButtonGroup bg = new ButtonGroup();
		bg.add(employeeRadio);
		bg.add(customerRadio);
		
		// add customerRadio and employeeRadio to mainMenu 
		mainMenu.add(customerRadio);
		mainMenu.add(employeeRadio);
		
		// default customerRadio is selected 
		customerRadio.setSelected(true);
		user_type = "Input Customer ID"; 
		// default label for text field is for customerID 
		JLabel id = new JLabel(user_type); 
		mainMenu.add(id); 
		
		// setup textfield for user to input their id 
		JTextField textField = new JTextField();
		textField.addActionListener(this);
		mainMenu.add(textField); 
		textField.setColumns(10);
		
		// action listener checker for employeeRadio and customerRadio 
		ActionListener checker = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JRadioButton button = (JRadioButton) e.getSource();
				if (button == customerRadio) {
					user_type = "Input Customer ID"; 
					id.setText(user_type);
				} else {
					user_type = "Input Employee ID"; 
					id.setText(user_type);
					
				}
			}
		};
		
		// add action listener (checker) to employeeRadio and customerRadio 
		employeeRadio.addActionListener(checker);
		customerRadio.addActionListener(checker);
		
		// setup userButton and add to mainMenu 
		JButton userButton = new JButton("Enter");
		userButton.setHorizontalAlignment(SwingConstants.SOUTH_EAST);
		mainMenu.add(userButton); 
		
		// USERBUTTON'S ACTION LISTENER THIS IS WHAT I AM WORKING ON RIGHT NOW
		// THIS PART OF THE FUNCTION WILL BE INTERACTING WITH THE QUERIES IN BRANCH.JAVA 
		 userButton.addActionListener(new ActionListener(){
		 @Override
		 public void actionPerformed(ActionEvent e) {
			 String textFieldValue = textField.getText(); 
			 
			 if (textField.getText().isEmpty()){
				 // if textField is empty 
				 JOptionPane.showMessageDialog(mainMenu, "Need to input ID");
			 } 
			 else if (notInt(textFieldValue)){
				 // if textField value is not of type int 
				 JOptionPane.showMessageDialog(mainMenuLabel,  "Invalid format"); 
			 }
			 else {  // either employeeRadio or customerRadio is selected 
				 
				 // case 1: customerRadio selected 
				 if (customerRadio.isSelected()){   
					 boolean valid = branch.customerMainMenu(textFieldValue); 
					 if (valid){
						 userID = Integer.parseInt(textFieldValue); 
						 textField.setText("");
						 mainMenu.setVisible(false);
						 initiateCustomerProfilePanel();
						switchCards(mainContentPanel, "Profile"); // calls customerMenu
					 } else {
						 // invalid id, prompt Dialog box 
						 JOptionPane.showMessageDialog(mainMenu, "Error: invalid ID. Please try again"); 
					 }
							
				} // TODO: EmployeeRadio 
				 // case 2: employeeRadio is selected 
				 else{ 
					 boolean validEID = branch.employeeMainMenu(textFieldValue); 
					 if (validEID){
						 userID = Integer.parseInt(textFieldValue);
						 textField.setText("");
						 mainMenu.setVisible(false);
						 initiateEmployeeProfilePanel(); 
						 switchCards(mainContentPanel, "Employee Profile");
					 } else{
						 // invalid EID, prompt Dialog box 
						 JOptionPane.showMessageDialog(mainMenu,  "Error: invalid ID. Please try again");
					 }
				}
			 }
		}
	});
}
	
	public static boolean notInt(String str)  
	{  
	  try  
	  {  
	    int i = Integer.parseInt(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return true;  
	  }  
	  return false;  
	}
	
	
	private void makeReservationPanel() 
	{
		customerReservationPanel.setLayout(null);
		// Profile includes: update customer stuff, delete dog, points,
		// See Rank
		// Make reservation 
		JComboBox options = createMainComboBox("Make a Reservation");
		customerReservationPanel.add(options);
		
		JLabel dogLabel = new JLabel("Dog's Name");
		dogLabel.setBounds(50, 50, 200, 30);
		JTextField textField = new JTextField();
		textField.setBounds(50, 70, 200, 30);
		
		JLabel serviceLabel = new JLabel("Select a service");
		serviceLabel.setBounds(500, 50, 200, 30);
		
		String[] dropDownOptions = {"Dog Walking", "Grooming", "Boarding", "Dog Training", "All"};
		JComboBox serviceOptions = new JComboBox(dropDownOptions);
		serviceOptions.setBounds(500, 70, 100, 20);
		serviceOptions.setSelectedIndex(4);
		
		JLabel startTimeLabel = new JLabel("Pick a date and time of when you will be dropping off your dog");
		startTimeLabel.setBounds(50, 100, 400, 30);
		JLabel formatLabel = new JLabel("Please type the date and time with the following form: YYYY-MM-DD 23:59");
		formatLabel.setBounds(50, 110, 500, 30);
		JTextField startTimeTextField = new JTextField();
		startTimeTextField.setText("YYYY-MM-DD 23:59");
		startTimeTextField.setBounds(50, 130, 200, 30);
		
		JLabel endTimeLabel = new JLabel("Pick a date and time of when you will be picking up your dog");
		endTimeLabel.setBounds(50, 160, 400, 30);
		JLabel formatLabel2 = new JLabel("Please type the date and time with the following form: YYYY-MM-DD 23:59");
		formatLabel2.setBounds(50, 170, 500, 30);
		JTextField endTimeTextField = new JTextField();
		endTimeTextField.setText("YYYY-MM-DD 23:59");
		endTimeTextField.setBounds(50, 190, 200, 30);
		
		JButton submitButton = new JButton("Submit");
		submitButton.setBounds(500, 120, 90, 40);
		ActionListener submitReservation = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Reservation reservation = new Reservation(userID, textField.getText(), startTimeTextField.getText(),
							endTimeTextField.getText(), (String) serviceOptions.getSelectedItem());
					branch.makeReservation(reservation);
					textField.setText("");
					startTimeTextField.setText("YY-MM-DD 23:59");
					endTimeTextField.setText("YY-MM-DD 23:59");
					serviceOptions.setSelectedIndex(4);
					JOptionPane.showMessageDialog(addDogPanel, "Your reservation for " + reservation.dogName + " has been added to the database. See you soon!");
				} catch (IllegalArgumentException exception) {
					JOptionPane.showMessageDialog(addDogPanel, "One or more of the fields you have inputed has produced an error.");
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(addDogPanel, "An Internal Error has occured, an error has been reported, please try again later.");
					System.out.println(exception);
				}
			}
		};
		submitButton.addActionListener(submitReservation);
		
		customerReservationPanel.add(dogLabel);
		customerReservationPanel.add(textField);
		customerReservationPanel.add(serviceLabel);
		customerReservationPanel.add(serviceOptions);
		customerReservationPanel.add(startTimeLabel);
		customerReservationPanel.add(formatLabel);
		customerReservationPanel.add(startTimeTextField);
		customerReservationPanel.add(endTimeLabel);
		customerReservationPanel.add(formatLabel2);
		customerReservationPanel.add(endTimeTextField);
		customerReservationPanel.add(submitButton);
		logoutButton(customerReservationPanel);
	}

	private void employeeProfilePanel(){
		Employee employee; 
		try {
			logoutButton(employeeProfilePanel);
			employee = branch.getEmployeeProfile(userID); 
			employeeProfilePanel.setLayout(null); 
			
			employeeProfilePanelOptions = createMainComboBoxEmployee("Employee Profile"); 
			JLabel employeeID = new JLabel("EID: " + employee.getEid());
			employeeID.setBounds(50, 50, 200, 30);
			
			JLabel employeeName = new JLabel("Name: " + employee.getName());
			employeeName.setBounds(50, 70, 200, 30); 
			
			// add JLabels that for query Employee output 
			if (employee.isAManager()){	
				employeeIsManager = true; 
				employeeID.setText("Manager ID: " + employee.getMid());
			}
			
			employeeListModel = new DefaultListModel();
			
			JLabel scheduleLabel = new JLabel("Your Schedule");
			scheduleLabel.setBounds(270, 50, 200, 30);
			
			JLabel startTimeLabel = new JLabel("Start Time");
			startTimeLabel.setBounds(270, 60, 150, 30);
			
			JLabel endTimeLabel = new JLabel("End Time");
			endTimeLabel.setBounds(420, 60, 150, 30);

			ArrayList<EmployeeSchedule> schedules = employee.getSchedule();
			for(int i = 0; i < schedules.size(); i++)
			{
				EmployeeSchedule schedule = schedules.get(i);
				employeeListModel.addElement(schedule.startTime + "      " + schedule.endTime);
			} 

			JList<String> employeScheduleList = new JList<> (employeeListModel);
			
			employeScheduleList.setBounds(250, 80, 270, 70);
			
			JLabel allReservationsLabel = new JLabel("All upcoming reservations");
			allReservationsLabel.setBounds(10, 140, 200, 30);
			
			JLabel confirmationNumberLabel = new JLabel("Confirmation Number");
			confirmationNumberLabel.setBounds(10, 160, 150, 30);
			
			JLabel reservationStartLabel = new JLabel("Reservation Starts");
			reservationStartLabel.setBounds(170, 160, 150, 30);
			
			JLabel reservationEndLabel = new JLabel("Reservation Ends");
			reservationEndLabel.setBounds(350, 160, 150, 30);

			JLabel customerIdLabel = new JLabel("Customer ID");
			customerIdLabel.setBounds(500, 160, 150, 30);

			employeeReservationListModel = new DefaultListModel();
			ArrayList<Reservation> allReservations = branch.getAllReservations();
			for(int i = 0; i < allReservations.size(); i++)
			{
				Reservation reservation = allReservations.get(i);
				employeeReservationListModel.addElement(reservation.confirmationNumber + "                                        " + reservation.reservationStart + 
						"                       " + reservation.reservationEnd + "                        " + reservation.getCustomerId());
			} 

			JScrollPane scrollPane = new JScrollPane();
			JList<String> employeeReservationList = new JList<> (employeeReservationListModel);
			scrollPane.setViewportView(employeeReservationList);
			employeeReservationList.setBounds(10, 180, 600, 100);

			
			JButton refreshButton = new JButton("Refresh");
			refreshButton.setBounds(680, 240, 80, 35);
			ActionListener refreshStuff = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ArrayList<Reservation> allReservations = branch.getAllReservations();
					employeeReservationListModel.clear();
					employeeListModel.clear();
					for(int i = 0; i < allReservations.size(); i++)
					{
						Reservation reservation = allReservations.get(i);
						employeeReservationListModel.addElement(reservation.confirmationNumber + "                                        " + reservation.reservationStart + 
								"                       " + reservation.reservationEnd + "                        " + reservation.getCustomerId());
					} 
					ArrayList<EmployeeSchedule> schedules = employee.getSchedule();
					for(int i = 0; i < schedules.size(); i++)
					{
						EmployeeSchedule schedule = schedules.get(i);
						employeeListModel.addElement(schedule.startTime + "      " + schedule.endTime);
					} 
					((DefaultListModel<String>) employeeReservationList.getModel()).addElement("");
					((DefaultListModel<String>) employeeReservationList.getModel()).removeElement("");
					((DefaultListModel<String>) employeScheduleList.getModel()).addElement("");
					((DefaultListModel<String>) employeScheduleList.getModel()).removeElement("");
				}
			};
			refreshButton.addActionListener(refreshStuff);

			employeeProfilePanel.add(employeeProfilePanelOptions);
			employeeProfilePanel.add(employeeID);
			employeeProfilePanel.add(employeeName);
			employeeProfilePanel.add(allReservationsLabel);
			employeeProfilePanel.add(scheduleLabel);
			employeeProfilePanel.add(startTimeLabel);
			employeeProfilePanel.add(endTimeLabel);
			employeeProfilePanel.add(employeScheduleList);
			employeeProfilePanel.add(allReservationsLabel);
			employeeProfilePanel.add(customerIdLabel);
			employeeProfilePanel.add(confirmationNumberLabel);
			employeeProfilePanel.add(reservationStartLabel);
			employeeProfilePanel.add(reservationEndLabel);
			employeeProfilePanel.add(employeeReservationList);
			employeeProfilePanel.add(scrollPane);
			employeeProfilePanel.add(refreshButton);
			
		}catch (Exception e){
			e.printStackTrace(); 
		}
	}
	
	private void addDogPanel() 
	{
		addDogPanel.repaint();
		addDogPanel.setLayout(null);
		JComboBox options = createMainComboBox("Add Dog");
		
		JLabel dogLabel = new JLabel("Dog's Name");
		dogLabel.setBounds(50, 60, 200, 30);
		JTextField textField = new JTextField();
		textField.setBounds(50, 90, 200, 30);
		
		JLabel ageLabel = new JLabel("Dog's Age");
		ageLabel.setBounds(350, 60, 200, 30);
		String[] dropDownOptions = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", 
									"11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
		JComboBox ageOptions = new JComboBox(dropDownOptions);
		ageOptions.setBounds(350, 90, 100, 20);
		ageOptions.setSelectedIndex(0);
		
		JLabel breedLabel = new JLabel("Dog's Breed");
		breedLabel.setBounds(50, 130, 200, 30);
		JTextField breedTextField = new JTextField();
		breedTextField.setBounds(50, 160, 200, 30);
		
		JLabel genderLabel = new JLabel("Gender");
		genderLabel.setBounds(350, 130, 200, 30);
		String[] dropDownOptionsGender = {"m", "f"};
		JComboBox genderOptions = new JComboBox(dropDownOptionsGender);
		genderOptions.setBounds(350, 160, 100, 20);
		genderOptions.setSelectedIndex(1);
		
		JButton submitButton = new JButton("Submit");
		submitButton.setBounds(500, 120, 90, 40);
		
		ActionListener submitDog = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Dog dog = new Dog(textField.getText(), breedTextField.getText(),
							(ageOptions.getSelectedIndex())+1, (String) genderOptions.getSelectedItem());
					branch.customerAddDog(userID, dog);
					textField.setText("");
					breedTextField.setText("");
					genderOptions.setSelectedIndex(1);
					JOptionPane.showMessageDialog(addDogPanel, "Your dog " + dog.name + " has been added to the database.");
				} catch (IllegalArgumentException exception) {
					JOptionPane.showMessageDialog(addDogPanel, "One or more of the fields you have inputed has produced an error.");
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(addDogPanel, "An Internal Error has occured, an error has been reported, please try again later.");
					System.out.println(exception);
				}
			}
		};

		submitButton.addActionListener(submitDog);

		addDogPanel.add(options);
		addDogPanel.add(dogLabel);
		addDogPanel.add(textField);
		addDogPanel.add(ageLabel);
		addDogPanel.add(ageOptions);
		addDogPanel.add(breedLabel);
		addDogPanel.add(breedTextField);
		addDogPanel.add(genderLabel);
		addDogPanel.add(genderOptions);
		addDogPanel.add(submitButton);
		logoutButton(addDogPanel);
	}
	
	private void customerProfilePanel() 
	{
		Customer customer;
		try {
			customer = branch.getCustomerProfile(userID);
			customerProfilePanel.setLayout(null);
			profilePanelOptions = createMainComboBox("Profile");
			
			JLabel customerId = new JLabel("Identification Number");
			customerId.setBounds(50, 20, 200, 30);
			JTextField idField = new JTextField();
			idField.setBounds(50, 40, 100, 30);
			idField.setText(Integer.toString(customer.getId()));
			idField.setEnabled(false);
			
			JLabel customerName = new JLabel("Name");
			customerName.setBounds(50, 60, 200, 30);
			JTextField nameField = new JTextField();
			nameField.setBounds(50, 80, 100, 30);
			nameField.setText(customer.getName());
			
			JLabel customerEmailAddress = new JLabel("Email Address");
			customerEmailAddress.setBounds(50, 100, 200, 30);
			JTextField emailAddressField = new JTextField();
			emailAddressField.setBounds(50, 120, 100, 30);
			emailAddressField.setText(customer.getEmailAddress());
			
			JLabel customerPhoneNumber = new JLabel("Phone Number");
			customerPhoneNumber.setBounds(50, 140, 200, 30);
			JTextField phoneNumberField = new JTextField();
			phoneNumberField.setBounds(50, 160, 100, 30);
			phoneNumberField.setText(customer.getPhoneNumber());

			JLabel customerAddress = new JLabel("Address");
			customerAddress.setBounds(50, 180, 200, 30);
			JTextField addressField = new JTextField();
			addressField.setBounds(50, 200, 100, 30);
			addressField.setText(customer.getAddress());
			
//			ArrayList<Dog> customerDogs = customer.getDogs();
//			addDogsToCustomerProfile(customerDogs);
			JButton refreshButton = new JButton("Refresh");
			refreshButton.setBounds(340, 240, 80, 35);
			ActionListener refreshDog = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					((DefaultListModel<String>) customerDogList.getModel()).addElement("");
					((DefaultListModel<String>) customerDogList.getModel()).removeElement("");
					((DefaultListModel<String>) copyCustomerDogList.getModel()).addElement("");
					((DefaultListModel<String>) copyCustomerDogList.getModel()).removeElement("");
				}
			};
			refreshButton.addActionListener(refreshDog);
			
			JButton submitButton = new JButton("Submit");
			submitButton.setBounds(600, 150, 80, 35);
			ActionListener submitCustomer = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Customer customer = new Customer(Integer.parseInt(idField.getText()), nameField.getText(),
								emailAddressField.getText(), phoneNumberField.getText(), addressField.getText());
						branch.customerUpdateProfile(userID, customer);
						JOptionPane.showMessageDialog(customerProfilePanel, "Your profile has successfully been updated.");
					} catch (IllegalArgumentException exception) {
						JOptionPane.showMessageDialog(customerProfilePanel, exception);
					} catch (Exception exception) {
						JOptionPane.showMessageDialog(customerProfilePanel, "An Internal Error has occured, an error has been reported, please try again later.");
						System.out.println(exception);
					}
				}
			};
			submitButton.addActionListener(submitCustomer);
			
			JButton viewReservationButton = new JButton("View Reservations");
			viewReservationButton.setBounds(550, 100, 180, 35);
			ActionListener openReservations = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						ArrayList<Reservation> customerReservation = branch.viewReservations(userID);
						viewCustomerProfileReservations(customerReservation);
					} catch (IllegalArgumentException exception) {
						JOptionPane.showMessageDialog(customerProfilePanel, exception);
					} catch (Exception exception) {
						JOptionPane.showMessageDialog(customerProfilePanel, "An Internal Error has occured, an error has been reported, please try again later.");
						System.out.println(exception);
					}
				}
			};
			
			viewReservationButton.addActionListener(openReservations);
			
			customerProfilePanel.add(profilePanelOptions);
			customerProfilePanel.add(customerId);
			customerProfilePanel.add(idField);
			customerProfilePanel.add(customerName);
			customerProfilePanel.add(nameField);
			customerProfilePanel.add(customerEmailAddress);
			customerProfilePanel.add(emailAddressField);
			customerProfilePanel.add(customerPhoneNumber);
			customerProfilePanel.add(phoneNumberField);
			customerProfilePanel.add(customerAddress);
			customerProfilePanel.add(addressField);
			customerProfilePanel.add(refreshButton);
			customerProfilePanel.add(submitButton);
			customerProfilePanel.add(viewReservationButton);
			logoutButton(customerProfilePanel);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	private DefaultListModel<String> addDogsToCustomerProfile(ArrayList<Dog> dogs) 
	{
		customerDogListModel = new DefaultListModel();
		
		JLabel dogsLabel = new JLabel("Your Dogs");
		dogsLabel.setBounds(250, 50, 200, 30);
		
		JLabel nameLabel = new JLabel("Name");
		nameLabel.setBounds(250, 60, 50, 30);
		
		JLabel breedLabel = new JLabel("Breed");
		breedLabel.setBounds(300, 60, 50, 30);
		
		JLabel ageLabel = new JLabel("Age");
		ageLabel.setBounds(350, 60, 50, 30);
		
		JLabel genderLabel = new JLabel("Gender");
		genderLabel.setBounds(400, 60, 50, 30);

		for(int i = 0; i < dogs.size(); i++)
		{
			Dog dog = dogs.get(i);
			customerDogListModel.addElement(dog.name + "      " + dog.breed 
					+ "         " + Integer.toString(dog.age) 
					+ "            " + dog.gender.toUpperCase());
		} 

		customerDogList = new JList<> (customerDogListModel);
		ListSelectionListener listListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				((DefaultListModel<String>) copyCustomerDogList.getModel()).addElement("");
				((DefaultListModel<String>) copyCustomerDogList.getModel()).removeElement("");
				setUpRemoveDogFrame(copyCustomerDogList, dogs);
				removeDog.setVisible(true);
			}
		};
		
		customerDogList.addListSelectionListener(listListener);
		
		customerDogList.setBounds(250, 80, 250, 150);
		customerProfilePanel.add(dogsLabel);
		customerProfilePanel.add(nameLabel);
		customerProfilePanel.add(breedLabel);
		customerProfilePanel.add(ageLabel);
		customerProfilePanel.add(genderLabel);
		customerProfilePanel.add(customerDogList);
		DefaultListModel<String> copycustomerDogListModel = customerDogListModel;
		copyCustomerDogList = new JList<> (copycustomerDogListModel);
		setUpRemoveDogFrame(copyCustomerDogList, dogs);
		return customerDogListModel;
	}
	
	private void viewCustomerProfileReservations(ArrayList<Reservation> reservations) 
	{	
		removeReservation.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		removeReservation.setLayout(null);
		removeReservation.setBounds(0,0,800,350);
		
		customerReservationListModel = new DefaultListModel();
		
		JLabel reservationLabel = new JLabel("Your Reservations");
		reservationLabel.setBounds(330, 20, 200, 30);
		
		JLabel confirmationNumberLabel = new JLabel("Confirmation Number");
		confirmationNumberLabel.setBounds(30, 50, 200, 30);
		
		JLabel dropoffLabel = new JLabel("Drop-off Date and Time");
		dropoffLabel.setBounds(180, 50, 200, 30);
		
		JLabel pickupLabel = new JLabel("Pick-up Date and Time ");
		pickupLabel.setBounds(360, 50, 200, 30);
		
		JLabel serviceLabel = new JLabel("Service");
		serviceLabel.setBounds(570, 50, 80, 30);
		
		JLabel dogIdLabel = new JLabel("Dog ID");
		dogIdLabel.setBounds(700, 50, 80, 30);

		for(int i = 0; i < reservations.size(); i++)
		{
			Reservation reservation = reservations.get(i);
			customerReservationListModel.addElement(Integer.toString(reservation.confirmationNumber) 
					+ "                                            " + reservation.reservationStart
					+ "                         " + reservation.reservationEnd
					+ "                            " + reservation.serviceType
					+ "                             " + reservation.getDogId());
		} 

		customerReservationList = new JList<> (customerReservationListModel);
		ActionListener removeReservationListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int index = customerReservationList.getSelectedIndex();
					Reservation reservation = reservations.get(index);
					((DefaultListModel) customerReservationList.getModel()).remove(index);
					branch.cancelReservation(userID, reservation.reservationStart, reservation.getDogId());
					JOptionPane.showMessageDialog(removeReservation, "Your reservation for " + reservation.reservationStart + " has successfully been removed");
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(removeReservation, "An Internal Error has occured, an error has been reported, please try again later.");
					System.out.println(exception.toString());
				}
			}
		};

		JButton cancelButton = new JButton("Cancel Selected Reservation");
		cancelButton.setBounds(250, 240, 270, 40);
		cancelButton.addActionListener(removeReservationListener);
		removeReservation.add(cancelButton);
		
		JButton refreshButton = new JButton("Refresh");
		refreshButton.setBounds(680, 240, 80, 35);
		ActionListener refreshStuff = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<Reservation> reservations;
				try {
					reservations = branch.viewReservations(userID);
					customerReservationListModel.clear();
					for(int i = 0; i < reservations.size(); i++)
					{
						Reservation reservation = reservations.get(i);
						customerReservationListModel.addElement(Integer.toString(reservation.confirmationNumber) 
								+ "                                            " + reservation.reservationStart
								+ "                         " + reservation.reservationEnd
								+ "                            " + reservation.serviceType
								+ "                             " + reservation.getDogId());
					} 
					((DefaultListModel<String>) customerReservationList.getModel()).addElement("");
					((DefaultListModel<String>) customerReservationList.getModel()).removeElement("");
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(removeReservation, "An Internal Error has occured, an error has been reported, please try again later.");
					System.out.println(e1.toString());
				}
			}
		};
		refreshButton.addActionListener(refreshStuff);

		customerReservationList.setBounds(30, 80, 700, 150);
		removeReservation.add(reservationLabel);
		removeReservation.add(confirmationNumberLabel);
		removeReservation.add(dropoffLabel);
		removeReservation.add(pickupLabel);
		removeReservation.add(serviceLabel);
		removeReservation.add(customerReservationList);
		removeReservation.add(dogIdLabel);
		removeReservation.add(refreshButton);
		removeReservation.setVisible(true);
	}
	
	private void setUpRemoveDogFrame(JList<String> copyCustomerDogList, ArrayList<Dog> dogs)
	{
		removeDog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		removeDog.setLayout(null);
		removeDog.setBounds(0,0,300,350);
		copyCustomerDogList.setBounds(15, 20, 250, 150);

		JButton removeButton = new JButton("Remove Dog");
		removeButton.setBounds(100, 200, 90, 40);
		
		ActionListener removeDogListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int index = copyCustomerDogList.getSelectedIndex();
					Dog dog = dogs.get(index);
					((DefaultListModel) customerDogList.getModel()).remove(index);
					((DefaultListModel) copyCustomerDogList.getModel()).remove(index);
					branch.deleteDog(userID, dog.name);
					// anonymous inner class for closing the window
//					removeDog.addWindowListener(new WindowAdapter() {
//						public void windowClosing(WindowEvent e) {
//							
//						}
//					});
					JOptionPane.showMessageDialog(removeDog, dog.name + " has successfully been removed");
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(removeDog, "An Internal Error has occured, an error has been reported, please try again later.");
					System.out.println(exception.toString());
				}
			}
		};
		removeButton.addActionListener(removeDogListener);
		removeDog.add(removeButton);
		removeDog.add(copyCustomerDogList);
	}
	
	private void seeRanksPanel(int points)
	{
		seeRanksPanel.setLayout(null);
		JComboBox options = createMainComboBox("See Rank");
//		options.setSelectedIndex(2);
		seeRanksPanel.add(options);
		
		JLabel pointsLabel = new JLabel("Your Points");
		pointsLabel.setBounds(50, 50, 200, 30);
		JTextField pointsField = new JTextField();
		pointsField.setText(Integer.toString(points));
		pointsField.setBounds(50, 70, 70, 30);
		pointsField.setEnabled(false);
		
		seeRanksPanel.add(pointsLabel);
		seeRanksPanel.add(pointsField);
		try {
			ArrayList<TopRankedLoyaltyMember> topRankedLoyaltyMembers = branch.seeRank();
			createTopRankedLoyaltyMemberTable(topRankedLoyaltyMembers);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(seeRanksPanel, "An Internal Error has occured, an error has been reported, please try again later.");
			System.out.println(e.getMessage());
		}
		logoutButton(seeRanksPanel);
	}
	
	private void logoutButton(JPanel panel)
	{
		JButton logOutButton = new JButton("Log Out");
		logOutButton.setBounds(650, 10, 80, 40);
		
		ActionListener logoutListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switchCards(mainContentPanel, "Main Menu");
			}
		};
		logOutButton.addActionListener(logoutListener);
		panel.add(logOutButton);
	}
	
	private void createTopRankedLoyaltyMemberTable(ArrayList<TopRankedLoyaltyMember> topRankedLoyaltyMembers) 
	{
		String[] columnNames = {"Name", "Points Acquired"};
		String[][] data = new String[topRankedLoyaltyMembers.size()][2];
		for (int i = 0; i < topRankedLoyaltyMembers.size(); i++) 
		{
			TopRankedLoyaltyMember member = topRankedLoyaltyMembers.get(i);
			for (int j = 0; j < 2; j++) {
				if (j == 0) {
					data[i][j] = member.name;
				}
				else {
					data[i][j] = Integer.toString(member.points);
				}
			}
		}
		JLabel pointsLabel = new JLabel("Top Three Loyalty Members");
		pointsLabel.setBounds(250, 50, 200, 30);
		JTable table = new JTable(data, columnNames);
		table.getTableHeader().setBounds(250, 80, 200, 30);
		table.setBounds(250, 110, 200, 50);
		
		seeRanksPanel.add(pointsLabel);
		seeRanksPanel.add(table);
		seeRanksPanel.add(table.getTableHeader());
	}

	private JComboBox createMainComboBox(String defaultSelect)
	{
		String[] dropDownOptions = {"Profile", "Add Dog", "See Rank", "Make a Reservation"};
		JComboBox options = new JComboBox(dropDownOptions);
		options.setBounds(250, 20, 300, 20);
		switch(defaultSelect) {
			case "Profile":
				options.setSelectedIndex(0);
				break;
			case "Add Dog":
				options.setSelectedIndex(1);
				break;
			case "See Rank":
				options.setSelectedIndex(2);
				break;
			default:
				options.setSelectedIndex(3);
				break;
		}
		ActionListener comboListener = new ComboBoxListener();
		options.addActionListener(comboListener);
		return options;
	}

	// A COMBO BOX FOR EMPLOYEE
	// Should be able to see more options if Employee is a manager 
	private JComboBox createMainComboBoxEmployee(String e_defaultSelect){
		String[] employeeDropDownOptions = {"Employee Profile"}; 
		JComboBox e_options = new JComboBox(employeeDropDownOptions); 
		e_options.setBounds(250, 20, 300, 20); 
		switch(e_defaultSelect){
		case "Employee Profile":
			e_options.setSelectedIndex(0); 
		default :
			e_options.setSelectedIndex(0); // add more options later 
		}
		ActionListener employeeComboListener = new ComboBoxListener(); 
		e_options.addActionListener(employeeComboListener);
		return e_options; 
	}
	
	private void switchCards(JPanel cardLayout, String cardName)
	{
		CardLayout cl = (CardLayout) (mainContentPanel.getLayout());
		switch(cardName)
		{
			case "Profile":
				try {
					profilePanelOptions.setSelectedIndex(0);
					ArrayList<Dog> customerDogs = branch.getCustomerProfile(userID).getDogs();
					DefaultListModel<String> customerDogListModelChanged = addDogsToCustomerProfile(customerDogs);
					
//					((DefaultListModel<String>) customerDogList.getModel()).clear();
					
				} catch (Exception e) {
					JOptionPane.showMessageDialog(removeDog, "An Internal Error has occured, an error has been reported, please try again later.");
					System.out.println(e);
				}
				break;
			
			case "Add Dog":
				addDogPanel();
				break;
			
			case "See Rank":
				try {
					branch.seePoints(userID);
				} catch (Exception e) {
					int response = JOptionPane.showConfirmDialog(seeRanksPanel, "You are not signed up with our loyalty program. Would you like to become a member?", 
							"Loyalty Membership - Access Denied", JOptionPane.YES_NO_OPTION);
					if (response == 0) {
						break;
					}
				}
				try {
					int customerPoints = branch.seePoints(userID);
					seeRanksPanel(customerPoints);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(seeRanksPanel, "An Internal Error has occured, an error has been reported, please try again later.");
					System.out.println(e);
				}
				break;
			case "Employee Profile":
				try{
					employeeProfilePanelOptions.setSelectedIndex(0);
				} catch(Exception e){
					JOptionPane.showMessageDialog(mainMenu, "Cannot Enter Employee Profile");
					System.out.println(e);
				}
				break;
				
			default:
				break;
		}
			
		cl.show(cardLayout, cardName);
	}
	
	class DataCreateAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			branch.runSqlScript();
			switchCards(mainContentPanel, "Main Menu");
		}
	}

	class DataCreateActionHitNo implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			switchCards(mainContentPanel, "Main Menu");
		}
	}
	
	class ComboBoxListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JComboBox<String> combo = (JComboBox<String>) e.getSource();
			String selected = (String) combo.getSelectedItem();
			((JComboBox<String>) e.getSource()).setSelectedItem(selected);
			switchCards(mainContentPanel, selected);
		}
	}
<<<<<<< .mine
}||||||| .r33
}
=======
	
	
}
>>>>>>> .r60
