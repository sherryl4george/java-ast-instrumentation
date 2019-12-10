// Fibonacci Series using Dynamic Programming 
class App 
{ 
   static void fib(int n) 
    { 
        int a = 0, b = 1, c; 
        if (n == 0) {
            System.out.println(a + " ");
            return;
        }
        for (int i = 2; i <= n; i++) 
        { 
            c = a + b; 
            a = b; 
            b = c; 
        } 
        System.out.println(b+ " ");
    } 
  
    public static void main (String args[]) 
    { 
        int n = 9; 
	if(args.length > 0)
	 n = Integer.parseInt(args[0]);
        fib(n); 
    }  
} 

