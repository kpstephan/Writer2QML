<?xml version="1.0" encoding="ISO-8859-1"?>
<!-- Stand:27.4.08, Anpassungen für Writer, unterscheidet sich zum Server-Stylesheet -->
<!-- Stylesheet für die html-Ausgabe des Fragebogens-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:qml="www.qml.uni-siegen.de">
    <!-- Ausgabeformat: XHTML, Zeichensatz -->
    <xsl:output method="xhtml" encoding="ISO-8859-1"/>
    
    <!-- Template, für die Unterscheidung zwischen Fragebögen, die aus mehreren Teilen bestehen und solchen, die aus nur einem
    Teil bestehen-->
    <!-- ++alles++ -->
    <xsl:template match="/">
        <!-- Abfrage, um welchen Typ es geht -->
        <xsl:choose>
            <!-- wenn es mindestens eine section vom Typ=page gibt... -->
            <xsl:when test="count(qml:questionnaire/qml:section[@type='page'])&gt;0">
                <xsl:for-each select="document('in.xml')/qml:questionnaire/qml:section[@type='page']">
                    <!--  ... wird für jeden Fragebogenteil eine eigene Output-Datei mit dem Namen out1.html bis outn.html erzeugt;
                    der Kontext wechselt auf Section-->
 
                    <xsl:result-document href="{if(position()= 1) then 'index.html' else concat('out', position(), '.html')}">
                        <!-- Aufruf des templates, das die html-Grundstruktur erzeugt -->
<xsl:call-template name="qml:questionnaire_page"/>
                    </xsl:result-document>
                </xsl:for-each>
            </xsl:when>
            <!-- falls der Fragebogen nur aus einem Teil besteht, wird nur eine Output-Datei mit Namen out1.html erzeugt;  Kontext ist questionnaire-->
            <xsl:otherwise>
                <xsl:for-each select="document('in.xml')/qml:questionnaire">  
                    <xsl:result-document href="index.html">
                <!--  Aufruf, des templates, das die html-Grundstruktur erzeugt für Fragebögen mit einer Seite-->             
<xsl:call-template name="qml:questionnaire"/>
                    </xsl:result-document>
                </xsl:for-each>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- template für die Grundstruktur der html-Ausgabe bei Fragebögen mit mehreren Seiten -->
    <xsl:template name="qml:questionnaire_page">
        <html>
            <head>
                <title>
                    <!-- Auslesen des Fragebogentitels für die Titelleiste der html-Datei -->
                    <xsl:value-of select="if(boolean(parent::qml:questionnaire/@title)) then parent::qml:questionnaire/@title else 'Fragebogen'"/>
                </title>
                <!-- Verknüpfung zum css-Stylesheet, das die Formatierungen für den Fragebogen enthält -->
                <link href="style.css" rel="styleSheet" type="text/css"/>
                <!-- Verknüpfung zur Javascript-Datei, wird benötigt für Abtesten der Textfeldlängen und für Löschen der Textfelder  bei Betätigen
                    eines anderen Radiobuttons der Frage-->
                <script type="text/javascript" language="JavaScript" src="script.js"/>
            </head>
            <body>
                <!-- falls der Fragebogen einen Titel hat.... -->
                <xsl:if test="boolean(parent::qml:questionnaire/@title)">
                    <!-- ...wird dieser ausgegeben -->
                        <h1><xsl:value-of select="parent::qml:questionnaire/@title"/></h1>
                </xsl:if>
                <!-- Angabe, aus wie vielen Teilen der Fragebogen besteht; Teil x von y, wobei x der aktuelle und y die Gesamtzahl der Teile ist -->
              <p class="pages">
                    <xsl:value-of select="concat('Teil ', position(), ' von ', count(parent::qml:questionnaire/qml:section[@type='page']))"/>
                </p>
                <!-- Verknüpfung zum php-Dokument, das den Fragebogen ausliefert und die Formularwerte übernimmt-->
   
   
                  <form action="{if(position()=last()) then 'index.html' else concat('out', position() +1, '.html')}" method="post">

                    <input type="hidden" name="qid" value="###qid###"/>
                    <input type="hidden" name="qstnSec" value="{position()}"/>
                    <!-- Aufruf des templates, dass die aktuelle section behandelt -->
<xsl:call-template name="section"/>
                    <!-- Button zum Registrieren der Daten, wenn es noch eine weitere Seite gibt, wird Text "Weiter..." angezeigt,
                        wenn es die letzte Seite ist, wird "Befragung registrieren" angezeigt -->
                    <xsl:choose>
                        <xsl:when test="position()!=last()">
                            <input type="submit" name="next" value="Weiter zur nächsten Seite"/>   
                        </xsl:when>
                        <xsl:otherwise>
                            <input type="submit"  name="end" value="Befragung abschließen"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </form>
            </body>
            </html>
    </xsl:template>
    
    <!-- template für die Grundstruktur der html-Ausgabe bei Fragebögen mit einer Seite -->
    <xsl:template name="qml:questionnaire">
        <html>
            <head>
                <title>
                    <!-- Auslesen des Fragebogentitels für die Titelleiste der html-Datei -->
                    <xsl:value-of select="if(boolean(@title)) then @title else 'Fragebogen'"/>
                </title>
                <!-- Verknüpfung zum css-Stylesheet, das die Formatierungen für den Fragebogen enthält -->
                <link href="style.css" rel="styleSheet" type="text/css"/>
                <!-- Verknüpfung zur Javascript-Datei, wird benötigt für Abtesten der Textfeldlängen und für Löschen der Textfelder  bei Betätigen
                    eines anderen Radiobuttons der Frage-->
                <script type="text/javascript" language="JavaScript" src="script.js"/>
            </head>
            <body>
                <!-- falls der Fragebogen einen Titel hat.... -->
                <xsl:if test="boolean(@title)">
                    <!-- ....wird dieser ausgegeben -->
                        <h1><xsl:value-of select="@title"/></h1>
                </xsl:if>
                <!-- Verknüpfung zum php-Dokument, das den Fragebogen ausliefert und die Formularwerte übernimmt-->
                <form action="index.html" method="post">
                    <!-- versteckte Formularfelder für die questionnaire Id und die Kodierung der Fragebogenseite für die Auslieferungslogik-->
                    <input type="hidden" name="qid" value="###qid###"/>
                    <input type="hidden" name="qstnSec" value="{position()}"/>
                    <!-- Aufruf des templates für alle Kindelemente von Questionnaire-->
<xsl:call-template name="children"/><!-- 2.2 -->
                    <!-- Button zum Registrieren der Befragungsdaten -->
                    <input type="submit" name="end" value="Befragung abschließen"/>
                </form>
            </body>
        </html>
    </xsl:template>
    
    <!-- Template für section-Elemente, ruft rekursiv das Children-template auf, um auf geschachtelte section-Ebenen eingehen zu können -->
    <!-- alle sections kommen hier an -->
    <xsl:template name="section">
        <!-- falls die Section einen Titel hat, wird der ausgegeben -->
        <xsl:if test="boolean(@title)">
               <h2><xsl:value-of select="@title"/></h2>
        </xsl:if>
        <!-- Unterscheidung verschiedener Arten von Sections und entsprechender Template-Aufruf -->
        <xsl:choose>
            <!-- Sections vom Typ group, bekommen eine Klasse für die Formatierung durchs CSS zugewiesen und
                rufen dann wieder das Template children auf, um die Kindelemente der Section zu behandeln-->
            <xsl:when test="self::qml:section[@type='group']">
                <div class="group">
<xsl:call-template name="children"/>
               </div>
            </xsl:when>
            <!-- Sections type page rufen einfach wieder das Children-template auf, in diesem Template wurde nur ihr Titel ausgegeben,
                die Behandlung der Kindelemente erfolgt über den Aufruf des children-templates-->
            <xsl:when test="self::qml:section[@type='page']">
<xsl:call-template name="children"/>
            </xsl:when>           
        </xsl:choose>
    </xsl:template>  
    
    <!--Template unterscheidet die verschiedenen Elemente, die als Kindelemente von questionnaire und section auftreten können,
        und ruft für jedes das entsprechende Item auf; hier kommen beide templates "questionnaire" (mit Kontext questionnaire) und
        "questionnaire_pages" (mit Kontext section) an-->
    <xsl:template name="children">
        <!-- ausgewählt werden alle Kindelemente des aktuellen Elementes, der Kontext wechselt jeweils zu diesem element-->
        <xsl:for-each select="child::*">
            <xsl:choose>
                <!-- aktuelles Element ist section -->
                <xsl:when test="self::qml:section">
<xsl:call-template name="section"/>
                </xsl:when>
                <!-- aktuelles Element ist  item, matrix oder differential -->
                <xsl:when test="self::qml:item or self::qml:matrix or self::qml:differential">
<xsl:call-template name="item"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:choose>
                        <xsl:when test="parent::qml:questionnaire">
                            <xsl:if test="position()=1">
                                <xsl:text disable-output-escaping="yes">&lt;div class="questionnaire"&gt;</xsl:text>       
                            </xsl:if>
                                <p class="{if(self::qml:intro) then 'introQuestionnaire' else (if(self::qml:instruction) then 'instructionQuestionnaire' else 'adQuestionnaire')}">
                                    <xsl:choose>
                                        <xsl:when test="self::qml:additionalText">
                                            <xsl:value-of select="."/>  
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:call-template name="intro_and_instruction"/>                               
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </p>                    
                            <xsl:if test="not(following-sibling::qml:intro) and not(following-sibling::qml:instruction) and not(following-sibling::qml:additionalText)">
                            <xsl:text disable-output-escaping="yes">&lt;/div&gt;</xsl:text>
                                </xsl:if>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:if test="position()=1">
                                <xsl:text disable-output-escaping="yes">&lt;div class="section"&gt;</xsl:text>       
                            </xsl:if>
                                <p class="{if(self::qml:intro) then 'introSection' else (if(self::qml:instruction) then 'instructionSection' else 'adSection')}">
                                    <xsl:choose>
                                        <xsl:when test="self::qml:additionalText">
                                            <xsl:value-of select="."/>  
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:call-template name="intro_and_instruction"/>                               
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </p>   
                            <xsl:if test="not(following-sibling::qml:intro) and not(following-sibling::qml:instruction) and not(following-sibling::qml:additionalText)">
                                <xsl:text disable-output-escaping="yes">&lt;/div&gt;</xsl:text>
                            </xsl:if>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
        </xsl:template>

      
<xsl:template name="intro_and_instruction">
        <xsl:choose>
            <xsl:when test="boolean(qml:textPar)">
                <xsl:for-each select="qml:textPar">
                    <p class="textPar">
                    <xsl:value-of select="."/>
                        </p>
                    </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
</xsl:template>   
    
    
    <!-- Template für die Nummerierung der Fragen -->
    <xsl:template name="number">
   <p class="number">
        <!-- gezählt werden alle section-Elemente, die eine matrix sind und alle sonstigen item-Elemente, die
            nicht Bestandteil einer matrix sind-->
        <xsl:number level="any" count="qml:matrix | qml:differential | qml:item[not(parent::qml:matrix) and not(parent::qml:differential)]" format="1."/>
     </p>
    </xsl:template>
    
   
    <!-- Template für die Behandlung von Itemarten: das sind matrix, differential, normale Items, Likert-Items-->
    <xsl:template name="item">
        <!-- Aufruf der Nummerierung -->
<!-- Behandlung aller Kindelemente von Item -->
        <xsl:for-each select="child::*">
            <xsl:choose>
                <!-- aktuelles Element ist intro -->
                <xsl:when test="self::qml:intro">
                    <p class="introItem">
<xsl:call-template name="intro_and_instruction"/>
                    </p>
                </xsl:when>
                <!-- aktuelles Element ist instruction-->
                <xsl:when test="self::qml:instruction">
                    <p class="instructionItem">
<xsl:call-template name="intro_and_instruction"/>
                    </p>
                </xsl:when>
                <!-- bei question, matrix, differential  und additionalText werden entsprechende css-Klassen vergeben und das Element ausgegeben -->
                <xsl:when test="self::qml:question or self::qml:matrixQuestion or self::qml:differentialQuestion or self::qml:additionalText">
                   <xsl:if test="not(self::qml:additionalText)">
                    <xsl:call-template name="number"/>
                   </xsl:if>
                    <p class="{if(self::qml:additionalText) then 'adItem' else 'question'}">
                        <xsl:value-of select="."/>
                    </p>
                </xsl:when>
            </xsl:choose>
        </xsl:for-each>
<!-- Unterscheidung der Arten von Antwortskalen, differential bekommt ein eigenes template, alle anderen werden zunächst in horizontale und vertikale Antwortskalen unterschieden -->
        <xsl:choose>
            <!-- differential -->
            <xsl:when test="qml:differentialResponses">
<xsl:call-template name="differential"/>                
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
    <!--horizontale Skala für matrix, likert und normal-->
            <xsl:when test="descendant-or-self::qml:item[position()=1]/qml:responses[@orientation='horizontal']">
<xsl:call-template name="scales_horizontal"/>
            </xsl:when>
                    <!-- vertikale Skala, trifft nur für normale Fragen + likert-skalen zu, Matrixfragen sind nur horizontal -->
                    <xsl:when test="descendant-or-self::qml:item[position()=1]/qml:responses[@orientation='vertical']">
<xsl:call-template name="scales_vertical"/>  
                    </xsl:when>                   
                    </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="scales_horizontal">
        <table class="horizontal">
            <xsl:choose>
                <!-- Likert Fragen horizontal -->
                <xsl:when test="qml:responses[@likert='true']">
<xsl:call-template name="likert_horizontal"/>
                </xsl:when>
                <xsl:when test="self::qml:matrix">
<xsl:call-template name="matrix"/>
                </xsl:when>
                <!-- normale Fragen horizontal -->
                <xsl:otherwise>
<xsl:call-template name="item_horizontal"/> 
                </xsl:otherwise>
            </xsl:choose>            
        </table>
    </xsl:template>
    
  <xsl:template name="matrix">
<xsl:for-each select="qml:item[1]">
      <tr>
          <th class="topleft">
              <xsl:if test="qml:responses/qml:intro or qml:responses/qml:instruction or qml:responses/qml:additionalText">
                  <xsl:for-each select="qml:responses/qml:intro | qml:responses/qml:instruction | qml:responses/qml:additionalText">
                      <xsl:value-of select="."/>
                      <xsl:if test="position()!=last()">
                          <br/>
                      </xsl:if>
                  </xsl:for-each>
                  </xsl:if>
          </th>
          <!-- Zeile mit Antworten -->
          <xsl:for-each select="qml:responses/qml:choice">
              <!--NonOpinion-Kategorien werden abgesetzt-->
              <xsl:if test="@type='nonOpinion' and not(boolean(preceding-sibling::qml:choice[@type='nonOpinion']))">
                  <th class="blindHorizontal"></th>
              </xsl:if>
              <th class="{if(@type='nonOpinion') then 'nonOpinion' else (if(string-length(self::qml:choice)&gt;0) then 'answer' else 'answerEmpty')}">
                  <xsl:value-of select="."/>
              </th>
              </xsl:for-each>
      </tr>
</xsl:for-each>
    <xsl:for-each select="qml:item">
        <tr>
            <th class="question">
                <xsl:number value="position()" format="a) "/>
                <xsl:value-of select="qml:question"/>
            </th>
            <xsl:for-each select="qml:responses/qml:choice">
                <xsl:if test="@type='nonOpinion' and not(boolean(preceding-sibling::qml:choice[@type='nonOpinion']))">
                    <td class="blindHorizotal"></td>
                </xsl:if>
                <!-- Spalte mit Buttons -->
                <td class="{if(@type='nonOpinion') then 'nonOpinion' else 'answer'}">
                    <xsl:call-template name="choose_buttons"/> 
                    <xsl:if test="parent::qml:responses[@showValues='true' and @type='single'] and @type!='open'">
                        <xsl:value-of select="if(@value) then @value else position()"/>
                    </xsl:if>
                </td>  
            </xsl:for-each>
      </tr>
    </xsl:for-each>
  </xsl:template>
    
   <xsl:template name="likert_horizontal">
       <tr>
       <xsl:for-each select="qml:responses/qml:choice">
           <!--NonOpinion-Kategorien werden abgesetzt-->
           <xsl:if test="@type='nonOpinion' and not(boolean(preceding-sibling::qml:choice[@type='nonOpinion']))">
                   <th class="blindHorizontal"></th>
                   <th class="nonOpinion">
                       <xsl:value-of select="."/>
                   </th>
                   <th class="likert_scale">
                       <xsl:call-template name="choose_buttons"/>
                       <xsl:if test="parent::qml:responses[@showValues='true']">
                           <xsl:value-of select="@value"/>
                       </xsl:if>
                   </th>
           </xsl:if>
           <!-- Skala -->
           <xsl:if test="boolean(@position)">
                   <th class="{if(@position='first') then 'likert_first' else 'likert_last'}">
                       <xsl:value-of select="."/>
                   </th>
               <xsl:if test="@position='first'">
                   <xsl:call-template name="likert_loop"/>
               </xsl:if>
           </xsl:if>
       </xsl:for-each>
           </tr>
   </xsl:template> 
    
    <xsl:template name="item_horizontal">
        <xsl:if test="qml:responses/qml:intro or qml:responses/qml:instruction or qml:responses/qml:additionalText">
            <tr>
                <th class="responsesIntro" colspan="{if(qml:responses/qml:choice[@type='nonOpinion']) then number(count(qml:responses/qml:choice[@type='nonOpinion']) + count(qml:responses/qml:choice[@type!='nonOpinion']) + 1) else count(qml:responses/qml:choice)}">
                    <xsl:for-each select="qml:responses/qml:intro | qml:responses/qml:instruction | qml:responses/qml:additionalText">
                        <xsl:value-of select="."/>
                        <xsl:if test="position()!=last()">
                            <br/>
                        </xsl:if>
                    </xsl:for-each>
                </th>
            </tr>    
        </xsl:if>
        <tr>
        <xsl:for-each select="qml:responses/qml:choice">
            <!--NonOpinion-Kategorien werden abgesetzt-->
            <xsl:if test="@type='nonOpinion' and not(boolean(preceding-sibling::qml:choice[@type='nonOpinion']))">
                    <th class="blindHorizontal"></th>
            </xsl:if>
            <!-- zeile mit den Antwortalternativen -->
            <th class="{if(@type='nonOpinion') then 'nonOpinion' else (if(string-length(self::qml:choice)&gt;0) then 'answer' else 'answerEmpty')}">
                <xsl:value-of select="."/>
            </th>
        </xsl:for-each>
        </tr>
        <tr>
            <xsl:for-each select="qml:responses/qml:choice">
                <xsl:if test="@type='nonOpinion' and not(boolean(preceding-sibling::qml:choice[@type='nonOpinion']))">
                    <td class="blindHorizotal"></td>
                </xsl:if>
                <!-- Spalte mit Buttons -->
            <td class="{if(@type='nonOpinion') then 'nonOpinion' else 'answer'}">
                <xsl:call-template name="choose_buttons"/> 
                <xsl:if test="parent::qml:responses[@showValues='true' and @type='single'] and @type!='open'">
                    <xsl:value-of select="if(@value) then @value else position()"/>
                </xsl:if>
            </td>
            </xsl:for-each>
        </tr>
    </xsl:template>
    
    <!-- hier kommen an: vertikale skalen: normale fragen und likert-fragen -->
    <xsl:template name="scales_vertical">
        <table class="vertical">
            <xsl:choose>
                <!-- Likert-Fragen vertical -->
                <xsl:when test="qml:responses[@likert='true']">
<xsl:call-template name="likert_vertical"/>                    
                </xsl:when>
                <xsl:otherwise>
                    <!-- Normale Fragen, vertical -->
<xsl:call-template name="item_vertical"/>                    
                </xsl:otherwise>
            </xsl:choose>
        </table>
    </xsl:template>
    
<!-- hier kommen an: normale fragen vertical--> 
    <xsl:template name="item_vertical">
<xsl:if test="qml:responses/qml:intro or qml:responses/qml:instruction or qml:responses/qml:additionalText">
    <tr>
        <th class="responsesIntro" colspan="2">
        <xsl:for-each select="qml:responses/qml:intro | qml:responses/qml:instruction | qml:responses/qml:additionalText">
            <xsl:value-of select="."/>
                  <xsl:if test="position()!=last()">
                <br/>
            </xsl:if>
        </xsl:for-each>
        </th>
        </tr>    
</xsl:if>
        <xsl:for-each select="qml:responses/qml:choice">
            <!--NonOpinion-Kategorien werden abgesetzt-->
            <xsl:if test="@type='nonOpinion' and not(boolean(preceding-sibling::qml:choice[@type='nonOpinion']))">
                <tr>
                    <th class="blindVertical"></th>
                    <td class="blindVertical"></td>
                </tr>
            </xsl:if>
            <tr>
                <!-- Spalte mit den Antwortalternativen -->
                <th class="{if(@type='nonOpinion') then 'nonOpinion' else (if(string-length(self::qml:choice)&gt;0) then 'answer' else (if(count(parent::qml:responses/qml:choice) =1) then 'oneAnswer' else 'answerEmpty'))}">
                    <xsl:value-of select="."/>
                </th>
                <!-- Aufruf des Templates für Auswahl der Formularfeldertypen -->
                <td class="{if(@type='nonOpinion') then 'nonOpinion' else (if(count(parent::qml:responses/qml:choice[@type='open'])&gt; 0) then 'answerVerticalOpen' else 'answer')}">
<xsl:call-template name="choose_buttons"/> 
                    <xsl:if test="parent::qml:responses[@showValues='true' and @type='single'] and @type!='open'">
                    <xsl:value-of select="if(@value) then @value else position()"/>
                    </xsl:if>
            </td>
            </tr>
            </xsl:for-each>
    </xsl:template>
                
<!-- Likert Skalen vertical -->
<xsl:template name="likert_vertical">
    <xsl:for-each select="qml:responses/qml:choice">
        <!--NonOpinion-Kategorien werden abgesetzt-->
        <xsl:if test="@type='nonOpinion' and not(boolean(preceding-sibling::qml:choice[@type='nonOpinion']))">
            <tr>  
                <th class="blindVertical"></th>
            </tr>
            <tr>
                <th class="nonOpinion">
                    <xsl:value-of select="."/>
                </th>
                <td class="likert_scale">
<xsl:call-template name="choose_buttons"/>
                    <xsl:if test="parent::qml:responses[@showValues='true']">
                        <xsl:value-of select="@value"/>
                    </xsl:if>
                </td>
            </tr>
        </xsl:if>
        <!-- Skala -->
        <xsl:if test="boolean(@position)">
        <tr>
            <th class="{if(@type='nonOpinion') then 'nonOpinion' else (if(@position='first') then 'likert_first' else 'likert_last')}">
                <xsl:value-of select="."/>
            </th>
        </tr>
        <xsl:if test="@position='first'">
<xsl:call-template name="likert_loop"/>
        </xsl:if>
            </xsl:if>
        </xsl:for-each>
</xsl:template>
    
    <!-- Template für die unbenannten Skalenpunkte in vertikalen Likert-Skalen -->
    <xsl:template name="likert_loop">
        <!-- Definition verschiedener Parameter -->
        <!-- index: aktueller Wert -->
        <xsl:param name="index" select="0"/>
        <!-- end: Abbruchbedingung der Schleife: Wert aus dem Attribut scalepoints-->
        <xsl:param name="end" select="parent::qml:responses/@scalepoints -1"/>
        <!-- step: dient dazu den index jeweils um eins hochzuzählen -->
        <xsl:param name="step" select="1"/>
        <!-- Erstellung des values, dass den Radiobuttons mitgegeben wird, richtet sich nach den verschiedenen Fällen -->
        <xsl:param name="value" select="
            if (boolean(@value))
            then 
            (
            if(@value &gt;0)
            then
            (
            if(following-sibling::qml:choice[@position='last']/@value &gt;0)
            then 
            (
            if(@value &lt; following-sibling::qml:choice[@position='last']/@value)
            then ((@value)+$index)
            else ((@value)-$index)
            )
            else ((@value)-$index)
            )
            else
            (
            if(following-sibling::qml:choice[@position='last']/@value &gt;0)
            then ((@value)+$index)
            else
            (
            if(abs(@value) &lt; abs(following-sibling::qml:choice[@position='last']/@value))
            then ((@value)-$index)
            else ((@value)+$index)
            )
            )
            )
            else ($index+1)"/>   
            <!-- Ausgabe Radiobutton -->
        <xsl:if test="parent::qml:responses[@orientation='vertical']">
<xsl:text disable-output-escaping="yes">&lt;tr&gt;</xsl:text>
        </xsl:if>
            <th class="likert_scale">
                <input type="radio" name="{ancestor::qml:item/@id}" value="{$value}"/>
                <xsl:if test="parent::qml:responses[@showValues='true']">
                    <xsl:value-of select="$value"/>
                </xsl:if>
            </th>
        <xsl:if test="parent::qml:responses[@orientation='vertical']">
            <xsl:text disable-output-escaping="yes">&lt;/tr&gt;</xsl:text>
        </xsl:if>
        <!-- Template wird so lange aufgerufen, bis alle unbenannten Skalenpunkte ausgegeben sind, die Abbruchbedingung ist über $end codiert -->
        <xsl:if test="$index!=$end">
            <xsl:call-template name="likert_loop">
                <xsl:with-param name="index" select="$index+$step"/>
                <xsl:with-param name="end" select="$end"/>
                <xsl:with-param name="step" select="$step"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- Template für die Wahl der Formularfeldertypen -->
    <xsl:template name="choose_buttons">
                <xsl:choose>
                    <!--Version 1: aktuelles choice ist eine geschlosse Kategorie-->
                    <xsl:when test="@type='closed' or @type='nonOpinion' or not(boolean(@type))">
<xsl:call-template name="closed_choice"/>
                        </xsl:when>
          <!-- Version 2: die aktuelle Kategorie ist offen und die Frage hat mindestens zwei Kategorien-->
                    <xsl:when test="@type='open' and count(parent::qml:responses/qml:choice)&gt;1">
<xsl:call-template name="open_choice"/>
                    </xsl:when>
                    <!-- Version 3: die aktuelle Kategorie ist offen und die Frage hat nur genau diese Antwortmöglichkeit -->
                    <xsl:when test="@type='open' and count(parent::qml:responses/qml:choice)=1">
<xsl:call-template name="only_one_choice"/>
                    </xsl:when>
                </xsl:choose>
    </xsl:template>
    
    <!-- Template für geschlossene Kategorien, Einfach- und Mehrfachnennung -->
    <xsl:template name="closed_choice">
<xsl:choose>
    <!-- Mehrfachnennung -->
    <xsl:when test="parent::qml:responses[@type='multiple']">
<xsl:call-template name="checkbox">
            <xsl:with-param name="name" select="concat(ancestor::qml:item/@id, '_', if(@value) then @value else position())"/>
        </xsl:call-template>
    </xsl:when>
    <!-- Einfachnennung -->
    <xsl:when test="parent::qml:responses[@type='single' or not(boolean(@type))]">
<xsl:call-template name="radiobutton">
    <xsl:with-param name="name" select="ancestor::qml:item/@id"/>
    <xsl:with-param name="value" select="if(@value) then @value else position()"/>
    <xsl:with-param name="onclick" select="concat(&quot;javascript:clearTextFields(&apos;&quot;, ancestor::qml:item/@id, &quot;&apos;)&quot;)"/>
</xsl:call-template>        
    </xsl:when>
</xsl:choose>
    </xsl:template>
    
    <xsl:template name="open_choice">
        <xsl:choose>
            <!-- bei Einfachnennung Aufruf des Template für die Radiobuttons und anschließend des Templates für Textfelder -->
            <xsl:when test="parent::qml:responses[@type='single']">
<xsl:call-template name="radiobutton">
    <xsl:with-param name="value" select="if (@value) then @value else position()"/>
    <xsl:with-param name="name" select="ancestor::qml:item/@id"/>
    <xsl:with-param name="onclick" 
select="concat(&quot;javascript:clearTextFields(&apos;&quot;, ancestor::qml:item/@id, &quot;&apos;, &apos;&quot;, ancestor::qml:item/@id, &quot;o&quot;, if(@value) then @value else position(), &quot;&apos;)&quot;)"/>
</xsl:call-template>
                <xsl:if test="parent::qml:responses[@showValues='true']">
                    <xsl:value-of select="if(@value) then @value else position()"/>
                </xsl:if>
                <xsl:call-template name="textfield">
                    <xsl:with-param name="name" select="concat(ancestor::qml:item/@id, 'o', if(@value) then @value else position() )"/>
                    <xsl:with-param name="onkeydown" 
select="concat(&quot;javascript:setRadioButton(&apos;&quot;, ancestor::qml:item/@id, &quot;&apos;, &apos;&quot;, if(@value) then @value else position(), &quot;&apos;); 
clearTextFields(&apos;&quot;, ancestor::qml:item/@id, &quot;&apos;, &apos;&quot;, ancestor::qml:item/@id, &quot;o&quot;, if(@value) then @value else position(), &quot;&apos;)&quot;)"/>   
                    <xsl:with-param name="onkeyup"
select="concat(&quot;javascript:chklen(&quot;, ancestor::qml:item/@id, &quot;o&quot;, if(@value) then @value else position(),  &quot;, &apos;&quot;, if(@length) then @length else '1000', &quot;&apos;)&quot;)"/>
                </xsl:call-template> 
            </xsl:when>
            <!-- bei Mehrfachnennung, Aufruf des Templates für Textfelder -->
            <xsl:when test="parent::qml:responses[@type='multiple']">
                <xsl:call-template name="textfield">
                    <xsl:with-param name="name" select="concat(ancestor::qml:item/@id, '_o', if(@value) then @value else position())"/>
                    <xsl:with-param name="onkeyup"
                        select="concat(&quot;javascript:chklen(&quot;, ancestor::qml:item/@id, &quot;_&quot;, if(@value) then @value else position(),  &quot;, &apos;&quot;, if(@length) then @length else '1000', &quot;&apos;)&quot;)"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
     
    <xsl:template name="only_one_choice">
        <!-- Aufruf des Templates für Textfelder -->
<xsl:call-template name="textfield">
            <xsl:with-param name="name" select="concat(ancestor::qml:item/@id, 'o', if(@value) then @value else position() )"/>
            <xsl:with-param name="onkeyup"
                select="concat(&quot;javascript:chklen(&quot;, ancestor::qml:item/@id, &quot;o&quot;, if(@value) then @value else position(),  &quot;, &apos;&quot;, if(@length) then @length else '1000', &quot;&apos;)&quot;)"/>
        </xsl:call-template>
    </xsl:template>

        <!-- Template für die Ausgabe der Radiobuttons -->
        <xsl:template name="radiobutton">
            <!-- Definition von Parametern -->
            <xsl:param name="value"/>
            <xsl:param name="onclick"/>
            <xsl:param name="name"/>
            <!-- Ausgabe eines Radiobuttons, Erzeugung der Werte durch Aufruf der Parameter -->
            <input type="radio" name="{$name}" value="{$value}" onclick="{$onclick}"/>
        </xsl:template>
        
        <!-- Template für die Checkboxen -->
        <xsl:template name="checkbox">
            <xsl:param name="name"/>
            <input type="checkbox" name="{$name}"/>
        </xsl:template>    
        
    <xsl:template name="textfield">
        <!-- Definition von Parametern -->
        <xsl:param name="name"/>
        <xsl:param name="onkeydown"/>
        <xsl:param name="onkeyup"/>
        <!-- Abfrage für Größe des Textfeldes -->
        <xsl:choose>
            <!-- vorgegebene Länge der Kategorie im QML-Dokument bis zu 75 Zeichen -->
            <xsl:when test="boolean(@length) and @length&lt;=75">
                <input type="text" class="text" name="{$name}" size="{if (number(@length)&lt;40) then @length else '40'}" maxlength="{@length}" onkeydown="{$onkeydown}"/>
            </xsl:when>
            <!-- mehr als 75 Zeichen oder keine Länge angegeben -->
            <xsl:otherwise>
                <xsl:choose>
                    <!-- wenn es nur eine Kategorie gibt, wird das Textfeld größer -->
                    <xsl:when test="count(parent::qml:responses/qml:choice)=1">
                        <textarea class="textarea" name="{$name}" rows="5" cols="50" onkeydown="{$onkeydown}" onkeyup="{$onkeyup}"/>
                    </xsl:when>
                    <!-- mehrere Antwortalternativen -->
                    <xsl:otherwise>
                        <textarea class="textarea" name="{$name}" rows="3" cols="50" onkeydown="{$onkeydown}" onkeyup="{$onkeyup}"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

   <xsl:template name="differential">
<table class="horizontal">
    <tr>
        <th class="topleft"></th>
        <xsl:for-each select="qml:differentialResponses/qml:choice">
            <th class="answerDifferential">
                <xsl:value-of select="."/>
            </th>
        </xsl:for-each>
        <th class="topleft"></th>
        <xsl:if test="qml:item[1]/qml:responses/qml:choice[@type='nonOpinion' ]">
            <th class="blindHorizontal"></th>
            <th class="topleft"></th>
            <th class="topleft"></th>
            </xsl:if>
    </tr>
    <xsl:for-each select="qml:item">
    <tr>
        <xsl:for-each select="qml:responses/qml:choice">
            <xsl:if test="@type='nonOpinion' and not(boolean(preceding-sibling::qml:choice[@type='nonOpinion']))">
                <th class="blindHorizontal"></th>
                <th class="nonOpinion">
                    <xsl:value-of select="."/>
                </th>
                <th class="likert_scale">
                    <xsl:call-template name="choose_buttons"/>
                    <xsl:if test="parent::qml:responses[@showValues='true']">
                   <xsl:value-of select="@value"/>
                    </xsl:if>
                </th>
            </xsl:if>
            <!-- Skala -->
            <xsl:if test="boolean(@position)">
                <th class="{if(@position='first') then 'likert_first' else 'likert_last'}">
                    <xsl:value-of select="."/>
                </th>
                <xsl:if test="@position='first'">
                    <xsl:call-template name="likert_loop"/>
                </xsl:if>
            </xsl:if>            
        </xsl:for-each>
    </tr>
        </xsl:for-each>
</table>
   </xsl:template>


        
    
    
    
</xsl:stylesheet>
