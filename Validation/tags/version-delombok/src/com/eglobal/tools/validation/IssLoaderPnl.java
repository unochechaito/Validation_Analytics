// Generated by delombok at Mon Feb 10 12:47:07 CST 2025
package com.eglobal.tools.validation;

import com.eglobal.tools.validation.files.IssRawcomLoader;
import com.eglobal.tools.validation.statistics.FileLocationState;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IssLoaderPnl extends JPanel implements TimeRangeListener {
	private static final long serialVersionUID = 1L;
	public static final String PROPERTY_INDETERMINATE = "INDETERMINATE";
	public static final String ENABLED_START_BUTTON = "START_BUTTON";
	public static final String STRING_PROGRESS_BAR = "STRING_PROGRESS_BAR";
	private JTextField inputFileTxt;
	private JProgressBar progressBar;
	private JButton startBtn;
	private JTextArea outputTextArea;
	private FileLocationState fileLocationState;
	private String startTime;
	private String endTime;
	private String env;

	/**
	 * Create the panel.
	 */
	public IssLoaderPnl(String env, FileLocationState fileLocationState) {
		this.fileLocationState = fileLocationState;
		this.env = env;
		setLayout(createGridBagLayout());
		JPanel fileSelectionPanel = new JPanel(new GridBagLayout());
		GridBagConstraints innerGbc = new GridBagConstraints();
		innerGbc.insets = new Insets(5, 5, 5, 0);
		innerGbc.gridwidth = GridBagConstraints.REMAINDER;
		innerGbc.fill = GridBagConstraints.HORIZONTAL;
		JLabel inputFileLbl = new JLabel("     Archivo de entrada");
		GridBagConstraints gbc_inputFileLbl = new GridBagConstraints();
		gbc_inputFileLbl.anchor = GridBagConstraints.EAST;
		gbc_inputFileLbl.insets = new Insets(5, 5, 5, 5);
		gbc_inputFileLbl.gridx = 0;
		gbc_inputFileLbl.gridy = 0;
		add(inputFileLbl, gbc_inputFileLbl);
		JButton inputFileBtn = new JButton("Seleccionar");
		inputFileBtn.addActionListener(this::chooseFile);
		GridBagConstraints gbc_inputFileBtn = new GridBagConstraints();
		gbc_inputFileBtn.insets = new Insets(5, 5, 5, 0);
		gbc_inputFileBtn.gridx = 0;
		gbc_inputFileBtn.gridy = 0;
		gbc_inputFileBtn.weightx = 0;
		fileSelectionPanel.add(inputFileBtn, gbc_inputFileBtn);
		inputFileTxt = new JTextField(20);
		inputFileTxt.setEditable(false);
		GridBagConstraints gbc_inputFileTxt = new GridBagConstraints();
		gbc_inputFileTxt.gridwidth = 9;
		gbc_inputFileTxt.insets = new Insets(5, 5, 5, 5);
		gbc_inputFileTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_inputFileTxt.gridx = 1;
		gbc_inputFileTxt.gridy = 0;
		gbc_inputFileTxt.weightx = 1;
		fileSelectionPanel.add(inputFileTxt, gbc_inputFileTxt);
		add(fileSelectionPanel, innerGbc);
//     JLabel outputTextLbl = new JLabel("               Salida:");
//     GridBagConstraints gbc_outputTextLbl = new GridBagConstraints();
//     gbc_outputTextLbl.anchor = GridBagConstraints.EAST;
//     gbc_outputTextLbl.gridwidth = 2;
//     gbc_outputTextLbl.insets = new Insets(0, 0, 5, 5);
//     gbc_outputTextLbl.gridx = 1;
//     gbc_outputTextLbl.gridy = 4;
//     add(outputTextLbl, gbc_outputTextLbl);
		outputTextArea = new JTextArea();
		outputTextArea.setWrapStyleWord(true);
		outputTextArea.setFont(new Font("Consolas", Font.PLAIN, 13));
		DefaultCaret caret = (DefaultCaret) outputTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		outputTextArea.append("Ambiente seleccionado: " + env + "\n");
		outputTextArea.append("Seleccione los archivos Rawcom emisores a leer\n\n");
		JScrollPane scroll = new JScrollPane(outputTextArea);
		GridBagConstraints gbc_outputTextArea = new GridBagConstraints();
		gbc_outputTextArea.gridwidth = 11;
		gbc_outputTextArea.gridheight = 4;
		gbc_outputTextArea.insets = new Insets(5, 5, 5, 5);
		gbc_outputTextArea.fill = GridBagConstraints.BOTH;
		gbc_outputTextArea.gridx = 0;
		gbc_outputTextArea.gridy = 2;
		add(scroll, gbc_outputTextArea);
		startBtn = new JButton("Start");
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(5, 5, 5, 5);
		gbc_button.gridx = 10;
		gbc_button.gridy = 7;
		add(startBtn, gbc_button);
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(false);
		progressBar.setValue(0);
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.insets = new Insets(5, 5, 5, 5);
		gbc_progressBar.fill = GridBagConstraints.BOTH;
		gbc_progressBar.gridwidth = 11;
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 10;
		add(progressBar, gbc_progressBar);
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		ProgressBarRepainter w = new ProgressBarRepainter();
		executor.schedule(() -> w.repaint(), 1, TimeUnit.SECONDS);
		startBtn.addActionListener(e -> EventQueue.invokeLater(new Runnable() {
			public void run() {
				if (inputFileTxt.getText().trim().isEmpty()) {
					JOptionPane.showMessageDialog(null, "No se ha seleccionado archivo", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					startBtn.setEnabled(false);
					String[] inputFiles = Arrays.stream(inputFileTxt.getText().split(",")).map(String::trim).toArray(String[]::new);
					IssRawcomLoader loader = new IssRawcomLoader(env, inputFiles, startTime, endTime);
					loader.addListener(w);
					loader.start();
					executor.shutdown();
				}
			}
		}));
	}

	private GridBagLayout createGridBagLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		layout.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		layout.columnWeights = new double[] {0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		layout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		return layout;
	}

	private void chooseFile(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();
		if (fileLocationState.getUltimaUbicacionSeleccionada() != null) {
			chooser.setCurrentDirectory(new File(fileLocationState.getUltimaUbicacionSeleccionada()));
		}
		chooser.setMultiSelectionEnabled(true);
		int option = chooser.showOpenDialog(inputFileTxt);
		if (option == JFileChooser.OPEN_DIALOG) {
			File[] selectedFiles = chooser.getSelectedFiles();
			StringBuilder sb = new StringBuilder();
			for (File f : selectedFiles) {
				if (!f.canRead()) {
					JOptionPane.showMessageDialog(this, "El archivo no puede ser leído");
				} else {
					if (sb.length() > 0) {
						sb.append(",");
					}
					fileLocationState.setUltimaUbicacionSeleccionada(chooser.getCurrentDirectory().getAbsolutePath());
					sb.append(f.getAbsolutePath());
				}
			}
			inputFileTxt.setText(sb.toString());
		}
	}


	class ProgressBarRepainter implements PropertyChangeListener {
		ProgressBarRepainter() {
			progressBar.setIndeterminate(false);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(PROPERTY_INDETERMINATE)) {
				boolean isIndeterminte = (boolean) evt.getNewValue();
				progressBar.setStringPainted(true);
				progressBar.setIndeterminate(isIndeterminte);
				progressBar.setString("");
				return;
			}
			if (evt.getPropertyName().equals(STRING_PROGRESS_BAR)) {
				java.lang.String stringValue = (String) evt.getNewValue();
				progressBar.setString(stringValue);
				return;
			}
			if (evt.getPropertyName().equals(ENABLED_START_BUTTON)) {
				startBtn.setEnabled((boolean) evt.getNewValue());
				return;
			}
			if ("percent".equals(evt.getPropertyName())) {
				if (progressBar.getValue() != (int) evt.getNewValue()) {
					progressBar.setIndeterminate(false);
					progressBar.setString(null);
					progressBar.setStringPainted(true);
					progressBar.setValue((int) evt.getNewValue());
				}
			}
			if ("text".equals(evt.getPropertyName())) {
				outputTextArea.append(evt.getNewValue().toString());
				outputTextArea.append("\n");
			}
		}

		public void repaint() {
			progressBar.repaint();
			startBtn.repaint();
			outputTextArea.repaint();
		}
	}

	@Override
	public void onTimeRangeChange(String startTime, String endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
}
