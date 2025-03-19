/*
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion
*   de correo seginfo@eglobal.com.mx
*/
package com.eglobal.tools.validation;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
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

    public AcqLoaderPnl(String env, FileLocationState fileLocationState) {
        this.fileLocationState = fileLocationState;
        this.env = env;
        initializeUI();
    }

    private void initializeUI() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);
        addInputFileComponents();
        addOutputTextArea();
        addStartButton();
        addProgressBar();
    }

    private void addInputFileComponents() {
        JLabel inputFileLbl = new JLabel("     Archivo de entrada");
        addComponent(inputFileLbl, 2, 1, 1, 1, GridBagConstraints.EAST, new Insets(10, 5, 10, 0));

        JButton inputFileBtn = new JButton("Seleccionar");
        inputFileBtn.addActionListener(this::handleFileSelection);
        addComponent(inputFileBtn, 3, 1, 1, 1, GridBagConstraints.CENTER, new Insets(10, 0, 10, 0));

        inputFileTxt = new JTextField(20);
        inputFileTxt.setEditable(false);
        addComponent(inputFileTxt, 4, 1, 6, 1, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 5));

        firstLoadCb = new JCheckBox("Primera carga");
        addComponent(firstLoadCb, 10, 1, 1, 1, GridBagConstraints.EAST, new Insets(10, 20, 10, 110));
    }

    private void addOutputTextArea() {
        outputTextArea = new JTextArea();
        outputTextArea.setWrapStyleWord(true);
        outputTextArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        DefaultCaret caret = (DefaultCaret) outputTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        outputTextArea.append("Ambiente seleccionado: " + env + "\n");
        outputTextArea.append("Seleccione los archivos Rawcom adquirentes a leer\n\n");

        JScrollPane scroll = new JScrollPane(outputTextArea);
        addComponent(scroll, 0, 2, 11, 4, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5));
    }

    private void addStartButton() {
        startBtn = new JButton("Start");
        startBtn.addActionListener(this::handleStartButtonClick);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 5);
        gbc.gridx = 10;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.EAST;
        add(startBtn, gbc);
    }

    private void addProgressBar() {
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setValue(0);
        addComponent(progressBar, 0, 10, 11, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5));
    }

    private void addComponent(Component component, int gridx, int gridy, int gridwidth, int gridheight, int fill,
            Insets insets) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.fill = fill;
        gbc.insets = insets;
        add(component, gbc);
    }

    private void handleFileSelection(java.awt.event.ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        if (fileLocationState.getUltimaUbicacionSeleccionada() != null) {
            chooser.setCurrentDirectory(new File(fileLocationState.getUltimaUbicacionSeleccionada()));
        }
        chooser.setMultiSelectionEnabled(true);
        int option = chooser.showOpenDialog(inputFileTxt);
        if (option == JFileChooser.OPEN_DIALOG) {
            File[] selectedFiles = chooser.getSelectedFiles();
            String selectedPaths = Arrays.stream(selectedFiles)
                    .filter(File::canRead)
                    .peek(f -> fileLocationState
                            .setUltimaUbicacionSeleccionada(chooser.getCurrentDirectory().getAbsolutePath()))
                    .map(File::getAbsolutePath)
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");
            inputFileTxt.setText(selectedPaths);
        }
    }

    private void handleStartButtonClick(java.awt.event.ActionEvent e) {
        if (inputFileTxt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No se ha seleccionado archivo", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            startBtn.setEnabled(false);
            String[] inputFiles = Arrays.stream(inputFileTxt.getText().split(",")).map(String::trim)
                    .toArray(String[]::new);
            AcqRawcomLoader loader = new AcqRawcomLoader(env, inputFiles, startTime, endTime);
            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception {
                    if (firstLoadCb.isSelected()) {
                        publish("*********Generando tablas nuevas*********\n");
                        DBManager.getInstance().setUpDB();
                    }
                    loader.addListener(new ProgressBarRepainter());
                    loader.start();
                    return null;
                }

                @Override
                protected void process(List<String> chunks) {
                    chunks.forEach(outputTextArea::append);
                }
            };
            worker.execute();
        }
    }

    private class ProgressBarRepainter implements PropertyChangeListener {
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
                    case "percent":
                        progressBar.setStringPainted(true);
                        progressBar.setValue((int) evt.getNewValue());
                        break;
                    case "text":
                        outputTextArea.append(evt.getNewValue().toString() + "\n");
                        break;
                    default:
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