package data;

public class Customer {
	private static int nextID = 101;
	private int customerID;
	private String customerFName;
	private String customerLName;
	private String customerAddress;
	private String customerMobile;
	private String customerHome;

	public Customer(String customerFName, String customerLName,
			String customerAddress, String customerMobile, String customerHome) {
		// TODO Auto-generated constructor stub
		setCustomerID(nextID);
		this.customerFName = customerFName;
		this.customerLName = customerLName;
		this.customerAddress = customerAddress;
		this.customerMobile = customerMobile;
		this.customerHome = customerHome;
		nextID++;
	}

	public int getCustomerID() {
		return customerID;
	}

	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

	public String getCustomerFName() {
		return customerFName;
	}

	public void setCustomerFName(String customerFName) {
		this.customerFName = customerFName;
	}

	public String getCustomerLName() {
		return customerLName;
	}

	public void setCustomerLName(String customerLName) {
		this.customerLName = customerLName;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getCustomerMobile() {
		return customerMobile;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}

	public String getCustomerHome() {
		return customerHome;
	}

	public void setCustomerHome(String customerHome) {
		this.customerHome = customerHome;
	}

}
