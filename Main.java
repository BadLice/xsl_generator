import java.io.*;
import java.util.ArrayList;

class Main
{
	public static Writer writer;
	public static String fileName = "PDF_riass.xsl";
	public static String xmlDataFile = "dati_def.xml";
	public static String colorCodeFieldset = "#04813e";
	public static String colorCodeModule = "#5c5c5c";


	public static void main(String[] args) 
	{
		try
		{
			System.out.println("----------------------------------- STARTING GENERATION -----------------------------------\n\n");

			escapeCharXmlDataFile();
			initDocument();
			new xmlDecoder();
			saveDocument();

			System.out.println("----------------------------------- FINISHED GENERATION -----------------------------------\n\n");

		}
		catch(Exception e)
		{
			System.out.println("ERROR in <<main>>: "+e);
			System.exit(0);
		}
	}

	public static String escapeApostrophe(String txt) //experimetal
	{
		ArrayList<Character> l = new ArrayList<Character>();
		
		for(int i=0;i<txt.length();i++)
		{
			//use to debug (prints every char decoded in bytes)
			//System.out.println((byte) txt.charAt(i));
			l.add(txt.charAt(i));
		}

		for(int i=0;i<l.size();i++)
		{
			if((byte) l.get(i).charValue()== -30 && (byte) l.get(i+1).charValue() == -84 && (byte) l.get(i+2).charValue() == 34)
			{
				//System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& apostrophe escaped");
				l.set(i,'\'');
				l.remove(i+1);
				l.remove(i+1);
			}

			if((byte) l.get(i).charValue()== -30 && (byte) l.get(i+1).charValue() == -128 && (byte) l.get(i+2).charValue() == -103)
			{
				//System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& apostrophe escaped");
				l.set(i,'\'');
				l.remove(i+1);
				l.remove(i+1);
			}
		}

		txt = "";
		for(int i=0;i<l.size();i++)
			txt += l.get(i).charValue();

		return txt;
	}

	public static String escapeChar(String txt)
	{
		txt = txt.replace("ì","&#236;");
		txt = txt.replace("à","&#224;");
		txt = txt.replace("ò","&#242;");
		txt = txt.replace("ù","&#249;");
		txt = txt.replace("è","&#232;");
		txt = txt.replace("é","&#233;");
		txt = txt.replace("°","&#176;");
		txt = txt.replace("‘","'");
		txt = txt.replace("’","'");
		txt = txt.replace("€","&#8364;");

		txt = escapeApostrophe(txt);

		return txt;
	}

	public static void escapeCharXmlDataFile()
	{
		try
		{
			System.out.println("XML data file: escaping special characters...");

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(xmlDataFile)));
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(xmlDataFile+"[_]")));
			String line = "";

			do
			{
				line = line.replace("UTF-8","ISO-8859-1");
				if(line.length()>0)
					writer.println(line);
				line = reader.readLine();

			}
			while(line != null);

			writer.close();
			reader.close();

			File file = new File(xmlDataFile+"[_]");
			File file2 = new File(xmlDataFile);
			if (file2.exists())
			   file2.delete();
			
			file.renameTo(file2);

			System.out.println("XML data file: escaped special characters");
		}
		catch(Exception e)
		{
			System.out.println("ERROR in <<escapeCharXmlDataFile>>: "+e);
			System.exit(0);
		}
	}

	public static void initDocument() throws IOException
	{
		System.out.println("Creating new XSL document '"+fileName+"'");
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
		writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:fo=\"http://www.w3.org/1999/XSL/Format\"><xsl:output method=\"xml\" indent=\"yes\" /><xsl:template match=\"/\"><fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" font-family=\"Arial, Helvetica, sans-serif\"><fo:layout-master-set><fo:simple-page-master master-name=\"A4\" page-width=\"210mm\" page-height=\"297mm\" margin-top=\"1cm\" margin-bottom=\"1cm\" margin-left=\"2cm\" margin-right=\"2cm\"><fo:region-body margin-top=\"3cm\" /><fo:region-before precedence=\"true\" extent=\"3cm\" /></fo:simple-page-master></fo:layout-master-set><fo:page-sequence master-reference=\"A4\"><fo:flow flow-name=\"xsl-region-body\">");
	}

	public static void saveDocument() throws IOException
	{
		System.out.println("Endend generation\nSaving...");
		String endDocument = "</fo:flow></fo:page-sequence></fo:root></xsl:template></xsl:stylesheet>";
		writer.write(endDocument);
		writer.close();
		System.out.println("XSL file saved successfully.");
	}

	public static void drawImage()
	{
		try
		{
			System.out.println("Drawing Regione Lombardia's logo");
			writer.write("<fo:block white-space-treatment=\"preserve\" text-align=\"center\"><fo:inline><fo:external-graphic src=\"url('data:image/jpeg;base64,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')\"/></fo:inline></fo:block>");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("ERROR in <<drawImage>>: "+e);
			System.exit(0);
		}
	}

	public static void setTitle(String title)
	{

		try
		{
			System.out.println("Setting document title: "+escapeChar(title));
			writer.write("<fo:block white-space-treatment=\"preserve\" padding-top=\"4px\" font-size=\"16pt\" font-weight=\"bold\"  text-align=\"center\" background-color=\"white\" border=\"1px solid "+colorCodeFieldset+"\" space-after=\"4mm\" space-before=\"4mm\">"+escapeChar(title)+"</fo:block>");

		}
		catch (Exception e)
		{
			System.out.println("ERROR in <<setTitle>>: "+e);
			System.exit(0);
		}
	}

	public static void setDocumentType(String type)
	{
		try
		{
			System.out.println("Setting document type: "+escapeChar(type));
			writer.write("<fo:block white-space-treatment=\"preserve\" padding-top=\"4px\" font-size=\"11pt\" font-weight=\"bold\" break-before=\"page\" text-align=\"center\" background-color=\"white\" border=\"1px solid black\" space-after=\"4mm\" space-before=\"4mm\">"+escapeChar(type)+"</fo:block>");
		}
		catch (Exception e)
		{
			System.out.println("ERROR in <<setDocumentType>>: "+e);
			System.exit(0);
		}
	}

	public static void createModule(String name, String label)
	{
		try
		{
			System.out.println("Creating module <<"+name+">>");
			if(label != null && !label.trim().equals(""))
				writer.write("<fo:block white-space-treatment=\"preserve\" font-size=\"11pt\" font-weight=\"bold\" text-align=\"center\" background-color=\"#d0d0d0\" space-after=\"4mm\" space-before=\"4mm\">"+escapeChar(label)+"</fo:block>");
			if(name != null && !name.trim().equals(""))
				writer.write("<fo:block white-space-treatment=\"preserve\" color=\""+colorCodeModule+"\" font-size=\"11pt\" font-weight=\"bold\" text-align=\"center\"  space-after=\"4mm\" space-before=\"4mm\">"+escapeChar(name)+"</fo:block>");
		}
		catch (Exception e)
		{
			System.out.println("ERROR in <<createModule>>: "+e);
			System.exit(0);
		}
	}

	public static void createItemList(ArrayList<Item> itemList)
	{
		try
		{
			//fieldset if condition
			if(itemList.get(0).fieldset.ifCondition.length()>0)
				writer.write("<xsl:if test='"+itemList.get(0).fieldset.ifCondition+"'>");

			if(itemList.size() == 1)
				writer.write("<xsl:if test='/_/"+escapeChar(itemList.get(0).name)+"'>");

			System.out.println("Opening fieldset <<"+escapeChar(itemList.get(0).fieldset.name)+">>");
			writer.write("<fo:block white-space-treatment=\"preserve\" color=\""+colorCodeFieldset+"\" font-size=\"11pt\" font-weight=\"bold\" text-align=\"left\" space-after=\"2mm\">"+escapeChar(itemList.get(0).fieldset.name.toUpperCase())+"</fo:block>");
			
			writer.write("<fo:table table-layout=\"fixed\" width=\"170mm\" space-after=\"2mm\" border-spacing=\"0pt 2pt\" border-bottom-style=\"solid\"><fo:table-column column-width=\"120mm\" /><fo:table-column column-width=\"50mm\" /><fo:table-body>");

			for(int i=0;i<itemList.size();i++)
			{
				Item o = itemList.get(i);

				System.out.println("Creating item <<"+escapeChar(o.name)+">> with options:\n\t- label: "+escapeChar(o.label)+"\n\t- file: "+o.file+"\n\t- if: "+o.ifCondition+"\n\t- integer: "+o.integer);

				//item if condition
				if(o.ifCondition.length()>0)
					writer.write("<xsl:if test='"+o.ifCondition+"'>");

				if(o.file)
				{
					writer.write("<xsl:if test='/_/"+escapeChar(o.name)+"'><fo:table-row border-bottom='1px dotted #dddddd'> <fo:table-cell font-size='11pt' padding-before='1mm' padding-after='1mm'><fo:block white-space-treatment=\"preserve\" font-size='11pt' text-align='left' space-after='3mm'><fo:list-block><fo:list-item><fo:list-item-label end-indent='label-end()'><fo:block>&#x2022;</fo:block></fo:list-item-label><fo:list-item-body start-indent='body-start()'><fo:block white-space-treatment=\"preserve\">");
					writer.write(escapeChar(o.label));
					writer.write("</fo:block></fo:list-item-body></fo:list-item></fo:list-block></fo:block></fo:table-cell></fo:table-row></xsl:if>");
				}
				else
				{
					//writing label
					writer.write("<xsl:if test='/_/"+escapeChar(o.name)+"'><fo:table-row border-bottom='1px dotted #dddddd' > <fo:table-cell font-size='11pt' padding-before='1mm' padding-after='1mm'><fo:block white-space-treatment=\"preserve\">");
					writer.write(escapeChar(o.label));
					writer.write("</fo:block></fo:table-cell><fo:table-cell font-size='11pt' padding='1mm'><fo:block white-space-treatment=\"preserve\">");

					//writing value
					if(o.euro)
						writer.write(escapeChar("€ "));

					writer.write("<xsl:value-of select='");

					if(o.integer)
						writer.write("substring-before(");

						writer.write("/_/"+escapeChar(o.name));
					
					if(o.decoded)
						writer.write("/@decoded");

					if(o.integer)
						writer.write(", \",\")");
					
					writer.write("'/></fo:block></fo:table-cell></fo:table-row></xsl:if>");

				}

				if(o.ifCondition.length()>0)
					writer.write("</xsl:if>");
			}

			writer.write("</fo:table-body></fo:table>");

			if(itemList.size() == 1)
				writer.write("</xsl:if>");

			if(itemList.get(0).fieldset.ifCondition.length()>0)
				writer.write("</xsl:if>");

			System.out.println("Closing fieldset: <<"+itemList.get(0).fieldset.name+">>");

		}
		catch (Exception e)
		{
			System.out.println("ERROR in <<createItemList>>: "+e);
			System.exit(0);
		}
	}

	public static void initSubmodule(SubModule sub)
	{
		try
		{
			System.out.println("Opening submodule <<"+sub.name+">>");
			//this xsl structure isn't goood for innested submodules (2+ level)
			writer.write("<xsl:if test='/_/"+sub.name+"_0."+sub.requiredItem+"'>");
			writer.write("<fo:block white-space-treatment=\"preserve\" font-size=\"11pt\" font-weight=\"bold\" text-align=\"left\" space-after=\"2mm\">"+escapeChar(sub.label.toUpperCase())+"</fo:block>");
			writer.write("<fo:table table-layout=\"fixed\" width=\"170mm\" space-after=\"2mm\" border-spacing=\"0pt 2pt\" border-bottom-style=\"solid\"><fo:table-body>");
			writer.write("<xsl:for-each select='(/_/*[starts-with(name(),\""+(sub.name)+"_\") and contains(name(),\"."+sub.requiredItem+"\")])'>");
		}
		catch (Exception e)
		{
			System.out.println("ERROR in <<initSubmodule>>: "+e);
			System.exit(0);
		}
	}

	public static void closeSubmodule(SubModule sub)
	{
		try
		{
			System.out.println("Closing submodule <<"+sub.name+">>");
			writer.write("</xsl:for-each></fo:table-body></fo:table></xsl:if>");
		}
		catch (Exception e)
		{
			System.out.println("ERROR in <<closeSubmodule>>: "+e);
			System.exit(0);
		}
	}

	public static void createSubmoduleList(SubModule sub, ArrayList<Item> itemList)
	{
		try
		{
			writer.write("<xsl:variable name='indexDoc' select='position()-1' />");
			
			for(int i=0; i<itemList.size();i++)
			{
				Item o = itemList.get(i);
				System.out.println("Creating submodule item <<"+escapeChar(o.name)+">> with options:\n\t- label: "+escapeChar(o.label)+"\n\t- file: "+o.file+"\n\t- if: "+o.ifCondition+"\n\t- integer: "+o.integer);

				//item if condition
				if(o.ifCondition.length()>0)
					writer.write("<xsl:if test='"+o.ifCondition+"'>");

				if(o.file)
				{
					writer.write("<xsl:if test='/_/*[name()=concat(\""+sub.name+"_\",$indexDoc,\"."+o.name+"\")]'><fo:table-row");
					writer.write(" border-bottom=\"1px dotted #dddddd\" ");
					writer.write("> <fo:table-cell font-size='11pt' padding-before='1mm' padding-after='1mm'><fo:block white-space-treatment=\"preserve\" font-size='11pt' text-align='left' space-after='3mm'><fo:list-block><fo:list-item><fo:list-item-label end-indent='label-end()'><fo:block>&#x2022;</fo:block></fo:list-item-label><fo:list-item-body start-indent='body-start()'><fo:block white-space-treatment=\"preserve\">");
					writer.write(escapeChar(o.label));
					writer.write("</fo:block></fo:list-item-body></fo:list-item></fo:list-block></fo:block></fo:table-cell></fo:table-row></xsl:if>");
				}
				else
				{
					//writing label
					writer.write("<xsl:if test='/_/*[name()=concat(\""+sub.name+"_\",$indexDoc,\"."+o.name+"\")]'><fo:table-row");
					writer.write(" border-bottom=\"1px dotted #dddddd\" ");
					writer.write("> <fo:table-cell font-size='11pt' padding-before='1mm' padding-after='1mm'><fo:block white-space-treatment=\"preserve\">");
					writer.write(escapeChar(o.label));
					writer.write("</fo:block></fo:table-cell><fo:table-cell font-size='11pt' padding='1mm'><fo:block white-space-treatment=\"preserve\">");

					//writing value
					if(o.euro)
						writer.write(escapeChar("€ "));

					writer.write("<xsl:value-of select='");

					if(o.integer)
						writer.write("substring-before(");

					writer.write("/_/*[name()=concat(\""+sub.name+"_\",$indexDoc,\"."+o.name+"\")]");
					
					if(o.decoded)
						writer.write("/@decoded");

					if(o.integer)
						writer.write(", \",\")");
					
					writer.write("'/></fo:block></fo:table-cell></fo:table-row></xsl:if>");
				}

				if(i == itemList.size()-1)
					writer.write("<fo:table-row border-bottom=\"1pt dotted "+colorCodeFieldset+"\"><fo:table-cell font-size=\"1pt\" padding-after=\"0mm\" padding-before=\"0mm\"><fo:block white-space-treatment=\"preserve\"> </fo:block></fo:table-cell></fo:table-row>	");

				if(o.ifCondition.length()>0)
					writer.write("</xsl:if>");
			}
		}
		catch (Exception e)
		{
			System.out.println("ERROR in <<createModule>>: "+e);
			System.exit(0);
		}
	}

}

class Item
{
	String name;
	String type;
	String value;
	String label;
	boolean decoded;
	boolean file;
	boolean integer;
	boolean euro;
	String ifCondition;
	Fieldset fieldset;

	Item(String name, String label, boolean decoded, boolean file, boolean integer, boolean euro, String ifCondition,Fieldset fieldset)
	{
		this.name = name;
		this.label=label;
		this.decoded=decoded;
		this.file=file;
		this.integer=integer;
		this.euro=euro;
		this.fieldset=fieldset;
		this.ifCondition=ifCondition.replace("\'","\"");
	}

}

class Fieldset
{
	String name;
	String ifCondition;

	Fieldset(String name,String ifCondition)
	{
		this.name = name;
		this.ifCondition=ifCondition.replace("\'","\"");
	}
}

class SubModule
{
	String name;
	String requiredItem;
	String label;

	SubModule(String name, String label, String requiredItem)
	{
		this.name=name;
		this.requiredItem=requiredItem;
		this.label=label;
	}

}