import java.util.*;
import astparser.*;


/**
 * Created by Suwadith 2015214 on 3/28/2017.
 */
public class PathFindingOnSquaredGrid {

    static Node[][] cell;
    static ArrayList<Node> pathList = new ArrayList<>();
    static ArrayList<Node> closedList = new ArrayList<>();

    // return a random N-by-N boolean matrix, where each entry is
    // true with probability p
    public static boolean[][] random(int N, double p) {
        TemplateClass.instrum(14, "MethodDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.random(int, double).N", N), new AP("SimpleName", "PathFindingOnSquaredGrid.random(int, double).p", p));
		boolean[][] a = new boolean[N][N];
		TemplateClass.instrum(15, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.random(int, double).a", ""));
        for (int i = 0; i < N; i++) {
			TemplateClass.instrum(16, "ForStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.random(int, double).i", i), new AP("SimpleName", "PathFindingOnSquaredGrid.random(int, double).N", N));
			for (int j = 0; j < N; j++) {
				TemplateClass.instrum(17, "ForStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.random(int, double).j", j), new AP("SimpleName", "PathFindingOnSquaredGrid.random(int, double).N", N));
				a[i][j] = StdRandom.bernoulli(p);
				TemplateClass.instrum(18, "Assign", new AP("Array Begin", "", "{"), new AP("inner SimpleName", "PathFindingOnSquaredGrid.random(int, double).a", a), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.random(int, double).i", i), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.random(int, double).j", j), new AP("Array End", "", "}"), new AP("MethodInvocation", "StdRandom", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.random(int, double).p", p));
			}
		}
        TemplateClass.instrum(21, "ReturnStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.random(int, double).a", a));
		return a;
    }

    /**
     * @param matrix         The boolean matrix that the framework generates
     * @param Ai             Starting point's x value
     * @param Aj             Starting point's y value
     * @param Bi             Ending point's x value
     * @param Bj             Ending point's y value
     * @param n              Length of one side of the matrix
     * @param v              Cost between 2 cells located horizontally or vertically next to each other
     * @param d              Cost between 2 cells located Diagonally next to each other
     * @param additionalPath Boolean to decide whether to calculate the cost of through the diagonal path
     * @param h              int value which decides the correct method to choose to calculate the Heuristic value
     */
    public static void generateHValue(boolean matrix[][], int Ai, int Aj, int Bi, int Bj, int n, int v, int d, boolean additionalPath, int h) {

        TemplateClass.instrum(24, "MethodDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).matrix", matrix), new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).Ai", Ai), new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).Aj", Aj), new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).Bi", Bi), new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).Bj", Bj), new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).n", n), new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).v", v), new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).d", d), new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).additionalPath", additionalPath), new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).h", h));
		for (int y = 0; y < matrix.length; y++) {
            TemplateClass.instrum(38, "ForStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).y", y), new AP("QualifiedName", "length", matrix.length));
			for (int x = 0; x < matrix.length; x++) {
                TemplateClass.instrum(39, "ForStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).x", x), new AP("QualifiedName", "length", matrix.length));
				//Creating a new Node object for each and every Cell of the Grid (Matrix)
                cell[y][x] = new Node(y, x);
				TemplateClass.instrum(41, "Assign", new AP("Array Begin", "", "{"), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).y", y), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).x", x), new AP("Array End", "", "}"), new AP("ClassInstanceCreation", "Node", "Node.Node(int, int)"), new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).y", y), new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).x", x));
                TemplateClass.instrum(43, "IfStatement", new AP("Array Begin", "", "{"), new AP("inner SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).matrix", matrix), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).y", y), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).x", x), new AP("Array End", "", "}"));
				//Checks whether a cell is Blocked or Not by checking the boolean value
                if (matrix[y][x]) {
                    TemplateClass.instrum(44, "IfStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).h", h), new AP("NumberLiteral", "", 1));
					if (h == 1) {
                        TemplateClass.instrum(46, "IfStatement", new AP("MethodInvocation", "Math.abs(int)", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).y", y), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).Bi", Bi), new AP("MethodInvocation", "Math.abs(int)", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).x", x), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).Bj", Bj));
						//Assigning the Chebyshev Heuristic value
                        if (Math.abs(y - Bi) > Math.abs(x - Bj)) {
                            cell[y][x].hValue = Math.abs(y - Bi);
							TemplateClass.instrum(47, "Assign", new AP("SimpleName", "Node.hValue", cell[y][x].hValue), new AP("MethodInvocation", "Math.abs(int)", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).y", y), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).Bi", Bi));
                        } else {
                            cell[y][x].hValue = Math.abs(x - Bj);
							TemplateClass.instrum(49, "Assign", new AP("SimpleName", "Node.hValue", cell[y][x].hValue), new AP("MethodInvocation", "Math.abs(int)", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).x", x), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).Bj", Bj));
                        }
                    } else {
						TemplateClass.instrum(52, "IfStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).h", h), new AP("NumberLiteral", "", 2));
						if (h == 2) {
							cell[y][x].hValue = Math.sqrt(Math.pow(y - Bi, 2) + Math.pow(x - Bj, 2));
							TemplateClass.instrum(53, "Assign", new AP("SimpleName", "Node.hValue", cell[y][x].hValue), new AP("MethodInvocation", "Math.sqrt(double)", ""), new AP("args MethodInvocation", "Math.pow(double, double)", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).y", y), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).Bi", Bi), new AP("args NumberLiteral", "", 2), new AP("args MethodInvocation", "Math.pow(double, double)", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).x", x), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).Bj", Bj), new AP("args NumberLiteral", "", 2));
						} else {
							TemplateClass.instrum(55, "IfStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).h", h), new AP("NumberLiteral", "", 3));
							if (h == 3) {
								cell[y][x].hValue = Math.abs(y - Bi) + Math.abs(x - Bj);
								TemplateClass.instrum(56, "Assign", new AP("SimpleName", "Node.hValue", cell[y][x].hValue), new AP("MethodInvocation", "Math.abs(int)", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).y", y), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).Bi", Bi), new AP("MethodInvocation", "Math.abs(int)", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).x", x), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).Bj", Bj));
							}
						}
					}
                } else {
                    //If the boolean value is false, then assigning -1 instead of the absolute length
                    cell[y][x].hValue = -1;
					TemplateClass.instrum(62, "Assign", new AP("SimpleName", "Node.hValue", cell[y][x].hValue), new AP("NumberLiteral", "", -1));
                }
            }
        }
        generatePath(cell, Ai, Aj, Bi, Bj, n, v, d, additionalPath);
		TemplateClass.instrum(66, "MethodInvocation", new AP("MethodInvocation", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean)", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).Ai", Ai), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).Aj", Aj), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).Bi", Bi), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).Bj", Bj), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).n", n), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).v", v), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).d", d), new AP("args SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).additionalPath", additionalPath));
    }

    public static void menu(int n,double p,int Ai,int Aj, int Bi, int Bj) {
        TemplateClass.instrum(69, "MethodDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).n", n), new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).p", p), new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Ai", Ai), new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Aj", Aj), new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Bi", Bi), new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Bj", Bj));
		int gCost = 0;
		TemplateClass.instrum(70, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).gCost", gCost), new AP("NumberLiteral", "", 0));

        //Generating a new Boolean Matrix according to the input values of n and p (Length, Percolation value)
        boolean[][] randomlyGenMatrix = random(n, p);
		TemplateClass.instrum(73, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).randomlyGenMatrix", randomlyGenMatrix), new AP("MethodInvocation", "PathFindingOnSquaredGrid.random(int, double)", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).n", n), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).p", p));

        //Creation of a Node type 2D array
        cell = new Node[randomlyGenMatrix.length][randomlyGenMatrix.length];
		TemplateClass.instrum(76, "Assign", new AP("SimpleName", "PathFindingOnSquaredGrid.cell", cell));

        Stopwatch timerFlow = null;
		TemplateClass.instrum(78, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).timerFlow", timerFlow), new AP("NullLiteral", "", null));

        //Loop to find all 3 pathways and their relative Final Cost values
        for (int j = 0; j < 3; j++) {

            TemplateClass.instrum(81, "ForStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).j", j), new AP("NumberLiteral", "", 3));
			TemplateClass.instrum(83, "IfStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).j", j), new AP("NumberLiteral", "", 0));
			if (j == 0) {
                timerFlow = new Stopwatch();
				TemplateClass.instrum(84, "Assign", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).timerFlow", timerFlow));
                //Method to generate Chebyshev path. Both Horizontal and Diagonal pathways are possible.
                generateHValue(randomlyGenMatrix, Ai, Aj, Bi, Bj, n, 10, 10, true, 1);
				TemplateClass.instrum(86, "MethodInvocation", new AP("MethodInvocation", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int)", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).randomlyGenMatrix", randomlyGenMatrix), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Ai", Ai), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Aj", Aj), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Bi", Bi), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Bj", Bj), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).n", n), new AP("args NumberLiteral", "", 10), new AP("args NumberLiteral", "", 10), new AP("args BooleanLiteral", "", true), new AP("args NumberLiteral", "", 1));

                TemplateClass.instrum(89, "IfStatement", new AP("SimpleName", "Node.hValue", cell[Ai][Aj].hValue), new AP("NumberLiteral", "", -1), new AP("MethodInvocation", "pathList.ArrayList<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Bi", Bi), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Bj", Bj));
				//Checks whether the end point has been reach (Stored in the pathList)
                if (cell[Ai][Aj].hValue!=-1&&pathList.contains(cell[Bi][Bj])) {
                    int for1 = 0;
					TemplateClass.instrum(90, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).for1", for1), new AP("NumberLiteral", "", 0));
					//Draws the path
                    for (int i = 0; i < for1; i++) {
						TemplateClass.instrum(92, "ForStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).i", i), new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).for1", for1));
						gCost += pathList.get(i).gValue;
						TemplateClass.instrum(93, "Assign", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).gCost", gCost), new AP("SimpleName", "Node.gValue", pathList.get(i).gValue));
						for1 = pathList.size();
						TemplateClass.instrum(94, "Assign", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).for1", for1), new AP("MethodInvocation", "pathList.ArrayList<Node>.size()", ""));
					}

                    System.out.println("Chebyshev Path Found");
					TemplateClass.instrum(97, "MethodInvocation", new AP("MethodInvocation", "System.out.PrintStream.println(String)", ""), new AP("args StringLiteral", "", "Chebyshev Path Found"));
                    System.out.println("Total Cost: " + gCost/10.0);
					TemplateClass.instrum(98, "MethodInvocation", new AP("MethodInvocation", "System.out.PrintStream.println(String)", ""), new AP("args StringLiteral", "", "Total Cost: "), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).gCost", gCost), new AP("args NumberLiteral", "", 10.0));
                    /*System.out.println("Total fCost: " + fCost);*/
                    StdOut.println("Elapsed time = " + timerFlow.elapsedTime());
					TemplateClass.instrum(100, "MethodInvocation", new AP("MethodInvocation", "StdOut", ""), new AP("args StringLiteral", "", "Elapsed time = "), new AP("args MethodInvocation", "timerFlow", ""));
                    gCost = 0;
					TemplateClass.instrum(101, "Assign", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).gCost", gCost), new AP("NumberLiteral", "", 0));

                } else {

                    System.out.println("Chebyshev Path Not found");
					TemplateClass.instrum(105, "MethodInvocation", new AP("MethodInvocation", "System.out.PrintStream.println(String)", ""), new AP("args StringLiteral", "", "Chebyshev Path Not found"));
                    StdOut.println("Elapsed time = " + timerFlow.elapsedTime());
					TemplateClass.instrum(106, "MethodInvocation", new AP("MethodInvocation", "StdOut", ""), new AP("args StringLiteral", "", "Elapsed time = "), new AP("args MethodInvocation", "timerFlow", ""));

                }

                //Clears Both the pathList and the closedList
                pathList.clear();
				TemplateClass.instrum(111, "MethodInvocation", new AP("MethodInvocation", "pathList.ArrayList<Node>.clear()", ""));
                closedList.clear();
				TemplateClass.instrum(112, "MethodInvocation", new AP("MethodInvocation", "closedList.ArrayList<Node>.clear()", ""));
            }


            TemplateClass.instrum(116, "IfStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).j", j), new AP("NumberLiteral", "", 1));
			if (j == 1) {
                timerFlow = new Stopwatch();
				TemplateClass.instrum(117, "Assign", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).timerFlow", timerFlow));
                generateHValue(randomlyGenMatrix, Ai, Aj, Bi, Bj, n, 10, 14, true, 2);
				TemplateClass.instrum(118, "MethodInvocation", new AP("MethodInvocation", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int)", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).randomlyGenMatrix", randomlyGenMatrix), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Ai", Ai), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Aj", Aj), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Bi", Bi), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Bj", Bj), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).n", n), new AP("args NumberLiteral", "", 10), new AP("args NumberLiteral", "", 14), new AP("args BooleanLiteral", "", true), new AP("args NumberLiteral", "", 2));

                TemplateClass.instrum(120, "IfStatement", new AP("SimpleName", "Node.hValue", cell[Ai][Aj].hValue), new AP("NumberLiteral", "", -1), new AP("MethodInvocation", "pathList.ArrayList<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Bi", Bi), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Bj", Bj));
				if (cell[Ai][Aj].hValue!=-1&&pathList.contains(cell[Bi][Bj])) {
                      for (int i = 0; i < pathList.size() - 1; i++) {
						TemplateClass.instrum(121, "ForStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).i", i), new AP("MethodInvocation", "pathList.ArrayList<Node>.size()", ""), new AP("NumberLiteral", "", 1));
						gCost += pathList.get(i).gValue;
						TemplateClass.instrum(122, "Assign", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).gCost", gCost), new AP("SimpleName", "Node.gValue", pathList.get(i).gValue));
					}

                    System.out.println("Euclidean Path Found");
					TemplateClass.instrum(125, "MethodInvocation", new AP("MethodInvocation", "System.out.PrintStream.println(String)", ""), new AP("args StringLiteral", "", "Euclidean Path Found"));
                    System.out.println("Total Cost: " + gCost/10.0);
					TemplateClass.instrum(126, "MethodInvocation", new AP("MethodInvocation", "System.out.PrintStream.println(String)", ""), new AP("args StringLiteral", "", "Total Cost: "), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).gCost", gCost), new AP("args NumberLiteral", "", 10.0));
                    StdOut.println("Elapsed time = " + timerFlow.elapsedTime());
					TemplateClass.instrum(127, "MethodInvocation", new AP("MethodInvocation", "StdOut", ""), new AP("args StringLiteral", "", "Elapsed time = "), new AP("args MethodInvocation", "timerFlow", ""));
                    gCost = 0;
					TemplateClass.instrum(128, "Assign", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).gCost", gCost), new AP("NumberLiteral", "", 0));

                } else {

                    System.out.println("Euclidean Path Not found");
					TemplateClass.instrum(132, "MethodInvocation", new AP("MethodInvocation", "System.out.PrintStream.println(String)", ""), new AP("args StringLiteral", "", "Euclidean Path Not found"));
                    StdOut.println("Elapsed time = " + timerFlow.elapsedTime());
					TemplateClass.instrum(133, "MethodInvocation", new AP("MethodInvocation", "StdOut", ""), new AP("args StringLiteral", "", "Elapsed time = "), new AP("args MethodInvocation", "timerFlow", ""));

                }

                pathList.clear();
				TemplateClass.instrum(137, "MethodInvocation", new AP("MethodInvocation", "pathList.ArrayList<Node>.clear()", ""));
                closedList.clear();
				TemplateClass.instrum(138, "MethodInvocation", new AP("MethodInvocation", "closedList.ArrayList<Node>.clear()", ""));
            }

            TemplateClass.instrum(141, "IfStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).j", j), new AP("NumberLiteral", "", 2));
			if (j == 2) {
                timerFlow = new Stopwatch();
				TemplateClass.instrum(142, "Assign", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).timerFlow", timerFlow));
                generateHValue(randomlyGenMatrix, Ai, Aj, Bi, Bj, n, 10, 10, false, 3);
				TemplateClass.instrum(143, "MethodInvocation", new AP("MethodInvocation", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int)", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).randomlyGenMatrix", randomlyGenMatrix), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Ai", Ai), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Aj", Aj), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Bi", Bi), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Bj", Bj), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).n", n), new AP("args NumberLiteral", "", 10), new AP("args NumberLiteral", "", 10), new AP("args BooleanLiteral", "", false), new AP("args NumberLiteral", "", 3));

                TemplateClass.instrum(145, "IfStatement", new AP("SimpleName", "Node.hValue", cell[Ai][Aj].hValue), new AP("NumberLiteral", "", -1), new AP("MethodInvocation", "pathList.ArrayList<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Bi", Bi), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).Bj", Bj));
				if (cell[Ai][Aj].hValue!=-1&&pathList.contains(cell[Bi][Bj])) {
                    for (int i = 0; i < pathList.size() - 1; i++) {
						TemplateClass.instrum(146, "ForStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).i", i), new AP("MethodInvocation", "pathList.ArrayList<Node>.size()", ""), new AP("NumberLiteral", "", 1));
						gCost += pathList.get(i).gValue;
						TemplateClass.instrum(147, "Assign", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).gCost", gCost), new AP("SimpleName", "Node.gValue", pathList.get(i).gValue));
					}

                    System.out.println("Manhattan Path Found");
					TemplateClass.instrum(150, "MethodInvocation", new AP("MethodInvocation", "System.out.PrintStream.println(String)", ""), new AP("args StringLiteral", "", "Manhattan Path Found"));
                    System.out.println("Total Cost: " + gCost/10.0);
					TemplateClass.instrum(151, "MethodInvocation", new AP("MethodInvocation", "System.out.PrintStream.println(String)", ""), new AP("args StringLiteral", "", "Total Cost: "), new AP("args SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).gCost", gCost), new AP("args NumberLiteral", "", 10.0));
                    StdOut.println("Elapsed time = " + timerFlow.elapsedTime());
					TemplateClass.instrum(152, "MethodInvocation", new AP("MethodInvocation", "StdOut", ""), new AP("args StringLiteral", "", "Elapsed time = "), new AP("args MethodInvocation", "timerFlow", ""));
                    gCost = 0;
					TemplateClass.instrum(153, "Assign", new AP("SimpleName", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int).gCost", gCost), new AP("NumberLiteral", "", 0));

                } else {

                    System.out.println("Manhattan Path Not found");
					TemplateClass.instrum(157, "MethodInvocation", new AP("MethodInvocation", "System.out.PrintStream.println(String)", ""), new AP("args StringLiteral", "", "Manhattan Path Not found"));
                    StdOut.println("Elapsed time = " + timerFlow.elapsedTime());
					TemplateClass.instrum(158, "MethodInvocation", new AP("MethodInvocation", "StdOut", ""), new AP("args StringLiteral", "", "Elapsed time = "), new AP("args MethodInvocation", "timerFlow", ""));

                }

                pathList.clear();
				TemplateClass.instrum(162, "MethodInvocation", new AP("MethodInvocation", "pathList.ArrayList<Node>.clear()", ""));
                closedList.clear();
				TemplateClass.instrum(163, "MethodInvocation", new AP("MethodInvocation", "closedList.ArrayList<Node>.clear()", ""));
            }
        }

    }

    /**
     * @param hValue         Node type 2D Array (Matrix)
     * @param Ai             Starting point's y value
     * @param Aj             Starting point's x value
     * @param Bi             Ending point's y value
     * @param Bj             Ending point's x value
     * @param n              Length of one side of the matrix
     * @param v              Cost between 2 cells located horizontally or vertically next to each other
     * @param d              Cost between 2 cells located Diagonally next to each other
     * @param additionalPath Boolean to decide whether to calculate the cost of through the diagonal path
     */
    public static void generatePath(Node hValue[][], int Ai, int Aj, int Bi, int Bj, int n, int v, int d, boolean additionalPath) {

        TemplateClass.instrum(169, "MethodDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).hValue", hValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).Ai", Ai), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).Aj", Aj), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).Bi", Bi), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).Bj", Bj), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).n", n), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).v", v), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).d", d), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).additionalPath", additionalPath));
		//Creation of a PriorityQueue and the declaration of the Comparator
        PriorityQueue<Node> openList = new PriorityQueue<>(11, new Comparator() {
            @Override
            //Compares 2 Node objects stored in the PriorityQueue and Reorders the Queue according to the object which has the lowest fValue
            public int compare(Object cell1, Object cell2) {
                TemplateClass.instrum(184, "MethodDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid..compare(Object, Object).cell1", cell1), new AP("SimpleName", "PathFindingOnSquaredGrid..compare(Object, Object).cell2", cell2));
				
				return ((Node) cell1).fValue < ((Node) cell2).fValue ? -1 :
                        ((Node) cell1).fValue > ((Node) cell2).fValue ? 1 : 0;
            }
        });
		TemplateClass.instrum(183, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).openList", ""));

        //Adds the Starting cell inside the openList
        openList.add(cell[Ai][Aj]);
		TemplateClass.instrum(193, "MethodInvocation", new AP("MethodInvocation", "openList.PriorityQueue<Node>.add(Node)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).Ai", Ai), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).Aj", Aj));

        TemplateClass.instrum(196, "WhileStatement", new AP("BooleanLiteral", "", true));
		//Executes the rest if there are objects left inside the PriorityQueue
        while (true) {

            //Gets and removes the objects that's stored on the top of the openList and saves it inside node
            Node node = openList.poll();
			TemplateClass.instrum(199, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).node", node), new AP("MethodInvocation", "openList.PriorityQueue<Node>.poll()", ""));

            TemplateClass.instrum(202, "IfStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).node", node), new AP("NullLiteral", "", null));
			//Checks if whether node is empty and f it is then breaks the while loop
            if (node == null) {
                break;
            }

            TemplateClass.instrum(208, "IfStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).node", node), new AP("Array Begin", "", "{"), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).Bi", Bi), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).Bj", Bj), new AP("Array End", "", "}"));
			//Checks if whether the node returned is having the same node object values of the ending point
            //If it des then stores that inside the closedList and breaks the while loop
            if (node == cell[Bi][Bj]) {
                closedList.add(node);
				TemplateClass.instrum(209, "MethodInvocation", new AP("MethodInvocation", "closedList.ArrayList<Node>.add(Node)", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).node", node));
                break;
            }

            closedList.add(node);
			TemplateClass.instrum(213, "MethodInvocation", new AP("MethodInvocation", "closedList.ArrayList<Node>.add(Node)", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).node", node));

            //Left Cell
            try {
                TemplateClass.instrum(217, "IfStatement", new AP("SimpleName", "Node.hValue", cell[node.x][node.y - 1].hValue), new AP("NumberLiteral", "", -1), new AP("MethodInvocation", "openList.PriorityQueue<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1), new AP("MethodInvocation", "closedList.ArrayList<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
				if (cell[node.x][node.y - 1].hValue != -1
                        && !openList.contains(cell[node.x][node.y - 1])
                        && !closedList.contains(cell[node.x][node.y - 1])) {
                    double tCost = node.fValue + v;
					TemplateClass.instrum(220, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).tCost", tCost), new AP("QualifiedName", "Node.fValue", node.fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).v", v));
                    cell[node.x][node.y - 1].gValue = v;
					TemplateClass.instrum(221, "Assign", new AP("SimpleName", "Node.gValue", cell[node.x][node.y - 1].gValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).v", v));
                    double cost = cell[node.x][node.y - 1].hValue + tCost;
					TemplateClass.instrum(222, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost), new AP("SimpleName", "Node.hValue", cell[node.x][node.y - 1].hValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).tCost", tCost));
                    TemplateClass.instrum(223, "IfStatement", new AP("SimpleName", "Node.fValue", cell[node.x][node.y - 1].fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost), new AP("MethodInvocation", "openList.PriorityQueue<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
					if (cell[node.x][node.y - 1].fValue > cost || !openList.contains(cell[node.x][node.y - 1])) {
						cell[node.x][node.y - 1].fValue = cost;
						TemplateClass.instrum(224, "Assign", new AP("SimpleName", "Node.fValue", cell[node.x][node.y - 1].fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost));
					}

                    openList.add(cell[node.x][node.y - 1]);
					TemplateClass.instrum(227, "MethodInvocation", new AP("MethodInvocation", "openList.PriorityQueue<Node>.add(Node)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
                    cell[node.x][node.y - 1].parent = node;
					TemplateClass.instrum(228, "Assign", new AP("SimpleName", "Node.parent", cell[node.x][node.y - 1].parent), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).node", node));
                }
            } catch (IndexOutOfBoundsException e) {
            }

            //Right Cell
            try {
                TemplateClass.instrum(235, "IfStatement", new AP("SimpleName", "Node.hValue", cell[node.x][node.y + 1].hValue), new AP("NumberLiteral", "", -1), new AP("MethodInvocation", "openList.PriorityQueue<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1), new AP("MethodInvocation", "closedList.ArrayList<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
				if (cell[node.x][node.y + 1].hValue != -1
                        && !openList.contains(cell[node.x][node.y + 1])
                        && !closedList.contains(cell[node.x][node.y + 1])) {
                    double tCost = node.fValue + v;
					TemplateClass.instrum(238, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).tCost", tCost), new AP("QualifiedName", "Node.fValue", node.fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).v", v));
                    cell[node.x][node.y + 1].gValue = v;
					TemplateClass.instrum(239, "Assign", new AP("SimpleName", "Node.gValue", cell[node.x][node.y + 1].gValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).v", v));
                    double cost = cell[node.x][node.y + 1].hValue + tCost;
					TemplateClass.instrum(240, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost), new AP("SimpleName", "Node.hValue", cell[node.x][node.y + 1].hValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).tCost", tCost));
                    TemplateClass.instrum(241, "IfStatement", new AP("SimpleName", "Node.fValue", cell[node.x][node.y + 1].fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost), new AP("MethodInvocation", "openList.PriorityQueue<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
					if (cell[node.x][node.y + 1].fValue > cost || !openList.contains(cell[node.x][node.y + 1])) {
						cell[node.x][node.y + 1].fValue = cost;
						TemplateClass.instrum(242, "Assign", new AP("SimpleName", "Node.fValue", cell[node.x][node.y + 1].fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost));
					}

                    openList.add(cell[node.x][node.y + 1]);
					TemplateClass.instrum(245, "MethodInvocation", new AP("MethodInvocation", "openList.PriorityQueue<Node>.add(Node)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
                    cell[node.x][node.y + 1].parent = node;
					TemplateClass.instrum(246, "Assign", new AP("SimpleName", "Node.parent", cell[node.x][node.y + 1].parent), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).node", node));
                }
            } catch (IndexOutOfBoundsException e) {
            }

            //Bottom Cell
            try {
                TemplateClass.instrum(253, "IfStatement", new AP("SimpleName", "Node.hValue", cell[node.x + 1][node.y].hValue), new AP("NumberLiteral", "", -1), new AP("MethodInvocation", "openList.PriorityQueue<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP("MethodInvocation", "closedList.ArrayList<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y));
				if (cell[node.x + 1][node.y].hValue != -1
                        && !openList.contains(cell[node.x + 1][node.y])
                        && !closedList.contains(cell[node.x + 1][node.y])) {
                    double tCost = node.fValue + v;
					TemplateClass.instrum(256, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).tCost", tCost), new AP("QualifiedName", "Node.fValue", node.fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).v", v));
                    cell[node.x + 1][node.y].gValue = v;
					TemplateClass.instrum(257, "Assign", new AP("SimpleName", "Node.gValue", cell[node.x + 1][node.y].gValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).v", v));
                    double cost = cell[node.x + 1][node.y].hValue + tCost;
					TemplateClass.instrum(258, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost), new AP("SimpleName", "Node.hValue", cell[node.x + 1][node.y].hValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).tCost", tCost));
                    TemplateClass.instrum(259, "IfStatement", new AP("SimpleName", "Node.fValue", cell[node.x + 1][node.y].fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost), new AP("MethodInvocation", "openList.PriorityQueue<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y));
					if (cell[node.x + 1][node.y].fValue > cost || !openList.contains(cell[node.x + 1][node.y])) {
						cell[node.x + 1][node.y].fValue = cost;
						TemplateClass.instrum(260, "Assign", new AP("SimpleName", "Node.fValue", cell[node.x + 1][node.y].fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost));
					}

                    openList.add(cell[node.x + 1][node.y]);
					TemplateClass.instrum(263, "MethodInvocation", new AP("MethodInvocation", "openList.PriorityQueue<Node>.add(Node)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y));
                    cell[node.x + 1][node.y].parent = node;
					TemplateClass.instrum(264, "Assign", new AP("SimpleName", "Node.parent", cell[node.x + 1][node.y].parent), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).node", node));
                }
            } catch (IndexOutOfBoundsException e) {
            }

            //Top Cell
            try {
                TemplateClass.instrum(271, "IfStatement", new AP("SimpleName", "Node.hValue", cell[node.x - 1][node.y].hValue), new AP("NumberLiteral", "", -1), new AP("MethodInvocation", "openList.PriorityQueue<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP("MethodInvocation", "closedList.ArrayList<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y));
				if (cell[node.x - 1][node.y].hValue != -1
                        && !openList.contains(cell[node.x - 1][node.y])
                        && !closedList.contains(cell[node.x - 1][node.y])) {
                    double tCost = node.fValue + v;
					TemplateClass.instrum(274, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).tCost", tCost), new AP("QualifiedName", "Node.fValue", node.fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).v", v));
                    cell[node.x - 1][node.y].gValue = v;
					TemplateClass.instrum(275, "Assign", new AP("SimpleName", "Node.gValue", cell[node.x - 1][node.y].gValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).v", v));
                    double cost = cell[node.x - 1][node.y].hValue + tCost;
					TemplateClass.instrum(276, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost), new AP("SimpleName", "Node.hValue", cell[node.x - 1][node.y].hValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).tCost", tCost));
                    TemplateClass.instrum(277, "IfStatement", new AP("SimpleName", "Node.fValue", cell[node.x - 1][node.y].fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost), new AP("MethodInvocation", "openList.PriorityQueue<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y));
					if (cell[node.x - 1][node.y].fValue > cost || !openList.contains(cell[node.x - 1][node.y])) {
						cell[node.x - 1][node.y].fValue = cost;
						TemplateClass.instrum(278, "Assign", new AP("SimpleName", "Node.fValue", cell[node.x - 1][node.y].fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost));
					}

                    openList.add(cell[node.x - 1][node.y]);
					TemplateClass.instrum(281, "MethodInvocation", new AP("MethodInvocation", "openList.PriorityQueue<Node>.add(Node)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y));
                    cell[node.x - 1][node.y].parent = node;
					TemplateClass.instrum(282, "Assign", new AP("SimpleName", "Node.parent", cell[node.x - 1][node.y].parent), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).node", node));
                }
            } catch (IndexOutOfBoundsException e) {
            }

            TemplateClass.instrum(287, "IfStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).additionalPath", additionalPath));
			if (additionalPath) {

                //TopLeft Cell
                try {
                    TemplateClass.instrum(291, "IfStatement", new AP("SimpleName", "Node.hValue", cell[node.x - 1][node.y - 1].hValue), new AP("NumberLiteral", "", -1), new AP("MethodInvocation", "openList.PriorityQueue<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1), new AP("MethodInvocation", "closedList.ArrayList<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
					if (cell[node.x - 1][node.y - 1].hValue != -1
                            && !openList.contains(cell[node.x - 1][node.y - 1])
                            && !closedList.contains(cell[node.x - 1][node.y - 1])) {
                        double tCost = node.fValue + d;
						TemplateClass.instrum(294, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).tCost", tCost), new AP("QualifiedName", "Node.fValue", node.fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).d", d));
                        cell[node.x - 1][node.y - 1].gValue = d;
						TemplateClass.instrum(295, "Assign", new AP("SimpleName", "Node.gValue", cell[node.x - 1][node.y - 1].gValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).d", d));
                        double cost = cell[node.x - 1][node.y - 1].hValue + tCost;
						TemplateClass.instrum(296, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost), new AP("SimpleName", "Node.hValue", cell[node.x - 1][node.y - 1].hValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).tCost", tCost));
                        TemplateClass.instrum(297, "IfStatement", new AP("SimpleName", "Node.fValue", cell[node.x - 1][node.y - 1].fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost), new AP("MethodInvocation", "openList.PriorityQueue<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
						if (cell[node.x - 1][node.y - 1].fValue > cost || !openList.contains(cell[node.x - 1][node.y - 1])) {
							cell[node.x - 1][node.y - 1].fValue = cost;
							TemplateClass.instrum(298, "Assign", new AP("SimpleName", "Node.fValue", cell[node.x - 1][node.y - 1].fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost));
						}

                        openList.add(cell[node.x - 1][node.y - 1]);
						TemplateClass.instrum(301, "MethodInvocation", new AP("MethodInvocation", "openList.PriorityQueue<Node>.add(Node)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
                        cell[node.x - 1][node.y - 1].parent = node;
						TemplateClass.instrum(302, "Assign", new AP("SimpleName", "Node.parent", cell[node.x - 1][node.y - 1].parent), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).node", node));
                    }
                } catch (IndexOutOfBoundsException e) {
                }

                //TopRight Cell
                try {
                    TemplateClass.instrum(309, "IfStatement", new AP("SimpleName", "Node.hValue", cell[node.x - 1][node.y + 1].hValue), new AP("NumberLiteral", "", -1), new AP("MethodInvocation", "openList.PriorityQueue<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1), new AP("MethodInvocation", "closedList.ArrayList<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
					if (cell[node.x - 1][node.y + 1].hValue != -1
                            && !openList.contains(cell[node.x - 1][node.y + 1])
                            && !closedList.contains(cell[node.x - 1][node.y + 1])) {
                        double tCost = node.fValue + d;
						TemplateClass.instrum(312, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).tCost", tCost), new AP("QualifiedName", "Node.fValue", node.fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).d", d));
                        cell[node.x - 1][node.y + 1].gValue = d;
						TemplateClass.instrum(313, "Assign", new AP("SimpleName", "Node.gValue", cell[node.x - 1][node.y + 1].gValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).d", d));
                        double cost = cell[node.x - 1][node.y + 1].hValue + tCost;
						TemplateClass.instrum(314, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost), new AP("SimpleName", "Node.hValue", cell[node.x - 1][node.y + 1].hValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).tCost", tCost));
                        TemplateClass.instrum(315, "IfStatement", new AP("SimpleName", "Node.fValue", cell[node.x - 1][node.y + 1].fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost), new AP("MethodInvocation", "openList.PriorityQueue<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
						if (cell[node.x - 1][node.y + 1].fValue > cost || !openList.contains(cell[node.x - 1][node.y + 1])) {
							cell[node.x - 1][node.y + 1].fValue = cost;
							TemplateClass.instrum(316, "Assign", new AP("SimpleName", "Node.fValue", cell[node.x - 1][node.y + 1].fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost));
						}

                        openList.add(cell[node.x - 1][node.y + 1]);
						TemplateClass.instrum(319, "MethodInvocation", new AP("MethodInvocation", "openList.PriorityQueue<Node>.add(Node)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
                        cell[node.x - 1][node.y + 1].parent = node;
						TemplateClass.instrum(320, "Assign", new AP("SimpleName", "Node.parent", cell[node.x - 1][node.y + 1].parent), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).node", node));
                    }
                } catch (IndexOutOfBoundsException e) {
                }

                //BottomLeft Cell
                try {
                    TemplateClass.instrum(327, "IfStatement", new AP("SimpleName", "Node.hValue", cell[node.x + 1][node.y - 1].hValue), new AP("NumberLiteral", "", -1), new AP("MethodInvocation", "openList.PriorityQueue<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1), new AP("MethodInvocation", "closedList.ArrayList<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
					if (cell[node.x + 1][node.y - 1].hValue != -1
                            && !openList.contains(cell[node.x + 1][node.y - 1])
                            && !closedList.contains(cell[node.x + 1][node.y - 1])) {
                        double tCost = node.fValue + d;
						TemplateClass.instrum(330, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).tCost", tCost), new AP("QualifiedName", "Node.fValue", node.fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).d", d));
                        cell[node.x + 1][node.y - 1].gValue = d;
						TemplateClass.instrum(331, "Assign", new AP("SimpleName", "Node.gValue", cell[node.x + 1][node.y - 1].gValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).d", d));
                        double cost = cell[node.x + 1][node.y - 1].hValue + tCost;
						TemplateClass.instrum(332, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost), new AP("SimpleName", "Node.hValue", cell[node.x + 1][node.y - 1].hValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).tCost", tCost));
                        TemplateClass.instrum(333, "IfStatement", new AP("SimpleName", "Node.fValue", cell[node.x + 1][node.y - 1].fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost), new AP("MethodInvocation", "openList.PriorityQueue<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
						if (cell[node.x + 1][node.y - 1].fValue > cost || !openList.contains(cell[node.x + 1][node.y - 1])) {
							cell[node.x + 1][node.y - 1].fValue = cost;
							TemplateClass.instrum(334, "Assign", new AP("SimpleName", "Node.fValue", cell[node.x + 1][node.y - 1].fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost));
						}

                        openList.add(cell[node.x + 1][node.y - 1]);
						TemplateClass.instrum(337, "MethodInvocation", new AP("MethodInvocation", "openList.PriorityQueue<Node>.add(Node)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
                        cell[node.x + 1][node.y - 1].parent = node;
						TemplateClass.instrum(338, "Assign", new AP("SimpleName", "Node.parent", cell[node.x + 1][node.y - 1].parent), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).node", node));
                    }
                } catch (IndexOutOfBoundsException e) {
                }

                //BottomRight Cell
                try {
                    TemplateClass.instrum(345, "IfStatement", new AP("SimpleName", "Node.hValue", cell[node.x + 1][node.y + 1].hValue), new AP("NumberLiteral", "", -1), new AP("MethodInvocation", "openList.PriorityQueue<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1), new AP("MethodInvocation", "closedList.ArrayList<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
					if (cell[node.x + 1][node.y + 1].hValue != -1
                            && !openList.contains(cell[node.x + 1][node.y + 1])
                            && !closedList.contains(cell[node.x + 1][node.y + 1])) {
                        double tCost = node.fValue + d;
						TemplateClass.instrum(348, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).tCost", tCost), new AP("QualifiedName", "Node.fValue", node.fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).d", d));
                        cell[node.x + 1][node.y + 1].gValue = d;
						TemplateClass.instrum(349, "Assign", new AP("SimpleName", "Node.gValue", cell[node.x + 1][node.y + 1].gValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).d", d));
                        double cost = cell[node.x + 1][node.y + 1].hValue + tCost;
						TemplateClass.instrum(350, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost), new AP("SimpleName", "Node.hValue", cell[node.x + 1][node.y + 1].hValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).tCost", tCost));
                        TemplateClass.instrum(351, "IfStatement", new AP("SimpleName", "Node.fValue", cell[node.x + 1][node.y + 1].fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost), new AP("MethodInvocation", "openList.PriorityQueue<Node>.contains(Object)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
						if (cell[node.x + 1][node.y + 1].fValue > cost || !openList.contains(cell[node.x + 1][node.y + 1])) {
							cell[node.x + 1][node.y + 1].fValue = cost;
							TemplateClass.instrum(352, "Assign", new AP("SimpleName", "Node.fValue", cell[node.x + 1][node.y + 1].fValue), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).cost", cost));
						}

                        openList.add(cell[node.x + 1][node.y + 1]);
						TemplateClass.instrum(355, "MethodInvocation", new AP("MethodInvocation", "openList.PriorityQueue<Node>.add(Node)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner QualifiedName", "Node.x", node.x), new AP(" inner NumberLiteral", "", 1), new AP(" inner QualifiedName", "Node.y", node.y), new AP(" inner NumberLiteral", "", 1));
                        cell[node.x + 1][node.y + 1].parent = node;
						TemplateClass.instrum(356, "Assign", new AP("SimpleName", "Node.parent", cell[node.x + 1][node.y + 1].parent), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).node", node));
                    }
                } catch (IndexOutOfBoundsException e) {
                }
            }
        }

        /*for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                System.out.print(cell[i][j].fValue + "    ");
            }
            System.out.println();
        }*/

        //Assigns the last Object in the closedList to the endNode variable
        Node endNode = closedList.get(closedList.size() - 1);
		TemplateClass.instrum(371, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).endNode", endNode), new AP("MethodInvocation", "closedList.ArrayList<Node>.get(int)", ""), new AP("args MethodInvocation", "closedList.ArrayList<Node>.size()", ""), new AP("args NumberLiteral", "", 1));

        TemplateClass.instrum(375, "WhileStatement", new AP("QualifiedName", "Node.parent", endNode.parent), new AP("NullLiteral", "", null));
		//Checks if whether the endNode variable currently has a parent Node. if it doesn't then stops moving forward.
        //Stores each parent Node to the PathList so it is easier to trace back the final path
        while (endNode.parent != null) {
            Node currentNode = endNode;
			TemplateClass.instrum(376, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).currentNode", currentNode), new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).endNode", endNode));
            pathList.add(currentNode);
			TemplateClass.instrum(377, "MethodInvocation", new AP("MethodInvocation", "pathList.ArrayList<Node>.add(Node)", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).currentNode", currentNode));
            endNode = endNode.parent;
			TemplateClass.instrum(378, "Assign", new AP("SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).endNode", endNode), new AP("QualifiedName", "Node.parent", endNode.parent));
        }

        pathList.add(cell[Ai][Aj]);
		TemplateClass.instrum(381, "MethodInvocation", new AP("MethodInvocation", "pathList.ArrayList<Node>.add(Node)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).Ai", Ai), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.generatePath(Node[][], int, int, int, int, int, int, int, boolean).Aj", Aj));
        //Clears the openList
        openList.clear();
		TemplateClass.instrum(383, "MethodInvocation", new AP("MethodInvocation", "openList.PriorityQueue<Node>.clear()", ""));

        System.out.println();
		TemplateClass.instrum(385, "MethodInvocation", new AP("MethodInvocation", "System.out.PrintStream.println()", ""));

    }

    public static void main(String[] args) {
        TemplateClass.instrum(389, "MethodDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.main(String[]).args", args));
		TemplateClass.instrum(390, "IfStatement", new AP("QualifiedName", "length", args.length), new AP("NumberLiteral", "", 6));
		if(args.length != 6) {
			System.out.println("Grid size , probability and co-ordinates must be given");
			TemplateClass.instrum(391, "MethodInvocation", new AP("MethodInvocation", "System.out.PrintStream.println(String)", ""), new AP("args StringLiteral", "", "Grid size , probability and co-ordinates must be given"));
		} else {
            int gridSize = Integer.parseInt(args[0]);
			TemplateClass.instrum(393, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.main(String[]).gridSize", gridSize), new AP("MethodInvocation", "Integer.parseInt(String)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.main(String[]).args", args), new AP(" inner NumberLiteral", "", 0));
            double probability = Double.parseDouble(args[1]);
			TemplateClass.instrum(394, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.main(String[]).probability", probability), new AP("MethodInvocation", "Double.Double.parseDouble(String)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.main(String[]).args", args), new AP(" inner NumberLiteral", "", 1));
            System.out.println(String.format("Grid Size - %d ; Probability - %f",gridSize,probability));
			TemplateClass.instrum(395, "MethodInvocation", new AP("MethodInvocation", "System.out.PrintStream.println(char[])", ""), new AP("args MethodInvocation", "String.String.format(String, Object[])", ""), new AP("args StringLiteral", "", "Grid Size - %d ; Probability - %f"), new AP("args SimpleName", "PathFindingOnSquaredGrid.main(String[]).gridSize", gridSize), new AP("args SimpleName", "PathFindingOnSquaredGrid.main(String[]).probability", probability));
            int y1 = Integer.parseInt(args[2]);
			TemplateClass.instrum(396, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.main(String[]).y1", y1), new AP("MethodInvocation", "Integer.parseInt(String)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.main(String[]).args", args), new AP(" inner NumberLiteral", "", 2));
            int x1 = Integer.parseInt(args[2]);
			TemplateClass.instrum(397, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.main(String[]).x1", x1), new AP("MethodInvocation", "Integer.parseInt(String)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.main(String[]).args", args), new AP(" inner NumberLiteral", "", 2));
            int y2 = Integer.parseInt(args[2]);
			TemplateClass.instrum(398, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.main(String[]).y2", y2), new AP("MethodInvocation", "Integer.parseInt(String)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.main(String[]).args", args), new AP(" inner NumberLiteral", "", 2));
            int x2 = Integer.parseInt(args[2]);
			TemplateClass.instrum(399, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.main(String[]).x2", x2), new AP("MethodInvocation", "Integer.parseInt(String)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.main(String[]).args", args), new AP(" inner NumberLiteral", "", 2));
            System.out.println(String.format("Coordinates 1 - (%d %d), Coordinates 2 - (%d %d)",x1,y1,x2,y2));
			TemplateClass.instrum(400, "MethodInvocation", new AP("MethodInvocation", "System.out.PrintStream.println(char[])", ""), new AP("args MethodInvocation", "String.String.format(String, Object[])", ""), new AP("args StringLiteral", "", "Coordinates 1 - (%d %d), Coordinates 2 - (%d %d)"), new AP("args SimpleName", "PathFindingOnSquaredGrid.main(String[]).x1", x1), new AP("args SimpleName", "PathFindingOnSquaredGrid.main(String[]).y1", y1), new AP("args SimpleName", "PathFindingOnSquaredGrid.main(String[]).x2", x2), new AP("args SimpleName", "PathFindingOnSquaredGrid.main(String[]).y2", y2));
            menu(gridSize,probability,y1,x1,y2,x2);
			TemplateClass.instrum(401, "MethodInvocation", new AP("MethodInvocation", "PathFindingOnSquaredGrid.menu(int, double, int, int, int, int)", ""), new AP("args SimpleName", "PathFindingOnSquaredGrid.main(String[]).gridSize", gridSize), new AP("args SimpleName", "PathFindingOnSquaredGrid.main(String[]).probability", probability), new AP("args SimpleName", "PathFindingOnSquaredGrid.main(String[]).y1", y1), new AP("args SimpleName", "PathFindingOnSquaredGrid.main(String[]).x1", x1), new AP("args SimpleName", "PathFindingOnSquaredGrid.main(String[]).y2", y2), new AP("args SimpleName", "PathFindingOnSquaredGrid.main(String[]).x2", x2));

        }
		TemplateClass.finalizeInstrum();
    }
}