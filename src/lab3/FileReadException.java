package lab3;

import java.io.Serial;
import java.nio.file.Path;

public class FileReadException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String path;

    public FileReadException(Path path, String reason, Throwable cause) {
        super("Не удалось прочитать файл " + path + ": " + reason, cause);
        this.path = path.toString();
    }

    public String getPath() {
        return path;
    }
}
