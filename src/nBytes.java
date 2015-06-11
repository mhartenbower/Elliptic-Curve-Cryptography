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
		File file = new File("test.txt");
		long length = file.length();
		long blocks = (long)Math.ceil(length/blockSize);
		byte[] array = new byte[8];
		String[] result = new String[8];
		InputStream in = new FileInputStream(file);
		int offset = 0;
		for(int i = 0; i < blocks; i++){
			in.read(array, 0, blockSize);  
			for(int j = 0; j < array.length; j++){
				System.out.print(array[j] + " " );
			}
			System.out.println();
			for(int k = 0; k < array.length; k++){
				result[k] = Character.toString((char)array[k]);
				System.out.print(result[k] + " ");
			}
		}
		in.close();
		
	}

}

