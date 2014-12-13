package com.inductiveautomation.xopc.drivers.modbus2.address.parser;

import java_cup.runtime.*;



/**
 * Lexer for a ModbusAddress
 */
%%

%class ModbusAddressLexer
%unicode
%cup
%line
%column

%{
  StringBuilder blockNumberBuffer = new StringBuilder();
  StringBuilder rowBuffer = new StringBuilder();
  StringBuilder columnBuffer = new StringBuilder();

  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}

LineTerminator 	= \r|\n|\r\n
WhiteSpace     	= {LineTerminator} | [ \t\f]
Letter			= [:letter:]
Digit 			= [:digit:] 

%state ADDRESS_TYPE
%state ADDRESS
%state BIT
%state STRING_LENGTH

%%

<YYINITIAL> {
	{Digit}						{ return symbol(sym.DIGIT, Integer.parseInt(yytext())); }
	
	"."							{ return symbol(sym.PERIOD); }
	
	{Letter}					{ yybegin(ADDRESS_TYPE); return symbol(sym.LETTER, yytext()); }
}

<ADDRESS_TYPE> {

	"BCD_32"					{ return symbol(sym.LETTER, yytext()); }
	
	"I_64"						{ return symbol(sym.LETTER, yytext()); }				

	"UI_64"						{ return symbol(sym.LETTER, yytext()); }	
		
	{Letter}					{ return symbol(sym.LETTER, yytext()); }
	
	{Digit}						{ yybegin(ADDRESS); return symbol(sym.DIGIT, Integer.parseInt(yytext())); }
}

<ADDRESS> {
	{Digit}						{ return symbol(sym.DIGIT, Integer.parseInt(yytext())); }
	
	"/" | "."					{ yybegin(BIT); return symbol(sym.BIT_SEPARATOR); }
	
	":" 						{ yybegin(STRING_LENGTH); return symbol(sym.STRING_SEPARATOR); }
}

<BIT> {
	{Digit}						{ return symbol(sym.DIGIT, Integer.parseInt(yytext())); }
}

<STRING_LENGTH> {
	{Digit}						{ return symbol(sym.DIGIT, Integer.parseInt(yytext())); }
}
								  

/* error fallback */
.|\n                            { throw new Error("Illegal character <" + yytext() + ">"); }

