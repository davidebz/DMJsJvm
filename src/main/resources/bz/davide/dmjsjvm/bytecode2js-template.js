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

bz_davide_JVM = new function()
{
	
	var classes_by_name = {}
	
	var Class_forName = function(className)
	{
		var clazz = classes_by_name[className];
		// TODO throw ClassNotFoundException
		return clazz
	}
	
	this.runMainMethodOf = function(className)
	{
		var clazz = Class_forName(className)
		var mainMethod = clazz.static_methods['main([Ljava/lang/String;)V']
		mainMethod.code()
	}

	var java_lang_Object_class = classes_by_name['java/lang/Object'] = {
			
		name :  'java/lang/Object',
		superName : null,
		fields : {},
	
		__className : 'java/lang/Class',
	
		methods : {
			'<init>()V': { code: function(_this) {}},
			'getClass()Ljava/lang/Class;': { code: function () {
				 return Class_forName(this.__className)
			}}
		}
	}

	var java_lang_Class_class = classes_by_name['java/lang/Class'] = {
		
		name : 'java/lang/Class',
		superName :  'java/lang/Object',
		fields : {},
		
		__className : 'java/lang/Class',
			
		methods : {
		   'newInstance()Ljava/lang/Object;' : { code: function() {  
			   var obj = {}
			   newRawInstance(this, obj)
			   // TODO check if the default constructor exists?!
			   obj['<init>()V']()
			   return obj;
		   }},
		   'getName()Ljava/lang/String;' : { code: function() {
			   var s = Class_forName('java/lang/String')['newInstance()Ljava/lang/Object;']()
			   s.value = this.__className;
			   return s
		   }},
		   // TODO implements method
		   'getDeclaredFields()[': {code: function(_this) {
			   return _this.fields;
		   }}
		}
	}
	
	var allocateObjectFields = function(clazz, obj)
	{
		var superName = clazz.superName;
		if (superName != null)
		{
			var superClass = Class_forName(superName)
			allocateObjectFields(superClass, obj)
		}
		obj.__className = clazz.name;
		for (var f in clazz.fields)
		{
			// TODO null, zero or false
			obj[f] = null;
		}
	}
	var installObjectMethods = function(clazz, obj)
	{
		var superName = clazz.superName;
		if (superName != null)
		{
			var superClass = Class_forName(superName)
			installObjectMethods(superClass, obj)
		}
		for (var f in clazz.methods)
		{
			obj[clazz.name + '.' + f] = clazz.methods[f].code;
			obj[f] = clazz.methods[f].code;
		}
	}
	
	var newRawInstance = function(clazz, obj)
	{
		allocateObjectFields(clazz, obj)
		installObjectMethods(clazz, obj)
		return obj
	}

	installObjectMethods(java_lang_Class_class, java_lang_Object_class)
	installObjectMethods(java_lang_Class_class, java_lang_Class_class)

	
/*servlet-dynamic-code*/
	
}