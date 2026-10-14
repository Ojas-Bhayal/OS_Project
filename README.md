# Page Replacement Algorithm Comparator

A Java (Swing) application that teaches, simulates and compares three classic **page replacement algorithms** from Operating Systems: **FIFO**, **LRU** and **Optimal (OPT/MIN)**.

Enter a reference string and a number of frames, and the program shows how many page hits and page faults each algorithm produces, lets you step through each algorithm one reference at a time, and puts the results side by side.

## Features

- **Theory section**: built-in notes on virtual memory, demand paging, page faults, performance metrics (hit ratio, fault ratio, effective access time), the three algorithms (pros, cons, data structures, best use), a worked example, locality of reference, Belady's anomaly, thrashing and the working-set model.
- **Input panel**: reference string (space- or comma-separated), frame count (1 to 16), and **Load Example**, **Run Simulation** and **Clear** buttons.
- **Step-by-step simulator**: choose an algorithm, then move with **First / Previous / Next / Last / Reset**. Shows the current frame contents, the requested page, a HIT/FAULT badge and a detailed execution table.
- **Comparator**: summary cards plus a results table with hits, faults, hit ratio and fault ratio for all three algorithms.
- **Console version**: `PageReplacement` can also be run in the terminal and prints a comparison table.

## Algorithms

| Algorithm | Victim selection | Needs future knowledge | Belady's anomaly |
|-----------|------------------|------------------------|------------------|
| FIFO | Page that has been in memory the longest | No | Possible |
| LRU | Page not used for the longest time | No | No |
| Optimal | Page whose next use is farthest in the future (or never) | Yes | No |

Optimal is a theoretical benchmark: for any input, its fault count is never higher than LRU's or FIFO's.

### Example

Reference string `7 0 1 2 0 3 0 4 2 3` with 3 frames (the built-in example):

| Algorithm | Faults | Hits |
|-----------|--------|------|
| FIFO | 9 | 1 |
| LRU | 8 | 2 |
| Optimal | 6 | 4 |

## Project Structure

```
OS_Project/
├── src/
│   ├── module-info.java                  # Module PageReplacementComparator (requires java.desktop)
│   └── os/main/com/
│       ├── PageReplacement.java          # Algorithms (FIFO, LRU, Optimal) + console program
│       └── PageReplacementGUI.java       # Swing GUI: theory, input, simulator, comparator
├── bin/                                  # Compiled classes (Eclipse output folder)
├── .classpath, .project, .settings/      # Eclipse project files
```

## Requirements

- A JDK with Swing support. The Eclipse configuration targets **JavaSE-25**; the code itself uses only standard Java features, so an earlier recent JDK should also work if you adjust the project's Java level.
- Optional: Eclipse IDE (the project is an Eclipse Java project).

## How to Run

### Eclipse

1. **File → Import → General → Existing Projects into Workspace** and select this folder.
2. Right-click `PageReplacementGUI.java` → **Run As → Java Application** for the GUI, or `PageReplacement.java` for the console version.

### Command line

From the project root:

```bash
# Compile
mkdir -p bin
javac -d bin src/module-info.java src/os/main/com/*.java

# Run the GUI
java -p bin -m PageReplacementComparator/os.main.com.PageReplacementGUI

# Run the console version
java -p bin -m PageReplacementComparator/os.main.com.PageReplacement
```

## Usage

### GUI

1. Type a reference string such as `7 0 1 2 0 3 0 4 2 3` (or click **Load Example**).
2. Set the number of frames.
3. Click **Run Simulation**.
4. Use the **Simulator** to step through FIFO, LRU or Optimal, and read the **Comparator** table for the final numbers.

### Console

```
Enter reference string: 7 0 1 2 0 3 0 4 2 3
Enter number of frames: 3
```

The program prints each step for every algorithm, followed by a comparison table of hits, faults, hit ratio and fault ratio.

## Input Rules

- Page numbers must be **non-negative integers**, separated by spaces or commas.
- The reference string cannot be empty.
- The number of frames must be at least 1 (the GUI allows 1 to 16).

Invalid input shows an error dialog in the GUI or a message in the console.

## Metrics

- Hit Ratio = (Page Hits / Total References) × 100
- Fault Ratio = (Page Faults / Total References) × 100
- Effective Access Time = (1 − p) × ma + p × fault_time

## Author

Ojas Bhayal

## License

No license has been specified yet.
