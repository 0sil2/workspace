import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CrunchifyTailFOnLogFile_LogFileTailer implements Runnable 
{

	private boolean debug = false;
	private int crunchifyRunEveryNSeconds = 200;
	private long lastKnownPosition = 0;
	private boolean shouldIRun = true;
	private File crunchifyFile = null;
	private static int crunchifyCounter = 0;
	private int LoopCnt_01 = 0;
	private int LoopCnt_02 = 0;

	public CrunchifyTailFOnLogFile_LogFileTailer(String myFile, int myInterval) 
	{
		crunchifyFile = new File(myFile);
		this.crunchifyRunEveryNSeconds = myInterval;
	}

	private void printLine(String message) {
		System.out.println(message);
	}

	public void stopRunning() {
		shouldIRun = false;
	}

	public void run() 
	{
		try 
		{
			while (shouldIRun) 
			{
				Thread.sleep(crunchifyRunEveryNSeconds);
				long fileLength = crunchifyFile.length();

				if (fileLength > lastKnownPosition) 
				{
					if( this.LoopCnt_01 == 0 )
					{
						lastKnownPosition = fileLength -1;
					}

					// Reading and writing file
					RandomAccessFile readWriteFileAccess = new RandomAccessFile(crunchifyFile, "r");
					readWriteFileAccess.seek(lastKnownPosition);
					String crunchifyLine = null;
					while ((crunchifyLine = readWriteFileAccess.readLine()) != null) 
					{
						if( this.LoopCnt_01 > 0 )
						{
							this.printLine( crunchifyLine );
						}
						crunchifyCounter++;
					}
					lastKnownPosition = readWriteFileAccess.getFilePointer();
					readWriteFileAccess.close();

					this.LoopCnt_01 = 99;
				}
				else 
				{
					if (debug)
					{
						this.printLine("Couldn't found new line after line # " + crunchifyCounter);
					}
				}
			}
		} 
		catch (Exception e) 
		{
			stopRunning();
		}
		if (debug)
			this.printLine("Exit the program...");
	}

	public static void main(String args[]) {
		ExecutorService crunchifyExecutor = Executors.newFixedThreadPool(4);
		// Replace username with your real value
		
		// For windows provide different path like: c:\\temp\\crunchify.log
		//String filePath = "C:\\UWB\\eclipse\\workspace\\UwbTagsDataLog\\2022\\TEST\\0913.txt";
		String filePath = args[0];
		CrunchifyTailFOnLogFile_LogFileTailer crunchify_tailF = new CrunchifyTailFOnLogFile_LogFileTailer(filePath, 200);
		crunchify_tailF.printLine( args[0] );
		// Start running log file tailer on crunchify.log file
		crunchifyExecutor.execute(crunchify_tailF);
	}
}
