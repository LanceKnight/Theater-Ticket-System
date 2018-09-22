import java.util.*;
import java.util.concurrent.Semaphore;
import java.lang.*;
import java.io.*;


public class Movie{	
	static LinkedList<Integer> line = new LinkedList<Integer>();
	static LinkedList<Integer> con_line = new LinkedList<Integer>();
	private static final int customer_total_num = 300;
	private static final int box_total_num = 2;
	private static Semaphore[] check_result = new Semaphore[ customer_total_num ];
	private static Semaphore[] leaves = new Semaphore[ customer_total_num ];	
	private static Semaphore box_line = new Semaphore( box_total_num, true );
	private static Semaphore customer_ready_ticket = new Semaphore( 0, true );
	private static Semaphore movie_told[] = new Semaphore[ customer_total_num ]; 
	private static Semaphore money_box = new Semaphore( 0, true );
	private static Semaphore box_ready = new Semaphore( 0, true );
	private static Semaphore mutex1 = new Semaphore( 1, true );
	private static Semaphore mutex2 = new Semaphore( 1, true );
	private static Semaphore mutex3 = new Semaphore( 1, true );
	private static Semaphore mutex4 = new Semaphore( 1, true );
	private static Semaphore initial = new Semaphore( 0, true );
	private static Semaphore concession_line = new Semaphore( 1, true );
	private static Semaphore check = new Semaphore( 1, true );
	private static Semaphore ticket_sold = new Semaphore( 0, true );
	private static Semaphore taker_ready = new Semaphore( 0, true);
	private static Semaphore order = new Semaphore( 0, true );
	private static Semaphore taker_line = new Semaphore( 1, true );
	private static Semaphore fill_order = new Semaphore( 0, true );
	private static Semaphore enter_theater = new Semaphore ( 0, true );
	private static Semaphore leave_concession = new Semaphore( 0, true );
	private static Semaphore ticket_torn = new Semaphore( 0, true );	
	private static Semaphore know_movie[] = new Semaphore[customer_total_num];

	private static int movie[] = new int[ customer_total_num ];
	private static int food[] = new int[ customer_total_num ];
	private static int custNum = 0;
	private static int boxNum = 0;
	private static int rest_cust = customer_total_num;
	private static boolean everyone_leaves = false;
	private static boolean sold_out = false;
	private static int movie_ticket[] = new int[5];
	private static String[] movie_names = new String[5];

	public static class Initial implements Runnable{
		public void run(){
			for( int i = 0; i < customer_total_num; i++ ){
				check_result[i] = new Semaphore( 0, true );
				movie_told[i] = new Semaphore( 0, true );
				leaves[i] = new Semaphore( 0, true );
				know_movie[i] = new Semaphore(0, true);
			}
			for ( int i = 0; i < customer_total_num + box_total_num; i++ ){
			initial.release();
			}
			//System.out.println("Initialized" );//
		}

	}



	public class Customer implements Runnable{
		Random rand = new Random();
		
		public void run(){
			
			this.wait( initial );
			
			
			int custNO = 0;
			
		
			this.wait( box_line );
			this.wait( mutex1 );
			custNO = custNum;
			line.add( custNO );
			custNum++;
			//System.out.println("customer " + custNO + ":I am in and I got a number");//
			
			mutex1.release();
			tell_movie( custNO );
			System.out.println( "Customer " + custNO + " buying ticket to " + movie_name( movie[custNO]) );
			know_movie[custNO].release();
			customer_ready_ticket.release();
			
		
			wait( box_ready );
			

			//System.out.println("customer " + custNO + ": I told the movie" );//	
			movie_told[ custNO ].release();
			this.wait( check_result[ custNO ] );
			
			//System.out.println("customer " + custNO + " :I know the result");//
			if( sold_out == true ){
				//System.out.println("customer " + custNO + ": no ticket and I will leave");
				System.out.println( "No ticket left for " + movie_name( movie[custNO] ) + " for customer " + custNO );
				System.out.println("Joined customer " + custNO );
				leaves[custNO].release();
			}
			else{
				//System.out.println( "Ticket taken from customer " + custNO);//
				this.pay( custNO, movie[custNO] );
				money_box.release();
				wait( ticket_sold );
				leaves[custNO].release();	
				

				//System.out.println(" see whether buy food");//
				int k = rand.nextInt( 100 );
				if(  k  < 50 ){
					
					//System.out.println("customer " + custNO + " I will buy food ");//
					this.wait( concession_line );
					this.wait( mutex4 );
					con_line.add( custNO );
					mutex4.release();

					this.tell_food( custNO );
					order.release();
					this.wait( fill_order );
					this.get_drink( custNO );
					leave_concession.release();
				}

				System.out.println("Customer " + custNO + " in line to see ticket taker" );
				this.wait( taker_line );
				taker_ready.release();
				this.wait( ticket_torn);
				System.out.println("Customer " + custNO + " enters theater to see movie " + movie_name( movie[custNO] ) );
				System.out.println("Joined customer " + custNO );
				enter_theater.release();

			}

		}
		

		String movie_name( int no ){
			String movie_name = "";
			switch( no ){
			

		
				case 0:
					movie_name = movie_names[0];//"101 Dalmatians";
					break;
				case 1:
					movie_name = movie_names[1];//]"Alice in Wonderland";
					break;
				case 2: 
					movie_name = movie_names[2];//"Beauty and the Beast";
					break;
				case 3:
					movie_name = movie_names[3];//"Finding Nemo";
					break;
				case 4:
					movie_name = movie_names[4];//"Toy Story";
					break;
			}
			
			return movie_name;
	
		}

		void pay( int i, int movie ){
			
			
			//This function does nothing,just represents that action
		}

		void tell_food( int i ){
			String foodName;
			food[i] = rand.nextInt(3);
			foodName = food_name( food[i] );
			System.out.println( "Customer " + i + " in line to buy "+ foodName );
		}

		String food_name( int i ){
			String foodName = "";
			switch( i ){
				case 0:
					foodName = "popcorn";
					break;
				case 1:
					foodName = "soda";
					break;
				case 2:
					foodName = "popcorn and soda";
					break;
				
			
			}
			return foodName;
		
		}

		void get_drink( int i ){
			String foodName;
			foodName = food_name( food[i] );
			System.out.println( "Customer " + i + " receives " + foodName );
		
		}


		void leave( int i ){
			rest_cust--;
			if( i == 0 ){
				everyone_leaves = true;
				System.out.println("every one leaves");
			}
			leaves[i].release();
		}


		

		void tell_movie( int i ){
			Random rand = new Random();
			movie[i] = ( int ) Math.ceil( rand.nextInt( 5 ) + 0 );
			
			//System.out.println("customer " + i + ": I want movie " + movie[i] );
			
		}

		void wait( Semaphore sem){
			try{
				sem.acquire();
			}
			catch( InterruptedException e ){
			}
		}
		
		
	
	}

	public class Box_Office_Agent implements Runnable{

		public void run(){
			int boxNO = 0;

			this.wait( initial );
		
			this.wait( mutex2 );
			boxNO = boxNum;
			boxNum++;
			mutex2.release();

			System.out.println("Box office Agent " + boxNO + " created");
			
			while( true ){

				wait( customer_ready_ticket );	
			
				this.wait( mutex1 );
				int i = line.poll();
				mutex1.release();
				
				wait( know_movie[i] );
				System.out.println("Box office Agent " + boxNO + " serving customer" + i );
				box_ready.release();
				wait( movie_told[i] );
				
				wait( check );
				this.check( i, movie[i] );
				check.release();

				check_result[i].release();
				if( sold_out != true ){
					wait( money_box );
					System.out.println( "Box office agent " + boxNO + " sold ticket for " + movie_name( movie[i] ) + " to customer " + i );
					
					try{
						Thread.sleep( 1500 );
					}
					catch( Exception e ){
					
					}
					ticket_sold.release();
				}
				wait( leaves[i] );
				box_line.release();
			
			}
		}
			
		void check( int i, int movie ){
			this.wait( mutex3 );
			//System.out.println("begin checking" );//
			if ( movie_ticket[ movie ] == 0 ){
				sold_out = true;
			}
			else{
				sold_out = false;
				movie_ticket[ movie ]--;
				
			}	
			mutex3.release();
			//System.out.println("finish checking");//
		}

		String movie_name( int no ){
			String movie_name = "";
			switch( no ){
				case 0:
					movie_name = movie_names[0];//"101 Dalmatians";
					break;
				case 1:
					movie_name = movie_names[1];//]"Alice in Wonderland";
					break;
				case 2: 
					movie_name = movie_names[2];//"Beauty and the Beast";
					break;
				case 3:
					movie_name = movie_names[3];//"Finding Nemo";
					break;
				case 4:
					movie_name = movie_names[4];//"Toy Story";
					break;
			}
			
			return movie_name;
	
		}


		void wait( Semaphore sem) {
		try{
			sem.acquire();
		}
		catch( InterruptedException e ){	
		}
		
		}


	}

	public static class Concession implements Runnable{
		public void run(){
			int custNO;
			while( true ){
				
				this.wait( order );
				
				this.wait( mutex4 );
				custNO = con_line.poll();
				mutex4.release();

				System.out.println( "Order for " + food_name( food[custNO] ) + " taken from custom " + custNO );
				try{
					Thread.sleep( 3000 );
				}
				catch( Exception e ){
				
				}
				this.fill_order( custNO );

				fill_order.release();
				this.wait( leave_concession );
				concession_line.release();
			}
		}

		void fill_order( int i ){
			System.out.println( "food given to customer " + i );
		
		}
		
		String food_name( int i ){
			String foodName = "";
			switch( i ){
				case 0:
					foodName = "popcorn";
					break;
				case 1:
					foodName = "soda";
					break;
				case 2:
					foodName = "popcorn and soda";
					break;
				
			
			}
			return foodName;
		
		}




		void wait( Semaphore sem ){
			try{
				sem.acquire();
			}
			catch( InterruptedException e ){	
			}
		}
	}

	public static class Ticket_taker implements Runnable{
		public void run(){
			while( true ){
				
				wait( taker_ready );
				try{
					Thread.sleep( 250 );
				}
				catch( Exception e ){
					
				}
				ticket_torn.release();
				this.wait( enter_theater );
				taker_line.release();
			}
			
		}

		void wait( Semaphore sem ){
			try{
				sem.acquire();
			}
			catch( InterruptedException e ){	
			}
		}
	
	}


   	public static void main(String[] args){
		
		File file = new File( args[0] );
		String part[];

		try{	
			Scanner sc = new Scanner( file );
			String line;
			int i = 0;
			
			while( sc.hasNextLine() ){
				line = sc.nextLine() ;
				part = line.split( "\t" );
				movie_names[i] = part[0];
				movie_ticket[i] = new Integer(  part[1] );
				//System.out.println( "========="+ movie_names[i] );
				i++;
			}
		}
		catch( Exception e ){
		
		}

		Customer cust[] = new Customer[ customer_total_num ];
		Box_Office_Agent box[] = new Box_Office_Agent[ box_total_num ];
		Concession con = new Concession();
		Ticket_taker tt = new Ticket_taker();
		Initial ini = new Initial();
		
		
		Thread customer[] = new Thread[customer_total_num];
		Thread box_office_agent[] = new Thread[ box_total_num ];
		Thread concession = new Thread( con );
		Thread initialization = new Thread( ini );
		Thread ticket_taker = new Thread( tt );
		
		// create thread
		for( int i = 0; i < customer_total_num; i++ ){
      		
			cust[i] = new Movie().new Customer();
			customer[i] = new Thread( cust[i] );
			customer[i].start();
		}

		for( int i = 0; i < box_total_num; i++ ){
		
			box[i] = new Movie().new Box_Office_Agent();
			box_office_agent[i] = new Thread( box[i] );
			box_office_agent[i].start();
		}
		
		initialization.start();
		concession.start();
		ticket_taker.start();
//		try{
//			Thread.sleep( 2 );	
//		}
//		catch( InterruptedException e ){
//		}
//
		try{
		 	
			for( int i = 0; i < customer_total_num; i++ ){
				customer[i].join();
			}
			
			System.exit(0);
			//for( int i = 0; i < box_total_num; i++ ){
				//box_office_agent[i].join();
			//}
		}
		catch ( InterruptedException e ){
		}
	}
}

