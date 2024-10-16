package edu.iastate.cs472.proj1;

import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 *  
 * @author Owen Kim
 *
 */

public class EightPuzzle 
{
	/**
	 * This static method solves an 8-puzzle with a given initial state using three heuristics. The 
	 * first two, allowing single moves only, compare the board configuration with the goal configuration 
	 * by the number of mismatched tiles, and by the Manhattan distance, respectively.  The third 
	 * heuristic, designed by yourself, allows double moves and must be also admissible.  The goal 
	 * configuration set for all puzzles is
	 * 
	 * 			1 2 3
	 * 			8   4
	 * 			7 6 5
	 * 
	 * @param s0
	 * @return a string specified in the javadoc below
	 */
	public static String solve8Puzzle(State s0)
	{
		if(!s0.solvable())
		{
			return "No solution exists for the following initial state:" + "\n\n" + s0.toString();
		}
		
		// 1) If there exists no solution, return a message that starts with "No solution 
		//    exists for the following initial state:" and follows with a blank line and 
		//    then what would be the output from a call s0.toString(). See the end of 
		//    Section 6 in the project description for an example. 
				

		// 2) Otherwise, solve the puzzle with the three heuristics.  The two solutions generated by
		//    the first two heuristics may be different but must have the same length for optimality. 
		
		Heuristic h[] = {Heuristic.TileMismatch, Heuristic.ManhattanDist, Heuristic.DoubleMoveHeuristic };
		String [] moves = new String[3]; 
		
		for (int i = 0; i < 3; i++)
		{
			moves[i] = AStar(s0, h[i]); 
		}
		
		// 3) Combine the three solution strings into one that would print out in the 
		//    output format specified in Section 6 of the project description.
		
		return moves[0] + "\n\n" + moves[1] + "\n\n" + moves[2];
	}

	
	/**
	 * This method implements the A* algorithm to solve the 8-puzzle with an input initial state s0. 
	 * The algorithm implementation is described in Section 3 of the project description. 
	 * 
	 * Precondition: the puzzle is solvable with the initial state s0.
	 * 
	 * @param s0  initial state
	 * @param h   heuristic 
	 * @return    solution string 
	 */
	public static String AStar(State s0, Heuristic h)
	{
		// Initialize the two lists used by the algorithm. 
		OrderedStateList OPEN = new OrderedStateList(h, true); 
		OrderedStateList CLOSE = new OrderedStateList(h, false);
		Move[] moves = new Move[0];
		if(h == Heuristic.TileMismatch || h == Heuristic.ManhattanDist)
		{
			moves = new Move[]{Move.LEFT, Move.RIGHT, Move.UP, Move.DOWN};
		}
		else if (h == Heuristic.DoubleMoveHeuristic)
		{
			moves = Move.values();
		}

		// Implement the algorithm described in Section 3 to solve the puzzle. 
		// Once a goal state s is reached, call solutionPath(s) and return the solution string.
		OPEN.addState(s0);
		while(OPEN.size() > 0)
		{
			State popped = OPEN.remove();
			if(popped.isGoalState())
			{
				return solutionPath(popped);
			}
			else
			{
				CLOSE.addState(popped);
				for (Move m : moves)
				{
					State child = popped.successorState(m);
					if(child != null)
					{
						child.cost();
						State old_open = OPEN.findState(child);
						State old_close = CLOSE.findState(child);
						if(OPEN.findState(child) == null && CLOSE.findState(child) == null)
						{
							OPEN.addState(child);
						}
						if(old_open != null)
						{
							if(old_open.compareTo(child) == 1)
							{
								child.predecessor = popped;
							}
						}
						if(old_close != null)
						{
							if(old_close.compareTo(child) == 1)
							{
								child.predecessor = popped;
								OPEN.addState(child);
								CLOSE.removeState(child);
							}
						}
					}
				}
			}
		}
		return "OPEN list empty, exit with failure";
	}
	
	
	
	/**
	 * From a goal state, follow the predecessor link to trace all the way back to the initial state. 
	 * Meanwhile, generate a string to represent board configurations in the reverse order, with 
	 * the initial configuration appearing first. Between every two consecutive configurations 
	 * is the move that causes their transition. A blank line separates a move and a configuration.  
	 * In the string, the sequence is preceded by the total number of moves and a blank line. 
	 * 
	 * See Section 6 in the projection description for an example. 
	 * 
	 * Call the toString() method of the State class. 
	 * 
	 * @param goal
	 * @return
	 */
	private static String solutionPath(State goal)
	{
		State cur = goal;
		StringBuilder sb = new StringBuilder();
		int count = 0;
		String h = "";
		if(State.heu == Heuristic.TileMismatch)
		{
			h = "number of mismatched tiles";
		}
		else if (State.heu == Heuristic.ManhattanDist)
		{
			h = "the Manhattan distance";
		}
		else
		{
			h = "double moves allowed";
		}
		while(cur.predecessor != null)
		{
			sb.insert(0,"\n\n" + cur.toString());
			sb.insert(0,"\n" + cur.move);
			cur = cur.predecessor;
			count++;
		}
		sb.insert(0,cur.toString());
		sb.insert(0,count + " moves in total (heuristic: " + h + ")\n\n");
		return sb.toString();
	}
}
