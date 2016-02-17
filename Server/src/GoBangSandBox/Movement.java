package GoBangSandBox;

/**
 * Created by Jason Song(wolfogre@outlook.com) on 02/04/2016.
 */
class Movement{
	public Movement(Sandbox.Player who, int row, int col) {
		this.who = who;
		this.row = row;
		this.col = col;
	}

	public Sandbox.Player who;
	public int row;
	public int col;
}