public class DoTest {
  private static int doSomething() {
    int x = 0;
    x++;
    return x;
  }
  private static void doSomethingAgain() {
    do{
      System.out.println(5);
    }while(doSomething() < 5);
  }
}
