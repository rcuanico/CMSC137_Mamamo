import com.cmsc137.inovejas.protobuf.Inovejas_Proto.*;

import java.io.*;
import java.util.*;
import java.lang.*;


public class Inovejas_Protobuf{

	public static void main(String[] args) {
		int choice = 0;
		Scanner sc = new Scanner(System.in);
		String name, filename;
		int age;

		do{
			System.out.println("== MENU ==");
			System.out.print("[1] Read \n[2] Write \n[3] Exit \nChoice: ");
			choice = sc.nextInt();
			sc.nextLine(); //catches \n

			if (choice == 2) { //write
				System.out.print("Enter name: ");
				name = sc.nextLine();

				System.out.print("Enter age: ");
				age = sc.nextInt();
				sc.nextLine(); //catches \n

				System.out.print("Enter filename: ");
				filename = sc.nextLine();

				//Create the 'Person' that has the name and age

				Person.Builder person = Person.newBuilder();

				person.setName(name);
				person.setAge(age);

				try{
					FileOutputStream output = new FileOutputStream(filename);
					person.build().writeTo(output);
					System.out.println("[!] Data Written to File");
				}catch(Exception e) {
					System.out.println(e);
				}
				
			}else if (choice == 1) { //read
				System.out.print("Enter filename: ");
				filename = sc.nextLine();
				try{
					Person person = Person.parseFrom(new FileInputStream(filename));
					System.out.println("Name: " + person.getName());
					System.out.println("Age: "+ person.getAge());
				}catch(Exception e){
					System.out.println(e);
				}

			}

		}while(choice!=3);
	}
}