package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.util.Arrays;


public class BeatBot implements IBot {
    private static final String BOTNAME = "Call Me Chad.bot";

    //These are the preferred moves inside a zone in order
    private final int[][] preferredInnerMoves = {
            {0, 0}, {2, 0},  //Top left, Top right
            {0, 2}, {2, 2},  //Bot left, Bot right
            {1, 1}, //Center
            {0, 1}, {2, 1}, //Top, Bottom
            {1, 0}, {1, 2}}; //left, Right

    private String[][] board;

    private void fillBoard(IGameState gameState) {
        board = Arrays.stream(gameState.getField().getBoard()).map(String[]::clone).toArray(String[][]::new);
    }

    private IMove localTactic(IGameState gameState, String[] localBoard, String[] ignoreTiles) {
        fillBoard(gameState);

        String player = "1";
        if (gameState.getMoveNumber() % 2 == 0)
            player = "0";

        if (placeMiddle(localBoard, player))
            return new Move(1, 1);

        switch (checkBorders(localBoard, player)) {
            case 1 -> {
                if (checkReturn(ignoreTiles, 1))
                    return new Move(0, 1); //Top
            }
            case 3 -> {
                if (checkReturn(ignoreTiles, 3))
                    return new Move(1, 0); //Left
            }
            case 5 -> {
                if (checkReturn(ignoreTiles, 5))
                    return new Move(1, 2); //Right
            }
            case 7 -> {
                if (checkReturn(ignoreTiles, 7))
                    return new Move(2, 1); //Bottom
            }
        }

        switch (getOneCorner(localBoard, player)) {
            case 0 -> {
                if (checkReturn(ignoreTiles, 0))
                    return new Move(0, 0); //Top Left
            }
            case 2 -> {
                if (checkReturn(ignoreTiles, 2))
                    return new Move(2, 0); //Top Right
            }
            case 6 -> {
                if (checkReturn(ignoreTiles, 6))
                    return new Move(0, 2); //Bottom Left
            }
            case 8 -> {
                if (checkReturn(ignoreTiles, 8))
                    return new Move(2, 2); //Bottom Right
            }
        }

        return localTactic(gameState, localBoard);
    }

    private boolean checkReturn(String[] ignoredTiles, int move) {
        for (String s : ignoredTiles) {
            if (Integer.parseInt(s) == move)
                return false;
        }
        return true;
    }

    private IMove localTactic(IGameState gameState, String[] localBoard) {
        return localTactic(gameState, localBoard, null);
    }

    /**
     * Checks the borders of the board to play (top, left, right and bottom)
     *
     * @param localBoard
     * @param player
     * @param ignoredBorder Ignores the border (Top: 1, Left: 3, Right: 5, Bottom: 7)
     * @return Returns the suggested play, -1 if it has no suggestions.
     */
    private int checkBorders(String[] localBoard, String player, int ignoredBorder) {
        //checks if all borders are taken
        if (!localBoard[2].equals(IField.AVAILABLE_FIELD) && !localBoard[4].equals(IField.AVAILABLE_FIELD) &&
                !localBoard[6].equals(IField.AVAILABLE_FIELD) && !localBoard[8].equals(IField.AVAILABLE_FIELD))
            return -1;

        //checks for top play
        if (!localBoard[0].equals(IField.AVAILABLE_FIELD) && !localBoard[0].equals(player) &&
                !localBoard[2].equals(IField.AVAILABLE_FIELD) && !localBoard[2].equals(player))
            if (localBoard[1].equals(IField.AVAILABLE_FIELD) && ignoredBorder != 1)
                return 1;

        //checks for left play
        if (!localBoard[0].equals(IField.AVAILABLE_FIELD) && !localBoard[0].equals(player) &&
                !localBoard[6].equals(IField.AVAILABLE_FIELD) && !localBoard[6].equals(player))
            if (localBoard[3].equals(IField.AVAILABLE_FIELD) && ignoredBorder != 3)
                return 3;

        //checks for bottom play
        if (!localBoard[8].equals(IField.AVAILABLE_FIELD) && !localBoard[8].equals(player) &&
                !localBoard[6].equals(IField.AVAILABLE_FIELD) && !localBoard[6].equals(player))
            if (localBoard[7].equals(IField.AVAILABLE_FIELD) && ignoredBorder != 7)
                return 7;

        //checks for right play
        if (!localBoard[8].equals(IField.AVAILABLE_FIELD) && !localBoard[8].equals(player) &&
                !localBoard[2].equals(IField.AVAILABLE_FIELD) && !localBoard[2].equals(player))
            if (localBoard[5].equals(IField.AVAILABLE_FIELD) && ignoredBorder != 5)
                return 5;

        return -1;
    }

    private int checkBorders(String[] localBoard, String player) {
        return checkBorders(localBoard, player, -1);
    }

    //region getOneCorner

    /**
     * Returns a corner to play if we have a move placed in a corner already.
     *
     * @param localBoard
     * @param player
     * @param ignoredCorner Ignores the corner (0: top left, 2: top right, 6: bottom left, 8: bottom right).
     * @return Returns a corner it suggests to play in, -1 if it doesn't have suggestions.
     */
    private int getOneCorner(String[] localBoard, String player, int ignoredCorner) {

        //checks if all corners are taken, if true, it returns -1
        if (!localBoard[0].equals(IField.AVAILABLE_FIELD) && !localBoard[2].equals(IField.AVAILABLE_FIELD) &&
                !localBoard[6].equals(IField.AVAILABLE_FIELD) && !localBoard[8].equals(IField.AVAILABLE_FIELD))
            return -1;

        //checks top left with the rest of the corners
        if (!localBoard[0].equals(IField.AVAILABLE_FIELD) && !localBoard[0].equals(player)) {
            if (localBoard[2].equals(IField.AVAILABLE_FIELD) && ignoredCorner != 2)
                return 2;
            else if (localBoard[6].equals(IField.AVAILABLE_FIELD) && ignoredCorner != 6)
                return 6;
            else if (localBoard[8].equals(IField.AVAILABLE_FIELD) && ignoredCorner != 8)
                return 8;
        }

        //checks top right with the remaining corners to be checked
        if (!localBoard[2].equals(IField.AVAILABLE_FIELD) && !localBoard[2].equals(player)) {
            if (localBoard[6].equals(IField.AVAILABLE_FIELD) && ignoredCorner != 6)
                return 6;
            else if (localBoard[8].equals(IField.AVAILABLE_FIELD) && ignoredCorner != 8)
                return 8;
        }

        //check the last corners needed to be checked
        if (!localBoard[6].equals(IField.AVAILABLE_FIELD) && !localBoard[6].equals(player)) {
            if (localBoard[8].equals(IField.AVAILABLE_FIELD) && ignoredCorner != 8)
                return 8;
        }

        return -1;
    }

    private int getOneCorner(String[] localBoard, String player) {
        return getOneCorner(localBoard, player, -1);
    }
    //endregion

    private boolean placeMiddle(String[] localBoard, String player) {
        if (getBoardMoves(localBoard) == 1) {
            if (localBoard[0].equals(player) || localBoard[2].equals(player) ||
                    localBoard[6].equals(player) || localBoard[8].equals(player))
                return true;
        }

        return false;
    }

    private int getBoardMoves(String[] receivedBoard) {
        int sum = 0;
        for (String s : receivedBoard) {
            if (!s.equals(IField.AVAILABLE_FIELD)) {
                sum++;
            }
        }
        return sum;
    }

    private Move globalTactic(IGameState gameState) {
        fillBoard(gameState);
        //TODO

        return null;
    }

    private Move tacticsConnector(IGameState gameState) {
        fillBoard(gameState);
        //TODO

        return null;
    }

    @Override
    public IMove doMove(IGameState state) {
        return null;
    }

    @Override
    public String getBotName() {
        return BOTNAME; //To change body of generated methods, choose Tools | Templates.
    }
}
