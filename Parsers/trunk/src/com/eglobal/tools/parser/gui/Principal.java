/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.parser.gui;

import com.eglobal.tools.parser.parsers.IParser;
import com.eglobal.tools.parser.parsers.IsoParser;
import com.eglobal.tools.parser.parsers.MastercardParser;
import com.eglobal.tools.parser.pojo.Message;
import com.eglobal.tools.parser.pojo.util.Converter;
import com.eglobal.tools.parser.utils.FileSystem;
import com.google.gson.Gson;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Optional;

public class Principal {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Principal.class);
	private JFrame frame;
	private JTextField fileSelectedText;
	private JButton loadBtn;
	private String canonicalFilePath;

	/**
	 * Create the application.
	 */
	public Principal() {
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(200, 200, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);
		fileSelectedText = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, fileSelectedText, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, fileSelectedText, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, fileSelectedText, -267, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(fileSelectedText);
		fileSelectedText.setColumns(10);
		JButton fileSelectBtn = getFileSelectBtn();
		springLayout.putConstraint(SpringLayout.NORTH, fileSelectBtn, -1, SpringLayout.NORTH, fileSelectedText);
		springLayout.putConstraint(SpringLayout.WEST, fileSelectBtn, 7, SpringLayout.EAST, fileSelectedText);
		frame.getContentPane().add(fileSelectBtn);
		loadBtn = new JButton("Cargar");
		loadBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Optional<String> optionalPath = Optional.ofNullable(canonicalFilePath);
				if (optionalPath.isPresent() && FileSystem.isAllowedPath.test(optionalPath.get())) {
					try (BufferedReader br = new BufferedReader(new FileReader(optionalPath.get()))) {
						while (br.ready()) {
							String line = br.readLine();
							String[] splited = line.split("]");
							String rawData = null;
							if (splited.length == 8) {
								rawData = splited[7];
							}
							ByteBuffer bf = null;
							IParser parser = null;
							if (rawData.startsWith("ISO")) {
								bf = ByteBuffer.wrap(rawData.getBytes());
								parser = IsoParser.getInstance();
							} else if (splited[3].contains("MASTER")) {
								rawData = rawData.replaceAll("\\*", "0");
								bf = ByteBuffer.wrap(Converter.hexStringBytetoByteArray(rawData));
								parser = MastercardParser.getInstance();
							} else {
								continue;
							}
							Message message = (Message) parser.parse(bf);
							Gson gson = new Gson();
							log.debug(gson.toJson(message));
						}
					} catch (FileNotFoundException e1) {
						log.error(String.format("Error al buscar el archivo: %s", optionalPath.get()), e1);
					} catch (IOException e1) {
						log.error(String.format("Error al leer el archivo: %s", optionalPath.get()), e1);
					}
				}
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, loadBtn, -1, SpringLayout.NORTH, fileSelectedText);
		springLayout.putConstraint(SpringLayout.EAST, loadBtn, -25, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(loadBtn);
		JComboBox<Object> parserSelectionCombo = new JComboBox<>();
		parserSelectionCombo.setModel(new DefaultComboBoxModel<>(new String[] {"MC", "ISO"}));
		parserSelectionCombo.setToolTipText("Selecciona el parser a utilizar");
		springLayout.putConstraint(SpringLayout.NORTH, parserSelectionCombo, 9, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, parserSelectionCombo, 6, SpringLayout.EAST, fileSelectBtn);
		springLayout.putConstraint(SpringLayout.EAST, parserSelectionCombo, -6, SpringLayout.WEST, loadBtn);
		frame.getContentPane().add(parserSelectionCombo);
	}

	private JButton getFileSelectBtn() {
		JButton fileSelectBtn = new JButton("...");
		fileSelectBtn.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			FileFilter filter = new com.eglobal.tools.parser.gui.FileFilter();
			chooser.setFileFilter(filter);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				log.debug("You chose to open this file: {}", chooser.getSelectedFile().getName());
				try {
					File selectedFile = chooser.getSelectedFile().getCanonicalFile();
					if (!FileSystem.isAllowedPath.test(selectedFile.getCanonicalPath())) {
						JOptionPane.showMessageDialog(frame, "El directorio al que desea acceder no esta permitido, seleccione otro directorio.", "Directorio Invalido", JOptionPane.ERROR_MESSAGE);
					} else {
						fileSelectedText.setText(selectedFile.getCanonicalPath());
						canonicalFilePath = selectedFile.getCanonicalPath();
					}
				} catch (IOException e1) {
					log.error("Error al obtener ruta canonica", e1);
				}
			}
		});
		return fileSelectBtn;
	}
}
