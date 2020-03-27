package com.mongodb.diginamic.rss;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Parser un flux RSS
 * 
 * @author Fobec 2010
 */

public class App {

	/**
	 * Parser le fichier XML
	 * 
	 * @param feedurl URL du flux RSS
	 */
	public void parse(String feedurl) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			URL url = new URL(feedurl);
			Document doc = builder.parse(url.openStream());
			NodeList channelNodes = null, itemNodes = null;
//			Element element = null;

			channelNodes = doc.getElementsByTagName("channel");
			
			// Pour chaque node <channel>
			for (int i = 0; i < channelNodes.getLength(); i++) {

				Node channelNode = channelNodes.item(i); // Récupére le noeud et sa liste de noeuds enfants
				// old
				// System.out.println("Title: " + channelNode.getChildNodes().item(1).getTextContent());
				System.out.println("## RSS Infos:");
				System.out.println("# Title: " + getChildByName(channelNode, "title").getTextContent());
				System.out.println("# Link: " + getChildByName(channelNode, "link").getTextContent());
				System.out.println("# Description: " + getChildByName(channelNode, "description").getTextContent());
				System.out.println("# Language: " + getChildByName(channelNode, "language").getTextContent());

				/**
				 * Elements (items) du flux RSS
				 **/
				itemNodes = doc.getElementsByTagName("item"); 
				// FIXME à chaque passage du for, on recrée la liste de nodes "item"
				// aussi, on va chercher TOUS les items, pas ceux uniquement contenus dans le channel actuel.

				System.out.println("## ITEMS:");
				for (int j = 0; j < itemNodes.getLength(); j++) {
					System.out.println("     Item #" + (j+1));
					System.out.println("\tTitle: " + getChildByName(itemNodes.item(j), "title").getTextContent());
					System.out.println("\tLink: " + getChildByName(itemNodes.item(j), "link").getTextContent());
//					System.out.println("\tDate: " + getChildByName(itemNodes.item(j), "pubDate").getTextContent());
					System.out.println("\tDate: " + GMTDateToFrench(getChildByName(itemNodes.item(j), "pubDate").getTextContent()));
					System.out.println("\tDesc: " + getChildByName(itemNodes.item(j), "description").getTextContent().substring(0,120) + "\n");
				}
				
			} // for
		} catch (SAXException ex) {
			Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ParserConfigurationException ex) {
			Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Méthode permettant de retourner ce que contient d'un noeud
	 * 
	 * @param _node le noeud principal
	 * @param _path suite des noms des noeud sans espace séparer par des "|"
	 * @return un string contenant le valeur du noeud voulut
	 */
	public String readNode(Node _node, String _path) {

		String[] paths = _path.split("|");
		Node node = null;

		if (paths != null && paths.length > 0) {
			node = _node;

			for (int i = 0; i < paths.length; i++) {
				node = getChildByName(node, paths[i].trim());
			}
		}

		if (node != null) {
			return node.getTextContent();
		} else {
			return "";
		}
	}

	/**
	 * renvoye le nom d'un noeud fils a partir de son nom
	 * 
	 * @param _node noeud pricipal
	 * @param _name nom du noeud fils
	 * @return le noeud fils
	 */
	public Node getChildByName(Node _node, String _name) {
		if (_node == null) {
			return null;
		}
		NodeList listChild = _node.getChildNodes();

		if (listChild != null) {
			for (int i = 0; i < listChild.getLength(); i++) {
				Node child = listChild.item(i);
				if (child != null) {
					if ((child.getNodeName() != null && (_name.equals(child.getNodeName())))
							|| (child.getLocalName() != null && (_name.equals(child.getLocalName())))) {
						return child;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Afficher une Date GML au format francais
	 * 
	 * @param gmtDate
	 * @return
	 */
	public String GMTDateToFrench(String gmtDate) {
		
		// We parse the string to construct a date
		LocalDateTime test = LocalDateTime.parse(gmtDate, DateTimeFormatter.RFC_1123_DATE_TIME);
		
		// Then we return the parsed date with a new format.
		return test.format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm:ss"));

	}

	/**
	 * Exemple
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		App reader = new App();
		reader.parse("https://www.programmez.com/rss.xml");
	}
}
