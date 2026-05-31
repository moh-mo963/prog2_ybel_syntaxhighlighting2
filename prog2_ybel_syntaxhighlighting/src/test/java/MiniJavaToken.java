package highlighting;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MiniJavaToken - Tests")
class MiniJavaToken {

    @Test
    void TokenAmAnfang() {
        // 1. Arrange: Input-Text mit dem Keyword direkt am Anfang
        String text = "package de.meinprojekt;";

        // Dein Regex für das Keyword "package"
        Pattern pattern = Pattern.compile("\\bpackage\\b");
        var matcher = pattern.matcher(text);

        // 2. Act: Wir prüfen, ob der Matcher etwas findet mo
        boolean gefunden = matcher.find();

        // 3. Assert: Es muss ein Match existieren UND er muss bei Index 0 (Anfang) starten
        assertTrue(gefunden, "Das Token sollte im Text gefunden werden.");
        assertEquals(0, matcher.start(), "Das Token muss direkt am Anfang (Index 0) beginnen.");
        assertEquals("package", matcher.group(), "Das gefundene Token muss exakt 'package' lauten.");
    }

    @Test
    void TokenAmEnde() {
        // 1. Arrange: Input-Text mit dem Keyword direkt am Anfang
        String text = "return null";

        // Dein Regex für das Keyword "package"
        Pattern pattern = Pattern.compile("\\bnull\\b");
        var matcher = pattern.matcher(text);

        // 2. Act: Wir prüfen, ob der Matcher etwas findet
        boolean gefunden = matcher.find();

        // 3. Assert: Es muss ein Match existieren UND am Ende sein
        assertTrue(gefunden, "Das Token sollte im Text gefunden werden.");
        assertEquals(text.length(), matcher.end(), "Das Token muss am Ende stehen.");
        assertEquals("null", matcher.group(), "Das gefundene Token muss exakt 'null' lauten.");
    }

    @Test
    void TokenInMitte() {
        // 1. Arrange: Input-Text mit dem Keyword direkt am Anfang
        String text = "return null;";

        // Dein Regex für das Keyword "package"
        Pattern pattern = Pattern.compile("\\bnull\\b");
        var matcher = pattern.matcher(text);

        // 2. Act: Wir prüfen, ob der Matcher etwas findet
        boolean gefunden = matcher.find();

        // 3. Assert: Es muss ein Match existieren UND am Ende sein
        assertTrue(gefunden, "Das Token sollte im Text gefunden werden.");
        assertEquals(text.length() - 1, matcher.end(), "Das Token muss bei length-1 stehen.");
        assertEquals("null", matcher.group(), "Das gefundene Token muss exakt 'null' lauten.");
    }

    @Test
    void MehrInEinemText() {
        String text = "return null;";

        Pattern pattern = Pattern.compile("\\breturn\\b|\\bnull\\b");
        var matcher = pattern.matcher(text);

        assertTrue(matcher.find(), "Das Token sollte im Text gefunden werden.");
        assertEquals(0, matcher.start(), "Das Token muss am Anfang stehen.");
        assertEquals("return", matcher.group(), "Das gefundene Token muss exakt 'return' lauten.");

        assertTrue(matcher.find(), "Das Token sollte im Text gefunden werden.");
        assertEquals(text.length() - 1, matcher.end(), "Das Token muss bei length-1 stehen.");
        assertEquals("null", matcher.group(), "Das gefundene Token muss exakt 'null' lauten.");
    }

    @Test
    void KeywordInKommentarWirdIgnoriert() {
        String text = "// Das ist ein return im Kommentar";

        Pattern commentPattern = Pattern.compile("//.*$", Pattern.MULTILINE);
        Pattern keywordPattern = Pattern.compile("\\breturn\\b");

        var commentMatcher = commentPattern.matcher(text);
        boolean istKommentar = commentMatcher.find();

        assertTrue(istKommentar, "Der gesamte Text sollte als Kommentar erkannt werden.");
        assertEquals("// Das ist ein return im Kommentar", commentMatcher.group());

        var keywordMatcher = keywordPattern.matcher(text);

        if (keywordMatcher.find()) {
            int keywordStart = keywordMatcher.start();
            int commentStart = commentMatcher.start();
            int commentEnd = commentMatcher.end();

            assertTrue(
                keywordStart >= commentStart && keywordStart < commentEnd,
                "Das gefundene Keyword liegt fälschlicherweise außerhalb des Kommentars.");
        }
    }

    @Test
    void KommentarZeichenInEinemString() {

        String text = "\"Dieser String enthält // und /* Zeichen\"";

        Pattern stringPattern = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"");
        Pattern lineCommentPattern = Pattern.compile("//.");
        Pattern blockCommentPattern = Pattern.compile("/\\*(?s).*?\\*/");

        var stringMatcher = stringPattern.matcher(text);
        boolean istString = stringMatcher.find();

        assertTrue(istString, "Der Text sollte als String erkannt werden.");
        assertEquals(
            text,
            stringMatcher.group(),
            "Der Match muss den kompletten String inklusive Anführungszeichen umfassen.");
        assertEquals(0, stringMatcher.start(), "Der String beginnt bei Index 0.");
        assertEquals(text.length(), stringMatcher.end(), "Der String geht bis zum Ende des Textes.");

        var lineCommentMatcher = lineCommentPattern.matcher(text);
        var blockCommentMatcher = blockCommentPattern.matcher(text);

        if (lineCommentMatcher.find()) {
            assertTrue(
                lineCommentMatcher.start() > stringMatcher.start()
                    && lineCommentMatcher.end() < stringMatcher.end(),
                "Der Zeilenkommentar-Regex hat fälschlicherweise Text innerhalb des Strings getroffen.");
        }

        if (blockCommentMatcher.find()) {
            assertTrue(
                blockCommentMatcher.start() > stringMatcher.start()
                    && blockCommentMatcher.end() < stringMatcher.end(),
                "Der Blockkommentar-Regex hat fälschlicherweise Text innerhalb des Strings getroffen.");
        }
    }

    @Test
    void TokenVorKlammer() {
        // 1. Arrange: Input-Text mit dem Keyword gefolgt von einer Klammer
        String text = "if(x) { }";

        Pattern pattern = Pattern.compile("\\bif\\b");
        var matcher = pattern.matcher(text);

        // 2. Act
        boolean gefunden = matcher.find();

        // 3. Assert
        assertTrue(gefunden, "Das Token sollte im Text gefunden werden.");
        assertEquals(0, matcher.start(), "Das Token muss am Anfang stehen.");
        assertEquals("if", matcher.group(), "Das gefundene Token muss exakt 'if' lauten.");
    }

    @Test
    void KeinMatchInnerhalbIdentifier() {
        // 1. Arrange: Keyword als Teil eines längeren Identifiers
        String text = "diff";

        Pattern pattern = Pattern.compile("\\bif\\b");
        var matcher = pattern.matcher(text);

        // 2. Act
        boolean gefunden = matcher.find();

        // 3. Assert: Es darf kein Match geben
        assertFalse(gefunden, "Das Token 'if' darf nicht innerhalb von 'diff' gematcht werden.");
    }

    @Test
    void KeywordInBlockKommentarWirdIgnoriert() {
        // 1. Arrange: Blockkommentar mit Keyword
        String text = "/* Hier steht return im Blockkommentar */";

        Pattern blockCommentPattern = Pattern.compile("/\\*(?s).*?\\*/");
        Pattern keywordPattern = Pattern.compile("\\breturn\\b");

        var blockMatcher = blockCommentPattern.matcher(text);
        boolean istBlock = blockMatcher.find();

        assertTrue(istBlock, "Der Text sollte als Blockkommentar erkannt werden.");
        assertEquals("/* Hier steht return im Blockkommentar */", blockMatcher.group());

        var keywordMatcher = keywordPattern.matcher(text);
        if (keywordMatcher.find()) {
            int kwStart = keywordMatcher.start();
            int blockStart = blockMatcher.start();
            int blockEnd = blockMatcher.end();

            assertTrue(
                kwStart >= blockStart && kwStart < blockEnd,
                "Das gefundene Keyword liegt fälschlicherweise außerhalb des Blockkommentars.");
        }
    }

    @Test
    void EscapedQuoteImStringEnthältKeyword() {
        // 1. Arrange: String mit escaped quotes und Keyword darin
        String text = "\"Er sagt \\\"return\\\" hier\"";

        Pattern stringPattern = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"");
        Pattern keywordPattern = Pattern.compile("\\breturn\\b");

        var stringMatcher = stringPattern.matcher(text);
        boolean istString = stringMatcher.find();

        assertTrue(istString, "Der Text sollte als String erkannt werden.");
        assertEquals(0, stringMatcher.start(), "Der String beginnt bei Index 0.");
        assertEquals(text.length(), stringMatcher.end(), "Der String geht bis zum Ende des Textes.");

        var keywordMatcher = keywordPattern.matcher(text);
        if (keywordMatcher.find()) {
            int kwStart = keywordMatcher.start();
            assertTrue(
                kwStart > stringMatcher.start() && kwStart < stringMatcher.end(),
                "Das Keyword 'return' muss innerhalb des Strings liegen.");
        }
    }
}
