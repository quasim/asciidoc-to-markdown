package com.jdd.apidoc;

import org.asciidoctor.ast.*;
import org.asciidoctor.converter.ConverterFor;
import org.asciidoctor.converter.StringConverter;

import java.util.Arrays;
import java.util.Map;

@ConverterFor(value = MarkdownConverter.DEFAULT_FORMAT, suffix = ".md")
public class MarkdownConverter extends StringConverter {

    public static final String DEFAULT_FORMAT = "markdown";

    private int[] olist = new int[5];

    private String lastMarker = "";

    public MarkdownConverter(String backend, Map<String, Object> opts) {
        super(backend, opts);
    }

    @Override
    public String convert(ContentNode contentNode, String s, Map<Object, Object> map) {
        StrBuilder sb = new StrBuilder();
        Document document = contentNode.getDocument();
        if (document.getSourceLocation() == null) {
            if (document.getTitle() != null) {
                sb.append("# ").appendLine(document.getTitle());
            }
        } else {
            String filename = document.getSourceLocation().getFile();
            String basename = filename.substring(0, filename.length() - 5);
            sb.append("# ").appendLine(basename);
        }
        walkStructuralNode(contentNode.getDocument().getBlocks(), sb);
        return sb.toString();
    }

    private void walkStructuralNode(java.util.List<StructuralNode> nodes, StrBuilder sb) {
        if (nodes == null) return;
        for (StructuralNode node : nodes) {
            String nodeName = node.getNodeName();
            Map<String, Object> attributes = node.getAttributes();
            for (String key : attributes.keySet()) {
                if ("include".equals(attributes.get(key))) {
                    sb.prepareAppendFragment();
                }
            }
            if (node instanceof Block) {
                if (StructuralNodeType.literal.name().equals(nodeName)) {
                    sb.newLine("> ").appendLine(((Block) node).getSource());
                } else if (StructuralNodeType.listing.name().equals(nodeName)) {
                    if ("source".equals(attributes.get(StructuralNodeAttribute.style.name()))) {
                        String language = (String) attributes.get(StructuralNodeAttribute.language.name());
                        sb.append("```").append(language).appendBlock(((Block) node).getSource()).appendLine("```");
                    }
                } else if (StructuralNodeType.paragraph.name().equals(nodeName)) {
//                    sb.newLine();
                    if (StructuralNodeType.quote.name().equals(node.getParent().getNodeName())) {
                        sb.newLine("> ");
                    }
                    sb.appendLine(((Block) node).getSource());
                } else if ("open".equals(nodeName)) {
                    String source = ((Block) node).getSource();
                    if ("abstract".equals(attributes.get(StructuralNodeAttribute.style.name()))) {
                        sb.appendBlock(StringUtils.wrap(source, "*"));
                    } else {
                        sb.appendBlock(source);
                    }
                }
            } else if (node instanceof DescriptionList) {
            } else if (node instanceof Document) {
            } else if (node instanceof org.asciidoctor.ast.List) {
                flipOlist(node.getStyle());
                sb.newLine(getOlist());
            } else if (node instanceof ListItem) {
                ListItem item = (ListItem) node;
                if (StructuralNodeType.olist.name().equals(node.getParent().getNodeName())) {
                    if (item.getMarker().length() < lastMarker.length()) {
                        flipOlist(null);
                        sb.newLine(getOlist());
                    }
                } else {
                    sb.newLine(item.getMarker());
                }
                sb.append(" ").appendLine(item.getSource().replace("\n::", "\n>"));
                lastMarker = ((ListItem) node).getMarker();
            } else if (node instanceof Section) {
                Section section = (Section) node;
                if (!StringUtils.isEmpty(section.getTitle())) {
                    sb.newLine().append(StringUtils.repeat("#", (section.getLevel() + 1))).append(" ").appendLine(section.getTitle());
                }
            } else if (node instanceof Table) {
                Table table = (Table) node;
                if (!StringUtils.isEmpty(table.getTitle())) {
                    sb.newLine().append("###### ").appendLine(table.getTitle());
                }
                java.util.List<Row> header = table.getHeader();
                for (Row row : header) {
                    java.util.List<Cell> cells = row.getCells();
                    StringBuilder headerText = new StringBuilder("|");
                    StringBuilder separateText = new StringBuilder("|");
                    for (Cell cell : cells) {
                        headerText.append(StringUtils.wrap(cell.getText(), " "));
                        Object attr = cell.getAttribute(StructuralNodeAttribute.halign.name());
                        if ("left".equals(attr)) {
                            separateText.append(" :------ ");
                        } else if ("right".equals(attr)) {
                            separateText.append(" ------: ");
                        } else if ("center".equals(attr)) {
                            separateText.append(" :------: ");
                        } else {
                            separateText.append(" ------ ");
                        }
                        headerText.append("|");
                        separateText.append("|");
                    }
                    sb.newLine(headerText.toString()).appendBlock(separateText.toString());
                }
                java.util.List<Row> body = table.getBody();
                for (int i = 0; i < body.size(); i++) {
                    Row row = body.get(i);
                    java.util.List<Cell> cells = row.getCells();
                    sb.append("|");
                    for (Cell cell : cells) {
                        sb.append(StringUtils.wrap(cell.getSource(), " "));
                        sb.append("|");
                    }
                    sb.newLine();
                    if (i == 0 && header.size() == 0) {
                        int len = body.get(0).getCells().size();
                        sb.appendLine(StringUtils.repeatAndJoin("|", len + 1," ------ "));
                    }
                }
                sb.newLine();
            }
            walkStructuralNode(node.getBlocks(), sb);
        }
    }

    private void flipOlist(String style) {
        if ("arabic".equalsIgnoreCase(style)) {
            Arrays.fill(olist, 1, 5, 0);
            olist[0]++;
        } else if ("loweralpha".equalsIgnoreCase(style)) {
            Arrays.fill(olist, 2, 5, 0);
            olist[1]++;
        } else if ("lowerroman".equalsIgnoreCase(style)) {
            Arrays.fill(olist, 3, 5, 0);
            olist[2]++;
        } else if ("upperalpha".equalsIgnoreCase(style)) {
            Arrays.fill(olist, 4, 5, 0);
            olist[3]++;
        } else if ("upperroman".equalsIgnoreCase(style)) {
            Arrays.fill(olist, 5, 5, 0);
            olist[4]++;
        } else {
            Arrays.fill(olist, 1, 5, 0);
            olist[0]++;
        }
    }

    private String getOlist() {
        String s = "";
        for (int i = olist.length; i > 0; i--) {
            if (olist[i - 1] > 0 || !"".equals(s)) {
                s = "".equals(s) ? String.valueOf(olist[i - 1]) : olist[i - 1] + "." + s;
            }
        }
        return s;
    }

}
