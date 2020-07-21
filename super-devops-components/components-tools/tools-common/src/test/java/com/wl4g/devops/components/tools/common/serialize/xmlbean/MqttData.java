//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.2.8-b130911.1802 生成的
// 请访问 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2020.07.21 时间 11:16:03 AM CST 
//

package com.wl4g.devops.components.tools.common.serialize.xmlbean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * anonymous complex type的 Java 类。
 * 
 * <p>
 * 以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Session">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="DataCollector">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *                             &lt;element name="type" maxOccurs="unbounded" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="yk" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;simpleContent>
 *                                             &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>byte">
 *                                               &lt;attribute name="pId" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                             &lt;/extension>
 *                                           &lt;/simpleContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="yt" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;simpleContent>
 *                                             &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>float">
 *                                               &lt;attribute name="pId" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                             &lt;/extension>
 *                                           &lt;/simpleContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                     &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "session" })
@XmlRootElement(name = "mqttData")
public class MqttData {

	@XmlElement(name = "Session", required = true)
	protected MqttData.Session session;

	/**
	 * 获取session属性的值。
	 * 
	 * @return possible object is {@link MqttData.Session }
	 * 
	 */
	public MqttData.Session getSession() {
		return session;
	}

	/**
	 * 设置session属性的值。
	 * 
	 * @param value
	 *            allowed object is {@link MqttData.Session }
	 * 
	 */
	public void setSession(MqttData.Session value) {
		this.session = value;
	}

	/**
	 * <p>
	 * anonymous complex type的 Java 类。
	 * 
	 * <p>
	 * 以下模式片段指定包含在此类中的预期内容。
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="DataCollector">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;sequence>
	 *                   &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
	 *                   &lt;element name="type" maxOccurs="unbounded" minOccurs="0">
	 *                     &lt;complexType>
	 *                       &lt;complexContent>
	 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                           &lt;sequence>
	 *                             &lt;element name="yk" maxOccurs="unbounded" minOccurs="0">
	 *                               &lt;complexType>
	 *                                 &lt;simpleContent>
	 *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>byte">
	 *                                     &lt;attribute name="pId" type="{http://www.w3.org/2001/XMLSchema}byte" />
	 *                                   &lt;/extension>
	 *                                 &lt;/simpleContent>
	 *                               &lt;/complexType>
	 *                             &lt;/element>
	 *                             &lt;element name="yt" maxOccurs="unbounded" minOccurs="0">
	 *                               &lt;complexType>
	 *                                 &lt;simpleContent>
	 *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>float">
	 *                                     &lt;attribute name="pId" type="{http://www.w3.org/2001/XMLSchema}byte" />
	 *                                   &lt;/extension>
	 *                                 &lt;/simpleContent>
	 *                               &lt;/complexType>
	 *                             &lt;/element>
	 *                           &lt;/sequence>
	 *                           &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *                         &lt;/restriction>
	 *                       &lt;/complexContent>
	 *                     &lt;/complexType>
	 *                   &lt;/element>
	 *                 &lt;/sequence>
	 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *               &lt;/restriction>
	 *             &lt;/complexContent>
	 *           &lt;/complexType>
	 *         &lt;/element>
	 *       &lt;/sequence>
	 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}byte" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "dataCollector" })
	public static class Session {

		@XmlElement(name = "DataCollector", required = true)
		protected MqttData.Session.DataCollector dataCollector;
		@XmlAttribute(name = "id")
		protected Byte id;

		/**
		 * 获取dataCollector属性的值。
		 * 
		 * @return possible object is {@link MqttData.Session.DataCollector }
		 * 
		 */
		public MqttData.Session.DataCollector getDataCollector() {
			return dataCollector;
		}

		/**
		 * 设置dataCollector属性的值。
		 * 
		 * @param value
		 *            allowed object is {@link MqttData.Session.DataCollector }
		 * 
		 */
		public void setDataCollector(MqttData.Session.DataCollector value) {
			this.dataCollector = value;
		}

		/**
		 * 获取id属性的值。
		 * 
		 * @return possible object is {@link Byte }
		 * 
		 */
		public Byte getId() {
			return id;
		}

		/**
		 * 设置id属性的值。
		 * 
		 * @param value
		 *            allowed object is {@link Byte }
		 * 
		 */
		public void setId(Byte value) {
			this.id = value;
		}

		/**
		 * <p>
		 * anonymous complex type的 Java 类。
		 * 
		 * <p>
		 * 以下模式片段指定包含在此类中的预期内容。
		 * 
		 * <pre>
		 * &lt;complexType>
		 *   &lt;complexContent>
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *       &lt;sequence>
		 *         &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
		 *         &lt;element name="type" maxOccurs="unbounded" minOccurs="0">
		 *           &lt;complexType>
		 *             &lt;complexContent>
		 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *                 &lt;sequence>
		 *                   &lt;element name="yk" maxOccurs="unbounded" minOccurs="0">
		 *                     &lt;complexType>
		 *                       &lt;simpleContent>
		 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>byte">
		 *                           &lt;attribute name="pId" type="{http://www.w3.org/2001/XMLSchema}byte" />
		 *                         &lt;/extension>
		 *                       &lt;/simpleContent>
		 *                     &lt;/complexType>
		 *                   &lt;/element>
		 *                   &lt;element name="yt" maxOccurs="unbounded" minOccurs="0">
		 *                     &lt;complexType>
		 *                       &lt;simpleContent>
		 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>float">
		 *                           &lt;attribute name="pId" type="{http://www.w3.org/2001/XMLSchema}byte" />
		 *                         &lt;/extension>
		 *                       &lt;/simpleContent>
		 *                     &lt;/complexType>
		 *                   &lt;/element>
		 *                 &lt;/sequence>
		 *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}string" />
		 *               &lt;/restriction>
		 *             &lt;/complexContent>
		 *           &lt;/complexType>
		 *         &lt;/element>
		 *       &lt;/sequence>
		 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
		 *     &lt;/restriction>
		 *   &lt;/complexContent>
		 * &lt;/complexType>
		 * </pre>
		 * 
		 * 
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "time", "type" })
		public static class DataCollector {

			@XmlElement(required = true)
			@XmlSchemaType(name = "dateTime")
			protected XMLGregorianCalendar time;
			protected List<MqttData.Session.DataCollector.Type> type;
			@XmlAttribute(name = "id")
			protected String id;

			/**
			 * 获取time属性的值。
			 * 
			 * @return possible object is {@link XMLGregorianCalendar }
			 * 
			 */
			public XMLGregorianCalendar getTime() {
				return time;
			}

			/**
			 * 设置time属性的值。
			 * 
			 * @param value
			 *            allowed object is {@link XMLGregorianCalendar }
			 * 
			 */
			public void setTime(XMLGregorianCalendar value) {
				this.time = value;
			}

			/**
			 * Gets the value of the type property.
			 * 
			 * <p>
			 * This accessor method returns a reference to the live list, not a
			 * snapshot. Therefore any modification you make to the returned
			 * list will be present inside the JAXB object. This is why there is
			 * not a <CODE>set</CODE> method for the type property.
			 * 
			 * <p>
			 * For example, to add a new item, do as follows:
			 * 
			 * <pre>
			 * getType().add(newItem);
			 * </pre>
			 * 
			 * 
			 * <p>
			 * Objects of the following type(s) are allowed in the list
			 * {@link MqttData.Session.DataCollector.Type }
			 * 
			 * 
			 */
			public List<MqttData.Session.DataCollector.Type> getType() {
				if (type == null) {
					type = new ArrayList<MqttData.Session.DataCollector.Type>();
				}
				return this.type;
			}

			/**
			 * 获取id属性的值。
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getId() {
				return id;
			}

			/**
			 * 设置id属性的值。
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setId(String value) {
				this.id = value;
			}

			/**
			 * <p>
			 * anonymous complex type的 Java 类。
			 * 
			 * <p>
			 * 以下模式片段指定包含在此类中的预期内容。
			 * 
			 * <pre>
			 * &lt;complexType>
			 *   &lt;complexContent>
			 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
			 *       &lt;sequence>
			 *         &lt;element name="yk" maxOccurs="unbounded" minOccurs="0">
			 *           &lt;complexType>
			 *             &lt;simpleContent>
			 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>byte">
			 *                 &lt;attribute name="pId" type="{http://www.w3.org/2001/XMLSchema}byte" />
			 *               &lt;/extension>
			 *             &lt;/simpleContent>
			 *           &lt;/complexType>
			 *         &lt;/element>
			 *         &lt;element name="yt" maxOccurs="unbounded" minOccurs="0">
			 *           &lt;complexType>
			 *             &lt;simpleContent>
			 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>float">
			 *                 &lt;attribute name="pId" type="{http://www.w3.org/2001/XMLSchema}byte" />
			 *               &lt;/extension>
			 *             &lt;/simpleContent>
			 *           &lt;/complexType>
			 *         &lt;/element>
			 *       &lt;/sequence>
			 *       &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}string" />
			 *     &lt;/restriction>
			 *   &lt;/complexContent>
			 * &lt;/complexType>
			 * </pre>
			 * 
			 * 
			 */
			@XmlAccessorType(XmlAccessType.FIELD)
			@XmlType(name = "", propOrder = { "yk", "yt" })
			public static class Type {

				protected List<MqttData.Session.DataCollector.Type.Yk> yk;
				protected List<MqttData.Session.DataCollector.Type.Yt> yt;
				@XmlAttribute(name = "Type")
				protected String type;

				/**
				 * Gets the value of the yk property.
				 * 
				 * <p>
				 * This accessor method returns a reference to the live list,
				 * not a snapshot. Therefore any modification you make to the
				 * returned list will be present inside the JAXB object. This is
				 * why there is not a <CODE>set</CODE> method for the yk
				 * property.
				 * 
				 * <p>
				 * For example, to add a new item, do as follows:
				 * 
				 * <pre>
				 * getYk().add(newItem);
				 * </pre>
				 * 
				 * 
				 * <p>
				 * Objects of the following type(s) are allowed in the list
				 * {@link MqttData.Session.DataCollector.Type.Yk }
				 * 
				 * 
				 */
				public List<MqttData.Session.DataCollector.Type.Yk> getYk() {
					if (yk == null) {
						yk = new ArrayList<MqttData.Session.DataCollector.Type.Yk>();
					}
					return this.yk;
				}

				/**
				 * Gets the value of the yt property.
				 * 
				 * <p>
				 * This accessor method returns a reference to the live list,
				 * not a snapshot. Therefore any modification you make to the
				 * returned list will be present inside the JAXB object. This is
				 * why there is not a <CODE>set</CODE> method for the yt
				 * property.
				 * 
				 * <p>
				 * For example, to add a new item, do as follows:
				 * 
				 * <pre>
				 * getYt().add(newItem);
				 * </pre>
				 * 
				 * 
				 * <p>
				 * Objects of the following type(s) are allowed in the list
				 * {@link MqttData.Session.DataCollector.Type.Yt }
				 * 
				 * 
				 */
				public List<MqttData.Session.DataCollector.Type.Yt> getYt() {
					if (yt == null) {
						yt = new ArrayList<MqttData.Session.DataCollector.Type.Yt>();
					}
					return this.yt;
				}

				/**
				 * 获取type属性的值。
				 * 
				 * @return possible object is {@link String }
				 * 
				 */
				public String getType() {
					return type;
				}

				/**
				 * 设置type属性的值。
				 * 
				 * @param value
				 *            allowed object is {@link String }
				 * 
				 */
				public void setType(String value) {
					this.type = value;
				}

				/**
				 * <p>
				 * anonymous complex type的 Java 类。
				 * 
				 * <p>
				 * 以下模式片段指定包含在此类中的预期内容。
				 * 
				 * <pre>
				 * &lt;complexType>
				 *   &lt;simpleContent>
				 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>byte">
				 *       &lt;attribute name="pId" type="{http://www.w3.org/2001/XMLSchema}byte" />
				 *     &lt;/extension>
				 *   &lt;/simpleContent>
				 * &lt;/complexType>
				 * </pre>
				 * 
				 * 
				 */
				@XmlAccessorType(XmlAccessType.FIELD)
				@XmlType(name = "", propOrder = { "value" })
				public static class Yk {

					@XmlValue
					protected byte value;
					@XmlAttribute(name = "pId")
					protected Byte pId;

					/**
					 * 获取value属性的值。
					 * 
					 */
					public byte getValue() {
						return value;
					}

					/**
					 * 设置value属性的值。
					 * 
					 */
					public void setValue(byte value) {
						this.value = value;
					}

					/**
					 * 获取pId属性的值。
					 * 
					 * @return possible object is {@link Byte }
					 * 
					 */
					public Byte getPId() {
						return pId;
					}

					/**
					 * 设置pId属性的值。
					 * 
					 * @param value
					 *            allowed object is {@link Byte }
					 * 
					 */
					public void setPId(Byte value) {
						this.pId = value;
					}

				}

				/**
				 * <p>
				 * anonymous complex type的 Java 类。
				 * 
				 * <p>
				 * 以下模式片段指定包含在此类中的预期内容。
				 * 
				 * <pre>
				 * &lt;complexType>
				 *   &lt;simpleContent>
				 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>float">
				 *       &lt;attribute name="pId" type="{http://www.w3.org/2001/XMLSchema}byte" />
				 *     &lt;/extension>
				 *   &lt;/simpleContent>
				 * &lt;/complexType>
				 * </pre>
				 * 
				 * 
				 */
				@XmlAccessorType(XmlAccessType.FIELD)
				@XmlType(name = "", propOrder = { "value" })
				public static class Yt {

					@XmlValue
					protected float value;
					@XmlAttribute(name = "pId")
					protected Byte pId;

					/**
					 * 获取value属性的值。
					 * 
					 */
					public float getValue() {
						return value;
					}

					/**
					 * 设置value属性的值。
					 * 
					 */
					public void setValue(float value) {
						this.value = value;
					}

					/**
					 * 获取pId属性的值。
					 * 
					 * @return possible object is {@link Byte }
					 * 
					 */
					public Byte getPId() {
						return pId;
					}

					/**
					 * 设置pId属性的值。
					 * 
					 * @param value
					 *            allowed object is {@link Byte }
					 * 
					 */
					public void setPId(Byte value) {
						this.pId = value;
					}

				}

			}

		}

	}

}
