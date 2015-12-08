package processors;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import filter.IntegerMethodFilter;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.visitor.Query;
import spoon.support.reflect.declaration.CtAnnotationImpl;

public class TestSkeletonGeneratorProcessor extends AbstractProcessor<CtClass<?>> {

	public void process(CtClass<?> c) {
		if(c.isTopLevel() && !c.hasModifier(ModifierKind.ABSTRACT) && c.hasModifier(ModifierKind.PUBLIC)){
			Class classe = null;
		      try {
		          classe = Class.forName(c.getParent()+"."+c.getSimpleName());
		       }
		       catch(Exception e) {
		          System.out.println("Impossible d'instancier la classe "+c.getParent()+"."+c.getSimpleName());
		       }
			CtPackage p = 	getFactory().Core().createPackage();
			p.setSimpleName("test");
			CtClass<?> newClass = getFactory().Class().create(getFactory().Package().create(p, c.getPackage().getSimpleName()) ,c.getSimpleName()+"Test");
			List<CtMethod<?>> methodsList =  Query.getElements( c, new IntegerMethodFilter());
			for(CtMethod<?> m : methodsList){
				if(m.hasModifier(ModifierKind.PUBLIC) && m.getParent().equals(c)){
					CtMethod<Void> newMethod = newClass.getFactory().Core().createMethod();
					newMethod.setType(getFactory().Type().VOID);
					
					newMethod.setSimpleName("test" + m.getSimpleName());
					newMethod.setVisibility(m.getVisibility());
					newMethod.setParameters(m.getParameters());
					

					
					CtAnnotation<?> annotation = new CtAnnotationImpl<>();					
					annotation.setAnnotationType(getFactory().Annotation().createReference(org.junit.Test.class));
					newMethod.addAnnotation(annotation);
					
					List<CtCodeSnippetStatement> listeInstruction = generateMethodResult(classe, m);
			        final CtCodeSnippetStatement statementInConstructor = listeInstruction.get(0);
			        final CtBlock ctBlockOfConstructor = getFactory().Code().createCtBlock(statementInConstructor);
					
			        newMethod.setBody(ctBlockOfConstructor);
			        
					for(int i=1;i<listeInstruction.size();i++){
				        newMethod.getBody().addStatement(listeInstruction.get(i));
					}
					
					newClass.addMethod(newMethod);


				}
			}
		}
	}

	private List<CtCodeSnippetStatement> generateMethodResult(Class classe, CtMethod<?> m) {
		List<CtCodeSnippetStatement> listeInstruction = new ArrayList<>();
		listeInstruction.add(getFactory().Code().createCodeSnippetStatement("junit.framework.Assert.fail()"));

		if(classe != null){
			Integer valeur = 1;
			 try {							 
				 Class tab[] = new Class[m.getParameters().size()];
				 for(int i=0;i<tab.length;i++){
					 tab[i] = m.getParameters().get(i).getType().getActualClass();
				 }
				Method methode= classe.getMethod(m.getSimpleName(), tab);
				if(tab.length == 0 && hasConstructorWithoutParameter(classe)){
					
					listeInstruction.add(getFactory().Code().createCodeSnippetStatement(classe.getCanonicalName()+" "+classe.getSimpleName()+(valeur++)+" = new "+classe.getCanonicalName()+"()"));
					Object retour = methode.invoke(classe.newInstance(), (Object[])null);
				}
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return listeInstruction;
	}

	private boolean hasConstructorWithoutParameter(Class classe) {
		try {
			Constructor constructeur =  classe.getConstructor(new Class[0]);
			if(constructeur.toGenericString().startsWith("public")){
				return true;
			}
		} catch (NoSuchMethodException e) {
			return false;
		} catch (SecurityException e) {
			return false;
		}
		
		return false;
	}

    
	public void processingDone(){
		System.out.println("Done !");
	}
	
}
