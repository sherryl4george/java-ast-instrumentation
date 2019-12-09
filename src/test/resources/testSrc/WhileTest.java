public class WhileTest {
  private static int doSomething() {
    int x = 0;
    x++;
    return x;
  }
  private static void doSomethingAgain() {
    while(doSomething() < 5) {
      System.out.println(5);
    }
  }
}
