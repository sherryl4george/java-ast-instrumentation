

import astparser.*;
/**
 * Created by Suwadith 2015214 on 3/28/2017.
 */

public class Node {

    int x;
    int y;
    double hValue;
    int gValue;
    double fValue;
    Node parent;


    public Node(int x, int y) {
        TemplateClass.instrum(15, "MethodDeclaration", new AP("SimpleName", "Node.Node(int, int).x", x), new AP("SimpleName", "Node.Node(int, int).y", y));
		this.x = x;
		TemplateClass.instrum(16, "Assign", new AP("SimpleName", "Node.x", this.x), new AP("SimpleName", "Node.Node(int, int).x", x));
        this.y = y;
		TemplateClass.instrum(17, "Assign", new AP("SimpleName", "Node.y", this.y), new AP("SimpleName", "Node.Node(int, int).y", y));
    }

}
