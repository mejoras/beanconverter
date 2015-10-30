package com.indra.isl.malaga;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.indra.davinci.common.log.LogModule;

/**
 * Método que copia atributos de objetos siempre y cuando coincidan nombre de
 * atributo origen y atributo destino. Funciona con tipos simples y objetos. En
 * caso de listas de objetos o tipos simples, itera y copia.
 *
 * @author dmachado
 *
 */
public class BeanConverter {

	/** Logger */
	private final static Logger LOGGER = LogModule.getInstance()
			.getLoggerModule(BeanConverter.class.getName(),
					BeanConverter.class.getName());

	/**
	 * Mapas donde contendremos la información necesario de la relación entre
	 * parametros y de los metodos.
	 */
	static Map<String, String> mapConverter = new HashMap<String, String>();
	static Map<String, String> mapConverterReverse = new HashMap<String, String>();
	static Map<String, String> mapMethodConverter = new HashMap<String, String>();

	/** Listado que contiene los nombres que contienen anotación. */
	static List<String> listNameAttr;

	/** Separadores. */
	static CharSequence comaChart = ",";
	static CharSequence parentesisChartOpen = "(";
	static CharSequence parentesisChartClose = ")";

	/** Clase Origen. */
	static Class<? extends Object> fromClass;

	/**
	 * Realiza la conversión de un objeto a otro sin necesidad de que los
	 * atributos de origen y destino coincidan.
	 *
	 * @param fromObj
	 *            Objeto de datos origen.
	 * @param clazz
	 *            Clase la cual se realizará la copia.
	 * @return Instancia con los valores correspondientes.
	 * @throws BeanConverterException
	 *             Excepción producida en la conversión.
	 */
	public static <T extends Object> Object copyProperties(Object fromObj,
			Class<T> clazz) throws BeanConverterException {

		// Listado de propiedades del Bean de datos.
		List<PropertyDescriptor> fromPd;

		// Atributos de la anotación 'NotNull'
		String[] souceNotNull = null;
		String[] souceAttrNotNull = null;

		// Atributos de la anotación 'Convert'
		String[] source = null;
		String[] sourceAttr = null;
		String[] convertMethod = null;
		listNameAttr = new ArrayList<String>();

		// Listado de attributos erroneos.
		List<String> errorAttr = new ArrayList<String>();

		// Instancia de la clase.
		Object instanceClass = null;

		try {
			// Comprobamos si la entrada es nula.
			if (fromObj == null) {
				return null;
			}

			// Instancia de las clases origen.
			fromClass = fromObj.getClass();

			if (fromClass.isEnum() && clazz.isEnum()) {
				try {
					// Find all possible values of source enum type and get the
					// selected one
					Object[] enumConstants = fromObj.getClass()
							.getEnumConstants();
					for (Object enumConstant : enumConstants) {
						if (enumConstant.equals(fromObj)) {

							// Using method 'valueOf' of source enum to set the
							// appropriate value in target enum
							Method method = clazz.getMethod("valueOf",
									String.class);
							return method.invoke(clazz, fromObj.toString());
						}
					}
				} catch (InvocationTargetException e) {
					LOGGER.log(Level.SEVERE, "Constant " + fromObj.toString()
							+ " not found in enum " + clazz.getName());
					return null;
				}
			}

			if (!fromClass.isEnum()) {
				/** Clase instanciada. */
				instanceClass = Class.forName(clazz.getName()).newInstance();
			}
			
			// Información de los bean de datos.
			BeanInfo fromBean = Introspector.getBeanInfo(fromClass);
			BeanInfo toBean = Introspector.getBeanInfo(clazz);

			// Devuelve descriptores para todas las propiedades del bean.
			PropertyDescriptor[] toPd = toBean.getPropertyDescriptors();
			fromPd = Arrays.asList(fromBean.getPropertyDescriptors());

			// Comprobamos si existen anotaciones a nivel de clase.
			if (clazz.isAnnotationPresent(NotNull.class)) {
				// Atributo de la anotación.
				souceNotNull = clazz.getAnnotation(NotNull.class).source();
				// Atributo de la anotación.
				souceAttrNotNull = clazz.getAnnotation(NotNull.class)
						.sourceAttr();

				// Iteramos sobre el array para comprobar que la clase existe.
				for (int i = 0; i < souceNotNull.length; i++) {
					// Si la clase es igual, es que existe y es la que debemos
					// comprobar.
					if (souceNotNull[i].equals(fromClass.getName())) {
						// Chequeamos que ningun atributo de la anotación que no
						// pueda ser nulo, lo sea.
						checkedAnotationNull(souceAttrNotNull[i], fromObj,
								fromPd);
					}
				}
			}

			// Recorremos los campos de la clase.
			for (Field field : clazz.getDeclaredFields()) {
				// Obtenemos el nombre del campo.
				String name = field.getName();
				// Si no tiene ningun tipo de anotación.
				if (!field.isAnnotationPresent(NotNull.class)
						&& !field.isAnnotationPresent(Convert.class)) {
					// Leemos las propiedas y realizamos su correspondiente
					// conversión.
					readProperties(toPd, field, fromObj, instanceClass, fromPd);
				} else if (field.isAnnotationPresent(NotNull.class)) {
					// Contiene anotación 'NotNull'. Comprobamos tambien si
					// contiene anotación 'Convert'
					if (!field.isAnnotationPresent(Convert.class)) {
						// No tiene anotación 'Convert'. Leemos las propiedas y
						// realizamos su correspondiente
						// conversión.
						checkedAnotationNull(field.getName(), fromObj, fromPd);
						// Leemos las propiedas y realizamos su correspondiente
						// conversión.
						readProperties(toPd, field, fromObj, instanceClass,
								fromPd);
					} else if (field.isAnnotationPresent(Convert.class)) {
						// Tratamos, extraemos y realizamos las operaciones
						// correspondientes.
						extracted(fromObj, fromPd, errorAttr, instanceClass,
								toPd, field, name);
					}
				} else if (field.isAnnotationPresent(Convert.class)) {
					// Tratamos, extraemos y realizamos las operaciones
					// correspondientes.
					extracted(fromObj, fromPd, errorAttr, instanceClass, toPd,
							field, name);
				}
			}
			// Recorremos los atributos que no existen.
			for (Iterator<String> iterator = errorAttr.iterator(); iterator
					.hasNext();) {
				String attrNonExistent = iterator.next();
				LOGGER.log(
						Level.WARNING,
						"The attribute '"
								+ attrNonExistent
								+ "' declared in the annotation does not exist in the class origin "
								+ fromClass.getName());
			}
		} catch (IntrospectionException e) {
			throw new BeanConverterException(
					"Exception produced due to introspection. "
							+ e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new BeanConverterException(
					"Exception produced due to an invalid argument", e);
		} catch (IllegalAccessException e) {
			throw new BeanConverterException(
					"Exception produced due to an invalid access", e);
		} catch (InvocationTargetException e) {
			throw new BeanConverterException(
					"Exception produced due to error invocation.", e);
		} catch (InstantiationException e) {
			LOGGER.log(Level.SEVERE,
					"(DefaultConstructor) - The class '" + e.getMessage()
							+ "' it failed due to:  " + e.getCause());
			throw new BeanConverterException(
					"Exception produced due to error instantiation.", e);
		} catch (ClassNotFoundException e) {
			throw new BeanConverterException(
					"Exception produced because the class is not found", e);
		} catch (NoSuchMethodException e) {
			throw new BeanConverterException(
					"Exception produced because there is no method.", e);
		} catch (SecurityException e) {
			throw new BeanConverterException(
					"Exception produced due to a security violation.", e);
		}

		// Devolvemos la instancia de la clase.
		return instanceClass;

	}

	/**
	 * Tratamos, extraemos y realizamos las operaciones correspondientes.
	 *
	 * @param fromObj
	 *            Objeto origen.
	 * @param fromPd
	 *            Propiedades objeto origen.
	 * @param errorAttr
	 *            Atributos erroneos.
	 * @param instanceClass
	 *            Clase Instancia.
	 * @param toPd
	 *            Propiedades.
	 * @param field
	 *            Campo.
	 * @param name
	 *            Nombre.
	 * @throws IllegalAccessException
	 *             Excepción lanzada por argumento invalido.
	 * @throws InvocationTargetException
	 *             Excepción lanzada por error de invocaicón.
	 * @throws ClassNotFoundException
	 *             Excepción lanzada por clase no encontradad.
	 * @throws BeanConverterException
	 *             Excepción lanzada por mala conversión.
	 * @throws NoSuchMethodException
	 *             Excepción lanzada por metodo no encontrado.
	 * @throws InstantiationException
	 *             Excepción lanzada por error en la instanciación de una clase.
	 */
	private static void extracted(Object fromObj,
			List<PropertyDescriptor> fromPd, List<String> errorAttr,
			Object instanceClass, PropertyDescriptor[] toPd, Field field,
			String name) throws IllegalAccessException,
			InvocationTargetException, ClassNotFoundException,
			BeanConverterException, NoSuchMethodException,
			InstantiationException {

		// Conversores/Atributos.
		String[] source;
		String[] sourceAttr;
		String[] convertMethod;

		// Listado de atriburtos.
		List<Object> attr = null;

		// Metodo.
		String methodReal = "";

		// Array que contendra atributos del metodo y metodo.
		String[] attrMethod = null;

		// Clase a convertir.
		String classConvert = "";

		// Atributo de la anotación (Clase).
		source = field.getAnnotation(Convert.class).source();
		// Atributo de la anotación (Atributo).
		sourceAttr = field.getAnnotation(Convert.class).sourceAttr();
		// Atributo del metodo (Método).
		convertMethod = field.getAnnotation(Convert.class).convertMethod();

		// Iteramos sobre el array para comprobar que la clase existe.
		for (int i = 0; i < source.length; i++) {
			// Si la clase es igual, es que existe y es la que debemos
			// comprobar.
			if (source[i].equals(fromClass.getName())) {
				try {
					// Creamos la relación AttributoAnotación - AtributoOrigen
					mapConverter.put(sourceAttr[i], name);
					// Si los atributos de la anotación no son correctos....
					if (!sourceAttr[i].isEmpty() && !convertMethod[i].isEmpty()
							|| sourceAttr[i].isEmpty()
							&& convertMethod[i].isEmpty()) {
						throw new UnsupportedOperationException(
								"Annotation misspecified.");
					} else {
						if (!sourceAttr[i].isEmpty()) {
							// Contiene atributo, no contiene el atributo de
							// anotación
							// convertMethod.
							checkedAnotationNull(sourceAttr[i], fromObj, fromPd);
							// Leemos las propiedas y realizamos su
							// correspondiente conversión.
							readProperties(toPd, field, fromObj, instanceClass,
									fromPd);
						} else if (!convertMethod[i].isEmpty()) {
							classConvert = "";

							// Obtenemos la cadena entera y la recorremos.
							String[] classMethod = convertMethod[i].toString()
									.split("\\.");
							for (int x = 0; x < classMethod.length - 1; x++) {
								classConvert = classConvert += classMethod[x]
										+ ".";
							}

							// Clase a la que hace referencia.
							classConvert = classConvert.substring(0,
									classConvert.length() - 1);
							// Metodo al cual hace referencia.
							String method = classMethod[classMethod.length - 1]
									.toString();

							// Si contiene parentesis tenemos method() ó
							// method(n, n...)
							if (method.contains(parentesisChartOpen)) {
								if (method.contains(parentesisChartClose)) {
									// Si no contiene comas teemos method() ó
									// method(n)
									if (!method.contains(comaChart)) {
										int firstParam = method.indexOf('(');
										int lastParam = method.indexOf(')');
										// Metodo sin parametros.
										if (lastParam - firstParam == 1) {
											methodReal = method.substring(0,
													lastParam - 1).trim();
											attr = new ArrayList<Object>();
										} else {
											// metodo con un unico parametro.
											String substring = method
													.substring(firstParam + 1,
															lastParam);
											attr = new ArrayList<Object>();
											attr.add(substring);
											methodReal = method.substring(0,
													firstParam).trim();
										}
									} else {
										// Contiene 'n' atributos.
										attrMethod = method.split("\\(");
										for (int x = 0; x < attrMethod.length; x++) {
											if (attrMethod[x]
													.contains(comaChart)) {
												attr = new ArrayList<Object>();
												String[] split3 = attrMethod[x]
														.substring(
																0,
																attrMethod[x]
																		.length() - 1)
														.split(",");
												for (int j = 0; j < split3.length; j++) {
													attr.add(split3[j]);
													errorAttr.add(split3[j]
															.trim());
												}
											}
										}
									}
									// Nombre del metodo.
									if (attrMethod != null) {
										methodReal = attrMethod[0].trim();
									}
								} else {
									// Excepción, anotación mal formada.
									throw new UnsupportedOperationException(
											"The operation can not be performed because the method '"
													+ attrMethod[0]
													+ "' is malformed.");
								}
							} else {
								// No tiene parentesis, es decir, se le pasa el
								// objeto entero.
								methodReal = method.trim();
							}

							// Definimos cuales son los que se corresponden con
							// los de la clase
							// origen.
							Field[] declaredFieldsTemp = fromClass
									.getDeclaredFields();
							for (int s = 0; s < declaredFieldsTemp.length; s++) {
								String nameTemp = declaredFieldsTemp[s]
										.getName();
								if (errorAttr.contains(nameTemp)) {
									errorAttr.remove(nameTemp);
								}
							}

							// Llamada al metodo definido en la anotación.
							Method methodCall = null;
							// Objeto 'class'.
							if (attr == null) {
								methodCall = Class.forName(classConvert)
										.getDeclaredMethod(methodReal, null);
								Object invoke = methodCall.invoke(Class
										.forName(classConvert).newInstance(),
										null);
							} else
							// Metodo sin parametros.
							if (attr.isEmpty()) {
								methodCall = Class.forName(classConvert)
										.getDeclaredMethod(methodReal, null);
								Object invoke = methodCall.invoke(Class
										.forName(classConvert).newInstance(),
										new Object[0]);
							} else {
								// Metodo con 'n' parametros.
								List<Object> getAttr = new ArrayList<Object>();
								// Obtenemos los getter de los atributos.
								for (Iterator iterator = attr.iterator(); iterator
										.hasNext();) {
									Object object = iterator.next();
									for (Iterator iterator2 = fromPd.iterator(); iterator2
											.hasNext();) {
										PropertyDescriptor propertyDescriptor = (PropertyDescriptor) iterator2
												.next();
										if (propertyDescriptor.getName()
												.equals(object.toString()
														.trim())) {
											getAttr.add(propertyDescriptor
													.getReadMethod().invoke(
															fromObj, null));
										}
									}
								}

								// Calculamos el total de atributos.
								Object[] args = new Object[getAttr.size()];
								for (int z = 0; z < getAttr.size(); z++) {
									Object object = getAttr.get(z);
									args[z] = object;
								}

								// Argumentos con su tamaño.
								Class[] cArg = new Class[getAttr.size()];

								// Definimos el tipo de cada atributo.
								Field[] declaredFields = fromClass
										.getDeclaredFields();
								int x = 0;
								for (int j = 0; j < declaredFields.length; j++) {
									Field fields = declaredFields[j];
									if (x < attr.size()) {
										if (errorAttr.contains(attr.get(x)
												.toString().trim())) {
											throw new IllegalArgumentException(
													"El atributo '"
															+ attr.get(x)
																	.toString()
																	.trim()
															+ "' no existe en la clase origen. "
															+ "No se ejecuta el metodo: "
															+ methodReal);

										}
										if (fields.getName().equals(
												attr.get(x).toString().trim())) {
											cArg[x] = fields.getType();
											x++;
										}
									}
								}

								// Definimos el metodo, los parametros y se
								// realiza la llamada a
								// dicho
								// metodo.
								methodCall = Class.forName(classConvert)
										.getDeclaredMethod(methodReal, cArg);
								Object invoke = methodCall.invoke(Class
										.forName(classConvert).newInstance(),
										args);

								// Recorremos las propiedades para llamar al
								// 'set'.
								for (PropertyDescriptor propertyDescriptorTemp : toPd) {
									if (propertyDescriptorTemp.getName()
											.equals(field.getName().toString()
													.trim())) {
										// Se establece al atributo el return de
										// la llamada al metodo.
										propertyDescriptorTemp.getWriteMethod()
												.invoke(instanceClass, invoke);
									}
								}

							}
						}
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new UnsupportedOperationException(
							"The number of attributes in the annotation is not well specified.");
				}
			}
		}
	}

	/**
	 * Lee las propiedades del bean de datos.
	 *
	 * @param toPd
	 *            Propiedades
	 * @param fromObj
	 *            Objeto origen.
	 * @param field
	 *            Campo.
	 * @param fromPd
	 * @throws ClassNotFoundException
	 *             Excepción lanzada por una clase no encontrada.
	 * @throws InvocationTargetException
	 *             Excepción lanzada por una invocación no encontrada.
	 * @throws IllegalArgumentException
	 *             Excepción lanzada por una argumento erroneo.
	 * @throws IllegalAccessException
	 *             Excepción lanzada por una acceso ilegal.
	 * @throws BeanConverterException
	 *             Excepción producida en la conversión.
	 */
	private static void readProperties(PropertyDescriptor[] toPd, Field field,
			Object fromObj, Object instanceClass,
			List<PropertyDescriptor> fromPd) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			ClassNotFoundException, BeanConverterException {

		// Recorremos las propiedades que exporta un Bean de Java a través de un
		// par de métodos de
		// acceso.
		for (PropertyDescriptor propertyDescriptorTo : toPd) {
//			System.out.println(propertyDescriptorTo.getDisplayName());
			if(propertyDescriptorTo.getDisplayName().equals(field.getName())){
				for (PropertyDescriptor pd : fromPd) {
					String name = mapConverter.get(pd.getName());
					if (pd.getName().equals(propertyDescriptorTo.getName())
							|| propertyDescriptorTo.getName().equals(name)) {
						convertMethodNative(propertyDescriptorTo, pd, fromObj,
								instanceClass);
					}
				}
			}
		}

	}

	/**
	 * Conversos para metodos GET/SET.
	 *
	 * @param propertyDescriptorTo
	 *            Propiedades.
	 * @param pd
	 *            Propiedades.
	 * @param fromObj
	 *            Objeto origen.
	 * @throws ClassNotFoundException
	 *             Excepción lanzada por una clase no encontrada.
	 * @throws InvocationTargetException
	 *             Excepción lanzada por una invocación no encontrada.
	 * @throws IllegalArgumentException
	 *             Excepción lanzada por una argumento erroneo.
	 * @throws IllegalAccessException
	 *             Excepción lanzada por una acceso ilegal.
	 * @throws BeanConverterException
	 */
	private static void convertMethodNative(
			PropertyDescriptor propertyDescriptorTo, PropertyDescriptor pd,
			Object fromObj, Object instanceClass)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, ClassNotFoundException,
			BeanConverterException {
		if (!pd.getDisplayName().equals("class")) {
			if (propertyDescriptorTo.getPropertyType().getName()
					.contains("java.lang")
					|| propertyDescriptorTo.getPropertyType().isPrimitive()) {
				// copia tipos simples
				if (propertyDescriptorTo.getWriteMethod() != null) {
					propertyDescriptorTo.getWriteMethod().invoke(instanceClass,
							pd.getReadMethod().invoke(fromObj, null));
				}
			} else if (propertyDescriptorTo.getPropertyType().getName()
					.contains("java.util.List")) {
				// Copia listas de objetos
				propertyDescriptorTo.getWriteMethod()
						.getGenericParameterTypes()[0].getClass();
				Type[] tipos = propertyDescriptorTo.getWriteMethod()
						.getGenericParameterTypes();
				String clase = tipos[0].toString()
						.replace("java.util.List<", "").replace(">", "");

				if (clase.contains("java.lang")) {
					// listas de objetos simples
					propertyDescriptorTo.getWriteMethod().invoke(instanceClass,
							pd.getReadMethod().invoke(fromObj, null));
				} else {
					// listas de objetos
					List<Object> lstFrom = (List) pd.getReadMethod().invoke(
							fromObj, null);
					List<Object> lstTo = new ArrayList<Object>();

					if (lstFrom != null) {
						for (Object o : lstFrom) {
							Object obj = copyProperties(o, Class.forName(clase));
							lstTo.add(obj);
						}
						propertyDescriptorTo.getWriteMethod().invoke(
								instanceClass, lstTo);
					}
				}
			} else if (propertyDescriptorTo.getPropertyType().getName()
					.contains("java.util.Collection")) {
				// Copia listas de objetos
				propertyDescriptorTo.getWriteMethod()
						.getGenericParameterTypes()[0].getClass();
				Type[] tipos = propertyDescriptorTo.getWriteMethod()
						.getGenericParameterTypes();
				String clase = tipos[0].toString()
						.replace("java.util.Collection<", "").replace(">", "");

				if (clase.contains("java.lang")) {
					// listas de objetos simples
					propertyDescriptorTo.getWriteMethod().invoke(instanceClass,
							pd.getReadMethod().invoke(fromObj, null));
				} else {
					// listas de objetos
					Collection<Object> lstFrom = (Collection) pd
							.getReadMethod().invoke(fromObj, null);
					Collection<Object> lstTo = new ArrayList<Object>();

					if (lstFrom != null) {
						for (Object o : lstFrom) {
							Object obj = copyProperties(o, Class.forName(clase));
							lstTo.add(obj);
						}
						propertyDescriptorTo.getWriteMethod().invoke(
								instanceClass, lstTo);
					}
				}
			} else {

				// Copia de objetos
				Object newInstance = copyProperties(
						pd.getReadMethod().invoke(fromObj, null),
						propertyDescriptorTo.getPropertyType());
				propertyDescriptorTo.getWriteMethod().invoke(instanceClass,
						newInstance);

			}
		}

	}

	/**
	 * Comprobamos que los atributos que no puedan ser nulos debinidos en la
	 * anotación de clase, no lo sean.
	 *
	 * @param souceAttrNotNull
	 *            Atributos a comprobar.
	 * @param fromObj
	 *            Clase origen.
	 * @param fromPd
	 * @throws InvocationTargetException
	 *             Excepción lanzada ante un error de invocación.
	 * @throws IllegalArgumentException
	 *             Excepción lanzada por un argumento ilegal.
	 * @throws IllegalAccessException
	 *             Excepción lanzada por un acceso ilegal.
	 */
	private static void checkedAnotationNull(String souceAttrNotNull,
			Object fromObj, List<PropertyDescriptor> fromPd)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {

		// Obtenemos los campos de la clase origen.
		Field[] fields = fromClass.getDeclaredFields();
		boolean control = true;

		// Recorremos los campos.
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals(souceAttrNotNull)) {
				control = false;
			}
		}

		// Atributo nulo.
		if (control) {
			throw new UnsupportedOperationException(
					"The operation can not be performed because the attribute '"
							+ souceAttrNotNull + "' is null.");

		}

		// Atributos.
		String[] atributos = null;

		// Si no es vacio y no contiene 'comas', es un unico atributo.
		if (!souceAttrNotNull.isEmpty()
				&& !souceAttrNotNull.contains(comaChart)) {
			// Unico atributo a controlar.
			atributos = new String[1];
			atributos[0] = souceAttrNotNull;
		} else {
			// 'n' numero de atributos a controlar.
			atributos = souceAttrNotNull
					.substring(0, souceAttrNotNull.length()).split(",");
		}

		// Recorremos los campos.
		for (int i = 0; i < fields.length; i++) {
			int x = 0;
			// Sacamos propiedades de cada campo.
			for (Iterator<PropertyDescriptor> iterator2 = fromPd.iterator(); iterator2
					.hasNext();) {
				PropertyDescriptor propertyDescriptor = iterator2.next();
				// Comprobamos si corresponde con alguno de los definidos en la
				// anotación.
				if (x < atributos.length
						&& propertyDescriptor.getName().equals(atributos[x])) {
					// Si el resultado de su 'GET' es nulo, lanzamos excepción.
					if (propertyDescriptor.getReadMethod()
							.invoke(fromObj, null) == null) {
						throw new UnsupportedOperationException(
								"The operation can not be performed because the attribute '"
										+ atributos[x] + "' is null.");
					} else {
						// Incrementamos el valor para seguir buscando.
						x++;
					}
				}
			}
		}

		LOGGER.info("Checked correctly attributes.");

	}

}
