package gui;

import java.awt.Component;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import data.Customer;
import data.CustomerOrder;
import data.Product;
import data.ProductToOrder;
import data.Supplier;

//this class deals with customer ordering only.
public class CustomerOrderPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private JComboBox comboSelectCustomer;
	private JButton selectCustomer;
	private Customer selectedCustomer;
	private JLabel productListLabel;
	private JButton btnOrder = null;
	private Object myProducts[][] ;
	private JLabel lblActiveCustomerText = null;
	private JLabel lblActiveCustomer = null;
	private JTable productsTable;
	private JTable previousOrdersTable;
	private JLabel lblPreviousCustomerOrder;
	private Object ordersObject[][];
	
	public CustomerOrderPanel() {
		setLayout(new MigLayout());
		ArrayList<String> customerNames = new ArrayList<String>();
		customerNames.add("");
		for ( Customer customer: Shop.getCustomers()){
			String name = customer.getCustomerFName()+" "+customer.getCustomerLName();
			customerNames.add(name);
		}
		
		JLabel lblCustomer = new JLabel("Customer:");
		add(lblCustomer, "split 5");
		
		comboSelectCustomer = new JComboBox(customerNames.toArray());
		add(comboSelectCustomer);
		comboSelectCustomer.setEditable(true);
		AutoCompleteDecorator.decorate(comboSelectCustomer);
		
		selectCustomer = new JButton("Select");
		add(selectCustomer);
		selectCustomer.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String concatenatedName = comboSelectCustomer.getSelectedItem().toString();
				if(getCustomerFromConcatenatedName(concatenatedName) != null){
					selectedCustomer = getCustomerFromConcatenatedName(concatenatedName);
					lblActiveCustomer.setText(selectedCustomer.getCustomerFName()+" "+selectedCustomer.getCustomerLName());
				}else{
					JOptionPane.showMessageDialog(CustomerOrderPanel.this, "No such customer in the list");
				}
			}
			
		});
		
		lblActiveCustomerText = new JLabel("Active Customer: ");
		lblActiveCustomer = new JLabel("");
		add(lblActiveCustomerText, "gapx 20px");
		add(lblActiveCustomer, "wrap");
		
		
		productListLabel = new JLabel("Product list:");
		add(productListLabel, "wrap");
		
		myProducts = new Object[Shop.getProducts().size()][8];
		int counter = 0;
		//make products array to feed into the table model
		for(Product product:Shop.getProducts()){
			if(product.isAvailable() && product.isDeleted()==false){
				myProducts[counter][0] = product.getId();
				myProducts[counter][1] = product.getName();
				myProducts[counter][2] = product.getSupplier().getSupplierName();
				myProducts[counter][3] = product.getCategory();
				myProducts[counter][4] = product.getPrice();
				myProducts[counter][5] = product.isDiscounted();
				myProducts[counter][6] = product.getQuantity();
				//this column will be editable
				myProducts[counter][7] = 0;
				counter ++;
			}
		}
		
		// make column names for table. Must be the same size as the Object[][] you will populate it with.
		String columnNames[] = {"Id","Name","Supplier","Category","Price","Discounted?","Quantity","Amount to Order"};
		//this is your table model. See below is a class that implements AbstractTableModel
		//the table model takes in the array of objects you want to populate and array of column names
		ProductTableModel productsTableModel = new ProductTableModel(myProducts, columnNames);
		//make the actual table and pass it the table model
		productsTable = new JTable(productsTableModel);
		//make the table sortable
		productsTable.setAutoCreateRowSorter(true);
		//add the table to scroll pane if the content is greater than the container
		JScrollPane scrollPane = new JScrollPane(productsTable);
		productsTable.setCellSelectionEnabled(true);
		
		productsTable.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				int row = productsTable.getSelectedRow();
				productsTable.changeSelection(row, 7, false, false);
				productsTable.requestFocus();
				productsTable.editCellAt(row, 7);
			}
			
		});
		
		add(scrollPane, "span 3, grow, push");
		
		//add the order button
		btnOrder = new JButton("Order");
		btnOrder.setToolTipText("All the products that have \"Amount to order\" greater than 0 will be placed on the order.");
		add(btnOrder, "aligny top, alignx left, wrap");
		btnOrder.addActionListener(new ButtonOrderHandler());
		
		
		//add the gui elements to view previous orders of a customer
		lblPreviousCustomerOrder = new JLabel("Previous Orders:");
		add(lblPreviousCustomerOrder, "cell 0 3");
		
		// make column names for table. Must be the same size as the Object[][] you will populate it with.
		String columnNames1[] = {"Id","Customer","Staff","Date","Total Net","Total Gross","Completed?"};
		//ArrayList<CustomerOrder> custOrde = getCustomerOrders();
		//ProductTableModel m = new ProductTableModel((Object[][]) custOrde.toArray(), columnNames1);
		//previousOrdersTable = new JTable(m);
		//JScrollPane x = new JScrollPane(previousOrdersTable);
		//add(x);

		
		
		
	}//end constructor
	
	public ArrayList<CustomerOrder> getCustomerOrders(){
		ArrayList<CustomerOrder> customerOrd = new ArrayList<CustomerOrder>();
		boolean foundAtLeastOne = false;
		for(CustomerOrder order:Shop.getCustomerOrders()){
			if(order.getCustomer() == selectedCustomer){
				customerOrd.add(order);
				foundAtLeastOne = true;
			}
		}
		if(foundAtLeastOne){
			return customerOrd;
		}else{
			return null;
		}
	}
	
	public Customer getCustomerFromConcatenatedName(String name){
		for(Customer customer:Shop.getCustomers()){
			String thisFullName = customer.getCustomerFName()+" "+customer.getCustomerLName();
			if(thisFullName.equalsIgnoreCase(name)){
				return customer;
			}
		}
		return null;
	}//end getCustomerFromConcatenatedName()

	public Object[][] getMyProducts() {
		return myProducts;
	}

	public void setMyProducts(Object[][] myProducts) {
		this.myProducts = myProducts;
	}
	
	
	public class ButtonOrderHandler implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			int option = JOptionPane.showConfirmDialog(null, 
					"All the products that have \"Amount to order\" greater than 0\n will be placed on the order." 
					, "", JOptionPane.OK_CANCEL_OPTION);
			if (option == JOptionPane.OK_OPTION){
				//get Updated product list
				Object[][] updatedProductList = CustomerOrderPanel.this.getMyProducts();
				ArrayList<ProductToOrder> productsToOrder = new ArrayList<ProductToOrder>();
				for(Object[] x : updatedProductList){
					//add only products that have amount set to > 0
					if((Integer) x[7] > 0){
						int id = (Integer) x[0];
						String name = (String) x[1];
						Supplier tempSupplier = null;
						boolean supplierFound = false;
						for(Supplier supplier:Shop.getSuppliers()){
							if(supplier.getSupplierName() == (String) x[2]){
								tempSupplier = supplier;
								supplierFound = true;
								break;
							}
						}
						if(supplierFound == false){
							JOptionPane.showMessageDialog(null, "No supplier has been found for at least on of the products. Make sure you do not edit suppliers and make the order in the same time.");
							return;
						}
						String category = (String) x[3];
						double price = (Double) x[4];
						boolean discounted = (Boolean) x[5];
						int amount = (Integer) x[7];
						productsToOrder.add(new ProductToOrder(id, name, tempSupplier, category, price, discounted, amount));	
					}
				}
				
				//create the actual order
				if(productsToOrder.size() > 0 && selectedCustomer != null){
					CustomerOrder order = new CustomerOrder(CustomerOrderPanel.this.selectedCustomer, GUIBackBone.getLoggedStaffMember(), productsToOrder);
					Shop.getCustomerOrders().add(order);
					System.out.println("Order has been created\nOrder id:"+order.getId()+"\nOrder totalGross: "+order.getTotalGross()+"\nOrder totalNet: "+order.getTotalNet());
				}else{
					JOptionPane.showMessageDialog(null, "Nothing to order or you did not select a customer!");
				}
				
		    }
		}
		
	}
	
	
}//end class CustomerOrderPanel





class ProductTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	private String columnNames[];
	private Object[][] data;
	
	public ProductTableModel(Object[][] objectPassed, String[] columnNamesPassed){
		data = objectPassed;
		columnNames = columnNamesPassed;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	
	@Override
	public String getColumnName(int col) {
        return columnNames[col];
    }

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		//Make sure that if the user changes the value to null (deletes everything from cell) the integer remains parsed to int
		//otherwise an exception is generated
		if(columnIndex != columnNames.length-1){
			return data[rowIndex][columnIndex];
		}else{
			if(data[rowIndex][columnIndex] != null){
				return data[rowIndex][columnIndex];
			}else{
				return 0;
			}
		}
	}
	
	public Class<?> getColumnClass(int c) {
	        return getValueAt(0, c).getClass();
	 }
		
	//allow edits ONLY on last column
	public boolean isCellEditable(int row, int col) {
        /*if (col < 2) {
            return false;
        } else {
            return true;
        }*/
		if(col == columnNames.length-1){
			return true;
		}else{
			return false;
		}
    }

	//allow saves ONLY on last column
    public void setValueAt(Object value, int row, int col) {
    	if(col == columnNames.length-1){
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    	}
    }
	
}//end class ProductTableModel
