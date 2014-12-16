package compiler;
import compiler.SymbolTable;
import compiler.Type;
import compiler.Descriptor;

//This was written by Cory Haight-Nali on or around 28 October, 2014.
//This class works with the others to make a compiler.
//Specifically, this class is the parser.
//The parser takes the tokens from the scanner, and parses them.

public class Parser extends Scanner{
	private BasicType intType;
	private ArrayType intArrayType;
	private BasicType stringType;
	private ProcedureType procedureType;
	private SymbolTable symTable;
	private String procName;
	private String holdingString;
	private Descriptor procDescriptor;
	private Descriptor intDescriptor;
	private Descriptor stringDescriptor;
	
	private int flag;
	
	public Parser(String path) {
		super(path);
		intType = new BasicType("int", Type.wordSize, null); 
		intArrayType = new ArrayType(Type.addressSize, intType);
		stringType = new BasicType("string", Type.wordSize, null);
		symTable = new SymbolTable();
		symTable.push(); //This is the basic level for the global scope.
	}//constructor method

	//Follows are a large number of nextXXX declarations. 
	//These all exist to parse the nextXXX, 
	//and most rely upon one or more "basic" nextXXX methods.
	//For example, nextExpression parses the next expression, 
	//in terms of conjunctions (using nextConjunction).
	//nextConjunction parses the next conjunction, in terms of comparisons, and so on.
	private void nextBeginStatement(){
		enter("nextBeginStatement");
		nextToken();//Skip the leading begin
		if(getToken() != boldEndToken){
			nextStatement();
			while(getToken() == semicolonToken){
				nextToken();
				nextStatement();
			}//Grab more statements as long as no 'end' token has been found.
		}//if
		nextExpected("No 'end' statement found!", boldEndToken);
		exit("nextBeginStatement");
	}//nextBeginStatement

	private void nextCodeStatement(){
		enter("nextCodeStatement");
		nextToken(); //Skip the leading boldCodeToken
		nextString(); //not correct, needs to be changed to deal with assembly code
		exit("nextCodeStatement");
	}//nextCodeStatement

	private void nextComparison(){
		enter("nextComparison");
		nextSum();
		if((getToken() >= equalToken && getToken() <= greaterToken) || 
				(getToken() >= lessEqualToken && getToken() <= lessToken)){
			nextToken();//Skip the found comparison operator token
			nextSum();
		}//if statement to get the other sum, to complete a binary comparison
		exit("nextComparison");
	}//nextComparison

	private void nextConjunction(){
		enter("nextConjunction");
		nextComparison();
		while(getToken() == boldAndToken){
			nextToken();//The while loop found the boldAndToken, now we skip it
			nextComparison();
		}//while loop to grab more comparisons
		exit("nextConjunction");
	}//nextConjunction

	private void nextDeclaration(){
		//in this method, flag is used for what we're declaring
		//flag ==1 means an int ; 2 means a string, 3 means an int array
		enter("nextDeclaration");
		
		switch(getToken()){
		case openBracketToken:{
			nextToken(); //Skip the open bracket
			nextExpected("Integer declaration desired", intConstantToken);
			nextExpected("Close bracket desired", closeBracketToken);
			nextExpected("Matching int desired", boldIntToken);	
			flag = 3;
			break;
		}
		case boldIntToken:{
			flag = 1;
			break;
		}
		case stringConstantToken:{
			flag = 2;
			break;
		}
		default:{
			error("Array, int, or string declaration expected");
		}
		}
		
		switch(flag){
		
		case 1:{
			symTable.setDescriptor(holdingString, intType);
		}
		case 2:{
			symTable.setDescriptor(holdingString, stringType);
		}
		default:{//flag == 3
			symTable.setDescriptor(holdingString, intArrayType);
		}
		}
		
		exit("nextDeclaration");
	}//nextDeclaration

	private void nextExpected(String message, int ... tokens){
		//This method takes an error message, message and a set of tokens.
		//It checks if the next token is in the set of tokens.
		//If the token is, awesome, skip it ; if not, call an error
		for(int token:tokens){
			if(getToken() == token){
				nextToken();
				return;
			}//if
		}//for
		error(message);	
	}//nextExpected

	private void nextExpression(){
		enter("nextExpression");
		nextConjunction();
		while(getToken() == boldOrToken){
			nextToken(); //Put this one in to skip the boldOrToken
			nextConjunction();
		}//while loop to grab more conjunctions
		exit("nextExpression");
	}//nextExpression

	private void nextIfStatement(){
		enter("nextIfStatement");
		nextToken();//Skip the leading boldIfToken
		nextExpression();
		nextExpected("No 'then' found!", boldThenToken);
		nextStatement();
		nextToken();
		if(getToken() == boldElseToken){
			nextStatement();
		}//if there's an 'else' clause
		exit("nextIfStatement");
	}//nextIfStatement

	private void nextProcedure(){
		enter("nextProcedure");
		symTable.push(); //add a new local scope to the symbol table for this procedure
		nextToken();//skip the leading boldProcToken
		nextExpected("Name token expected", nameToken);
		nextExpected("Open paren expected", openParenToken);
		if(getToken() != closeParenToken){
			nextDeclaration();
			while(getToken() == commaToken){
				nextToken(); //Skip the comma
				nextDeclaration();
			}//while
		}//if
		nextExpected("Close parenthesis expected", closeParenToken);
		if(getToken() == boldIntToken){
			procDescriptor.setType(intType);
		}
		else if(getToken() == stringConstantToken){
			procDescriptor.setType(stringType);
		}
		else{
			error("int or string expected");
		}
		nextToken();//skip the int or string
		nextExpected("Colon expected", colonToken);
		if(getToken() != boldBeginToken){
			nextDeclaration();
			while(getToken() == semicolonToken){
				nextToken(); //Skip the semicolon
				nextDeclaration();
			}//while
		}//if
		nextBeginStatement();
		symTable.pop(); //pop the local scope
		exit("nextProcedure");
	}//nextProcedure
	
	private void nextProcedureHead(){
		nextToken();//skip the leading boldProcToken
		procName = nextName(getChar()); //get the name
		procedureType = new ProcedureType();
		symTable.setDescriptor(procName, procDescriptor);
		nextExpected("Open paren expected", openParenToken);
		while(getToken() != closeParenToken){
			if(getToken() == boldIntToken){
				procedureType.addParameter(intType);
				nextToken();//skip the boldIntToken
				nextToken();//skip the name of the parameter
			}
			else if(getToken() == stringConstantToken){
				procedureType.addParameter(stringType);
				nextToken();//skip the stringConstantToken
				nextToken();//skip the name of the parameter
			}
			else
				error("Procedure parameter expected");
		}//while to get parameters
		nextExpected("Close paren expected", closeParenToken);
		//if statement to get the procedure's returned value type
		if(getToken() == boldIntToken){
			procedureType.addValue(intType);
		}
		else if(getToken() == stringConstantToken){
			procedureType.addValue(stringType);
		}
		else
			error("Procedure return value expected");
	}//nextProcedureHead

	private void nextProduct(){
		enter("nextProduct");
		nextTerm();
		while((getToken() == starToken) || (getToken() == slashToken)){
			nextToken();//skip the found multiplication or division symbol
			nextTerm();
		}//if statement to get a potential second part of the product
		exit("nextProduct");
	}//nextProduct

	private void nextProgramPart(){
		if(getToken() == boldProcToken){
			nextProcedureHead();
		}
		else
			nextToken(); //skip the non-proc token
	}//nextProgramPartPassOne
	
	private void nextProgramPassOne(){
		enter("nextProgramPassOne");
		while(getChar() != eofChar){
			nextProgramPart();
		}//while we're not at the end of the file, keep reading program parts
		exit("nextProgramPassOne");
	}//nextProgramPassOne
	
	public void nextProgramPassTwo(){
		enter("nextProgramPassTwo");
		nextToken(); //Shouldn't be here, but makes the code work better.  
			//Thus I'm presumably missing a nextToken(); at the end of a few methods
		if((getToken() == boldIntToken) || (getToken() == stringConstantToken) 
				|| (getToken() == openBracketToken)){
			nextDeclaration();
		}
		else if(getToken() == boldProcToken){
			nextProcedure();
		}
		else error("Proper beginning of the program desired.");
		//Parse the intiial declaration or procedure
		
		while(getToken() == semicolonToken){
		nextToken(); //skip the semicolon
			if((getToken() == boldIntToken) || (getToken() == stringConstantToken)
					|| (getToken() == openBracketToken)){
				nextDeclaration();
			}
			else if(getToken() == boldProcToken){
				nextProcedure();
			}
			else{
				error("Beginning of another Declaration or Procedure desired.");
			}
		}//while there is another semicolon, 
		//go around and parse another procedure or declaration
		exit("nextProgramPassTwo");
	}//nextProgramPassTwo

	private void nextStatement(){
		enter("nextStatement");	
		switch (getToken()){
		case nameToken:{
			nextToken();//skip the nameToken
			if(getToken() == openParenToken){
				if(getToken() != closeParenToken){
					nextToken();
					nextExpression();
					while(getToken() == commaToken){
						nextToken(); //Skip the comma
						nextExpression();
					}//while
					nextExpected("Closing parenthesis expected", closeParenToken);
				}//if
			}//elif
			else{
				if(getToken() == openBracketToken){
					nextToken();//Skip the open bracket token
					nextExpression();
					nextExpected("Closing bracket expected", closeBracketToken);
				}//elif
				nextExpected("Colon equals (assignment operator) expected", colonEqualToken);
				nextExpression();
			}//else
			break;
		}//case if nameToken is the first token

		case boldBeginToken:{
			nextBeginStatement();
			break;
		}//case to parse a beginToken
		case boldCodeToken:{
			nextCodeStatement();
			break;
		}//case to parse a code statement
		case boldIfToken:{
			nextIfStatement();
			break;
		}//case to parse an if statement
		case boldValueToken:{
			nextValueStatement();
			break;
		}//case to parse a value statement
		case boldWhileToken:{
			nextWhileStatement();
			break;
		}
		default:{
			error("Ill-formed statement");
			break;
		}
		}//switch statement to replace a long if/elif chain
		exit("nextStatement");
	}//nextStatement

	private void nextSum(){
		enter("nextSum");
		nextProduct();
		while((getToken() == plusToken) || (getToken() == dashToken)){
			nextToken();//skip the found addition or subtraction symbol
			nextProduct();
		}//if statement to get the possible second part of the sum
		exit("nextSum");
	}//nextSum
	
	private void nextTerm(){
		enter("nextTerm");
		if((getToken() == dashToken) || (getToken() == boldNotToken)){
			nextToken();//Skip the noticed dash/not
			nextTerm();
		}
		else nextUnit();
		exit("nextTerm");
	}//nextTerm

	private Type nextUnit(){
		enter("nextUnit");
		
		switch(getToken()){
		case intConstantToken:{
			nextToken();
			return intType;
		}
		case stringConstantToken:{
			nextToken();
			return stringType;
		}//if the next token is int or string, skip it
		case openParenToken:{
			nextToken(); 
			nextExpression();
			nextExpected("Close parenthesis not found.", closeParenToken); 
			break;
		}//First we skip the (.  Then we grab an expression, and then balance the paren with a ).
		case nameToken:{
			nextToken();
			if(getToken() == openBracketToken){
				nextToken();
				nextExpression();
				nextExpected("Close bracket not found.", closeBracketToken);
			}//if name is followed by an open bracket
			else if(getToken() == openParenToken){
				nextToken();
				if(getToken() != closeParenToken){
					nextExpression();
					while(getToken() == commaToken){
						nextToken();
						nextExpression();
					}//while
				}//if
				nextExpected("Close parenthesis not found", closeParenToken);
			}//else if name is followed by a parenthesis
			break;
		}//case to follow the BNF diagram branch beginning with "name"
		default:{
			error("Ill-formed expression (unit)");
		}
		}//switch
		exit("nextUnit");
	}//nextUnit
	
	private void nextValueStatement(){
		enter("nextValueStatement");
		nextToken();//Skip the leading boldValueToken
		nextExpression();
		exit("nextValueStatement");
	}//nextValueStatement
	
	private void nextWhileStatement(){
		enter("nextWhileStatement");
		nextToken(); //Skip the leading boldWhileToken
		nextExpression();
		nextExpected("No 'do' found", boldDoToken);
		nextStatement();
		exit("nextWhileStatement");
	}//nextWhileStatement
	
	private void passOne(){
		enter("passOne");
		nextProgramPassOne();
		exit("passOne");
	}//passOne
	
	private void passTwo(){
		enter("passTwo");
		nextProgramPassTwo();
		exit("passTwo");
	}//passTwo
	
	public static void main(String[] args){
		Parser activeParser = new Parser(args[0]);
		activeParser.passOne();
		activeParser.close();
		activeParser.reread();
		activeParser.passTwo();
		activeParser.close();
	}//main
	
}//program
