package filesystem;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Filesystem {

	String file_store_location="./files.txt";
	Map<Integer,Boolean> block_status= new HashMap<>();
	RandomAccessFile file_store;
	List<FFile> files= new ArrayList<>();
	int file_metadata_size= 4096;//16 files
	int max_blocks=60*16;
	
	int total_size = 1024*19;
	class FFile{
		
		int file_num=-1;//2B
		String name="";//14 B
		List<Integer> inode_block_num;//240
		
	}
	void init() throws IOException {
		boolean file_exists=false;
		File file = new File(file_store_location);
		if(file.exists())
			file_exists=true;
		file_store = new RandomAccessFile(file_store_location, "rw");
		file_store.setLength(total_size);
		file_store.seek(0);
		
		for(int i=0;i<max_blocks;i++) {
			block_status.put(i, false);
		}
		
		//create a file // of size_atlease_200MB
		
		// if file exists, just get the handle;
		int seek_position;
		for(int i=0;i<16;i++) {
			FFile f = new FFile();
			seek_position= i*256;
			file_store.seek(seek_position);
			f.file_num=i;
		       //f.file_num=file_store.readInt();
	    	 
		   // seek_position = seek_position+4;
		    StringBuffer buff = new StringBuffer("");
		    
		    for(int j=0;j<7;j++) {
		    	file_store.seek(seek_position);	
		    	char c = file_store.readChar();
		    	if(c!='\0')
		    	 {
		    		buff.append(c);
		    		
		    	 }
		    	seek_position=seek_position+2;
		    }
		    f.name=buff.toString();
		    f.inode_block_num = new ArrayList<>();
		    for(int j=0;j<60;j++) {
		    	file_store.seek(seek_position);
		    	int n = file_store.readInt();
		    	if(n!=0)
		    	 {
		    		 
		    		 f.inode_block_num .add(n);
		    		 block_status.put(n, true);
		    	 
		    	 }
		    	seek_position= seek_position+4;
		    }
		    
		    files.add(f);
		}
	}
	
	void write_to_file(String file_name,String content) throws IOException {
		
		FFile file_to_be_written=null;
		for(FFile file:files) {
			if(file.name==file_name) {
				file_to_be_written = file;
				break;
			}
		}
		StringBuffer stringbuff;
		 
		// write to blocks
		for(int i=0;i<content.length();i+=2)
		{
			stringbuff = new StringBuffer();
			for(int j=0;j<2&& j<content.length();j++)
			 stringbuff.append(content.charAt(i+j));
			int block_num= search_free_block();
			write_to_block(block_num, stringbuff.toString());
			file_to_be_written.inode_block_num.add(block_num);
			
			
		}
		
		// write filled blocks to inodes
		int seek_position = file_to_be_written.file_num*256+14;
		for(int i=0;i<file_to_be_written.inode_block_num.size();i++) {
			file_store.seek(seek_position);
			file_store.writeInt(file_to_be_written.inode_block_num.get(i));
			seek_position=seek_position+4;
		}
		
	}
	
	void create_file(String file_name) throws IOException {
		
		FFile f=null;
		for(FFile file:files) {
			if(file.name==file_name) {
				return;
				
			}
		}
		
		for(FFile file:files) {
			if(file.name.isEmpty()) {
				file.name=file_name;
				f=file;
				break;
			}
		}
		
		write_file_metadata(f,file_name);
		
		//assign a new file
		
		
	}
	
	void write_file_metadata(FFile f,String name) throws IOException{
		int seek_position = f.file_num*256;
		/*file_store.seek(seek_position);
		file_store.writeInt(f.file_num);
		seek_position= seek_position+4;*/
		
		for(int i=0;i<name.length();i++) {
			file_store.seek(seek_position);
			file_store.writeChar(name.charAt(i));
			seek_position+=2;
		}
		
	}
	
	void empty_file_name(FFile f,String name) throws IOException {
		int seek_position = f.file_num*256;
		/*file_store.seek(seek_position);
		file_store.writeInt(f.file_num);
		seek_position= seek_position+4;*/
		
		for(int i=0;i<name.length();i++) {
			file_store.seek(seek_position);
			file_store.writeChar('\0');
			seek_position+=2;
		}
	}
	
	
	void read_block(int block_num,StringBuffer buff) throws IOException{
		int seek_position=file_metadata_size+block_num*4;
		long pos= block_num*4;
		
		for(int i=0;i<2;i++) {
			file_store.seek(seek_position);
			buff.append(file_store.readChar());
			seek_position+=2;
		}
		//do a seek and read bytes.
	}
	
	void write_to_block(int block_num,String buff) throws IOException {
		
		block_status.put(block_num, true);
		int seek_position=file_metadata_size+block_num*4;
		long pos= block_num*4;
		
		for(int i=0;i<buff.length()&&i<2;i++) {
			file_store.seek(seek_position);
			file_store.writeChar(buff.charAt(i));
			seek_position+=2;
		}
	}
	
	

	void rename(String file_name,String new_name) throws IOException {
		
		for(FFile file:files) {
			if(file.name.equals(file_name)) {
				file.name=new_name;
				write_file_metadata(file,new_name);
				return;
			}
		}
		
		
		
	}
	
	
	FFile search_file(String file_name) {
		
		for(FFile file:files) {
			if(file.name.equals(file_name)) {
				return file;
			}
		}
		
		return null;
	}
	
	
	void delete(String file_name) throws IOException {
		
		for(FFile file:files) {
			if(file.name.equals(file_name)) {
				empty_file_name(file,file.name);
				file.name="";
				int seek_position = file.file_num*256+14;
				for(int block_n:file.inode_block_num) {
					
						file_store.seek(seek_position);
						file_store.writeInt(0);
						seek_position=seek_position+4;
					
					block_status.put(block_n, false);
				}
				file.inode_block_num.clear();
				
			
				return;
			}
		}
		
	}
	
	
	int search_free_block() {
		
		for(int i=1;i<=max_blocks;i++) {
			if(!block_status.get(i)) {
				
				return i;
			}
			
		}
		
		return -1;
		
	}
	
	void print_file_content(String file_name) throws IOException {
		
		FFile file=search_file(file_name);
		
		if(file!=null) {
		   int seek_position =file_metadata_size;
		   StringBuffer buff ;
			buff = new StringBuffer();
			for(int block :file.inode_block_num) {
			
				read_block(block,buff);
				
			}
			System.out.print(buff.toString());
		}
	}
	
	
	void list_files() {
		
		for(FFile file:files) {
			if(!file.name.isEmpty())
			{
				System.out.print(file.name);
				System.out.println(" "+file.file_num);
			}
		}
	}
	
	
	void execute( List<String> args) throws IOException {
		
		String command = args.get(0);
		if(command.equals("mf")) {
			
			create_file(args.get(1));
			write_to_file(args.get(1), args.get(2));
			
		}else if(command.equals("df")) {
			delete(args.get(1));
			
		}else if(command.equals("rf")) {
			rename(args.get(1), args.get(2));
			
		}else if(command.equals("pf")) {
			print_file_content(args.get(1));
			
		}else if(command.equals("ls")) {
			list_files();
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		
		Filesystem f = new Filesystem();
		f.init();
		List<String> a = new ArrayList();
		
		/*a.add("mf");
		a.add("file9");
		a.add("wert");*/
		/*
		a.add("mf");
		a.add("file4");
		a.add("ab");*/
		
		// a.add("pf");
		 //a.add("file9");
		//a.add("ty");*/
		
		//a.add("ls");
		/* a.add("rf");
		a.add("file9");
		a.add("file10");*/
		
		//a.add("df");
		//a.add("file10");
		for(String arg:args)
		 a.add(arg);
		if(a.size()>0)
		f.execute(a);
	}
}
