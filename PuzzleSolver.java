import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import aima.core.agent.Action;
import aima.core.agent.impl.DynamicAction;
import aima.core.search.framework.ActionsFunction;
import aima.core.search.framework.EvaluationFunction;
import aima.core.search.framework.GoalTest;
import aima.core.search.framework.HeuristicFunction;
import aima.core.search.framework.Node;
import aima.core.search.framework.Problem;
import aima.core.search.framework.ResultFunction;

public class PuzzleSolver {

	private static final Results resultFunction = new Results();
	private static final Actions actionsFunction = new Actions();
	private static final Goal goal = new Goal();
	private static final Heuristic heuristic = new Heuristic();
	private static final RecursiveBestFirstSearchEvaluationFunction ef = new RecursiveBestFirstSearchEvaluationFunction(heuristic);

	public static void main(String[] args)
	{
		//Create an initial state
		File file = new File("puzzle.txt"); //Create the file to read the puzzle from
		List<Integer> initialState = new ArrayList<Integer>();
		try {
			@SuppressWarnings("resource")
			Scanner fileReader = new Scanner(file);
			while (fileReader.hasNext())
			{
				initialState.add(fileReader.nextInt());
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		Board newBoard = new Board(initialState); //Create a new board with the initial state
		//Create a problem with the board, the list of available actions, what each action does, and the goal
		Problem problem = new Problem(newBoard, actionsFunction, resultFunction, goal); 
		//Create a new class to search for solution
		RecursiveBestFirstSearch search = new RecursiveBestFirstSearch(ef);
		try {
			//Try to search for solution
			List<Action> solution = search.search(problem);
			System.out.println("Solution of length " + solution.size() + "\n");
			for (Action i : solution) //Solution found, print solution
			{
				System.out.println(i.toString());
			}
		} catch (Exception e) {
			System.out.println("Error: Can't find solution with given memory constraints.");
		}
	}
	//Action function holds information about what actions are available
	public static class Actions implements ActionsFunction
	{
		@Override
		public Set<Action> actions(Object tempState) {
			Board board = (Board) tempState; //Cast arg as board
			Set<Action> possibleActions = new LinkedHashSet<Action>(); //Holds list of possible actions
			int posOf0 = board.getPosOf0(); //Get position of blank spot
			if ((posOf0 % 3) != 0){
				possibleActions.add(Board.LEFT);
			}
			if ((posOf0 % 3) != 2) {
				possibleActions.add(Board.RIGHT);
			}
			if ((posOf0 / 3) != 2) {
				possibleActions.add(Board.DOWN);
			}
			if ((posOf0 / 3) != 0) {
				possibleActions.add(Board.UP);
			}
			return possibleActions; //Return list of possible actions
		}

		public int getPosOf0(List<Integer> state) //Get position of blank spot
		{
			for (int i = 0; i < state.size(); i++)
			{
				if (state.get(i) == 0) return i;			
			}
			return -1;
		}

	}
	//Returns a state with some action applied to some previous state
	public static class Results implements ResultFunction
	{
		@Override
		public Object result(Object tempState, Action action) {
			Board board = (Board) tempState; //Cast arg as board
			//Create new board with the action applied and return it
			if (action.equals(Board.LEFT))
			{
				Board newBoard = new Board(board);
				newBoard.moveLeft();
				return newBoard;
			}
			else if (action.equals(Board.RIGHT))
			{
				Board newBoard = new Board(board);
				newBoard.moveRight();
				return newBoard;
			}
			else if (action.equals(Board.UP))
			{
				Board newBoard = new Board(board);
				newBoard.moveUp();
				return newBoard;
			}
			else if (action.equals(Board.DOWN))
			{
				Board newBoard = new Board(board);
				newBoard.moveDown();
				return newBoard;
			}
			return tempState;
		}

	}
	//Information about the board state
	public static class Board
	{
		//Possible actions
		public static Action LEFT = new DynamicAction("Left");
		public static Action RIGHT = new DynamicAction("Right");
		public static Action UP = new DynamicAction("Up");
		public static Action DOWN = new DynamicAction("Down");
		//Finalized goal state
		@SuppressWarnings("serial")
		private static final List<Integer> goalState = new ArrayList<Integer>(){{
			for (int i = 0; i < 9; i++) add(i);
		}};
		private ArrayList<Integer> states; //Holds the state the board is in

		public Board(List<Integer> states)
		{
			this.states = (ArrayList<Integer>) states;
		}

		public Board()
		{
			states = new ArrayList<Integer>();
			for (Integer i : goalState)
			{
				states.add(i);
			}
		}

		public Board(Board board)
		{
			states = new ArrayList<Integer>();
			for (Integer i : board.getState())
			{
				states.add(i);
			}
		}

		public ArrayList<Integer> getState() //Return states
		{
			return states;
		}

		public int getPosOf0() //Return position of blank
		{
			for (int i = 0; i < states.size(); i++)
			{
				if (states.get(i) == 0) return i;			
			}
			return -1;
		}

		public int getPos(int target) //Not used
		{
			for (int i = 0; i < 9; i++)
			{
				if (states.get(i) == target) return i;
			}
			return -1;
		}

		public int getXLoc(int index) //Return X location of target
		{
			for (int i = 0; i < 9; i++)
			{
				if (states.get(i) == index) return i % 3;
			}
			return -1;
		}

		public int getYLoc(int index) //Return Y location of target
		{
			for (int i = 0; i < 9; i++)
			{
				if (states.get(i) == index) return i / 3;
			}
			return -1;
		}

		public void moveDown() //Performs actions
		{
			int oldPos = getPosOf0();
			int newPos = oldPos + 3;
			int oldValue = states.get(newPos);
			states.set(newPos, 0);
			states.set(oldPos, oldValue);
		}

		public void moveUp()
		{
			int oldPos = getPosOf0();
			int newPos = oldPos - 3;
			int oldValue = states.get(newPos);
			states.set(newPos, 0);
			states.set(oldPos, oldValue);
		}

		public void moveLeft()
		{
			int oldPos = getPosOf0();
			int newPos = oldPos - 1;
			int oldValue = states.get(newPos);
			states.set(newPos, 0);
			states.set(oldPos, oldValue);
		}

		public void moveRight()
		{
			int oldPos = getPosOf0();
			int newPos = oldPos + 1;
			int oldValue = states.get(newPos);
			states.set(newPos, 0);
			states.set(oldPos, oldValue);
		}
	}
	//Tests if goal state is reached
	public static class Goal implements GoalTest
	{

		@Override
		public boolean isGoalState(Object state) {
			Board board = (Board) state;
			if (board.getState().equals(Board.goalState)) return true;
			else return false;
		}

	}
	//Evaluation function for board, implements Manhattan block heuristic
	public static class RecursiveBestFirstSearchEvaluationFunction implements EvaluationFunction
	{

		private HeuristicFunction hf = null;

		public RecursiveBestFirstSearchEvaluationFunction(HeuristicFunction hf) {
			this.hf = hf;
		}

		@Override
		public double f(Node n) {
			// f(n) = h(n)
			return hf.h(n.getState());
		}

	}
	//Manhattan block heuristic
	public static class Heuristic implements HeuristicFunction
	{

		@Override
		public double h(Object state) {
			Board board = (Board) state;
			int value = 0;
			for (Integer i : board.getState())
			{
				if (i != 0) value += evaluate(i, board.getXLoc(i), board.getYLoc(i));
			}
			return value; //Total heuristic value
		}
		//Calculate heuristic for a specific value
		public int evaluate(int target, int xLocation, int yLocation)
		{
			int tempValue = 0;
			tempValue += Math.abs(xLocation - (target % 3));
			tempValue += Math.abs(yLocation - (target / 3));
			return tempValue;
		}

	}
}
