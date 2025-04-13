import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Bank_operation {
	static String url="THE_URL_OF_DB";
	static String user="root";
	static String password="YOUR_DB_PASSWORD";
	
	static Connection con;
	
	static Scanner a=new Scanner(System.in);
	
	 public static void main(String[] args) throws SQLException {
		 try {
			 con=DriverManager.getConnection(url, user, password);
			 con.setAutoCommit(false);
			 
			 if(!login()) {
				 return;
			 }
			
			 while(true) {
				 
				 System.out.println("");
				 System.out.println("----------BANKING MANAGEMENT-----------");
				 System.out.println("1.CREATE ACCOUNT");
				 System.out.println("2.SHOW BALANCE");
				 System.out.println("3.TRANSFER");
				 System.out.println("4.WITHDRAW");
				 System.out.println("5.DELETE ACCOUNT");
				 System.out.println("6.VIEW ACCOUNT");
				 System.out.println("7.DEPOSIT");
				 System.out.println("8.LOGOUT");
				 System.out.println("");
				 System.out.print("SELECT YOUR OPTION: ");
				 int opt=a.nextInt();
				 if(opt==1) {
					 createacc();
					 
				 }
				 if(opt==2) {
					 showBalance();
					 
				 }
				 if(opt==3) {
					 Transfer();
					 
				 }
				 if(opt==4) {
					 withdraw();
					 
				 }
				 if(opt==5) {
					 deleteAccount();
					 
				 }
				 if(opt==6) {
					 viewaccount();
					 
				 }
				 if(opt==7) {
					 deposit();
				 }
				 if(opt==8) {
					 break;
				 }
			 }
		 }
		 catch(Exception e) {
			 System.out.println(e);
		 }
		 finally {
			 System.out.println("HAVE A GOOD DAY");
			 }
	 }
	 
	 private static boolean login() throws Exception {
	       
	        System.out.print("Enter Username: ");
	        String username = a.nextLine();

	        System.out.print("Acc_no: ");
	        String acc_no = a.nextLine();

	        String query = "SELECT * FROM Accounts WHERE name=? AND acc_no=?";
	        PreparedStatement psmt = con.prepareStatement(query);
	        psmt.setString(1, username);
	        psmt.setString(2, acc_no);
	        ResultSet rs = psmt.executeQuery();

	        if (rs.next()) {
	            System.out.println("Login successful! Welcome, " + username);
	            return true; // Login successful
	        } else {
	            System.out.println("Invalid credentials");
	            return false; // Login failed
	        }
	    }

	 private static void deposit() throws SQLException {
		    String query = "UPDATE accounts SET balance = balance + ? WHERE acc_no = ?";
		    PreparedStatement stmt = con.prepareStatement(query);


		    System.out.print("Enter your Account Number: ");
		    int acc_no = a.nextInt();

		    System.out.print("Enter the amount to deposit: ");
		    double amount = a.nextDouble();

		    stmt.setDouble(1, amount); 
		    stmt.setInt(2, acc_no);    

		    try {
		        con.setAutoCommit(false);
		        int row = stmt.executeUpdate();
		        if (row > 0) {
		            System.out.println("Deposit successful!");
		            con.commit(); 
		        } else {
		            System.out.println("Account not found.");
		            con.rollback(); 
		        }
		    } catch (SQLException e) {
		        System.out.println("An error occurred during the deposit operation.");
		        e.printStackTrace();
		        con.rollback();
		    } finally {
		        try {
		            con.setAutoCommit(true);
		            stmt.close();
		        } catch (SQLException e) {
		            System.out.println("Error during cleanup: " + e);
		        }
		    }
		}

	private static void viewaccount() throws SQLException {
		 String query="select * from accounts where acc_no=?";
		 PreparedStatement stmt=con.prepareStatement(query);
		 System.out.println("ENTER YOUR ACC_NO: ");
		 int acc_no=a.nextInt();
		 stmt.setInt(1, acc_no);
		 
		 try {
			 ResultSet rs=stmt.executeQuery();
			 if(rs.next()) {
				 System.out.println("ACC_No: "+rs.getInt(1));
				 System.out.println("NAME OF THE ACCOUNT HOLDER: "+rs.getString(2));
				 System.out.println("AGE: "+rs.getInt(3));
				 System.out.println("PHONE NUMBER: "+rs.getString(4));
				 System.out.println("BALANCE: "+rs.getDouble(5));
			 }
			 else {
				 System.out.println("ACCOUNT NOT FOUND: ");
			 }
		 }
		 catch (Exception e) {
		        System.out.println("SOMETHING WENT WRONG");
		        e.printStackTrace();
		    } finally {
		        try {
		            if (stmt != null) stmt.close();
		        } catch (SQLException e) {
		            System.out.println("Error closing statement: " + e);
		        }
		    }
	}

	private static void deleteAccount() throws SQLException {
		 
		 String query="delete from accounts where acc_no=?";
		 PreparedStatement stmt=con.prepareStatement(query);
		 System.out.print("ENTER YOUR ACC_NO: ");
		 int acc_no=a.nextInt();
		 stmt.setInt(1, acc_no);
		 System.out.print("TO CONFIRM ENTER Y , TO CANCEL PRESS X");
		 char sel = a.next().charAt(0);
		    if (sel == 'Y') {
		        try {
		            con.setAutoCommit(false);  
		            
		            int row = stmt.executeUpdate();
		            
		            if (row > 0) {
		                con.commit();  
		                System.out.println("ACCOUNT DELETED SUCCESSFULLY");
		            } else {
		                con.rollback();  // Rollback if no rows were deleted
		                System.out.println("ACCOUNT NOT FOUND");
		            }
		        } catch (SQLException e) {
		            System.out.println("SOMETHING WENT WRONG");
		            e.printStackTrace();
		            con.rollback();  // Rollback in case of error
		        } finally {
		            try {
		                con.setAutoCommit(true);  // Reset auto-commit to true
		            } catch (SQLException sqlEx) {
		                System.out.println("Error restoring auto-commit: " + sqlEx);
		            }
		            
		            try {
		                if (stmt != null) {
		                    stmt.close();  // Close the PreparedStatement
		                }
		            } catch (SQLException sqlEx) {
		                System.out.println("Error closing PreparedStatement: " + sqlEx);
		            }
		        }
		    } else if (sel == 'X') {
		        System.out.println("ACCOUNT DELETION CANCELED");
		    } else {
		        System.out.println("INVALID SELECTION. PLEASE ENTER 'Y' OR 'X'.");
		    }
		}
	private static void withdraw() throws SQLException {
		 
		 String query="update accounts set balance=balance-? where acc_no=? AND balance>?";
		 PreparedStatement stmt=con.prepareStatement(query);
		 System.out.println("ENTER ACC_NO: ");
		 int acc_no=a.nextInt();
		 System.out.println("ENTER AMOUNT TO BE WITHDRAWN: ");
		 Double amount=a.nextDouble();
		 stmt.setDouble(1, amount);
		 stmt.setInt(2, acc_no);
		 stmt.setDouble(3, amount);
		 
		 try {
			 con.setAutoCommit(false);
			 int row=stmt.executeUpdate();
			 if(row>0) {
				 System.out.println("THE WITHDRAW IS SUCCESS");
				 con.commit();
			 }
			 else {
				 System.out.println("WITHDRAW FAILED");
				 con.rollback();
			 }

		 }
		 catch(Exception e) {
			 System.out.println("SOMETHING WENT WRONG");
			 e.printStackTrace();
		 }
		 
		 finally {
		        try {
		            con.setAutoCommit(true);  
		        } catch (SQLException sqlEx) {
		            System.out.println("Error restoring auto-commit: " + sqlEx);
		        }
		        
		        try {
		            if (stmt != null) {
		                stmt.close();
		            }
		        } catch (SQLException sqlEx) {
		            System.out.println("Error closing PreparedStatement: " + sqlEx);
		        }
		        
		        System.out.println("ENSURE YOU COLLECTED THE MONEY");
		    }
		 
		
	}
	private static void Transfer() throws SQLException {
		
		 String withdraw="update accounts set balance=balance-? where acc_no=? AND balance>=?";
		 String add="update accounts set balance=balance+? where acc_no=?";
		 PreparedStatement remove=con.prepareStatement(withdraw);
		 PreparedStatement depo=con.prepareStatement(add);
		 System.out.println("ENTER YOUR ACC_NO: ");
		 int acc_no=a.nextInt();
		 System.out.println("ENTER THE AMOUNT: ");
		 double amount=a.nextDouble();
		 remove.setDouble(1, amount);
		 remove.setInt(2,acc_no);
		 remove.setDouble(3, amount);

		 
		 
		 System.out.println("ENTER THE RECIEVERS ACC_NO: ");
		 int acc_no1=a.nextInt();
		 depo.setDouble(1, amount);
		 depo.setInt(2,acc_no1);
		 
		 try {
			 if (acc_no == acc_no1) {
				    System.out.println("Sender and receiver account cannot be the same.");
				    return;
				}
			 
			 if (amount <= 0) {
				    System.out.println("Transfer amount must be greater than 0.");
				    return;
				}
			 con.setAutoCommit(false);
			 int row=remove.executeUpdate();
			 int row1=depo.executeUpdate();
			 
			 if(row>0&&row1>0) {
				 System.out.println("TRANSACTION IS SUCESSFULL");
				 con.commit();
			 }
			 else {
				 System.out.println("TRANSACTION FAILED");
				 con.rollback();
			 }
			
		 }
		 catch (SQLException e) {
		        System.out.println("SOMETHING WENT WRONG");
		        System.out.println(e);
		        try {
		            con.rollback();  
		        } catch (SQLException sqlEx) {
		            System.out.println("Error during rollback: " + sqlEx);
		        }
		    } finally {
		        try {
		            con.setAutoCommit(true);
		            if (remove != null) remove.close();
		            if (depo != null) depo.close();
		            con.setAutoCommit(true);
		    
		        }
		         catch (SQLException sqlEx) {
		            System.out.println("Error restoring auto-commit: " + sqlEx);
		        }
		    }
	}

	private static void showBalance() throws SQLException {
		    a.nextLine();  // Clear input buffer

		    String query = "SELECT balance FROM accounts WHERE acc_no=?";
		    PreparedStatement stmt = con.prepareStatement(query);

		    int acc_no = 0;
		    boolean validInput = false;

		    
		    while (!validInput) {
		        try {
		            System.out.print("Enter your Account No: ");
		            acc_no = a.nextInt();
		            validInput = true;  
		        } catch (InputMismatchException e) {
		            System.out.println("Invalid input! Please enter a valid account number.");
		            a.nextLine();  
		        }
		    }

		    stmt.setInt(1, acc_no);
		    ResultSet rs = stmt.executeQuery();

		    if (rs.next()) {
		        System.out.println("Balance: " + rs.getInt(1));
		    } else {
		        System.out.println("Account not found.");
		    }
		}

	private static void createacc() throws SQLException {
		a.nextLine();
		System.out.println("CREATE YOUR OWN ACCOUNT");
		String query="insert into Accounts(acc_no,name,age,phone,balance) values(?,?,?,?,0)";
		PreparedStatement stmt=con.prepareStatement(query);
		System.out.print("ENTER YOUR ACCOUNT_NO :");
		int acc_no=a.nextInt();
		stmt.setInt(1,acc_no);
		a.nextLine();
		System.out.println("ENTER YOUR NAME: ");
		String name=a.nextLine();
		stmt.setString(2, name);
		System.out.println("ENTER YOUR AGE: ");
		int age=a.nextInt();
		stmt.setInt(3, age);
		System.out.println("ENTER YOUR MOBILE_NUMBER: ");
		a.nextLine();
		String num=a.nextLine();
		stmt.setString(4, num);
		
		
		try {
			con.setAutoCommit(false);
			int row=stmt.executeUpdate();
			if(row>0) {
				System.out.println("ACCOUNT HAS BEEN CREATED SUCESSFULLY");
				con.commit();
			}
		}
		catch(Exception e) {
			System.out.println(e);
			System.out.println("SOMETHING WENT WRONG");
			con.rollback();
		}
		finally{
			System.out.println("THANK YOU");
		}
	}
	
	
}
