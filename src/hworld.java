
public class hworld {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String s = "Requests per second:    179.65 [#/sec] (mean)";
		String a = " ";
		 
		if(s.substring(0, 19).equalsIgnoreCase("Requests per second")) {
			
			System.out.println("hey");
			
			//a = s.substring(24,30);
			//System.out.println(a);
			if(s.substring(27,28).contains(".")) 
			{
				int c = Integer.parseInt(s.substring(24,27));
				System.out.println(c);
				 
			}
			else{
				int c = Integer.parseInt(s.substring(24,28));
				System.out.println(c);
			}
			
			

			
		}
		System.out.println("hey1" + s.substring(0,19));

	}

}
