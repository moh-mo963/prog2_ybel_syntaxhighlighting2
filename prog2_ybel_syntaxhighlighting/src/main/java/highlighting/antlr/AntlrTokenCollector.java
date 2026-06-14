package highlighting.antlr;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.MiniJavaColours;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.*;

// Importiere den MiniJavaLexer (stelle sicher, dass er generiert wurde!)
// Falls er in einem anderen Paket generiert wurde, passe den Import entsprechend an:
// import deine.generierte.paket.pfad.MiniJavaLexer;

public class AntlrTokenCollector extends SyntaxHighlighter {

    @Override
    public List<HighlightRegion> collectMatches(String text) {
        var lexer = new MiniJavaLexer(CharStreams.fromString(text));
        var stream = new CommonTokenStream(lexer);
        stream.fill();

        var tokens = stream.getTokens();
        var regions = new ArrayList<HighlightRegion>();

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            int type = token.getType();

            if (type == Token.EOF) continue;

            Color colour = colourFor(type);
            if (colour != null) {
                regions.add(new HighlightRegion(token.getStartIndex(), token.getStopIndex() + 1, colour));
            }

            // Annotation-Logik: @ + IDENTIFIER
            if (type == MiniJavaLexer.AT && i + 1 < tokens.size()) {
                Token next = tokens.get(i + 1);
                if (next.getType() == MiniJavaLexer.IDENTIFIER
                    && next.getStartIndex() == token.getStopIndex() + 1) {
                    regions.add(new HighlightRegion(
                        next.getStartIndex(),
                        next.getStopIndex() + 1,
                        MiniJavaColours.ANNOTATION_COLOUR));
                }
            }
        }
        return regions;
    }

    private Color colourFor(int tokenType) {
        return switch (tokenType) {
            case MiniJavaLexer.STRING_LITERAL -> MiniJavaColours.STRING_LITERAL_COLOUR;
            case MiniJavaLexer.CHAR_LITERAL -> MiniJavaColours.CHAR_LITERAL_COLOUR;
            case MiniJavaLexer.PACKAGE, MiniJavaLexer.IMPORT, MiniJavaLexer.CLASS,
                 MiniJavaLexer.PUBLIC, MiniJavaLexer.PRIVATE, MiniJavaLexer.FINAL,
                 MiniJavaLexer.RETURN, MiniJavaLexer.NULL, MiniJavaLexer.NEW,
                 MiniJavaLexer.IF, MiniJavaLexer.ELSE, MiniJavaLexer.WHILE,
                 MiniJavaLexer.EXTENDS, MiniJavaLexer.IMPLEMENTS -> MiniJavaColours.KEYWORD_COLOUR;
            case MiniJavaLexer.LINE_COMMENT -> MiniJavaColours.LINE_COMMENT_COLOUR;
            case MiniJavaLexer.JAVADOC_COMMENT -> MiniJavaColours.JAVADOC_COMMENT_COLOUR;
            case MiniJavaLexer.BLOCK_COMMENT -> MiniJavaColours.BLOCK_COMMENT_COLOUR;
            case MiniJavaLexer.AT -> MiniJavaColours.ANNOTATION_COLOUR;
            default -> null;
        };
    }
}
