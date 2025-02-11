package gov.iti.jets.controller;

import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlToTextFlowConverter {
    public static TextFlow convertHtmlToTextFlow(String htmlContent) {
        TextFlow textFlow = new TextFlow();

        if (htmlContent == null || htmlContent.isEmpty()) {
            return textFlow;
        }
        try {
           
            if (!htmlContent.toLowerCase().contains("<html")) {
                htmlContent = "<html><body>" + htmlContent + "</body></html>";
            }

            Document doc = Jsoup.parse(htmlContent);
            doc.outputSettings().prettyPrint(false); 
            processElement(doc.body(), textFlow);
        } catch (Exception e) {
            e.printStackTrace();
            
            Text fallbackText = new Text(htmlContent.replaceAll("<[^>]*>", ""));
            textFlow.getChildren().add(fallbackText);
        }

        return textFlow;
    }

    private static void processElement(Element element, TextFlow textFlow) {
        if (element.childNodes().isEmpty()) {
            String text = element.text();

            if (!text.trim().isEmpty()) {
                Text textNode = new Text(text);
                String style = getStyleForElement(element);
                if (!style.isEmpty()) {
                    textNode.setStyle(style);
                }
                textFlow.getChildren().add(textNode);
            }
            return;
        }
    
        for (Node node : element.childNodes()) {
            if (node instanceof TextNode) {
                String text = ((TextNode) node).text();
                if (!text.trim().isEmpty()) {
                    Text textNode = new Text(text);
                    String style = getStyleForElement(element);
                    if (!style.isEmpty()) {
                        textNode.setStyle(style);
                    }
                    textFlow.getChildren().add(textNode);
                }
            } else if (node instanceof Element) {
                processElement((Element) node, textFlow);
            }
        }
    }

    private static String getStyleForElement(Element element) {
        StringBuilder styleBuilder = new StringBuilder();

        switch (element.tagName().toLowerCase()) {
            case "b": case "strong":
                styleBuilder.append("-fx-font-weight: bold;");
                break;
            case "i": case "em":
                styleBuilder.append("-fx-font-style: italic;");
                break;
            case "u": case "ins":
                styleBuilder.append("-fx-underline: true;");
                break;
            case "strike": case "s":
                styleBuilder.append("-fx-strikethrough: true;");
                break;
            case "h1":
                styleBuilder.append("-fx-font-size: 24px; -fx-font-weight: bold;");
                break;
            case "h2":
                styleBuilder.append("-fx-font-size: 20px; -fx-font-weight: bold;");
                break;
            case "h3":
                styleBuilder.append("-fx-font-size: 18px; -fx-font-weight: bold;");
                break;
        }

        
        String inlineStyle = element.attr("style");
        if (!inlineStyle.isEmpty()) {
            styleBuilder.append(convertCssToJavaFxStyle(inlineStyle));
        }

        return styleBuilder.toString();
    }

    private static String convertCssToJavaFxStyle(String cssStyle) {
        StringBuilder javaFxStyle = new StringBuilder();
        String[] styles = cssStyle.split(";");
        for (String style : styles) {
            style = style.trim();
            if (style.startsWith("color:")) {
                javaFxStyle.append("-fx-fill: ").append(style.substring(6).trim()).append(";");
            }
            if (style.startsWith("background-color:")) {
                javaFxStyle.append("-fx-background-color: ").append(style.substring(17).trim()).append(";");
            }
            if (style.startsWith("font-weight:")) {
                javaFxStyle.append("-fx-font-weight: ").append(style.substring(12).trim()).append(";");
            }
            if (style.startsWith("font-style:")) {
                javaFxStyle.append("-fx-font-style: ").append(style.substring(11).trim()).append(";");
            }
            if (style.startsWith("font-size:")) {
                javaFxStyle.append("-fx-font-size: ").append(style.substring(10).trim()).append(";");
            }
            if (style.startsWith("font-family:")) {
                javaFxStyle.append("-fx-font-family: ").append(style.substring(12).trim()).append(";");
            }
            if (style.startsWith("text-decoration:")) {
                String decoration = style.substring(16).trim();
                if (decoration.contains("underline")) {
                    javaFxStyle.append("-fx-underline: true;");
                }
                if (decoration.contains("line-through")) {
                    javaFxStyle.append("-fx-strikethrough: true;");
                }
            }
        }
        return javaFxStyle.toString();
    }
}
