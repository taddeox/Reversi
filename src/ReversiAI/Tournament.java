package ReversiAI;

public class Tournament {
    public static void main(String args[]) {
        if(args[2].equals("a"))
            new AIGuy(Integer.parseInt(args[1]), args[0]);
        else if(args[2].equals("n"))
            new NewGuy(Integer.parseInt(args[1]), args[0]);

    }
}
