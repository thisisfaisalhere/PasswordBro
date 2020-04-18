package com.virusX.passwordBro;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;

class CSVWriter {

    private PrintWriter pw;

    private static final char DEFAULT_SEPARATOR = ',';

    private static final String DEFAULT_LINE_END = "\n";

    CSVWriter(Writer writer) {
        this.pw = new PrintWriter(writer);
    }

    void writeNext(ArrayList<String> dataList) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < dataList.size(); i++) {
            stringBuilder.append(dataList.get(i));
            if(i < dataList.size() - 1) {
                stringBuilder.append(DEFAULT_SEPARATOR);
            }
        }
        stringBuilder.append(DEFAULT_LINE_END);
        pw.write(stringBuilder.toString());
    }

    void flush() throws IOException {
        pw.flush();
    }

    void close() throws IOException {
        pw.flush();
        pw.close();
    }

}