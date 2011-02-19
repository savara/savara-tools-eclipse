/*
 * Copyright 2005-8 Pi4 Technologies Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Change History:
 * 16 Jan, 2008 : Initial version created by martin
 */
package org.savara.tools.monitor.ui;


import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

//import org.apache.xml.serialize.OutputFormat;
//import org.apache.xml.serialize.XMLSerializer;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.ByteArrayOutputStream;

import org.xml.sax.SAXException;

/**
 *
 */
public class XmlPrettyPrinter{

    /**
     *
     */
    public static String prettify(String input){

        // Find the implementation
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;

        try{
            builder = factory.newDocumentBuilder();
        }
        catch(ParserConfigurationException pce){
            System.err.println("Exception: " + pce);
            return null;
        }

        StringBufferInputStream stringBufferInputStream = new StringBufferInputStream(input);

        Document doc = null;

        try{
            doc = builder.parse(stringBufferInputStream);
        }
        catch(SAXException se){
            System.err.println("Exception: " + se);
            return null;
        }
        catch(IOException ioe){
            System.err.println("Exception: " + ioe);
            return null;
        }
        
        String text=null;
        
        try {
        	text = org.pi4soa.common.xml.XMLUtils.getText(doc, true);
        } catch(Exception e) {
        	System.err.println("Exception: " + e);
        }
        
        return(text);

        /*
        OutputFormat format = new OutputFormat(doc);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLSerializer serializer = new XMLSerializer(byteArrayOutputStream, format);

        try{
            serializer.serialize(doc);
        }
        catch(IOException ioe){
            System.err.println("Exception: " + ioe);
            return null;
        }

        return byteArrayOutputStream.toString();
        */
    }

}
