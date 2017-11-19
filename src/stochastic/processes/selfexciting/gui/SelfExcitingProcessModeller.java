package stochastic.processes.selfexciting.gui;

import static java.lang.System.err;
import static java.lang.System.out;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import fastmath.arb.Real;
import stochastic.processes.selfexciting.AbstractSelfExcitingProcess;
import stochastic.processes.selfexciting.ExponentialSelfExcitingProcess;
import stochastic.processes.selfexciting.SelfExcitingProcessFactory;
import stochastic.processes.selfexciting.SelfExcitingProcessFactory.Type;

public class SelfExcitingProcessModeller
{

  private final class ModelParametersFileFilter extends FileFilter
  {
    @Override
    public String
           getDescription()
    {
      return process.getClass().getSimpleName() + " parameters (" + process.getType().getFilenameExtension() + " )";
    }

    @Override
    public boolean
           accept(File f)
    {
      return f.getName().contains("." + process.getType().getFilenameExtension() + ".") || f.isDirectory();
    }
  }

  private JFrame frame;
  private JComboBox<SelfExcitingProcessFactory.Type> processTypeComboBox;
  private ParameterPanel parameterPanel;
  private ExponentialSelfExcitingProcess process;
  private Container contentPane;
  private KernelPanel kernelPanel;
  private DefaultTableModel coeffecientModel;
  private JTable coeffecientTable;

  /**
   * Launch the application.
   */
  public static void
         main(String[] args)
  {
    EventQueue.invokeLater(() -> {
      try
      {
        SelfExcitingProcessModeller window = new SelfExcitingProcessModeller();
        window.frame.setVisible(true);
      }
      catch (Exception e)
      {
        e.printStackTrace(err);
      }
    });
  }

  /**
   * Create the application.
   */
  public SelfExcitingProcessModeller()
  {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   * 
   * @wbp.parser.entryPoint
   */
  private void
          initialize()
  {
    frame = new JFrame();
    frame.setTitle(getClass().getSimpleName());
    frame.setBounds(100, 100, 2000, 700);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    contentPane = frame.getContentPane();
    contentPane.setLayout(new BorderLayout());

    JPanel topPanel = getTopPanel();
    topPanel.setMaximumSize(new Dimension(3000, 500));
    contentPane.add(topPanel, BorderLayout.PAGE_START);
    refreshProcess();

    updateParameterPanel();

    refreshTypeComboBox(null);

    contentPane.add(kernelPanel = new KernelPanel(process), BorderLayout.PAGE_END);

    doLayout();
  }

  public JPanel
         getTopPanel()
  {
    JPanel topPanel = new JPanel(new BorderLayout());

    Type[] processTypes = SelfExcitingProcessFactory.Type.values();
    processTypeComboBox = new JComboBox<>(processTypes);
    processTypeComboBox.addActionListener(this::refreshTypeComboBox);

    JPanel topLeftPanel = new JPanel(new FlowLayout());

    topLeftPanel.add(processTypeComboBox);
    JButton loadParameterButton = new JButton("Load parameters");
    loadParameterButton.addActionListener(event -> {
      JFileChooser fileChooser = new JFileChooser(Paths.get(".").toAbsolutePath().normalize().toString());
      fileChooser.setFileFilter(new ModelParametersFileFilter());
      if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
      {
        EventQueue.invokeLater(() -> {
          out.println("Loading parameters from " + fileChooser.getSelectedFile());
          process.loadParameters(fileChooser.getSelectedFile());
          parameterPanel.sliderEnabled = false;
          for (Field field : process.getParameterFields())
          {
            parameterPanel.setParameterValue(field.getName(), process.getFieldValue(field));
          }
          parameterPanel.sliderEnabled = true;
        });
      }

    });
    topLeftPanel.add(loadParameterButton);
    JButton loadPointsButton = new JButton("Load points");
    topLeftPanel.add(loadPointsButton);
    loadPointsButton.addActionListener((ActionListener) event -> {
      JFileChooser fileChooser = new JFileChooser(Paths.get(".").toAbsolutePath().normalize().toString());
      fileChooser.setFileFilter(new FileFilter()
      {

        @Override
        public boolean
               accept(File f)
        {
          return f.getName().toLowerCase().endsWith(".mat") || f.isDirectory();
        }

        @Override
        public String
               getDescription()
        {

          return ".mat files";
        }
      });

      if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
      {
        EventQueue.invokeLater(() -> {
          out.println("Loading (marked) points from " + fileChooser.getSelectedFile());
        });
      }
    });

    topPanel.add(topLeftPanel, BorderLayout.WEST);

    process = (ExponentialSelfExcitingProcess) Type.values()[0].instantiate(1);

    coeffecientModel = new DefaultTableModel(process != null ? process.order() : 0, tableColumnNames.length);
    coeffecientModel.setColumnIdentifiers(tableColumnNames);
    coeffecientTable = new JTable(coeffecientModel);
    coeffecientTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    setAmplitudeDecayValues();

    JPanel topRightPanel = new JPanel(new BorderLayout());

    tableScroller = new JScrollPane(coeffecientTable);
    topRightPanel.add(tableScroller, BorderLayout.PAGE_START);
    topPanel.add(topRightPanel, BorderLayout.CENTER);
    topRightPanel.revalidate();
    // ModelViewer.getLogPriceChart(process);
    topRightBottomPanel = new JPanel();
    topRightPanel.add(topRightBottomPanel, BorderLayout.PAGE_END);
    return topPanel;
  }

  public static void

         resizeColumns(JTable table)
  {
    final TableColumnModel columnModel = table.getColumnModel();
    for (int column = 0; column < table.getColumnCount(); column++)
    {
      int width = 50; // Min width
      for (int row = 0; row < table.getRowCount(); row++)
      {
        TableCellRenderer renderer = table.getCellRenderer(row, column);
        Component comp = table.prepareRenderer(renderer, row, column);
        width = Math.max(comp.getPreferredSize().width + 1, width);
      }
      // if (width > 300)
      // width = 300;
      columnModel.getColumn(column).setPreferredWidth(width + 10);
    }
  }

  JPanel topRightBottomPanel;

  public void

         updateParameterPanel()
  {
    if (parameterPanel != null)
    {
      topRightBottomPanel.remove(parameterPanel);
    }
    topRightBottomPanel.add(parameterPanel = new ParameterPanel(process, this::onParameterUpdated), BorderLayout.PAGE_END);
    topRightBottomPanel.revalidate();
    doLayout();
  }

  public void
         onParameterUpdated()
  {
    if (kernelPanel != null)
    {
      kernelPanel.refreshGraphs();
    }
    setAmplitudeDecayValues();
  }

  public void
         doLayout()
  {
    frame.validate();
    frame.pack();
  }

  public void
         refreshTypeComboBox(ActionEvent event)
  {
    refreshProcess();
    setAmplitudeDecayValues();

    /**
     * TODO: add m and M here
     */
    // JPanel mrowPanel = getParameterRow("m", 0.1, 5, 1000);
    // JPanel MrowPanel = getParameterRowPanel("M", 1, 15, 15);
    // JSpinner Mspinner = new JSpinner(new SpinnerNumberModel(15, 1, 15, 1));

    // parameterPanel.add(Mspinner);
    // JPanel MowPanel = new JPanel(new GridLayout(1, 5));

  }

  public AbstractSelfExcitingProcess
         refreshProcess()
  {
    Type type = getSelectedType();
    if (process == null || !process.getType().equals(type))
    {
      out.println("Switched kernel to " + type);
      process = (ExponentialSelfExcitingProcess) type.instantiate(1);
      coeffecientModel.setRowCount(process.order());
      updateParameterPanel();
      if (kernelPanel != null)
      {
        kernelPanel.setProcess(process);
      }

    }
    setAmplitudeDecayValues();
    resizeColumns(coeffecientTable);

    tableScroller.revalidate();

    return process;
  }

  public void
         setAmplitudeDecayValues()
  {
    for (int i = 0; i < process.order(); i++)
    {
      double amplitude = process.α(i);
      double decayRate = process.β(i);
      Real amplifiedJointDecayRate = process.γ(i);
      double halfLife = process.getHalfLife(i);
      coeffecientModel.setValueAt(i, i, 0);
      coeffecientModel.setValueAt(amplitude, i, 1);
      coeffecientModel.setValueAt(decayRate, i, 2);
      coeffecientModel.setValueAt(amplifiedJointDecayRate.toString(), i, 3);
      coeffecientModel.setValueAt(halfLife, i, 4);
    }
    coeffecientTable.repaint();
  }

  public Type
         getSelectedType()
  {
    return SelfExcitingProcessFactory.Type.values()[processTypeComboBox.getSelectedIndex()];
  }

  String[] tableColumnNames = new String[]
  { "#", "α", "β", "γ", "half-life" };

  private JScrollPane tableScroller;

}
