package processors;

import java.lang.annotation.Annotation;
import java.util.Set;

import jdk.nashorn.internal.runtime.regexp.JoniRegExp.Factory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.AnnotationFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtAnnotationImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;

public class TestSkeletonGeneratorProcessor extends AbstractProcessor<CtClass<?>> {

	public void process(CtClass<?> c) {
		if(c.isTopLevel() && !c.hasModifier(ModifierKind.ABSTRACT)){
			CtPackage p = 	getFactory().Core().createPackage();
			p.setSimpleName("test");
			CtClass<?> newClass = getFactory().Class().create(getFactory().Package().create(p, c.getPackage().getSimpleName()) ,c.getSimpleName()+"Test");
			Set<CtMethod<?>> methodsList = c.getAllMethods();
			for(CtMethod<?> m : methodsList){
				if(m.hasModifier(ModifierKind.PUBLIC)){
					CtMethod<Void> newMethod = newClass.getFactory().Core().createMethod();
					newMethod.setType(getFactory().Type().VOID);
					
					newMethod.setSimpleName("test" + m.getSimpleName());
					newMethod.setVisibility(m.getVisibility());
					newMethod.setParameters(m.getParameters());
					
			        final CtCodeSnippetStatement statementInConstructor = getFactory().Code().createCodeSnippetStatement("junit.framework.Assert.fail()");
			        final CtBlock ctBlockOfConstructor = getFactory().Code().createCtBlock(statementInConstructor);
					
			        newMethod.setBody(ctBlockOfConstructor);
					CtAnnotation<?> annotation = new CtAnnotationImpl<>();					
					annotation.setAnnotationType(getFactory().Annotation().createReference(org.junit.Test.class));
					
					newMethod.addAnnotation(annotation);
					
					newClass.addMethod(newMethod);
					
					
				}
			}
		}
	}

	public void processingDone(){
		System.out.println("Done !");
	}
	
}
