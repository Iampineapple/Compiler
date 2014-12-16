package compiler;

import java.util.Hashtable;

//Written by Cory Haight-Nali on or around 07 October 2014.
//Written with consulting/advice from Adam Dawn, Jim Moen, and David Nyquist.  
//This class works with the others to make a compiler.
//The scanner should look at the snarl source file, and return a great number of tokens.
//The parser will then parse the tokens.
//This class extends Source, which extends Common, thus 'cascadingly' extending them both.

class Scanner extends Source{
	private int token; 
	//This is the int that tells us what the current token is.
	private String holdingString; 
	//This is a 'dummy string' to use as a holding string when scanning multiple characters into one token. 
	private int constantInt;
	//This holds the numeric value of an int when we find an intConstantToken.
	private static final char   andChar = '\u2227';  //  alternate, unicode, AND character
	private static final char   orChar = '\u2228';  //  alternate, unicode, OR character
	private static final char	gEqualChar = '\u2265'; //alternate unicode greater than or equals char
	private static final char 	lEqualChar = '\u2264'; //alternate unicode lesser than or equals char
	private static final char	nEqualChar = '\u2260'; //alternate unicode not-equals character
	private static final char 	starChar =  '\u00D7'; //alternate unicode times character
	private Hashtable<String, Integer> nameTable;
	
	public Scanner(String path){
		super (path); //magic keyword to call the superclass's constructor to initialize its variables
		nameTable = new Hashtable<String, Integer>();//create the nameTable to see if keywords are reserved
		holdingString = "";
		constantInt = 0;
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
		}//Scanner
	
	public int getInt(){
		return constantInt; 
	}//getInt

	public String getString(){
		return holdingString;
	}//getString
	
	public int getToken(){
		return token;
	}//getToken
	
	private boolean isDigit(char ch){	
		return (ch >= '0' && ch <= '9');
	}//isDigit
	
	private boolean isLetter(char ch){
		// check if ch is a letter - copied directly from the example of what
		//NOT to do in the Java documentation 
		//(http://docs.oracle.com/javase/tutorial/i18n/text/charintro.html)
		// as this only checks for English characters, and the 
		//Java gods desire Java to be multinational.
		// Happily, SNARL is not designed by the Java gods, so this test is good enough for us.
		return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'));
	}//isLetter
	
	private void nextBlank(){
		nextChar(); //character's a blank, so march on to the next one
	}//nextBlank
	
	private void nextInteger(char character){
		nextSingleToken(intConstantToken);
		holdingString = "";
		while(isDigit(getChar())){
			holdingString += getChar();
			nextChar();
		}
		try{
			constantInt = Integer.parseInt(holdingString);
		}
		catch(Exception e){
			error("Integer ill-formed");
		}
	}//nextInteger
	
	private void nameLookup(String theString){
		Integer nameLookup;
		nameLookup = nameTable.get(theString);
		if(nameLookup == null){
			nextSingleToken(nameToken);
		}//if the integer is null - that is, if the string in holdingString is not a reserved name
		else{
			nextSingleToken(nameLookup);
		}
	}//nameLookup
	
	public String nextName(char character){
		holdingString = "";
		while(isDigit(getChar()) || isLetter(getChar())){
			holdingString += getChar();
			nextChar();
		}//While loop to grab the entire word/string
		nameLookup(holdingString);
		return holdingString;
	}//nextName

	private void nextSingleToken(int token){
		this.token = token; //assign the object's token to be the passed in token
	}//nextSingleToken
	
	public void nextToken(){
		//This method looks at the first character of a token, and switches to another method.
		//It either determines what the next token is, 
		//or passes the first char to a method that will do so.
		token = ignoredToken;
		while(token == ignoredToken){
			if(isDigit(getChar())){
				nextInteger(getChar());
			}//elif
			else if(isLetter(getChar())){
				nextName(getChar());
			}//elif
			else if(getChar() == '#'){
				while(!atLineEnd()){
					nextChar();
				}//while
				nextChar();
			}//See a #, beginning a comment, and ignore the rest of the line/comment
			else if(getChar() == ' '){
				nextBlank();
			}//elif
			else if(getChar() == ':'){
				nextChar();
				if(getChar() == '='){
					nextSingleToken(colonEqualToken);
					nextChar();
				}//if it is a colonEqualToken
				else nextSingleToken(colonToken);
			}//elif to decide between colonToken or colonEqualToken
			else{
				holdingString = Character.toString(getChar());
				nameLookup(holdingString);
				nextChar();
			}//else
		}//while
	}//nextToken
		
	public static void main(String[] args){
		enter("main");
		Scanner activeScanner = new Scanner(args[0]);

		while(activeScanner.getChar() != eofChar){			
			activeScanner.nextToken();
			System.out.println("Current Token is: "+ activeScanner.getToken() + " aka " + tokenToString(activeScanner.token));
			if((activeScanner.token == nameToken) || (activeScanner.token == stringConstantToken)){
				System.out.println(activeScanner.holdingString);
			}//if the active token is a nameToken or stringConstantToken, print the string
			else if(activeScanner.token == intConstantToken){
				System.out.println(activeScanner.getInt());
			}//otherwise, if the active token is an intConstantToken, print the int
			else if(activeScanner.token != ignoredToken){
				System.out.println(tokenToString(activeScanner.token));
			}
		}//while loop to keep getting more tokens
		exit("main");
	}//main
}//class
