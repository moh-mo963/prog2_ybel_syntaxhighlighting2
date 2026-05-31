grammar MiniJava;

// --------------------
// Package declaration
// --------------------
@header {
package highlighting.antlr;
}


// --------------------
// Parser rules
// --------------------
compilationUnit: packageDecl? importDecl* typeDecl* EOF ;

packageDecl: 'package' qualifiedName ';' ;
importDecl: 'import' qualifiedName ('.' '*')? ';' ;

typeDecl: classDecl ;
classDecl: modifier* 'class' IDENTIFIER ( 'extends' qualifiedName )? ( 'implements' qualifiedName (',' qualifiedName)* )? classBody ;
classBody: '{' classBodyDeclaration* '}' ;
classBodyDeclaration: memberDecl | ';' ;
memberDecl: fieldDecl | methodDecl | constructorDecl ;
fieldDecl: modifier* type IDENTIFIER ('=' expression)? ';' ;
methodDecl: modifier* type IDENTIFIER '(' parameterList? ')' methodBody ;
constructorDecl: modifier* IDENTIFIER '(' parameterList? ')' methodBody ;

modifier: 'public' | 'private' | 'final' | annotation ;

parameterList: parameter (',' parameter)* ;
parameter: type IDENTIFIER ;

type: qualifiedName ('[' ']')* ;
qualifiedName: IDENTIFIER ('.' IDENTIFIER)* ;
methodBody: block ;

block: '{' blockStatement* '}' ;

blockStatement: localVarDecl | statement | ';' ;

localVarDecl: type IDENTIFIER ('=' expression)? ';' ;

statement: block
         | 'return' expression? ';'
         | 'if' '(' expression ')' statement ('else' statement)?
         | 'while' '(' expression ')' statement
         | expression ';'
         ;

expression: primary (operator expression)? ;

primary: literal
       | IDENTIFIER
       | 'new' type '(' argumentList? ')'
       | '(' expression ')'
       ;

argumentList: expression (',' expression)* ;

literal: STRING_LITERAL | CHAR_LITERAL | 'null' ;

operator: '+' | '-' | '*' | '/' | '%' | '==' | '!=' | '<' | '>' | '<=' | '>='
        | '&&' | '||' | '=' | '.' | '::'
        ;

annotation: '@' IDENTIFIER ( '(' argumentList? ')' )? ;


// --------------------
// Lexer rules
// --------------------

// Keywords
PACKAGE     : 'package';
IMPORT      : 'import';
CLASS       : 'class';
PUBLIC      : 'public';
PRIVATE     : 'private';
FINAL       : 'final';
RETURN      : 'return';
NULL        : 'null';
NEW         : 'new';
IF          : 'if';
ELSE        : 'else';
WHILE       : 'while';
EXTENDS     : 'extends';
IMPLEMENTS  : 'implements';

// Literals
STRING_LITERAL : '"' ( ESC_SEQ | ~["\\] )* '"' ;
CHAR_LITERAL   : '\'' ( ESC_SEQ | ~['\\] ) '\'' ;
fragment ESC_SEQ : '\\' [btnfr"'\\] ;
IDENTIFIER     : [a-zA-Z_] [a-zA-Z_0-9]* ;

// Comments: "hidden" instead of "skip" for tokenstream-based highlighting
LINE_COMMENT    : '//' ~[\r\n]*             -> channel(HIDDEN);
JAVADOC_COMMENT : '/**' (.|[\r\n])*? '*/'   -> channel(HIDDEN);
BLOCK_COMMENT   : '/*'  (.|[\r\n])*? '*/'   -> channel(HIDDEN);

WS              : [ \t\r\n]+    -> skip;

// Other characters (operators, brackets, etc.)
LPAREN      : '(';
RPAREN      : ')';
LBRACE      : '{';
RBRACE      : '}';
LBRACK      : '[';
RBRACK      : ']';
SEMI        : ';';
COMMA       : ',';
DOT         : '.';
AT          : '@';

PLUS        : '+';
MINUS       : '-';
STAR        : '*';
SLASH       : '/';
PERCENT     : '%';
ASSIGN      : '=';
LT          : '<';
GT          : '>';
BANG        : '!';
QUESTION    : '?';
COLON       : ':';

LE          : '<=';
GE          : '>=';
EQUAL       : '==';
NOTEQUAL    : '!=';
AND         : '&&';
OR          : '||';
