package highlighting.regex;

import static org.junit.jupiter.api.Assertions.*;

import highlighting.core.HighlightRegion;
import highlighting.presets.MiniJavaColours;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RegexHighlighterTest {

    private final RegexHighlighter highlighter = new RegexHighlighter();

    @Test
    void simpleNonOverlapping() {
        String text = "String x = \"hello\";";
        var regions = highlighter.computeRegions(text);
        var expected =
            List.of(new HighlightRegion(11, text.length() - 1, MiniJavaColours.STRING_LITERAL_COLOUR));
        assertEquals(expected, regions);
    }

    @Test
    void keywordInsideComment() {
        String text = "/* int */ int";
        var regions = highlighter.computeRegions(text);
        var expected =
            List.of(
                new HighlightRegion(0, 9, MiniJavaColours.BLOCK_COMMENT_COLOUR),
                new HighlightRegion(10, text.length(), MiniJavaColours.KEYWORD_COLOUR));
        assertEquals(expected, regions);
    }

    @Test
    void javadocWinsOverBlockComment() {
        String text = "/** javadoc */";
        var regions = highlighter.computeRegions(text);
        // only the javadoc region should be kept
        assertEquals(1, regions.size());
        var r = regions.get(0);
        assertEquals(0, r.start());
        assertEquals(text.length(), r.end());
        assertEquals(MiniJavaColours.JAVADOC_COMMENT_COLOUR, r.colour());
    }

    @Test
    void adjacentRegionsKept() {
        String text = "\"a\"\"b\""; // two adjacent string literals
        var regions = highlighter.computeRegions(text);
        var expected =
            List.of(
                new HighlightRegion(0, 3, MiniJavaColours.STRING_LITERAL_COLOUR),
                new HighlightRegion(3, 6, MiniJavaColours.STRING_LITERAL_COLOUR));
        assertEquals(expected, regions);
    }

    @Test
    void emptyAndNoMatches() {
        assertTrue(highlighter.computeRegions("").isEmpty());
        assertTrue(highlighter.computeRegions("no tokens here").isEmpty());
    }
}
