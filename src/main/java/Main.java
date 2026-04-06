import util.Token;
import util.Token.CAT_CODE;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        // 1. Define your suite of test files
        String[] testFiles = {
                "test_err_keywords.iloc",
                "test_err_registers.iloc",
                "test_err_symbols.iloc",
                "test.iloc" // Keep your master test file at the end as a control!
        };

        System.out.println("Starting Automated Scanner Test Suite...\n");

        // 2. Loop through every file in the array
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
            // Your Scanner constructor likely handles appending "input/"
            Scanner scanner = new Scanner(filename);
            Token currentToken;

            // The Extraction Loop
            do {
                currentToken = scanner.scan();
                printToken(currentToken);
            } while (currentToken.category != CAT_CODE.EOF);

            scanner.closeScanner();

        } catch (IOException e) {
            // If the file is missing or locked, it prints the error but DOES NOT crash the whole suite
            System.err.println("  [!] Fatal Error reading " + filename + ": " + e.getMessage());
        }
        System.out.println(); // Blank line for readability between files
    }

    // Your existing print helper method
    private static void printToken(Token t) {
        // Highlighting ERROR tokens with an indicator to make them pop out in the console
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