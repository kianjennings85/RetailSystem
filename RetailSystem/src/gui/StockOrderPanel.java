package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import tableModels.ProductTableModel;
import data.Product;
import data.ProductToOrder;
import data.StockOrder;
import data.Supplier;

public class StockOrderPanel extends JPanel {

	private JComboBox comboSuppliers;
	// private JButton btnSeclectSupplier;
	private Supplier selectedSupplier;
	// private JLabel lblActiveSupplierText = null;
	// private JLabel lblActiveSupplier = null;
	private JLabel lblProductList;
	private JTable tableProducts;
	private JTable tableOrders;
	private JScrollPane scrollPaneProducts;
	private JScrollPane scrollPaneOrders;
	private JButton btnDisplayAllProducts = new JButton("Display all products");
	private JButton btnAddToOrder = new JButton("Add to order");
	private JButton btnOrder = new JButton("Submit order");
	private ArrayList<Object[]> arrayTemporaryOrder = new ArrayList<Object[]>();
	private ArrayList<Object[]> arrayOrder = new ArrayList<Object[]>();
	private Object[][] arrayTableProducts;
	private Object[][] arrayTableOrders;
	private JLabel lblError = new JLabel("");
	private Timer timer;
	private JComboBox comboSearchForProducts;
	private JButton btnDisplayCurrentOrder = new JButton("Show Current Order");
	private JButton btnUpdateOrderCompletion = new JButton("Update Orders");
	private JLabel lblProductsSearch = new JLabel("Product search: ");

	public StockOrderPanel() {
		setLayout(new MigLayout());
		ArrayList<String> supplierNames = new ArrayList<String>();
		// populates the suppliers combo box
		for (Supplier supplier : Shop.getSuppliers()) {
			String name = supplier.getSupplierName();
			supplierNames.add(name);
		}

		JPanel jpanel = new JPanel();
		jpanel.setBackground(Color.BLUE);
		jpanel.setOpaque(true);

		JLabel supplier = new JLabel("Supplier:");
		add(supplier, "split 5");
		// jpanel.add(supplier);
		setVisible(true);

		comboSuppliers = new JComboBox(supplierNames.toArray());
		add(comboSuppliers);
		comboSuppliers.setEditable(true);
		AutoCompleteDecorator.decorate(comboSuppliers);
		comboSuppliers.getEditor().getEditorComponent()
				.addKeyListener(new KeyListener() {

					@Override
					public void keyTyped(KeyEvent e) {
					}

					@Override
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ENTER) {
							populateProductsTable(comboSuppliers.getItemAt(
									comboSuppliers.getSelectedIndex())
									.toString());
						}

					}

					@Override
					public void keyReleased(KeyEvent e) {
					}

				});

		add(btnDisplayAllProducts);
		btnDisplayAllProducts.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				populateProductsTable("");
			}

		});
		
		add(lblProductsSearch,"gapx 50");
		String[] xxx = {"hello","hello1"};
		comboSearchForProducts = new JComboBox(xxx);
		add(comboSearchForProducts, "wrap");

		lblProductList = new JLabel("Supplier's Product list:");
		add(lblProductList, "wrap");

		arrayTableProducts = new Object[Shop.getProducts().size()][9];
		int counter = 0;

		for (Product products : Shop.getProducts()) {

			arrayTableProducts[counter][0] = products.getId();
			arrayTableProducts[counter][1] = products.getName();
			arrayTableProducts[counter][2] = products.getCategory();
			arrayTableProducts[counter][3] = products.getPrice();
			arrayTableProducts[counter][4] = products.getSupplier()
					.getSupplierName();
			arrayTableProducts[counter][5] = products.getQuantity();
			arrayTableProducts[counter][6] = products.isAvailable();
			arrayTableProducts[counter][7] = products.isFlaggedForOrder();

			arrayTableProducts[counter][8] = 0;
			counter++;
		}

		String columnNames[] = { "Id", "Name", "Category", "Price", "Supplier",
				"Quantity", "In Shop?", "Required", "Amount to Order" };
		ProductTableModel productsTableModel = new ProductTableModel(
				arrayTableProducts, columnNames);

		tableProducts = new JTable(productsTableModel);
		tableProducts.setAutoCreateRowSorter(true);

		scrollPaneProducts = new JScrollPane(tableProducts);
		// tableProducts.setCellSelectionEnabled(true);
		tableProducts.setAutoCreateRowSorter(true);
		tableProducts.getColumnModel().getSelectionModel()
				.addListSelectionListener(new ListSelectionListener() {

					public void valueChanged(ListSelectionEvent e) {
						int row = tableProducts.getSelectedRow();
						tableProducts.requestFocus();
						// tableProducts.editCellAt(row, 7);
						tableProducts.changeSelection(row, 8, false, false);
					}

				});

		add(scrollPaneProducts, "span 3, grow, push");

		JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new MigLayout());
		tempPanel.add(btnAddToOrder, "wrap, growx, pushx");
		tempPanel.add(btnOrder, "growx, pushx");
		add(tempPanel, "wrap, alignx left, aligny top, growx");

		btnAddToOrder.addActionListener(new ButtonTemporaryOrderHandler());
		btnOrder.addActionListener(new ButtonOrderHandler());

		// ORDER TABLE
		JLabel lblOrders = new JLabel("Orders:");
		add(lblOrders);
		add(lblError, "pushx, alignx center, wrap");
		lblError.setVisible(false);
		lblError.setFont(new Font("Serif",Font.BOLD,15));
		scrollPaneOrders = new JScrollPane();
		add(scrollPaneOrders, "push,grow,span 3");
		displayOrderTable();
		JPanel panelx = new JPanel();
		panelx.setLayout(new MigLayout());
		panelx.add(btnDisplayCurrentOrder,"growx, wrap");
		panelx.add(btnUpdateOrderCompletion,"growx");
		add(panelx,"alignx left, aligny top");

	}// end Constructor

	public void populateProductsTable(String forSupplier) {
		boolean supplierFound = false;
		for (Supplier supplier : Shop.getSuppliers()) {
			if (supplier.getSupplierName().equalsIgnoreCase(forSupplier)) {
				supplierFound = true;
				break;
			}
		}

		String columnNames[] = { "Id", "Name", "Category", "Price", "Supplier",
				"Quantity", "In Shop?", "Required", "Amount to Order" };

		// display all products from all suppliers
		if (supplierFound != true) {

			arrayTableProducts = new Object[Shop.getProducts().size()][9];
			int counter = 0;

			for (Product products : Shop.getProducts()) {

				arrayTableProducts[counter][0] = products.getId();
				arrayTableProducts[counter][1] = products.getName();
				arrayTableProducts[counter][2] = products.getCategory();
				arrayTableProducts[counter][3] = products.getPrice();
				arrayTableProducts[counter][4] = products.getSupplier()
						.getSupplierName();
				arrayTableProducts[counter][5] = products.getQuantity();
				arrayTableProducts[counter][6] = products.isAvailable();
				arrayTableProducts[counter][7] = products.isFlaggedForOrder();

				arrayTableProducts[counter][8] = 0;
				counter++;
			}

			ProductTableModel productsTableModel = new ProductTableModel(
					arrayTableProducts, columnNames);
			tableProducts = new JTable(productsTableModel);
			tableProducts.setAutoCreateRowSorter(true);
			tableProducts.getColumnModel().getSelectionModel()
					.addListSelectionListener(new ListSelectionListener() {

						public void valueChanged(ListSelectionEvent e) {
							int row = tableProducts.getSelectedRow();
							tableProducts.requestFocus();
							// tableProducts.editCellAt(row, 7);
							tableProducts.changeSelection(row, 8, false, false);
						}

					});
			scrollPaneProducts.getViewport().add(tableProducts);
			StockOrderPanel.this.repaint();
		} else {
			// display products only for the selected supplier
			int counterForThisSuppliersProducts = 0;
			for (Product products : Shop.getProducts()) {
				if (products.getSupplier().getSupplierName()
						.equalsIgnoreCase(forSupplier)) {
					counterForThisSuppliersProducts++;
				}
			}
			arrayTableProducts = new Object[counterForThisSuppliersProducts][9];
			int counter = 0;

			for (Product products : Shop.getProducts()) {

				if (products.getSupplier().getSupplierName()
						.equalsIgnoreCase(forSupplier)) {
					arrayTableProducts[counter][0] = products.getId();
					arrayTableProducts[counter][1] = products.getName();
					arrayTableProducts[counter][2] = products.getCategory();
					arrayTableProducts[counter][3] = products.getPrice();
					arrayTableProducts[counter][4] = products.getSupplier()
							.getSupplierName();
					arrayTableProducts[counter][5] = products.getQuantity();
					arrayTableProducts[counter][6] = products.isAvailable();
					arrayTableProducts[counter][7] = products
							.isFlaggedForOrder();

					arrayTableProducts[counter][8] = 0;
					counter++;
				}
			}

			ProductTableModel productsTableModel = new ProductTableModel(
					arrayTableProducts, columnNames);
			tableProducts = new JTable(productsTableModel);
			tableProducts.setAutoCreateRowSorter(true);
			tableProducts.getColumnModel().getSelectionModel()
					.addListSelectionListener(new ListSelectionListener() {

						public void valueChanged(ListSelectionEvent e) {
							int row = tableProducts.getSelectedRow();
							tableProducts.requestFocus();
							// tableProducts.editCellAt(row, 7);
							tableProducts.changeSelection(row, 8, false, false);
						}

					});
			scrollPaneProducts.getViewport().add(tableProducts);
			StockOrderPanel.this.repaint();
		}
	}// end PopulateProducts()

	/**
	 * Shows the lblError text for 4 seconds.
	 * 
	 * @param error
	 *            Text for the error message
	 * @param color
	 *            Color of the message
	 */
	public void displayErrorMessage(String error, Color color) {
		if (lblError.isVisible() == false) {
			lblError.setForeground(color);
			lblError.setText(error);
			lblError.setVisible(true);
			timer = new Timer(4000, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					lblError.setVisible(false);
					timer.stop();
				}

			});
			timer.start();
		}
	}

	public void displayOrderTable() {
		String columnNamesForOrders[] = { "Id", "Staff member",
				"Date of order", "Total", "Completed" };

		ArrayList<StockOrder> stockOrders = Shop.getStockOrders();
		arrayTableOrders = new Object[stockOrders.size()][5];
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		int x = 0;
		for (StockOrder stockOrder : stockOrders) {
			System.out.println(stockOrders.get(x).isCompleted());
			arrayTableOrders[x][0] = stockOrders.get(x).getId();
			arrayTableOrders[x][1] = stockOrders.get(x).getStaff().getName()
					+ " " + stockOrders.get(x).getStaff().getSurname();
			arrayTableOrders[x][2] = sdf.format(stockOrders.get(x).getDate());
			arrayTableOrders[x][3] = stockOrders.get(x).getTotal();
			arrayTableOrders[x][4] = stockOrders.get(x).isCompleted();
			x++;
		}

		ProductTableModel ordersTableModel = new ProductTableModel(
				arrayTableOrders, columnNamesForOrders);
		tableOrders = new JTable(ordersTableModel);
		tableOrders.setAutoCreateRowSorter(true);
		scrollPaneOrders.getViewport().add(tableOrders);
		scrollPaneOrders.repaint();
		tableOrders.getColumnModel().getSelectionModel()
				.addListSelectionListener(new ListSelectionListener() {

					public void valueChanged(ListSelectionEvent e) {
						int row = tableOrders.getSelectedRow();
						tableOrders.requestFocus();
						// tableProducts.editCellAt(row, 7);
						tableOrders.changeSelection(row, 4, false, false);
					}

				});
	}

	public class ButtonTemporaryOrderHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub arrayTemporaryOrder
			AbstractTableModel tableModel = (AbstractTableModel) tableProducts
					.getModel();
			tableModel.fireTableDataChanged();

			boolean found = false;
			for (int i = 0; i < arrayTableProducts.length; i++) {
				if ((int) arrayTableProducts[i][8] > 0) {
					Object[] thisObject = new Object[9];
					thisObject[0] = arrayTableProducts[i][0];
					thisObject[1] = arrayTableProducts[i][1];
					thisObject[2] = arrayTableProducts[i][2];
					thisObject[3] = arrayTableProducts[i][3];
					thisObject[4] = arrayTableProducts[i][4];
					thisObject[5] = arrayTableProducts[i][5];
					thisObject[6] = arrayTableProducts[i][6];
					thisObject[7] = arrayTableProducts[i][7];
					thisObject[8] = arrayTableProducts[i][8];
					arrayTemporaryOrder.add(thisObject);
					found = true;
				}
			}
			if (found == false) {
				displayErrorMessage("Nothing to order!", Color.red);
			}
			for (int i = 0; i < arrayTableProducts.length; i++) {
				if ((int) arrayTableProducts[i][8] > 0) {
					arrayTableProducts[i][8] = 0;
				}
			}
			tableModel.fireTableDataChanged();
			displayOrderTable();
			// StockOrderPanel.this.repaint();
		}

	}// end inner class handler

	public class ButtonOrderHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (arrayTemporaryOrder.size() > 0) {
				System.out.println("The size of the temp array is: "
						+ arrayTemporaryOrder.size());

				ArrayList<ProductToOrder> productsToOrder = new ArrayList<ProductToOrder>();
				for (int i = 0; i < arrayTemporaryOrder.size(); i++) {
					Object object[] = arrayTemporaryOrder.get(i);
					int id = (int) object[0];
					String name = (String) object[1];
					Supplier supplier = null;

					for (Supplier sup : Shop.getSuppliers()) {
						if (sup.getSupplierName().equalsIgnoreCase(
								(String) object[4])) {
							supplier = sup;
							break;
						}
					}
					String category = (String) object[2];
					double price = (double) object[3];
					int amount = (int) object[8];

					productsToOrder.add(new ProductToOrder(id, name, supplier,
							category, price, false, amount));
				}
				StockOrder sO = new StockOrder(productsToOrder,
						GUIBackBone.getLoggedStaffMember());
				Shop.getStockOrders().add(sO);

				ArrayList<StockOrder> stockOrders = Shop.getStockOrders();
				arrayTableOrders = new Object[stockOrders.size()][5];
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				int x = 0;
				for (StockOrder stockOrder : stockOrders) {
					System.out.println(stockOrders.get(x).isCompleted());
					arrayTableOrders[x][0] = stockOrders.get(x).getId();
					arrayTableOrders[x][1] = stockOrders.get(x).getStaff()
							.getName()
							+ " " + stockOrders.get(x).getStaff().getSurname();
					arrayTableOrders[x][2] = sdf.format(stockOrders.get(x)
							.getDate());
					arrayTableOrders[x][3] = stockOrders.get(x).getTotal();
					arrayTableOrders[x][4] = stockOrders.get(x).isCompleted();
					x++;
				}

				String columnNamesForOrders[] = { "Id", "Staff member",
						"Date of order", "Total", "Completed" };
				ProductTableModel ordersTableModel = new ProductTableModel(
						arrayTableOrders, columnNamesForOrders);
				tableOrders = new JTable(ordersTableModel);
				tableOrders.getColumnModel().getSelectionModel()
						.addListSelectionListener(new ListSelectionListener() {

							public void valueChanged(ListSelectionEvent e) {
								int row = tableOrders.getSelectedRow();
								tableOrders.requestFocus();
								// tableProducts.editCellAt(row, 7);
								tableOrders.changeSelection(row, 4, false,
										false);
							}

						});
				scrollPaneOrders.getViewport().add((tableOrders));
				StockOrderPanel.this.repaint();

				// clear the temporaryArrayOforders
				arrayTemporaryOrder = new ArrayList<Object[]>();
			} else {
				displayErrorMessage("No order to submit", Color.red);
			}
		}// end actionPerformed

	}// end inner class
}// end class