package org.dows.setting.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpellCheckProcessor {

    private static final String API_URL = "http://localhost:11434/v1/chat/completions";
    private static final Set<String> STYLES_TO_SKIP = Set.of("CL", "AU", "EH", "TY", "DOI", "LRH", "RRH", "AF", "AT",
            "AS", "ABKWH", "ABKW", "H1", "cit", "AQ", "H2", "AN", "author", "adate", "atl", "stl", "vol", "iss", "first-page",
            "last-page", "REF", "org", "btl", "city", "pub", "aulabel", "Hyperlink", "CP", "H3", "DR", "Front matter",
            "OQ", "QS", "H4", "H5", "EX", "DI", "PO", "EQ", "EN", "NNUM", "CPB", "TCH", "TT", "TNL", "TBL", "CPSO");
    private static final String INPUT_FOLDER = "D:/before";
    private static final String OUTPUT_FOLDER = "D:/after";

    public static void main(String[] args) {
        processFolder();
    }

    public static void processFolder() {
        try {
            // Ensure folders exist
            Path inputPath = Paths.get(INPUT_FOLDER);
            Path outputPath = Paths.get(OUTPUT_FOLDER);

            if (!Files.exists(inputPath)) {
                Files.createDirectories(inputPath);
                System.out.println("Created input directory: " + INPUT_FOLDER);
            }

            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
                System.out.println("Created output directory: " + OUTPUT_FOLDER);
            }

            // Get all DOCX files in the input folder
            List<Path> docxFiles = Files.list(inputPath)
                    .filter(path -> path.toString().toLowerCase().endsWith(".docx"))
                    .collect(Collectors.toList());

            if (docxFiles.isEmpty()) {
                System.out.println("No DOCX files found in " + INPUT_FOLDER);
                return;
            }

            // Process each file
            for (Path docxFile : docxFiles) {
                try (InputStream inputStream = Files.newInputStream(docxFile)) {
                    String correctedFileName = readAndProcessDocxFile(inputStream, docxFile.getFileName().toString());
                    System.out.println("Processed: " + docxFile.getFileName() + " → " + correctedFileName);

                    // Move the original file to the output folder and delete the original
                    Path destinationPath = Paths.get(OUTPUT_FOLDER, docxFile.getFileName().toString());
                    Files.move(docxFile, destinationPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Moved original file to: " + destinationPath);
                } catch (Exception e) {
                    System.out.println("Error processing " + docxFile.getFileName() + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readAndProcessDocxFile(InputStream inputStream, String originalFileName) throws Exception {
        XWPFDocument doc = new XWPFDocument(inputStream);

        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            if (isParagraphStyleToSkip(paragraph)) {
                continue;
            }

            String originalText = paragraph.getText();
            if (!originalText.trim().isEmpty()) {
                String correctedText = checkText(originalText);
                replaceParagraphText(paragraph, correctedText);
            }
        }
        return writeToFile(doc, originalFileName);
    }

    private static boolean isParagraphStyleToSkip(XWPFParagraph paragraph) {
        String styleId = paragraph.getStyleID();
        if (styleId != null && STYLES_TO_SKIP.contains(styleId.toUpperCase())) {
            return true;
        }

        for (XWPFRun run : paragraph.getRuns()) {
            CTR ctr = run.getCTR();
            if (ctr.isSetRPr()) {
                List<CTString> rStyleList = ctr.getRPr().getRStyleList();
                if (rStyleList == null || rStyleList.isEmpty()) {
                    continue;
                }
                for (CTString rStyle : rStyleList) {
                    if (rStyle != null && STYLES_TO_SKIP.contains(rStyle.getVal().toUpperCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static String checkText(String text) {
        try {
            return callGrammarCheckApi(text);
        } catch (IOException e) {
            e.printStackTrace();
            return text;
        }
    }

    private static String callGrammarCheckApi(String text) throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String payload = "{"
                + "\"model\":\"qwen2.5:3b\","
                + "\"messages\":["
                + "{\"role\":\"system\", \"content\":\"You are an expert copy editor. Your task is to review the provided text and return the corrected version of the text. ONLY fix grammatical errors, spelling mistakes, punctuation issues, and incorrect word usage. DO NOT enhance, rewrite, or improve the sentence in any way. STRICTLY retain all existing quotes exactly as they are, including straight single quotes (''), straight double quotes (\\\"\\\"), curved single quotes (‘ ’), and curved double quotes (“ ”). Do not alter, add, or remove any quotes under any circumstances. STRICTLY retain all brackets exactly as they are, including parentheses (), square brackets [], curly braces {}, and angle brackets <>. Do not alter, add, or remove any brackets under any circumstances. Ensure parallelism in lists or structures is preserved where it exists. Do not alter the original tone, style, or structure of the text. Do not include any explanations, comments, or additional notes.\"},"
                + "{\"role\":\"user\", \"content\":\"" + text + "\"}"
                + "],"
                + "\"temperature\":0.1"
                + "}";

        try (OutputStream os = connection.getOutputStream()) {
            os.write(payload.getBytes(StandardCharsets.UTF_8));
        }

        if (connection.getResponseCode() == 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                return extractCorrectedText(response.toString());
            }
        } else {
            throw new IOException("Error: " + connection.getResponseCode());
        }
    }

    private static String extractCorrectedText(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray choices = jsonObject.getJSONArray("choices");
        return !choices.isEmpty() ? choices.getJSONObject(0).getJSONObject("message").getStr("content") : "No corrected text found.";
    }

    private static void replaceParagraphText(XWPFParagraph paragraph, String newText) {
        paragraph.getRuns().forEach(run -> run.setText("", 0));
        paragraph.createRun().setText(newText);
    }

    private static String writeToFile(XWPFDocument doc, String originalFileName) throws Exception {
        new File(OUTPUT_FOLDER).mkdirs();
        String correctedFileName = "T_" + originalFileName;
        try (FileOutputStream out = new FileOutputStream(OUTPUT_FOLDER + "/" + correctedFileName)) {
            doc.write(out);
        }
        return correctedFileName;
    }
}