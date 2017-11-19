package stochastic.processes.selfexciting.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.GridLayout;

public class fdgfdgfdg
{

  private JFrame frame;

  /**
   * Launch the application.
   */
  public static void
         main(String[] args)
  {
    EventQueue.invokeLater(new Runnable()
    {
      public void
             run()
      {
        try
        {
          fdgfdgfdg window = new fdgfdgfdg();
          window.frame.setVisible(true);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public fdgfdgfdg()
  {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void
          initialize()
  {
    frame = new JFrame();
    frame.setBounds(100, 100, 450, 300);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JSplitPane splitPane = new JSplitPane();
    splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    frame.getContentPane().add(splitPane, BorderLayout.CENTER);
    
    JPanel panel = new JPanel();
    splitPane.setLeftComponent(panel);
    panel.setLayout(new GridLayout(0, 1, 0, 0));
    
    JPanel panel_1 = new JPanel();
    splitPane.setRightComponent(panel_1);
  }

}
