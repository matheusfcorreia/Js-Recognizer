import java.util.List;
import java.util.Stack;

public class Machine {

	private TreeObject s;
	private TreeObject variable;
	private TreeObject value;
	private TreeObject object;
	private TreeObject array;
	private TreeObject function;
	private TreeObject functionContent;
	private TreeObject expression;
	
	private Stack<String> lastOperator;
	public static int index = 0;
	private boolean hasError = false;
	
	public Machine() {
		s = new TreeObject("S");
		variable = new TreeObject("Variable");
		s.getChildren().add(variable);
		lastOperator = new Stack<>();
	}
	
	public void evaluateLeftSide(List<Token> tokens) {
		for(Token token : tokens) {
			if(token.getContent().equals("=")) {
				if(lastOperator.peek().equals(Types.IDENTIFIER)) {
					variable.getChildren().add(new TreeObject("="));
					lastOperator.push("=");
					index++;
					tokens = tokens.subList(index, tokens.size());
				} else {
					hasError = true;
				}
				break;
			} else 
			if(TypeObject.typeTokens.contains(token.getContent())) {
				if(lastOperator.isEmpty()) {
					lastOperator.push(Types.TYPE);
					TreeObject t = new TreeObject(Types.TYPE);
					t.getChildren().add(new TreeObject(token.getContent()));
					variable.getChildren().add(t);
					index++;
				} else {
					hasError = true;
					break;
				}
			} else
			if(token.getContent().matches(IdentifierObject.IDENTIFIER_REGEX)) {
				if(lastOperator.isEmpty() || lastOperator.peek().equals(Types.TYPE)) {
					lastOperator.push(Types.IDENTIFIER);
					TreeObject t = new TreeObject(Types.IDENTIFIER);
					t.getChildren().add(new TreeObject(token.getContent()));
					variable.getChildren().add(t);
					index++;
				} else {
					hasError = true;
					break;
				}
			}
		}
	}
	
	public void evaluateRightSide(List<Token> tokens) {
		if(lastOperator.peek().equals("=") && !hasError && tokens.size() > 0 
				&& !tokens.get(0).getContent().equals(";")) {
			
			boolean isString = false;
			boolean isObject = false;
			boolean isArray = false;
			boolean isFunction = false;
			boolean isExpression = false;
			
			value = new TreeObject("Value");
			variable.getChildren().add(value);
			
			for(Token token : tokens) {
				if("".equals(token.getContent())) continue;
				if(token.getContent().endsWith("){")) {
					TreeObject t = new TreeObject(Types.IDENTIFIER);
					token.setContent(token.getContent().replace("){", ""));
					t.getChildren().add(new TreeObject(token.getContent()));
					function.getChildren().add(t);
					function.getChildren().add(new TreeObject(")"));
					function.getChildren().add(new TreeObject("{"));
					function.getChildren().add(functionContent);
				} else
				if(token.getContent().startsWith("function")) {
					isFunction = true;
					token.setContent(token.getContent().replace("function(", "").replace(",", ""));
					function = new TreeObject(Types.FUNCTION);
					value.getChildren().add(function);
					function.getChildren().add(new TreeObject("function("));
					TreeObject i = new TreeObject(Types.IDENTIFIER);
					i.getChildren().add(new TreeObject(token.getContent()));
					function.getChildren().add(i);
					function.getChildren().add(new TreeObject(","));
					functionContent = new TreeObject("FunctionContent");
				} else
				if(!"".equals(token.getContent()) && token.getContent().length() > 1 && token.getContent().charAt(1) == '[') {
					String a = token.getContent().replace("[", " ");
					String [] array = a.split(" ");
					TreeObject matrix = new TreeObject(Types.MATRIX);
					matrix.getChildren().add(new TreeObject("["));
					for(String s : array) {
						if(s.equals("")) continue;
						TreeObject vector = new TreeObject(Types.VECTOR);
						s = s.replace("]", "");
						vector.getChildren().add(new TreeObject("["));
						String[] vectorTokens = s.split("]");
						for(String e : vectorTokens) {
							String[] f = e.split(",");
							for(String g : f) {
							TreeObject t = new TreeObject(Types.SVALUE);
							TreeObject y = new TreeObject(Types.NUMBER);
							y.getChildren().add(new TreeObject(g));
							t.getChildren().add(y);
							vector.getChildren().add(t);
							vector.getChildren().add(new TreeObject(","));
							}
						}
						vector.getChildren().add(new TreeObject("]"));
						matrix.getChildren().add(vector);
					}
					matrix.getChildren().add(new TreeObject("]"));
					value.getChildren().add(matrix);
				} else 
				if(isFunction && !token.getContent().equals("}")) { //FUNCTION CONTENT
					functionContent.getChildren().add( new TreeObject(token.getContent()));
				} else
				if(isObject && token.getContent().endsWith("}")) {
					token.setContent(token.getContent().replace("}", ""));
					TreeObject s = new TreeObject(Types.SVALUE);
					TreeObject string = new TreeObject(Types.STRING);
					string.getChildren().add(new TreeObject(token.getContent()));
					s.getChildren().add(string);
					object.getChildren().add(s);
					object.getChildren().add(new TreeObject("}"));
				} else 
				if(token.getContent().equals("+")) {
					if(isExpression) {
						lastOperator.push(Types.ARITHMETIC);
						TreeObject t = new TreeObject(Types.OPERATOR);
						t.getChildren().add(new TreeObject(token.getContent()));
						expression.getChildren().add(t);
					}
				} else 
				if(token.getContent().matches(IdentifierObject.NUMBER_REGEX)) { //NUMBER DEFINITION
					if(lastOperator.peek().equals("=") ||
							lastOperator.peek().equals(Types.ARITHMETIC)
							|| lastOperator.peek().equals(Types.ARITHMETIC)) {
						lastOperator.push(Types.NUMBER);
						TreeObject t = new TreeObject(Types.NUMBER);
						t.getChildren().add(new TreeObject(token.getContent()));
						TreeObject sV = new TreeObject(Types.SVALUE);
						sV.getChildren().add(t);
						value.getChildren().add(sV);
					}
					
				} else 
					
				if(ArithmetObject.arithmetTokens.contains(token.getContent())) { //ARITHMETIC 
					if(!lastOperator.peek().equals(Types.IDENTIFIER) && 
							!lastOperator.peek().equals(Types.BOOLEAN) &&
							!lastOperator.peek().equals(Types.ARRAY) &&
							!lastOperator.peek().equals(Types.STRING) &&
							!lastOperator.peek().equals(Types.NUMBER)) {
						hasError = true;
						break;
					}
					if(!isExpression) {
						isExpression = true;
						TreeObject s = new TreeObject(Types.IDENTIFIER);
						s.getChildren().add(value.getChildren().get(value.getChildren().size()-1).getChildren().get(0));
						value.getChildren().remove(value.getChildren().size()-1);
						expression = new TreeObject(Types.EXPRESSION);
						expression.getChildren().add(s);
						TreeObject ary = new TreeObject(Types.ARITHMETIC);
						ary.getChildren().add(new TreeObject(token.getContent()));
						expression.getChildren().add(ary);
						value.getChildren().add(expression);					
					} else {
						TreeObject t = new TreeObject(Types.ARITHMETIC);
						t.getChildren().add(new TreeObject(token.getContent()));
						expression.getChildren().add(t);
						
					}
				} else
					
				if(BooleanObject.booleanTokens.contains(token.getContent())) { //BOOLEAN
					if(lastOperator.peek().equals("=") ||
							lastOperator.peek().equals(Types.BOOLEAN)) {
						lastOperator.push(token.getContent());
						TreeObject t = new TreeObject(Types.BOOLEAN);
						TreeObject sV = new TreeObject(Types.SVALUE);
						t.getChildren().add(new TreeObject(token.getContent()));
						sV.getChildren().add(t);
						value.getChildren().add(sV);
					}
				} else
					
				if(token.getContent().matches(IdentifierObject.IDENTIFIER_REGEX) && !isString && !isExpression && !isObject) { //IDENTIFIER
					if(lastOperator.peek().equals("=") ||
							lastOperator.peek().equals(Types.ARITHMETIC)) {
						lastOperator.push(Types.IDENTIFIER);
						TreeObject t = new TreeObject(Types.IDENTIFIER);
						t.getChildren().add(new TreeObject(token.getContent()));
						value.getChildren().add(t);
					} 
				} else
				if(token.getContent().matches(IdentifierObject.IDENTIFIER_REGEX) && isExpression) {
					TreeObject t = new TreeObject(Types.IDENTIFIER);
					t.getChildren().add(new TreeObject(token.getContent()));
					expression.getChildren().add(t);
				} else 
				if(token.getContent().matches(IdentifierObject.IDENTIFIER_REGEX) && isObject) {
					TreeObject sV = new TreeObject(Types.SVALUE);
					TreeObject t = new TreeObject(Types.IDENTIFIER);
					t.getChildren().add(new TreeObject(token.getContent()));
					sV.getChildren().add(t);
					object.getChildren().add(sV);
				} else 
				if(token.getContent().length() > 0 && token.getContent().charAt(0) == '{') { //OBJECT START
					isObject = true;
					object = new TreeObject(Types.OBJECT);
					value.getChildren().add(object);
					object.getChildren().add(new TreeObject("{"));
				} else
					
				if(token.getContent().equals("}") && isObject) { //OBJECT END
					isObject = false;
					object.getChildren().add(new TreeObject("}"));
				} else
					
				if(token.getContent().equals("}") && isFunction) { //FUNCTION END
					isFunction = false;
					function.getChildren().add(new TreeObject("}"));
				} else 
					
				if(token.getContent().startsWith("'") || token.getContent().startsWith("\"")) {
					if(token.getContent().endsWith("'") || token.getContent().endsWith("\"")) {
						if(isObject) {
							TreeObject t = new TreeObject(Types.STRING);
							t.getChildren().add(new TreeObject(token.getContent()));
							object.getChildren().add(t);
						}
					} else {
						if(token.getContent().charAt(token.getContent().length()-2) == '\'' && isArray) {
							token.setContent(token.getContent().replace("]", ""));
							TreeObject t = new TreeObject(Types.SVALUE);
							TreeObject s = new TreeObject(Types.STRING);
							s.getChildren().add(new TreeObject(token.getContent()));
							t.getChildren().add(s);
							array.getChildren().add(t);
							array.getChildren().add(new TreeObject("]"));
							
						} else {
							isString = true;
							TreeObject t = new TreeObject(Types.STRING);
							t.getChildren().add(new TreeObject(token.getContent()));
							if(isObject) {
								TreeObject sV = new TreeObject(Types.SVALUE);
								sV.getChildren().add(t);
								object.getChildren().add(sV);
							}
							if(isArray) {
								array.getChildren().add(t);
							}
						}
					}
				} else 
					
				if(token.getContent().endsWith("'") || token.getContent().endsWith("\"")) {
					isString = false;
					TreeObject t = new TreeObject(Types.STRING);
					t.getChildren().add(new TreeObject(token.getContent()));
					object.getChildren().add(t);
				} else 
					
				if(token.getContent().equals(":") && isObject) {
					object.getChildren().add(new TreeObject(token.getContent()));
				} else
					
				if(token.getContent().startsWith("[")) {
					array = new TreeObject(Types.ARRAY);
					value.getChildren().add(array);
					isArray = true;
					token.setContent(token.getContent().replace("[", "").replace(",", ""));
					TreeObject t = new TreeObject(Types.SVALUE);
					TreeObject s = new TreeObject(Types.STRING);
					s.getChildren().add(new TreeObject(token.getContent()));
					t.getChildren().add(s);
					array.getChildren().add(new TreeObject("["));
					array.getChildren().add(t);
					array.getChildren().add(new TreeObject(","));
				}
			
			}
		}
	}
	
	public TreeObject getRoot() {
		return s;
	}
}
