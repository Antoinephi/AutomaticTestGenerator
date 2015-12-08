package filter;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;

public class IntegerMethodFilter implements Filter<CtMethod<?>> {

	@Override
	public boolean matches(CtMethod<?> method) {
		if(method.getType().getActualClass().equals(Integer.class) || method.getType().getActualClass().equals(int.class)){
			return true;
		}
		
		return false;
	}

}
