import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

//This class reads n number of bytes from a text file at a time
public class nBytes {
	int blockSize;
	File fout;
	long numberofBlocks;
	byte[] array;
	
	//Read a file n bytes at a time (to handle large files)
	public byte[] readFile(int blockSize, File fout, int offset) throws IOException{
		this.blockSize = blockSize; //specify the size of the file block (in bytes)
		this.fout = fout;
		array = new byte[blockSize];
		InputStream in = new FileInputStream(this.fout);
		in.read(array, offset, blockSize); //read 8 bytes into the array
		in.close();
		return array;
	}
	
	/*
	public void writeFile(String line) throws IOException{
		File fout = new File("encrypted.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(fout,true));
		bw.write(line);
		bw.close();
	}
	*/
	
}

