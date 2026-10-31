package com.canteenx.util;

import com.canteenx.dao.CategoryDAO;
import com.canteenx.dao.MenuItemDAO;
import com.canteenx.model.Category;
import com.canteenx.model.MenuItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class demonstrating core XML concepts from the WT Syllabus:
 * 1. DOM Parsing (Document Object Model) with XXE protection
 * 2. SAX Parsing (Simple API for XML) with stream counting
 * 3. XML Schema (XSD) Validation
 * 4. DTD Validation with EntityResolver
 * 5. Full XML Menu Import with database persistence & schema constraint validation
 */
public class XmlParserUtil {

    // Default image mappings for recognized dishes from sample-menu.xml
    private static final Map<String, String> DISH_IMAGE_MAP = new HashMap<>();
    static {
        DISH_IMAGE_MAP.put("masala dosa", "assets/images/dosa.jpg");
        DISH_IMAGE_MAP.put("idli vada combo", "assets/images/idli.jpg");
        DISH_IMAGE_MAP.put("poha with sev", "assets/images/poha.jpg");
        DISH_IMAGE_MAP.put("special veg thali", "assets/images/thali.jpg");
        DISH_IMAGE_MAP.put("paneer fried rice", "assets/images/fried_rice.jpg");
        DISH_IMAGE_MAP.put("chole bhature (2 pcs)", "assets/images/chole.jpg");
        DISH_IMAGE_MAP.put("aloo samosa (2 pcs)", "assets/images/samosa.jpg");
        DISH_IMAGE_MAP.put("grilled cheese sandwich", "assets/images/sandwich.jpg");
        DISH_IMAGE_MAP.put("veg spring roll", "assets/images/spring_roll.jpg");
        DISH_IMAGE_MAP.put("kulhad masala chai", "assets/images/chai.jpg");
        DISH_IMAGE_MAP.put("cold coffee with ice cream", "assets/images/cold_coffee.jpg");
        DISH_IMAGE_MAP.put("fresh lime soda", "assets/images/lime_soda.jpg");
        DISH_IMAGE_MAP.put("gulab jamun (2 pcs)", "assets/images/gulab_jamun.jpg");
        DISH_IMAGE_MAP.put("chocolate brownie", "assets/images/brownie.jpg");
        DISH_IMAGE_MAP.put("paneer tikka roll", "assets/images/paneer_roll.jpg");
    }

    /**
     * Configures XXE protection on DocumentBuilderFactory.
     */
    private static void applySecurityFeatures(DocumentBuilderFactory dbf) {
        try {
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false); // allowed for DTD
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
        } catch (Exception ignored) {}
    }

    /**
     * Validates an XML file against an XSD schema.
     */
    public static ValidationResult validateAgainstXsd(File xmlFile, File xsdFile) {
        if (xmlFile == null || !xmlFile.exists()) {
            return new ValidationResult(false, "XML file not found: " + (xmlFile != null ? xmlFile.getName() : "null"));
        }
        if (xsdFile == null || !xsdFile.exists()) {
            return new ValidationResult(false, "XSD schema file not found: " + (xsdFile != null ? xsdFile.getName() : "null"));
        }
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "file");
            Schema schema = factory.newSchema(xsdFile);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlFile));
            return new ValidationResult(true, "XML successfully validated against XSD schema: " + xsdFile.getName());
        } catch (Exception e) {
            return new ValidationResult(false, "XSD Validation failed: " + e.getMessage());
        }
    }

    /**
     * Validates an XML file against a DTD schema using DocumentBuilder.
     * Uses a custom EntityResolver to resolve local DTD references safely.
     */
    public static ValidationResult validateAgainstDtd(File xmlFile) {
        if (xmlFile == null || !xmlFile.exists()) {
            return new ValidationResult(false, "XML file not found.");
        }
        final List<String> errors = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(false);
            applySecurityFeatures(factory);

            DocumentBuilder builder = factory.newDocumentBuilder();
            final File parentDir = xmlFile.getParentFile();

            builder.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(String publicId, String systemId) {
                    if (systemId != null && systemId.endsWith("menu.dtd")) {
                        File dtdFile = new File(parentDir, "menu.dtd");
                        if (dtdFile.exists()) {
                            try {
                                return new InputSource(new FileInputStream(dtdFile));
                            } catch (Exception ignored) {}
                        }
                    }
                    return null;
                }
            });

            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) {
                    errors.add("[Warning] Line " + exception.getLineNumber() + ": " + exception.getMessage());
                }

                @Override
                public void error(SAXParseException exception) {
                    errors.add("[Error] Line " + exception.getLineNumber() + ": " + exception.getMessage());
                }

                @Override
                public void fatalError(SAXParseException exception) {
                    errors.add("[Fatal] Line " + exception.getLineNumber() + ": " + exception.getMessage());
                }
            });

            builder.parse(xmlFile);
            if (errors.isEmpty()) {
                return new ValidationResult(true, "XML successfully validated against DTD schema.");
            } else {
                return new ValidationResult(false, String.join("; ", errors));
            }
        } catch (Exception e) {
            return new ValidationResult(false, "DTD Validation failed: " + e.getMessage());
        }
    }

    /**
     * Parses food items from an XML file using DOM parser, extracting categories and food items.
     */
    public static List<MenuItem> parseMenuItemsUsingDOM(InputStream xmlStream) throws Exception {
        List<MenuItem> items = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        applySecurityFeatures(factory);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlStream);
        doc.getDocumentElement().normalize();

        NodeList categoryList = doc.getElementsByTagName("category");
        if (categoryList.getLength() > 0) {
            for (int c = 0; c < categoryList.getLength(); c++) {
                Element catElem = (Element) categoryList.item(c);
                int catId = 0;
                try {
                    catId = Integer.parseInt(catElem.getAttribute("id"));
                } catch (NumberFormatException ignored) {}

                String catName = "";
                NodeList catNameNodes = catElem.getElementsByTagName("categoryName");
                if (catNameNodes.getLength() > 0) {
                    catName = catNameNodes.item(0).getTextContent().trim();
                }

                NodeList foodNodes = catElem.getElementsByTagName("foodItem");
                for (int f = 0; f < foodNodes.getLength(); f++) {
                    Element foodElem = (Element) foodNodes.item(f);
                    MenuItem item = parseFoodElement(foodElem, catId, catName);
                    items.add(item);
                }
            }
        } else {
            // Fallback for flat foodItem list
            NodeList foodNodeList = doc.getElementsByTagName("foodItem");
            for (int i = 0; i < foodNodeList.getLength(); i++) {
                Element foodElem = (Element) foodNodeList.item(i);
                MenuItem item = parseFoodElement(foodElem, 1, "General");
                items.add(item);
            }
        }

        return items;
    }

    private static MenuItem parseFoodElement(Element foodElem, int categoryId, String categoryName) {
        int id = 0;
        try {
            id = Integer.parseInt(foodElem.getAttribute("id"));
        } catch (NumberFormatException ignored) {}

        String name = foodElem.getElementsByTagName("name").item(0).getTextContent().trim();
        String desc = "";
        NodeList descList = foodElem.getElementsByTagName("description");
        if (descList.getLength() > 0) {
            desc = descList.item(0).getTextContent().trim();
        }

        BigDecimal price = BigDecimal.ZERO;
        NodeList priceList = foodElem.getElementsByTagName("price");
        if (priceList.getLength() > 0) {
            try {
                price = new BigDecimal(priceList.item(0).getTextContent().trim());
            } catch (Exception ignored) {}
        }

        boolean available = true;
        NodeList availList = foodElem.getElementsByTagName("isAvailable");
        if (availList.getLength() > 0) {
            available = Boolean.parseBoolean(availList.item(0).getTextContent().trim());
        }

        String matchedImage = DISH_IMAGE_MAP.get(name.toLowerCase());
        if (matchedImage == null) {
            matchedImage = "assets/images/food-placeholder.jpg";
        }

        MenuItem item = new MenuItem();
        item.setItemId(id);
        item.setCategoryId(categoryId);
        item.setCategoryName(categoryName);
        item.setItemName(name);
        item.setDescription(desc);
        item.setPrice(price);
        item.setAvailable(available);
        item.setImageUrl(matchedImage);
        return item;
    }

    /**
     * Demonstrates SAX parsing on the XML stream with element counting.
     */
    public static int countElementsUsingSAX(InputStream xmlStream) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (Exception ignored) {}

        SAXParser saxParser = factory.newSAXParser();
        final int[] count = {0};
        DefaultHandler handler = new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) {
                count[0]++;
            }
        };

        saxParser.parse(xmlStream, handler);
        return count[0];
    }

    /**
     * Imports food items and categories from XML into the MySQL database.
     * Enforces schema rules:
     * - Name cannot be blank
     * - Price must be positive and <= 5000 (PriceType in menu.xsd)
     * - Resolves or creates categories
     * - Inserts or updates menu items
     */
    public static ImportResult importMenuFromXml(InputStream xmlStream, MenuItemDAO menuItemDAO, CategoryDAO categoryDAO) {
        ImportResult result = new ImportResult();
        try {
            List<MenuItem> items = parseMenuItemsUsingDOM(xmlStream);
            if (items.isEmpty()) {
                result.setSuccess(false);
                result.setMessage("No food items found in XML document to import.");
                return result;
            }

            for (MenuItem parsed : items) {
                // Validation against schema constraints
                if (parsed.getItemName() == null || parsed.getItemName().trim().isEmpty()) {
                    result.addWarning("Skipped food item with empty name.");
                    continue;
                }
                if (parsed.getPrice() == null || parsed.getPrice().compareTo(BigDecimal.ZERO) <= 0 ||
                    parsed.getPrice().compareTo(new BigDecimal("5000.00")) > 0) {
                    result.addWarning("Skipped item '" + parsed.getItemName() + "': price must be > 0 and <= 5000.");
                    continue;
                }

                // Resolve category
                int catId = parsed.getCategoryId();
                Category category = categoryDAO.getCategoryById(catId);
                if (category == null) {
                    // Try by category name if available
                    String catName = parsed.getCategoryName();
                    if (catName != null && !catName.trim().isEmpty()) {
                        category = categoryDAO.getCategoryByName(catName);
                        if (category == null) {
                            Category newCat = new Category(0, catName, "Imported from XML");
                            boolean catCreated = categoryDAO.addCategory(newCat);
                            if (catCreated) {
                                catId = newCat.getCategoryId();
                                result.incrementCategoriesCreated();
                            }
                        } else {
                            catId = category.getCategoryId();
                        }
                    } else {
                        catId = 1; // Default to Breakfast
                    }
                }
                parsed.setCategoryId(catId);

                // Check if item exists in database by name
                MenuItem existing = menuItemDAO.getMenuItemByName(parsed.getItemName());
                if (existing != null) {
                    // Update existing item
                    existing.setCategoryId(parsed.getCategoryId());
                    existing.setDescription(parsed.getDescription());
                    existing.setPrice(parsed.getPrice());
                    existing.setAvailable(parsed.isAvailable());
                    if (parsed.getImageUrl() != null && !parsed.getImageUrl().isEmpty()) {
                        existing.setImageUrl(parsed.getImageUrl());
                    }
                    menuItemDAO.updateMenuItem(existing);
                    result.incrementItemsUpdated();
                } else {
                    // Insert new item
                    menuItemDAO.addMenuItem(parsed);
                    result.incrementItemsInserted();
                }
            }

            result.setSuccess(true);
            result.setMessage(String.format("XML Menu Import Successful: %d items added, %d items updated, %d new categories created.",
                result.getItemsInserted(), result.getItemsUpdated(), result.getCategoriesCreated()));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("XML Import failed due to parsing error: " + e.getMessage());
        }
        return result;
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class ImportResult {
        private boolean success;
        private String message;
        private int itemsInserted = 0;
        private int itemsUpdated = 0;
        private int categoriesCreated = 0;
        private final List<String> warnings = new ArrayList<>();

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public int getItemsInserted() { return itemsInserted; }
        public void incrementItemsInserted() { this.itemsInserted++; }
        public int getItemsUpdated() { return itemsUpdated; }
        public void incrementItemsUpdated() { this.itemsUpdated++; }
        public int getCategoriesCreated() { return categoriesCreated; }
        public void incrementCategoriesCreated() { this.categoriesCreated++; }
        public List<String> getWarnings() { return warnings; }
        public void addWarning(String w) { warnings.add(w); }
    }
}
