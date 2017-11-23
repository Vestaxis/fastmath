package util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicInteger;

import javax.accessibility.AccessibleContext;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

public class ProgressWindow extends JOptionPane
{
  private boolean cancelled = false;

  @Override
  public void
         setValue(Object newValue)
  {
    if (newValue instanceof Integer)
    {
      myBar.setValue((Integer) newValue);
    }
    else if (newValue instanceof String)
    {
      String cmd = (String) newValue;
      if ("Cancel".equals(cmd))
      {
        cancelled = true;
      }
      else
      {
        System.out.println("unhandled cmd = " + cmd);
      }
    }
  }

  public static void
         main(String args[]) throws InterruptedException
  {
    ProgressWindow pw = new ProgressWindow("message", "note", 30);
    pw.show(null);
    AtomicInteger count = new AtomicInteger();
    while (!pw.cancelled)
    {
      Thread.sleep(1000);
      pw.setValue(count.incrementAndGet());
    }
    pw.close();
  }

  public void
         close()
  {
    dialog.setVisible(false);
    setVisible(false);

  }

  private static Object[] cancelOption;
  private String note;

  ProgressWindow(String message, String note, JProgressBar myBar)
  {
    super(new Object[]
    { message, note, myBar }, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, cancelOption = new Object[]
    { UIManager.getString("OptionPane.cancelButtonText") }, null);
    this.note = note;
    this.myBar = myBar;
    this.message = message;

  }

  public ProgressWindow(String message, String note, int max)
  {
    this(message, note, new JProgressBar(0, max));
  }

  private JProgressBar myBar;
  private JLabel noteLabel;
  private JDialog dialog;

  public void
         show(Component parentComponent)
  {
    if (note != null)
      noteLabel = new JLabel(note);
    dialog = createDialog(parentComponent, UIManager.getString("ProgressMonitor.progressText"));
    dialog.show();
    
  }

  public int
         getMaxCharactersPerLineCount()
  {
    return 60;
  }

  /**
   * Returns the specified component's toplevel <code>Frame</code> or
   * <code>Dialog</code>.
   *
   * @param parentComponent
   *          the <code>Component</code> to check for a <code>Frame</code> or
   *          <code>Dialog</code>
   * @return the <code>Frame</code> or <code>Dialog</code> that contains the
   *         component, or the default frame if the component is
   *         <code>null</code>, or does not have a valid <code>Frame</code> or
   *         <code>Dialog</code> parent
   * @exception HeadlessException
   *              if <code>GraphicsEnvironment.isHeadless</code> returns
   *              <code>true</code>
   * @see java.awt.GraphicsEnvironment#isHeadless
   */
  static Window
         getWindowForComponent(Component parentComponent) throws HeadlessException
  {
    if (parentComponent == null)
      return getRootFrame();
    if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
      return (Window) parentComponent;
    return getWindowForComponent(parentComponent.getParent());
  }

  // Equivalent to JOptionPane.createDialog,
  // but create a modeless dialog.
  // This is necessary because the Solaris implementation doesn't
  // support Dialog.setModal yet.
  public JDialog
         createDialog(Component parentComponent,
                      String title)
  {
    final JDialog dialog;

    Window window = getWindowForComponent(parentComponent);
    if (window instanceof Frame)
    {
      dialog = new JDialog((Frame) window, title, false);
    }
    else
    {
      dialog = new JDialog((Dialog) window, title, false);
    }

    Container contentPane = dialog.getContentPane();

    contentPane.setLayout(new BorderLayout());
    contentPane.add(this, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(parentComponent);
    dialog.addWindowListener(new WindowAdapter()
    {
      boolean gotFocus = false;

      public void
             windowClosing(WindowEvent we)
      {
        setValue(cancelOption[0]);
      }

      public void
             windowActivated(WindowEvent we)
      {
        // Once window gets focus, set initial focus
        if (!gotFocus)
        {
          selectInitialValue();
          gotFocus = true;
        }
      }
    });

    addPropertyChangeListener(new PropertyChangeListener()
    {
      public void
             propertyChange(PropertyChangeEvent event)
      {
        if (dialog.isVisible() && event.getSource() == ProgressWindow.this
            && (event.getPropertyName().equals(VALUE_PROPERTY) || event.getPropertyName().equals(INPUT_VALUE_PROPERTY)))
        {
          dialog.setVisible(false);
          dialog.dispose();
        }
      }
    });

    return dialog;
  }

  /////////////////
  // Accessibility support for ProgressOptionPane
  ////////////////

  /**
   * Gets the AccessibleContext for the ProgressOptionPane
   *
   * @return the AccessibleContext for the ProgressOptionPane
   * @since 1.5
   */
  public AccessibleContext
         getAccessibleContext()
  {
    return ProgressWindow.this.getAccessibleContext();
  }

  /*
   * Returns the AccessibleJOptionPane
   */
  private AccessibleContext
          getAccessibleJOptionPane()
  {
    return super.getAccessibleContext();
  }
}
