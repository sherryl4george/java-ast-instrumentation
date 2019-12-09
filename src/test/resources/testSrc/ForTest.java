public class ForTest {
  private static int doSomething() {
    int x = 0;
    x++;
    return x;
  }
  private static void doSomethingAgain() {
    for(int i=0; doSomething() < 5; i++) {
      System.out.println(5);
    }
  }
}
