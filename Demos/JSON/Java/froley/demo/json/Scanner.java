package froley.demo.json;

import java.io.*;

public class Scanner
{
  static public int columnsPerTab = 2;

  public String filepath;
  public String source;
  public int count;
  public int position;
  public int line;
  public int column;
  public int curColumnsPerTab = Scanner.columnsPerTab;

  public Scanner( String filepath, String source )
  {
    this.filepath = filepath;
    count = source.length();
    StringBuilder builder = new StringBuilder( count );
    if (count >= 1 && source.charAt(0) == 0xFEFF)
    {
      // Discard Byte Order Mark (BOM)
      for (int i=1; i<count; ++i)
      {
        builder.print( source.charAt(i) );
      }
    }
    else if (count >= 3 && source.charAt(0) == 0xEF && source.charAt(1) == 0xBB && source.charAt(2) == 0xBF)
    {
      // Discard Byte Order Mark (BOM)
      for (int i=3; i<count; ++i)
      {
        builder.print( source.charAt(i) );
      }
    }
    else
    {
      builder.print( source );
    }
    line = 1;
    column = 1;
    this.source = builder.toString();
  }

  public Scanner( File file )
  {
    filepath = file.getAbsolutePath();
    try
    {
      count = (int) file.length();
      StringBuilder builder = new StringBuilder( count );
      BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream(file.getPath()) ), 1024 );

      int firstCh = reader.read();
      if (firstCh != -1 && firstCh != 0xFEFF)
      {
        // Discard Byte Order Mark (BOM)
        builder.print( (char) firstCh );
      }

      for (int ch=reader.read(); ch!=-1; ch=reader.read())
      {
        builder.print( (char) ch );
      }
      count = builder.length();
      reader.close();
      source = builder.toString();
    }
    catch (Exception err)
    {
      source = "";
    }
    line = 1;
    column = 1;
  }

  public boolean consume( char ch )
  {
    if (position == count) return false;
    if (ch != source.charAt(position)) return false;
    read();
    return true;
  }

  public boolean consume( String value )
  {
    int n = value.length();
    if ( !hasAnother(n) ) return false;
    for (int i=n; --i>=0;)
    {
      if (peek(i) != value.charAt(i)) return false;
    }
    for (int i=n; --i>=0;) read();
    return true;
  }

  public Error error( String message )
  {
    return new Error( filepath, source.toString(), line, column, message );
  }

  public boolean hasAnother()
  {
    return (position < count);
  }

  public boolean hasAnother( int additional )
  {
    return (position+additional <= count);
  }

  public char peek( int lookahead )
  {
    if (position+lookahead >= count) return (char) 0;
    return source.charAt( position+lookahead );
  }

  public char read()
  {
    char ch = source.charAt( position++ );
    switch (ch)
    {
      case '\t': column += curColumnsPerTab; return ch;
      case '\n': ++line; column=1; return ch;
      default:   ++column; return ch;
    }
  }
}