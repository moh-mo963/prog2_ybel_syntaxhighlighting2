package highlighting.presets;

import highlighting.regex.Token;
import java.util.List;
import java.util.regex.Pattern;

public final class MiniJavaTokens {

    // TODO (Phase I+II: RegexHighlighter/ScanningHighlighter)
    // TODO: Define the MiniJava tokens used by the highlighters. Each token is a mapping from a
    // regular expression to a colour (and, if applicable, a specific matching group). The order of
    // tokens in this list determines their relative priority during highlighting. One example token
    // definition is provided below; define the remaining tokens in an analogous way.

    // Basic token set for MiniJava. Extend this list with further tokens as needed (e.g. identifiers,
    // numeric literals, operators, brackets, whitespace), following the same pattern. Each token is
    // defined by a regular expression and a colour. Optionally, a specific capturing group within the
    // pattern can be selected as the "highlighted" region.
    public static List<Token> defaultTokens() {
        return List.of(
            // Example: string literals (students should define further tokens below)
            Token.of(Pattern.compile("\"([^\"\\\\]|\\\\.)*\""), MiniJavaColours.STRING_LITERAL_COLOUR),
            Token.of(
                Pattern.compile("/\\*\\*(?s).*?\\*/", Pattern.MULTILINE),
                MiniJavaColours.JAVADOC_COMMENT_COLOUR),
            Token.of(
                Pattern.compile("/\\*(?s).*?\\*/", Pattern.MULTILINE),
                MiniJavaColours.BLOCK_COMMENT_COLOUR),
            Token.of(Pattern.compile("//.*$", Pattern.MULTILINE), MiniJavaColours.LINE_COMMENT_COLOUR),
            Token.of(Pattern.compile("\'[\\w]\'"), MiniJavaColours.CHAR_LITERAL_COLOUR),
            Token.of(
                Pattern.compile(
                    "\\bpackage\\b|\\bimport\\b|\\bclass\\b|\\bpublic\\b|\\bprivate\\b|"
                        + "\\bfinal\\b|\\breturn\\b|\\bnull\\b|\\bnew\\b|\\bint\\b"),
                MiniJavaColours.KEYWORD_COLOUR),
            Token.of(Pattern.compile("@[a-zA-Z-]+"), MiniJavaColours.ANNOTATION_COLOUR)

            // TODO: Define additional tokens for MiniJava, e.g. character literals, keywords,
            // annotations, comments, identifiers, numbers, operators, etc.
        );
    }
}
