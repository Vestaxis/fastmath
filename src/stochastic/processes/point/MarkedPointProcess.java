package stochastic.processes.point;

import static java.lang.System.err;
import static java.lang.System.out;
import static java.util.Arrays.asList;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel.MapMode;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import fastmath.DoubleColMatrix;
import fastmath.DoubleRowMatrix;
import fastmath.Pair;
import fastmath.Vector;
import stochastic.processes.pointprocesses.finance.ArchivableEvent;
import stochastic.processes.pointprocesses.finance.Quote;
import stochastic.processes.pointprocesses.finance.TradeTick;
import stochastic.processes.pointprocesses.finance.TwoSidedQuote;
import stochastics.annotations.Units;
import util.DateUtils;

public class MarkedPointProcess implements Iterable<ArchivableEvent>, Iterator<ArchivableEvent>, Comparable<MarkedPointProcess>
{
  private static double openTime = 9.5;

  @Units(time = TimeUnit.HOURS)
  public final static double closeTime = 16;

  @Units(time = TimeUnit.HOURS)
  public static final double tradingDuration = closeTime - openTime;

  private int eventCount;

  @Override
  public String toString()
  {
    return String.format("MarkedPointProcess[mppFile=%s, eventCount=%s]", mppFile, eventCount);
  }

  private RandomAccessFile indexFile;
  private Vector points;
  String symbol;
  int i = 0;
  private File mppFile;
  private Integer year;
  private Integer month;
  private Integer day;
  private Date date;
  private ByteBuffer buffer;

  public MarkedPointProcess(Pair<RandomAccessFile, RandomAccessFile> pair, File mppFile, String symbol) throws IOException
  {
    this.mppFile = mppFile;
    RandomAccessFile raf = pair.left;
    buffer = raf.getChannel().map(MapMode.READ_ONLY, 0, raf.length()).order(ByteOrder.nativeOrder());
    indexFile = pair.right;
    points = new Vector(buffer);
    this.symbol = symbol;
    eventCount = (int) (indexFile.length() / (Integer.BYTES * 2 + Byte.BYTES));
    String[] tokens = mppFile.getName().split("-");
    year = Integer.valueOf(tokens[1]);
    month = Integer.valueOf(tokens[2]);
    day = Integer.valueOf(tokens[3].split("\\.")[0]);
    date = new GregorianCalendar(year, month, day).getTime();
  }

  private ArchivableEvent getNextEvent(String symbol, Vector points, RandomAccessFile indices) throws IOException
  {
    i++;
    try
    {
      ArchivableEvent.EventType type = ArchivableEvent.EventType.values()[indices.readByte()];
      int pos = indices.readInt();
      int len = indices.readInt();

      Vector point = points.slice(pos, pos + len);

      ArchivableEvent ae = null;
      switch (type)
      {
      case TwoSidedQuote:
        ae = new TwoSidedQuote(point, symbol);
        break;
      case TradeTick:
        ae = new TradeTick(point, symbol);
        break;
      default:
        throw new IllegalArgumentException("unhandled type " + type);
      }
      return ae;
    }
    catch (EOFException e)
    {
      err.println("EOF at record " + i + " but recordCount is " + eventCount);
      return null;
    }
  }

  public <E extends ArchivableEvent> Stream<E> eventStream(Class<E> eventClass)
  {
    return stream().filter(eventClass::isInstance).map(eventClass::cast);
  }

  public Stream<TradeTick> tradeStream()
  {
    return eventStream(TradeTick.class).filter(trade -> trade.getPrice() > 0);
  }

  public Stream<TwoSidedQuote> quoteStream()
  {
    return eventStream(TwoSidedQuote.class);
  }

  public Stream<ArchivableEvent> tradeAndQuoteStream()
  {
    return stream().filter(event -> event instanceof TradeTick || event instanceof Quote);
  }

  @Override
  public boolean hasNext()
  {
    return i < eventCount;
  }

  @Override
  public ArchivableEvent next()
  {
    try
    {
      return getNextEvent(symbol, points, indexFile);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public Iterator<ArchivableEvent> iterator()
  {
    reset();
    return this;
  }

  public int getAge(TimeUnit timeUnit)
  {
    Date now = new Date();
    long diffInMillies = now.getTime() - date.getTime();
    return (int) DateUtils.convertTimeUnits(diffInMillies, TimeUnit.MILLISECONDS, TimeUnit.DAYS);
  }

  @SuppressWarnings("unchecked")
  public <E extends ArchivableEvent> Stream<E> stream()
  {
    reset();
    return (Stream<E>) StreamSupport.stream(spliterator(), false);
  }

  private void reset()
  {
    i = 0;
    try
    {
      indexFile.seek(0);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public Date getDate()
  {
    return date;
  }

  @Override
  public int compareTo(MarkedPointProcess o)
  {
    return date.compareTo(o.getDate());
  }

  public File getMppFile()
  {
    return mppFile;
  }

  public void setMppFile(File mppFile)
  {
    this.mppFile = mppFile;
  }

  /**
   * 
   * @param W
   *          window length in units of seconds
   * 
   * @param dt
   *          discretization size in units of seconds
   * @return
   */
  public DoubleColMatrix discretize(final double dt)
  {
    int m = (int) (tradingDuration / DateUtils.convertToHours(TimeUnit.SECONDS, dt));
    int n = ArchivableEvent.EventType.values().length;
    DoubleColMatrix A = new DoubleColMatrix(m, n);
    for (ArchivableEvent event : this)
    {
      double t = DateUtils.convertTimeUnits(event.getTimeOfDay() - openTime, TimeUnit.HOURS, TimeUnit.SECONDS);
      int tk = (int) (t / dt);
      int type = event.getEventType();
      if (tk >= 0 && tk < m)
      {
        A.set(tk, type, 1); // + A.get( tk, type ) );
      }
    }
    return A;
  }

  public DoubleRowMatrix getTradeMatrix(TimeUnit timeUnit)
  {
    DoubleRowMatrix tradeMatrix = new DoubleRowMatrix(0, TradeTick.FIELDCNT).setName(symbol);

    final Vector lastt = new Vector(1);
    tradeStream().forEach(event -> {
      Vector point = new Vector(event.getMarks());
      double fractionalHourOfDay = event.getTimeOfDay();
      double t = event.getTimeOfDay(timeUnit);
      // double t = fractionalHourOfDay;
      // double t = DateUtils.convertTimeUnits(fractionalHourOfDay,
      // TimeUnit.MILLISECONDS, timeUnits);
      point.set(0, t);
      double prevt = lastt.get(0);
      if (prevt == t) { return; }

      if (fractionalHourOfDay >= openTime && fractionalHourOfDay <= closeTime)
      {
        lastt.set(0, t);
        tradeMatrix.appendRow(point);
      }
    });

    return tradeMatrix;
  }

  public DoubleRowMatrix getBuySellMatrix(TimeUnit timeUnit)
  {
    throw new UnsupportedOperationException("Lee-Ready tick test");
  }
}
