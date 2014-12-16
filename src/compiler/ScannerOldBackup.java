package compiler;

import java.util.Hashtable;

//Written by Cory Haight-Nali on or around 07 October 2014.
//This class works with the others to make a compiler.
//As you may have guessed from the name, this class is the scanner.
//This class extends Source, which extends Common, thus 'cascadingly' extending them both.

class Scanner extends Source{
	int token;
	String holdingString = "";
	int constantInt = 0;
	private static final char    andChar = '\u2227';  //  alternate, unicode, AND character
	private static final char    orChar = '\u2228';  //  alternate, unicode, OR character
	private static final char	gEqualChar = '\u2265'; //alternate unicode greater than or equals char
	private static final char 	lEqualChar = '\u2264'; //alternate unicode lesser than or equals char
	private static final char	nEqualChar = '\u2260'; //alternate unicode not-equals character
	private static final char 	starChar = '\u00D7'; //alternate unicode star character
	private Hashtable<String, Integer> nameTable;
	
	public Scanner(String path){
		super (path); //magic keyword to call the superclass's constructor to initialize its variables
		nameTable = new Hashtable<String, Integer>();//create the nameTable to see if keywords are reserved
		//Now, we initialize a whole bunch of keywords!		
		nameTable.put("begin", boldBeginToken);
		nameTable.put("and", boldAndToken);
		nameTable.put("code", boldCodeToken);
		nameTable.put("do", boldDoToken);
		nameTable.put("else", boldElseToken);
		nameTable.put("end", boldEndToken);
		nameTable.put("if", boldIfToken);
		nameTable.put("int", boldIntToken);
		nameTable.put("not", boldNotToken);
		nameTable.put("or", boldOrToken);
		nameTable.put("proc", boldProcToken);
		nameTable.put("string", boldStringToken);
		nameTable.put("then", boldThenToken);
		nameTable.put("value", boldValueToken);
		nameTable.put("while", boldWhileToken);
		nameTable.put("]", closeBracketToken);
		nameTable.put(")", closeParenToken);
		nameTable.put(":=", colonEqualToken);
		nameTable.put(":", colonToken);
		nameTable.put(",", commaToken);
		nameTable.put("-", dashToken);
		nameTable.put("end of file", endFileToken);
		nameTable.put("=", equalToken);
		nameTable.put(">=", greaterEqualToken);
		nameTable.put(">", greaterToken);
		nameTable.put("ignored", ignoredToken);
		nameTable.put("int constant", intConstantToken);
		nameTable.put("<=", lessEqualToken);
		nameTable.put("<>", lessGreaterToken);
		nameTable.put(">", lessToken);
		nameTable.put("name", nameToken);
		nameTable.put("[", openBracketToken);
		nameTable.put("open paren", openParenToken);
		nameTable.put("+", plusToken);
		nameTable.put(";", semicolonToken);
		nameTable.put("/", slashToken);
		nameTable.put("star", starToken);
		nameTable.put("string constant", stringConstantToken);

		
		
		
	}//Scanner

	public int getToken(){
		return token;
	}//getToken

	public int getInt(String theString){
		return Integer.parseInt(theString);
	}//getInt

	public void nextToken(){//Here's where the magic happens.  Read the input, and decide what the next token should be
		token = ignoredToken;
		while(token == ignoredToken){
			if(isDigit(getChar())){
				holdingString ="";
				while(isDigit(getChar())){
					holdingString += getChar();
					nextChar();
				}//Keep reading more characters while they're numbers
				try{
					constantInt = Integer.parseInt(holdingString);
				}
				catch (Exception e){
					error("Integer value out of bounds");
				}
			}
			switch(getChar()){
			case ' ': nextBlank(); 
			break;
			case 'a': 
				nextChar();
				if(getChar() == 'n' ){
					nextChar();
					if(getChar()== 'd' ){
						nextSingleToken(boldAndToken);
					}
				}//if the input is 'and'
				break;
			case andChar: nextSingleToken(boldAndToken); 
				break;//The other way to get an and symbol: ^
			case 'b':
				nextChar();
				if(getChar() == 'e'){
					nextChar();
					if(getChar() == 'g'){
						nextChar();
						if(getChar() == 'i'){
							nextChar();
							if(getChar() == 'n'){
								nextSingleToken(boldBeginToken);
							}
						}	
					}
				}//if the input is 'begin'
				break;
			case 'c':
				nextChar(); 
				if(getChar() == 'o'){ 
					nextChar(); 
					if(getChar() == 'd'){ 
						nextChar(); 
						if(getChar() == 'e'){
							nextSingleToken(boldCodeToken);
						}
					}
				}
				break;

			case 'd':
				nextChar(); 
				if(getChar() == 'o'){ 
					nextSingleToken(boldDoToken);
				}//next token is do
				break;

			case 'e':
				nextChar(); 
				if(getChar() == 'l'){ 
					nextChar(); 
					if(getChar() == 's'){ 
						nextChar(); 
						if(getChar() == 'e'){ 
							nextSingleToken(boldElseToken);
						}
					}
				}//next token is else
				else if(getChar() == 'n'){//We're still in the case of the first letter being e, so check for the rest of 'end' 
					nextChar(); 
					if(getChar() == 'd'){ 
						nextSingleToken(boldEndToken);
					}
				}//next token is end!
				break;
				
			case 'i':
				nextChar(); 
				if(getChar() == 'f'){ 
					nextSingleToken(boldIfToken);
				}//next token is if!

				else if(getChar() == 'n'){ 
					nextChar(); 
					if(getChar() == 't'){ 
						nextSingleToken(boldIntToken);
					}
				}//next token is int
				break;
				
			case 'n':
				nextChar(); 
				if(getChar() == 'o'){ 
					nextChar(); 
					if(getChar() == 't'){ 
						nextSingleToken(boldNotToken);
					}
				}//next token then is not
				break;
				
			case 'o': 
				nextChar(); 
				if(getChar() == 'r'){ 
					nextSingleToken(boldOrToken);
				}
				break;
				
			case orChar: nextSingleToken(boldOrToken);//in both cases, next token is Or
				break;
			
			case 'p': 
				nextChar(); 
				if(getChar() == 'r'){ 
					nextChar(); 
					if(getChar() == 'o'){ 
						nextChar(); 
						if(getChar() == 'c'){ 
							nextSingleToken(boldProcToken);
						}
					}
				}//next token is Proc
				break;
				
			case 's': 
				nextChar(); 
				if(getChar() == 't'){ 
					nextChar(); 
					if(getChar() == 'r'){ 
						nextChar(); 
						if(getChar() == 'i'){ 
							nextChar(); 
							if(getChar() == 'n'){ 
								nextChar(); 
								if(getChar() == 'g'){ 
									nextSingleToken(boldStringToken);
								}
							}
						}
					}
				}//next token is String
				break;
				
			case 't': 
				nextChar(); 
				if(getChar() == 'h'){ 
					nextChar(); 
					if(getChar() == 'e'){ 
						nextChar(); 
						if(getChar() == 'n'){ 
							nextSingleToken(boldThenToken);
						}
					}
				}//next token is Then
				break;

			case 'v': 
				nextChar(); 
				if(getChar() == 'a'){ 
					nextChar(); 
					if(getChar() == 'l'){ 
						nextChar(); 
						if(getChar() == 'u'){ 
							nextChar(); 
							if(getChar() == 'e'){ 
								nextSingleToken(boldValueToken);
							}
						}
					}
				}//next token is Value
				break;

			case 'w':
				nextChar(); 
				if(getChar() == 'h'){ 
					nextChar(); 
					if(getChar() == 'i'){ 
						nextChar(); 
						if(getChar() == 'l'){ 
							nextChar(); 
							if(getChar() == 'e'){ 
								nextSingleToken(boldWhileToken);
							}
						}
					}
				}//next token is While
				break;
				
			case ']': nextSingleToken(closeBracketToken);
				break;
			case ')': nextSingleToken(closeParenToken);
			break;
			case ':':
				nextChar();
				if(getChar() == '=') nextSingleToken(colonEqualToken);
				else nextSingleToken(colonToken);
				break;
			case ',': nextSingleToken(commaToken);
			break;
			case '-': nextSingleToken(dashToken);
			break;
			case eofChar: nextSingleToken(endFileToken);
			break;
			case '=': nextSingleToken(equalToken);
			break;
			case '>':
				nextChar();
				if(getChar() == '=') nextSingleToken(greaterEqualToken);
				else nextSingleToken(greaterToken);
				break;
			case gEqualChar: nextSingleToken(greaterEqualToken);
			break;
			case'<':
				nextChar();
				if(getChar() == '=') nextSingleToken(lessEqualToken);
				else if(getChar() == '>') nextSingleToken(lessGreaterToken);
				else nextSingleToken(lessToken);
				break;
			case lEqualChar: nextSingleToken(lessEqualToken);
			break;
			case nEqualChar: nextSingleToken(lessGreaterToken);
			break;

			case '#': 
				nextChar();
				nextSingleToken(ignoredToken);
				while((getChar() != eofChar) && (!atLineEnd())){
					nextChar();
				} //it's a comment, so we just finish the line
				break;				

			case '+': nextSingleToken(plusToken);
			break;
			case '[': nextSingleToken(openBracketToken);
			break;
			case '(': nextSingleToken(openParenToken);
			break;
			case ';': nextSingleToken(semicolonToken);
			break;
			case '/': nextSingleToken(slashToken);
			break;
			case '*': nextSingleToken(starToken);
			break;
			case starChar: nextSingleToken(starToken);
			break;
			case '"':
				holdingString = "";
				while(getChar() != '"'){
					if(atLineEnd()) {
						error("Endless string found !");
					}//the string did not end on the same line as it began, so throw an error
					holdingString += getChar();
					nextChar();
				}//while the string is still ongoing ; that is, it hasn't been ended with another doublequote
				break;
			
			default: error("Input not recognized.");
			
			
			nextChar();
			}//switch to figure out what to do with the next token	
			
		}//while loop to grab more tokens
	}//nextToken
	
	private boolean isDigit(char ch){
		return (ch >= '0' && ch <= '9');
	}//isDigit
	
	private boolean isLetter(char ch){
		// check if ch is a letter - copied directly from the example of what NOT to do in the Java documentation 
		//(http://docs.oracle.com/javase/tutorial/i18n/text/charintro.html)
		// as this only checks for English characters, and the Java gods desire Java to be multinational.
		// Happily, SNARL is not designed by the Java gods, so this test is good enough for us.
		return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'));
	}//isLetter
	
	private void nextBlank(){
		nextChar(); //character's a blank, so march on to the next one
	}//nextBlank
	
	private void nextSingleToken(int token){
		this.token = token;
		nextChar();
	}//nextSingleToken
	
	public static void main(String[] args){
		if(args.length < 1){
			//error("The scanner did not receive a file!");
		}
		Scanner activeScanner = new Scanner(args[0]);
		while(activeScanner.getChar() != eofChar){
			activeScanner.nextToken();
			System.out.println(tokenToString(activeScanner.token));
		}//while loop to keep getting more tokens
	}//main
}//class
