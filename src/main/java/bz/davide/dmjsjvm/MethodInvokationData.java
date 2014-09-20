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

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;

public class MethodInvokationData
{
   String ofClass;
   String name;
   String signature;
   int    parametersCount;

   static MethodInvokationData parse(byte[] code, int i, ConstantPool constantPool)
   {
      int index1;
      int index2;
      int index;
      MethodInvokationData data = new MethodInvokationData();
      i++;
      index1 = code[i] & 0xff;
      i++;
      index2 = code[i] & 0xff;
      index = index1 << 8 | index2;

      Constant constant = constantPool.getConstant(index);
      ConstantClass targetClassNameConstant;

      ConstantNameAndType constantNameAndType;

      if (constant instanceof ConstantInterfaceMethodref)
      {
         ConstantInterfaceMethodref constantInterfaceMethodref = (ConstantInterfaceMethodref) constant;
         constantNameAndType = (ConstantNameAndType) constantPool.getConstant(constantInterfaceMethodref.getNameAndTypeIndex());
         targetClassNameConstant = (ConstantClass) constantPool.getConstant(constantInterfaceMethodref.getClassIndex());
      }
      else
      {
         ConstantMethodref constantMethodref = (ConstantMethodref) constant;
         constantNameAndType = (ConstantNameAndType) constantPool.getConstant(constantMethodref.getNameAndTypeIndex());
         targetClassNameConstant = (ConstantClass) constantPool.getConstant(constantMethodref.getClassIndex());
      }

      ConstantUtf8 constantUtf8 = (ConstantUtf8) constantPool.getConstant(constantNameAndType.getNameIndex());

      data.ofClass = ((ConstantUtf8) constantPool.getConstant(targetClassNameConstant.getNameIndex())).getBytes();

      data.name = constantUtf8.getBytes();
      data.signature = constantNameAndType.getSignature(constantPool);

      data.parametersCount = Bytecode2JsConverter.countParametersFromSignature(data.signature);

      return data;
   }

}
