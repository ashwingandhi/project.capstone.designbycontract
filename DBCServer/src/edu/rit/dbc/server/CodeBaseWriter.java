package edu.rit.dbc.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * 
 * CodeBaseWriter is a utility class to generate class files and write them to
 * Code-base
 * 
 * @author Ashwin
 * 
 */
public class CodeBaseWriter {

	//Host name
	private static final String SFTP_HOST = "glados.cs.rit.edu";
	//Port
	private static final int SFTP_PORT = 22;
	//User Name
	private static final String SFTP_USER = "axg4794";
	//Password
	private static final String SFTP_PASSWORD = "steven8Gerrard";
	//Directory
	private static final String SFTP_WORKINGDIR =  "/home/stu3/s3/axg4794/public_html/codebase/edu/rit/dbc/server";

	// Location to the DBCServer class files 
	private static final String bin_path = "C:\\Users\\Public\\workspace\\DBCServer\\bin\\";
	// Package folder structure of DBCServer
	private static final String package_path = "edu\\rit\\dbc\\server";

	private static FileOutputStream fos;

	private static final String classPath_ContractProxy = bin_path
			+ package_path + "\\ContractProxy.class";
	private static final String classPath_AbstractContractProxy = bin_path
			+ package_path + "\\AbstractContractProxy.class";
	private static final String classPath_ContractException = bin_path+package_path+"\\ContractException.class";

	/**
	 * Generates class file of ContractProxy Copies class files of
	 * ContractProxy, Contract and AbstractContractProxy to Code base
	 * 
	 * @param code
	 * @throws IOException
	 */
	protected void write(byte[] code, Class<?> interfaceType)
			throws IOException {

		String classPath_Contract = bin_path + package_path + "\\Contract_"
				+ interfaceType.getSimpleName() + ".class";

		// Write ContractProxy.class to server class path
		fos = new FileOutputStream(classPath_ContractProxy);
		fos.write(code);
		fos.close();

		writeToCodebase(classPath_ContractProxy);
		writeToCodebase(classPath_Contract);
		writeToCodebase(classPath_AbstractContractProxy);
		writeToCodebase(classPath_ContractException);
	}

	/**
	 * Uses Secure File Transfer Protocol to transfer files to code base.
	 * 
	 * @param fileName
	 */
	protected void writeToCodebase(String fileName) {

		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		System.out.println("preparing the host information for sftp.");
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(SFTP_USER, SFTP_HOST, SFTP_PORT);
			session.setPassword(SFTP_PASSWORD);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			System.out.println("Host connected.");
			channel = session.openChannel("sftp");
			channel.connect();
			System.out.println("sftp channel opened and connected.");
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(SFTP_WORKINGDIR);
			File f = new File(fileName);
			channelSftp.put(new FileInputStream(f), f.getName());
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			channelSftp.exit();
			System.out.println("sftp Channel exited.");
			channel.disconnect();
			System.out.println("Channel disconnected.");
			session.disconnect();
			System.out.println("Host Session disconnected.");
		}
	}
}
