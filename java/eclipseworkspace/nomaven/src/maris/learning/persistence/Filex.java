package maris.learning.persistence;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/** this is another change too
 * Wrapper to java.io as java.io has many possible Exceptions caused by the system
 * @author nickmeet
 *
 */
public class Filex {
	private String fileName;		// for debugging
	private File f;
	/**
	 * Prints the lasts two stacks of the stacktrace
	 * @param ex
	 * @param msg cause of exception (System, user or bug) and explanation
	 */
	private static void stackTraceTop(Throwable ex, String msg){
		System.out.println( msg );
		System.out.println( "Generated message: "+ex.getMessage() );
		System.out.println( "at " + ex.getStackTrace()[0].toString() );
		System.out.println( "at " + ex.getStackTrace()[1].toString() );
	}
	/**
	 * Appends text file with string or saves string to a text file. 
	 * @param fileName Name of output text file. If it does not exist, it is created
	 * @param saveString String to write to file.
	 * @return true if successful, false otherwise.
	 */
	private boolean write(String saveString, boolean append) {
		boolean saved = false;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(this.f,append));
			try {
				bw.write(saveString);		// may throw IOException
				saved = true;
			} finally {
				bw.close();					// may throw IOException
			}
		} catch (IOException ex) {
			stackTraceTop(ex,"Error caused by: System\n"+
					"Can not save string \""+saveString+"\" to file "+this.fileName+"\n");
		}
		return saved;
	}
	/**
	 * For debugging
	 * @param args
	 */
	public static void main_io(String[] args) {
		Filex x = new Filex("./hello.txt");
		if(x.set("Hello world!"))
			System.out.println( x.get() );		
	}
	public static void main(String[] args) {
		MyLibrary testLibrary = new MyLibrary("Test Drive Library");
		Book b1 = new Book("War and Peace");
		b1.setAuthor("Tolstoy");
		Book b2 = new Book("Great Expectations");
		b2.setAuthor("Charles Dickens");
		
		Person jim = new Person();
		jim.setName("Jim");
		Person sue = new Person();
		sue.setName("Sue");
		
		testLibrary.addBook(b1);
		testLibrary.addBook(b2);
		testLibrary.addPerson(jim);
		testLibrary.addPerson(sue);
		Filex x = new Filex("./hello.xml");
		x.setXML(testLibrary);
		
		testLibrary.checkOut(b1, sue);	
		testLibrary.printStatus();
		((MyLibrary) x.getXML()).printStatus();//XML format simplifies detecting that no book was checked out
	}
	
	/*** Methods for file IO***/
	/**
	 * If file does not exist, it is created
	 * @param filename file name with the path. To give the name of a file
	 * called x.txt located at the current directory write "./x.txt"
	 */
	public Filex(String fileName){
		this.fileName = fileName;
		this.f = new File(fileName);
		try{
			if(f.isDirectory())
				stackTraceTop(new Throwable(),"Error caused by: System\n"+
						"This is not a file, it is a directory: "+fileName+"\n");
		}catch(SecurityException ex){
				stackTraceTop(ex,"Error caused by: System\n"+
						"Application has no write access to "+fileName+"\n");
			}
		try{
			f.createNewFile();
		}catch(IOException ex){
			stackTraceTop(ex,"Error caused by: System\n"+
					"File "+fileName+" does not exist and it can not be created\n");
		}
	}
	/**
	 * Saves string to a text file.
	 * @param fileName Name of output text file. If it does not exist, it is created
	 * @param saveString String to write to file.
	 * @return true if successful, false otherwise.
	 */
	public boolean set(String saveString) {
		return write(saveString,false);
	}
	/**
	 * Appends text file with string.
	 * @param fileName Name of output text file. If it does not exist, it is created
	 * @param saveString String to write to file.
	 * @return true if successful, false otherwise.
	 */
	public boolean add(String saveString) {
		return write(saveString,true);
	}
	/**
	 * Retrieve all text from file. Note that "\n" line feed is added to
	 * the end of the last line of the file.
	 * TODO: Also read file per line
	 * @return Contents of text file (as String)
	 */
	public String get() {
		BufferedReader br = null;	// is a performance wrapper to FileReader
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new FileReader(fileName));
			try {
				String s;
				while ((s = br.readLine()) != null)
					sb.append(s + "\n");	// add linefeed (\n) back since stripped by readline()
			} finally {
				br.close();
			}
		} catch(FileNotFoundException ex){
			stackTraceTop(ex,"Error caused by: System\n"+
					"File not found: \""+this.fileName+"\n");
		} catch (IOException ex) {
			stackTraceTop(ex,"Error caused by: System\n"+
					"Can not read string from file "+this.fileName+"\n");
		}
		return sb.toString();
	}
	/*** Methods for XML Serialization***/
	/**
	 * Stores object on file in XML format
	 * @param obj
	 */
	public void setXML(Object obj) {
		String xmlSerializedObj = new XStream(new DomDriver()).toXML(obj);
		set(xmlSerializedObj);
	}
	/**
	 * Gets the one object stored on file in XML format
	 * TODO: Also read file per top object
	 * @param obj
	 * @return null or an instance of class X that has to be casted to type X, as java is strongly typed
	 */
	public Object getXML() {
		String fromFile = get();
		return new XStream(new DomDriver()).fromXML(fromFile);
	}
}