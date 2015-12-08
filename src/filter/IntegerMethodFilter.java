package filter;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;

public class IntegerMethodFilter implements Filter<CtMethod<?>> {

	@Override
	public boolean matches(CtMethod<?> method) {
		if(method.getType().getSimpleName().endsWith("Integer") || method.getType().getSimpleName().equals("int")){
			return true;
		}
		
		return false;
	}

}
