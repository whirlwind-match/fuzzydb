package likemynds.util;

import java.io.File;
import java.util.logging.Logger;

import com.archopolis.internal.util.LogFactory;

public class FileUtils {

	static private final Logger log = LogFactory.getLogger(FileUtils.class);
	
	static public boolean deleteDirectory(File path) {
		boolean deleted = deleteDirectoryInternal(path);
		log.info( deleted ?
				"Delete succeeded for path: " + path :
				"Delete failed for path: " + path
				);
		return deleted;
	}

	// Internal version that does the deleting recursively
	static private boolean deleteDirectoryInternal(File path) {
		if (!path.exists()) {
			return true;
		}

		File[] files = path.listFiles();
		boolean succeeded = true;
		for (File file : files) {
			if (file.isDirectory()) {
				succeeded &= deleteDirectoryInternal(file);
			} else {
				succeeded &= file.delete();
			}
		}
		return succeeded && path.delete(); // Deliberately won't call path.delete() if !succeeded (it'll fail)
	}
}
