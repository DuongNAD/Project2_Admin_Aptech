using System;
using System.IO;
using System.Text.RegularExpressions;
using System.Text;

public class CodeFixer {
    public static void Main() {
        string dir = @""e:\Project\CourseSalesSystem\PROJECT2_JAVA\PROJECT2_JAVA\src\main\java"";
        string[] files = Directory.GetFiles(dir, ""*.java"", SearchOption.AllDirectories);

        foreach (string file in files) {
            string text = File.ReadAllText(file, Encoding.UTF8);
            string original = text;

            // Fix broken for-each loops: for (Type ? var : list) -> for (Type var : list)
            text = Regex.Replace(text, @""for\s*\(\s*([A-Za-z0-9_<>.?\[\]]+)\s+\?\s+([A-Za-z0-9_]+)\s*:\s*([A-Za-z0-9_.()]+)\s*\)"", ""for ($1 $2 : $3)"");
            
            // Fix broken ternary operator assignments: something != null  val : other -> something != null ? val : other
            text = Regex.Replace(text, @""(==|!=)\s+null\s+([^:;?]+)\s*:"", ""$1 null ? $2 :"");
            text = Regex.Replace(text, @""(==|!=)\s+""""\s+([^:;?]+)\s*:"", ""$1 """" ? $2 :"");
            text = Regex.Replace(text, @""\(\)\s+([^:;.?]+)\s*:"", ""() ? $1 :"");

            if (text != original) {
                File.WriteAllText(file, text, new UTF8Encoding(false));
                Console.WriteLine(""Fixed syntax in: "" + Path.GetFileName(file));
            }
        }
        Console.WriteLine(""Done."");
    }
}
