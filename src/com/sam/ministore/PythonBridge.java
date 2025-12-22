package com.sam.ministore;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PythonBridge {

    public static String call(String... args) {
        try {
            System.out.println("Calling Python: " + String.join(" ", args));
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.redirectErrorStream(true);

            Process p = pb.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            int exit = p.waitFor();
            if (exit != 0) {
                throw new RuntimeException("Python failed: " + out);
}

            return out.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
