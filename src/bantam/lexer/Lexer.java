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
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_END,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NOT_ACCEPT,
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
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NOT_ACCEPT,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NOT_ACCEPT,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NOT_ACCEPT,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NOT_ACCEPT,
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
		/* 238 */ YY_NO_ANCHOR,
		/* 239 */ YY_NO_ANCHOR,
		/* 240 */ YY_NO_ANCHOR,
		/* 241 */ YY_NO_ANCHOR,
		/* 242 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"30:9,2,3,30:2,4,30:18,1,21,28,9:2,22,25,9,12,13,6,15,24,14,27,5,48,50,49,54" +
",51,57,55,52,53,56,9,23,19,18,20,9:2,58:26,16,7,17,9,59,9,33,43,31,47,38,39" +
",58,45,41,58,44,32,58,8,40,58:2,36,34,35,37,58,42,46,58:2,10,26,11,9,30,0,2" +
"9")[0];

	private int yy_rmap[] = unpackFromString(1,243,
"0,1:5,2,1:2,3,1:5,4,5,1:2,6,7,8,9,1:4,10,11,12,13,1:10,14,15,14,1,14,1,14:2" +
",15,14:7,11:10,16,17,18,19,20,21,22,23,24,25,26,27,28,24,11,29,30,31,32,33," +
"34,35,36,37,38,39,40,41,42,43,44,45,46,23,47,48,49,50,51,52,53,54,55,56,57," +
"58,16,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81," +
"82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,82,97,80,98,64,99,100,101,82,1" +
"4,102,103,74,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,11" +
"9,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,1" +
"38,139,140,141,142,143,144,145,146,147,148,149,150,151,149,152,153,154,155," +
"156,157,158,159,157,160,161,162,163,164,165,166,167,168,169,170,171,172,173" +
",174,175,176,177,178,179")[0];

	private int yy_nxt[][] = unpackFromString(180,60,
"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,69,77,26," +
"27,1,8,148,161:3,166,169,161,172,174,161,70,176,178,161:4,68,240,241,242:7," +
"161,28,-1:65,29,30,-1:61,161,-1:22,161:7,78,161:9,-1,161:11,-1:14,31,-1:60," +
"32,-1:62,33,-1:59,34,-1:59,35,-1:59,36,-1:42,27:2,71,27:3,79,27:20,39,40,27" +
":30,-1:8,28,-1:22,28:29,-1,29:2,-1:2,29:24,-1,29:30,-1,149:3,162,42:2,30,14" +
"9:4,42:2,149:15,-1,149:30,-1:8,161,-1:22,161:17,-1,161:11,-1,42:3,80,42,73," +
"88,42:21,-1,42:30,-1:8,28,-1:22,28:17,67:10,28:2,-1:8,28,-1:22,28:17,68,240" +
",241,242:7,28:2,-1:25,37,-1:42,155,-1:22,161:8,41,161:8,-1,161:11,-1,151:2," +
"71,151:3,83,151:20,44,40,151:30,-1,72:4,-1:2,72:5,-1:2,72:15,-1,72:30,-1,42" +
":3,80,49,73,88,42:21,-1,42:30,-1,100:6,117,100:20,-1:2,100:30,-1,80:5,84,-1" +
",80:21,-1,80:30,-1:8,28,-1:22,28:17,57:5,28,57:2,28,57,28:2,-1:26,38,-1:41," +
"161,-1:22,161:11,43,161:5,-1,161:11,-1,87:2,91,87:3,94,97,87:19,100,40,87:5" +
",97:2,87:2,97,87:20,-1:8,161,-1:22,161:2,165,161:14,-1,161:11,-1,154:2,152," +
"154:3,103,164,154:19,74,40,154:5,164:2,154:2,164,154:20,-1,80:4,75,84,-1,80" +
":21,-1,80:30,-1:8,28,-1:22,28:17,58:10,28:2,-1:8,161,-1:22,161:6,108,161:10" +
",-1,161:11,-1,184:2,168,184:3,106,184:20,46,40,184:30,-1,42:3,80,42:2,88,42" +
":21,-1,42:30,-1:8,28,-1:22,28:17,59:10,28:2,-1:8,161,-1:22,161:4,111,161:12" +
",-1,161:11,-1,171:2,168,171:3,109,171:20,44,40,171:30,-1:8,28,-1:22,28:17,6" +
"0:10,28:2,-1:8,161,-1:22,161:3,114,161:13,-1,161:11,-1,97:2,112,97:3,79,97:" +
"20,100,40,97:30,-1:8,28,-1:22,28:17,57:5,61,57:2,61,57,28:2,-1:8,161,-1:22," +
"161,116,161:15,-1,161:11,-1,97:2,112,97:3,115,97:20,39,40,97:30,-1:8,28,-1:" +
"22,28:17,62:10,28:2,-1:8,161,-1:22,161:5,45,161:11,-1,161:11,-1:8,28,-1:22," +
"28:17,63:10,28:2,-1:8,161,-1:22,161:10,120,161:6,-1,161:11,-1,164:2,127,129" +
",164:2,83,164:20,74,40,164:30,-1:8,28,-1:22,28:17,64:10,28:2,-1:8,161,-1:22" +
",161:7,122,161:9,-1,161:11,-1,87:2,91,87:3,158,184,87:19,100,40,87:5,184:2," +
"87:2,184,87:20,-1:8,28,-1:22,28:17,65:10,28:2,-1:8,161,-1:22,161:7,47,161:9" +
",-1,161:11,-1,135:2,137,135:3,133,171,135:19,74,40,135:5,171:2,135:2,171,13" +
"5:20,-1:8,28,-1:22,28:17,66:10,28:2,-1:8,161,-1:22,161:6,126,161:10,-1,161:" +
"11,-1,164:2,112,129,164:2,131,164:20,44,40,164:30,-1:8,161,-1:22,161:7,48,1" +
"61:9,-1,161:11,-1,97:2,112,97:3,115,97:20,100,40,97:30,-1:8,161,-1:22,161:3" +
",130,161:13,-1,161:11,-1,100:6,117,100:21,-1,100:30,-1:8,161,-1:22,161:4,15" +
"9,161:12,-1,161:11,-1,119:2,156,119:3,121,119:20,39,40,119:30,-1:8,161,-1:2" +
"2,161,132,161:15,-1,161:11,-1,137:2,152,137:3,139,127,137:19,100,40,137:5,1" +
"27:2,137:2,127,137:20,-1:8,161,-1:22,161:2,134,161:14,-1,161:11,-1,123:6,14" +
"1,123:20,46,40,123:30,-1:8,161,-1:22,161:3,50,161:13,-1,161:11,-1,160:2,137" +
",160:3,141,123,160:19,100,40,160:5,123:2,160:2,123,160:20,-1:8,161,-1:22,16" +
"1:5,136,161:11,-1,161:11,-1,127:6,143,127:20,39,40,127:30,-1:8,138,-1:22,16" +
"1:17,-1,161:11,-1,164:2,127,129,164:2,131,164:20,44,40,164:30,-1:8,161,-1:2" +
"2,161:7,51,161:9,-1,161:11,-1,164:2,127,129,164:2,131,164:20,74,40,164:30,-" +
"1:8,161,-1:22,161:7,52,161:9,-1,161:11,-1,171:2,123,171:3,133,171:20,74,40," +
"171:30,-1:8,161,-1:22,161:13,53,161:3,-1,161:11,-1,171:2,123,171:3,109,171:" +
"20,44,40,171:30,-1:8,54,-1:22,161:17,-1,161:11,-1,123:6,125,123:20,46,40,12" +
"3:30,-1:8,161,-1:22,161:16,142,-1,161:11,-1,127:6,121,127:20,100,40,127:30," +
"-1:8,144,-1:22,161:17,-1,161:11,-1,123:6,141,123:20,100,40,123:30,-1:8,161," +
"-1:22,161:3,55,161:13,-1,161:11,-1,127:6,143,127:20,100,40,127:30,-1:8,161," +
"-1:22,145,161:16,-1,161:11,-1:8,161,-1:22,161:7,146,161:9,-1,161:11,-1:8,16" +
"1,-1:22,161:9,147,161:7,-1,161:11,-1:8,161,-1:22,161:8,56,161:8,-1,161:11,-" +
"1:8,161,-1:22,161,82,161:15,-1,161:11,-1,149:3,162,42,73,30,149:4,42:2,149:" +
"15,-1,149:30,-1:8,28,-1:22,28:17,76:4,81:2,76,81:3,28:2,-1,151:2,156,151:3," +
"83,151:20,44,40,151:30,-1:8,161,-1:22,161:4,157,161:12,-1,161:11,-1:8,161,-" +
"1:22,161:3,118,161:13,-1,161:11,-1:8,161,-1:22,161:7,128,161:9,-1,161:11,-1" +
",184:2,168,184:3,158,184:20,100,40,184:30,-1:8,161,-1:22,161:2,140,161:14,-" +
"1,161:11,-1,162:4,80,84,72,162:4,80:2,162:15,-1,162:30,-1:8,28,-1:22,28:17," +
"81:10,28:2,-1:8,161,-1:22,161:3,124,161:13,-1,161:11,-1:8,161,-1:22,161:5,8" +
"6,161:11,-1,161:11,-1:8,28,-1:22,28:17,85:10,28:2,-1,171:2,168,171:3,133,17" +
"1:20,44,40,171:30,-1:8,161,-1:22,161:7,90,161:9,-1,161:11,-1:8,28,-1:22,28:" +
"17,89:10,28:2,-1,171:2,123,171:3,133,171:20,44,40,171:30,-1:8,161,-1:22,161" +
",93,161:13,153,161,-1,161:11,-1:8,28,-1:22,28:17,92:10,28:2,-1:8,161,-1:22," +
"161:2,96,161:6,99,161:7,-1,161:11,-1:8,28,-1:22,28:17,76:3,95,98:2,76,98:3," +
"28:2,-1:8,161,-1:22,161:14,102,161:2,-1,161:11,-1:8,28,-1:22,28:17,101:10,2" +
"8:2,-1:8,161,-1:22,161:5,105,161:11,-1,161:11,-1:8,28,-1:22,28:17,104:10,28" +
":2,-1:8,28,-1:22,28:17,107:10,28:2,-1:8,28,-1:22,28:17,110:10,28:2,-1:8,28," +
"-1:22,28:17,113:10,28:2,-1:8,28,-1:22,28:17,150:4,163:2,150:2,163,150,28:2," +
"-1,184:2,168,184:3,158,184:20,46,40,184:30,-1:8,28,-1:22,28:17,163:10,28:2," +
"-1:8,28,-1:22,28:17,167:10,28:2,-1:8,28,-1:22,28:17,170:10,28:2,-1:8,28,-1:" +
"22,28:17,150:4,173:2,150,175,173,150,28:2,-1:8,28,-1:22,28:17,177:10,28:2,-" +
"1:8,28,-1:22,28:17,179:10,28:2,-1:8,28,-1:22,28:17,180:10,28:2,-1:8,28,-1:2" +
"2,28:17,181:10,28:2,-1:8,28,-1:22,28:17,182:10,28:2,-1:8,28,-1:22,28:17,183" +
":3,185:3,183,185:3,28:2,-1:8,28,-1:22,28:17,185:10,28:2,-1:8,28,-1:22,28:17" +
",186:10,28:2,-1:8,28,-1:22,28:17,183:3,187:3,188,187:3,28:2,-1:8,28,-1:22,2" +
"8:17,189:10,28:2,-1:8,28,-1:22,28:17,190:10,28:2,-1:8,28,-1:22,28:17,191:10" +
",28:2,-1:8,28,-1:22,28:17,192:10,28:2,-1:8,28,-1:22,28:17,193:10,28:2,-1:8," +
"28,-1:22,28:17,194:8,195,194,28:2,-1:8,28,-1:22,28:17,195:10,28:2,-1:8,28,-" +
"1:22,28:17,196:10,28:2,-1:8,28,-1:22,28:17,194:5,197,194:2,198,194,28:2,-1:" +
"8,28,-1:22,28:17,199:10,28:2,-1:8,28,-1:22,28:17,200:10,28:2,-1:8,28,-1:22," +
"28:17,201:10,28:2,-1:8,28,-1:22,28:17,204:10,28:2,-1:8,28,-1:22,28:17,202:1" +
"0,28:2,-1:8,28,-1:22,28:17,203:4,204:2,203,204:3,28:2,-1:8,28,-1:22,28:17,2" +
"05:10,28:2,-1:8,28,-1:22,28:17,203:3,206,207:2,203,207:3,28:2,-1:8,28,-1:22" +
",28:17,208:10,28:2,-1:8,28,-1:22,28:17,209:10,28:2,-1:8,28,-1:22,28:17,210:" +
"4,213:2,210,213:3,28:2,-1:8,28,-1:22,28:17,213:10,28:2,-1:8,28,-1:22,28:17," +
"211:10,28:2,-1:8,28,-1:22,28:17,212:5,213,212:2,213,212,28:2,-1:8,28,-1:22," +
"28:17,214:10,28:2,-1:8,28,-1:22,28:17,212:4,215,216,212:2,216,212,28:2,-1:8" +
",28,-1:22,28:17,217:10,28:2,-1:8,28,-1:22,28:17,218:4,219,222,218,219,222,2" +
"19,28:2,-1:8,28,-1:22,28:17,219:3,218,219,222,219,218,222,218,28:2,-1:8,28," +
"-1:22,28:17,222:10,28:2,-1:8,28,-1:22,28:17,219:5,222,219:2,222,219,28:2,-1" +
":8,28,-1:22,28:17,218:4,219,222,218:2,222,218,28:2,-1:8,28,-1:22,28:17,220:" +
"10,28:2,-1:8,28,-1:22,28:17,221:4,222:2,221,222:3,28:2,-1:8,28,-1:22,28:17," +
"223:10,28:2,-1:8,28,-1:22,28:17,221:3,224,225:2,221,225:3,28:2,-1:8,28,-1:2" +
"2,28:17,226:5,227,226:2,228,226,28:2,-1:8,28,-1:22,28:17,229:4,227:2,229,22" +
"7,228,227,28:2,-1:8,28,-1:22,28:17,230:3,229,228:2,230,229,228,229,28:2,-1:" +
"8,28,-1:22,28:17,228:10,28:2,-1:8,28,-1:22,28:17,231:10,28:2,-1:8,28,-1:22," +
"28:17,232,233,234,233:7,28:2,-1:8,28,-1:22,28:17,235:4,236,237,235,236,238," +
"236,28:2,-1:8,28,-1:22,28:17,239:10,28:2");

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
						{}
					case -7:
						break;
					case 6:
						{ return new Symbol(TokenIds.DIVIDE, new Token("DIVIDE", yyline)); }
					case -8:
						break;
					case 7:
						{ return new Symbol(TokenIds.TIMES, new Token("TIMES", yyline)); }
					case -9:
						break;
					case 8:
						{ throw new RuntimeException("Unmatched lexeme " +
                            yytext() + " at line " + yyline); }
					case -10:
						break;
					case 9:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -11:
						break;
					case 10:
						{
    return new Symbol(
            TokenIds.LEX_ERROR, new Token("LEX_ERROR", "Unsupported Character", yyline));
}
					case -12:
						break;
					case 11:
						{ return new Symbol(TokenIds.LBRACE, new Token("LBRACE", yyline)); }
					case -13:
						break;
					case 12:
						{ return new Symbol(TokenIds.RBRACE, new Token("RBRACE", yyline)); }
					case -14:
						break;
					case 13:
						{ return new Symbol(TokenIds.LPAREN, new Token("LPAREN", yyline)); }
					case -15:
						break;
					case 14:
						{ return new Symbol(TokenIds.RPAREN, new Token("RPAREN", yyline)); }
					case -16:
						break;
					case 15:
						{ return new Symbol(TokenIds.MINUS, new Token("MINUS", yyline)); }
					case -17:
						break;
					case 16:
						{ return new Symbol(TokenIds.PLUS, new Token("PLUS", yyline)); }
					case -18:
						break;
					case 17:
						{ return new Symbol(TokenIds.LSQBRACE, new Token("LSQBRACE", yyline)); }
					case -19:
						break;
					case 18:
						{ return new Symbol(TokenIds.RSQBRACE, new Token("RSQBRACE", yyline)); }
					case -20:
						break;
					case 19:
						{ return new Symbol(TokenIds.ASSIGN, new Token("ASSIGN", yyline)); }
					case -21:
						break;
					case 20:
						{ return new Symbol(TokenIds.LT, new Token("LT", yyline)); }
					case -22:
						break;
					case 21:
						{ return new Symbol(TokenIds.GT, new Token("GT", yyline)); }
					case -23:
						break;
					case 22:
						{ return new Symbol(TokenIds.NOT, new Token("NOT", yyline)); }
					case -24:
						break;
					case 23:
						{ return new Symbol(TokenIds.MODULUS, new Token("MODULUS", yyline)); }
					case -25:
						break;
					case 24:
						{ return new Symbol(TokenIds.SEMI, new Token("SEMI", yyline)); }
					case -26:
						break;
					case 25:
						{ return new Symbol(TokenIds.COMMA, new Token("COMMA", yyline)); }
					case -27:
						break;
					case 26:
						{ return new Symbol(TokenIds.DOT, new Token("DOT", yyline)); }
					case -28:
						break;
					case 27:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -29:
						break;
					case 28:
						{return new Symbol(TokenIds.LEX_ERROR,
                             new Token("LEX_ERROR", "Illegal Identifier", yyline)); }
					case -30:
						break;
					case 29:
						{}
					case -31:
						break;
					case 30:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","unterminated multiline comment", yyline));
}
					case -32:
						break;
					case 31:
						{ return new Symbol(TokenIds.DECR, new Token("DECR", yyline)); }
					case -33:
						break;
					case 32:
						{ return new Symbol(TokenIds.INCR,  new Token("INCR", yyline)); }
					case -34:
						break;
					case 33:
						{ return new Symbol(TokenIds.EQ, new Token("EQ", yyline)); }
					case -35:
						break;
					case 34:
						{ return new Symbol(TokenIds.LEQ, new Token("LEQ", yyline)); }
					case -36:
						break;
					case 35:
						{ return new Symbol(TokenIds.GEQ, new Token("GEQ", yyline)); }
					case -37:
						break;
					case 36:
						{ return new Symbol(TokenIds.NE, new Token("NE", yyline)); }
					case -38:
						break;
					case 37:
						{ return new Symbol(TokenIds.AND, new Token("AND", yyline)); }
					case -39:
						break;
					case 38:
						{ return new Symbol(TokenIds.OR, new Token("OR", yyline)); }
					case -40:
						break;
					case 39:
						{
    if (yytext().length() > 5000) {
                return new Symbol(TokenIds.error, new Token("LEX_ERROR",
                "String constant of illegal length", yyline));
    } else {
        return new Symbol(TokenIds.STRING_CONST, new Token("STRING_CONST",
        yytext().substring(1,yytext().length()-1), yyline));
    }
}
					case -41:
						break;
					case 40:
						{
    return new Symbol(
            TokenIds.error, new Token("LEX_ERROR","String constant unterminated",yyline));
}
					case -42:
						break;
					case 41:
						{ return new Symbol(TokenIds.IF, new Token("IF", yyline)); }
					case -43:
						break;
					case 43:
						{ return new Symbol(TokenIds.NEW, new Token("NEW", yyline)); }
					case -44:
						break;
					case 44:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant spanning multiple lines",yyline));
}
					case -45:
						break;
					case 45:
						{ return new Symbol(TokenIds.FOR, new Token("FOR", yyline)); }
					case -46:
						break;
					case 46:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant contains illegal escape characters",yyline));
}
					case -47:
						break;
					case 47:
						{ return new Symbol(TokenIds.BOOLEAN_CONST, new Token("TRUE",yyline)); }
					case -48:
						break;
					case 48:
						{ return new Symbol(TokenIds.ELSE, new Token("ELSE", yyline)); }
					case -49:
						break;
					case 49:
						{}
					case -50:
						break;
					case 50:
						{ return new Symbol(TokenIds.CLASS, new Token("CLASS", yyline)); }
					case -51:
						break;
					case 51:
						{ return new Symbol(TokenIds.BOOLEAN_CONST, new Token("FALSE",yyline)); }
					case -52:
						break;
					case 52:
						{ return new Symbol(TokenIds.WHILE, new Token("WHILE", yyline)); }
					case -53:
						break;
					case 53:
						{ return new Symbol(TokenIds.BREAK, new Token("BREAK", yyline)); }
					case -54:
						break;
					case 54:
						{ return new Symbol(TokenIds.RETURN, new Token("RETURN", yyline)); }
					case -55:
						break;
					case 55:
						{ return new Symbol(TokenIds.EXTENDS, new Token("EXTENDS", yyline)); }
					case -56:
						break;
					case 56:
						{ return new Symbol(TokenIds.INSTANCEOF, new Token("INSTANCEOF", yyline)); }
					case -57:
						break;
					case 57:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
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
    return new Symbol(TokenIds.LEX_ERROR, new Token(
        "LEX_ERROR", yytext() + " int const too big: line " + yyline, yyline));
}
					case -65:
						break;
					case 65:
						{
    return new Symbol(TokenIds.LEX_ERROR, new Token(
        "LEX_ERROR", yytext() + " int const too big: line " + yyline, yyline));
}
					case -66:
						break;
					case 66:
						{
    return new Symbol(TokenIds.LEX_ERROR, new Token(
        "LEX_ERROR", yytext() + " int const too big: line " + yyline, yyline));
}
					case -67:
						break;
					case 67:
						{
    return new Symbol(TokenIds.LEX_ERROR, new Token(
        "LEX_ERROR", yytext() + " int const too big: line " + yyline, yyline));
}
					case -68:
						break;
					case 68:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -69:
						break;
					case 69:
						{ throw new RuntimeException("Unmatched lexeme " +
                            yytext() + " at line " + yyline); }
					case -70:
						break;
					case 70:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -71:
						break;
					case 71:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -72:
						break;
					case 72:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","unterminated multiline comment", yyline));
}
					case -73:
						break;
					case 74:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant spanning multiple lines",yyline));
}
					case -74:
						break;
					case 75:
						{}
					case -75:
						break;
					case 76:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -76:
						break;
					case 77:
						{ throw new RuntimeException("Unmatched lexeme " +
                            yytext() + " at line " + yyline); }
					case -77:
						break;
					case 78:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -78:
						break;
					case 79:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -79:
						break;
					case 81:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -80:
						break;
					case 82:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -81:
						break;
					case 83:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -82:
						break;
					case 85:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -83:
						break;
					case 86:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -84:
						break;
					case 87:
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
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -107:
						break;
					case 111:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -108:
						break;
					case 112:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -109:
						break;
					case 113:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
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
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -138:
						break;
					case 142:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -139:
						break;
					case 143:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -140:
						break;
					case 144:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -141:
						break;
					case 145:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -142:
						break;
					case 146:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -143:
						break;
					case 147:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
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
            "LEX_ERROR","unterminated multiline comment", yyline));
}
					case -146:
						break;
					case 150:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
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
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -149:
						break;
					case 153:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -150:
						break;
					case 154:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -151:
						break;
					case 155:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -152:
						break;
					case 156:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -153:
						break;
					case 157:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -154:
						break;
					case 158:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -155:
						break;
					case 159:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
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
            "LEX_ERROR","unterminated multiline comment", yyline));
}
					case -159:
						break;
					case 163:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -160:
						break;
					case 164:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -161:
						break;
					case 165:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
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
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -168:
						break;
					case 172:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -169:
						break;
					case 173:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -170:
						break;
					case 174:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -171:
						break;
					case 175:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -172:
						break;
					case 176:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
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
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
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
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
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
					case 239:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -236:
						break;
					case 240:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -237:
						break;
					case 241:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -238:
						break;
					case 242:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -239:
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
