import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;




//This class reads n number of bytes from a text file at a time
public class nBytes {
	
	
	
	public static void main(String[] args) throws IOException{
		int blockSize = 8;
		File file = new File("data10.txt");
		long length = file.length();
		long blocks = (long)Math.ceil((double)length/(double)blockSize);
		byte[] array = new byte[8];
		String[] result = new String[8];
		InputStream in = new FileInputStream(file);
		int offset = 0;
		for(int i = 0; i < blocks; i++){
			in.read(array, 0, blockSize);  
			for(int j = 0; j < array.length; j++){
			}
			for(int k = 0; k < array.length; k++){
				result[k] = Character.toString((char)array[k]);
			}
			array = new byte[8];
		}
		in.close();
		System.out.println("Done");
	}

}

