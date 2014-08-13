package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

public class PopupDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	public PopupDialog(String text) {	
		System.out.println("test");
		JPanel contentPanePanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(contentPanePanel);
		scrollPane.setMaximumSize(new Dimension(1024,768));
		contentPanePanel.setLayout(new MigLayout());
		setLayout(new GridBagLayout());
		JLabel lblText = new JLabel(text);
		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(this);
		contentPanePanel.add(lblText, "wrap, push");
		contentPanePanel.add(btnOk,"pushx, alignx center");
		setContentPane(scrollPane);
		
		pack();
		setVisible(true);
		

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.setVisible(false);		
	}

}
