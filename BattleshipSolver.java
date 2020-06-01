/*
Program to solve battleship puzzles.
*/

import java.util.*;
import java.awt.*;

public class BattleshipSolver{
	static Scanner console = new Scanner(System.in);
	static DrawingPanel panel = new DrawingPanel(600,600);
	static Graphics g = panel.getGraphics();
	static String quick_solve = "no";
	
	public static void main(String[] args){
		
		//Setting up grid
		g.drawLine(50, 50, 550, 50);
		g.drawLine(50, 50, 50, 550);
		g.drawLine(550, 50, 550, 550);
		g.drawLine(50, 550, 550, 550);
		
		for(int i = 100; i <= 500; i = i + 50){
			g.drawLine(i, 50, i, 550);
			g.drawLine(50, i, 550, i);
		}
		
		int[] row_num = new int[10];
		int[] col_num = new int[10];
		
		//Entering given spaces
		g.setFont(new Font("SansSerif", Font.BOLD, 30));
		System.out.println("Please enter numbers from top to bottom with spaces in between:");
		for(int i = 1; i <= 10; i++){
			row_num[i-1] = console.nextInt();
			g.drawString(row_num[i-1] + "", 560, 85 + 50*(i-1));
		}
		System.out.println("Please enter numbers from left to right with spaces in between:");
		for(int i = 1; i <= 10; i++){
			col_num[i-1] = console.nextInt();
			g.drawString(col_num[i-1] + "", 65 + 50*(i-1), 590);
		}	
		
		int[][] cells = new int[10][10];
		for(int i = 0; i <= 9; i++){
			for(int j = 0; j <= 9; j++){
				cells[i][j] = 0;
			}
		}
		
		System.out.print("Please enter number of given squares: ");
		int numGiven = console.nextInt();
		for(int i = 1; i <= numGiven; i++){
			System.out.print("Row: ");
			int row = console.nextInt();
			System.out.print("Column: ");
			int col = console.nextInt();
			System.out.println("Type (ship, water): ");
			String type = console.next();
			if(type.equals("water")){
				cells[row-1][col-1] = 1; 
			}else if(type.equals("ship")){
				System.out.println("Please enter orientation of straight side (top, bottom, left, right, all, none): ");
				String side = console.next();
				if(side.equals("top")){
					cells[row-1][col-1] = 2;
				}else if(side.equals("bottom")){
					cells[row-1][col-1] = 3;
				}else if(side.equals("left")){
					cells[row-1][col-1] = 4;
				}else if(side.equals("right")){
					cells[row-1][col-1] = 5;
				}else if(side.equals("all")){
					cells[row-1][col-1] = 6;
				}else if(side.equals("none")){
					cells[row-1][col-1] = 7;
				}
			}
			draw(g, cells);
		}
		
		System.out.println("Quick Solve? (yes/no)");
		quick_solve = console.next();
			
		if(quick_solve.equals("yes")){
			System.out.println("Starting to Solve!");
		}else{
			if(console.nextLine().isEmpty()){
				System.out.println("Starting to Solve!");
			}
		}
		
		//Check 1: Fill in already completed rows/columns
		if(quick_solve.equals("yes")){
			cells = check1(cells, row_num, col_num);
			draw(g, cells);
		}else{
			if(console.nextLine().isEmpty()){
				cells = check1(cells, row_num, col_num);
				draw(g, cells);
			}
		}

		//Check 2: Fill in ship parts that fit and completes the rows/columns
		if(quick_solve.equals("yes")){
			cells = check2(cells, row_num, col_num);
			draw(g, cells);
		}else{
			if(console.nextLine().isEmpty()){
				cells = check2(cells, row_num, col_num);
				draw(g, cells);
			}
		}

		//Check 3: Places ship markers
		//Check 4: Fills water around known ships
		if(quick_solve.equals("yes")){
			cells = check4(cells, row_num, col_num);
			draw(g, cells);
		}else{
			if(console.nextLine().isEmpty()){
				cells = check4(cells, row_num, col_num);
				draw(g, cells);
			}
		}
		
		//Fitting the Battleship: If repeating checks 3 and 4 does not work, will try to fit in the battleship if possible
		if(quick_solve.equals("yes")){
			cells = fitting_the_battleship(cells, row_num, col_num);
			draw(g, cells);
		}else{
			if(console.nextLine().isEmpty()){
				cells = fitting_the_battleship(cells, row_num, col_num);
				draw(g, cells);
			}
		}

		//Check 5: Filling out submarines
		if(quick_solve.equals("yes")){
			cells = check5(cells);
			draw(g, cells);
		}else{
			if(console.nextLine().isEmpty()){
				cells = check5(cells);
				draw(g, cells);
			}
		}
		
		System.out.println("FINISHED!");
	}
	public static void print2D (int mat[][]){ 
        // Loop through all rows 
        for (int[] row : mat){ 
            // converting each row as string 
            // and then printing in a separate line 
			System.out.println(Arrays.toString(row));
		}
	}
	public static int[][] copyMatrix (int mat[][]){
		//Copies the matrix
		
		int[][] newMatrix = new int[10][10];
		for(int i = 0; i <= 9; i++){
			for(int j = 0; j <= 9; j++){
				newMatrix[i][j] = mat[i][j];
			}
		}
		return newMatrix;
	}
	public static boolean compareMatrix (int mat1[][], int mat2[][]){
		//Compares 2 matrixes
		
		boolean equal = true;
		for(int i = 0; i <= 9; i++){
			for(int j = 0; j <= 9; j++){
				if(mat1[i][j] != mat2[i][j]){
					equal = false;
					break;
				}
			}
		}
		return equal;
	}
	public static void draw_x (Graphics g, int r, int c){
		//Draws an X for water
		
		int x = c*50 ;
		int y = r*50 ;
		
		g.drawLine(x, y, x+50, y+50);
		g.drawLine(x, y+50, x+50, y);
	}
	public static void draw_ship (Graphics g, int r, int c, int orient){
		//Draws a ship part based on orientation of straight parts
		
		int x = c*50 ;
		int y = r*50 ;
		
		g.fillOval(x+10, y+10, 30, 30);
		if(orient==2){
			g.fillRect(x+10, y+10, 30, 15);
		}else if(orient==3){
			g.fillRect(x+10, y+25, 30, 15);
		}else if(orient==4){
			g.fillRect(x+10, y+10, 15, 30);
		}else if(orient==5){
			g.fillRect(x+25, y+10, 15, 30);
		}else if(orient==6){
			g.fillRect(x+10, y+10, 30, 30);
		}else if(orient==7){
		}
	}
	public static void draw_marker (Graphics g, int r, int c){
		//draws an empty circle for a ship marker (used to mark incompleted ships)
		
		int x = c*50 ;
		int y = r*50 ;
		
		g.drawOval(x+10, y+10, 30, 30);
	}
	public static void erase (Graphics g, int r, int c){
		//erased the space
		
		int x = c*50 ;
		int y = r*50 ;
		g.setColor(Color.white);
		g.fillRect(x+2, y+2, 46, 46);
		g.setColor(Color.black);
	}
	public static void draw (Graphics g, int[][]cells){
		//draws the whole board
		
		for(int i = 0; i <= 9; i++){
			for(int j = 0; j <= 9; j++){
				erase(g, i+1, j+1);
				if(cells[i][j] == 1){
					draw_x(g, i+1, j+1);
				}else if(cells[i][j] >= 2 && cells[i][j] <=7){
					draw_ship(g, i+1, j+1, cells[i][j]);
				}else if(cells[i][j] == 8){
					draw_marker(g, i+1, j+1);
				}
			}
		}
	}
	public static int[][] check1 (int[][]cells, int[]row_num, int[]col_num){
		//Fill in already completed rows
		System.out.println("Check 1");
		
		int[][] temp_cells = cells;
		
		for(int i = 0; i <= 9; i++){
			int num_ships = 0;
			for(int j = 0; j <= 9; j++){
				if(cells[i][j] >= 2){
					num_ships++;
				}
			}
			// System.out.println(num_ships);
			if(row_num[i] - num_ships == 0){
				for(int j = 0; j <= 9; j++){
					if(cells[i][j] == 0){
						temp_cells[i][j] = 1;
					}
				}
			}
		}
		
		for(int j = 0; j <= 9; j++){
			int num_ships = 0;
			for(int i = 0; i <= 9; i++){
				if(cells[i][j] >= 2){
					num_ships++;
				}
			}
			// System.out.println(num_ships);
			if(col_num[j] - num_ships == 0){
				for(int i = 0; i <= 9; i++){
					if(cells[i][j] == 0){
						temp_cells[i][j] = 1;
					}
				}
			}
		}
		
		return temp_cells;
	}
	public static int[][] check2 (int[][]cells, int[]row_num, int[]col_num){
		//Fill in ship parts that fit and completes the rows/columns
		System.out.println("Check 2");
		
		int[][] temp_cells = cells;
		
		for(int i = 0; i <= 9; i++){
			int pot_ships = 0;
			for(int j = 0; j <= 9; j++){
				if(cells[i][j] >= 2){
					pot_ships++;
				}
				if(cells[i][j] == 4 || cells[i][j] == 5){
					pot_ships++;
				}
				if(cells[i][j] == 6){
					pot_ships = pot_ships + 2;
				}
			}
			// System.out.println(pot_ships);
			if(row_num[i] - pot_ships == 0){
				for(int j = 0; j <= 9; j++){
					if(cells[i][j] == 4){
						temp_cells[i][j-1] = 5;
					}
					if(cells[i][j] == 5){
						temp_cells[i][j+1] = 4;
					}
					if(cells[i][j] == 6){
						temp_cells[i][j+1] = 4;
						temp_cells[i][j-1] = 5;
						j++;
					}					
				}
			}
		}
		
		for(int j = 0; j <= 9; j++){
			int pot_ships = 0;
			for(int i = 0; i <= 9; i++){
				if(cells[i][j] >= 2){
					pot_ships++;
				}
				if(cells[i][j] == 2 || cells[i][j] == 3){
					pot_ships++;
				}
				if(cells[i][j] == 6){
					pot_ships = pot_ships + 2;
				}
			}
			// System.out.println(pot_ships);
			if(col_num[j] - pot_ships == 0){
				for(int i = 0; i <= 9; i++){
					if(cells[i][j] == 2){
						temp_cells[i-1][j] = 3;
					}
					if(cells[i][j] == 3){
						temp_cells[i+1][j] = 2;
					}
					if(cells[i][j] == 6){
						temp_cells[i+1][j] = 2;
						temp_cells[i-1][j] = 3;
					}
				}
			}
		}
		
		temp_cells = check1(temp_cells, row_num, col_num);
		
		return temp_cells;
	}
	public static int[][] check3 (int[][]cells, int[]row_num, int[]col_num){
		//Places ship markers
		
		int[][] temp_cells = cells;
		int[][] comp_cells = new int[10][10];
		
		while(!compareMatrix(comp_cells, temp_cells)){
			comp_cells = copyMatrix(temp_cells);
			for(int i = 0; i <= 9; i++){
				int num_empty = 0;
				for(int j = 0; j <= 9; j++){
					if(comp_cells[i][j] != 1){
						num_empty++;
					}
				}
				// System.out.println("row " + i + ": " + num_empty);
				if(row_num[i] == num_empty){
					for(int j = 0; j <= 9; j++){
						if(comp_cells[i][j] == 0){
							temp_cells[i][j] = 8;
						}
					}
				}
			}
			
			System.out.println("Check 3 rows");
			draw(g, temp_cells);
			if(quick_solve.equals("yes")){
				temp_cells = check1(temp_cells, row_num, col_num);
				draw(g, temp_cells);
			}else{
				if(console.nextLine().isEmpty()){
					temp_cells = check1(temp_cells, row_num, col_num);
					draw(g, temp_cells);
				}
			}
			
			for(int j = 0; j <= 9; j++){
				int num_empty = 0;
				for(int i = 0; i <= 9; i++){
					if(comp_cells[i][j] != 1){
						num_empty++;
					}
				}
				// System.out.println("col " + j + ": " + num_empty);
				if(col_num[j] == num_empty){
					for(int i = 0; i <= 9; i++){
						if(comp_cells[i][j] == 0){
							temp_cells[i][j] = 8;
						}
					}
				}
			}
			
			System.out.println("Check 3 columns");
			draw(g, temp_cells);
			if(quick_solve.equals("yes")){
				temp_cells = check1(temp_cells, row_num, col_num);
				draw(g, temp_cells);
			}else{
				if(console.nextLine().isEmpty()){
					temp_cells = check1(temp_cells, row_num, col_num);
					draw(g, temp_cells);
				}
			}
			temp_cells = convert(temp_cells);
		}
		return temp_cells;
	}
	public static int[][] check4 (int[][]cells, int[]row_num, int[]col_num){
		//Fills water around known ships
		
		int[][] temp_cells = cells;
		int[][] comp_cells = new int[10][10];
		
		while(!compareMatrix(comp_cells, temp_cells)){
			comp_cells = copyMatrix(temp_cells);
			for(int i = 0; i <= 9; i++){
				for(int j = 0; j <= 9; j++){
					if(cells[i][j] >= 2 && cells[i][j] < 8){
						if(i<9){
							if(cells[i+1][j] == 0){
								temp_cells[i+1][j] = 1;
							}
							if(j<9){
								if(cells[i+1][j+1] == 0){
								temp_cells[i+1][j+1] = 1;
								}
							}
							if(j>0){
								if(cells[i+1][j-1] == 0){
									temp_cells[i+1][j-1] = 1;
								}
							}
						}
						if(i>0){
							if(cells[i-1][j] == 0){
								temp_cells[i-1][j] = 1;
							}
							if(j>0){
								if(cells[i-1][j-1] == 0){
								temp_cells[i-1][j-1] = 1;
								}
							}
							if(j<9){
								if(cells[i-1][j+1] == 0){
									temp_cells[i-1][j+1] = 1;
								}
							}
						}
						if(j<9){
							if(cells[i][j+1] == 0){
								temp_cells[i][j+1] = 1;
							}
						}
						if(j>0){
							if(cells[i][j-1] == 0){
								temp_cells[i][j-1] = 1;
							}
						}
					}
				}
			}
			
			System.out.println("Check 4");
			draw(g, temp_cells);
			if(quick_solve.equals("yes")){
				temp_cells = check3(temp_cells, row_num, col_num);
				draw(g, temp_cells);
			}else{
				if(console.nextLine().isEmpty()){
					temp_cells = check3(temp_cells, row_num, col_num);
					draw(g, temp_cells);
				}
			}
			
		}
		

		return temp_cells;
	}
	public static int[][] check5 (int[][]cells){
		//Filling out submarines
		System.out.println("Check 5");
		
		int[][] temp_cells = cells;
		
		for(int i = 0; i <= 9; i++){
			if(i==0){
				for(int j = 0; j <= 9; j++){
					if(j==0){
						if(cells[i][j] == 8 && cells[i+1][j] == 1 && cells[i][j+1] == 1 && cells[i+1][j+1] == 1){
							temp_cells[i][j] = 7;
						}
					}
					if(j>0 && j<9){
						if(cells[i][j] == 8 && cells[i+1][j] == 1 && cells[i][j+1] == 1 && cells[i][j-1] == 1 && cells[i+1][j+1] == 1 && cells[i+1][j-1] == 1){
							temp_cells[i][j] = 7;
						}
					}
					if(j==9){
						if(cells[i][j] == 8 && cells[i+1][j] == 1 && cells[i][j-1] == 1 && cells[i+1][j-1] == 1){
							temp_cells[i][j] = 7;
						}
					}
				}
			}
			if(i>0 && i<9){
				for(int j = 0; j <= 9; j++){
					if(j==0){
						if(cells[i][j] == 8 && cells[i+1][j] == 1 && cells[i-1][j] == 1 && cells[i][j+1] == 1 && cells[i+1][j+1] == 1 && cells[i-1][j+1] == 1){
							temp_cells[i][j] = 7;
						}
					}
					if(j>0 && j<9){
						if(cells[i][j] == 8 && cells[i+1][j] == 1 && cells[i-1][j] == 1 && cells[i][j+1] == 1 && cells[i][j-1] == 1 && cells[i+1][j+1] == 1 && cells[i+1][j-1] == 1 && cells[i-1][j+1] == 1 && cells[i-1][j-1] == 1){
							temp_cells[i][j] = 7;
						}
					}
					if(j==9){
						if(cells[i][j] == 8 && cells[i+1][j] == 1 && cells[i-1][j] == 1 && cells[i][j-1] == 1 && cells[i+1][j-1] == 1 && cells[i-1][j-1] == 1){
							temp_cells[i][j] = 7;
						}
					}
				}
			}
			if(i==9){
				for(int j = 0; j <= 9; j++){
					if(j==0){
						if(cells[i][j] == 8 && cells[i+1][j] == 1 && cells[i][j+1] == 1 && cells[i+1][j+1] == 1){
							temp_cells[i][j] = 7;
						}
					}
					if(j>0 && j<9){
						if(cells[i][j] == 8 && cells[i-1][j] == 1 && cells[i][j+1] == 1 && cells[i][j-1] == 1 && cells[i-1][j+1] == 1 && cells[i-1][j-1] == 1){
							temp_cells[i][j] = 7;
						}
					}
					if(j==9){
						if(cells[i][j] == 8 && cells[i-1][j] == 1 && cells[i][j-1] == 1 && cells[i-1][j-1] == 1){
							temp_cells[i][j] = 7;
						}
					}
				}
			}
		}
		
		return temp_cells;
	}	
	public static int[][] convert (int[][]cells){ 
		//Converts ship markers to complete ships
		
		int[][] temp_cells = cells;
		
		for(int i = 0; i <= 9; i++){
			int ship_start = -1;
			boolean ship = false;
			for(int j = 0; j <= 9; j++){
				if(cells[i][j] == 8){
					if(j == 0){
						ship_start = j;
						ship = true;
					}else{
						if(cells[i][j-1] == 1){
							ship_start = j;
							ship = true;
						}
					}
				}
				if(j > 1 && ship == true){
					if(cells[i][j] == 1 && cells[i][j-1] == 8){
						if(j-1 != ship_start){
							temp_cells[i][j-1] = 4;
							temp_cells[i][ship_start] = 5;
							for(int n = ship_start + 1; n < j-1; n++){
								temp_cells[i][n] = 6;
							}
						}
						ship = false;
					}
				}
				if(cells[i][j] == 8 && j == 9 && ship == true){
					if(j != ship_start){
						temp_cells[i][j] = 4;
						temp_cells[i][ship_start] = 5;
						for(int n = ship_start + 1; n < j; n++){
							temp_cells[i][n] = 6;
						}
					}
					ship = false;
				}
			}
		}
		
		for(int j = 0; j <= 9; j++){
			int ship_start = -1;
			boolean ship = false;
			for(int i = 0; i <= 9; i++){
				if(cells[i][j] == 8){
					if(i == 0){
						ship_start = i;
						ship = true;
					}else{
						if(cells[i-1][j] == 1){
							ship_start = i;
							ship = true;
						}
					}
				}
				if(i > 1 && ship == true){
					if(cells[i][j] == 1 && cells[i-1][j] == 8){
						if(i-1 != ship_start){
							temp_cells[i-1][j] = 2;
							temp_cells[ship_start][j] = 3;
							for(int n = ship_start + 1; n < i-1; n++){
								temp_cells[n][j] = 6;
							}
						}
						ship = false;
					}
				}
				if(cells[i][j] == 8 && i == 9 && ship == true){
					if(i != ship_start){
						temp_cells[i][j] = 2;
						temp_cells[ship_start][j] = 3;
						for(int n = ship_start + 1; n < i; n++){
							temp_cells[n][j] = 6;
						}
					}
					ship = false;
				}
			}
		}
		
		return temp_cells;
	}
	
	public static int[][] fitting_the_battleship (int[][]cells, int[]row_num, int[]col_num){
		//If repeating checks 3 and 4 does not work, will try to fit in the battleship if possible.
		
		int[][] temp_cells = copyMatrix(cells);
		
		//Checking if battleship has already been used.
		boolean battleship = false;
		for(int i = 0; i <= 9; i++){
			int ship_length = 0;
			for(int j = 0; j <= 9; j++){
				if(cells[i][j] == 5 || cells[i][j] == 6){
					ship_length++;
				}
				if(cells[i][j] == 4){
					ship_length++;
					if(ship_length == 4){
						battleship = true;
					}else{
						ship_length = 0;
					}
				}
			}
		}
		for(int j = 0; j <= 9; j++){
			int ship_length = 0;
			for(int i = 0; i <= 9; i++){
				if(cells[i][j] == 3 || cells[i][j] == 6){
					ship_length++;
				}
				if(cells[i][j] == 2){
					ship_length++;
					if(ship_length == 4){
						battleship = true;
					}else{
						ship_length = 0;
					}
				}
			}
		}
		
		if(battleship == false){
			ArrayList<Integer[]> possible_placements_hor = new ArrayList<Integer[]>();
			for(int i = 0; i <= 9; i++){
				if(row_num[i] >= 4){
					boolean can_place = true;
					int blank_length = 0;
					for(int j = 0; j <= 9; j++){
						if(cells[i][j] >= 2 && cells[i][j] <= 7){
							can_place = false;
							System.out.println("here3");
							break;
						}
						if(cells[i][j] == 0 || cells[i][j] == 8){
							blank_length++;
						}
						if(cells[i][j] == 1){
							if(blank_length >= 4){
								for(int a = 1; a <= blank_length-3; a++){
									Integer[] ship = new Integer[5];
									for(int n = 0; n < 4; n++){
										ship[n] = j-a-n;
									}
									ship[4] = i;
									possible_placements_hor.add(ship);
								}
							}
							
							// if(blank_length >= 4){
								// Integer[] ship = new Integer[5];
								// for(int n = 0; n < 4; n++){
									// ship[n] = j-1-n;
								// }
								// ship[4] = i;
								// possible_placements_hor.add(ship);
							// }
							// if(blank_length >= 5){
								// Integer[] ship = new Integer[5];
								// for(int n = 0; n < 4; n++){
									// ship[n] = j-2-n;
								// }
								// ship[4] = i;
								// possible_placements_hor.add(ship);
							// }
							blank_length = 0;
						}
						if(j == 9){
							if(blank_length >= 4){
								for(int a = 1; a <= blank_length-3; a++){
									Integer[] ship = new Integer[5];
									for(int n = 0; n < 4; n++){
										ship[n] = j-a-n+1;
									}
									ship[4] = i;
									possible_placements_hor.add(ship);
								}
							}
							
							// if(blank_length >= 4){
								// Integer[] ship = new Integer[5];
								// for(int n = 0; n < 4; n++){
									// ship[n] = j-n;
								// }
								// ship[4] = i;
								// possible_placements_hor.add(ship);
							// }
							// if(blank_length >= 5){
								// Integer[] ship = new Integer[5];
								// for(int n = 0; n < 4; n++){
									// ship[n] = j-1-n;
								// }
								// ship[4] = i;
								// possible_placements_hor.add(ship);
							// }
						}
								
					}
				}
			}
			
			ArrayList<Integer[]> possible_placements_ver = new ArrayList<Integer[]>();
			for(int j = 0; j <= 9; j++){
				if(col_num[j] >= 4){
					boolean can_place = true;
					int blank_length = 0;
					for(int i = 0; i <= 9; i++){
						if(cells[i][j] >= 2 && cells[i][j] <= 7){
							can_place = false;
							System.out.println("here3");
							break;
						}
						if(cells[i][j] == 0 || cells[i][j] == 8){
							blank_length++;
						}
						if(cells[i][j] == 1){
							if(blank_length >= 4){
								for(int a = 1; a <= blank_length-3; a++){
									Integer[] ship = new Integer[5];
									for(int n = 0; n < 4; n++){
										ship[n] = i-a-n;
									}
									ship[4] = j;
									possible_placements_ver.add(ship);
								}
							}
							
							// if(blank_length >= 4){
								// Integer[] ship = new Integer[5];
								// for(int n = 0; n < 4; n++){
									// ship[n] = i-1-n;
								// }
								// ship[4] = j;
								// possible_placements_ver.add(ship);
							// }
							// if(blank_length >= 5){
								// Integer[] ship = new Integer[5];
								// for(int n = 0; n < 4; n++){
									// ship[n] = i-2-n;
								// }
								// ship[4] = j;
								// possible_placements_ver.add(ship);
							// }
							blank_length = 0;
						}
						if(i == 9){
							if(blank_length >= 4){
								for(int a = 1; a <= blank_length-3; a++){
									Integer[] ship = new Integer[5];
									for(int n = 0; n < 4; n++){
										ship[n] = i-a-n+1;
									}
									ship[4] = j;
									possible_placements_ver.add(ship);
								}
							}
							
							
							// if(blank_length >= 4){
								// Integer[] ship = new Integer[5];
								// for(int n = 0; n < 4; n++){
									// ship[n] = i-n;
								// }
								// ship[4] = j;
								// possible_placements_ver.add(ship);
							// }
							// if(blank_length >= 5){
								// Integer[] ship = new Integer[5];
								// for(int n = 0; n < 4; n++){
									// ship[n] = i-1-n;
								// }
								// ship[4] = j;
								// possible_placements_ver.add(ship);
							// }
						}
								
					}
				}
			}
			
			boolean complete1 = false;
			if(possible_placements_hor.size() > 0){
				for(int i = 0; i < possible_placements_hor.size(); i++){
					System.out.println("Placing Battleship");
					Integer[] ship = possible_placements_hor.get(i);
					temp_cells[ship[4]][ship[3]] = 5;
					temp_cells[ship[4]][ship[0]] = 4;
					for(int n = 1; n < 3; n++){
						temp_cells[ship[4]][ship[n]] = 6;
					}
					draw(g, temp_cells);
					
					if(quick_solve.equals("yes")){
						temp_cells = check1(temp_cells, row_num, col_num);
						temp_cells = check4(temp_cells, row_num, col_num);
					}else{
						if(console.nextLine().isEmpty()){
							temp_cells = check1(temp_cells, row_num, col_num);
							temp_cells = check4(temp_cells, row_num, col_num);
						}
					}
					
					
					boolean complete2 = true;
					for(int ii = 0; ii <= 9; ii++){
						for(int jj = 0; jj <= 9; jj++){
							if(temp_cells[ii][jj] == 0){
								complete2 = false;
							}
						}
					}
					if(complete2 == true){
						complete1 = true;
						break;
					}else{
						temp_cells = copyMatrix(cells);
					}
				}
			}
			
			if(complete1 == false && possible_placements_ver.size() > 0){
				for(int j = 0; j < possible_placements_ver.size(); j++){
					System.out.println("Placing Battleship");
					Integer[] ship = possible_placements_ver.get(j);
					temp_cells[ship[3]][ship[4]] = 3;
					temp_cells[ship[0]][ship[4]] = 2;
					for(int n = 1; n < 3; n++){
						temp_cells[ship[n]][ship[4]] = 6;
					}
					draw(g, temp_cells);
					
					if(quick_solve.equals("yes")){
						temp_cells = check1(temp_cells, row_num, col_num);
						temp_cells = check4(temp_cells, row_num, col_num);
					}else{
						if(console.nextLine().isEmpty()){
							temp_cells = check1(temp_cells, row_num, col_num);
							temp_cells = check4(temp_cells, row_num, col_num);
						}
					}
					
					
					boolean complete2 = true;
					for(int ii = 0; ii <= 9; ii++){
						for(int jj = 0; jj <= 9; jj++){
							if(temp_cells[ii][jj] == 0){
								complete2 = false;
							}
						}
					}
					if(complete2 == true){
						complete1 = true;
						break;
					}else{
						temp_cells = copyMatrix(cells);
					}
				}
			}
		}
		
		return temp_cells;
	}
	
		
	
}