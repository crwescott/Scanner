package util;

public class Token {
    public CAT_CODE category;
    public OP_CODE opCode;
    public int lexeme;
    public Position pos;

    public Token(int line, int col) {
        this.category = CAT_CODE.ERROR;
        this.opCode = OP_CODE.NONE;
        this.pos = new Position(line, col);
    }

    public enum CAT_CODE {
        MEMOP,
        LOADI,
        ARITHOP,
        OUTPUT,
        NOP,
        CONSTANT,
        REGISTER,
        COMMA,
        INTO,
        EOF,
        EOL,
        ERROR;
    }

    public enum OP_CODE {
        LOAD,
        STORE,
        LOADI,
        ADD,
        SUB,
        MULT,
        LSHIFT,
        RSHIFT,
        OUTPUT,
        NOP,
        NONE;
    }
}
