package edu.rit.dbc.server;

import java.io.IOException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

/**
 * ProxyGenerator.java binds the service into the registry and generates byte
 * code for ContractProxy
 * 
 * @author Ashwin
 * 
 */
public class ProxyGenerator extends ClassLoader {
	
	private static final String package_path_asm = "edu/rit/dbc/server/";
	private static final String package_path_classLoader = package_path_asm.replace("/", ".");
	private static final String AbstractContractProxy = package_path_asm+"AbstractContractProxy";
	private static final String ContractProxy = package_path_asm+"ContractProxy";
	
	private static final int RSTORE = 33;
	private static final int RRETURN = 151;
	
	public ProxyGenerator() {}

	/**
	 * Constructor: Binds the service and generates byte code for ContractProxy
	 * @param service
	 * @param bind_name
	 * @param service_interface
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public ProxyGenerator(Object service, String bind_name,
			Class<?> service_interface) throws IOException,
			InstantiationException, IllegalAccessException {
		bindServer(service, bind_name);
		createProxyByteCode(service_interface, bind_name);
	}

	/**
	 * Generates Byte Code for the class ContractProxy
	 * @param reference
	 * @param bind_name
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void createProxyByteCode(Class<?> reference, String bind_name)
			throws IOException, InstantiationException, IllegalAccessException {

		

		Class<?> interfaceType = reference;
		String interfaceName = interfaceType.getName().replace(".", "/");
		String Contract = package_path_asm+"Contract_"+interfaceType.getSimpleName();
		Method[] methods = interfaceType.getMethods();
		

		// ClassWriter computes maxStack and maxLocals, when
		// ClassWriter.COMPUTE_MAXS is passed to it.
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		int maxStack = 0, maxLocals = 0;
		FieldVisitor fv;
		MethodVisitor mv;

		/**
		 * Create class ContractProxy extends - AbstractContractProxy implements
		 * - {IAdder, java.io.Serializable}
		 * 
		 * ALOAD instructions read a local variable and push its value on the
		 * operand stack. DUP pushes a copy of the top stack value. ASTORE
		 * instructions pop a value from the operand stack and store it in a
		 * local variable.
		 */
		cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, ContractProxy, null,
				AbstractContractProxy, new String[] { interfaceName,
						"java/io/Serializable" });

		fv = cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC,
				"serialVersionUID", "J", null, new Long(-6025540187823398337L));
		fv.visitEnd();

		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, AbstractContractProxy, "<init>",
				"()V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(maxStack, maxLocals);
		mv.visitEnd();

		/**
		 * Define methods declared in the Service Interface
		 */
		TypeHandler typeHandler = new TypeHandler();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String methodName = method.getName();
			System.out.println(methodName);
			String methodDescription = Type.getMethodDescriptor(method);
			String[] exceptions = typeHandler.getMethodExceptions(method);

			// Calculate the Local Variable types
			Type[] argTypes = Type.getArgumentTypes(method);
			int[] op_argLoadType = typeHandler.localVariableLoadType(argTypes);

			// Calculate the return type of the method
			Type returnType = Type.getReturnType(method);
			int op_returnType = typeHandler.calculateReturnType(returnType);
			int rload = op_returnType;
			int rstore = op_returnType + RSTORE;
			int rreturn = op_returnType + RRETURN;

			// Number of arguments passed to the method
			int n_arguments = argTypes.length;

			mv = cw.visitMethod(ACC_PUBLIC, methodName, methodDescription,
					null, exceptions);
			mv.visitCode();
			/***********************************************************************************/
		/*	mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
					"Ljava/io/PrintStream;");
			mv.visitLdcInsn("Inside Contract Proxy");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
					"(Ljava/lang/String;)V");*/
			/***********************************************************************************/

			// Contract object
			mv.visitTypeInsn(NEW, Contract); 
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, Contract, "<init>", "()V");
			mv.visitVarInsn(ASTORE, n_arguments + 1);
			// depends on no of method arguments
			mv.visitInsn(ICONST_0 + n_arguments); 
			// object array
			mv.visitTypeInsn(ANEWARRAY, "java/lang/Object"); 

			for (int j = 0; j < n_arguments; j++) {
				mv.visitInsn(DUP);
				// method arguments index
				mv.visitInsn(ICONST_0 + j); 
				mv.visitVarInsn(op_argLoadType[j], j + 1);
				String wrapper = typeHandler.getWrapper(argTypes[j]);
				String wrapperDescriptor = typeHandler
						.getWrapperDescriptor(argTypes[j]);
				if (wrapper != null && wrapperDescriptor != null) {
					mv.visitMethodInsn(INVOKESTATIC, wrapper, "valueOf",
							wrapperDescriptor);
				}
				mv.visitInsn(AASTORE);
			}
			// Contract object
			mv.visitVarInsn(ASTORE, n_arguments + 2);
			//No of arguments
			mv.visitVarInsn(ALOAD, n_arguments + 1);
			//Contract object
			mv.visitVarInsn(ALOAD, n_arguments + 2);
			mv.visitMethodInsn(INVOKEVIRTUAL, Contract, "checkContract_"
					+ methodName, "([Ljava/lang/Object;)Z");
			
			//Boolean store 
			mv.visitVarInsn(ISTORE, n_arguments + 3);
			// Boolean load
			mv.visitVarInsn(ILOAD, n_arguments + 3);

			Label l0 = new Label();
			mv.visitJumpInsn(IFNE, l0);
			mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException",
					"<init>", "()V");
			mv.visitInsn(ATHROW);
			mv.visitLabel(l0);
			mv.visitFrame(F_APPEND, 3, new Object[] { Contract,
					"[Ljava/lang/Object;", Opcodes.INTEGER }, 0, null);

			mv.visitMethodInsn(INVOKESTATIC,
					"java/rmi/registry/LocateRegistry", "getRegistry",
					"()Ljava/rmi/registry/Registry;");
			mv.visitFieldInsn(PUTSTATIC, ContractProxy, "registry",
					"Ljava/rmi/registry/Registry;");
			mv.visitFieldInsn(GETSTATIC, ContractProxy, "registry",
					"Ljava/rmi/registry/Registry;");
			mv.visitLdcInsn(bind_name);
			mv.visitMethodInsn(INVOKEINTERFACE, "java/rmi/registry/Registry",
					"lookup", "(Ljava/lang/String;)Ljava/rmi/Remote;");
			mv.visitTypeInsn(CHECKCAST, interfaceName);

			mv.visitVarInsn(ASTORE, n_arguments + 1);
			mv.visitVarInsn(ALOAD, n_arguments + 1);

			for (int j = 1; j <= n_arguments; j++) {
				mv.visitVarInsn(op_argLoadType[j - 1], j);
			}
			mv.visitMethodInsn(INVOKEINTERFACE, interfaceName, methodName,
					methodDescription);

			mv.visitVarInsn(rstore, n_arguments + 2);
			mv.visitVarInsn(rload, n_arguments + 2);
			mv.visitInsn(rreturn);
			// These values are computed by ClassWriter
			// Reference Page 44 - asm4.0-guide.pdf
			mv.visitMaxs(maxStack, maxLocals);
			mv.visitEnd();
		}
		/**
		 * Create method bind signature - public void bind(String bind_name)
		 * 
		 * Binds the ContractProxy into the registry
		 */
		mv = cw.visitMethod(ACC_PUBLIC, "bind", "(Ljava/lang/String;)V", null,
				new String[] { "java/rmi/RemoteException" });
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ASTORE, 2);
		mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf",
				"(Ljava/lang/Object;)Ljava/lang/String;");
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>",
				"(Ljava/lang/String;)V");
		mv.visitLdcInsn("_proxy");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
				"(Ljava/lang/String;)Ljava/lang/StringBuilder;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder",
				"toString", "()Ljava/lang/String;");
		mv.visitVarInsn(ASTORE, 1);
		mv.visitMethodInsn(INVOKESTATIC, "java/rmi/registry/LocateRegistry",
				"getRegistry", "()Ljava/rmi/registry/Registry;");
		mv.visitFieldInsn(PUTSTATIC, ContractProxy, "registry",
				"Ljava/rmi/registry/Registry;");
		mv.visitFieldInsn(GETSTATIC, ContractProxy, "registry",
				"Ljava/rmi/registry/Registry;");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/rmi/registry/Registry",
				"rebind", "(Ljava/lang/String;Ljava/rmi/Remote;)V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(maxStack, maxLocals);
		mv.visitEnd();

		cw.visitEnd();
		byte[] code = cw.toByteArray();

		CodeBaseWriter cbw = new CodeBaseWriter();
		cbw.write(code, interfaceType);
		ProxyGenerator loader = new ProxyGenerator();
		Class<?> contractProxyClass = loader.defineClass(package_path_classLoader+"ContractProxy", code,
				0, code.length);

		AbstractContractProxy cp = (AbstractContractProxy) contractProxyClass
				.newInstance();
		cp.bind(bind_name);
	}

	/**
	 * Binds the service into the registry
	 * 
	 * @param service
	 * @param bind_name
	 * @throws RemoteException
	 */
	private void bindServer(Object service, String bind_name)
			throws RemoteException {
		Registry registry = LocateRegistry.getRegistry();
		registry.rebind(bind_name, (Remote) service);
		System.out.println("object registered");
	}
}
