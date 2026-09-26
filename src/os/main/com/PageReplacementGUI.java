package os.main.com;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PageReplacementGUI extends JFrame {

    // =========================================================
    // CORE RESULTS
    // =========================================================
    private PageReplacement.Result fifoResult;
    private PageReplacement.Result lruResult;
    private PageReplacement.Result optimalResult;

    // =========================================================
    // INPUT COMPONENTS
    // =========================================================
    private JTextField referenceField;
    private JSpinner frameSpinner;

    // =========================================================
    // COMPARATOR COMPONENTS
    // =========================================================
    private JTable resultTable;
    private DefaultTableModel resultTableModel;
    private JLabel statusLabel;
    private JLabel referencesValueLabel;
    private JLabel framesValueLabel;
    private JLabel algorithmsValueLabel;

    // =========================================================
    // SIMULATOR COMPONENTS
    // =========================================================
    private JComboBox<String> algorithmSelector;
    private JTable simulationTable;
    private DefaultTableModel simulationTableModel;
    private JPanel frameCardsPanel;

    private JLabel simulationStepLabel;
    private JLabel simulationPageLabel;
    private JLabel simulationResultBadge;
    private JLabel simulationMessageLabel;

    private JButton firstButton;
    private JButton previousButton;
    private JButton nextButton;
    private JButton lastButton;
    private JButton resetButton;

    private int currentSimulationStep = 0;

    // =========================================================
    // DESIGN SYSTEM & PALETTE
    // =========================================================
    private static final Color BG_PAGE         = new Color(248, 250, 252); // Slate 50
    private static final Color CARD_BG         = Color.WHITE;
    private static final Color CARD_BORDER     = new Color(226, 232, 240); // Slate 200
    private static final Color CARD_SUBTLE     = new Color(248, 250, 252); // Slate 50

    private static final Color TEXT_MAIN       = new Color(30, 41, 59);    // Slate 800
    private static final Color TEXT_MUTED      = new Color(100, 116, 139); // Slate 500
    private static final Color NAVY_BLUE       = new Color(30, 64, 175);   // Blue 800

    private static final Color PRIMARY         = new Color(37, 99, 235);   // Blue 600
    private static final Color PRIMARY_HOVER   = new Color(29, 78, 216);   // Blue 700
    private static final Color PRIMARY_LIGHT   = new Color(239, 246, 255); // Blue 50

    private static final Color SUCCESS_TEXT    = new Color(22, 101, 52);   // Green 800
    private static final Color SUCCESS_BG      = new Color(220, 252, 231); // Green 100
    private static final Color DANGER_TEXT     = new Color(153, 27, 27);   // Red 800
    private static final Color DANGER_BG       = new Color(254, 226, 226); // Red 100

    private static final Font FONT_TITLE       = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONT_SUBTITLE    = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_SEC_HEAD    = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_CARD_HEAD   = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_SUB_HEAD    = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_BODY        = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD_BODY   = new Font("Segoe UI", Font.BOLD, 13);

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    public PageReplacementGUI() {
        setTitle("Page Replacement Algorithm Comparator");
        setSize(1200, 880);
        setMinimumSize(new Dimension(1000, 720));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createGUI();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            PageReplacementGUI gui = new PageReplacementGUI();
            gui.setVisible(true);
        });
    }

    // =========================================================
    // ROOT LAYOUT (100% Full-Width Single Continuous Page)
    // =========================================================
    private void createGUI() {
        getContentPane().setBackground(BG_PAGE);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);

        // Page container tracking viewport width
        FullWidthPage page = new FullWidthPage();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(BG_PAGE);
        page.setBorder(new EmptyBorder(16, 24, 40, 24));

        // 1. THEORY
        page.add(createTheorySection());
        page.add(Box.createVerticalStrut(24));

        // 2. INPUT
        page.add(createInputSection());
        page.add(Box.createVerticalStrut(24));

        // 3. SIMULATOR
        page.add(createSimulatorSection());
        page.add(Box.createVerticalStrut(24));

        // 4. COMPARATOR
        page.add(createComparatorSection());

        JScrollPane scrollPane = new JScrollPane(page);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.getVerticalScrollBar().setUnitIncrement(24);
        scrollPane.getViewport().setBackground(BG_PAGE);

        add(scrollPane, BorderLayout.CENTER);
    }

    // =========================================================
    // HEADER BANNER
    // =========================================================
    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(CARD_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_BORDER),
                new EmptyBorder(22, 24, 18, 24)
        ));

        JLabel title = new JLabel("PAGE REPLACEMENT ALGORITHM COMPARATOR");
        title.setFont(FONT_TITLE);
        title.setForeground(NAVY_BLUE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Theory  •  Simulation  •  Comparison");
        subtitle.setFont(FONT_SUBTITLE);
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        return header;
    }

    // =========================================================
    // 1. THEORY SECTION
    // =========================================================
    private JPanel createTheorySection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel secTitle = new JLabel("1. THEORY");
        secTitle.setFont(FONT_SEC_HEAD);
        secTitle.setForeground(NAVY_BLUE);
        secTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.add(secTitle);
        section.add(Box.createVerticalStrut(14));

        // Page Replacement Card
        section.add(createCard(
                "PAGE REPLACEMENT",
                "Page replacement is a memory-management technique used in virtual memory systems. "
                        + "When a requested page is not currently present in physical memory and all available "
                        + "frames are occupied, the operating system must select an existing page to remove "
                        + "so that the required page can be loaded. The selected page is called the victim page."
        ));
        section.add(Box.createVerticalStrut(16));

        // Important Terms Subheading
        JLabel termsTitle = new JLabel("IMPORTANT TERMS");
        termsTitle.setFont(FONT_CARD_HEAD);
        termsTitle.setForeground(TEXT_MAIN);
        termsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.add(termsTitle);
        section.add(Box.createVerticalStrut(10));

        // 2x2 Grid of Terms
        JPanel termsGrid = new JPanel(new GridLayout(2, 2, 14, 14));
        termsGrid.setOpaque(false);
        termsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        termsGrid.add(createMiniCard("PAGE", "A fixed-size block of virtual memory."));
        termsGrid.add(createMiniCard("FRAME", "A fixed-size block of physical memory that stores a page."));
        termsGrid.add(createMiniCard("REFERENCE STRING", "The sequence of page requests generated by a process during execution."));
        termsGrid.add(createMiniCard("PAGE HIT", "A requested page is already present in one of the available frames, so no replacement is required."));

        section.add(termsGrid);
        section.add(Box.createVerticalStrut(14));

        // Page Fault Card
        section.add(createCard(
                "PAGE FAULT",
                "A page fault occurs when the referenced page is not currently held in any frame. "
                        + "The page must be loaded into a free frame or, when all frames are full, "
                        + "a victim page must be selected and replaced. Page faults are important because "
                        + "loading a missing page can require relatively slow secondary-storage access."
        ));
        section.add(Box.createVerticalStrut(14));

        // Performance Metric Card
        section.add(createCard(
                "PERFORMANCE METRIC — PAGE FAULTS",
                "Page-fault count is the primary performance metric used in this experiment. "
                        + "For a fixed reference string and frame count, FIFO, LRU and Optimal can be "
                        + "compared by counting how many page faults occur. Reducing unnecessary page faults "
                        + "reduces the number of expensive page-loading operations."
        ));
        section.add(Box.createVerticalStrut(18));

        // Three Algorithms Comparison Card
        section.add(createAlgorithmsThreeColumnCard());
        section.add(Box.createVerticalStrut(18));

        // Access Patterns & Comparison Matrix Card
        section.add(createAccessAndComparisonCard());

        return section;
    }

    private JPanel createAlgorithmsThreeColumnCard() {
        JPanel outer = createCardContainer();
        outer.setLayout(new BorderLayout(0, 14));

        JLabel heading = new JLabel("PAGE REPLACEMENT ALGORITHMS", SwingConstants.CENTER);
        heading.setFont(FONT_CARD_HEAD);
        heading.setForeground(TEXT_MAIN);
        outer.add(heading, BorderLayout.NORTH);

        JPanel cols = new JPanel(new GridLayout(1, 3, 14, 0));
        cols.setOpaque(false);

        cols.add(createAlgorithmColumn(
                "FIFO",
                "First In, First Out",
                "FIFO replaces the page that entered memory first. The replacement decision follows arrival order rather than recent usage.",
                new String[]{
                        "Simple to understand and implement.",
                        "Straightforward replacement order.",
                        "Requires relatively little bookkeeping."
                },
                new String[]{
                        "Does not consider how recently a page was used.",
                        "A frequently used page can still be removed simply because it is old.",
                        "Can exhibit Belady's anomaly."
                }
        ));

        cols.add(createAlgorithmColumn(
                "LRU",
                "Least Recently Used",
                "LRU replaces the page that has not been referenced for the longest time. It uses recent access behavior as an approximation of which pages may be needed again.",
                new String[]{
                        "Uses recent page-access behavior.",
                        "Works well with temporal locality.",
                        "Provides a practical approximation of future page usage."
                },
                new String[]{
                        "Requires tracking page recency.",
                        "Requires more bookkeeping than FIFO.",
                        "Can perform poorly on large sequential or cyclic scans."
                }
        ));

        cols.add(createAlgorithmColumn(
                "OPTIMAL",
                "Optimal Page Replacement",
                "Optimal replaces the page whose next use is farthest in the future, or a page that will never be referenced again. It assumes that the complete future reference string is known and is therefore used as a theoretical benchmark.",
                new String[]{
                        "Provides the theoretical minimum page-fault count.",
                        "Useful as a benchmark for FIFO and LRU.",
                        "Provides a lower bound for comparison."
                },
                new String[]{
                        "Requires knowledge of future page references.",
                        "Future knowledge is generally unavailable to a running operating system.",
                        "Used mainly as a theoretical benchmark."
                }
        ));

        outer.add(cols, BorderLayout.CENTER);
        return outer;
    }

    private JPanel createAlgorithmColumn(
            String title, String subtitle, String desc, String[] pros, String[] cons) {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(CARD_SUBTLE);
        col.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLbl.setForeground(NAVY_BLUE);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLbl = new JLabel(subtitle, SwingConstants.CENTER);
        subLbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subLbl.setForeground(TEXT_MUTED);
        subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        col.add(titleLbl);
        col.add(Box.createVerticalStrut(3));
        col.add(subLbl);
        col.add(Box.createVerticalStrut(14));

        col.add(createSubHeader("HOW IT WORKS", NAVY_BLUE));
        col.add(Box.createVerticalStrut(4));
        col.add(createWrapArea(desc));
        col.add(Box.createVerticalStrut(14));

        col.add(createSubHeader("ADVANTAGES", SUCCESS_TEXT));
        col.add(Box.createVerticalStrut(4));
        for (String p : pros) col.add(createBulletItem(p, SUCCESS_TEXT));
        col.add(Box.createVerticalStrut(14));

        col.add(createSubHeader("DISADVANTAGES", DANGER_TEXT));
        col.add(Box.createVerticalStrut(4));
        for (String c : cons) col.add(createBulletItem(c, DANGER_TEXT));

        return col;
    }

    private JPanel createAccessAndComparisonCard() {
        JPanel outer = createCardContainer();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));

        JLabel mainTitle = new JLabel("ACCESS PATTERNS, LOCALITY & ALGORITHM COMPARISON", SwingConstants.CENTER);
        mainTitle.setFont(FONT_CARD_HEAD);
        mainTitle.setForeground(TEXT_MAIN);
        mainTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        outer.add(mainTitle);
        outer.add(Box.createVerticalStrut(14));

        JLabel subHead = new JLabel("ACCESS PATTERNS AND LOCALITY", SwingConstants.CENTER);
        subHead.setFont(FONT_SUB_HEAD);
        subHead.setForeground(NAVY_BLUE);
        subHead.setAlignmentX(Component.CENTER_ALIGNMENT);
        outer.add(subHead);
        outer.add(Box.createVerticalStrut(10));

        JPanel grid = new JPanel(new GridLayout(1, 3, 14, 0));
        grid.setOpaque(false);

        grid.add(createAccessBlock(
                "LOCALITY OF REFERENCE",
                "Real programs commonly access memory in patterns rather than in completely random orders. "
                        + "Loops and repeated working sets cause pages to be referenced repeatedly. "
                        + "Temporal locality is particularly relevant to LRU because recently used pages may be more likely to be used again soon."
        ));
        grid.add(createAccessBlock(
                "CYCLIC ACCESS",
                "A cyclic or sequential reference pattern can expose weaknesses in recency-based replacement. "
                        + "When more distinct pages are repeatedly accessed than there are available frames, "
                        + "the least recently used page may be exactly the page required next."
        ));
        grid.add(createAccessBlock(
                "BELADY'S ANOMALY",
                "Belady's anomaly is the counter-intuitive situation in which increasing the number of frames "
                        + "can increase the number of page faults. FIFO can exhibit this behavior because its replacement "
                        + "decision depends on arrival order rather than current usage pattern."
        ));

        outer.add(grid);
        outer.add(Box.createVerticalStrut(18));

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(CARD_BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        outer.add(sep);
        outer.add(Box.createVerticalStrut(16));

        JLabel compTitle = new JLabel("ALGORITHM COMPARISON", SwingConstants.CENTER);
        compTitle.setFont(FONT_SUB_HEAD);
        compTitle.setForeground(NAVY_BLUE);
        compTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        outer.add(compTitle);
        outer.add(Box.createVerticalStrut(10));

        String[] cols = {"Feature", "FIFO", "LRU", "Optimal"};
        Object[][] data = {
                {"Replacement basis", "Arrival order", "Least recent use", "Farthest future use"},
                {"Future knowledge", "No", "No", "Yes"},
                {"Implementation", "Simple", "Requires recency tracking", "Theoretical"},
                {"Belady's anomaly", "Possible", "No", "No"},
                {"Response to locality", "Does not use locality directly", "Benefits from recent usage", "Ideal benchmark"},
                {"Primary purpose", "Simple replacement policy", "Practical recency-based approach", "Theoretical benchmark"}
        };

        JTable table = new JTable(data, cols);
        styleTable(table);
        table.setRowHeight(32);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(table, BorderLayout.CENTER);

        outer.add(tablePanel);
        return outer;
    }

    // =========================================================
    // 2. INPUT SECTION
    // =========================================================
    private JPanel createInputSection() {
        JPanel section = createSectionWrapper("2. INPUT");

        JPanel form = createCardContainer();
        form.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Reference String
        JLabel refLabel = new JLabel("Reference String");
        refLabel.setFont(FONT_BOLD_BODY);
        refLabel.setForeground(TEXT_MAIN);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(refLabel, gbc);

        referenceField = new JTextField("7 0 1 2 0 3 0 4 2 3");
        referenceField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        referenceField.setPreferredSize(new Dimension(500, 36));
        referenceField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 4; gbc.weightx = 1.0;
        form.add(referenceField, gbc);

        // Row 2: Frames + Buttons
        JLabel frameLabel = new JLabel("Number of Frames");
        frameLabel.setFont(FONT_BOLD_BODY);
        frameLabel.setForeground(TEXT_MAIN);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0;
        form.add(frameLabel, gbc);

        frameSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 16, 1));
        frameSpinner.setPreferredSize(new Dimension(80, 36));
        frameSpinner.setFont(FONT_BOLD_BODY);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0;
        form.add(frameSpinner, gbc);

        JButton exampleBtn = createButton("Load Example", new Color(241, 245, 249), TEXT_MAIN, false);
        exampleBtn.addActionListener(e -> loadExample());
        gbc.gridx = 2; gbc.gridy = 1;
        form.add(exampleBtn, gbc);

        JButton runBtn = createButton("Run Simulation", PRIMARY, Color.WHITE, true);
        runBtn.addActionListener(e -> runComparison());
        gbc.gridx = 3; gbc.gridy = 1;
        form.add(runBtn, gbc);

        JButton clearBtn = createButton("Clear", new Color(241, 245, 249), TEXT_MUTED, false);
        clearBtn.addActionListener(e -> clearFields());
        gbc.gridx = 4; gbc.gridy = 1;
        form.add(clearBtn, gbc);

        section.add(form);
        return section;
    }

    // =========================================================
    // 3. SIMULATOR SECTION
    // =========================================================
    private JPanel createSimulatorSection() {
        JPanel section = createSectionWrapper("3. SIMULATOR");

        // Controls Bar Card
        JPanel controlCard = createCardContainer();
        controlCard.setLayout(new BorderLayout(14, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JLabel algoLbl = new JLabel("Algorithm:");
        algoLbl.setFont(FONT_BOLD_BODY);
        algoLbl.setForeground(TEXT_MAIN);

        algorithmSelector = new JComboBox<>(new String[]{"FIFO", "LRU", "Optimal"});
        algorithmSelector.setFont(FONT_BOLD_BODY);
        algorithmSelector.setPreferredSize(new Dimension(130, 34));
        algorithmSelector.addActionListener(e -> {
            currentSimulationStep = 0;
            refreshSimulationTable();
            showCurrentSimulationStep();
        });

        left.add(algoLbl);
        left.add(algorithmSelector);
        controlCard.add(left, BorderLayout.WEST);

        // Center: Stepper Buttons
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        nav.setOpaque(false);

        firstButton = createSmallButton("First");
        previousButton = createSmallButton("Previous");
        nextButton = createSmallButton("Next");
        lastButton = createSmallButton("Last");
        resetButton = createSmallButton("Reset");

        firstButton.addActionListener(e -> showFirstStep());
        previousButton.addActionListener(e -> showPreviousStep());
        nextButton.addActionListener(e -> showNextStep());
        lastButton.addActionListener(e -> showLastStep());
        resetButton.addActionListener(e -> resetSimulator());

        nav.add(firstButton);
        nav.add(previousButton);
        nav.add(nextButton);
        nav.add(lastButton);
        nav.add(resetButton);
        controlCard.add(nav, BorderLayout.CENTER);

        simulationStepLabel = new JLabel("Step 0 / 0", SwingConstants.RIGHT);
        simulationStepLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        simulationStepLabel.setForeground(NAVY_BLUE);
        controlCard.add(simulationStepLabel, BorderLayout.EAST);

        section.add(controlCard);
        section.add(Box.createVerticalStrut(14));

        // Current Memory State Card
        JPanel memoryCard = createCardContainer();
        memoryCard.setLayout(new BorderLayout(0, 12));
        memoryCard.add(createCardTitle("CURRENT MEMORY STATE"), BorderLayout.NORTH);

        frameCardsPanel = new JPanel();
        frameCardsPanel.setOpaque(false);
        buildEmptyFrameCards();
        memoryCard.add(frameCardsPanel, BorderLayout.CENTER);

        // State Details Sub-bar
        JPanel stateBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        stateBar.setBackground(CARD_SUBTLE);
        stateBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, CARD_BORDER));

        simulationPageLabel = new JLabel("Page: -");
        simulationPageLabel.setFont(FONT_BOLD_BODY);

        simulationResultBadge = new JLabel("Result: -");
        simulationResultBadge.setFont(FONT_BOLD_BODY);
        simulationResultBadge.setOpaque(true);
        simulationResultBadge.setBackground(CARD_SUBTLE);
        simulationResultBadge.setBorder(new EmptyBorder(3, 10, 3, 10));

        simulationMessageLabel = new JLabel("Run a simulation first.");
        simulationMessageLabel.setFont(FONT_BODY);
        simulationMessageLabel.setForeground(TEXT_MUTED);

        stateBar.add(simulationPageLabel);
        stateBar.add(simulationResultBadge);
        stateBar.add(simulationMessageLabel);

        memoryCard.add(stateBar, BorderLayout.SOUTH);
        section.add(memoryCard);
        section.add(Box.createVerticalStrut(14));

        // Detailed Execution Table Card
        JPanel detailCard = createCardContainer();
        detailCard.setLayout(new BorderLayout(0, 8));
        detailCard.add(createCardTitle("DETAILED EXECUTION"), BorderLayout.NORTH);

        simulationTable = createSimulationTable();
        JScrollPane simScroll = new JScrollPane(simulationTable);
        simScroll.setPreferredSize(new Dimension(0, 240));
        simScroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        simScroll.getViewport().setBackground(Color.WHITE);
        detailCard.add(simScroll, BorderLayout.CENTER);

        section.add(detailCard);

        updateSimulatorButtons();
        return section;
    }

    // =========================================================
    // 4. COMPARATOR SECTION
    // =========================================================
    private JPanel createComparatorSection() {
        JPanel section = createSectionWrapper("4. COMPARATOR");

        // Summary Metric Cards
        JPanel metrics = new JPanel(new GridLayout(1, 3, 14, 0));
        metrics.setOpaque(false);
        metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JPanel m1 = createMetricCard("REFERENCES", "0");
        JPanel m2 = createMetricCard("FRAMES", "0");
        JPanel m3 = createMetricCard("ALGORITHMS", "3");

        referencesValueLabel = getMetricValue(m1);
        framesValueLabel = getMetricValue(m2);
        algorithmsValueLabel = getMetricValue(m3);

        metrics.add(m1);
        metrics.add(m2);
        metrics.add(m3);

        section.add(metrics);
        section.add(Box.createVerticalStrut(14));

        // Comparison Results Card
        JPanel resultsCard = createCardContainer();
        resultsCard.setLayout(new BorderLayout(0, 10));
        resultsCard.add(createCardTitle("COMPARISON RESULTS"), BorderLayout.NORTH);

        resultTableModel = new DefaultTableModel(
                new String[]{"Algorithm", "Hits", "Faults", "Hit Ratio", "Fault Ratio"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        resultTable = new JTable(resultTableModel);
        styleTable(resultTable);
        resultTable.setRowHeight(34);

        JScrollPane tableScroll = new JScrollPane(resultTable);
        tableScroll.setPreferredSize(new Dimension(0, 136));
        tableScroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        tableScroll.getViewport().setBackground(Color.WHITE);

        resultsCard.add(tableScroll, BorderLayout.CENTER);

        statusLabel = new JLabel("Run the simulation to compare FIFO, LRU and Optimal.", SwingConstants.LEFT);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_MUTED);
        resultsCard.add(statusLabel, BorderLayout.SOUTH);

        section.add(resultsCard);
        return section;
    }

    // =========================================================
    // EXECUTION & LOGIC HANDLERS
    // =========================================================
    private void loadExample() {
        referenceField.setText("7 0 1 2 0 3 0 4 2 3");
        frameSpinner.setValue(3);
        statusLabel.setText("Example loaded. Click Run Simulation.");
        statusLabel.setForeground(PRIMARY);
    }

    private void runComparison() {
        String input = referenceField.getText().trim();
        if (input.isEmpty()) {
            showError("Please enter a reference string.");
            return;
        }

        int frames = (Integer) frameSpinner.getValue();

        try {
            String[] values = input.split("[,\\s]+");
            int[] pages = new int[values.length];

            for (int i = 0; i < values.length; i++) {
                pages[i] = Integer.parseInt(values[i]);
                if (pages[i] < 0) {
                    showError("Page numbers must be non-negative integers.");
                    return;
                }
            }

            // Core Page Replacement Algorithms
            fifoResult = PageReplacement.fifo(pages, frames);
            lruResult = PageReplacement.lru(pages, frames);
            optimalResult = PageReplacement.optimal(pages, frames);

            // Populate Comparator Table
            resultTableModel.setRowCount(0);
            addComparisonRow("FIFO", fifoResult, pages.length);
            addComparisonRow("LRU", lruResult, pages.length);
            addComparisonRow("Optimal", optimalResult, pages.length);

            referencesValueLabel.setText(String.valueOf(pages.length));
            framesValueLabel.setText(String.valueOf(frames));
            algorithmsValueLabel.setText("3");

            statusLabel.setText(String.format("Comparison complete  •  %d references  •  %d frames", pages.length, frames));
            statusLabel.setForeground(SUCCESS_TEXT);

            // Simulator Update
            currentSimulationStep = 0;
            refreshSimulationTable();
            showCurrentSimulationStep();

        } catch (NumberFormatException ex) {
            showError("Use only valid page numbers separated by spaces or commas.");
        }
    }

    private void addComparisonRow(String algorithm, PageReplacement.Result result, int total) {
        double hitRatio = (double) result.pageHits / total * 100.0;
        double faultRatio = (double) result.pageFaults / total * 100.0;

        resultTableModel.addRow(new Object[]{
                algorithm,
                result.pageHits,
                result.pageFaults,
                String.format("%.2f%%", hitRatio),
                String.format("%.2f%%", faultRatio)
        });
    }

    private void clearFields() {
        referenceField.setText("");
        frameSpinner.setValue(3);
        fifoResult = null;
        lruResult = null;
        optimalResult = null;
        currentSimulationStep = 0;

        resultTableModel.setRowCount(0);
        referencesValueLabel.setText("0");
        framesValueLabel.setText("0");
        algorithmsValueLabel.setText("3");

        statusLabel.setText("Enter a reference string and run the simulation.");
        statusLabel.setForeground(TEXT_MUTED);

        if (simulationTableModel != null) simulationTableModel.setRowCount(0);
        simulationStepLabel.setText("Step 0 / 0");
        simulationPageLabel.setText("Page: -");
        simulationResultBadge.setText("Result: -");
        simulationResultBadge.setBackground(CARD_SUBTLE);
        simulationResultBadge.setForeground(TEXT_MAIN);
        simulationMessageLabel.setText("Run a simulation first.");

        buildEmptyFrameCards();
        updateSimulatorButtons();
    }

    private PageReplacement.Result getSelectedResult() {
        String sel = String.valueOf(algorithmSelector.getSelectedItem());
        if ("FIFO".equals(sel)) return fifoResult;
        if ("LRU".equals(sel)) return lruResult;
        if ("Optimal".equals(sel)) return optimalResult;
        return null;
    }

    private void refreshSimulationTable() {
        if (simulationTable == null) return;
        fillSimulationTable(getSelectedResult());
    }

    private void fillSimulationTable(PageReplacement.Result result) {
        if (result == null || result.steps == null || result.steps.isEmpty()) {
            simulationTableModel = new DefaultTableModel(new Object[]{"Step", "Page", "Result"}, 0);
            simulationTable.setModel(simulationTableModel);
            buildEmptyFrameCards();
            return;
        }

        int frameCount = result.steps.get(0).frames.length;
        String[] cols = new String[3 + frameCount];
        cols[0] = "Step";
        cols[1] = "Page";
        cols[2] = "Result";
        for (int i = 0; i < frameCount; i++) cols[3 + i] = "Frame " + (i + 1);

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (PageReplacement.SimulationStep s : result.steps) {
            Object[] row = new Object[3 + frameCount];
            row[0] = s.stepNumber;
            row[1] = s.page;
            row[2] = s.result;
            for (int i = 0; i < frameCount; i++) {
                row[3 + i] = (s.frames[i] == -1) ? "-" : s.frames[i];
            }
            model.addRow(row);
        }

        simulationTableModel = model;
        simulationTable.setModel(simulationTableModel);
        styleTable(simulationTable);

        simulationTable.getColumnModel().getColumn(2).setCellRenderer(new ResultCellRenderer());
    }

    private void showCurrentSimulationStep() {
        PageReplacement.Result result = getSelectedResult();

        if (result == null || result.steps == null || result.steps.isEmpty()) {
            simulationStepLabel.setText("Step 0 / 0");
            simulationPageLabel.setText("Page: -");
            simulationResultBadge.setText("Result: -");
            simulationResultBadge.setBackground(CARD_SUBTLE);
            simulationResultBadge.setForeground(TEXT_MAIN);
            simulationMessageLabel.setText("Run a simulation first.");
            buildEmptyFrameCards();
            updateSimulatorButtons();
            return;
        }

        List<PageReplacement.SimulationStep> steps = result.steps;
        if (currentSimulationStep < 0) currentSimulationStep = 0;
        if (currentSimulationStep >= steps.size()) currentSimulationStep = steps.size() - 1;

        PageReplacement.SimulationStep step = steps.get(currentSimulationStep);

        simulationStepLabel.setText(String.format("Step %d / %d", currentSimulationStep + 1, steps.size()));
        simulationPageLabel.setText("Page: " + step.page);

        if ("HIT".equals(step.result)) {
            simulationResultBadge.setText("Result: HIT");
            simulationResultBadge.setForeground(SUCCESS_TEXT);
            simulationResultBadge.setBackground(SUCCESS_BG);
            simulationMessageLabel.setText("Page already exists in memory.");
        } else {
            simulationResultBadge.setText("Result: FAULT");
            simulationResultBadge.setForeground(DANGER_TEXT);
            simulationResultBadge.setBackground(DANGER_BG);
            simulationMessageLabel.setText("Page fault: memory state updated.");
        }

        buildFrameCards(step.frames);
        selectCurrentSimulationRow();
        updateSimulatorButtons();
    }

    private void buildFrameCards(int[] frames) {
        frameCardsPanel.removeAll();
        if (frames == null || frames.length == 0) {
            buildEmptyFrameCards();
            return;
        }

        frameCardsPanel.setLayout(new GridLayout(1, frames.length, 12, 0));
        for (int i = 0; i < frames.length; i++) {
            frameCardsPanel.add(createFrameSlot(i + 1, frames[i]));
        }
        frameCardsPanel.revalidate();
        frameCardsPanel.repaint();
    }

    private void buildEmptyFrameCards() {
        frameCardsPanel.removeAll();
        frameCardsPanel.setLayout(new BorderLayout());

        JLabel lbl = new JLabel("Run a simulation to see the frame contents.", SwingConstants.CENTER);
        lbl.setFont(FONT_SUBTITLE);
        lbl.setForeground(TEXT_MUTED);
        lbl.setBorder(new EmptyBorder(22, 0, 22, 0));

        frameCardsPanel.add(lbl, BorderLayout.CENTER);
        frameCardsPanel.revalidate();
        frameCardsPanel.repaint();
    }

    private JPanel createFrameSlot(int frameNum, int pageVal) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        boolean isEmpty = (pageVal == -1);

        card.setBackground(isEmpty ? CARD_SUBTLE : PRIMARY_LIGHT);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isEmpty ? CARD_BORDER : PRIMARY, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel title = new JLabel("FRAME " + frameNum, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        title.setForeground(isEmpty ? TEXT_MUTED : PRIMARY);

        JLabel val = new JLabel(isEmpty ? "-" : String.valueOf(pageVal), SwingConstants.CENTER);
        val.setFont(new Font("Segoe UI", Font.BOLD, 26));
        val.setForeground(isEmpty ? TEXT_MUTED : NAVY_BLUE);

        card.add(title, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    private void selectCurrentSimulationRow() {
        if (simulationTable == null) return;
        int row = currentSimulationStep;
        if (row >= 0 && row < simulationTable.getRowCount()) {
            simulationTable.setRowSelectionInterval(row, row);
            simulationTable.scrollRectToVisible(simulationTable.getCellRect(row, 0, true));
        }
    }

    private void showFirstStep() { currentSimulationStep = 0; showCurrentSimulationStep(); }
    private void showPreviousStep() { if (currentSimulationStep > 0) currentSimulationStep--; showCurrentSimulationStep(); }
    private void showNextStep() {
        PageReplacement.Result res = getSelectedResult();
        if (res != null && res.steps != null && currentSimulationStep < res.steps.size() - 1) {
            currentSimulationStep++;
        }
        showCurrentSimulationStep();
    }
    private void showLastStep() {
        PageReplacement.Result res = getSelectedResult();
        if (res != null && res.steps != null && !res.steps.isEmpty()) {
            currentSimulationStep = res.steps.size() - 1;
        }
        showCurrentSimulationStep();
    }
    private void resetSimulator() {
        currentSimulationStep = 0;
        showCurrentSimulationStep();
    }

    private void updateSimulatorButtons() {
        if (firstButton == null) return;
        PageReplacement.Result res = getSelectedResult();
        boolean hasSteps = res != null && res.steps != null && !res.steps.isEmpty();

        firstButton.setEnabled(hasSteps && currentSimulationStep > 0);
        previousButton.setEnabled(hasSteps && currentSimulationStep > 0);
        nextButton.setEnabled(hasSteps && currentSimulationStep < res.steps.size() - 1);
        lastButton.setEnabled(hasSteps && currentSimulationStep < res.steps.size() - 1);
        resetButton.setEnabled(hasSteps);
    }

    // =========================================================
    // UI BUILDER HELPERS
    // =========================================================
    private JPanel createSectionWrapper(String title) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel header = new JLabel(title);
        header.setFont(FONT_SEC_HEAD);
        header.setForeground(NAVY_BLUE);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrapper.add(header);
        wrapper.add(Box.createVerticalStrut(10));
        return wrapper;
    }

    private JPanel createCardContainer() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                new EmptyBorder(16, 18, 16, 18)
        ));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return panel;
    }

    private JPanel createCard(String title, String text) {
        JPanel card = createCardContainer();
        card.setLayout(new BorderLayout(0, 8));
        card.add(createCardTitle(title), BorderLayout.NORTH);
        card.add(createWrapArea(text), BorderLayout.CENTER);
        return card;
    }

    private JPanel createMiniCard(String title, String text) {
        JPanel card = createCardContainer();
        card.setLayout(new BorderLayout(0, 6));
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_CARD_HEAD);
        titleLbl.setForeground(TEXT_MAIN);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(createWrapArea(text), BorderLayout.CENTER);
        return card;
    }

    private JPanel createAccessBlock(String title, String text) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(CARD_SUBTLE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(NAVY_BLUE);

        p.add(t, BorderLayout.NORTH);
        p.add(createWrapArea(text), BorderLayout.CENTER);
        return p;
    }

    private JLabel createCardTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_CARD_HEAD);
        l.setForeground(TEXT_MAIN);
        return l;
    }

    private JLabel createSubHeader(String text, Color col) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(col);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JTextArea createWrapArea(String text) {
        JTextArea a = new JTextArea(text);
        a.setEditable(false);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setFont(FONT_BODY);
        a.setForeground(TEXT_MAIN);
        a.setOpaque(false);
        a.setBorder(null);
        return a;
    }

    private JPanel createBulletItem(String text, Color col) {
        JPanel p = new JPanel(new BorderLayout(6, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(2, 0, 2, 0));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dot = new JLabel("•");
        dot.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dot.setForeground(col);

        p.add(dot, BorderLayout.WEST);
        p.add(createWrapArea(text), BorderLayout.CENTER);
        return p;
    }

    private JPanel createMetricCard(String title, String value) {
        JPanel card = createCardContainer();
        card.setLayout(new BorderLayout(0, 4));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 11));
        t.setForeground(TEXT_MUTED);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 26));
        v.setForeground(NAVY_BLUE);

        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);
        return card;
    }

    private JLabel getMetricValue(JPanel panel) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JLabel && ((JLabel) c).getFont().getSize() >= 24) {
                return (JLabel) c;
            }
        }
        return null;
    }

    private JTable createSimulationTable() {
        simulationTableModel = new DefaultTableModel(new Object[]{"Step", "Page", "Result"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(simulationTableModel);
        styleTable(table);
        return table;
    }

    private void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_MAIN);
        table.setBackground(Color.WHITE);
        table.setGridColor(CARD_BORDER);
        table.setShowGrid(true);
        table.setRowHeight(30);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_MAIN);
        table.setFillsViewportHeight(true);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(239, 242, 247));
        table.getTableHeader().setForeground(TEXT_MAIN);
        table.getTableHeader().setPreferredSize(new Dimension(0, 34));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
    }

    private JButton createButton(String text, Color bg, Color fg, boolean isPrimary) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD_BODY);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isPrimary ? PRIMARY : CARD_BORDER, 1),
                new EmptyBorder(8, 18, 8, 18)
        ));
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (isPrimary) {
            btn.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { btn.setBackground(PRIMARY_HOVER); }
                @Override public void mouseExited(MouseEvent e) { btn.setBackground(PRIMARY); }
            });
        }
        return btn;
    }

    private JButton createSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(new Color(241, 245, 249));
        btn.setForeground(TEXT_MAIN);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                new EmptyBorder(5, 12, 5, 12)
        ));
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    // =========================================================
    // RESPONSIVE SCROLLABLE TRACKER (Forces 100% Full-Width)
    // =========================================================
    private static class FullWidthPage extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
            return 20;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
            return 60;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true; // Crucial: Locks panel width to 100% of viewport
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    // =========================================================
    // TABLE HIT/FAULT STATUS RENDERER
    // =========================================================
    private static class ResultCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object val, boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, val, isSelected, hasFocus, row, col);
            setHorizontalAlignment(SwingConstants.CENTER);

            if (!isSelected && val != null) {
                String str = val.toString();
                if ("HIT".equals(str)) {
                    setForeground(SUCCESS_TEXT);
                    setBackground(SUCCESS_BG);
                } else if ("FAULT".equals(str)) {
                    setForeground(DANGER_TEXT);
                    setBackground(DANGER_BG);
                } else {
                    setForeground(TEXT_MAIN);
                    setBackground(Color.WHITE);
                }
            } else if (isSelected) {
                setBackground(PRIMARY_LIGHT);
                setForeground(TEXT_MAIN);
            }
            return c;
        }
    }
}