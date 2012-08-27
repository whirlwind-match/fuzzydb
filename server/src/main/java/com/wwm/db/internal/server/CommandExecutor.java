/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.db.internal.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.fuzzydb.client.exceptions.NotImplementedException;
import org.fuzzydb.client.exceptions.StoreExistsException;
import org.fuzzydb.client.exceptions.UnknownStoreException;
import org.fuzzydb.client.exceptions.UnknownTransactionException;
import org.fuzzydb.client.internal.comms.messages.AllocNewIdsCmd;
import org.fuzzydb.client.internal.comms.messages.BeginTransactionCmd;
import org.fuzzydb.client.internal.comms.messages.CreateStoreCmd;
import org.fuzzydb.client.internal.comms.messages.DeleteStoreCmd;
import org.fuzzydb.client.internal.comms.messages.DisposeCmd;
import org.fuzzydb.client.internal.comms.messages.EchoCmd;
import org.fuzzydb.client.internal.comms.messages.EchoRsp;
import org.fuzzydb.client.internal.comms.messages.ListStoresCmd;
import org.fuzzydb.client.internal.comms.messages.ListStoresRsp;
import org.fuzzydb.client.internal.comms.messages.OkRsp;
import org.fuzzydb.client.internal.comms.messages.OpenStoreCmd;
import org.fuzzydb.client.internal.comms.messages.OpenStoreRsp;
import org.fuzzydb.client.internal.comms.messages.ShutdownCmd;
import org.fuzzydb.client.internal.comms.messages.TransactionCommand;
import org.fuzzydb.client.internal.comms.messages.WWSearchCmd;
import org.fuzzydb.client.internal.comms.messages.WWSearchFetchCmd;
import org.fuzzydb.core.exceptions.ArchException;
import org.fuzzydb.io.core.MessageSink;
import org.fuzzydb.io.core.SourcedMessage;
import org.fuzzydb.io.core.layer2.SourcedMessageImpl;
import org.fuzzydb.io.core.messages.Command;
import org.fuzzydb.io.core.messages.ErrorRsp;


public class CommandExecutor {

	private static final String commandMethodPrefix = "cmd";
	private final String commandPackageName;

	// HashMap usage reviewed by Neale: Seems efficient as Class.hashCode() uses built-in, which will be cached (we'd hope)
	private final Map<Class<?>, Method> commandMap = new HashMap<Class<?>, Method>();
	private final ServerTransactionCoordinator stc;
	private final Database database;
	
	public CommandExecutor(ServerTransactionCoordinator stc, Database database) {
		this.stc = stc;
		this.database = database;
		
		// figure out the command package name.
		// We'll assume all commands are in the same package.
		String echoClassName = EchoCmd.class.getName();
		int lastDot = echoClassName.lastIndexOf('.');
		commandPackageName = echoClassName.substring(0, lastDot) + '.';
		
		// Build a map of commands to methods using reflection api
		Method[] methods = this.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			addMethod(methods[i]);
		}
		
		Method[] transMethods = PersistentServerTransaction.class.getDeclaredMethods();
		for (int i = 0; i < transMethods.length; i++) {
			addMethod(transMethods[i]);
		}
	}
	
	private void addMethod(Method method) {
		String name = method.getName();
		if (name.startsWith(commandMethodPrefix)) {
			String commandClassName = commandPackageName + name.substring(commandMethodPrefix.length());
			Class<?> commandClass = null;
			try {
				commandClass = Class.forName(commandClassName);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
			commandMap.put(commandClass, method);
		}		
	}
	
	private void sendErrorRsp(SourcedMessage command, ArchException ae) {
		ErrorRsp er = new ErrorRsp(command.getMessage().getStoreId(), command.getMessage().getCommandId(), ae);
		try {
			command.getSource().send(er);
		} catch (IOException e1) {
			command.getSource().close();	// Problem writing a response to the comms, close the connection
		}
	}
	
	public void execute(SourcedMessage command) {
		MessageSink source = command.getSource();
		Command cmd = (Command)command.getMessage();
		ByteBuffer packet = command.getPacket();
		int cid = cmd.getCommandId();
		int storeId = cmd.getStoreId();
		
		Object executionTarget = null;
		if (cmd instanceof TransactionCommand) {
			int tid = ((TransactionCommand)cmd).getTid();
			try {
				executionTarget = stc.beginAction(source, tid);
			} catch (UnknownTransactionException e) {
				sendErrorRsp(command, e);
				return;
			}
		} else {
			executionTarget = this;
		}

		try
		{
			Class<?> commandClass = cmd.getClass();
			Method method = commandMap.get(commandClass); 
			
			if (method == null) {
				ArchException ae = new NotImplementedException(commandClass.getSimpleName());
				ErrorRsp er = new ErrorRsp(storeId, cid, ae);
				try {
					command.getSource().send(er);
				} catch (IOException e1) {
					command.getSource().close();	// Problem writing a response to the comms, close the connection
				}
				return;
			}
			
			try {
	//			try {
	//				cmdEchoCmd(command.getStoreId(), command.getMessage().getCommandId(), command.getSource(), (EchoCmd)command.getMessage());
	//			} catch (IOException e) {
	//				command.getSource().close();	// Problem writing a response to the comms, close the connection
	//			}
				method.invoke(executionTarget, storeId, cid, source, cmd, packet);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("IllegalArgumentException returned when invoking command: " + method, e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("IllegalAccessException returned when invoking command: " + method, e);
			} catch (InvocationTargetException e) {
				Throwable targetException = e.getTargetException();
				
				// Allow exceptions on some 'dynamic' areas (e.g. where we use Scorers) to have their
				// exceptions returned to the client instead of being fatal.
				if (!(targetException instanceof ArchException) && isCommandSafe(cmd)){
					targetException = new ArchException("non-fatal server exception", targetException);
				}
				if (targetException instanceof ArchException) {
					// Package up all ArchExceptions as an error response and send it
					ArchException ae = (ArchException) targetException;
					sendErrorRsp(command, ae);
				} else if (IOException.class.isInstance(e.getCause())) {
					command.getSource().close();	// Problem writing a response to the comms, close the connection
				} else {
					if (targetException instanceof RuntimeException) {
						throw (RuntimeException)targetException;
					} else if (targetException instanceof Error) {
						throw (Error)targetException; // FIXME(nu->ac): Please document intended thread behaviour here.
						// ... what thread is getting killed, and, shouldn't teh client be told something?
						// I think we should always sendErrorRsp().
					} else {
						throw new RuntimeException(targetException);
					}
				}
			}
		} finally {
			if (executionTarget instanceof PersistentServerTransaction) {
				PersistentServerTransaction transaction = (PersistentServerTransaction)executionTarget;
				stc.endAction(transaction);
			}
		}
	}

	/**
	 * FIXME: This should test a tag interface or a single parent ReadOnlyTransactionCmd class, I think.
	 */
	private boolean isCommandSafe(Command cmd) {
		return ( cmd instanceof WWSearchCmd 
				|| cmd instanceof WWSearchFetchCmd);
	}

	private void beginTx(int storeId, int cid, MessageSink source, BeginTransactionCmd command, ByteBuffer packet) throws UnknownStoreException {
		PersistentServerTransaction transaction = new PersistentServerTransaction(stc, source, command.getTid(), storeId);
		stc.addTransaction(transaction);

		Command payload = command.getPayload();
		if (payload != null) {
			// execute payload command
			SourcedMessage command2 = new SourcedMessageImpl(source, payload, packet);
			execute(command2);
		} else {
			// payload was empty, just ack the begin transaction
			sendOkRsp(source, storeId, cid);
		}
	}
	
	private void sendOkRsp(MessageSink source, int storeId, int cid){
		OkRsp ok = new OkRsp(storeId, cid);
		try {
			source.send(ok);
		} catch (IOException e1) {
			source.close();	// Problem writing a response to the comms, close the connection
		}
	}

	@SuppressWarnings("unused") // Used via reflection
	private void cmdEchoCmd(int storeId, int cid, MessageSink source, EchoCmd command, ByteBuffer packet) throws IOException {
		EchoRsp rsp = new EchoRsp(storeId, cid, command.getMessage());
		source.send(rsp);
	}
	
	@SuppressWarnings("unused") // Used via reflection
	private void cmdCreateStoreCmd(int storeId, int cid, MessageSink source, CreateStoreCmd command, ByteBuffer packet) throws IOException, StoreExistsException {
		ServerCreateStoreTransaction transaction = new ServerCreateStoreTransaction(stc, source);
		transaction.setWriteCommand(command, packet);
		transaction.commit();
	}

	@SuppressWarnings("unused") // Used via reflection
	private void cmdDeleteStoreCmd(int storeId, int cid, MessageSink source, DeleteStoreCmd command, ByteBuffer packet) throws IOException, UnknownStoreException {
		ServerDeleteStoreTransaction transaction = new ServerDeleteStoreTransaction(stc, source);
		transaction.setWriteCommand(command, packet);
		transaction.commit();
	}
	
	
	@SuppressWarnings("unused") // Used via reflection
	private void cmdListStoresCmd(int storeId, int cid, MessageSink source, ListStoresCmd command, ByteBuffer packet) throws IOException {
		Collection<String> stores = new ArrayList<String>(stc.getRepository().getStoreNames());
		ListStoresRsp rsp = new ListStoresRsp(cid, stores);
		source.send(rsp);
	}

	@SuppressWarnings("unused") // Used via reflection
	private void cmdOpenStoreCmd(int storeId, int cid, MessageSink source, OpenStoreCmd command, ByteBuffer packet) throws UnknownStoreException, IOException {
		String storeName = command.getStoreName();
		int id = stc.getRepository().getStore(storeName).getStoreId();
		OpenStoreRsp rsp = new OpenStoreRsp(cid, storeName, id);
		source.send(rsp);
	}
	
	@SuppressWarnings("unused") // Used via reflection
	private void cmdAllocNewIdsCmd(int storeId, int cid, MessageSink source, AllocNewIdsCmd command, ByteBuffer packet) {
		ServerAllocNewIdsTransaction transaction = new ServerAllocNewIdsTransaction(stc, source);
		transaction.setWriteCommand(command, packet);
		transaction.commit();
	}
	
	@SuppressWarnings("unused") // Used via reflection
	private void cmdBeginAndCommitCmd(int storeId, int cid, MessageSink source, BeginTransactionCmd command, ByteBuffer packet) throws UnknownStoreException {
		beginTx(storeId, cid, source, command, packet);
	}
	
	@SuppressWarnings("unused") // Used via reflection
	private void cmdBeginTransactionCmd(int storeId, int cid, MessageSink source, BeginTransactionCmd command, ByteBuffer packet) throws UnknownStoreException {
		beginTx(storeId, cid, source, command, packet);
	}
	
	@SuppressWarnings("unused") // Used via reflection
	private void cmdDisposeCmd(int storeId, int cid, MessageSink source, DisposeCmd command, ByteBuffer packet) throws UnknownStoreException {
		ArrayList<Integer> tids = command.getDisposedTransactions();
		for (Integer tid : tids) {
			stc.tryRemoveTransaction(source, tid);
		}
		sendOkRsp(source, storeId, cid);
	}

	@SuppressWarnings("unused") // Used via reflection
	private void cmdShutdownCmd(int storeId, int cid, MessageSink source, ShutdownCmd command, ByteBuffer packet) throws UnknownStoreException {
		// Unusual case: try and send the OK before we close down, so the client stands a chance of getting it before the network connection is closed.
		sendOkRsp(source, storeId, cid);
		database.closeNonBlocking();
	}

}
