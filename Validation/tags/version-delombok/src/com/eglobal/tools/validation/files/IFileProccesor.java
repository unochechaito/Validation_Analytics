package com.eglobal.tools.validation.files;

import java.io.File;
import java.io.IOException;

public interface IFileProccesor<F extends File> {
	void setFile(F File);
	void process() throws InterruptedException, IOException;
	int loadingPercent();
	long bytesRead();
	long linesRead();
}
