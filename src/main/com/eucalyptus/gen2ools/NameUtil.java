// $Id: NameUtil.java,v 1.6 2005/08/19 19:17:08 dsosnoski Exp $

package com.eucalyptus.gen2ools;

import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class NameUtil {

	private static final Log log = LogFactory.getLog(NameUtil.class);

	static String removeFieldTypeSuffix; // suffix removed from class names

	static String addFieldTypePrefix; // Jibx

	static String addFieldPrefix; // _

	static String addListSuffix = ""; // suffix added to collections, both variables
								 // and methods

	static String reservedWordPrefix = "_";

	private static HashSet reservedWords; // Java Reserved Words (keywords and
										  // literals)

	static {
		reservedWords = new HashSet(51);

		//keywords
		reservedWords.add("abstract");
		reservedWords.add("boolean");
		reservedWords.add("break");
		reservedWords.add("byte");
		reservedWords.add("case");
		reservedWords.add("catch");
		reservedWords.add("char");
		reservedWords.add("class");
		reservedWords.add("const");
		reservedWords.add("continue");

		reservedWords.add("default");
		reservedWords.add("do");
		reservedWords.add("double");
		reservedWords.add("else");
		reservedWords.add("extends");
		reservedWords.add("final");
		reservedWords.add("finally");
		reservedWords.add("float");
		reservedWords.add("for");
		reservedWords.add("goto");

		reservedWords.add("if");
		reservedWords.add("implements");
		reservedWords.add("import");
		reservedWords.add("instanceof");
		reservedWords.add("int");
		reservedWords.add("interface");
		reservedWords.add("long");
		reservedWords.add("native");
		reservedWords.add("new");
		reservedWords.add("package");

		reservedWords.add("private");
		reservedWords.add("protected");
		reservedWords.add("public");
		reservedWords.add("return");
		reservedWords.add("short");
		reservedWords.add("static");
		reservedWords.add("strictfp");
		reservedWords.add("super");
		reservedWords.add("switch");
		reservedWords.add("synchronized");

		reservedWords.add("this");
		reservedWords.add("throw");
		reservedWords.add("throws");
		reservedWords.add("transient");
		reservedWords.add("try");
		reservedWords.add("void");
		reservedWords.add("volatile");
		reservedWords.add("while");

		//literals
		reservedWords.add("true");
		reservedWords.add("false");
		reservedWords.add("null");
	}

	static String trimSuffix(String word) {
		if (word == null || word.equals("")) {
			//return "";
			return null;
		}

		if (removeFieldTypeSuffix != null
				&& word.endsWith(removeFieldTypeSuffix)) {
			int suffixIndex = word.length() - removeFieldTypeSuffix.length();
			return word.substring(0, suffixIndex);
		}

		return word;
	}

	/** Changes a word to lowerCamelCase. */
	static String toLowerCamelCase(String word) {
		/*
		 * if( word.length() > 1 ){ // make the first leter lower case return
		 * word.substring( 0, 1 ).toLowerCase() + word.substring( 1 ); } return
		 * word;
		 */
		word = firstAcronymToLowerCase(word);
		return toCamelCase(word, false);
	}

	/** Changes a word to UpperCamelCase. */
	static String toUpperCamelCase(String word) {
		/*
		 * if( word.length() > 1 ){ // make the first leter upper case return
		 * word.substring( 0, 1 ).toUpperCase() + word.substring( 1 ); } return
		 * word;
		 */
		return toCamelCase(word, true);
	}

	private static String firstAcronymToLowerCase(String word) {
		if (word.length() == 1) {
			return word;
		}

		StringBuffer sb = new StringBuffer();
		char[] ca = word.toCharArray();
		int i = 0;
		while (i < ca.length) {
			if (Character.isUpperCase(ca[i])) {
				i++;
			} else {
				break;
			}
		}

		if (i < 2) {
			return word; // the first letter is capitalized
		}

		// leave the previous letter capitalized
		//   unless it is the last letter
		if (i != ca.length) {
			i--;
		}

		for (int j = 0; j < i; j++) {
			sb.append(Character.toLowerCase(ca[j]));
		}

		for (int j = i; j < ca.length; j++) {
			sb.append(ca[j]);
		}

		return sb.toString();
	}

	// 2004-01-25 allow digits also
	private static String toCamelCase(String word, boolean upperCaseFirstLetter) {
		if (word == null || word.equals("")) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		char[] ca = word.toCharArray();
        if (Character.isDigit(ca[0])) {
            sb.append('_');
        }
		if (upperCaseFirstLetter) {
			sb.append(Character.toUpperCase(ca[0]));
		} else {
			sb.append(Character.toLowerCase(ca[0]));
		}

		boolean wasLetter = true; // previous letter was a letter or digit
		for (int i = 1; i < ca.length; i++) {
			//if ( Character.isLetter( ca[ i ] ) || Character.isDigit( ca[ i ]
			// ) ) {
			if (Character.isLetterOrDigit(ca[i])) {
				if (!wasLetter) {
					wasLetter = true;
					sb.append(Character.toUpperCase(ca[i]));
				} else {
					sb.append(ca[i]);
				}
			} else {
				wasLetter = false;
			}
		}

		return sb.toString();
	}

	/** does the xml type passed in have a namespace prefix */
	static boolean hasPrefix(String type) {
		if (type == null) {
			log.error("null type passed to hasPrefix()");
			return false;
		}
		return type.indexOf(':') > 0;
	}

	// remove namespace prefix
	//   change "xs:string" to "string" and so on
	static String trimPrefix(String type) {
		//int colonIndex = type.indexOf( ':' );
		int colonIndex = type.lastIndexOf(':');
		if (colonIndex > 0 && colonIndex < type.length()) {
			type = type.substring(colonIndex + 1);
		}
		return type;
	}

	static String getPrefix(String type) {
		String prefix = null;
//		int colonIndex = type.indexOf(':');
		int colonIndex = type.lastIndexOf(':');
		if (colonIndex > 0 && colonIndex < type.length()) {
			prefix = type.substring(0, colonIndex);
		}
		return prefix;
	}

	static boolean isReservedWord(String word) {
		return reservedWords.contains(word);
	}
}