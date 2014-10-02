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

import org.w3c.dom.Element;
import bz.davide.dmjsjvm.NativeJavascriptCode;

public class Document extends Node
{
   @NativeJavascriptCode(src = "var je = Class_forName('org/w3c/dom/Element')['newInstance()Ljava/lang/Object;'](); je.native = this.native.documentElement;\n return je;")
   public native Element getDocumentElement();
}
