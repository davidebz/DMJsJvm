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

package bz.davide.dmjsjvm.xml;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import bz.davide.dmjsjvm.NativeJavascriptCode;

public class DOMParser
{
   @NativeJavascriptCode(src = "var parser = new DOMParser();\n var xdoc = parser.parseFromString(arguments[0].value,'text/xml');\n var jdoc = Class_forName('org/w3c/dom/Document')['newInstance()Ljava/lang/Object;']();\n jdoc.native = xdoc;\n return jdoc;  ")
   public static Document parse(String xml)
                                           throws ParserConfigurationException,
                                           UnsupportedEncodingException,
                                           SAXException,
                                           IOException
   {
      DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
      return doc;
   }

}
