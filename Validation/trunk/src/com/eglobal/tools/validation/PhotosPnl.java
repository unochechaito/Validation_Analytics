package com.eglobal.tools.validation;


import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;


import com.eglobal.tools.parser.utils.FileSystem;
import com.eglobal.tools.validation.photos.PhotoShooter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


public class PhotosPnl extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextField inputFileTxt;
    private JButton generateBtn;
    private JComboBox<String> photoComboBox;
    private PhotoTableModel tableModel;
    private JTable photoTable;
    private List<PhotoItem> photos;
    private List<Photo> availablePhotos;
    private JProgressBar progressBar;


    private ProgressBarRepainter w;
    private File canonicalDirectorySelected;
    private String env;


    public PhotosPnl(String env) {
        setLayout(createGridBagLayout());
        this.env = env;


        addComponent(new JLabel("Directorio: "), 0, 0, GridBagConstraints.EAST, 1, 1);
        inputFileTxt = new JTextField(20);
        addComponent(inputFileTxt, 1, 0, GridBagConstraints.HORIZONTAL, 3, 1);


        JButton inputFileBtn = new JButton("...");
        inputFileBtn.addActionListener(this::chooseFile);
        addComponent(inputFileBtn, 4, 0, GridBagConstraints.NONE, 1, 1);


        addComponent(new JLabel("Foto: "), 0, 1, GridBagConstraints.EAST, 1, 1);
        photoComboBox = new JComboBox<>();
        loadPhotoNames();
        addComponent(photoComboBox, 1, 1, GridBagConstraints.HORIZONTAL, 3, 1);


        JButton addPhotoBtn = new JButton("Agregar");
        addPhotoBtn.addActionListener(this::addPhoto);
        addComponent(addPhotoBtn, 4, 1, GridBagConstraints.NONE, 1, 1);


        JButton addAllPhotosBtn = new JButton("Agregar Todas");
        addAllPhotosBtn.addActionListener(this::addAllPhotos);
        addComponent(addAllPhotosBtn, 5, 1, GridBagConstraints.NONE, 1, 1);


        photos = new ArrayList<>();
        tableModel = new PhotoTableModel(photos);
        photoTable = new JTable(tableModel);
        setTableProperties();


        JScrollPane tableScrollPane = new JScrollPane(photoTable);
        addComponent(tableScrollPane, 0, 2, GridBagConstraints.BOTH, 6, 5);


        generateBtn = new JButton("Generar");
        addComponent(generateBtn, 5, 7, GridBagConstraints.NONE, 1, 1);
        generateBtn.addActionListener(this::takePhotos);


        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setValue(0);
        addComponent(progressBar, 0, 8, GridBagConstraints.BOTH, 8, 1);


        addTableMouseListener();
        addTableKeyListener();


        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        w = new ProgressBarRepainter();
        executor.schedule(() -> w.repaint(), 1, TimeUnit.SECONDS);
    }


    public void openComparisonWindow(String env) {
        JFrame comparisonWindow = new ComparisonWindow(env);
        comparisonWindow.setVisible(true);
    }


    private void takePhotos(ActionEvent e) {
        if (canonicalDirectorySelected == null || !canonicalDirectorySelected.exists()) {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado un directorio destino", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        List<Photo> selectedPhotos = new ArrayList<>();
        for (PhotoItem pi : photos) {
            Photo p = availablePhotos.stream().filter(photo -> photo.getId().equals(pi.id)).findFirst().orElse(null);
            if (p != null) {
                selectedPhotos.add(p);
            }
        }
        if(selectedPhotos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado al menos una foto", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        generateBtn.setEnabled(false);
        PhotoShooter ps = null;
        ps = new PhotoShooter(selectedPhotos, canonicalDirectorySelected);
        ps.addListener(w);
        ps.start();
    }


    private void loadPhotoNames() {
        Gson gson = new Gson();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("resources/photos/photos"+env+".json");
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            if (inputStream != null) {
                JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
                Type photoListType = new TypeToken<List<Photo>>() {}.getType();
                availablePhotos = gson.fromJson(jsonObject.get("photos"), photoListType);
                for (Photo photo : availablePhotos) {
                    photoComboBox.addItem(photo.getName());
                }
            } else {
                JOptionPane.showMessageDialog(this, "El archivo photos.json no se encuentra en resources.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los nombres de las fotos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void setTableProperties() {
        photoTable.setPreferredScrollableViewportSize(new Dimension(500, 100));
        TableColumn idColumn = photoTable.getColumnModel().getColumn(0);
        idColumn.setPreferredWidth(50);
        idColumn.setMinWidth(50);
        idColumn.setMaxWidth(50);
    }


    private void addPhoto(ActionEvent e) {
        String selectedPhotoName = (String) photoComboBox.getSelectedItem();
        if (selectedPhotoName != null && !selectedPhotoName.isEmpty()) {
            boolean exists = photos.stream().anyMatch(photo -> photo.name.equals(selectedPhotoName));
            if (!exists) {
                Photo selectedPhoto = availablePhotos.stream()
                        .filter(photo -> photo.getName().equals(selectedPhotoName))
                        .findFirst().orElse(null);
                if (selectedPhoto != null) {
                    PhotoItem item = new PhotoItem(selectedPhoto.getId(), selectedPhoto.getName());
                    photos.add(item);
                    tableModel.fireTableDataChanged();
                }
            } else {
                JOptionPane.showMessageDialog(this, "La foto ya ha sido agregada.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void addAllPhotos(ActionEvent e) {
        for (Photo photo : availablePhotos) {
            boolean exists = photos.stream().anyMatch(p -> p.name.equals(photo.getName()));
            if (!exists) {
                PhotoItem item = new PhotoItem(photo.getId(), photo.getName());
                photos.add(item);
            }
        }
        tableModel.fireTableDataChanged();
    }


    private void deleteSelectedPhotos() {
        int[] selectedRows = photoTable.getSelectedRows();
        if (selectedRows.length > 0) {
            List<PhotoItem> itemsToRemove = new ArrayList<>();
            for (int selectedRow : selectedRows) {
                itemsToRemove.add(photos.get(selectedRow));
            }
            photos.removeAll(itemsToRemove);
            tableModel.fireTableDataChanged();
        }
    }


    private void addComponent(JComponent component, int x, int y, int fill, int width, int height) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.fill = fill;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        add(component, gbc);
    }


    private GridBagLayout createGridBagLayout() {
        GridBagLayout layout = new GridBagLayout();
        layout.columnWidths = new int[]{100, 200, 100, 100, 100, 100};
        layout.rowHeights = new int[]{30, 30, 30, 30, 30, 30, 100, 30, 30};
        layout.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0};
        layout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        return layout;
    }


    private void chooseFile(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = chooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION && FileSystem.isAllowedPath.test(chooser.getSelectedFile().getPath())) {
            File f = chooser.getSelectedFile();
            if (f != null && f.isDirectory()) {  // Validación añadida
                try {
                    inputFileTxt.setText(f.getCanonicalPath());
                    canonicalDirectorySelected = chooser.getSelectedFile().getCanonicalFile();
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(this, "Error al obtener la ruta del directorio.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un directorio válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void addTableMouseListener() {
        photoTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int[] selectedRows = photoTable.getSelectedRows();
                    if (selectedRows.length > 0) {
                        JPopupMenu popup = new JPopupMenu();
                        JMenuItem deleteSelected = new JMenuItem("Eliminar Seleccionados");
                        deleteSelected.addActionListener(evt -> deleteSelectedPhotos());
                        popup.add(deleteSelected);
                        popup.show(photoTable, e.getX(), e.getY());
                    }
                }
            }
        });
    }


    private void addTableKeyListener() {
        photoTable.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteSelectedPhotos();
                }
            }
        });
    }


    @Deprecated
    private File validateAndNormalizePath(String path) throws IOException {
        try {
            Path normalizedPath = Paths.get(path).normalize().toAbsolutePath();


            File canonicalFile = normalizedPath.toFile().getCanonicalFile();


            // Validar si la ruta está permitida
            if (!FileSystem.isAllowedPath.test(canonicalFile.getPath())) {
                throw new IOException("Ruta no permitida: " + path);
            }


            return canonicalFile;
        } catch (InvalidPathException e) {
            throw new IOException("Ruta no valida: " + path, e);
        }
    }




    class PhotoTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private final List<PhotoItem> photoData;
        private final String[] columnNames = {"ID", "Nombre de la Fotografia"};


        public PhotoTableModel(List<PhotoItem> photos) {
            this.photoData = photos;
        }


        @Override
        public int getRowCount() {
            return photoData.size();
        }


        @Override
        public int getColumnCount() {
            return columnNames.length;
        }


        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            PhotoItem item = photoData.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return item.id;
                case 1:
                    return item.name;
                default:
                    return null;
            }
        }


        @Override
        public String getColumnName(int index) {
            return columnNames[index];
        }
    }


    static class PhotoItem {
        private final String id;
        private final String name;


        public PhotoItem(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }


    class ProgressBarRepainter implements PropertyChangeListener {


        ProgressBarRepainter() {
            progressBar.setIndeterminate(false);
        }


        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("percent".equals(evt.getPropertyName())) {
                if (progressBar.getValue() != (int) evt.getNewValue()) {
                    progressBar.setIndeterminate(false);
                    progressBar.setStringPainted(true);
                    progressBar.setValue((int) evt.getNewValue());
                }
                if (progressBar.getValue() == 100) {
                    generateBtn.setEnabled(true);
                }
            }
        }


        public void repaint() {
            progressBar.repaint();
            generateBtn.repaint();
        }
    }
}





