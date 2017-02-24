package edu.rit.dbc.server;




import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Type.BOOLEAN_TYPE;
import static org.objectweb.asm.Type.BYTE_TYPE;
import static org.objectweb.asm.Type.CHAR_TYPE;
import static org.objectweb.asm.Type.DOUBLE_TYPE;
import static org.objectweb.asm.Type.FLOAT_TYPE;
import static org.objectweb.asm.Type.INT_TYPE;
import static org.objectweb.asm.Type.LONG_TYPE;
import static org.objectweb.asm.Type.SHORT_TYPE;

import java.lang.reflect.Method;

import org.objectweb.asm.Type;

/**
 * TypeHandler: Utility class that helps to handle Type Descriptors of data types and methods
 * @author Ashwin
 *
 */
public class TypeHandler {
	/**
	 * This function finds the return type of the method 
	 * @param returnType
	 * @return byte code instruction
	 */
	public int calculateReturnType(Type returnType) {
		return getType(returnType);
	}

	/**
	 * This function finds the type of each local variable to be loaded
	 * @param argTypes
	 * @return array of byte code instructions
	 */
	public int[] localVariableLoadType(Type[] argTypes) {
		
		int[] type = new int[argTypes.length];
		
		for(int i=0;i<argTypes.length;i++){
			type[i] = getType(argTypes[i]);
		}
		return type;
	}
	
	/**
	 * Finds the Byte Code Instruction given a Type Descriptor 
	 * @param type
	 * @return byte code instruction
	 */
	public int getType(Type type) {
		int load;
		if(type.equals(BOOLEAN_TYPE)||type.equals(CHAR_TYPE)||type.equals(BYTE_TYPE)||type.equals(SHORT_TYPE)||type.equals(INT_TYPE)){
			load = ILOAD;
		}
		else if(type.equals(LONG_TYPE)){
			load = LLOAD;
		}
		else if(type.equals(FLOAT_TYPE)){
			load = FLOAD;
		}
		else if(type.equals(DOUBLE_TYPE)){
			load = DLOAD;
		}
		else{
			load = ALOAD;
		}
		return load;
	}

	/**
	 * This method returns an array of exceptions thrown by a method
	 * @param method 
	 * @return array of Exceptions
	 */
	public String[] getMethodExceptions(Method method) {
		Class<?>[] methodExceptions = method.getExceptionTypes();
		String[] exceptions = new String[methodExceptions.length];
		for (int i = 0; i < methodExceptions.length; i++) {
			exceptions[i] = methodExceptions[i].getName().replace('.', '/');
		}
		return exceptions;
	}
	
	/**
	 * Get wrapper classes fully qualified names for primitive types.
	 * @param type
	 * @return
	 */
	public String getWrapper(Type type){
		if(type.equals(Type.INT_TYPE)){
			return java.lang.Integer.class.getName().replace('.', '/');
		}
		else if(type.equals(Type.FLOAT_TYPE)){
			return java.lang.Float.class.getName().replace('.', '/');
		}
		else if(type.equals(Type.SHORT_TYPE)){
			return java.lang.Short.class.getName().replace('.', '/');
		}
		else if(type.equals(Type.BOOLEAN_TYPE)){
			return java.lang.Boolean.class.getName().replace('.', '/');
		}
		else if(type.equals(Type.BYTE_TYPE)){
			return java.lang.Byte.class.getName().replace('.', '/');
		}
		else if(type.equals(Type.LONG_TYPE)){
			return java.lang.Long.class.getName().replace('.', '/');
		}
		else if(type.equals(Type.CHAR_TYPE)){
			return java.lang.Character.class.getName().replace('.', '/');
		}
		else if(type.equals(Type.DOUBLE_TYPE)){
			return java.lang.Double.class.getName().replace('.', '/');
		}
		else {
		return null;
		}
	}
	
	/**
	 * Get type descriptor for the valueOf() method called by the wrapper classes
	 * @param type
	 * @return
	 */
	public String getWrapperDescriptor(Type type){
		if(type.equals(Type.INT_TYPE)){
			return "(I)"+Type.getObjectType(java.lang.Integer.class.getName()).toString().replace('.', '/');
		}
		else if(type.equals(Type.FLOAT_TYPE)){
			return "(F)"+Type.getObjectType(java.lang.Float.class.getName()).toString().replace('.', '/');
		}
		else if(type.equals(Type.SHORT_TYPE)){
			return "(S)"+Type.getObjectType(java.lang.Short.class.getName()).toString().replace('.', '/');
		}
		else if(type.equals(Type.BOOLEAN_TYPE)){
			return "(Z)"+Type.getObjectType(java.lang.Boolean.class.getName()).toString().replace('.', '/');
		}
		else if(type.equals(Type.BYTE_TYPE)){
			return "(B)"+Type.getObjectType(java.lang.Byte.class.getName()).toString().replace('.', '/');
		}
		else if(type.equals(Type.LONG_TYPE)){
			return "(J)"+Type.getObjectType(java.lang.Long.class.getName()).toString().replace('.', '/');
		}
		else if(type.equals(Type.CHAR_TYPE)){
			return "(C)"+Type.getObjectType(java.lang.Character.class.getName()).toString().replace('.', '/');
		}
		else if(type.equals(Type.DOUBLE_TYPE)){
			return "(D)"+Type.getObjectType(java.lang.Double.class.getName()).toString().replace('.', '/');
		}
		else {
		return null;
		}
	}
}
