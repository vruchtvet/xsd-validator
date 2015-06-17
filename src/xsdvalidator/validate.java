/*
 * XSD Validator.
 * 
 * Copyright 2013 Adrian Mouat
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
 */
package xsdvalidator;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class validate {

    //This should really come from the script or arg
    private final static String PROGRAM_NAME = "xsdv";
    // Would rather this was autogenerated
    private final static String VERSION = "1.1";
    
    private final static int VALIDATION_FAIL = 1;
    private final static int ERROR_READING_SCHEMA = 2;
    private final static int ERROR_READING_XML = 3;
    
    private static String mXSDFileName;
    private static String mXMLFileName;
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        
        parseArgs(args);
        SchemaFactory factory = SchemaFactory.newInstance(
                "http://www.w3.org/2001/XMLSchema");
            

        File XSDFile = new File(mXSDFileName);
        File XMLFile = new File(mXMLFileName); 
        
        try {
            Schema schema = factory.newSchema(XSDFile);
            Validator validator = schema.newValidator();

            Source source = new StreamSource(XMLFile);


            try {
                validator.validate(source);
                System.out.println(mXMLFileName + " validates.");
            }
            catch (SAXParseException ex) {
                System.out.println(mXMLFileName + " fails to validate because: \n");
                System.out.println(ex.getMessage());
                System.out.println("At: " + ex.getLineNumber() 
                        + ":" + ex.getColumnNumber());
                System.out.println();
                System.exit(VALIDATION_FAIL);
            }
            catch (SAXException ex) {
                System.out.println(mXMLFileName + " fails to validate because: \n");
                System.out.println(ex.getMessage());
                System.out.println();
                System.exit(VALIDATION_FAIL);
            }
            catch (IOException io) {
                System.err.println("Error reading XML source: " + mXMLFileName);
                System.err.println(io.getMessage());
                System.exit(ERROR_READING_XML);
            }

        } catch (SAXException sch) {
            System.err.println("Error reading XML Schema: " + mXSDFileName);
            System.exit(ERROR_READING_SCHEMA);
        }

    }
    
    /**
     * Checks and interprets the command line arguments.
     *
     * Code is based on Sun standard code for handling arguments.
     *
     * @param args    An array of the command line arguments
     */
    private static void parseArgs(final String[] args) {
        
        int argNo = 0;
        String currentArg;
        char flag;

        while (argNo < args.length && args[argNo].startsWith("-")) {
            currentArg = args[argNo++];

            //"wordy" arguments
 
            if (currentArg.equals("--version")) {
                printVersionAndExit();
            } else if (currentArg.equals("--help")) {
                printHelpAndExit();
            } else {

                //(series of) flag arguments
                for (int charNo = 1; charNo < currentArg.length(); charNo++) {
                    flag = currentArg.charAt(charNo);
                    switch (flag) {
                        case 'V':
                            printVersionAndExit();
                            break;
                        case 'h':
                            printHelpAndExit();
                            break;

                        default:
                            System.err.println("Illegal option " + flag);
                            printUsageAndExit();
                            break;
                    }
                }
            }
        }

        if ((argNo + 2) != args.length) {
            //Not given 2 files on input
            printUsageAndExit();
        }

        mXSDFileName = args[argNo];
        mXMLFileName = args[++argNo];
    }
    
    /**
     * Outputs usage message to standard error.
     */
    public static void printUsageAndExit() {
        
        System.err.println(
                "Usage: " + PROGRAM_NAME + " [OPTION]... XSDFILE XMLFILE");
        System.exit(2); //2 indicates incorrect usage
    }
    
    public static void printHelpAndExit() {
        
        System.out.print(
                "\nUsage: " + PROGRAM_NAME + " [OPTION]... XSDFILE XMLFILE\n\n " +
                "Validates the XML document at XMLFILE against the XML Schema at" +
                " XSDFILE.\n\n" +
                "--version  -V  Output version number.\n" +
                "--help  -h  Output this help.\n");

        
        System.exit(0);
    }

    /**
     * Outputs the current version of diffxml to standard out.
     */
    public static void printVersionAndExit() {
        
        System.out.println(PROGRAM_NAME + " Version " + VERSION + "\n");
        System.exit(0);
    }

}
