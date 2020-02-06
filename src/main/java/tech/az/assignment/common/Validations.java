package tech.az.assignment.common;

import java.io.*;
import java.util.Objects;

public class Validations {

    private Validations() {
    }

    public static void validateArguments(String[] args) {
        if (args.length != 3
                || (Objects.isNull(args[0]) || args[0].isEmpty())
                || (Objects.isNull(args[1]) || args[1].isEmpty())
                || (Objects.isNull(args[2]) || args[2].isEmpty())) {
            throw new IllegalArgumentException("You must run this method with 3 arguments first and second one are the " +
                    "file paths and the third one is encoding");
        }
    }

    public static void validateInputFilePaths(String[] filePaths, String encoding) throws IOException {
        for (int i = 0; i < 2; i++) {
            File file = new File(filePaths[i]);
            //noinspection EmptyTryBlock
            try (InputStream in = new FileInputStream(file);
                 Reader reader = new InputStreamReader(in, encoding)) {
            }
        }
        if (!encoding.equalsIgnoreCase("UTF-8")) throw new UnsupportedEncodingException("Encoding must be UTF-8!");
    }

}
