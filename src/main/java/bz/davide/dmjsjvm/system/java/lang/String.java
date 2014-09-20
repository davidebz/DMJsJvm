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

package bz.davide.dmjsjvm.system.java.lang;

import bz.davide.dmjsjvm.NativeJavascriptCode;

public class String
{
   public String()
   {
      this.initNativeString();
   }

   @NativeJavascriptCode(src = "this.value = ''")
   public native void initNativeString();

   @NativeJavascriptCode(src = "var obj = Class_forName('java/lang/String')['newInstance()Ljava/lang/Object;'](); obj.value = this.value.toLowerCase(); return obj")
   public native java.lang.String toLowerCase();

   @Override
   @NativeJavascriptCode(src = "var val = this.value === arguments[0].value ; return val")
   public native boolean equals(java.lang.Object str2);

   @NativeJavascriptCode(src = "var val = this.value.toLowerCase() === arguments[0].value.toLowerCase() ; return val")
   public native boolean equalsIgnoreCase(java.lang.String str2);
}
