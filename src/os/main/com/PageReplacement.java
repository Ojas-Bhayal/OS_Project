package os.main.com;

import java.util.Scanner;
import java.util.ArrayList;

public class PageReplacement {
	public static void main(String[] args) {

		System.out.println(
		        "Loaded PageReplacement from: " +
		        PageReplacement.class
		                .getProtectionDomain()
		                .getCodeSource()
		                .getLocation()
		);
		
		Scanner sc = new Scanner(System.in);

		System.out.print("Enter reference string: ");
		String input = sc.nextLine().trim();

		if (input.isEmpty()) {
		    System.out.println("Reference string cannot be empty.");
		    return;
		}

		System.out.print("Enter number of frames: ");
		if (!sc.hasNextInt()) {
		    System.out.println("Frame count must be a valid integer.");
		    return;
		}
		int frames = sc.nextInt();
		if (frames <= 0) {
		    System.out.println("Number of frames must be greater than 0.");
		    return;
		}

		String[] values = input.split("[,\\s]+");
		int[] pages = new int[values.length];

		for (int i = 0; i < values.length; i++) {
		    try {
		        pages[i] = Integer.parseInt(values[i]);
		    } catch (NumberFormatException e) {
		        System.out.println("Invalid page number: '" + values[i] + "'. Only integers allowed.");
		        return;
		    }

		    if (pages[i] < 0) {
		        System.out.println("Page numbers must be non-negative.");
		        return;
		    }
		}

		/*
		System.out.println("Reference string:");

		for (int page : pages) {
			System.out.print(page + " ");
		}

		System.out.println();
		System.out.println("Number of frames: " + frames);
		*/
		
		Result fifoResult = fifo(pages, frames);
		Result lruResult = lru(pages, frames);
		Result optimalResult = optimal(pages, frames);
		
		System.out.println("\n==========================================================");
		System.out.println("             PAGE REPLACEMENT COMPARISON");
		System.out.println("==========================================================");

		System.out.printf("%-15s %-10s %-10s %-12s %-12s%n",
		        "Algorithm", "Hits", "Faults", "Hit Ratio", "Fault Ratio");

		System.out.println("----------------------------------------------------------");

		double fifoHitRatio = (double) fifoResult.pageHits / pages.length * 100;
		double fifoFaultRatio = (double) fifoResult.pageFaults / pages.length * 100;

		double lruHitRatio = (double) lruResult.pageHits / pages.length * 100;
		double lruFaultRatio = (double) lruResult.pageFaults / pages.length * 100;

		double optimalHitRatio = (double) optimalResult.pageHits / pages.length * 100;
		double optimalFaultRatio = (double) optimalResult.pageFaults / pages.length * 100;

		System.out.printf("%-15s %-10d %-10d %-11.2f%% %-11.2f%%%n",
		        "FIFO",
		        fifoResult.pageHits,
		        fifoResult.pageFaults,
		        fifoHitRatio,
		        fifoFaultRatio);

		System.out.printf("%-15s %-10d %-10d %-11.2f%% %-11.2f%%%n",
		        "LRU",
		        lruResult.pageHits,
		        lruResult.pageFaults,
		        lruHitRatio,
		        lruFaultRatio);

		System.out.printf("%-15s %-10d %-10d %-11.2f%% %-11.2f%%%n",
		        "Optimal",
		        optimalResult.pageHits,
		        optimalResult.pageFaults,
		        optimalHitRatio,
		        optimalFaultRatio);

		System.out.println("----------------------------------------------------------");
		System.out.println("Total References : " + pages.length);
		System.out.println("Number of Frames  : " + frames);
		System.out.println("==========================================================");
		
		
	}
	
	// FIFO
	static Result fifo(int[] pages, int frameCount) {

	    int[] memory = new int[frameCount];

	    // -1 means the frame is empty
	    for (int i = 0; i < frameCount; i++) {
	        memory[i] = -1;
	    }

	    int pointer = 0;
	    int pageFaults = 0;
	    int pageHits = 0;
	    ArrayList<SimulationStep> steps =
	            new ArrayList<>();

	    System.out.println("\n========== FIFO ==========");

	    for (int index = 0; index < pages.length; index++) {

	        int page = pages[index];

	        boolean found = false;

	        // Check whether page is already in memory
	        for (int i = 0; i < frameCount; i++) {
	            if (memory[i] == page) {
	                found = true;
	                break;
	            }
	        }

	        if (found) {

	            // Page already exists
	            pageHits++;

	            System.out.println("Page " + page + " -> HIT");

	        } else {

	            // Page is not in memory
	            pageFaults++;

	            memory[pointer] = page;

	            System.out.println("Page " + page + " -> FAULT");

	            pointer = (pointer + 1) % frameCount;
	        }

	        // Display current frames
	        System.out.print("Frames: ");

	        for (int i = 0; i < frameCount; i++) {
	            if (memory[i] == -1) {
	                System.out.print("- ");
	            } else {
	                System.out.print(memory[i] + " ");
	            }
	        }

	        System.out.println();
	        
	        steps.add(
	                new SimulationStep(
	                        index + 1,
	                        page,
	                        found ? "HIT" : "FAULT",
	                        memory
	                )
	        );
	    }

	    System.out.println("---------------------------");
	    System.out.println("Page Hits   : " + pageHits);
	    System.out.println("Page Faults : " + pageFaults);

	    return new Result(
	            pageFaults,
	            pageHits,
	            steps
	    );
	}
	
	// LRU
	static Result lru(int[] pages, int frameCount) {

	    int[] memory = new int[frameCount];

	    // -1 means the frame is empty
	    for (int i = 0; i < frameCount; i++) {
	        memory[i] = -1;
	    }

	    int pageFaults = 0;
	    int pageHits = 0;
	    ArrayList<SimulationStep> steps =
	            new ArrayList<>();

	    // Stores the pages in order of usage.
	    // First = least recently used
	    // Last = most recently used
	    java.util.ArrayList<Integer> recent = new java.util.ArrayList<>();

	    System.out.println("\n========== LRU ==========");

	    for (int index = 0; index < pages.length; index++) {

	        int page = pages[index];

	        boolean found = false;

	        // Check whether page is already in memory
	        for (int i = 0; i < frameCount; i++) {

	            if (memory[i] == page) {
	                found = true;
	                break;
	            }
	        }

	        if (found) {

	            // Page is already in memory
	            pageHits++;

	            System.out.println("Page " + page + " -> HIT");

	            // Remove page from its old position
	            recent.remove(Integer.valueOf(page));

	            // Add it at the end because it was just used
	            recent.add(page);

	        } else {

	            // Page is not in memory
	            pageFaults++;

	            System.out.println("Page " + page + " -> FAULT");

	            // Check for an empty frame
	            int emptyFrame = -1;

	            for (int i = 0; i < frameCount; i++) {

	                if (memory[i] == -1) {
	                    emptyFrame = i;
	                    break;
	                }
	            }

	            if (emptyFrame != -1) {

	                // There is an empty frame
	                memory[emptyFrame] = page;

	            } else {

	                // No empty frame.
	                // Remove the least recently used page.
	                int lruPage = recent.get(0);

	                // Find that page in memory
	                for (int i = 0; i < frameCount; i++) {

	                    if (memory[i] == lruPage) {
	                        memory[i] = page;
	                        break;
	                    }
	                }

	                // Remove old page from usage list
	                recent.remove(0);
	            }

	            // New page is now the most recently used
	            recent.add(page);
	        }

	        // Display current frames
	        System.out.print("Frames: ");

	        for (int i = 0; i < frameCount; i++) {

	            if (memory[i] == -1) {
	                System.out.print("- ");
	            } else {
	                System.out.print(memory[i] + " ");
	            }
	        }

	        System.out.println();
	        
	        steps.add(
	                new SimulationStep(
	                        index + 1,
	                        page,
	                        found ? "HIT" : "FAULT",
	                        memory
	                )
	        );
	    }

	    System.out.println("---------------------------");
	    System.out.println("Page Hits   : " + pageHits);
	    System.out.println("Page Faults : " + pageFaults);

	    return new Result(
	            pageFaults,
	            pageHits,
	            steps
	    );
	}
	
	// OPTIMAL
	static Result optimal(int[] pages, int frameCount) {

	    int[] memory = new int[frameCount];

	    // -1 means the frame is empty
	    for (int i = 0; i < frameCount; i++) {
	        memory[i] = -1;
	    }

	    int pageFaults = 0;
	    int pageHits = 0;
	    ArrayList<SimulationStep> steps =
	            new ArrayList<>();

	    System.out.println("\n========== OPTIMAL ==========");

	    for (int index = 0; index < pages.length; index++) {

	        int page = pages[index];

	        boolean found = false;

	        // Check if page is already in memory
	        for (int i = 0; i < frameCount; i++) {

	            if (memory[i] == page) {
	                found = true;
	                break;
	            }
	        }

	        if (found) {

	            // Page already exists
	            pageHits++;

	            System.out.println("Page " + page + " -> HIT");

	        } else {

	            // Page fault
	            pageFaults++;

	            System.out.println("Page " + page + " -> FAULT");

	            // First check whether there is an empty frame
	            int emptyFrame = -1;

	            for (int i = 0; i < frameCount; i++) {

	                if (memory[i] == -1) {
	                    emptyFrame = i;
	                    break;
	                }
	            }

	            if (emptyFrame != -1) {

	                // There is an empty frame
	                memory[emptyFrame] = page;

	            } else {

	                // All frames are full.
	                // Find the page that will be used farthest in the future.

	                int replaceIndex = -1;
	                int farthest = -1;

	                for (int i = 0; i < frameCount; i++) {

	                    int nextUse = Integer.MAX_VALUE;

	                    // Search for the next occurrence
	                    // of memory[i] in the future.
	                    for (int j = index + 1; j < pages.length; j++) {

	                        if (pages[j] == memory[i]) {
	                            nextUse = j;
	                            break;
	                        }
	                    }

	                    // If this page is never used again,
	                    // replace it immediately.
	                    if (nextUse == Integer.MAX_VALUE) {
	                        replaceIndex = i;
	                        break;
	                    }

	                    // Otherwise choose the page
	                    // whose next use is farthest away.
	                    if (nextUse > farthest) {
	                        farthest = nextUse;
	                        replaceIndex = i;
	                    }
	                }

	                memory[replaceIndex] = page;
	            }
	        }

	        // Display current frames
	        System.out.print("Frames: ");

	        for (int i = 0; i < frameCount; i++) {

	            if (memory[i] == -1) {
	                System.out.print("- ");
	            } else {
	                System.out.print(memory[i] + " ");
	            }
	        }

	        System.out.println();
	        
	        steps.add(
	                new SimulationStep(
	                        index + 1,
	                        page,
	                        found ? "HIT" : "FAULT",
	                        memory
	                )
	        );
	    }

	    System.out.println("---------------------------");
	    System.out.println("Page Hits   : " + pageHits);
	    System.out.println("Page Faults : " + pageFaults);

	    return new Result(
	            pageFaults,
	            pageHits,
	            steps
	    );
	}
	
	static class Result {

	    int pageFaults;
	    int pageHits;

	    ArrayList<SimulationStep> steps;

	    Result(
	            int pageFaults,
	            int pageHits,
	            ArrayList<SimulationStep> steps
	    ) {
	        this.pageFaults = pageFaults;
	        this.pageHits = pageHits;
	        this.steps = steps;
	    }
	}
	
	static class SimulationStep {

	    int stepNumber;
	    int page;
	    String result;
	    int[] frames;

	    SimulationStep(
	            int stepNumber,
	            int page,
	            String result,
	            int[] frames
	    ) {
	        this.stepNumber = stepNumber;
	        this.page = page;
	        this.result = result;
	        this.frames = frames.clone();
	    }
	}
}
