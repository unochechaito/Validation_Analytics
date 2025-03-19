/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.validation.files;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;


public class TimeIntervalChecker {


//    private final SimpleDateFormat sdf;
//
//    public TimeIntervalChecker(String timeFormat) {
//        this.sdf = new SimpleDateFormat(timeFormat);
//    }


    private final DateTimeFormatter formatter;         //Esta clase ya es a prueba de hilos
    private static final LocalDateTime REFERENCE_DATE = LocalDateTime.of(2024, 8, 21, 3, 0);


    public TimeIntervalChecker(String timeFormat) {
        this.formatter = DateTimeFormatter.ofPattern(timeFormat);
    }


    public boolean isTimeWithinInterval(String time, String startTime, String endTime) {
        try {
            // Parse all times with the specified format
//            Date timeToCheck = sdf.parse(time);
//            Date start = sdf.parse(startTime);
//            Date end = sdf.parse(endTime);


            LocalTime timeToCheck = LocalTime.parse(time, formatter);       //Uso de la nueva variable formatter
            LocalTime start = LocalTime.parse(startTime, formatter);
            LocalTime end = LocalTime.parse(endTime, formatter);


            // Check if the time is within the interval
//            return !timeToCheck.before(start) && !timeToCheck.after(end);
            return !timeToCheck.isBefore(start) && !timeToCheck.isAfter(end);    //Refactorizando el "return"
        } catch (DateTimeParseException e) {                                     //Nuevo tipo de excepción
            e.printStackTrace();
            return false;
        }
    }

//    public boolean isPeriodWithinInterval(String period, String startTime, String endTime) {
//      try {
//         LocalTime start = LocalTime.parse(startTime, formatter);
//            LocalTime end = LocalTime.parse(endTime, formatter);
//
//            int down = start.getHour() * 2 + (start.getMinute() >= 30 ? 1 : 0) - 1;
//            int top = end.getHour() * 2 + (end.getMinute() >= 30 ? 1 : 0) - 1;
//
//            int p = Integer.parseInt(period);
//            return (p >= down) && (p <= top);
//      } catch (DateTimeParseException e) {                                     //Nuevo tipo de excepción
//            e.printStackTrace();
//            return false;
//        }
//    }


    public boolean isPeriodWithinInterval(String period, String startTime, String endTime, String lineDate) {
        try {
            LocalDate trxDate = LocalDate.parse(lineDate, DateTimeFormatter.ofPattern("yyMMdd"));
            long daysSinceReference = ChronoUnit.DAYS.between(REFERENCE_DATE.toLocalDate(), trxDate);
            int dayOfCycle = (int) (daysSinceReference % 2);


            LocalTime start = LocalTime.parse(startTime, formatter);
            LocalTime end = LocalTime.parse(endTime, formatter);


            int down = start.getHour() * 2 + (start.getMinute() >= 30 ? 1 : 0);
            int top = end.getHour() * 2 + (end.getMinute() >= 30 ? 1 : 0);

//			Este cambio de momento no aplica (validado con DESC de produccion
            if (dayOfCycle == 1) {
                down += 48;
                top += 48;
            }


            int p = Integer.parseInt(period);


            return (p >= down) && (p <= top);


        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void main(String[] args) {
        String time = "185131";
        String startTime = "185140";
        String endTime = "185142";
        String timeFormat = "HHmmss";


        TimeIntervalChecker checker = new TimeIntervalChecker(timeFormat);
        boolean result = checker.isTimeWithinInterval(time, startTime, endTime);
        System.out.println("Is the time within the interval? " + result);
    }
}



