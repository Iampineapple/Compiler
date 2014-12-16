//
//  SNARL/SOURCE. Read characters and assert errors.
//
//    James Moen
//    13 Sep 14
//
package compiler;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//  SOURCE. Read characters from a Snarl source file. Maybe assert errors.

class Source extends Common
{
  private char           ch;         //  Last character read from LINE.
  private String         line;       //  Last line read from READER.
  private int            lineCount;  //  Number of LINEs read from READER.
  private int            lineIndex;  //  Index of current character in LINE.
  private String         path;       //  Pathname of source file.
  private BufferedReader reader;     //  Read source characters from here.

//  Constructor. Return a new SOURCE that reads characters from the source file
//  denoted by PATH.

  public Source(String path)
  {
    this.path = path;
    reread();
  }

//  CLOSE. Break the connection between READER and the source file PATH.

  public void close()
  {
    try
    {
      reader.close();
    }
    catch (IOException ignore)
    {
      throw new RuntimeException("Cannot close " + path + ".");
    }
  }

//  ERROR. Write an error message to standard output, then halt. We first write
//  the current source line, preceded by a 5-digit line number with leading '0'
//  characters. On the next line, we write a caret in the column where we found
//  the error. On the next line, we write MESSAGE.

  public void error(String message)
  {
    int power = 10000;
    int temp  = lineCount;
    for (int count = 1; count <= 5; count += 1)
    {
      System.out.print(temp / power);
      temp %= power;
      power /= 10;
    }
    System.out.println(" " + line);
    writeBlanks(lineIndex + 5);
    System.out.println("↑");
    System.out.println(message);
    System.exit(1);
  }

//  GET CHAR. Return the most recently read character from the source file.

  public char getChar()
  {
    return ch;
  }

//  NEXT CHAR. Read the next character from the source file.

  public void nextChar()
  {
    if (atLineEnd())
    {
      nextLine();
    }
    ch = line.charAt(lineIndex);
    lineIndex += 1;
  }

//  AT LINE END. Test if we've reached the end of the current line.

  public boolean atLineEnd()
  {
    return lineIndex >= line.length();
  }

//  NEXT LINE. Read the next LINE from READER and append a blank. The last line
//  is just an EOF CHAR.

  private void nextLine()
  {
    lineCount += 1;
    lineIndex = 0;
    try
    {
      line = reader.readLine();
      if (line == null)
      {
        line = "" + eofChar;
      }
      else
      {
        line += " ";
      }
    }
    catch (IOException ignore)
    {
      throw new RuntimeException("Cannot read " + path + ".");
    }
  }

//  REREAD. Initialize (or reinitialize) the source file, positioning it at its
//  first character.

  public void reread()
  {
    try
    {
      lineCount = 0;
      lineIndex = 0;
      reader = new BufferedReader(new FileReader(path));
      nextLine();
      nextChar();
    }
    catch (IOException ignore)
    {
      throw new RuntimeException("Cannot open " + path + ".");
    }
  }

//  MAIN. For testing. List a file whose path is on the command line. Do it two
//  times, to test REREAD.

  public static void main(String[] files)
  {
    Source source = new Source(files[0]);

    while (source.getChar() != eofChar)
    {
      while (! source.atLineEnd())
      {
        System.out.print(source.getChar());
        source.nextChar();
      }
      System.out.println();
      source.nextChar();
    }

    source.close();
    source.reread();

    while (source.getChar() != eofChar)
    {
      while (! source.atLineEnd())
      {
        System.out.print(source.getChar());
        source.nextChar();
      }
      System.out.println();
      source.nextChar();
    }
  }
}