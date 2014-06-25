import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import aima.core.agent.Action;
import aima.core.agent.impl.DynamicAction;
import aima.core.search.framework.ActionsFunction;
import aima.core.search.framework.ResultFunction;


public class PuzzleGenerator {

	public static void main(String[] args)
	{
		File file = new File("puzzle.txt");
		try {
			PrintWriter writer = new PrintWriter(file);
			List<Integer> initialState = generatePuzzle();
			for(int i = 0; i < initialState.size(); i += 3)
			{
				for (int j = i; j < i + 3; j++)
				{
					if ((j + 1) == i + 3)
					{
						writer.print(initialState.get(j) + "\n");
					}
					else writer.print(initialState.get(j) + "\t");
				}
				writer.println();
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<Integer> generatePuzzle()
	{
		Board randomBoard = new Board();
		Actions possibleActions = new Actions();
		Action actionToPerform;
		Action prevAction = null;
		Results results = new Results();
		Random gen = new Random();
		for (int i = 0; i < 20; i++)
		{
			Set<Action> randomActions = (LinkedHashSet<Action>) possibleActions.actions(randomBoard);
			actionToPerform = (Action) randomActions.toArray()[gen.nextInt(randomActions.size())];
			if (prevAction != null)
			{
				boolean cont = true;
				do
				{
					if (actionToPerform.equals(Board.UP) && prevAction.equals(Board.DOWN)) {
						actionToPerform = (Action) randomActions.toArray()[gen.nextInt(randomActions.size())];
					}
					else if (actionToPerform.equals(Board.DOWN) && prevAction.equals(Board.UP)) {
						actionToPerform = (Action) randomActions.toArray()[gen.nextInt(randomActions.size())];
					}
					else if (actionToPerform.equals(Board.LEFT) && prevAction.equals(Board.RIGHT)){
						actionToPerform = (Action) randomActions.toArray()[gen.nextInt(randomActions.size())];
					}
					else if (actionToPerform.equals(Board.RIGHT) && prevAction.equals(Board.LEFT)) {
						actionToPerform = (Action) randomActions.toArray()[gen.nextInt(randomActions.size())];
					}
					else cont = false;
				} while(cont);
			}
			prevAction = actionToPerform;
			randomBoard = (Board) results.result(randomBoard, actionToPerform);
		}
		return randomBoard.getState();
	}

	public static class Board
	{
		public static Action LEFT = new DynamicAction("Left");
		public static Action RIGHT = new DynamicAction("Right");
		public static Action UP = new DynamicAction("Up");
		public static Action DOWN = new DynamicAction("Down");

		@SuppressWarnings("serial")
		private static final List<Integer> goalState = new ArrayList<Integer>(){{
			for (int i = 0; i < 9; i++) add(i);
		}};
		private ArrayList<Integer> states;

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

		public ArrayList<Integer> getState()
		{
			return states;
		}

		public int getPosOf0()
		{
			for (int i = 0; i < states.size(); i++)
			{
				if (states.get(i) == 0) return i;			
			}
			return -1;
		}

		public int getPos(int target)
		{
			for (int i = 0; i < 9; i++)
			{
				if (states.get(i) == target) return i;
			}
			return -1;
		}

		public int getXLoc(int index)
		{
			for (int i = 0; i < 9; i++)
			{
				if (states.get(i) == index) return i % 3;
			}
			return -1;
		}

		public int getYLoc(int index)
		{
			for (int i = 0; i < 9; i++)
			{
				if (states.get(i) == index) return i / 3;
			}
			return -1;
		}

		public void moveDown()
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

	public static class Actions implements ActionsFunction
	{
		@Override
		public Set<Action> actions(Object tempState) {
			Board board = (Board) tempState;
			Set<Action> possibleActions = new LinkedHashSet<Action>();
			int posOf0 = board.getPosOf0();
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
			return possibleActions;
		}

		public int getPosOf0(List<Integer> state)
		{
			for (int i = 0; i < state.size(); i++)
			{
				if (state.get(i) == 0) return i;			
			}
			return -1;
		}

	}

	public static class Results implements ResultFunction
	{
		@Override
		public Object result(Object tempState, Action action) {
			Board board = (Board) tempState;
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
}
