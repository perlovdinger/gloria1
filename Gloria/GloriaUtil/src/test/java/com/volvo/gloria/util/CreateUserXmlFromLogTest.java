/*
 * Copyright 2009 Volvo Information Technology AB 
 * 
 * Licensed under the Volvo IT Corporate Source License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *      http://java.volvo.com/licenses/CORPORATE-SOURCE-LICENSE 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.volvo.gloria.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.naming.NamingException;

import org.junit.Ignore;
import org.junit.Test;

public class CreateUserXmlFromLogTest {

    @Ignore
    @Test
    public void testCreateUserXmlFromLog() throws IOException, NamingException {
        InputStream in = ExtractLDAPInfo.class.getClassLoader().getResourceAsStream("inituser/gloria_gateway_xml.log");
        BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
        String line = null;
        String idStringStart = "UserGatewayBean] - UserGatewayBean xml=";
        String idStringEnd = "</GMFVerb></UserAndOrganisation>";
        while ((line = br.readLine()) != null) {
            if (line.contains(idStringStart)) {
                //System.out.println("line=" + line);
                int startPosXml = line.lastIndexOf(idStringStart) + 39;
                int endPosXml = line.lastIndexOf(idStringEnd) + 33;             
                
                String xml = line.substring(startPosXml,endPosXml);            
                //System.out.println("xml=" + xml);
                String startUserId = "<UserID>";
                String endUserId = "</UserID>";
                int startPosUserId = line.lastIndexOf(startUserId) + 8;
                int endPosUserId = line.lastIndexOf(endUserId);
                String userid = line.substring(startPosUserId,endPosUserId); 
                BufferedWriter xmlFile = new BufferedWriter(new FileWriter(new File(userid + ".xml")));
                xmlFile.write(xml);
                xmlFile.close();
            }
        }
    }  
}
