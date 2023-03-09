package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.util.Arrays;


public class BeatBot implements IBot {
    private static final String BOTNAME = "Call Me Chad.bot";
    private String[][] board;
    private boolean checkedWithoutIgnore = false;

    private void fillBoard(IGameState gameState) {
        board = Arrays.stream(gameState.getField().getBoard()).map(String[]::clone).toArray(String[][]::new);
    }

    private IMove localTactic(IGameState gameState, String[] localBoard, String[] ignoreTiles) {
        fillBoard(gameState);

        if (getBoardMoves(localBoard) == 0) {
            int[] preferredMoves = {
                    0, 2, 6, 8, //corners
                    4, //middle
                    1, 3, 5, 7 //borders
            }; //borders

            for (int move : preferredMoves) {
                if (checkReturn(ignoreTiles, move))
                    return moveConverter(move);
            }
        }

        String player = "1";
        if (gameState.getMoveNumber() % 2 == 0)
            player = "0";

        if (placeMiddle(localBoard, player) && checkReturn(ignoreTiles, 4))
            return new Move(1, 1);

        enemyIsWinning(localBoard, player, ignoreTiles);

        if (checkBorders(localBoard, player) != -1) {
            return moveConverter(checkBorders(localBoard, player));
        }

        if (getOneCorner(localBoard, player) != -1) {
            return moveConverter(getOneCorner(localBoard, player));
        }

        if (!checkedWithoutIgnore) {
            checkedWithoutIgnore = true;
            return localTactic(gameState, localBoard);
        } else {
            checkedWithoutIgnore = false;
            return gameState.getField().getAvailableMoves().get(0);
        }

    }

    private IMove localTactic(IGameState gameState, String[] localBoard) {
        return localTactic(gameState, localBoard, null);
    }

    private int enemyIsWinning(String[] localBoard, String player, String[] ignoredTiles) {
        //vertical check
        for (int i = 0; i < 3; i++) {
            //top & mid, top & bot, mid & bot,
            if (localBoard[i].equals(player)) {
                if (localBoard[i + 3].equals(player) && checkReturn(ignoredTiles, i + 6))
                    return i + 6; //counter: i + 6
                if (localBoard[i + 6].equals(player) && checkReturn(ignoredTiles, i + 3))
                    return i + 3; //counter: i + 3
            }
            if (localBoard[i + 3].equals(player) && localBoard[i + 6].equals(player) && checkReturn(ignoredTiles, i))
                return i; //counter: i
        }

        //horizontal check [] +
        for (int i = 0; i < 7; i += 3) {
            if (localBoard[i].equals(player)) {
                if (localBoard[i + 1].equals(player) && checkReturn(ignoredTiles, i + 2))
                    return i + 2; //counter: i + 2
                if (localBoard[i + 2].equals(player) && checkReturn(ignoredTiles, i + 1))
                    return i + 1; //counter: i + 1
            }
            if (localBoard[i + 1].equals(player) && localBoard[i + 2].equals(player) && checkReturn(ignoredTiles, i))
                return i;
        }

        //diagonal check X
        //0, 2, 4, 6, 8
        if (localBoard[4].equals(player)) {
            if (localBoard[0].equals(player) && checkReturn(ignoredTiles, 8))
                return 8; //counter: 8
            if (localBoard[2].equals(player) && checkReturn(ignoredTiles, 6))
                return 6; //counter: 6
            if (localBoard[6].equals(player) && checkReturn(ignoredTiles, 2))
                return 2;//counter: 2
            if (localBoard[8].equals(player) && checkReturn(ignoredTiles, 0))
                return 0;//counter: 0
        }


        if (localBoard[0].equals(player) && localBoard[8].equals(player) && checkReturn(ignoredTiles, 4))
            return 4;//counter: 4

        if (localBoard[2].equals(player) && localBoard[6].equals(player) && checkReturn(ignoredTiles, 4))
            return 4; //counter: 4

        return -1;
    }

    private IMove moveConverter(int move) {
        switch (move) {
            case 0 -> {
                return new Move(0, 0);
            }
            case 1 -> {
                return new Move(0, 1);
            }
            case 2 -> {
                return new Move(0, 2);
            }
            case 3 -> {
                return new Move(1, 0);
            }
            case 4 -> {
                return new Move(1, 1);
            }
            case 5 -> {
                return new Move(1, 2);
            }
            case 6 -> {
                return new Move(2, 0);
            }
            case 7 -> {
                return new Move(2, 1);
            }
            case 8 -> {
                return new Move(2, 2);
            }
            default -> {
                return null;
            }
        }
    }

    private int enemyIsWinning(String[] localBoard, String player) {
        return enemyIsWinning(localBoard, player);
    }

    private boolean checkReturn(String[] ignoredTiles, int move) {
        for (String s : ignoredTiles) {
            if (Integer.parseInt(s) == move)
                return false;
        }
        return true;
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
        if (!localBoard[0].equals(IField.AVAILABLE_FIELD) && !localBoard[2].equals(IField.AVAILABLE_FIELD) &&
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

    /**
     * Returns the number of moves made on the given board.
     *
     * @param receivedBoard
     * @return
     */
    private int getBoardMoves(String[] receivedBoard) {
        int sum = 0;
        for (String s : receivedBoard) {
            if (!s.equals(IField.AVAILABLE_FIELD)) {
                sum++;
            }
        }
        return sum;
    }

    private IMove globalTactic(IGameState gameState) {
        fillBoard(gameState);
        //TODO

        return null;
    }

    private IMove tacticsConnector(IGameState gameState) {
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