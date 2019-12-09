#### AST Parsing and Instrumentation -

The parsing and instrumentation is done in multiple steps. 
1. AST Parsing
    1. Block Rewrite
    2. Code Rewrite 
2. Instrumentation  

###### 1. AST Parsing
This consists of Block rewriting and code rewriting. This is done as a first step to transform the code, before we begin instrumentation. 
###### i. Block Rewrite
As part of block rewrite step, all singled statements inside control structures , are converted to blocks.

This is done for all control statements including, *for*, *do-while*, *while*, *for-each*, *if-else if-else*. 
This is done to ensure that when we need to add an additional logging statement, we do not need to handle the absence of blocks. 

*Example* - 
```
int i = 3;
if(i < 2)
    System.out.println("hello");
else
    System.out.println("hi"); 
```
is transformed to
```
int i = 3;
if(i < 2) {
System.out.println("hello");
}
else {
System.out.println("hi");
}
```
###### ii. Code Rewrite
In the code rewrite step, we transform the method invocations in the expressions in looping constructs, *for*, *while* and *do-while* into
single assignments. 

This is only  done for simple infix expressions with a single method invocation on either side of the operand; i.e. statements of the below nature, are not handled. 
```
while(x() + x() + x() < 10) 
```

But for simple expressions involving one method invocation, we account for nested loops as well.

*Example* - 
```
while(x() < 5) {
    do {
        System.out.println("Hi");
    } while(x()< 2);
}

int x() {
    return 2; 
}
```
is transformed to 
```
int wh1 = x();
while(wh1 < 5) {
    int do1 = 0;
    do {   
        System.out.println(i);
        do1 = x();
    } while(do1 < 2);
    wh1 = x();
}

int x() {
    return 2; 
}
```
In addition to the loop construct transformation, we also add imports to include the Template class package (*This class includes the instrumentation method that is inserted into the original source*) and an additional last statement in the main() method to disconnect 
the socket (*This is part of the IPC to communicate back with the launcher program*).

###### 3. Instrumentation
Instrumentation is added to capture the variables, the bindings and their values in a Java source program. As part of this implementation, we handle the following constructs through visitors. 
1. Control structures - *for*, *while*, *do-while*, *for-each*, *if-else-if-else*, *switch*

    *Sample statement* -
    ```
    for(int i=0; i < 10 ; i++) {
        /*Do something here.*/
    }
    ```
    *Instrumentation Statement inserted* -
    ```
    for (int x = 0; x < matrix.length; x++) 
    TemplateClass.instrum(39, "ForStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).x", x), new AP("QualifiedName", "length", matrix.length));
    ```
    *Resulting trace with details of Location, Statement type, Binding and variable value* - 
    ```
    Line: 39, SeenAt: "ForStatement"
    Name: "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).x", Type: "SimpleName", Value: "6"
    Name: "()", Type: "QualifiedName", Value: "10"
    ```
2. Return statements - Return statements at the end of a function call. 
    
    *Sample statement* -
    ```java
    return 2; 
    ```
    *Instrumentation Statement inserted* -
    ```
    return a;
    TemplateClass.instrum(21, "ReturnStatement", new AP("SimpleName", "PathFindingOnSquaredGrid.random(int, double).a", a));
    ```
    *Resulting trace with details of Location, Statement type, Binding and variable value* - 
    ```
    Line: 21, SeenAt: "ReturnStatement"
    Name: "PathFindingOnSquaredGrid.random(int, double).a", Type: "SimpleName", Value: "[[Z@27c20538"
    ```
3. Method Declarations - All method declarations along with the parameters in the method signatures. We only call out the formal parameters along with their bindings and values in the trace. The bindings include the name of the method themselves. 
    *Sample statement* -
   ```
    public static void main(String[] args) {
        /* Do something here */
    }
    ```
   *Instrumentation Statement inserted* -
   ```
   public static void main(String[] args) 
   TemplateClass.instrum(389, "MethodDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.main(String[]).args", args));
   ```
    *Resulting trace with details of Location, Statement type, Binding and variable value* - 
    ```
   Line: 389, SeenAt: "MethodDeclaration"
   Name: "PathFindingOnSquaredGrid.main(String[]).args", Type: "SimpleName", Value: "[Ljava.lang.String;@311d617d"
    ```       
4. Variable Declaration Statements - All variable declaration statements, including multiple declarations with initializers. 
     
    *Sample statement* -
    ```
    int i = 0;
    int i,j;
    int i = 0, j = 0;
    ```
     *Instrumentation Statement inserted* -
     ```
   gridSize = Integer.parseInt(args[0]);
   TemplateClass.instrum(393, "VariableDeclaration", new AP("SimpleName", "PathFindingOnSquaredGrid.main(String[]).gridSize", gridSize), new AP("MethodInvocation", "Integer.parseInt(String)", ""), new AP("inner SimpleName", "PathFindingOnSquaredGrid.main(String[]).args", args), new AP(" inner NumberLiteral", "", 0));
    ```       
   *Resulting trace with details of Location, Statement type, Binding and variable value* - 
     ```
    Line: 393, SeenAt: "VariableDeclaration"
    Name: "PathFindingOnSquaredGrid.main(String[]).gridSize", Type: "SimpleName", Value: "10"
    Name: "Integer.parseInt(String)", Type: "MethodInvocation", Value: ""
    Name: "PathFindingOnSquaredGrid.main(String[]).args", Type: "inner SimpleName", Value: "[Ljava.lang.String;@311d617d"
    Name: "", Type: " inner NumberLiteral", Value: "0"
   
   // This translates to gridSize = Integer.parseInt(args[0]). inner SimpleName is a type that is used to signify array access. 
     ```      
5. Expression statements - We handle instrumentation of a lot of expression statement constructs as provided by the Java language. Below is a list of constructs that we handle in our instrumentation. 
    *Sample Statements* -
    ```
    i = x();                     //Assignment (with a method invocation).
    i = i++;                     //Assignment (postfix expression).
    i = -2;                      //Assignment (prefix expression).
    i = x() + 2;                 //Assignment (infix expression).
    cell[y][x] = new Node(y, x); //Class Instance creation.
    cell[y] = Node.y             //Field Access.
    if (node == cell[Bi][Bj])    //Array access.
    cell[0][1] = 2;              //Array initialization.
    ```
    *Sample Instrumentation Statement inserted* -
    ```
    cell[y][x] = new Node(y, x);
    TemplateClass.instrum(41, "Assign", new AP("Array Begin", "", "{"), new AP("inner SimpleName", "PathFindingOnSquaredGrid.cell", cell), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).y", y), new AP(" inner SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).x", x), new AP("Array End", "", "}"), new AP("ClassInstanceCreation", "Node", "Node.Node(int, int)"), new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).y", y), new AP("SimpleName", "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).x", x));
    ```
    *Resulting trace with details of Location, Statement type, Binding and variable value* - 
   ```
   Line: 41, SeenAt: "Assign"
   Name: "", Type: "Array Begin", Value: "{"
   Name: "PathFindingOnSquaredGrid.cell", Type: "inner SimpleName", Value: "[[LNode;@72d818d1"
   Name: "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).y", Type: " inner SimpleName", Value: "0"
   Name: "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).x", Type: " inner SimpleName", Value: "0"
   Name: "", Type: "Array End", Value: "}"
   Name: "Node", Type: "ClassInstanceCreation", Value: "Node.Node(int, int)"
   Name: "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).y", Type: "SimpleName", Value: "0"
   Name: "PathFindingOnSquaredGrid.generateHValue(boolean[][], int, int, int, int, int, int, int, boolean, int).x", Type: "SimpleName", Value: "0"
   
   This shows that an array is assigned a value new Node(y,x) 
   ```