package lab3;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    private static final String DEFAULT_DICTIONARY = "dictionary.txt";

    public static void main(String[] args) {
        Path dictionaryPath = Path.of(args.length > 0 ? args[0] : DEFAULT_DICTIONARY);

        Dictionary dictionary;
        try {
            dictionary = Dictionary.load(dictionaryPath);
        } catch (FileReadException | InvalidFileFormatException e) {
            System.out.println("Ошибка: " + e.getMessage());
            return;
        }

        System.out.println("Загружен словарь " + dictionaryPath.toAbsolutePath() + ", записей: " + dictionary.size());
        System.out.println("Введите текст для перевода (пустая строка — выход):");

        Translator translator = new Translator(dictionary);
        Scanner in = new Scanner(System.in, consoleCharset());
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.isBlank()) {
                break;
            }
            System.out.println(translator.translate(line));
        }
    }

    private static Charset consoleCharset() {
        String encoding = System.getProperty("stdin.encoding");
        return encoding != null && Charset.isSupported(encoding) ? Charset.forName(encoding) : Charset.defaultCharset();
    }
}
