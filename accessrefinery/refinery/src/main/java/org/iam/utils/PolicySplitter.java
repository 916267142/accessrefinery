package org.iam.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;

/**
 * Utility for splitting a JSON array of policy objects into individual files.
 * <p>
 * Reads a JSON file containing a list of policy objects, and writes each policy
 * to a separate file in the specified output directory. Output files are formatted
 * with pretty-printed JSON and named sequentially.
 * </p>
 */
public class PolicySplitter {
    public static void main(String[] args) {
        String inputFilePath = "/home/simple/accessrefinery/data/config_bucket_policy_iam_modified.json";
        String outputDir = "/home/simple/accessrefinery/data/RW";

        try {
            Files.createDirectories(Paths.get(outputDir));

            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
            DefaultIndenter indenter = new DefaultIndenter("\t", DefaultIndenter.SYS_LF);
            printer.indentArraysWith(indenter);
            printer.indentObjectsWith(indenter);
            ObjectWriter writer = mapper.writer(printer);

            List<ObjectNode> policies = mapper.readValue(
                new File(inputFilePath),
                new TypeReference<List<ObjectNode>>() {}
            );

            for (int i = 0; i < policies.size(); i++) {
                String filename = String.format("rw_%03d.json", i + 1);
                File outputFile = Paths.get(outputDir, filename).toFile();
                writer.writeValue(outputFile, policies.get(i));
                System.out.println("✅ Generated: " + outputFile.getPath());
            }

            System.out.println("🎉 Policy split completed.");
        } catch (IOException e) {
            System.err.println("❌ Error during processing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
