/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.components.tools.common.serialize;

import com.wl4g.devops.components.tools.common.serialize.xmlbean.MqttData;

/**
 * {@link JaxbUtilsTests}
 * 
 * <pre>
 * Step 1: Convert the sample XML data into an XSD definition file. 
 * 	       Recommended online conversion: <a href=
"https://www.freeformatter.com/xsd-generator.html#ad -output">https://www.freeformatter.com/xsd-generator.html#ad -output</a>
 *
 * Step 2: Generate the JAXB class from the XSD definition file. For example, 
 * 		   Use Eclipse: Select XSD file -> Right-click -> Generate -> JAXB classes...
 * 		   Use IDEA: Tools > JAXB > Generate Jaca Code From Xml Schema Using JAXB...
 * </pre>
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-07-21
 * @since
 */
public class JaxbUtilsTests {

	public static void main(String[] args) {
		String xml = "";
		xml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		xml += "<mqttData>";
		xml += "  <Session id=\"10\">";
		xml += "  <DataCollector id=\"ncyt02_dc\">";
		xml += "  	<time>2016-11-22T03:39:30Z</time>";
		xml += "    <type Type=\"Yk\">";
		xml += "      <yk pId=\"0\">1</yk>";
		xml += "      <yk pId=\"1\">1</yk>";
		xml += "      <yk pId=\"2\">1</yk>";
		xml += "      <yk pId=\"3\">0</yk>";
		xml += "      <yk pId=\"4\">1</yk>";
		xml += "      <yk pId=\"5\">1</yk>";
		xml += "      <yk pId=\"6\">0</yk>";
		xml += "      <yk pId=\"7\">1</yk>";
		xml += "      <yk pId=\"8\">0</yk>";
		xml += "    </type>";
		xml += "    <type Type=\"Yt\">";
		xml += "      <yt pId=\"0\">30.00</yt>";
		xml += "      <yt pId=\"1\">40.00</yt>";
		xml += "      <yt pId=\"2\">50.00</yt>";
		xml += "      <yt pId=\"3\">60.00</yt>";
		xml += "      <yt pId=\"4\">70.00</yt>";
		xml += "      <yt pId=\"5\">80.00</yt>";
		xml += "      <yt pId=\"6\">90.00</yt>";
		xml += "      <yt pId=\"7\">100.00</yt>";
		xml += "      <yt pId=\"8\">11.000</yt>";
		xml += "    </type>";
		xml += "  </DataCollector>";
		xml += "  </Session>";
		xml += "</mqttData>";

		MqttData msg = JaxbUtils.fromXml(xml, MqttData.class);
		System.out.println(msg.getSession().getDataCollector().getTime());

	}

}
