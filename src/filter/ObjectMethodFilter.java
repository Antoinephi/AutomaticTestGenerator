package filter;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.visitor.Filter;

public class ObjectMethodFilter  implements Filter<CtMethod<?>> {

	@Override
	public boolean matches(CtMethod<?> element) {
		if(element.getType().isPrimitive()){
			return false;
		}
		
		for(CtParameter<?> parametre : element.getParameters()){
			if(!parametre.getReference().getType().isPrimitive()){
				return false;
			}
		}
		return true;
	}

}
