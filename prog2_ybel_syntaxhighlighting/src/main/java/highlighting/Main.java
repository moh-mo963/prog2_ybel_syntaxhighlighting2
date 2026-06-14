package highlighting;

import highlighting.antlr.MiniJavaLexer;
import highlighting.antlr.MiniJavaParser;
import highlighting.antlr.PrettyPrinterVisitor;
import java.util.Scanner;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Main {

    private static final String[] EXAMPLE_NAMES = {
        "Einfache Klasse mit Feld und Methode",
        "Methode mit if/else und while",
        "Verschachtelte Bloecke"
    };

    private static final String[] EXAMPLES = {
        "class Box{private Item item;public Item get(){return item;}}",
        """
    class Flow{public Item run(Item item){if(item==null)return null;else{while(item!=null){item=next;}return item;}}}
    """,
        """
    class Nested{public Item work(Item input){{Item current=input;{current=new Item();}}return input;}}
    """
    };

    public static void main(String... args) {

        try (var scanner = new Scanner(System.in)) {
            int indentWidth = readIndentWidth(scanner);

            for (int i = 0; i < EXAMPLES.length; i++) {
                System.out.println();
                System.out.println("=== " + EXAMPLE_NAMES[i] + " ===");
                System.out.println(prettyPrint(EXAMPLES[i], indentWidth));
            }
        }
    }

    private static int readIndentWidth(Scanner scanner) {
        System.out.print("Leerzeichen pro Einrueckstufe (z.B. 2, 4 oder 8): ");
        String input = scanner.nextLine().trim();

        try {
            return Math.max(0, Integer.parseInt(input));
        } catch (NumberFormatException e) {
            System.out.println("Keine gueltige Zahl, verwende 2 Leerzeichen.");
            return 2;
        }
    }

    private static String prettyPrint(String source, int indentWidth) {
        MiniJavaParser.CompilationUnitContext tree = parse(source);
        var visitor = new PrettyPrinterVisitor(indentWidth);
        visitor.visit(tree);
        return visitor.result();
    }

    private static MiniJavaParser.CompilationUnitContext parse(String source) {
        var lexer = new MiniJavaLexer(CharStreams.fromString(source));
        var tokens = new CommonTokenStream(lexer);
        var parser = new MiniJavaParser(tokens);
        return parser.compilationUnit();
    }
}
