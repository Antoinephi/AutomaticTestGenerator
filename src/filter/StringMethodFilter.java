package filter;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;

public class StringMethodFilter implements Filter<CtMethod<?>> {

	@Override
	public boolean matches(CtMethod<?> method) {
		if(method.getType().getSimpleName().endsWith("String")){
			return true;
		}
		
		return false;
	}

}
