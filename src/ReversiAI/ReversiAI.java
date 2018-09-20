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

class AIGuy {

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


    // main function that (1) establishes a connection with the server, and then plays whenever it is this player's turn
    public AIGuy(int _me, String host) {
        me = _me;
        initClient(host);

        int myMove;

        while (true) {
            System.out.println("Read");
            readMessage();

            if (turn == me) {
                System.out.println("Move");
                validMoves = getValidMoves(round, state);

                myMove = move();
                //myMove = generator.nextInt(numValidMoves);        // select a move randomly

                String sel = validMoves[myMove] / 8 + "\n" + validMoves[myMove] % 8;

                System.out.println("Selection: " + validMoves[myMove] / 8 + ", " + validMoves[myMove] % 8);

                sout.println(sel);
            }
        }
        //while (turn == me) {
        //    System.out.println("My turn");

            //readMessage();
        //}
    }

    // You should modify this function
    // validMoves is a list of valid locations that you could place your "stone" on this turn
    // Note that "state" is a global variable 2D list that shows the state of the game
    private int move() {
        // just move randomly for now

        int myMove = generator.nextInt(numValidMoves);

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

//    private Pair<Integer,Integer> minimax(int move, int depth, int alpha, int beta, boolean isMaxPlayer, int[] currentMoves) {
//        if(depth == 0 || isTerminal()){
//            return heuristic(move);
//        }
//        if(isMaxPlayer){
//            int value = Integer.MAX_VALUE;
//
//            for(int i = 0; i < currentMoves.length; i++){
//
//                //value = Math.max(value,minimax(currentMoves[i],depth-1,alpha,beta,false,));
//            }
//
//        }
//        // Go through list of possible moves
//        // calculate maximum cost of each
//        // find minimum of maximum expected cost
//        // return move
//    }
//
   private int heuristic(int state[][],int playerNumber, int round) {
        int temp = me;
        me = playerNumber;
        int[] possibleMoves = getValidMoves(round, state);
        me = temp;
        return possibleMoves.length;
   }

   private boolean isTerminal(int[] currentMoves){
       return currentMoves.length == 0;
   }

    // generates the set of valid moves for the player; returns a list of valid moves (validMoves)
    private int[] getValidMoves(int round, int state[][]) {
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
            System.out.println("Valid Moves:");
            for (i = 0; i < numValidMoves; i++) {
                System.out.println(validMoves[i] / 8 + ", " + validMoves[i] % 8);
            }
        }
        else {
            System.out.println("Valid Moves:");
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    if (state[i][j] == 0) {
                        if (couldBe(state, i, j)) {
                            validMoves[numValidMoves] = i*8 + j;
                            numValidMoves ++;
                            System.out.println(i + ", " + j);
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

    private boolean checkDirection(int state[][], int row, int col, int incx, int incy) {
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
            if (me == 1) {
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

    private boolean couldBe(int state[][], int row, int col) {
        int incx, incy;

        for (incx = -1; incx < 2; incx++) {
            for (incy = -1; incy < 2; incy++) {
                if ((incx == 0) && (incy == 0))
                    continue;

                if (checkDirection(state, row, col, incx, incy))
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
            System.out.println(t1);
            t2 = Double.parseDouble(sin.readLine());
            System.out.println(t2);
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    state[i][j] = Integer.parseInt(sin.readLine());
                }
            }
            sin.readLine();
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }

        System.out.println("Turn: " + turn);
        System.out.println("Round: " + round);
        for (i = 7; i >= 0; i--) {
            for (j = 0; j < 8; j++) {
                System.out.print(state[i][j]);
            }
            System.out.println();
        }
        System.out.println();
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
        new AIGuy(Integer.parseInt(args[1]), args[0]);
    }

}
