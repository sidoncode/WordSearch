package word.search.ui.game.board;

import java.util.Set;

import word.search.model.Word;

public class Solver {

    public static int[] xVector = {0,  1,  1, 1, 0, -1, -1, -1};
    public static int[] yVector = {-1, -1, 0, 1, 1,  1,  0, -1};


    public static String coordinateConverter(int direction){
        switch(direction){
            case 0:
                return "W";
            case 1 :
                return "NW";
            case 2:
                return "N";
            case 3:
                return "NE";
            case 4:
                return "E";
            case 5:
                return  "SE";
            case 6:
                return  "S";
            case 7:
                return "SW";

            default:
                return "null";
        }
    }


    public static String solveIn8Dir(String word, char[][] grid, int row, int col){
        if(grid[row][col] != word.charAt(0)) return "";

        String dir = "";

        int wordLen = word.length();

        for(int direction = 0; direction < 8; direction ++){
            int rowDir = row + yVector[direction];
            int colDir = col+ xVector[direction];
            int i;

            for(i = 1; i < wordLen; i++){
                if(!isValidDir(grid, rowDir, colDir) )break ;
                if(grid[rowDir][colDir] != word.charAt(i)) break;

                rowDir += yVector[direction];
                colDir += xVector[direction];
            }

            if(i == wordLen){
                dir += coordinateConverter(direction) + " ";
            }
        }

        return dir.trim();
    }



    public static boolean isValidDir(char[][] grid, int rowDir, int colDir){
        if(rowDir >= grid.length || rowDir < 0 || colDir >= grid[0].length || colDir < 0){
            return false;
        }
        return true;
    }



    public static void findPositions(char[][] grid, String word, Set<Integer> positions){

        for(int row = 0; row < grid.length; row++){
            for(int col = 0; col < grid[0].length; col++){
                String dir = solveIn8Dir(word, grid, row, col);
                if(dir != ""){
                    String[] list = dir.split(" ");

                    for(String direction : list){

                        int r = row;
                        int c = col;

                        if(direction.equals("N"))fillArray(r, c, word, grid,positions, 1, 0);
                        if(direction.equals("S"))fillArray(r, c, word, grid,positions, -1, 0);
                        if(direction.equals("W"))fillArray(r, c, word, grid,positions, 0, -1);
                        if(direction.equals("E"))fillArray(r, c, word, grid,positions, 0, 1);
                        if(direction.equals("NE"))fillArray(r, c, word, grid,positions, 1, 1);
                        if(direction.equals("SE"))fillArray(r, c, word, grid,positions, -1, 1);
                        if(direction.equals("NW"))fillArray(r, c, word, grid,positions, 1, -1);
                        if(direction.equals("SW"))fillArray(r, c, word, grid,positions, -1, -1);
                    }
                }
            }
        }
    }



    private static void fillArray(int r, int c, String word, char[][] grid, Set<Integer> positions, int a, int b){
        for(int i = 0; i < word.length(); i++){
            int position = (r * grid.length) + c;
            positions.add(position);
            c += a;
            r += b;
        }
    }





    public static int findFirstLetterPosition(char[][] grid, Word word){
        for(int row = 0; row < grid.length; row++){
            for(int col = 0; col < grid[0].length; col++){
                String dir = solveIn8Dir(word.answer, grid, row, col);
                if(dir != ""){
                    return (row * grid.length) + col;
                }
            }
        }
        return -1;
    }


}
