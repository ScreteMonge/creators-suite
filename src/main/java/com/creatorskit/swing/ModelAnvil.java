package com.creatorskit.swing;

import com.creatorskit.CreatorsPlugin;
import com.creatorskit.models.CustomModel;
import com.creatorskit.models.DetailedModel;
import com.creatorskit.models.LightingStyle;
import net.runelite.api.Client;
import net.runelite.api.JagexColor;
import net.runelite.api.Model;
import net.runelite.api.ModelData;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ModelAnvil extends JFrame
{
    private ClientThread clientThread;
    private final Client client;
    private final CreatorsPlugin plugin;
    private final BufferedImage ICON = ImageUtil.loadImageResource(getClass(), "/panelicon.png");
    private final BufferedImage DUPLICATE = ImageUtil.loadImageResource(getClass(), "/Duplicate.png");
    private final BufferedImage CLOSE = ImageUtil.loadImageResource(getClass(), "/Close.png");
    private final BufferedImage ARROW_LEFT = ImageUtil.loadImageResource(getClass(), "/Arrow_Left.png");
    private final BufferedImage ARROW_RIGHT = ImageUtil.loadImageResource(getClass(), "/Arrow_Right.png");
    private final BufferedImage ARROW_DOWN = ImageUtil.loadImageResource(getClass(), "/Arrow_Down.png");
    private final BufferedImage ARROW_UP = ImageUtil.loadImageResource(getClass(), "/Arrow_Up.png");
    private final BufferedImage TRANSLATE = ImageUtil.loadImageResource(getClass(), "/Translate.png");
    private final BufferedImage ROTATE = ImageUtil.loadImageResource(getClass(), "/Rotate.png");
    private final BufferedImage TRANSLATE_SUBTILE = ImageUtil.loadImageResource(getClass(), "/Translate subtile.png");
    private final BufferedImage SCALE = ImageUtil.loadImageResource(getClass(), "/Scale.png");
    private final Dimension SPINNER_DIMENSION = new Dimension(65, 25);
    private final ArrayList<JPanel> complexPanels = new ArrayList<>();
    public static final File MODELS_DIR = new File(RuneLite.RUNELITE_DIR, "creatorskit");
    JPanel complexMode = new JPanel();
    JScrollPane scrollPane = new JScrollPane();
    GridBagConstraints c = new GridBagConstraints();
    JCheckBox brightLightCheckBox = new JCheckBox("Actor Lighting");
    JCheckBox noLightCheckBox = new JCheckBox("No Lighting");
    //Pattern arrayPattern = Pattern.compile("\\s|[^(?:\\d,)|\\-]|\\d+-\\d+|\\D,");

    @Inject
    public ModelAnvil(Client client, ClientThread clientThread, CreatorsPlugin plugin)
    {
        this.client = client;
        this.clientThread = clientThread;
        this.plugin = plugin;

        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setLayout(new GridBagLayout());
        setTitle("RuneLite Model Anvil");
        setIconImage(ICON);

        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(4, 4, 4, 4);

        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        JLabel modelForgeLabel = new JLabel("Model Anvil");
        modelForgeLabel.setFont(FontManager.getRunescapeBoldFont());
        modelForgeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        modelForgeLabel.setVerticalAlignment(SwingConstants.CENTER);
        add(modelForgeLabel, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.ipady = 10;
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 0, 10, 0));
        add(buttonsPanel, c);

        JTextField nameField = new JTextField();
        nameField.setText("Name");
        nameField.setHorizontalAlignment(JTextField.CENTER);
        buttonsPanel.add(nameField);

        JPanel lightingPanel = new JPanel();
        lightingPanel.setLayout(new GridLayout(0, 1));
        buttonsPanel.add(lightingPanel);

        brightLightCheckBox.setToolTipText("Light the model with a formula better suited for Players & NPCs");
        brightLightCheckBox.setFocusable(false);
        lightingPanel.add(brightLightCheckBox);

        noLightCheckBox.setToolTipText("Apply no lighting to the model");
        noLightCheckBox.setFocusable(false);
        lightingPanel.add(noLightCheckBox);

        brightLightCheckBox.addChangeListener(e ->
        {
            if (brightLightCheckBox.isSelected())
                noLightCheckBox.setSelected(false);
        });

        noLightCheckBox.addChangeListener(e ->
        {
            if (noLightCheckBox.isSelected())
                brightLightCheckBox.setSelected(false);
        });

        JButton forgeButton = new JButton("Forge");
        forgeButton.setFocusable(false);
        buttonsPanel.add(forgeButton);

        JButton forgeSetButton = new JButton("Forge & Set");
        forgeSetButton.setFocusable(false);
        buttonsPanel.add(forgeSetButton);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.ipady = 0;
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new LineBorder(ColorScheme.LIGHT_GRAY_COLOR, 1));
        add(tabbedPane, c);

        scrollPane.setViewportView(complexMode);
        complexMode.setLayout(new GridLayout(0, 4, 8, 8));
        complexMode.setBackground(Color.BLACK);
        tabbedPane.addTab("Complex Mode", scrollPane);

        JPanel headerPanel = new JPanel();
        scrollPane.setColumnHeaderView(headerPanel);

        JButton addButton = new JButton("Add");
        addButton.setFocusable(false);
        addButton.addActionListener(e -> { createComplexPanel(); });
        headerPanel.add(addButton);

        JButton clearButton = new JButton("Clear");
        clearButton.setFocusable(false);
        clearButton.addActionListener(e ->
        {
            for (JPanel complexModePanel : complexPanels)
            {
                complexMode.remove(complexModePanel);
                repaint();
                revalidate();
            }

            complexPanels.clear();
        });
        headerPanel.add(clearButton);

        JPopupMenu loadPopupMenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Load");
        menuItem.addActionListener(e -> LinkBrowser.open(MODELS_DIR.toString()));
        loadPopupMenu.add(menuItem);

        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(e -> openLoadDialog());
        loadButton.setFocusable(false);
        loadButton.setComponentPopupMenu(loadPopupMenu);
        headerPanel.add(loadButton);

        JButton saveButton = new JButton("Save");
        saveButton.setFocusable(false);
        saveButton.addActionListener(e -> openSaveDialog());
        headerPanel.add(saveButton);

        JCheckBox priorityCheckBox = new JCheckBox("Priority");
        priorityCheckBox.setToolTipText("Use an oversimplified method of resolving render order issues (useful when merging models but not for NPCs/Players)");
        priorityCheckBox.setFocusable(false);
        headerPanel.add(priorityCheckBox);

        //SIMPLE MODE
        JPanel simpleMode = new JPanel();
        simpleMode.setLayout(new GridBagLayout());
        tabbedPane.addTab("Simple Mode", simpleMode);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        JLabel modelLoadLabel = new JLabel("Models to load:");
        modelLoadLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        simpleMode.add(modelLoadLabel, c);

        c.gridx = 0;
        c.gridy = 1;
        JLabel newColourLabel = new JLabel("New colours:");
        newColourLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        simpleMode.add(newColourLabel, c);

        c.gridx = 0;
        c.gridy = 2;
        JLabel oldColourLabel = new JLabel("Old colours:");
        oldColourLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        simpleMode.add(oldColourLabel, c);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        JTextField modelLoadField = new JTextField();
        simpleMode.add(modelLoadField, c);

        c.gridx = 1;
        c.gridy = 1;
        JTextField newColourField = new JTextField();
        simpleMode.add(newColourField, c);

        c.gridx = 1;
        c.gridy = 2;
        JTextField oldColourField = new JTextField();
        simpleMode.add(oldColourField, c);


        forgeButton.addActionListener(e ->
        {
            boolean simpleModeActive = tabbedPane.getSelectedComponent() == simpleMode;
            forgeModel(client, simpleModeActive, modelLoadField, oldColourField, newColourField, nameField, priorityCheckBox.isSelected(), brightLightCheckBox.isSelected(), false);
        });

        forgeSetButton.addActionListener(e ->
        {
            boolean simpleModeActive = tabbedPane.getSelectedComponent() == simpleMode;
            forgeModel(client, simpleModeActive, modelLoadField, oldColourField, newColourField, nameField, priorityCheckBox.isSelected(), brightLightCheckBox.isSelected(), true);
        });

        validate();
        pack();
    }

    public void createComplexPanel()
    {
        createComplexPanel("Name", -1, 0, 0, 0, 0, 0, 0, 128, 128, 128, 0, "", "");
    }

    public void createComplexPanel(String name, int modelId, int xTile, int yTile, int zTile, int xTranslate, int yTranslate, int zTranslate, int scaleX, int scaleY, int scaleZ, int rotate, String newColours, String oldColours)
    {
        JPanel complexModePanel = new JPanel();
        complexModePanel.setLayout(new GridBagLayout());
        complexModePanel.setBorder(new LineBorder(getBorderColour(modelId), 1));

        c.insets = new Insets(2, 4, 2, 4);
        c.weightx = 1;
        c.weighty = 0;

        c.gridwidth = 4;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        JTextField nameField = new JTextField(name);
        nameField.setName("nameField");
        nameField.setToolTipText("Name for organizational purposes only");
        complexModePanel.add(nameField, c);

        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 2;
        JPanel arrowPanel = new JPanel();
        arrowPanel.setLayout(new BorderLayout());
        complexModePanel.add(arrowPanel, c);

        JButton arrowUpButton = new JButton(new ImageIcon(ARROW_UP));
        arrowUpButton.setFocusable(false);
        arrowUpButton.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
        arrowUpButton.setToolTipText("Move panel up");
        arrowUpButton.addActionListener(e -> {setPanelIndex(complexModePanel, -4);});
        arrowPanel.add(arrowUpButton, BorderLayout.PAGE_START);

        JButton arrowLeftButton = new JButton(new ImageIcon(ARROW_LEFT));
        arrowLeftButton.setFocusable(false);
        arrowLeftButton.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
        arrowLeftButton.setToolTipText("Move panel left");
        arrowLeftButton.addActionListener(e -> {setPanelIndex(complexModePanel, -1);});
        arrowPanel.add(arrowLeftButton, BorderLayout.LINE_START);

        JButton arrowRightButton = new JButton(new ImageIcon(ARROW_RIGHT));
        arrowRightButton.setFocusable(false);
        arrowRightButton.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
        arrowRightButton.setToolTipText("Move panel right");
        arrowRightButton.addActionListener(e -> {setPanelIndex(complexModePanel, 1);});
        arrowPanel.add(arrowRightButton, BorderLayout.LINE_END);

        JButton arrowDownButton = new JButton(new ImageIcon(ARROW_DOWN));
        arrowDownButton.setFocusable(false);
        arrowDownButton.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
        arrowDownButton.setToolTipText("Move panel up");
        arrowDownButton.addActionListener(e -> {setPanelIndex(complexModePanel, 4);});
        arrowPanel.add(arrowDownButton, BorderLayout.PAGE_END);

        c.weightx = 0;
        c.gridx = 1;
        c.gridy = 2;
        JPanel xyzPanel = new JPanel();
        xyzPanel.setLayout(new GridLayout(3, 0));
        complexModePanel.add(xyzPanel, c);

        JLabel xLabel = new JLabel("x:");
        xLabel.setToolTipText("East/West");
        xLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        xyzPanel.add(xLabel);

        JLabel yLabel = new JLabel("y:");
        yLabel.setToolTipText("North/South");
        yLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        xyzPanel.add(yLabel);

        JLabel zLabel = new JLabel("z:");
        zLabel.setToolTipText("Up/Down");
        zLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        xyzPanel.add(zLabel);

        c.weightx = 1;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 3;
        JLabel colourLabel = new JLabel("Colours:");
        colourLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        colourLabel.setToolTipText("Choose new Colours to replace default Colours");
        complexModePanel.add(colourLabel, c);

        c.gridwidth = 2;
        c.gridx = 4;
        c.gridy = 0;
        SpinnerNumberModel modelIdModel = new SpinnerNumberModel(modelId, -1, 99999, 1);
        JSpinner modelIdSpinner = new JSpinner(modelIdModel);
        modelIdSpinner.setBackground((modelId == -1) ? ColorScheme.PROGRESS_ERROR_COLOR : ColorScheme.MEDIUM_GRAY_COLOR);
        modelIdSpinner.setToolTipText("Set the id of the model you want to draw from the cache");
        modelIdSpinner.setName("modelIdSpinner");
        modelIdSpinner.addChangeListener(e ->
        {
            complexModePanel.setBorder(new LineBorder(getBorderColour((int) modelIdSpinner.getValue()), 1));
            modelIdSpinner.setBackground(((int) modelIdSpinner.getValue() == -1) ? ColorScheme.PROGRESS_ERROR_COLOR : ColorScheme.MEDIUM_GRAY_COLOR);
        });
        complexModePanel.add(modelIdSpinner, c);

        c.gridx = 8;
        c.gridy = 0;
        c.gridwidth = 1;
        JButton duplicateButton = new JButton(new ImageIcon(DUPLICATE));
        duplicateButton.setFocusable(false);
        duplicateButton.setToolTipText("Duplicate panel");
        complexModePanel.add(duplicateButton, c);

        c.gridx = 9;
        c.gridy = 0;
        c.gridwidth = 1;
        JButton removeButton = new JButton(new ImageIcon(CLOSE));
        removeButton.setFocusable(false);
        removeButton.setToolTipText("Remove panel");
        complexModePanel.add(removeButton, c);

        c.gridx = 2;
        c.gridy = 1;
        c.gridwidth = 2;
        JLabel tileLabel = new JLabel(new ImageIcon(TRANSLATE));
        tileLabel.setToolTipText("Translate by full tile");
        tileLabel.setBackground(Color.BLACK);
        complexModePanel.add(tileLabel, c);

        c.gridx = 4;
        c.gridy = 1;
        c.gridwidth = 2;
        JLabel translateLabel = new JLabel(new ImageIcon(TRANSLATE_SUBTILE));
        translateLabel.setToolTipText("Translate by sub-tile (1/128 of a tile)");
        translateLabel.setBackground(Color.BLACK);
        complexModePanel.add(translateLabel, c);

        c.gridx = 6;
        c.gridy = 1;
        c.gridwidth = 2;
        JLabel scaleLabel = new JLabel(new ImageIcon(SCALE));
        scaleLabel.setToolTipText("Scale. 128 is default scale");
        scaleLabel.setBackground(Color.BLACK);
        complexModePanel.add(scaleLabel, c);

        c.gridx = 8;
        c.gridy = 1;
        c.gridwidth = 2;
        JLabel rotateLabel = new JLabel(new ImageIcon(ROTATE));
        rotateLabel.setToolTipText("Rotate");
        rotateLabel.setBackground(Color.BLACK);
        complexModePanel.add(rotateLabel, c);

        c.gridx = 2;
        c.gridy = 2;
        c.gridwidth = 2;
        JPanel tilePanel = new JPanel();
        tilePanel.setLayout(new GridLayout(3, 0));
        complexModePanel.add(tilePanel, c);

        JSpinner xTileSpinner = new JSpinner();
        xTileSpinner.setValue(xTile);
        xTileSpinner.setToolTipText("E/W");
        xTileSpinner.setPreferredSize(SPINNER_DIMENSION);
        xTileSpinner.setName("xTileSpinner");
        tilePanel.add(xTileSpinner);

        JSpinner yTileSpinner = new JSpinner();
        yTileSpinner.setValue(yTile);
        yTileSpinner.setToolTipText("N/S");
        yTileSpinner.setName("yTileSpinner");
        tilePanel.add(yTileSpinner);

        JSpinner zTileSpinner = new JSpinner();
        zTileSpinner.setValue(zTile);
        zTileSpinner.setToolTipText("U/D");
        zTileSpinner.setName("zTileSpinner");
        tilePanel.add(zTileSpinner);

        c.gridx = 4;
        c.gridy = 2;
        c.gridwidth = 2;
        JPanel translatePanel = new JPanel();
        translatePanel.setLayout(new GridLayout(3, 0));
        complexModePanel.add(translatePanel, c);

        JSpinner xSpinner = new JSpinner();
        xSpinner.setValue(xTranslate);
        xSpinner.setToolTipText("E/W");
        xSpinner.setPreferredSize(SPINNER_DIMENSION);
        xSpinner.setName("xSpinner");
        translatePanel.add(xSpinner);

        JSpinner ySpinner = new JSpinner();
        ySpinner.setValue(yTranslate);
        ySpinner.setToolTipText("N/S");
        ySpinner.setName("ySpinner");
        translatePanel.add(ySpinner);

        JSpinner zSpinner = new JSpinner();
        zSpinner.setValue(zTranslate);
        zSpinner.setToolTipText("U/D");
        zSpinner.setName("zSpinner");
        translatePanel.add(zSpinner);

        c.gridx = 6;
        c.gridy = 2;
        c.gridwidth = 2;
        JPanel scalePanel = new JPanel();
        scalePanel.setLayout(new GridLayout(3, 0));
        complexModePanel.add(scalePanel, c);

        JSpinner xScaleSpinner = new JSpinner();
        xScaleSpinner.setValue(scaleX);
        xScaleSpinner.setPreferredSize(SPINNER_DIMENSION);
        xScaleSpinner.setToolTipText("E/W");
        xScaleSpinner.setName("xScaleSpinner");
        scalePanel.add(xScaleSpinner);

        JSpinner yScaleSpinner = new JSpinner();
        yScaleSpinner.setValue(scaleY);
        yScaleSpinner.setToolTipText("N/S");
        yScaleSpinner.setName("yScaleSpinner");
        scalePanel.add(yScaleSpinner);

        JSpinner zScaleSpinner = new JSpinner();
        zScaleSpinner.setValue(scaleZ);
        zScaleSpinner.setToolTipText("U/D");
        zScaleSpinner.setName("zScaleSpinner");
        scalePanel.add(zScaleSpinner);

        c.gridx = 8;
        c.gridy = 2;
        c.gridwidth = 2;
        JPanel rotatePanel = new JPanel();
        rotatePanel.setBackground(Color.BLACK);
        rotatePanel.setLayout(new GridLayout(3, 0));
        complexModePanel.add(rotatePanel, c);

        JCheckBox check90 = new JCheckBox();
        check90.setText("90°");
        check90.setName("check90");
        check90.setHorizontalAlignment(SwingConstants.LEFT);
        check90.setFocusable(false);
        rotatePanel.add(check90);

        JCheckBox check180 = new JCheckBox();
        check180.setText("180°");
        check180.setName("check180");
        check180.setHorizontalAlignment(SwingConstants.LEFT);
        check180.setFocusable(false);
        rotatePanel.add(check180);

        JCheckBox check270 = new JCheckBox();
        check270.setText("270°");
        check270.setName("check270");
        check270.setHorizontalAlignment(SwingConstants.LEFT);
        check270.setFocusable(false);
        rotatePanel.add(check270);

        check90.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                check180.setSelected(false);
                check270.setSelected(false);
            }
        });

        check180.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                check90.setSelected(false);
                check270.setSelected(false);
            }
        });

        check270.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                check180.setSelected(false);
                check90.setSelected(false);
            }
        });

        switch (rotate)
        {
            case 0:
                break;
            case 1:
                check270.setSelected(true);
                break;
            case 2:
                check180.setSelected(true);
                break;
            case 3:
                check90.setSelected(true);
        }

        HashMap<Short, Short> colourMap = new HashMap<>();
        if (!newColours.equals("") && !oldColours.equals(""))
        {
            try
            {
                String[] newCols = newColours.split(",");
                String[] oldCols = oldColours.split(",");

                for (int i = 0; i < newCols.length; i++)
                {
                    Short newCol = Short.parseShort(newCols[i]);
                    Short oldCol = Short.parseShort(oldCols[i]);
                    colourMap.put(oldCol, newCol);
                }
            }
            catch (Exception e)
            {
                System.out.println("Broken");
            }
        }

        JTextField colourNewField = new JTextField();
        colourNewField.setName("colourNewField");
        colourNewField.setText(newColours);
        colourNewField.setVisible(false);
        complexModePanel.add(colourNewField);

        JTextField colourOldField = new JTextField();
        colourOldField.setName("colourOldField");
        colourOldField.setText(oldColours);
        colourOldField.setVisible(false);
        complexModePanel.add(colourOldField);

        JButton clearColoursButton = new JButton("Clear (" + colourMap.size() + ")");

        JFrame swapperFrame = new JFrame("Colour Swapper: " + nameField.getText());
        swapperFrame.setVisible(false);
        swapperFrame.setEnabled(false);
        swapperFrame.setIconImage(ICON);
        swapperFrame.setLayout(new FlowLayout());

        JPanel gridMenu = new JPanel();
        gridMenu.setLayout(new GridLayout(0, 2, 2, 2));
        swapperFrame.add(gridMenu);

        JColorChooser colorChooser = new JColorChooser();
        swapperFrame.add(colorChooser);

        c.gridx = 2;
        c.gridy = 3;
        c.gridwidth = 6;
        JButton colourSwapper = new JButton("Colour Swapper");
        colourSwapper.setFocusable(false);
        colourSwapper.setToolTipText("Opens an interface to swap colours on this model");
        complexModePanel.add(colourSwapper, c);
        colourSwapper.addActionListener(e ->
        {
            swapperFrame.setVisible(true);
            swapperFrame.setEnabled(true);
            swapperFrame.setTitle("Colour Swapper: " + nameField.getText());
            gridMenu.removeAll();

            clientThread.invokeLater(() ->
            {
                ModelData md = client.loadModelData((int) modelIdSpinner.getValue());
                if (md == null)
                {
                    return;
                }

                ArrayList<Short> list = new ArrayList<>();
                for (short s : md.getFaceColors())
                {
                    if (!list.contains(s))
                    {
                        list.add(s);
                    }
                }

                SwingUtilities.invokeLater(() ->
                {
                    JLabel oldColourTitle = new JLabel("Old Colours");
                    oldColourTitle.setFont(FontManager.getRunescapeBoldFont());
                    oldColourTitle.setToolTipText("Default colours of the model");
                    oldColourTitle.setHorizontalAlignment(SwingConstants.CENTER);
                    oldColourTitle.setVerticalAlignment(SwingConstants.BOTTOM);
                    gridMenu.add(oldColourTitle);

                    JLabel newColourTitle = new JLabel("New Colours");
                    newColourTitle.setToolTipText("Pick colours to replace Old Colours");
                    newColourTitle.setFont(FontManager.getRunescapeBoldFont());
                    newColourTitle.setHorizontalAlignment(SwingConstants.CENTER);
                    newColourTitle.setVerticalAlignment(SwingConstants.BOTTOM);
                    gridMenu.add(newColourTitle);

                    for (int i = 0; i < list.size(); i++)
                    {
                        short s = list.get(i);
                        JLabel oldColour = new JLabel(s + "", JLabel.CENTER);
                        oldColour.setFont(FontManager.getRunescapeBoldFont());
                        oldColour.setOpaque(true);
                        oldColour.setBackground(Color.BLACK);
                        Color color = colorFromShort(s);
                        oldColour.setBorder(new LineBorder(color, 12));
                        gridMenu.add(oldColour);

                        JButton newColour = new JButton();
                        newColour.setName("newColour");
                        newColour.setFocusable(false);
                        newColour.setFont(FontManager.getRunescapeBoldFont());
                        newColour.setText("Set");
                        gridMenu.add(newColour);
                        if (colourMap.containsKey(s))
                        {
                            newColour.setBorder(new LineBorder(colorFromShort(colourMap.get(s)), 12));
                            newColour.setText("Unset");
                        }

                        newColour.addActionListener(f ->
                        {
                            if (newColour.getText().equals("Unset"))
                            {
                                colourMap.remove(s);
                                newColour.setBorder(new EmptyBorder(4, 4, 4, 4));
                                newColour.setText("Set");
                            }
                            else
                            {
                                Color colourToAdd = colorChooser.getColor();
                                colourMap.put(s, shortFromColour(colourToAdd));
                                newColour.setBorder(new LineBorder(colourToAdd, 12));
                                newColour.setText("Unset");
                            }

                            String[] mapToCSV = hashmapToCSV(colourMap);
                            colourNewField.setText(mapToCSV[1]);
                            colourOldField.setText(mapToCSV[0]);
                            clearColoursButton.setText("Clear (" + colourMap.size() + ")");

                            swapperFrame.revalidate();
                            swapperFrame.repaint();
                            swapperFrame.pack();
                            revalidate();
                            repaint();
                        });
                    }

                    swapperFrame.revalidate();
                    swapperFrame.repaint();
                    swapperFrame.pack();
                    revalidate();
                    repaint();
                });
            });

            swapperFrame.revalidate();
            swapperFrame.repaint();
            swapperFrame.pack();
            revalidate();
            repaint();
        });

        c.gridx = 8;
        c.gridy = 3;
        c.gridwidth = 2;
        clearColoursButton.setFocusable(false);
        clearColoursButton.setToolTipText("Clears all swapped colours");
        complexModePanel.add(clearColoursButton, c);
        clearColoursButton.addActionListener(e ->
        {
            for (Component component : gridMenu.getComponents())
            {
                if (component instanceof JButton)
                {
                    JButton button = (JButton) component;
                    if (button.getText().equals("Unset"))
                    {
                        button.setBorder(new EmptyBorder(4, 4, 4, 4));
                        button.setText("Set");
                    }
                }
            }

            colourMap.clear();
            colourNewField.setText("");
            colourOldField.setText("");
            clearColoursButton.setText("Clear (0)");

            swapperFrame.revalidate();
            swapperFrame.repaint();
            swapperFrame.pack();
            revalidate();
            repaint();
        });

        duplicateButton.addActionListener(e ->
        {
            int rotation = 0;
            if (check270.isSelected())
            {
                rotation = 1;
            }

            if (check180.isSelected())
            {
                rotation = 2;
            }

            if (check90.isSelected())
            {
                rotation = 3;
            }

            String[] colourSwaps = hashmapToCSV(colourMap);

            createComplexPanel
                    (nameField.getText(),
                            (int) modelIdSpinner.getValue(),
                            (int) xTileSpinner.getValue(),
                            (int) yTileSpinner.getValue(),
                            (int) zTileSpinner.getValue(),
                            (int) xSpinner.getValue(),
                            (int) ySpinner.getValue(),
                            (int) zSpinner.getValue(),
                            (int) xScaleSpinner.getValue(),
                            (int) yScaleSpinner.getValue(),
                            (int) zScaleSpinner.getValue(),
                            rotation,
                            colourSwaps[1],
                            colourSwaps[0]
                    );
        });

        removeButton.addActionListener(e ->
        {
            swapperFrame.setVisible(false);
            swapperFrame.setEnabled(false);
            complexMode.remove(complexModePanel);
            complexPanels.remove(complexModePanel);
            repaint();
            revalidate();
        });

        nameField.addActionListener(e ->
        {
            nameField.setText(nameField.getText().replaceAll("=", ""));
            swapperFrame.setTitle("Colour Swapper: " + nameField.getText());
        });

        complexMode.add(complexModePanel);
        complexModePanel.setEnabled(true);
        complexModePanel.setVisible(true);
        complexPanels.add(complexModePanel);

        revalidate();
        repaint();
    }

    private void setPanelIndex(JPanel panel, int change)
    {
        int newPosition = complexMode.getComponentZOrder(panel) + change;
        if (newPosition < 0)
            newPosition = 0;

        if (newPosition >= complexPanels.size())
            newPosition = complexPanels.size() - 1;

        complexMode.setComponentZOrder(panel, newPosition);
        repaint();
        revalidate();
    }

    private void forgeModel(Client client, boolean simpleModeActive, JTextField modelLoadField, JTextField oldColourField, JTextField newColourField, JTextField nameField, boolean setPriority, boolean actorLighting, boolean forgeAndSet)
    {
        if (client == null)
        {
            return;
        }

        if (simpleModeActive)
        {
            if (modelLoadField.getText().isEmpty())
            {
                clientThread.invokeLater(() ->
                {
                    plugin.sendChatMessage("You must enter at least one ModelId to Forge a new model.");
                });
                return;
            }

            String[] modelIdString = modelLoadField.getText().split(",");
            int[] modelIds = new int[modelIdString.length];

            String[] oldColourStringArray = oldColourField.getText().split(",");
            short[] oldColours = new short[oldColourStringArray.length];
            String[] newColourStringArray = newColourField.getText().split(",");
            short[] newColours = new short[newColourStringArray.length];

            if (!oldColourField.getText().isEmpty() && !newColourField.getText().isEmpty())
            {
                if (oldColourStringArray.length != newColourStringArray.length)
                {
                    clientThread.invokeLater(() -> plugin.sendChatMessage("There must be the same number of New and Old colours."));
                    return;
                }

                try
                {
                    for (int i = 0; i < oldColourStringArray.length; i++)
                    {
                        oldColours[i] = Short.parseShort(oldColourStringArray[i]);
                        newColours[i] = Short.parseShort(newColourStringArray[i]);
                    }
                }
                catch (Exception exception)
                {
                    clientThread.invokeLater(() -> plugin.sendChatMessage("Please reformat your Colours by separating each Colour with a comma and removing excess characters."));
                    return;
                }
            }

            try
            {
                for (int i = 0; i < modelIdString.length; i++)
                {
                    modelIds[i] = Integer.parseInt(modelIdString[i]);
                }
            }
            catch (Exception exception)
            {
                clientThread.invokeLater(() -> plugin.sendChatMessage("Please reformat your ModelIds by separating each ModelId with a comma and removing excess characters."));
                return;
            }

            clientThread.invokeLater(() ->
            {
                Model model = plugin.constructSimpleModel(modelIds, oldColours, newColours, getLightingStyle());
                CustomModel customModel = new CustomModel(model, nameField.getText());
                plugin.addCustomModel(customModel, forgeAndSet);
            });
            return;
        }

        if (complexPanels.isEmpty())
            return;

        clientThread.invokeLater(() ->
        {
            Model model = forgeComplexModel(setPriority);
            if (model == null)
            {
                return;
            }

            CustomModel customModel = new CustomModel(model, nameField.getText());
            plugin.addCustomModel(customModel, forgeAndSet);
        });
    }

    private Model forgeComplexModel(boolean setPriority)
    {
        return plugin.createComplexModel(panelsToDetailedModels(), setPriority, getLightingStyle());
    }

    private DetailedModel[] panelsToDetailedModels()
    {
        DetailedModel[] detailedModels = new DetailedModel[complexPanels.size()];
        for (int i = 0; i < complexPanels.size(); i++) {
            JPanel complexModePanel = complexPanels.get(i);
            String name = "";
            int modelId = 0;
            int xTile = 0;
            int yTile = 0;
            int zTile = 0;
            int xTranslate = 0;
            int yTranslate = 0;
            int zTranslate = 0;
            int xScale = 128;
            int yScale = 128;
            int zScale = 128;
            int rotate = 0;
            String recolourNew = "";
            String recolourOld = "";

            for (Component component : complexModePanel.getComponents())
            {
                String primaryComponentName = component.getName();
                if (component instanceof JSpinner)
                {
                    if (primaryComponentName.equals("modelIdSpinner"))
                    {
                        modelId = (int) ((JSpinner) component).getValue();
                        continue;
                    }
                }

                if (component instanceof JTextField) {
                    if (primaryComponentName.equals("nameField")) {
                        name = ((JTextField) component).getText();
                        continue;
                    }

                    if (primaryComponentName.equals("colourNewField")) {
                        recolourNew = ((JTextField) component).getText();
                        continue;
                    }

                    if (primaryComponentName.equals("colourOldField")) {
                        recolourOld = ((JTextField) component).getText();
                    }
                }

                if (component instanceof JPanel)
                {
                    JPanel componentPanel = (JPanel) component;
                    for (Component comp : componentPanel.getComponents())
                    {
                        String compName = comp.getName();
                        if (comp instanceof JSpinner)
                        {
                            if (compName.equals("xSpinner")) {
                                xTranslate = (int) ((JSpinner) comp).getValue();
                                continue;
                            }

                            if (compName.equals("ySpinner")) {
                                yTranslate = (int) ((JSpinner) comp).getValue();
                                continue;
                            }

                            if (compName.equals("zSpinner")) {
                                zTranslate = (int) ((JSpinner) comp).getValue();
                                continue;
                            }

                            if (compName.equals("xTileSpinner")) {
                                xTile = (int) ((JSpinner) comp).getValue();
                                continue;
                            }

                            if (compName.equals("yTileSpinner")) {
                                yTile = (int) ((JSpinner) comp).getValue();
                                continue;
                            }

                            if (compName.equals("zTileSpinner")) {
                                zTile = (int) ((JSpinner) comp).getValue();
                                continue;
                            }

                            if (compName.equals("xScaleSpinner")) {
                                xScale = (int) ((JSpinner) comp).getValue();
                                continue;
                            }

                            if (compName.equals("yScaleSpinner")) {
                                yScale = (int) ((JSpinner) comp).getValue();
                                continue;
                            }

                            if (compName.equals("zScaleSpinner")) {
                                zScale = (int) ((JSpinner) comp).getValue();
                                continue;
                            }

                            continue;
                        }

                        if (comp instanceof JCheckBox) {
                            JCheckBox checkBox = (JCheckBox) comp;

                            if (compName.equals("check90") && checkBox.isSelected()) {
                                rotate = 3;
                                continue;
                            }

                            if (compName.equals("check180") && checkBox.isSelected()) {
                                rotate = 2;
                                continue;
                            }

                            if (compName.equals("check270") && checkBox.isSelected()) {
                                rotate = 1;
                            }
                        }
                    }
                }
            }

            DetailedModel detailedModel = new DetailedModel(name, modelId, xTile, yTile, zTile, xTranslate, yTranslate, zTranslate, xScale, yScale, zScale, rotate, recolourNew, recolourOld);
            detailedModels[i] = detailedModel;
        }

        return detailedModels;
    }

    private void openSaveDialog()
    {
        File outputDir = MODELS_DIR;
        outputDir.mkdirs();

        JFileChooser fileChooser = new JFileChooser(outputDir)
        {
            @Override
            public void approveSelection()
            {
                File f = getSelectedFile();
                if (!f.getName().endsWith(".txt"))
                {
                    f = new File(f.getPath() + ".txt");
                }
                if (f.exists() && getDialogType() == SAVE_DIALOG)
                {
                    int result = JOptionPane.showConfirmDialog(
                            this,
                            "File already exists, overwrite?",
                            "Warning",
                            JOptionPane.YES_NO_CANCEL_OPTION
                    );
                    switch (result)
                    {
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.NO_OPTION:
                        case JOptionPane.CLOSED_OPTION:
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            cancelSelection();
                            return;
                    }
                }
                super.approveSelection();
            }
        };
        fileChooser.setSelectedFile(new File("model.txt"));
        fileChooser.setDialogTitle("Save current model collection");

        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.getName().endsWith(".txt"))
            {
                selectedFile = new File(selectedFile.getPath() + ".txt");
            }
            saveToFile(selectedFile);
        }
    }

    public void saveToFile(File file)
    {
        try {
            FileWriter writer = new FileWriter(file, false);

            for (DetailedModel detailedModel : panelsToDetailedModels())
            {
                writer.write("\n\n" + "name=" + detailedModel.getName() + "\n" + "modelid=" + detailedModel.getModelId() + "\n" + "xtile=" + detailedModel.getXTile() + "\n"  + "ytile="+ detailedModel.getYTile() + "\n"  + "ztile=" + detailedModel.getZTile() + "\n" + "xt=" + detailedModel.getXTranslate() + "\n" + "yt=" + detailedModel.getYTranslate() + "\n" + "zt=" + detailedModel.getZTranslate() + "\n" + "xs=" + detailedModel.getXScale() + "\n" + "ys=" + detailedModel.getYScale() + "\n" + "zs=" + detailedModel.getZScale() + "\n" + "r=" + detailedModel.getRotate() + "\n" + "n=" + detailedModel.getRecolourNew() + "\n" + "o=" + detailedModel.getRecolourOld() + "\n" + "");
            }

            writer.close();
        }
        catch (IOException e)
        {
            System.out.println("Error occurred while writing to file.");
        }
    }

    private void openLoadDialog()
    {
        File outputDir = MODELS_DIR;
        outputDir.mkdirs();

        JFileChooser fileChooser = new JFileChooser(outputDir);
        fileChooser.setDialogTitle("Choose a model collection to load");

        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = fileChooser.getSelectedFile();
            plugin.loadCustomModel(selectedFile);
        }
    }

    private void stringToCSV(Pattern pattern, JTextField jTextField)
    {
        jTextField.setText(pattern.matcher(jTextField.getText()).replaceAll(""));
    }

    private String[] hashmapToCSV(HashMap<Short, Short> map)
    {
        if (map.isEmpty())
        {
            return new String[]{"", ""};
        }

        StringBuilder oldBuilder = new StringBuilder();
        StringBuilder newBuilder = new StringBuilder();
        for (Map.Entry<Short, Short> entry : map.entrySet())
        {
            short oldColour = entry.getKey();
            short newColour = entry.getValue();

            oldBuilder.append(oldColour).append(",");
            newBuilder.append(newColour).append(",");
        }

        //remove final comma
        oldBuilder.deleteCharAt(oldBuilder.length() - 1);
        newBuilder.deleteCharAt(newBuilder.length() - 1);
        return new String[]{oldBuilder.toString(), newBuilder.toString()};
    }

    private Color colorFromShort(short s)
    {
        float hue = (float) JagexColor.unpackHue(s) / JagexColor.HUE_MAX;
        float sat = (float) JagexColor.unpackSaturation(s) / JagexColor.SATURATION_MAX;
        float lum = (float) JagexColor.unpackLuminance(s) / JagexColor.LUMINANCE_MAX;
        int[] rgb = hslToRgb(hue, sat, lum);
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    private short shortFromColour(Color color)
    {
        float[] col = rgbToHsl(color.getRed(), color.getGreen(), color.getBlue());
        int hue = (int) (col[0] * JagexColor.HUE_MAX);
        int sat = (int) (col[1] * JagexColor.SATURATION_MAX);
        int lum = (int) (col[2] * JagexColor.LUMINANCE_MAX);
        return JagexColor.packHSL(hue, sat, lum);
    }

    private Color getBorderColour(int modelId)
    {
        if (modelId == -1)
        {
            return ColorScheme.MEDIUM_GRAY_COLOR;
        }

        float hue = ((float) modelId - 25) / 50;
        return Color.getHSBColor(hue, 1, (float) 0.7);
    }

    private LightingStyle getLightingStyle()
    {
        if (brightLightCheckBox.isSelected())
            return LightingStyle.ACTOR;
        if (noLightCheckBox.isSelected())
            return LightingStyle.NONE;
        return LightingStyle.DEFAULT;
    }

    public static int[] hslToRgb(float h, float s, float l){
        float r, g, b;

        if (s == 0f) {
            r = g = b = l; // achromatic
        } else {
            float q = l < 0.5f ? l * (1 + s) : l + s - l * s;
            float p = 2 * l - q;
            r = hueToRgb(p, q, h + 1f/3f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1f/3f);
        }
        return new int[]{to255(r), to255(g), to255(b)};
    }

    public static float[] rgbToHsl(int red, int green, int blue) {
        float r = red / 255f;
        float g = green / 255f;
        float b = blue / 255f;

        float max = (r > g && r > b) ? r : (g > b) ? g : b;
        float min = (r < g && r < b) ? r : (g < b) ? g : b;

        float h, s, l;
        l = (max + min) / 2.0f;

        if (max == min) {
            h = s = 0.0f;
        } else {
            float d = max - min;
            s = (l > 0.5f) ? d / (2.0f - max - min) : d / (max + min);

            if (r > g && r > b)
                h = (g - b) / d + (g < b ? 6.0f : 0.0f);

            else if (g > b)
                h = (b - r) / d + 2.0f;

            else
                h = (r - g) / d + 4.0f;

            h /= 6.0f;
        }
        return new float[]{h, s, l};
    }

    public static int to255(float v) { return (int)Math.min(255,256*v); }

    public static float hueToRgb(float p, float q, float t) {
        if (t < 0f)
            t += 1f;
        if (t > 1f)
            t -= 1f;
        if (t < 1f/6f)
            return p + (q - p) * 6f * t;
        if (t < 1f/2f)
            return q;
        if (t < 2f/3f)
            return p + (q - p) * (2f/3f - t) * 6f;
        return p;
    }
}
