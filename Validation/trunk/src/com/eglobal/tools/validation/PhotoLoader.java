/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.validation;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class PhotoLoader extends JPanel {
    private JComboBox<String> photoComboBox;
    private List<Photo> availablePhotos;
    private String env;
    private boolean defaultItemDisplayed = true;
    private boolean suppressSelectionEvent = false;
    private CsvComparator csvComparator;
    private String leftDirectory;
    private String rightDirectory;

    public PhotoLoader(String env, JPanel panel, CsvComparator csvComparator) {
        this.env = env;
        this.csvComparator = csvComparator;
        this.leftDirectory = leftDirectory;
        this.rightDirectory = rightDirectory;
        setLayout(new GridBagLayout());
        JLabel lblPhoto = new JLabel("Foto:");
        GridBagConstraints gbc_lblPhoto = new GridBagConstraints();
        gbc_lblPhoto.anchor = GridBagConstraints.EAST;
        gbc_lblPhoto.insets = new Insets(10, 10, 25, 5);
        gbc_lblPhoto.gridx = 0;
        gbc_lblPhoto.gridy = 0;
        add(lblPhoto, gbc_lblPhoto);
        photoComboBox = new JComboBox<>();
        photoComboBox.addItem("Seleccionar foto");
        GridBagConstraints gbc_photoComboBox = new GridBagConstraints();
        gbc_photoComboBox.insets = new Insets(10, 5, 25, 5);
        gbc_photoComboBox.fill = GridBagConstraints.EAST;
        gbc_photoComboBox.gridx = 1;
        gbc_photoComboBox.gridy = 0;
        add(photoComboBox, gbc_photoComboBox);
        loadPhotoNames();
        photoComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                if (defaultItemDisplayed) {
                    ActionListener[] listeners = photoComboBox.getActionListeners();
                    for (ActionListener listener : listeners) {
                        photoComboBox.removeActionListener(listener);
                    }
                    photoComboBox.removeItemAt(0);
                    defaultItemDisplayed = false;
                    for (ActionListener listener : listeners) {
                        photoComboBox.addActionListener(listener);
                    }
                }
            }
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        photoComboBox.addActionListener(e -> {
            if (suppressSelectionEvent) {
                suppressSelectionEvent = false;
            } else {
                String selectedPhotoName = (String) photoComboBox.getSelectedItem();
                if (selectedPhotoName != null && !"Seleccionar foto".equals(selectedPhotoName)) {
                    if (csvComparator == null) {
                        JOptionPane.showMessageDialog(null, "El comparador CSV no esta inicializado.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (leftDirectory == null || leftDirectory.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El directorio Base no esta configurado.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (rightDirectory == null || rightDirectory.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El directorio A comparar no esta configurado.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        csvComparator.compareCsvFiles(leftDirectory, rightDirectory, selectedPhotoName);
                    } catch (IOException ioException) {
                        JOptionPane.showMessageDialog(null, "Error al comparar archivos: " + ioException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    executeCsvComparison(selectedPhotoName);
                }
            }
        });
    }

    private void loadPhotoNames() {
        Gson gson = new Gson();
        try (
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("resources/photos/photos" + env + ".json");
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            if (inputStream != null) {
                JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
                java.lang.reflect.Type photoListType = new TypeToken<List<Photo>>() {
                }.getType();
                availablePhotos = gson.fromJson(jsonObject.get("photos"), photoListType);
                for (Photo photo : availablePhotos) {
                    if (photo.isComparable()) {
                        photoComboBox.addItem(photo.getName());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "El archivo photos.json no se encuentra en resources.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los nombres de las fotos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getSelectedPhoto() {
        return (String) photoComboBox.getSelectedItem();
    }

    public void setLoadActionListener(ActionListener actionListener) {
        photoComboBox.addActionListener(actionListener);
    }

    public void executeCsvComparison(String selectedPhoto) {
        if (leftDirectory == null || rightDirectory == null) {
            JOptionPane.showMessageDialog(this, "Los directorios Base y A comparar no estan configurados.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            csvComparator.compareCsvFiles(leftDirectory, rightDirectory, selectedPhoto);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al comparar archivos CSV: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @java.lang.SuppressWarnings("all")
    public void setLeftDirectory(final String leftDirectory) {
        this.leftDirectory = leftDirectory;
    }

    @java.lang.SuppressWarnings("all")
    public void setRightDirectory(final String rightDirectory) {
        this.rightDirectory = rightDirectory;
    }
}
