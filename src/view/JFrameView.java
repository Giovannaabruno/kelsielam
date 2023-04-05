package view;



import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;

import java.awt.FlowLayout;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import controller.ImageController;
import model.Layer;
import model.Pixel;
import model.Project;

/**
 * Implements the CollagingView interface. Constructs a GUI using the Swing framework.
 */
// add a ActionListener. hook it to the button to creat the effect
public class JFrameView extends JFrame implements CollagingView {

  private final JLabel projectNameLabel;


  private ImagePanel imagePanel;

  private Project currentProject;

  /**
   * Constructor for the JFrameView class.
   */
  public JFrameView() {
    super("Collaging Images");

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

    JFrame imageWindow = new JFrame();
    imageWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));


    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new FlowLayout());


    JButton loadButton = new JButton("Load Image");
    bottomPanel.add(loadButton);

    JButton saveButton = new JButton("Save Image");
    bottomPanel.add(saveButton);

    ImageController ic = new ImageController();


    JPanel commandPanel = new CommandPanel(ic);
    bottomPanel.add(commandPanel);

    JButton newProjectButton = new JButton("New Project");
    topPanel.add(newProjectButton);

    this.projectNameLabel = new JLabel("No Project");
    topPanel.add(this.projectNameLabel);
    currentProject = ic.newProject(800, 600);
    newProjectButton.addActionListener(new ActionListener() {
      ///part5

      /**
       * ActionPerformed, represents the dialogue messages.
       * @param e the event to be processed
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        String projectName = (String) JOptionPane.showInputDialog(
                topPanel,
                "Enter project name.",
                "New Project Dialog",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                ""
        );
        projectNameLabel.setText("Project Name: " + projectName);
        currentProject = ic.newProject(800, 600);
      }
    });

    JFrame currentWindow = this;
    loadButton.addActionListener(new ActionListener() {

      /**
       * Method actionPerformed, creates the action sequences.
       * @param e the event to be processed
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int retVal = fc.showOpenDialog(currentWindow);
        if (retVal == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fc.getSelectedFile();
          if (!selectedFile.exists()) {
            // throw file not found error
            JOptionPane.showMessageDialog(currentWindow, "File not found.");
          } else {
            Layer layer = null;
            BufferedImage img = null;
            if (currentProject != null) {
              if (getExtension(selectedFile.getName()).equals("ppm")) {
                // load ppm
                layer = ic.loadImage(selectedFile, selectedFile.getName());
                System.out.println("Loading layer " + selectedFile.getName());
                currentProject.addLayer(layer);
                System.out.println("Loading ppm");
                img = ppmImageToBufferedImage(layer);
              } else {
                try {
                  img = ImageIO.read(selectedFile);
                } catch (IOException err) {
                  JOptionPane.showMessageDialog(currentWindow, "File wasn't found.");
                }
              }
              if (img != null) {
                imagePanel = new ImagePanel(img, ic);
                imageWindow.add(imagePanel);
                imageWindow.setVisible(true);
                imageWindow.setSize(600,600);
                imageWindow.setTitle(selectedFile.getName());
              }
            } else {
              JOptionPane.showMessageDialog(currentWindow,
                      "No current project so cannot assign to layer.");
            }
          }
        }
      }
    });

    if (currentProject != null) {
      DefaultListModel<String> listModel = new DefaultListModel<>();
      for (int l = 0; l < currentProject.getNumberLayers(); l++) {
        listModel.addElement(currentProject.getLayer(l).getName());
      }

      JList layerList = new JList<>(listModel);
      layerList.setSelectedIndex(1);
      layerList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      layerList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
      layerList.setVisibleRowCount(-1); // max number of possible rows

      JScrollPane listScroller = new JScrollPane(layerList);
      bottomPanel.add(listScroller);
    }

    this.add(topPanel);
    this.add(bottomPanel);
    this.setPreferredSize(new Dimension(600, 800));
    this.pack();
    this.setVisible(true);
  }

  /**
   * Method getExtension, gets the Java file extension.
   *
   * @param filename type
   */
  private String getExtension(String filename) {
    String ext = "";
    int i = filename.lastIndexOf(".");
    if (i > 0) {
      ext = filename.substring(i + 1);
    }
    return ext;
  }

  /**
   * Method ppmImageToBufferedImage, it will converse a ppm image to a
   * buffer image in order to draw it on the screen.
   *
   * @param layer equals layers
   * @return find Image
   */
  public static BufferedImage ppmImageToBufferedImage(Layer layer) {
    java.awt.image.BufferedImage finalImage =
            new java.awt.image.BufferedImage(layer.getWidth(),
                    layer.getHeight(), BufferedImage.TYPE_INT_ARGB
            );

    for (int y = 0; y < layer.getHeight(); y++) {
      for (int x = 0; x < layer.getWidth(); x++) {
        Pixel p = layer.getPixelAt(y, x);
        char a = (char) p.getAlpha();
        char r = (char) p.getRed();
        char g = (char) p.getGreen();
        char b = (char) p.getBlue();

        int argb = a << 24;
        argb |= r << 16;
        argb |= g << 8;
        argb |= b;
        finalImage.setRGB(x, y, argb);
      }
    }
    return finalImage;
  }

  /**
   * Renders a given message to the GUI Application.
   *
   * @param message the message to be printed
   * @throws IOException if the transmission of the message to the data output fails
   */
  @Override
  public void renderMessage(String message) throws IOException {
    System.out.println("Hi");

  }

}
