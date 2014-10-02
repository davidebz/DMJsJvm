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

package bz.davide.dmjsjvm.system.org.w3c.dom;

import bz.davide.dmjsjvm.NativeJavascriptCode;

public class Attr
{
   @NativeJavascriptCode(src = "var obj = Class_forName('java/lang/String')['newInstance()Ljava/lang/Object;']();\n obj.value = this.native.name;\n return obj;")
   public native String getName();

   @NativeJavascriptCode(src = "var obj = Class_forName('java/lang/String')['newInstance()Ljava/lang/Object;']();\n obj.value = this.native.value;\n return obj;")
   public native String getValue();
}
