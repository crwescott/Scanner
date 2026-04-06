import java.io.IOException;
import java.io.PushbackReader;
import java.io.FileReader;

import util.Token.CAT_CODE;
import util.Token.OP_CODE;
import util.Token;

public class Scanner {
    private String filename;
    private final PushbackReader reader;

    private int line = 1;
    private int column = 1;

    private int prevLine = 1;

    public Scanner(String sourceFilename) throws IOException {
        filename = "input/" + sourceFilename;
        reader = new PushbackReader(new FileReader(filename));

    }

    private static final int MAX_STATES = 100; // Plenty of room for Lab 1
    private static final int MAX_LEXEME_LENGTH = 4096;
    private static final int ERROR_STATE = -1;

    private static final TokenInfo[] FINAL_STATES = new TokenInfo[MAX_STATES];
    private static final int[][] TABLE = new int[MAX_STATES][256];

    private static class TokenInfo {
        CAT_CODE category;
        OP_CODE opCode;

        TokenInfo(CAT_CODE category, OP_CODE opCode) {
            this.category = category;
            this.opCode = opCode;
        }
    }

    // State 0 is START
    private static int nextAvailableState = 1;

    static {
        initializeTable();
    }


    private static void setupRegisters() {
        int afterRState = nextAvailableState++;
        TABLE[0]['r'] = afterRState;

        int registerState = nextAvailableState++;
        FINAL_STATES[registerState] = new TokenInfo(CAT_CODE.REGISTER, OP_CODE.NONE);

        for (char c = '0'; c <= '9'; c++) {
            TABLE[afterRState][c] = registerState;   // After-R -> digit -> REGISTER
            TABLE[registerState][c] = registerState;
        }
    }

    private static void setupConstants() {
        int constantState = nextAvailableState++;
        FINAL_STATES[constantState] = new TokenInfo(CAT_CODE.CONSTANT, OP_CODE.NONE);

        for (char c = '0'; c <= '9'; c++) {
            TABLE[0][c] = constantState;             // START -> digit -> CONSTANT
            TABLE[constantState][c] = constantState; // CONSTANT -> digit -> CONSTANT (The Loop)
        }
    }

    private static void initializeTable() {
        for (int i = 0; i < MAX_STATES; i++) {
            for (int j = 0; j < 256; j++) {
                TABLE[i][j] = ERROR_STATE;
            }
        }

        setupRegisters();
        setupConstants();

        addKeyword("load",   CAT_CODE.MEMOP,   OP_CODE.LOAD);
        addKeyword("loadI",  CAT_CODE.LOADI,   OP_CODE.LOADI);
        addKeyword("store",  CAT_CODE.MEMOP,   OP_CODE.STORE);
        addKeyword("add",    CAT_CODE.ARITHOP, OP_CODE.ADD);
        addKeyword("sub",    CAT_CODE.ARITHOP, OP_CODE.SUB);
        addKeyword("mult",   CAT_CODE.ARITHOP, OP_CODE.MULT);
        addKeyword("lshift", CAT_CODE.ARITHOP, OP_CODE.LSHIFT);
        addKeyword("rshift", CAT_CODE.ARITHOP, OP_CODE.RSHIFT);
        addKeyword("output", CAT_CODE.OUTPUT,  OP_CODE.OUTPUT);
        addKeyword("nop",    CAT_CODE.NOP,     OP_CODE.NOP);
        addKeyword("=>",     CAT_CODE.INTO,    OP_CODE.NONE);
        addKeyword(",",      CAT_CODE.COMMA,   OP_CODE.NONE);
    }

    private static void addKeyword(String word, CAT_CODE cat, OP_CODE op) {
        int currentState = 0;
        for (char c : word.toCharArray()) {
            if (TABLE[currentState][c] == ERROR_STATE) {
                TABLE[currentState][c] = nextAvailableState++;
            }
            currentState = TABLE[currentState][c];
        }

        FINAL_STATES[currentState] = new TokenInfo(cat, op);
    }

    private int nextChar() throws IOException {
        return reader.read();
    }

    private void pushChar(int c) throws IOException {
        if (c != -1) reader.unread(c);
    }

    public int peekChar() throws IOException {
        int c = reader.read();
        if (c != -1) reader.unread(c);
        return c;
    }

    private int removeFiller(int c) throws IOException {
        while (c != -1) {
            if (Character.isWhitespace((char) c) && c != '\n' && c != '\r') {
                column++;
                c = nextChar();
            } else if (c == '/') {
                if (peekChar() == '/') {
                    column += 2;
                    c = nextChar();
                    c = nextChar();

                    while (c != -1 && c != '\n' && c != '\r') {
                        column++;
                        c = nextChar();
                    }

                } else {
                    break;
                }
            } else {
                break;
            }
        }

        return c;
    }

    public Token scan() throws IOException {
        int currentState = 0; // START
        StringBuilder lexeme = new StringBuilder();

        int c = nextChar();
        c = removeFiller(c);

        Token token = new Token();
        token.category = CAT_CODE.ERROR;
        token.opCode = OP_CODE.NONE;
        token.pos.line = line;
        token.pos.column = column;

        if (c == -1) {
            token.category = CAT_CODE.EOF;
            return token;
        }

        if (c == '\n' || c == '\r') {
            token.category = CAT_CODE.EOL;

            if (c == '\r' && peekChar() == '\n') {
                nextChar(); // Consume the '\n' so it doesn't trigger again
            }

            line++;
            column = 1;

            return token;
        }

        while ((c != -1) && (lexeme.length() < MAX_LEXEME_LENGTH)) {
            int nextState = TABLE[currentState][c];

            if (nextState == ERROR_STATE) {
                if (lexeme.isEmpty()) {
                    lexeme.append((char) c);
                    column++;
                } else {
                    pushChar(c);
                }
                break;
            }

            currentState = nextState;
            lexeme.append((char)c);
            column++;

            c = nextChar();
        }

        TokenInfo info = FINAL_STATES[currentState];

        if (info != null) {
            token.category = info.category;
            token.opCode = info.opCode;

            String text = lexeme.toString();

            if (token.category == CAT_CODE.CONSTANT) {
                token.lexeme = Integer.parseInt(text);
            } else if (token.category == CAT_CODE.REGISTER) {
                token.lexeme = Integer.parseInt(text.substring(1)); // Skip the 'r'
            }
        }

        return token;
    }

    public void closeScanner() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
