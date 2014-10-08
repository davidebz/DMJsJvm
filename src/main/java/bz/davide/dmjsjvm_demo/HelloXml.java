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

package bz.davide.dmjsjvm_demo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import bz.davide.dmjsjvm.xml.DOMParser;

public class HelloXml
{
   public static void main(String[] args)
                                         throws UnsupportedEncodingException,
                                         ParserConfigurationException,
                                         SAXException,
                                         IOException
   {
      String xml = "<doc date=\"2014-10-02\">DMJsJvm is <b>great</b>!</doc>";
      Document doc = DOMParser.parse(xml);
      Element docElement = doc.getDocumentElement();
      System.out.println(docElement.getTagName());
      String dateAttribute = docElement.getAttribute("date");
      System.out.println(dateAttribute);
      NodeList childs = docElement.getChildNodes();
      Text text1 = (Text) childs.item(0);
      System.out.println(text1.getNodeValue());
   }
}