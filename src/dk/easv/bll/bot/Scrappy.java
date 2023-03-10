package dk.easv.bll.bot;

import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Scrappy implements IBot {
    private static final String BOTNAME = "Scrappy";
    private String[][] board;
    private int boardX, boardY;

    private void fillBoard(IGameState state) {
        board = state.getField().getBoard();
    }

    //priority moves is a good idea and would make the bot smarter but don't have time to implement
    //leaving it in for later on when I want to develop the idea further
    private IMove scrappyTactic(IGameState state, List<IMove> ignoredMoves, List<IMove> prioMoves) {
        fillBoard(state);
        List<IMove> availableMoves = state.getField().getAvailableMoves();
        fillBoardXandY(availableMoves);
        List<IMove> suggestedMoves = new ArrayList<>();

        String player = "1";
        String ai = "0";
        if (state.getMoveNumber() % 2 == 0) {
            player = "0";
            ai = "1";
        }


        //first ignores risky moves:
        for (IMove m : availableMoves) {
            if (ignoredMoves != null)
                if (!ignoredMoves.contains(m))
                    suggestedMoves.add(m);
        }

        //if every move is risky, scraps the ignore list:
        if (suggestedMoves.isEmpty())
            for (IMove m : availableMoves) {
                suggestedMoves.add(m);
            }

        //if board is empty, place into a random corner that is not ignored:
        if (availableMoves.size() == 9) {
            return calculateCorner(suggestedMoves);
        }

        //if board has a single move and in a corner and middle is not ignored, play middle:
        if (availableMoves.size() == 8) {
            if (enemyMoveIsInCorner(player))
                return new Move(1 + boardX, 1 + boardY);
        }

        //if board is about to be completed, complete it (if not ignored)
        if (getBoardCompletionMove(ai) != null) {
            return getBoardCompletionMove(ai);
        }

        //if enemy is about to complete the board, sabotage it
        if (getBoardCompletionMove(player) != null) {
            return getBoardCompletionMove(player);
        }

        return state.getField().getAvailableMoves().get(0);
    }

    private List<IMove> checkPrioMoves(List<IMove> availableMoves) {
        //TODO
        return null;
    }

    private IMove getBoardCompletionMove(String currentPlayer) {
        //TODO
        return null;
    }

    private boolean enemyMoveIsInCorner(String player) {
        return board[boardX][boardY] == player || board[2 + boardX][boardY] == player ||
                board[boardX][2 + boardY] == player || board[2 + boardX][2 + boardY] == player;
    }

    //there's a smarter way but I don't have time to develop it
    private IMove calculateCorner(List<IMove> suggestedMoves) {
        for (IMove m : suggestedMoves) {
            if (m.equals(new Move(boardX, boardY)) || m.equals(new Move(2 + boardX, boardY)) ||
                    m.equals(new Move(boardX, 2 + boardY)) || m.equals(new Move(2 + boardX, 2 + boardY)))
                return m;
        }
        return null;
    }

    private void fillBoardXandY(List<IMove> availableMoves) {
        if (!availableMoves.isEmpty()) {
            if (availableMoves.get(0).getX() > 5)
                boardX = 6;
            else if (availableMoves.get(0).getX() > 2)
                boardX = 3;
            else
                boardX = 0;

            if (availableMoves.get(0).getY() > 5)
                boardY = 6;
            else if (availableMoves.get(0).getY() > 2)
                boardY = 3;
            else
                boardY = 0;
        }
    }

    private List<IMove> checkRiskyMoves(List<IMove> availableMoves) {
        //TODO

        return null;
    }


    private IMove scrappyGlobal(IGameState state) {
        List<IMove> availableMoves = state.getField().getAvailableMoves();
        List<IMove> ignoredMoves = checkRiskyMoves(availableMoves);
        List<IMove> prioMoves = checkPrioMoves(availableMoves);

        return scrappyTactic(state, ignoredMoves, prioMoves);
    }


    @Override
    public IMove doMove(IGameState state) {
        return scrappyGlobal(state);
    }

    @Override
    public String getBotName() {
        return BOTNAME; //To change body of generated methods, choose Tools | Templates.
    }
}
