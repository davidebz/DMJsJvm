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

import java.io.PrintWriter;
import java.util.ArrayList;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantFieldref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;

public class Bytecode
{
   static void convert(PrintWriter print,
                       ConstantPool constantPool,
                       FixedLocalVariable[] methodParametersVariables,
                       byte[] code,
                       ArrayList<String> toConvertClasses)
   {

      int index1;
      int index2;
      int index;
      byte signedByte;
      short signed32;
      ConstantClass constantClass;

      for (int i = 0; i < code.length; i++)
      {
         int bytecode = code[i] & 0xFF;
         print.print("                        case "
                     + String.format("%d", i)
                     + ": PC = "
                     + String.format("%d", i)
                     + "; /* 0x"
                     + String.format("%02X", bytecode)
                     + " ");
         ConstantFieldref constantFieldref;
         ConstantNameAndType constantNameAndType;
         ConstantUtf8 constantUtf8;
         ConstantFieldref fieldName;
         int nameAndTypeIndex;
         ConstantNameAndType nameAndType;
         String name;
         switch (bytecode)
         {
            case 0x01: // aconst_null
               print.println(" aconst_null */ operand_stack.push(null)");
            break;
            case 0x03: // iconst_0
               print.println(" iconst_0    */ operand_stack.push(0)");
            break;
            case 0x04: // iconst_1
               print.println(" iconst_1    */ operand_stack.push(1)");
            break;
            case 0x06: // iconst_3
               print.println(" iconst_3    */ operand_stack.push(3)");
            break;
            case 0x10: // bipush
               i++;
               index1 = code[i] & 0xff;
               print.println(" iconst_4    */ operand_stack.push(" + index1 + ")");
            break;
            case 0x12: // ldc
               i++;
               index1 = code[i] & 0xff;
               ConstantString constx = (ConstantString) constantPool.getConstant(index1);
               // TODO escape javascript string
               print.println(" ldc         */ var text = '" + constx.getBytes(constantPool) + "'");
               print.println("                var javaLangString = Class_forName('java/lang/String')['newInstance()Ljava/lang/Object;']()");
               print.println("                javaLangString.value = text");
               print.println("                operand_stack.push(javaLangString)");
               toConvertClasses.add("java/lang/String");
            break;
            case 0x15: // iload
               i++;
               index1 = code[i] & 0xff;
               print.println(" iload # "
                             + index1
                             + " */ operand_stack.push(local_vars."
                             + methodParametersVariables[index1].getName()
                             + ")");
            break;
            case 0x19: // aload
               i++;
               index1 = code[i] & 0xff;
               print.println(" aload # "
                             + index1
                             + " */ operand_stack.push(local_vars."
                             + methodParametersVariables[index1].getName()
                             + ")");
            break;
            case 0x1B: // iload_1
               print.println(" iload_1    */ operand_stack.push(local_vars."
                             + methodParametersVariables[1].getName()
                             + ")");
            break;
            case 0x1D: // iload_3
               print.println(" iload_3    */ operand_stack.push(local_vars."
                             + methodParametersVariables[3].getName()
                             + ")");
            break;
            case 0x2A: // aload_0
               print.println(" aload_0     */ operand_stack.push(local_vars."
                             + methodParametersVariables[0].getName()
                             + ")");
            break;
            case 0x2B: // aload_1
               print.println(" aload_1     */ operand_stack.push(local_vars."
                             + methodParametersVariables[1].getName()
                             + ")");
            break;
            case 0x2C: // aload_2
               print.println(" aload_2     */ operand_stack.push(local_vars."
                             + methodParametersVariables[2].getName()
                             + ")");
            break;
            case 0x2D: // aload_3
               print.println(" aload_3     */ operand_stack.push(local_vars."
                             + methodParametersVariables[3].getName()
                             + ")");
            break;
            case 0x36: // istore
               i++;
               index1 = code[i] & 0xff;
               print.println(" istore # "
                             + index1
                             + " */ local_vars."
                             + methodParametersVariables[index1].getName()
                             + " = operand_stack.pop()");
            break;
            case 0x3A: // astore
               i++;
               index1 = code[i] & 0xff;
               print.println(" astore # "
                             + index1
                             + " */ local_vars."
                             + methodParametersVariables[index1].getName()
                             + " = operand_stack.pop()");
            break;
            case 0x3C: // istore_1
               print.println(" istore_1    */ local_vars."
                             + methodParametersVariables[1].getName()
                             + " = operand_stack.pop()");
            break;

            case 0x3e: // istore_3
               print.println(" istore_3    */ local_vars."
                             + methodParametersVariables[3].getName()
                             + " = operand_stack.pop()");
            break;

            case 0x4C: // astore_1
               print.println(" astore_1    */ local_vars."
                             + methodParametersVariables[1].getName()
                             + " = operand_stack.pop()");
            break;
            case 0x4E: // astore_3
               print.println(" astore_3    */ local_vars."
                             + methodParametersVariables[3].getName()
                             + " = operand_stack.pop()");
            break;

            case 0x4D: // astore_2
               print.println(" astore_2    */ local_vars."
                             + methodParametersVariables[2].getName()
                             + " = operand_stack.pop()");
            break;
            case 0x57: // pop
               print.println(" pop         */ var discard = operand_stack.pop();");
            break;
            case 0x59: // dup
               print.println(" dup         */ var val = operand_stack[operand_stack.length-1]; operand_stack.push(val);");
            break;
            case 0x84: // iinc
               i++;
               index1 = code[i] & 0xff;
               i++;
               signedByte = code[i];

               print.println(" iinc         */ local_vars."
                             + methodParametersVariables[index1].getName()
                             + " += "
                             + signedByte);
            break;
            case 0x99: // ifeq
               i++;
               index1 = code[i] & 0xff;
               i++;
               index2 = code[i] & 0xff;
               signed32 = (short) (index1 << 8 | index2);
               print.println(" ifeq */ var val = operand_stack.pop(); if (val == 0) { PC = "
                             + (i - 2 + signed32)
                             + "; continue;}");
            break;
            case 0xa2: // if_icmpge
               i++;
               index1 = code[i] & 0xff;
               i++;
               index2 = code[i] & 0xff;
               signed32 = (short) (index1 << 8 | index2);
               print.println(" if_icmpge */ var val2 = operand_stack.pop(); var val1 = operand_stack.pop(); if (val1 >= val2) { PC = "
                             + (i - 2 + signed32)
                             + "; continue;}");
            break;

            case 0xa1: // if_icmplt
               i++;
               index1 = code[i] & 0xff;
               i++;
               index2 = code[i] & 0xff;
               signed32 = (short) (index1 << 8 | index2);
               print.println(" if_icmplt */ var val2 = operand_stack.pop(); var val1 = operand_stack.pop(); if (val1 < val2) { PC = "
                             + (i - 2 + signed32)
                             + "; continue;}");
            break;

            case 0xa7: // goto
               i++;
               index1 = code[i] & 0xff;
               i++;
               index2 = code[i] & 0xff;
               signed32 = (short) (index1 << 8 | index2);
               print.println(" goto        */ PC = " + (i - 2 + signed32) + "; continue;");
            break;

            case 0xB0: // areturn
               print.println(" areturn     */ return operand_stack.pop() ");
            break;
            case 0xB1: // return
               print.println(" return      */ return ");
            break;
            case 0xB2: // getstatic

               i++;
               index1 = code[i] & 0xff;
               i++;
               index2 = code[i] & 0xff;
               index = index1 << 8 | index2;
               print.println("  */");

               constantFieldref = (ConstantFieldref) constantPool.getConstant(index);
               constantNameAndType = (ConstantNameAndType) constantPool.getConstant(constantFieldref.getNameAndTypeIndex());
               constantUtf8 = (ConstantUtf8) constantPool.getConstant(constantNameAndType.getNameIndex());
               String targetClass = ((ConstantUtf8) constantPool.getConstant(((ConstantClass) constantPool.getConstant(constantFieldref.getClassIndex())).getNameIndex())).getBytes();
               print.println("                               operand_stack.push(Class_forName('"
                             + targetClass
                             + "').static_fields['"
                             + constantUtf8.getBytes()
                             + "'])");

               toConvertClasses.add(targetClass);
            break;
            case 0xB3: // putstatic
               i++;
               index1 = code[i] & 0xff;
               i++;
               index2 = code[i] & 0xff;
               index = index1 << 8 | index2;
               print.println("  */");

               constantFieldref = (ConstantFieldref) constantPool.getConstant(index);
               constantNameAndType = (ConstantNameAndType) constantPool.getConstant(constantFieldref.getNameAndTypeIndex());
               constantUtf8 = (ConstantUtf8) constantPool.getConstant(constantNameAndType.getNameIndex());
               targetClass = ((ConstantUtf8) constantPool.getConstant(((ConstantClass) constantPool.getConstant(constantFieldref.getClassIndex())).getNameIndex())).getBytes();
               targetClass = reverseAdjustSystemClassName(targetClass);
               print.println("                               var obj = Class_forName('"
                             + targetClass
                             + "'); obj.static_fields['"
                             + constantUtf8.getBytes()
                             + "'] = operand_stack.pop()");

               toConvertClasses.add(targetClass);
            break;

            case 0xB4: // getfield
               i++;
               index1 = code[i] & 0xff;
               i++;
               index2 = code[i] & 0xff;
               index = index1 << 8 | index2;
               fieldName = (ConstantFieldref) constantPool.getConstant(index);
               nameAndTypeIndex = fieldName.getNameAndTypeIndex();
               nameAndType = (ConstantNameAndType) constantPool.getConstant(nameAndTypeIndex);
               name = nameAndType.getName(constantPool);
               print.println("getfield  */");
               print.println("   var obj = operand_stack.pop()");
               print.println("   var val = obj['" + name + "'];");
               print.println("   operand_stack.push(val)");
            break;

            case 0xB5: // putfield
               i++;
               index1 = code[i] & 0xff;
               i++;
               index2 = code[i] & 0xff;
               index = index1 << 8 | index2;
               fieldName = (ConstantFieldref) constantPool.getConstant(index);
               nameAndTypeIndex = fieldName.getNameAndTypeIndex();
               nameAndType = (ConstantNameAndType) constantPool.getConstant(nameAndTypeIndex);
               name = nameAndType.getName(constantPool);
               print.println("putfield  */");
               print.println("   var val = operand_stack.pop()");
               print.println("   var obj = operand_stack.pop()");
               print.println("   obj['" + name + "'] = val;");
            break;

            case 0xB6: // invokeVirtual
            case 0xB7: // invokeSpecial
            case 0xB8: // invokeStatic
            case 0xB9: // invokeInterface
               MethodInvokationData methodInvokationData = MethodInvokationData.parse(code, i, constantPool);
               methodInvokationData.ofClass = reverseAdjustSystemClassName(methodInvokationData.ofClass);
               i += 2;
               switch (bytecode)
               {
                  case 0xB7: // invokeSpecial
                     print.print("invokeSpecial");
                  break;
                  case 0xB8: // invokeStatic
                     print.print("invokeStatic");
                  break;
                  case 0xB9: // invokeInterface
                     print.print("invokeInterface");
                     i += 2; // remove count , 0
                  break;
               }
               print.println("  */");
               for (int p = methodInvokationData.parametersCount; p >= 1; p--)
               {
                  print.println("                                              var param_"
                                + p
                                + " =  operand_stack.pop()");
               }
               if (bytecode == 0xB8)
               {
                  print.print("                                              var result = Class_forName('"
                              + methodInvokationData.ofClass
                              + "').static_methods['"
                              + methodInvokationData.name
                              + methodInvokationData.signature
                              + "'].code(");
               }
               else
               {
                  print.println("                                              var obj = operand_stack.pop()");
                  print.print("                                              var result = obj['"
                              + (bytecode == 0xB7 ? methodInvokationData.ofClass + "." : "")
                              + methodInvokationData.name
                              + methodInvokationData.signature
                              + "'](");
               }
               for (int p = 1; p <= methodInvokationData.parametersCount; p++)
               {
                  print.print("param_" + p + (p < methodInvokationData.parametersCount ? " ," : ""));
               }
               print.println(")");
               if (!methodInvokationData.signature.endsWith(")V"))
               {
                  print.println("                                              operand_stack.push(result)");
               }
               toConvertClasses.add(methodInvokationData.ofClass);
            break;
            case 0xBB: // new
               i++;
               index1 = code[i] & 0xff;
               i++;
               index2 = code[i] & 0xff;
               index = index1 << 8 | index2;
               constantClass = (ConstantClass) constantPool.getConstant(index);
               String newInstanceName = constantClass.getBytes(constantPool);
               newInstanceName = reverseAdjustSystemClassName(newInstanceName);
               print.println(" new         */ var obj = {}; newRawInstance(Class_forName('"
                             + newInstanceName
                             + "'), obj); operand_stack.push(obj); ");
            break;
            case 0xbf: // athrow
               // TODO implementes throw
               print.println(" athrow      */ throw operand_stack.pop()");
            break;
            case 0xc0: // checkcast
               i++;
               index1 = code[i] & 0xff;
               i++;
               index2 = code[i] & 0xff;
               signed32 = (short) (index1 << 8 | index2);
            // TODO implement check with exception
            break;
            case 0xC1: // instanceof
               i++;
               index1 = code[i] & 0xff;
               i++;
               index2 = code[i] & 0xff;
               index = index1 << 8 | index2;
               constantClass = (ConstantClass) constantPool.getConstant(index);
               String instanceofName = constantClass.getBytes(constantPool);
               print.println(" instanceof   */      var obj = operand_stack.pop()");
               print.println("                      var result = Class_instanceOf(obj,'" + instanceofName + "')");
               print.println("           operand_stack.push(result)");
            break;

            default:
               throw new IllegalStateException(String.format("Unsupported bytecode: %02X", bytecode));
         }
      }
   }

   static String reverseAdjustSystemClassName(String binaryName)
   {
      if (binaryName.startsWith("bz/davide/dmjsjvm/system/"))
      {
         binaryName = binaryName.substring("bz/davide/dmjsjvm/system/".length());
      }
      return binaryName;
   }

}
