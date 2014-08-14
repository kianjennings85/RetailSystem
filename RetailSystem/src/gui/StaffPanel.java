package gui;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.CardLayout;

import net.miginfocom.swing.MigLayout;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.BoxLayout;

import data.Customer;
import data.Staff;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class StaffPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField nameField;
	private JTextField surNameField;
	private JTextField salaryField;
	private JTextField userNameField;
	private JTextField passwordField;
	
	private Staff activeMember;
	
	ArrayList<String> staffMembers = new ArrayList<String>();
	JComboBox staffComboBox = new JComboBox(staffMembers.toArray());
	
	//private Shop shop;

	public StaffPanel() {
		setLayout(new MigLayout("", "[][grow][grow][][][][][][][][]", "[][][][][][][][][][][][][][][][][][][][][][][][][][][]"));
		
		for(Staff s : Shop.getStaffMembers()){
			String name = s.getName() +" "+ s.getSurname();
			staffMembers.add(name);
			if(s.isDeleted() == false){
				staffComboBox.addItem(s.getUsername());
			}
		}
		
		JLabel lblName = new JLabel("ADD NEW STAFF HERE: ");
		add(lblName, "cell 0 0");
		
		JLabel lblNewLabel = new JLabel("Name");
		add(lblNewLabel, "cell 0 2,alignx trailing");
		
		
		//Get values from each field and add to staffMembers
		nameField = new JTextField();
		add(nameField, "cell 1 2,growx");
		nameField.setColumns(10);
		
		JLabel lblSurname = new JLabel("Surname");
		add(lblSurname, "cell 0 3,alignx trailing");
		
		surNameField = new JTextField();
		add(surNameField, "cell 1 3,growx");
		surNameField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Salary");
		add(lblNewLabel_1, "cell 0 4,alignx trailing");
		
		salaryField = new JTextField();
		add(salaryField, "cell 1 4,growx");
		salaryField.setColumns(10);
		
		JLabel lblUsername = new JLabel("UserName");
		add(lblUsername, "cell 0 5,alignx trailing");
		
		userNameField = new JTextField();
		add(userNameField, "cell 1 5,growx");
		userNameField.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		add(lblPassword, "cell 0 6,alignx trailing");
		
		passwordField = new JPasswordField();
		add(passwordField, "cell 1 6,growx");
		passwordField.setColumns(10);
		
		//Get the Values from each textField and save them to their respective slots in the StaffMenbers Array
		//Add a new Staff Member with these values
		//Reset the form
		JButton btnAddStaff = new JButton("Add Staff Member");
		btnAddStaff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(nameField.getText().length() > 0 && surNameField.getText().length() > 0 && salaryField.getText().length() > 0 
						&& userNameField.getText().length() > 0 && passwordField.getText().length() > 0){
					
					//variables for parsing
					double salaryD = Double.parseDouble(salaryField.getText());
					
					//add the values to the array
					Staff s = new Staff(nameField.getText(),surNameField.getText(),salaryD
							,userNameField.getText(),passwordField.getText());
					
					//Access the Shop class and the staffMembers Array
					//shop = new Shop();
					ArrayList<Staff> staffMembers = Shop.getStaffMembers();
					
					//Remove items from Combo Box and re-add the entire list, which
					//contains the new Staff Member
					staffComboBox.removeAllItems();
					staffMembers.add(s);
					
					//Iterate through the Array staffMembers
					for(Staff staff : staffMembers){
						System.out.println("Staff" + staff.getName());
						
						//Add new Staff To ComboBox "staffComboBox"
						if(staff.isDeleted() == false){
					    staffComboBox.addItem(staff.getUsername());
					    //staffComboBox.addItem(s.getName());
						}
					}
					
					//Reset each TextField
					nameField.setText(""); 
					surNameField.setText("");
					salaryField.setText("");
					userNameField.setText("");
					passwordField.setText("");
				
					System.out.println("New Staff Member Added");
				}
				else{
					System.out.println("Invalid. Please Make Sure you fill in each TextField");
				}
			}
		});
		add(btnAddStaff, "flowx,cell 1 8");
		
		JLabel lblMembers = new JLabel("Members");
		add(lblMembers, "cell 0 14");
		
		
		add(staffComboBox, "wrap");
		add(staffComboBox, "cell 1 14,growx");

		JButton btnRemove = new JButton("Remove");
		add(btnRemove, "cell 3 25");
		
		//Remove Selected Item from ComboBox
		btnRemove.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				Shop.deleteStaff((String) staffComboBox.getSelectedItem());
				staffComboBox.removeItem(staffComboBox.getSelectedItem());
			}
		});
		
		
		//Edit Details
		JButton btnEditdetails = new JButton("EditDetails");
		add(btnEditdetails, "cell 4 25");
		
		btnEditdetails.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				//variables for parsing
				double salaryD = Double.parseDouble(salaryField.getText());
				
				System.out.println("Edit listener");
				Shop.EditDetails(nameField.getText(), surNameField.getText(), salaryD,
						userNameField.getText(), passwordField.getText());
				
			}
		});
		
		staffComboBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				Object selectedStaff = staffComboBox.getSelectedItem();
				
				System.out.println("Combo Select: " + selectedStaff);
				
				
				for(Staff s : Shop.getStaffMembers()){
					
					if(s.getUsername().equals(selectedStaff)){
						System.out.println(s.getUsername());
						//Shop.deleteStaff((String) staffComboBox.getSelectedItem());
						//staffComboBox.removeItem(staffComboBox.getSelectedItem());
						
						nameField.setText(s.getName()); 
						surNameField.setText(s.getSurname());
						salaryField.setText(String.valueOf(s.getSalary()));
						userNameField.setText(s.getUsername());
						passwordField.setText(s.getPassword());
						System.out.println("Here");
					}
				}
					//selectedStaff = null;
				
			}
		});
		
	}
	
	//Method to retrieve the name from The Array staffMembers in the Shop class 
	public Staff getStaffName(String name){
		for(Staff staff:Shop.getStaffMembers()){
			String sName = staff.getName();
			if(sName.equalsIgnoreCase(name)){
				return staff;
			}
		}
		return null;
	}
}