//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.2.8-b130911.1802 生成的
// 请访问 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2020.07.21 时间 11:16:03 AM CST 
//

package com.wl4g.devops.components.tools.common.serialize.xmlbean;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the
 * com.cn7782.collect.common.message.ncyt.subykyt package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package:
	 * com.cn7782.collect.common.message.ncyt.subykyt
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link MqttData }
	 * 
	 */
	public MqttData createMqttData() {
		return new MqttData();
	}

	/**
	 * Create an instance of {@link MqttData.Session }
	 * 
	 */
	public MqttData.Session createMqttDataSession() {
		return new MqttData.Session();
	}

	/**
	 * Create an instance of {@link MqttData.Session.DataCollector }
	 * 
	 */
	public MqttData.Session.DataCollector createMqttDataSessionDataCollector() {
		return new MqttData.Session.DataCollector();
	}

	/**
	 * Create an instance of {@link MqttData.Session.DataCollector.Type }
	 * 
	 */
	public MqttData.Session.DataCollector.Type createMqttDataSessionDataCollectorType() {
		return new MqttData.Session.DataCollector.Type();
	}

	/**
	 * Create an instance of {@link MqttData.Session.DataCollector.Type.Yk }
	 * 
	 */
	public MqttData.Session.DataCollector.Type.Yk createMqttDataSessionDataCollectorTypeYk() {
		return new MqttData.Session.DataCollector.Type.Yk();
	}

	/**
	 * Create an instance of {@link MqttData.Session.DataCollector.Type.Yt }
	 * 
	 */
	public MqttData.Session.DataCollector.Type.Yt createMqttDataSessionDataCollectorTypeYt() {
		return new MqttData.Session.DataCollector.Type.Yt();
	}

}
