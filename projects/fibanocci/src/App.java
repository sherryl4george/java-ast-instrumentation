// Fibonacci Series using Dynamic Programming
class App
{
	// Method to print first n Fibonacci Numbers
	static void printFibonacciNumbers(int n)
	{
		int f1 = 0, f2 = 1, i;

		if (n < 1)
			return;

		for (i = 1; i <= n; i++)
		{
			System.out.print(f2+" ");
			int next = f1 + f2;
			f1 = f2;
			f2 = next;
		}
	}

	// Driver Code
	public static void main(String[] args)
	{
		int n = 9;
		if(args.length > 0)
			n = Integer.parseInt(args[0]);
		printFibonacciNumbers(n);
	}
} 

