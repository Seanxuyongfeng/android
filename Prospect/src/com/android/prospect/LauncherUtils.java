package com.android.prospect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import android.util.Log;

import com.android.prospect.HanziToPinyin.Token;

public class LauncherUtils {

    public static String getSortKey(String displayName) {
        ArrayList<Token> tokens = HanziToPinyin.getInstance().get(displayName);
        if (tokens != null && tokens.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Token token : tokens) {
                // Put Chinese character's pinyin, then proceed with the
                // character itself.
                if (Token.PINYIN == token.type) {
                    if (sb.length() > 0) {
                        sb.append(' ');
                    }
                    
                    sb.append(token.target);
                    sb.append(' ');
                    sb.append(token.source);
                } else {
                    if (sb.length() > 0) {
                        sb.append(' ');
                    }
                    sb.append(token.source);
                }
            }
            return sb.toString();
        }
        return displayName;
    }
    
    
    public static Iterator<String> getNameLookupKeys(String name) {
        HashSet<String> keys = new HashSet<String>();
        ArrayList<Token> tokens = HanziToPinyin.getInstance().get(name);
        final int tokenCount = tokens.size();
        final StringBuilder keyPinyin = new StringBuilder();
        final StringBuilder keyInitial = new StringBuilder();
        // There is no space among the Chinese Characters, the variant name
        // lookup key wouldn't work for Chinese. The keyOrignal is used to
        // build the lookup keys for itself.
        final StringBuilder keyOrignal = new StringBuilder();
        for (int i = tokenCount - 1; i >= 0; i--) {
            final Token token = tokens.get(i);
            if (Token.PINYIN == token.type) {
                keyPinyin.insert(0, token.target);
                keyInitial.insert(0, token.target.charAt(0));
            } else if (Token.LATIN == token.type) {
                // Avoid adding space at the end of String.
                if (keyPinyin.length() > 0) {
                    keyPinyin.insert(0, ' ');
                }
                if (keyOrignal.length() > 0) {
                    keyOrignal.insert(0, ' ');
                }
                keyPinyin.insert(0, token.source);
                keyInitial.insert(0, token.source.charAt(0));
            }
            keyOrignal.insert(0, token.source);
            keys.add(keyOrignal.toString());
            keys.add(keyPinyin.toString());
            keys.add(keyInitial.toString());
        }
        return keys.iterator();
    }    
    
    public static void appendName(StringBuilder builder, String name){
    	Iterator<String> it = LauncherUtils.getNameLookupKeys(name);
    	if(it != null){
    		while(it.hasNext()){
    			if(builder.length() != 0){
    				builder.append(' ');
    			}
    			builder.append(NameNormalizer.normalize(it.next()));
    		}
    	}
    }
    
    public static String appendName(String name){
    	StringBuilder builder = new StringBuilder();
    	Iterator<String> it = LauncherUtils.getNameLookupKeys(name);
    	if(it != null){
    		while(it.hasNext()){
    			if(builder.length() != 0){
    				builder.append(' ');
    			}
    			builder.append(NameNormalizer.normalize(it.next()));
    		}
    	}
    	
    	return builder.toString();
    }
    
    public static String toPinYin(String name){
    	StringBuilder builder = new StringBuilder();
    	ArrayList<Token> tokens = HanziToPinyin.getInstance().get(name);
    	if(tokens != null && tokens.size() > 0){
    		for(Token token : tokens){
    			final String target;
    			if(Token.PINYIN == token.type){
    				target = token.target;
    			}else{
    				target = token.source;
    			}
    			builder.append(target.toUpperCase());
     		}
    	}
    	
    	return builder.toString();
    }
    
    public static void appendNameShorthandLookup(StringBuilder builder, String name){
    	ArrayList<Token> tokens = HanziToPinyin.getInstance().get(name);
    	if(tokens != null && tokens.size() > 0){
    		for(Token token : tokens){
    			String target;
    			if(Token.PINYIN == token.type){
    				target = token.target;
    			}else{
    				target = token.source;
    			}
    			final char c = target.charAt(0);
    			if(Character.isDigit(c)){
    				builder.append(target.toUpperCase());
    			}else{
    				builder.append(Character.toUpperCase(c));
    			}
    		}
    	}
    }
    
}
