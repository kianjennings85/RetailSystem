package data;

import gui.Shop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.codehaus.jackson.map.ObjectMapper;

public class JsonExample {

	/**
	 * Returns the whole list of products stored in /resources/products.json
	 * @return
	 */
	public static ArrayList<Product> readProductsFromFile() {
		Scanner in=null;
		try {
			in = new Scanner(new FileReader("resources/products.json"));
			ObjectMapper mapper = new ObjectMapper();
			while (in.hasNextLine()) {
				Product product = mapper
						.readValue(in.nextLine(), Product.class);
				System.out.println(product.toString());
				Shop.getProducts().add(product);
			}
			return Shop.getProducts();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			in.close();
		}
		return null;
	}

	/**
	 * Saves the product in /resources/products.json as a Json object
	 * @param product
	 */
	public static void saveProductToFile(Product product) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String result = "\n" + mapper.writeValueAsString(product);
			FileWriter writer = new FileWriter("resources/products.json", true);
			writer.write(result);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}