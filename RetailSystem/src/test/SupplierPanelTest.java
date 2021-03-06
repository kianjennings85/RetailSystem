package test;

import static org.junit.Assert.*;
import gui.SupplierPanel;

import java.util.ArrayList;

import javax.swing.JTextField;

import org.junit.Before;
import org.junit.Test;

import data.Supplier;

public class SupplierPanelTest {
	SupplierPanel supplierPanel;
	ArrayList<Supplier> suppliers = new ArrayList<Supplier>();
	Supplier supplier;
	private Object test1;
	
	@Before
	public void setUp() {
		 //throws Exception
		supplierPanel = new SupplierPanel();
		supplier = new Supplier("test1", "test1");
		supplier.setSupplierId(102);
		suppliers.add(supplier);
	}

	
	@Test
	public void testAddSupplier() {
		supplierPanel.setSupplierName("test");
		supplierPanel.setSupplierAddress("test");
		supplierPanel.createSupplier(supplierPanel.getSupplierName(),supplierPanel.getSupplierAddress());
		if((supplier.getSupplierName().length()==0)&&(supplier.getSupplierAddress().length()==0)){
			fail("Supplier not saved");
		}
	
	}

	@Test
	public void testDeleteSupplier() {
		Supplier s = new Supplier("test2", "test2");
		s.setSupplierId(103);
		suppliers.add(s);
		String supplierList = "Id: 103, name: test2, address: test2";
		suppliers.size();
		supplierPanel.deleteSupplier(supplierList, 103, s);
		assertTrue(true);
		if(s.isSupplierDeleted()== false){
			fail("supplier wasn't set to deleted");
		}
	}
}
