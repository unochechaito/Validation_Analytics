/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.validation;

import com.eglobal.tools.validation.db.DBManager;
import com.eglobal.tools.validation.statistics.FileLocationState;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class AppScr {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AppScr.class);
    private JFrame frame;
    private FileLocationState fileLocationState;
    private String env;
    private String[] options = new String[] {"ATM", "POS"};

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                    log.error("Cannot use Look and Feel Flatlaf");
                    try {
                        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                        UIManager.put("ProgressBar.foreground", Color.WHITE);
                        UIManager.put("nimbusOrange", new Color(10, 90, 150));
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e2) {
                        log.error("Cannot use Look and Feel Nimbus");
                        try {
                            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e3) {
                            log.error("Cannot use Look and Feel", e3);
                        }
                    }
                }
                AppScr window = new AppScr();
                window.frame.setVisible(true);
            }
        });
    }

    /**
     * Create the application.
     */
    public AppScr() {
        this.fileLocationState = loadFileLocationState();
        int opt = JOptionPane.showOptionDialog(null, "Selecciona el ambiente", "Ambiente a usar", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        initialize(opt);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize(int opt) {
        frame = new JFrame();
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DBManager.getInstance();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
//             DBManager.getInstance().deleteDB();
                deleteTempFiles();
            }
        });
        frame.getContentPane().setLayout(new BorderLayout(0, 0));
        frame.setResizable(true);
        env = options[opt];
        frame.setTitle("Herramienta de validacion (" + options[opt] + ")");
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        JMenu archivoMenu = new JMenu("Archivo");
        menuBar.add(archivoMenu);
        JMenuItem cerrarMenuItem = new JMenuItem("Cerrar");
        archivoMenu.add(cerrarMenuItem);
        cerrarMenuItem.addActionListener(e -> {
            log.info("Cerrando la aplicacion desde el menu...");
            WindowEvent closingEvent = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closingEvent);
        });
        JMenu reportesMenu = new JMenu("Fotos");
        menuBar.add(reportesMenu);
        JMenuItem generacionFotosMenuItem = new JMenuItem("Generacion de Fotos");
        reportesMenu.add(generacionFotosMenuItem);
        generacionFotosMenuItem.addActionListener(e -> openPhotosWindow());
//        JMenuItem generarMenuItem = new JMenuItem("Comparar");
//        reportesMenu.add(generarMenuItem);
//        generarMenuItem.addActionListener(e -> openReports());
        TimeRangePicker timeRangePicker = new TimeRangePicker();
//        frame.getContentPane().add(timeRangePicker, BorderLayout.NORTH);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        frame.add(timeRangePicker, gbc);
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
//            private static final long serialVersionUID = 1L;
//
//            public Color getForegroundAt(int index) {
//                if(getSelectedIndex() == index) {
//                    return Color.BLACK;
//                }
//                return Color.GRAY;
//            }
//
//            @Override
//            public Color getBackgroundAt(int index) {
//                if(getSelectedIndex() == index) {
//                    return Color.GREEN;
//                }
//                return Color.WHITE;
//            }
//        };
        AcqLoaderPnl acqLoaderPnl = new AcqLoaderPnl(options[opt], fileLocationState);
        DescAdditionalLoaderPnl descAdditionalLoaderPnl = new DescAdditionalLoaderPnl(options[opt], fileLocationState);
        DescLoaderPnl descLoaderPnl = new DescLoaderPnl(options[opt], fileLocationState);
        IssLoaderPnl issLoaderPnl = new IssLoaderPnl(options[opt], fileLocationState);
        timeRangePicker.addTimeRangeListener(acqLoaderPnl, "%02d:%02d:%02d");
        timeRangePicker.addTimeRangeListener(descLoaderPnl, "%02d%02d%02d");
        timeRangePicker.addTimeRangeListener(descAdditionalLoaderPnl, "%02d%02d%02d");
        timeRangePicker.addTimeRangeListener(issLoaderPnl, "%02d:%02d:%02d");
        tabbedPane.add("Carga de Rawcom Adquirente", acqLoaderPnl);
        tabbedPane.add("Carga de Adicionales DESC", descAdditionalLoaderPnl);
        tabbedPane.add("Carga de DESC", descLoaderPnl);
        tabbedPane.add("Carga de Rawcom Emisor", issLoaderPnl);
//        tabbedPane.add("Generacion de Fotos", new PhotosPnl(env));  //Pestaña original
//        tabbedPane.setBackgroundAt(0, Color.RED);
//        tabbedPane.setBackgroundAt(1, Color.BLUE);
//        tabbedPane.setBackgroundAt(2, Color.GREEN);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(tabbedPane, gbc);
//        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

//    private void openReports() {
//        JFrame comparisonWindow = new ComparisonWindow(env);
//        comparisonWindow.setVisible(true);
//    }
    private void openPhotosWindow() {
        JFrame photosFrame = new JFrame("Generacion de Fotos");
        photosFrame.setBounds(150, 150, 600, 500);
        photosFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        PhotosPnl photosPnl = new PhotosPnl(env);
        photosFrame.getContentPane().add(photosPnl, BorderLayout.CENTER);
        JMenuBar menuBar = createPhotosMenuBar(photosPnl);
        photosFrame.setJMenuBar(menuBar);
        photosFrame.setVisible(true);
    }

    private JMenuBar createPhotosMenuBar(PhotosPnl photosPnl) {
        JMenuBar menuBar = new JMenuBar();
        JMenu fotosMenu = new JMenu("Fotos");
        JMenuItem compararFotosItem = new JMenuItem("Comparar Fotos");
        compararFotosItem.addActionListener(e -> photosPnl.openComparisonWindow(env));
        fotosMenu.add(compararFotosItem);
        menuBar.add(fotosMenu);
        return menuBar;
    }

    private void deleteTempFiles() {
//        String tmpPath = System.getProperty("user.dir") + File.separator + "Validation" + File.separator + "tmp";
        File tmpPath = new File("tmp");
        File tmpDir = new File(String.valueOf(tmpPath));
        log.debug("Intentando eliminar archivos en: " + tmpPath);
        if (!tmpDir.exists()) {
            log.warn("El directorio temporal no existe: " + tmpPath);
            return;
        }
        File[] files = tmpDir.listFiles();
        if (files == null || files.length == 0) {
            log.warn("No hay archivos en el directorio temporal.");
            return;
        }
        for (File file : files) {
            log.debug("Revisando archivo: " + file.getName());
            if (file.isFile() && file.getName().endsWith(".db") && !file.getName().equals("validation.db")) {
                if (file.delete()) {
                    log.info("Archivo eliminado: " + file.getName());
                } else {
                    log.error("No se pudo eliminar el archivo (Esta en uso?): " + file.getName());
                }
            }
        }
    }

    private FileLocationState loadFileLocationState() {
//        File file = new File("fileLocationState.ser");
//        if (file.exists()) {
//            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
//                return (FileLocationState) ois.readObject();
//            } catch (IOException | ClassNotFoundException e) {
//                log.error("Error al cargar el estado de la ubicación del archivo", e);
//            }
//        }
        return new FileLocationState();
    }
}
