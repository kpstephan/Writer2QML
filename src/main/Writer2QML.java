/*

 Copyright 2004-2008 Karsten Stephan

 This file is part of Writer2QML.

 Writer2QML is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Writer2QML is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with Writer2QML. If not, see <http://www.gnu.org/licenses/>.

*/


//writer2qml - SWT-version

/*
 * Verwendet JQstnModel
 * Später dieses Modell überarbeiten
 *
 *
 */

package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xml.sax.SAXException;

import model.converter.ODFParagraphsExtractor;
import model.converter.Parapgraphs2SimpleQstnConverter;
import model.simplequestionnaire.Model2QmlConverter;
import model.simplequestionnaire.Qstn;
import model.writerparagraphs.StyleCodingError;
import model.writerparagraphs.TextParagraph;
import model.writerparagraphs.TextParagraphList;




public class Writer2QML {

  //Name der Applikation
  static final String APPNAME = "Writer2QML 1.2";


  //List of paragraph properties read from Libreoffice Writer file
  TextParagraphList textParagraphList = new TextParagraphList();

  //questionnaire
  Qstn qstn;

  //Beinhaltet das QML Konvertierungsergebnis
  private String qmlFileContentString;


  //Lineseparator, Fileseparator und Homedirectory
  String ls, fs, appDir, homeDir;


  //Fenster
  Display display = new Display ();
  Shell shell = new Shell (display);

  //Variablen für Oberflächenelemente hier deklarieren


  //Images
  Image appIcon;
  Image iconOpen;
  Image iconReload;
  Image iconOkEnabled;
  Image iconOkDisabled;

  //Bilder freigeben
  private void disposeImages(){

	  if( appIcon != null)  appIcon.dispose();
	  if( iconOpen != null) iconOpen.dispose();
	  if( iconReload != null) iconReload.dispose();
	  if( iconOkEnabled != null) iconOkEnabled.dispose();
	  if( iconOkDisabled != null) iconOkDisabled.dispose();
  }

  //MenuItems
  MenuItem miExportMessages;
  MenuItem miExportQml;
  MenuItem miExportHtml;

  //Hauptbereiche des Anwendungsfensters
  ToolBar toolBar;
  final Composite clientBereich;
  final Composite statusBar;

  //Toolbuttons
  ToolItem toolButtonOpen;
  ToolItem toolButtonReload;


  Label statusLabel;


  //Im Clientbereich wird alternativ ein tabFolder oder ein helpComposite
  //angezeigt. Die Umschaltung geschieht durch ein Stacklayout.
  final StackLayout clientAreaStackLayout;


  //HelpComposite im CLientbereich
  //HelpPanel wird in zwei Bereiche helpTop und helpBottom unterteilt
  Composite helpPanel;
  Composite helpTop;
  Composite helpBottom;

  Browser helpBrowser;
  Browser htmlBrowser;


  //Kartenreiter im Clientbereich
  TabFolder tabFolder;
  TabItem messageTab;
  TabItem qmlTab;
  TabItem htmlTab;

  //Sashform mit Tree und Text im Kartenreiter "messageText"
  SashForm messageSash;
  //Composite testPanel;
  Tree messageTree;
  StyledText messageText;

  //StyledText für QML Ausgabe in Tab2
  StyledText qmlText;


  //Button
  Button closeHelpButton;



  //Farben
  private Color bgSelected;

  //Eventhandler
  MenuSelected menuSelected1 = new MenuSelected();
  OpenSelected openSelected = new OpenSelected();
  HelpSelected helpSelected = new HelpSelected();
  AboutSelected aboutSelected = new AboutSelected();
  AboutQmlSelected aboutQmlSelected = new AboutQmlSelected();
  ExportMessagesSelected exportMessagesSelected = new ExportMessagesSelected();
  ExportQmlSelected exportQmlSelected = new ExportQmlSelected();
  ExportHtmlSelected exportHtmlSelected = new ExportHtmlSelected();


  CloseHelpSelected closeHelpSelected = new CloseHelpSelected();
  ReloadSelected reloadSelected = new ReloadSelected();

  XmlLineStyleListener xmlLineStyleListener = new XmlLineStyleListener();

  //Konfiguration
  //Muss der absolute Pfad angegeben werden?
  ApplicationSettings settings = new ApplicationSettings("settings.txt");

  //Aktuelle Datei
  File currentFile;

  //Objekt zum Markieren einer Zeile
  SingleLineSelector lineSelector = new SingleLineSelector();

  String urlResultFile;

  //Konstruktor
  public Writer2QML(){

	ls = System.getProperty( "line.separator" );
	fs = System.getProperty( "file.separator" );
	appDir = System.getProperty( "user.dir" );
	homeDir = System.getProperty( "user.home" );

    shell.setLayout(new GridLayout());

    shell.setText( APPNAME );
    shell.setSize(800, 600);

    //Menu
    Menu menuBar = new Menu( shell, SWT.BAR );

    //Menu-File
    MenuItem fileMenuItem = new MenuItem( menuBar, SWT.CASCADE );
    fileMenuItem.setText("Datei");

    Menu fileMenu = new Menu( shell, SWT.DROP_DOWN );

    MenuItem miOpen = new MenuItem( fileMenu, SWT.PUSH );
    miOpen.setText("Writer Datei konvertieren..");
    miOpen.addSelectionListener( openSelected );

    //MenuItem für Untermenu
    MenuItem miExport = new MenuItem( fileMenu, SWT.CASCADE );
    miExport.setText( "Exportieren" );

    //Untermenu
    Menu exportMenu = new Menu ( fileMenu );
    miExport.setMenu( exportMenu );

    //Items im Untermenu
    //ExportMessages
    miExportMessages = new MenuItem( exportMenu, SWT.PUSH );
    miExportMessages.setText("Meldungen Writerimport..");
    miExportMessages.addSelectionListener( exportMessagesSelected );
    miExportMessages.setEnabled( false );

    //Export QML
    miExportQml = new MenuItem( exportMenu, SWT.PUSH );
    miExportQml.setText("QML Ausgabe..");
    miExportQml.addSelectionListener( exportQmlSelected );
    miExportQml.setEnabled( false );

    //Export HTML
    miExportHtml = new MenuItem( exportMenu, SWT.PUSH );
    miExportHtml.setText("HTML Ansicht..");
    miExportHtml.addSelectionListener( exportHtmlSelected );
    miExportHtml.setEnabled( false );


    @SuppressWarnings("unused")
	MenuItem miSep = new MenuItem( fileMenu, SWT.SEPARATOR );

    MenuItem miExit = new MenuItem( fileMenu, SWT.PUSH );
    miExit.setText("Beenden");
    miExit.addSelectionListener( menuSelected1 );

    fileMenuItem.setMenu( fileMenu );


    //Hilfe-Menu
    MenuItem helpMenuItem = new MenuItem( menuBar, SWT.CASCADE );
    helpMenuItem.setText("Hilfe");

    Menu helpMenu = new Menu( shell, SWT.DROP_DOWN );

    MenuItem miHelp = new MenuItem( helpMenu, SWT.PUSH );
    miHelp.setText("Hilfe");
    miHelp.addSelectionListener( helpSelected );

    MenuItem miQml = new MenuItem( helpMenu, SWT.PUSH );
    miQml.setText("über das QML-Projekt");
    miQml.addSelectionListener( aboutQmlSelected );


    MenuItem miAbout = new MenuItem( helpMenu, SWT.PUSH );
    miAbout.setText("Über writer2qml");
    //miAbout.addSelectionListener( menuSelected1 );
    miAbout.addSelectionListener( aboutSelected );

    helpMenuItem.setMenu( helpMenu );


    shell.setMenuBar( menuBar );


    //Hauptbereiche des Anwendungsfensters
    //coolbar = new CoolBar( shell, SWT.NONE );
    toolBar = new ToolBar( shell, SWT.HORIZONTAL );
    clientBereich = new Composite(shell, SWT.BORDER);
    statusBar = new Composite(shell, SWT.NONE);


    //Icons
    try{
      appIcon = new Image( shell.getDisplay(), new FileInputStream( appDir + fs + "images" + fs + "writer2qml-32x32.png"));
      iconOpen = new Image( shell.getDisplay(), new FileInputStream( appDir + fs + "images" + fs + "fileOpen-24x24.png"));
      iconReload = new Image( shell.getDisplay(), new FileInputStream(appDir + fs + "images" + fs + "reload-24x24.png"));
      iconOkEnabled = new Image( shell.getDisplay(), new FileInputStream(appDir + fs + "images" + fs + "grnball-10x10.gif"));
      iconOkDisabled = new Image( shell.getDisplay(), new FileInputStream(appDir + fs + "images" + fs + "gryball-10x10.gif"));


    } catch (IOException e){
    	//Image beim Start nicht gefunden:
    	//irgendwie schlau behandeln
    	MessageBox messageBox = new MessageBox( shell, SWT.ICON_INFORMATION | SWT.OK );
		messageBox.setText("Fehler");
		messageBox.setMessage("Datei nicht gefunden \n homeDir");
		messageBox.open();
    }

    //Applicationsicon setzen
    shell.setImage( appIcon );


    //Toolbar Elemente
    toolButtonOpen = new ToolItem( toolBar, SWT.PUSH );
    //toolButtonOpen.setText( "Öffnen" );
    toolButtonOpen.setImage( iconOpen );
    toolButtonOpen.setToolTipText("Writerdatei öffnen");
    toolButtonOpen.addSelectionListener( openSelected );

    toolButtonReload = new ToolItem( toolBar, SWT.PUSH );
    //toolButtonReload.setText( "Reload" );
    toolButtonReload.setImage( iconReload );
    toolButtonReload.setToolTipText("Aktuelle Writerdatei wieder einlesen");
    toolButtonReload.addSelectionListener( reloadSelected );



    //Unterer Clientbereich für ScrolledCompsite und Composite
    clientBereich.setLayoutData(new GridData(GridData.FILL_BOTH));


    //Layout für Clientbereich
    //Im Clientbereich wird alternativ ein tabFolder oder ein helpComposite
    //angezeigt. Die Umschaltung geschieht durch ein Stacklayout.
    clientAreaStackLayout = new StackLayout();
    clientBereich.setLayout( clientAreaStackLayout );


    //HelpPanel
    //helpTop enthält die helpTop zur ANzeige der Hilfe und helpBottom
    //Layout?
    helpPanel = new Composite( clientBereich, SWT.NONE );
    helpPanel.setLayout(new GridLayout());

    helpTop = new Composite( helpPanel, SWT.BORDER );
    helpTop.setLayoutData(new GridData(GridData.FILL_BOTH));
    helpTop.setLayout( new FillLayout() );

    helpBottom = new Composite( helpPanel, SWT.BORDER );
    helpBottom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    helpBottom.setLayout(new FillLayout() );

    helpBrowser = new Browser( helpTop, SWT.NONE );

    closeHelpButton = new Button( helpBottom, SWT.PUSH );
    closeHelpButton.setText("Hilfe Schließen");
    closeHelpButton.addSelectionListener( closeHelpSelected );


    //Kartenreiter im Clientbereich
    tabFolder = new TabFolder( clientBereich, SWT.NONE);

    //messageTab
    messageTab = new TabItem( tabFolder, SWT.NONE);
    messageTab.setText( "Meldungen Writerimport" );

    //QML-Tab
    qmlTab = new TabItem( tabFolder, SWT.NONE);
    qmlTab.setText(" QML Ausgabe");
    qmlTab.setImage( iconOkDisabled );


    //Html-Tab
    htmlTab = new TabItem( tabFolder, SWT.NONE);
    htmlTab.setText(" HTML Ansicht");
    htmlTab.setImage( iconOkDisabled );


    //html-Browser
    htmlBrowser = new Browser( tabFolder, SWT.BORDER );
    htmlTab.setControl( htmlBrowser );


    //Sashform mit Tree und Text im Kartenreiter "messageText"
    messageSash = new SashForm(tabFolder, SWT.HORIZONTAL );


    //Treeview
    //Sollte in ein Scrolled Composite!
    messageTree = new Tree( messageSash, SWT.SINGLE | SWT.BORDER );


    //Listener für Treeview, später als eigenes Objekt deklarieren
    //SelectionListener für TreeView erzeugen.
    //Bei Click auf treeItem wird (Integer) getData gelesen und
    //Die entsprechende Zeile im messageText angesprungen
    messageTree.addSelectionListener( new SelectionListener(){

  	public void widgetDefaultSelected(SelectionEvent e) { }

  	public void widgetSelected(SelectionEvent e) {
  		//Vor Umwandlung jeweils absichern?
  		TreeItem item = (TreeItem) e.item;
  		if( item.getData() != null ){

            int er = ( (Integer) item.getData() ).intValue();

            //Zeile Markieren
            lineSelector.selectLine( er - 1 );

  		    //Springe zur Zeile
  			int gotoLine = er -4;
  			if (gotoLine < 0 ) gotoLine = 0;

  			messageText.setTopIndex( gotoLine );


  		}

  	}

    });



    //Textfeld zur Anzeige von Medungen beim Import
    messageText = new StyledText( messageSash, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
    messageText.setEditable( false );

    //Verhältnis Tree - Text
    messageSash.setWeights(new int[] {1, 2});


    //Sash mit messageTree und messageText in Message-Tab
    messageTab.setControl( messageSash );



    //Textfeld für Anzeige der QML-Datei im QML-Tab
    qmlText = new StyledText( tabFolder, SWT.V_SCROLL | SWT.H_SCROLL |  SWT.BORDER);
    qmlText.setEditable( false );
    qmlText.addLineStyleListener( xmlLineStyleListener );
    qmlTab.setControl( qmlText );


    //Control auswählen, welches im Clientbereich angezeigt wird.
    clientAreaStackLayout.topControl = tabFolder;
    //clientAreaStackLayout.topControl = helpPanel;



    //Statusbar
    //final Composite statusBar = new Composite(shell, SWT.NONE);
    statusBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    statusBar.setLayout(new FillLayout());

    statusLabel = new Label( statusBar,SWT.NONE );


    //Farben
    bgSelected = display.getSystemColor( SWT.COLOR_LIST_SELECTION );
    //bgSelected = display.getSystemColor( SWT.COLOR_LIST_BACKGROUND);



    //nicht im Konstruktor, procedure run
    shell.open ();
    while (!shell.isDisposed ()) {
        if (!display.readAndDispatch ()) display.sleep ();
    }

    settings.saveToFile();
    bgSelected.dispose();
    disposeImages();
    display.dispose ();

  } //Konstruktor


  private void setStatusMessage( String message ){

	  statusLabel.setText( message );
  }


  //Listener XML-Syntaxhighlighting in der QML-Ansicht
  public class XmlLineStyleListener implements LineStyleListener{

	  public void lineGetStyle(LineStyleEvent event) {

		  ArrayList<StyleRange> styles = new ArrayList<StyleRange>();

		  //Farben für Syntax-Highlighting
		  Color red = display.getSystemColor( SWT.COLOR_DARK_RED );
		  Color green = display.getSystemColor( SWT.COLOR_DARK_GREEN );

		  StyleRange style;

		  String line = event.lineText;
		  int lineOffset = event.lineOffset;

		  //Reguläre Ausdrücke zum Finden der XML-Bestandteile
	      String elemenNamePatternStr = "<(/)?qml:(\\w)+";
		  String elementGtPatternStr = ">";
		  String attribNamePatternStr = "(\\w)+=";
		  String attribContentPatternStr = "\"(\\w)*\"";

          //Vorverarbeitung der RegEx
		  //Performanter außerhalb dieser Routine
		  Pattern elementNamePattern = Pattern.compile( elemenNamePatternStr );
		  Pattern elementGtPattern = Pattern.compile( elementGtPatternStr );
		  Pattern attributeNamePattern = Pattern.compile( attribNamePatternStr );
		  Pattern attributeContentPattern = Pattern.compile( attribContentPatternStr );


		  //Element Start '<qml:xyz'
		  Matcher elementNameMatcher = elementNamePattern.matcher( line );
		  while( elementNameMatcher.find() ){
			  style = new StyleRange();
			  style.start = lineOffset + elementNameMatcher.start();
			  style.length = (elementNameMatcher.end() - elementNameMatcher.start() );
			  style.fontStyle = SWT.BOLD;
			  styles.add( style );
		  }

		  //Element End '>'
		  Matcher elementGtMatcher = elementGtPattern.matcher( line );
		  while( elementGtMatcher.find() ){
			  style = new StyleRange();
			  style.start = lineOffset + elementGtMatcher.start();
			  style.length = (elementGtMatcher.end() - elementGtMatcher.start());
			  style.fontStyle = SWT.BOLD;
			  //styles.add( style );
			  //System.out.println("start: " + (lineOffset + elementGtMatcher.start()) + "len: " + (elementGtMatcher.end() - elementGtMatcher.start()));
		  }


		  //Attribute Name 'xy='
		  Matcher attributeNameMatcher = attributeNamePattern.matcher( line );
		  while( attributeNameMatcher.find() ){
			  style = new StyleRange();
			  style.start = lineOffset + attributeNameMatcher.start();
			  style.length = attributeNameMatcher.end() - attributeNameMatcher.start();
			  style.foreground = red;
			  styles.add( style );
		  }

		  //Attribute Value '"abc"'
		  Matcher attributeContentMatcher = attributeContentPattern.matcher( line );
		  while( attributeContentMatcher.find() ){
			  style = new StyleRange();
			  style.start = lineOffset + attributeContentMatcher.start();
			  style.length = attributeContentMatcher.end() - attributeContentMatcher.start();
			  style.foreground = green;
			  styles.add( style );

		  }


		  //Styles für die Zeile setzen
		  event.styles = (StyleRange[]) styles.toArray( new StyleRange[ 0 ]);

	  } //lineGetStyle()
  } // XmlLineStyleListener



  final class OpenSelected implements SelectionListener{

    public void widgetDefaultSelected(SelectionEvent arg0) {	}
	public void widgetSelected(SelectionEvent arg0) {

		//Datei Öffnen
		FileDialog dlg = new FileDialog( shell, SWT.OPEN );
		String[]s = new String[ 2 ];
		s[ 0 ] = "OpenOffice.org Writer Dateien (*.odt)";
		s[ 1 ] = "Alle Dateien (*.*)";
		dlg.setFilterNames( s );
		dlg.setFilterExtensions( new String[]{"*.odt", "*.*" });

		//Letzten verwendeten Pfad holen
		String filterPath = settings.getValue( "lastWriterDir" );
		if( filterPath != null ) dlg.setFilterPath( filterPath );
		else dlg.setFilterPath( homeDir );


		String fileName = dlg.open();
		if(fileName != null ){

			//letzes Verzeichnis speichern
			settings.setValue("lastWriterDir", dlg.getFilterPath());

			//Anzeige aktualisieren
			//

			//Bestehende Ergebnisansichten zurücksetzen
			clearResultViews();
			//Writer Datei Konvertieren
			convertWriterFile( fileName );

		}
	}
  } //OpenSelected


  //Export Messages wurde ausgewählt
  final class ExportMessagesSelected implements SelectionListener{
	  public void widgetDefaultSelected(SelectionEvent arg0) {}
	  public void widgetSelected(SelectionEvent arg0) {

		  //Dialog Datei Speichern
		  FileDialog dlg = new FileDialog( shell, SWT.SAVE );

		  //Titel
		  dlg.setText("Meldungen Writerimport Exportieren");

		  //Dateifilter
		  String[]s = new String[ 2 ];
		  s[ 0 ] = "Text Dateien (*.txt)";
		  s[ 1 ] = "Alle Dateien (*.*)";
		  dlg.setFilterNames( s );
		  dlg.setFilterExtensions( new String[]{"*.txt", "*.*" });

		  //DateiName
		  if( currentFile != null ) dlg.setFileName( currentFile.getName() + ".txt" );

		  //Startverzeichnis des Dialogs
		  //Letzten Verwendeten Pfad holen
		  String filterPath = settings.getValue( "lastMessageTextDir" );
		  if( filterPath != null ){
			  //warum funktioniert das nicht?
			  dlg.setFilterPath( filterPath );
			  //System.out.println("qmldir: " + filterPath );
		  }
		 //ansonsten home des users
		 //warum funktioniert das nicht?
		 else{
			 dlg.setFilterPath( homeDir );
		     //System.out.println("dialogDir: " + dlg.getFilterPath());
		 }



		  String fileName = dlg.open();
		  if(fileName != null ){

			  //Verwendetes Verzeichnis speichern
			  settings.setValue( "lastMessageTextDir", dlg.getFilterPath());

			  String messageContentString = messageText.getText();

			  if( messageContentString.length() > 0 ){


				  //System.out.println("fileName: " + fileName );
				  File outFile = new File( fileName );

				  //Existiert Datei?
				  if( ! outFile.exists() ){
					  Utilities.writeStringtoIsoFile( fileName, messageContentString );
				  }
				  //Datei existiert bereits
				  else{
					  //Dialog zum Nachfragen
					  MessageBox mb = new MessageBox( shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO );
					  mb.setText( "Datei Überschreiben?" );
					  mb.setMessage( "Die Datei " + fileName + "existiert bereits." +
							  "möchten Sie die Datei Überschreiben?");
					  int res = mb.open();

					  if( res == SWT.YES ) Utilities.writeStringtoIsoFile( fileName, messageContentString );


				  }



			  } //Content zum Speichern vorhanden

		  }  //Dateiname wurde ausgewählt

	  } //Selected

  }//Listener



  //Export QML wurde ausgewählt
  final class ExportQmlSelected implements SelectionListener{
	  public void widgetDefaultSelected(SelectionEvent arg0) {}


	  public void widgetSelected(SelectionEvent arg0) {

		  //Dialog Datei Speichern
		  FileDialog dlg = new FileDialog( shell, SWT.SAVE );

		  //Titel
		  dlg.setText("QML Datei Exportieren");

		  //Dateifilter
		  String[]s = new String[ 2 ];
		  s[ 0 ] = "QML Dateien (*.xml)";
		  s[ 1 ] = "Alle Dateien (*.*)";
		  dlg.setFilterNames( s );
		  dlg.setFilterExtensions( new String[]{"*.xml", "*.*" });

		  //DateiName
		  if( currentFile != null ) dlg.setFileName( currentFile.getName() + ".xml" );

		  //Startverzeichnis des Dialogs
		  //Letzten Verwendeten Pfad holen
		  String filterPath = settings.getValue( "lastQmlDir" );
		  if( filterPath != null ){
			  //warum funktioniert das nicht?
			  dlg.setFilterPath( filterPath );
			  //System.out.println("qmldir: " + filterPath );
		  }
		 //ansonsten home des users
		 //warum funktioniert das nicht?
		 else dlg.setFilterPath( homeDir );



		  String fileName = dlg.open();
		  if(fileName != null ){

			  //Verwendetes Verzeichnis speichern
			  settings.setValue( "lastQmlDir", dlg.getFilterPath());

			  if( qmlFileContentString != null){


				  System.out.println("fileName: " + fileName );
				  File outFile = new File( fileName );

				  //Existiert Datei?
				  if( ! outFile.exists() ){
					  Utilities.writeStringtoIsoFile( fileName, qmlFileContentString);
				  }
				  //Datei existiert bereits
				  else{
					  //Dialog zum Nachfragen
					  MessageBox mb = new MessageBox( shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO );
					  mb.setText( "Datei Überschreiben?" );
					  mb.setMessage( "Die Datei " + fileName + "existiert bereits." +
							  "möchten Sie die Datei Überschreiben?");
					  int res = mb.open();

					  if( res == SWT.YES ) Utilities.writeStringtoIsoFile( fileName, qmlFileContentString);


				  }

			      //QML-Datei exportieren
			      //String in Datei schreiben, Mit outputStreamWriter Zeichenkodierung vornehmen


			  } //Content zum Speichern vorhanden

		  }  //Dateiname wurde ausgewählt

	  } //Selected

  }//Listener




  //Export HTML wurde ausgewählt
  final class ExportHtmlSelected implements SelectionListener{
	  public void widgetDefaultSelected(SelectionEvent arg0) {}

	  public void widgetSelected(SelectionEvent arg0) {

		  String destDirName;

		  //Dialog Datei Speichern
		  DirectoryDialog dlg = new DirectoryDialog( shell );
		  dlg.setText("HTML-Ansicht Exportieren");
		  String dlgDir = dlg.open();

		  //HTML-Dateien exportieren
		  if(dlgDir != null){

			  //Name des Zielverzeichnisses aus Dialogauswahl +
			  //Name der aktuell geladenen Datei zusammenbauen
			  if(currentFile != null){

			      destDirName = dlgDir + fs + currentFile.getName() + "-html-files";
			      System.out.println( "DD: " + destDirName );

			      //destDir anlegen
			      File destDir = new File( destDirName );


			      //Hier evtl fragen, ob Datei überschrieben werden soll
			      /*
			        Dialog zum Nachfragen
					MessageBox mb = new MessageBox( shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO );
					mb.setText( "Datei Überschreiben?" );
					mb.setMessage( "Die Datei " + destHtmlFileName + "existiert bereits." +
					  "möchten Sie die Datei Überschreiben?");
					int res = mb.open();
					if( res == SWT.YES ){}

			       */

			      if( ! destDir.exists()) destDir.mkdir();

		          String xsltDir = appDir + fs + "xslt";
		          String sourceFileName, destFileName;
			      //Alte Dateien aus vorherigen Transformationen löschen
			      String[] dir = new File( xsltDir ).list();
			      for( int i=0; i < dir.length; i++ ){
				      //System.out.print( xsltDir + fs + dir[i]);
				      if( ( dir[i].equals("index.html")) || (dir[i].startsWith("out")) || ( dir[i].equals("style.css")) ){

					      //Datei Kopieren
				          sourceFileName = xsltDir + fs + dir[ i ];
				          destFileName = destDirName + fs + dir[ i ];
				          Utilities.copyFile( new File(sourceFileName), new File(destFileName) );

				      } //Datei soll kopiert werden

			     } // Alle Dateien des Verzeichnisses

			 } // Name der aktuell konvertierten Datei existiert

		  } // User hat Verzeichnis ausgewählt



	  } //Selected

  }//Listener





  //Hilfe wurde ausgewählt
  final class HelpSelected implements SelectionListener{

	public void widgetDefaultSelected(SelectionEvent arg0) {}

	public void widgetSelected(SelectionEvent arg0) {

        //TDO Auf jeder Palttform testen, ob Seite geladen wird.

		//direkt an Browser übergeben
		String helpFile = "file://" + System.getProperty("user.dir") + "/help/de/help.html";

        //Hilfeseite laden
		//helpBrowser.setText("<html><body><h2>Writer2QML</h2>Hilfe ...</body></html>");
		helpBrowser.setUrl( helpFile );


		//Hilfspanel anzeigen
		clientAreaStackLayout.topControl = helpPanel;
	    clientBereich.layout();

	}

  } //HelpSelected


//Über wurde ausgewählt
  final class AboutSelected implements SelectionListener{

	public void widgetDefaultSelected(SelectionEvent arg0) {}

	public void widgetSelected(SelectionEvent arg0) {

        //TDO Auf jeder Palttform testen, ob Seite geladen wird.

		//direkt an Browser übergeben
		String helpFile = "file://" + System.getProperty("user.dir") + "/help/de/about.html";

        //Hilfeseite laden
		//helpBrowser.setText("<html><body><h2>Writer2QML</h2>Hilfe ...</body></html>");
		helpBrowser.setUrl( helpFile );


		//Hilfspanel anzeigen
		clientAreaStackLayout.topControl = helpPanel;
	    clientBereich.layout();

	}

  } //AboutSelected


//Über wurde ausgewählt
  final class AboutQmlSelected implements SelectionListener{

	public void widgetDefaultSelected(SelectionEvent arg0) {}

	public void widgetSelected(SelectionEvent arg0) {

        //TDO Auf jeder Palttform testen, ob Seite geladen wird.

		//direkt an Browser übergeben
		String helpFile = "file://" + System.getProperty("user.dir") + "/help/de/about-qml.html";

        //Hilfeseite laden
		//helpBrowser.setText("<html><body><h2>Writer2QML</h2>Hilfe ...</body></html>");
		helpBrowser.setUrl( helpFile );


		//Hilfspanel anzeigen
		clientAreaStackLayout.topControl = helpPanel;
	    clientBereich.layout();

	}

  } //AboutQmlSelected;



  //Hilfe Schließen wurde ausgewählt
  final class CloseHelpSelected implements SelectionListener{

	public void widgetDefaultSelected(SelectionEvent arg0) {}

	public void widgetSelected(SelectionEvent arg0) {

        //Tabfolder anzeigen
		clientAreaStackLayout.topControl = tabFolder;
	    clientBereich.layout();

	}

  } //HelpSelected




//Reload wurde ausgewählt
  final class ReloadSelected implements SelectionListener{

	public void widgetDefaultSelected(SelectionEvent arg0) {}

	public void widgetSelected(SelectionEvent arg0) {
		if( currentFile != null ){

			//Bestehende Ergebnisansichten zurücksetzen
			clearResultViews();

			if( currentFile.exists() ){
				convertWriterFile( currentFile.toString() );
			}

		}
	}

  } //ReloadSelected



  final class MenuSelected implements SelectionListener{

	public void widgetDefaultSelected(SelectionEvent e) {}

	public void widgetSelected(SelectionEvent e) {

	  String selectedItem = ((MenuItem)e.getSource()).getText();


	  //Beenden wurde ausgewählt
	  if( selectedItem.equals("Beenden")){
		//if(handleChangesBeforeDiscard()){} ?? Jackwind 201
		shell.dispose();
	  }


	}
  }//MenuSelected



  private void clearResultViews(){

    //Qml String löschen
	qmlFileContentString = "";

	//messageText + styleranges löschen
	//replaceStyleRanges(int start, int length, StyleRange[] ranges)

	//Titelzeile Aktualisieren
	shell.setText( APPNAME );

	//Alte QML-Ansicht löschen
	qmlText.setText( "" );
	qmlTab.setImage( iconOkDisabled );

	//Alte HTML-Ansicht löschen
	htmlBrowser.setText( "" );
	htmlTab.setImage( iconOkDisabled );

	//Tabfolder anzeigen
	clientAreaStackLayout.topControl = tabFolder;
    clientBereich.layout();

	//Tab "Meldungen Writerimport" anzeigen
	tabFolder.setSelection( 0 );

	//MenuItems deaktivieren
    miExportMessages.setEnabled( false );
    miExportQml.setEnabled( false );
    miExportHtml.setEnabled( false );


 }


  public class UpdateHtmlViewRunnable implements Runnable{

	/*
	 * Ergebnis richtig prüfen !
	 * Endlsoviech vermeiden !
	 */


	public void run() {


		//Transformation gibt result-Datei plus Dateien index.html [ + Dateien out2.html bis outN.html ] aus
		final String urlQstnFile = "file://" + appDir + fs + "xslt" + fs + "index.html";
        String xsltDir = appDir + fs + "xslt";

		//Alte Dateien aus vorherigen Transformationen löschen
		String[] dir = new File( xsltDir ).list();
		for( int i=0; i < dir.length; i++ ){
			//System.out.print( xsltDir + fs + dir[i]);
			if( ( dir[i].equals("index.html")) || (dir[i].startsWith("out")) ){
				//System.out.print( " --Ja--" );
				//Datei löschen
			    File file = new File( xsltDir + fs + dir[i]);
			    if( file.exists() ) file.delete();
			}
			//System.out.println( "" );
		 }





	    ProcessBuilder builder = new ProcessBuilder( "java", "-jar", "saxon9.jar", "-o", "out.null", "in.xml", "in.xsl" );
	    builder.directory( new File( xsltDir ) );
	    int exitValue=-1;
	    try{
	      Process p = builder.start();
	      exitValue = p.waitFor();
	    }catch( IOException e ){
	  	    System.out.println("nix xslt");
	  	    e.printStackTrace();
	    }catch (InterruptedException e){
		    System.out.println("Prozess unterbrochen");
	    }

	    //System.out.println( "Wert: " + exitValue );


	    //Bei Erfolg Ansicht aktualisieren

	    //Funktioniert evtl. nicht, Testen. Erfolg auf andere Weise feststellen:
	    //stderror abfragen oder überprüfen, ob Datei erzeugt wurde
	    if (exitValue == 0 ){

	    	//SWT UI Elemente können nicht in einem Nebenthread
	    	//nur über display.asyncExec angesprochen werden.
	    	//http://help.eclipse.org/help31/index.jsp?topic=/org.eclipse.platform.doc.isv/guide/swt_threading.htm
	        display.asyncExec (new Runnable () {
	    	    public void run () {
	    	    	if (! shell.isDisposed()){
	    	    	    htmlBrowser.setUrl( urlQstnFile );
	    	    	    htmlTab.setImage( iconOkEnabled );
	    	    	    //Menu aktualisieren
	    		        miExportHtml.setEnabled( true );
	                }
	    	    }
	    	});



	    	//display.asyncExec( new UpdateHtmlViewRunnable() );

	    }

	    //System.out.println( "url: " + urlResultFile );


	}


  } //updateHtmlView();








  private class SingleLineSelector{

	  private int selectedLine = -1;
	  private int lineOffset;
	  private int lineCharCount;
	  private StyleRange[] ranges;

	  public void selectLine( int lineIndex ){

		  //Alte Zeilenmarkierung löschen
		  if( selectedLine != -1 ){

			  //Nur erster StyleRangem der unten gelesen wird, wird benötigt
			  //Was ist das für ein Unsinn, unten Styleranges richtig lesen
			  StyleRange[] firstRange = new StyleRange[1];
			  firstRange[0] = ranges[0];
			  messageText.setLineBackground( selectedLine , 1, display.getSystemColor( SWT.COLOR_WHITE ));
			  messageText.replaceStyleRanges(lineOffset, lineCharCount, firstRange );
		  }


		  //Neue Zeile Markieren
		  selectedLine = lineIndex;
		  lineOffset = messageText.getOffsetAtLine( lineIndex );
		  lineCharCount = ( messageText.getOffsetAtLine( lineIndex + 1 ) - lineOffset )  ;

		  //Aktuelle Styleranges zur späteren Wiederherstellung speichern
		  ranges = messageText.getStyleRanges( lineOffset, lineCharCount );


		  //Hintergrundfarbe der Zeile auf "markiert" setzen
		  messageText.setLineBackground( lineIndex , 1, bgSelected );

		  //Textfarbe weiß
	      StyleRange sr = new StyleRange();
		  sr.start = lineOffset;
		  sr.length = lineCharCount;
		  sr.background = bgSelected;
		  sr.foreground = display.getSystemColor( SWT.COLOR_WHITE );
		  messageText.setStyleRange( sr );

		  //Darstellung messageText aktualisieren
		  messageText.redraw();

	  }


  }




  public void convertWriterFile(String fileName ){

	  //Letzte geöffnete Datei speichern
	  settings.setValue("lastOpenedFile", fileName);
	  currentFile = new File( fileName );
	  String xmlFileName = fileName + ".xml";

	  //Shelltitel Aktualisieren
	  //Evtl. Pfad oder Pfadfragement anzeigen
	  String titleText = APPNAME + " - " + currentFile.getName();
	  if ( ! shell.getText().equals (titleText )) shell.setText( titleText );


	  //In der derzeitigen Version des Messageviewers
	  //wird nur eine Datei gleichzeitig angezeigt
	  //Kein MVC, vor Erweiterungen Ändern
	  //Tree und Styledtext leeren
	  messageTree.removeAll();


	  //Styleranges im StyledText Löschen um später Darstellungsfehler zu vermeiden
	  //wie funktioniert das?
	  //messageText.replaceStyleRanges( 0, messageText.getCharCount()-1, new StyleRange[ 0 ] );

	  messageText.setText("");


	  /* The logic should be structured like this:
		 *
		 * - read paragraphs
		 * - check paragraphs
		 * - convert paragraphs to qstn
		 * - convert qstn to simple_qml
		 * - convert simple_qml to simle_html_questionnaire
		 *
		 * - NEW 1: convert paragraphs to Zofar-QML
		 * - NEW 2: convert QML to clickable Zofar questionnaire
		 *
		 */


	/** read paragraphs **/


	  try {

		textParagraphList = ODFParagraphsExtractor.extractParagraphsFromODF(fileName);

	} catch (IOException | ParserConfigurationException | SAXException e1) {

		//TODO handle ParserConfigurationException properly
		//e1.printStackTrace();
		//Datei ist keine Gültige ODT-Datei
		//Kein ZIP oder kein content/style.xml oder content-style nicht wohlgeformt
		 MessageBox messageBox = new MessageBox( shell, SWT.ICON_ERROR );
		 messageBox.setText( "Writer2QML: Fehler" );
		 messageBox.setMessage( "Die ausgewählte Datei ist keine gültige OpenOffice.org Writer Datei" );
		 messageBox.open();

		 return;
	}


	   /** check paragraphs **/
       boolean paragraphListIsValid = textParagraphList.checkParStyles();



	   /** convert paragraph list to zofar qml (the convert method is not implemented yet  ) **/
	   if (paragraphListIsValid){
		  //Paragraphs2ZofarQmlConverter.convertToZofarQml(textParagraphList);
	   }




	   /*********  convert paragraph list to simple questionnaire **********/
	   //q&d, move codingError property and method W2qOodataReader.checkParStyles() to TextParagraphList
	   if (paragraphListIsValid){
		   qstn = Parapgraphs2SimpleQstnConverter.convertToSimpleQuestionnaire(textParagraphList);
	   }




	   /********* Menu aktualisieren **********/

       miExportMessages.setEnabled( true );



	   /********************************* Modell nach QML Konvertieren *************************************/
	   //Wenn keine Fehler aufgetreten sind konvertieren
	   if( paragraphListIsValid ){

		   //Modell nach QML KOnvertieren
		   //fileName hat keine Bedeutung
		   //Model2QmlConverter converter = new Model2QmlConverter( handler.getQstn(), new File( xmlFileName ));
		   Model2QmlConverter converter = new Model2QmlConverter( qstn, new File( xmlFileName ));
		   StringBuffer qml = converter.convert();

		   //Ergebnis in anzeigen
		   qmlFileContentString = qml.toString();
		   qmlText.setText( qmlFileContentString );

		   //Ergebnis als Datei speichern
		   //Hier Unicode unten iso!!
		   //Direkt Viewer übergeben?
		   //direkt ohne BufferedWriter schreiben?
		   BufferedWriter f;
		   try{
			   f = new BufferedWriter( new FileWriter( xmlFileName ) );
			   f.write( qmlFileContentString );
			   f.close();

		   }catch( IOException e){
			   //Fehler behandeln
		   }



		   //Speichern in datei und
		   //Konvertieren der Ausgabe nach ISO

		   //Ergebnis als Datei für XSLT speichern

		   String xslXmlFileName =  appDir + fs + "xslt" + fs + "in.xml";

		   //String in Datei schreiben, Zeichenkodierung ISO
		   Utilities.writeStringtoIsoFile( xslXmlFileName, qmlFileContentString);

		   //UI aktualisieren
		   qmlTab.setImage( iconOkEnabled );
		   miExportQml.setEnabled( true );



	   } //Konvertieren des Modells


	   //Statusmeldung
	   if( paragraphListIsValid ){
		   setStatusMessage( "Ausgabe gespeichert: " + xmlFileName );
	   }else setStatusMessage( "" );





	   /******************************** QML nach Html konvertieren ***************************************/



	   //Update HTML-View
	   //Achtung: Nur wenn vorher alles geklappt hat.
	   //Dateiexport exceptions etc. behandeln mit hatAllesGeklappt Flag
	   //Wenn keine Fehler aufgetreten sind Ergebnisdatei nach HTML Umwandeln
	   if( paragraphListIsValid ){
		   //display.asyncExec( new UpdateHtmlViewRunnable() );
		   Thread thread = new Thread( new UpdateHtmlViewRunnable() );
		   thread.start();
	   }


	   /********************************* Ausgabe der Absätze ****************************************/
	   //Später als eigene Funktion updateMessageView()
	   //Insgesamt convertWriterFile() neu strukturieren


	   /*
	    * Im folgenden werden die Fehler ausgegeben: wie funktioniert das
	    * Quelle sind zwei Listen.
	    * codingErrors enthält Fehlerobjekte ( Zeile, Fehlermeldung )
	    * writerContentBuffer enthält Absatzobjekte ( Zeile, Typ und Text )
	    * Die Fehler werden als TreeNodes in einem Tree,
	    * die Absätze als Zeilen in StyledText ausgegeben
	    *
	    * Für jeden Absatz existiert maximal ein Fehlerobjekt. Die Fehlerobjekte sind
	    * anhand der betroffenen Absatznummern in aufsteigender Reihenfolge geordnet.
	    *
	    * Der folgende Code iteriert über alle Absätze, wenn der Absatz mit einem
	    * Fehler verbunden ist (Fehler.Absatznummer = Absatz.nummer), wir ein "Fehlerknoten"
	    * in den Tree eingefügt. Der Knoten enthält ein Integer Datenobjekt mit der
	    * Zeilennummer des Fehlers im StyledText. Die entsprechende Zeile im StyledText
	    * wird markiert.
	    *
	    * Aus Performancegründen wird das häufige Aktualisieren des StyledText
	    * vermieden.Der Text wird in einem Stringbuffer, die Styleranges in einer Arraylsit
	    * zwischengesprichert und am Ende aktualisiert
	    *
	    */




       //Überschrift mit Name der Datei
	   String heading = "Datei: " + "\"" + currentFile.getName() + "\"";
	   messageText.append( ls );
	   int parStyleOffset = messageText.getCharCount();
	   messageText.append( heading + ls + ls );
	   //Überschrift Fett darstellen
	   StyleRange sr = new StyleRange();
	   sr.start = parStyleOffset;
	   sr.length = heading.length();
	   sr.fontStyle = SWT.BOLD;
	   messageText.setStyleRange( sr );


	   //Aktualisierung des StyledText unterdrücken
	   messageText.setRedraw( false );



	   //Fehler als Treenodes Ausgeben
	   //Root-Knoten in Baum einfügen
	   TreeItem rootNode = new TreeItem( messageTree, SWT.NONE );
	   rootNode.setText( currentFile.getName() );
	   rootNode.setExpanded( true );


	   //Keine Fehler vorhanden: Diese Information als Baumknoten ausgeben
	   if( paragraphListIsValid ){
	       TreeItem node = new TreeItem( rootNode, SWT.NONE );
		   node.setText("QML-Datei gespeichert");
		   node.setImage( iconOkEnabled );
		   node.setExpanded( true );
		   //Warum funktioniert das nicht?
		   //node.getParent().notifyListeners (SWT.Expand, null);
		   //Zeigt Item, natürlich selektiert
		   //messageTree.setSelection( node );
		   //Zeigt Item, "wandert" der Baum wenn er nicht ins feld passt?
		   messageTree.showItem( node );
       }


	   //Sonderfall: Datei enthält keine Absätze aber Fehler
	   if( (! paragraphListIsValid) && ( textParagraphList.isEmpty() ) ){

		   //Fehlermessage
		   String errMessage = "[0] Writer Datei enthält keine gültigen QML-Formate ";

		   //Stylerange: Fehlermeldung rot
		   sr = new StyleRange();
		   sr.start = messageText.getCharCount();
		   sr.length = errMessage.length();
		   sr.background = display.getSystemColor( SWT.COLOR_WHITE );
		   sr.foreground = display.getSystemColor( SWT.COLOR_RED );

		   //Text einfügen
		   messageText.append( errMessage );
		   messageText.setStyleRange( sr );

		   //Knoten in Tree einfügen
		   TreeItem node = new TreeItem( rootNode, SWT.NONE );
		   node.setText("Fehler [0]");
		   node.setExpanded( true );
		   messageTree.showItem( node );

	   }





	   //Iterator über Fehler initialisieren
	   //currentError referenziert erstes Fehlerobjekt oder Null
	   StyleCodingError currentError;
	   //Iterator<StyleCodingError> errorIt = handler.codingErrors.iterator();
	   Iterator<StyleCodingError> errorIt = textParagraphList.errorIterator();
	   if( errorIt.hasNext()){
		   currentError = ( StyleCodingError ) errorIt.next();
	   } else
		   currentError = null;


	   //Über alle Absätze iterieren
	   Iterator<TextParagraph> parIt = textParagraphList.iterator();

 //Absatzzähler: Besser Absatznummer als Instanzvariable von Absatz?
	   int parCounter=0;
	   while( parIt.hasNext() ){

		   parCounter++;
		   TextParagraph par = (TextParagraph) parIt.next();

		   //Absatzinformationen in messageText kopieren;
		   //Besser erst in Stringbuffer  kopieren, dann append aufrufen?
		   //Nein: Langsamer aufbau ist cool
		   //Besser Zeilenumpruch vor beginn einer Zeile anhängen?


		   String parStyleStr = "[" + textParagraphList.styleMap.get( par.getStyle() ) + "]";

		   messageText.append( "["+parCounter+"] ");
		   parStyleOffset = messageText.getCharCount();
		   messageText.append( parStyleStr + "  " + par.getContent() );


		   //Formatstring Dunkelgrün darstellen
		   sr = new StyleRange();
		   sr.start = parStyleOffset;
		   sr.length = parStyleStr.length();
		   sr.background = display.getSystemColor( SWT.COLOR_WHITE );
		   sr.foreground = display.getSystemColor( SWT.COLOR_DARK_GREEN );
		   messageText.setStyleRange( sr );


		   //Leerer Absatz
		   if( par.getContent().length() == 0 ){

			   String emptyMarker = "(leer)";

			   //Blau formatieren
			   StyleRange s = new StyleRange();
			   s.start = messageText.getCharCount();
			   s.length = emptyMarker.length();
			   s.background = display.getSystemColor( SWT.COLOR_WHITE );
			   s.foreground = display.getSystemColor( SWT.COLOR_DARK_GRAY );

			   messageText.append( emptyMarker );
			   messageText.setStyleRange( s );


		   }


		   //Zeilenumbruch
		   messageText.append( ls );


		   //Existiert Fehler zu diesem Absatz?
		   if( currentError != null ){

			   if( currentError.parNumber == parCounter){

				   //Fehlerknoten für Tree erzeugen
				   TreeItem node = new TreeItem( rootNode, SWT.NONE );
				   node.setText("Fehler [" + currentError.parNumber + "]");
				   node.setExpanded( true );
				   //Zeigt Item, "wandert" der Baum wenn er nicht ins feld passt?
				   messageTree.showItem( node );

				   //Zeilennummer für Fehlerlink in Integer Objekt speichern
				   node.setData( new Integer( messageText.getLineCount()-1 ) );


				   //Fehlermeldung im messageText ausgeben
				   String errorMessage =  "    " + currentError.errorDescription + ls;
				   messageText.append( errorMessage );

				   //Schrift auf rot setzen
				   sr = new StyleRange();
				   sr.start = messageText.getOffsetAtLine( messageText.getLineCount()-2);
				   sr.length = errorMessage.length();
				   sr.background = display.getSystemColor( SWT.COLOR_WHITE );
				   sr.foreground = display.getSystemColor( SWT.COLOR_RED );
				   messageText.setStyleRange( sr );


				   //??Zeile Markieren, mit Hellblau?
				   //Vorherige Zeile hervorheben
				   //Notlösung, weil Markierung nicht funktioniert
				   /*
				   int lineNumber = messageText.getLineCount()-3;
				   if (lineNumber < 0 )lineNumber = 0;

				   //Achtung testen!!
				   int lineLength;
				   if( messageText.getLineCount() > lineNumber ){
					   lineLength = messageText.getOffsetAtLine( lineNumber + 1 ) -
					   messageText.getOffsetAtLine( lineNumber );
				   }else{
					   lineLength = messageText.getCharCount() -
					   messageText.getOffsetAtLine( lineNumber );
				   }

				   sr = new StyleRange();
				   sr.start = messageText.getOffsetAtLine( lineNumber );
				   sr.length = lineLength;
				   sr.background = bgSelected;
				   sr.foreground = display.getSystemColor( SWT.COLOR_WHITE );
				   messageText.setStyleRange( sr );
				   messageText.setLineBackground( lineNumber , 1, bgSelected );
				   */



				   //Nächsten Fehler holen
				   if( errorIt.hasNext()){
					   currentError = ( StyleCodingError ) errorIt.next();
				   } else
					   currentError = null;

			   }

		   }//weitere Fehler existieren




       }//Über alle Absätze iterieren

	   //Mit zwei Leerzeilen abschließen
	   messageText.append( ls + ls );


	   //messageText aktualisieren
	   messageText.setRedraw( true );
	   messageText.redraw();

	   //messageText.setText( textBuffer.toString() );
	   //messageText.setStyleRanges( (StyleRange[]) styleList.toArray( new StyleRange[ 0 ]) );



	   //Falls Fehler existieren, zumn ersten Fehler navigieren
	   //und Baumknoten markieren

	   if( ! paragraphListIsValid ){
		   if( rootNode.getItemCount() > 0){
		       TreeItem firstChildNode = rootNode.getItem( 0 );
		       if( firstChildNode.getData() != null ){

		           int errorPos = ((Integer) firstChildNode.getData()).intValue();

		           int gotoLine = errorPos -4;
		           if (gotoLine < 0 ) gotoLine = 0;
		           messageText.setTopIndex( gotoLine );
		           messageTree.setSelection( firstChildNode );

		           //Zeile im MessageText markieren
		           lineSelector.selectLine( errorPos - 1 );


		       }// Knoten enthält Daten
		   }//Knoten hat Kinder
	   }// Fehler sind vorhanden


  } //convertWriterFile()



  public static void main (String [] args) {
    new Writer2QML();
  }



}

