
public class Dog {
	private int id;
	public String name;
	public String breed;
	public int age;
	public String gender;
	
	public Dog(String name, String breed, int age, String gender) 
	{
		if (name == null || name.isEmpty() || breed == null || breed.isEmpty() || gender == null || gender.isEmpty()) 
		{
			throw new IllegalArgumentException("One or more of the variables are empty");
		}
		this.name = name;
		this.breed = breed;
		this.age = age;
		this.gender = gender;
	}
	
	public Dog(int id, String name, String breed, int age, String gender) 
	{
		this.id = id;
		this.name = name;
		this.breed = breed;
		this.age = age;
		this.gender = gender;
	}
	
	public int getId()
	{
		return this.id;
	}
}
