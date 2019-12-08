public class MainTest {
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
  public static void main(String[] args){
    doSomethingAgain();
  }
}
