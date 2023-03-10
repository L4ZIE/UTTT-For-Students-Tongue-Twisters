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

    private void fillBoard(IGameState state){
        board = state.getField().getBoard();
    }

    private IMove scrappyTactic(IGameState state){
        fillBoard(state);
        List<IMove> availableMoves = state.getField().getAvailableMoves();
        fillBoardXandY(availableMoves);
        List<IMove> ignoredMoves = checkRiskyMoves(availableMoves);
        List<IMove> suggestedMoves = new ArrayList<>();
        Random rnd;

        //first ignores risky moves:
        for (IMove m : availableMoves) {
            if (!ignoredMoves.contains(m))
                suggestedMoves.add(m);
        }

        //if every move is risky, scraps the ignore list:
        if(suggestedMoves.isEmpty())
            for(IMove m : availableMoves){
                suggestedMoves.add(m);
            }

        //if board is empty, place into a random corner that is not ignored:
        if(availableMoves.size() == 9){
            rnd = new Random();
            return calculateCorner(ignoredMoves);
        }

        //if board has a single move and in a corner and middle is not ignored, play middle:
        if (availableMoves.size() == 8){
            if (enemyMoveIsInCorner())
                return new Move(1 + boardX, 1 + boardY);
        }

        return null;
    }

    private boolean enemyMoveIsInCorner() {
        //TODO
        return false;
    }

    private IMove calculateCorner(List<IMove> ignoredMoves) {
        //TODO
        return null;
    }

    private void fillBoardXandY(List<IMove> availableMoves) {
        //TODO
    }

    private List<IMove> checkRiskyMoves(List<IMove> availableMoves) {
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
