package com.eglobal.tools.validation;

import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.FlowLayout;

public class EnvironmentChooser extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public EnvironmentChooser() {
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblNewLabel = new JLabel("Ambiente: ");
		add(lblNewLabel);
		
		JComboBox comboBox = new JComboBox(new String[]{"ATM", "POS"});
		add(comboBox);
		
	}

}
