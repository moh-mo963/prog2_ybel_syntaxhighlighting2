package highlighting.antlr;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

/// MiniJava Pretty Printer (minimal, stateful)
///
/// Requirements:
/// - Reproduce the whole program (comments and whitespaces are gone).
/// - Ignore whitespace from the input; instead, generate:
///     - indentation for class bodies and blocks,
///     - exactly one line per statement (lines ending in ';').
///
/// Simplification:
/// Everything that is not indentation or line breaks is printed as raw tokens (with a very simple
/// space heuristic). Expression and signature formatting is therefore not "nice", which is
/// acceptable for this exercise.
public final class PrettyPrinterVisitor extends MiniJavaBaseVisitor<Void> {

    private final StringBuilder out = new StringBuilder();
    private final int indentWidth;
    private int currentIndent = 0;
    private boolean atLineStart = true;

    // For simple spacing between tokens:
    private Token lastToken = null;

    public PrettyPrinterVisitor(int indentWidth) {
        this.indentWidth = Math.max(0, indentWidth);
    }

    public String result() {
        return out.toString();
    }

    // ----------------------------------------------------
    // Structural methods – these enforce indentation and "one statement per line"
    //
    // TODO: implement the four structural visitXyz-methods below: visitCompilationUnit,
    // visitClassBody, visitBlock, and visitStatement
    // ----------------------------------------------------

    @Override
    public Void visitCompilationUnit(MiniJavaParser.CompilationUnitContext ctx) {
        if (ctx.packageDecl() != null) {
            visit(ctx.packageDecl());
            endLine();
            if (!ctx.importDecl().isEmpty() || !ctx.typeDecl().isEmpty()) blankLine();
        }

        for (var importDecl : ctx.importDecl()) {
            visit(importDecl);
            endLine();
        }
        if (!ctx.importDecl().isEmpty() && !ctx.typeDecl().isEmpty()) blankLine();

        for (int i = 0; i < ctx.typeDecl().size(); i++) {
            visit(ctx.typeDecl(i));
            endLine();
            if (i + 1 < ctx.typeDecl().size()) blankLine();
        }

        return null;
    }

    @Override
    public Void visitClassBody(MiniJavaParser.ClassBodyContext ctx) {
        visit(ctx.getChild(0)); // {
        nl();

        currentIndent++;
        for (var declaration : ctx.classBodyDeclaration()) {
            visit(declaration);
            endLine();
        }
        currentIndent--;

        visit(ctx.getChild(ctx.getChildCount() - 1)); // }
        return null;
    }

    @Override
    public Void visitBlock(MiniJavaParser.BlockContext ctx) {
        visit(ctx.getChild(0)); // {
        nl();

        currentIndent++;
        for (var statement : ctx.blockStatement()) {
            visit(statement);
            endLine();
        }
        currentIndent--;

        visit(ctx.getChild(ctx.getChildCount() - 1)); // }
        return null;
    }

    @Override
    public Void visitStatement(MiniJavaParser.StatementContext ctx) {
        if (ctx.getChild(0) instanceof MiniJavaParser.BlockContext) {
            visit(ctx.getChild(0));
            return null;
        }

        int firstTokenType = ctx.getStart().getType();
        if (firstTokenType == MiniJavaLexer.IF) {
            visit(ctx.getChild(0)); // if
            visit(ctx.getChild(1)); // (
            visit(ctx.expression());
            visit(ctx.getChild(3)); // )
            visitNestedStatement(ctx.statement(0));

            if (ctx.statement().size() > 1) {
                endLine();
                visit(ctx.getChild(5)); // else
                visitNestedStatement(ctx.statement(1));
            }
            return null;
        }

        if (firstTokenType == MiniJavaLexer.WHILE) {
            visit(ctx.getChild(0)); // while
            visit(ctx.getChild(1)); // (
            visit(ctx.expression());
            visit(ctx.getChild(3)); // )
            visitNestedStatement(ctx.statement(0));
            return null;
        }

        visitChildren(ctx);
        endLine();
        return null;
    }

    // ---------------- helper methods ----------------

    private void visitNestedStatement(MiniJavaParser.StatementContext statement) {
        if (statement.getChild(0) instanceof MiniJavaParser.BlockContext) {
            write(" ");
            visit(statement);
        } else {
            nl();
            currentIndent++;
            visit(statement);
            currentIndent--;
        }
    }

    private void endLine() {
        if (!atLineStart) nl();
    }

    private void blankLine() {
        endLine();
        if (out.length() == 0 || out.charAt(out.length() - 1) != '\n') nl();
        if (out.length() < 2 || out.charAt(out.length() - 2) != '\n') nl();
    }

    private void indent() {
        if (atLineStart) {
            out.repeat(" ", Math.max(0, indentWidth * currentIndent));
            atLineStart = false;
        }
    }

    private void write(String s) {
        if (s == null || s.isEmpty()) return;
        indent();
        out.append(s);
    }

    private void nl() {
        out.append('\n');
        atLineStart = true;
        lastToken = null; // Reset spacing context at the beginning of a line
    }

    private void writeln(String s) {
        write(s);
        nl();
    }

    // --------------- token output + basic spacing ---------------

    @Override
    public Void visitTerminal(TerminalNode node) {
        Token t = node.getSymbol();
        String text = t.getText();

        if (lastToken != null) {
            int prevType = lastToken.getType();
            int curType = t.getType();

            // Simple heuristic: insert a space between "word-like" tokens
            if (needsSpaceBetween(prevType, curType)) write(" ");
        }

        write(text);
        lastToken = t;
        return null;
    }

    private boolean needsSpaceBetween(int prevType, int curType) {
        return isWordLike(prevType) && isWordLike(curType);
    }

    private boolean isWordLike(int type) {
        return type == MiniJavaLexer.IDENTIFIER
            || type == MiniJavaLexer.STRING_LITERAL
            || type == MiniJavaLexer.CHAR_LITERAL
            || type == MiniJavaLexer.NULL
            || type == MiniJavaLexer.PACKAGE
            || type == MiniJavaLexer.IMPORT
            || type == MiniJavaLexer.CLASS
            || type == MiniJavaLexer.PUBLIC
            || type == MiniJavaLexer.PRIVATE
            || type == MiniJavaLexer.FINAL
            || type == MiniJavaLexer.RETURN
            || type == MiniJavaLexer.NEW
            || type == MiniJavaLexer.IF
            || type == MiniJavaLexer.ELSE
            || type == MiniJavaLexer.WHILE
            || type == MiniJavaLexer.EXTENDS
            || type == MiniJavaLexer.IMPLEMENTS;
    }
}
