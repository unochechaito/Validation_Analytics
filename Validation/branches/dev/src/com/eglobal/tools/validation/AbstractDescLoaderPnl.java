/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.validation;

import com.eglobal.tools.validation.files.DescLoader;
import com.eglobal.tools.validation.statistics.FileLocationState;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;

public abstract class AbstractDescLoaderPnl extends JPanel implements TimeRangeListener {
	private static final long serialVersionUID = 1L;
	public static final String PROPERTY_INDETERMINATE = "INDETERMINATE";
	public static final String ENABLED_START_BUTTON = "START_BUTTON";
	public static final String STRING_PROGRESS_BAR = "STRING_PROGRESS_BAR";
	public static final String PERCENT_VALUE_BAR = "PERCENT_VALUE_BAR";
	protected JTextField inputFileTxt;
	protected JProgressBar progressBar;
	protected JButton startBtn;
	protected JTextArea outputTextArea;
	protected FileLocationState fileLocationState;
	protected String startTime;
	protected String endTime;
	protected String env;

	public AbstractDescLoaderPnl(String env, FileLocationState fileLocationState) {
		this.fileLocationState = fileLocationState;
		this.env = env;
		setLayout(createGridBagLayout());
		JPanel fileSelectionPanel = new JPanel(new GridBagLayout());
		GridBagConstraints innerGbc = new GridBagConstraints();
		innerGbc.insets = new Insets(5, 5, 5, 0);
		JButton inputFileBtn = new JButton("Seleccionar");
		inputFileBtn.addActionListener(this::chooseFile);
		inputFileTxt = new JTextField(20);
		inputFileTxt.setEditable(false);
		innerGbc.gridx = 0;
		innerGbc.gridy = 0;
		innerGbc.weightx = 0;
		innerGbc.fill = GridBagConstraints.NONE;
		fileSelectionPanel.add(inputFileBtn, innerGbc);
		innerGbc.gridx = 1;
		innerGbc.weightx = 1.0;
		innerGbc.fill = GridBagConstraints.HORIZONTAL;
		fileSelectionPanel.add(inputFileTxt, innerGbc);
		addComponent(new JLabel(getFileLabelText()), 0, 0, GridBagConstraints.NONE, 1, 1);
		addComponent(fileSelectionPanel, 1, 0, GridBagConstraints.HORIZONTAL, 6, 1);
		outputTextArea = new JTextArea();
		outputTextArea.setWrapStyleWord(true);
		outputTextArea.setFont(new Font("Consolas", Font.PLAIN, 13));
		DefaultCaret caret = (DefaultCaret) outputTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scroll = new JScrollPane(outputTextArea);
		addComponent(scroll, 0, 2, GridBagConstraints.BOTH, 7, 6);
		outputTextArea.append("Ambiente seleccionado: " + env + "\n");
		outputTextArea.append(getInitialText() + "\n\n");
		startBtn = new JButton("Start");
		startBtn.addActionListener(this::handleStartButtonClick);
		addComponent(startBtn, 6, 8, GridBagConstraints.NONE, 1, 1);
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(false);
		progressBar.setValue(0);
		addComponent(progressBar, 0, 9, GridBagConstraints.BOTH, 7, 1);
	}

	protected abstract String getFileLabelText();

	protected abstract String getInitialText();

	protected abstract boolean isValidFile(File file);

	private GridBagLayout createGridBagLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0};
		layout.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		layout.columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		layout.rowWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		return layout;
	}

	private void addComponent(JComponent component, int x, int y, int fill, int width, int height) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = fill;
		add(component, gbc);
	}

	private void chooseFile(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		if (fileLocationState.getUltimaUbicacionSeleccionada() != null) {
			chooser.setCurrentDirectory(new File(fileLocationState.getUltimaUbicacionSeleccionada()));
		}
		int option = chooser.showOpenDialog(inputFileTxt);
		if (option == JFileChooser.OPEN_DIALOG) {
			File[] selectedFiles = chooser.getSelectedFiles();
            String selectedPaths = Arrays.stream(selectedFiles)
                    .filter(File::canRead)
                    .peek(f -> fileLocationState.setUltimaUbicacionSeleccionada(chooser.getCurrentDirectory().getAbsolutePath()))
                    .map(File::getAbsolutePath)
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");
            inputFileTxt.setText(selectedPaths);
		}
	}

	private void handleStartButtonClick(ActionEvent evt) {
		if (inputFileTxt.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "No se ha seleccionado archivo", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			startBtn.setEnabled(false);
			String[] inputFiles = Arrays.stream(inputFileTxt.getText().split(",")).map(String::trim).toArray(String[]::new);
			DescLoader loader = new DescLoader(env, inputFiles, startTime, endTime);
			
			SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception {
                    loader.addListener(new ProgressBarRepainter());
                    loader.start();
                    return null;
                }
            };
            worker.execute();
		}
	}


	class ProgressBarRepainter implements PropertyChangeListener {
		ProgressBarRepainter() {
			progressBar.setIndeterminate(false);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			SwingUtilities.invokeLater(() -> {
				switch (evt.getPropertyName()) {
                case PROPERTY_INDETERMINATE:
                    progressBar.setIndeterminate((boolean) evt.getNewValue());
                    progressBar.setString("");
                    break;
                case STRING_PROGRESS_BAR:
                    progressBar.setString((String) evt.getNewValue());
                    break;
                case ENABLED_START_BUTTON:
                    startBtn.setEnabled((boolean) evt.getNewValue());
                    break;
                case PERCENT_VALUE_BAR:
                	progressBar.setStringPainted(true);
                    progressBar.setValue((int) evt.getNewValue());
                    break;
                case "text":
                    outputTextArea.append(evt.getNewValue().toString() + "\n");
                    break;
            }
			});
		}
	}

	@Override
	public void onTimeRangeChange(String startTime, String endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
}
