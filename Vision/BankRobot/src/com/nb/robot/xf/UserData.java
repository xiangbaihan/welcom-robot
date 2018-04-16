package com.nb.robot.xf;

// Data structure kept among Xunfei SDK calls. 
public class UserData {
	final static int MAX_GRAMMARID_LEN = 32;
	
	int build_fini; //标识语法构建是否完成
	int update_fini; //标识更新词典是否完成
	int errcode; //记录语法构建或更新词典回调错误码
	String grammar_id;
	String result;
	
	public UserData(int build_fini, int update_fini, int errcode, String grammar_id, String result) {
		this.build_fini = build_fini;
		this.update_fini = update_fini;
		this.errcode = errcode;
		this.grammar_id = grammar_id;
		this.result = result;
	}
	
	public String getGrammarId() {
		return this.grammar_id;		
	}
	
	public int getBuildFini() {
		return this.build_fini;		
	}
	
	public int getUpdateFini() {
		return this.update_fini;		
	}
	
	public int getErrorCode() {
		return this.errcode;		
	}
	
	public String getResult() {
		return this.result;		
	}
}
