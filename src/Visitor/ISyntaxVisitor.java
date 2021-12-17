package Visitor;

import leafs.*;
import nodes.*;
import org.w3c.dom.Element;

public interface ISyntaxVisitor {

    public Element visit(ProgramNode programNode);
    public Element visit(BodyNode bodyNode);
    public Element visit(VarDeclNode varDeclNode);
    public Element visit(TypeNode typeNode);
    public Element visit(IdInitNode idInitNode);
    public Element visit(ConstNode constNode);
    public Element visit(Bool_ConstNode bool_constNode);
    public Element visit(FunNode funNode);
    public Element visit(ParDeclNode parDeclNode);
    public Element visit(StatListNode statListNode);
    public Element visit(IfStatNode node);
    public Element visit(WhileStatNode node);
    public Element visit(ReadStatNode node);
    public Element visit(WriteStatNode node);
    public Element visit(AssignStatNode node);
    public Element visit(CallFunNode node);
    public Element visit(ExprNode exprNode);
    public Element visit(LeafBool leaf);
    public Element visit(LeafID leaf);
    public Element visit(LeafIntegerConst leaf);
    public Element visit(LeafRealConst leaf);
    public Element visit(LeafStringConst leaf);
    public Element visit(ModeWriteNode writeNode);
    public Element visit(ModeParNode modpar);
    public Element visit(ReturnStatNode node);


}
