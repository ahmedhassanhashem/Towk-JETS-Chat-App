package gov.iti.jets.controller;

import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
            if (!htmlContent.contains("<html") && !htmlContent.contains("<body")) {
                htmlContent = "<html><body>" + htmlContent + "</body></html>";
            }

            Document doc = Jsoup.parse(htmlContent);
            Elements elements = doc.select("body > *");

            for (Element element : elements) {
                processElement(element, textFlow);
            }
        } catch (Exception e) {
            Text fallbackText = new Text(htmlContent.replaceAll("<[^>]*>", ""));
            textFlow.getChildren().add(fallbackText);
        }

        return textFlow;
    }

    private static void processElement(Element element, TextFlow textFlow) {
        String tagName = element.tagName();
        String text = element.text();

        if (text.isEmpty()) return;

        boolean hasBackground = element.attr("style").contains("background-color");
        StringBuilder styleBuilder = new StringBuilder();

        switch (tagName.toLowerCase()) {
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
            case "ol": case "ul":
                handleList(element, textFlow, tagName.equals("ol"));
                return;
        }

        String inlineStyle = element.attr("style");
        if (!inlineStyle.isEmpty()) {
            styleBuilder.append(convertCssToJavaFxStyle(inlineStyle));
        }

        if (hasBackground) {
            Label label = new Label(text);
            label.setStyle(styleBuilder.toString() + "-fx-padding: 2 4; -fx-background-radius: 3;");
            textFlow.getChildren().add(label);
        } else {
            Text textNode = new Text(text + " ");
            if (!styleBuilder.toString().isEmpty()) {
                textNode.setStyle(styleBuilder.toString());
            }
            textFlow.getChildren().add(textNode);
        }
    }

    private static void handleList(Element listElement, TextFlow textFlow, boolean ordered) {
        Elements items = listElement.select("li");
        int counter = 1;
        for (Element item : items) {
            String prefix = ordered ? counter++ + ". " : "\u2022 ";
            Text listItem = new Text(prefix + item.text() + "\n");
            textFlow.getChildren().add(listItem);
        }
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

    public static void applyStyledText(String htmlContent, TextFlow previewArea) {
        TextFlow styledText = convertHtmlToTextFlow(htmlContent);
        previewArea.getChildren().clear();
        previewArea.getChildren().addAll(styledText.getChildren());
    }
} 
