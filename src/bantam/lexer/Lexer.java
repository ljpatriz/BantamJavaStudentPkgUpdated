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
		/* 38 */ YY_END,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NOT_ACCEPT,
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
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NOT_ACCEPT,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
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
		/* 86 */ YY_NOT_ACCEPT,
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
		/* 238 */ YY_NO_ANCHOR,
		/* 239 */ YY_NO_ANCHOR,
		/* 240 */ YY_NO_ANCHOR,
		/* 241 */ YY_NO_ANCHOR,
		/* 242 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"29:9,2,3,29:2,8,29:18,1,21,27,9:2,22,25,9,12,13,5,15,24,14,29,4,47,49,48,53" +
",50,56,54,51,52,55,9,23,19,18,20,9:2,57:26,16,6,17,9,58,9,32,42,30,46,37,38" +
",57,44,40,57,43,31,57,7,39,57:2,35,33,34,36,57,41,45,57:2,10,26,11,9,29,0,2" +
"8")[0];

	private int yy_rmap[] = unpackFromString(1,243,
"0,1:4,2,1:2,3,1:5,4,5,1:2,6,7,8,9,1:3,10,11,12,13,1:10,14,15,14,1,14,1,14:2" +
",15,14:7,11:10,16,17,18,19,20,21,22,23,24,25,26,27,28,29,11,30,31,24,32,33," +
"34,35,36,37,38,39,40,41,42,43,44,45,46,23,47,48,49,50,51,52,53,54,55,56,57," +
"58,16,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81," +
"82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,84,98,82,99,66,100,101,102," +
"84,14,103,104,55,105,106,105,107,108,109,110,111,60,112,113,114,115,116,117" +
",118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,13" +
"6,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,149,152,153,1" +
"54,155,156,157,158,159,157,160,161,162,163,164,165,166,167,168,169,170,171," +
"172,173,174,175,176,177,178,179")[0];

	private int yy_nxt[][] = unpackFromString(180,59,
"1,2,3,4,5,6,7,8,-1,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,67,75,25," +
"1,7,147,160:3,167,170,160,173,175,160,68,177,179,160:4,66,240,241,242:7,160" +
",26,-1:63,27,28,-1:60,160,-1:22,160:7,76,160:9,-1,160:11,-1:14,29,-1:59,30," +
"-1:61,31,-1:58,32,-1:58,33,-1:58,34,-1:41,25:2,69,25:2,77,25:20,37,38,25:30" +
",-1:7,26,-1:22,26:29,-1,27:2,-1,27:4,-1,27:19,-1,27:30,-1,148:3,40:2,28,148" +
",161,148:3,40:2,148:14,-1,148:30,-1:7,160,-1:22,160:17,-1,160:11,-1,40:4,71" +
",78,40,82,40:19,-1,40:30,-1:7,26,-1:22,26:17,65:10,26:2,-1:7,26,-1:22,26:17" +
",66,240,241,242:7,26:2,-1:25,35,-1:40,154,-1:22,160:8,39,160:8,-1,160:11,-1" +
",150:2,69,150:2,81,150:20,42,38,150:30,-1,70:3,-1:2,70:6,-1:2,70:14,-1,70:3" +
"0,-1,40:3,47,71,78,40,82,40:19,-1,40:30,-1,98:5,117,98:20,-1:2,98:30,-1,82:" +
"4,86,-1,82:21,-1,82:30,-1:7,26,-1:22,26:17,55:5,26,55:2,26,55,26:2,-1:26,36" +
",-1:39,160,-1:22,160:11,41,160:5,-1,160:11,-1,85:2,89,85:2,92,95,85:19,98,3" +
"8,85:5,95:2,85:2,95,85:20,-1,40:5,78,40,82,40:19,-1,40:30,-1:7,160,-1:22,16" +
"0:2,165,160:14,-1,160:11,-1,153:2,151,153:2,101,164,153:19,72,38,153:5,164:" +
"2,153:2,164,153:20,-1:7,26,-1:22,26:17,56:10,26:2,-1:7,160,-1:22,160:6,106," +
"160:10,-1,160:11,-1,163:2,169,163:2,104,163,107,163:18,44,38,163:30,-1,82:3" +
",73,86,-1,82:21,-1,82:30,-1:7,26,-1:22,26:17,57:10,26:2,-1:7,160,-1:22,160:" +
"4,109,160:12,-1,160:11,-1,172:2,169,172:2,110,172,113,172:18,42,38,172:30,-" +
"1:7,26,-1:22,26:17,58:10,26:2,-1:7,160,-1:22,160:3,112,160:13,-1,160:11,-1," +
"95:2,157,95:2,77,95:20,98,38,95:30,-1:7,26,-1:22,26:17,55:5,59,55:2,59,55,2" +
"6:2,-1:7,160,-1:22,160,114,160:15,-1,160:11,-1,95:2,157,95:2,115,95:20,37,3" +
"8,95:30,-1:7,26,-1:22,26:17,60:10,26:2,-1:7,160,-1:22,160:5,43,160:11,-1,16" +
"0:11,-1:7,26,-1:22,26:17,61:10,26:2,-1:7,160,-1:22,160:10,118,160:6,-1,160:" +
"11,-1,164:2,127,164:2,81,164,166,164:18,72,38,164:30,-1:7,26,-1:22,26:17,62" +
":10,26:2,-1:7,160,-1:22,160:7,120,160:9,-1,160:11,-1,85:2,89,85:2,131,163,8" +
"5:19,98,38,85:5,163:2,85:2,163,85:20,-1:7,26,-1:22,26:17,63:10,26:2,-1:7,16" +
"0,-1:22,160:7,45,160:9,-1,160:11,-1,163:2,169,163:2,131,163,107,163:18,44,3" +
"8,163:30,-1:7,26,-1:22,26:17,64:10,26:2,-1:7,160,-1:22,160:6,124,160:10,-1," +
"160:11,-1,135:2,137,135:2,133,172,135:19,72,38,135:5,172:2,135:2,172,135:20" +
",-1:7,160,-1:22,160:7,46,160:9,-1,160:11,-1,172:2,123,172:2,133,172,113,172" +
":18,42,38,172:30,-1:7,160,-1:22,160:3,128,160:13,-1,160:11,-1,95:2,157,95:2" +
",115,95:20,98,38,95:30,-1:7,160,-1:22,160:4,158,160:12,-1,160:11,-1,98:5,11" +
"7,98:21,-1,98:30,-1:7,160,-1:22,160,130,160:15,-1,160:11,-1,119:2,155,119:2" +
",121,119:20,37,38,119:30,-1:7,160,-1:22,160:2,132,160:14,-1,160:11,-1,137:2" +
",151,137:2,139,127,137:19,98,38,137:5,127:2,137:2,127,137:20,-1:7,160,-1:22" +
",160:3,48,160:13,-1,160:11,-1,123:5,141,123:20,44,38,123:30,-1:7,160,-1:22," +
"160:5,134,160:11,-1,160:11,-1,159:2,137,159:2,141,123,159:19,98,38,159:5,12" +
"3:2,159:2,123,159:20,-1:7,136,-1:22,160:17,-1,160:11,-1,127:5,143,127:20,37" +
",38,127:30,-1:7,160,-1:22,160:7,49,160:9,-1,160:11,-1,164:2,127,164:2,129,1" +
"64,166,164:18,72,38,164:30,-1:7,160,-1:22,160:7,50,160:9,-1,160:11,-1,163:2" +
",169,163:2,131,163,107,163:18,98,38,163:30,-1:7,160,-1:22,160:13,51,160:3,-" +
"1,160:11,-1,172:2,123,172:2,133,172,113,172:18,72,38,172:30,-1:7,52,-1:22,1" +
"60:17,-1,160:11,-1,172:2,123,172:2,110,172,113,172:18,42,38,172:30,-1:7,160" +
",-1:22,160:16,140,-1,160:11,-1,123:5,125,123:20,44,38,123:30,-1:7,142,-1:22" +
",160:17,-1,160:11,-1,127:5,121,127:20,98,38,127:30,-1:7,160,-1:22,160:3,53," +
"160:13,-1,160:11,-1,123:5,141,123:20,98,38,123:30,-1:7,160,-1:22,144,160:16" +
",-1,160:11,-1,127:5,143,127:20,98,38,127:30,-1:7,160,-1:22,160:7,145,160:9," +
"-1,160:11,-1:7,160,-1:22,160:9,146,160:7,-1,160:11,-1:7,160,-1:22,160:8,54," +
"160:8,-1,160:11,-1:7,160,-1:22,160,80,160:15,-1,160:11,-1,148:3,40,71,28,14" +
"8,161,148:3,40:2,148:14,-1,148:30,-1:7,26,-1:22,26:17,74:4,79:2,74,79:3,26:" +
"2,-1,150:2,155,150:2,81,150:20,42,38,150:30,-1:7,160,-1:22,160:4,156,160:12" +
",-1,160:11,-1:7,160,-1:22,160:3,116,160:13,-1,160:11,-1:7,160,-1:22,160:7,1" +
"26,160:9,-1,160:11,-1,164:2,157,164:2,129,164,166,164:18,42,38,164:30,-1:7," +
"160,-1:22,160:2,138,160:14,-1,160:11,-1,161:3,82,86,70,161:5,82:2,161:14,-1" +
",161:30,-1:7,26,-1:22,26:17,79:10,26:2,-1,164:2,127,164:2,129,164,166,164:1" +
"8,42,38,164:30,-1:7,160,-1:22,160:3,122,160:13,-1,160:11,-1:7,160,-1:22,160" +
":5,84,160:11,-1,160:11,-1:7,26,-1:22,26:17,83:10,26:2,-1,172:2,169,172:2,13" +
"3,172,113,172:18,42,38,172:30,-1:7,160,-1:22,160:7,88,160:9,-1,160:11,-1:7," +
"26,-1:22,26:17,87:10,26:2,-1:7,160,-1:22,160,91,160:13,152,160,-1,160:11,-1" +
":7,26,-1:22,26:17,90:10,26:2,-1:7,160,-1:22,160:2,94,160:6,97,160:7,-1,160:" +
"11,-1:7,26,-1:22,26:17,74:3,93,96:2,74,96:3,26:2,-1:7,160,-1:22,160:14,100," +
"160:2,-1,160:11,-1:7,26,-1:22,26:17,99:10,26:2,-1:7,160,-1:22,160:5,103,160" +
":11,-1,160:11,-1:7,26,-1:22,26:17,102:10,26:2,-1:7,26,-1:22,26:17,105:10,26" +
":2,-1:7,26,-1:22,26:17,108:10,26:2,-1:7,26,-1:22,26:17,111:10,26:2,-1:7,26," +
"-1:22,26:17,149:4,162:2,149:2,162,149,26:2,-1:7,26,-1:22,26:17,162:10,26:2," +
"-1:7,26,-1:22,26:17,168:10,26:2,-1:7,26,-1:22,26:17,171:10,26:2,-1:7,26,-1:" +
"22,26:17,149:4,174:2,149,176,174,149,26:2,-1:7,26,-1:22,26:17,178:10,26:2,-" +
"1:7,26,-1:22,26:17,180:10,26:2,-1:7,26,-1:22,26:17,181:10,26:2,-1:7,26,-1:2" +
"2,26:17,182:10,26:2,-1:7,26,-1:22,26:17,183:10,26:2,-1:7,26,-1:22,26:17,184" +
":3,185:3,184,185:3,26:2,-1:7,26,-1:22,26:17,185:10,26:2,-1:7,26,-1:22,26:17" +
",186:10,26:2,-1:7,26,-1:22,26:17,184:3,187:3,188,187:3,26:2,-1:7,26,-1:22,2" +
"6:17,189:10,26:2,-1:7,26,-1:22,26:17,190:10,26:2,-1:7,26,-1:22,26:17,191:10" +
",26:2,-1:7,26,-1:22,26:17,192:10,26:2,-1:7,26,-1:22,26:17,193:10,26:2,-1:7," +
"26,-1:22,26:17,194:8,195,194,26:2,-1:7,26,-1:22,26:17,195:10,26:2,-1:7,26,-" +
"1:22,26:17,196:10,26:2,-1:7,26,-1:22,26:17,194:5,197,194:2,198,194,26:2,-1:" +
"7,26,-1:22,26:17,199:10,26:2,-1:7,26,-1:22,26:17,200:10,26:2,-1:7,26,-1:22," +
"26:17,201:10,26:2,-1:7,26,-1:22,26:17,204:10,26:2,-1:7,26,-1:22,26:17,202:1" +
"0,26:2,-1:7,26,-1:22,26:17,203:4,204:2,203,204:3,26:2,-1:7,26,-1:22,26:17,2" +
"05:10,26:2,-1:7,26,-1:22,26:17,203:3,206,207:2,203,207:3,26:2,-1:7,26,-1:22" +
",26:17,208:10,26:2,-1:7,26,-1:22,26:17,209:10,26:2,-1:7,26,-1:22,26:17,210:" +
"4,213:2,210,213:3,26:2,-1:7,26,-1:22,26:17,213:10,26:2,-1:7,26,-1:22,26:17," +
"211:10,26:2,-1:7,26,-1:22,26:17,212:5,213,212:2,213,212,26:2,-1:7,26,-1:22," +
"26:17,214:10,26:2,-1:7,26,-1:22,26:17,212:4,215,216,212:2,216,212,26:2,-1:7" +
",26,-1:22,26:17,217:10,26:2,-1:7,26,-1:22,26:17,218:4,219,222,218,219,222,2" +
"19,26:2,-1:7,26,-1:22,26:17,219:3,218,219,222,219,218,222,218,26:2,-1:7,26," +
"-1:22,26:17,222:10,26:2,-1:7,26,-1:22,26:17,219:5,222,219:2,222,219,26:2,-1" +
":7,26,-1:22,26:17,218:4,219,222,218:2,222,218,26:2,-1:7,26,-1:22,26:17,220:" +
"10,26:2,-1:7,26,-1:22,26:17,221:4,222:2,221,222:3,26:2,-1:7,26,-1:22,26:17," +
"223:10,26:2,-1:7,26,-1:22,26:17,221:3,224,225:2,221,225:3,26:2,-1:7,26,-1:2" +
"2,26:17,226:5,227,226:2,228,226,26:2,-1:7,26,-1:22,26:17,229:4,227:2,229,22" +
"7,228,227,26:2,-1:7,26,-1:22,26:17,230:3,229,228:2,230,229,228,229,26:2,-1:" +
"7,26,-1:22,26:17,228:10,26:2,-1:7,26,-1:22,26:17,231:10,26:2,-1:7,26,-1:22," +
"26:17,232,233,234,233:7,26:2,-1:7,26,-1:22,26:17,235:4,236,237,235,236,238," +
"236,26:2,-1:7,26,-1:22,26:17,239:10,26:2");

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
						{ return new Symbol(TokenIds.AND, new Token("AND", yyline)); }
					case -37:
						break;
					case 36:
						{ return new Symbol(TokenIds.OR, new Token("OR", yyline)); }
					case -38:
						break;
					case 37:
						{
    if (yytext().length() > 5000) {
                return new Symbol(TokenIds.error, new Token("LEX_ERROR",
                "String constant of illegal length", yyline));
    } else {
        return new Symbol(TokenIds.STRING_CONST, new Token("STRING_CONST",
        yytext().substring(1,yytext().length()-1), yyline));
    }
}
					case -39:
						break;
					case 38:
						{
    return new Symbol(
            TokenIds.error, new Token("LEX_ERROR","String constant unterminated",yyline));
}
					case -40:
						break;
					case 39:
						{ return new Symbol(TokenIds.IF, new Token("IF", yyline)); }
					case -41:
						break;
					case 41:
						{ return new Symbol(TokenIds.NEW, new Token("NEW", yyline)); }
					case -42:
						break;
					case 42:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant spanning multiple lines",yyline));
}
					case -43:
						break;
					case 43:
						{ return new Symbol(TokenIds.FOR, new Token("FOR", yyline)); }
					case -44:
						break;
					case 44:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant contains illegal escape characters",yyline));
}
					case -45:
						break;
					case 45:
						{ return new Symbol(TokenIds.BOOLEAN_CONST, new Token("TRUE",yyline)); }
					case -46:
						break;
					case 46:
						{ return new Symbol(TokenIds.ELSE, new Token("ELSE", yyline)); }
					case -47:
						break;
					case 47:
						{}
					case -48:
						break;
					case 48:
						{ return new Symbol(TokenIds.CLASS, new Token("CLASS", yyline)); }
					case -49:
						break;
					case 49:
						{ return new Symbol(TokenIds.BOOLEAN_CONST, new Token("FALSE",yyline)); }
					case -50:
						break;
					case 50:
						{ return new Symbol(TokenIds.WHILE, new Token("WHILE", yyline)); }
					case -51:
						break;
					case 51:
						{ return new Symbol(TokenIds.BREAK, new Token("BREAK", yyline)); }
					case -52:
						break;
					case 52:
						{ return new Symbol(TokenIds.RETURN, new Token("RETURN", yyline)); }
					case -53:
						break;
					case 53:
						{ return new Symbol(TokenIds.EXTENDS, new Token("EXTENDS", yyline)); }
					case -54:
						break;
					case 54:
						{ return new Symbol(TokenIds.INSTANCEOF, new Token("INSTANCEOF", yyline)); }
					case -55:
						break;
					case 55:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
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
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -67:
						break;
					case 67:
						{ throw new RuntimeException("Unmatched lexeme " +
                            yytext() + " at line " + yyline); }
					case -68:
						break;
					case 68:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -69:
						break;
					case 69:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -70:
						break;
					case 70:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","unterminated multiline comment", yyline));
}
					case -71:
						break;
					case 72:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant spanning multiple lines",yyline));
}
					case -72:
						break;
					case 73:
						{}
					case -73:
						break;
					case 74:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -74:
						break;
					case 75:
						{ throw new RuntimeException("Unmatched lexeme " +
                            yytext() + " at line " + yyline); }
					case -75:
						break;
					case 76:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -76:
						break;
					case 77:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -77:
						break;
					case 79:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -78:
						break;
					case 80:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -79:
						break;
					case 81:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -80:
						break;
					case 83:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -81:
						break;
					case 84:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -82:
						break;
					case 85:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -83:
						break;
					case 87:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -84:
						break;
					case 88:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -85:
						break;
					case 89:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -86:
						break;
					case 90:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -87:
						break;
					case 91:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -88:
						break;
					case 92:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -89:
						break;
					case 93:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -90:
						break;
					case 94:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -91:
						break;
					case 95:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -92:
						break;
					case 96:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -93:
						break;
					case 97:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -94:
						break;
					case 98:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -95:
						break;
					case 99:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -96:
						break;
					case 100:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -97:
						break;
					case 101:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -98:
						break;
					case 102:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -99:
						break;
					case 103:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -100:
						break;
					case 104:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -101:
						break;
					case 105:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -102:
						break;
					case 106:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -103:
						break;
					case 107:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -104:
						break;
					case 108:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -105:
						break;
					case 109:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -106:
						break;
					case 110:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -107:
						break;
					case 111:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
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
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","unterminated multiline comment", yyline));
}
					case -145:
						break;
					case 149:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -146:
						break;
					case 150:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
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
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -154:
						break;
					case 158:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
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
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -157:
						break;
					case 161:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","unterminated multiline comment", yyline));
}
					case -158:
						break;
					case 162:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -159:
						break;
					case 163:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
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
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -163:
						break;
					case 167:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -164:
						break;
					case 168:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -165:
						break;
					case 169:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
}
					case -166:
						break;
					case 170:
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
					case -167:
						break;
					case 171:
						{
    return new Symbol(TokenIds.INT_CONST,
            new Token("INT_CONST", yytext().replaceFirst("^0+(?!$)", ""), yyline));
}
					case -168:
						break;
					case 172:
						{
    return new Symbol(TokenIds.error, new Token(
            "LEX_ERROR","String constant unterminated",yyline));
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
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
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
						{ return new Symbol(TokenIds.ID,
                                new Token("ID", yytext(),yyline));}
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
