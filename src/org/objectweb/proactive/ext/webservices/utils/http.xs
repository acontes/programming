<?xml version="1.0" encoding="UTF-8" ?>

<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" attributeFormDefault="unqualified">
  <xs:element name="Action">
    <xs:complexType>
      <xs:sequence>
        <xs:element ref="ProActiveOAID" minOccurs="0" maxOccurs="1" />
        <xs:element ref="ProActiveObject" minOccurs="0" maxOccurs="1" />
      </xs:sequence>
      <xs:attribute name="name" type="xs:string" use="required" />
    </xs:complexType>
  </xs:element>

  <xs:element name="ProActiveMessage" >
    <xs:complexType>
      <xs:sequence>
      <xs:element ref="Action" minOccurs="1" maxOccurs="1" />
      </xs:sequence>
    </xs:complexType>
  </xs:element>

  <xs:element name="ProActiveOAID" type="xs:string">
<!--    <xs:complexType mixed="true" /> -->
  </xs:element>

  <xs:element name="ProActiveObject" type="xs:string">
<!--    <xs:complexType mixed="true" /> -->
  </xs:element>

</xs:schema>