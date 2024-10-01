package edu.iastate.cs472.proj1;

import java.io.FileNotFoundException;

public class Main
{
    public static void main(String[] args) throws FileNotFoundException {
        int[][] g_board = {{1,2,3},{8,0,4},{7,6,5}};
        int[][] i_board = {{4,1,2},{5,0,3},{8,6,7}};
        State goal = new State(g_board);
        State init = new State(i_board);
        State.heu = Heuristic.TileMismatch;
        System.out.println(init.cost());
//
//        State s1 = init.successorState(Move.LEFT);
//        State s2 = init.successorState(Move.RIGHT);
//        State s3 = init.successorState(Move.UP);
//        State s4 = init.successorState(Move.DOWN);
//
//        System.out.println(s1.toString());
//        System.out.println(s2.toString());
//        System.out.println(s3.toString());
//        System.out.println(s4.toString());

//        State s5 = new State(new int[][]{{2, 0, 3}, {1, 8, 4}, {7, 6, 5}});
//        State s6 = new State(new int[][]{{2, 8, 1}, {7, 5, 3}, {6, 0, 4}});
//        StateComparator sc = new StateComparator();
//        System.out.println(sc.compare(s5,s6));
    }
}
