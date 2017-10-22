package kelly.springboot.weixin;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;


/**
 * Created by kelly.li on 17/10/22.
 */
public class XPaths {

    private static final Logger LOGGER = LoggerFactory.getLogger(XPaths.class);
    private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private static XPathFactory xPathFactory = XPathFactory.newInstance();
    private final XPath xPath;
    private final Document document;

    public XPaths(InputSource in) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        document = documentBuilder.parse(in);
        xPath = xPathFactory.newXPath();
    }


    public static XPaths load(String xml) {
        try {
            return new XPaths(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            LOGGER.error("load xml fail {}", xml, e);
            return null;
        }
    }

    public String getString(String expression) {
        return (String) evalXPath(expression, null, XPathConstants.STRING);
    }

    private Object evalXPath(String expression, Object item, QName returnType) {
        item = null == item ? document : item;
        try {
            return xPath.evaluate(expression, item, returnType);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }


}
