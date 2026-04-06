package util;

public class Token {
    public CAT_CODE category;
    public OP_CODE opCode;
    public int lexeme;
    public Position pos;

    public Token() {
        pos = new Position();
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
