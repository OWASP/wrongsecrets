package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.FILE_MOUNT_ERROR;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Challenge about deployment credentials that were committed in a Maven {@code settings.xml}. The
 * settings file ships with the application and contains a {@code <server>} entry for a fictional
 * Nexus repository manager. The password of that server entry is the answer: the answer is always
 * read from the file itself and is never stored in the code.
 */
@Slf4j
@Component
public class Challenge75 extends FixedAnswerChallenge {

  static final String NEXUS_SERVER_ID = "wrongsecrets-nexus";

  private final Resource settingsFile;

  /**
   * Constructor for creating a new Challenge75 object.
   *
   * @param settingsFile the Maven settings.xml that holds the Nexus deployment credentials
   */
  public Challenge75(
      @Value("classpath:challenges/challenge-75/settings.xml") Resource settingsFile) {
    this.settingsFile = settingsFile;
  }

  @Override
  public String getAnswer() {
    try (InputStream settingsXml = settingsFile.getInputStream()) {
      var servers = parse(settingsXml).getElementsByTagName("server");
      for (int i = 0; i < servers.getLength(); i++) {
        Node server = servers.item(i);
        if (NEXUS_SERVER_ID.equals(childText(server, "id"))) {
          var password = childText(server, "password");
          if (password != null && !password.isBlank()) {
            return password;
          }
        }
      }
      log.warn("Could not find the Nexus credentials in the settings.xml of challenge 75");
      return FILE_MOUNT_ERROR;
    } catch (IOException e) {
      log.warn("Could not read the settings.xml of challenge 75", e);
      return FILE_MOUNT_ERROR;
    } catch (ParserConfigurationException | SAXException e) {
      log.warn("Could not parse the settings.xml of challenge 75", e);
      return FILE_MOUNT_ERROR;
    }
  }

  private static Document parse(InputStream settingsXml)
      throws ParserConfigurationException, SAXException, IOException {
    var factory = DocumentBuilderFactory.newInstance();
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
    factory.setXIncludeAware(false);
    factory.setExpandEntityReferences(false);
    return factory.newDocumentBuilder().parse(settingsXml);
  }

  private static String childText(Node parent, String childName) {
    NodeList children = parent.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE && childName.equals(child.getNodeName())) {
        return child.getTextContent().trim();
      }
    }
    return null;
  }
}
