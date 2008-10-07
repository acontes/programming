<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ec2="http://ec2.amazonaws.com/doc/2008-05-05/" exclude-result-prefixes="ec2">
    <xsl:output method="xml" omit-xml-declaration="no" indent="yes"/>
    <xsl:variable name="ns" select="'http://ec2.amazonaws.com/doc/2008-05-05/'"/>
    <xsl:template match="ec2:TerminateInstancesResponse">
        <xsl:element name="TerminateInstancesResponse" namespace="{$ns}">
            <xsl:element name="ResponseMetadata" namespace="{$ns}">
                <xsl:element name="RequestId" namespace="{$ns}"/>
            </xsl:element>
            <xsl:element name="TerminateInstancesResult" namespace="{$ns}">
                <xsl:apply-templates select="ec2:instancesSet"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="ec2:instancesSet">
        <xsl:for-each select="ec2:item">
            <xsl:element name="TerminatingInstance" namespace="{$ns}">
                <xsl:element name="InstanceId" namespace="{$ns}">
                    <xsl:value-of select="ec2:instanceId"/>
                </xsl:element>
                <xsl:element name="ShutdownState" namespace="{$ns}">
                    <xsl:element name="Code" namespace="{$ns}">
                        <xsl:value-of select="ec2:shutdownState/ec2:code"/>
                    </xsl:element>
                    <xsl:element name="Name" namespace="{$ns}">
                        <xsl:value-of select="ec2:shutdownState/ec2:name"/>
                    </xsl:element>
                </xsl:element>
                <xsl:element name="PreviousState" namespace="{$ns}">
                    <xsl:element name="Code" namespace="{$ns}">
                        <xsl:value-of select="ec2:previousState/ec2:code"/>
                    </xsl:element>
                    <xsl:element name="Name" namespace="{$ns}">
                        <xsl:value-of select="ec2:previousState/ec2:name"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>