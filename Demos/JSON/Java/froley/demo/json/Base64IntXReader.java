package froley.demo.json;

class Base64IntXReader
{
  String            data;
  BossStringBuilder builder = new BossStringBuilder();
  int               remainingBase64;
  int               position;
  int               available;
  int               nextByte;
  int               nextNextByte;

  public Base64IntXReader( String encoded )
  {
    while ((encoded.length() & 3) != 0) encoded += '=';

    remainingBase64 = (encoded.length() * 3) / 4;
    for (int i=encoded.length(); --i>=0;)
    {
      if (encoded.charAt(i) != '=') break;
      --remainingBase64;
    }
    data = encoded;
  }

  public boolean hasAnother()
  {
    return (remainingBase64 > 0);
  }

  public int readInt32X()
  {
    int result = readBase64();
    if ((result & 0xC0) != 0x80)
    {
      // leftmost 2 bits are not %10
      // -64..127
      if (result < 128) return result;
      else              return (result - 256);
    }
    else
    {
      // 10NNNxxx xxxxxxxx*N
      int n = ((result>>3) & 7) + 1; // 1 to 8 bytes follow
      result = (result & 7);         //  0..7 (unsigned)
      if (result >= 4) result -= 8;  // -4..3 (signed)

      for (int i=n; --i>=0;) result = (result << 8) | readBase64();

      return result;
    }
  }

  public String readString()
  {
    builder.clear();
    int n = readInt32X();
    for (int i=n; --i>=0;)
    {
      builder.writeUnicode( readInt32X() );
    }
    return builder.toString();
  }

  protected int readBase64()
  {
    if (remainingBase64 == 0) return 0;
    --remainingBase64;

    if (--available >= 0)
    {
      int result = nextByte;
      nextByte = nextNextByte;
      return result;
    }

    int b1 = base64ToValue( position );
    int b2 = base64ToValue( position+1 );
    int b3 = base64ToValue( position+2 );
    int b4 = base64ToValue( position+3 );
    position += 4;

    int result = (b1 << 2) | (b2 >> 4);
    nextByte = ((b2 & 15) << 4) | (b3 >> 2);
    nextNextByte = ((b3 & 3) << 6) | b4;
    available = 2;
    return result;
  }

  protected base64ToValue( int pos )
  {
    int base64 = data.charAt( pos );
    if (base64 >= 'A' && base64 <= 'Z') return (base64 - 'A');
    if (base64 >= 'a' && base64 <= 'z') return (base64 - 'a') + 26;
    if (base64 >= '0' && base64 <= '9') return (base64 - '0') + 52;
    if (base64 == '+') return 62;
    return 63;
  }
}