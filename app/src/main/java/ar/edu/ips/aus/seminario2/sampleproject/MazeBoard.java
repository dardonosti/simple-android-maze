package ar.edu.ips.aus.seminario2.sampleproject;

/**
 * A maze 2D rectangular board. Knows the maze layout, dimensions. Can be queried for width, height
 * and piece by positional (0 based index). Can export/import textual representation.
 */
public class MazeBoard {

    public enum Direction {
        NONE,
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    private int width = 0;
    private int height = 0;
    private BoardPiece[] board = null;

    public int getVerticalTileCount() {return height;}

    public int getHorizontalTileCount() { return width;}

    public int getWidth() {
        return width;
    }

    public BoardPiece getPiece(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new ArrayIndexOutOfBoundsException("Check your coordinates!");
        }
        return board[x%width+height*y];
    }

    public boolean isWinFlag(double x, double y) {
        return getPiece((int)x, (int)y).isWinFlag();
    }

    private MazeBoard() {}

    public static MazeBoard from(String repr) {
        if (repr == null || repr.isEmpty())
            throw new IllegalArgumentException("Empty representation.");

        MazeBoard maze = new MazeBoard();

        switch (repr) {
            // test with simple layout
            case "wolf3d": {
                maze.board = new BoardPiece[9];
                maze.height = 3; maze.width = 3;

                maze.board[0] = new BoardPiece(false,false, false,true);
                maze.board[1] = new BoardPiece(false,false, true, true);
                maze.board[2] = new BoardPiece(true,false,false,false);
                maze.board[3] = new BoardPiece(false,true,true,false);
                maze.board[4] = new BoardPiece(true,true,true,true);
                maze.board[5] = new BoardPiece(true,false,false,true);
                maze.board[6] = new BoardPiece(false,false,true,false);
                maze.board[7] = new BoardPiece(true, true,false,false);
                maze.board[8] = new BoardPiece(false,true,false,false);

                maze.board[4].setWinFlag(true); //SET WIN FLAG
                maze.board[4].setWolf3d(true);
                break;
            }

            case " 3 x 3 ": {
                maze.board = new BoardPiece[9];
                maze.height = 3; maze.width = 3;

                maze.board[0] = new BoardPiece(false,false, false,true);
                maze.board[1] = new BoardPiece(false,false, true, false);
                maze.board[2] = new BoardPiece(true,false,false,true);
                maze.board[3] = new BoardPiece(false,true,true,true);
                maze.board[4] = new BoardPiece(true,false,true,false);
                maze.board[5] = new BoardPiece(true,true,false,true);
                maze.board[6] = new BoardPiece(false,true,true,false);
                maze.board[7] = new BoardPiece(true, false,false,false);
                maze.board[8] = new BoardPiece(false,true,false,false);

                maze.board[4].setWinFlag(true); //SET WIN FLAG
                break;
            }

            case " 4 x 4 ": {
                maze.board = new BoardPiece[16];
                maze.height = 4; maze.width = 4;

                maze.board[0] = new BoardPiece(false,false,true,true);
                maze.board[1] = new BoardPiece(true,false, false, false);
                maze.board[2] = new BoardPiece(false,false,true,true);
                maze.board[3] = new BoardPiece(true,false,false,false);
                maze.board[4] = new BoardPiece(false,true,false,true);
                maze.board[5] = new BoardPiece(false,false,false,true);
                maze.board[6] = new BoardPiece(false,true,true,false);
                maze.board[7] = new BoardPiece(true,false,false,true);
                maze.board[8] = new BoardPiece(false,true,false,true);
                maze.board[9] = new BoardPiece(false,true,true,false);
                maze.board[10] = new BoardPiece(true,false,true,true);
                maze.board[11] = new BoardPiece(true,true,false,true);
                maze.board[12] = new BoardPiece(false,true,true,false);
                maze.board[13] = new BoardPiece(true,false,true,false);
                maze.board[14] = new BoardPiece(true,true,false,false);
                maze.board[15] = new BoardPiece(false,true,false,false);

                maze.board[5].setWinFlag(true); //SET WIN FLAG
                break;
            }

            case " 5 x 5 ": {
                maze.board = new BoardPiece[25];
                maze.height = 5; maze.width = 5;

                maze.board[0] = new BoardPiece(false,false,true,true);
                maze.board[1] = new BoardPiece(true,false, true, false);
                maze.board[2] = new BoardPiece(true,false,false,true);
                maze.board[3] = new BoardPiece(false,false, true, false);
                maze.board[4] = new BoardPiece(true,false, false, true);
                maze.board[5] = new BoardPiece(false,true,true,false);
                maze.board[6] = new BoardPiece(true,false,false,true);
                maze.board[7] = new BoardPiece(false,true,true,true);
                maze.board[8] = new BoardPiece(true,false,true,false);
                maze.board[9] = new BoardPiece(true,true,false,false);
                maze.board[10] = new BoardPiece(false,false, true, true);
                maze.board[11] = new BoardPiece(true,true,false,true);
                maze.board[12] = new BoardPiece(false,true, true, false);
                maze.board[13] = new BoardPiece(true,false,false,true);
                maze.board[14] = new BoardPiece(false,false, false, true);
                maze.board[15] = new BoardPiece(false,true, false, true);
                maze.board[16] = new BoardPiece(false,true,true,false);
                maze.board[17] = new BoardPiece(true,false, false, true);
                maze.board[18] = new BoardPiece(false,true,false,true);
                maze.board[19] = new BoardPiece(false,true,false,true);
                maze.board[20] = new BoardPiece(false,true, true, false);
                maze.board[21] = new BoardPiece(true,false,false,false);
                maze.board[22] = new BoardPiece(false,true,false,false);
                maze.board[23] = new BoardPiece(false,true, true, false);
                maze.board[24] = new BoardPiece(true,true,false,false);

                maze.board[12].setWinFlag(true); //SET WIN FLAG
                break;

            }
            case " 6 x 6 ":{
                maze.board = new BoardPiece[36];
                maze.height = 6; maze.width = 6;

                maze.board[0] = new BoardPiece(false,false,true,false);
                maze.board[1] = new BoardPiece(true,false, true, true);
                maze.board[2] = new BoardPiece(true,false,false,true);
                maze.board[3] = new BoardPiece(false,false, true, true);
                maze.board[4] = new BoardPiece(true,false, true, true);
                maze.board[5] = new BoardPiece(true,false,false,false);
                maze.board[6] = new BoardPiece(false,false,true,false);
                maze.board[7] = new BoardPiece(true,true,false,true);
                maze.board[8] = new BoardPiece(false,true,false,false);
                maze.board[9] = new BoardPiece(false,true,false,true);
                maze.board[10] = new BoardPiece(false,true, true, false);
                maze.board[11] = new BoardPiece(true,false,false,true);
                maze.board[12] = new BoardPiece(false,false, true, true);
                maze.board[13] = new BoardPiece(true,true,true,true);
                maze.board[14] = new BoardPiece(true,false, true, false);
                maze.board[15] = new BoardPiece(true,true, false, true);
                maze.board[16] = new BoardPiece(false,false,true,true);
                maze.board[17] = new BoardPiece(true,true, false, false);
                maze.board[18] = new BoardPiece(false,true,false,true);
                maze.board[19] = new BoardPiece(false,true,true,false);
                maze.board[20] = new BoardPiece(true,false, false, true);
                maze.board[21] = new BoardPiece(false,true,false,true);
                maze.board[22] = new BoardPiece(false,true,true,false);
                maze.board[23] = new BoardPiece(true,false, false, true);
                maze.board[24] = new BoardPiece(false,true,true,false);
                maze.board[25] = new BoardPiece(true,false,false,true);
                maze.board[26] = new BoardPiece(false,true,false,false);
                maze.board[27] = new BoardPiece(false,true,false,true);
                maze.board[28] = new BoardPiece(false,false,false,true);
                maze.board[29] = new BoardPiece(false,true,false,true);
                maze.board[30] = new BoardPiece(false,false,true,false);
                maze.board[31] = new BoardPiece(true,true,true,false);
                maze.board[32] = new BoardPiece(true,false,false,false);
                maze.board[33] = new BoardPiece(false,true,true,false);
                maze.board[34] = new BoardPiece(true,true,false,false);
                maze.board[35] = new BoardPiece(false,true,false,false);

                maze.board[20].setWinFlag(true); //SET WIN FLAG
                break;
            }
            case " 7 x 7 ":{
                maze.board = new BoardPiece[49];
                maze.height = 7; maze.width = 7;

                maze.board[0] = new BoardPiece(false,false,true,true);
                maze.board[1] = new BoardPiece(true,false, true, false);
                maze.board[2] = new BoardPiece(true,false,true,false);
                maze.board[3] = new BoardPiece(true,false, true, false);
                maze.board[4] = new BoardPiece(true,false, true, true);
                maze.board[5] = new BoardPiece(true,false,false,true);
                maze.board[6] = new BoardPiece(false,false,false,true);
                maze.board[7] = new BoardPiece(false,true,false,true);
                maze.board[8] = new BoardPiece(false,false,true,true);
                maze.board[9] = new BoardPiece(true,false,false,true);
                maze.board[10] = new BoardPiece(false,false, true, true);
                maze.board[11] = new BoardPiece(true,true,false,false);
                maze.board[12] = new BoardPiece(false,true, true, false);
                maze.board[13] = new BoardPiece(true,true,false,false);
                maze.board[14] = new BoardPiece(false,true, true, false);
                maze.board[15] = new BoardPiece(true,true, false, false);
                maze.board[16] = new BoardPiece(false,true,false,true);
                maze.board[17] = new BoardPiece(false,true, true, false);
                maze.board[18] = new BoardPiece(true,false,true,false);
                maze.board[19] = new BoardPiece(true,false,true,false);
                maze.board[20] = new BoardPiece(true,false, false, true);
                maze.board[21] = new BoardPiece(false,false,false,true);
                maze.board[22] = new BoardPiece(false,false,true,true);
                maze.board[23] = new BoardPiece(true,true, true, true);
                maze.board[24] = new BoardPiece(true,false,true,false);
                maze.board[25] = new BoardPiece(true,false,false,true);
                maze.board[26] = new BoardPiece(false,false,true,true);
                maze.board[27] = new BoardPiece(true,true,false,false);
                maze.board[28] = new BoardPiece(false,true,false,true);
                maze.board[29] = new BoardPiece(false,true,false,true);
                maze.board[30] = new BoardPiece(false,true,true,false);
                maze.board[31] = new BoardPiece(true,false,false,true);
                maze.board[32] = new BoardPiece(false,true,false,true);
                maze.board[33] = new BoardPiece(false,true,false,true);
                maze.board[34] = new BoardPiece(false,false,false,true);
                maze.board[35] = new BoardPiece(false,true,true,false);
                maze.board[36] = new BoardPiece(true,true,true,false);
                maze.board[37] = new BoardPiece(true,false,false,true);
                maze.board[38] = new BoardPiece(false,true,false,true);
                maze.board[39] = new BoardPiece(false,true,false,false);
                maze.board[40] = new BoardPiece(false,true,false,true);
                maze.board[41] = new BoardPiece(false,true,false,true);
                maze.board[42] = new BoardPiece(false,false,true,false);
                maze.board[43] = new BoardPiece(true,false,true,false);
                maze.board[44] = new BoardPiece(true,true,false,false);
                maze.board[45] = new BoardPiece(false,true,true,false);
                maze.board[46] = new BoardPiece(true,false,false,false);
                maze.board[47] = new BoardPiece(false,true,true,false);
                maze.board[48] = new BoardPiece(true,true,false,false);

                maze.board[24].setWinFlag(true); //SET WIN FLAG
                break;

            }
            case " 8 x 8 ":{
                maze.board = new BoardPiece[64];
                maze.height = 8; maze.width = 8;

                maze.board[0] = new BoardPiece(false,false,true,true);
                maze.board[1] = new BoardPiece(true,false, false, true);
                maze.board[2] = new BoardPiece(false,false,true,true);
                maze.board[3] = new BoardPiece(true,false, false, true);
                maze.board[4] = new BoardPiece(false,false, true, true);
                maze.board[5] = new BoardPiece(true,false,true,false);
                maze.board[6] = new BoardPiece(true,false,true,false);
                maze.board[7] = new BoardPiece(true,false,false,false);
                maze.board[8] = new BoardPiece(false,true,false,true);
                maze.board[9] = new BoardPiece(false,true,false,false);
                maze.board[10] = new BoardPiece(false,true, false, true);
                maze.board[11] = new BoardPiece(false,true,false,true);
                maze.board[12] = new BoardPiece(false,true, true, false);
                maze.board[13] = new BoardPiece(true,false,true,false);
                maze.board[14] = new BoardPiece(true,false, true, true);
                maze.board[15] = new BoardPiece(true,false, false, true);
                maze.board[16] = new BoardPiece(false,true,false,true);
                maze.board[17] = new BoardPiece(false,false, true, true);
                maze.board[18] = new BoardPiece(true,true,false,false);
                maze.board[19] = new BoardPiece(false,true,false,true);
                maze.board[20] = new BoardPiece(false,false, true, false);
                maze.board[21] = new BoardPiece(true,false,false,true);
                maze.board[22] = new BoardPiece(false,true,false,false);
                maze.board[23] = new BoardPiece(false,true, false, true);
                maze.board[24] = new BoardPiece(false,true,false,true);
                maze.board[25] = new BoardPiece(false,true,false,true);
                maze.board[26] = new BoardPiece(false,false,true,true);
                maze.board[27] = new BoardPiece(true,true,false,false);
                maze.board[28] = new BoardPiece(false,false,true,true);
                maze.board[29] = new BoardPiece(true,true,false,false);
                maze.board[30] = new BoardPiece(false,false,true,true);
                maze.board[31] = new BoardPiece(true,true,false,false);
                maze.board[32] = new BoardPiece(false,true,false,true);
                maze.board[33] = new BoardPiece(false,true,false,true);
                maze.board[34] = new BoardPiece(false,true,true,false);
                maze.board[35] = new BoardPiece(true,false,true,false);
                maze.board[36] = new BoardPiece(true,true,false,false);
                maze.board[37] = new BoardPiece(false,false,true,true);
                maze.board[38] = new BoardPiece(true,true,true,false);
                maze.board[39] = new BoardPiece(true,false,false,true);
                maze.board[40] = new BoardPiece(false,true,true,false);
                maze.board[41] = new BoardPiece(true,true,false,true);
                maze.board[42] = new BoardPiece(false,false,true,true);
                maze.board[43] = new BoardPiece(true,false,true,false);
                maze.board[44] = new BoardPiece(true,false,false,true);
                maze.board[45] = new BoardPiece(false,true,true,true);
                maze.board[46] = new BoardPiece(true,false,false,true);
                maze.board[47] = new BoardPiece(false,true,false,false);
                maze.board[48] = new BoardPiece(false,false,true,true);
                maze.board[49] = new BoardPiece(true,true,false,false);
                maze.board[50] = new BoardPiece(false,true,false,true);
                maze.board[51] = new BoardPiece(false,false,false,true);
                maze.board[52] = new BoardPiece(false,true,true,false);
                maze.board[53] = new BoardPiece(true,true,false,false);
                maze.board[54] = new BoardPiece(false,true,true,false);
                maze.board[55] = new BoardPiece(true,false,false,true);
                maze.board[56] = new BoardPiece(false,true,true,false);
                maze.board[57] = new BoardPiece(true,false,true,false);
                maze.board[58] = new BoardPiece(true,true,true,false);
                maze.board[59] = new BoardPiece(true,true,false,false);
                maze.board[60] = new BoardPiece(false,false,true,false);
                maze.board[61] = new BoardPiece(true,false,true,false);
                maze.board[62] = new BoardPiece(true,false,true,false);
                maze.board[63] = new BoardPiece(true,true,false,false);

                maze.board[31].setWinFlag(true); //SET WIN FLAG
                break;

            }
            case " 9 x 9 ":{
                maze.board = new BoardPiece[81];
                maze.height = 9; maze.width = 9;

                maze.board[0] = new BoardPiece(false,false,true,true);
                maze.board[1] = new BoardPiece(true,false, true, false);
                maze.board[2] = new BoardPiece(true,false,true,false);
                maze.board[3] = new BoardPiece(true,false, true, false);
                maze.board[4] = new BoardPiece(true,false, true, false);
                maze.board[5] = new BoardPiece(true,false,true,true);
                maze.board[6] = new BoardPiece(true,false,false,false);
                maze.board[7] = new BoardPiece(false,false,true,true);
                maze.board[8] = new BoardPiece(true,false,false,false);
                maze.board[9] = new BoardPiece(false,true,true,false);
                maze.board[10] = new BoardPiece(true,false, false, true);
                maze.board[11] = new BoardPiece(false,false,true,false);
                maze.board[12] = new BoardPiece(true,false, true, false);
                maze.board[13] = new BoardPiece(true,false,false,true);
                maze.board[14] = new BoardPiece(false,true, true, true);
                maze.board[15] = new BoardPiece(true,false, false, true);
                maze.board[16] = new BoardPiece(false,true,true,false);
                maze.board[17] = new BoardPiece(true,false, false, true);
                maze.board[18] = new BoardPiece(false,false,false,true);
                maze.board[19] = new BoardPiece(false,true,false,true);
                maze.board[20] = new BoardPiece(false,false, true, true);
                maze.board[21] = new BoardPiece(true,false,true,false);
                maze.board[22] = new BoardPiece(true,true,false,false);
                maze.board[23] = new BoardPiece(false,true, false, false);
                maze.board[24] = new BoardPiece(false,true,false,true);
                maze.board[25] = new BoardPiece(false,false,true,false);
                maze.board[26] = new BoardPiece(true,true,false,true);
                maze.board[27] = new BoardPiece(false,true,true,false);
                maze.board[28] = new BoardPiece(true,true,false,true);
                maze.board[29] = new BoardPiece(false,true,false,true);
                maze.board[30] = new BoardPiece(false,false,true,true);
                maze.board[31] = new BoardPiece(true,false,true,true);
                maze.board[32] = new BoardPiece(true,false,true,false);
                maze.board[33] = new BoardPiece(true,true,false,false);
                maze.board[34] = new BoardPiece(false,false,true,true);
                maze.board[35] = new BoardPiece(true,true,false,false);
                maze.board[36] = new BoardPiece(false,false,true,true);
                maze.board[37] = new BoardPiece(true,true,true,false);
                maze.board[38] = new BoardPiece(true,true,false,false);
                maze.board[39] = new BoardPiece(false,true,false,true);
                maze.board[40] = new BoardPiece(false,true,false,true);
                maze.board[41] = new BoardPiece(false,false,true,true);
                maze.board[42] = new BoardPiece(true,false,false,true);
                maze.board[43] = new BoardPiece(false,true,true,true);
                maze.board[44] = new BoardPiece(true,false,false,true);
                maze.board[45] = new BoardPiece(false,true,true,true);
                maze.board[46] = new BoardPiece(true,false,false,false);
                maze.board[47] = new BoardPiece(false,false,true,true);
                maze.board[48] = new BoardPiece(true,true,false,false);
                maze.board[49] = new BoardPiece(false,true,true,false);
                maze.board[50] = new BoardPiece(true,true,false,true);
                maze.board[51] = new BoardPiece(false,true,true,false);
                maze.board[52] = new BoardPiece(true,true,false,false);
                maze.board[53] = new BoardPiece(false,true,false,false);
                maze.board[54] = new BoardPiece(false,true,false,false);
                maze.board[55] = new BoardPiece(false,false,true,true);
                maze.board[56] = new BoardPiece(true,true,false,true);
                maze.board[57] = new BoardPiece(false,false,false,true);
                maze.board[58] = new BoardPiece(false,false,true,true);
                maze.board[59] = new BoardPiece(true,true,false,false);
                maze.board[60] = new BoardPiece(false,false,true,true);
                maze.board[61] = new BoardPiece(true,false,true,false);
                maze.board[62] = new BoardPiece(true,false,false,true);
                maze.board[63] = new BoardPiece(false,false,true,true);
                maze.board[64] = new BoardPiece(true,true,false,false);
                maze.board[65] = new BoardPiece(false,true,false,false);
                maze.board[66] = new BoardPiece(false,true,false,true);
                maze.board[67] = new BoardPiece(false,true,true,false);
                maze.board[68] = new BoardPiece(true,false,true,false);
                maze.board[69] = new BoardPiece(true,true,true,false);
                maze.board[70] = new BoardPiece(true,false,false,false);
                maze.board[71] = new BoardPiece(false,true,false,true);
                maze.board[72] = new BoardPiece(false,true,true,false);
                maze.board[73] = new BoardPiece(true,false,true,false);
                maze.board[74] = new BoardPiece(true,false,true,false);
                maze.board[75] = new BoardPiece(true,true,false,false);
                maze.board[76] = new BoardPiece(false,false,true,false);
                maze.board[77] = new BoardPiece(true,false,true,false);
                maze.board[78] = new BoardPiece(true,false,true,false);
                maze.board[79] = new BoardPiece(true,false,true,false);
                maze.board[80] = new BoardPiece(true,true,false,false);

                maze.board[40].setWinFlag(true); //SET WIN FLAG
                break;

            }

        }

        return maze;
    }
    public String toString() {return null;}

}
