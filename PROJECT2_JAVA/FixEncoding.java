import java.nio.file.*;
import java.nio.charset.*;
import java.io.IOException;

public class FixEncoding {
    public static void main(String[] args) throws IOException {
        Path startDir = Paths.get("e:/Project/CourseSalesSystem/PROJECT2_JAVA/PROJECT2_JAVA/src");
        Files.walk(startDir)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".fxml") || p.toString().endsWith(".java"))
                .forEach(p -> {
                    try {
                        byte[] bytesCount = Files.readAllBytes(p);
                        String text = new String(bytesCount, StandardCharsets.UTF_8);
                        if (text.contains("Ã") || text.contains("Â")) {
                            // Fix double encoding
                            byte[] isoBytes = text.getBytes(StandardCharsets.ISO_8859_1);
                            String fixedText = new String(isoBytes, StandardCharsets.UTF_8);
                            if (!text.equals(fixedText) && !fixedText.contains("Ã") && !fixedText.contains("?")) {
                                Files.write(p, fixedText.getBytes(StandardCharsets.UTF_8));
                                System.out.println("Fixed encoding for: " + p);
                            }
                        }
                    } catch (Exception e) {
                    }
                });
        System.out.println("Done check.");
    }
}
