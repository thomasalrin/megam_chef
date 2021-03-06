package org.megam.chef.parser;

import java.util.Formatter;

import org.megam.chef.BootStrapChef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * 
 * @author rajthilak
 * 
 */
public class JSONRequestParser {

	private JSONRequest reqData;
	private ComputeInfo compute;
	private CommandJson reqCommand;
	private String reqid;
	private String node_name;	
	private Logger logger = LoggerFactory.getLogger(JSONRequestParser.class);

	/**
	 * 
	 * @param jsonString
	 */
	public JSONRequestParser(String jsonString) {
		parse(jsonString);

	}

	/**
	 * 
	 * @param jsonString
	 *            parse the json string passed.
	 */
	private void parse(String jsonString) {
		try {
			Gson gson = new Gson();
			reqCommand = gson.fromJson(jsonString, CommandJson.class);			
			reqData = reqCommand.getReqCommand();
			reqData.setReqType(reqCommand.getReqType());
			compute = reqData.getAccess();
			compute.setReqType(reqCommand.getReqType());
			reqCommand.getnodeId();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	public JSONRequest data() {
		return reqData;
	}
	
	public String getReqId() {
		return reqCommand.getId();
	}
	
	public String getNodeName() {
		return reqCommand.getNodeName();
	}
	
	
	public String toString() {
		StringBuilder strbd = new StringBuilder();
		final Formatter formatter = new Formatter(strbd);
		formatter.format("%s%n",
				"*----------------------------------------------*");
		formatter.format("%12s = %s%n", "reqdata", data());
		formatter.format("%s%n",
				"*----------------------------------------------*");
		formatter.close();
		return strbd.toString();

	}

}
