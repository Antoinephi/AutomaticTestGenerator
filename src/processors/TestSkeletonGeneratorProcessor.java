package processors;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTimeUtils;

import filter.IntegerMethodFilter;
import parameters.IntValues;
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
		
		DateTimeUtils.setCurrentMillisFixed(1449669010386l);
		
		if(c.isTopLevel() && !c.hasModifier(ModifierKind.ABSTRACT) && c.hasModifier(ModifierKind.PUBLIC)){
			Class<?> classe = null;
		      try {
		          classe = Class.forName(c.getParent()+"."+c.getSimpleName());
		       }
		       catch(Exception e) {
		          System.out.println("Impossible d'instancier la classe "+c.getParent()+"."+c.getSimpleName());
		       }
			CtPackage p = 	getFactory().Core().createPackage();
			p.setSimpleName("test");
			CtClass<?> newClass = getFactory().Class().create(getFactory().Package().create(p, c.getPackage().getSimpleName()) ,c.getSimpleName()+"Test");
			newClass.addModifier(ModifierKind.PUBLIC);
			CtMethod<Void> newMethodBefore = newClass.getFactory().Core().createMethod();
			newMethodBefore.setType(getFactory().Type().VOID_PRIMITIVE);
			newMethodBefore.setSimpleName("setupMethod");
			
			CtAnnotation<?> annotationSetup = new CtAnnotationImpl<>();					
			annotationSetup.setAnnotationType(getFactory().Annotation().createReference(org.junit.BeforeClass.class));
			newMethodBefore.addAnnotation(annotationSetup);
			
			CtCodeSnippetStatement codeSetup =  getFactory().Code().createCodeSnippetStatement("org.joda.time.DateTimeUtils.setCurrentMillisFixed(1449669010386l)");
			final CtBlock blockSetup = getFactory().Code().createCtBlock(codeSetup);
			newMethodBefore.setBody(blockSetup);
			newMethodBefore.addModifier(ModifierKind.STATIC);
			newMethodBefore.addModifier(ModifierKind.PUBLIC);
			newClass.addMethod(newMethodBefore);

			List<CtMethod<?>> methodsList =  Query.getElements( c, new IntegerMethodFilter());
			for(CtMethod<?> m : methodsList){
				if(m.hasModifier(ModifierKind.PUBLIC) && m.getParent().equals(c)){
					CtMethod<Void> newMethod = newClass.getFactory().Core().createMethod();
					newMethod.setType(getFactory().Type().VOID_PRIMITIVE);
					
					newMethod.setSimpleName("test" + m.getSimpleName());
					newMethod.setVisibility(m.getVisibility());
					

					
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

	private List<CtCodeSnippetStatement> generateMethodResult(Class<?> classe, CtMethod<?> m) {
		List<CtCodeSnippetStatement> listeInstruction = new ArrayList<>();
		listeInstruction.add(getFactory().Code().createCodeSnippetStatement("junit.framework.Assert.fail()"));

		if(classe != null){
			Integer valeur = 1;
			 try {							 
				 Class<?> tab[] = new Class[m.getParameters().size()];
				 for(int i=0;i<tab.length;i++){
					 tab[i] = m.getParameters().get(i).getType().getActualClass();
				 }
				Method methode = classe.getMethod(m.getSimpleName(), tab);
				if(m.getType().getActualClass().equals(Integer.class) || m.getType().getActualClass().equals(int.class)){
					integerManager(classe, m, listeInstruction, valeur, tab, methode);
				}else if(m.getType().getActualClass().equals(java.lang.String.class)){
					//TODO stringManager
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

	private void integerManager(Class<?> classe, CtMethod<?> m, List<CtCodeSnippetStatement> listeInstruction,
			Integer valeur, Class<?>[] tab, Method methode)
					throws IllegalAccessException, InvocationTargetException, InstantiationException {
		if(hasConstructorWithoutParameter(classe)){
			String nameObjet = classe.getSimpleName()+valeur++;
			listeInstruction.add(getFactory().Code().createCodeSnippetStatement(classe.getCanonicalName()+" "+nameObjet+" = new "+classe.getCanonicalName()+"()"));
			Object retour = executeMethod(classe, methode, tab);
			if(retour != null){
				listeInstruction.remove(0);
				listeInstruction.add(getFactory().Code().createCodeSnippetStatement("junit.framework.Assert.assertEquals("+retour.toString()+","+nameObjet+"."+m.getSimpleName()+"())"));
			}
		}
	}

	private Object executeMethod(Class<?> classe, Method methode, Class<?>[] tabParameter) {
		try{
			if(tabParameter.length == 0){
				return methode.invoke(classe.newInstance(), (Object[])null);
			}else{
				Object tab[] = new Object[tabParameter.length];
				
				for(int i=0;i<tabParameter.length;i++){
					Class parametre = tabParameter[i];
					if(!parametre.isPrimitive()){
						tab[i] = null;
					}else{
						switch (parametre.getName()) {
						case "int":
							Integer parametre1 = new Integer(IntValues.ZERO.toString());
							tab[i] = parametre1;
							//TODO gérer le paramètre côté génération
							break;

						default:
							System.out.println(parametre.getName());
							tab[i] = null;
							//TODO tenter d'instancier quand même
						}

					}
					
				}
				
				return methode.invoke(classe.newInstance(), tab);
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	private boolean hasConstructorWithoutParameter(Class<?> classe) {
		try {
			Constructor<?> constructeur =  classe.getConstructor(new Class[0]);
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
