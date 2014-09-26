/*
DMJsJvm - Small and smart javascript jvm that run java bytecode

Copyright (C) 2014 Davide Montesin <d@vide.bz> - Bolzano/Bozen - Italy

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>
*/

package bz.davide.dmjsjvm;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.Method;

public class Bytecode2JsConverter
{
   public static void convert(Class mainClass, Writer out)
                                                          throws IOException,
                                                          ClassFormatException,
                                                          ClassNotFoundException
   {
      ArrayList<String> convertedClasses = new ArrayList<>();
      ArrayList<String> toConvertClasses = new ArrayList<String>()
      {
         @Override
         public boolean add(String e)
         {
            if (e.startsWith("bz/davide/dmjsjvm/system/"))
            {
               throw new IllegalStateException("Wrong package");
            }
            return super.add(e);
         }
      };
      ArrayList<String> staticInitClasses = new ArrayList<>();

      convertedClasses.add("java/lang/Class");
      convertedClasses.add("java/lang/Object");

      String mainClassBinaryName = convertDotNameToBinaryName(mainClass.getName());
      toConvertClasses.add(mainClassBinaryName);

      PrintWriter print = new PrintWriter(out);

      while (toConvertClasses.size() > 0)
      {
         String binaryName = toConvertClasses.remove(0);
         if (!convertedClasses.contains(binaryName))
         {
            convertedClasses.add(binaryName);
            convertClassBytecode2Js(binaryName, print, toConvertClasses, staticInitClasses);
         }
      }

      for (String name : staticInitClasses)
      {
         print.println("   Class_forName('" + name + "').static_methods['<clinit>()V'].code()");
      }

      print.close();
   }

   static String convertDotNameToBinaryName(String dotName)
   {
      String result = dotName.replaceAll("\\.", "/");
      return result;
   }

   static void convertClassBytecode2Js(String binaryName,
                                       PrintWriter print,
                                       ArrayList<String> toConvertClasses,
                                       ArrayList<String> staticInitClasses)
                                                                           throws ClassFormatException,
                                                                           IOException,
                                                                           ClassNotFoundException
   {
      String adjustSystemClassName = adjustSystemClassName(binaryName);

      String fileName = adjustSystemClassName + ".class";
      InputStream input = Bytecode2JsConverter.class.getClassLoader().getResourceAsStream(fileName);
      if (input == null)
      {
         throw new ClassNotFoundException(adjustSystemClassName);
      }
      JavaClass classBytecode = new ClassParser(input, "???").parse();
      input.close();
      print.println("    // ======================= " + binaryName + " =======================");
      print.println();
      print.println("    classes_by_name['" + binaryName + "'] =");
      print.println("    {");
      print.println("       name : '" + binaryName + "',");
      String superClassName = ((ConstantClass) classBytecode.getConstantPool().getConstant(classBytecode.getSuperclassNameIndex())).getBytes(classBytecode.getConstantPool());
      superClassName = Bytecode.reverseAdjustSystemClassName(superClassName);
      print.println("       superName : '" + superClassName + "',");
      toConvertClasses.add(superClassName);
      print.println("       __className : 'java/lang/Class',");

      JavaClass[] interfaces = classBytecode.getInterfaces();
      print.print("       interfaces : [ ");
      for (int i = 0; i < interfaces.length; i++)
      {
         JavaClass interf = interfaces[i];
         String interfaceName = ((ConstantClass) classBytecode.getConstantPool().getConstant(interf.getClassNameIndex())).getBytes(classBytecode.getConstantPool());
         print.print(interfaceName);
         if (i < interfaces.length - 1)
         {
            print.print(", ");
         }
      }
      print.println("],");

      convertFieldBytecode2Js(classBytecode, print, toConvertClasses);
      convertMethodBytecode2Js(binaryName, classBytecode, print, toConvertClasses, staticInitClasses);

      print.println("    }");
      print.println("    installObjectMethods(java_lang_Class_class, classes_by_name['" + binaryName + "'])");
   }

   static void convertFieldBytecode2Js(JavaClass classBytecode,
                                       PrintWriter print,
                                       ArrayList<String> toConvertClasses)
                                                                          throws ClassFormatException,
                                                                          IOException,
                                                                          ClassNotFoundException
   {
      Field[] fields = classBytecode.getFields();
      ArrayList<Field> notStatic = new ArrayList<>();
      ArrayList<Field> isStatic = new ArrayList<>();
      for (int i = 0; i < fields.length; i++)
      {
         Field field = fields[i];

         if (field.isStatic())
         {
            isStatic.add(field);
         }
         else
         {
            notStatic.add(field);
         }

      }
      print.println("       fields : {");
      for (int i = 0; i < notStatic.size(); i++)
      {
         Field field = notStatic.get(i);
         String signature = field.getGenericSignature();
         if (signature == null)
         {
            signature = field.getSignature();
         }
         print.println("         '" + field.getName() + "' : {signature: '" + signature + "', isStatic: false}");
         if (i < notStatic.size() - 1)
         {
            print.println(",");
         }
         // TODO check array too
         String typeSignature = field.getType().getSignature();
         if (typeSignature.startsWith("L"))
         {
            typeSignature = typeSignature.replaceAll("bz/davide/dmjsjvm/system/", "");
            typeSignature = typeSignature.substring(1);
            typeSignature = typeSignature.substring(0, typeSignature.length() - 1);
            toConvertClasses.add(typeSignature);
         }
      }
      print.println("       },");
      print.println("       static_fields : {");
      for (int i = 0; i < isStatic.size(); i++)
      {
         Field field = isStatic.get(i);
         print.println("         '" + field.getName() + "' : null");
      }
      print.println("       },");
   }

   static void convertMethodBytecode2Js(String binaryName,
                                        JavaClass classBytecode,
                                        PrintWriter print,
                                        ArrayList<String> toConvertClasses,
                                        ArrayList<String> staticInitClasses)
                                                                            throws ClassFormatException,
                                                                            IOException,
                                                                            ClassNotFoundException
   {
      ArrayList<Method> notStatic = new ArrayList<>();
      ArrayList<Method> isStatic = new ArrayList<>();
      for (Method method : classBytecode.getMethods())
      {
         if (method.isStatic())
         {
            isStatic.add(method);
         }
         else
         {
            notStatic.add(method);
         }
      }
      print.println("       methods : {");
      for (int i = 0; i < notStatic.size(); i++)
      {
         Method method = notStatic.get(i);
         String name = method.getName();
         String signature = method.getGenericSignature();
         if (signature == null)
         {
            signature = method.getSignature();
         }
         signature = signature.replaceAll("bz/davide/dmjsjvm/system/", "");
         String name_signature = name + signature;
         print.println("         '"
                       + name_signature
                       + "' : {isStatic: "
                       + method.isStatic()
                       + ", code : function() {");

         String javascriptNativeCode = null;
         AnnotationEntry[] annotationEntries = method.getAnnotationEntries();
         if (annotationEntries.length > 0)
         {
            for (AnnotationEntry annotationEntry : annotationEntries)
            {
               String type = annotationEntry.getAnnotationType();
               if (type.equals("Lbz/davide/dmjsjvm/NativeJavascriptCode;"))
               {
                  javascriptNativeCode = annotationEntry.getElementValuePairs()[0].getValue().stringifyValue();
               }
            }
         }

         if (javascriptNativeCode == null)
         {
            convertMethodBodyBytecode2Js(classBytecode, method, print, toConvertClasses);
         }
         else
         {
            print.println(javascriptNativeCode);
         }

         String returnType = method.getReturnType().getSignature();
         if (returnType.startsWith("L") && returnType.endsWith(";"))
         {
            String returnClass = returnType.substring(1).substring(0, returnType.length() - 2);
            toConvertClasses.add(returnClass);
         }

         if (i < notStatic.size() - 1)
         {
            print.println("         }},");
         }
         else
         {
            print.println("         }}");
         }

      }
      print.println("       },");
      print.println("       static_methods : {");
      for (int i = 0; i < isStatic.size(); i++)
      {
         Method method = isStatic.get(i);
         String name = method.getName();
         String signature = method.getGenericSignature();
         if (signature == null)
         {
            signature = method.getSignature();
         }
         String name_signature = name + signature;
         if (name_signature.equals("<clinit>()V"))
         {
            if (staticInitClasses.contains(binaryName))
            {
               throw new IllegalStateException();
            }
            staticInitClasses.add(binaryName);
         }
         print.println("         '"
                       + name_signature
                       + "' : { isStatic: "
                       + method.isStatic()
                       + ", code : function() {");

         String javascriptNativeCode = null;
         AnnotationEntry[] annotationEntries = method.getAnnotationEntries();
         if (annotationEntries.length > 0)
         {
            for (AnnotationEntry annotationEntry : annotationEntries)
            {
               String type = annotationEntry.getAnnotationType();
               if (type.equals("Lbz/davide/dmjsjvm/NativeJavascriptCode;"))
               {
                  javascriptNativeCode = annotationEntry.getElementValuePairs()[0].getValue().stringifyValue();
               }
            }
         }

         if (javascriptNativeCode == null)
         {
            convertMethodBodyBytecode2Js(classBytecode, method, print, toConvertClasses);
         }
         else
         {
            print.println(javascriptNativeCode);
         }

         String returnType = method.getReturnType().getSignature();
         if (returnType.startsWith("L") && returnType.endsWith(";"))
         {
            String returnClass = returnType.substring(1).substring(0, returnType.length() - 2);
            toConvertClasses.add(returnClass);
         }

         if (i < isStatic.size() - 1)
         {
            print.println("         }},");
         }
         else
         {
            print.println("         }}");
         }

      }
      print.println("       }");
   }

   static void convertMethodBodyBytecode2Js(JavaClass classBytecode,
                                            Method method,
                                            PrintWriter print,
                                            ArrayList<String> toConvertClasses)
   {
      boolean isStatic = method.isStatic();
      boolean isAbstract = method.isAbstract();
      boolean isNative = method.isNative();

      String signature = method.getGenericSignature();
      if (signature == null)
      {
         signature = method.getSignature();
      }
      int paramCount = countParametersFromSignature(signature);
      if (!isAbstract && !isNative)
      {
         ArrayList<FixedLocalVariable> methodParametersVariablesArr = new ArrayList<>();
         if (!method.getName().equals("<clinit>"))
         {
            if (method.getLocalVariableTable() != null
                && method.getLocalVariableTable().getLocalVariableTable() != null)
            {
               for (LocalVariable localVariable : method.getLocalVariableTable().getLocalVariableTable())
               {
                  methodParametersVariablesArr.add(new FixedLocalVariable(localVariable.getName()));
               }
            }
         }
         print.println("                var local_vars = {};");

         if (!isStatic)
         {
            print.println("                local_vars.this = this");
            paramCount++; // "this" is the first parameter in this case
         }
         // TODO Odd case. By anonymous synthetic constructor is one parameter missing! What is the reason?
         // I make the ArrayList at least length as paramCount
         if (methodParametersVariablesArr.size() < paramCount)
         {
            for (int i = methodParametersVariablesArr.size(); i < paramCount; i++)
            {
               methodParametersVariablesArr.add(new FixedLocalVariable("noname_" + i));
            }
         }
         FixedLocalVariable[] methodParametersVariables = methodParametersVariablesArr.toArray(new FixedLocalVariable[0]);
         for (int i = isStatic ? 0 : 1; i < methodParametersVariables.length; i++)
         {
            // TODO What if the name are not unique ? I.e. more catch with the same name ...
            print.print("                local_vars." + methodParametersVariables[i].getName());
            if (i < paramCount)
            {
               print.println(" = " + "arguments[" + (isStatic ? i : i - 1) + "]");
            }
            else
            {
               print.println();
            }
         }
         print.println();
         print.println("                var PC = 0;");
         print.println("                var operand_stack = [];");
         print.println("                var exception_table = [];");
         print.println("                while (true)");
         print.println("                {");
         print.println("                   try");
         print.println("                   {");
         print.println("                     switch (PC)");
         print.println("                     {");
         Bytecode.convert(print,
                          classBytecode.getConstantPool(),
                          methodParametersVariables,
                          method.getCode().getCode(),
                          toConvertClasses);
         print.println("                     }");
         print.println("                   }");
         print.println("                   catch (exception)");
         print.println("                   {");
         print.println("                      throw exception");
         print.println("                   }");
         print.println("                }");
         print.println();
      }
   }

   private static String adjustSystemClassName(String binaryName)
   {
      if (binaryName.startsWith("java/") || binaryName.startsWith("javax/"))
      {
         return "bz/davide/dmjsjvm/system/" + binaryName;
      }
      return binaryName;
   }

   static int countParametersFromSignature(String signature)
   {
      if (signature.charAt(0) != '(')
      {
         throw new IllegalStateException("Invalid signature: " + signature);
      }
      int count = 0;
      for (int i = 1; i < signature.length(); i++)
      {
         if (signature.charAt(i) == ')')
         {
            return count;
         }
         count++;
         switch (signature.charAt(i))
         {
            case 'L':
               while (signature.charAt(i) != ';')
               {
                  i++;
               }
            break;
            case '[':
               while (signature.charAt(i) == '[')
               {
                  i++;
               }
               if (signature.charAt(i) == 'L')
               {
                  while (signature.charAt(i) != ';')
                  {
                     i++;
                  }
               }
            break;
         }

      }
      throw new IllegalStateException("Signature not end with ): " + signature);
   }

}
