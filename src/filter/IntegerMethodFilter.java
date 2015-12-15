package filter;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.visitor.Filter;

public class IntegerMethodFilter implements Filter<CtMethod<?>> {

	@Override
	public boolean matches(CtMethod<?> method) {
		
		for(CtParameter<?> parametre : method.getParameters()){
			if(!parametre.getReference().getType().isPrimitive()){
				return false;
			}
		}
		
		if(method.getType().getActualClass().equals(Integer.class) || method.getType().getActualClass().equals(int.class)){
			return true;
		}
		
		
		return false;
	}

}
