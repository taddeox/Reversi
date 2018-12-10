package ReversiAI;
import javafx.util.Pair;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.lang.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.math.*;
import java.text.*;

class NewGuy {

    public Socket s;
    public BufferedReader sin;
    public PrintWriter sout;
    Random generator = new Random();

    double t1, t2;
    int me;
    int boardState;
    int state[][] = new int[8][8]; // state[0][0] is the bottom left corner of the board (on the GUI)
    int turn = -1;
    int round;

    int validMoves[] = new int[64];
    int numValidMoves;

    int[][] boardValues = new int[][]{
            {20, -3, 11, 8, 8, 11, -3, 20},
            {-3, -7, -4, 1, 1, -4, -7, -3},
            {11, -4, 2, 2, 2, 2, -4, 11},
            {8, 1, 2, -3, -3, 2, 1, 8},
            {8, 1, 2, -3, -3, 2, 1, 8},
            {11, -4, 2, 2, 2, 2, -4, 11},
            {-3, -7, -4, 1, 1, -4, -7, -3},
            {20, -3, 11, 8, 8, 11, -3, 20},
    };

    long currentTime;// = System.currentTimeMillis();
    long turnTime;
    long totalTime = 180000;
    int totalRounds = 65;


    // main function that (1) establishes a connection with the server, and then plays whenever it is this player's turn
    public NewGuy(int _me, String host) {
        me = _me;
        initClient(host);

        int myMove;

        while (true) {
            //System.out.println("Read");
            readMessage();

            if (turn == me) {
                //System.out.println("Move");
                validMoves = getValidMoves(round, state, me);

                myMove = move();
                //myMove = generator.nextInt(numValidMoves);        // select a move randomly

                String sel = validMoves[myMove] / 8 + "\n" + validMoves[myMove] % 8;

                //System.out.println("Selection: " + validMoves[myMove] / 8 + ", " + validMoves[myMove] % 8);

                sout.println(sel);
            }
        }
        //while (turn == me) {
        //    System.out.println("My turn");

        //readMessage();
        //}
    }

    int d = 7;
    boolean clockMoves = false;
    // You should modify this function
    // validMoves is a list of valid locations that you could place your "stone" on this turn
    // Note that "state" is a global variable 2D list that shows the state of the game
    private int move() {
        long turnStart = System.currentTimeMillis();

        int top = 0;
        int bot = 0;
        int lef = 0;
        int rig = 0;
        int cornerCount = 0;

        if(state[0][0] != 0)
        {
            boardValues[0][1] = 5;
            boardValues[1][1] = 5;
            boardValues[1][0] = 5;
            bot++;
            lef++;
            cornerCount++;
        }
        if(state[0][7] != 0)
        {
            boardValues[0][6] = 5;
            boardValues[1][6] = 5;
            boardValues[1][7] = 5;
            top++;
            lef++;
            cornerCount++;
        }
        if(state[7][0] != 0)
        {
            boardValues[6][0] = 5;
            boardValues[6][1] = 5;
            boardValues[7][1] = 5;
            bot++;
            rig++;
            cornerCount++;
        }
        if(state[7][7] != 0)
        {
            boardValues[6][7] = 5;
            boardValues[6][6] = 5;
            boardValues[7][6] = 5;
            top++;
            rig++;
            cornerCount++;
        }
        if(cornerCount >= 2){
            posMult = 1;
        }
//        if(top == 2 || bot == 2 || rig == 2 || lef == 2){
////            System.out.println(round);
//            posMult = 1;
//        }

//        if(round >= 39){
//            d = 10;
//        }
//        if(round >= 24){
//            posMult = 1;
//        }

        if(round >= 48){
            d = 20;
        }
//        if(round < 5){
//            return generator.nextInt(numValidMoves);
//        }
        turnTime = (totalTime/((totalRounds - round)/2));
//        System.out.println(turnTime);
//        System.out.println(round);

        currentTime = System.currentTimeMillis();
        int myMove = minimax(-1, d, Integer.MIN_VALUE, Integer.MAX_VALUE, true, validMoves, state, me, round).getKey();

        totalTime -= (System.currentTimeMillis() - turnStart);
//        System.out.println(totalTime);

        for(int i = 0; i < validMoves.length; i++){
            if(myMove == validMoves[i])
                return i;
        }

        return myMove;
    }

    // function alphabeta(node, depth, α, β, maximizingPlayer) is
    //     if depth = 0 or node is a terminal node then
    //         return the heuristic value of node
    //     if maximizingPlayer then
    //         value := −∞
    //         for each child of node do
    //             value := max(value, alphabeta(child, depth − 1, α, β, FALSE))
    //             α := max(α, value)
    //             if α ≥ β then
    //                 break (* β cut-off *)
    //         return value
    //     else
    //         value := +∞
    //         for each child of node do
    //             value := min(value, alphabeta(child, depth − 1, α, β, TRUE))
    //             β := min(β, value)
    //             if α ≥ β then
    //                 break (* α cut-off *)
    //         return value

    private Pair<Integer,Integer> minimax(int move, int depth, int alpha, int beta, boolean isMaxPlayer, int[] currentMoves, int currentState[][], int playerNumber, int round) {
        int currentNumValidMoves = numValidMoves;

        if(depth == 0 || currentNumValidMoves == 0){
            return new Pair<>(move, heuristic(move, isMaxPlayer, currentNumValidMoves, currentState, playerNumber));
        }
        if(isMaxPlayer){
            int value = Integer.MIN_VALUE;

            for(int i = 0; i < currentNumValidMoves; i++){
                int nextState[][] = makeMove(currentMoves[i], currentState, playerNumber);
                int[] nextMoves = getValidMoves(round + 1, nextState, otherPlayer(playerNumber));
                //int[] myMoves = getValidMoves(round + 2, nextState);

                int temp = value;
                //int currentValue = heuristic(currentMoves[i], isMaxPlayer, myMoves.length, nextState, playerNumber);
                value = Math.max(value, minimax(currentMoves[i],depth-1,alpha,beta,false, nextMoves, nextState, otherPlayer(playerNumber), round+1).getValue());
                if(value != temp)
                    move = currentMoves[i];

                alpha = Math.max(alpha, value);
                if(alpha >= beta)
                    break;
            }
            return new Pair<>(move, value);
        }
        else{
            int value = Integer.MAX_VALUE;

            for(int i = 0; i < currentNumValidMoves; i++){
                int nextState[][] = makeMove(currentMoves[i], currentState, playerNumber);
                int[] nextMoves = getValidMoves(round + 1, nextState, otherPlayer(playerNumber));
                //int[] myMoves = getValidMoves(round + 2, nextState);

                int temp = value;
                //int currentValue = heuristic(currentMoves[i], isMaxPlayer, myMoves.length, nextState, playerNumber);
                value = Math.min(value, minimax(currentMoves[i], depth-1, alpha, beta, true, nextMoves, nextState, otherPlayer(playerNumber), round+1).getValue());
                if(value != temp)
                    move = currentMoves[i];

                beta = Math.min(beta, value);
                if(alpha >= beta)
                    break;
            }
            return new Pair<>(move, value);
        }
    }

    private int[][] makeMove(int move, int[][]currentState, int playerNumber){
        int[][] newState = new int[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                newState[i][j] = currentState[i][j];
            }
        }

        newState[move/8][move%8] = playerNumber;
        changeColors(move/8, move%8, playerNumber-1, newState);
        return newState;
    }

    private int otherPlayer(int playerNumber){
        if(playerNumber == 1)
            return 2;
        else
            return 1;
    }

    private int heuristic(int move, boolean isMaxPlayer, int numValidMoves, int[][] state, int playerNumber) {
        if(move == -1)
            return 0;

        int winning = 0;
        int position = calcPosition(state, me);
        int validMoves = numValidMoves;
        int myTiles = numOfTiles(state, me);
        int oppTiles = numOfTiles(state, otherPlayer(me));
        int TileDifference = 0;
        if(myTiles > oppTiles)
            TileDifference = (100 * myTiles)/(myTiles + oppTiles);
        else if(myTiles < oppTiles)
            TileDifference = (-100 * oppTiles)/(myTiles + oppTiles);
        int cornerCount = calcCorners(state, me);

//       if(!isMaxPlayer) {
//           //position *= -1;
//           //validMoves *= -1;
//       }
        int myFront = 0;
        int oppFront = 0;
        int X1[] = {-1, -1, 0, 1, 1, 1, 0, -1};
        int Y1[] = {0, 1, 1, 1, 0, -1, -1, -1};
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(state[i][j] != 0){
                    for(int k = 0; k < 8; k++){
                        int x = i + X1[k];
                        int y = j + Y1[k];
                        if(x >= 0 && x < 8 && y >= 0 && y < 8 && state[x][y] == 0){
                            if(state[i][j] == me)
                                myFront++;
                            else
                                oppFront++;
                            break;
                        }
                    }
                }
            }
        }
        int front = 0;
        if(myFront > oppFront)
            front = (-100 * myFront)/(myFront + oppFront);
        else if (myFront < oppFront)
            front = (100 * oppFront)/(myFront + oppFront);



        if(numValidMoves == 0){
            boolean player1wins = isPlayer1Winning(state);
            if((player1wins && me == 1) || (!player1wins && me == 2))
                winning = 1;
            else
                winning = -1;
            //            System.out.println(player1wins);
        }

        getValidMoves(round, state, otherPlayer(me));

        int mobility = 0;
        if(numValidMoves > validMoves)
            mobility = (100 * numValidMoves)/(numValidMoves + validMoves);
        else if (numValidMoves < validMoves)
            mobility = (-100 * validMoves)/(playerNumber + validMoves);

        return (int) (winning * winMult + position * posMult + TileDifference * diffMult + front * frontMult + mobility * mobilityMult + cornerCount * cornerMult);
    }

    int posMult = 10;
    int diffMult = 10;
    double frontMult = 74.396;
    double mobilityMult = 78.922;
    double cornerMult = 801.724;
    int winMult = 1200;

    private int calcLines(int[][] state, int playerNumber) {
        int count = 0;
        boolean safe = true;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(state[i][j] != playerNumber){
                    safe = false;
                    break;
                }
            }

            if(safe)
                count++;
            else
                break;
        }

        safe = true;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(state[j][i] != playerNumber){
                    safe = false;
                    break;
                }
            }

            if(safe)
                count++;
            else
                break;
        }

        safe = true;
        for(int i = 7; i >= 0; i--){
            for(int j = 0; j < 8; j++){
                if(state[i][j] != playerNumber){
                    safe = false;
                    break;
                }
            }

            if(safe)
                count++;
            else
                break;
        }

        safe = true;
        for(int i = 7; i >= 7; i--){
            for(int j = 0; j < 8; j++){
                if(state[j][i] != playerNumber){
                    safe = false;
                    break;
                }
            }

            if(safe)
                count++;
            else
                break;
        }

        return count;
    }

    private int CalcRowsAndCols(int[][] state, int me) {
        int count = 0;
        for(int i = 0; i < 8; i++){
            boolean filled = true;
            int myTiles = 0;
            for(int j = 0; j < 8; j++){
                if(state[i][j] == me){
                    myTiles++;
                }
                if(state[i][j] == 0){
                    filled = false;
                }
            }
            if(filled && myTiles > 4){
                count++;
            }

            filled = true;
            myTiles = 0;
            for(int j = 0; j < 8; j++){
                if(state[j][i] == me){
                    myTiles++;
                }
                if(state[j][i] == 0){
                    filled = false;
                }
            }
            if(filled && myTiles > 4){
                count++;
            }
        }

        return count;
    }

    private int calcCorners(int[][] state, int playerNumber) {
        int count = 0;

        if(state[0][0] == playerNumber)
            count++;
        if(state[7][0] == playerNumber)
            count++;
        if(state[0][7] == playerNumber)
            count++;
        if(state[7][7] == playerNumber)
            count++;
        if(state[0][0] == otherPlayer(playerNumber))
            count--;
        if(state[7][0] == otherPlayer(playerNumber))
            count--;
        if(state[0][7] == otherPlayer(playerNumber))
            count--;
        if(state[7][7] == otherPlayer(playerNumber))
            count--;

        return count;
    }

    private int calcPosition(int[][] state, int playerNumber) {
        int value = 0;
        for(int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[i].length; j++) {
                if(state[i][j] == playerNumber){
                    value += boardValues[i][j];
                }
                else if(state[i][j] == otherPlayer(playerNumber)){
                    value -= boardValues[i][j];
                }
            }
        }
        return value;
    }

    private int numOfTiles(int[][] state, int playerNumber) {
        int count = 0;
        for(int i = 0; i < state.length; i++){
            for(int j = 0; j < state[i].length; j++){
                if(state[i][j] == playerNumber){
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isPlayer1Winning(int [][]state){
        int count1 = 0;
        int count2 = 0;

        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(state[i][j] == 1)
                    count1++;
                else if(state[i][j] == 2)
                    count2++;
            }
        }

        return count1 > count2;
    }

    private boolean isTerminal(int currentNumValidMoves){
        return currentNumValidMoves == 0;
    }


    public static void checkDirection(int row, int col, int incx, int incy, int turn, int[][] state) {
        int sequence[] = new int[7];
        int seqLen;
        int i, r, c;

        seqLen = 0;
        for (i = 1; i < 8; i++) {
            r = row+incy*i;
            c = col+incx*i;

            if ((r < 0) || (r > 7) || (c < 0) || (c > 7))
                break;

            sequence[seqLen] = state[r][c];
            seqLen++;
        }

        int count = 0;
        for (i = 0; i < seqLen; i++) {
            if (turn == 0) {
                if (sequence[i] == 2)
                    count ++;
                else {
                    if ((sequence[i] == 1) && (count > 0))
                        count = 20;
                    break;
                }
            }
            else {
                if (sequence[i] == 1)
                    count ++;
                else {
                    if ((sequence[i] == 2) && (count > 0))
                        count = 20;
                    break;
                }
            }
        }

        if (count > 10) {
            if (turn == 0) {
                i = 1;
                r = row+incy*i;
                c = col+incx*i;
                while (state[r][c] == 2) {
                    state[r][c] = 1;
                    i++;
                    r = row+incy*i;
                    c = col+incx*i;
                }
            }
            else {
                i = 1;
                r = row+incy*i;
                c = col+incx*i;
                while (state[r][c] == 1) {
                    state[r][c] = 2;
                    i++;
                    r = row+incy*i;
                    c = col+incx*i;
                }
            }
        }
    }

    public static void changeColors(int row, int col, int turn, int[][] state) {
        int incx, incy;

        for (incx = -1; incx < 2; incx++) {
            for (incy = -1; incy < 2; incy++) {
                if ((incx == 0) && (incy == 0))
                    continue;

                checkDirection(row, col, incx, incy, turn, state);
            }
        }
    }

    // generates the set of valid moves for the player; returns a list of valid moves (validMoves)
    private int[] getValidMoves(int round, int state[][], int playerNumber) {
        int i, j;
        int validMoves[] = new int[64];


        numValidMoves = 0;
        if (round < 4) {
            if (state[3][3] == 0) {
                validMoves[numValidMoves] = 3*8 + 3;
                numValidMoves ++;
            }
            if (state[3][4] == 0) {
                validMoves[numValidMoves] = 3*8 + 4;
                numValidMoves ++;
            }
            if (state[4][3] == 0) {
                validMoves[numValidMoves] = 4*8 + 3;
                numValidMoves ++;
            }
            if (state[4][4] == 0) {
                validMoves[numValidMoves] = 4*8 + 4;
                numValidMoves ++;
            }
            //System.out.println("Valid Moves:");
            //for (i = 0; i < numValidMoves; i++) {
            //    System.out.println(validMoves[i] / 8 + ", " + validMoves[i] % 8);
            //}
        }
        else {
            //System.out.println("Valid Moves:");
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    if (state[i][j] == 0) {
                        if (couldBe(state, i, j, playerNumber)) {
                            validMoves[numValidMoves] = i*8 + j;
                            numValidMoves ++;
                            //System.out.println(i + ", " + j);
                        }
                    }
                }
            }
        }

        return validMoves;

        //if (round > 3) {
        //    System.out.println("checking out");
        //    System.exit(1);
        //}
    }

    private boolean checkDirection(int state[][], int row, int col, int incx, int incy, int playerNumber) {
        int sequence[] = new int[7];
        int seqLen;
        int i, r, c;

        seqLen = 0;
        for (i = 1; i < 8; i++) {
            r = row+incy*i;
            c = col+incx*i;

            if ((r < 0) || (r > 7) || (c < 0) || (c > 7))
                break;

            sequence[seqLen] = state[r][c];
            seqLen++;
        }

        int count = 0;
        for (i = 0; i < seqLen; i++) {
            if (playerNumber == 1) {
                if (sequence[i] == 2)
                    count ++;
                else {
                    if ((sequence[i] == 1) && (count > 0))
                        return true;
                    break;
                }
            }
            else {
                if (sequence[i] == 1)
                    count ++;
                else {
                    if ((sequence[i] == 2) && (count > 0))
                        return true;
                    break;
                }
            }
        }

        return false;
    }

    private boolean couldBe(int state[][], int row, int col, int playerNumber) {
        int incx, incy;

        for (incx = -1; incx < 2; incx++) {
            for (incy = -1; incy < 2; incy++) {
                if ((incx == 0) && (incy == 0))
                    continue;

                if (checkDirection(state, row, col, incx, incy, playerNumber))
                    return true;
            }
        }

        return false;
    }

    public void readMessage() {
        int i, j;
        String status;
        try {
            //System.out.println("Ready to read again");
            turn = Integer.parseInt(sin.readLine());

            if (turn == -999) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }

                System.exit(1);
            }

            //System.out.println("Turn: " + turn);
            round = Integer.parseInt(sin.readLine());
            t1 = Double.parseDouble(sin.readLine());
            //System.out.println(t1);
            t2 = Double.parseDouble(sin.readLine());
            //System.out.println(t2);
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    state[i][j] = Integer.parseInt(sin.readLine());
                }
            }
            sin.readLine();
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }

//        System.out.println("Turn: " + turn);
//        System.out.println("Round: " + round);
//        for (i = 7; i >= 0; i--) {
//            for (j = 0; j < 8; j++) {
//                System.out.print(state[i][j]);
//            }
//            System.out.println();
//        }
//        System.out.println();
    }

    public void initClient(String host) {
        int portNumber = 3333+me;

        try {
            s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
            sin = new BufferedReader(new InputStreamReader(s.getInputStream()));

            String info = sin.readLine();
            System.out.println(info);
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }


    // compile on your machine: javac *.java
    // call: java RandomGuy [ipaddress] [player_number]
    //   ipaddress is the ipaddress on the computer the server was launched on.  Enter "localhost" if it is on the same computer
    //   player_number is 1 (for the black player) and 2 (for the white player)
    public static void main(String args[]) {
        new NewGuy(Integer.parseInt(args[1]), args[0]);
    }

}
