package datascript.tools;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SerialFieldTag;
import com.sun.tools.javac.util.ListBuffer;

/**
 * 
 */

/**
 * @author HWellmann
 *
 */
public class CommentParser
{
    class Tag
    {
        String tag;
        String text;
        
        Tag(String tag, String text)
        {
            this.tag = tag;
            this.text = text;
        }
        
        String kind()
        {
            return tag;
        }
    }
    
    /**
     * sorted comments with different tags.
     */
    private final ListBuffer<Tag> tagList = new ListBuffer<Tag>();
    
    /**
     * text minus any tags.
     */
    private String text;
    
    
    /**
     * constructor of Comment.
     */
    CommentParser(final String commentString) {
        
        /**
         * Separate the comment into the text part and zero to N tags.
         * Simple state machine is in one of three states:
         * <pre>
         * IN_TEXT: parsing the comment text or tag text.
         * TAG_NAME: parsing the name of a tag.
         * TAG_GAP: skipping through the gap between the tag name and
         * the tag text.
         * </pre>
         */
        class CommentStringParser {
            /**
             * The entry point to the comment string parser
             */
            void parseCommentStateMachine() {
                final int IN_TEXT = 1;
                final int TAG_GAP = 2;
                final int TAG_NAME = 3;
                int state = TAG_GAP;
                boolean newLine = true;
                String tagName = null;
                int tagStart = 0;
                int textStart = 0;
                int lastNonWhite = -1;
                int len = commentString.length();
                for (int inx = 0; inx < len; ++inx) {
                    char ch = commentString.charAt(inx);
                    boolean isWhite = Character.isWhitespace(ch);
                    switch (state)  {
                        case TAG_NAME:
                            if (isWhite) {
                                tagName = commentString.substring(tagStart, inx);
                                state = TAG_GAP;
                            }
                            break;
                        case TAG_GAP:
                            if (isWhite) {
                                break;
                            }
                            textStart = inx;
                            state = IN_TEXT;
                            /* fall thru */
                        case IN_TEXT:
                            if (newLine && ch == '@') {
                                parseCommentComponent(tagName, textStart,
                                                      lastNonWhite+1);
                                tagStart = inx;
                                state = TAG_NAME;
                            }
                            break;
                    };
                    if (ch == '\n') {
                        newLine = true;
                    } else if (!isWhite) {
                        lastNonWhite = inx;
                        newLine = false;
                    }
                }
                // Finish what's currently being processed
                switch (state)  {
                    case TAG_NAME:
                        tagName = commentString.substring(tagStart, len);
                        /* fall thru */
                    case TAG_GAP:
                        textStart = len;
                        /* fall thru */
                    case IN_TEXT:
                        parseCommentComponent(tagName, textStart, lastNonWhite+1);
                        break;
                };
            }
            
            /**
             * Save away the last parsed item.
             */
            void parseCommentComponent(String tagName,
                                       int from, int upto) {
                String tx = upto <= from ? "" : commentString.substring(from, upto);
                if (tagName == null) {
                    text = tx;
                } else {
                    Tag tag;
                    if (tagName.equals("@exception") || tagName.equals("@throws")) {
                        warnIfEmpty(tagName, tx);
                        tag = new Tag(tagName, tx);
                    } else if (tagName.equals("@param")) {
                        warnIfEmpty(tagName, tx);
                        tag = new Tag(tagName, tx);
                    } else if (tagName.equals("@see")) {
                        warnIfEmpty(tagName, tx);
                        tag = new Tag(tagName, tx);
                    } else if (tagName.equals("@serialField")) {
                        warnIfEmpty(tagName, tx);
                        tag = new Tag(tagName, tx);
                    } else if (tagName.equals("@return")) {
                        warnIfEmpty(tagName, tx);
                        tag = new Tag(tagName, tx);
                    } else if (tagName.equals("@author")) {
                        warnIfEmpty(tagName, tx);
                        tag = new Tag(tagName, tx);
                    } else if (tagName.equals("@version")) {
                        warnIfEmpty(tagName, tx);
                        tag = new Tag(tagName, tx);
                    } else {
                        tag = new Tag(tagName, tx);
                    }
                    tagList.append(tag);
                }
            }
            
            void warnIfEmpty(String tagName, String tx) {
                if (tx.length() == 0) {
                    System.err.println("tag_has_no_arguments" + tagName);
                }
            }
            
        }
        
        new CommentStringParser().parseCommentStateMachine();
    }
    
    /**
     * Return the text of the comment.
     */
    String commentText() {
        return text;
    }
    
    /**
     * Return all tags in this comment.
     */
    Tag[] tags() {
        return tagList.toArray(new Tag[tagList.length()]);
    }
    
    /**
     * Return tags of the specified kind in this comment.
     */
    Tag[] tags(String tagname) {
        ListBuffer<Tag> found = new ListBuffer<Tag>();
        String target = tagname;
        if (target.charAt(0) != '@') {
            target = "@" + target;
        }
        for (Tag tag : tagList) {
            if (tag.kind().equals(target)) {
                found.append(tag);
            }
        }
        return found.toArray(new Tag[found.length()]);
    }
    
    
    /**
     * Return param tags (excluding type param tags) in this comment.
     */
    ParamTag[] paramTags() {
        return paramTags(false);
    }
    
    /**
     * Return type param tags in this comment.
     */
    ParamTag[] typeParamTags() {
        return paramTags(true);
    }
    
    /**
     * Return param tags in this comment.  If typeParams is true
     * include only type param tags, otherwise include only ordinary
     * param tags.
     */
    private ParamTag[] paramTags(boolean typeParams) {
        ListBuffer<ParamTag> found = new ListBuffer<ParamTag>();
        for (Tag next : tagList) {
            if (next instanceof ParamTag) {
                ParamTag p = (ParamTag)next;
                if (typeParams == p.isTypeParameter()) {
                    found.append(p);
                }
            }
        }
        return found.toArray(new ParamTag[found.length()]);
    }
    
    /**
     * Return see also tags in this comment.
     */
    SeeTag[] seeTags() {
        ListBuffer<SeeTag> found = new ListBuffer<SeeTag>();
        for (Tag next : tagList) {
            if (next instanceof SeeTag) {
                found.append((SeeTag)next);
            }
        }
        return found.toArray(new SeeTag[found.length()]);
    }
    
    /**
     * Return serialField tags in this comment.
     */
    SerialFieldTag[] serialFieldTags() {
        ListBuffer<SerialFieldTag> found = new ListBuffer<SerialFieldTag>();
        for (Tag next : tagList) {
            if (next instanceof SerialFieldTag) {
                found.append((SerialFieldTag)next);
            }
        }
        return found.toArray(new SerialFieldTag[found.length()]);
    }
    

    
    /**
     * Return text for this Doc comment.
     */
    public String toString() {
        return text;
    }
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        String comment1 = "/**\n" +
                          " * This is a comment.\n" +
                          " * Second line of comment.\n" +
                          " * @param bla1  text text\n" +
                          "  */";
        CommentParser cp = new CommentParser(comment1);
        System.out.println(cp);

    }

}
