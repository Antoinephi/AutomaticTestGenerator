package filter;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;

public class StringMethodFilter implements Filter<CtMethod<?>> {

	@Override
	public boolean matches(CtMethod<?> method) {
		if(method.getType().getActualClass().equals(java.lang.String.class)){
			return true;
		}
		
		return false;
	}

}
