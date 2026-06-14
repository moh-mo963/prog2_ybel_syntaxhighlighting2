package highlighting.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import highlighting.antlr.AntlrTokenCollector; // WICHTIG: Import der Klasse
import highlighting.core.HighlightRegion;
import highlighting.presets.MiniJavaColours;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AntlrTokenCollectorTest {

    private final AntlrTokenCollector highlighter = new AntlrTokenCollector();

    @Test
    void highlightsTokensFromAntlrLexer() {
        String text = "@A class /*x*/";

        // computeRegions ruft intern collectMatches auf
        List<HighlightRegion> regions = highlighter.computeRegions(text);

        // Erwartete Anzahl: @, A, class, /*x*/ (4 Regionen)
        assertEquals(4, regions.size(), "Die Anzahl der Regionen sollte 4 sein.");

        assertEquals(MiniJavaColours.ANNOTATION_COLOUR, regions.get(0).colour());
        assertEquals(MiniJavaColours.ANNOTATION_COLOUR, regions.get(1).colour());
        assertEquals(MiniJavaColours.KEYWORD_COLOUR, regions.get(2).colour());
        assertEquals(MiniJavaColours.BLOCK_COMMENT_COLOUR, regions.get(3).colour());
    }
}
