public class ComtrolInstrum{
    public void controls(int i) {
        if(i > 0) {
            i++;
        }
        else if(i > 25) {
            i--;
        }
        else{
            System.out.println("do nothing");
        }
    }

    public void test() {
        int a = 0;
        while(a < 50) {
            controls(a);
            a++;
        }
        for(int i=0; i<a; i++) {
            System.out.println("Testing for");
        }
    }
}