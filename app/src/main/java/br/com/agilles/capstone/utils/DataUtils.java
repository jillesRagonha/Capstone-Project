package br.com.agilles.capstone.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataUtils {
    public static final String DIA_MES = "dd/MM/yyyy";

    public static String dataEmTexto(int dayOfMonth, int month, int year) {
        Calendar data = Calendar.getInstance();
        data.set(year, month,dayOfMonth);

        SimpleDateFormat formatoBrasileiroData = new SimpleDateFormat(DIA_MES);
        String dataFormatada = formatoBrasileiroData.format(data.getTime());
        return dataFormatada;
    }
}
