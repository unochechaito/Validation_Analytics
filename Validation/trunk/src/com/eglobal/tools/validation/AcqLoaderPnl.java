/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.validation;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import com.eglobal.tools.validation.db.DBManager;
import com.eglobal.tools.validation.files.AcqRawcomLoader;
import com.eglobal.tools.validation.statistics.FileLocationState;

public class AcqLoaderPnl extends JPanel implements TimeRangeListener {
	private static final long serialVersionUID = 1L;
	public static final String PROPERTY_INDETERMINATE = "INDETERMINATE";
	public static final String ENABLED_START_BUTTON = "START_BUTTON";
	public static final String STRING_PROGRESS_BAR = "STRING_PROGRESS_BAR";
	private JTextField inputFileTxt;
	private JProgressBar progressBar;
	private JButton startBtn;
	private JTextArea outputTextArea;
	private JCheckBox firstLoadCb;
	private FileLocationState fileLocationState;
	private String startTime;
	private String endTime;
	private String env;

	/**
	 * Create the panel.
	 */
	public AcqLoaderPnl(String env, FileLocationState fileLocationState) {
		this.fileLocationState = fileLocationState;
		this.env = env;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		JLabel inputFileLbl = new JLabel("     Archivo de entrada");
		GridBagConstraints gbc_inputFileLbl = new GridBagConstraints();
		gbc_inputFileLbl.anchor = GridBagConstraints.EAST;
		gbc_inputFileLbl.insets = new Insets(10, 5, 10, 0);
		gbc_inputFileLbl.gridx = 2;
		gbc_inputFileLbl.gridy = 1;
		add(inputFileLbl, gbc_inputFileLbl);
		JButton inputFileBtn = new JButton("Seleccionar");
		setupFileChooserButton(inputFileBtn);
		GridBagConstraints gbc_inputFileBtn = new GridBagConstraints();
		gbc_inputFileBtn.insets = new Insets(10, 0, 10, 0);
		gbc_inputFileBtn.gridx = 3;
		gbc_inputFileBtn.gridy = 1;
		add(inputFileBtn, gbc_inputFileBtn);
		inputFileTxt = new JTextField();
		GridBagConstraints gbc_inputFileTxt = new GridBagConstraints();
		gbc_inputFileTxt.gridwidth = 6;
		gbc_inputFileTxt.insets = new Insets(10, 0, 10, 5);
		gbc_inputFileTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_inputFileTxt.gridx = 4;
		gbc_inputFileTxt.gridy = 1;
		add(inputFileTxt, gbc_inputFileTxt);
		inputFileTxt.setColumns(20);
		inputFileTxt.setEditable(false);
		firstLoadCb = new JCheckBox("Primera carga");
		GridBagConstraints gbc_firstLoadCb = new GridBagConstraints();
		gbc_firstLoadCb.insets = new Insets(10, 20, 10, 110);
		gbc_firstLoadCb.anchor = GridBagConstraints.EAST;
		gbc_firstLoadCb.gridx = 10;
		gbc_firstLoadCb.gridy = 1;
		add(firstLoadCb, gbc_firstLoadCb);
		// Añadir el componente personalizado TimeRangePicker
		/*
     timeRangePicker = new TimeRangePicker();
     GridBagConstraints gbc_timeRangePicker = new GridBagConstraints();
     gbc_timeRangePicker.insets = new Insets(0, 0, 5, 5);
     gbc_timeRangePicker.gridx = 3;
     gbc_timeRangePicker.gridy = 3;
     gbc_timeRangePicker.gridwidth = 7;
     add(timeRangePicker, gbc_timeRangePicker);
     */
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
//     outputTextArea.setBackground(new Color(246, 248, 254, 100));
		DefaultCaret caret = (DefaultCaret) outputTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		outputTextArea.append("Ambiente seleccionado: " + env + "\n");
		outputTextArea.append("Seleccione los archivos Rawcom adquirentes a leer\n\n");
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
		gbc_button.insets = new Insets(5, 0, 5, 5);
		gbc_button.gridx = 10;
		gbc_button.gridy = 7;
		gbc_button.anchor = GridBagConstraints.EAST;
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
				if (inputFileTxt.getText().trim().length() == 0) {
					JOptionPane.showMessageDialog(null, "No se ha seleccionado archivo", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					startBtn.setEnabled(false);
					//String[] inputFiles = inputFileTxt.getText().split(",");
					String[] inputFiles = Arrays.stream(inputFileTxt.getText().split(",")).map(String::trim).toArray(String[]::new);
					//String inputFile = inputFileTxt.getText();
					AcqRawcomLoader loader = new AcqRawcomLoader(env, inputFiles, startTime, endTime);
					//AcqRawcomLoader loader = new AcqRawcomLoader(inputFile, startTime, endTime);
					if (firstLoadCb.isSelected()) {
						outputTextArea.append("*********Generando tablas nuevas*********\n");
						DBManager.getInstance().setUpDB();
					}
					loader.addListener(w);
					loader.start();
					executor.shutdown();
				}
			}
		}));
	}

	private void setupFileChooserButton(JButton inputFileBtn) {
		inputFileBtn.addActionListener(e -> {
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
						JOptionPane.showMessageDialog(inputFileBtn, "El archivo no puede ser leido");
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
		});
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
