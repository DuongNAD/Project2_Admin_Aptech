import java.io.*;
import java.nio.file.*;
import java.util.regex.*;
import java.nio.charset.*;

public class TernaryFixer {
    public static void main(String[] args) throws IOException {
        Path startDir = Paths.get("e:/Project/CourseSalesSystem/PROJECT2_JAVA/PROJECT2_JAVA/src/main/java");
        Files.walk(startDir)
             .filter(Files::isRegularFile)
             .filter(p -> p.toString().endsWith(".java"))
             .forEach(p -> {
                 try {
                     String text = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
                     String original = text;
                     
                     // 1. Fix "var != null  true : false" -> "var != null ? true : false"
                     text = text.replaceAll("(==|!=)\\s+null\\s+([^:;?]+)\\s*:", " null ?  :");
                     
                     // 2. Fix "var == ""  true : false" -> "var == "" ? true : false"
                     text = text.replaceAll("(==|!=)\\s+\"\"\\s+([^:;?]+)\\s*:", " \"\" ?  :");
                     
                     // 3. Fix "method()  true : false" -> "method() ? true : false"
                     text = text.replaceAll("\\(\\)\\s+([^:;?.]+)\\s*:", "() ?  :");
                     
                     // 4. Fix specific known cases
                     text = text.replaceAll("empty\\s+null\\s*:", "empty ? null :");
                     text = text.replaceAll("completed\\s+\"([^\"]*)\"\\s*:", "completed ? \"\" :");
                     
                     // 5. Fix the bad for-each loops from the first regex script
                     text = text.replaceAll("for\\s*\\(\\s*([A-Za-z0-9_<>.?\\[\\]]+)\\s+\\?\\s+([A-Za-z0-9_]+)\\s*:\\s*([A-Za-z0-9_.()]+)\\s*\\)", "for (  : )");

                     // Clean up double ? if regex was greedy
                     text = text.replaceAll("\\?\\s+\\?", "?");
                     
                     if (!text.equals(original)) {
                         Files.write(p, text.getBytes(StandardCharsets.UTF_8));
                         System.out.println("Restored syntax in: " + p.getFileName());
                     }
                 } catch (Exception e) {}
             });
    }
}
