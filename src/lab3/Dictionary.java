package lab3;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Словарь: ключ — слово или выражение в нижнем регистре, слова разделены одним пробелом.
 */
public class Dictionary {

    private static final String SEPARATOR = "|";

    private final Map<String, String> entries = new HashMap<>();
    private int maxWordsInKey;

    public static Dictionary load(Path path) throws FileReadException, InvalidFileFormatException {
        List<String> lines = readLines(path);
        Dictionary dictionary = new Dictionary();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (i == 0 && line.startsWith("﻿")) {
                line = line.substring(1);
            }
            if (!line.isBlank()) {
                dictionary.addLine(i + 1, line);
            }
        }
        return dictionary;
    }

    private static List<String> readLines(Path path) throws FileReadException {
        if (Files.isDirectory(path)) {
            throw new FileReadException(path, "это папка, а не файл", null);
        }
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            throw new FileReadException(path, "файл не существует", e);
        } catch (AccessDeniedException e) {
            throw new FileReadException(path, "нет доступа к файлу", e);
        } catch (CharacterCodingException e) {
            throw new FileReadException(path, "файл должен быть в кодировке UTF-8", e);
        } catch (IOException e) {
            throw new FileReadException(path, e.getMessage(), e);
        }
    }

    private void addLine(int lineNumber, String line) throws InvalidFileFormatException {
        int separatorIndex = line.indexOf(SEPARATOR);
        if (separatorIndex < 0 || separatorIndex != line.lastIndexOf(SEPARATOR)) {
            throw new InvalidFileFormatException(lineNumber, line);
        }
        String key = normalize(line.substring(0, separatorIndex));
        String translation = line.substring(separatorIndex + 1).trim();
        if (key.isEmpty() || translation.isEmpty()) {
            throw new InvalidFileFormatException(lineNumber, line);
        }
        entries.put(key, translation);
        maxWordsInKey = Math.max(maxWordsInKey, key.split(" ").length);
    }

    public static String normalize(String phrase) {
        return Arrays.stream(phrase.trim().split("\\s+"))
                .filter(word -> !word.isEmpty())
                .map(word -> word.toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(" "));
    }

    public Optional<String> translate(String normalizedPhrase) {
        return Optional.ofNullable(entries.get(normalizedPhrase));
    }

    public int getMaxWordsInKey() {
        return maxWordsInKey;
    }

    public int size() {
        return entries.size();
    }
}
