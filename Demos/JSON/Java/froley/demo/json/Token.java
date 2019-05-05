package froley.demo.json;

import java.io.*;
import java.util.*;

public class Token
{
  // GLOBAL PROPERTIES
  static public String nextFilepath;
  static public String nextSource;
  static public int    nextLine;
  static public int    nextColumn;

  // PROPERTIES
  public String filepath;
  public String source;
  public String content;
  public int    type;
  public int    line;
  public int    column;

  // METHODS
  public Token( int type )
  {
    this( type, null );
  }

  public Token( int type, String content )
  {
    this( type, content, nextFilepath, nextSource, nextLine, nextColumn );
  }

  public Token( int type, String content, String filepath, String source, int line, int column )
  {
    this.type     = type;
    this.content  = content;
    this.filepath = filepath;
    this.source   = source;
    this.line     = line;
    this.column   = column;
  }

  public Token cloned( int newType )
  {
    if (newType == 0) newType = TokenType.EOI;
    return new Token( type, content, filepath, source, line, column );
  }

  public Error error( String message )
  {
    return new Error( filepath, source, line, column, message );
  }

  public String toString()
  {
    if (content != null) return content;
    return TokenType.symbols[ type ];
  }
}
