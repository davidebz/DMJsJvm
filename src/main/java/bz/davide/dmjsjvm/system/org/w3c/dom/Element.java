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

import org.w3c.dom.NamedNodeMap;
import bz.davide.dmjsjvm.NativeJavascriptCode;

public class Element extends Node
{
   @NativeJavascriptCode(src = "var jstring = Class_forName('java/lang/String')['newInstance()Ljava/lang/Object;']();\n jstring.value = this.native.tagName;\n return jstring;")
   public native String getTagName();

   @NativeJavascriptCode(src = "var obj = Class_forName('org/w3c/dom/NodeList')['newInstance()Ljava/lang/Object;']();\n obj.native = this.native.childNodes;\n return obj;")
   public native org.w3c.dom.NodeList getChildNodes();

   @NativeJavascriptCode(src = "var obj = Class_forName('org/w3c/dom/NamedNodeMap')['newInstance()Ljava/lang/Object;']();\n obj.native = this.native.attributes;\n return obj;")
   public native NamedNodeMap getAttributes();

   @NativeJavascriptCode(src = "var attr = this.native.getAttribute(arguments[0].value); var obj = Class_forName('java/lang/String')['newInstance()Ljava/lang/Object;']();\n obj.value = attr; return obj;")
   public native String getAttribute(String attributeName);
}
