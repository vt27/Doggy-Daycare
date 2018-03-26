import java.util.ArrayList;

public class Employee {
	private int e_id;
	private String e_name;
	private int m_id; 
	private boolean isManager;
	private ArrayList<EmployeeSchedule> schedule; 
	
	//if 0 = not managed by anyone
	private int managedBy;
	
	
	public Employee(int e_id, String e_name) {
		this.e_id = e_id;
		this.e_name = e_name;
		this.isManager = false;
		this.schedule = new ArrayList<EmployeeSchedule>();
		// default, employee is not a manager: m_id is null 
	}
	
	public int getEid()
	{
		return this.e_id;
	}
	
	public String getName()
	{
		return this.e_name;
	}
	
	public int getMid(){
		return this.m_id; 
	}
	
	public void setMID(int m_id){
		this.m_id = m_id; 
	}
	
	public void setAsManager(){
		this.isManager = true;
	}
	
	public void setManagedBy(int m_id) {
		this.managedBy = m_id;
	}
	public boolean isAManager(){
		return this.isManager; 
	}
	
	public ArrayList<EmployeeSchedule> getSchedule()
	{
		return this.schedule;
	}

	public void newSchedule(ArrayList<EmployeeSchedule> schedule) 
	{
		this.schedule = schedule;
	}
}