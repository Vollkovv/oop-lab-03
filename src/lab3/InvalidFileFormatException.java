package lab3;

import java.io.Serial;

public class InvalidFileFormatException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    private final int lineNumber;
    private final String line;

    public InvalidFileFormatException(int lineNumber, String line) {
        super("Строка " + lineNumber + " не соответствует формату «слово или выражение | перевод»: " + line);
        this.lineNumber = lineNumber;
        this.line = line;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getLine() {
        return line;
    }
}
