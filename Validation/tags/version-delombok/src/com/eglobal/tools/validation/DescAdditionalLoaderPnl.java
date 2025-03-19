package com.eglobal.tools.validation;


import com.eglobal.tools.validation.statistics.FileLocationState;


import java.io.File;


public class DescAdditionalLoaderPnl extends AbstractDescLoaderPnl {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public DescAdditionalLoaderPnl(String env, FileLocationState fileLocationState) {
        super(env, fileLocationState);
    }


    @Override
    protected String getFileLabelText() {
        return "     Archivo Adicional";
    }


    @Override
    protected String getInitialText() {
        return "Seleccione los adicionales DESC a leer";
    }


    @Override
    protected boolean isValidFile(File file) {
//        return file.getName().endsWith("AD.txt"); // Solo archivos que terminan en "AD.txt"
        return true;
    }
}






//package com.eglobal.tools.validation;
//
//import com.eglobal.tools.validation.files.DescLoader;
//import com.eglobal.tools.validation.statistics.FileLocationState;
//
//import javax.swing.*;
//import javax.swing.text.DefaultCaret;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//import java.io.File;
//import java.util.Arrays;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//public class DescAdditionalLoaderPnl extends JPanel implements TimeRangeListener {
//
//    private static final long serialVersionUID = 1L;
//    private JTextField inputFileTxt;
//    private JProgressBar progressBar;
//    private JButton startBtn;
//    private JTextArea outputTextArea;
//    private FileLocationState fileLocationState;
//    private String startTime;
//    private String endTime;
//    private String env;
//
//    public DescAdditionalLoaderPnl(String env, FileLocationState fileLocationState) {
//
//        this.fileLocationState = fileLocationState;
//        this.env = env;
//
//        setLayout(createGridBagLayout());
//        addComponent(new JLabel("Archivo Adicional"), 0, 0, GridBagConstraints.EAST, 1, 1);
//
//        inputFileTxt = new JTextField(20);
//        inputFileTxt.setEditable(false);
//        addComponent(inputFileTxt, 1, 0, GridBagConstraints.HORIZONTAL, 5, 1);
//
//        JButton inputFileBtn = new JButton("...");
//        inputFileBtn.addActionListener(this::chooseFile);
//        addComponent(inputFileBtn, 6, 0, GridBagConstraints.NONE, 1, 1);
//
//        addComponent(new JLabel("Salida:"), 0, 2, GridBagConstraints.NORTHEAST, 1, 1);
//        outputTextArea = new JTextArea();
//        outputTextArea.setWrapStyleWord(true);
//        outputTextArea.setFont(new Font("Consolas", Font.PLAIN, 13));
//        DefaultCaret caret = (DefaultCaret) outputTextArea.getCaret();
//        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
//        JScrollPane scroll = new JScrollPane(outputTextArea);
//        addComponent(scroll, 1, 2, GridBagConstraints.BOTH, 6, 6);
//        outputTextArea.append("Ambiente seleccionado: " + env + "\n");
//        outputTextArea.append("Seleccione los archivos adicionales DESC a leer\n\n");
//
//        startBtn = new JButton("Start");
//        startBtn.addActionListener(e -> startProcess());
//        addComponent(startBtn, 6, 8, GridBagConstraints.NONE, 1, 1);
//
//        progressBar = new JProgressBar();
//        progressBar.setIndeterminate(false);
//        progressBar.setValue(0);
//        addComponent(progressBar, 0, 9, GridBagConstraints.BOTH, 7, 1);
//
//        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//        ProgressBarRepainter w = new ProgressBarRepainter();
//        executor.schedule(w::repaint, 1, TimeUnit.SECONDS);
//
//    }
//
//
//    private GridBagLayout createGridBagLayout() {
//        GridBagLayout layout = new GridBagLayout();
//        layout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
//        layout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//        layout.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
//        layout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
//        return layout;
//    }
//
//
//    private void addComponent(JComponent component, int x, int y, int fill, int width, int height) {
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.gridx = x;
//        gbc.gridy = y;
//        gbc.gridwidth = width;
//        gbc.gridheight = height;
//        gbc.insets = new Insets(5, 5, 5, 5);
//        gbc.anchor = GridBagConstraints.CENTER;
//        gbc.fill = fill;
//        add(component, gbc);
//    }
//
//
//    private void chooseFile(ActionEvent e) {
//        JFileChooser chooser = new JFileChooser();
//        chooser.setMultiSelectionEnabled(true);
//
//        if (fileLocationState.getUltimaUbicacionSeleccionada() != null) {
//            chooser.setCurrentDirectory(new File(fileLocationState.getUltimaUbicacionSeleccionada()));
//        }
//
//        int option = chooser.showOpenDialog(inputFileTxt);
//        if (option == JFileChooser.OPEN_DIALOG) {
//            File[] selectedFiles = chooser.getSelectedFiles();
//            StringBuilder sb = new StringBuilder();
//            for (File f : selectedFiles) {
////                if (!f.getName().endsWith("AD.txt")) {
////                    JOptionPane.showMessageDialog(this, "Solo se permiten archivos adicionales (terminados en AD.txt)");
////                    return;
////                }
//
//                if (!f.canRead()) {
//                    JOptionPane.showMessageDialog(this, "El archivo no puede ser leído");
//                } else {
//                    if (sb.length() > 0) {
//                        sb.append(",");
//                    }
//                    fileLocationState.setUltimaUbicacionSeleccionada(chooser.getCurrentDirectory().getAbsolutePath());
//                    sb.append(f.getAbsolutePath());
//                }
//            }
//            inputFileTxt.setText(sb.toString());
//        }
//    }
//
//
//    private void startProcess() {
//        EventQueue.invokeLater(() -> {
//            if (inputFileTxt.getText().trim().isEmpty()) {
//                JOptionPane.showMessageDialog(null, "No se ha seleccionado archivo", "Error", JOptionPane.ERROR_MESSAGE);
//            } else {
//                startBtn.setEnabled(false);
//                String[] inputFiles = Arrays.stream(inputFileTxt.getText().split(",")).map(String::trim).toArray(String[]::new);
//                DescLoader loader = new DescLoader(env, inputFiles, startTime, endTime);
//
//                ProgressBarRepainter w = new ProgressBarRepainter();
//                loader.addListener(w);
//
//                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//                executor.schedule(w::repaint, 1, TimeUnit.SECONDS);
//                loader.start();
//                executor.shutdown();
//            }
//        });
//    }
//
//
//    class ProgressBarRepainter implements PropertyChangeListener {
//
//
//        ProgressBarRepainter() {
//            progressBar.setIndeterminate(false);
//        }
//
//        @Override
//        public void propertyChange(PropertyChangeEvent evt) {
//            if ("percent".equals(evt.getPropertyName())) {
//                if (progressBar.getValue() != (int) evt.getNewValue()) {
//                    progressBar.setIndeterminate(false);
//                    progressBar.setStringPainted(true);
//                    progressBar.setValue((int) evt.getNewValue());
//                }
//                if (progressBar.getValue() == 100) {
//                    startBtn.setEnabled(true);
//                }
//            }
//            if ("text".equals(evt.getPropertyName())) {
//                outputTextArea.append(evt.getNewValue().toString());
//                outputTextArea.append("\n");
//            }
//        }
//
//
//        public void repaint() {
//            progressBar.repaint();
//            startBtn.repaint();
//            outputTextArea.repaint();
//        }
//    }
//
//
//    @Override
//    public void onTimeRangeChange(String startTime, String endTime) {
//        this.startTime = startTime;
//        this.endTime = endTime;
//    }
//
//}

