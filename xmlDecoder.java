import java.util.ArrayList;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

class xmlDecoder
{
	static Fieldset currFieldset;
	static ArrayList<SubModule> subModuleList = new ArrayList<SubModule>();
	static boolean xmlRead=false;
	static boolean xslRead=false;

	//starts recursive documents file reading
	public static void readDocumentsName()
	{
		int level = 0;
		ArrayList<Item> templateItems = new ArrayList<Item>();

		try
		{
			File file = new File("template.xml");
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = dBuilder.parse(file);

			if(!doc.getDocumentElement().getNodeName().equals("_"))
			{
				System.out.println("Missing or invalid root name.\nThe 'template.xml' document must have '<_>' as root element");
				System.exit(0);
			}

			if (doc.getDocumentElement().hasChildNodes())
				intoNodeDocumentsName(doc.getDocumentElement().getChildNodes(),level);
			xmlDecoder.currFieldset = null;
			xmlDecoder.subModuleList.clear();

		}
		catch (Exception e)
		{
			System.err.println("ERROR in <<readDocumentsName>>: "+e);
		}
	}

	//recursive function: reads output file names from xml "template"
	private static void intoNodeDocumentsName(NodeList nodeList,int level) 
	{
		ArrayList<Item> itemList = new ArrayList<Item>();

	    for (int count = 0; count < nodeList.getLength(); count++)
	    {

			Node currNode = nodeList.item(count);

			if (currNode.getNodeType() == Node.ELEMENT_NODE)
			{

				switch(currNode.getNodeName().toLowerCase())
				{
					case "xml_file_name":
						Main.xmlDataFile = currNode.getTextContent();
						xmlDecoder.xmlRead=true;
						System.out.println("Setting xml document name: "+Main.xmlDataFile);
					break;

					case "xsl_file_name":
						Main.fileName = currNode.getTextContent();
						xmlDecoder.xslRead=true;
						System.out.println("Setting xsl document name: "+Main.fileName);
					break;

					case "document":
						if (currNode.hasChildNodes())
							intoNodeDocumentsName(currNode.getChildNodes(),level);
					break;
				}
			}

			if(xslRead && xmlRead)
				return;
		}

		if(level!=0)
			Main.createSubmoduleList(subModuleList.get(level-1),itemList);
		else
		if(itemList.size()>0)
			Main.createItemList(itemList);
	}

	//starts recursive template reading
	public static void readXmlInputFile()
	{
		int level = 0;
		ArrayList<Item> templateItems = new ArrayList<Item>();

		try
		{
			File file = new File("template.xml");
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = dBuilder.parse(file);

			if(!doc.getDocumentElement().getNodeName().equals("_"))
			{
				System.out.println("Missing or invalid root name.\nThe 'template.xml' document must have '<_>' as root element");
				System.exit(0);
			}

			if (doc.getDocumentElement().hasChildNodes())
				intoNode(doc.getDocumentElement().getChildNodes(),level);
		}
		catch (Exception e)
		{
			System.err.println("ERROR in <<readXmlInputFile>>: "+e);
		}
	}

	//recursive function: reads each item of xml "template"
	private static void intoNode(NodeList nodeList,int level) 
	{
		ArrayList<Item> itemList = new ArrayList<Item>();

	    for (int count = 0; count < nodeList.getLength(); count++)
	    {

			Node currNode = nodeList.item(count);

			//System.out.println("Current node: "+currNode.getNodeName());

			if (currNode.getNodeType() == Node.ELEMENT_NODE)
			{

				switch(currNode.getNodeName().toLowerCase())
				{
					//dont consider the following items:
					case "xml_file_name":
					case "xsl_file_name":
					break;

					case "doc_init_title":
						Main.setTitle(currNode.getTextContent());
					break;

					case "doc_init_type":
						Main.setDocumentType(currNode.getTextContent().toUpperCase());
					break;

					case "doc_init_image":
						if(currNode.getTextContent().toLowerCase().equals("true"))
							Main.drawImage();
					break;

					case "doc_init_color_fieldset":
						Main.colorCodeFieldset = currNode.getTextContent().toLowerCase();
					break;

					case "doc_init_color_module":
						Main.colorCodeModule = currNode.getTextContent().toLowerCase();
					break;

					case "document":
						if (currNode.hasChildNodes())
							intoNode(currNode.getChildNodes(),level);
					break;

					case "module":
						if(currNode.hasAttributes())
						{
							String name = "";
							String label = "";

							NamedNodeMap nodeMap = currNode.getAttributes();

							for (int i = 0; i < nodeMap.getLength(); i++)
							{
								Node node = nodeMap.item(i);

								if(node.getNodeName().toLowerCase().equals("name"))
									name = node.getNodeValue();

								if(node.getNodeName().toLowerCase().equals("label"))
									label = node.getNodeValue();
							}

							Main.createModule(name,label);

							if (currNode.hasChildNodes())
							{
								intoNode(currNode.getChildNodes(),level);
							}
						}
						// else
						// {
						// 	throw Exception; //to be defined
						// }
					break;

					case "submodule":

						if(currNode.hasAttributes())
						{
							String label = "";
							String subModuleName = "";
							String requiredItem = "";
							
							NamedNodeMap nodeMap = currNode.getAttributes();

							for (int i = 0; i < nodeMap.getLength(); i++)
							{
								Node node = nodeMap.item(i);

								if(node.getNodeName().toLowerCase().equals("label"))
									label = node.getNodeValue();

								if(node.getNodeName().toLowerCase().equals("name"))
									subModuleName = node.getNodeValue();

								if(node.getNodeName().toLowerCase().equals("required-item"))
									requiredItem = node.getNodeValue();
							}

							subModuleList.add(new SubModule(subModuleName,label,requiredItem));

							if (currNode.hasChildNodes())
							{
								Main.initSubmodule(subModuleList.get(level));
								intoNode(currNode.getChildNodes(),level+1);
								Main.closeSubmodule(subModuleList.get(level));
								subModuleList.remove(level);
							}
						}
					break;

					case "fieldset":
						if(currNode.hasAttributes())
						{
							String ifCondition = "";
							String label = "";
							
							NamedNodeMap nodeMap = currNode.getAttributes();

							for (int i = 0; i < nodeMap.getLength(); i++)
							{
								Node node = nodeMap.item(i);

								if(node.getNodeName().toLowerCase().equals("label"))
									label = node.getNodeValue();

								if(node.getNodeName().toLowerCase().equals("if"))
									ifCondition = node.getNodeValue();
							}

							currFieldset = new Fieldset(label,ifCondition);

							if (currNode.hasChildNodes())
							{
								intoNode(currNode.getChildNodes(),level);
							}
						}
					break;

					//is an item
					default:
						
						String name = currNode.getNodeName();
						boolean decoded = false;
						String label=currNode.getTextContent();
						String ifCondition = "";
						boolean file = false;
						boolean integer = false;
						boolean euro = false;

						if(currNode.hasAttributes())
						{
							NamedNodeMap nodeMap = currNode.getAttributes();

							for (int i = 0; i < nodeMap.getLength(); i++)
							{
								Node node = nodeMap.item(i);

								if(node.getNodeName().toLowerCase().equals("label"))
									label = node.getNodeValue();

								if(node.getNodeName().toLowerCase().equals("decoded"))
									decoded = true;

								if(node.getNodeName().toLowerCase().equals("file"))
									file = true;

								if(node.getNodeName().toLowerCase().equals("integer"))
									integer = true;

								if(node.getNodeName().toLowerCase().equals("if"))
									ifCondition = node.getNodeValue();

								if(node.getNodeName().toLowerCase().equals("euro"))
									euro = true;

							}
						}

						itemList.add(new Item(name,label,decoded,file,integer,euro,ifCondition,currFieldset));

					break;
				}
			}

		}

		if(level!=0)
			Main.createSubmoduleList(subModuleList.get(level-1),itemList);
		else
		if(itemList.size()>0)
			Main.createItemList(itemList);
	}
}
