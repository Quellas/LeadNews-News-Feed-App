package com.itheima.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringEscapeUtils;
import org.trie4j.doublearray.DoubleArray;
import org.trie4j.louds.TailLOUDSTrie;
import org.trie4j.patricia.PatriciaTrie;

public class Sample {
	public static void main(String[] args) throws Exception{
		PatriciaTrie pat = new PatriciaTrie();
		pat.insert("黑马");
		pat.insert("黑猫");
		pat.insert("黑马头条");
		pat.insert("黑嘿!");
		pat.contains("Hello"); // -> true
		Iterable<String> wo = pat.predictiveSearch("黑");// -> {"Wonder", "Wonderful!", "World"} as Iterable<String>
		System.out.println(wo);
		
		/*DoubleArray da = new DoubleArray(pat); // construct DoubleArray from existing Trie
		da.contains("World"); // -> true
		
		TailLOUDSTrie lt = new TailLOUDSTrie(pat); // construct LOUDS succinct Trie with ConcatTailBuilder(default)
		lt.contains("Wonderful!"); // -> true
		lt.commonPrefixSearch("Wonderful!"); // -> {"Wonder", "Wonderful!"} as Iterable<String>*/


		String s1="{\"MsgId\":1,\"TotalCount\":10,\"FilterCount\":8,\"SentCount\":7,\"ErrorCount\":1}";
		System.out.println(StringEscapeUtils.unescapeJava(s1));





	}
}