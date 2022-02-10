package Visitor;

import leafs.*;
import nodes.*;
import org.w3c.dom.Element;


public interface ISemanticVisitor {
    public void visit(LeafRealConst leaf);
    public void visit(LeafStringConst leaf);
    public void visit(LeafIntegerConst leaf);
    public void visit(LeafBool leaf);
    public void visit(LeafID leaf);
    public void visit(ExprNode exprNode);
    public void visit(CallFunNode callFunNode);
    public void visit(ParDeclNode parDeclNode);
    public void visit(TypeNode typeNode);
    public void visit(ModeParNode modeParNode);
    public void visit(ReturnStatNode returnStatNode);
    public void visit(AssignStatNode assignStatNode);
    public void visit(WriteStatNode writeStatNode);
    public void visit(WhileStatNode whileStatNode);
    public void visit(ReadStatNode readStatNode);
    public void visit(IfStatNode ifStatNode);
    public void visit(ProgramNode programNode);
    public void visit(IdInitNode idInitNode);
    public void visit(FunNode funNode);
    public void visit(VarDeclNode varDeclNode);
    public void visit(BodyNode bodyNode);
    public void visit(StatListNode statListNode);
    public void visit(ConstNode constNode);
    //public void visit(Bool_ConstNode bool_constNode);

}
