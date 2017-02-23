/*
Class: CS461
Project: 1
File: lexer.jlex
Date: Monday, February 13, 2017
Group: Larry Jacob Nick Luis
*/
/* Bantam Java Compiler and Language Toolset.
   Copyright (C) 2009 by Marc Corliss (corliss@hws.edu) and 
                         David Furcy (furcyd@uwosh.edu) and
                         E Christopher Lewis (lewis@vmware.com).
   ALL RIGHTS RESERVED.
   The Bantam Java toolset is distributed under the following 
   conditions:
     You may make copies of the toolset for your own use and 
     modify those copies.
     All copies of the toolset must retain the author names and 
     copyright notice.
     You may not sell the toolset or distribute it in 
     conjunction with a commerical product or service without 
     the expressed written consent of the authors.
   THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS 
   OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE 
   IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
   PARTICULAR PURPOSE. 
*/
/* code below is copied to the file containing the bantam.lexer */
package bantam.lexer;
import bantam.parser.TokenIds;
/* import Symbol class, which represents the symbols that are passed
   from the bantam.lexer to the bantam.parser.  Each symbol consists of an ID
   and a token value, which is defined in Token.java */
import java_cup.runtime.Symbol;


public class Lexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

    /* code below is copied to the class containing the bantam.lexer */
    /** maximum string size allowed */
    private final int MAX_STRING_SIZE = 5000;
    /** boolean indicating whether debugging is enabled */
    private boolean debug = false;
    /** boolean indicating whether we're lexing multiple files or a single file */
    private boolean multipleFiles = false;
    /** array that holds the names of each file we're lexing 
      * (used only when multipleFiles is true)
      * */
    private String[] filenames;
    /** array that holds the reader for each file we're lexing 
      * (used only when multipleFiles is true)
      * */
    private java.io.BufferedReader[] fileReaders;
    /** current file number used to index filenames and fileReaders
      * (used only when multipleFiles is true)
      * */
    private int fileCnt = 0;
    /** Lexer constructor - defined in JLex specification file
      * Needed to handle lexing multiple files
      * @param filenames list of filename strings
      * @param debug boolean indicating whether debugging is enabled
      * */
    public Lexer(String[] filenames, boolean debug) {
	// call private constructor, which does some initialization
	this();
	this.debug = debug;
	// set the multipleFiles flag to true (provides compatibility
	// with the single file constructors)
	multipleFiles = true;
	// initialize filenames field to parameter filenames
	// used later for finding the name of the current file
	this.filenames = filenames;
	// check that there is at least one specified filename
	if (filenames.length == 0)
	    throw new RuntimeException("Must specify at least one filename to scan");
	// must initialize readers for each file (BufferedReader)
	fileReaders = new java.io.BufferedReader[filenames.length];
	for (int i = 0; i < filenames.length; i++) {
	    // try...catch checks if file is found
	    try {
		// create the ith file reader
		fileReaders[i] = new java.io.BufferedReader(new java.io.FileReader(filenames[i]));
	    }
	    catch(java.io.FileNotFoundException e) {
		// if file not found then report an error and exit
		System.err.println("Error: file '" + filenames[i] + "' not found");
		System.exit(1);
	    }
	}
	// set yy_reader (a JLex variable) to the first file reader
	yy_reader = fileReaders[0];
	// set yyline to 1 (as opposed to 0)
	yyline = 1;
    }
    /** holds the current string constant
      * note: we use StringBuffer so that appending does not require constructing a new object 
      * */
    private StringBuffer currStringConst;
    /** getter method for accessing the current line number
      * @return current line number
      * */
    public int getCurrLineNum() {
	return yyline;
    }
    /** getter method for accessing the current file name
      * @return current filename string
      * */
    public String getCurrFilename() {
	return filenames[fileCnt];
    }
    /** print tokens - used primarily for debugging the bantam.lexer
      * */
    public void printTokens() throws java.io.IOException {
	// prevFileCnt is used to determine when the filename has changed
	// every time an EOF is encountered fileCnt is incremented
	// by testing fileCnt with prevFileCnt, we can determine when the
	// filename has changed and print the filename along with the tokens
	int prevFileCnt = -1;
	// try...catch needed since next_token() can throw an IOException
	try {
	    // iterate through all tokens
	    while (true) {
		// get the next token
		Symbol symbol = next_token();
		// check if file has changed
		if (prevFileCnt != fileCnt) {
		    // if it has then print out the new filename
		    System.out.println("# " + filenames[fileCnt]);
		    // update prevFileCnt
		    prevFileCnt = fileCnt;
		}
		// print out the token
		System.out.println((Token)symbol.value);
		// if we've reached the EOF (EOF only returned for the last
		// file) then we break out of loop
		if (symbol.sym == TokenIds.EOF)
		    break;
	    }
	}
	catch (java.io.IOException e) {
	    // if an IOException occurs then print error and exit
	    System.err.println("Unexpected IO exception while scanning.");
	    throw e;
	}
    }
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	public Lexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	public Lexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Lexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;

    // set yyline to 1 (as opposed to 0)
    yyline = 1;
	}

	private boolean yy_eof_done = false;
	private final int YYINITIAL = 0;
	private final int yy_state_dtrans[] = {
		0
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yychar = yychar
			+ yy_buffer_index - yy_buffer_start;
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NO_ANCHOR,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_END,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NOT_ACCEPT,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NOT_ACCEPT,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NOT_ACCEPT,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NOT_ACCEPT,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NOT_ACCEPT,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR,
		/* 168 */ YY_NO_ANCHOR,
		/* 169 */ YY_NO_ANCHOR,
		/* 170 */ YY_NO_ANCHOR,
		/* 171 */ YY_NO_ANCHOR,
		/* 172 */ YY_NO_ANCHOR,
		/* 173 */ YY_NO_ANCHOR,
		/* 174 */ YY_NO_ANCHOR,
		/* 175 */ YY_NO_ANCHOR,
		/* 176 */ YY_NO_ANCHOR,
		/* 177 */ YY_NO_ANCHOR,
		/* 178 */ YY_NO_ANCHOR,
		/* 179 */ YY_NO_ANCHOR,
		/* 180 */ YY_NO_ANCHOR,
		/* 181 */ YY_NO_ANCHOR,
		/* 182 */ YY_NO_ANCHOR,
		/* 183 */ YY_NO_ANCHOR,
		/* 184 */ YY_NO_ANCHOR,
		/* 185 */ YY_NO_ANCHOR,
		/* 186 */ YY_NO_ANCHOR,
		/* 187 */ YY_NO_ANCHOR,
		/* 188 */ YY_NO_ANCHOR,
		/* 189 */ YY_NO_ANCHOR,
		/* 190 */ YY_NO_ANCHOR,
		/* 191 */ YY_NO_ANCHOR,
		/* 192 */ YY_NO_ANCHOR,
		/* 193 */ YY_NO_ANCHOR,
		/* 194 */ YY_NO_ANCHOR,
		/* 195 */ YY_NO_ANCHOR,
		/* 196 */ YY_NO_ANCHOR,
		/* 197 */ YY_NO_ANCHOR,
		/* 198 */ YY_NO_ANCHOR,
		/* 199 */ YY_NO_ANCHOR,
		/* 200 */ YY_NO_ANCHOR,
		/* 201 */ YY_NO_ANCHOR,
		/* 202 */ YY_NO_ANCHOR,
		/* 203 */ YY_NO_ANCHOR,
		/* 204 */ YY_NO_ANCHOR,
		/* 205 */ YY_NO_ANCHOR,
		/* 206 */ YY_NO_ANCHOR,
		/* 207 */ YY_NO_ANCHOR,
		/* 208 */ YY_NO_ANCHOR,
		/* 209 */ YY_NO_ANCHOR,
		/* 210 */ YY_NO_ANCHOR,
		/* 211 */ YY_NO_ANCHOR,
		/* 212 */ YY_NO_ANCHOR,
		/* 213 */ YY_NO_ANCHOR,
		/* 214 */ YY_NO_ANCHOR,
		/* 215 */ YY_NO_ANCHOR,
		/* 216 */ YY_NO_ANCHOR,
		/* 217 */ YY_NO_ANCHOR,
		/* 218 */ YY_NO_ANCHOR,
		/* 219 */ YY_NO_ANCHOR,
		/* 220 */ YY_NO_ANCHOR,
		/* 221 */ YY_NO_ANCHOR,
		/* 222 */ YY_NO_ANCHOR,
		/* 223 */ YY_NO_ANCHOR,
		/* 224 */ YY_NO_ANCHOR,
		/* 225 */ YY_NO_ANCHOR,
		/* 226 */ YY_NO_ANCHOR,
		/* 227 */ YY_NO_ANCHOR,
		/* 228 */ YY_NO_ANCHOR,
		/* 229 */ YY_NO_ANCHOR,
		/* 230 */ YY_NO_ANCHOR,
		/* 231 */ YY_NO_ANCHOR,
		/* 232 */ YY_NO_ANCHOR,
		/* 233 */ YY_NO_ANCHOR,
		/* 234 */ YY_NO_ANCHOR,
		/* 235 */ YY_NO_ANCHOR,
		/* 236 */ YY_NO_ANCHOR,
		/* 237 */ YY_NO_ANCHOR,
		/* 238 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"27:9,2,3,27:2,8,27:18,1,21,25,9:2,22,27,9,12,13,5,15,24,14,27,4,45,47,46,51" +
",48,54,52,49,50,53,9,23,19,18,20,9:2,55:26,16,6,17,9,56,9,30,40,28,44,35,36" +
",55,42,38,55,41,29,55,7,37,55:2,33,31,32,34,55,39,43,55:2,10,27,11,9,27,0,2" +
"6")[0];

	private int yy_rmap[] = unpackFromString(1,239,
"0,1:4,2,1:2,3,1:5,4,5,1:2,6,7,8,9,1:3,10,11,12,13,1:8,14,15,14,1,14,1,14:2," +
"15,14:7,11:10,16,17,18,19,20,21,22,23,24,25,26,27,11,28,29,23,30,31,32,33,3" +
"4,35,36,37,38,39,40,41,42,43,44,22,45,46,47,48,49,50,51,52,53,54,55,56,16,5" +
"7,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,8" +
"2,83,84,85,86,87,88,89,90,91,92,93,94,95,82,96,80,97,64,98,99,100,82,14,101" +
",102,53,103,104,103,105,106,107,108,109,58,110,111,112,113,114,115,116,117," +
"118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136" +
",137,138,139,140,141,142,143,144,145,146,147,148,149,147,150,151,152,153,15" +
"4,155,156,157,155,158,159,160,161,162,163,164,165,166,167,168,169,170,171,1" +
"72,173,174,175,176,177")[0];

	private int yy_nxt[][] = unpackFromString(178,57,
"1,2,3,4,5,6,7,8,-1,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,1,7,14" +
"3,156:3,163,166,156,169,171,156,65,173,175,156:4,64,236,237,238:7,156,26,-1" +
":61,27,28,-1:58,156,-1:20,156:7,72,156:9,-1,156:11,-1:14,29,-1:57,30,-1:59," +
"31,-1:56,32,-1:56,33,-1:56,34,-1:39,25:2,66,25:2,73,25:18,35,36,25:30,-1:7," +
"26,-1:20,26:29,-1,27:2,-1,27:4,-1,27:17,-1,27:30,-1,144:3,38:2,28,144,157,1" +
"44:3,38:2,144:12,-1,144:30,-1:7,156,-1:20,156:17,-1,156:11,-1,38:4,68,74,38" +
",78,38:17,-1,38:30,-1:7,26,-1:20,26:17,63:10,26:2,-1:7,26,-1:20,26:17,64,23" +
"6,237,238:7,26:2,-1:7,150,-1:20,156:8,37,156:8,-1,156:11,-1,146:2,66,146:2," +
"77,146:18,40,36,146:30,-1,67:3,-1:2,67:6,-1:2,67:12,-1,67:30,-1,38:3,45,68," +
"74,38,78,38:17,-1,38:30,-1,94:5,113,94:18,-1:2,94:30,-1,78:4,82,-1,78:19,-1" +
",78:30,-1:7,26,-1:20,26:17,53:5,26,53:2,26,53,26:2,-1:7,156,-1:20,156:11,39" +
",156:5,-1,156:11,-1,81:2,85,81:2,88,91,81:17,94,36,81:5,91:2,81:2,91,81:20," +
"-1,38:5,74,38,78,38:17,-1,38:30,-1:7,156,-1:20,156:2,161,156:14,-1,156:11,-" +
"1,149:2,147,149:2,97,160,149:17,69,36,149:5,160:2,149:2,160,149:20,-1:7,26," +
"-1:20,26:17,54:10,26:2,-1:7,156,-1:20,156:6,102,156:10,-1,156:11,-1,159:2,1" +
"65,159:2,100,159,103,159:16,42,36,159:30,-1,78:3,70,82,-1,78:19,-1,78:30,-1" +
":7,26,-1:20,26:17,55:10,26:2,-1:7,156,-1:20,156:4,105,156:12,-1,156:11,-1,1" +
"68:2,165,168:2,106,168,109,168:16,40,36,168:30,-1:7,26,-1:20,26:17,56:10,26" +
":2,-1:7,156,-1:20,156:3,108,156:13,-1,156:11,-1,91:2,153,91:2,73,91:18,94,3" +
"6,91:30,-1:7,26,-1:20,26:17,53:5,57,53:2,57,53,26:2,-1:7,156,-1:20,156,110," +
"156:15,-1,156:11,-1,91:2,153,91:2,111,91:18,35,36,91:30,-1:7,26,-1:20,26:17" +
",58:10,26:2,-1:7,156,-1:20,156:5,41,156:11,-1,156:11,-1:7,26,-1:20,26:17,59" +
":10,26:2,-1:7,156,-1:20,156:10,114,156:6,-1,156:11,-1,160:2,123,160:2,77,16" +
"0,162,160:16,69,36,160:30,-1:7,26,-1:20,26:17,60:10,26:2,-1:7,156,-1:20,156" +
":7,116,156:9,-1,156:11,-1,81:2,85,81:2,127,159,81:17,94,36,81:5,159:2,81:2," +
"159,81:20,-1:7,26,-1:20,26:17,61:10,26:2,-1:7,156,-1:20,156:7,43,156:9,-1,1" +
"56:11,-1,159:2,165,159:2,127,159,103,159:16,42,36,159:30,-1:7,26,-1:20,26:1" +
"7,62:10,26:2,-1:7,156,-1:20,156:6,120,156:10,-1,156:11,-1,131:2,133,131:2,1" +
"29,168,131:17,69,36,131:5,168:2,131:2,168,131:20,-1:7,156,-1:20,156:7,44,15" +
"6:9,-1,156:11,-1,168:2,119,168:2,129,168,109,168:16,40,36,168:30,-1:7,156,-" +
"1:20,156:3,124,156:13,-1,156:11,-1,91:2,153,91:2,111,91:18,94,36,91:30,-1:7" +
",156,-1:20,156:4,154,156:12,-1,156:11,-1,94:5,113,94:19,-1,94:30,-1:7,156,-" +
"1:20,156,126,156:15,-1,156:11,-1,115:2,151,115:2,117,115:18,35,36,115:30,-1" +
":7,156,-1:20,156:2,128,156:14,-1,156:11,-1,133:2,147,133:2,135,123,133:17,9" +
"4,36,133:5,123:2,133:2,123,133:20,-1:7,156,-1:20,156:3,46,156:13,-1,156:11," +
"-1,119:5,137,119:18,42,36,119:30,-1:7,156,-1:20,156:5,130,156:11,-1,156:11," +
"-1,155:2,133,155:2,137,119,155:17,94,36,155:5,119:2,155:2,119,155:20,-1:7,1" +
"32,-1:20,156:17,-1,156:11,-1,123:5,139,123:18,35,36,123:30,-1:7,156,-1:20,1" +
"56:7,47,156:9,-1,156:11,-1,160:2,123,160:2,125,160,162,160:16,69,36,160:30," +
"-1:7,156,-1:20,156:7,48,156:9,-1,156:11,-1,159:2,165,159:2,127,159,103,159:" +
"16,94,36,159:30,-1:7,156,-1:20,156:13,49,156:3,-1,156:11,-1,168:2,119,168:2" +
",129,168,109,168:16,69,36,168:30,-1:7,50,-1:20,156:17,-1,156:11,-1,168:2,11" +
"9,168:2,106,168,109,168:16,40,36,168:30,-1:7,156,-1:20,156:16,136,-1,156:11" +
",-1,119:5,121,119:18,42,36,119:30,-1:7,138,-1:20,156:17,-1,156:11,-1,123:5," +
"117,123:18,94,36,123:30,-1:7,156,-1:20,156:3,51,156:13,-1,156:11,-1,119:5,1" +
"37,119:18,94,36,119:30,-1:7,156,-1:20,140,156:16,-1,156:11,-1,123:5,139,123" +
":18,94,36,123:30,-1:7,156,-1:20,156:7,141,156:9,-1,156:11,-1:7,156,-1:20,15" +
"6:9,142,156:7,-1,156:11,-1:7,156,-1:20,156:8,52,156:8,-1,156:11,-1:7,156,-1" +
":20,156,76,156:15,-1,156:11,-1,144:3,38,68,28,144,157,144:3,38:2,144:12,-1," +
"144:30,-1:7,26,-1:20,26:17,71:4,75:2,71,75:3,26:2,-1,146:2,151,146:2,77,146" +
":18,40,36,146:30,-1:7,156,-1:20,156:4,152,156:12,-1,156:11,-1:7,156,-1:20,1" +
"56:3,112,156:13,-1,156:11,-1:7,156,-1:20,156:7,122,156:9,-1,156:11,-1,160:2" +
",153,160:2,125,160,162,160:16,40,36,160:30,-1:7,156,-1:20,156:2,134,156:14," +
"-1,156:11,-1,157:3,78,82,67,157:5,78:2,157:12,-1,157:30,-1:7,26,-1:20,26:17" +
",75:10,26:2,-1,160:2,123,160:2,125,160,162,160:16,40,36,160:30,-1:7,156,-1:" +
"20,156:3,118,156:13,-1,156:11,-1:7,156,-1:20,156:5,80,156:11,-1,156:11,-1:7" +
",26,-1:20,26:17,79:10,26:2,-1,168:2,165,168:2,129,168,109,168:16,40,36,168:" +
"30,-1:7,156,-1:20,156:7,84,156:9,-1,156:11,-1:7,26,-1:20,26:17,83:10,26:2,-" +
"1:7,156,-1:20,156,87,156:13,148,156,-1,156:11,-1:7,26,-1:20,26:17,86:10,26:" +
"2,-1:7,156,-1:20,156:2,90,156:6,93,156:7,-1,156:11,-1:7,26,-1:20,26:17,71:3" +
",89,92:2,71,92:3,26:2,-1:7,156,-1:20,156:14,96,156:2,-1,156:11,-1:7,26,-1:2" +
"0,26:17,95:10,26:2,-1:7,156,-1:20,156:5,99,156:11,-1,156:11,-1:7,26,-1:20,2" +
"6:17,98:10,26:2,-1:7,26,-1:20,26:17,101:10,26:2,-1:7,26,-1:20,26:17,104:10," +
"26:2,-1:7,26,-1:20,26:17,107:10,26:2,-1:7,26,-1:20,26:17,145:4,158:2,145:2," +
"158,145,26:2,-1:7,26,-1:20,26:17,158:10,26:2,-1:7,26,-1:20,26:17,164:10,26:" +
"2,-1:7,26,-1:20,26:17,167:10,26:2,-1:7,26,-1:20,26:17,145:4,170:2,145,172,1" +
"70,145,26:2,-1:7,26,-1:20,26:17,174:10,26:2,-1:7,26,-1:20,26:17,176:10,26:2" +
",-1:7,26,-1:20,26:17,177:10,26:2,-1:7,26,-1:20,26:17,178:10,26:2,-1:7,26,-1" +
":20,26:17,179:10,26:2,-1:7,26,-1:20,26:17,180:3,181:3,180,181:3,26:2,-1:7,2" +
"6,-1:20,26:17,181:10,26:2,-1:7,26,-1:20,26:17,182:10,26:2,-1:7,26,-1:20,26:" +
"17,180:3,183:3,184,183:3,26:2,-1:7,26,-1:20,26:17,185:10,26:2,-1:7,26,-1:20" +
",26:17,186:10,26:2,-1:7,26,-1:20,26:17,187:10,26:2,-1:7,26,-1:20,26:17,188:" +
"10,26:2,-1:7,26,-1:20,26:17,189:10,26:2,-1:7,26,-1:20,26:17,190:8,191,190,2" +
"6:2,-1:7,26,-1:20,26:17,191:10,26:2,-1:7,26,-1:20,26:17,192:10,26:2,-1:7,26" +
",-1:20,26:17,190:5,193,190:2,194,190,26:2,-1:7,26,-1:20,26:17,195:10,26:2,-" +
"1:7,26,-1:20,26:17,196:10,26:2,-1:7,26,-1:20,26:17,197:10,26:2,-1:7,26,-1:2" +
"0,26:17,200:10,26:2,-1:7,26,-1:20,26:17,198:10,26:2,-1:7,26,-1:20,26:17,199" +
":4,200:2,199,200:3,26:2,-1:7,26,-1:20,26:17,201:10,26:2,-1:7,26,-1:20,26:17" +
",199:3,202,203:2,199,203:3,26:2,-1:7,26,-1:20,26:17,204:10,26:2,-1:7,26,-1:" +
"20,26:17,205:10,26:2,-1:7,26,-1:20,26:17,206:4,209:2,206,209:3,26:2,-1:7,26" +
",-1:20,26:17,209:10,26:2,-1:7,26,-1:20,26:17,207:10,26:2,-1:7,26,-1:20,26:1" +
"7,208:5,209,208:2,209,208,26:2,-1:7,26,-1:20,26:17,210:10,26:2,-1:7,26,-1:2" +
"0,26:17,208:4,211,212,208:2,212,208,26:2,-1:7,26,-1:20,26:17,213:10,26:2,-1" +
":7,26,-1:20,26:17,214:4,215,218,214,215,218,215,26:2,-1:7,26,-1:20,26:17,21" +
"5:3,214,215,218,215,214,218,214,26:2,-1:7,26,-1:20,26:17,218:10,26:2,-1:7,2" +
"6,-1:20,26:17,215:5,218,215:2,218,215,26:2,-1:7,26,-1:20,26:17,214:4,215,21" +
"8,214:2,218,214,26:2,-1:7,26,-1:20,26:17,216:10,26:2,-1:7,26,-1:20,26:17,21" +
"7:4,218:2,217,218:3,26:2,-1:7,26,-1:20,26:17,219:10,26:2,-1:7,26,-1:20,26:1" +
"7,217:3,220,221:2,217,221:3,26:2,-1:7,26,-1:20,26:17,222:5,223,222:2,224,22" +
"2,26:2,-1:7,26,-1:20,26:17,225:4,223:2,225,223,224,223,26:2,-1:7,26,-1:20,2" +
"6:17,226:3,225,224:2,226,225,224,225,26:2,-1:7,26,-1:20,26:17,224:10,26:2,-" +
"1:7,26,-1:20,26:17,227:10,26:2,-1:7,26,-1:20,26:17,228,229,230,229:7,26:2,-" +
"1:7,26,-1:20,26:17,231:4,232,233,231,232,234,232,26:2,-1:7,26,-1:20,26:17,2" +
"35:10,26:2");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

    /* code below is executed when the end-of-file is reached */
    switch(yy_lexical_state) {
    case YYINITIAL:
	// if in YYINITIAL when EOF occurs then no error
	break;
    // if defining other states then might want to add other cases here...
    }
    // if we reach here then we should either start lexing the next
    // file (if there are more files to lex) or return EOF (if we're
    // at the file)
    if (multipleFiles && fileCnt < fileReaders.length - 1) {
	// more files to lex so update yy_reader and yyline and then continue
	yy_reader = fileReaders[++fileCnt];
	yyline = 1;
	continue;
    }
    // if we reach here, then we're at the last file so we return EOF
    // to bantam.parser
    return new Symbol(TokenIds.EOF, new Token("EOF", yyline));
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 0:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -2:
						break;
					case 1:
						
					case -3:
						break;
					case 2:
						{}
					case -4:
						break;
					case 3:
						{}
					case -5:
						break;
					case 4:
						{}
					case -6:
						break;
					case 5:
						{ return new Symbol(TokenIds.DIVIDE, new Token("DIVIDE", yyline)); }
					case -7:
						break;
					case 6:
						{ return new Symbol(TokenIds.TIMES, new Token("TIMES", yyline)); }
					case -8:
						break;
					case 7:
						{ throw new RuntimeException("Unmatched lexeme " +
                            yytext() + " at line " + yyline); }
					case -9:
						break;
					case 8:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -10:
						break;
					case 9:
						{
    return new Symbol(
            TokenIds.LEX_ERROR, new Token("LEX_ERROR", "Unsupported Character", yyline));
}
					case -11:
						break;
					case 10:
						{ return new Symbol(TokenIds.LBRACE, new Token("LBRACE", yyline)); }
					case -12:
						break;
					case 11:
						{ return new Symbol(TokenIds.RBRACE, new Token("RBRACE", yyline)); }
					case -13:
						break;
					case 12:
						{ return new Symbol(TokenIds.LPAREN, new Token("LPAREN", yyline)); }
					case -14:
						break;
					case 13:
						{ return new Symbol(TokenIds.RPAREN, new Token("RPAREN", yyline)); }
					case -15:
						break;
					case 14:
						{ return new Symbol(TokenIds.MINUS, new Token("MINUS", yyline)); }
					case -16:
						break;
					case 15:
						{ return new Symbol(TokenIds.PLUS, new Token("PLUS", yyline)); }
					case -17:
						break;
					case 16:
						{ return new Symbol(TokenIds.LSQBRACE, new Token("LSQBRACE", yyline)); }
					case -18:
						break;
					case 17:
						{ return new Symbol(TokenIds.RSQBRACE, new Token("RSQBRACE", yyline)); }
					case -19:
						break;
					case 18:
						{ return new Symbol(TokenIds.ASSIGN, new Token("ASSIGN", yyline)); }
					case -20:
						break;
					case 19:
						{ return new Symbol(TokenIds.LT, new Token("LT", yyline)); }
					case -21:
						break;
					case 20:
						{ return new Symbol(TokenIds.GT, new Token("GT", yyline)); }
					case -22:
						break;
					case 21:
						{ return new Symbol(TokenIds.NOT, new Token("NOT", yyline)); }
					case -23:
						break;
					case 22:
						{ return new Symbol(TokenIds.MODULUS, new Token("MODULUS", yyline)); }
					case -24:
						break;
					case 23:
						{ return new Symbol(TokenIds.SEMI, new Token("SEMI", yyline)); }
					case -25:
						break;
					case 24:
						{ return new Symbol(TokenIds.COMMA, new Token("COMMA", yyline)); }
					case -26:
						break;
					case 25:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -27:
						break;
					case 26:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -28:
						break;
					case 27:
						{}
					case -29:
						break;
					case 28:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","unterminated multiline comment", yyline));
}
					case -30:
						break;
					case 29:
						{ return new Symbol(TokenIds.DECR, new Token("DECR", yyline)); }
					case -31:
						break;
					case 30:
						{ return new Symbol(TokenIds.INCR,  new Token("INCR", yyline)); }
					case -32:
						break;
					case 31:
						{ return new Symbol(TokenIds.EQ, new Token("EQ", yyline)); }
					case -33:
						break;
					case 32:
						{ return new Symbol(TokenIds.LEQ, new Token("LEQ", yyline)); }
					case -34:
						break;
					case 33:
						{ return new Symbol(TokenIds.GEQ, new Token("GEQ", yyline)); }
					case -35:
						break;
					case 34:
						{ return new Symbol(TokenIds.NE, new Token("NE", yyline)); }
					case -36:
						break;
					case 35:
						{
    if (yytext().length() > 5000) {
                return new Symbol(TokenIds.error, new Token("LEX_ERROR",
                "String constant of illegal length", yyline));
    } else {
        return new Symbol(TokenIds.STRING_CONST, new Token("STRING_CONST",
        yytext().substring(1,yytext().length()-1), yyline));
    }
}
					case -37:
						break;
					case 36:
						{
    return new Symbol(
            TokenIds.error, new Token("LEX_ERROR","String constant unterminated",yyline));
}
					case -38:
						break;
					case 37:
						{ return new Symbol(TokenIds.IF, new Token("IF", yyline)); }
					case -39:
						break;
					case 39:
						{ return new Symbol(TokenIds.NEW, new Token("NEW", yyline)); }
					case -40:
						break;
					case 40:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant spanning multiple lines",yyline));
}
					case -41:
						break;
					case 41:
						{ return new Symbol(TokenIds.FOR, new Token("FOR", yyline)); }
					case -42:
						break;
					case 42:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant contains illegal escape characters",yyline));
}
					case -43:
						break;
					case 43:
						{ return new Symbol(TokenIds.BOOLEAN_CONST, new Token("TRUE",yyline)); }
					case -44:
						break;
					case 44:
						{ return new Symbol(TokenIds.ELSE, new Token("ELSE", yyline)); }
					case -45:
						break;
					case 45:
						{}
					case -46:
						break;
					case 46:
						{ return new Symbol(TokenIds.CLASS, new Token("CLASS", yyline)); }
					case -47:
						break;
					case 47:
						{ return new Symbol(TokenIds.BOOLEAN_CONST, new Token("FALSE",yyline)); }
					case -48:
						break;
					case 48:
						{ return new Symbol(TokenIds.WHILE, new Token("WHILE", yyline)); }
					case -49:
						break;
					case 49:
						{ return new Symbol(TokenIds.BREAK, new Token("BREAK", yyline)); }
					case -50:
						break;
					case 50:
						{ return new Symbol(TokenIds.RETURN, new Token("RETURN", yyline)); }
					case -51:
						break;
					case 51:
						{ return new Symbol(TokenIds.EXTENDS, new Token("EXTENDS", yyline)); }
					case -52:
						break;
					case 52:
						{ return new Symbol(TokenIds.INSTANCEOF, new Token("INSTANCEOF", yyline)); }
					case -53:
						break;
					case 53:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -54:
						break;
					case 54:
						{
    return new Symbol(TokenIds.LEX_ERROR, new Token(
        "LEX_ERROR", yytext() + " int const too big: line " + yyline, yyline));
}
					case -55:
						break;
					case 55:
						{
    return new Symbol(TokenIds.LEX_ERROR, new Token(
        "LEX_ERROR", yytext() + " int const too big: line " + yyline, yyline));
}
					case -56:
						break;
					case 56:
						{
    return new Symbol(TokenIds.LEX_ERROR, new Token(
        "LEX_ERROR", yytext() + " int const too big: line " + yyline, yyline));
}
					case -57:
						break;
					case 57:
						{
    return new Symbol(TokenIds.LEX_ERROR, new Token(
        "LEX_ERROR", yytext() + " int const too big: line " + yyline, yyline));
}
					case -58:
						break;
					case 58:
						{
    return new Symbol(TokenIds.LEX_ERROR, new Token(
        "LEX_ERROR", yytext() + " int const too big: line " + yyline, yyline));
}
					case -59:
						break;
					case 59:
						{
    return new Symbol(TokenIds.LEX_ERROR, new Token(
        "LEX_ERROR", yytext() + " int const too big: line " + yyline, yyline));
}
					case -60:
						break;
					case 60:
						{
    return new Symbol(TokenIds.LEX_ERROR, new Token(
        "LEX_ERROR", yytext() + " int const too big: line " + yyline, yyline));
}
					case -61:
						break;
					case 61:
						{
    return new Symbol(TokenIds.LEX_ERROR, new Token(
        "LEX_ERROR", yytext() + " int const too big: line " + yyline, yyline));
}
					case -62:
						break;
					case 62:
						{
    return new Symbol(TokenIds.LEX_ERROR, new Token(
        "LEX_ERROR", yytext() + " int const too big: line " + yyline, yyline));
}
					case -63:
						break;
					case 63:
						{
    return new Symbol(TokenIds.LEX_ERROR, new Token(
        "LEX_ERROR", yytext() + " int const too big: line " + yyline, yyline));
}
					case -64:
						break;
					case 64:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -65:
						break;
					case 65:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -66:
						break;
					case 66:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -67:
						break;
					case 67:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","unterminated multiline comment", yyline));
}
					case -68:
						break;
					case 69:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant spanning multiple lines",yyline));
}
					case -69:
						break;
					case 70:
						{}
					case -70:
						break;
					case 71:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -71:
						break;
					case 72:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -72:
						break;
					case 73:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -73:
						break;
					case 75:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -74:
						break;
					case 76:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -75:
						break;
					case 77:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -76:
						break;
					case 79:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -77:
						break;
					case 80:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -78:
						break;
					case 81:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -79:
						break;
					case 83:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -80:
						break;
					case 84:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -81:
						break;
					case 85:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -82:
						break;
					case 86:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -83:
						break;
					case 87:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -84:
						break;
					case 88:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -85:
						break;
					case 89:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -86:
						break;
					case 90:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -87:
						break;
					case 91:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -88:
						break;
					case 92:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -89:
						break;
					case 93:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -90:
						break;
					case 94:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -91:
						break;
					case 95:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -92:
						break;
					case 96:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -93:
						break;
					case 97:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -94:
						break;
					case 98:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -95:
						break;
					case 99:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -96:
						break;
					case 100:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -97:
						break;
					case 101:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -98:
						break;
					case 102:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -99:
						break;
					case 103:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -100:
						break;
					case 104:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -101:
						break;
					case 105:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -102:
						break;
					case 106:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -103:
						break;
					case 107:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -104:
						break;
					case 108:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -105:
						break;
					case 109:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -106:
						break;
					case 110:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -107:
						break;
					case 111:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -108:
						break;
					case 112:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -109:
						break;
					case 113:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -110:
						break;
					case 114:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -111:
						break;
					case 115:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -112:
						break;
					case 116:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -113:
						break;
					case 117:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -114:
						break;
					case 118:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -115:
						break;
					case 119:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -116:
						break;
					case 120:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -117:
						break;
					case 121:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -118:
						break;
					case 122:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -119:
						break;
					case 123:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -120:
						break;
					case 124:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -121:
						break;
					case 125:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -122:
						break;
					case 126:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -123:
						break;
					case 127:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -124:
						break;
					case 128:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -125:
						break;
					case 129:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -126:
						break;
					case 130:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -127:
						break;
					case 131:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -128:
						break;
					case 132:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -129:
						break;
					case 133:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -130:
						break;
					case 134:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -131:
						break;
					case 135:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -132:
						break;
					case 136:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -133:
						break;
					case 137:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -134:
						break;
					case 138:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -135:
						break;
					case 139:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -136:
						break;
					case 140:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -137:
						break;
					case 141:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -138:
						break;
					case 142:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -139:
						break;
					case 143:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -140:
						break;
					case 144:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","unterminated multiline comment", yyline));
}
					case -141:
						break;
					case 145:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -142:
						break;
					case 146:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -143:
						break;
					case 147:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -144:
						break;
					case 148:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -145:
						break;
					case 149:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -146:
						break;
					case 150:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -147:
						break;
					case 151:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -148:
						break;
					case 152:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -149:
						break;
					case 153:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -150:
						break;
					case 154:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -151:
						break;
					case 155:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -152:
						break;
					case 156:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -153:
						break;
					case 157:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","unterminated multiline comment", yyline));
}
					case -154:
						break;
					case 158:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -155:
						break;
					case 159:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -156:
						break;
					case 160:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -157:
						break;
					case 161:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -158:
						break;
					case 162:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -159:
						break;
					case 163:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -160:
						break;
					case 164:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -161:
						break;
					case 165:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -162:
						break;
					case 166:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -163:
						break;
					case 167:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -164:
						break;
					case 168:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -165:
						break;
					case 169:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -166:
						break;
					case 170:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -167:
						break;
					case 171:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -168:
						break;
					case 172:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -169:
						break;
					case 173:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -170:
						break;
					case 174:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -171:
						break;
					case 175:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -172:
						break;
					case 176:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -173:
						break;
					case 177:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -174:
						break;
					case 178:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -175:
						break;
					case 179:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -176:
						break;
					case 180:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -177:
						break;
					case 181:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -178:
						break;
					case 182:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -179:
						break;
					case 183:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -180:
						break;
					case 184:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -181:
						break;
					case 185:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -182:
						break;
					case 186:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -183:
						break;
					case 187:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -184:
						break;
					case 188:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -185:
						break;
					case 189:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -186:
						break;
					case 190:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -187:
						break;
					case 191:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -188:
						break;
					case 192:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -189:
						break;
					case 193:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -190:
						break;
					case 194:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -191:
						break;
					case 195:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -192:
						break;
					case 196:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -193:
						break;
					case 197:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -194:
						break;
					case 198:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -195:
						break;
					case 199:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -196:
						break;
					case 200:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -197:
						break;
					case 201:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -198:
						break;
					case 202:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -199:
						break;
					case 203:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -200:
						break;
					case 204:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -201:
						break;
					case 205:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -202:
						break;
					case 206:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -203:
						break;
					case 207:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -204:
						break;
					case 208:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -205:
						break;
					case 209:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -206:
						break;
					case 210:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -207:
						break;
					case 211:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -208:
						break;
					case 212:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -209:
						break;
					case 213:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -210:
						break;
					case 214:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -211:
						break;
					case 215:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -212:
						break;
					case 216:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -213:
						break;
					case 217:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -214:
						break;
					case 218:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -215:
						break;
					case 219:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -216:
						break;
					case 220:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -217:
						break;
					case 221:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -218:
						break;
					case 222:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -219:
						break;
					case 223:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -220:
						break;
					case 224:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -221:
						break;
					case 225:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -222:
						break;
					case 226:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -223:
						break;
					case 227:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -224:
						break;
					case 228:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -225:
						break;
					case 229:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -226:
						break;
					case 230:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -227:
						break;
					case 231:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -228:
						break;
					case 232:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -229:
						break;
					case 233:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -230:
						break;
					case 234:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -231:
						break;
					case 235:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -232:
						break;
					case 236:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -233:
						break;
					case 237:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -234:
						break;
					case 238:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -235:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
