
public class Reservation {
	private int customerId;
	private int dogId;
	public String dogName;
	public String reservationStart;
	public String reservationEnd;
	public String serviceType;
	public int confirmationNumber;
	
	
	public Reservation(int customerId, String dogName, String reservationStart, String reservationEnd, String serviceType) 
	{
		if (dogName == null || reservationStart == null || reservationEnd == null | serviceType == null) 
		{
			throw new IllegalArgumentException("Fields cannot be empty");
		}
		this.customerId = customerId;
		this.dogName = dogName;
		this.reservationStart = reservationStart;
		this.reservationEnd = reservationEnd;
		this.serviceType = serviceType;
	}
	
	public Reservation(int dogId, String reservationStart, String reservationEnd, String serviceType, int confirmationNumber) 
	{
		if (reservationStart == null || reservationEnd == null | serviceType == null) 
		{
			throw new IllegalArgumentException("Fields cannot be empty");
		}
		this.reservationStart = reservationStart;
		this.reservationEnd = reservationEnd;
		this.serviceType = serviceType;
		this.confirmationNumber = confirmationNumber;
		this.dogId = dogId;
	}
	
	public Reservation(int confirmationNumber, String reservationStart, String reservationEnd, int customerId) 
	{
		if (reservationStart == null || reservationEnd == null) 
		{
			throw new IllegalArgumentException("Fields cannot be empty");
		}
		this.reservationStart = reservationStart;
		this.reservationEnd = reservationEnd;
		this.customerId = customerId;
		this.confirmationNumber = confirmationNumber;
	}
	
	public int getCustomerId()
	{
		return this.customerId;
	}
	
	public int getDogId()
	{
		return this.dogId;
	}

}
