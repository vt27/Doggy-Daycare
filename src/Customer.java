import java.util.ArrayList;

public class Customer {
	private int id;
	private String name;
	private String emailAddress;
	private String phoneNumber;
	private String address;
	private ArrayList<Dog> dogs;
	
	public Customer(int id, String name, String emailAddress, String phoneNumber, String address) {
		this.id = id;
		this.name = name;
		this.emailAddress = emailAddress;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.dogs = new ArrayList<Dog>();
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getEmailAddress()
	{
		return this.emailAddress;
	}
	
	public String getPhoneNumber()
	{
		return this.phoneNumber;
	}
	
	public String getAddress()
	{
		return this.address;
	}
	
	public ArrayList<Dog> getDogs()
	{
		return this.dogs;
	}
	
	public void addDogs(ArrayList<Dog> dogs) 
	{
		this.dogs = dogs;
	}

}
