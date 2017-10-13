package stochastic.processes.pointprocesses.finance;

import java.util.HashMap;
import java.util.Map;

public enum TradeType
{
  MANNING_FILL("HE"), // tradeAuditIndex >= 6
  HOUSE_FILL("HE"),
  BOOK_TRADE("DT"),
  BROKER_FILL("BE"),
  CURRENCY_TRADE("FX"),
  CURRENCY_BOOK_TRANSFER("FT"),
  ADR_CONVERSION("AE"),
  POSITION_BOOK_TRANSFER("TR"),
  POSITION_FLIP("PF"),
  ORDER_BOOKING("OB"),
  ORDER_SPLIT("SP"),
  MULTI_FILL("MF"),
  ORDER_CROSS("OX");

  String fidessaCode;

  static Map<String, TradeType> lookupMap = new HashMap<>();
  // Initialize a map to lookup the enum from a string
  static
  {
    for ( TradeType tradeType : TradeType.values() )
    {
      lookupMap.put( tradeType.fidessaCode, tradeType );
    }
  }

  TradeType(String fidessaCode)
  {
    this.fidessaCode = fidessaCode;
  }

  public static TradeType getFromCode( String code, int tradeAuditIndex )
  {
    if ( !code.equals( "HE" ) )
    {
      return lookupMap.get( code );
    }
    if ( tradeAuditIndex >= 6 )
    {
      return MANNING_FILL;
    }
    return HOUSE_FILL;
  }
}
