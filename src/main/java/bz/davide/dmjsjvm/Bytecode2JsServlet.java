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
import java.io.StringWriter;
import java.util.Scanner;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Bytecode2JsServlet extends HttpServlet
{
   private static final String SERVLET_DYNAMIC_CODE = "/*servlet-dynamic-code*/";
   String                      mainClassName;

   @Override
   public void init(ServletConfig config) throws ServletException
   {
      super.init(config);
      this.mainClassName = config.getInitParameter("mainClass");
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      try
      {
         InputStream jsTemplateStream = this.getClass().getClassLoader().getResourceAsStream("bz/davide/dmjsjvm/bytecode2js-template.js");
         String jsTemplate = new Scanner(jsTemplateStream, "UTF-8").useDelimiter("\\A").next();
         jsTemplateStream.close();

         Class mainClass = Class.forName(this.mainClassName);
         StringWriter stringWriter = new StringWriter();
         Bytecode2JsConverter.convert(mainClass, stringWriter);
         resp.setHeader("Content-Type", "text/javascript");
         String dynamicjs = stringWriter.getBuffer().toString();

         String jsCode = jsTemplate.replace(SERVLET_DYNAMIC_CODE, dynamicjs);
         System.out.println(jsCode);
         resp.getWriter().write(jsCode);
      }
      catch (Exception exxx)
      {
         exxx.printStackTrace();
         resp.setHeader("Content-Type", "text/javascript");
         resp.getWriter().write("alert('Exception during converting bytecode to javascript. See log')");
      }
   }
}
