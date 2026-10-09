package lab3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Переводит текст, выбирая в каждой позиции выражение из словаря с самой длинной левой частью.
 */
public class Translator {

    private static final Pattern WORD = Pattern.compile("[\\p{L}\\p{N}]+(?:['’-][\\p{L}\\p{N}]+)*");

    private final Dictionary dictionary;

    public Translator(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public String translate(String text) {
        List<Token> tokens = tokenize(text);
        StringBuilder result = new StringBuilder();

        int i = 0;
        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (!token.word()) {
                result.append(token.text());
                i++;
                continue;
            }

            Match match = findLongestMatch(tokens, i);
            if (match != null) {
                result.append(match.translation());
                i = match.nextIndex();
            } else {
                result.append(token.text());
                i++;
            }
        }
        return result.toString();
    }

    private Match findLongestMatch(List<Token> tokens, int start) {
        List<Integer> wordEnds = new ArrayList<>();
        StringBuilder phrase = new StringBuilder();

        int i = start;
        while (i < tokens.size() && wordEnds.size() < dictionary.getMaxWordsInKey()) {
            if (!wordEnds.isEmpty()) {
                if (i + 1 >= tokens.size() || !tokens.get(i).text().isBlank() || !tokens.get(i + 1).word()) {
                    break;
                }
                phrase.append(' ');
                i++;
            }
            phrase.append(tokens.get(i).text());
            wordEnds.add(i + 1);
            i++;
        }

        String[] words = Dictionary.normalize(phrase.toString()).split(" ");
        for (int count = wordEnds.size(); count > 0; count--) {
            String candidate = String.join(" ", List.of(words).subList(0, count));
            Optional<String> translation = dictionary.translate(candidate);
            if (translation.isPresent()) {
                return new Match(translation.get(), wordEnds.get(count - 1));
            }
        }
        return null;
    }

    private static List<Token> tokenize(String text) {
        List<Token> tokens = new ArrayList<>();
        Matcher matcher = WORD.matcher(text);
        int position = 0;
        while (matcher.find()) {
            if (matcher.start() > position) {
                tokens.add(new Token(text.substring(position, matcher.start()), false));
            }
            tokens.add(new Token(matcher.group(), true));
            position = matcher.end();
        }
        if (position < text.length()) {
            tokens.add(new Token(text.substring(position), false));
        }
        return tokens;
    }

    private record Token(String text, boolean word) {
    }

    private record Match(String translation, int nextIndex) {
    }
}
