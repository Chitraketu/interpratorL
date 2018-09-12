package hw1;

/**
 * 
 * @author chitraketu
 *
 */
public class Variable {

	int valInt;
	String valStr;
	boolean valBool;
	String type;

	public static Variable copyVar(Variable var) {
		Variable vnew = new Variable();
		vnew.type = var.type;
		vnew.valBool = var.valBool;
		vnew.valInt = var.valInt;
		vnew.valStr = var.valStr;
		return vnew;
	}

	public Variable(String val) {
		if (val.contains("\"")) {
			this.type = "String";
			this.valStr = val.replaceAll("^\"|\"$", "");
		} else {
			try {
				int vint = Integer.parseInt(val);
				this.type = "Integer";
				this.valInt = vint;
			} catch (Exception e) {
				if (val.equals("TRUE") || val.equals("FALSE")) {
					this.type = "Boolean";
					this.valBool = val.toLowerCase().contains("true");
				}
			}
		}

	}

	public Variable() {
	}

	public String getValue() {
		if (type == "Integer")
			return valInt + "";
		else if (type == "String")
			return "\"" + valStr + "\"";
		else
			return valBool ? "TRUE" : "FALSE";
	}
}