<xsl:stylesheet xmlns:phi="http://linkeddata.systems/SemanticPHI/Resource#"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                version="2.0">
<xsl:output method="html" encoding="utf-8" indent="yes"/>
<!-- the only node that is labelled is the root node, so use that to determine base-uri (since the function isn't available in a browser) -->
<xsl:variable name="docroot" select="//rdfs:label/../@rdf:about"/>
<xsl:variable name="label" select="//rdfs:label"/>


<xsl:template match="/">

<html>
<head>
<title>
<xsl:value-of select="$label"/>
</title>
<style type="text/css">
body {font-family: Verdana,Arial,Helvetica, sans-serif; font-size: 11px; } table {vertical-align: top;} tr { vertical-align: top;} td { vertical-align: top; font-family: Verdana,Arial,Helvetica, sans-serif; font-size: 11px; } .header { text-align:right; text-decoration: ; font-weight:bold; color:rgb(0,0,0); }
</style>
</head>
<body>
<h1>About: <xsl:value-of select="$label"/></h1>
<h4><xsl:value-of select='$docroot'/></h4>

<br/>

<xsl:for-each select="//rdf:Description[@rdf:about=$docroot]">

<xsl:for-each select="./*">
    <xsl:variable
    name="url"
    select="./@rdf:resource"></xsl:variable>
    <xsl:variable
    name="content"
    select="."></xsl:variable>
    
     <h3> <xsl:value-of select="name(.)"/>:  </h3>
     <p style="text-indent: 50px">
         <xsl:choose>
         <xsl:when test="$content = ''">
           <a href='{$url}'><xsl:value-of select="$url"/></a>
         </xsl:when>
         <xsl:when test="not($content = '')">
           <xsl:value-of select="$content"/> 
         </xsl:when>
       </xsl:choose>
     </p>

</xsl:for-each>

</xsl:for-each>





</body>
</html>

</xsl:template>
</xsl:stylesheet>
