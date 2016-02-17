package GoBangSandBox;

import java.util.Stack;

/**
 * Created by Jason Song(wolfogre@outlook.com) on 02/01/2016.
 */
public class Sandbox {
	public enum Player{BLACK, WHITE, NONE, UNKNOWN}

	public final int BLACK_CODE = 1;
	public final int WHITE_CODE = 2;

	public final int MATRIX_SIZE = 15;

	Stack<Movement> movements;

	public Sandbox(){
		movements = new Stack<>();
	}

	public void init(){
		movements.clear();
	}

	public Player getNextPlayer(){
		if(movements.isEmpty())
			return Player.BLACK;
		if(movements.peek().who.equals(Player.BLACK))
			return Player.WHITE;
		return Player.BLACK;
	}

	public Player getWinner(){
		int[][] matrix = getMatrix();
		if(checkWinner(matrix, BLACK_CODE))
			return Player.BLACK;
		if(checkWinner(matrix, WHITE_CODE))
			return Player.WHITE;
		if(isFull())
			return Player.NONE;
		return Player.UNKNOWN;
	}

	public boolean move(Player who, int row, int col){
		// 原本这里应该检查落子位置是否有效,
		// 但这种错误在实际使用阶段不应该发生,
		// 这里要是抛出异常,服务端很难办,
		// 如果真的发生了也是客户端的责任,服务端不买单
		assert(row >= 0 && row < MATRIX_SIZE);
		assert(col >= 0 && col < MATRIX_SIZE);
		assert(Player.BLACK.equals(who) || Player.WHITE.equals(who));
		assert(movements.isEmpty() && Player.BLACK.equals(who) || !movements.peek().who.equals(who));

		movements.add(new Movement(who, row, col));
		return 	getWinner().equals(Player.UNKNOWN);
	}

	public void regret(Player who){
		while(!movements.isEmpty()){
			if(movements.pop().who.equals(who))
				break;
		}
	}

	public int[][] getMatrix(){
		int[][] matrix = new int[MATRIX_SIZE][MATRIX_SIZE];
		for(Movement movement : movements){
			if(movement.who.equals(Player.BLACK))
				matrix[movement.row][movement.col] = BLACK_CODE;
			else
				matrix[movement.row][movement.col] = WHITE_CODE;
		}
		return matrix;
	}

	private boolean checkWinner(int[][] matrix, int playerCode){
		for(int row = 0; row < MATRIX_SIZE; ++row)
			for(int col = 0; col < MATRIX_SIZE; ++col)
			{
				if(checkWinnerHelp(matrix, playerCode, 5, row, col, 1, 0))
					return true;
				if(checkWinnerHelp(matrix, playerCode, 5, row, col, 1, 1))
					return true;
				if(checkWinnerHelp(matrix, playerCode, 5, row, col, 0, 1))
					return true;
			}
		return false;
	}

	private boolean checkWinnerHelp(int[][] matrix, int playerCode, int remain, int row, int col, int rowMove, int colMove){
		if(remain == 1 && matrix[row][col] == playerCode)
			return true;
		if(matrix[row][col] != playerCode)
			return false;
		if(row + rowMove < 0 || row + rowMove > MATRIX_SIZE - 1)
			return false;
		if(col + colMove < 0 || col + colMove > MATRIX_SIZE - 1)
			return false;
		return checkWinnerHelp(matrix, playerCode, remain - 1, row + rowMove, col + colMove, rowMove, colMove);
	}

	private boolean isFull(){
		return movements.size() == MATRIX_SIZE * MATRIX_SIZE;
	}
}

