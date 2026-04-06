import util.Token;
import util.Token.CAT_CODE;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        String[] testFiles = {
                "test_err_keywords.iloc",
                "test_err_registers.iloc",
                "test_err_symbols.iloc",
                "test.iloc"
        };

        System.out.println("=== Starting Testing ===\n");

        for (String filename : testFiles) {
            runTestOnFile(filename);
        }

        System.out.println("\n=== All Tests Completed ===");
    }

    // A modular method to handle a single file
    private static void runTestOnFile(String filename) {
        System.out.println("==========================================");
        System.out.println("  RUNNING TEST: " + filename);
        System.out.println("==========================================");

        try {
            Scanner scanner = new Scanner(filename);
            Token currentToken;

            do {
                currentToken = scanner.scan();
                printToken(currentToken);
            } while (currentToken.category != CAT_CODE.EOF);

            scanner.closeScanner();

        } catch (IOException e) {
            System.err.println(" Error reading " + filename + ": " + e.getMessage());
        }
        System.out.println();
    }

    private static void printToken(Token t) {
        String prefix = (t.category == CAT_CODE.ERROR) ? ">>> " : "    ";

        System.out.printf("%sLine: %-3d Col: %-3d | Category: %-10s",
                prefix, t.pos.line, t.pos.column, t.category);

        if (t.category == CAT_CODE.CONSTANT || t.category == CAT_CODE.REGISTER) {
            System.out.printf(" | Lexeme: %d", t.lexeme);
        } else if (t.opCode != Token.OP_CODE.NONE) {
            System.out.printf(" | OpCode: %s", t.opCode);
        }

        System.out.println();
    }
}