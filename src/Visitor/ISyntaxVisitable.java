package Visitor;

import org.w3c.dom.Element;

public interface ISyntaxVisitable {
    Element accept(ISyntaxVisitor v);
}
