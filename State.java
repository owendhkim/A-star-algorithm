package edu.iastate.cs472.proj1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 *  
 * @author
 *
 */


/**
 * This class represents a board configuration in the 8-puzzle.  Only the initial configuration is 
 * generated by a constructor, while intermediate configurations will be generated via calling
 * the method successorState().  State objects will form two circular doubly-linked lists OPEN and 
 * CLOSED, which will be used by the A* algorithm to search for a path from a given initial board
 * configuration to the final board configuration below: 
 * 
 *  1 2 3 
 *  8   4
 *  7 6 5
 *  {{1,2,3},{8,0,4},{7,6,5}}
 *
 * The final configuration (i.e., the goal state) above is not explicitly represented as an object 
 * of the State class. 
 */

public class State implements Cloneable, Comparable<State>
{
	public int[][] board; 		// configuration of tiles 
	
	public State previous;    	// previous node on the OPEN/CLOSED list
	public State next; 			// next node on the OPEN/CLOSED list
	public State predecessor; 	// predecessor node on the path from the initial state 
	
	public Move move;           // the move that generated this state from its predecessor
	public int numMoves; 	    // number of moves from the initial state to this state

	public static Heuristic heu; // heuristic used. shared by all the states. 
	
	private int numMismatchedTiles = -1;    // number of mismatched tiles between this state 
	                                        // and the goal state; negative if not computed yet.
	private int ManhattanDistance = -1;     // Manhattan distance between this state and the 
	                                        // goal state; negative if not computed yet. 
	private int numSingleDoubleMoves = -1;  // number of single and double moves with each double 
										    // move counted as one; negative if not computed yet. 

	
	/**
	 * Constructor (for the initial state).  
	 * 
	 * It takes a 2-dimensional array representing an initial board configuration. The empty 
	 * square is represented by the number 0.  
	 * 
	 *     a) Initialize all three links previous, next, and predecessor to null.  
	 *     b) Set move to null and numMoves to zero.
	 *
	 * @param board
	 * @throws IllegalArgumentException		if board is not a 3X3 array or its nine entries are 
	 * 										not respectively the digits 0, 1, ..., 8. 
	 */
    public State (int[][] board) throws IllegalArgumentException
    {
		this.board = board;
		this.previous = null;
		this.next = null;
		this.predecessor = null;
		this.move = null;
		this.numMoves = 0;
	}
    
    
    /**
     * Constructor (for the initial state) 
     * 
     * It takes a state from an input file that has three rows, each containing three digits 
     * separated by exactly one blank.  Every row starts with a digit. The nine digits are 
     * from 0 to 8 with no duplicates.  
     * 
     * Do the same initializations as for the first constructor. 
     * 
     * @param inputFileName
     * @throws FileNotFoundException
     * @throws IllegalArgumentException  if the file content does not meet the above requirements. 
     */
    public State (String inputFileName) throws FileNotFoundException, IllegalArgumentException
    {
		int[][] board = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
		try
		{
			File f = new File(inputFileName);
			Scanner s = new Scanner(f);
			while (s.hasNextInt())
			{
				for (int i = 0; i < board.length; i++)
				{
					for (int j = 0; j < board[0].length; j++)
					{
						board[i][j] = s.nextInt();
					}
				}
			}
		}
		catch (IllegalArgumentException | FileNotFoundException e)
		{
			System.out.println("Exception caught: " + e.getMessage());
			e.printStackTrace();
		}

		this.previous = null;
		this.next = null;
		this.predecessor =null;
		this.move = null;
		this.numMoves = 0;
	}
    
    
    /**
     * Generate the successor state resulting from a given move.  Throw an exception if the move 
     * cannot be executed.  Besides setting the array board[][] properly, you also need to do the 
     * following:
     * 
     *     a) set the predecessor of the successor state to this state;
     *     b) set the private instance variable move of the successor state to the parameter m; 
     *     c) Set the links next and previous to null;
     *     d) Set the variable numMoves for the successor state to this.numMoves + 1. 
     * 
     * @param m  one of the moves LEFT, RIGHT, UP, DOWN, DBL_LEFT, DBL_RIGHT, DBL_UP, and DBL_DOWN
	 *           LEFT -> move 0 block to the right
	 *           RIGHT -> move 0 block to the left
	 *           UP -> move 0 block down
	 *           DOWN -> move 0 block up
     * @return null  			if the successor state is this.predecessor
     *         successor state  otherwise 
     * @throws IllegalArgumentException if LEFT when the empty square is in the right column, or
     *                                  if RIGHT when the empty square is in the left column, or
     *                                  if UP when the empty square is in the bottom row, or 
     *                                  if DOWN when the empty square is in the top row, or
     *                                  if DBL_LEFT when the empty square is not in the left column, or 
     *                                  if DBL_RIGHT when the empty square is not in the right column, or 
     *                                  if DBL_UP when the empty square is not in the top row, or 
     *                                  if DBL_DOWN when the empty square is not in the bottom row. 
     */                                  
    public State successorState(Move m) throws IllegalArgumentException
    {
		int empty_row = 0;
		int empty_col = 0;
		for (int i = 0; i < this.board.length; i++)
		{
			for (int j = 0; j < this.board[0].length; j++)
			{
				if (this.board[i][j] == 0)
				{
					empty_row = i;
					empty_col = j;
				}
			}
		}
		switch (m)
		{
			case LEFT:
				if (empty_col == 2)
				{
					throw new IllegalArgumentException();
				}
				else
				{
					State s = (State) this.clone();
					s.predecessor = this;
					s.move = m;
					s.numMoves = this.numMoves + 1;
					s.board[empty_row][empty_col] = s.board[empty_row][empty_col + 1];
					s.board[empty_row][empty_col + 1] = 0;
					if (this.predecessor != null && this.predecessor.equals(s))
					{
						return null;
					}
					else
					{
						return s;
					}
				}
			case RIGHT:
				if (empty_col == 0)
				{
					throw new IllegalArgumentException();
				}
				else
				{
					State s = (State) this.clone();
					s.predecessor = this;
					s.move = m;
					s.numMoves = this.numMoves + 1;
					s.board[empty_row][empty_col] = s.board[empty_row][empty_col - 1];
					s.board[empty_row][empty_col - 1] = 0;
					if (this.predecessor != null && this.predecessor.equals(s))
					{
						return null;
					}
					else
					{
						return s;
					}				}
			case UP:
				if (empty_row == 2)
				{
					throw new IllegalArgumentException();
				}
				else
				{
					State s = (State) this.clone();
					s.predecessor = this;
					s.move = m;
					s.numMoves = this.numMoves + 1;
					s.board[empty_row][empty_col] = s.board[empty_row + 1][empty_col];
					s.board[empty_row + 1][empty_col] = 0;
					if (this.predecessor != null && this.predecessor.equals(s))
					{
						return null;
					}
					else
					{
						return s;
					}				}
			case DOWN:
				if (empty_row == 0)
				{
					throw new IllegalArgumentException();
				}
				else
				{
					State s = (State) this.clone();
					s.predecessor = this;
					s.move = m;
					s.numMoves = this.numMoves + 1;
					s.board[empty_row][empty_col] = s.board[empty_row - 1][empty_col];
					s.board[empty_row - 1][empty_col] = 0;
					if (this.predecessor != null && this.predecessor.equals(s))
					{
						return null;
					}
					else
					{
						return s;
					}
				}
			case DBL_LEFT:
				break;
			case DBL_RIGHT:
				break;
			case DBL_UP:
				break;
			case DBL_DOWN:
				break;
		}
		throw new IllegalArgumentException("Invalid move: " + m);
    }
    
        
    /**
     * Determines if the board configuration in this state can be rearranged into the goal configuration. 
     * According to the PowerPoint notes that introduce the 8-puzzle, we check if this state has an odd number 
     * of inversions. 
     */
    /**
     * 
     * @return true if the puzzle starting in this state can be rearranged into the goal state.
     */
    public boolean solvable() //done
    {
		int[] flat = new int[9];
		int idx = 0;
		int inv_c = 0;
		for (int i = 0; i < this.board.length; i++)
		{
			for (int j = 0; j < this.board[0].length; j++)
			{
				flat[idx] = this.board[i][j];
				idx++;
			}
		}
		for (int i = 0; i < flat.length; i++)
		{
			for (int j = i+1; j < flat.length; j++)
			{
				if (flat[i] > flat[j] && flat[i] != 0 && flat[j] != 0)
				{
					inv_c++;
				}
			}
		}
		if ((inv_c - 7) % 2 == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
    }
    
    
    /**
     * Check if this state is the goal state, namely, if the array board[][] stores the following contents: 
     * 
     * 		1 2 3 
     * 		8 0 4 
     * 		7 6 5 
     * 
     * @return
     */
    public boolean isGoalState() //done
    {
    	int[][] goal = {{1,2,3},{8,0,4},{7,6,5}};
    	return Arrays.deepEquals(this.board, goal);
    }
    
    
    /**
     * Write the board configuration according to the following format:
     * 
     *     a) Output row by row in three lines with no indentations.  
     *     b) Two adjacent tiles in each row have exactly one blank in between. 
     *     c) The empty square is represented by a blank.  
     *     
     * For example, 
     * 
     * 2   3
     * 1 8 4
     * 7 6 5
     * 
     */
    @Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.board.length; i++)
		{
			for (int j = 0; j < this.board[0].length; j++)
			{
				if (j != 0)
				{
					sb.append(" ");
				}
				if (this.board[i][j] == 0)
				{
					sb.append(" ");
				}
				else
				{
					sb.append(this.board[i][j]);
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}
    
    
    /**
     * Create a clone of this State object by copying over the board[][]. Set the links previous,
     * next, and predecessor to null. 
     * 
     * The method is called by SuccessorState(); 
     */
    @Override
    public Object clone()
    {
		int[][] deepcpy_board = new int[this.board.length][this.board[0].length];
		for (int i = 0; i < this.board.length; i++)
		{
			deepcpy_board[i] = Arrays.copyOf(this.board[i], this.board[i].length);
		}
		State s = new State(deepcpy_board);
		s.previous = null;
		s.next = null;
		s.predecessor = null;
    	return s;
    }
  

    /**
     * Compare this state with the argument state.  Two states are equal if their arrays board[][] 
     * have the same content.
     */
    @Override 
    public boolean equals(Object o) //done
    {
		State s2 = (State) o;
		return Arrays.deepEquals(this.board, s2.board);
    }
        
    
    /**
     * Evaluate the cost of this state as the sum of the number of moves from the initial state and 
     * the estimated number of moves to the goal state using the heuristic stored in the instance 
     * variable heu. 
     * 
     * If heu == TileMismatch, add up numMoves and the return values from computeNumMismatchedTiles().
     * If heu == MahattanDist, add up numMoves and the return values of computeMahattanDistance(). 
     * If heu == DoubleMoveHeuristic, add up numMoves and the return value of computeNumSingleDoubleMoves(). 
     * 
     * @return estimated number of moves from the initial state to the goal state via this state.
     * @throws IllegalArgumentException if heuristic is none of TileMismatch, MahattanDist, DoubleMoveHeuristic. 
     */
    public int cost() throws IllegalArgumentException //done
    {
		try
		{
			switch (heu)
			{
				case TileMismatch -> {return this.numMoves + this.computeNumMismatchedTiles();}
				case ManhattanDist -> {return this.numMoves + this.computeManhattanDistance();}
				case DoubleMoveHeuristic -> {return this.numMoves + this.computeNumSingleDoubleMoves();}
			}
		}
		catch (IllegalArgumentException e)
		{
			throw new IllegalArgumentException();
		}
        return 0;
    }

    
    /**
     * Compare two states by the cost. Let c1 and c2 be the costs of this state and the argument state s.
     * 
     * @return -1 if c1 < c2 
     *          0 if c1 = c2 
     *          1 if c1 > c2 
     *          
     * Call the method cost(). This comparison will be used in maintaining the OPEN list by the A* algorithm.
     */
    @Override
    public int compareTo(State s) //done
    {
		return Integer.compare(this.cost(), s.cost());
	}
    

    /**
     * Return the value of the private variable numMismatchedTiles if it is non-negative, and compute its 
     * value otherwise. 
     * 
     * @return the number of mismatched tiles between this state and the goal state. 
     */
	private int computeNumMismatchedTiles() //done
	{
		if(this.numMismatchedTiles >= 0)
		{
			return this.numMismatchedTiles;
		}
		else
		{
			int[][] goal = {{1,2,3},{8,0,4},{7,6,5}};
			int count = 0;
			for (int i = 0; i < this.board.length; i++)
			{
				for (int j = 0; j < this.board[0].length; j++)
				{
					if(goal[i][j] != this.board[i][j])
					{
						count++;
					}
				}
			}
			this.numMismatchedTiles = count;
			return count;
		}
	}

	
	/**
	 * Return the value of the private variable ManhattanDistance if it is non-negative, and compute its value 
	 * otherwise.
	 * 
	 * @return the Manhattan distance between this state and the goal state. 
	 */
	private int computeManhattanDistance() //done
	{
		if(this.ManhattanDistance >= 0)
		{
			return this.ManhattanDistance;
		}
		else
		{
			HashMap<Integer,int[]> hm = new HashMap<>();
			int[][] goal = {{1,2,3},{8,0,4},{7,6,5}};
			int count = 0;
			int sum = 0;

			for (int i = 0; i < this.board.length; i++)
			{
				for (int j = 0; j < this.board[0].length; j++)
				{
					int[] arr = {i,j};
					hm.put(goal[i][j],arr);
				}
			}

			for (int i = 0; i < this.board.length; i++)
			{
				for (int j = 0; j < this.board[0].length; j++)
				{
					if(this.board[i][j] != goal[i][j] && this.board[i][j] != 0)
					{
						int[] cord = hm.get(this.board[i][j]);
						int man_d = Math.abs(cord[0] - i) + Math.abs(cord[1] - j);
						sum = sum + man_d;
					}
				}
			}
			this.ManhattanDistance = sum;
			return sum;
		}
	}
	
	
	/**
	 * Return the value of the private variable numSingleDoubleMoves if it is non-negative, and compute its value 
	 * otherwise. 
	 * 
	 * @return the value of the private variable numSingleDoubleMoves that bounds from below the number of moves, 
	 *         single or double, which will take this state to the goal state.
	 */
	private int computeNumSingleDoubleMoves()
	{
		// TODO 
		return 0; 
	}
}
