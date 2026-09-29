package os.main.com;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
    private static final Font FONT_MONO        = new Font("Consolas", Font.PLAIN, 13);

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
    // 1. THEORY SECTION  (every child is LEFT-aligned for a clean edge)
    // =========================================================
    private JPanel createTheorySection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel secTitle = new JLabel("1. THEORY");
        secTitle.setFont(FONT_SEC_HEAD);
        secTitle.setForeground(NAVY_BLUE);
        section.add(secTitle);
        section.add(Box.createVerticalStrut(14));

        // ---- 1.1 Virtual memory & demand paging
        section.add(createCard(
                "VIRTUAL MEMORY AND DEMAND PAGING",
                "Virtual memory lets a process use an address space that is larger than the physical memory "
                        + "installed in the machine. The address space is divided into fixed-size pages, and physical "
                        + "memory is divided into frames of the same size. Under demand paging, a page is brought into "
                        + "a frame only when the process actually references it, instead of loading the whole program "
                        + "in advance.\n\n"
                        + "Because physical memory is limited, the set of frames eventually fills up. From that point on, "
                        + "every newly required page forces the operating system to make a decision: which page currently "
                        + "in memory should be evicted? Page replacement algorithms are the policies that make this decision."
        ));
        section.add(Box.createVerticalStrut(14));

        // ---- 1.2 Page replacement
        section.add(createCard(
                "PAGE REPLACEMENT",
                "Page replacement is a memory-management technique used in virtual memory systems. "
                        + "When a requested page is not currently present in physical memory and all available "
                        + "frames are occupied, the operating system must select an existing page to remove "
                        + "so that the required page can be loaded. The selected page is called the victim page.\n\n"
                        + "A good replacement policy keeps the pages that will be needed soon and evicts the pages that "
                        + "will not. Since the cost of a wrong choice is a slow disk access, the quality of a policy is "
                        + "judged by how few page faults it produces for a given reference string and number of frames."
        ));
        section.add(Box.createVerticalStrut(18));

        // ---- 1.3 Important terms (3 x 3 grid)
        section.add(createTheoryHeading("IMPORTANT TERMS"));
        section.add(Box.createVerticalStrut(10));

        HPanel termsGrid = new HPanel(new GridLayout(0, 3, 14, 14));
        termsGrid.setOpaque(false);
        termsGrid.add(createMiniCard("PAGE", "A fixed-size block of virtual (logical) memory."));
        termsGrid.add(createMiniCard("FRAME", "A fixed-size block of physical memory that can hold exactly one page."));
        termsGrid.add(createMiniCard("REFERENCE STRING", "The sequence of page numbers requested by a process during its execution."));
        termsGrid.add(createMiniCard("PAGE HIT", "The requested page is already present in one of the frames, so no replacement is required."));
        termsGrid.add(createMiniCard("PAGE FAULT", "The requested page is not in any frame and must be brought in from secondary storage."));
        termsGrid.add(createMiniCard("VICTIM PAGE", "The page chosen by the replacement algorithm to be evicted from memory."));
        termsGrid.add(createMiniCard("HIT RATIO", "The fraction of references that are page hits: hits divided by total references."));
        termsGrid.add(createMiniCard("FAULT RATIO", "The fraction of references that cause a page fault: faults divided by total references."));
        termsGrid.add(createMiniCard("DEMAND PAGING", "A loading strategy in which pages are fetched into memory only when they are referenced."));
        section.add(termsGrid);
        section.add(Box.createVerticalStrut(18));

        // ---- 1.4 Page fault
        section.add(createCard(
                "PAGE FAULT",
                "A page fault occurs when the referenced page is not currently held in any frame. "
                        + "The page must be loaded into a free frame or, when all frames are full, "
                        + "a victim page must be selected and replaced. Page faults are important because "
                        + "loading a missing page can require relatively slow secondary-storage access, which is "
                        + "many thousands of times slower than an access to main memory."
        ));
        section.add(Box.createVerticalStrut(14));

        // ---- 1.5 Page fault handling steps
        section.add(createStepsCard(
                "HOW A PAGE FAULT IS HANDLED",
                new String[]{
                        "The CPU references a page. The hardware checks the page table and finds that the page is not in memory, raising a page-fault trap.",
                        "The operating system checks that the reference is valid. An invalid reference terminates the process.",
                        "The OS looks for a free frame. If one exists, it is used immediately.",
                        "If no frame is free, the page replacement algorithm selects a victim page. If the victim was modified (dirty), it is first written back to disk.",
                        "The required page is read from secondary storage into the freed frame, and the page table is updated.",
                        "The interrupted instruction is restarted, and this time the page is found in memory."
                }
        ));
        section.add(Box.createVerticalStrut(14));

        // ---- 1.6 Performance metrics
        section.add(createMetricsTheoryCard());
        section.add(Box.createVerticalStrut(18));

        // ---- 1.7 Three algorithms
        section.add(createAlgorithmsThreeColumnCard());
        section.add(Box.createVerticalStrut(18));

        // ---- 1.8 Worked example
        section.add(createWorkedExampleCard());
        section.add(Box.createVerticalStrut(18));

        // ---- 1.9 Access patterns + comparison matrix
        section.add(createAccessAndComparisonCard());
        section.add(Box.createVerticalStrut(18));

        // ---- 1.10 Related concepts
        section.add(createRelatedConceptsCard());
        section.add(Box.createVerticalStrut(14));

        // ---- 1.11 Key takeaways
        section.add(createStepsCard(
                "KEY TAKEAWAYS",
                new String[]{
                        "Page faults are expensive, so a good replacement policy is one that minimises them.",
                        "Optimal always gives the fewest faults, but it needs future knowledge and is used only as a benchmark.",
                        "LRU approximates Optimal by looking at the past, and works well when programs show temporal locality.",
                        "FIFO is the simplest policy, but it ignores usage and can suffer from Belady's anomaly.",
                        "For any input, the fault counts satisfy Optimal ≤ LRU and Optimal ≤ FIFO. FIFO and LRU cannot be ranked in general."
                }
        ));

        // Force one consistent left edge for everything in this section
        for (Component c : section.getComponents()) {
            if (c instanceof JComponent) ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        return section;
    }

    private JLabel createTheoryHeading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_CARD_HEAD);
        l.setForeground(TEXT_MAIN);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // ---------- Performance metrics card ----------
    private JPanel createMetricsTheoryCard() {
        JPanel outer = createCardContainer();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));

        outer.add(leftAligned(createCardTitle("PERFORMANCE METRIC — PAGE FAULTS")));
        outer.add(Box.createVerticalStrut(8));
        outer.add(leftAligned(createWrapArea(
                "Page-fault count is the primary performance metric used in this experiment. "
                        + "For a fixed reference string and frame count, FIFO, LRU and Optimal can be "
                        + "compared by counting how many page faults occur. Reducing unnecessary page faults "
                        + "reduces the number of expensive page-loading operations.")));
        outer.add(Box.createVerticalStrut(12));

        HPanel formulas = new HPanel(new GridLayout(1, 3, 14, 0));
        formulas.setOpaque(false);
        formulas.add(createFormulaBox("HIT RATIO", "Hit Ratio = (Page Hits / Total References) × 100"));
        formulas.add(createFormulaBox("FAULT RATIO", "Fault Ratio = (Page Faults / Total References) × 100"));
        formulas.add(createFormulaBox("EFFECTIVE ACCESS TIME", "EAT = (1 − p) × ma + p × fault_time"));
        outer.add(leftAligned(formulas));
        outer.add(Box.createVerticalStrut(10));

        outer.add(leftAligned(createWrapArea(
                "Here p is the page-fault rate, ma is the main-memory access time and fault_time is the total time "
                        + "needed to service a fault. Since fault_time is enormously larger than ma, even a very small "
                        + "increase in p can slow the whole system noticeably. Also note that Hits + Faults always equals "
                        + "the total number of references, so hit ratio + fault ratio = 100%.")));
        return outer;
    }

    private JPanel createFormulaBox(String title, String formula) {
        HPanel box = new HPanel(new BorderLayout(0, 6));
        box.setBackground(CARD_SUBTLE);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(NAVY_BLUE);
        WrapText f = new WrapText(formula);
        styleWrapText(f);
        f.setFont(FONT_MONO);
        box.add(t, BorderLayout.NORTH);
        box.add(f, BorderLayout.CENTER);
        return box;
    }

    // ---------- Numbered list card ----------
    private JPanel createStepsCard(String title, String[] items) {
        JPanel card = createCardContainer();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(leftAligned(createCardTitle(title)));
        card.add(Box.createVerticalStrut(10));
        for (int i = 0; i < items.length; i++) {
            card.add(leftAligned(createNumberedItem(i + 1, items[i])));
            if (i < items.length - 1) card.add(Box.createVerticalStrut(8));
        }
        return card;
    }

    private JPanel createNumberedItem(int number, String text) {
        HPanel row = new HPanel(new BorderLayout(12, 0));
        row.setOpaque(false);

        JLabel badge = new JLabel(String.valueOf(number), SwingConstants.CENTER);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(PRIMARY);
        badge.setOpaque(true);
        badge.setBackground(PRIMARY_LIGHT);
        badge.setBorder(BorderFactory.createLineBorder(PRIMARY, 1));
        badge.setPreferredSize(new Dimension(26, 26));

        JPanel badgeHolder = new JPanel(new BorderLayout());
        badgeHolder.setOpaque(false);
        badgeHolder.add(badge, BorderLayout.NORTH);

        row.add(badgeHolder, BorderLayout.WEST);
        row.add(createWrapArea(text), BorderLayout.CENTER);
        return row;
    }

    // ---------- Algorithms (three columns) ----------
    private JPanel createAlgorithmsThreeColumnCard() {
        JPanel outer = createCardContainer();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));

        outer.add(leftAligned(createCardTitle("PAGE REPLACEMENT ALGORITHMS")));
        outer.add(Box.createVerticalStrut(12));

        HPanel cols = new HPanel(new GridLayout(1, 3, 14, 0));
        cols.setOpaque(false);

        cols.add(createAlgorithmColumn(
                "FIFO",
                "First In, First Out",
                "FIFO replaces the page that entered memory first. The frames are treated as a queue: the oldest "
                        + "page sits at the head and is evicted, and every newly loaded page joins the tail. "
                        + "The replacement decision follows arrival order rather than recent usage.",
                "A queue (or a circular pointer) holding pages in order of arrival. Both eviction and insertion take O(1) time.",
                new String[]{
                        "Simple to understand and implement.",
                        "Straightforward replacement order.",
                        "Requires relatively little bookkeeping.",
                        "Very low overhead per page fault."
                },
                new String[]{
                        "Does not consider how recently a page was used.",
                        "A frequently used page can still be removed simply because it is old.",
                        "Can exhibit Belady's anomaly.",
                        "Usually produces more faults than LRU on programs with locality."
                },
                "Systems where simplicity matters more than the fault rate, or as a building block for improved variants such as Second Chance."
        ));

        cols.add(createAlgorithmColumn(
                "LRU",
                "Least Recently Used",
                "LRU replaces the page that has not been referenced for the longest time. It uses recent access "
                        + "behavior as an approximation of which pages may be needed again: if a page was used "
                        + "recently, it is likely to be used again soon.",
                "A stack or linked list ordered by recency, a hash map plus doubly linked list, or a per-page timestamp/counter. Exact LRU needs an update on every memory reference.",
                new String[]{
                        "Uses recent page-access behavior.",
                        "Works well with temporal locality.",
                        "Provides a practical approximation of future page usage.",
                        "Never suffers from Belady's anomaly."
                },
                new String[]{
                        "Requires tracking page recency.",
                        "Requires more bookkeeping than FIFO.",
                        "Can perform poorly on large sequential or cyclic scans.",
                        "Exact LRU needs hardware support, so real systems use approximations."
                },
                "General-purpose workloads with strong locality, such as caches, database buffer pools and most application memory."
        ));

        cols.add(createAlgorithmColumn(
                "OPTIMAL",
                "Optimal Page Replacement (OPT / MIN)",
                "Optimal replaces the page whose next use is farthest in the future, or a page that will never be "
                        + "referenced again. It assumes that the complete future reference string is known and is "
                        + "therefore used as a theoretical benchmark.",
                "For each fault, scan forward in the reference string to find the next use of every page in memory. No practical data structure can replace future knowledge.",
                new String[]{
                        "Provides the theoretical minimum page-fault count.",
                        "Useful as a benchmark for FIFO and LRU.",
                        "Provides a lower bound for comparison.",
                        "Never suffers from Belady's anomaly."
                },
                new String[]{
                        "Requires knowledge of future page references.",
                        "Future knowledge is generally unavailable to a running operating system.",
                        "Used mainly as a theoretical benchmark.",
                        "Cannot be implemented in a real, general-purpose system."
                },
                "Offline analysis, simulation and research, to measure how close a practical algorithm comes to the best possible result."
        ));

        outer.add(cols);
        return outer;
    }

    private JPanel createAlgorithmColumn(
            String title, String subtitle, String desc, String impl,
            String[] pros, String[] cons, String bestFor) {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(CARD_SUBTLE);
        col.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLbl.setForeground(NAVY_BLUE);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subLbl.setForeground(TEXT_MUTED);

        col.add(leftAligned(titleLbl));
        col.add(Box.createVerticalStrut(3));
        col.add(leftAligned(subLbl));
        col.add(Box.createVerticalStrut(14));

        addColumnBlock(col, "HOW IT WORKS", NAVY_BLUE, desc);
        addColumnBlock(col, "TYPICAL IMPLEMENTATION", NAVY_BLUE, impl);

        col.add(leftAligned(createSubHeader("ADVANTAGES", SUCCESS_TEXT)));
        col.add(Box.createVerticalStrut(4));
        for (String p : pros) col.add(leftAligned(createBulletItem(p, SUCCESS_TEXT)));
        col.add(Box.createVerticalStrut(14));

        col.add(leftAligned(createSubHeader("DISADVANTAGES", DANGER_TEXT)));
        col.add(Box.createVerticalStrut(4));
        for (String c : cons) col.add(leftAligned(createBulletItem(c, DANGER_TEXT)));
        col.add(Box.createVerticalStrut(14));

        addColumnBlock(col, "BEST SUITED FOR", NAVY_BLUE, bestFor);
        return col;
    }

    private void addColumnBlock(JPanel col, String header, Color headerColor, String text) {
        col.add(leftAligned(createSubHeader(header, headerColor)));
        col.add(Box.createVerticalStrut(4));
        col.add(leftAligned(createWrapArea(text)));
        col.add(Box.createVerticalStrut(14));
    }

    // ---------- Worked example ----------
    private JPanel createWorkedExampleCard() {
        JPanel outer = createCardContainer();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));

        outer.add(leftAligned(createCardTitle("WORKED EXAMPLE")));
        outer.add(Box.createVerticalStrut(8));
        outer.add(leftAligned(createWrapArea(
                "Consider the reference string 7 0 1 2 0 3 0 4 2 3 with 3 frames (this is the default input of the "
                        + "simulator). The first three references always fault because the frames start empty. "
                        + "The decisions after that are where the algorithms differ.")));
        outer.add(Box.createVerticalStrut(12));

        HPanel grid = new HPanel(new GridLayout(1, 3, 14, 0));
        grid.setOpaque(false);
        grid.add(createAccessBlock("FIFO  —  9 faults, 1 hit",
                "When 2 arrives, 7 is the oldest page and is evicted. Later, 0 is evicted even though it was just "
                        + "used, because it entered memory before 1, 2 and 3. Only the second reference to 0 is a hit."));
        grid.add(createAccessBlock("LRU  —  8 faults, 2 hits",
                "When 2 arrives, 7 is the least recently used and is evicted. Since 0 is reused often, it stays in memory "
                        + "and the references to 0 at positions 5 and 7 are both hits."));
        grid.add(createAccessBlock("OPTIMAL  —  6 faults, 4 hits",
                "Looking ahead, Optimal evicts 7 (never used again), then 1 (never used again), then 0 (not needed "
                        + "again). This keeps 2 and 3 in memory so the final two references are hits."));
        outer.add(leftAligned(grid));
        outer.add(Box.createVerticalStrut(10));
        outer.add(leftAligned(createWrapArea(
                "Result: Optimal (6) ≤ LRU (8) ≤ FIFO (9). Use the Simulator below to step through each algorithm "
                        + "and confirm these frame contents yourself.")));
        return outer;
    }

    // ---------- Access patterns & comparison ----------
    private JPanel createAccessAndComparisonCard() {
        JPanel outer = createCardContainer();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));

        outer.add(leftAligned(createCardTitle("ACCESS PATTERNS, LOCALITY & ALGORITHM COMPARISON")));
        outer.add(Box.createVerticalStrut(12));

        JLabel subHead = new JLabel("ACCESS PATTERNS AND LOCALITY");
        subHead.setFont(FONT_SUB_HEAD);
        subHead.setForeground(NAVY_BLUE);
        outer.add(leftAligned(subHead));
        outer.add(Box.createVerticalStrut(10));

        HPanel grid = new HPanel(new GridLayout(1, 3, 14, 0));
        grid.setOpaque(false);

        grid.add(createAccessBlock(
                "LOCALITY OF REFERENCE",
                "Real programs commonly access memory in patterns rather than in completely random orders. "
                        + "Loops and repeated working sets cause pages to be referenced repeatedly. "
                        + "Temporal locality (recently used pages are used again soon) is particularly relevant to LRU. "
                        + "Spatial locality (nearby addresses are used together) explains why sequential code and arrays "
                        + "touch neighbouring pages one after another."
        ));
        grid.add(createAccessBlock(
                "CYCLIC ACCESS",
                "A cyclic or sequential reference pattern can expose weaknesses in recency-based replacement. "
                        + "When more distinct pages are repeatedly accessed than there are available frames, "
                        + "the least recently used page may be exactly the page required next. "
                        + "For example, looping over pages 1 2 3 4 with only 3 frames makes LRU fault on every single reference."
        ));
        grid.add(createAccessBlock(
                "BELADY'S ANOMALY",
                "Belady's anomaly is the counter-intuitive situation in which increasing the number of frames "
                        + "can increase the number of page faults. FIFO can exhibit this behavior because its replacement "
                        + "decision depends on arrival order rather than current usage pattern. Classic example: "
                        + "1 2 3 4 1 2 5 1 2 3 4 5 gives 9 faults with 3 frames but 10 faults with 4 frames under FIFO."
        ));
        outer.add(leftAligned(grid));
        outer.add(Box.createVerticalStrut(18));

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(CARD_BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        outer.add(leftAligned(sep));
        outer.add(Box.createVerticalStrut(16));

        JLabel compTitle = new JLabel("ALGORITHM COMPARISON");
        compTitle.setFont(FONT_SUB_HEAD);
        compTitle.setForeground(NAVY_BLUE);
        outer.add(leftAligned(compTitle));
        outer.add(Box.createVerticalStrut(10));

        String[] cols = {"Feature", "FIFO", "LRU", "Optimal"};
        Object[][] data = {
                {"Replacement basis", "Arrival order", "Least recent use", "Farthest future use"},
                {"Future knowledge", "No", "No", "Yes"},
                {"Data structure", "Queue", "Stack / list / counters", "Look-ahead scan"},
                {"Implementation", "Simple", "Requires recency tracking", "Theoretical"},
                {"Belady's anomaly", "Possible", "No", "No"},
                {"Stack algorithm", "No", "Yes", "Yes"},
                {"Response to locality", "Does not use locality directly", "Benefits from recent usage", "Ideal benchmark"},
                {"Worst-case pattern", "Belady-type strings", "Cyclic scans larger than memory", "None (minimum faults)"},
                {"Practical in real OS", "Yes (with variants)", "Yes (approximated)", "No"},
                {"Primary purpose", "Simple replacement policy", "Practical recency-based approach", "Theoretical benchmark"}
        };

        JTable table = new JTable(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        styleTable(table);
        table.setRowHeight(32);
        // Feature column left-aligned & bold, the rest centered
        DefaultTableCellRenderer featureRenderer = new DefaultTableCellRenderer();
        featureRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        featureRenderer.setFont(FONT_BOLD_BODY);
        featureRenderer.setBorder(new EmptyBorder(0, 12, 0, 8));
        table.getColumnModel().getColumn(0).setCellRenderer(featureRenderer);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(table, BorderLayout.CENTER);

        outer.add(leftAligned(tablePanel));
        return outer;
    }

    // ---------- Related concepts ----------
    private JPanel createRelatedConceptsCard() {
        JPanel outer = createCardContainer();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));

        outer.add(leftAligned(createCardTitle("RELATED CONCEPTS")));
        outer.add(Box.createVerticalStrut(10));

        HPanel grid = new HPanel(new GridLayout(0, 2, 14, 14));
        grid.setOpaque(false);
        grid.add(createAccessBlock("THRASHING",
                "Thrashing happens when a process does not have enough frames for its active pages, so it faults "
                        + "continuously and spends more time swapping pages than executing instructions. CPU utilisation "
                        + "then drops sharply. Adding frames or reducing the number of running processes resolves it."));
        grid.add(createAccessBlock("WORKING-SET MODEL",
                "The working set of a process is the set of pages it referenced during its most recent window of "
                        + "references. If the frames allocated to a process cover its working set, the fault rate stays low; "
                        + "if not, the process is likely to thrash."));
        grid.add(createAccessBlock("STACK ALGORITHMS",
                "An algorithm is a stack algorithm if the set of pages held with n frames is always a subset of the "
                        + "set held with n + 1 frames. Such algorithms, including LRU and Optimal, can never show "
                        + "Belady's anomaly. FIFO is not a stack algorithm."));
        grid.add(createAccessBlock("PRACTICAL APPROXIMATIONS",
                "Because exact LRU is expensive, operating systems use reference bits and approximations such as "
                        + "Second Chance (FIFO with a reference bit) and the Clock algorithm. Modified (dirty) bits are also "
                        + "used to prefer evicting clean pages, which avoids a disk write."));
        outer.add(leftAligned(grid));
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

        JButton runBtn = createButton("Run Simulation", PRIMARY, Color.BLACK, true);
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
    private <T extends JComponent> T leftAligned(T c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

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

    /** Card panel: full width, but never taller than its content. */
    private JPanel createCardContainer() {
        JPanel panel = new HPanel(null);
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                new EmptyBorder(16, 18, 16, 18)
        ));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
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
        HPanel p = new HPanel(new BorderLayout(0, 8));
        p.setBackground(CARD_SUBTLE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel t = new JLabel(title);
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
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(col);
        return l;
    }

    private void styleWrapText(JTextArea a) {
        a.setEditable(false);
        a.setFocusable(false);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setFont(FONT_BODY);
        a.setForeground(TEXT_MAIN);
        a.setOpaque(false);
        a.setBorder(null);
        a.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /** Wrapping text that always computes its height for the width it is given. */
    private JTextArea createWrapArea(String text) {
        WrapText a = new WrapText(text);
        styleWrapText(a);
        return a;
    }

    /** Bullet row: the dot stays at the top of the first line, text wraps beside it. */
    private JPanel createBulletItem(String text, Color col) {
        HPanel p = new HPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(2, 0, 2, 0));

        JLabel dot = new JLabel("•");
        dot.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dot.setForeground(col);

        JPanel dotHolder = new JPanel(new BorderLayout());
        dotHolder.setOpaque(false);
        dotHolder.add(dot, BorderLayout.NORTH);

        p.add(dotHolder, BorderLayout.WEST);
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
    // LAYOUT HELPER CLASSES
    // =========================================================

    /** JPanel whose maximum height equals its preferred height (no vertical stretching in BoxLayout). */
    private static class HPanel extends JPanel {
        HPanel(LayoutManager lm) {
            super(lm);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
        }
    }

    /**
     * Read-only wrapping text area. Swing's JTextArea reports a wrong height until it knows its
     * width; this version measures its height for the width it was actually given and
     * re-validates when that width changes, so text never clips or leaves gaps.
     */
    private static class WrapText extends JTextArea {
        private int lastWidth = 0;

        WrapText(String text) {
            super(text);
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    if (getWidth() != lastWidth) {
                        lastWidth = getWidth();
                        SwingUtilities.invokeLater(WrapText.this::revalidate);
                    }
                }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            int w = lastWidth > 0 ? lastWidth : 400;
            Insets in = getInsets();
            View root = getUI().getRootView(this);
            root.setSize(Math.max(1, w - in.left - in.right), 0);
            int h = (int) Math.ceil(root.getPreferredSpan(View.Y_AXIS)) + in.top + in.bottom;
            return new Dimension(100, h);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(0, getPreferredSize().height);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
        }
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
            return true; // Locks panel width to 100% of viewport
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