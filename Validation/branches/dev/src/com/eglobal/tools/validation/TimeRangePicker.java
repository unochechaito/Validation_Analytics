/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.validation;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


import javax.swing.*;
import javax.swing.border.TitledBorder;


public class TimeRangePicker extends JPanel {
    private static final long serialVersionUID = 1L;
    private JSpinner startHourSpinner;
    private JSpinner startMinuteSpinner;
    private JSpinner startSecondSpinner;
    private JSpinner endHourSpinner;
    private JSpinner endMinuteSpinner;
    private JSpinner endSecondSpinner;
    //private List<TimeRangeListener> listeners = new ArrayList<>();
    private Map<TimeRangeListener, String> listeners = new HashMap<>();

    public TimeRangePicker() {

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        setBorder(BorderFactory.createEmptyBorder(10, 90, 10, 90));
//        gbc.insets = new Insets(5, 5, 5, 5);
//        gbc.fill = GridBagConstraints.BOTH;
//        gbc.weightx = 1.0;
//        gbc.weighty = 1.0;

        JPanel startPanel = new JPanel(new GridBagLayout());
        startPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIManager.getColor("controlShadow")),
                "Tiempo de muestra inicial",
                TitledBorder.LEADING,
                TitledBorder.TOP
        ));

//        setLayout(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();

        startHourSpinner = createFormattedSpinner(0, 23);
        startMinuteSpinner = createFormattedSpinner(0, 59);
        startSecondSpinner = createFormattedSpinner(0, 59);

        GridBagConstraints startGbc = new GridBagConstraints();
        startGbc.insets = new Insets(5, 5, 5, 5);
        startGbc.anchor = GridBagConstraints.WEST;

        startGbc.gridx = 0;
        startGbc.gridy = 0;
        startPanel.add(new JLabel("Hora:"), startGbc);

        startGbc.gridx = 1;
        startPanel.add(startHourSpinner, startGbc);

        startGbc.gridx = 0;
        startGbc.gridy = 1;
        startPanel.add(new JLabel("Minuto:"), startGbc);

        startGbc.gridx = 1;
        startPanel.add(startMinuteSpinner, startGbc);

        startGbc.gridx = 0;
        startGbc.gridy = 2;
        startPanel.add(new JLabel("Segundo:"), startGbc);

        startGbc.gridx = 1;
        startPanel.add(startSecondSpinner, startGbc);

        JPanel endPanel = new JPanel(new GridBagLayout());
        endPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIManager.getColor("controlShadow")),
                "Tiempo de muestra final",
                TitledBorder.LEADING,
                TitledBorder.TOP
        ));


        endHourSpinner = createFormattedSpinner(0, 23);
        endHourSpinner.setValue(23);
        endMinuteSpinner = createFormattedSpinner(0, 59);
        endMinuteSpinner.setValue(59);
        endSecondSpinner = createFormattedSpinner(0, 59);
        endSecondSpinner.setValue(59);

        GridBagConstraints endGbc = new GridBagConstraints();
        endGbc.insets = new Insets(5, 5, 5, 5);
        endGbc.anchor = GridBagConstraints.WEST;

        endGbc.gridx = 0;
        endGbc.gridy = 0;
        endPanel.add(new JLabel("Hora:"), endGbc);

        endGbc.gridx = 1;
        endPanel.add(endHourSpinner, endGbc);

        endGbc.gridx = 0;
        endGbc.gridy = 1;
        endPanel.add(new JLabel("Minuto:"), endGbc);

        endGbc.gridx = 1;
        endPanel.add(endMinuteSpinner, endGbc);

        endGbc.gridx = 0;
        endGbc.gridy = 2;
        endPanel.add(new JLabel("Segundo:"), endGbc);

        endGbc.gridx = 1;
        endPanel.add(endSecondSpinner, endGbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(startPanel, gbc);

        JPanel spacerPanel = new JPanel();
        spacerPanel.setOpaque(false);
        gbc.gridx = 1;
        gbc.weightx = 0.3;
        add(spacerPanel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        add(endPanel, gbc);

        addChangeListener(startHourSpinner);
        addChangeListener(startMinuteSpinner);
        addChangeListener(startSecondSpinner);
        addChangeListener(endHourSpinner);
        addChangeListener(endMinuteSpinner);
        addChangeListener(endSecondSpinner);

    }


    private JSpinner createFormattedSpinner(int minValue, int maxValue) {
        SpinnerModel model = new SpinnerNumberModel(0, minValue, maxValue, 1);
        JSpinner spinner = new JSpinner(model);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "00");
        DecimalFormat format = editor.getFormat();
        format.setMinimumIntegerDigits(2);
        spinner.setEditor(editor);
        return spinner;
    }


    private void addChangeListener(JSpinner spinner) {
        spinner.addChangeListener(e -> notifyOnChange());
    }


    public void addTimeRangeListener(TimeRangeListener listener, String format) {
        listeners.put(listener, format);

        // Notify listeners about the initial time values
        notifyOnChange(listener, format);
    }


    private void notifyOnChange(TimeRangeListener listener, String format) {
        String startTime = getStartTime(format);
        String endTime = getEndTime(format);
        listener.onTimeRangeChange(startTime, endTime);
    }


    private void notifyOnChange() {
        for (Map.Entry<TimeRangeListener, String> item : listeners.entrySet()) {
            String startTime = getStartTime(item.getValue());
            String endTime = getEndTime(item.getValue());
            item.getKey().onTimeRangeChange(startTime, endTime);
        }
    }


    public String getStartTime(String format) {
        int hour = (int) startHourSpinner.getValue();
        int minute = (int) startMinuteSpinner.getValue();
        int second = (int) startSecondSpinner.getValue();
        return String.format(format, hour, minute, second);
    }


    public String getEndTime(String format) {
        int hour = (int) endHourSpinner.getValue();
        int minute = (int) endMinuteSpinner.getValue();
        int second = (int) endSecondSpinner.getValue();
        return String.format(format, hour, minute, second);
    }

}
