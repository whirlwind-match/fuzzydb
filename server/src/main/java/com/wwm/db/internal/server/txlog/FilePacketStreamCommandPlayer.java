package com.wwm.db.internal.server.txlog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;

import com.wwm.db.core.LogFactory;
import com.wwm.db.internal.comms.messages.BeginAndCommitCmd;
import com.wwm.db.internal.comms.messages.CommitCmd;
import com.wwm.db.internal.comms.messages.OkRsp;
import com.wwm.db.internal.server.CommandProcessingPool;
import com.wwm.io.core.ClassLoaderInterface;
import com.wwm.io.core.Message;
import com.wwm.io.core.MessageSink;
import com.wwm.io.core.PacketInterface;
import com.wwm.io.core.layer2.PacketCodec;
import com.wwm.io.core.layer2.SourcedMessageImpl;
import com.wwm.io.core.messages.ErrorRsp;
import com.wwm.io.core.messages.PacketMessage;

/**
 * Reads packets from a File and applies the commands from each packet until exhausted or
 * shutdown.
 */
class FilePacketStreamCommandPlayer implements MessageSink {

	static private final Logger log = LogFactory.getLogger(FilePacketStreamCommandPlayer.class);
	
	private final PacketInterface reader;
	private final CommandProcessingPool commandProcessor; 
	private final Semaphore executing = new Semaphore(0);
	private final ClassLoaderInterface cli;

	public FilePacketStreamCommandPlayer(File file, CommandProcessingPool commandProcessor, ClassLoaderInterface cli) throws FileNotFoundException {
		reader = new TxLogReader(file);
		this.commandProcessor = commandProcessor;
		this.cli = cli;
	}

	/**
	 * Iterate over the packets in this transaction log applying the retrieved commands
	 * to the supplied commandProcessor
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	public void run() {
		// TODO Auto-generated method stub
		
		int playbackErrors = 0; // count the number of errors we get, and report it
		
		PacketCodec pc = new PacketCodec(reader, cli);
		try {
			for (;;) {
				Collection<PacketMessage> messages = pc.read();
				if (messages == null) return;
				for (PacketMessage message : messages) {
					
					Message m = message.getMessage();
					int storeId = m.getStoreId();
					int cid = m.getCommandId();

					if (m instanceof CommitCmd) {
						CommitCmd cc = (CommitCmd)m;
						int tid = cc.getTid();
						m = new BeginAndCommitCmd(storeId, cid, tid, cc);
					}
					
					try {
						commandProcessor.execute(new SourcedMessageImpl(this, m, message.getPacket()));
					} catch (Exception e) {
						playbackErrors++;
					}
					// block until commmandProcessor passing response back to send() gives us a permit to continue
					executing.acquireUninterruptibly(); 
				}
			}
		} catch (IOException e) {
			log.error("Error playing back TxLog", e);
			return;
		} finally {
			if (playbackErrors > 0){
				log.error("Errors found during playback: " + playbackErrors + " commands failed.");
			}
			pc.close();
		}
	}

	public void close() {
		// do nothing 
	}

	/**
	 * Receive response to having executed a command from the TxLog
	 */
	public void send(Message m) {
		executing.release();
		// always release, otherwise error will deadlock.
		if (m instanceof ErrorRsp) {
			ErrorRsp er = (ErrorRsp)m;
			throw new RuntimeException(er.getError());
		}
		if (!(m instanceof OkRsp)) {
			throw new RuntimeException();
		}
	}

}
