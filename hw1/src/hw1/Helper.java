package hw1;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chitraketu
 */
public class Helper {

	static enum BoolVals {
		TRUE, FALSE
	}

	static enum ReservedWords {
		PRINT, FOR, LESSTHAN, WHILE, DO;

		public static boolean contains(String w) {
			for (ReservedWords word : ReservedWords.values()) {
				if (word.name().equals(w))
					return true;
			}
			return false;
		}
	}

	public static Map<String, Variable> variables = new HashMap<String, Variable>();
	public static Map<Character, Runnable> operations = new HashMap<>();

	public static String[] tokenize(String line) {
		String regex = "\"([^\"]*)\"|(\\S+)";
		Matcher m = Pattern.compile(regex).matcher(line);

		String[] tokens = new String[100];
		int i = 0;
		while (m.find()) {
			if (m.group(1) != null)
				tokens[i++] = "\"" + m.group(1) + "\"";
			else
				tokens[i++] = m.group(2);
		}
		return tokens;
	}
	
	public static void executeFor(int iter, String[] statementsTokenList) throws Throwable {
		for(int i = 0;i< iter;i++)
			executeLine(statementsTokenList);
	}
	
	static void executeWhile(String boolVar, String[] statementsTokenList) throws Throwable {
		Variable cond = Helper.variables.get(boolVar);
		if (!cond.type.equals("Boolean"))
			throw new Throwable();
		while(cond.valBool) {
			executeLine(statementsTokenList);
			cond = Helper.variables.get(boolVar);
		}
	}
	
	public static void executeStatement(String[] tokens) throws Throwable {
		String first = tokens[0];
		boolean containsLessthan = false;
		for (int i = 0;i<tokens.length && tokens[i] != null;i++){
			if (tokens[i].equals("LESSTHAN")) {
				containsLessthan = true;
				break;
			}
		}
		if (!Helper.ReservedWords.contains(first)) {
			String operator = tokens[1];
			String val = tokens[2];
			if(!containsLessthan)
				Helper.handleOp(operator, first, val);
			else {
				assign("temp1",tokens[2]);
				assign("temp2",tokens[4]);
				Variable var1 = variables.get("temp1");
				Variable var2 = variables.get("temp2");
				if(var1.type.equals("Integer") & var2.type.equals("Integer"))
					handleOp("=", first, var1.valInt < var2.valInt ?"TRUE":"FALSE" );
				else
					throw new Throwable("must be an integer");
			}
		} else {
			switch (first) {
			case "PRINT":
				String var = tokens[1];
				System.out.println(var + "=" + Helper.variables.get(var).getValue());
				break;
			case "FOR":
				int iter = Integer.parseInt(tokens[1]);
				String[] forStatementsTokenList = new String[50];
				System.arraycopy(tokens, 3, forStatementsTokenList, 0, tokens.length-4);
				executeFor(iter, forStatementsTokenList);
				break;
			case "WHILE":
				String boolVar = tokens[1];
				String[]whileStatementsTokenList = new String[50];
				System.arraycopy(tokens, 4, whileStatementsTokenList , 0, tokens.length-5);
				executeWhile(boolVar,whileStatementsTokenList);

			default:
				break;
			}
		}
	}

	

	public static void executeLine(String[] tokens) throws Throwable {
		String first = tokens[0];
		int j = 0;
		int len = 0;
		while(len < tokens.length && tokens[len++] != null);
		if(len != tokens.length)len--;

		if(first.equals("FOR") || first.equals("WHILE")) {
			//handle the for loop
			Stack<String> braces = new Stack<String>();
			while(tokens[j++] != null && j < tokens.length) {
				if(tokens[j].equals("{")) {
					braces.push("{");
					break;
				}
			}
			//System.out.println(j);
			while(tokens[j++] != null && j < tokens.length) {
				if(tokens[j].equals("{")) {
					braces.push("{");
				}else if (tokens[j].equals("}")) {
					braces.pop();
				}
				if (braces.isEmpty()) {
					break;
				}
			}
			j++;
			//System.out.println(j);
		}else {
			while(!tokens[j++].equals( ";") && j < tokens.length);
		}
		String[] statement = new String[j];
		String[] tokensList = new String[len-j];
		System.arraycopy(tokens, 0, statement, 0, j);
		System.arraycopy(tokens, j, tokensList, 0, len-j);
		executeStatement(statement);
		if(tokensList.length > 0)
			executeLine(tokensList);

	}

	public static void addNAssign(String v, String val) throws Throwable {
		if (Helper.variables.containsKey(v)) {
			assign("temp", val);
			Variable var1 = variables.get(v);
			Variable var2 = variables.get("temp");
			if (!var1.type.equals(var2.type))
				throw new Throwable("type mismatch error");

			// logic to add or concatenate
			if (var1.type == "String") {
				var1.valStr = var1.valStr.concat(var2.valStr);
			} else
				var1.valInt += var2.valInt;

			variables.remove("temp");

		} else {
			throw new Throwable(v + " not defined error");
		}

	}

	public static void mulNAssign(String v, String val) throws Throwable {
		if (Helper.variables.containsKey(v)) {
			assign("temp", val);
			Variable var1 = variables.get(v);
			Variable var2 = variables.get("temp");
			if (!var1.type.equals(var2.type) || !var1.type.equals("Integer") )
				throw new Throwable("type mismatch error");

			// logic to multiply
			var1.valInt *= var2.valInt;
			variables.remove("temp");

		} else {
			throw new Throwable(v + " not defined error");
		}

	}

	public static void andNAssign(String v, String val) throws Throwable {
		if (Helper.variables.containsKey(v)) {
			assign("temp", val);
			Variable var1 = variables.get(v);
			Variable var2 = variables.get("temp");
			if (!var1.type.equals(var2.type) || !var1.type.equals("Boolean"))
				throw new Throwable("type mismatch error");

			// logic to perform and operation
			var1.valBool &= var2.valBool;
			variables.remove("temp");

		} else {
			throw new Throwable(v + " not defined error");
		}

	}

	public static void assign(String v, String val) {
		if (Helper.variables.containsKey(val)) {
			variables.put(v, Variable.copyVar(Helper.variables.get(val)));
			return;
		}
		variables.put(v, new Variable(val));
	}

	public static void handleOp(String operator, String first, String val) throws Throwable {
		switch (operator) {
		case "=":
			assign(first, val);
			break;

		case "+=":
			addNAssign(first, val);
			break;
		case "*=":
			mulNAssign(first, val);
			break;
		case "&=":
			andNAssign(first, val);
			break;
		default:
			break;
		}
	}
}